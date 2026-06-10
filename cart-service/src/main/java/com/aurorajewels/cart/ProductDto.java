package com.aurorajewels.cart;

public class ProductDto {
  private Long id;
  private String name;
  private String category;
  private int price;
  private String metal;
  private String image;
  private String description;

  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getCategory() { return category; }
  public void setCategory(String category) { this.category = category; }
  public int getPrice() { return price; }
  public void setPrice(int price) { this.price = price; }
  public String getMetal() { return metal; }
  public void setMetal(String metal) { this.metal = metal; }
  public String getImage() { return image; }
  public void setImage(String image) { this.image = image; }
  public String getDescription() { return description; }
  public void setDescription(String description) { this.description = description; }
}
