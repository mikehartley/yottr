package uk.co.yottr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.co.yottr.controller.MenuItem;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final MenuItem menuItem;

    public WebConfig(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // pages only: static files have no menu
        registry.addInterceptor(menuItem).excludePathPatterns("/resources/**", "/webjars/**", "/actuator/**");
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // pages with nothing but their text
        registry.addViewController("/about").setViewName("about");
        registry.addViewController("/faq").setViewName("faq");
        registry.addViewController("/terms").setViewName("terms");
        registry.addViewController("/privacy").setViewName("privacy");
    }
}
