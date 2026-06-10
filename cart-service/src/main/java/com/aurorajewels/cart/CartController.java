package com.aurorajewels.cart;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class CartController {
  private final RestTemplate restTemplate;
  private final String productServiceUrl;
  private final Map<String, List<CartItem>> carts = new ConcurrentHashMap<>();

  public CartController(RestTemplate restTemplate, @Value("${product.service.url}") String productServiceUrl) {
    this.restTemplate = restTemplate;
    this.productServiceUrl = productServiceUrl;
  }

  @GetMapping("/api/carts/{sessionId}")
  public CartSummary cart(@PathVariable String sessionId) {
    return new CartSummary(carts.getOrDefault(sessionId, new ArrayList<>()));
  }

  @PostMapping("/api/carts/{sessionId}/items")
  public ResponseEntity<CartSummary> add(@PathVariable String sessionId, @RequestBody AddCartItemRequest request) {
    ProductDto product = restTemplate.getForObject(productServiceUrl + "/api/products/" + request.getProductId(), ProductDto.class);
    if (product == null) {
      return ResponseEntity.notFound().build();
    }
    List<CartItem> items = carts.computeIfAbsent(sessionId, key -> new ArrayList<>());
    items.stream()
        .filter(item -> item.getProductId().equals(product.getId()))
        .findFirst()
        .ifPresentOrElse(CartItem::increment, () -> items.add(new CartItem(product)));
    return ResponseEntity.ok(new CartSummary(items));
  }

  @DeleteMapping("/api/carts/{sessionId}/items/{productId}")
  public CartSummary remove(@PathVariable String sessionId, @PathVariable Long productId) {
    List<CartItem> items = carts.computeIfAbsent(sessionId, key -> new ArrayList<>());
    items.removeIf(item -> item.getProductId().equals(productId));
    return new CartSummary(items);
  }

  @DeleteMapping("/api/carts/{sessionId}")
  public CartSummary clear(@PathVariable String sessionId) {
    carts.remove(sessionId);
    return new CartSummary(new ArrayList<>());
  }
}
