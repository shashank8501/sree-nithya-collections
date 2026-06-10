package com.aurorajewels.cart;

public class CartItem {
  private Long productId;
  private String name;
  private int price;
  private String image;
  private int quantity;

  public CartItem() {
  }

  public CartItem(ProductDto product) {
    this.productId = product.getId();
    this.name = product.getName();
    this.price = product.getPrice();
    this.image = product.getImage();
    this.quantity = 1;
  }

  public Long getProductId() { return productId; }
  public String getName() { return name; }
  public int getPrice() { return price; }
  public String getImage() { return image; }
  public int getQuantity() { return quantity; }
  public void increment() { this.quantity += 1; }
}
