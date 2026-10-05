# DevOps & CI/CD Documentation

## 1. Overview

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              CI/CD PIPELINE OVERVIEW                                  │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  ┌─────────────────────────────────────────────────────────────────────────────┐  │
│  │                              GIT FLOW                                           │  │
│  │                                                                              │  │
│  │    feature/* ──► develop ──► staging ──► main (production)              │  │
│  │         │              │              │               │                    │  │
│  │         │              │              │               │                    │  │
│  │         │              ▼              ▼               ▼                    │  │
│  │         │      ┌──────────────────────────────────────────────┐         │  │
│  │         │      │              GITHUB ACTIONS                     │         │  │
│  │         │      │                                              │         │  │
│  │         │      │  develop  ──► dev.yml (CI only)              │         │  │
│  │         │      │  staging  ──► staging.yml (CI + Deploy)     │         │  │
│  │         │      │  main     ──► production.yml (Full Deploy)   │         │  │
│  │         │      │                                              │         │  │
│  │         │      └──────────────────────────────────────────────┘         │  │
│  │         │                                                        │         │  │
│  │         └────────────────────────────────────────────────────────┘         │  │
│  │                                                                        │         │  │
│  └─────────────────────────────────────────────────────────────────────────────┘  │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. CI/CD Workflows

### 2.1 Development Workflow (dev.yml)

**Trigger:** Push to `develop` branch

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              DEV WORKFLOW                                           │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Jobs:                                                                              │
│  ─────                                                                              │
│                                                                                      │
│  ┌────────────────┐  ┌────────────────┐  ┌────────────────┐                        │
│  │ Java Tests     │  │ Go Tests      │  │ Node Tests    │                        │
│  │ ────────────  │  │ ────────────  │  │ ────────────  │                        │
│  │ • user-service│  │ • auth-svc   │  │ • ai-gateway │                        │
│  │ • order-svc   │  │ • credit-svc │  │ • api-client │                        │
│  │ • payment-svc │  │              │  │              │                        │
│  └───────┬────────┘  └───────┬────────┘  └───────┬────────┘                        │
│          │                   │                   │                                 │
│          └───────────────────┴───────────────────┘                                 │
│                              │                                                     │
│                              ▼                                                     │
│  ┌────────────────────────────────────────────────────────────────────────────┐   │
│  │                          Docker Compose Test                                 │   │
│  │                                                                            │   │
│  │  • Start all services with docker-compose                                  │   │
│  │  • Run integration tests                                                  │   │
│  │  • Health check all endpoints                                             │   │
│  │                                                                            │   │
│  └────────────────────────────────────────────────────────────────────────────┘   │
│                              │                                                     │
│                              ▼                                                     │
│  ┌────────────────────────────────────────────────────────────────────────────┐   │
│  │                       Security Scan                                         │   │
│  │                                                                            │   │
│  │  • Trivy vulnerability scan                                               │   │
│  │  • Dependency check                                                       │   │
│  │  • Secret scanning                                                        │   │
│  │                                                                            │   │
│  └────────────────────────────────────────────────────────────────────────────┘   │
│                              │                                                     │
│                              ▼                                                     │
│                    ┌─────────────────┐                                            │
│                    │   Slack Notify  │                                            │
│                    │  (on failure)   │                                            │
│                    └─────────────────┘                                            │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 Staging Workflow (staging.yml)

**Trigger:** Push to `staging` branch

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                             STAGING WORKFLOW                                         │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Pre-deployment:                                                                    │
│  ──────────────                                                                    │
│                                                                                      │
│  ┌─────────────────┐                                                               │
│  │ Pre-deployment  │                                                               │
│  │    Checks       │                                                               │
│  └────────┬────────┘                                                               │
│           │                                                                         │
│           ▼                                                                         │
│  ┌────────────────────────────────────────────────────────────────────────────┐   │
│  │                      BUILD DOCKER IMAGES                                      │   │
│  │                                                                            │   │
│  │  Parallel build for all services:                                           │   │
│  │  • user-service (multi-arch: amd64, arm64)                                │   │
│  │  • order-service                                                           │   │
│  │  • payment-service                                                         │   │
│  │  • auth-service                                                           │   │
│  │  • credit-service                                                         │   │
│  │  • ai-gateway                                                             │   │
│  │  • notification-service                                                   │   │
│  │  • web-app                                                                │   │
│  │  • admin-dashboard                                                        │   │
│  │                                                                            │   │
│  └────────────────────────────────────────────────────────────────────────────┘   │
│           │                                                                         │
│           ▼                                                                         │
│  ┌────────────────────────────────────────────────────────────────────────────┐   │
│  │                    DEPLOY TO KUBERNETES (STAGING)                            │   │
│  │                                                                            │   │
│  │  • Configure kubectl with KUBE_CONFIG_STAGING                              │   │
│  │  • Run database migrations                                                 │   │
│  │  • Deploy all services                                                    │   │
│  │  • Wait for rollout completion                                            │   │
│  │  • Run smoke tests                                                        │   │
│  │                                                                            │   │
│  └────────────────────────────────────────────────────────────────────────────┘   │
│                              │                                                     │
│                              ▼                                                     │
│                    ┌─────────────────┐                                            │
│                    │   Slack Notify  │                                            │
│                    │  (success/fail) │                                            │
│                    └─────────────────┘                                            │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 2.3 Production Workflow (production.yml)

**Trigger:** Push to `main` branch (or manual dispatch)

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                           PRODUCTION WORKFLOW (Blue-Green)                           │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Phase 1: Approval & Security                                                        │
│  ────────────────────────────────                                                    │
│                                                                                      │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐                   │
│  │    Manual       │  │    Security     │  │     DB         │                   │
│  │   Approval      │  │     Scan        │  │   Migration    │                   │
│  │   (Required)    │  │  • Trivy        │  │                │                   │
│  │                 │  │  • SAST        │  │ • Dry run      │                   │
│  │                 │  │  • Secrets     │  │ • Apply        │                   │
│  └─────────────────┘  └─────────────────┘  └─────────────────┘                   │
│           │                   │                   │                                │
│           └───────────────────┴───────────────────┘                                │
│                               │                                                     │
│                               ▼                                                     │
│  Phase 2: Build & Deploy                                                           │
│  ────────────────────────                                                          │
│                                                                                      │
│  ┌────────────────────────────────────────────────────────────────────────────┐   │
│  │                      BLUE-GREEN DEPLOYMENT                                   │   │
│  │                                                                            │   │
│  │         ┌─────────────────────────────────────────────┐                  │   │
│  │         │           KUBERNETES CLUSTER                 │                  │   │
│  │         │                                              │                  │   │
│  │         │   BLUE (Active)        GREEN (Standby)     │                  │   │
│  │         │   • user-service       • user-service        │                  │   │
│  │         │   • order-service     • order-service        │                  │   │
│  │         │   • auth-service      • auth-service         │                  │   │
│  │         │   • credit-service    • credit-service       │                  │   │
│  │         │   • ai-gateway       • ai-gateway           │                  │   │
│  │         │                                              │                  │   │
│  │         └─────────────────────────────────────────────┘                  │   │
│  │                               │                                             │   │
│  │                               │ Deploy to GREEN                           │   │
│  │                               ▼                                             │   │
│  │         ┌─────────────────────────────────────────────┐                  │   │
│  │         │           SMOKE TESTS PASS?                  │                  │   │
│  │         │                                              │                  │   │
│  │         │   YES ─────────────► Switch Traffic ────► Cleanup BLUE        │   │
│  │         │                                              │                  │   │
│  │         │   NO ──────────────► Rollback ────────────► Alert            │   │
│  │         │                                              │                  │   │
│  │         └─────────────────────────────────────────────┘                  │   │
│  │                                                                            │   │
│  └────────────────────────────────────────────────────────────────────────────┘   │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 3. GitHub Actions Workflows

### 3.1 Workflow Files

```
.github/
└── workflows/
    ├── dev.yml              # Development CI (on push to develop)
    ├── staging.yml          # Staging Deploy (on push to staging)
    ├── production.yml       # Production Deploy (on push to main)
    └── docker-compose.test.yml  # Test environment config
```

### 3.2 Secrets Required

```
GitHub Repository Secrets:
──────────────────────────

AWS_ACCESS_KEY_ID         # AWS credentials for ECR/Kubernetes
AWS_SECRET_ACCESS_KEY
KUBE_CONFIG_STAGING       # kubeconfig for staging cluster
KUBE_CONFIG_PROD         # kubeconfig for production cluster
SLACK_WEBHOOK_URL        # Slack notifications
VNPAY_TMN_CODE           # Payment gateway (staging)
VNPAY_HASH_SECRET        # Payment gateway (staging)
EAS_BUILD_CREDENTIALS    # Expo build credentials (iOS/Android)
```

### 3.3 Environment Variables

```yaml
# All workflows share these
REGISTRY: ghcr.io
IMAGE_NAME: ${{ github.repository }}
AWS_REGION: ap-southeast-1

# Staging specific
ECR_REPOSITORY: aicafe-staging
CLUSTER_NAME: aicafe-staging
NAMESPACE: staging

# Production specific
PRODUCTION_CLUSTER: aicafe-prod
PREPROD_CLUSTER: aicafe-preprod
```

---

## 4. Docker Configuration

### 4.1 Docker Compose Files

```
Project Root/
├── docker-compose.yml              # Local development (full stack)
└── .github/workflows/
    └── docker-compose.test.yml     # CI testing environment
```

### 4.2 Local Development Setup

```bash
# Start all services locally
docker-compose up -d

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Rebuild specific service
docker-compose up -d --build user-service

# Access specific service
docker-compose exec postgres psql -U aicafe -d aicafe_dev
```

### 4.3 Service Ports

| Service | Port | Description |
|---------|------|-------------|
| PostgreSQL | 5432 | Database |
| Redis | 6379 | Cache |
| Kafka | 9092 | Message broker |
| API Gateway (Kong) | 8000 | HTTP API |
| Kong Admin | 8001 | Admin API |
| User Service | 8081 | User management |
| Order Service | 8082 | Order processing |
| Payment Service | 8083 | Payment gateway |
| Auth Service | 8084 | Authentication |
| Credit Service | 8085 | Credit management |
| AI Gateway | 8086 | AI model proxy |
| Notification Service | 8087 | Push notifications |
| Web App | 3000 | Customer frontend |
| Admin Dashboard | 3001 | Admin frontend |
| Prometheus | 9090 | Metrics |
| Grafana | 3002 | Dashboards |
| Jaeger | 16686 | Distributed tracing |

---

## 5. Kubernetes Deployment

### 5.1 Cluster Architecture

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                           KUBERNETES ARCHITECTURE                                  │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Production Cluster (EKS)                                                            │
│  ──────────────────────────                                                           │
│                                                                                      │
│                         ┌─────────────────┐                                        │
│                         │   ingress-nginx │                                        │
│                         │   (Load Balancer)│                                        │
│                         └────────┬────────┘                                        │
│                                  │                                                  │
│           ┌──────────────────────┼──────────────────────┐                         │
│           │                      │                      │                          │
│           ▼                      ▼                      ▼                          │
│  ┌─────────────────┐   ┌─────────────────┐   ┌─────────────────┐               │
│  │    api-gateway  │   │     web-app     │   │  admin-dashboard │               │
│  │   (kong/ingress)│   │  (Next.js)      │   │   (Next.js)     │               │
│  └────────┬────────┘   └────────┬────────┘   └────────┬────────┘               │
│           │                      │                      │                       │
│           └──────────────────────┼──────────────────────┘                       │
│                                  │                                               │
│           ┌─────────────────────┼─────────────────────┐                        │
│           │                     │                     │                        │
│           ▼                     ▼                     ▼                        │
│  ┌─────────────────────────────────────────────────────────────────┐          │
│  │                        Services Layer                             │          │
│  │                                                                  │          │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐        │          │
│  │  │ user-svc │  │order-svc │  │payment-svc│  │ auth-svc │        │          │
│  │  └──────────┘  └──────────┘  └──────────┘  └──────────┘        │          │
│  │                                                                  │          │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐                      │          │
│  │  │credit-svc│  │ ai-gateway│ │notif-svc │                      │          │
│  │  └──────────┘  └──────────┘  └──────────┘                      │          │
│  │                                                                  │          │
│  └─────────────────────────────────────────────────────────────────┘          │
│                                  │                                               │
│  ┌───────────────────────────────┼───────────────────────────────┐               │
│  │                               ▼                               │               │
│  │  ┌──────────┐  ┌──────────┐  ┌──────────┐                   │               │
│  │  │PostgreSQL│  │  Redis   │  │   S3     │                   │               │
│  │  │  (RDS)   │  │(ElastiCache)│ │(Assets)  │                   │               │
│  │  └──────────┘  └──────────┘  └──────────┘                   │               │
│  │                                                                  │               │
│  └─────────────────────────────────────────────────────────────────┘               │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 5.2 Blue-Green Deployment Strategy

```yaml
# Each service has 2 deployments: blue and green
# Traffic switches via Kubernetes Service selector

apiVersion: v1
kind: Service
metadata:
  name: user-service
spec:
  selector:
    app: user-service
    color: blue  # Switch between 'blue' and 'green'
  ports:
    - port: 80
      targetPort: 8080
---
# Deployments
apiVersion: apps/v1
kind: Deployment
metadata:
  name: user-service-blue
spec:
  replicas: 3
  selector:
    matchLabels:
      app: user-service
      color: blue
  template:
    metadata:
      labels:
        app: user-service
        color: blue
```

---

## 6. Monitoring & Observability

### 6.1 Stack

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                         OBSERVABILITY STACK                                        │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Metrics                    │  Logs                     │  Traces                 │
│  ───────────────────────────┼───────────────────────────┼───────────────────────── │
│  Prometheus                 │  Loki                     │  Jaeger                  │
│  Grafana (Dashboards)       │  Grafana (Explore)        │  Tempo                  │
│                                                                                      │
│  Alerts:                                                                             │
│  ───────                                                                             │
│  • High error rate (>1%)                                                               │
│  • High latency (p99 > 500ms)                                                        │
│  • Pod restart loops                                                                  │
│  • Disk pressure                                                                      │
│  • Memory pressure                                                                    │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 6.2 Alerting Channels

| Severity | Channel | Response Time |
|----------|---------|--------------|
| Critical | PagerDuty + Slack | 5 minutes |
| High | Slack #alerts-high | 15 minutes |
| Medium | Slack #alerts-medium | 1 hour |
| Low | Email daily digest | Next business day |

---

## 7. Deployment Checklist

### 7.1 Pre-Deployment

```
☐ Code reviewed and approved
☐ All tests passing
☐ Security scan completed (no critical issues)
☐ Database migration reviewed
☐ Rollback plan prepared
☐ Team notified of deployment window
☐ Stakeholders available for verification
```

### 7.2 Post-Deployment

```
☐ Smoke tests passed
☐ Error rates normal
☐ Latency within SLA
☐ All services healthy
☐ Key user flows working
☐ Monitoring dashboards verified
☐ Rollback plan archived
☐ Deployment documented
☐ Team notified of completion
```

### 7.3 Rollback Criteria

```
Trigger automatic rollback if:
─────────────────────────────────────
• Error rate > 5% (sustained for 5 minutes)
• API latency p99 > 2 seconds
• Health check failures > 50%
• Critical security vulnerability discovered
• Manual trigger by on-call engineer
```

---

## 8. Repository Secrets Setup

### 8.1 Required GitHub Secrets

```bash
# AWS Credentials (for ECR and EKS)
AWS_ACCESS_KEY_ID=AKIA...
AWS_SECRET_ACCESS_KEY=...

# Kubernetes Configs (base64 encoded kubeconfig files)
KUBE_CONFIG_STAGING=<base64-encoded-staging-kubeconfig>
KUBE_CONFIG_PROD=<base64-encoded-prod-kubeconfig>

# Slack for notifications
SLACK_WEBHOOK_URL=https://hooks.slack.com/services/...

# Payment Gateway (Staging)
VNPAY_TMN_CODE=TEST
VNPAY_HASH_SECRET=TEST

# Expo Build (for mobile apps)
EAS_BUILD_CREDENTIALS={"buildCredentials":...}
```

### 8.2 Generating Kubeconfig

```bash
# Get EKS cluster credentials
aws eks update-kubeconfig --name aicafe-staging --region ap-southeast-1

# Encode for GitHub secret
cat ~/.kube/config | base64

# For multiple clusters, create separate kubeconfig files
aws eks update-kubeconfig --name aicafe-staging --region ap-southeast-1 --kubeconfig kubeconfig-staging
aws eks update-kubeconfig --name aicafe-prod --region ap-southeast-1 --kubeconfig kubeconfig-prod
```

---

## 9. Common Operations

### 9.1 Manual Deployment

```bash
# Deploy to staging (manual trigger)
gh workflow run staging.yml

# Deploy specific service
kubectl set image deployment/user-service user-service=<image> -n staging

# Rollback to previous version
kubectl rollout undo deployment/user-service -n staging

# Check deployment status
kubectl rollout status deployment/user-service -n staging
```

### 9.2 Debugging

```bash
# Get pod logs
kubectl logs -f deployment/user-service -n staging

# Exec into pod
kubectl exec -it <pod-name> -n staging -- /bin/sh

# Check pod events
kubectl describe pod <pod-name> -n staging

# Port forward for local testing
kubectl port-forward svc/user-service 8080:80 -n staging
```

### 9.3 Database Operations

```bash
# Run migration manually
kubectl run db-migration \
  --image=<ecr-repo>/user-service:<sha> \
  --restart=Never \
  --namespace=production \
  --overrides='{"spec":{"template":{"spec":{"containers":[{"name":"migration","command":["java","-jar","/app/user-service.jar","migrate"]}]}}}}'

# Wait for migration
kubectl wait --for=condition=complete job/db-migration --timeout=300s -n production
```

---

## 10. Disaster Recovery

### 10.1 Backup Strategy

| Data | Frequency | Retention | Storage |
|------|-----------|-----------|---------|
| Database | Daily + WAL | 30 days | S3 Cross-region |
| Redis | Nightly snapshot | 7 days | S3 |
| Kubernetes configs | On change | Git history | GitHub |
| Docker images | On deploy | 30 images | ECR |

### 10.2 Recovery Procedures

```
RTO (Recovery Time Objective): 1 hour
RPO (Recovery Point Objective): 1 hour
─────────────────────────────────────

Database Recovery:
1. Restore from latest snapshot
2. Apply WAL replay to point-in-time
3. Verify data integrity
4. Update DNS if needed

Full Cluster Recovery:
1. Recreate EKS cluster
2. Deploy from latest successful image tags
3. Restore database from backup
4. Update ingress/DNS
5. Verify all services
```

---

## 11. Cost Optimization

### 11.1 Kubernetes Resource Management

```yaml
# Resource requests and limits for cost control
resources:
  requests:
    memory: "256Mi"
    cpu: "250m"
  limits:
    memory: "512Mi"
    cpu: "500m"

# Horizontal Pod Autoscaler
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: user-service-hpa
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: user-service
  minReplicas: 2
  maxReplicas: 10
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 70
```

### 11.2 Cost Monitoring

```
Track in Grafana dashboard:
• AWS Cost Explorer integration
• Per-namespace resource usage
• Idle resources
• Spot instance coverage
• ECR storage usage
```

---

## 12. Quick Reference

### 12.1 CI/CD Commands

```bash
# Trigger workflows
gh workflow run dev.yml
gh workflow run staging.yml
gh workflow run production.yml

# Check workflow status
gh run list --workflow=dev.yml

# Cancel running workflow
gh run cancel <run-id>

# View workflow logs
gh run view <run-id> --log
```

### 12.2 Kubernetes Commands

```bash
# Switch context
kubectl config use-context aicafe-staging
kubectl config use-context aicafe-prod

# List all resources
kubectl get all -n staging
kubectl get all -n production

# Scale deployment
kubectl scale deployment/user-service --replicas=5 -n production

# Restart deployment (rolling restart)
kubectl rollout restart deployment/user-service -n production

# View resource usage
kubectl top pods -n production
kubectl top nodes
```

### 12.3 Docker Commands

```bash
# Build and push image
docker build -t ghcr.io/owner/repo/service:sha .
docker push ghcr.io/owner/repo/service:sha

# Run local stack
docker-compose up -d
docker-compose logs -f user-service

# Cleanup
docker-compose down -v
docker system prune -f
```
