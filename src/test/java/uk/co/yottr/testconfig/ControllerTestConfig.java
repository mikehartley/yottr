package uk.co.yottr.testconfig;

import nz.net.ultraq.thymeleaf.LayoutDialect;
import org.mockito.Mockito;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.Ordered;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.thymeleaf.extras.springsecurity3.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;
import uk.co.yottr.repository.UserRepository;
import uk.co.yottr.service.BoatService;
import uk.co.yottr.service.UserService;

/*
 * Copyright (c) 2014. Mike Hartley Solutions Ltd
 * All rights reserved.
 */

@Configuration
@EnableWebMvc
@EnableSpringDataWebSupport
@ComponentScan(value = { "uk.co.yottr.controller" })
public class ControllerTestConfig {

    @Bean
    public PageableHandlerMethodArgumentResolver modifyPageableHandlerMethodArgumentResolver(PageableHandlerMethodArgumentResolver resolver) {
        resolver.setFallbackPageable(new PageRequest(0, 5));
        return resolver;
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("Messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setCacheSeconds(-1);
        return messageSource;
    }

    @Bean
    public ViewResolver viewResolver() {
        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
        viewResolver.setTemplateEngine(templateEngine());
        viewResolver.setCharacterEncoding("UTF-8");

        // all message/* views will not be handled by this resolver;
        viewResolver.setExcludedViewNames(new String[]{"message/*"});

        viewResolver.setOrder(Ordered.HIGHEST_PRECEDENCE);
        viewResolver.setCache(false);
        return viewResolver;
    }

    @Bean
    public SpringTemplateEngine templateEngine() {
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver());

        engine.addDialect(new SpringSecurityDialect());
        engine.addDialect(new LayoutDialect());

        return engine;
    }

    @Bean
    public TemplateResolver templateResolver() {
        ServletContextTemplateResolver resolver = new ServletContextTemplateResolver();
        resolver.setPrefix("/WEB-INF/thymeleaf/");
        resolver.setSuffix(".xhtml");
        resolver.setTemplateMode("HTML5");
        resolver.setCacheable(false);
        return resolver;
    }

    @Bean
    public UserRepository mockUserRepository() {
        return Mockito.mock(UserRepository.class);
    }

    @Bean
    public BoatService mockBoatService() {
        return Mockito.mock(BoatService.class);
    }

    @Bean
    public UserService mockUserService() {
        return Mockito.mock(UserService.class);
    }
 }