package uk.co.yottr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uk.co.yottr.controller.MemberSides;
import uk.co.yottr.controller.MenuItem;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final MenuItem menuItem;
    private final MemberSides memberSides;

    public WebConfig(MenuItem menuItem, MemberSides memberSides) {
        this.menuItem = menuItem;
        this.memberSides = memberSides;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // pages only: static files have no menu
        registry.addInterceptor(menuItem).excludePathPatterns("/resources/**", "/webjars/**", "/actuator/**");
        registry.addInterceptor(memberSides).excludePathPatterns("/resources/**", "/webjars/**", "/actuator/**");
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
