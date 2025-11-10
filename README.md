# TheDetailingMafia Backend - Microservices Architecture

## 🚗 Project Overview
TheDetailingMafia is a comprehensive car wash service platform built using microservices architecture. The system provides on-demand car washing services with AI-powered features, payment processing, and real-time notifications.

## 🏗️ Architecture Overview
The backend consists of 11 microservices following Domain-Driven Design (DDD) principles with database-per-service architecture:

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   API Gateway   │────│ Discovery Service│────│   User Service  │
│     (8080)      │    │     (8761)      │    │   (8081)        │
│                 │    │                 │    │   MySQL DB      │
└─────────────────┘    └─────────────────┘    └─────────────────┘
         │                                              │
         ├──────────────────────────────────────────────┼─────────────┐
         │                                              │             │
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐    │
│ Booking Service │    │   Car Service   │    │ Payment Service │    │
│     (8084)      │    │     (8083)      │    │     (8086)      │    │
│   MySQL DB      │    │   MySQL DB      │    │   MySQL DB      │    │
└─────────────────┘    └─────────────────┘    └─────────────────┘    │
         │                       │                       │             │
         └───────────────────────┼───────────────────────┼─────────────┤
                                 │                       │             │
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐    │
│ Washer Service  │    │Notification Svc │    │   AI Service    │    │
│     (8085)      │    │     (8087)      │    │   (8088) + Bot  │    │
│   MySQL DB      │    │                 │    │ PostgreSQL+Vec  │    │
└─────────────────┘    └─────────────────┘    └─────────────────┘    │
         │                       │                       │             │
         └───────────────────────┼───────────────────────┼─────────────┤
                                 │                       │             │
                      ┌─────────────────┐    ┌─────────────────┐    │
                      │ Review Service  │    │   RabbitMQ      │    │
                      │     (8092)      │    │   Message Bus   │    │
                      │   MongoDB       │    │                 │    │
                      └─────────────────┘    └─────────────────┘    │
                                 │                       │             │
                      ┌─────────────────┐    ┌─────────────────┐    │
                      │  Media Service  │    │   ELK Stack     │    │
                      │     (8089)      │    │ (Elasticsearch │    │
                      │   MongoDB       │    │ Logstash,Kibana)│    │
                      └─────────────────┘    └─────────────────┘    │
                                 │                       │             │
                                 └───────────────────────┴─────────────┘
```

## 🛠️ Technology Stack

### Core Framework
- **Spring Boot**: 3.4.4 / 3.2.4
- **Spring Cloud**: 2024.0.1 / 2023.0.5
- **Java**: 17
- **Maven**: Build tool and dependency management

### Microservices Components
- **Spring Cloud Gateway**: API Gateway and routing
- **Netflix Eureka**: Service discovery and registration
- **OpenFeign**: Declarative REST client for inter-service communication
- **Resilience4j**: Circuit breaker pattern implementation

### Security & Authentication
- **Spring Security**: Authentication and authorization
- **JWT (JSON Web Tokens)**: Stateless authentication
  - jjwt-api: 0.12.6 / 0.11.5
- **OAuth2**: Resource server configuration

### Database & Persistence
- **MySQL**: Individual databases per service (User, Booking, Car, Payment, Washer)
- **PostgreSQL**: Vector database for AI service with PGVector
- **MongoDB**: Document database for Review service
- **Spring Data JPA**: Data access layer for relational databases
- **Spring Data MongoDB**: Data access for document database
- **HikariCP**: Connection pooling for optimized performance
- **Database Per Service**: True microservices data isolation

### Message Queue & Communication
- **RabbitMQ**: Asynchronous messaging between services
- **Spring AMQP**: RabbitMQ integration

### AI & Machine Learning
- **Spring AI**: 1.0.1
- **Google Vertex AI**: Gemini model integration
- **Google Cloud AI Platform**: 3.30.0
- **PGVector**: Vector store for RAG (Retrieval-Augmented Generation)
- **Apache PDFBox**: PDF document processing
- **FAISS**: Vector similarity search

### Payment Processing
- **Stripe**: Payment gateway integration
  - stripe-java: 24.0.0

### Email & Notifications
- **Spring Mail**: Email service integration
- **Thymeleaf**: Email template engine with custom layouts
- **SMTP**: Gmail integration for notifications
- **RabbitMQ Listeners**: @RabbitListener for event-driven notifications
- **Template Management**: Multiple HTML email templates for different events

### Documentation & API
- **SpringDoc OpenAPI**: 2.7.0
- **Swagger UI**: API documentation and testing

### Monitoring & Observability
- **Spring Boot Actuator**: Health checks and metrics
- **Logback**: Logging framework with custom configurations

### Testing
- **JUnit 5**: Unit testing framework
- **Mockito**: 5.14.2 - Mocking framework
- **Spring Boot Test**: Integration testing
- **Spring Security Test**: Security testing

### Development Tools
- **Lombok**: Boilerplate code reduction
- **Jakarta Validation**: Bean validation
- **Hibernate Validator**: 8.0.1.Final

### Cross-Cutting Concerns
- **Aspect-Oriented Programming (AOP)**: Method execution logging and performance monitoring
- **Custom Logback Configuration**: File and console logging with custom patterns
- **Global Exception Handling**: Centralized error management across all services
- **Method-Level Security**: @PreAuthorize annotations for fine-grained access control
- **Event-Driven Architecture**: RabbitMQ-based asynchronous communication
- **Circuit Breaker Pattern**: Resilience4j for fault tolerance
- **Scheduled Tasks**: Spring @Scheduled for automated processes

### Advanced Features
- **Reactive Programming**: WebFlux in API Gateway
- **Vector Search**: PGVector for AI-powered similarity search
- **Template Engine**: Thymeleaf for dynamic email templates
- **Payment Processing**: Complete Stripe integration with webhooks
- **Service Mesh**: Eureka-based service discovery and communication
- **Auto-Assignment**: Intelligent order assignment using Spring Scheduler
- **Multi-Database Support**: MySQL per service, PostgreSQL for vectors, MongoDB for documents
- **Database Per Service**: True microservices data isolation
- **Smart Chatbot**: AI-powered conversational interface with multi-service integration
- **Connection Pooling**: HikariCP optimization across all services
- **Cloud Integration**: MongoDB Atlas for scalable document storage

## 📋 Services Details

### 1. Discovery Service (Port: 8761)
- **Purpose**: Service registry and discovery
- **Technology**: Netflix Eureka Server
- **Dependencies**: Spring Cloud Netflix Eureka Server
- **Features**:
  - Service registration and health monitoring
  - Load balancing support
  - Service discovery dashboard

### 2. API Gateway Service (Port: 8080)
- **Purpose**: Single entry point, routing, and load balancing
- **Technology**: Spring Cloud Gateway (Reactive)
- **Features**: 
  - Request routing and filtering
  - Load balancing with Eureka integration
  - Cross-cutting concerns (CORS, authentication)
  - Reactive programming with WebFlux
  - RabbitMQ integration for messaging

### 3. User Service (Port: 8081)
- **Purpose**: User management, authentication, and authorization
- **Database**: Dedicated MySQL database (`mafia_user_service`)
- **Security Features**:
  - **Spring Security**: Custom security configuration
  - **JWT Authentication**: Token-based stateless authentication (jjwt 0.12.6)
  - **BCrypt Password Encoding**: Secure password hashing (strength 12)
  - **Role-based Access Control**: Admin, Customer, Washer roles
  - **OAuth2 Resource Server**: JWT validation
- **Performance Optimizations**:
  - **HikariCP Connection Pooling**: Optimized database connections
  - **Connection Pool Settings**: Max 10, Min 2 idle connections
- **Cross-cutting Concerns**:
  - **AOP Logging**: Method execution time tracking with @Around advice
  - **Custom Logback Configuration**: File and console logging
  - **Global Exception Handling**: Centralized error management
- **API Documentation**: Swagger UI with SpringDoc OpenAPI
- **Testing**: JUnit 5, Mockito 5.14.2, Spring Security Test
- **Validation**: Jakarta Validation API, Hibernate Validator

### 4. Booking Service (Port: 8084)
- **Purpose**: Order and booking management with advanced scheduling and real-time chat
- **Database**: Dedicated MySQL database (`mafia_booking_service`)
- **Advanced Features**:
  - **Spring Scheduler**: Automatic order assignment every 30 seconds (@Scheduled)
  - **Circuit Breaker**: Resilience4j for fault tolerance with custom configuration
  - **OpenFeign**: Declarative REST clients for User and Car services
  - **RabbitMQ Integration**: Event publishing with TopicExchange
  - **Spring AI Integration**: Vertex AI Gemini model integration
  - **WebSocket Chat**: Real-time messaging between customers and washers using STOMP
  - **Chat Message Storage**: Persistent chat history with MySQL storage
- **Performance Optimizations**:
  - **HikariCP Connection Pooling**: Max 10 connections, optimized timeouts
  - **Feign Circuit Breaker**: 50% failure threshold, 3s wait duration
- **Security**: JWT authentication with custom filters
- **Cross-cutting Concerns**:
  - **AOP Logging**: Performance monitoring
  - **Custom Logback**: File-based logging configuration
  - **Global Exception Handling**: Custom exceptions
- **Event-Driven Architecture**: Publishes order events for notifications
- **Real-time Communication**: WebSocket configuration for instant messaging

### 5. Car Service (Port: 8083)
- **Purpose**: Vehicle information management
- **Database**: Dedicated MySQL database (`mafia_car_service`)
- **Integration Features**:
  - **OpenFeign Client**: User Service integration for customer validation
  - **API-based Validation**: Customer existence validation via User Service
  - **Feign Configuration**: Custom timeouts (5s connect, 5s read)
- **Performance Optimizations**:
  - **HikariCP Connection Pooling**: Max 8 connections, optimized for car data
- **Security Features**:
  - JWT authentication and authorization
  - Role-based access control
- **Cross-cutting Concerns**:
  - **AOP Logging**: Method execution tracking
  - **Custom Logback Configuration**: Structured logging
  - **Global Exception Handling**: Centralized error management
- **API Documentation**: Swagger UI integration
- **Features**:
  - Car registration and details management
  - Customer-car relationship mapping (via email references)
  - Car service history tracking

### 6. Payment Service (Port: 8086)
- **Purpose**: Payment processing and transaction management
- **Database**: Dedicated MySQL database (`mafia_payment_service`)
- **Payment Integration**:
  - **Stripe Payment Gateway**: Complete integration (stripe-java 24.0.0)
  - **Stripe Checkout Sessions**: Hosted payment pages
  - **Payment Intent Management**: Secure payment processing
  - **Webhook Support**: Payment status updates
- **Performance Optimizations**:
  - **HikariCP Connection Pooling**: Max 8 connections for payment processing
- **Security Features**:
  - **Method-level Security**: @PreAuthorize annotations
  - **OAuth2 Integration**: Multiple OAuth2 configurations
  - **JWT Authentication**: Stateless security
- **Event-Driven Architecture**:
  - **RabbitMQ Integration**: Payment event publishing
  - **OpenFeign**: Integration with Order service
- **Cross-cutting Concerns**:
  - **AOP Logging**: Transaction monitoring
  - **Custom Logback**: Payment audit logging

### 7. Washer Service (Port: 8085)
- **Purpose**: Washer management and service assignment
- **Database**: Dedicated MySQL database (`mafia_washer_service`)
- **Performance Optimizations**:
  - **HikariCP Connection Pooling**: Max 8 connections for washer operations
- **Integration Features**:
  - **OpenFeign Clients**: Order and Payment service integration
  - **Circuit Breaker**: Fault tolerance with Resilience4j
  - **RabbitMQ**: Event publishing for washer actions
- **Business Logic**:
  - Work request acceptance/rejection
  - Invoice generation with payment integration
  - Order completion workflow
  - Payment status validation
- **Cross-cutting Concerns**:
  - **AOP Logging**: Service method monitoring
  - **Global Exception Handling**: Feign exception management
- **API Documentation**: Swagger UI

### 8. Notification Service (Port: 8087)
- **Purpose**: Email notifications and messaging
- **Message Queue**: RabbitMQ with @RabbitListener
- **Email Integration**:
  - **Spring Mail**: SMTP email sending
  - **Thymeleaf Templates**: HTML email templates
  - **Gmail SMTP**: Production email delivery
- **Template System**:
  - Order creation notifications
  - Order assignment alerts
  - Payment confirmation emails
  - Invoice generation notices
  - Order completion notifications
- **Event Processing**: Asynchronous message consumption
- **Template Files**:
  - email-layout.html (base template)
  - order-created-email.html
  - order-assigned-email.html
  - payment-confirmation-email.html
  - invoice-generated-email.html
  - order-completed-email.html
  - washer-accepted-email.html

### 10. Review Service (Port: 8092)
- **Purpose**: Customer feedback and rating management
- **Database**: MongoDB Atlas cloud database (`carwash` database)
- **Document Storage**:
  - **Spring Data MongoDB**: Document-based data access
  - **MongoDB Atlas**: Cloud-hosted MongoDB cluster
  - **Document Collections**: Reviews with flexible schema
- **Security Features**:
  - **JWT Authentication**: Token-based authentication (jjwt 0.12.6)
  - **Spring Security**: Custom security configuration
- **Integration Features**:
  - **OpenFeign Client**: Order service integration for validation
  - **Eureka Client**: Service discovery integration
- **Business Logic**:
  - Customer review submission
  - Rating system (1-5 stars)
  - Order-based review validation
  - Review history and analytics
- **Features**:
  - Review creation and management
  - Order validation before review submission
  - Customer feedback collection
  - Rating aggregation and statistics

### 11. Media Service (Port: 8089)
- **Purpose**: File and media management for images and documents
- **Database**: MongoDB with GridFS for file storage
- **File Storage Features**:
  - **GridFS Integration**: Large file storage in MongoDB
  - **Multi-format Support**: Images, documents, and media files
  - **File Metadata Management**: Comprehensive file information tracking
  - **Entity-based Organization**: Files organized by entity type (PROFILE, CAR, SERVICE_BEFORE, SERVICE_AFTER)
- **Upload Capabilities**:
  - **Profile Images**: User profile photo management
  - **Car Images**: Vehicle photo storage and retrieval
  - **Service Images**: Before/after service documentation
  - **File Size Limits**: Configurable upload size restrictions (10MB default)
- **Security Features**:
  - **JWT Authentication**: Secure upload endpoints
  - **Role-based Access**: Different permissions for customers and washers
  - **Public Display**: Optimized image serving without authentication
- **Performance Optimizations**:
  - **Direct File Serving**: Efficient image delivery
  - **Error Handling**: Comprehensive file validation and error management
  - **Cleanup Operations**: Orphaned metadata removal
- **Integration**:
  - **API Gateway Routing**: Seamless integration with frontend
  - **Cross-service Usage**: Profile, car, and service image management
  - **Eureka Client**: Service discovery integration
- **Features**:
  - Multi-entity file organization
  - Secure file upload and retrieval
  - Image optimization and serving
  - Metadata management and cleanup
  - RESTful API for file operations

### 9. AI Service (Port: 8088)
- **Purpose**: AI-powered features, document processing, intelligent chatbot, and speech processing
- **Database**: PostgreSQL with PGVector extension
- **AI/ML Stack**:
  - **Google Vertex AI**: Gemini model integration
  - **Spring AI**: 1.0.1 framework
  - **Vector Embeddings**: Text-to-vector conversion (3072 dimensions)
  - **PGVector Store**: Vector similarity search
  - **RAG Implementation**: Retrieval-Augmented Generation
  - **Google Cloud Speech-to-Text**: Audio transcription capabilities
  - **Google Cloud Text-to-Speech**: Voice synthesis for responses
- **Smart Chatbot Features**:
  - **ChatbotService**: Intelligent conversational AI
  - **Multi-Service Integration**: User, Car, Booking, Washer service clients
  - **Context-Aware Responses**: User profile and booking history integration
  - **Automated Booking**: AI-powered booking assistance
  - **Real-time Order Management**: Status updates and tracking
  - **Voice Interaction**: Speech-to-text and text-to-speech capabilities
- **Document Processing**:
  - **Apache PDFBox**: PDF text extraction
  - **FAISS**: Vector similarity search optimization
  - **Spring AI PDF Reader**: Document ingestion
- **Speech Processing**:
  - **Audio File Handling**: WebM/Opus audio format support
  - **Real-time Transcription**: Live audio processing
  - **Voice Commands**: Speech-based booking and queries
- **Authentication**:
  - **Google Cloud Credentials**: Service account authentication
  - **OAuth2 Scopes**: Cloud platform access
- **Integration**:
  - **WebClient**: Reactive HTTP client for Vertex AI API
  - **OpenFeign Clients**: User, Car, Booking, Washer service integration
  - **RabbitMQ**: Event-driven AI processing
  - **Eureka Client**: Service discovery
- **Features**:
  - PDF document ingestion and processing
  - Vector embedding generation
  - Semantic search capabilities
  - AI-powered customer support chatbot
  - Document similarity matching
  - Intelligent booking assistance
  - Multi-service data aggregation
  - Voice-enabled interactions
  - Audio transcription and synthesis

## 🔧 Configuration

### Database Configuration
```properties
# User Service
spring.datasource.url=jdbc:mysql://localhost:3306/mafia_user_service

# Booking Service  
spring.datasource.url=jdbc:mysql://localhost:3306/mafia_booking_service

# Car Service
spring.datasource.url=jdbc:mysql://localhost:3306/mafia_car_service

# Payment Service
spring.datasource.url=jdbc:mysql://localhost:3306/mafia_payment_service

# Washer Service
spring.datasource.url=jdbc:mysql://localhost:3306/mafia_washer_service

# AI Service (PostgreSQL)
spring.datasource.driver-class-name=org.postgresql.Driver

# Review Service (MongoDB)
spring.data.mongodb.uri=mongodb+srv://carwashuser:password@cluster.mongodb.net/
spring.data.mongodb.database=carwash

# Connection Pooling (All MySQL services)
spring.datasource.hikari.maximum-pool-size=8-10
spring.datasource.hikari.minimum-idle=2
```

### Message Queue Configuration
```properties
# RabbitMQ
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```

### Service Discovery Configuration
```properties
# Eureka Client
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
```

## 🚀 Getting Started

### Prerequisites
- Java 17
- Maven 3.6+
- MySQL 8.0+
- PostgreSQL 13+ (for AI service)
- RabbitMQ 3.8+
- Google Cloud Account (for AI features)
- Stripe Account (for payments)

### Installation & Setup

1. **Clone the repository**
```bash
git clone <repository-url>
cd Backend
```

2. **Start Infrastructure Services**
```bash
# Start MySQL
# Start PostgreSQL
# Start RabbitMQ
```

3. **Start Services in Order**
```bash
# 1. Discovery Service
cd TheDetailingMafia-DiscoveryService-master
mvn spring-boot:run

# 2. API Gateway
cd TheDetailingMafia-APIGatewayService-master
mvn spring-boot:run

# 3. Core Services (can be started in parallel)
cd TheDetailingMafia-UserService-master
mvn spring-boot:run

cd TheDetailingMafia-CarService-master
mvn spring-boot:run

cd TheDetailingMafia-BookingService-master
mvn spring-boot:run

cd TheDetailingMafia-PaymentService-master
mvn spring-boot:run

cd TheDetailingMafia-WasherService-master
mvn spring-boot:run

cd TheDetailingMafia-NotificationService-master
mvn spring-boot:run

cd TheDetailingMafia-AI-service-master/ai
mvn spring-boot:run
```

### Environment Variables
Set the following environment variables:
```bash
# Database
DB_HOST=localhost
DB_PORT=3306
DB_NAME=thecarwashdatabase
DB_USERNAME=root
DB_PASSWORD=your_password

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000

# Stripe
STRIPE_SECRET_KEY=your_stripe_secret_key
STRIPE_PUBLISHABLE_KEY=your_stripe_publishable_key

# Google Cloud (AI Service)
GOOGLE_APPLICATION_CREDENTIALS=path/to/service-account.json
VERTEX_AI_PROJECT_ID=your-project-id
VERTEX_AI_LOCATION=your-location

# Email
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
```

## 📊 API Documentation
Each service provides Swagger UI documentation:
- User Service: http://localhost:8081/swagger-ui
- Car Service: http://localhost:8083/swagger-ui
- Booking Service: http://localhost:8084/swagger-ui
- Washer Service: http://localhost:8085/swagger-ui
- Payment Service: http://localhost:8086/swagger-ui
- AI Service: http://localhost:8088/swagger-ui
- Media Service: http://localhost:8089/swagger-ui
- Review Service: http://localhost:8092/swagger-ui

## 🐳 Docker Deployment
The entire microservices architecture can be deployed using Docker Compose:

```bash
# Start all services with dependencies
docker-compose up -d

# Scale specific services
docker-compose up -d --scale booking-service=3

# View logs
docker-compose logs -f [service-name]
```

### Docker Services Included:
- **Databases**: MySQL, PostgreSQL, MongoDB
- **Message Queue**: RabbitMQ
- **Monitoring**: ELK Stack (Elasticsearch, Logstash, Kibana)
- **All Microservices**: Complete backend infrastructure

## 🔍 Monitoring
- **Eureka Dashboard**: http://localhost:8761
- **Actuator Endpoints**: Available on all services at `/actuator`
- **Health Checks**: `/actuator/health`
- **Metrics**: `/actuator/metrics`

## 🧪 Testing
Run tests for all services:
```bash
mvn test
```

## 📝 Version History
- **Current Version**: 1.0.0-SNAPSHOT
- **Spring Boot**: 3.4.4 / 3.2.4
- **Spring Cloud**: 2024.0.1 / 2023.0.5
- **Java**: 17

## 🤝 Contributing
1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## 📄 License
This project is licensed under the MIT License.

## 📞 Support
For support and questions, please contact the development team.

---
**TheDetailingMafia Backend Team** 🚗✨