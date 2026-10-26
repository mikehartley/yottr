package uk.co.yottr.testconfig;

import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import uk.co.yottr.repository.UserRepository;

@TestConfiguration
@ComponentScan(value = { "uk.co.yottr.model", "uk.co.yottr.validator" })
public class UnitConfig {

    @Bean
    public UserRepository mockUserRepository() {
        return Mockito.mock(UserRepository.class);
    }

    @Bean(name = "validator")
    public LocalValidatorFactoryBean validator() {
        return new LocalValidatorFactoryBean();
    }
}
