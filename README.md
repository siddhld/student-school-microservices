# Student-School Microservices Project

[![Project-Architecture.png](https://i.postimg.cc/GtPT0sT5/Project-Architecture.png)](https://postimg.cc/hQjtTvrL)

## Overview
The Student-School Microservices project is an example of microservices architecture using Spring Boot and Spring Cloud. This application demonstrates how different services such as Auth Service, School Service, and Student Service interact through an API Gateway, using service discovery and centralized configuration management.

The system is designed for managing students and schools with services that are independent yet communicate with each other via REST APIs. Authentication and authorization are handled using JSON Web Tokens (JWT), ensuring that only authenticated users can access certain features.

## Project Structure
This project includes the following microservices:
- **API Gateway**: Central point to manage requests and route them to appropriate services.
- **Auth Service**: Handles user authentication and JWT token generation.
- **Student Service**: Manages student data.
- **School Service**: Manages school data.
- **Config Server**: Centralized configuration management for all microservices.
- **Discovery Server (Eureka)**: Service registry for locating instances of microservices.
  
### Service URLs:
- **API Gateway**: http://localhost:8222
- **Config Server**: http://localhost:8888
- **Discovery Server**: http://localhost:8761
- **Auth Service**: http://localhost:8099
- **Student Service**: http://localhost:8090
- **School Service**: http://localhost:8091
- **pgAdmin**: http://localhost:5050
- **Zipkin**: http://localhost:9411

## Technologies Used
- **Java 17**
- **Spring Boot**
- **Spring Cloud** (Config Server, Eureka, Gateway)
- **Spring Security** for authentication
- **JWT (JSON Web Token)** for stateless security
- **JPA/Hibernate** for ORM
- **MySQL and PostgreSQL** for databases
- **Feign Client** for inter-service communication
- **Docker** for containerization
- **Zipkin** for distributed tracing
- **pgAdmin** for PostgreSQL management

## Microservice Breakdown

### 1. **API Gateway**
- **Role**: Serves as the entry point for all requests in the system. It routes incoming requests to the appropriate services, manages authentication, and applies any security filters.
- **Flow**: 
  - Users authenticate via the Auth Service through the API Gateway.
  - Once authenticated, a JWT token is issued.
  - The API Gateway verifies this JWT token with each request and routes it to the correct service (Student or School).
- **Technologies**: Spring Cloud Gateway, Spring Security, Feign Client.

### 2. **Auth Service**
- **Role**: This service is responsible for handling user authentication and generating JWT tokens. When a user logs in, they receive a token which they use for subsequent requests.
- **Flow**:
  - Users send login requests with credentials.
  - The Auth Service validates the credentials, and if successful, generates a JWT token.
  - This token is sent back to the client to be included in the headers of future requests.
- **Security**: The JWT token contains encoded user information and is signed using a secret key to prevent tampering.

### 3. **Student Service**
- **Role**: Manages student-related data such as creating, updating, retrieving, or deleting student records.
- **Flow**:
  - Requests to access student data go through the API Gateway.
  - The API Gateway verifies the JWT token before forwarding the request to the Student Service.
  - The Student Service interacts with the database (MySQL or PostgreSQL) to perform CRUD operations.
- **Technologies**: Spring Boot, JPA, Feign Client for communication.

### 4. **School Service**
- **Role**: Manages school-related data such as adding, updating, or retrieving school information.
- **Flow**:
  - Requests are routed through the API Gateway, which verifies the JWT token before forwarding the request to the School Service.
  - The School Service handles school data interactions and communicates with the Student Service when necessary.
- **Technologies**: Spring Boot, JPA, Feign Client.

### 5. **Config Server**
- **Role**: Provides centralized configuration for all microservices. It fetches configuration from a Git repository or local file and distributes it to each service.
- **Flow**:
  - On service startup, each microservice contacts the Config Server to retrieve its configuration.
  - This allows for dynamic configuration management across services without needing to redeploy them.

### 6. **Discovery Server (Eureka)**
- **Role**: Eureka is used for service registration and discovery. Each service registers itself with Eureka so that other services can discover and communicate with it.
- **Flow**:
  - When a service starts, it registers itself with the Discovery Server (Eureka).
  - Other services query the Discovery Server to find out where instances of services like Student Service or School Service are running.

## Microservices Flow

1. **User Authentication**:
   - A user sends a login request to the **API Gateway**, which forwards it to the **Auth Service**.
   - The Auth Service verifies the credentials, generates a JWT token, and sends it back to the client.
   
2. **Accessing Student or School Data**:
   - For any subsequent requests to access student or school data, the user includes the JWT token in the headers.
   - The **API Gateway** intercepts the request, validates the JWT token, and forwards the request to the appropriate service (Student or School).
   - The **Student Service** or **School Service** interacts with the database to perform the required operation and sends the response back through the API Gateway to the client.

## Security Implementation

### API Gateway
- The **API Gateway** uses **Spring Security** to secure all incoming requests.
- It intercepts each request and checks for the presence of a valid JWT token.
- The token is validated by the API Gateway before forwarding the request to the respective service.
- If the token is missing or invalid, the request is rejected.

### Auth Service
- **Spring Security** is used in the **Auth Service** to handle login requests and issue JWT tokens.
- Upon successful login, a JWT token is generated, which contains encoded user information (e.g., user ID, roles).
- The token is signed using a secret key to ensure its integrity.
- The token is stateless, meaning that no session is stored on the server; all required user information is included in the token itself.
  
### JWT Authentication Flow:
1. **Login Request**: The client sends a login request with user credentials.
2. **JWT Generation**: If the credentials are valid, the Auth Service generates a JWT token and sends it to the client.
3. **Token Validation**: For every subsequent request, the client sends the token in the headers. The API Gateway verifies the token’s authenticity and forwards the request if valid.
4. **Protected Routes**: Only authenticated users can access certain routes such as fetching student or school data.

### Key Benefits of JWT in Security:
- **Stateless Authentication**: Since JWT tokens are stateless, the server doesn't need to store session information, reducing overhead.
- **Scalability**: JWT-based systems are more scalable as token verification is independent of the server.
- **Security**: JWT tokens are signed, making them secure against tampering. The signature ensures that the data in the token hasn't been altered.

## Setup and Running the Project

### Prerequisites:
- Docker installed
- Java 17
- Maven


 **Start the Services:**
   ```bash
   docker-compose up -d

   start - Config Server
   start - Discovery Server
   start - API Gateway Server
   start - Auth-Service
   start - School Server
   start - Student Server
   ```
