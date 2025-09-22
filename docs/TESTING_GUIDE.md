# API Testing Guide

## Quick Start with Postman

### 1. Import Collection and Environment
1. Open Postman
2. Import `postman/Community_Management_API.postman_collection.json`
3. Import `postman/Community_Management_Environment.postman_environment.json`
4. Select the "Community Management Environment" from the environment dropdown

### 2. Initial Setup Steps

#### Step 1: Register a Test User
```
POST {{base_url}}/auth/register
```
Body:
```json
{
  "fullName": "Test User",
  "email": "testuser@example.com",
  "password": "testPassword123"
}
```

#### Step 2: Register an Admin User (Do this first for testing)
```
POST {{base_url}}/auth/register
```
Body:
```json
{
  "fullName": "Admin User",
  "email": "admin@example.com",
  "password": "adminPassword123"
}
```

#### Step 3: Login as Regular User
```
POST {{base_url}}/auth/login
```
Body:
```json
{
  "email": "testuser@example.com",
  "password": "testPassword123"
}
```
**Note**: The JWT token will be automatically saved to the environment variable.

#### Step 4: Login as Admin (After promoting user to admin)
```
POST {{base_url}}/auth/login
```
Body:
```json
{
  "email": "admin@example.com",
  "password": "adminPassword123"
}
```

### 3. Testing Workflow

#### A. User Management Flow
1. **Register User** → **Login** → **Get Profile** → **Update Profile** → **Upload Avatar**

#### B. Event Management Flow (Admin)
1. **Login as Admin** → **Create Event** → **Get All Events** → **Register for Event** → **Get Participants** → **Update Event** → **Delete Event**

#### C. Volunteer Management Flow (Admin)
1. **Login as Admin** → **Create Opportunity** → **Get Opportunities** → **Register for Opportunity** → **Get My Registrations** → **Update Registration Status**

#### D. Document Management Flow (Admin)
1. **Login as Admin** → **Upload Document** → **Get Documents** → **Update Document** → **Delete Document**

#### E. Admin Operations Flow
1. **Login as Admin** → **Get All Users** → **Update User Role** → **Get Dashboard Stats** → **Get Audit Logs**

### 4. Test Data Examples

#### Sample Event Data
```json
{
  "title": "Community Cleanup Day",
  "description": "Join us for a community-wide cleanup initiative",
  "eventDate": "2024-12-15T09:00:00",
  "endDate": "2024-12-15T15:00:00",
  "location": "Central Park",
  "maxParticipants": 100,
  "registrationRequired": true
}
```

#### Sample Volunteer Opportunity Data
```json
{
  "title": "Food Bank Volunteer",
  "description": "Help sort and distribute food to families in need",
  "requirements": "Must be 16+ years old, comfortable lifting 25lbs",
  "location": "Community Food Bank",
  "dateTime": "2024-12-20T08:00:00",
  "durationHours": 6,
  "maxVolunteers": 15
}
```

#### Sample Document Upload
- Use form-data with:
  - `request` field containing JSON metadata
  - `file` field containing the actual file

## Command Line Testing with curl

### Authentication
```bash
# Register user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Test User",
    "email": "test@example.com",
    "password": "password123"
  }'

# Login and save token
TOKEN=$(curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }' | jq -r '.token')

echo "Token: $TOKEN"
```

### API Calls with Authentication
```bash
# Get user profile
curl -X GET http://localhost:8080/api/users/profile \
  -H "Authorization: Bearer $TOKEN"

# Get all events
curl -X GET http://localhost:8080/api/events \
  -H "Authorization: Bearer $TOKEN"

# Create event (admin only)
curl -X POST http://localhost:8080/api/events \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Test Event",
    "description": "Test Description",
    "eventDate": "2024-12-15T18:00:00",
    "location": "Test Location",
    "maxParticipants": 50,
    "registrationRequired": true
  }'
```

## Database Verification Queries

### Check User Registration
```sql
SELECT id, email, full_name, role, is_active, created_at
FROM users
ORDER BY created_at DESC;
```

### Check Events
```sql
SELECT id, title, event_date, location, max_participants, current_participants, status
FROM events
ORDER BY created_at DESC;
```

### Check Event Registrations
```sql
SELECT er.id, e.title as event_title, u.full_name as user_name, er.status, er.registered_at
FROM event_registrations er
JOIN events e ON er.event_id = e.id
JOIN users u ON er.user_id = u.id
ORDER BY er.registered_at DESC;
```

### Check Volunteer Opportunities
```sql
SELECT id, title, date_time, location, max_volunteers, current_volunteers, status
FROM volunteer_opportunities
ORDER BY created_at DESC;
```

## Common Test Scenarios

### 1. User Registration and Authentication
- Register new user
- Login with correct credentials
- Login with incorrect credentials (should fail)
- Access protected endpoint without token (should fail)
- Access protected endpoint with expired token (should fail)

### 2. Role-Based Access Control
- Try admin endpoint as regular user (should fail with 403)
- Login as admin and access admin endpoints (should succeed)
- Update user role and verify access changes

### 3. Event Management
- Create event as regular user (should fail)
- Create event as admin (should succeed)
- Register for event multiple times (should fail second time)
- Register for full event (should fail if at capacity)
- Update event as non-admin (should fail)

### 4. File Upload
- Upload file within size limit (should succeed)
- Upload file exceeding size limit (should fail)
- Upload file with unsupported format (should fail)
- Download uploaded file (should succeed)

### 5. Data Validation
- Submit incomplete registration data (should fail with validation errors)
- Submit invalid email format (should fail)
- Submit weak password (should fail if validation implemented)

## Error Testing

### Expected Error Responses

#### 400 Bad Request
```json
{
  "success": false,
  "error": "Validation failed: [field] is required",
  "timestamp": "2024-01-01T10:00:00Z"
}
```

#### 401 Unauthorized
```json
{
  "success": false,
  "error": "Access denied: Authentication required",
  "timestamp": "2024-01-01T10:00:00Z"
}
```

#### 403 Forbidden
```json
{
  "success": false,
  "error": "Access denied: Insufficient permissions",
  "timestamp": "2024-01-01T10:00:00Z"
}
```

#### 404 Not Found
```json
{
  "success": false,
  "error": "Resource not found",
  "timestamp": "2024-01-01T10:00:00Z"
}
```

## Performance Testing

### Load Testing with curl
```bash
# Simple load test script
for i in {1..100}; do
  curl -X GET http://localhost:8080/api/events \
    -H "Authorization: Bearer $TOKEN" \
    -w "Time: %{time_total}s\n" \
    -o /dev/null -s &
done
wait
```

### JMeter Test Plan
1. Create Thread Group with 50 users
2. Add HTTP Request samplers for key endpoints
3. Include authentication in HTTP Header Manager
4. Add listeners for response times and error rates
5. Run test and analyze results

## Security Testing

### JWT Token Security
- Test with modified token (should fail)
- Test with expired token (should fail)
- Test with malformed token (should fail)

### SQL Injection Testing
- Try SQL injection in search parameters
- Test with special characters in input fields

### File Upload Security
- Try uploading executable files
- Test with files containing malicious content
- Verify file type validation

## Monitoring During Testing

### Application Logs
```bash
tail -f logs/community-backend.log | grep ERROR
```

### Database Performance
```sql
SHOW PROCESSLIST;
SHOW STATUS LIKE 'Threads_connected';
```

### Redis Monitoring
```bash
redis-cli monitor
```

### System Resources
```bash
htop
iostat -x 1
```

---

This testing guide provides comprehensive coverage for manually testing all aspects of the Community Management API. Use it alongside the Postman collection for thorough API validation.