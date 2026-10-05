from pydantic import BaseModel, Field
from typing import Optional, List, Literal
from datetime import datetime


class ChatMessage(BaseModel):
    role: Literal["user", "assistant", "system"]
    content: str
    timestamp: Optional[datetime] = None


class ChatRequest(BaseModel):
    message: str = Field(..., min_length=1, max_length=4000)
    model: str = Field(default="gpt-4o-mini")
    session_id: Optional[str] = None
    temperature: float = Field(default=0.7, ge=0, le=2)
    max_tokens: int = Field(default=1000, ge=1, le=4000)
    stream: bool = Field(default=False)


class ChatResponse(BaseModel):
    message: ChatMessage
    model: str
    session_id: str
    tokens_used: int
    cost: float
    credits_remaining: float


class ImageGenerationRequest(BaseModel):
    prompt: str = Field(..., min_length=1, max_length=4000)
    model: str = Field(default="dall-e-3")
    size: Literal["1024x1024", "1024x1792", "1792x1024"] = Field(default="1024x1024")
    quality: Literal["standard", "hd"] = Field(default="standard")
    style: Literal["vivid", "natural"] = Field(default="vivid")
    n: int = Field(default=1, ge=1, le=10)


class ImageGenerationResponse(BaseModel):
    images: List[str]  # URLs
    model: str
    cost: float
    credits_remaining: float


class ModelInfo(BaseModel):
    id: str
    name: str
    provider: str
    type: str  # chat, image, video
    max_tokens: Optional[int] = None
    input_cost_per_1k: float
    output_cost_per_1k: float


class SessionInfo(BaseModel):
    id: str
    user_id: str
    messages: List[ChatMessage]
    created_at: datetime
    updated_at: datetime
    tokens_used: int
    cost: float


class CreditsResponse(BaseModel):
    user_id: str
    balance: float
    currency: str = "credits"
