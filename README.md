# AtlasRH V2 Test

This repository is a test-focused project built on top of the existing AtlasRH HR application. Its purpose is to validate backend business logic with unit tests, especially service-layer behavior such as employee creation and department validation.

## Project purpose

This project is not meant to replace the main application. Instead, it acts as a testing companion for the AtlasRH backend and demonstrates how to:

- test Spring service logic in isolation
- mock repository and service dependencies with Mockito
- validate business rules without needing a running database
- keep the main project cleaner while still covering core behaviors

## Tech stack

- Java 17
- Spring Boot 4.1.1
- Maven
- JUnit 5
- Mockito

## Project structure

```text
AtlasRH-V2-Test/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── org/example/atlasrh/
│   │           ├── controller/
│   │           ├── dto/
│   │           ├── mapper/
│   │           ├── model/
│   │           ├── repository/
│   │           └── service/
│   └── test/
│       └── java/
│           └── org/example/atlasrh/
│               └── EmployeeServiceTest.java
├── pom.xml
├── mvnw
├── mvnw.cmd
├── README.md
└── target/
```

## Prerequisites

Before running the tests, make sure you have:

- Java 17 or newer
- Maven 3.9+

## Running the tests

Run all test classes:

```bash
./mvnw test
```

Run only the employee service test:

```bash
./mvnw test -Dtest=EmployeeServiceTest
```

## US6: Leave balance and length of service

The employee leave-request API includes these read-only endpoints:

```http
GET /api/leaverequest/employee/{employeeId}/balance
GET /api/leaverequest/employee/{employeeId}/length-of-service
```

The balance response includes each configured leave type, its annual allowance, approved working days used in the current calendar year, and the remaining balance. The service-length response reports completed years and remaining months since the employee's hire date.

Example balance response:

```json
[
  {
    "leaveTypeId": 1,
    "leaveTypeName": "Annual",
    "annualAllowanceDays": 25,
    "approvedDaysUsed": 3,
    "remainingDays": 22
  }
]
```

Example length-of-service response:

```json
{
  "years": 4,
  "months": 7
}
```

## Example test coverage

The current test verifies that:

- a department is fetched through `DepartmentService`
- the employee is assigned to that department
- the repository `save()` method is called
- the returned employee is not null

This is a good example of how to test a service method with mocked dependencies.

## Notes

This repository is intended for testing and learning. It complements the main AtlasRH project and shows how to validate logic in a clean, isolated way before integrating with full application or database testing.
