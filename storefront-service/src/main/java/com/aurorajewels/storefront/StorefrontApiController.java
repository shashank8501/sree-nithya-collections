package com.aurorajewels.storefront;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
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
  private final boolean renderSingleService;
  private final List<Map<String, Object>> demoProducts = new ArrayList<>();
  private final Map<String, List<Map<String, Object>>> demoCarts = new ConcurrentHashMap<>();
  private final List<Map<String, Object>> demoOrders = new ArrayList<>();
  private final AtomicLong demoProductIds = new AtomicLong(5);
  private final AtomicLong demoOrderIds = new AtomicLong(1000);

  public StorefrontApiController(RestTemplate restTemplate,
      @Value("${product.service.url}") String productServiceUrl,
      @Value("${cart.service.url}") String cartServiceUrl,
      @Value("${order.service.url}") String orderServiceUrl,
      @Value("${internal.api.token}") String internalApiToken,
      @Value("${render.single-service:false}") boolean renderSingleService) {
    this.restTemplate = restTemplate;
    this.productServiceUrl = productServiceUrl;
    this.cartServiceUrl = cartServiceUrl;
    this.orderServiceUrl = orderServiceUrl;
    this.internalApiToken = internalApiToken;
    this.renderSingleService = renderSingleService;
    seedDemoProducts();
  }

  @GetMapping("/shop-api/products")
  public Object products(@RequestParam(defaultValue = "all") String category,
                         @RequestParam(defaultValue = "") String search) {
    if (renderSingleService) {
      String normalizedCategory = category.toLowerCase();
      String normalizedSearch = search.toLowerCase();
      return demoProducts.stream()
          .filter(product -> "all".equals(normalizedCategory)
              || normalizedCategory.equals(String.valueOf(product.get("category"))))
          .filter(product -> normalizedSearch.isBlank()
              || String.valueOf(product.get("name")).toLowerCase().contains(normalizedSearch)
              || String.valueOf(product.get("category")).toLowerCase().contains(normalizedSearch))
          .collect(Collectors.toList());
    }
    return restTemplate.getForObject(productServiceUrl + "/api/products?category={category}&search={search}",
        Object.class, category, search);
  }

  @PostMapping("/shop-api/products")
  public Object createProduct(@RequestBody Map<String, Object> request) {
    if (renderSingleService) {
      Map<String, Object> product = new LinkedHashMap<>(request);
      product.put("id", demoProductIds.incrementAndGet());
      demoProducts.add(product);
      return product;
    }
    return restTemplate.postForObject(productServiceUrl + "/api/products", withInternalToken(request), Object.class);
  }

  @PutMapping("/shop-api/products/{id}")
  public void updateProduct(@PathVariable Long id, @RequestBody Map<String, Object> request) {
    if (renderSingleService) {
      demoProducts.stream()
          .filter(product -> id.equals(asLong(product.get("id"))))
          .findFirst()
          .ifPresent(product -> {
            product.putAll(request);
            product.put("id", id);
          });
      return;
    }
    restTemplate.exchange(productServiceUrl + "/api/products/" + id, HttpMethod.PUT, withInternalToken(request), Void.class);
  }

  @DeleteMapping("/shop-api/products/{id}")
  public void deleteProduct(@PathVariable Long id) {
    if (renderSingleService) {
      demoProducts.removeIf(product -> id.equals(asLong(product.get("id"))));
      return;
    }
    restTemplate.exchange(productServiceUrl + "/api/products/" + id, HttpMethod.DELETE, withInternalToken(null), Void.class);
  }

  @GetMapping("/shop-api/carts/{sessionId}")
  public Object cart(@PathVariable String sessionId) {
    if (renderSingleService) {
      return cartSummary(demoCarts.getOrDefault(sessionId, new ArrayList<>()));
    }
    return restTemplate.getForObject(cartServiceUrl + "/api/carts/" + sessionId, Object.class);
  }

  @PostMapping("/shop-api/carts/{sessionId}/items")
  public Object addCartItem(@PathVariable String sessionId, @RequestBody Map<String, Long> request) {
    if (renderSingleService) {
      Long productId = request.get("productId");
      Map<String, Object> product = demoProducts.stream()
          .filter(item -> productId.equals(asLong(item.get("id"))))
          .findFirst()
          .orElse(null);
      if (product == null) {
        return cartSummary(demoCarts.getOrDefault(sessionId, new ArrayList<>()));
      }
      List<Map<String, Object>> items = demoCarts.computeIfAbsent(sessionId, key -> new ArrayList<>());
      Map<String, Object> existing = items.stream()
          .filter(item -> productId.equals(asLong(item.get("productId"))))
          .findFirst()
          .orElse(null);
      if (existing == null) {
        Map<String, Object> item = new LinkedHashMap<>();
        item.put("productId", productId);
        item.put("name", product.get("name"));
        item.put("price", product.get("price"));
        item.put("image", product.get("image"));
        item.put("quantity", 1);
        items.add(item);
      } else {
        existing.put("quantity", asInt(existing.get("quantity")) + 1);
      }
      return cartSummary(items);
    }
    return restTemplate.postForObject(cartServiceUrl + "/api/carts/" + sessionId + "/items", request, Object.class);
  }

  @DeleteMapping("/shop-api/carts/{sessionId}/items/{productId}")
  public void removeCartItem(@PathVariable String sessionId, @PathVariable Long productId) {
    if (renderSingleService) {
      demoCarts.computeIfAbsent(sessionId, key -> new ArrayList<>())
          .removeIf(item -> productId.equals(asLong(item.get("productId"))));
      return;
    }
    restTemplate.delete(cartServiceUrl + "/api/carts/" + sessionId + "/items/" + productId);
  }

  @DeleteMapping("/shop-api/carts/{sessionId}")
  public void clearCart(@PathVariable String sessionId) {
    if (renderSingleService) {
      demoCarts.remove(sessionId);
      return;
    }
    restTemplate.delete(cartServiceUrl + "/api/carts/" + sessionId);
  }

  @PostMapping("/shop-api/orders")
  public ResponseEntity<Object> createOrder(@RequestBody Map<String, Object> request) {
    if (renderSingleService) {
      Map<String, Object> order = new LinkedHashMap<>(request);
      @SuppressWarnings("unchecked")
      List<Map<String, Object>> items = (List<Map<String, Object>>) order.getOrDefault("items", new ArrayList<>());
      int total = items.stream()
          .mapToInt(item -> asInt(item.get("price")) * asInt(item.get("quantity")))
          .sum();
      order.put("id", demoOrderIds.incrementAndGet());
      order.put("total", total);
      order.put("status", "CONFIRMED");
      order.put("paymentStatus", "PAID");
      order.put("createdAt", Instant.now().toString());
      demoOrders.add(order);
      return ResponseEntity.ok(order);
    }
    return restTemplate.postForEntity(orderServiceUrl + "/api/orders", withInternalToken(request), Object.class);
  }

  @GetMapping("/shop-api/admin/orders")
  public Object orders() {
    if (renderSingleService) {
      return demoOrders;
    }
    return restTemplate.exchange(orderServiceUrl + "/api/orders", HttpMethod.GET, withInternalToken(null), Object.class).getBody();
  }

  private HttpEntity<Object> withInternalToken(Object body) {
    HttpHeaders headers = new HttpHeaders();
    headers.set("X-Internal-Token", internalApiToken);
    return new HttpEntity<>(body, headers);
  }

  private Map<String, Object> cartSummary(List<Map<String, Object>> items) {
    int total = items.stream()
        .mapToInt(item -> asInt(item.get("price")) * asInt(item.get("quantity")))
        .sum();
    int count = items.stream()
        .mapToInt(item -> asInt(item.get("quantity")))
        .sum();
    Map<String, Object> summary = new LinkedHashMap<>();
    summary.put("items", items);
    summary.put("total", total);
    summary.put("count", count);
    return summary;
  }

  private void seedDemoProducts() {
    demoProducts.addAll(Arrays.asList(
        product(1L, "Ruby Halo Ring", "rings", 248, "/assets/ruby-ring.svg", "A warm ring with a ruby-tone center stone."),
        product(2L, "Moonlight Solitaire Ring", "rings", 320, "/assets/diamond-ring.svg", "A bright solitaire ring with a clean bridal profile."),
        product(3L, "Aqua Pendant Necklace", "necklaces", 290, "/assets/aqua-necklace.svg", "A pendant necklace with an aqua gemstone drop."),
        product(4L, "Violet Drop Earrings", "earrings", 176, "/assets/violet-earrings.svg", "Colorful drop earrings for evening styling."),
        product(5L, "Emerald Bead Bracelet", "bracelets", 154, "/assets/emerald-bracelet.svg", "A polished bracelet with green accent stones.")
    ));
  }

  private Map<String, Object> product(Long id, String name, String category, int price, String image, String description) {
    Map<String, Object> product = new LinkedHashMap<>();
    product.put("id", id);
    product.put("name", name);
    product.put("category", category);
    product.put("price", price);
    product.put("metal", "");
    product.put("image", image);
    product.put("description", description);
    return product;
  }

  private Long asLong(Object value) {
    return value instanceof Number ? ((Number) value).longValue() : Long.valueOf(String.valueOf(value));
  }

  private int asInt(Object value) {
    return value instanceof Number ? ((Number) value).intValue() : Integer.parseInt(String.valueOf(value));
  }
}
