# AtlasRH

AtlasRH is a Spring Boot REST API for human resources management. The project has grown from a simple department/employee backend into a more complete HR platform covering employee contracts, leave management, and leave approval workflows.

## What's new

The latest version of the project includes several major improvements:

- Contract management for employees with contract types: `CDI`, `CDD`, and `STAGE`
- Leave type management with annual leave allowance rules
- Leave request lifecycle: create, update, view, cancel, approve, and reject requests
- Manager-based approval workflow using `LeaveStatus` values such as `PENDING`, `APPROVED`, `REJECTED`, and `CANCELLED`
- Dedicated REST controllers for contracts and leave modules
- DTO-based responses and MapStruct mapping for cleaner API data transfer

## Overview

This application uses Java 17, Spring Boot 4.1.1, Spring Data JPA, PostgreSQL, MapStruct, and Maven to expose HR operations through a REST API. It is designed as a backend foundation for a company or HR administration system.

## Core features

- Manage departments
- Register employees and assign them to a department
- Link an employee to a contract
- List and query available leave types
- Submit and track leave requests
- Approve or reject employee leave requests
- Persist HR data with JPA and PostgreSQL
- Expose clean API responses using DTOs

## Tech stack

- Java 17
- Spring Boot 4.1.1
- Spring Web
- Spring Data JPA
- PostgreSQL
- Maven
- MapStruct
- Lombok

## Project structure

```text
AtlasRH/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/example/atlasrh/
│   │   │       ├── controller/
│   │   │       ├── dto/
│   │   │       ├── mapper/
│   │   │       ├── model/
│   │   │       ├── repository/
│   │   │       └── service/
│   │   └── resources/
│   │       └── application.properties
│   └── test/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── README.md
└── target/
```

## Prerequisites

Before running the project, make sure you have:

- Java 17+
- Maven 3.9+
- PostgreSQL installed and running
- A database named `atlasrhv1` or update the credentials in the configuration

## Configuration

The project configuration is defined in `src/main/resources/application.properties`.

```properties
spring.application.name=AtlasRH

spring.datasource.url=jdbc:postgresql://localhost:5432/atlasrhv1
spring.datasource.username=postgres
spring.datasource.password=1996

spring.datasource.driver-class-name=org.postgresql.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

If your PostgreSQL setup is different, update the datasource URL, username, and password accordingly.

## Running the application

### With Maven

```bash
./mvnw clean install
./mvnw spring-boot:run
```

The application will start on the default Spring Boot port:

```text
http://localhost:8080
```

## API endpoints

### Department endpoints

```http
GET /api/department
POST /api/department
```

### Employee endpoints

```http
GET /api/employee
GET /api/employee/{id}
POST /api/employee/{departmentId}
```

### Contract endpoints

```http
GET /api/contract
POST /api/contract/{employeeId}
```

### Leave type endpoints

```http
GET /api/leavetype
GET /api/leavetype/{id}
POST /api/leavetype
```

### Leave request endpoints

```http
POST /api/leaverequest?employeeId={employeeId}&leaveTypeId={leaveTypeId}
PUT /api/leaverequest/{id}
GET /api/leaverequest/{id}
GET /api/leaverequest
GET /api/leaverequest/employee/{employeeId}
GET /api/leaverequest/employee/{employeeId}/status?status=PENDING
PUT /api/leaverequest/cancel?employeeId={employeeId}&requestId={requestId}
PUT /api/leaverequest/{requestId}/approve?managerId={managerId}
PUT /api/leaverequest/{requestId}/reject?managerId={managerId}
```

## Data model summary

### Department

- `id`
- `name`
- `description`
- `employees`

### Employee

- `matricule`
- `firstName`
- `lastName`
- `email`
- `jobTitle`
- `hireDate`
- `role`
- `department`
- `contract`

### Contract

- `id`
- `contractType` (`CDI`, `CDD`, `STAGE`)
- `startDate`
- `endDate`
- `salary`

### Leave type

- `id`
- `name`
- `annualallowancedays`

### Leave request

- `id`
- `startDate`
- `endDate`
- `reason`
- `status`
- `employee`
- `leavetype`

## Notes

AtlasRH is now positioned as a practical HR backend with both employee administration and leave-management workflows. The project is well-suited for learning, internal tooling, or further expansion with authentication, validation, and additional HR business rules.

## License

This project is currently unlicensed and intended for learning and internal development use unless otherwise specified.

## Contributing

Contributions are welcome. If you want to improve the API, add validation, write tests, or extend the HR modules, feel free to fork the project and submit a pull request.
# AtlasRH-V2-Test
# AtlasRH-V2-Test
