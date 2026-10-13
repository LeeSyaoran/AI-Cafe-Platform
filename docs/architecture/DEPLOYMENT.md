# Deployment Documentation

## 1. Infrastructure Overview

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                             INFRASTRUCTURE OVERVIEW                                    │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│                              ┌─────────────────┐                                       │
│                              │   CloudFlare   │                                       │
│                              │   CDN / WAF     │                                       │
│                              └────────┬────────┘                                       │
│                                       │                                                │
│                                       ▼                                                │
│                              ┌─────────────────┐                                       │
│                              │  Load Balancer  │                                       │
│                              │   (AWS ALB)     │                                       │
│                              └────────┬────────┘                                       │
│                                       │                                                │
│                    ┌──────────────────┼──────────────────┐                            │
│                    │                  │                  │                            │
│                    ▼                  ▼                  ▼                            │
│              ┌──────────┐      ┌──────────┐      ┌──────────┐                        │
│              │  Kong    │      │  Kong    │      │  Kong    │                        │
│              │  Gateway │      │  Gateway │      │  Gateway │                        │
│              │  (Node 1)│      │  (Node 2)│      │  (Node 3)│                        │
│              └────┬─────┘      └────┬─────┘      └────┬─────┘                        │
│                   │                 │                 │                             │
│         ┌─────────┴─────────────────┴─────────────────┴─────────┐                    │
│         │                                                       │                    │
│         ▼                                                       ▼                    │
│  ┌─────────────────────────────────────────────────────────────────────┐          │
│  │                         Kubernetes Cluster (EKS)                      │          │
│  │                                                                       │          │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐      │          │
│  │  │  Java Services  │  │   Go Services   │  │   AI Gateway    │      │          │
│  │  │  ─────────────  │  │  ─────────────  │  │  ─────────────  │      │          │
│  │  │  • user-service │  │  • auth-svc    │  │  • openai-proxy │      │          │
│  │  │  • order-service│  │  • credit-svc  │  │  • anthropic-px │      │          │
│  │  │  • payment-svc │  │  • workspace-svc│  │  • stability-px │      │          │
│  │  │  • admin-svc   │  │  • notification-│  │                 │      │          │
│  │  │                │  │    svc          │  │                 │      │          │
│  │  └─────────────────┘  └─────────────────┘  └─────────────────┘      │          │
│  │                                                                       │          │
│  └─────────────────────────────────────────────────────────────────────┘          │
│                                       │                                                │
│         ┌─────────────────────────────┼─────────────────────────────┐                   │
│         │                             │                             │                   │
│         ▼                             ▼                             ▼                   │
│  ┌──────────┐                  ┌──────────┐                  ┌──────────┐            │
│  │PostgreSQL│                  │  Redis   │                  │    S3    │            │
│  │(Primary +│                  │ Cluster  │                  │  Assets  │            │
│  │ Replicas)│                  │          │                  │  Backups │            │
│  └──────────┘                  └──────────┘                  └──────────┘            │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. Kubernetes Configuration

### 2.1 Cluster Setup

```yaml
# k8s/cluster.yaml
apiVersion: eksctl.io/v1alpha5
kind: ClusterConfig
metadata:
  name: aicafe-prod
  region: ap-southeast-1
  version: "1.29"

iam:
  withOIDC: true
  serviceAccounts:
    - metadata:
        name: aws-load-balancer-controller
        namespace: kube-system
      wellKnownPolicies:
        awsLoadBalancerController: true
    - metadata:
        name: external-dns
        namespace: kube-system
      wellKnownPolicies:
        externalDNS: true
    - metadata:
        name: cert-manager
        namespace: cert-manager
      wellKnownPolicies:
        certManager: true

managedNodeGroups:
  - name: general
    instanceType: m6i.xlarge
    desiredCapacity: 3
    minSize: 2
    maxSize: 10
    volumeSize: 50
    volumeType: gp3
    privateNetworking: true
    labels:
      workload: general
    tags:
      nodegroup-role: general

  - name: ai-workers
    instanceType: g4dn.xlarge
    desiredCapacity: 2
    minSize: 1
    maxSize: 5
    volumeSize: 100
    volumeType: gp3
    privateNetworking: true
    labels:
      workload: ai-worker
    taints:
      - key: workload
        value: ai-worker
        effect: NoSchedule

  - name: gpu-workers
    instanceType: p4d.24xlarge
    desiredCapacity: 1
    minSize: 0
    maxSize: 2
    volumeSize: 500
    volumeType: gp3
    privateNetworking: true
    labels:
      workload: gpu-worker
    taints:
      - key: workload
        value: gpu-worker
        effect: NoSchedule

addons:
  - name: vpc-cni
    version: latest
    configurationValues: |-
      enableNetworkPolicy: "true"
  - name: coredns
    version: latest
  - name: kube-proxy
    version: latest
  - name: aws-ebs-csi-driver
    version: latest
    serviceAccountRoleARN: arn:aws:iam::123456789:role/EBSCSIDriverRole
```

### 2.2 Namespaces

```yaml
# k8s/namespaces.yaml
apiVersion: v1
kind: Namespace
metadata:
  name: ingress-nginx
  labels:
    name: ingress-nginx
---
apiVersion: v1
kind: Namespace
metadata:
  name: cert-manager
  labels:
    name: cert-manager
---
apiVersion: v1
kind: Namespace
metadata:
  name: monitoring
  labels:
    name: monitoring
---
apiVersion: v1
kind: Namespace
metadata:
  name: aicafe
  labels:
    name: aicafe
---
apiVersion: v1
kind: Namespace
metadata:
  name: ai-gateway
  labels:
    name: ai-gateway
```

### 2.3 Ingress Configuration

```yaml
# k8s/ingress.yaml
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: aicafe-ingress
  namespace: aicafe
  annotations:
    kubernetes.io/ingress.class: nginx
    nginx.ingress.kubernetes.io/rewrite-target: /
    nginx.ingress.kubernetes.io/proxy-body-size: "10m"
    nginx.ingress.kubernetes.io/proxy-read-timeout: "300"
    nginx.ingress.kubernetes.io/proxy-write-timeout: "300"
    nginx.ingress.kubernetes.io/rate-limit: "100"
    nginx.ingress.kubernetes.io/rate-limit-window: "1m"
    nginx.ingress.kubernetes.io/ssl-redirect: "true"
    nginx.ingress.kubernetes.io/force-ssl-redirect: "true"
    cert-manager.io/cluster-issuer: letsencrypt-prod
spec:
  tls:
    - hosts:
        - api.aicafe.vn
        - app.aicafe.vn
        - ws.aicafe.vn
      secretName: aicafe-tls
  rules:
    - host: api.aicafe.vn
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: kong-gateway
                port:
                  number: 443
    - host: app.aicafe.vn
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: frontend-service
                port:
                  number: 80
    - host: ws.aicafe.vn
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: kong-gateway
                port:
                  number: 443
```

### 2.4 Service Deployment (Java)

```yaml
# k8s/services/user-service.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-service
  namespace: aicafe
  labels:
    app: user-service
    tier: backend
spec:
  replicas: 3
  selector:
    matchLabels:
      app: user-service
  template:
    metadata:
      labels:
        app: user-service
        tier: backend
      annotations:
        prometheus.io/scrape: "true"
        prometheus.io/port: "8080"
        prometheus.io/path: "/actuator/prometheus"
    spec:
      serviceAccountName: user-service
      securityContext:
        runAsNonRoot: true
        runAsUser: 1000
        fsGroup: 1000
      containers:
        - name: user-service
          image: 123456789.dkr.ecr.ap-southeast-1.amazonaws.com/user-service:latest
          imagePullPolicy: Always
          ports:
            - containerPort: 8080
              name: http
            - containerPort: 8443
              name: https
          env:
            - name: SPRING_PROFILES_ACTIVE
              value: "prod"
            - name: JAVA_OPTS
              value: "-Xms512m -Xmx1024m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
            - name: DB_HOST
              valueFrom:
                secretKeyRef:
                  name: db-credentials
                  key: host
            - name: DB_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: db-credentials
                  key: password
            - name: REDIS_HOST
              valueFrom:
                configMapKeyRef:
                  name: redis-config
                  key: host
            - name: SPRING_DATASOURCE_HIKARI_MAXIMUM_POOL_SIZE
              value: "20"
            - name: SPRING_DATASOURCE_HIKARI_MINIMUM_IDLE
              value: "5"
          resources:
            requests:
              cpu: 250m
              memory: 512Mi
            limits:
              cpu: 1000m
              memory: 1Gi
          livenessProbe:
            httpGet:
              path: /actuator/health/liveness
              port: 8080
            initialDelaySeconds: 60
            periodSeconds: 10
            failureThreshold: 3
          readinessProbe:
            httpGet:
              path: /actuator/health/readiness
              port: 8080
            initialDelaySeconds: 30
            periodSeconds: 5
            failureThreshold: 3
          lifecycle:
            preStop:
              exec:
                command: ["/bin/sh", "-c", "sleep 10"]
      affinity:
        podAntiAffinity:
          preferredDuringSchedulingIgnoredDuringExecution:
            - weight: 100
              podAffinityTerm:
                labelSelector:
                  matchLabels:
                    app: user-service
                topologyKey: kubernetes.io/hostname
      topologySpreadConstraints:
        - maxSkew: 1
          topologyKey: topology.kubernetes.io/zone
          whenUnsatisfiable: ScheduleAnyway
          labelSelector:
            matchLabels:
              app: user-service
---
apiVersion: v1
kind: Service
metadata:
  name: user-service
  namespace: aicafe
  labels:
    app: user-service
spec:
  type: ClusterIP
  ports:
    - port: 8080
      targetPort: 8080
      protocol: TCP
      name: http
  selector:
    app: user-service
---
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: user-service-hpa
  namespace: aicafe
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: user-service
  minReplicas: 3
  maxReplicas: 20
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
    - type: Resource
      resource:
        name: memory
        target:
          type: Utilization
          averageUtilization: 80
  behavior:
    scaleDown:
      stabilizationWindowSeconds: 300
      policies:
        - type: Percent
          value: 10
          periodSeconds: 60
    scaleUp:
      stabilizationWindowSeconds: 0
      policies:
        - type: Percent
          value: 100
          periodSeconds: 15
```

### 2.5 Service Deployment (Go)

```yaml
# k8s/services/credit-service.yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: credit-service
  namespace: aicafe
  labels:
    app: credit-service
    tier: backend
spec:
  replicas: 3
  selector:
    matchLabels:
      app: credit-service
  template:
    metadata:
      labels:
        app: credit-service
        tier: backend
      annotations:
        prometheus.io/scrape: "true"
        prometheus.io/port: "8080"
        prometheus.io/path: "/metrics"
    spec:
      serviceAccountName: credit-service
      securityContext:
        runAsNonRoot: true
        runAsUser: 1000
      containers:
        - name: credit-service
          image: 123456789.dkr.ecr.ap-southeast-1.amazonaws.com/credit-service:latest
          imagePullPolicy: Always
          ports:
            - containerPort: 8080
              name: http
          env:
            - name: ENV
              value: "production"
            - name: LOG_LEVEL
              value: "info"
            - name: DB_HOST
              valueFrom:
                secretKeyRef:
                  name: db-credentials
                  key: host
            - name: DB_PASSWORD
              valueFrom:
                secretKeyRef:
                  name: db-credentials
                  key: password
            - name: REDIS_HOST
              valueFrom:
                configMapKeyRef:
                  name: redis-config
                  key: host
            - name: GOMAXPROCS
              value: "4"
            - name: GOMEMLIMIT
              value: "1024MiB"
          resources:
            requests:
              cpu: 100m
              memory: 256Mi
            limits:
              cpu: 500m
              memory: 1Gi
          livenessProbe:
            httpGet:
              path: /health/live
              port: 8080
            initialDelaySeconds: 10
            periodSeconds: 10
          readinessProbe:
            httpGet:
              path: /health/ready
              port: 8080
            initialDelaySeconds: 5
            periodSeconds: 5
      affinity:
        podAntiAffinity:
          preferredDuringSchedulingIgnoredDuringExecution:
            - weight: 100
              podAffinityTerm:
                labelSelector:
                  matchLabels:
                    app: credit-service
                topologyKey: kubernetes.io/hostname
---
apiVersion: v1
kind: Service
metadata:
  name: credit-service
  namespace: aicafe
spec:
  type: ClusterIP
  ports:
    - port: 8080
      targetPort: 8080
  selector:
    app: credit-service
```

### 2.6 ConfigMaps and Secrets

```yaml
# k8s/configmaps.yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: redis-config
  namespace: aicafe
data:
  host: "redis.aicafe.svc.cluster.local"
  port: "6379"
  db: "0"
---
apiVersion: v1
kind: ConfigMap
metadata:
  name: ai-config
  namespace: ai-gateway
data:
  OPENAI_BASE_URL: "https://api.openai.com/v1"
  ANTHROPIC_BASE_URL: "https://api.anthropic.com/v1"
  STABILITY_BASE_URL: "https://api.stability.ai/v1"
---
apiVersion: v1
kind: Secret
metadata:
  name: db-credentials
  namespace: aicafe
type: Opaque
stringData:
  host: "postgres-primary.ap-southeast-1.rds.amazonaws.com"
  username: "aicafe_app"
  password: "${DB_PASSWORD}"
  name: "aicafe_prod"
---
apiVersion: v1
kind: Secret
metadata:
  name: redis-credentials
  namespace: aicafe
type: Opaque
stringData:
  password: "${REDIS_PASSWORD}"
```

---

## 3. CI/CD Pipeline

### 3.1 GitHub Actions - Build & Deploy

```yaml
# .github/workflows/deploy.yml
name: Build and Deploy

on:
  push:
    branches:
      - main
    tags:
      - 'v*'
  workflow_dispatch:
    inputs:
      environment:
        description: 'Environment to deploy'
        required: true
        type: choice
        options:
          - staging
          - production

env:
  REGISTRY: 123456789.dkr.ecr.ap-southeast-1.amazonaws.com
  EKS_CLUSTER: aicafe-prod

jobs:
  build-java:
    name: Build Java Services
    runs-on: ubuntu-latest
    outputs:
      image_tag: ${{ steps.vars.outputs.image_tag }}
    steps:
      - uses: actions/checkout@v4

      - name: Set up JDK 17
        uses: actions/setup-java@v4
        with:
          java-version: '17'
          distribution: 'temurin'
          cache: 'maven'

      - name: Cache Maven packages
        uses: actions/cache@v3
        with:
          path: ~/.m2/repository
          key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}
          restore-keys: ${{ runner.os }}-maven-

      - name: Build with Maven
        run: mvn clean package -DskipTests

      - name: Generate image tag
        id: vars
        run: echo "image_tag=$(git rev-parse --short HEAD)" >> $GITHUB_OUTPUT

      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: ap-southeast-1

      - name: Login to Amazon ECR
        id: login-ecr
        uses: aws-actions/amazon-ecr-login@v2

      - name: Build and push user-service
        env:
          ECR_REGISTRY: ${{ steps.login-ecr.outputs.registry }}
          IMAGE_TAG: ${{ steps.vars.outputs.image_tag }}
        run: |
          docker build -t $ECR_REGISTRY/user-service:$IMAGE_TAG ./services/user-service
          docker push $ECR_REGISTRY/user-service:$IMAGE_TAG

      - name: Build and push order-service
        env:
          ECR_REGISTRY: ${{ steps.login-ecr.outputs.registry }}
          IMAGE_TAG: ${{ steps.vars.outputs.image_tag }}
        run: |
          docker build -t $ECR_REGISTRY/order-service:$IMAGE_TAG ./services/order-service
          docker push $ECR_REGISTRY/order-service:$IMAGE_TAG

  build-go:
    name: Build Go Services
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v4

      - name: Set up Go
        uses: actions/setup-go@v5
        with:
          go-version: '1.22'
          cache: true

      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: ap-southeast-1

      - name: Login to Amazon ECR
        id: login-ecr
        uses: aws-actions/amazon-ecr-login@v2

      - name: Build and push credit-service
        env:
          ECR_REGISTRY: ${{ steps.login-ecr.outputs.registry }}
          IMAGE_TAG: ${{ github.sha }}
        run: |
          cd services/credit-service
          docker build -t $ECR_REGISTRY/credit-service:$IMAGE_TAG .
          docker push $ECR_REGISTRY/credit-service:$IMAGE_TAG

      - name: Build and push auth-service
        env:
          ECR_REGISTRY: ${{ steps.login-ecr.outputs.registry }}
          IMAGE_TAG: ${{ github.sha }}
        run: |
          cd services/auth-service
          docker build -t $ECR_REGISTRY/auth-service:$IMAGE_TAG .
          docker push $ECR_REGISTRY/auth-service:$IMAGE_TAG

  deploy:
    name: Deploy to Kubernetes
    runs-on: ubuntu-latest
    needs: [build-java, build-go]
    environment: ${{ github.event.inputs.environment || 'staging' }}

    steps:
      - uses: actions/checkout@v4

      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: ap-southeast-1

      - name: Update kubeconfig
        uses: aws-actions/aws-eks-bestpractices@v1
        with:
          cluster: ${{ env.EKS_CLUSTER }}
          version: '1.29'

      - name: Deploy Java services
        env:
          IMAGE_TAG: ${{ github.sha }}
          NAMESPACE: aicafe
        run: |
          kubectl set image deployment/user-service \
            user-service=${{ env.REGISTRY }}/user-service:$IMAGE_TAG \
            -n $NAMESPACE
          
          kubectl set image deployment/order-service \
            order-service=${{ env.REGISTRY }}/order-service:$IMAGE_TAG \
            -n $NAMESPACE

      - name: Deploy Go services
        env:
          IMAGE_TAG: ${{ github.sha }}
          NAMESPACE: aicafe
        run: |
          kubectl set image deployment/credit-service \
            credit-service=${{ env.REGISTRY }}/credit-service:$IMAGE_TAG \
            -n $NAMESPACE
          
          kubectl set image deployment/auth-service \
            auth-service=${{ env.REGISTRY }}/auth-service:$IMAGE_TAG \
            -n $NAMESPACE

      - name: Verify deployment
        run: |
          kubectl rollout status deployment/user-service -n aicafe --timeout=300s
          kubectl rollout status deployment/credit-service -n aicafe --timeout=300s
          
      - name: Run smoke tests
        run: |
          kubectl run smoke-test --image=curlimages/curl:latest \
            --restart=Never -n $NAMESPACE -- \
            curl -s https://api.aicafe.vn/health || exit 1

  rollback:
    name: Rollback
    runs-on: ubuntu-latest
    if: failure()
    steps:
      - uses: actions/checkout@v4

      - name: Configure AWS credentials
        uses: aws-actions/configure-aws-credentials@v4
        with:
          aws-access-key-id: ${{ secrets.AWS_ACCESS_KEY_ID }}
          aws-secret-access-key: ${{ secrets.AWS_SECRET_ACCESS_KEY }}
          aws-region: ap-southeast-1

      - name: Update kubeconfig
        run: aws eks update-kubeconfig --name ${{ env.EKS_CLUSTER }}

      - name: Rollback deployments
        run: |
          kubectl rollout undo deployment/user-service -n aicafe
          kubectl rollout undo deployment/credit-service -n aicafe
```

### 3.2 Helm Charts

```yaml
# helm/aicafe-services/Chart.yaml
apiVersion: v2
name: aicafe-services
description: AI Cafe Platform Services
type: application
version: 1.0.0
appVersion: "1.0"
dependencies:
  - name: common
    version: 1.x.x
    repository: https://charts.bitnami.com/bitnami
  - name: postgresql
    version: 12.x.x
    repository: https://charts.bitnami.com/bitnami
    condition: postgresql.enabled
  - name: redis
    version: 17.x.x
    repository: https://charts.bitnami.com/bitnami
    condition: redis.enabled

# helm/aicafe-services/values.yaml
global:
  imageRegistry: 123456789.dkr.ecr.ap-southeast-1.amazonaws.com
  imagePullSecrets:
    - name: ecr-secret
  storageClass: gp3

namespace: aicafe

userService:
  enabled: true
  replicaCount: 3
  image:
    repository: aicafe/user-service
    tag: latest
    pullPolicy: Always
  service:
    type: ClusterIP
    port: 8080
  ingress:
    enabled: true
    className: nginx
    host: api.aicafe.vn
    tls:
      enabled: true
      secretName: aicafe-tls
  resources:
    limits:
      cpu: 1000m
      memory: 1Gi
    requests:
      cpu: 250m
      memory: 512Mi
  autoscaling:
    enabled: true
    minReplicas: 3
    maxReplicas: 20
    targetCPUUtilizationPercentage: 70
    targetMemoryUtilizationPercentage: 80
  env:
    SPRING_PROFILES_ACTIVE: prod
    JAVA_OPTS: "-Xms512m -Xmx1024m"
  persistence:
    enabled: false
  metrics:
    enabled: true
    serviceMonitor:
      enabled: true

creditService:
  enabled: true
  replicaCount: 3
  image:
    repository: aicafe/credit-service
    tag: latest
  resources:
    limits:
      cpu: 500m
      memory: 1Gi
    requests:
      cpu: 100m
      memory: 256Mi
  autoscaling:
    enabled: true
    minReplicas: 3
    maxReplicas: 10

redis:
  enabled: true
  architecture: replication
  auth:
    enabled: true
    existingSecret: redis-credentials
  master:
    persistence:
      enabled: true
      size: 10Gi
      storageClass: gp3
    resources:
      limits:
        cpu: 500m
        memory: 1Gi
      requests:
        cpu: 100m
        memory: 256Mi
  replica:
    replicaCount: 2
    persistence:
      enabled: true
      size: 10Gi
    resources:
      limits:
        cpu: 500m
        memory: 1Gi
      requests:
        cpu: 100m
        memory: 256Mi
```

---

## 4. Database Migration

### 4.1 Flyway Configuration (Java)

```properties
# src/main/resources/db/migration.properties
flyway.url=jdbc:postgresql://${DB_HOST}:5432/${DB_NAME}
flyway.user=${DB_USERNAME}
flyway.password=${DB_PASSWORD}
flyway.locations=classpath:db/migration
flyway.baselineOnMigrate=true
flyway.baselineVersion=1
flyway.table=schema_version
flyway.validateOnMigrate=true
flyway.outOfOrder=false
flyway.connectRetries=3
flyway.connectRetriesInterval=10
```

```sql
-- V1__Initial_schema.sql
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    phone VARCHAR(20) NOT NULL UNIQUE,
    phone_encrypted BYTEA,
    email VARCHAR(255),
    email_encrypted BYTEA,
    name VARCHAR(100),
    avatar_url VARCHAR(500),
    tier VARCHAR(20) NOT NULL DEFAULT 'BASIC',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP
);

CREATE TABLE wallets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    credits BIGINT NOT NULL DEFAULT 0,
    workspace_minutes INT NOT NULL DEFAULT 0,
    version INT NOT NULL DEFAULT 1,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT unique_user_wallet UNIQUE (user_id)
);

CREATE TABLE orders (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    order_number VARCHAR(50) NOT NULL UNIQUE,
    type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    subtotal DECIMAL(12,2) NOT NULL,
    discount DECIMAL(12,2) NOT NULL DEFAULT 0,
    tax DECIMAL(12,2) NOT NULL DEFAULT 0,
    total DECIMAL(12,2) NOT NULL,
    payment_provider VARCHAR(20),
    payment_transaction_id VARCHAR(100),
    paid_at TIMESTAMP,
    idempotency_key VARCHAR(100),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_phone ON users(phone);
CREATE INDEX idx_users_status ON users(status);
CREATE INDEX idx_wallets_user_id ON wallets(user_id);
CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_status ON orders(status);
CREATE INDEX idx_orders_created_at ON orders(created_at);
```

```sql
-- V2__Add_ai_tables.sql
CREATE TABLE ai_models (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    provider VARCHAR(50) NOT NULL,
    model_id VARCHAR(100) NOT NULL,
    type VARCHAR(20) NOT NULL, -- CHAT, IMAGE, VIDEO
    credits_per_unit INT NOT NULL,
    max_tokens INT,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE ai_usage_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id),
    model_id UUID NOT NULL REFERENCES ai_models(id),
    request_id VARCHAR(100),
    input_tokens INT,
    output_tokens INT,
    total_tokens INT,
    credits_used INT NOT NULL,
    latency_ms INT,
    status VARCHAR(20) NOT NULL,
    error_message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_ai_usage_user ON ai_usage_logs(user_id);
CREATE INDEX idx_ai_usage_created ON ai_usage_logs(created_at);
```

### 4.2 Go Migration

```go
// internal/database/migrate.go
package database

import (
    "embed"
    "fmt"
    "io/fs"
    "path/filepath"
    "sort"
    "strings"

    "github.com/golang-migrate/migrate/v4"
    "github.com/golang-migrate/migrate/v4/source/iofs"
)

//go:embed migrations/*.sql
var migrations embed.FS

func RunMigrations(db *sql.DB, dbName string) error {
    // Get migration files
    entries, err := fs.ReadDir(migrations, "migrations")
    if err != nil {
        return fmt.Errorf("failed to read migrations: %w", err)
    }

    // Sort files
    var files []string
    for _, e := range entries {
        if !e.IsDir() && strings.HasSuffix(e.Name(), ".sql") {
            files = append(files, e.Name())
        }
    }
    sort.Strings(files)

    // Run each migration
    for _, file := range files {
        content, err := fs.ReadFile(migrations, filepath.Join("migrations", file))
        if err != nil {
            return fmt.Errorf("failed to read %s: %w", file, err)
        }

        _, err = db.Exec(string(content))
        if err != nil {
            // Ignore "already exists" errors
            if !strings.Contains(err.Error(), "already exists") {
                return fmt.Errorf("failed to run %s: %w", file, err)
            }
        }
    }

    return nil
}
```

---

## 5. Monitoring & Observability

### 5.1 Prometheus Configuration

```yaml
# monitoring/prometheus.yaml
apiVersion: monitoring.coreos.com/v1
kind: Prometheus
metadata:
  name: prometheus
  namespace: monitoring
spec:
  replicas: 2
  retention: 30d
  retentionSize: 50GB
  storage:
    volumeClaimTemplate:
      spec:
        storageClassName: gp3
        resources:
          requests:
            storage: 50Gi
  serviceAccountName: prometheus
  serviceMonitorSelector:
    matchLabels:
      team: platform
  ruleSelector:
    matchLabels:
      role: alert-rules
  alerting:
    alertmanagers:
      - namespace: monitoring
        name: alertmanager-main
        port: web
---
apiVersion: monitoring.coreos.com/v1
kind: ServiceMonitor
metadata:
  name: user-service
  namespace: aicafe
  labels:
    team: platform
spec:
  selector:
    matchLabels:
      app: user-service
  endpoints:
    - port: http
      path: /actuator/prometheus
      interval: 15s
      tlsConfig:
        insecureSkipVerify: false
  namespaceSelector:
    matchNames:
      - aicafe
```

### 5.2 Grafana Dashboard

```json
{
  "dashboard": {
    "title": "AI Cafe - Service Overview",
    "panels": [
      {
        "title": "Request Rate",
        "type": "graph",
        "gridPos": {"x": 0, "y": 0, "w": 12, "h": 8},
        "targets": [
          {
            "expr": "sum(rate(http_requests_total{service=~\"$service\"}[5m])) by (service)",
            "legendFormat": "{{service}}"
          }
        ]
      },
      {
        "title": "Error Rate",
        "type": "graph",
        "gridPos": {"x": 12, "y": 0, "w": 12, "h": 8},
        "targets": [
          {
            "expr": "sum(rate(http_requests_total{service=~\"$service\", status=~\"5..\"}[5m])) by (service) / sum(rate(http_requests_total{service=~\"$service\"}[5m])) by (service)",
            "legendFormat": "{{service}}"
          }
        ]
      },
      {
        "title": "Latency (p99)",
        "type": "graph",
        "gridPos": {"x": 0, "y": 8, "w": 12, "h": 8},
        "targets": [
          {
            "expr": "histogram_quantile(0.99, sum(rate(http_request_duration_seconds_bucket{service=~\"$service\"}[5m])) by (service, le))",
            "legendFormat": "{{service}} p99"
          }
        ]
      },
      {
        "title": "CPU Usage",
        "type": "graph",
        "gridPos": {"x": 12, "y": 8, "w": 12, "h": 8},
        "targets": [
          {
            "expr": "sum(rate(container_cpu_usage_seconds_total{pod=~\"$service-.*\"}[5m])) by (pod)",
            "legendFormat": "{{pod}}"
          }
        ]
      }
    ]
  }
}
```

### 5.3 Alert Rules

```yaml
# monitoring/alerts.yaml
groups:
  - name: aicafe-alerts
    rules:
      - alert: HighErrorRate
        expr: sum(rate(http_requests_total{status=~"5.."}[5m])) / sum(rate(http_requests_total[5m])) > 0.05
        for: 5m
        labels:
          severity: critical
        annotations:
          summary: "High error rate detected"
          description: "Error rate is {{ $value | humanizePercentage }} for {{ $labels.service }}"

      - alert: ServiceDown
        expr: up{job="user-service"} == 0
        for: 2m
        labels:
          severity: critical
        annotations:
          summary: "Service is down"
          description: "{{ $labels.job }} has been down for more than 2 minutes"

      - alert: HighLatency
        expr: histogram_quantile(0.99, sum(rate(http_request_duration_seconds_bucket[5m])) by (le, service)) > 2
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "High latency detected"
          description: "p99 latency is {{ $value }}s for {{ $labels.service }}"

      - alert: HighMemoryUsage
        expr: (sum(container_memory_usage_bytes) by (pod) / sum(container_spec_memory_limit_bytes) by (pod)) > 0.9
        for: 10m
        labels:
          severity: warning
        annotations:
          summary: "High memory usage"
          description: "Memory usage is above 90% for {{ $labels.pod }}"

      - alert: LowCreditBalance
        expr: wallet_credits < 100
        for: 5m
        labels:
          severity: warning
        annotations:
          summary: "Low credit balance"
          description: "User {{ $labels.user_id }} has only {{ $value }} credits remaining"
```

---

## 6. Backup & Recovery

### 6.1 Database Backup

```yaml
# k8s/backup/cronjob.yaml
apiVersion: batch/v1
kind: CronJob
metadata:
  name: postgres-backup
  namespace: aicafe
spec:
  schedule: "0 2 * * *"
  concurrencyPolicy: Forbid
  successfulJobsHistoryLimit: 3
  failedJobsHistoryLimit: 3
  jobTemplate:
    spec:
      backoffLimit: 3
      template:
        spec:
          serviceAccountName: backup-service
          containers:
            - name: backup
              image: 123456789.dkr.ecr.ap-southeast-1.amazonaws.com/backup-tool:latest
              env:
                - name: DB_HOST
                  valueFrom:
                    secretKeyRef:
                      name: db-credentials
                      key: host
                - name: DB_USER
                  valueFrom:
                    secretKeyRef:
                      name: db-credentials
                      key: username
                - name: DB_PASSWORD
                  valueFrom:
                    secretKeyRef:
                      name: db-credentials
                      key: password
                - name: DB_NAME
                  valueFrom:
                    secretKeyRef:
                      name: db-credentials
                      key: name
                - name: S3_BUCKET
                  value: "aicafe-backups-prod"
                - name: BACKUP_RETENTION_DAYS
                  value: "30"
              command:
                - /bin/sh
                - -c
                - |
                  TIMESTAMP=$(date +%Y%m%d_%H%M%S)
                  FILENAME="aicafe-backup-${TIMESTAMP}.sql.gz"
                  
                  # Create backup
                  pg_dump -h $DB_HOST -U $DB_USER -d $DB_NAME | gzip > /tmp/$FILENAME
                  
                  # Upload to S3
                  aws s3 cp /tmp/$FILENAME s3://$S3_BUCKET/backups/$FILENAME
                  
                  # Cleanup old backups
                  aws s3 ls s3://$S3_BUCKET/backups/ | while read -r line; do
                    backup_date=$(echo $line | awk '{print $4}' | sed 's/aicafe-backup-\(.*\)\.sql\.gz/\1/')
                    backup_epoch=$(date -d ${backup_date:0:8} +%s 2>/dev/null || echo 0)
                    cutoff_epoch=$(date -d "$BACKUP_RETENTION_DAYS days ago" +%s)
                    if [ $backup_epoch -lt $cutoff_epoch ]; then
                      aws s3 rm s3://$S3_BUCKET/backups/aicafe-backup-${backup_date}.sql.gz
                    fi
                  done
                  
                  rm /tmp/$FILENAME
              resources:
                requests:
                  memory: 256Mi
                  cpu: 100m
                limits:
                  memory: 512Mi
                  cpu: 500m
          restartPolicy: OnFailure
```

### 6.2 Point-in-Time Recovery

```bash
#!/bin/bash
# scripts/restore-pitr.sh

set -e

# Variables
DB_INSTANCE="aicafe-postgres-prod"
S3_BUCKET="aicafe-backups-prod"
TARGET_DATE="2024-01-15"
TARGET_TIME="14:30:00"
NEW_INSTANCE_NAME="aicafe-postgres-restore"

echo "Starting point-in-time recovery..."
echo "Target: ${TARGET_DATE} ${TARGET_TIME}"

# Create DB instance from PITR
aws rds restore-db-instance-to-point-in-time \
    --source-db-instance-identifier $DB_INSTANCE \
    --target-db-instance-identifier $NEW_INSTANCE_NAME \
    --restore-time "${TARGET_DATE}T${TARGET_TIME}Z" \
    --db-instance-class db.r6g.xlarge \
    --no-publicly-accessible \
    --storage-encrypted

echo "Waiting for restore to complete..."
aws rds wait db-instance-available --db-instance-identifier $NEW_INSTANCE_NAME

echo "Restoring completed. New instance: $NEW_INSTANCE_NAME"
echo "Remember to update connection strings and verify data before switching."
```

---

## 7. Rollback Procedures

### 7.1 Kubernetes Rollback

```bash
#!/bin/bash
# scripts/k8s-rollback.sh

set -e

NAMESPACE=${1:-aicafe}
DEPLOYMENT=${2:-user-service}
REVISION=${3:-1}

echo "Rolling back $DEPLOYMENT in $NAMESPACE to revision $REVISION..."

# Check current rollout
kubectl rollout history deployment/$DEPLOYMENT -n $NAMESPACE

# Perform rollback
kubectl rollout undo deployment/$DEPLOYMENT -n $NAMESPACE --to-revision=$REVISION

# Wait for rollout
kubectl rollout status deployment/$DEPLOYMENT -n $NAMESPACE --timeout=300s

echo "Rollback completed successfully"

# Verify
kubectl get pods -n $NAMESPACE -l app=$DEPLOYMENT
kubectl describe deployment $DEPLOYMENT -n $NAMESPACE | grep -A 5 "Annotations"
```

### 7.2 Database Rollback

```sql
-- Rollback migration example (V2__rollback.sql)
-- Only run if absolutely necessary and data is backed up

BEGIN;

-- Drop new tables
DROP TABLE IF EXISTS ai_usage_logs;
DROP TABLE IF EXISTS ai_models;

-- Verify no dependent data exists
SELECT 
    (SELECT COUNT(*) FROM ai_usage_logs) as usage_count,
    (SELECT COUNT(*) FROM ai_models) as model_count;

-- Commit only if counts are 0
COMMIT;
```

---

## 8. Deployment Checklist

### 8.1 Pre-Deployment

- [ ] All tests passing in CI
- [ ] Code review approved
- [ ] Database migrations reviewed
- [ ] Rollback plan documented
- [ ] Monitoring dashboards accessible
- [ ] On-call team notified
- [ ] Maintenance window scheduled
- [ ] Backup completed

### 8.2 Deployment Steps

1. **Build Phase**
   - Run unit tests
   - Build Docker images
   - Push to ECR
   - Scan for vulnerabilities

2. **Deploy Phase**
   - Apply database migrations (with backup)
   - Deploy services using rolling update
   - Verify health checks
   - Run smoke tests

3. **Post-Deployment**
   - Monitor error rates
   - Monitor latency
   - Verify functionality
   - Update documentation

### 8.3 Rollback Triggers

- Error rate > 5%
- Latency p99 > 5 seconds
- Critical service down > 2 minutes
- Database connection failures
- Authentication failures

---

## 9. Environment Configuration

### 9.1 Environment Variables

```bash
# .env.staging
# Database
DB_HOST=staging-postgres.ap-southeast-1.rds.amazonaws.com
DB_PORT=5432
DB_NAME=aicafe_staging
DB_USERNAME=aicafe_app
DB_PASSWORD=<from-secrets-manager>

# Redis
REDIS_HOST=staging-redis.ap-southeast-1.elasticache.amazonaws.com
REDIS_PORT=6379
REDIS_PASSWORD=<from-secrets-manager>

# AWS
AWS_REGION=ap-southeast-1
AWS_S3_BUCKET=aicafe-assets-staging

# AI Providers
OPENAI_API_KEY=<from-secrets-manager>
ANTHROPIC_API_KEY=<from-secrets-manager>
STABILITY_API_KEY=<from-secrets-manager>

# Application
SPRING_PROFILES_ACTIVE=staging
ENV=staging
LOG_LEVEL=info
```

### 9.2 Secret Management

```bash
#!/bin/bash
# scripts/update-secrets.sh

set -e

SECRET_NAME=$1
REGION=${2:-ap-southeast-1}

echo "Updating secret: $SECRET_NAME"

# Get current secret
aws secretsmanager get-secret-value \
    --secret-id $SECRET_NAME \
    --region $REGION \
    --query SecretString \
    --output text > current_secret.json

# Open editor
${EDITOR:-vi} current_secret.json

# Update secret
aws secretsmanager update-secret \
    --secret-id $SECRET_NAME \
    --secret-string file://current_secret.json \
    --region $REGION

rm current_secret.json
echo "Secret updated successfully"
```
