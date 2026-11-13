# AtlasRH Testing and Quality Report

## Summary

The final local verification completed on 2026-11-13. `./mvnw clean verify` passed all 64 tests with no failures, errors, or skips. JaCoCo line coverage is 92.6%, above the Maven-enforced 80% minimum. The final SonarQube analysis reported no open bugs, vulnerabilities, code smells, or security hotspots, and its Quality Gate passed.

## Test strategy and results

| Requirement | Implementation | Result |
|---|---|---|
| T1 — Service rules | JUnit 5 and Mockito tests for employee identity uniqueness and self-manager validation, active-contract constraints, working-day and leave-balance calculations, leave decisions, and cancellation | Passed |
| T2 — Controllers | `@WebMvcTest` and MockMvc tests for department, employee, contract, leave type, and leave request endpoints, including JSON, validation, and status codes | Passed |
| T3 — Repositories | `@DataJpaTest` for employee search/filter/pagination/sorting and leave-request query behavior | Passed |
| T4 — End-to-end | Two `@SpringBootTest` + MockMvc scenarios: create employee and leave request then approve; reject deletion of a department with employees | Passed |
| Test isolation | Active `test` profile uses `jdbc:h2:mem:atlasrh-test` and a create/drop schema | Confirmed |

Final command: `./mvnw clean verify`

| Test result | Value |
|---|---:|
| Tests | 64 |
| Failures / errors / skipped | 0 / 0 / 0 |
| Build | Success |

## JaCoCo coverage

JaCoCo report: `target/site/jacoco/index.html` (XML: `target/site/jacoco/jacoco.xml`).

| Counter | Covered | Total | Coverage |
|---|---:|---:|---:|
| Lines | 541 | 584 | 92.6% |
| Instructions | 2,259 | 2,505 | 90.2% |
| Branches | 112 | 162 | 69.1% |

The Maven `verify` phase enforces at least 80% bundle line coverage. The final run met the threshold.

## SonarQube findings and fixes

A first local scan passed its Quality Gate but reported 10 actionable issues:

| Initial finding | Count | Fix |
|---|---:|---|
| `java:S4684`: controllers accepted JPA entities as request bodies, enabling unsafe input binding/over-posting | 6 | Added validated request DTOs for employees, contracts, departments, leave requests, and leave types; controllers now map from DTOs instead of binding entities |
| `java:S8696`: `DayOfWeek` values compared by reference instead of value | 4 | Replaced reference comparisons with `.equals()` |

The leave-request update path was also narrowed to editable dates and reason. Existing status, manager comment, employee, and leave type are preserved, preventing over-posted JSON fields from changing decision or ownership data. Regression tests cover the service update and controller request behavior.

### Final analysis

Local SonarQube Community analysis against project `AtlasRH`:

| Metric | Final result |
|---|---:|
| Bugs | 0 |
| Vulnerabilities | 0 |
| Code smells | 0 |
| Security hotspots | 0 |
| Overall Sonar coverage | 87.1% |
| Sonar line coverage | 92.2% |
| New-code coverage | 80.4% (80% gate threshold) |
| Quality Gate | **Passed** |

The Sonar scan also reported 0 new violations and 0.0% duplicated new lines. Sonar and JaCoCo coverage values differ because the tools calculate coverage over different analyzed source/condition scopes; the JaCoCo values above are the Maven report counters.

## Before and after

| Measure | Before | After |
|---|---:|---:|
| Automated tests | 10, including 1 failing | 64 passing, 0 skipped |
| JaCoCo line coverage | Not configured | 92.6%; 80% threshold enforced |
| Sonar bugs | 4 | 0 |
| Sonar vulnerabilities | 6 | 0 |
| Sonar code smells | 0 | 0 |
| Sonar Quality Gate | Initial scan passed with findings | Passed; no current issues |

## Reproduction

Run the test suite and coverage:

```bash
./mvnw clean verify
```

Run SonarQube analysis against an available local instance:

```bash
./mvnw clean verify sonar:sonar \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.projectKey=AtlasRH \
  -Dsonar.token="$SONAR_TOKEN" \
  -Dsonar.qualitygate.wait=true
```

See the [README](./README.md) for test-profile and project instructions and the [presentation outline](./PRESENTATION-OUTLINE.md) for the demo sequence.

## Remaining external/process work

- `JIRA-PLAN.md` contains three Jira-ready story drafts; they still need to be created on the actual Jira board.
- The repository is on `main`; the requested branch-per-part workflow was not fabricated after the fact. Create and merge feature branches through the team's normal process if required.
