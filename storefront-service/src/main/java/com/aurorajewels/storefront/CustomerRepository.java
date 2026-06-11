package com.aurorajewels.storefront;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
  Optional<Customer> findByEmail(String email);
  Optional<Customer> findByEmailIgnoreCase(String email);
  boolean existsByEmail(String email);
  boolean existsByEmailIgnoreCase(String email);
}
