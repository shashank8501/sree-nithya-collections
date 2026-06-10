package com.aurorajewels.product;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ProductController {
  private final ProductRepository productRepository;
  private final String internalApiToken;

  public ProductController(ProductRepository productRepository,
      @Value("${internal.api.token}") String internalApiToken) {
    this.productRepository = productRepository;
    this.internalApiToken = internalApiToken;
  }

  @GetMapping("/api/products")
  public List<Product> products(@RequestParam(defaultValue = "all") String category,
                                @RequestParam(defaultValue = "") String search) {
    String normalizedCategory = category.toLowerCase(Locale.ROOT);
    String normalizedSearch = search.toLowerCase(Locale.ROOT);
    List<Product> products = "all".equals(normalizedCategory)
        ? productRepository.findAll()
        : productRepository.findByCategoryIgnoreCase(normalizedCategory);

    return products.stream()
        .filter(product -> normalizedSearch.isBlank()
            || product.getName().toLowerCase(Locale.ROOT).contains(normalizedSearch)
            || product.getMetal().toLowerCase(Locale.ROOT).contains(normalizedSearch)
            || product.getCategory().toLowerCase(Locale.ROOT).contains(normalizedSearch))
        .collect(Collectors.toList());
  }

  @GetMapping("/api/products/{id}")
  public ResponseEntity<Product> product(@PathVariable Long id) {
    return productRepository.findById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping("/api/products")
  public ResponseEntity<Product> create(@RequestBody Product product,
                                        @RequestHeader(value = "X-Internal-Token", required = false) String token) {
    if (!isAuthorized(token)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    Long nextId = productRepository.findAll().stream()
        .map(Product::getId)
        .max(Long::compareTo)
        .orElse(0L) + 1;
    product.setId(nextId);
    return ResponseEntity.ok(productRepository.save(product));
  }

  @PutMapping("/api/products/{id}")
  public ResponseEntity<Product> update(@PathVariable Long id, @RequestBody Product updatedProduct,
                                        @RequestHeader(value = "X-Internal-Token", required = false) String token) {
    if (!isAuthorized(token)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    return productRepository.findById(id)
        .map(product -> {
          product.setName(updatedProduct.getName());
          product.setCategory(updatedProduct.getCategory());
          product.setPrice(updatedProduct.getPrice());
          product.setMetal(updatedProduct.getMetal());
          product.setImage(updatedProduct.getImage());
          product.setDescription(updatedProduct.getDescription());
          return ResponseEntity.ok(productRepository.save(product));
        })
        .orElse(ResponseEntity.notFound().build());
  }

  @DeleteMapping("/api/products/{id}")
  public ResponseEntity<Void> delete(@PathVariable Long id,
                                     @RequestHeader(value = "X-Internal-Token", required = false) String token) {
    if (!isAuthorized(token)) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    if (!productRepository.existsById(id)) {
      return ResponseEntity.notFound().build();
    }
    productRepository.deleteById(id);
    return ResponseEntity.noContent().build();
  }

  private boolean isAuthorized(String token) {
    return internalApiToken.equals(token);
  }
}
