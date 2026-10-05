from fastapi import APIRouter, Depends, Header

from app.schemas.chat import CreditsResponse

router = APIRouter()

# In-memory credit storage (use database in production)
_credits = {}


@router.get("/", response_model=CreditsResponse)
async def get_credits(user_id: str = Header(..., alias="X-User-ID")):
    """Get user credits balance"""
    balance = _credits.get(user_id, 1000.0)  # Default 1000 credits
    return CreditsResponse(user_id=user_id, balance=balance)


@router.post("/add")
async def add_credits(
    user_id: str = Header(..., alias="X-User-ID"),
    amount: float = 100.0
):
    """Add credits to user account (admin only)"""
    current = _credits.get(user_id, 0)
    _credits[user_id] = current + amount
    return {"user_id": user_id, "balance": _credits[user_id]}


@router.post("/deduct")
async def deduct_credits(
    user_id: str = Header(..., alias="X-User-ID"),
    amount: float = 10.0
):
    """Deduct credits from user account"""
    current = _credits.get(user_id, 0)
    if current < amount:
        return {"error": "Insufficient credits", "balance": current}
    _credits[user_id] = current - amount
    return {"user_id": user_id, "balance": _credits[user_id]}
