# AI Café - Model Router

## Overview

Model Router tự động chọn model phù hợp dựa trên task, tier, và chi phí.

## Auto Model Routing Flow

```
User Request
     │
     ▼
Task Classifier
     │
     ▼
┌─────────────────┐
│    Task Type    │
└─────────────────┘
     │
┌────┼────┐
│     │     │
▼     ▼     ▼
Coding Reasoning Media
 │     │     │
 ▼     ▼     ▼
Coding Reasoning Image/Video
Model  Model   Model
```

## Router Decision Factors

1. **Customer Tier** - AI access level
2. **Task** - Type of work requested
3. **Credit balance** - Available credits
4. **Provider cost** - Cheapest suitable option
5. **Provider latency** - Response speed
6. **Provider availability** - Uptime
7. **Rate limits** - Provider quotas
8. **Model capability** - Task fit

## Task Types

| Task | Primary Models | Fallback |
|------|---------------|----------|
| Chat | Reasoning | Standard |
| Coding | Coding | Standard |
| Reasoning | Reasoning | Premium |
| Image | Image | Premium |
| Video | Video | Premium |
| Analysis | Reasoning | Standard |

## AI Menu

Thay vì bắt khách chọn model, giao diện bắt đầu bằng nhiệm vụ:

```
What do you want to do?

[ 📝 Code ]
[ 🐛 Debug ]
[ 🌐 Build Website ]
[ 📊 Analyze Data ]
[ 🎨 Generate Image ]
[ 🎬 Generate Video ]
[ ✍️ Write Content ]
[ 🧠 Reason ]
[ 📚 Learn AI ]
```

**Example flow:**
```
Build Website
     │
     ▼
Premium Coding Model
     +
Workspace
     +
Code Tools
```

## Provider Architecture

Không phụ thuộc một provider:

```
                 AI GATEWAY
                      │
     ┌────────────────┼────────────────┐
     │                │                │
 Provider A      Provider B      Provider C
     │                │                │
  Chat/Coding      Reasoning      Image/Video
```

**Provider được chọn theo:**
- Cost
- Quality
- Latency
- Availability
- Rate limits
- Context
- Capability

## Task Classifier

Internal classifier để route request:

```python
classify_task(user_input) → {
    "type": "coding" | "reasoning" | "media" | "chat",
    "subtype": "debug" | "write" | "refactor" | "explain",
    "complexity": "low" | "medium" | "high",
    "estimated_tokens": 1000
}
```

## Dynamic Model Selection

```python
def select_model(task, tier, credits, budget):
    available_models = get_models_for_tier(tier)
    
    # Filter by task capability
    suitable = [m for m in available_models if m.supports(task)]
    
    # Sort by cost
    suitable.sort(key=lambda m: m.cost_per_token)
    
    # Check budget
    if total_monthly_cost > budget * 0.95:
        suitable = downgrade_models(suitable)
    
    return suitable[0]
```

## Cost Optimization

```python
if budget_at_80_percent():
    log_warning("AI spending approaching limit")
    
if budget_at_95_percent():
    send_alert_to_admin()
    
if budget_at_100_percent():
    auto_downgrade_models()
    # Premium → Standard → Economy
    # Or block video
```
