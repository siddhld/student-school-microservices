# Student-School Microservices Project

## Overview

This project is a microservices-based architecture built with Spring Boot. It consists of various services including an API Gateway, Config Server, Discovery Server, Student Microservice, School Microservice, and other supporting services. The application demonstrates modern microservices patterns such as service discovery, centralized configuration, inter-service communication using OpenFeign, and distributed tracing with Zipkin.

![architecture](https://imgs.search.brave.com/AZkKYOifhfns8OmMQcTUfP9YTKWXy4N2vwDbbzYEV9U/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly91cGxv/YWQud2lraW1lZGlh/Lm9yZy93aWtpcGVk/aWEvY29tbW9ucy9i/L2I2L1BlbmNpbF9k/cmF3aW5nX29mX2Ff/Z2lybF9pbl9lY3N0/YXN5LmpwZw)

## Project Components

### 1. API Gateway
- **Port**: 8222
- **Description**: The API Gateway serves as the single entry point for all client requests, managing and routing them to the appropriate microservices.

### 2. Config Server
- **Port**: 8888
- **Description**: The Config Server centralizes configuration management for all microservices, simplifying application maintenance and ensuring consistency across environments.

### 3. Discovery Server
- **Port**: 8761
- **Description**: The Discovery Server provides service registration and discovery, enabling seamless service-to-service communication within the microservices ecosystem.

### 4. Student Microservice
- **Port**: 8090
- **Description**: The Student Microservice is responsible for managing student-related data and operations, such as adding, updating, and retrieving student records.

### 5. School Microservice
- **Port**: 8091
- **Description**: The School Microservice manages school-related data and operations, including adding, updating, and retrieving school records.

### 6. PostgreSQL Database
- **Port**: 5433 (Host) -> 5432 (Container)
- **Description**: PostgreSQL serves as the database for the microservices, providing persistent storage for student and school data.

### 7. pgAdmin
- **Port**: 5050
- **Description**: pgAdmin is a web-based database management tool for PostgreSQL, allowing for easy administration and monitoring of the database.

### 8. Zipkin
- **Port**: 9411
- **Description**: Zipkin is used for distributed tracing within the microservices, enabling better visibility and troubleshooting of latency issues across services.

## Features

### Inter-Service Communication using OpenFeign
- The project demonstrates inter-service communication using OpenFeign, a declarative REST client that simplifies service-to-service communication within the microservices ecosystem.

### Distributed Tracing with Zipkin
- The project integrates Zipkin for distributed tracing, enhancing application observability and enabling the visualization and troubleshooting of latency issues.

## Getting Started

### Prerequisites
- Docker
- Docker Compose

### Running the Application

1. **Clone the Repository:**
   ```bash
   git clone https://github.com/siddhld/student-school-microservices.git
   cd student-school-microservices
   ```

2. **Start the Services:**
   ```bash
   docker-compose up -d

   start - Config Server
   start - Discovery Server
   start - API Gateway Server
   start - School Server
   start - Student Server
   ```

3. **Access the Services:**
   - API Gateway: `http://localhost:8222`
   - Config Server: `http://localhost:8888`
   - Discovery Server: `http://localhost:8761`
   - Student Service: `http://localhost:8090`
   - School Service: `http://localhost:8091`
   - pgAdmin: `http://localhost:5050`
   - Zipkin: `http://localhost:9411`
