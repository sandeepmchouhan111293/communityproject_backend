# Community Management API Documentation

## Overview
This document provides comprehensive API documentation for the Community Management Backend system. All endpoints use RESTful conventions and JSON for data exchange.

## Base URL
```
http://localhost:8080/api
```

## Authentication
Most endpoints require JWT authentication. Include the token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

## Response Format
All API responses follow this standard format:

### Success Response
```json
{
  "success": true,
  "data": { /* response data */ },
  "message": "Operation completed successfully"
}
```

### Error Response
```json
{
  "success": false,
  "error": "Error message",
  "timestamp": "2024-01-15T10:30:00Z"
}
```

## Status Codes
- `200` - OK: Success
- `201` - Created: Resource created successfully
- `400` - Bad Request: Invalid request data
- `401` - Unauthorized: Authentication required
- `403` - Forbidden: Insufficient permissions
- `404` - Not Found: Resource not found
- `500` - Internal Server Error: Server error

---

# Authentication Endpoints

## Register User
**Endpoint:** `POST /api/auth/register`
**Description:** Register a new user account
**Authentication:** Not required

### Request Body
```json
{
  "fullName": "John Doe",
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```

### Response
```json
{
  "success": true,
  "message": "User registered successfully"
}
```

### Validation Rules
- `fullName`: Required, 2-100 characters
- `email`: Required, valid email format, unique
- `password`: Required, minimum 8 characters

---

## Login
**Endpoint:** `POST /api/auth/login`
**Description:** Authenticate user and receive JWT token
**Authentication:** Not required

### Request Body
```json
{
  "email": "john.doe@example.com",
  "password": "securePassword123"
}
```

### Response
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI..."
}
```

---

# User Management Endpoints

## Get Current User Profile
**Endpoint:** `GET /api/users/profile`
**Description:** Get the authenticated user's profile information
**Authentication:** Required (MEMBER, ADMIN)

### Response
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "email": "john.doe@example.com",
  "fullName": "John Doe",
  "city": "New York",
  "state": "NY",
  "district": "Manhattan",
  "communityName": "NYC Community",
  "phone": "+1234567890",
  "avatarUrl": "http://localhost:8080/api/files/avatar.jpg",
  "role": "MEMBER",
  "createdAt": "2024-01-01T10:00:00Z"
}
```

---

## Update User Profile
**Endpoint:** `PUT /api/users/profile`
**Description:** Update the authenticated user's profile
**Authentication:** Required (MEMBER, ADMIN)

### Request Body
```json
{
  "fullName": "John Smith",
  "city": "New York",
  "state": "NY",
  "district": "Manhattan",
  "communityName": "NYC Community",
  "phone": "+1234567890"
}
```

### Response
```json
{
  "id": "123e4567-e89b-12d3-a456-426614174000",
  "email": "john.doe@example.com",
  "fullName": "John Smith",
  "city": "New York",
  "state": "NY",
  "district": "Manhattan",
  "communityName": "NYC Community",
  "phone": "+1234567890",
  "avatarUrl": "http://localhost:8080/api/files/avatar.jpg",
  "role": "MEMBER",
  "createdAt": "2024-01-01T10:00:00Z"
}
```

---

## Upload Avatar
**Endpoint:** `POST /api/users/avatar`
**Description:** Upload user avatar image
**Authentication:** Required (MEMBER, ADMIN)
**Content-Type:** `multipart/form-data`

### Request Form Data
- `file`: Image file (JPG, PNG, GIF - max 5MB)

### Response
```json
{
  "success": true,
  "message": "Avatar updated successfully. You can download it from: http://localhost:8080/api/files/avatar-123.jpg"
}
```

---

# Event Management Endpoints

## Create Event
**Endpoint:** `POST /api/events`
**Description:** Create a new community event
**Authentication:** Required (ADMIN only)

### Request Body
```json
{
  "title": "Community Meetup",
  "description": "Monthly community gathering to discuss local issues",
  "eventDate": "2024-01-15T18:00:00",
  "endDate": "2024-01-15T21:00:00",
  "location": "Community Center, 123 Main St",
  "maxParticipants": 50,
  "imageUrl": "https://example.com/event-image.jpg",
  "registrationRequired": true
}
```

### Response
```json
{
  "id": "456e7890-e89b-12d3-a456-426614174000",
  "title": "Community Meetup",
  "description": "Monthly community gathering to discuss local issues",
  "eventDate": "2024-01-15T18:00:00Z",
  "endDate": "2024-01-15T21:00:00Z",
  "location": "Community Center, 123 Main St",
  "maxParticipants": 50,
  "currentParticipants": 0,
  "status": "UPCOMING",
  "createdBy": "123e4567-e89b-12d3-a456-426614174000",
  "createdByName": "John Doe",
  "imageUrl": "https://example.com/event-image.jpg",
  "registrationRequired": true,
  "createdAt": "2024-01-01T10:00:00Z",
  "updatedAt": "2024-01-01T10:00:00Z"
}
```

### Validation Rules
- `title`: Required, 1-200 characters
- `eventDate`: Required, future date
- `endDate`: Optional, must be after eventDate
- `location`: Optional, max 500 characters
- `maxParticipants`: Optional, positive integer

---

## Get All Events
**Endpoint:** `GET /api/events`
**Description:** Retrieve all events with optional filtering
**Authentication:** Required (MEMBER, ADMIN)

### Query Parameters
- `title` (optional): Filter by title containing text
- `location` (optional): Filter by location containing text
- `status` (optional): Filter by status (UPCOMING, ACTIVE, COMPLETED, CANCELLED)

### Example Request
```
GET /api/events?title=meetup&location=center&status=UPCOMING
```

### Response
```json
[
  {
    "id": "456e7890-e89b-12d3-a456-426614174000",
    "title": "Community Meetup",
    "description": "Monthly community gathering",
    "eventDate": "2024-01-15T18:00:00Z",
    "endDate": "2024-01-15T21:00:00Z",
    "location": "Community Center",
    "maxParticipants": 50,
    "currentParticipants": 15,
    "status": "UPCOMING",
    "createdBy": "123e4567-e89b-12d3-a456-426614174000",
    "createdByName": "John Doe",
    "imageUrl": "https://example.com/event-image.jpg",
    "registrationRequired": true,
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T10:00:00Z"
  }
]
```

---

## Get Event by ID
**Endpoint:** `GET /api/events/{eventId}`
**Description:** Retrieve a specific event by ID
**Authentication:** Required (MEMBER, ADMIN)

### Path Parameters
- `eventId`: UUID of the event

### Response
Same as Create Event response format.

---

## Update Event
**Endpoint:** `PUT /api/events/{eventId}`
**Description:** Update an existing event
**Authentication:** Required (ADMIN only)

### Path Parameters
- `eventId`: UUID of the event

### Request Body
```json
{
  "title": "Updated Event Title",
  "description": "Updated description",
  "status": "ACTIVE",
  "maxParticipants": 75
}
```

### Response
Updated event object (same format as Create Event response).

---

## Delete Event
**Endpoint:** `DELETE /api/events/{eventId}`
**Description:** Delete an event
**Authentication:** Required (ADMIN only)

### Path Parameters
- `eventId`: UUID of the event

### Response
```
204 No Content
```

---

## Register for Event
**Endpoint:** `POST /api/events/{eventId}/register`
**Description:** Register current user for an event
**Authentication:** Required (MEMBER, ADMIN)

### Path Parameters
- `eventId`: UUID of the event

### Response
```json
{
  "id": "789e1234-e89b-12d3-a456-426614174000",
  "eventId": "456e7890-e89b-12d3-a456-426614174000",
  "eventTitle": "Community Meetup",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "userName": "John Doe",
  "status": "REGISTERED",
  "registeredAt": "2024-01-01T15:30:00Z"
}
```

---

## Unregister from Event
**Endpoint:** `DELETE /api/events/{eventId}/register`
**Description:** Unregister current user from an event
**Authentication:** Required (MEMBER, ADMIN)

### Response
```
204 No Content
```

---

## Get Event Participants
**Endpoint:** `GET /api/events/{eventId}/participants`
**Description:** Get list of participants for an event
**Authentication:** Required (MEMBER, ADMIN)

### Response
```json
[
  {
    "id": "789e1234-e89b-12d3-a456-426614174000",
    "eventId": "456e7890-e89b-12d3-a456-426614174000",
    "eventTitle": "Community Meetup",
    "userId": "123e4567-e89b-12d3-a456-426614174000",
    "userName": "John Doe",
    "status": "REGISTERED",
    "registeredAt": "2024-01-01T15:30:00Z"
  }
]
```

---

# Volunteer Management Endpoints

## Create Volunteer Opportunity
**Endpoint:** `POST /api/volunteers/opportunities`
**Description:** Create a new volunteer opportunity
**Authentication:** Required (ADMIN only)

### Request Body
```json
{
  "title": "Beach Cleanup",
  "description": "Help clean our local beach and protect marine life",
  "requirements": "Bring gloves, water bottle, and sun protection",
  "location": "Sunset Beach, Pier 15",
  "dateTime": "2024-01-20T09:00:00",
  "durationHours": 4,
  "maxVolunteers": 20
}
```

### Response
```json
{
  "id": "abc12345-e89b-12d3-a456-426614174000",
  "title": "Beach Cleanup",
  "description": "Help clean our local beach and protect marine life",
  "requirements": "Bring gloves, water bottle, and sun protection",
  "location": "Sunset Beach, Pier 15",
  "dateTime": "2024-01-20T09:00:00Z",
  "durationHours": 4,
  "maxVolunteers": 20,
  "currentVolunteers": 0,
  "status": "ACTIVE",
  "createdBy": "123e4567-e89b-12d3-a456-426614174000",
  "createdByName": "John Doe",
  "createdAt": "2024-01-01T10:00:00Z",
  "updatedAt": "2024-01-01T10:00:00Z"
}
```

---

## Get All Volunteer Opportunities
**Endpoint:** `GET /api/volunteers/opportunities`
**Description:** Retrieve all volunteer opportunities with optional filtering
**Authentication:** Required (MEMBER, ADMIN)

### Query Parameters
- `title` (optional): Filter by title containing text
- `location` (optional): Filter by location containing text
- `status` (optional): Filter by status (ACTIVE, COMPLETED, CANCELLED)

### Response
Array of volunteer opportunity objects (same format as create response).

---

## Get Volunteer Opportunity by ID
**Endpoint:** `GET /api/volunteers/opportunities/{opportunityId}`
**Description:** Retrieve a specific volunteer opportunity
**Authentication:** Required (MEMBER, ADMIN)

### Response
Single volunteer opportunity object.

---

## Update Volunteer Opportunity
**Endpoint:** `PUT /api/volunteers/opportunities/{opportunityId}`
**Description:** Update an existing volunteer opportunity
**Authentication:** Required (ADMIN only)

### Request Body
```json
{
  "title": "Updated Beach Cleanup",
  "status": "COMPLETED",
  "maxVolunteers": 25
}
```

---

## Delete Volunteer Opportunity
**Endpoint:** `DELETE /api/volunteers/opportunities/{opportunityId}`
**Description:** Delete a volunteer opportunity
**Authentication:** Required (ADMIN only)

### Response
```
204 No Content
```

---

## Register for Volunteer Opportunity
**Endpoint:** `POST /api/volunteers/opportunities/{opportunityId}/register`
**Description:** Register current user for a volunteer opportunity
**Authentication:** Required (MEMBER, ADMIN)

### Response
```json
{
  "id": "def67890-e89b-12d3-a456-426614174000",
  "opportunityId": "abc12345-e89b-12d3-a456-426614174000",
  "opportunityTitle": "Beach Cleanup",
  "userId": "123e4567-e89b-12d3-a456-426614174000",
  "userName": "John Doe",
  "status": "PENDING",
  "notes": null,
  "registeredAt": "2024-01-01T15:30:00Z"
}
```

---

## Get My Volunteer Registrations
**Endpoint:** `GET /api/volunteers/my-registrations`
**Description:** Get current user's volunteer registrations
**Authentication:** Required (MEMBER, ADMIN)

### Response
Array of volunteer registration objects.

---

## Get Opportunity Registrations
**Endpoint:** `GET /api/volunteers/opportunities/{opportunityId}/registrations`
**Description:** Get all registrations for a specific opportunity
**Authentication:** Required (ADMIN only)

---

## Update Registration Status
**Endpoint:** `PUT /api/volunteers/registrations/{registrationId}`
**Description:** Update volunteer registration status
**Authentication:** Required (ADMIN only)

### Request Body
```json
{
  "status": "APPROVED",
  "notes": "Excellent volunteer with prior experience"
}
```

---

# Document Management Endpoints

## Upload Document
**Endpoint:** `POST /api/documents`
**Description:** Upload a new document
**Authentication:** Required (ADMIN only)
**Content-Type:** `multipart/form-data`

### Request Form Data
- `request`: JSON object with document metadata
- `file`: Document file

### Request JSON Part
```json
{
  "title": "Community Guidelines 2024",
  "description": "Official community guidelines and rules",
  "category": "POLICY",
  "accessLevel": "PUBLIC"
}
```

### Response
```json
{
  "id": "ghi78901-e89b-12d3-a456-426614174000",
  "title": "Community Guidelines 2024",
  "description": "Official community guidelines and rules",
  "category": "POLICY",
  "accessLevel": "PUBLIC",
  "fileType": "application/pdf",
  "fileSize": "2.5 MB",
  "fileUrl": "http://localhost:8080/api/files/guidelines.pdf",
  "uploadedBy": "123e4567-e89b-12d3-a456-426614174000",
  "uploadedByName": "John Doe",
  "downloadCount": 0,
  "createdAt": "2024-01-01T10:00:00Z",
  "updatedAt": "2024-01-01T10:00:00Z"
}
```

### Document Categories
- `POLICY`: Policies and guidelines
- `FORM`: Forms and applications
- `NEWSLETTER`: Community newsletters
- `MEETING_MINUTES`: Meeting minutes and records
- `RESOURCE`: General resources
- `OTHER`: Other documents

### Access Levels
- `PUBLIC`: Accessible to all users
- `MEMBER`: Accessible to logged-in members only
- `ADMIN`: Accessible to admins only

---

## Get Documents
**Endpoint:** `GET /api/documents`
**Description:** Retrieve documents based on user's access level
**Authentication:** Required (MEMBER, ADMIN)

### Query Parameters
- `title` (optional): Filter by title containing text
- `category` (optional): Filter by document category

### Response
Array of document objects (format same as upload response).

---

## Get Document by ID
**Endpoint:** `GET /api/documents/{documentId}`
**Description:** Retrieve a specific document
**Authentication:** Required (MEMBER, ADMIN)

---

## Update Document
**Endpoint:** `PUT /api/documents/{documentId}`
**Description:** Update document metadata
**Authentication:** Required (ADMIN only)

### Request Body
```json
{
  "title": "Updated Community Guidelines 2024",
  "description": "Updated community guidelines",
  "accessLevel": "MEMBER"
}
```

---

## Delete Document
**Endpoint:** `DELETE /api/documents/{documentId}`
**Description:** Delete a document
**Authentication:** Required (ADMIN only)

---

## Get Document Categories
**Endpoint:** `GET /api/documents/categories`
**Description:** Get list of available document categories
**Authentication:** Required (MEMBER, ADMIN)

### Response
```json
["POLICY", "FORM", "NEWSLETTER", "MEETING_MINUTES", "RESOURCE", "OTHER"]
```

---

# Admin Endpoints

## Get All Users
**Endpoint:** `GET /api/admin/users`
**Description:** Get list of all users in the system
**Authentication:** Required (ADMIN only)

### Response
```json
[
  {
    "id": "123e4567-e89b-12d3-a456-426614174000",
    "email": "john.doe@example.com",
    "fullName": "John Doe",
    "role": "MEMBER",
    "city": "New York",
    "state": "NY",
    "district": "Manhattan",
    "communityName": "NYC Community",
    "phone": "+1234567890",
    "avatarUrl": "http://localhost:8080/api/files/avatar.jpg",
    "isActive": true,
    "lastLogin": "2024-01-01T15:30:00Z",
    "createdAt": "2024-01-01T10:00:00Z",
    "updatedAt": "2024-01-01T15:30:00Z"
  }
]
```

---

## Get User by ID
**Endpoint:** `GET /api/admin/users/{userId}`
**Description:** Get specific user details
**Authentication:** Required (ADMIN only)

---

## Update User Role
**Endpoint:** `PUT /api/admin/users/{userId}/role`
**Description:** Update a user's role
**Authentication:** Required (ADMIN only)

### Request Body
```json
{
  "role": "ADMIN"
}
```

### Available Roles
- `MEMBER`: Regular community member
- `ADMIN`: Administrator with full access

---

## Delete User
**Endpoint:** `DELETE /api/admin/users/{userId}`
**Description:** Delete a user account
**Authentication:** Required (ADMIN only)

---

## Get Audit Logs
**Endpoint:** `GET /api/admin/audit-logs`
**Description:** Get system audit logs
**Authentication:** Required (ADMIN only)

### Response
```json
[
  {
    "id": "audit123-e89b-12d3-a456-426614174000",
    "userId": "123e4567-e89b-12d3-a456-426614174000",
    "userName": "John Doe",
    "action": "UPDATE_USER_ROLE",
    "entityType": "User",
    "entityId": "456e7890-e89b-12d3-a456-426614174000",
    "oldValue": "MEMBER",
    "newValue": "ADMIN",
    "timestamp": "2024-01-01T15:30:00Z"
  }
]
```

---

## Get Dashboard Statistics
**Endpoint:** `GET /api/admin/dashboard`
**Description:** Get system statistics for admin dashboard
**Authentication:** Required (ADMIN only)

### Response
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

---

## System Health Check
**Endpoint:** `GET /api/admin/health`
**Description:** Check system health status
**Authentication:** Required (ADMIN only)

### Response
```json
{
  "status": "Healthy",
  "timestamp": "2024-01-01T15:30:00Z"
}
```

---

# File Management Endpoints

## Download File
**Endpoint:** `GET /api/files/{fileName}`
**Description:** Download uploaded files
**Authentication:** Not required for public files, may require authentication for protected files

### Response
Binary file content with appropriate Content-Type header.

---

# Error Codes and Messages

## Common Error Responses

### 400 Bad Request
```json
{
  "success": false,
  "error": "Validation failed: Email is required",
  "timestamp": "2024-01-01T15:30:00Z"
}
```

### 401 Unauthorized
```json
{
  "success": false,
  "error": "Invalid or expired token",
  "timestamp": "2024-01-01T15:30:00Z"
}
```

### 403 Forbidden
```json
{
  "success": false,
  "error": "You do not have permission to access this resource",
  "timestamp": "2024-01-01T15:30:00Z"
}
```

### 404 Not Found
```json
{
  "success": false,
  "error": "Resource not found",
  "timestamp": "2024-01-01T15:30:00Z"
}
```

### 409 Conflict
```json
{
  "success": false,
  "error": "Email address already in use",
  "timestamp": "2024-01-01T15:30:00Z"
}
```

### 422 Validation Error
```json
{
  "success": false,
  "error": "User already registered for this event",
  "timestamp": "2024-01-01T15:30:00Z"
}
```

---

# Rate Limiting

Currently, no rate limiting is implemented. Consider implementing rate limiting for production use.

# CORS Configuration

CORS is currently disabled. Configure appropriately for production deployment.

# File Upload Limits

- Maximum file size: 10MB (configurable)
- Allowed file types: PDF, DOC, DOCX, JPG, PNG, GIF
- Files are stored locally by default (configurable for AWS S3)

---

*This documentation covers all currently implemented endpoints. For additional endpoints or features, please refer to the source code or contact the development team.*