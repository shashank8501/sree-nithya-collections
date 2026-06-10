package com.aurorajewels.order;

import java.util.ArrayList;
import java.util.List;

public class CreateOrderRequest {
  private String customerName;
  private String email;
  private String paymentMethod;
  private int total;
  private List<OrderItemRequest> items = new ArrayList<>();

  public String getCustomerName() { return customerName; }
  public void setCustomerName(String customerName) { this.customerName = customerName; }
  public String getEmail() { return email; }
  public void setEmail(String email) { this.email = email; }
  public String getPaymentMethod() { return paymentMethod; }
  public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
  public int getTotal() { return total; }
  public void setTotal(int total) { this.total = total; }
  public List<OrderItemRequest> getItems() { return items; }
  public void setItems(List<OrderItemRequest> items) { this.items = items; }
}
