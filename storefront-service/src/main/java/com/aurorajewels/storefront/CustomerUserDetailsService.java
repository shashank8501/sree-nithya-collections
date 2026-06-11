package com.aurorajewels.storefront;

import java.util.Collections;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class CustomerUserDetailsService implements UserDetailsService {
  private final CustomerRepository customerRepository;
  private final PasswordEncoder passwordEncoder;
  private final String adminUsername;
  private final String adminPassword;

  public CustomerUserDetailsService(CustomerRepository customerRepository,
      PasswordEncoder passwordEncoder,
      @Value("${admin.username}") String adminUsername,
      @Value("${admin.password}") String adminPassword) {
    this.customerRepository = customerRepository;
    this.passwordEncoder = passwordEncoder;
    this.adminUsername = adminUsername;
    this.adminPassword = adminPassword;
  }

  @Override
  public UserDetails loadUserByUsername(String username) {
    if (adminUsername.equals(username)) {
      return User.withUsername(adminUsername)
          .password(passwordEncoder.encode(adminPassword))
          .roles("ADMIN")
          .build();
    }

    return customerRepository.findByEmailIgnoreCase(username.trim())
        .map(customer -> new User(customer.getEmail(), customer.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER"))))
        .orElseThrow(() -> new UsernameNotFoundException(username));
  }
}
