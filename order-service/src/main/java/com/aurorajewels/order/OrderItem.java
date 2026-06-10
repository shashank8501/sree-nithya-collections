package com.aurorajewels.order;

import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class OrderItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private Long productId;
  private String name;
  private int price;
  private int quantity;

  @JsonIgnore
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private Order order;

  public OrderItem() {
  }

  public OrderItem(OrderItemRequest request) {
    this.productId = request.getProductId();
    this.name = request.getName();
    this.price = request.getPrice();
    this.quantity = request.getQuantity();
  }

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public Long getProductId() { return productId; }
  public void setProductId(Long productId) { this.productId = productId; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public int getPrice() { return price; }
  public void setPrice(int price) { this.price = price; }
  public int getQuantity() { return quantity; }
  public void setQuantity(int quantity) { this.quantity = quantity; }
  public Order getOrder() { return order; }
  public void setOrder(Order order) { this.order = order; }
}
