# yottr

A web site for matching boat owners who need crew with sailors looking for a berth. An owner
posts a listing for a boat, saying what kind of sailing it is (cruising, racing, a delivery, a
course), who they're looking for, the qualifications wanted and how costs are shared. Crew can
browse and search the listings.

It started in 2014 and has recently been brought up to date. It isn't deployed anywhere. The
listing fields still being worked out are in `design.txt`, and the wider wish list is in
`TODO.txt`.

## Stack

- Java 25, Spring Boot 4 (Spring MVC, Thymeleaf, Spring Security, Spring Data JPA)
- Postgres, with the schema managed by Flyway
- Micrometer and Prometheus for metrics, with a Grafana dashboard
- JUnit, Testcontainers and Cucumber for tests

## Prerequisites

- JDK 25
- Docker. The app starts its local services from `compose.yaml`, and the tests use Testcontainers.
- `unzip`, on Linux and WSL. Without it the wrapper downloads Maven as a `.tar.gz`, checks it against
  the zip's checksum, and stops with "your Maven distribution might be compromised".

Use the Maven wrapper, `./mvnw` (`mvnw.cmd` on Windows). It downloads the Maven version named in
`.mvn/wrapper/maven-wrapper.properties` for this project only, leaving the Maven on your `PATH`
alone. A plain `mvn` is stopped by an enforcer rule that says so.

## Building

```sh
./mvnw clean verify            # add -DskipITs to leave out the integration tests
```

The Cucumber acceptance tests (`src/test/resources/features`) run with the integration tests,
and their report is written to `target/cucumber-report.html`.

## Running locally

```sh
./mvnw spring-boot:run
```

This runs with the `dev` profile (`application-dev.properties`: SQL and security logging, and
templates reloaded without a restart) and starts these from `compose.yaml`:

| Service    | Where                    | Notes                                         |
|------------|--------------------------|-----------------------------------------------|
| yottr      | http://localhost:8080/   |                                               |
| Postgres   | a random host port       | data kept in the `yottr-data` volume          |
| Prometheus | http://localhost:39090   |                                               |
| Grafana    | http://localhost:33000   | Yottr dashboard; anonymous viewing, `admin` / `admin` to edit |

The Prometheus and Grafana ports are kept away from the usual 9090 and 3000 so they don't clash
with other projects.

### Sample data

To put an admin user `mike` and some boats in the local database:

```sh
./mvnw test -Dtest=InitialiseDatabase -Dyottr.initialise=true -Dyottr.admin.password=<a password>
```

No password is kept in the source. If you want somewhere to note yours, `local-credentials.properties`
in the project root is ignored by git. Nothing reads it.

## Database

Flyway owns the schema, in `src/main/resources/db/migration`. V2 loads the reference data
(qualification levels, financial arrangements, frequencies). Hibernate only validates the schema,
so a change to an entity needs a new migration. A database that Hibernate created before Flyway
was introduced is baselined at V1.

## Running elsewhere

```sh
java -jar target/yottr.jar
```

with `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME` and `SPRING_DATASOURCE_PASSWORD` set.

## Operations

- `/actuator/health` and `/actuator/prometheus` are open, for Prometheus to scrape.
- Administrators can change log levels at `/admin/logging`, or through `/actuator/loggers`. The
  changes last until the application restarts.
- In production, put the actuator on its own management port (`management.server.port`) that
  isn't public.

## Licence

Apache License 2.0. See `LICENSE`.
