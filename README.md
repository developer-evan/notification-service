# Notification Service

A Spring Boot microservice that accepts notification requests over HTTP, persists them in MongoDB, and processes email delivery asynchronously via RabbitMQ.

## How It Works

1. A client sends a `POST /api/notifications` request.
2. The notification is saved to MongoDB with status `PENDING`.
3. A message is published to RabbitMQ.
4. A consumer picks up the message and sends the email via Mailtrap SMTP.
5. The notification status is updated to `SENT` or `FAILED`.

Failed messages are routed to a dead-letter queue for later inspection.

## Tech Stack

- Java 26
- Spring Boot 4.1
- MongoDB Atlas
- RabbitMQ
- Mailtrap (SMTP)
- Maven

## Prerequisites

- Java 26+
- Maven (or use the included `./mvnw` wrapper)
- Docker (for local RabbitMQ)
- MongoDB connection string
- Mailtrap SMTP credentials

## Getting Started

### 1. Clone the repository

```bash
git clone <repository-url>
cd notification-service-application
```

### 2. Configure environment variables

Copy the example env file and fill in your values:

```bash
cp .env.example .env
```

Required variables:

| Variable        | Description                          |
|-----------------|--------------------------------------|
| `MONGODB_URI`   | MongoDB connection string (Atlas)    |
| `MAIL_USERNAME` | Mailtrap SMTP username               |
| `MAIL_PASSWORD` | Mailtrap SMTP password               |

The app loads `.env` automatically via `spring.config.import` in `application.yml`. **Do not commit `.env`** — it is listed in `.gitignore`.

### 3. Start RabbitMQ

```bash
docker compose up -d
```

RabbitMQ will be available at:

- AMQP: `localhost:5672`
- Management UI: http://localhost:15672 (guest / guest)

### 4. Run the application

```bash
./mvnw spring-boot:run
```

On Windows:

```bash
mvnw.cmd spring-boot:run
```

The service starts on **http://localhost:8080**.

## API Endpoints

### Health check

```http
GET /api/health
```

### Create notification

```http
POST /api/notifications
Content-Type: application/json

{
  "recipient": "user@example.com",
  "subject": "Welcome",
  "body": "Thanks for signing up!"
}
```

**Response** `201 Created`:

```json
{
  "id": "6a5941134a9541fd3c6b2fef",
  "recipient": "user@example.com",
  "subject": "Welcome",
  "status": "PENDING",
  "createdAt": "2026-07-16T20:37:39.148Z"
}
```

### Get notification by ID

```http
GET /api/notifications/{id}
```

**Response** `200 OK`:

```json
{
  "id": "6a5941134a9541fd3c6b2fef",
  "recipient": "user@example.com",
  "subject": "Welcome",
  "status": "SENT",
  "createdAt": "2026-07-16T20:37:39.148Z"
}
```

### Ping

```http
GET /api/ping
```

## Notification Statuses

| Status    | Meaning                                      |
|-----------|----------------------------------------------|
| `PENDING` | Saved and queued, email not yet sent         |
| `SENT`    | Email delivered successfully                 |
| `FAILED`  | Email delivery failed (see `failureReason`)  |

## Configuration

Main settings live in `src/main/resources/application.yml`. Sensitive values are injected from `.env`:

```yaml
spring:
  mongodb:
    uri: ${MONGODB_URI}

  mail:
    host: sandbox.smtp.mailtrap.io
    port: 2525
    username: ${MAIL_USERNAME}
    password: ${MAIL_PASSWORD}
```

RabbitMQ queue and exchange names are under the `notification.rabbitmq` prefix in `application.yml`.

## Actuator

Health and metrics endpoints are exposed at `/actuator`:

- `/actuator/health`
- `/actuator/info`
- `/actuator/metrics`

## Project Structure

```
src/main/java/com/mogeni/notificationserviceapplication/
├── config/          # MongoDB, RabbitMQ configuration
├── controller/      # REST endpoints
├── dto/             # Request/response objects
├── entity/          # MongoDB documents
├── exception/       # Custom exceptions
├── repository/      # Spring Data MongoDB repos
└── service/         # Business logic, producer, consumer, email
```

## Running Tests

```bash
./mvnw test
```
