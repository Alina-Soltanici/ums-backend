# UMS Backend - User Management System API

A robust Spring Boot REST API for managing users, built with modern Java technologies and containerized with Docker.

## 📋 Overview

This backend API provides comprehensive user management functionality, including user creation, authentication, role management, and CRUD operations. Built with Spring Boot, it offers a scalable and maintainable solution for applications requiring user management capabilities.

## 🛠️ Technology Stack

- **Framework**: Spring Boot
- **Language**: Java (99.4%)
- **Build Tool**: Maven
- **Containerization**: Docker
- **Architecture**: RESTful API

## ✨ Key Features

- User registration and authentication
- User profile management (CRUD operations)
- Role-based access control
- RESTful API endpoints
- Docker containerization for easy deployment
- Maven-based dependency management

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- Docker (optional, for containerized deployment)

### Installation & Running

#### Option 1: Run with Maven
```bash
# Clone the repository
git clone https://github.com/Alina-Soltanici/ums-backend.git
cd ums-backend

# Build the project
./mvnw clean install

# Run the application
./mvnw spring-boot:run
```

#### Option 2: Run with Docker
```bash
# Build Docker image
docker build -t ums-backend .

# Run container
docker run -p 8080:8080 ums-backend
```

The API will be available at `http://localhost:8080`

## 📚 API Endpoints

The API provides endpoints for:

- **User Management**: Create, read, update, and delete user accounts
- **Authentication**: User login and session management
- **Authorization**: Role-based access control

*(Add specific endpoint documentation based on your controllers)*

## 🗂️ Project Structure
```
ums-backend/
├── .mvn/wrapper/          # Maven wrapper files
├── src/
│   ├── main/
│   │   ├── java/          # Java source files
│   │   └── resources/     # Configuration files
│   └── test/              # Test files
├── Dockerfile             # Docker configuration
├── pom.xml               # Maven dependencies
└── mvnw                  # Maven wrapper script
```

## 🐳 Docker Support

The project includes a Dockerfile for containerization. This enables:
- Consistent deployment across environments
- Easy scaling and orchestration
- Simplified dependency management

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 👤 Author

**Alina Soltanici**
- GitHub: [@Alina-Soltanici](https://github.com/Alina-Soltanici)

## 📝 License

This project is open source and available under the MIT License.

## 🔗 Related Projects

- [Frontend Application](https://ums-frontend.vercel.app) - Live demo deployed on Vercel
- [Frontend Repository](https://github.com/Alina-Soltanici/ums-frontend) - Source code