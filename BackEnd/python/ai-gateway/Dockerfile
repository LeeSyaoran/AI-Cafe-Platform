# Python AI Gateway Dockerfile - No gcc needed
FROM python:3.11-slim-bookworm

WORKDIR /app

# Copy requirements and install Python dependencies (no gcc needed)
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy application code
COPY . .

EXPOSE 4000

CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "4000"]
