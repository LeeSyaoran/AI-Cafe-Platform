# AI Café Platform - Domain & DNS Architecture

## 1. Domain Overview

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                           DOMAIN STRUCTURE                                          │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Primary Domain:                                                                    │
│  ───────────────                                                                    │
│                                                                                      │
│                         aicafe.vn                                                  │
│                              │                                                      │
│         ┌────────────────────┼────────────────────┐                                 │
│         │                    │                    │                                   │
│         ▼                    ▼                    ▼                                   │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                        │
│  │   WWW       │    │    API      │    │  STATIC     │                        │
│  │  Landing    │    │  Gateway   │    │   Assets    │                        │
│  │  Page       │    │             │    │             │                        │
│  └─────────────┘    └─────────────┘    └─────────────┘                        │
│         │                    │                    │                                   │
│         ▼                    ▼                    ▼                                   │
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐                        │
│  │  Customer   │    │   Admin     │    │   POS       │                        │
│  │   Web       │    │  Dashboard  │    │   Portal    │                        │
│  └─────────────┘    └─────────────┘    └─────────────┘                        │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 2. DNS Records Configuration

### 2.1 Production DNS Records

```
Zone: aicafe.vn
Provider: Cloudflare

┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              DNS RECORDS (PRODUCTION)                                │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Type    │ Name            │ Value                           │ TTL   │ Proxy │
│  ────────┼────────────────┼─────────────────────────────────┼───────┼────────│
│  A       │ @               │ 76.76.21.21 (CDN IP)           │ Auto  │  ✓     │
│  CNAME   │ www             │ aicafe.pages.dev               │ Auto  │  ✓     │
│  ────────┼────────────────┼─────────────────────────────────┼───────┼────────│
│  A       │ api             │ <ELB DNS>                      │ Auto  │  ✓     │
│  CNAME   │ admin           │ <CloudFront distribution>       │ Auto  │  ✓     │
│  CNAME   │ pos             │ <CloudFront distribution>       │ Auto  │  ✓     │
│  CNAME   │ dashboard       │ <CloudFront distribution>       │ Auto  │  ✓     │
│  ────────┼────────────────┼─────────────────────────────────┼───────┼────────│
│  CNAME   │ cdn             │ cdn.aicafe.pages.dev           │ Auto  │  ✓     │
│  CNAME   │ assets          │ assets.aicafe.pages.dev         │ Auto  │  ✓     │
│  ────────┼────────────────┼─────────────────────────────────┼───────┼────────│
│  CNAME   │ mail            │ mail.aicafe.pages.dev          │ Auto  │  ✓     │
│  MX      │ @               │ 10 mail.aicafe.vn              │ Auto  │  ✗     │
│  ────────┼────────────────┼─────────────────────────────────┼───────┼────────│
│  TXT     │ @               │ v=spf1 include:sendgrid.net ~all│ Auto  │  ✗     │
│  TXT     │ _dmarc          │ v=DMARC1; p=quarantine; rua=mailto:dmarc@aicafe.vn │ Auto │ ✗ │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 2.2 Staging DNS Records

```
Zone: aicafe-staging.io (hoặc aicafe.vn với prefix)
Provider: Cloudflare

┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              DNS RECORDS (STAGING)                                   │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Type    │ Name                │ Value                       │ TTL   │ Proxy │
│  ────────┼────────────────────┼─────────────────────────────┼───────┼────────│
│  CNAME   │ staging            │ <staging ELB DNS>          │ Auto  │  ✓     │
│  CNAME   │ api-staging        │ <staging API ELB DNS>      │ Auto  │  ✓     │
│  CNAME   │ admin-staging      │ <staging admin CloudFront> │ Auto  │  ✓     │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 2.3 Development DNS Records (Local)

```
# Local development sử dụng /etc/hosts hoặc dnsmasq

# /etc/hosts (macOS/Linux)
127.0.0.1   localhost
127.0.0.1   api.local.aicafe.vn
127.0.0.1   web.local.aicafe.vn
127.0.0.1   admin.local.aicafe.vn
127.0.0.1   pos.local.aicafe.vn
```

---

## 3. Subdomain Architecture

### 3.1 Subdomain Matrix

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              SUBDOMAIN MATRIX                                       │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Subdomain              │ Service           │ Environment │ Description             │
│  ──────────────────────┼───────────────────┼─────────────┼────────────────────────│
│  aicafe.vn              │ Customer Web      │ Production │ Landing & Login         │
│  www.aicafe.vn          │ Customer Web      │ Production │ Alias for main          │
│  api.aicafe.vn          │ API Gateway       │ Production │ REST + WebSocket API    │
│  admin.aicafe.vn        │ Admin Dashboard   │ Production │ Admin Panel            │
│  pos.aicafe.vn          │ POS Portal        │ Production │ POS Management          │
│  dashboard.aicafe.vn    │ Analytics         │ Production │ BI Dashboard            │
│  cdn.aicafe.vn          │ Static Assets     │ Production │ CDN for images/files    │
│  assets.aicafe.vn       │ Uploads           │ Production │ User uploaded files     │
│  ───────────────────────┼───────────────────┼─────────────┼────────────────────────│
│  staging.aicafe.vn      │ Customer Web      │ Staging    │ Staging version         │
│  api-staging.aicafe.vn  │ API Gateway       │ Staging    │ Staging API             │
│  admin-staging.aicafe.vn│ Admin Dashboard   │ Staging    │ Staging Admin          │
│  ───────────────────────┼───────────────────┼─────────────┼────────────────────────│
│  *.preview.aicafe.vn    │ Preview Apps     │ Preview    │ PR Preview Deployments  │
│  ───────────────────────┼───────────────────┼─────────────┼────────────────────────│
│  dev.aicafe.vn          │ Dev Environment   │ Dev        │ Development Server      │
│  api-dev.aicafe.vn      │ API Dev           │ Dev        │ Development API        │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 3.2 Service URLs Summary

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              SERVICE ENDPOINTS                                       │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Environment  │ Service           │ URL                                      │
│  ──────────────┼───────────────────┼──────────────────────────────────────────│
│  PRODUCTION    │ Customer Web      │ https://aicafe.vn                       │
│               │ Admin Dashboard   │ https://admin.aicafe.vn                  │
│               │ POS Portal       │ https://pos.aicafe.vn                    │
│               │ API Gateway      │ https://api.aicafe.vn                    │
│               │ WebSocket        │ wss://api.aicafe.vn/ws                  │
│               │ CDN              │ https://cdn.aicafe.vn                    │
│               │ Assets           │ https://assets.aicafe.vn                 │
│  ──────────────┼───────────────────┼──────────────────────────────────────────│
│  STAGING       │ Customer Web      │ https://staging.aicafe.vn               │
│               │ Admin Dashboard   │ https://admin-staging.aicafe.vn          │
│               │ API Gateway      │ https://api-staging.aicafe.vn             │
│  ──────────────┼───────────────────┼──────────────────────────────────────────│
│  DEVELOPMENT   │ Customer Web      │ http://localhost:3000                   │
│               │ Admin Dashboard   │ http://localhost:3001                   │
│               │ API Gateway      │ http://localhost:8000 (Kong)            │
│               │ User Service     │ http://localhost:8081                   │
│               │ Order Service    │ http://localhost:8082                   │
│               │ Auth Service     │ http://localhost:8084                   │
│               │ Credit Service   │ http://localhost:8085                   │
│               │ AI Gateway      │ http://localhost:8086                   │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 4. SSL/TLS Configuration

### 4.1 Certificate Management

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              SSL CERTIFICATES                                       │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Provider: Let's Encrypt (for development) / AWS ACM (for production)              │
│                                                                                      │
│  Production Certificates (AWS ACM + CloudFront):                                    │
│  ─────────────────────────────────────────────────────────                         │
│                                                                                      │
│  Certificate for: *.aicafe.vn + aicafe.vn                                          │
│  Issuer: Amazon                                                                    │
│  Validation: DNS                                                                    │
│  Auto-renewal: Yes (90 days before expiry)                                         │
│                                                                                      │
│  Staging Certificates (Let's Encrypt):                                             │
│  ───────────────────────────────────────────────                                   │
│                                                                                      │
│  Certificate for: *.aicafe-staging.io                                               │
│  Issuer: Let's Encrypt                                                            │
│  Validation: DNS                                                                   │
│  Auto-renewal: Yes via cert-manager in Kubernetes                                  │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 TLS Versions & Ciphers

```
# Recommended TLS Configuration for Production

ssl_protocols TLSv1.2 TLSv1.3;
ssl_prefer_server_ciphers off;
ssl_ciphers ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256:ECDHE-ECDSA-AES256-GCM-SHA384:ECDHE-RSA-AES256-GCM-SHA384;

# HSTS Header
add_header Strict-Transport-Security "max-age=31536000; includeSubDomains; preload" always;

# Security Headers
add_header X-Frame-Options "SAMEORIGIN" always;
add_header X-Content-Type-Options "nosniff" always;
add_header X-XSS-Protection "1; mode=block" always;
add_header Referrer-Policy "strict-origin-when-cross-origin" always;
```

---

## 5. CDN Configuration (CloudFront)

### 5.1 CloudFront Distributions

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                           CLOUDFRONT DISTRIBUTIONS                                 │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Distribution 1: Customer Web                                                      │
│  ────────────────────────────────────                                              │
│  Domain: d1abc123.cloudfront.net (assigned)                                       │
│  Alias: aicafe.vn, www.aicafe.vn                                                 │
│  Origin: ALB (Application Load Balancer)                                          │
│  Behaviors: Static + Dynamic                                                      │
│  Price Class: Use all edge locations                                               │
│  SSL: CloudFront Managed                                                          │
│                                                                                      │
│  Distribution 2: Admin Dashboard                                                   │
│  ────────────────────────────────────                                              │
│  Domain: d4xyz456.cloudfront.net                                                  │
│  Alias: admin.aicafe.vn                                                           │
│  Origin: S3 bucket (Next.js export)                                              │
│  Behaviors: Static only                                                            │
│  SSL: CloudFront Managed                                                          │
│                                                                                      │
│  Distribution 3: Static Assets                                                     │
│  ────────────────────────────────────                                              │
│  Domain: d7assets789.cloudfront.net                                               │
│  Alias: cdn.aicafe.vn, assets.aicafe.vn                                          │
│  Origin: S3 bucket (public assets)                                               │
│  Behaviors: Cache everything                                                       │
│  TTL: Default 24 hours                                                            │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 5.2 Cache Behavior

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              CACHE BEHAVIORS                                        │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Path Pattern         │ Origin              │ TTL        │ Cookies    │
│  ────────────────────┼────────────────────┼────────────┼────────────│
│  /_next/static/*     │ S3 / ALB           │ 1 year    │ None       │
│  /static/*           │ S3                 │ 1 year    │ None       │
│  /api/*              │ ALB (API Gateway)  │ No cache  │ Forward    │
│  /_next/*            │ ALB (SSR)          │ 0 (no-cache) │ Forward │
│  *.html              │ S3                 │ 1 hour    │ None       │
│  /*                  │ ALB (SSR)          │ 0         │ Forward    │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 6. API Gateway Routes (Kong)

### 6.1 Kong Configuration

```yaml
# infrastructure/kong/kong.yml

_format_version: "3.0"

services:
  # User Service
  - name: user-service
    url: http://user-service:8080
    routes:
      - name: user-api
        paths:
          - /api/v1/users
        methods:
          - GET
          - POST
          - PUT
          - DELETE
        strip_path: false

  # Order Service
  - name: order-service
    url: http://order-service:8080
    routes:
      - name: order-api
        paths:
          - /api/v1/orders
        methods:
          - GET
          - POST
          - PUT
        strip_path: false

  # Auth Service
  - name: auth-service
    url: http://auth-service:8080
    routes:
      - name: auth-api
        paths:
          - /api/v1/auth
        methods:
          - POST
        strip_path: false

  # Credit Service
  - name: credit-service
    url: http://credit-service:8080
    routes:
      - name: credit-api
        paths:
          - /api/v1/credits
        methods:
          - GET
          - POST
        strip_path: false

  # AI Gateway
  - name: ai-gateway
    url: http://ai-gateway:8080
    routes:
      - name: ai-api
        paths:
          - /api/v1/ai
        methods:
          - POST
        strip_path: false

  # WebSocket
  - name: websocket-service
    url: http://ai-gateway:8080
    routes:
      - name: ws-route
        paths:
          - /ws
        protocols:
          - ws
          - wss

plugins:
  # Rate Limiting
  - name: rate-limiting
    config:
      minute: 100
      hour: 1000
      policy: redis

  # CORS
  - name: cors
    config:
      origins:
        - "https://aicafe.vn"
        - "https://admin.aicafe.vn"
      methods:
        - GET
        - POST
        - PUT
        - DELETE
        - OPTIONS
      headers:
        - Authorization
        - Content-Type
      credentials: true

  # JWT Authentication
  - name: jwt
    config:
      uri_param_names:
        - token
      cookie_names: []

  # Request Transformer
  - name: request-transformer
    config:
      add:
        headers:
          - X-Kong-Upstream-Latency
```

### 6.2 API Versioning

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              API VERSIONING                                          │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  URL Pattern: /api/v{version}/{resource}                                            │
│                                                                                      │
│  Current Versions:                                                                   │
│  ───────────────                                                                    │
│                                                                                      │
│  Version   │ Status      │ Sunset Date   │ Notes                                  │
│  ──────────┼─────────────┼───────────────┼────────────────────────────────────────│
│  v1        │ Current     │ -             │ Stable, production ready              │
│  v2        │ In Progress │ -             │ Breaking changes from v1              │
│  v1beta    │ Deprecated  │ 2025-06-01    │ Legacy, migrate to v1 or v2          │
│  v0        │ Sunset      │ 2024-12-01    │ Removed                               │
│                                                                                      │
│  Migration Path:                                                                    │
│  ──────────────                                                                     │
│                                                                                      │
│  v0 → v1beta → v1 → v2                                                             │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 7. Third-Party Domains

### 7.1 External Service Domains

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                           EXTERNAL SERVICE DOMAINS                                   │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Service              │ Domain                     │ Purpose                       │
│  ────────────────────┼────────────────────────────┼───────────────────────────────│
│  OpenAI              │ api.openai.com            │ GPT models                    │
│  Anthropic           │ api.anthropic.com         │ Claude models                 │
│  Stripe              │ api.stripe.com             │ Payment processing            │
│  VNPay              │ vnpay.vn                   │ Vietnam payment gateway       │
│  Twilio             │ api.twilio.com             │ SMS OTP                       │
│  SendGrid           │ api.sendgrid.com           │ Email                        │
│  Firebase           │ googleapis.com             │ Push notifications            │
│  Sentry             │ o123456.ingest.sentry.io  │ Error tracking               │
│  Datadog            │ api.datadoghq.com          │ APM and logs                 │
│  AWS S3             │ s3.amazonaws.com          │ File storage                  │
│  Cloudflare         │ cloudflare.net             │ CDN and DNS                   │
│  Google Analytics   │ google-analytics.com       │ Analytics                    │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

### 7.2 Webhook Endpoints

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              WEBHOOK CONFIGURATION                                   │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Outgoing Webhooks (from our system):                                               │
│  ───────────────────────────────────────────                                        │
│                                                                                      │
│  Endpoint                    │ Events              │ Payload Format                 │
│  ────────────────────────────┼─────────────────────┼───────────────────────────────│
│  Partner System A            │ order.completed     │ JSON REST                    │
│  Partner System B           │ user.created        │ JSON REST                    │
│  Internal Analytics         │ *.created, *.updated│ JSON via Kafka               │
│                                                                                      │
│  Incoming Webhooks (to our system):                                                 │
│  ──────────────────────────────────────────                                        │
│                                                                                      │
│  Source              │ Endpoint                          │ Events                   │
│  ───────────────────┼──────────────────────────────────┼───────────────────────────│
│  VNPay              │ https://api.aicafe.vn/webhooks/vnpay │ payment.completed     │
│  Stripe             │ https://api.aicafe.vn/webhooks/stripe │ payment.*           │
│  Twilio             │ https://api.aicafe.vn/webhooks/sms  │ sms.delivered         │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 8. Local Development Setup

### 8.1 hosts file

```bash
# Windows: C:\Windows\System32\drivers\etc\hosts
# macOS/Linux: /etc/hosts

# AI Café Development
127.0.0.1   localhost
127.0.0.1   aicafe.vn
127.0.0.1   api.aicafe.vn
127.0.0.1   admin.aicafe.vn
127.0.0.1   pos.aicafe.vn
```

### 8.2 SSL for Local Development

```bash
# Generate self-signed certificate for local HTTPS
openssl req -x509 -newkey rsa:4096 -keyout key.pem -out cert.pem -days 365 -nodes \
  -subj "/C=VN/ST=HCM/L=HCMC/O=AICafe/CN=*.aicafe.vn"

# Trust the certificate (macOS)
sudo security add-trusted-cert -d -r trustRoot -k /Library/Keychains/System.keychain cert.pem

# Trust the certificate (Linux)
sudo cp cert.pem /usr/local/share/ca-certificates/aicafe.crt
sudo update-ca-certificates
```

---

## 9. DNS Failover Configuration

### 9.1 Health Check Setup

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                              DNS FAILOVER                                           │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│  Primary Endpoint: api.aicafe.vn ───────────────► Health Check                  │
│         │                                                       │                   │
│         │                    ┌─────────────────────────────────┘                   │
│         │                    │                                                     │
│         ▼                    ▼                                                     │
│  ┌─────────────┐     ┌─────────────┐                                            │
│  │   HEALTHY   │────►│  Route DNS │ ──────────────────────► Primary IP          │
│  └─────────────┘     └─────────────┘                                            │
│         │                                                                           │
│         │ Fail                                                              │
│         ▼                                                                   │
│  ┌─────────────┐     ┌─────────────┐                                            │
│  │   UNHEALTHY │────►│  Route DNS │ ──────────────────────► Failover IP        │
│  └─────────────┘     └─────────────┘                                            │
│                                                                                      │
│  Health Check Configuration:                                                        │
│  ───────────────────────────                                                       │
│  • Protocol: HTTPS                                                                 │
│  • Port: 443                                                                       │
│  • Path: /health                                                                    │
│  • Interval: 30 seconds                                                            │
│  • Threshold: 3 consecutive failures                                               │
│  • Timeout: 5 seconds                                                             │
│  • Evaluate: Fastest response                                                      │
│                                                                                      │
└─────────────────────────────────────────────────────────────────────────────────────┘
```

---

## 10. Domain Registrar Setup Checklist

### 10.1 Required DNS Records (Production)

```
Step 1: Configure Nameservers
────────────────────────────────
Point to Cloudflare:
  ns1.cloudflare.com
  ns2.cloudflare.com

Step 2: Add DNS Records
────────────────────────────────
A Record (@):
  Name: @
  Value: 76.76.21.21 (Cloudflare Anycast)
  TTL: Auto
  Proxy: Enabled

CNAME Records:
  www     → aicafe.pages.dev
  api     → <ELB DNS Address>
  admin   → <CloudFront Distribution>
  pos     → <CloudFront Distribution>

MX Records (if using email):
  @       → 10 mail.aicafe.vn

TXT Records:
  @       → v=spf1 include:sendgrid.net ~all
  _dmarc  → v=DMARC1; p=quarantine; rua=mailto:dmarc@aicafe.vn

Step 3: Enable DNSSEC
────────────────────────────────
DS Record (add at registrar):
  Key Tag: <from Cloudflare>
  Algorithm: 13 (ECDSAP256SHA256)
  Digest Type: 2 (SHA-256)
  Digest: <from Cloudflare>
```

### 10.2 Verification Commands

```bash
# Check DNS propagation
dig aicafe.vn
dig api.aicafe.vn
dig admin.aicafe.vn

# Check SSL certificate
openssl s_client -connect api.aicafe.vn:443 -servername api.aicafe.vn </dev/null 2>/dev/null | openssl x509 -noout -dates

# Check DNSSEC
dig DS aicafe.vn
dnssec-debugger.dsviz.net

# Check mail records
dig MX aicafe.vn
dig TXT aicafe.vn
```

---

## 11. Quick Reference

### 11.1 Environment URLs

```
Production:
  Web:       https://aicafe.vn
  Admin:     https://admin.aicafe.vn
  POS:       https://pos.aicafe.vn
  API:       https://api.aicafe.vn
  WebSocket: wss://api.aicafe.vn/ws

Staging:
  Web:       https://staging.aicafe.vn
  Admin:     https://admin-staging.aicafe.vn
  API:       https://api-staging.aicafe.vn

Development:
  Web:       http://localhost:3000
  Admin:     http://localhost:3001
  API:       http://localhost:8000
```

### 11.2 Common Operations

```bash
# Update DNS
# 1. Go to Cloudflare Dashboard
# 2. Select domain aicafe.vn
# 3. Go to DNS > Records
# 4. Add/Edit/Delete records

# Check domain status
whois aicafe.vn
dig aicafe.vn NS

# SSL Certificate Info
echo | openssl s_client -connect api.aicafe.vn:443 -servername api.aicafe.vn 2>/dev/null | openssl x509 -noout -text

# Clear Cloudflare cache
# 1. Go to Cloudflare Dashboard
# 2. Caching > Configuration
# 3. "Purge Everything" button
```
