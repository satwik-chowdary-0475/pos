package com.increff.pos.spring;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static Logger logger = Logger.getLogger(SecurityConfig.class);
    //TODO: add UI authorities commented for now
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http//
                // Match only these URLs
                .requestMatchers()//
                .antMatchers("/api/**")//
                .antMatchers("/ui/**")//
                .and().authorizeRequests()//
                .antMatchers(HttpMethod.GET, "/api/brand/**", "/api/product/**", "/api/inventory/**")
                .hasAnyAuthority("supervisor", "operator")
                .antMatchers(HttpMethod.POST, "/api/brand/**", "/api/product/**", "/api/inventory/**", "/api/order/**")
                .hasAuthority("supervisor")
                .antMatchers(HttpMethod.PUT, "/api/brand/**", "/api/product/**", "/api/inventory/**", "/api/order/**")
                .hasAuthority("supervisor")
                .antMatchers(HttpMethod.DELETE, "/api/brand/**", "/api/product/**", "/api/inventory/**", "/api/order/**")
                .hasAuthority("supervisor")
                .antMatchers("/ui/supervisor/**").hasAuthority("supervisor")
                .antMatchers("/ui/**").hasAnyAuthority("supervisor", "operator")
                // Ignore CSRF and CORS
                .and().csrf().disable().cors().disable();
        logger.info("Configuration complete");
    }

    // For enabling swagger
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**");
    }

}