from fastapi import APIRouter, HTTPException, Depends
from typing import Optional

from app.schemas.chat import ModelInfo

router = APIRouter()

# AI Models Registry
_MODELS = {
    "gpt-4o-mini": {
        "id": "gpt-4o-mini",
        "name": "GPT-4o Mini",
        "provider": "OpenAI",
        "type": "chat",
        "max_tokens": 16000,
        "input_cost_per_1k": 0.15,
        "output_cost_per_1k": 0.60,
        "supports_streaming": True,
        "supports_function_calls": True,
    },
    "gpt-4o": {
        "id": "gpt-4o",
        "name": "GPT-4o",
        "provider": "OpenAI",
        "type": "chat",
        "max_tokens": 128000,
        "input_cost_per_1k": 2.50,
        "output_cost_per_1k": 10.00,
        "supports_streaming": True,
        "supports_function_calls": True,
    },
    "claude-3-5-sonnet": {
        "id": "claude-3-5-sonnet",
        "name": "Claude 3.5 Sonnet",
        "provider": "Anthropic",
        "type": "chat",
        "max_tokens": 200000,
        "input_cost_per_1k": 3.00,
        "output_cost_per_1k": 15.00,
        "supports_streaming": True,
        "supports_function_calls": False,
    },
    "claude-3-opus": {
        "id": "claude-3-opus",
        "name": "Claude 3 Opus",
        "provider": "Anthropic",
        "type": "chat",
        "max_tokens": 200000,
        "input_cost_per_1k": 15.00,
        "output_cost_per_1k": 75.00,
        "supports_streaming": True,
        "supports_function_calls": False,
    },
    "gemini-1.5-pro": {
        "id": "gemini-1.5-pro",
        "name": "Gemini 1.5 Pro",
        "provider": "Google",
        "type": "chat",
        "max_tokens": 2000000,
        "input_cost_per_1k": 1.25,
        "output_cost_per_1k": 5.00,
        "supports_streaming": True,
        "supports_function_calls": True,
    },
    "dall-e-3": {
        "id": "dall-e-3",
        "name": "DALL-E 3",
        "provider": "OpenAI",
        "type": "image",
        "input_cost_per_1k": 4.00,
        "output_cost_per_1k": 0.0,
    },
}


@router.get("/", response_model=list[ModelInfo])
async def list_models(
    provider: Optional[str] = None,
    model_type: Optional[str] = None
):
    """List available AI models, optionally filtered by provider or type"""
    models = list(_MODELS.values())

    if provider:
        models = [m for m in models if m["provider"].lower() == provider.lower()]
    if model_type:
        models = [m for m in models if m["type"] == model_type]

    return [
        ModelInfo(
            id=m["id"],
            name=m["name"],
            provider=m["provider"],
            type=m["type"],
            max_tokens=m.get("max_tokens"),
            input_cost_per_1k=m.get("input_cost_per_1k", 0),
            output_cost_per_1k=m.get("output_cost_per_1k", 0)
        )
        for m in models
    ]


@router.get("/{model_id}", response_model=ModelInfo)
async def get_model(model_id: str):
    """Get details for a specific model"""
    if model_id not in _MODELS:
        raise HTTPException(status_code=404, detail={"code": "MODEL_NOT_FOUND", "message": "Model not found"})

    m = _MODELS[model_id]
    return ModelInfo(
        id=m["id"],
        name=m["name"],
        provider=m["provider"],
        type=m["type"],
        max_tokens=m.get("max_tokens"),
        input_cost_per_1k=m.get("input_cost_per_1k", 0),
        output_cost_per_1k=m.get("output_cost_per_1k", 0)
    )
