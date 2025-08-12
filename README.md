# OnePassLink Backend

A secure, zero-knowledge one-time secret sharing service built with Spring Boot 3.5. Users can share sensitive information (passwords, API keys, etc.) through encrypted, self-destructing links.

## 🔐 Security Model

- **Zero-knowledge architecture**: Secrets are encrypted client-side before reaching the server
- **One-time access**: Secrets are automatically deleted after first retrieval
- **URL fragments**: Encryption keys live in URL fragments (`#key`) and never hit the server
- **TTL expiry**: All secrets have configurable time-to-live limits (1 minute to 7 days)
- **Rate limiting**: Built-in abuse protection with IP-based rate limiting

## 🏗️ Architecture

- **Backend**: Spring Boot 3.5 with Java 21
- **Storage**: Redis for ephemeral data with TTL
- **Security**: Strict CSP headers, HSTS, comprehensive security configuration
- **API**: RESTful endpoints with OpenAPI/Swagger documentation
- **Observability**: Prometheus metrics, health checks, structured logging

## 📋 Prerequisites

- **Java 21** or higher
- **Redis 6.0+** (for local development)
- **Maven 3.6+**
- **Docker** (optional, for containerized deployment)

## 🚀 Quick Start

### 1. Clone and Setup

```bash
git clone <your-repo-url>
cd backend
```

### 2. Start Redis (Local Development)

```bash
# Using Docker
docker run -d --name redis-onepasslink -p 6379:6379 redis:7-alpine

# Or install Redis locally
# Windows: https://redis.io/download
# macOS: brew install redis && brew services start redis
# Ubuntu: sudo apt install redis-server && sudo systemctl start redis
```

### 3. Run the Application

```bash
# Development mode (with relaxed security)
mvn spring-boot:run

# Or using your IDE
# Run OnePassLinkBackendApplication.main()
```

### 4. Access the API

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/v3/api-docs
- **Health Check**: http://localhost:8080/actuator/health
- **Test Endpoint**: http://localhost:8080/test

## 📖 API Endpoints

### Core Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/secrets` | Create a new encrypted secret |
| `GET` | `/api/secrets/{id}` | Retrieve and delete secret (one-time) |
| `HEAD` | `/api/secrets/{id}` | Check if secret exists |

### Management Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/actuator/health` | Application health status |
| `GET` | `/actuator/prometheus` | Prometheus metrics |

## 🔧 Configuration

### Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| `SERVER_PORT` | `8080` | Application port |
| `REDIS_PASSWORD` | _(empty)_ | Redis password |
| `REDIS_DATABASE` | `0` | Redis database number |
| `RATE_LIMIT_ENABLED` | `true` | Enable rate limiting |
| `RATE_LIMIT_RPM` | `10` | Requests per minute per IP |
| `SECRET_MAX_SIZE` | `102400` | Max secret size (100KB) |
| `SECRET_MAX_TTL` | `604800` | Max TTL (7 days) |
| `LOG_LEVEL` | `INFO` | Logging level |

### Application Profiles

- **`dev`** (default): Relaxed security, all endpoints accessible
- **`prod`**: Full security headers, strict CSP, production-ready

## 🔄 Development to Production

### Development Environment (Current)

```yaml
# application.yml
spring:
  profiles:
    active: dev  # Relaxed security for development
```

**Features:**
- No authentication required
- All endpoints accessible
- Relaxed CSP headers for Swagger UI
- Debug endpoints enabled

### Production Deployment

#### 1. Update Configuration

```yaml
# application.yml or application-prod.yml
spring:
  profiles:
    active: prod  # Enable production security
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD}
      ssl: ${REDIS_SSL:false}
    
server:
  port: 8080
```

#### 2. Environment Variables (Production)

```bash
export SPRING_PROFILES_ACTIVE=prod
export REDIS_HOST=your-redis-host
export REDIS_PASSWORD=your-redis-password
export REDIS_SSL=true
export SECRET_MAX_SIZE=51200  # 50KB for production
export RATE_LIMIT_RPM=5       # Stricter rate limiting
```

#### 3. Docker Deployment

```bash
# Build the application
mvn clean package -DskipTests

# Build Docker image
docker build -t onepasslink-backend .

# Run with production configuration
docker run -d \
  --name onepasslink-backend \
  -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e REDIS_HOST=your-redis-host \
  -e REDIS_PASSWORD=your-redis-password \
  onepasslink-backend
```

#### 4. Docker Compose (Recommended)

```bash
# Use the provided docker-compose.yml
docker-compose up -d
```

## 🛡️ Security Features

### Production Security (Profile: `prod`)

- **Strict CSP**: Prevents XSS attacks
- **HSTS**: HTTP Strict Transport Security with preload
- **Frame Protection**: X-Frame-Options: DENY
- **Content Type Protection**: X-Content-Type-Options: nosniff
- **Referrer Policy**: Privacy protection
- **Cross-Origin Policies**: Additional security layers

### Development Security (Profile: `dev`)

- **Relaxed CSP**: Allows Swagger UI to function
- **No Authentication**: Easy development access
- **Debug Endpoints**: Additional debugging capabilities

## 📊 Monitoring & Observability

### Health Checks

```bash
# Application health
curl http://localhost:8080/actuator/health

# Redis connectivity
curl http://localhost:8080/actuator/health/redis
```

### Metrics

```bash
# Prometheus metrics
curl http://localhost:8080/actuator/prometheus
```

### Logging

Logs are configured with structured output:
- **Console**: Development-friendly format
- **File**: `/logs/onepasslink.log` (production)
- **Levels**: Configurable via `LOG_LEVEL` environment variable

## 🧪 Testing

### Run Tests

```bash
# Unit tests
mvn test

# Integration tests
mvn test -Dtest=*IntegrationTest

# All tests with coverage
mvn clean test jacoco:report
```

### Manual API Testing

Use the Swagger UI at http://localhost:8080/swagger-ui.html for interactive testing.

## 🔧 Development

### Project Structure

```
src/main/java/com/OnePassLink/backend/
├── api/           # REST controllers
├── config/        # Spring configuration
├── model/         # Data models and DTOs
├── repository/    # Data access layer
├── service/       # Business logic
└── util/          # Utility classes
```

### Adding New Features

1. **Models**: Add to `model/` package
2. **Endpoints**: Add to `api/` package with OpenAPI annotations
3. **Business Logic**: Add to `service/` package
4. **Configuration**: Add to `config/` package
5. **Tests**: Mirror structure in `src/test/java/`

## 🚢 Deployment Options

### 1. Docker (Recommended)

```bash
docker build -t onepasslink-backend .
docker run -p 8080:8080 onepasslink-backend
```

### 2. JAR Deployment

```bash
mvn clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

### 3. Cloud Platforms

- **AWS**: ECS, Elastic Beanstalk, or EC2
- **Google Cloud**: Cloud Run, GKE, or Compute Engine
- **Azure**: Container Instances, AKS, or App Service
- **Heroku**: Direct deployment with Redis add-on

## 🔗 Related Projects

- **Frontend**: (Link to your frontend repository when ready)
- **Infrastructure**: (Link to Terraform/K8s configs when ready)

## 📄 API Documentation

Interactive API documentation is available at `/swagger-ui.html` when the application is running.

## 👨‍💻 Author

**Manjeet Kumar**
- 🌐 Website: [manjeet.work](https://manjeet.work)
- 📧 Email: manjeet.kmr28@gmail.com
- 🐙 GitHub: [@manjeetkmr18](https://github.com/manjeetkmr18)

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Troubleshooting

### Common Issues

**1. Redis Connection Failed**
```bash
# Check Redis is running
redis-cli ping
# Should return: PONG
```

**2. Port Already in Use**
```bash
# Change port in application.yml or use environment variable
export SERVER_PORT=8081
```

**3. Swagger UI Not Loading**
```bash
# Ensure you're using the dev profile
export SPRING_PROFILES_ACTIVE=dev
```

**4. Authentication Required in Dev Mode**
```bash
# Verify dev profile is active
curl http://localhost:8080/actuator/health
```
