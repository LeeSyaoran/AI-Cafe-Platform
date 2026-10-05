# AI Café - Workspace

## Overview

Workspace là môi trường làm việc trực tuyến có tích hợp AI.

## Concept

Workspace Time là quyền truy cập, không phải AI Credit.

```
Developer
2h Workspace
5k Credits

Khách có thể:
- 2h workspace
- 5k AI
```

## Workspace Features

```
Workspace bao gồm:
├── Code editor
├── Terminal
├── AI chat
├── File manager
├── Git
├── Project storage
├── Preview
└── AI tools
```

## User Flow

```
Drink
  │
  ▼
Open Workspace
  │
  ▼
Start Project
  │
  ▼
Use AI
  │
  ▼
Build
```

## Tier-based Workspace

| Tier | Workspace Time | Storage | Concurrent |
|------|---------------|---------|------------|
| Basic | 1h | 100MB | 1 project |
| Developer | 2h | 500MB | 3 projects |
| Pro | 4h | 2GB | 10 projects |
| Builder | 8h | 10GB | Unlimited |

## Workspace Timer

```
┌─────────────────────────────────┐
│ WORKSPACE ACTIVE                │
│                                 │
│    ⏱️ 1h 23m remaining          │
│                                 │
│    [ Extend Time ]              │
│    [ End Session ]              │
└─────────────────────────────────┘
```

## Workspace Session Tracking

```sql
workspace_sessions
- id
- user_id
- started_at
- ended_at
- duration_minutes
- status (active/ended/expired)

workspace_usage
- id
- user_id
- session_id
- date
- minutes_used
- project_count
- created_at
```

## AI Tools Integration

### Coding Tools
- Code completion
- Error explanation
- Code review
- Refactoring suggestions

### Project Tools
- Scaffold generation
- Dependency management
- Build assistance
- Deployment help

### Learning Tools
- Documentation lookup
- Best practice suggestions
- Tutorial generation

## Workspace vs Chat

| Aspect | Chat | Workspace |
|--------|------|----------|
| Persistence | Session-based | Project-based |
| File access | No | Yes |
| Git | No | Yes |
| Terminal | No | Yes |
| Preview | No | Yes |
| AI context | Limited | Full project |
| Time tracking | No | Yes |

## Session Management

```python
class WorkspaceSession:
    user_id: str
    tier: str
    max_minutes: int
    started_at: datetime
    used_minutes: int
    
    def is_active(self):
        return self.used_minutes < self.max_minutes
    
    def time_remaining(self):
        return max(0, self.max_minutes - self.used_minutes)
    
    def extend(self, minutes):
        # Purchase additional time
        pass
```

## Project Persistence

```
/User
└── /projects
    ├── /project-1
    │   ├── index.html
    │   ├── styles.css
    │   └── script.js
    └── /project-2
        └── ...
```

- Projects persist after session ends
- Credits used for AI, not storage
- Storage is separate allocation
