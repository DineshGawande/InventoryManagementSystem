# Inventory Management System API

A RESTful API for managing product inventory with stock tracking, low-stock alerts, and comprehensive CRUD operations.

## 📋 Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation & Setup](#installation--setup)
- [Running the Application](#running-the-application)
- [API Documentation](#api-documentation)
- [API Endpoints](#api-endpoints)
- [Request & Response Examples](#request--response-examples)
- [Testing](#testing)
- [Configuration](#configuration)
- [Project Structure](#project-structure)
- [Troubleshooting](#troubleshooting)

## ✨ Features

- **Product Management**: Create, read, update, and delete products
- **Stock Management**: Add and remove stock with validation
- **Low Stock Alerts**: Automatic tracking of products below threshold
- **Data Validation**: Input validation and business rule enforcement
- **Error Handling**: Comprehensive error responses with meaningful messages
- **API Documentation**: Interactive Swagger UI for testing
- **Database Support**: H2 (development) and MySQL (production)

## 🛠 Technology Stack

- **Java 17** - Latest LTS version
- **Spring Boot 3.2.1** - Application framework
- **Spring Data JPA** - Database operations
- **Hibernate** - ORM framework
- **H2 Database** - In-memory database for development
- **MySQL** - Production database (optional)
- **Lombok** - Reduces boilerplate code
- **MapStruct** - DTO mapping
- **Swagger/OpenAPI** - API documentation
- **JUnit 5 & Mockito** - Testing frameworks
- **Maven** - Build and dependency management

## 📦 Prerequisites

Before running this application, ensure you have:

- **Java 17** or higher installed
- **Maven 3.8+** installed
- **Git** (to clone the repository)
- **MySQL 8.0+** (optional, for production)
- **Postman** or **cURL** (for testing APIs)

### Verify Installation

```bash
java -version    # Should show Java 17 or higher
mvn -version     # Should show Maven 3.8 or higher
```

## 🚀 Installation & Setup

### Step 1: Clone the Repository

```bash
git clone https://github.com/yourusername/inventory-management-system.git
cd inventory-management-system
```

### Step 2: Build the Project

```bash
mvn clean install
```

This will:
- Download all dependencies
- Compile the source code
- Run tests
- Create executable JAR file

## 🏃 Running the Application

### Method 1: Using Maven

```bash
mvn spring-boot:run
```

### Method 2: Using JAR File

```bash
# First build the JAR
mvn clean package -DskipTests

# Then run it
java -jar target/inventory-management-system-1.0.0.jar
```

### Method 3: Using IDE

1. Import project as Maven project
2. Run `InventoryManagementApplication.java` as Java Application

### Verify Application is Running

Once started, you should see:
```
Started InventoryManagementApplication in X.XXX seconds
```

The application will be available at:
- **API Base URL**: `http://localhost:8080/api`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **H2 Console**: `http://localhost:8080/h2-console` (if using H2)

## 📚 API Documentation

### Interactive Documentation

Access Swagger UI for interactive API testing:
```
http://localhost:8080/swagger-ui.html
```

### H2 Database Console

Access H2 console to view database:
```
URL: http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:inventorydb
Username: sa
Password: (leave blank)
```

## 🔌 API Endpoints

### Product Management

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| GET | `/api/products` | Get all products | No |
| GET | `/api/products/{id}` | Get product by ID | No |
| POST | `/api/products` | Create new product | No |
| PUT | `/api/products/{id}` | Update product | No |
| DELETE | `/api/products/{id}` | Delete product | No |

### Stock Management

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| PATCH | `/api/products/{id}/stock/add` | Add stock | No |
| PATCH | `/api/products/{id}/stock/remove` | Remove stock | No |
| GET | `/api/products/low-stock` | Get low stock products | No |

## 📝 Request & Response Examples

### 1. Create a Product

**Request:**
```bash
POST http://localhost:8080/api/products
Content-Type: application/json

{
  "name": "Laptop",
  "description": "Dell Inspiron 15 3000 Series",
  "stockQuantity": 50,
  "lowStockThreshold": 10
}
```

**cURL:**
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop",
    "description": "Dell Inspiron 15 3000 Series",
    "stockQuantity": 50,
    "lowStockThreshold": 10
  }'
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "Laptop",
  "description": "Dell Inspiron 15 3000 Series",
  "stockQuantity": 50,
  "lowStockThreshold": 10,
  "createdAt": "2025-10-06T10:30:00",
  "updatedAt": "2025-10-06T10:30:00"
}
```

### 2. Get All Products

**Request:**
```bash
GET http://localhost:8080/api/products
```

**cURL:**
```bash
curl -X GET http://localhost:8080/api/products
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Laptop",
    "description": "Dell Inspiron 15 3000 Series",
    "stockQuantity": 50,
    "lowStockThreshold": 10,
    "createdAt": "2025-10-06T10:30:00",
    "updatedAt": "2025-10-06T10:30:00"
  },
  {
    "id": 2,
    "name": "Mouse",
    "description": "Wireless Optical Mouse",
    "stockQuantity": 5,
    "lowStockThreshold": 10,
    "createdAt": "2025-10-06T11:00:00",
    "updatedAt": "2025-10-06T11:00:00"
  }
]
```

### 3. Get Product by ID

**Request:**
```bash
GET http://localhost:8080/api/products/1
```

**cURL:**
```bash
curl -X GET http://localhost:8080/api/products/1
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Laptop",
  "description": "Dell Inspiron 15 3000 Series",
  "stockQuantity": 50,
  "lowStockThreshold": 10,
  "createdAt": "2025-10-06T10:30:00",
  "updatedAt": "2025-10-06T10:30:00"
}
```

### 4. Update Product

**Request:**
```bash
PUT http://localhost:8080/api/products/1
Content-Type: application/json

{
  "name": "Laptop - Updated",
  "description": "Dell Inspiron 15 3000 Series - New Model",
  "lowStockThreshold": 15
}
```

**cURL:**
```bash
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Laptop - Updated",
    "description": "Dell Inspiron 15 3000 Series - New Model",
    "lowStockThreshold": 15
  }'
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Laptop - Updated",
  "description": "Dell Inspiron 15 3000 Series - New Model",
  "stockQuantity": 50,
  "lowStockThreshold": 15,
  "createdAt": "2025-10-06T10:30:00",
  "updatedAt": "2025-10-06T12:00:00"
}
```

### 5. Add Stock

**Request:**
```bash
PATCH http://localhost:8080/api/products/1/stock/add
Content-Type: application/json

{
  "quantity": 25
}
```

**cURL:**
```bash
curl -X PATCH http://localhost:8080/api/products/1/stock/add \
  -H "Content-Type: application/json" \
  -d '{"quantity": 25}'
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Laptop",
  "description": "Dell Inspiron 15 3000 Series",
  "stockQuantity": 75,
  "lowStockThreshold": 10,
  "createdAt": "2025-10-06T10:30:00",
  "updatedAt": "2025-10-06T13:00:00"
}
```

### 6. Remove Stock

**Request:**
```bash
PATCH http://localhost:8080/api/products/1/stock/remove
Content-Type: application/json

{
  "quantity": 10
}
```

**cURL:**
```bash
curl -X PATCH http://localhost:8080/api/products/1/stock/remove \
  -H "Content-Type: application/json" \
  -d '{"quantity": 10}'
```

**Response (200 OK):**
```json
{
  "id": 1,
  "name": "Laptop",
  "description": "Dell Inspiron 15 3000 Series",
  "stockQuantity": 65,
  "lowStockThreshold": 10,
  "createdAt": "2025-10-06T10:30:00",
  "updatedAt": "2025-10-06T14:00:00"
}
```

### 7. Get Low Stock Products

**Request:**
```bash
GET http://localhost:8080/api/products/low-stock
```

**cURL:**
```bash
curl -X GET http://localhost:8080/api/products/low-stock
```

**Response (200 OK):**
```json
[
  {
    "id": 2,
    "name": "Mouse",
    "description": "Wireless Optical Mouse",
    "stockQuantity": 5,
    "lowStockThreshold": 10,
    "createdAt": "2025-10-06T11:00:00",
    "updatedAt": "2025-10-06T11:00:00"
  },
  {
    "id": 3,
    "name": "Keyboard",
    "description": "Mechanical Keyboard",
    "stockQuantity": 8,
    "lowStockThreshold": 10,
    "createdAt": "2025-10-06T11:30:00",
    "updatedAt": "2025-10-06T11:30:00"
  }
]
```

### 8. Delete Product

**Request:**
```bash
DELETE http://localhost:8080/api/products/1
```

**cURL:**
```bash
curl -X DELETE http://localhost:8080/api/products/1
```

**Response (204 No Content)**

## ❌ Error Responses

### Product Not Found (404)

```json
{
  "timestamp": "2025-10-06T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: 999",
  "path": "/api/products/999"
}
```

### Validation Error (400)

```json
{
  "timestamp": "2025-10-06T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "errors": {
    "name": "Name is required",
    "stockQuantity": "Stock quantity must be greater than or equal to 0"
  },
  "path": "/api/products"
}
```

### Insufficient Stock (400)

```json
{
  "timestamp": "2025-10-06T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Insufficient stock. Available: 5, Requested: 10",
  "path": "/api/products/1/stock/remove"
}
```

## 🧪 Testing

### Run All Tests

```bash
mvn test
```

### Run Specific Test Class

```bash
mvn test -Dtest=ProductServiceTest
```

### Run Tests with Coverage

```bash
mvn clean test jacoco:report
```

Coverage report will be available at: `target/site/jacoco/index.html`

### Test Categories

1. **Unit Tests**: Service layer and business logic
2. **Integration Tests**: REST controllers and database operations
3. **Repository Tests**: JPA queries and database interactions

## ⚙️ Configuration

### Application Properties (H2 Database)

```properties
# Application Name
spring.application.name=Inventory Management System

# Server Configuration
server.port=8080

# H2 Database
spring.datasource.url=jdbc:h2:mem:inventorydb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# H2 Console
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# Logging
logging.level.com.inventory=DEBUG
logging.level.org.springframework.web=INFO
logging.level.org.hibernate.SQL=DEBUG

# Swagger/OpenAPI
springdoc.api-docs.path=/api-docs
springdoc.swagger-ui.path=/swagger-ui.html
```

### MySQL Configuration

Create `application-mysql.properties`:

```properties
# MySQL Database
spring.datasource.url=jdbc:mysql://localhost:3306/inventory_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=inventory_user
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA/Hibernate
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false
```

Activate MySQL profile:
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=mysql
```

## 📁 Project Structure

```
inventory-management-system/
├── src/
│   ├── main/
│   │   ├── java/com/inventory/
│   │   │   ├── InventoryManagementApplication.java
│   │   │   ├── controller/
│   │   │   │   └── ProductController.java
│   │   │   ├── service/
│   │   │   │   ├── ProductService.java
│   │   │   │   └── impl/
│   │   │   │       └── ProductServiceImpl.java
│   │   │   ├── repository/
│   │   │   │   └── ProductRepository.java
│   │   │   ├── entity/
│   │   │   │   └── Product.java
│   │   │   ├── dto/
│   │   │   │   ├── ProductDTO.java
│   │   │   │   ├── ProductCreateDTO.java
│   │   │   │   ├── ProductUpdateDTO.java
│   │   │   │   └── StockUpdateDTO.java
│   │   │   ├── mapper/
│   │   │   │   └── ProductMapper.java
│   │   │   ├── exception/
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ProductNotFoundException.java
│   │   │   │   └── InsufficientStockException.java
│   │   │   └── config/
│   │   │       └── OpenApiConfig.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── application-mysql.properties
│   │       └── data.sql (optional)
│   └── test/
│       └── java/com/inventory/
│           ├── service/
│           │   └── ProductServiceTest.java
│           ├── controller/
│           │   └── ProductControllerTest.java
│           └── repository/
│               └── ProductRepositoryTest.java
├── pom.xml
├── README.md
└── .gitignore
```

## 🔧 Troubleshooting

### Common Issues

**Issue 1: Port 8080 already in use**

Solution: Change port in `application.properties`:
```properties
server.port=8081
```

**Issue 2: H2 Console not accessible**

Solution: Verify H2 console is enabled:
```properties
spring.h2.console.enabled=true
```

**Issue 3: Database connection error (MySQL)**

Solution: Check MySQL is running and credentials are correct:
```bash
mysql -u inventory_user -p
```

**Issue 4: Build fails with compilation errors**

Solution: Ensure Java 17 is installed and JAVA_HOME is set:
```bash
export JAVA_HOME=/path/to/java17
mvn clean install -U
```

**Issue 5: Tests failing**

Solution: Run with specific profile:
```bash
mvn test -Dspring.profiles.active=test
```

## 📊 API Testing Collection

### Postman Collection

Import this JSON into Postman for quick testing:

```json
{
  "info": {
    "name": "Inventory Management API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Get All Products",
      "request": {
        "method": "GET",
        "url": "http://localhost:8080/api/products"
      }
    },
    {
      "name": "Create Product",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\"name\":\"Laptop\",\"description\":\"Dell Inspiron\",\"stockQuantity\":50,\"lowStockThreshold\":10}"
        },
        "url": "http://localhost:8080/api/products"
      }
    }
  ]
}
```

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👥 Contact

- **Author**: Your Name
- **Email**: your.email@example.com
- **GitHub**: [@yourusername](https://github.com/yourusername)

## 🙏 Acknowledgments

- Spring Boot Team for excellent framework
- Open source community
- All contributors

---

**Happy Coding! 🚀**

