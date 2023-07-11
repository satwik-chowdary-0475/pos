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
                .antMatchers(HttpMethod.GET, "/api/brands/**", "/api/products/**", "/api/inventory/**")
                .hasAnyAuthority("SUPERVISOR", "OPERATOR")
                .antMatchers(HttpMethod.POST, "/api/brands/**", "/api/products/**", "/api/inventory/**")
                .hasAuthority("SUPERVISOR")
                .antMatchers(HttpMethod.PUT, "/api/brands/**", "/api/products/**", "/api/inventory/**")
                .hasAuthority("SUPERVISOR")
                .antMatchers(HttpMethod.DELETE, "/api/brands/**", "/api/products/**", "/api/inventory/**")
                .hasAuthority("SUPERVISOR")
                .antMatchers("/api/orders/**").hasAnyAuthority("SUPERVISOR","OPERATOR")
                .antMatchers("/api/reports/**").hasAuthority("SUPERVISOR")
                .antMatchers("/ui/reports/**").hasAuthority("SUPERVISOR")
                .antMatchers("/ui/SUPERVISOR/**").hasAuthority("SUPERVISOR")
                .antMatchers("/ui/**").hasAnyAuthority("SUPERVISOR", "OPERATOR")
                // Ignore CSRF and CORS
                .and().csrf().disable().cors().disable().exceptionHandling().accessDeniedPage("/site/error-403");
        logger.info("Configuration complete");
    }

    // TODO: SHOULD MODIFY THIS, FOR NOW JUST TESTING PURPOSE
    @Override
    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**","/api/**","/ui/**");
        web.ignoring().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**");
    }

}