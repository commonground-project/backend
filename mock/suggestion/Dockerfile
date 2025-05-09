FROM python:3.9-slim
LABEL authors="yichen"

WORKDIR /app

COPY app.py .
COPY config.py .
COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

EXPOSE 5000

CMD ["python", "app.py"]
