# Spring Boot 4 Multi-Module Project with Event-Driven Saga

This project demonstrates a production-like microservices architecture using Spring Boot 4.0.0, Spring Framework 7.x, with an event-driven Saga pattern implemented using Apache Kafka. The API Gateway uses Spring WebFlux for reactive routing with round-robin load balancing.

## Architecture Overview

The project consists of:

- **API Gateway** (port 8080) - Spring WebFlux-based reverse proxy with round-robin load balancing
- **User Service** (2 instances on ports 8081, 8082) - Manages users and their balances
- **Order Service** (port 8091) - Manages orders and orchestrates the saga
- **Event-Driven Saga (Choreography)** - Uses Apache Kafka for asynchronous communication with compensation support
- **PostgreSQL** - Separate databases for each service
- **Distributed Tracing** - Micrometer Tracing + Zipkin for end-to-end observability

## Key Highlights

- **Spring Boot 4.0.0 with Spring Framework 7.x** - Latest and greatest!
- **Reactive Gateway** - Pure WebFlux implementation (no Spring Cloud Gateway dependencies)
- **Saga Pattern** - Asynchronous event-driven orchestration via Kafka with compensation support
- **Load Balancing** - Round-robin distribution across user-service instances
- **End-to-End Tracing** - Track requests across all services with the same traceId
- **Production-Ready** - Health checks, metrics, migrations, and comprehensive documentation

## Technology Stack

- **Java 21** - Programming language
- **Spring Boot 4.0.0** - Application framework
- **Spring Framework 7.x** - Core framework
- **Spring WebFlux** - Reactive web framework (Gateway)
- **Apache Kafka 7.6.0** - Message broker for event-driven communication
- **PostgreSQL 16** - Relational database
- **Flyway** - Database migrations
- **MapStruct 1.6.2** - DTO mapping
- **Micrometer Tracing + Zipkin** - Distributed tracing
- **Docker & Docker Compose** - Containerization

## Project Structure

```
springboot4/
├── common-events/          # Shared event DTOs (Kafka events)
├── gateway/               # API Gateway (WebFlux)
├── user-service/          # User Service (2 instances)
├── order-service/         # Order Service
├── docker-compose.yml     # Docker Compose configuration
└── pom.xml                # Parent POM
```

## Prerequisites

Before you begin, ensure you have the following installed on your local machine:

### Required Software

1. **Java 21**
   - **Eclipse Temurin (Adoptium)** - OpenJDK builds: https://adoptium.net/
   - **Amazon Corretto** - Amazon's OpenJDK distribution: https://aws.amazon.com/corretto/
   - **Oracle JDK** - Oracle's official JDK: https://www.oracle.com/java/technologies/downloads/#java21
   - **Microsoft Build of OpenJDK**: https://www.microsoft.com/openjdk
   - **Azul Zulu** - OpenJDK builds: https://www.azul.com/downloads/
   
   **Recommended**: Eclipse Temurin (Adoptium) or Amazon Corretto for production use.
   
   Verify installation:
     ```bash
     java -version
     # Should show: openjdk version "21" or java version "21"
     ```

2. **Maven 3.9+**
   - Download from: https://maven.apache.org/download.cgi
   - Verify installation:
     ```bash
     mvn -version
     # Should show: Apache Maven 3.9.x
     ```

3. **Docker Desktop** (or Docker Engine + Docker Compose)
   - **macOS/Windows**: Download from https://www.docker.com/products/docker-desktop/
   - **Linux**: Install Docker Engine and Docker Compose plugin
   - Verify installation:
     ```bash
     docker --version
     # Should show: Docker version 20.10+
     docker compose version
     # Should show: Docker Compose version v2.0+
     ```

4. **Git** (to clone the repository)
   - Verify installation:
     ```bash
     git --version
     ```

### System Requirements

- **RAM**: Minimum 8GB (16GB recommended)
- **Disk Space**: At least 2GB free space
- **CPU**: Multi-core processor recommended
- **Ports**: Ensure the following ports are available:
  - `8080` - API Gateway
  - `8081`, `8082` - User Service instances
  - `8091` - Order Service
  - `5433`, `5434` - PostgreSQL databases
  - `9092`, `9093` - Kafka
  - `9411` - Zipkin
  - `2181` - Zookeeper

## Getting Started

### Step 1: Clone the Repository

```bash
git clone <repository-url>
cd springboot4
```

### Step 2: Verify Prerequisites

Run these commands to verify all prerequisites are installed:

```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Check Docker version
docker --version
docker compose version

# Check if ports are available (optional)
# On macOS/Linux:
lsof -i :8080 -i :8081 -i :8082 -i :8091 -i :5433 -i :5434 -i :9092 -i :9411
# If any ports are in use, stop those services or change ports in docker-compose.yml
```

### Step 3: Build the Project

Build all modules using Maven:

```bash
# From the project root directory
mvn clean package -DskipTests
```

This command will:
- Compile all modules (common-events, gateway, user-service, order-service)
- Run tests (skipped with `-DskipTests` flag)
- Package JAR files in each service's `target/` directory

**Expected output**: `BUILD SUCCESS` message

**Note**: The first build may take 5-10 minutes as Maven downloads dependencies.

### Step 4: Start the System with Docker Compose

Start all services using Docker Compose:

```bash
docker compose up --build
```

This command will:
1. Build Docker images for all services (gateway, user-service, order-service)
2. Start PostgreSQL databases (user_db on port 5433, order_db on port 5434)
3. Start Zookeeper (for Kafka coordination)
4. Start Kafka broker (ports 9092, 9093)
5. Start Zipkin (distributed tracing UI on port 9411)
6. Start user-service instances (ports 8081, 8082)
7. Start order-service (port 8091)
8. Start gateway (port 8080)

**Expected behavior**:
- You'll see logs from all services
- Services will start in dependency order (databases first, then Kafka, then services)
- Wait until you see "Started GatewayApplication" or similar messages for all services
- This process typically takes 1-2 minutes

**To run in detached mode** (background):
```bash
docker compose up --build -d
```

**To view logs**:
```bash
# All services
docker compose logs -f

# Specific service
docker compose logs -f gateway
docker compose logs -f user-service-1
docker compose logs -f order-service
```

### Step 5: Verify Services are Running

Check the health of all services:

```bash
# Check container status
docker compose ps

# All containers should show "healthy" status
```

Test individual service health endpoints:

```bash
# Gateway
curl http://localhost:8080/actuator/health

# User Service Instance 1
curl http://localhost:8081/actuator/health

# User Service Instance 2
curl http://localhost:8082/actuator/health

# Order Service
curl http://localhost:8091/actuator/health
```

All should return: `{"status":"UP"}`

## Access Points

Once all services are running, you can access:

| Service | URL | Description |
|---------|-----|-------------|
| **API Gateway** | http://localhost:8080 | Main entry point for all API requests |
| **User Service 1** | http://localhost:8081 | Direct access to user service instance 1 |
| **User Service 2** | http://localhost:8082 | Direct access to user service instance 2 |
| **Order Service** | http://localhost:8091 | Direct access to order service |
| **Zipkin UI** | http://localhost:9411 | Distributed tracing dashboard |
| **Zipkin API** | http://localhost:9411/api/v2 | Zipkin REST API |

## Testing the System

**Important Note**: All IDs (user IDs, order IDs) are auto-generated UUIDs. When you run these examples, you'll get different UUID values. Always use the actual IDs returned in the responses, not the example UUIDs shown in this documentation.

### Example 1: Create a User

Create a new user with an initial balance:

```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "john_doe",
    "initialBalance": 10000
  }'
```

**Expected Response**:
```json
{
  "id": "{userId}",
  "username": "john_doe",
  "balance": 10000,
  "createdAt": "2025-12-12T22:00:00"
}
```

**Note**: The `createdAt` field is automatically set to the current date and time when the user is created. The actual value will be the current timestamp in ISO format (e.g., `2025-12-12T22:00:00`).

**Important**: Save the actual `id` value from the response (it will be a UUID like `550e8400-e29b-41d4-a716-446655440000`). You'll need this `{userId}` for creating orders in the next steps.

### Example 2: Get User by ID

Retrieve user information:

```bash
# Replace {userId} with the actual user ID from Example 1
curl http://localhost:8080/users/{userId}
```

**Example** (replace `{userId}` with the actual UUID from Example 1):
```bash
curl http://localhost:8080/users/{userId}
```

### Example 3: Create an Order (Starts Saga)

Create an order to trigger the event-driven saga:

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "{userId}",
    "amount": 5000
  }'
```

**Note**: Replace `{userId}` with the actual user ID from Example 1.

**Expected Response** (initially PENDING):
```json
{
  "id": "{orderId}",
  "userId": "{userId}",
  "amount": 5000,
  "status": "PENDING",
  "createdAt": "2025-12-12T22:00:05"
}
```

**Note**: The `createdAt` field is automatically set to the current date and time when the order is created.

**Important**: Save the actual order `id` value from the response (it will be a UUID). You'll need this `{orderId}` for checking order status in the next steps.

**What happens behind the scenes**:
1. Order Service creates order with status `PENDING`
2. Publishes `OrderCreatedEvent` to Kafka topic `order.created`
3. User Service listens to the event and attempts to reserve credit
4. If successful: Order status changes to `CONFIRMED`
5. If failed: Order status changes to `CANCELED`

### Example 4: Check Order Status

Wait 2-3 seconds for the saga to complete, then check the order status:

```bash
# Replace {orderId} with the actual order ID from Example 3
curl http://localhost:8080/orders/{orderId}
```

**Example** (replace `{orderId}` with the actual order ID from Example 3):
```bash
curl http://localhost:8080/orders/{orderId}
```

**Expected Response** (should be CONFIRMED if user had sufficient balance):
```json
{
  "id": "{orderId}",
  "userId": "{userId}",
  "amount": 5000,
  "status": "CONFIRMED",
  "createdAt": "2025-12-12T22:00:05"
}
```

**Note**: The `createdAt` field shows when the order was created (automatically set to current timestamp in ISO format).

### Example 5: Test Insufficient Balance Scenario

Create another order with an amount greater than the user's remaining balance:

```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "{userId}",
    "amount": 10000
  }'
```

**Note**: Replace `{userId}` with the actual user ID from Example 1.

Wait a few seconds, then check the order status - it should be `CANCELED` because the user doesn't have enough balance (user had 10000, first order used 5000, remaining balance is 5000, but order requested 10000).

### Example 6: Test Compensation (Order Cancellation)

This example demonstrates the compensation mechanism in the saga pattern. When an order is canceled, the reserved credit is automatically released back to the user.

**Step 1**: Create a user with initial balance:
```bash
curl -X POST http://localhost:8080/users \
  -H "Content-Type: application/json" \
  -d '{
    "username": "compensation_test",
    "initialBalance": 10000
  }'
```

**Save the `id` value** from the response.

**Step 2**: Check initial balance:
```bash
# Replace {userId} with the actual user ID from Step 1
curl http://localhost:8080/users/{userId}
```

**Expected Response**:
```json
{
  "id": "{userId}",
  "username": "compensation_test",
  "balance": 10000,
  "createdAt": "2025-12-12T22:00:00"
}
```

**Note**: 
- The `id` field will contain an auto-generated UUID. Save this `{userId}` for the next steps.
- The `createdAt` field is automatically set to the current date and time when the user is created (ISO format timestamp).

**Step 3**: Create an order (credit will be reserved):
```bash
curl -X POST http://localhost:8080/orders \
  -H "Content-Type: application/json" \
  -d '{
    "userId": "{userId}",
    "amount": 5000
  }'
```

**Note**: Replace `{userId}` with the actual user ID from Step 1.

**Save the order `id`** from the response (it will be a UUID like `{orderId}`).

**Step 4**: Wait 2-3 seconds for credit reservation, then check balance:
```bash
curl http://localhost:8080/users/{userId}
```

**Note**: Replace `{userId}` with the actual user ID from Step 1.

**Expected Response** (balance should be reduced):
```json
{
  "id": "{userId}",
  "username": "compensation_test",
  "balance": 5000,
  "createdAt": "2025-12-12T22:00:00"
}
```

**Note**: The `createdAt` field shows when the user was created (automatically set to current timestamp in ISO format).

**Step 5**: Cancel the order (triggers compensation):
```bash
curl -X DELETE http://localhost:8080/orders/{orderId}
```

**Note**: Replace `{orderId}` with the actual order ID from Step 3.

**Expected Response**: `204 No Content`

**Step 6**: Wait 2-3 seconds for compensation to complete, then check balance again:
```bash
curl http://localhost:8080/users/{userId}
```

**Note**: Replace `{userId}` with the actual user ID from Step 1.

**Expected Response** (balance should be restored):
```json
{
  "id": "{userId}",
  "username": "compensation_test",
  "balance": 10000,
  "createdAt": "2025-12-12T22:00:00"
}
```

**Note**: The `createdAt` field shows when the user was created (automatically set to current timestamp in ISO format).

**Step 7**: Verify order status is CANCELED:
```bash
curl http://localhost:8080/orders/{orderId}
```

**Note**: Replace `{orderId}` with the actual order ID from Step 3.

**Expected Response**:
```json
{
  "id": "{orderId}",
  "userId": "{userId}",
  "amount": 5000,
  "status": "CANCELED",
  "createdAt": "2025-12-12T22:00:05"
}
```

**Note**: The `createdAt` field shows when the order was created (automatically set to current timestamp in ISO format).

**What happened**:
1. Order was created and credit was reserved (balance: 10000 → 5000)
2. Order was canceled via DELETE endpoint
3. `OrderCanceledEvent` was published to Kafka
4. User Service received the event and released the credit (compensation)
5. Balance was restored (5000 → 10000)
6. Order status changed to `CANCELED`

### Example 7: Test Round-Robin Load Balancing

The gateway distributes requests to user-service instances in a round-robin fashion. Test this:

```bash
# Make 10 requests - they should alternate between instance 1 and 2
for i in {1..10}; do
  echo "Request $i:"
  curl -X POST http://localhost:8080/users \
    -H "Content-Type: application/json" \
    -d "{\"username\":\"testuser$i\",\"initialBalance\":1000}" \
    -s | grep -o '"id":"[^"]*"' | head -1
  sleep 0.2
done
```

Check the gateway logs to see round-robin routing:
```bash
docker compose logs gateway | grep "Round-robin"
```

## Viewing Distributed Traces in Zipkin

### Access Zipkin UI

Open your web browser and navigate to:
```
http://localhost:9411
```

### Finding Traces

1. **By Service Name**:
   - In the search box, select a service from the dropdown:
     - `gateway`
     - `user-service`
     - `order-service`
   - Click "Run Query"

2. **By Trace ID**:
   - If you have a trace ID from logs (format: `[service-name,traceId,spanId]`), enter it in the search box
   - Click "Run Query"

3. **By Time Range**:
   - Use the time range selector to filter traces by time
   - Default shows traces from the last 15 minutes

### Understanding Trace Visualization

- **Each trace** represents a single request flow through multiple services
- **Spans** represent individual operations (HTTP requests, Kafka messages)
- **Timeline view** shows the duration and sequence of operations
- **Dependency graph** shows service relationships

### Example: Viewing an Order Creation Trace

1. Create an order using Example 3
2. Open Zipkin UI: http://localhost:9411
3. Search for service: `order-service`
4. Click on a trace to see the full flow:
   - Gateway receives request
   - Gateway forwards to Order Service
   - Order Service publishes Kafka event
   - User Service consumes event
   - User Service publishes response event
   - Order Service updates status

### Zipkin API Endpoints

You can also query traces programmatically:

```bash
# Get traces for a specific service
curl "http://localhost:9411/api/v2/traces?serviceName=gateway&limit=10"

# Get traces for order-service
curl "http://localhost:9411/api/v2/traces?serviceName=order-service&limit=10"

# Get traces for user-service
curl "http://localhost:9411/api/v2/traces?serviceName=user-service&limit=10"
```

## API Endpoints Reference

All endpoints are accessible through the gateway at `http://localhost:8080`.

### User Service Endpoints

#### Create User
```bash
POST /users
Content-Type: application/json

{
  "username": "string",
  "initialBalance": 10000
}
```

**Response**: `201 Created`
```json
{
  "id": "{userId}",
  "username": "string",
  "balance": 10000,
  "createdAt": "2025-12-12T22:00:00"
}
```

**Note**: 
- The `id` field is an auto-generated UUID. Use the actual value returned in the response.
- The `createdAt` field is automatically set to the current date and time when the user is created (ISO format timestamp).

#### Get User by ID
```bash
GET /users/{id}
```

**Response**: `200 OK`
```json
{
  "id": "{userId}",
  "username": "string",
  "balance": 10000,
  "createdAt": "2025-12-12T22:00:00"
}
```

**Note**: 
- Replace `{userId}` in the URL with the actual UUID returned when creating the user.
- The `createdAt` field shows when the user was created (automatically set to current timestamp in ISO format).

### Order Service Endpoints

#### Create Order (Starts Saga)
```bash
POST /orders
Content-Type: application/json

{
  "userId": "{userId}",
  "amount": 5000
}
```

**Note**: Replace `{userId}` with the actual user UUID from the create user response.

**Response**: `201 Created`
```json
{
  "id": "{orderId}",
  "userId": "{userId}",
  "amount": 5000,
  "status": "PENDING",
  "createdAt": "2025-12-12T22:00:05"
}
```

**Note**: 
- The `id` field is an auto-generated UUID. Use the actual value returned in the response.
- The `createdAt` field is automatically set to the current date and time when the order is created (ISO format timestamp).
- Status will change to `CONFIRMED` or `CANCELED` after saga completes (2-3 seconds).

#### Get Order by ID
```bash
GET /orders/{id}
```

**Response**: `200 OK`
```json
{
  "id": "{orderId}",
  "userId": "{userId}",
  "amount": 5000,
  "status": "CONFIRMED",
  "createdAt": "2025-12-12T22:00:05"
}
```

**Note**: 
- Replace `{orderId}` in the URL with the actual UUID returned when creating the order.
- The `createdAt` field shows when the order was created (automatically set to current timestamp in ISO format).

#### Cancel Order (Compensation)
```bash
DELETE /orders/{id}
```

**Response**: `204 No Content`

**What happens**:
1. Order status is updated to `CANCELED`
2. `OrderCanceledEvent` is published to Kafka topic `order.canceled`
3. User Service receives the event and releases reserved credit (compensation)
4. User's balance is restored

**Note**: This endpoint triggers the compensation mechanism in the saga pattern. Only orders with status `PENDING` or `CONFIRMED` will trigger compensation (orders that had credit reserved).

### Health Check Endpoints

All services expose health check endpoints:

```bash
# Gateway
GET http://localhost:8080/actuator/health

# User Service 1
GET http://localhost:8081/actuator/health

# User Service 2
GET http://localhost:8082/actuator/health

# Order Service
GET http://localhost:8091/actuator/health
```

### Prometheus Metrics (Optional)

Metrics are exposed at `/actuator/prometheus`:

```bash
curl http://localhost:8080/actuator/prometheus
curl http://localhost:8081/actuator/prometheus
curl http://localhost:8091/actuator/prometheus
```

## Saga Flow Explained

The order processing saga follows a **choreography pattern**:

1. **Client → Gateway → Order Service**:
   - POST `/orders` with `userId` and `amount`
   - Order Service creates order with status `PENDING`
   - Publishes `OrderCreatedEvent` to Kafka topic `order.created`

2. **User Service** (listens to `order.created`):
   - Attempts to reserve credit from user's balance
   - If successful: publishes `UserCreditReservedEvent` to `user.credit-reserved`
   - If failed: publishes `UserCreditReservationFailedEvent` to `user.credit-reservation-failed`

3. **Order Service** (listens to credit events):
   - On `UserCreditReservedEvent`: updates order status to `CONFIRMED`
   - On `UserCreditReservationFailedEvent`: updates order status to `CANCELED`

4. **Compensation Flow** (when order is canceled):
   - Order Service receives DELETE request and publishes `OrderCanceledEvent` to `order.canceled`
   - User Service listens to `order.canceled` and releases reserved credit
   - User's balance is restored automatically

**Key Points**:
- All communication is asynchronous via Kafka
- No direct service-to-service HTTP calls for saga coordination
- Each service is responsible for its own part of the saga
- Compensation mechanism ensures data consistency when orders are canceled
- Distributed tracing tracks the entire flow across services

## Kafka Topics

The following Kafka topics are used:

- `order.created` - Published by order-service when an order is created
- `order.canceled` - Published by order-service when an order is canceled (triggers compensation)
- `user.credit-reserved` - Published by user-service when credit is successfully reserved
- `user.credit-reservation-failed` - Published by user-service when credit reservation fails

Topics are **auto-created** by Kafka when first used.

## Stopping the System

### Stop All Services

```bash
docker compose down
```

This stops all containers but preserves data volumes (databases).

### Stop and Remove Volumes

To completely remove all data (databases will be reset):

```bash
docker compose down -v
```

**Warning**: This will delete all data in PostgreSQL databases!

### Stop Specific Service

```bash
docker compose stop gateway
docker compose stop user-service-1
```

### Restart Services

```bash
docker compose restart gateway
docker compose up -d  # Start stopped services
```

## Troubleshooting

### Issue: Docker Compose Fails to Start

**Error**: "client version X is too old"

**Solution**: Upgrade Docker Desktop to the latest version
- macOS/Windows: Download from https://www.docker.com/products/docker-desktop/
- Linux: Update Docker Engine and Docker Compose plugin

**Alternative**: Use `docker-compose` (with hyphen) instead of `docker compose`:
```bash
docker-compose up --build
```

### Issue: Port Already in Use

**Error**: "Bind for 0.0.0.0:8080 failed: port is already allocated"

**Solution**: 
1. Find the process using the port:
   ```bash
   # macOS/Linux
   lsof -i :8080
   
   # Windows
   netstat -ano | findstr :8080
   ```
2. Stop the process or change the port in `docker-compose.yml`

### Issue: Services Not Starting

**Symptoms**: Containers keep restarting or show "unhealthy" status

**Solution**:
1. Check logs:
   ```bash
   docker compose logs <service-name>
   # Example:
   docker compose logs gateway
   docker compose logs user-service-1
   ```

2. Check service dependencies:
   ```bash
   docker compose ps
   ```
   Ensure all dependencies (PostgreSQL, Kafka, Zipkin) are healthy before services start.

3. Check database connectivity:
   ```bash
   docker compose logs postgres-user-db
   docker compose logs postgres-order-db
   ```

### Issue: Kafka Connection Errors

**Symptoms**: Services can't connect to Kafka

**Solution**:
1. Verify Kafka is running:
   ```bash
   docker compose ps kafka
   docker compose logs kafka
   ```

2. Check Kafka health:
   ```bash
   docker compose exec kafka kafka-broker-api-versions --bootstrap-server localhost:9092
   ```

3. Restart Kafka:
   ```bash
   docker compose restart kafka
   ```

### Issue: Database Connection Errors

**Symptoms**: Services can't connect to PostgreSQL

**Solution**:
1. Verify PostgreSQL containers are running:
   ```bash
   docker compose ps postgres-user-db postgres-order-db
   ```

2. Check database logs:
   ```bash
   docker compose logs postgres-user-db
   docker compose logs postgres-order-db
   ```

3. Test database connection:
   ```bash
   docker compose exec postgres-user-db psql -U user -d user_db -c "SELECT 1;"
   docker compose exec postgres-order-db psql -U user -d order_db -c "SELECT 1;"
   ```

### Issue: No Traces in Zipkin

**Symptoms**: Zipkin UI shows no traces

**Solution**:
1. Verify Zipkin is running:
   ```bash
   docker compose ps zipkin
   curl http://localhost:9411/health
   ```

2. Check service logs for tracing errors:
   ```bash
   docker compose logs gateway | grep -i trace
   docker compose logs user-service-1 | grep -i trace
   ```

3. Verify tracing configuration in `application.yml` files

4. Make some API requests and wait a few seconds for traces to appear

### Issue: Maven Build Fails

**Error**: "release version 21 not supported"

**Solution**: Ensure Java 21 is installed and active:
```bash
java -version  # Should show version 21
mvn -version   # Should show Java 21 in JAVA_HOME
```

**On macOS with multiple Java versions**:
```bash
# List installed Java versions
/usr/libexec/java_home -V

# Set JAVA_HOME for current session
export JAVA_HOME=$(/usr/libexec/java_home -v 21)
```

### Issue: Out of Memory Errors

**Symptoms**: Services crash with OutOfMemoryError

**Solution**: Increase Docker Desktop memory allocation:
1. Open Docker Desktop
2. Go to Settings → Resources
3. Increase Memory to at least 8GB (16GB recommended)
4. Apply & Restart

## Development

### Running Services Locally (without Docker)

If you want to run services locally for development:

1. **Start infrastructure services only** (PostgreSQL, Kafka, Zipkin):
   ```bash
   docker compose up postgres-user-db postgres-order-db zookeeper kafka zipkin
   ```

2. **Run services from your IDE or terminal**:
   ```bash
   # Terminal 1 - User Service Instance 1
   cd user-service
   mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081

   # Terminal 2 - User Service Instance 2
   cd user-service
   mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8082

   # Terminal 3 - Order Service
   cd order-service
   mvn spring-boot:run

   # Terminal 4 - Gateway
   cd gateway
   mvn spring-boot:run
   ```

   Services will automatically connect to Docker containers for PostgreSQL, Kafka, and Zipkin.

### Rebuilding After Code Changes

After making code changes:

```bash
# Rebuild and restart all services
docker compose up --build

# Or rebuild specific service
docker compose build gateway
docker compose up -d gateway
```

## Database Schemas

### user_db (User Service)

```sql
CREATE TABLE users (
    id UUID PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    balance BIGINT NOT NULL,
    created_at TIMESTAMP NOT NULL
);
```

### order_db (Order Service)

```sql
CREATE TABLE orders (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    amount BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL
);
```

Flyway migrations are automatically executed on service startup.

## Distributed Tracing

All services are configured with:
- **Micrometer Tracing** bridge to Zipkin
- **Trace context propagation** across HTTP (via WebClient) and Kafka
- **Automatic span creation** for HTTP requests and Kafka messages

**Log format** includes `traceId` and `spanId`:
```
2025-12-12 22:00:00.000  INFO [gateway,abc123def456,xyz789] ... - Message
```

**Trace ID** is the same across all services for a single transaction, allowing you to track a request from gateway through all downstream services.

## Load Balancing

The API Gateway implements **round-robin load balancing** for user-service instances:

- Requests to `/users/**` are distributed evenly between:
  - `user-service-1` (port 8081)
  - `user-service-2` (port 8082)

- Each request alternates between instances
- Load is distributed 50/50 between instances

## License

This is a demonstration project for educational purposes.
