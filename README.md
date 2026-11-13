# AtlasRH V2

AtlasRH is a Spring Boot REST API for HR data, including departments, employees, contracts, leave types, and leave requests.

## Stack

- Java 17 and Spring Boot 4.1.1
- Maven, JUnit 5, Mockito, Spring MVC Test, and Spring Data JPA Test
- H2 for isolated tests; PostgreSQL for application profiles
- JaCoCo and SonarQube Scanner for Maven

## Test strategy

- Service unit tests mock repositories and collaborators.
- Controller tests use `@WebMvcTest` and `MockMvc`.
- Repository tests use `@DataJpaTest` to verify filtering, pagination, and sorting.
- End-to-end tests use `@SpringBootTest` and MockMvc across the web, service, repository, and database layers.

Tests use the `test` profile and its dedicated in-memory H2 database (`jdbc:h2:mem:atlasrh-test`). The test profile creates and drops its schema; tests never connect to a development or deployment database.

Run tests:

```bash
./mvnw test
```

Run the employee service tests only:

```bash
./mvnw test -Dtest=EmployeeServiceTest
```

Generate coverage:

```bash
./mvnw clean verify
```

JaCoCo reports are generated at `target/site/jacoco/index.html` (HTML) and `target/site/jacoco/jacoco.xml` (XML).

## SonarQube

Start a local SonarQube server, configure the `AtlasRH` project, and provide its token through `SONAR_TOKEN`:

```bash
./mvnw clean verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.projectKey=AtlasRH \
  -Dsonar.token=$SONAR_TOKEN \
  -Dsonar.qualitygate.wait=true
```

This command waits for the configured Quality Gate and fails if it does not pass. Sonar findings and Quality Gate status must be recorded from a completed analysis against the target SonarQube instance, not inferred from local test results.

## Covered business rules

The tests exercise employee number/email uniqueness, self-manager rejection, active-contract constraints, leave working-day and balance calculations, leave approval/rejection/cancellation, HTTP validation and response statuses, employee filtering and pagination/sorting, and two full-stack scenarios.

Fixes made alongside the tests include enforcing employee uniqueness and manager rules, selecting active contracts correctly, validating leave request references, implementing employee/leave-request search paths, preventing cancellation of decided requests, correcting DTO mapping by binding Lombok and MapStruct annotation processing, using validated request DTOs instead of binding JPA entities, preserving leave ownership/decision fields on update, and using value-based `DayOfWeek` comparisons.

| Measure | Before | After |
|---|---:|---:|
| Automated tests | 10, with 1 failing | 64 passing, 0 skipped |
| JaCoCo line coverage | Not configured | 92.6% (80% minimum enforced) |
| JaCoCo instruction coverage | Not configured | 90.2% |
| JaCoCo branch coverage | Not configured | 69.1% |
| SonarQube findings / Quality Gate | Initial scan: 4 bugs, 6 vulnerabilities | 0 bugs, 0 vulnerabilities, 0 code smells, 0 security hotspots; Quality Gate passed |

Refresh coverage results using `./mvnw clean verify`. The detailed results/fixes are in the [testing and quality report](./TESTING-QUALITY-REPORT.md), and the [presentation outline](./PRESENTATION-OUTLINE.md) provides the 15-minute demo flow. The [Jira planning draft](./JIRA-PLAN.md) is not posted to a Jira board.

Before applying the model changes to an existing database, check for duplicate employee emails and plan the migration of enum columns and the employee-contract/manager relationships. Tests create a fresh H2 schema and do not migrate or modify any application database.

## Leave balance and length-of-service API

```http
GET /api/leaverequest/employee/{employeeId}/balance
GET /api/leaverequest/employee/{employeeId}/length-of-service
```

The balance response reports each leave type's annual allowance, approved working days used in the current calendar year, and remaining balance. The service-length response reports completed years and remaining months since the employee's hire date.
