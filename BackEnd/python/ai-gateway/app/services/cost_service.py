# Cost model per 1000 tokens
COST_TABLE = {
    # OpenAI Chat
    "gpt-4o-mini": {"input": 0.15, "output": 0.60},
    "gpt-4o": {"input": 2.50, "output": 10.00},
    "gpt-4-turbo": {"input": 10.00, "output": 30.00},
    "gpt-3.5-turbo": {"input": 0.50, "output": 1.50},

    # Anthropic
    "claude-3-5-sonnet": {"input": 3.00, "output": 15.00},
    "claude-3-5-sonnet-20241022": {"input": 3.00, "output": 15.00},
    "claude-3-opus": {"input": 15.00, "output": 75.00},
    "claude-3-sonnet": {"input": 3.00, "output": 15.00},

    # Google
    "gemini-1.5-pro": {"input": 1.25, "output": 5.00},
    "gemini-1.5-flash": {"input": 0.075, "output": 0.30},
}

# Image cost per image
IMAGE_COST = {
    "dall-e-3": 100,  # credits per image
    "dall-e-2": 50,
}


class CostService:
    def __init__(self):
        # In production, use database/Redis
        self._balances = {}

    def calculate_chat_cost(self, model: str, tokens: int) -> float:
        """Calculate cost for chat completion"""
        costs = COST_TABLE.get(model, {"input": 1.0, "output": 2.0})
        # Assume 50% input, 50% output tokens
        input_tokens = tokens // 2
        output_tokens = tokens - input_tokens
        cost = (input_tokens / 1000) * costs["input"] + (output_tokens / 1000) * costs["output"]
        return round(cost, 4)

    def calculate_image_cost(self, model: str, count: int = 1) -> float:
        """Calculate cost for image generation"""
        cost_per_image = IMAGE_COST.get(model, 50)
        return cost_per_image * count

    def deduct_credits(self, user_id: str, amount: float) -> bool:
        """Deduct credits from user balance"""
        balance = self.get_balance(user_id)
        if balance < amount:
            raise ValueError("Insufficient credits")
        self._balances[user_id] = balance - amount
        return True

    def add_credits(self, user_id: str, amount: float):
        """Add credits to user balance"""
        current = self.get_balance(user_id)
        self._balances[user_id] = current + amount

    def get_balance(self, user_id: str) -> float:
        """Get user balance"""
        return self._balances.get(user_id, 1000.0)  # Default 1000 credits
