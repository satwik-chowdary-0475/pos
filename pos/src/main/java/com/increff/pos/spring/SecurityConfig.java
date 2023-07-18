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

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http//
                // Match only these URLs
                .requestMatchers()//
                .antMatchers("/api/**")//
                .antMatchers("/home","/brands/**","/reports/**","/products/**","/inventory/**","/orders/**")
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
                .antMatchers("/reports/**").hasAuthority("SUPERVISOR")
                .antMatchers("/SUPERVISOR/**").hasAuthority("SUPERVISOR")
                .antMatchers("/home", "/brands/**", "/orders/**", "/product/**", "/inventory/**").hasAnyAuthority("SUPERVISOR", "OPERATOR")
                .anyRequest().authenticated()//
                .and()
                .formLogin()
                    .loginPage("/login")
                    .loginProcessingUrl("/session/login")
                    .defaultSuccessUrl("/home")
                    .failureUrl("/site/login?error=true")
                    .permitAll()
                .and()
                .logout()
                    .logoutUrl("/session/logout")
                    .logoutSuccessUrl("/")
                    .permitAll()
                // Ignore CSRF and CORS
                .and().csrf().disable().cors().disable();
        logger.info("Configuration complete");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
//        web.ignoring().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**","/api/**","/ui/**");
        web.ignoring().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources", "/configuration/security", "/swagger-ui.html", "/webjars/**");
    }

}