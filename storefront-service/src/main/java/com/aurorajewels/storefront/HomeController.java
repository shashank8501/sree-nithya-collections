package com.aurorajewels.storefront;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
  @GetMapping("/")
  public String home() {
    return "index";
  }

  @GetMapping("/admin")
  public String admin() {
    return "admin";
  }

  @GetMapping("/admin/orders")
  public String adminOrders() {
    return "admin-orders";
  }

  @GetMapping("/login")
  public String login() {
    return "login";
  }
}
