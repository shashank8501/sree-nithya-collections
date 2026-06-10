package com.aurorajewels.storefront;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
  @Bean
  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf().disable()
        .authorizeRequests()
        .antMatchers("/", "/login", "/signup", "/css/**", "/js/**", "/assets/**", "/uploads/**").permitAll()
        .antMatchers(HttpMethod.GET, "/shop-api/products").permitAll()
        .antMatchers("/shop-api/carts/**", "/shop-api/orders").permitAll()
        .antMatchers(HttpMethod.POST, "/shop-api/products").hasRole("ADMIN")
        .antMatchers(HttpMethod.PUT, "/shop-api/products/**").hasRole("ADMIN")
        .antMatchers(HttpMethod.DELETE, "/shop-api/products/**").hasRole("ADMIN")
        .antMatchers("/admin/**", "/admin", "/shop-api/products/**", "/shop-api/admin/**").hasRole("ADMIN")
        .antMatchers("/account").hasRole("CUSTOMER")
        .anyRequest().authenticated()
        .and()
        .formLogin()
        .loginPage("/login")
        .defaultSuccessUrl("/", false)
        .permitAll()
        .and()
        .logout()
        .logoutSuccessUrl("/")
        .permitAll();
    return http.build();
  }

  @Bean
  PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
}
