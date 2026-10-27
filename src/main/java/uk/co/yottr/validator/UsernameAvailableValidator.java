package uk.co.yottr.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.yottr.repository.UserRepository;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

@Component
public class UsernameAvailableValidator implements ConstraintValidator<UsernameAvailable, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(UsernameAvailable param) {
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext ctx) {

        // a missing username is left to @NotNull
        if (userRepository == null || username == null) {
            return true;
        }

        return !userRepository.existsByUsernameIgnoreCase(username);
    }
}
