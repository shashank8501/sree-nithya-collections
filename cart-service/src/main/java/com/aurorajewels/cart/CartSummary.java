package com.aurorajewels.cart;

import java.util.List;

public class CartSummary {
  private List<CartItem> items;
  private int total;
  private int count;

  public CartSummary(List<CartItem> items) {
    this.items = items;
    this.total = items.stream().mapToInt(item -> item.getPrice() * item.getQuantity()).sum();
    this.count = items.stream().mapToInt(CartItem::getQuantity).sum();
  }

  public List<CartItem> getItems() { return items; }
  public int getTotal() { return total; }
  public int getCount() { return count; }
}
