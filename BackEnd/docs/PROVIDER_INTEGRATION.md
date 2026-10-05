# AI Café - Provider Integration

## Overview

AI Gateway kết nối với nhiều AI Provider để cung cấp dịch vụ AI đa dạng.

## Supported Providers

| Provider | Services | Notes |
|----------|----------|-------|
| OpenAI | Chat, Coding, Image, Video | Primary |
| Anthropic | Chat, Reasoning | Reasoning focus |
| Google | Chat, Image | Cost-effective |
| Stability | Image | Image specialist |
| Midjourney | Image | Premium quality |

## Provider Configuration

```yaml
providers:
  openai:
    api_key: ${OPENAI_API_KEY}
    base_url: https://api.openai.com/v1
    rate_limits:
      requests_per_minute: 60
      tokens_per_minute: 90000
    models:
      - gpt-4o
      - gpt-4o-mini
      - gpt-4-turbo
      - dall-e-3
  
  anthropic:
    api_key: ${ANTHROPIC_API_KEY}
    base_url: https://api.anthropic.com/v1
    rate_limits:
      requests_per_minute: 50
    models:
      - claude-3-5-sonnet
      - claude-3-5-haiku
      - claude-3-opus
  
  google:
    api_key: ${GOOGLE_API_KEY}
    base_url: https://generativelanguage.googleapis.com/v1
    rate_limits:
      requests_per_minute: 60
    models:
      - gemini-1.5-pro
      - gemini-1.5-flash
      - imagen-3
```

## Usage Tracking

```sql
provider_usage
- id
- provider_id
- model
- input_tokens
- output_tokens
- cached_tokens
- request_count
- total_cost
- period (hour/day/month)
- created_at

ai_costs
- id
- provider_id
- model
- cost_per_input_token
- cost_per_output_token
- effective_date
- created_at
```

## Cost Calculation

```python
def calculate_provider_cost(provider, model, usage):
    pricing = get_model_pricing(provider, model)
    
    cost = (
        usage.input_tokens * pricing.input_cost +
        usage.output_tokens * pricing.output_cost +
        usage.cached_tokens * pricing.cached_cost
    )
    
    return cost
```

## Provider Failover

```python
async def call_with_failover(task, user_tier):
    providers = get_available_providers()
    
    for provider in providers:
        if not provider.supports(task):
            continue
        
        if provider.is_available():
            try:
                return await provider.execute(task)
            except RateLimitError:
                continue
            except ServiceUnavailable:
                continue
    
    raise AllProvidersUnavailable()
```

## Monitoring

### Admin Dashboard Metrics

```
AI PROVIDER STATUS
━━━━━━━━━━━━━━━━━━━
OpenAI     ✓ Healthy
Anthropic  ✓ Healthy  
Google     ✓ Healthy
Stability  ⚠ Degraded

COST BREAKDOWN
━━━━━━━━━━━━━━━━━━━
Today:      420,000đ
This week:  2.8M
This month: 9.2M

PROVIDER USAGE
━━━━━━━━━━━━━━━━━━━
OpenAI:     65%
Anthropic:  25%
Google:     10%
```

## Provider Selection Logic

```python
def select_provider(task_type, user_tier, budget_status):
    if task_type == "reasoning":
        if user_tier in ["pro", "builder"]:
            return anthropic  # Best reasoning
        return openai  # Fallback
    
    if task_type == "image":
        if budget_status == "critical":
            return google  # Cheapest
        return stability  # Best quality
    
    if task_type == "video":
        return openai  # Only one supports
    
    return openai  # Default
```

## API Key Management

- All API keys stored server-side
- Keys encrypted at rest
- Keys rotated periodically
- No keys exposed to frontend
- Audit log for all API calls
