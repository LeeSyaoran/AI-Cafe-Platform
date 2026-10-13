# AI Café - Roadmap

## Overview

Lộ trình phát triển AI Café Platform theo các giai đoạn.

## Phase 1: MVP (Days 1-30)

**Mục tiêu:** Validate customer demand

### Build
- [ ] Basic tier (30k)
- [ ] Developer tier (50k)
- [ ] Pro tier (80k)

### Support
- [ ] Chat
- [ ] Coding
- [ ] Basic Reasoning

### Track
- [ ] Usage
- [ ] AI Cost
- [ ] Return Rate
- [ ] Upgrade Rate

### Tech Stack
```
Frontend:    React + TailwindCSS
Backend:     Node.js / Python
Database:    PostgreSQL
AI Gateway:  Custom
Providers:   OpenAI (1 provider)
```

## Phase 2: Growth (Days 31-60)

**Mục tiêu:** Expand features and test pricing

### Add
- [ ] Membership tiers
- [ ] Loyalty program
- [ ] Image generation
- [ ] Workspace timer
- [ ] Employee AI

### Test Pricing
- [ ] 30k pricing validation
- [ ] 50k pricing validation
- [ ] 80k pricing validation
- [ ] Membership price points

### Tech
- [ ] Multiple AI Providers
- [ ] Model Router
- [ ] Daily Limits
- [ ] Credit Expiration
- [ ] Promotions engine

## Phase 3: Scale (Days 61-90)

**Mục tiêu:** Full feature set and optimization

### Add
- [ ] Video generation
- [ ] Public API
- [ ] Developer API Keys
- [ ] Team Accounts
- [ ] Referral program
- [ ] Drink Pass

### Analytics
- [ ] Advanced Analytics
- [ ] Provider Failover
- [ ] Automatic Cost Optimization
- [ ] Advanced Workspace

### Business
- [ ] Review pricing based on data
- [ ] Adjust credit allocation
- [ ] Finalize membership structure
- [ ] Negotiate provider contracts
- [ ] Set AI budget

## Post-Launch (Ongoing)

### Customer Features
- [ ] Mobile app
- [ ] Team workspace
- [ ] Organization billing
- [ ] Custom integrations
- [ ] White-label options

### AI Features
- [ ] More providers
- [ ] Custom fine-tuned models
- [ ] Agent workflows
- [ ] Multi-modal input
- [ ] Voice interaction

### Business Features
- [ ] Multi-location support
- [ ] Franchise model
- [ ] Partner API
- [ ] Marketplace

## Milestones

```
Day 1      MVP launched (internal)
Day 7      First real customers
Day 14     50 customers/day
Day 30     100 customers/day
Day 60     Membership launched
Day 90     Video API launched
Day 180    500 customers/day
Day 365    1,000 customers/day
```

## KPIs by Phase

### Phase 1 KPIs
| Metric | Target |
|--------|--------|
| Daily active users | 50 |
| AI activation rate | 60% |
| Return rate (7 days) | 30% |
| Upgrade rate | 5% |
| AI cost/customer | <5,000đ |
| Customer satisfaction | >4.0 |

### Phase 2 KPIs
| Metric | Target |
|--------|--------|
| Daily active users | 100 |
| Membership conversion | 10% |
| Retention rate | 70% |
| NPS score | >50 |
| Revenue/month | 150M |

### Phase 3 KPIs
| Metric | Target |
|--------|--------|
| Daily active users | 200 |
| API revenue | 5% of total |
| Repeat customer rate | 60% |
| Premium tier ratio | 30% |

## Technical Debt

Track và giải quyết:
- [ ] Code documentation
- [ ] Test coverage
- [ ] Performance optimization
- [ ] Security audit
- [ ] Database indexing
- [ ] Caching strategy

## Dependencies

```
MVP:
├── User authentication
├── Order system
├── Credit wallet
├── AI Gateway
└── Basic AI models

Growth:
├── Membership billing
├── Loyalty engine
├── Multi-provider routing
└── Admin dashboard

Scale:
├── Video processing
├── API gateway
├── Webhook system
└── Analytics pipeline
```

## Risks & Mitigations

| Risk | Impact | Mitigation |
|------|--------|------------|
| High AI cost | Profitability | Strict rate limits |
| Low adoption | Revenue | Marketing push |
| Provider outage | Service | Multi-provider |
| Fraud | Revenue loss | Monitoring |
| Competition | Market share | Unique value |
