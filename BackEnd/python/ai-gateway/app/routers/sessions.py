from fastapi import APIRouter, HTTPException, Depends
from typing import Optional

from app.schemas.chat import SessionInfo, ChatMessage

router = APIRouter()

# In-memory session storage (use Redis in production)
_sessions = {}


@router.get("/", response_model=list[SessionInfo])
async def list_sessions(user_id: str = Depends(lambda: None)):
    """List all chat sessions for a user"""
    # TODO: Fetch from database
    return []


@router.get("/{session_id}", response_model=SessionInfo)
async def get_session(session_id: str):
    """Get a specific chat session"""
    if session_id not in _sessions:
        raise HTTPException(status_code=404, detail={"code": "SESSION_NOT_FOUND", "message": "Session not found"})
    return _sessions[session_id]


@router.delete("/{session_id}")
async def delete_session(session_id: str):
    """Delete a chat session"""
    if session_id in _sessions:
        del _sessions[session_id]
    return {"message": "Session deleted"}


@router.post("/{session_id}/clear")
async def clear_session(session_id: str):
    """Clear all messages from a session"""
    if session_id in _sessions:
        _sessions[session_id]["messages"] = []
    return {"message": "Session cleared"}
