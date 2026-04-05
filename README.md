# Financial Data Processing Backend

A fully functional Spring Boot backend for financial data processing with role-based access control (RBAC).

## Features

Based on the feature mapping requirements:

| Feature | Viewer | Analyst | Admin |
|---------|--------|---------|-------|
| View Users | ❌ | ❌ | ✅ |
| Create User | ❌ | ❌ | ✅ |
| View Records | ✅ | ✅ | ✅ |
| Create Record | ❌ | ❌ | ✅ |
| Update Record | ❌ | ❌ | ✅ |
| Delete Record | ❌ | ❌ | ✅ |
| Dashboard Summary | ✅ | ✅ | ✅ |

## Technology Stack

- **Java 21**
- **Spring Boot 3.2.0**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA**
- **MySQL 8**
- **Lombok** (for boilerplate reduction)
- **Maven**

## Prerequisites

1. Java 21 or higher
2. MySQL Server running locally
3. Maven

## Database Setup

The database `finance_dbase` will be automatically created on application startup. Update credentials in `application.properties` if needed:

```properties
spring.datasource.username=root
spring.datasource.password=your_password
```

## Running the Application

1. Clone the repository
2. Navigate to project directory
3. Run: `mvn spring-boot:run`

Or build and run:
```bash
mvn clean package
java -jar target/financial-data-processing-1.0.0.jar
```

## Default Users

| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | ADMIN |
| viewer | viewer123 | VIEWER |
| analyst | analyst123 | ANALYST |

## API Endpoints

### Authentication
- `POST /api/auth/login` - Login and get JWT token

### Users (Admin Only)
- `POST /api/users` - Create user
- `GET /api/users` - List all users
- `GET /api/users/{id}` - Get user by ID
- `PUT /api/users/{id}` - Update user
- `DELETE /api/users/{id}` - Delete user
- `GET /api/users/{id}/records` - Get user's records

### Financial Records
- `GET /api/records` - List all records (Viewer, Analyst, Admin)
- `GET /api/records/{id}` - Get record by ID (Viewer, Analyst, Admin)
- `GET /api/records/type/{type}` - Filter by type (Viewer, Analyst, Admin)
- `GET /api/records/category/{category}` - Filter by category (Viewer, Analyst, Admin)
- `POST /api/records` - Create record (Admin only)
- `PUT /api/records/{id}` - Update record (Admin only)
- `DELETE /api/records/{id}` - Delete record (Admin only)

### Dashboard
- `GET /api/dashboard/summary` - Get dashboard summary (Viewer, Analyst, Admin)

## Request/Response Examples

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

Response:
```json
{
  "token": "eyJhbGc...",
  "type": "Bearer",
  "username": "admin",
  "roles": ["ADMIN"]
}
```

### Create User (Admin only)
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "username": "newuser",
    "email": "new@finance.com",
    "password": "password123",
    "firstName": "New",
    "lastName": "User",
    "roles": ["VIEWER"]
  }'
```

### Create Financial Record (Admin only)
```bash
curl -X POST http://localhost:8080/api/records \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <token>" \
  -d '{
    "title": "New Income",
    "description": "Monthly revenue",
    "type": "INCOME",
    "amount": 10000.00,
    "transactionDate": "2024-01-15",
    "category": "Sales",
    "reference": "REF-006"
  }'
```

## Security Configuration

- JWT-based authentication
- Role-based access control using Spring Security annotations
- BCrypt password encoding
- Stateless session management

## Project Structure

```
com.finance.demo/
├── config/          # Configuration classes (DataInitializer)
├── controller/      # REST controllers
├── dto/            # Data Transfer Objects
├── entity/         # JPA entities (User, Role, FinancialRecord)
├── exception/      # Custom exceptions and handlers
├── repository/     # Spring Data JPA repositories
├── security/       # Security config, JWT, UserDetails
└── service/        # Business logic
```

## Testing

Run tests with:
```bash
mvn test
```

PostMan Testing Outputs Link - https://docs.google.com/document/d/19_PzLPHDyQxay9c3EAeTkr8ZSX0wng-xi7nh1zGZjZs/edit?usp=sharing
