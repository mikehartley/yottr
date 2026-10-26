yottr
=====

Prerequisites:
  JDK 25
  Docker (the app starts its local services from compose.yaml, and the tests use Testcontainers)

Maven:
  Use the wrapper, ./mvnw (or mvnw.cmd on Windows). It downloads and uses the Maven version in
  .mvn/wrapper/maven-wrapper.properties (3.10.0) for this project only, so the Maven on your PATH
  is unchanged. A plain "mvn" is stopped by the enforcer rule with a message saying so.

To build:
  ./mvnw clean verify [-DskipITs]
  The Cucumber acceptance tests (src/test/resources/features) run with the integration tests;
  their report is target/cucumber-report.html.

To run locally:
  ./mvnw spring-boot:run
  This also starts, from compose.yaml:
    Postgres     data kept in the yottr-data volume
    Prometheus   http://localhost:39090
    Grafana      http://localhost:33000  (Yottr dashboard; anonymous viewing, admin / admin to edit)

To put some initial data in the local database (reference data, an admin user "mike" and some boats):
  ./mvnw test -Dtest=InitialiseDatabase -Dyottr.initialise=true

To run elsewhere:
  java -jar target/yottr.jar
  with SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME and SPRING_DATASOURCE_PASSWORD set

URL (by default):
  http://localhost:8080/

Operations:
  /actuator/health and /actuator/prometheus are open, for Prometheus.
  Administrators can change log levels at /admin/logging, or through /actuator/loggers.
  Changes last until the application restarts.
  In production, put the actuator on its own management port (management.server.port) that isn't public.
