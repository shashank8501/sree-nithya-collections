package com.aurorajewels.order;

public class OrderItemRequest {
  private Long productId;
  private String name;
  private int price;
  private int quantity;

  public Long getProductId() { return productId; }
  public void setProductId(Long productId) { this.productId = productId; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public int getPrice() { return price; }
  public void setPrice(int price) { this.price = price; }
  public int getQuantity() { return quantity; }
  public void setQuantity(int quantity) { this.quantity = quantity; }
}
