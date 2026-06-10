package com.aurorajewels.storefront;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CustomerController {
  private final CustomerRepository customerRepository;
  private final PasswordEncoder passwordEncoder;

  public CustomerController(CustomerRepository customerRepository, PasswordEncoder passwordEncoder) {
    this.customerRepository = customerRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @GetMapping("/signup")
  public String signup() {
    return "signup";
  }

  @PostMapping("/signup")
  public String register(@RequestParam String name,
                         @RequestParam String email,
                         @RequestParam String password,
                         Model model) {
    if (customerRepository.existsByEmail(email)) {
      model.addAttribute("error", "Email is already registered.");
      return "signup";
    }
    Customer customer = new Customer();
    customer.setName(name);
    customer.setEmail(email);
    customer.setPassword(passwordEncoder.encode(password));
    customerRepository.save(customer);
    return "redirect:/login?registered";
  }

  @GetMapping("/account")
  public String account() {
    return "account";
  }
}
