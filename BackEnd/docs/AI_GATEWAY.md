# AI Café - AI Gateway

## Overview

AI Gateway là thành phần trung tâm của hệ thống.

## Architecture

```
Customer
   │
   ▼
API Gateway
   ├── Authentication
   ├── Authorization
   ├── Credit Check
   ├── Time Check
   ├── Rate Limit
   ├── Daily Limit
   ├── Budget Check
   ├── Model Router
   └── Usage Tracking
           │
           ▼
      AI Providers
```

## Responsibilities

### Authentication
- Verify user session
- Issue session tokens
- Track user identity

### Authorization
- Check user tier
- Verify AI access rights
- Validate workspace time

### Credit Check
- Verify sufficient credits
- Calculate credit cost
- Deduct credits

### Time Check
- Verify workspace time remaining
- Track workspace usage
- Handle session timeout

### Rate Limiting

| Tier | Requests/minute |
|------|-----------------|
| Basic | 10 RPM |
| Developer | 30 RPM |
| Pro | 60 RPM |
| Builder | 120 RPM |

### Daily Limit
```
Daily limit: 5,000 Credits
Used: 4,800
Remaining today: 200
```

### Budget Check
- Monitor daily/monthly AI budget
- Alert at 80%
- Warning at 95%
- Restrict at 100%

### Model Router
- Classify task type
- Select appropriate model
- Consider cost/quality/latency

### Usage Tracking
- Log all requests
- Track tokens used
- Calculate provider cost

## Security

### API Key Handling

**Không bao giờ:**
```
Browser → Provider API
```

**Phải:**
```
Browser → AI Café Backend → AI Gateway → Provider API
```

API keys phải được lưu server-side.

**Khách chỉ nhận:**
- AI Café Session Token
- AI Café API Key

## Rate Limiting Per User

- Requests / minute
- Tokens / minute
- Daily Credits
- Concurrent Requests
- Video Requests / day

## Fraud Protection

Hệ thống cần phát hiện:
- Bot
- Spam
- Credit farming
- Account abuse
- API key sharing
- Refund abuse
- Video abuse
- Concurrent request abuse

**Biện pháp:**
- Rate limit
- Device monitoring
- IP monitoring
- Session management
- Usage anomaly detection
- Daily limits

## Budget Protection

```
Daily AI Budget: 500,000đ
Monthly AI Budget: 15,000,000đ
```

**When spending increases:**

| Threshold | Action |
|-----------|--------|
| 80% | Monitor |
| 95% | Warning |
| 100% | Restrict expensive models |

**Auto-adjust:**
```
Premium → Standard → Economy
```
Hoặc khóa video.
