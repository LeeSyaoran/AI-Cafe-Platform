# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Initial project structure and documentation

---

## [v1.0.0] - 2024-01-15

### Added

#### Core Services
- **user-service** (Java/Spring Boot)
  - User registration and authentication
  - Phone-based OTP verification
  - JWT token management
  - User profile management
  - Membership tier handling

- **order-service** (Java/Spring Boot)
  - Credit purchase orders
  - Order state management
  - Idempotency handling
  - Multiple payment provider support

- **credit-service** (Go)
  - Credit balance management
  - Credit deduction for AI requests
  - Credit history tracking
  - Daily/monthly usage reporting

- **auth-service** (Go)
  - Authentication middleware
  - Token validation
  - Session management
  - Rate limiting

#### AI Services
- **ai-gateway**
  - OpenAI integration
  - Anthropic Claude integration
  - Stability AI integration
  - Streaming response support
  - Token usage tracking
  - Credit deduction middleware

#### Frontend
- **web-app**
  - User dashboard
  - AI chat interface
  - Credit purchase flow
  - Usage statistics visualization
  - Profile management

#### Infrastructure
- Kubernetes deployment configurations
- PostgreSQL database schemas
- Redis caching setup
- Prometheus metrics
- Grafana dashboards
- Loki logging
- AlertManager configurations

#### Documentation
- README with project overview
- Architecture documentation
- API reference
- Deployment guide
- Monitoring setup
- Contributing guidelines

### Features

#### Authentication
- Phone number verification with OTP
- JWT-based authentication
- Refresh token rotation
- Rate-limited OTP requests
- Account lockout protection

#### Credit System
- Credit purchase via payment providers
- Per-request credit deduction
- Credit history tracking
- Usage analytics
- Tier-based credit allocation

#### AI Integration
- Multi-provider support (OpenAI, Anthropic, Stability)
- Token-based billing
- Streaming responses
- Model selection
- Usage reporting

#### Payment Integration
- VNPay payment gateway
- Multiple payment methods
- Webhook handling
- Idempotent payment processing
- Refund support

---

## Version History

| Version | Release Date | Status |
|---------|--------------|--------|
| v1.0.0 | 2024-01-15 | Latest |
| v0.1.0 | 2024-01-01 | Alpha |

---

## Upcoming Features (Planned)

### v1.1.0
- [ ] Push notifications
- [ ] Email notifications
- [ ] Admin dashboard
- [ ] User referral system
- [ ] API key management for developers

### v1.2.0
- [ ] Team/workspaces
- [ ] Collaborative features
- [ ] Custom AI agents
- [ ] AI model fine-tuning
- [ ] Enterprise SSO

### v1.3.0
- [ ] Mobile app (iOS/Android)
- [ ] Offline support
- [ ] Advanced analytics
- [ ] A/B testing framework
- [ ] Multi-language support

---

## Migration Guides

### Upgrading to v1.0.0

No breaking changes for initial release.

### Future Migrations

When upgrading to future versions, follow these guides:

1. Check release notes for breaking changes
2. Update environment variables if needed
3. Run database migrations
4. Update client applications if API changes
5. Test thoroughly in staging before production

---

## Deprecation Policy

When deprecating features:
1. Announce deprecation in release notes (2 releases in advance)
2. Mark deprecated code with appropriate annotations
3. Provide migration path in documentation
4. Remove deprecated code after 2 release cycles
