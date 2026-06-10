package com.aurorajewels.order;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OrderController {
  private final OrderRepository orderRepository;
  private final String internalApiToken;

  public OrderController(OrderRepository orderRepository,
      @Value("${internal.api.token}") String internalApiToken) {
    this.orderRepository = orderRepository;
    this.internalApiToken = internalApiToken;
  }

  @GetMapping("/api/orders")
  public ResponseEntity<List<Order>> orders(@RequestHeader(value = "X-Internal-Token", required = false) String token) {
    if (!isAuthorized(token)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    return ResponseEntity.ok(orderRepository.findAll());
  }

  @PostMapping("/api/orders")
  public ResponseEntity<Order> create(@RequestBody CreateOrderRequest request,
                                      @RequestHeader(value = "X-Internal-Token", required = false) String token) {
    if (!isAuthorized(token)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    if (request.getItems() == null || request.getItems().isEmpty()) {
      return ResponseEntity.badRequest().build();
    }
    return ResponseEntity.ok(orderRepository.save(new Order(request)));
  }

  private boolean isAuthorized(String token) {
    return internalApiToken.equals(token);
  }
}
