package uk.co.yottr.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.co.yottr.model.User;
import uk.co.yottr.repository.UserRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Component
public class UsernameAvailableValidator implements ConstraintValidator<UsernameAvailable, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void initialize(UsernameAvailable param) {
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext ctx) {

        if (userRepository == null) {
            return true;
        }

        final User user = userRepository.findByUsername(username);
        return user == null;
    }
}
