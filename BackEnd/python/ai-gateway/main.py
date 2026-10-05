from fastapi import FastAPI, HTTPException, Request
from fastapi.middleware.cors import CORSMiddleware
from fastapi.responses import JSONResponse

from app.core.config import settings
from app.routers import chat, models, sessions, credits

app = FastAPI(
    title="AI Café AI Gateway",
    description="AI Gateway for AI Café Platform - Chat, Image, Video",
    version="1.0.0",
    docs_url="/docs",
    redoc_url="/redoc",
)

# CORS
app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.CORS_ORIGINS,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


@app.exception_handler(HTTPException)
async def http_exception_handler(request: Request, exc: HTTPException):
    return JSONResponse(
        status_code=exc.status_code,
        content={
            "success": False,
            "error": {
                "code": exc.detail.get("code", "HTTP_ERROR"),
                "message": exc.detail.get("message", str(exc.detail)),
            }
        }
    )


@app.exception_handler(Exception)
async def general_exception_handler(request: Request, exc: Exception):
    return JSONResponse(
        status_code=500,
        content={
            "success": False,
            "error": {
                "code": "INTERNAL_ERROR",
                "message": str(exc),
            }
        }
    )


@app.get("/health")
async def health_check():
    return {"status": "healthy", "service": "ai-gateway"}


# Routers
app.include_router(chat.router, prefix="/v1/chat", tags=["Chat"])
app.include_router(models.router, prefix="/v1/models", tags=["Models"])
app.include_router(sessions.router, prefix="/v1/sessions", tags=["Sessions"])
app.include_router(credits.router, prefix="/v1/credits", tags=["Credits"])


if __name__ == "__main__":
    import uvicorn
    uvicorn.run("main:app", host="0.0.0.0", port=4000, reload=True)
