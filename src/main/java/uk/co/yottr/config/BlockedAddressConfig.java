package uk.co.yottr.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import uk.co.yottr.security.BlockedAddressFilter;
import uk.co.yottr.service.BlockedAddressService;

/**
 * Puts BlockedAddressFilter ahead of every other filter, Spring Security's included.
 */
@Configuration
public class BlockedAddressConfig {

    @Bean
    public FilterRegistrationBean<BlockedAddressFilter> blockedAddressFilter(BlockedAddressService blockedAddressService) {
        final FilterRegistrationBean<BlockedAddressFilter> registration = new FilterRegistrationBean<>(new BlockedAddressFilter(blockedAddressService));
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
