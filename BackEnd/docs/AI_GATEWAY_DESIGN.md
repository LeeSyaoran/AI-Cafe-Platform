# AI Café Platform - AI Gateway Design

## 1. AI Gateway Overview

```
┌─────────────────────────────────────────────────────────────────────┐
│                         AI Gateway Architecture                       │
├─────────────────────────────────────────────────────────────────────┤
│                                                                      │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │                    Request Ingestion                          │  │
│  │  • Auth validation                                           │  │
│  │  • Credit check                                              │  │
│  │  • Rate limiting                                             │  │
│  │  • Request logging                                           │  │
│  └───────────────────────────┬───────────────────────────────────┘  │
│                              │                                       │
│                              ▼                                       │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │                    Task Classification                       │  │
│  │  • Intent detection                                          │  │
│  │  • Complexity scoring                                         │  │
│  │  • Capability mapping                                        │  │
│  └───────────────────────────┬───────────────────────────────────┘  │
│                              │                                       │
│                              ▼                                       │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │                    Model Routing                             │  │
│  │  • Tier-based selection                                       │  │
│  │  • Cost optimization                                         │  │
│  │  • Provider selection                                        │  │
│  │  • Fallback handling                                         │  │
│  └───────────────────────────┬───────────────────────────────────┘  │
│                              │                                       │
│                              ▼                                       │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │                    Provider Execution                        │  │
│  │  • OpenAI                                                    │  │
│  │  • Anthropic                                                │  │
│  │  • Google                                                    │  │
│  │  • Stability                                                 │  │
│  │  • Replicate                                                 │  │
│  └───────────────────────────┬───────────────────────────────────┘  │
│                              │                                       │
│                              ▼                                       │
│  ┌───────────────────────────────────────────────────────────────┐  │
│  │                    Response Processing                       │  │
│  │  • Token counting                                            │  │
│  │  • Cost calculation                                          │  │
│  │  • Usage tracking                                            │  │
│  │  • Caching (when applicable)                                 │  │
│  └───────────────────────────────────────────────────────────────┘  │
│                                                                      │
└─────────────────────────────────────────────────────────────────────┘
```

## 2. Task Types & Capabilities

### 2.1 Task Type Taxonomy

```
┌─────────────────────────────────────────────────────────────────┐
│                      Task Types                                  │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  CHAT                                                             │
│  ├── General Conversation                                         │
│  ├── Question Answering                                          │
│  ├── Summarization                                               │
│  ├── Translation                                                 │
│  └── Analysis                                                    │
│                                                                  │
│  CODE                                                             │
│  ├── Code Generation                                             │
│  ├── Code Completion                                             │
│  ├── Code Review                                                 │
│  ├── Debugging                                                   │
│  └── Explanation                                                 │
│                                                                  │
│  REASONING                                                        │
│  ├── Math                                                         │
│  ├── Logic                                                       │
│  ├── Planning                                                    │
│  └── Problem Solving                                             │
│                                                                  │
│  IMAGE                                                            │
│  ├── Generation                                                  │
│  ├── Editing                                                     │
│  ├── Variation                                                  │
│  └── Inpainting                                                 │
│                                                                  │
│  VIDEO                                                            │
│  ├── Generation                                                  │
│  ├── Editing                                                     │
│  └── Effects                                                     │
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

### 2.2 Task to Model Mapping

```yaml
# Task-to-Model Mapping Configuration

chat:
  general:
    primary: gpt-4o
    fallback: gpt-4o-mini
    tiers: [basic, developer, pro, builder]
    
  analysis:
    primary: gpt-4o
    fallback: claude-3-5-sonnet
    tiers: [developer, pro, builder]

code:
  generation:
    primary: gpt-4o
    fallback: gpt-4o-mini
    tiers: [basic, developer, pro, builder]
    
  review:
    primary: claude-3-5-sonnet
    fallback: gpt-4o
    tiers: [developer, pro, builder]

reasoning:
  math:
    primary: gpt-4o
    fallback: claude-3-5-sonnet
    tiers: [developer, pro, builder]
    
  planning:
    primary: claude-3-5-sonnet
    fallback: gpt-4o
    tiers: [developer, pro, builder]

image:
  generation:
    primary: dalle-3
    fallback: stable-diffusion-xl
    tiers: [basic, developer, pro, builder]
    
  hd_generation:
    primary: dalle-3-hd
    fallback: dalle-3
    tiers: [developer, pro, builder]

video:
  generation:
    primary: sora
    fallback: stable-video
    tiers: [pro, builder]
    
  short_clips:
    primary: Anthropic-video
    fallback: stable-video
    tiers: [pro, builder]
```

## 3. Model Registry

### 3.1 Available Models

```python
# src/providers/models.py
from dataclasses import dataclass
from enum import Enum

class ModelTier(Enum):
    ECONOMY = "economy"
    STANDARD = "standard"
    PREMIUM = "premium"
    ULTRA = "ultra"

class TaskCapability(Enum):
    CHAT = "chat"
    CODE = "code"
    REASONING = "reasoning"
    IMAGE = "image"
    VIDEO = "video"

@dataclass
class ModelConfig:
    id: str
    provider: str
    name: str
    tier: ModelTier
    capabilities: list[TaskCapability]
    
    # Rate limits
    rpm: int
    tpm: int
    max_tokens: int
    
    # Cost (USD per 1M tokens or per image/video)
    input_cost_per_1k: float
    output_cost_per_1k: float
    
    # Credit multiplier
    credit_multiplier: float = 1.0
    
    # Capabilities
    streaming: bool = True
    function_calling: bool = False
    vision: bool = False

# Model Registry
MODELS: dict[str, ModelConfig] = {
    # GPT-4o Family
    "gpt-4o": ModelConfig(
        id="gpt-4o",
        provider="openai",
        name="GPT-4o",
        tier=ModelTier.PREMIUM,
        capabilities=[TaskCapability.CHAT, TaskCapability.CODE, TaskCapability.REASONING],
        rpm=500,
        tpm=120_000,
        max_tokens=128_000,
        input_cost_per_1k=2.50,
        output_cost_per_1k=10.00,
        credit_multiplier=1.2,
        streaming=True,
        function_calling=True,
    ),
    
    "gpt-4o-mini": ModelConfig(
        id="gpt-4o-mini",
        provider="openai",
        name="GPT-4o Mini",
        tier=ModelTier.STANDARD,
        capabilities=[TaskCapability.CHAT, TaskCapability.CODE],
        rpm=2000,
        tpm=2_000_000,
        max_tokens=128_000,
        input_cost_per_1k=0.15,
        output_cost_per_1k=0.60,
        credit_multiplier=1.0,
        streaming=True,
        function_calling=True,
    ),
    
    # Claude 3.5 Family
    "claude-3-5-sonnet": ModelConfig(
        id="claude-3-5-sonnet",
        provider="anthropic",
        name="Claude 3.5 Sonnet",
        tier=ModelTier.PREMIUM,
        capabilities=[TaskCapability.CHAT, TaskCapability.CODE, TaskCapability.REASONING],
        rpm=2000,
        tpm=1_000_000,
        max_tokens=200_000,
        input_cost_per_1k=3.00,
        output_cost_per_1k=15.00,
        credit_multiplier=1.2,
        streaming=True,
        vision=True,
    ),
    
    "claude-3-5-haiku": ModelConfig(
        id="claude-3-5-haiku",
        provider="anthropic",
        name="Claude 3.5 Haiku",
        tier=ModelTier.ECONOMY,
        capabilities=[TaskCapability.CHAT, TaskCapability.CODE],
        rpm=4000,
        tpm=1_000_000,
        max_tokens=200_000,
        input_cost_per_1k=0.80,
        output_cost_per_1k=4.00,
        credit_multiplier=1.0,
        streaming=True,
        vision=True,
    ),
    
    # Gemini Family
    "gemini-1-5-pro": ModelConfig(
        id="gemini-1-5-pro",
        provider="google",
        name="Gemini 1.5 Pro",
        tier=ModelTier.PREMIUM,
        capabilities=[TaskCapability.CHAT, TaskCapability.CODE, TaskCapability.REASONING],
        rpm=2000,
        tpm=1_000_000,
        max_tokens=1_000_000,
        input_cost_per_1k=1.25,
        output_cost_per_1k=5.00,
        credit_multiplier=1.1,
        streaming=True,
        vision=True,
    ),
    
    # Image Models
    "dalle-3": ModelConfig(
        id="dalle-3",
        provider="openai",
        name="DALL-E 3",
        tier=ModelTier.STANDARD,
        capabilities=[TaskCapability.IMAGE],
        rpm=50,
        tpm=None,
        max_tokens=None,
        input_cost_per_1k=4.00,  # Per image
        output_cost_per_1k=0,
        credit_multiplier=1.5,
        streaming=False,
    ),
    
    "dalle-3-hd": ModelConfig(
        id="dalle-3-hd",
        provider="openai",
        name="DALL-E 3 HD",
        tier=ModelTier.PREMIUM,
        capabilities=[TaskCapability.IMAGE],
        rpm=50,
        tpm=None,
        max_tokens=None,
        input_cost_per_1k=8.00,  # Per image
        output_cost_per_1k=0,
        credit_multiplier=2.0,
        streaming=False,
    ),
    
    "stable-diffusion-xl": ModelConfig(
        id="stable-diffusion-xl",
        provider="stability",
        name="Stable Diffusion XL",
        tier=ModelTier.ECONOMY,
        capabilities=[TaskCapability.IMAGE],
        rpm=100,
        tpm=None,
        max_tokens=None,
        input_cost_per_1k=0.50,
        output_cost_per_1k=0,
        credit_multiplier=1.0,
        streaming=False,
    ),
    
    # Video Models
    "sora": ModelConfig(
        id="sora",
        provider="openai",
        name="Sora",
        tier=ModelTier.ULTRA,
        capabilities=[TaskCapability.VIDEO],
        rpm=10,
        tpm=None,
        max_tokens=None,
        input_cost_per_1k=100.00,  # Per video
        output_cost_per_1k=0,
        credit_multiplier=5.0,
        streaming=False,
    ),
    
    "stable-video": ModelConfig(
        id="stable-video",
        provider="stability",
        name="Stable Video Diffusion",
        tier=ModelTier.PREMIUM,
        capabilities=[TaskCapability.VIDEO],
        rpm=20,
        tpm=None,
        max_tokens=None,
        input_cost_per_1k=10.00,
        output_cost_per_1k=0,
        credit_multiplier=2.0,
        streaming=False,
    ),
}
```

## 4. Router Design

### 4.1 Model Router Implementation

```python
# src/core/router.py
from typing import Optional
from dataclasses import dataclass
from ..providers.models import MODELS, ModelConfig, TaskCapability, ModelTier
from ..schemas.requests import AIRequest
from ..schemas.responses import RouterDecision

@dataclass
class RouterContext:
    user_id: str
    tier: str
    task_type: str
    sub_type: Optional[str]
    prompt: str
    budget_status: str  # normal, warning, critical
    require_vision: bool
    require_streaming: bool

class ModelRouter:
    def __init__(self):
        self.classifier = TaskClassifier()
    
    async def select_model(self, ctx: RouterContext) -> RouterDecision:
        """Select the best model for the request."""
        
        # 1. Classify the task
        classification = await self.classifier.classify(
            ctx.prompt,
            ctx.task_type,
            ctx.require_vision
        )
        
        # 2. Get available models for user's tier
        available_models = self._get_models_for_tier(ctx.tier)
        
        # 3. Filter by capability
        suitable = [
            m for m in available_models
            if classification.capability in m.capabilities
        ]
        
        # 4. Apply constraints
        if ctx.require_vision:
            suitable = [m for m in suitable if m.vision]
        
        if ctx.require_streaming:
            suitable = [m for m in suitable if m.streaming]
        
        # 5. Apply budget-based selection
        suitable = self._apply_budget_filter(suitable, ctx.budget_status)
        
        # 6. Sort by cost-effectiveness
        suitable.sort(key=lambda m: m.input_cost_per_1k * m.credit_multiplier)
        
        if not suitable:
            raise NoModelAvailableError(
                f"No suitable model for task: {ctx.task_type}"
            )
        
        primary = suitable[0]
        fallback = suitable[1] if len(suitable) > 1 else None
        
        return RouterDecision(
            primary_model=primary,
            fallback_model=fallback,
            estimated_cost=self._estimate_cost(primary),
            reasoning=classification.reasoning,
        )
    
    def _get_models_for_tier(self, tier: str) -> list[ModelConfig]:
        """Get models available for user's tier."""
        tier_rankings = {
            "basic": [ModelTier.ECONOMY],
            "developer": [ModelTier.ECONOMY, ModelTier.STANDARD],
            "pro": [ModelTier.ECONOMY, ModelTier.STANDARD, ModelTier.PREMIUM],
            "builder": [ModelTier.ECONOMY, ModelTier.STANDARD, ModelTier.PREMIUM, ModelTier.ULTRA],
        }
        
        allowed_tiers = tier_rankings.get(tier, [ModelTier.ECONOMY])
        
        return [
            m for m in MODELS.values()
            if m.tier in allowed_tiers and m.id.startswith("dalle") is False  # Special handling for images
        ]
    
    def _apply_budget_filter(
        self, 
        models: list[ModelConfig], 
        budget_status: str
    ) -> list[ModelConfig]:
        """Filter models based on budget status."""
        
        if budget_status == "critical":
            # Only use economy models
            return [m for m in models if m.tier == ModelTier.ECONOMY]
        
        elif budget_status == "warning":
            # Avoid ultra tier
            return [m for m in models if m.tier != ModelTier.ULTRA]
        
        return models  # normal: use all available
    
    def _estimate_cost(self, model: ModelConfig) -> float:
        """Estimate cost for a typical request."""
        # Assumes average 1000 input tokens, 500 output tokens
        return (
            model.input_cost_per_1k * model.credit_multiplier +
            model.output_cost_per_1k * model.credit_multiplier
        ) * 0.5  # Rough estimate
```

### 4.2 Task Classifier

```python
# src/core/classifier.py
import re
from dataclasses import dataclass
from typing import Optional

@dataclass
class ClassificationResult:
    capability: str
    sub_type: str
    confidence: float
    reasoning: str

class TaskClassifier:
    """Classifies user prompts into task types."""
    
    CODE_PATTERNS = [
        r"write code",
        r"implement",
        r"function",
        r"class \w+",
        r"def \w+\(",
        r"import \w+",
        r"```[\w]*",
        r"debug",
        r"fix.*bug",
        r"refactor",
        r"algorithm",
    ]
    
    REASONING_PATTERNS = [
        r"prove",
        r"calculate",
        r"solve for",
        r"explain why",
        r"logical.*step",
        r"proof",
        r"deduce",
        r"infer",
        r"analyze.*pattern",
    ]
    
    IMAGE_PATTERNS = [
        r"draw",
        r"paint",
        r"generate.*image",
        r"create.*picture",
        r"design.*logo",
        r"illustration",
        r"render",
    ]
    
    VIDEO_PATTERNS = [
        r"generate.*video",
        r"create.*clip",
        r"animate",
        r"make.*movie",
    ]
    
    def classify(
        self, 
        prompt: str, 
        requested_type: Optional[str],
        require_vision: bool
    ) -> ClassificationResult:
        """Classify the task based on prompt and request."""
        
        # If explicitly requested, use that
        if requested_type:
            return ClassificationResult(
                capability=requested_type,
                sub_type=self._get_sub_type(prompt, requested_type),
                confidence=0.9,
                reasoning=f"Explicitly requested: {requested_type}"
            )
        
        # Pattern matching
        prompt_lower = prompt.lower()
        
        # Check code patterns
        if self._matches_any(prompt_lower, self.CODE_PATTERNS):
            return ClassificationResult(
                capability="code",
                sub_type=self._classify_code_type(prompt_lower),
                confidence=0.85,
                reasoning="Code-related keywords detected"
            )
        
        # Check reasoning patterns
        if self._matches_any(prompt_lower, self.REASONING_PATTERNS):
            return ClassificationResult(
                capability="reasoning",
                sub_type="problem_solving",
                confidence=0.8,
                reasoning="Reasoning-related keywords detected"
            )
        
        # Check image patterns
        if self._matches_any(prompt_lower, self.IMAGE_PATTERNS):
            return ClassificationResult(
                capability="image",
                sub_type="generation",
                confidence=0.9,
                reasoning="Image-related keywords detected"
            )
        
        # Check video patterns
        if self._matches_any(prompt_lower, self.VIDEO_PATTERNS):
            return ClassificationResult(
                capability="video",
                sub_type="generation",
                confidence=0.9,
                reasoning="Video-related keywords detected"
            )
        
        # Default to chat
        return ClassificationResult(
            capability="chat",
            sub_type="general",
            confidence=0.7,
            reasoning="Defaulting to general chat"
        )
    
    def _matches_any(self, text: str, patterns: list[str]) -> bool:
        """Check if any pattern matches."""
        for pattern in patterns:
            if re.search(pattern, text, re.IGNORECASE):
                return True
        return False
    
    def _classify_code_type(self, prompt: str) -> str:
        """Classify the type of code task."""
        if "debug" in prompt or "fix" in prompt:
            return "debugging"
        if "review" in prompt:
            return "review"
        if "complete" in prompt or "suggest" in prompt:
            return "completion"
        return "generation"
    
    def _get_sub_type(self, prompt: str, capability: str) -> str:
        """Get the sub-type for a capability."""
        sub_types = {
            "chat": ["general", "qa", "summarization", "translation", "analysis"],
            "code": ["generation", "completion", "review", "debugging", "explanation"],
            "reasoning": ["math", "logic", "planning", "problem_solving"],
            "image": ["generation", "editing", "variation", "inpainting"],
            "video": ["generation", "editing", "effects"],
        }
        return sub_types.get(capability, ["general"])[0]
```

## 5. Provider Integration

### 5.1 Provider Interface

```python
# src/providers/base.py
from abc import ABC, abstractmethod
from typing import AsyncIterator, Any
from ..schemas.requests import AIRequest
from ..schemas.responses import AIResponse, UsageStats
from ..providers.models import ModelConfig

class BaseProvider(ABC):
    """Base interface for AI providers."""
    
    def __init__(self, api_key: str, config: dict):
        self.api_key = api_key
        self.config = config
        self.client = None
    
    @abstractmethod
    async def initialize(self) -> None:
        """Initialize the provider client."""
        pass
    
    @abstractmethod
    async def chat(
        self,
        request: AIRequest,
        model: ModelConfig
    ) -> AIResponse:
        """Execute a chat request."""
        pass
    
    @abstractmethod
    async def chat_stream(
        self,
        request: AIRequest,
        model: ModelConfig
    ) -> AsyncIterator[str]:
        """Execute a streaming chat request."""
        pass
    
    @abstractmethod
    async def generate_image(
        self,
        request: AIRequest,
        model: ModelConfig
    ) -> dict:
        """Generate an image."""
        pass
    
    @abstractmethod
    async def generate_video(
        self,
        request: AIRequest,
        model: ModelConfig
    ) -> dict:
        """Generate a video."""
        pass
    
    def calculate_cost(
        self, 
        model: ModelConfig, 
        usage: UsageStats
    ) -> int:
        """Calculate credit cost based on usage."""
        input_cost = (usage.input_tokens / 1000) * model.input_cost_per_1k
        output_cost = (usage.output_tokens / 1000) * model.output_cost_per_1k
        
        total_cost_usd = (input_cost + output_cost) * model.credit_multiplier
        
        # Convert to credits (1 credit ≈ 1 VND)
        return int(total_cost_usd * 1000)
```

### 5.2 OpenAI Provider

```python
# src/providers/openai.py
import openai
from openai import AsyncOpenAI
from typing import AsyncIterator
from .base import BaseProvider
from ..schemas.requests import ChatRequest, ImageRequest
from ..schemas.responses import AIResponse, UsageStats
from ..providers.models import ModelConfig

class OpenAIProvider(BaseProvider):
    """OpenAI API provider."""
    
    async def initialize(self) -> None:
        self.client = AsyncOpenAI(api_key=self.api_key)
    
    async def chat(
        self,
        request: ChatRequest,
        model: ModelConfig
    ) -> AIResponse:
        """Execute a chat completion."""
        
        start_time = datetime.utcnow()
        
        response = await self.client.chat.completions.create(
            model=model.id,
            messages=[
                {"role": "system", "content": request.system_prompt or ""},
                {"role": "user", "content": request.message}
            ],
            temperature=request.temperature or 0.7,
            max_tokens=request.max_tokens,
            stream=False,
        )
        
        latency_ms = (datetime.utcnow() - start_time).total_seconds() * 1000
        
        content = response.choices[0].message.content
        usage = response.usage
        
        return AIResponse(
            content=content,
            model=model.id,
            provider="openai",
            usage=UsageStats(
                input_tokens=usage.prompt_tokens,
                output_tokens=usage.completion_tokens,
                total_tokens=usage.total_tokens,
            ),
            latency_ms=int(latency_ms),
            credits_used=self.calculate_cost(model, UsageStats(
                input_tokens=usage.prompt_tokens,
                output_tokens=usage.completion_tokens,
                total_tokens=usage.total_tokens,
            )),
        )
    
    async def chat_stream(
        self,
        request: ChatRequest,
        model: ModelConfig
    ) -> AsyncIterator[str]:
        """Execute a streaming chat completion."""
        
        stream = await self.client.chat.completions.create(
            model=model.id,
            messages=[
                {"role": "system", "content": request.system_prompt or ""},
                {"role": "user", "content": request.message}
            ],
            temperature=request.temperature or 0.7,
            max_tokens=request.max_tokens,
            stream=True,
        )
        
        async for chunk in stream:
            if chunk.choices[0].delta.content:
                yield chunk.choices[0].delta.content
    
    async def generate_image(
        self,
        request: ImageRequest,
        model: ModelConfig
    ) -> dict:
        """Generate an image using DALL-E."""
        
        response = await self.client.images.generate(
            model=model.id,
            prompt=request.prompt,
            size=request.size or "1024x1024",
            quality=request.quality or "standard",
            n=1,
        )
        
        image_data = response.data[0]
        
        return {
            "url": image_data.url,
            "revised_prompt": image_data.revised_prompt,
            "credits_used": self.calculate_image_cost(model),
        }
    
    def calculate_image_cost(self, model: ModelConfig) -> int:
        """Calculate cost for image generation."""
        # Cost is per image in USD * credit multiplier * 1000
        base_cost = model.input_cost_per_1k  # This is per image
        return int(base_cost * model.credit_multiplier * 1000)
```

### 5.3 Anthropic Provider

```python
# src/providers/anthropic.py
import anthropic
from anthropic import AsyncAnthropic
from typing import AsyncIterator
from .base import BaseProvider
from ..schemas.requests import ChatRequest
from ..schemas.responses import AIResponse, UsageStats
from ..providers.models import ModelConfig

class AnthropicProvider(BaseProvider):
    """Anthropic Claude API provider."""
    
    async def initialize(self) -> None:
        self.client = AsyncAnthropic(api_key=self.api_key)
    
    async def chat(
        self,
        request: ChatRequest,
        model: ModelConfig
    ) -> AIResponse:
        """Execute a Claude chat completion."""
        
        start_time = datetime.utcnow()
        
        response = await self.client.messages.create(
            model=model.id,
            max_tokens=request.max_tokens or 4096,
            messages=[
                {"role": "user", "content": request.message}
            ],
            system=request.system_prompt,
            temperature=request.temperature,
        )
        
        latency_ms = (datetime.utcnow() - start_time).total_seconds() * 1000
        
        content = response.content[0].text
        usage = response.usage
        
        return AIResponse(
            content=content,
            model=model.id,
            provider="anthropic",
            usage=UsageStats(
                input_tokens=usage.input_tokens,
                output_tokens=usage.output_tokens,
                total_tokens=usage.input_tokens + usage.output_tokens,
            ),
            latency_ms=int(latency_ms),
            credits_used=self.calculate_cost(model, UsageStats(
                input_tokens=usage.input_tokens,
                output_tokens=usage.output_tokens,
                total_tokens=usage.input_tokens + usage.output_tokens,
            )),
        )
    
    async def chat_stream(
        self,
        request: ChatRequest,
        model: ModelConfig
    ) -> AsyncIterator[str]:
        """Execute a streaming Claude chat."""
        
        async with self.client.messages.stream(
            model=model.id,
            max_tokens=request.max_tokens or 4096,
            messages=[
                {"role": "user", "content": request.message}
            ],
            system=request.system_prompt,
            temperature=request.temperature,
        ) as stream:
            async for text in stream.text_stream:
                yield text
```

## 6. Rate Limiting

### 6.1 Rate Limiter Implementation

```python
# src/core/limiter.py
import time
import redis.asyncio as redis
from dataclasses import dataclass
from typing import Optional

@dataclass
class RateLimitResult:
    allowed: bool
    remaining: int
    reset_at: int
    retry_after: Optional[int] = None

class RateLimiter:
    """Redis-based rate limiter."""
    
    def __init__(self, redis_client: redis.Redis):
        self.redis = redis_client
    
    async def check(
        self,
        key: str,
        limit: int,
        window_seconds: int = 60
    ) -> RateLimitResult:
        """Check if request is within rate limit."""
        
        now = time.time()
        window_start = int(now // window_seconds) * window_seconds
        window_end = window_start + window_seconds
        
        # Increment counter
        current = await self.redis.incr(key)
        
        # Set expiry on first request
        if current == 1:
            await self.redis.expire(key, window_seconds + 1)
        
        if current > limit:
            return RateLimitResult(
                allowed=False,
                remaining=0,
                reset_at=window_end,
                retry_after=window_end - int(now),
            )
        
        return RateLimitResult(
            allowed=True,
            remaining=limit - current,
            reset_at=window_end,
        )
    
    async def check_user_limits(
        self,
        user_id: str,
        tier: str,
        task_type: str
    ) -> RateLimitResult:
        """Check all user limits."""
        
        tier_limits = {
            "basic": {"rpm": 10, "tpm": 10_000, "daily": 2000},
            "developer": {"rpm": 30, "tpm": 50_000, "daily": 5000},
            "pro": {"rpm": 60, "tpm": 150_000, "daily": 10000},
            "builder": {"rpm": 120, "tpm": 500_000, "daily": 18000},
        }
        
        limits = tier_limits.get(tier, tier_limits["basic"])
        
        # Check requests per minute
        rpm_result = await self.check(f"rpm:{user_id}", limits["rpm"], 60)
        if not rpm_result.allowed:
            return rpm_result
        
        # Check tokens per minute
        # Token checking happens after tokenization
        
        return RateLimitResult(allowed=True, remaining=1, reset_at=int(time.time()) + 60)
    
    async def check_daily_limit(
        self,
        user_id: str,
        tier: str
    ) -> RateLimitResult:
        """Check daily credit limit."""
        
        today = time.strftime("%Y-%m-%d")
        
        tier_limits = {
            "basic": 2000,
            "developer": 5000,
            "pro": 10000,
            "builder": 18000,
        }
        
        limit = tier_limits.get(tier, 2000)
        
        result = await self.check(f"daily:{user_id}:{today}", limit, 86400)
        
        return result
```

## 7. Usage Tracking

### 7.1 Usage Tracker

```python
# src/core/tracker.py
import asyncio
from datetime import datetime, timedelta
from typing import Optional
import redis.asyncio as redis

class UsageTracker:
    """Track AI usage for billing and analytics."""
    
    def __init__(self, redis_client: redis.Redis, db_pool):
        self.redis = redis_client
        self.db = db_pool
    
    async def record_usage(
        self,
        user_id: str,
        model_id: str,
        provider: str,
        task_type: str,
        input_tokens: int,
        output_tokens: int,
        credits_used: int,
        latency_ms: int,
        status: str = "success"
    ) -> None:
        """Record usage for a single request."""
        
        now = datetime.utcnow()
        today = now.strftime("%Y-%m-%d")
        hour = now.strftime("%Y-%m-%d:%H")
        
        # Update Redis counters (for real-time tracking)
        pipe = self.redis.pipeline()
        
        # Daily usage
        pipe.hincrby(f"usage:daily:{user_id}:{today}", "requests", 1)
        pipe.hincrby(f"usage:daily:{user_id}:{today}", "credits", credits_used)
        pipe.hincrby(f"usage:daily:{user_id}:{today}", "input_tokens", input_tokens)
        pipe.hincrby(f"usage:daily:{user_id}:{today}", "output_tokens", output_tokens)
        pipe.expire(f"usage:daily:{user_id}:{today}", 86400 * 2)
        
        # Hourly usage
        pipe.hincrby(f"usage:hourly:{provider}:{hour}", "requests", 1)
        pipe.hincrby(f"usage:hourly:{provider}:{hour}", "cost", credits_used)
        pipe.expire(f"usage:hourly:{provider}:{hour}", 86400 * 2)
        
        await pipe.execute()
        
        # Log to database (async, non-blocking)
        asyncio.create_task(self._log_to_database(
            user_id, model_id, provider, task_type,
            input_tokens, output_tokens, credits_used,
            latency_ms, status
        ))
    
    async def _log_to_database(
        self,
        user_id: str,
        model_id: str,
        provider: str,
        task_type: str,
        input_tokens: int,
        output_tokens: int,
        credits_used: int,
        latency_ms: int,
        status: str
    ) -> None:
        """Log usage to database for persistence."""
        
        async with self.db.connect() as conn:
            await conn.execute(
                """
                INSERT INTO ai_requests (
                    user_id, model_id, provider, task_type,
                    input_tokens, output_tokens, credits_used,
                    latency_ms, status, created_at
                ) VALUES ($1, $2, $3, $4, $5, $6, $7, $8, $9, NOW())
                """,
                user_id, model_id, provider, task_type,
                input_tokens, output_tokens, credits_used,
                latency_ms, status
            )
    
    async def get_user_daily_usage(self, user_id: str) -> dict:
        """Get user's daily usage from cache."""
        
        today = datetime.utcnow().strftime("%Y-%m-%d")
        
        usage = await self.redis.hgetall(f"usage:daily:{user_id}:{today}")
        
        return {
            "requests": int(usage.get(b"requests", 0)),
            "credits": int(usage.get(b"credits", 0)),
            "input_tokens": int(usage.get(b"input_tokens", 0)),
            "output_tokens": int(usage.get(b"output_tokens", 0)),
        }
    
    async def get_provider_hourly_stats(self, provider: str) -> dict:
        """Get provider's hourly statistics."""
        
        hour = datetime.utcnow().strftime("%Y-%m-%d:%H")
        
        stats = await self.redis.hgetall(f"usage:hourly:{provider}:{hour}")
        
        return {
            "requests": int(stats.get(b"requests", 0)),
            "cost": int(stats.get(b"cost", 0)),
        }
```

## 8. API Endpoints

### 8.1 Chat Endpoint

```python
# src/api/routes/chat.py
from fastapi import APIRouter, HTTPException, Depends, BackgroundTasks
from ..dependencies import get_auth, get_credit_service, get_router
from ..schemas.requests import ChatRequest
from ..schemas.responses import ChatResponse, StreamResponse

router = APIRouter()

@router.post("/chat", response_model=ChatResponse)
async def chat(
    request: ChatRequest,
    background_tasks: BackgroundTasks,
    auth = Depends(get_auth),
    credit_service = Depends(get_credit_service),
    router = Depends(get_router),
):
    """Process a chat request."""
    
    # 1. Check credits
    user = auth.user
    
    # 2. Route request
    decision = await router.select_model(
        RouterContext(
            user_id=user.id,
            tier=user.tier,
            task_type=request.task_type,
            sub_type=None,
            prompt=request.message,
            budget_status=credit_service.get_budget_status(user.id),
            require_vision=False,
            require_streaming=request.stream,
        )
    )
    
    # 3. Check if streaming or not
    if request.stream:
        return StreamingResponse(
            chat_generator(
                request=request,
                model=decision.primary_model,
                provider=decision.primary_model.provider,
                credit_service=credit_service,
            ),
            media_type="text/event-stream"
        )
    
    # 4. Process normally
    response = await chat_sync(
        request=request,
        model=decision.primary_model,
        provider=decision.primary_model.provider,
    )
    
    # 5. Deduct credits (async)
    background_tasks.add_task(
        credit_service.deduct,
        user_id=user.id,
        amount=response.credits_used,
        context={
            "service": "chat",
            "model": response.model,
            "tokens": response.usage.total_tokens,
        }
    )
    
    # 6. Track usage (async)
    background_tasks.add_task(
        usage_tracker.record_usage,
        user_id=user.id,
        model_id=response.model,
        provider=response.provider,
        task_type="chat",
        **response.usage.__dict__,
        credits_used=response.credits_used,
        latency_ms=response.latency_ms,
    )
    
    return response

async def chat_sync(request: ChatRequest, model, provider) -> ChatResponse:
    """Synchronous chat processing."""
    
    if provider == "openai":
        result = await openai_provider.chat(request, model)
    elif provider == "anthropic":
        result = await anthropic_provider.chat(request, model)
    else:
        raise HTTPException(400, f"Unknown provider: {provider}")
    
    return result
```

### 8.2 Image Generation Endpoint

```python
# src/api/routes/image.py
from fastapi import APIRouter, HTTPException, Depends, BackgroundTasks
from ..dependencies import get_auth, get_credit_service, get_router
from ..schemas.requests import ImageRequest

router = APIRouter()

@router.post("/image")
async def generate_image(
    request: ImageRequest,
    background_tasks: BackgroundTasks,
    auth = Depends(get_auth),
    credit_service = Depends(get_credit_service),
    router = Depends(get_router),
):
    """Generate an image."""
    
    user = auth.user
    
    # Route to appropriate model
    decision = await router.select_model(
        RouterContext(
            user_id=user.id,
            tier=user.tier,
            task_type="image",
            sub_type=None,
            prompt=request.prompt,
            budget_status=credit_service.get_budget_status(user.id),
            require_vision=False,
            require_streaming=False,
        )
    )
    
    # Generate image
    if decision.primary_model.provider == "openai":
        result = await openai_provider.generate_image(request, decision.primary_model)
    elif decision.primary_model.provider == "stability":
        result = await stability_provider.generate_image(request, decision.primary_model)
    else:
        raise HTTPException(400, f"Image generation not supported for {decision.primary_model.provider}")
    
    # Deduct credits
    background_tasks.add_task(
        credit_service.deduct,
        user_id=user.id,
        amount=result["credits_used"],
        context={
            "service": "image",
            "model": decision.primary_model.id,
        }
    )
    
    return {
        "url": result["url"],
        "revised_prompt": result.get("revised_prompt"),
        "credits_used": result["credits_used"],
        "model": decision.primary_model.id,
    }
```

## 9. Error Handling

### 9.1 Error Types

```python
# src/core/errors.py
from fastapi import HTTPException

class AIError(Exception):
    """Base AI Gateway error."""
    def __init__(self, message: str, code: str, details: dict = None):
        self.message = message
        self.code = code
        self.details = details or {}
        super().__init__(message)

class NoModelAvailableError(AIError):
    """No model available for request."""
    def __init__(self, message: str):
        super().__init__(message, "NO_MODEL_AVAILABLE")

class ProviderError(AIError):
    """Error from AI provider."""
    def __init__(self, provider: str, message: str, details: dict = None):
        super().__init__(
            f"{provider}: {message}",
            "PROVIDER_ERROR",
            {"provider": provider, **details}
        )

class RateLimitError(AIError):
    """Rate limit exceeded."""
    def __init__(self, retry_after: int):
        super().__init__(
            "Rate limit exceeded",
            "RATE_LIMIT_EXCEEDED",
            {"retry_after": retry_after}
        )

class InsufficientCreditsError(AIError):
    """Not enough credits."""
    def __init__(self, required: int, available: int):
        super().__init__(
            "Insufficient credits",
            "INSUFFICIENT_CREDITS",
            {"required": required, "available": available}
        )

# HTTP Exception mappings
def to_http_exception(error: AIError) -> HTTPException:
    """Convert AI error to HTTP exception."""
    mappings = {
        "NO_MODEL_AVAILABLE": (400, "No model available for this request"),
        "PROVIDER_ERROR": (502, "AI provider error"),
        "RATE_LIMIT_EXCEEDED": (429, "Rate limit exceeded"),
        "INSUFFICIENT_CREDITS": (402, "Insufficient credits"),
    }
    
    status_code, message = mappings.get(error.code, (500, error.message))
    
    return HTTPException(
        status_code=status_code,
        detail={"error": error.code, "message": message, **error.details}
    )
```

## 10. Monitoring

### 10.1 Health Check

```python
# src/api/routes/health.py
from fastapi import APIRouter, Depends
from ...providers.registry import ProviderRegistry

router = APIRouter()

@router.get("/health")
async def health_check():
    """Basic health check."""
    return {
        "status": "healthy",
        "timestamp": datetime.utcnow().isoformat(),
    }

@router.get("/health/detailed")
async def detailed_health(
    redis=Depends(get_redis),
    db=Depends(get_db),
):
    """Detailed health check with component status."""
    
    # Check Redis
    redis_ok = await redis.ping()
    
    # Check Database
    try:
        await db.execute("SELECT 1")
        db_ok = True
    except Exception:
        db_ok = False
    
    # Check Providers
    provider_status = await ProviderRegistry.health_check()
    
    all_ok = redis_ok and db_ok and all(p["healthy"] for p in provider_status.values())
    
    return {
        "status": "healthy" if all_ok else "degraded",
        "timestamp": datetime.utcnow().isoformat(),
        "components": {
            "redis": "ok" if redis_ok else "error",
            "database": "ok" if db_ok else "error",
            "providers": provider_status,
        }
    }

@router.get("/metrics")
async def metrics():
    """Get gateway metrics."""
    
    return {
        "requests_today": await get_daily_request_count(),
        "cost_today": await get_daily_cost(),
        "active_users": await get_active_user_count(),
        "provider_usage": await get_provider_breakdown(),
    }
```
