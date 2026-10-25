package uk.co.yottr.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.co.yottr.conversion.DateFormatter;

/*
 * Copyright (c) 2015. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

/**
 * Most of the old hand-written MVC set-up (Thymeleaf, message source, paging defaults, static resources)
 * is now Spring Boot auto-configuration driven by application.properties.
 */
@Configuration
public class AppConfig implements WebMvcConfigurer {

    private final MessageSource messageSource;

    public AppConfig(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatter(new DateFormatter(messageSource));
    }

//    @Bean TODO figure out how to wire up a custom message interpolator!
//    public MessageInterpolator messageInterpolator() {
//        return new CustomMessageInterpolator(MessageSourceResourceBundleLocator )
//    }
}
