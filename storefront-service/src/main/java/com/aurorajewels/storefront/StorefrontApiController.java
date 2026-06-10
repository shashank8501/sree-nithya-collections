package com.aurorajewels.storefront;

import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class StorefrontApiController {
  private final RestTemplate restTemplate;
  private final String productServiceUrl;
  private final String cartServiceUrl;
  private final String orderServiceUrl;
  private final String internalApiToken;

  public StorefrontApiController(RestTemplate restTemplate,
      @Value("${product.service.url}") String productServiceUrl,
      @Value("${cart.service.url}") String cartServiceUrl,
      @Value("${order.service.url}") String orderServiceUrl,
      @Value("${internal.api.token}") String internalApiToken) {
    this.restTemplate = restTemplate;
    this.productServiceUrl = productServiceUrl;
    this.cartServiceUrl = cartServiceUrl;
    this.orderServiceUrl = orderServiceUrl;
    this.internalApiToken = internalApiToken;
  }

  @GetMapping("/shop-api/products")
  public Object products(@RequestParam(defaultValue = "all") String category,
                         @RequestParam(defaultValue = "") String search) {
    return restTemplate.getForObject(productServiceUrl + "/api/products?category={category}&search={search}",
        Object.class, category, search);
  }

  @PostMapping("/shop-api/products")
  public Object createProduct(@RequestBody Map<String, Object> request) {
    return restTemplate.postForObject(productServiceUrl + "/api/products", withInternalToken(request), Object.class);
  }

  @PutMapping("/shop-api/products/{id}")
  public void updateProduct(@PathVariable Long id, @RequestBody Map<String, Object> request) {
    restTemplate.exchange(productServiceUrl + "/api/products/" + id, HttpMethod.PUT, withInternalToken(request), Void.class);
  }

  @DeleteMapping("/shop-api/products/{id}")
  public void deleteProduct(@PathVariable Long id) {
    restTemplate.exchange(productServiceUrl + "/api/products/" + id, HttpMethod.DELETE, withInternalToken(null), Void.class);
  }

  @GetMapping("/shop-api/carts/{sessionId}")
  public Object cart(@PathVariable String sessionId) {
    return restTemplate.getForObject(cartServiceUrl + "/api/carts/" + sessionId, Object.class);
  }

  @PostMapping("/shop-api/carts/{sessionId}/items")
  public Object addCartItem(@PathVariable String sessionId, @RequestBody Map<String, Long> request) {
    return restTemplate.postForObject(cartServiceUrl + "/api/carts/" + sessionId + "/items", request, Object.class);
  }

  @DeleteMapping("/shop-api/carts/{sessionId}/items/{productId}")
  public void removeCartItem(@PathVariable String sessionId, @PathVariable Long productId) {
    restTemplate.delete(cartServiceUrl + "/api/carts/" + sessionId + "/items/" + productId);
  }

  @DeleteMapping("/shop-api/carts/{sessionId}")
  public void clearCart(@PathVariable String sessionId) {
    restTemplate.delete(cartServiceUrl + "/api/carts/" + sessionId);
  }

  @PostMapping("/shop-api/orders")
  public ResponseEntity<Object> createOrder(@RequestBody Map<String, Object> request) {
    return restTemplate.postForEntity(orderServiceUrl + "/api/orders", withInternalToken(request), Object.class);
  }

  @GetMapping("/shop-api/admin/orders")
  public Object orders() {
    return restTemplate.exchange(orderServiceUrl + "/api/orders", HttpMethod.GET, withInternalToken(null), Object.class).getBody();
  }

  private HttpEntity<Object> withInternalToken(Object body) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Internal-Token", internalApiToken);
    return new HttpEntity<>(body, headers);
  }
}
