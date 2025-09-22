# Community Management Backend - Setup & Deployment Guide

## Table of Contents
1. [Development Environment Setup](#development-environment-setup)
2. [Database Setup](#database-setup)
3. [Application Configuration](#application-configuration)
4. [Running the Application](#running-the-application)
5. [Testing](#testing)
6. [Production Deployment](#production-deployment)
7. [Docker Deployment](#docker-deployment)
8. [Monitoring & Maintenance](#monitoring--maintenance)
9. [Troubleshooting](#troubleshooting)

---

## Development Environment Setup

### Prerequisites
- **Java 17** or higher
- **Maven 3.6+**
- **MySQL 8.0+**
- **Redis Server**
- **Git**
- **IDE** (IntelliJ IDEA, Eclipse, or VS Code)

### Installation Steps

#### 1. Install Java 17
```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk

# Windows (using Chocolatey)
choco install openjdk17

# macOS (using Homebrew)
brew install openjdk@17

# Verify installation
java -version
javac -version
```

#### 2. Install Maven
```bash
# Ubuntu/Debian
sudo apt install maven

# Windows (using Chocolatey)
choco install maven

# macOS (using Homebrew)
brew install maven

# Verify installation
mvn -version
```

#### 3. Install MySQL
```bash
# Ubuntu/Debian
sudo apt install mysql-server

# Windows
# Download from https://dev.mysql.com/downloads/mysql/

# macOS
brew install mysql

# Start MySQL service
sudo systemctl start mysql  # Linux
brew services start mysql   # macOS
```

#### 4. Install Redis
```bash
# Ubuntu/Debian
sudo apt install redis-server

# Windows
# Download from https://github.com/tporadowski/redis/releases

# macOS
brew install redis

# Start Redis service
sudo systemctl start redis-server  # Linux
brew services start redis          # macOS
```

---

## Database Setup

### 1. Create Database and User
```sql
# Connect to MySQL as root
mysql -u root -p

# Create database
CREATE DATABASE community_management;

# Create user (replace with secure password)
CREATE USER 'admin'@'localhost' IDENTIFIED BY 'SecurePassword123!';

# Grant privileges
GRANT ALL PRIVILEGES ON community_management.* TO 'admin'@'localhost';
FLUSH PRIVILEGES;

# Verify
SHOW DATABASES;
SELECT User, Host FROM mysql.user WHERE User = 'admin';
```

### 2. Configure MySQL for Development
```sql
# Optional: Set timezone
SET GLOBAL time_zone = '+00:00';

# Check current settings
SHOW VARIABLES LIKE 'time_zone';
SHOW VARIABLES LIKE 'sql_mode';
```

### 3. Database Schema
The application uses Hibernate with `ddl-auto: update`, so tables will be created automatically on first run. For production, consider using Flyway or Liquibase for database migrations.

---

## Application Configuration

### 1. Clone Repository
```bash
git clone https://github.com/your-username/communityproject_backend.git
cd communityproject_backend
```

### 2. Configure Application Properties

#### Development Configuration (`src/main/resources/application-dev.yml`)
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/community_management?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: admin
    password: SecurePassword123!
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQL8Dialect
  redis:
    host: localhost
    port: 6379
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-idle: 8
        min-idle: 0

logging:
  level:
    com.community.management: DEBUG
    org.springframework.security: DEBUG
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE

app:
  jwt:
    secret: "ThisIsAVerySecureSecretKeyForJWTGenerationAndValidationWhichIsLongEnoughForHS512Algorithm"
    expiration: 86400000 # 24 hours in milliseconds

file:
  upload-dir: "./uploads"
  max-file-size: 10MB
  max-request-size: 10MB

# Server configuration
server:
  port: 8080
  servlet:
    context-path: /
  compression:
    enabled: true
```

#### Production Configuration (`src/main/resources/application-prod.yml`)
```yaml
spring:
  datasource:
    url: ${DB_URL:jdbc:mysql://localhost:3306/community_management}
    username: ${DB_USERNAME:admin}
    password: ${DB_PASSWORD}
    driver-class-name: com.mysql.cj.jdbc.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
  jpa:
    hibernate:
      ddl-auto: validate  # Use 'validate' in production
    show-sql: false
    properties:
      hibernate:
        format_sql: false
        dialect: org.hibernate.dialect.MySQL8Dialect
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 20
        max-idle: 10
        min-idle: 5

logging:
  level:
    com.community.management: INFO
    org.springframework.security: WARN
    org.hibernate.SQL: WARN
  file:
    name: ./logs/community-backend.log
  logback:
    rollingpolicy:
      max-file-size: 10MB
      max-history: 30

app:
  jwt:
    secret: ${JWT_SECRET}
    expiration: ${JWT_EXPIRATION:86400000}

file:
  upload-dir: ${FILE_UPLOAD_DIR:./uploads}
  max-file-size: ${MAX_FILE_SIZE:10MB}
  max-request-size: ${MAX_REQUEST_SIZE:10MB}

server:
  port: ${SERVER_PORT:8080}
  servlet:
    context-path: ${CONTEXT_PATH:/}
  compression:
    enabled: true
```

### 3. Environment Variables for Production
Create a `.env` file (don't commit to git):
```bash
# Database Configuration
DB_URL=jdbc:mysql://localhost:3306/community_management
DB_USERNAME=admin
DB_PASSWORD=YourSecurePassword

# Redis Configuration
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=YourRedisPassword

# JWT Configuration
JWT_SECRET=YourVerySecureJWTSecretKeyThatIsAtLeast256BitsLong
JWT_EXPIRATION=86400000

# File Upload Configuration
FILE_UPLOAD_DIR=./uploads
MAX_FILE_SIZE=10MB
MAX_REQUEST_SIZE=10MB

# Server Configuration
SERVER_PORT=8080
CONTEXT_PATH=/

# Logging
LOG_LEVEL=INFO
```

---

## Running the Application

### 1. Development Mode
```bash
# Build the project
mvn clean compile

# Run with development profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# Or with Maven wrapper
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev

# Alternative: Run from IDE
# Import project into your IDE and run CommunityManagementApplication.java
```

### 2. Package and Run JAR
```bash
# Package the application
mvn clean package -DskipTests

# Run the JAR
java -jar target/community-backend-0.0.1-SNAPSHOT.jar

# Run with specific profile
java -jar target/community-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### 3. Verify Application is Running
```bash
# Check health endpoint
curl http://localhost:8080/api/admin/health

# Check application logs
tail -f logs/community-backend.log
```

---

## Testing

### 1. Run Unit Tests
```bash
mvn test
```

### 2. Run Integration Tests
```bash
mvn verify
```

### 3. Generate Test Coverage Report
```bash
mvn clean test jacoco:report
# View report at target/site/jacoco/index.html
```

### 4. Manual Testing with Postman
1. Import the Postman collection from `postman/Community_Management_API.postman_collection.json`
2. Set up environment variables:
   - `base_url`: `http://localhost:8080/api`
3. Run the authentication requests first to get JWT tokens
4. Test other endpoints using the obtained tokens

---

## Production Deployment

### 1. Server Preparation

#### System Requirements
- **CPU**: 2+ cores
- **RAM**: 4GB minimum, 8GB recommended
- **Storage**: 20GB minimum
- **OS**: Ubuntu 20.04+ / CentOS 8+ / RHEL 8+

#### Install Dependencies
```bash
# Update system
sudo apt update && sudo apt upgrade -y

# Install Java 17
sudo apt install openjdk-17-jdk

# Install MySQL
sudo apt install mysql-server
sudo mysql_secure_installation

# Install Redis
sudo apt install redis-server

# Install Nginx (optional, for reverse proxy)
sudo apt install nginx

# Create application user
sudo useradd -m -s /bin/bash community
sudo mkdir -p /opt/community-backend
sudo chown community:community /opt/community-backend
```

### 2. Database Setup for Production
```sql
# Create production database
CREATE DATABASE community_management_prod;

# Create production user with limited privileges
CREATE USER 'community_prod'@'localhost' IDENTIFIED BY 'VerySecureProductionPassword';
GRANT SELECT, INSERT, UPDATE, DELETE ON community_management_prod.* TO 'community_prod'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Deploy Application
```bash
# Copy JAR to server
scp target/community-backend-0.0.1-SNAPSHOT.jar user@server:/opt/community-backend/

# Create application directory structure
sudo mkdir -p /opt/community-backend/{logs,uploads,config}
sudo chown -R community:community /opt/community-backend

# Create production configuration
sudo cp application-prod.yml /opt/community-backend/config/
```

### 4. Create Systemd Service
```bash
# Create service file
sudo nano /etc/systemd/system/community-backend.service
```

```ini
[Unit]
Description=Community Management Backend
After=network.target mysql.service redis.service

[Service]
Type=forking
User=community
Group=community
WorkingDirectory=/opt/community-backend
ExecStart=/usr/bin/java -jar /opt/community-backend/community-backend-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod --spring.config.location=file:/opt/community-backend/config/application-prod.yml
ExecStop=/bin/kill -TERM $MAINPID
Restart=always
RestartSec=10

# Environment variables
Environment=JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC"
EnvironmentFile=/opt/community-backend/config/.env

# Security settings
NoNewPrivileges=true
PrivateTmp=true
ProtectSystem=strict
ReadWritePaths=/opt/community-backend/logs /opt/community-backend/uploads

[Install]
WantedBy=multi-user.target
```

### 5. Start and Enable Service
```bash
# Reload systemd
sudo systemctl daemon-reload

# Enable service
sudo systemctl enable community-backend

# Start service
sudo systemctl start community-backend

# Check status
sudo systemctl status community-backend

# View logs
sudo journalctl -u community-backend -f
```

### 6. Configure Nginx Reverse Proxy (Optional)
```bash
# Create Nginx configuration
sudo nano /etc/nginx/sites-available/community-backend
```

```nginx
server {
    listen 80;
    server_name your-domain.com;

    # Redirect HTTP to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name your-domain.com;

    # SSL configuration (replace with your certificates)
    ssl_certificate /path/to/your/certificate.crt;
    ssl_certificate_key /path/to/your/private.key;
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;

    # Gzip compression
    gzip on;
    gzip_types text/plain application/json application/javascript text/css application/xml;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;

    # Rate limiting
    limit_req_zone $binary_remote_addr zone=api:10m rate=10r/s;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;

        # Timeouts
        proxy_connect_timeout 60s;
        proxy_send_timeout 60s;
        proxy_read_timeout 60s;

        # Rate limiting
        limit_req zone=api burst=20 nodelay;
    }

    # File upload endpoint with larger body size
    location /api/documents {
        client_max_body_size 10M;
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # Static file serving for uploads
    location /uploads/ {
        alias /opt/community-backend/uploads/;
        expires 1y;
        add_header Cache-Control "public, immutable";
    }
}
```

```bash
# Enable site
sudo ln -s /etc/nginx/sites-available/community-backend /etc/nginx/sites-enabled/

# Test configuration
sudo nginx -t

# Restart Nginx
sudo systemctl restart nginx
```

---

## Docker Deployment

### 1. Create Dockerfile
```dockerfile
FROM openjdk:17-jdk-slim

# Install necessary packages
RUN apt-get update && apt-get install -y \
    curl \
    && rm -rf /var/lib/apt/lists/*

# Create application user
RUN adduser --disabled-password --gecos '' community

# Set working directory
WORKDIR /app

# Copy JAR file
COPY target/community-backend-0.0.1-SNAPSHOT.jar app.jar

# Create uploads directory
RUN mkdir -p uploads logs && chown -R community:community /app

# Switch to application user
USER community

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:8080/api/admin/health || exit 1

# Run application
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### 2. Create Docker Compose Configuration
```yaml
version: '3.8'

services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - DB_HOST=mysql
      - REDIS_HOST=redis
    depends_on:
      - mysql
      - redis
    volumes:
      - uploads_data:/app/uploads
      - logs_data:/app/logs
    restart: unless-stopped
    networks:
      - community-network

  mysql:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=rootpassword
      - MYSQL_DATABASE=community_management
      - MYSQL_USER=admin
      - MYSQL_PASSWORD=password
    volumes:
      - mysql_data:/var/lib/mysql
    ports:
      - "3306:3306"
    restart: unless-stopped
    networks:
      - community-network

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    restart: unless-stopped
    networks:
      - community-network

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./nginx.conf:/etc/nginx/nginx.conf
      - ./ssl:/etc/nginx/ssl
    depends_on:
      - app
    restart: unless-stopped
    networks:
      - community-network

volumes:
  mysql_data:
  redis_data:
  uploads_data:
  logs_data:

networks:
  community-network:
    driver: bridge
```

### 3. Docker Commands
```bash
# Build and run
docker-compose up -d

# View logs
docker-compose logs -f app

# Scale application
docker-compose up -d --scale app=3

# Stop services
docker-compose down

# Remove all data
docker-compose down -v
```

---

## Monitoring & Maintenance

### 1. Application Monitoring
```bash
# Check application status
curl http://localhost:8080/api/admin/health

# Monitor logs
tail -f /opt/community-backend/logs/community-backend.log

# Check system resources
htop
df -h
free -h
```

### 2. Database Monitoring
```sql
-- Check database connections
SHOW PROCESSLIST;

-- Check table sizes
SELECT
    table_name AS 'Table',
    ROUND(((data_length + index_length) / 1024 / 1024), 2) AS 'Size (MB)'
FROM information_schema.TABLES
WHERE table_schema = 'community_management'
ORDER BY (data_length + index_length) DESC;

-- Check slow queries
SHOW VARIABLES LIKE 'slow_query_log';
SHOW VARIABLES LIKE 'long_query_time';
```

### 3. Redis Monitoring
```bash
# Connect to Redis CLI
redis-cli

# Monitor Redis
redis-cli monitor

# Check Redis info
redis-cli info

# Check memory usage
redis-cli info memory
```

### 4. Log Rotation
```bash
# Create logrotate configuration
sudo nano /etc/logrotate.d/community-backend
```

```
/opt/community-backend/logs/*.log {
    daily
    missingok
    rotate 30
    compress
    notifempty
    create 644 community community
    postrotate
        systemctl reload community-backend
    endscript
}
```

### 5. Backup Strategy
```bash
#!/bin/bash
# backup-script.sh

DATE=$(date +%Y%m%d_%H%M%S)
BACKUP_DIR="/opt/backups"
DB_NAME="community_management"

# Create backup directory
mkdir -p $BACKUP_DIR

# Database backup
mysqldump -u admin -p$DB_PASSWORD $DB_NAME > $BACKUP_DIR/db_backup_$DATE.sql

# Uploads backup
tar -czf $BACKUP_DIR/uploads_backup_$DATE.tar.gz /opt/community-backend/uploads

# Remove old backups (keep last 7 days)
find $BACKUP_DIR -name "*.sql" -mtime +7 -delete
find $BACKUP_DIR -name "*.tar.gz" -mtime +7 -delete

echo "Backup completed: $DATE"
```

```bash
# Make script executable
chmod +x backup-script.sh

# Add to crontab for daily backups
crontab -e
# Add: 0 2 * * * /opt/community-backend/backup-script.sh
```

---

## Troubleshooting

### Common Issues

#### 1. Application Won't Start
```bash
# Check Java version
java -version

# Check if port is in use
sudo netstat -tlnp | grep :8080

# Check application logs
tail -f /opt/community-backend/logs/community-backend.log

# Check systemd service status
sudo systemctl status community-backend
```

#### 2. Database Connection Issues
```bash
# Test database connection
mysql -u admin -p -h localhost community_management

# Check MySQL service
sudo systemctl status mysql

# Check MySQL logs
sudo tail -f /var/log/mysql/error.log
```

#### 3. Redis Connection Issues
```bash
# Test Redis connection
redis-cli ping

# Check Redis service
sudo systemctl status redis-server

# Check Redis logs
sudo tail -f /var/log/redis/redis-server.log
```

#### 4. File Upload Issues
```bash
# Check upload directory permissions
ls -la /opt/community-backend/uploads

# Fix permissions if needed
sudo chown -R community:community /opt/community-backend/uploads
sudo chmod 755 /opt/community-backend/uploads
```

#### 5. Memory Issues
```bash
# Check memory usage
free -h

# Adjust JVM memory settings
# Edit /etc/systemd/system/community-backend.service
Environment=JAVA_OPTS="-Xms1g -Xmx4g -XX:+UseG1GC"

# Reload and restart
sudo systemctl daemon-reload
sudo systemctl restart community-backend
```

### Performance Tuning

#### 1. JVM Tuning
```bash
# For 8GB RAM server
JAVA_OPTS="-Xms2g -Xmx6g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -XX:+UseStringDeduplication"
```

#### 2. Database Tuning
```sql
-- MySQL configuration tuning (my.cnf)
[mysqld]
innodb_buffer_pool_size = 2G
innodb_log_file_size = 256M
max_connections = 200
query_cache_size = 64M
tmp_table_size = 64M
max_heap_table_size = 64M
```

#### 3. Redis Tuning
```bash
# Redis configuration (redis.conf)
maxmemory 1gb
maxmemory-policy allkeys-lru
save 900 1
save 300 10
save 60 10000
```

---

## Security Best Practices

### 1. Database Security
- Use strong passwords
- Limit database user privileges
- Enable SSL for database connections
- Regular security updates

### 2. Application Security
- Use strong JWT secrets
- Enable HTTPS in production
- Implement rate limiting
- Regular dependency updates
- Input validation and sanitization

### 3. Server Security
- Regular OS updates
- Firewall configuration
- SSH key authentication
- Fail2ban for intrusion prevention

### 4. Monitoring
- Log monitoring and alerting
- Security scanning
- Regular backups
- Access log analysis

---

This guide covers the complete setup and deployment process for the Community Management Backend. For specific issues or additional configuration requirements, refer to the official Spring Boot documentation or contact the development team.