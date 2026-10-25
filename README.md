yottr
=====

Prerequisites:
  JDK 25
  Maven 3.6.3 or later
  Docker (the app starts its local Postgres from compose.yaml, and the tests use Testcontainers)

To build:
  mvn clean verify [-DskipITs]

To run locally:
  mvn spring-boot:run
  (starts Postgres from compose.yaml on first use; data is kept in the yottr-data volume)

To put some initial data in the local database (reference data, an admin user "mike" and some boats):
  mvn test -Dtest=InitialiseDatabase -Dyottr.initialise=true

To run elsewhere:
  java -jar target/yottr.jar
  with SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME and SPRING_DATASOURCE_PASSWORD set

URL (by default):
  http://localhost:8080/

The Selenium sign-in test needs the app running and Firefox installed, so it is left out of the build:
  mvn verify -Dit.test=SignInSignOutIT
