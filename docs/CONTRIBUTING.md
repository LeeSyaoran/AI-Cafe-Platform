# Contributing Guide

## 1. Getting Started

### 1.1 Prerequisites

```
Development Environment Requirements:
├── Java 17+
│   └── https://adoptium.net/
├── Go 1.22+
│   └── https://go.dev/dl/
├── Node.js 20+
│   └── https://nodejs.org/
├── Docker Desktop
│   └── https://docker.com/products/docker-desktop
├── kubectl
│   └── https://kubernetes.io/docs/tasks/tools/
└── AWS CLI v2
    └── https://aws.amazon.com/cli/
```

### 1.2 Repository Setup

```bash
# Clone the repository
git clone https://github.com/aicafe/platform.git
cd platform

# Install pre-commit hooks
brew install pre-commit  # macOS
# or
pip install pre-commit

pre-commit install

# Copy environment files
cp .env.example .env.local

# Start development environment
docker-compose up -d
```

### 1.3 Project Structure

```
platform/
├── services/
│   ├── user-service/           # Java Spring Boot
│   │   ├── src/main/java/
│   │   ├── src/main/resources/
│   │   └── src/test/java/
│   ├── order-service/          # Java Spring Boot
│   ├── credit-service/         # Go
│   ├── auth-service/           # Go
│   └── ai-gateway/             # Go/Node.js
├── web/
│   ├── src/
│   └── public/
├── docs/
├── infrastructure/
│   ├── k8s/
│   └── terraform/
└── scripts/
```

---

## 2. Branch Strategy

```
┌─────────────────────────────────────────────────────────────────────────────────────┐
│                               GIT FLOW                                               │
├─────────────────────────────────────────────────────────────────────────────────────┤
│                                                                                      │
│   main ────────────────────────────────────────────────────────────────────────────►│
│     │                                                                               │
│     │    ┌─────────────────────────────────────────────────────────────────────┐    │
│     │    │                        release/v1.x.x                                 │    │
│     │    │  (Release branches - stable, tagged)                                │    │
│     │    └─────────────────────────────────────────────────────────────────────┘    │
│     │                                                                               │
│     │                                                                               │
│     │    ┌─────────────────────────────────────────────────────────────────────┐    │
│     │    │                        develop                                         │    │
│     │    │  (Integration branch - features merged here)                          │    │
│     │    └──────────┬─────────────────────┬─────────────────────┬───────────────┘    │
│     │               │                     │                     │                      │
│     │               ▼                     ▼                     ▼                      │
│     │    ┌──────────────────┐  ┌──────────────────┐  ┌──────────────────┐          │
│     │    │ feature/         │  │ feature/         │  │ feature/         │          │
│     │    │ user-auth        │  │ payment-vnpay    │  │ ai-streaming    │          │
│     │    └──────────────────┘  └──────────────────┘  └──────────────────┘          │
│     │                                                                               │
│     │    ┌─────────────────────────────────────────────────────────────────────┐    │
│     │    │                        hotfix/*                                      │    │
│     │    │  (Critical fixes - merge to main AND develop)                        │    │
│     │    └─────────────────────────────────────────────────────────────────────┘    │
│     │                                                                               │
└─────┴───────────────────────────────────────────────────────────────────────────────┘
```

### 2.1 Branch Naming

| Type | Pattern | Example |
|------|---------|---------|
| Feature | `feature/<ticket-id>-<short-description>` | `feature/AIC-123-user-auth` |
| Bugfix | `fix/<ticket-id>-<short-description>` | `fix/AIC-456-login-error` |
| Hotfix | `hotfix/<ticket-id>-<short-description>` | `hotfix/AIC-789-payment-crash` |
| Release | `release/<version>` | `release/v1.2.0` |
| Chore | `chore/<short-description>` | `chore/update-deps` |

---

## 3. Development Workflow

### 3.1 Feature Development

```bash
# 1. Create feature branch from develop
git checkout develop
git pull origin develop
git checkout -b feature/AIC-123-my-feature

# 2. Make changes and commit
git add .
git commit -m "feat: add my feature

- Added new endpoint
- Updated tests
- Updated documentation

Refs: AIC-123"

# 3. Keep branch updated with develop
git fetch origin
git rebase origin/develop

# 4. Push and create PR
git push -u origin feature/AIC-123-my-feature

# 5. Create PR via GitHub CLI
gh pr create \
  --title "feat: add my feature" \
  --body "## Summary
Description of changes

## Changes
- Change 1
- Change 2

## Testing
- [ ] Unit tests
- [ ] Integration tests
- [ ] Manual testing

Closes #123" \
  --reviewer @eng-team
```

### 3.2 Commit Messages

Follow [Conventional Commits](https://www.conventionalcommits.org/):

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Types:**
- `feat` - New feature
- `fix` - Bug fix
- `docs` - Documentation
- `style` - Formatting
- `refactor` - Code refactoring
- `test` - Adding tests
- `chore` - Maintenance

**Examples:**
```bash
# Feature
feat(auth): add phone verification with OTP

Add phone number verification using time-based OTP.
Users can now verify their phone number before using AI services.

Closes #123

# Bug fix
fix(credits): prevent negative balance on concurrent requests

- Added optimistic locking to wallet updates
- Added transaction isolation level SERIALIZABLE
- Added retry logic for deadlock scenarios

Closes #456

# Breaking change
feat(payment)!: change payment response format

BREAKING CHANGE: Payment callback now returns JSON instead of query params

Migrate existing integrations:
- Update webhook handler to parse JSON body
- Update signature validation

Refs: #789
```

---

## 4. Code Standards

### 4.1 Java Style Guide

```java
// Package naming
package com.aicafe.user.service;
package com.aicafe.credit.repository;

// Class naming - nouns, PascalCase
public class UserService { }
public class CreditRepository { }
public class PaymentController { }

// Interface naming
public interface UserRepository { }
public interface PaymentGateway { }

// Method naming - verbs, camelCase
public User createUser(RegisterRequest request);
public Optional<User> findById(UUID id);
public void updateProfile(UUID userId, ProfileRequest request);

// Constants - SCREAMING_SNAKE_CASE
public static final int MAX_RETRY_ATTEMPTS = 3;
public static final String DEFAULT_TIER = "BASIC";

// Boolean methods - is/has/can prefixes
public boolean isActive();
public boolean hasCredits();
public boolean canDeduct(long amount);

// Dependency injection via constructor
@Service
public class UserService {
    
    private final UserRepository userRepository;
    private final OtpService otpService;
    private final EventPublisher eventPublisher;
    
    public UserService(
            UserRepository userRepository,
            OtpService otpService,
            EventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.otpService = otpService;
        this.eventPublisher = eventPublisher;
    }
}
```

### 4.2 Go Style Guide

```go
// Package naming - lowercase, no underscores
package auth      // not auth_service
package credit    // not credits

// File naming - lowercase, snake_case
// user_service.go, not UserService.go

// Type naming - PascalCase
type UserService struct { }
type Wallet struct { }
type Config struct { }

// Interface naming - er suffix for interfaces
type Repository interface { }
type Service interface { }
type Handler interface { }

// Method naming - PascalCase
func (s *UserService) CreateUser(ctx context.Context, req *CreateUserRequest) (*User, error)
func (w *Wallet) DeductCredits(ctx context.Context, amount int64) error

// Error naming - Error suffix
var ErrUserNotFound = errors.New("user not found")
var ErrInsufficientCredits = errors.New("insufficient credits")

// Context usage
func (s *UserService) GetUser(ctx context.Context, id string) (*User, error) {
    // ctx must be first parameter
}

// Defer for cleanup
func (h *Handler) HandleRequest(w http.ResponseWriter, r *http.Request) {
    body, err := io.ReadAll(r.Body)
    defer r.Body.Close()
    // ...
}

// Return early
func (s *Service) ProcessRequest(req *Request) error {
    if req == nil {
        return ErrInvalidRequest
    }
    // ...
}
```

### 4.3 TypeScript/React Style Guide

```typescript
// File naming - PascalCase for components
// UserProfile.tsx, not user_profile.tsx or UserProfileComponent.tsx

// Hook naming - use prefix
const useUserProfile = (userId: string) => { };
const useCreditBalance = () => { };

// Component naming
export const UserCard = ({ user }: UserCardProps) => { };
export const CreditBalance = () => { };

// Type naming
type User = {
  id: string;
  name: string;
  email: string;
};

interface UserProfileProps {
  user: User;
  onEdit: () => void;
}

// Event handlers - handle prefix
const handleClick = () => { };
const handleSubmit = (data: FormData) => { };
const handleChange = (e: ChangeEvent) => { };

// State - use prefix
const [isLoading, setIsLoading] = useState(false);
const [userData, setUserData] = useState<User | null>(null);
```

---

## 5. Testing Requirements

### 5.1 Coverage Requirements

| Layer | Minimum Coverage |
|-------|-----------------|
| Service/Handler | 80% |
| Repository/DAL | 70% |
| Controller | 60% |

### 5.2 Test Naming

```java
// Java - MethodName_Scenario_ExpectedBehavior
class UserServiceTest {
    
    @Test
    void createUser_WithValidRequest_ReturnsUser() { }
    
    @Test
    void createUser_WithDuplicatePhone_ThrowsException() { }
    
    @Test
    void findById_WhenUserExists_ReturnsUser() { }
    
    @Test
    void findById_WhenUserNotFound_ReturnsEmpty() { }
}
```

```go
// Go - Test[Subject]_[Scenario]
func TestUserService_CreateUser(t *testing.T) {
    t.Run("with valid request returns user", func(t *testing.T) { })
    
    t.Run("with duplicate phone returns error", func(t *testing.T) { })
}
```

### 5.3 Test Structure

```java
// AAA Pattern - Arrange, Act, Assert
@Test
void testName() {
    // Arrange
    when(repository.findById(any())).thenReturn(Optional.of(user));
    
    // Act
    User result = service.findById(user.getId());
    
    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Test");
    verify(repository, times(1)).findById(any());
}
```

---

## 6. Pull Request Process

### 6.1 PR Checklist

```markdown
## PR Checklist
- [ ] Code follows style guidelines
- [ ] Self-reviewed code
- [ ] Tests added/updated and passing
- [ ] Documentation updated
- [ ] No console.log or debug statements
- [ ] No commented-out code
- [ ] Environment variables documented
- [ ] Breaking changes noted
```

### 6.2 PR Template

```markdown
## Description
[Brief description of changes]

## Type of Change
- [ ] Bug fix (non-breaking change)
- [ ] New feature (non-breaking change)
- [ ] Breaking change (fix or feature)
- [ ] Documentation update

## Changes
[Detailed list of changes]

## Testing
- [ ] Unit tests added/updated
- [ ] Integration tests added/updated
- [ ] Manual testing performed

## Screenshots (if UI changes)
[Add screenshots here]

## Checklist
- [ ] My code follows the style guidelines
- [ ] I have performed self-review
- [ ] I have commented complex code
- [ ] I have made corresponding changes to documentation
- [ ] My changes generate no new warnings
- [ ] I have added tests that prove my fix is effective
- [ ] New and existing tests pass locally
```

### 6.3 Code Review Guidelines

**Reviewer Checklist:**
1. Does the code do what the PR claims?
2. Is the code readable and maintainable?
3. Are there tests covering the changes?
4. Are there any security concerns?
5. Are there any performance concerns?
6. Does it follow the project's coding standards?
7. Is the documentation updated?
8. Are there any edge cases not handled?

**Review Comments:**
```markdown
- [nitpick] Consider using `const` instead of `let` for variables that are never reassigned
- [suggestion] This could be simplified using...
- [question] How does this handle the case when X?
- [blocking] This needs to be fixed before merge
- [praise] Nice solution!
```

---

## 7. Environment Setup

### 7.1 Local Development

```bash
# Start all services
docker-compose up -d

# Run specific service with hot reload
cd services/user-service
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Run tests
./mvnw test

# Run specific test
./mvnw test -Dtest=UserServiceTest

# Run integration tests (requires Docker)
./mvnw verify -Dspring.profiles.active=testcontainers
```

### 7.2 Environment Variables

```bash
# .env.local for local development
# Copy from .env.example and fill in values

# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=aicafe_dev
DB_USERNAME=aicafe
DB_PASSWORD=dev_password

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# AWS
AWS_REGION=ap-southeast-1
AWS_ACCESS_KEY_ID=local
AWS_SECRET_ACCESS_KEY=local

# External APIs
VNPAY_TMN_CODE=TEST
VNPAY_HASH_SECRET=secret
```

### 7.3 IDE Setup

**VS Code (recommended):**
```json
// .vscode/settings.json
{
  "editor.formatOnSave": true,
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "[java]": {
    "editor.defaultFormatter": "redhat.java"
  },
  "[go]": {
    "editor.defaultFormatter": "golang.go"
  },
  "files.exclude": {
    "**/.classpath": true,
    "**/.project": true,
    "**/.settings": true
  }
}
```

**IntelliJ:**
- Install plugins: Lombok, CheckStyle, SonarLint
- Enable: Format on save, Organize imports on save
- Set: Java 17, Go 1.22

---

## 8. Security Guidelines

### 8.1 Secrets Management

```bash
# NEVER commit secrets to git
# Use .gitignore
.env
*.local
secrets/
credentials/

# Use environment variables or secrets manager
# Local development: .env.local (not committed)
# Staging/Production: AWS Secrets Manager
```

### 8.2 Security Checklist

```markdown
## Security Checklist
- [ ] No hardcoded credentials
- [ ] Input validation on all endpoints
- [ ] SQL injection prevention (use parameterized queries)
- [ ] XSS prevention (sanitize user input)
- [ ] CSRF protection enabled
- [ ] Rate limiting configured
- [ ] Sensitive data encrypted at rest
- [ ] HTTPS enforced in production
- [ ] Audit logging for sensitive operations
```

---

## 9. Release Process

### 9.1 Versioning

Follow [Semantic Versioning](https://semver.org/):
- MAJOR version: Breaking changes
- MINOR version: New features (backward compatible)
- PATCH version: Bug fixes

### 9.2 Release Steps

```bash
# 1. Update version
./scripts/bump-version.sh 1.2.0

# 2. Generate changelog
./scripts/generate-changelog.sh v1.2.0

# 3. Create release branch
git checkout develop
git pull
git checkout -b release/v1.2.0

# 4. Update dependencies
./mvnw versions:display-dependency-updates
./mvnw versions:use-latest-versions

# 5. Run release tests
./mvnw verify -P release

# 6. Merge to main
git checkout main
git merge release/v1.2.0
git tag v1.2.0
git push origin main --tags

# 7. Merge back to develop
git checkout develop
git merge release/v1.2.0
git push origin develop

# 8. Create GitHub release
gh release create v1.2.0 \
  --title "Release v1.2.0" \
  --notes "$(cat CHANGELOG.md | head -100)"
```

---

## 10. Contact & Support

| Channel | Purpose | Response Time |
|---------|---------|---------------|
| Slack #dev-help | Development questions | < 24h |
| Slack #dev-reviews | PR reviews | < 4h |
| GitHub Issues | Bug reports | < 48h |
| Wiki | Documentation | Always |

### Code Owners

```
/services/user-service      @team-backend-java
/services/order-service     @team-backend-java
/services/credit-service   @team-backend-go
/services/auth-service     @team-backend-go
/services/ai-gateway       @team-ai-platform
/web                       @team-frontend
```
