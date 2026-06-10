const productGrid = document.querySelector("#productGrid");
const filters = document.querySelectorAll(".filter");
const searchInput = document.querySelector("#searchInput");
const cartButton = document.querySelector("#cartButton");
const closeCart = document.querySelector("#closeCart");
const cartDrawer = document.querySelector("#cartDrawer");
const cartItems = document.querySelector("#cartItems");
const cartCount = document.querySelector("#cartCount");
const cartTotal = document.querySelector("#cartTotal");
const checkoutForm = document.querySelector("#checkoutForm");
const orderMessage = document.querySelector("#orderMessage");

const sessionId = localStorage.getItem("sreeNithyaSessionId") || crypto.randomUUID();
localStorage.setItem("sreeNithyaSessionId", sessionId);

let activeFilter = "all";
let cart = { items: [], total: 0, count: 0 };

const money = value => `$${Number(value).toLocaleString("en-US")}`;

async function loadProducts() {
  const params = new URLSearchParams({ category: activeFilter, search: searchInput.value.trim() });
  const response = await fetch(`/shop-api/products?${params}`);
  const products = await response.json();
  productGrid.innerHTML = products.map(product => `
    <article class="product-card">
      <div class="product-media"><img src="${product.image}" alt="${product.name}" loading="lazy"></div>
      <div class="product-info">
        <div class="product-meta"><strong>${money(product.price)}</strong></div>
        <h3>${product.name}</h3>
        <p>${product.description}</p>
        <button type="button" data-add="${product.id}">Add to cart</button>
      </div>
    </article>
  `).join("") || `<p class="empty-cart">No pieces matched your search.</p>`;
}

async function loadCart() {
  const response = await fetch(`/shop-api/carts/${sessionId}`);
  cart = await response.json();
  renderCart();
}

function renderCart() {
  cartCount.textContent = cart.count || 0;
  cartTotal.textContent = money(cart.total || 0);

  if (!cart.items || !cart.items.length) {
    cartItems.innerHTML = `<p class="empty-cart">Your cart is ready for something beautiful.</p>`;
    return;
  }

  cartItems.innerHTML = cart.items.map(item => `
    <div class="cart-row">
      <img src="${item.image}" alt="${item.name}">
      <div>
        <h3>${item.name}</h3>
        <p>${item.quantity} x ${money(item.price)}</p>
      </div>
      <button class="remove-btn" type="button" data-remove="${item.productId}">Remove</button>
    </div>
  `).join("");
}

async function addToCart(productId) {
  const response = await fetch(`/shop-api/carts/${sessionId}/items`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ productId })
  });
  cart = await response.json();
  renderCart();
  openCart();
}

async function removeFromCart(productId) {
  await fetch(`/shop-api/carts/${sessionId}/items/${productId}`, { method: "DELETE" });
  await loadCart();
}

function openCart() {
  cartDrawer.classList.add("open");
  cartDrawer.setAttribute("aria-hidden", "false");
}

function hideCart() {
  cartDrawer.classList.remove("open");
  cartDrawer.setAttribute("aria-hidden", "true");
}

filters.forEach(button => {
  button.addEventListener("click", () => {
    filters.forEach(item => item.classList.remove("active"));
    button.classList.add("active");
    activeFilter = button.dataset.filter;
    loadProducts();
  });
});

productGrid.addEventListener("click", event => {
  const addButton = event.target.closest("[data-add]");
  if (addButton) addToCart(Number(addButton.dataset.add));
});

cartItems.addEventListener("click", event => {
  const removeButton = event.target.closest("[data-remove]");
  if (removeButton) removeFromCart(Number(removeButton.dataset.remove));
});

checkoutForm.addEventListener("submit", async event => {
  event.preventDefault();
  if (!cart.total) {
    orderMessage.textContent = "Add an item before checkout.";
    return;
  }
  const form = new FormData(checkoutForm);
  const response = await fetch("/shop-api/orders", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({
      customerName: form.get("customerName"),
      email: form.get("email"),
      paymentMethod: form.get("paymentMethod"),
      total: cart.total,
      items: cart.items.map(item => ({
        productId: item.productId,
        name: item.name,
        price: item.price,
        quantity: item.quantity
      }))
    })
  });
  const order = await response.json();
  orderMessage.textContent = `Order #${order.id} confirmed. Your cart is cleared.`;
  await fetch(`/shop-api/carts/${sessionId}`, { method: "DELETE" });
  checkoutForm.reset();
  await loadCart();
  setTimeout(() => {
    hideCart();
    orderMessage.textContent = "";
  }, 1800);
});

searchInput.addEventListener("input", loadProducts);
cartButton.addEventListener("click", openCart);
closeCart.addEventListener("click", hideCart);
cartDrawer.addEventListener("click", event => {
  if (event.target === cartDrawer) hideCart();
});
document.addEventListener("keydown", event => {
  if (event.key === "Escape") hideCart();
});

loadProducts();
loadCart();
