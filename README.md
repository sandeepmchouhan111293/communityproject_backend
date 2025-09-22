# Community Management Backend

A comprehensive Spring Boot application for managing community operations including user management, events, volunteer opportunities, document sharing, and discussions.

## 🚀 Features

### Core Functionality
- **User Management**: Registration, authentication, profile management with role-based access
- **Event Management**: Create, manage and register for community events
- **Volunteer Opportunities**: Post and manage volunteer opportunities with registration system
- **Document Management**: Upload and manage community documents with access controls
- **Discussion Forums**: Community discussions with replies
- **Directory Service**: Community member directory
- **Family Management**: Family member tracking for users
- **Audit Logging**: Complete audit trail for admin actions
- **Notifications**: User notification system
- **Settings**: Global and user-specific settings management

### Technical Features
- JWT-based authentication and authorization
- Role-based access control (ADMIN/MEMBER)
- Redis caching for performance optimization
- File upload support (Local/AWS S3)
- Comprehensive audit logging
- RESTful API design
- Input validation and error handling

## 🛠️ Technology Stack

- **Framework**: Spring Boot 3.2.5
- **Java Version**: 17
- **Database**: MySQL 8+
- **Caching**: Redis
- **Security**: Spring Security with JWT
- **ORM**: Hibernate/JPA
- **Build Tool**: Maven
- **Documentation**: OpenAPI/Swagger (planned)

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Redis server
- IDE (IntelliJ IDEA, Eclipse, VS Code)

## 🔧 Installation & Setup

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/communityproject_backend.git
cd communityproject_backend
```

### 2. Database Setup
```sql
-- Create database
CREATE DATABASE community_management;

-- Create user (optional)
CREATE USER 'admin'@'localhost' IDENTIFIED BY 'SNCorridor147@';
GRANT ALL PRIVILEGES ON community_management.* TO 'admin'@'localhost';
FLUSH PRIVILEGES;
```

### 3. Configuration
Update `src/main/resources/application-dev.yml`:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/community_management?useSSL=false&serverTimezone=UTC
    username: your_username
    password: your_password
  redis:
    host: localhost
    port: 6379

app:
  jwt:
    secret: "YourSecretKeyHere"
    expiration: 86400000 # 24 hours

file:
  upload-dir: "./uploads"
```

### 4. Build and Run
```bash
# Build the project
mvn clean compile

# Run the application
mvn spring-boot:run

# Or run with specific profile
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

The application will start on `http://localhost:8080`

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Authentication
All endpoints except authentication require a JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## 🔐 Authentication Endpoints

### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "success": true,
  "message": "User registered successfully"
}
```

### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI..."
}
```

## 👤 User Management Endpoints

### Get Current User Profile
```http
GET /api/users/profile
Authorization: Bearer <token>
```

### Update User Profile
```http
PUT /api/users/profile
Authorization: Bearer <token>
Content-Type: application/json

{
  "fullName": "John Smith",
  "city": "New York",
  "state": "NY",
  "district": "Manhattan",
  "communityName": "NYC Community",
  "phone": "+1234567890"
}
```

### Upload Avatar
```http
POST /api/users/avatar
Authorization: Bearer <token>
Content-Type: multipart/form-data

file: [binary file data]
```

## 🎉 Event Management Endpoints

### Create Event (Admin Only)
```http
POST /api/events
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "title": "Community Meetup",
  "description": "Monthly community gathering",
  "eventDate": "2024-01-15T18:00:00",
  "endDate": "2024-01-15T21:00:00",
  "location": "Community Center",
  "maxParticipants": 50,
  "imageUrl": "https://example.com/image.jpg",
  "registrationRequired": true
}
```

### Get All Events
```http
GET /api/events
Authorization: Bearer <token>

# Optional query parameters:
GET /api/events?title=meetup&location=center&status=UPCOMING
```

### Get Event by ID
```http
GET /api/events/{eventId}
Authorization: Bearer <token>
```

### Update Event (Admin Only)
```http
PUT /api/events/{eventId}
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "title": "Updated Event Title",
  "description": "Updated description",
  "status": "ACTIVE"
}
```

### Delete Event (Admin Only)
```http
DELETE /api/events/{eventId}
Authorization: Bearer <admin-token>
```

### Register for Event
```http
POST /api/events/{eventId}/register
Authorization: Bearer <token>
```

### Unregister from Event
```http
DELETE /api/events/{eventId}/register
Authorization: Bearer <token>
```

### Get Event Participants
```http
GET /api/events/{eventId}/participants
Authorization: Bearer <token>
```

## 🤝 Volunteer Management Endpoints

### Create Volunteer Opportunity (Admin Only)
```http
POST /api/volunteers/opportunities
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "title": "Beach Cleanup",
  "description": "Help clean our local beach",
  "requirements": "Bring gloves and water",
  "location": "Sunset Beach",
  "dateTime": "2024-01-20T09:00:00",
  "durationHours": 4,
  "maxVolunteers": 20
}
```

### Get All Volunteer Opportunities
```http
GET /api/volunteers/opportunities
Authorization: Bearer <token>

# Optional query parameters:
GET /api/volunteers/opportunities?title=cleanup&location=beach&status=ACTIVE
```

### Get Opportunity by ID
```http
GET /api/volunteers/opportunities/{opportunityId}
Authorization: Bearer <token>
```

### Update Opportunity (Admin Only)
```http
PUT /api/volunteers/opportunities/{opportunityId}
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "title": "Updated Title",
  "status": "COMPLETED"
}
```

### Delete Opportunity (Admin Only)
```http
DELETE /api/volunteers/opportunities/{opportunityId}
Authorization: Bearer <admin-token>
```

### Register for Opportunity
```http
POST /api/volunteers/opportunities/{opportunityId}/register
Authorization: Bearer <token>
```

### Unregister from Opportunity
```http
DELETE /api/volunteers/opportunities/{opportunityId}/register
Authorization: Bearer <token>
```

### Get My Volunteer Registrations
```http
GET /api/volunteers/my-registrations
Authorization: Bearer <token>
```

### Get Opportunity Registrations (Admin Only)
```http
GET /api/volunteers/opportunities/{opportunityId}/registrations
Authorization: Bearer <admin-token>
```

### Update Registration Status (Admin Only)
```http
PUT /api/volunteers/registrations/{registrationId}
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "status": "APPROVED",
  "notes": "Excellent volunteer!"
}
```

## 📄 Document Management Endpoints

### Upload Document (Admin Only)
```http
POST /api/documents
Authorization: Bearer <admin-token>
Content-Type: multipart/form-data

request: {
  "title": "Community Guidelines",
  "description": "Official community guidelines document",
  "category": "POLICY",
  "accessLevel": "PUBLIC"
}
file: [binary file data]
```

### Get Documents
```http
GET /api/documents
Authorization: Bearer <token>

# Optional query parameters:
GET /api/documents?title=guidelines&category=POLICY
```

### Get Document by ID
```http
GET /api/documents/{documentId}
Authorization: Bearer <token>
```

### Update Document (Admin Only)
```http
PUT /api/documents/{documentId}
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "title": "Updated Guidelines",
  "description": "Updated community guidelines",
  "accessLevel": "MEMBER"
}
```

### Delete Document (Admin Only)
```http
DELETE /api/documents/{documentId}
Authorization: Bearer <admin-token>
```

### Get Document Categories
```http
GET /api/documents/categories
Authorization: Bearer <token>
```

## 👨‍💼 Admin Endpoints

### Get All Users (Admin Only)
```http
GET /api/admin/users
Authorization: Bearer <admin-token>
```

### Get User by ID (Admin Only)
```http
GET /api/admin/users/{userId}
Authorization: Bearer <admin-token>
```

### Update User Role (Admin Only)
```http
PUT /api/admin/users/{userId}/role
Authorization: Bearer <admin-token>
Content-Type: application/json

{
  "role": "ADMIN"
}
```

### Delete User (Admin Only)
```http
DELETE /api/admin/users/{userId}
Authorization: Bearer <admin-token>
```

### Get Audit Logs (Admin Only)
```http
GET /api/admin/audit-logs
Authorization: Bearer <admin-token>
```

### Get Dashboard Statistics (Admin Only)
```http
GET /api/admin/dashboard
Authorization: Bearer <admin-token>
```

**Response:**
```json
{
  "totalUsers": 150,
  "activeUsers": 140,
  "adminUsers": 5,
  "memberUsers": 145,
  "totalEvents": 25,
  "upcomingEvents": 8,
  "completedEvents": 15,
  "totalDiscussions": 45,
  "totalVolunteerOpportunities": 12,
  "activeVolunteerOpportunities": 8,
  "totalDocuments": 30,
  "publicDocuments": 20,
  "memberDocuments": 10
}
```

### System Health Check (Admin Only)
```http
GET /api/admin/health
Authorization: Bearer <admin-token>
```

## 📁 File Management Endpoints

### Download File
```http
GET /api/files/{fileName}
```

## 🗂️ Additional Endpoints

The application also includes endpoints for:
- **Discussions**: Create and manage community discussions
- **Directory**: Manage community member directory
- **Family Members**: Manage family member information
- **Notifications**: Handle user notifications
- **Settings**: Manage global and user settings

## 🔒 Security

### Roles
- **ADMIN**: Full access to all endpoints, can manage users and content
- **MEMBER**: Limited access, can view and participate in community activities

### JWT Configuration
- **Secret**: Configurable via `app.jwt.secret`
- **Expiration**: 24 hours (configurable via `app.jwt.expiration`)
- **Algorithm**: HS512

## 🚀 Deployment

### Docker Deployment (Recommended)
```bash
# Build Docker image
docker build -t community-backend .

# Run with Docker Compose
docker-compose up -d
```

### Traditional Deployment
```bash
# Build JAR
mvn clean package

# Run JAR
java -jar target/community-backend-0.0.1-SNAPSHOT.jar
```

## 🧪 Testing

### Run Tests
```bash
mvn test
```

### Test Coverage
```bash
mvn clean test jacoco:report
```

## 📊 Monitoring & Logging

- **Logging Level**: DEBUG for development, INFO for production
- **Audit Logging**: All admin actions are logged
- **Performance Monitoring**: Redis caching for frequently accessed data

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details.

## 📞 Support

For support, email support@community.com or create an issue in this repository.

## 🔄 Version History

- **v0.0.1-SNAPSHOT**: Initial release with core functionality

---

**Note**: Replace placeholder values (URLs, credentials, etc.) with actual values for your deployment.