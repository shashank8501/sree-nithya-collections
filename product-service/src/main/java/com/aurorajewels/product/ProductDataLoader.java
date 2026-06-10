package com.aurorajewels.product;

import java.util.Arrays;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ProductDataLoader implements CommandLineRunner {
  private final ProductRepository productRepository;

  public ProductDataLoader(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  @Override
  public void run(String... args) {
    if (productRepository.count() > 0) {
      return;
    }

    productRepository.saveAll(Arrays.asList(
        new Product(1L, "Ruby Halo Ring", "rings", 248, "18k Gold", "/assets/ruby-ring.svg", "A warm gold ring with a ruby-tone center stone."),
        new Product(2L, "Moonlight Solitaire Ring", "rings", 320, "White Gold", "/assets/diamond-ring.svg", "A bright solitaire ring with a clean bridal profile."),
        new Product(3L, "Aqua Pendant Necklace", "necklaces", 290, "Gold Vermeil", "/assets/aqua-necklace.svg", "A pendant necklace with an aqua gemstone drop."),
        new Product(4L, "Violet Drop Earrings", "earrings", 176, "Gold Vermeil", "/assets/violet-earrings.svg", "Colorful drop earrings for evening styling."),
        new Product(5L, "Emerald Bead Bracelet", "bracelets", 154, "Gold Filled", "/assets/emerald-bracelet.svg", "A polished bracelet with green accent stones.")
    ));
  }
}
