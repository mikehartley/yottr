package uk.co.yottr.validator;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import uk.co.yottr.model.User;
import uk.co.yottr.repository.UserRepository;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

public class UsernameAvailableValidator implements ConstraintValidator<UsernameAvailable, String>, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void initialize(UsernameAvailable param) {
    }

    @Override
    public boolean isValid(String username, ConstraintValidatorContext ctx) {

        if (applicationContext == null) {
            return true;
        }

        UserRepository userRepository = applicationContext.getBean(UserRepository.class);

        final User user = userRepository.findByUsername(username);
        return user == null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
