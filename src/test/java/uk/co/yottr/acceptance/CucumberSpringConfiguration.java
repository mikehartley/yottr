package uk.co.yottr.acceptance;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import uk.co.yottr.testconfig.TestcontainersConfig;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * The whole application on a random port, against Postgres in Docker. Scenarios share the database,
 * so each one creates its own members under unique usernames.
 */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(TestcontainersConfig.class)
public class CucumberSpringConfiguration {
}
