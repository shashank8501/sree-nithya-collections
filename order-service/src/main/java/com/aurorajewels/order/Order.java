package com.aurorajewels.order;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "customer_orders")
public class Order {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String customerName;
  private String email;
  private String paymentMethod;
  private String paymentStatus;
  private int total;
  private String status;
  private Instant createdAt;
  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> items = new ArrayList<>();

  public Order() {
  }

  public Order(CreateOrderRequest request) {
    this.customerName = request.getCustomerName();
    this.email = request.getEmail();
    this.paymentMethod = request.getPaymentMethod();
    this.paymentStatus = "PAID";
    this.status = "CONFIRMED";
    this.createdAt = Instant.now();
    this.items = request.getItems().stream()
        .map(OrderItem::new)
        .collect(Collectors.toList());
    this.items.forEach(item -> item.setOrder(this));
    this.total = this.items.stream()
        .mapToInt(item -> item.getPrice() * item.getQuantity())
        .sum();
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getCustomerName() { return customerName; }
  public void setCustomerName(String customerName) { this.customerName = customerName; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getPaymentMethod() { return paymentMethod; }
  public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
  public String getPaymentStatus() { return paymentStatus; }
  public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }
  public int getTotal() { return total; }
  public void setTotal(int total) { this.total = total; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
  public List<OrderItem> getItems() { return items; }
  public void setItems(List<OrderItem> items) { this.items = items; }
}
