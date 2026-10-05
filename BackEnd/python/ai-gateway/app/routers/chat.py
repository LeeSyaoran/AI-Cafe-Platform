from fastapi import APIRouter, HTTPException, Depends, Header
from typing import Optional

from app.schemas.chat import (
    ChatRequest, ChatResponse, ChatMessage,
    ImageGenerationRequest, ImageGenerationResponse,
    ModelInfo, CreditsResponse
)
from app.services.openai_service import OpenAIService
from app.services.claude_service import ClaudeService
from app.services.cost_service import CostService

router = APIRouter()


def get_current_user_id(x_user_id: str = Header(...)) -> str:
    """Extract user ID from request header (set by API Gateway)"""
    return x_user_id


@router.post("/chat", response_model=ChatResponse)
async def chat(
    request: ChatRequest,
    user_id: str = Depends(get_current_user_id)
):
    """Send a chat message and receive AI response"""
    try:
        # Route to appropriate AI provider
        if request.model.startswith("gpt") or request.model.startswith("o1"):
            service = OpenAIService()
            response = await service.chat(
                message=request.message,
                model=request.model,
                temperature=request.temperature,
                max_tokens=request.max_tokens
            )
        elif request.model.startswith("claude"):
            service = ClaudeService()
            response = await service.chat(
                message=request.message,
                model=request.model,
                temperature=request.temperature,
                max_tokens=request.max_tokens
            )
        else:
            raise HTTPException(status_code=400, detail={"code": "INVALID_MODEL", "message": f"Unknown model: {request.model}"})

        # Calculate cost and deduct credits
        cost_service = CostService()
        cost = cost_service.calculate_chat_cost(request.model, response["tokens_used"])
        cost_service.deduct_credits(user_id, cost)

        return ChatResponse(
            message=ChatMessage(role="assistant", content=response["content"]),
            model=request.model,
            session_id=request.session_id or "default",
            tokens_used=response["tokens_used"],
            cost=cost,
            credits_remaining=cost_service.get_balance(user_id)
        )
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail={"code": "CHAT_ERROR", "message": str(e)})


@router.post("/image", response_model=ImageGenerationResponse)
async def generate_image(
    request: ImageGenerationRequest,
    user_id: str = Depends(get_current_user_id)
):
    """Generate images using AI"""
    try:
        service = OpenAIService()
        result = await service.generate_image(
            prompt=request.prompt,
            model=request.model,
            size=request.size,
            quality=request.quality,
            n=request.n
        )

        # Calculate cost
        cost_service = CostService()
        cost = cost_service.calculate_image_cost(request.model, request.n)
        cost_service.deduct_credits(user_id, cost)

        return ImageGenerationResponse(
            images=result["images"],
            model=request.model,
            cost=cost,
            credits_remaining=cost_service.get_balance(user_id)
        )
    except HTTPException:
        raise
    except Exception as e:
        raise HTTPException(status_code=500, detail={"code": "IMAGE_ERROR", "message": str(e)})


@router.get("/models", response_model=list[ModelInfo])
async def list_models():
    """List available AI models"""
    return [
        ModelInfo(
            id="gpt-4o-mini",
            name="GPT-4o Mini",
            provider="OpenAI",
            type="chat",
            max_tokens=16000,
            input_cost_per_1k=0.15,
            output_cost_per_1k=0.60
        ),
        ModelInfo(
            id="gpt-4o",
            name="GPT-4o",
            provider="OpenAI",
            type="chat",
            max_tokens=128000,
            input_cost_per_1k=2.50,
            output_cost_per_1k=10.00
        ),
        ModelInfo(
            id="claude-3-5-sonnet",
            name="Claude 3.5 Sonnet",
            provider="Anthropic",
            type="chat",
            max_tokens=200000,
            input_cost_per_1k=3.00,
            output_cost_per_1k=15.00
        ),
        ModelInfo(
            id="dall-e-3",
            name="DALL-E 3",
            provider="OpenAI",
            type="image",
            input_cost_per_1k=4.00,
            output_cost_per_1k=0.0
        ),
    ]


@router.get("/credits", response_model=CreditsResponse)
async def get_credits(user_id: str = Depends(get_current_user_id)):
    """Get user credits balance"""
    cost_service = CostService()
    balance = cost_service.get_balance(user_id)
    return CreditsResponse(user_id=user_id, balance=balance)
