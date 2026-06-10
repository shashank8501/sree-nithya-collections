const productForm = document.querySelector("#productForm");
const productList = document.querySelector("#adminProductList");
const adminMessage = document.querySelector("#adminMessage");
const formTitle = document.querySelector("#formTitle");
const resetFormButton = document.querySelector("#resetForm");
const refreshProductsButton = document.querySelector("#refreshProducts");
const imageUpload = document.querySelector("#imageUpload");

let products = [];

const money = value => `$${Number(value).toLocaleString("en-US")}`;

async function loadProducts(attempt = 1) {
  productList.innerHTML = `<p class="empty-cart">Loading products...</p>`;
  try {
    const response = await fetch("/shop-api/products", { cache: "no-store" });
    if (!response.ok) {
      throw new Error(`Product request failed: ${response.status}`);
    }
    products = await response.json();
    renderProducts();
  } catch (error) {
    if (attempt < 3) {
      setTimeout(() => loadProducts(attempt + 1), 600);
      return;
    }
    productList.innerHTML = `<p class="empty-cart">Products did not load. Click Refresh.</p>`;
  }
}

function renderProducts() {
  productList.innerHTML = products.map(product => `
    <article class="admin-product-row">
      <img src="${product.image}" alt="${product.name}">
      <div>
        <h3>${product.name}</h3>
        <p>${product.category} | ${money(product.price)}</p>
        <p>${product.description}</p>
      </div>
      <div class="admin-product-actions">
        <button class="plain-button" type="button" data-edit="${product.id}">Edit</button>
        <button class="danger-button" type="button" data-delete="${product.id}">Delete</button>
      </div>
    </article>
  `).join("") || `<p class="empty-cart">No products found.</p>`;
}

function getPayload() {
  const form = new FormData(productForm);
  return {
    name: form.get("name").trim(),
    category: form.get("category"),
    price: Number(form.get("price")),
    metal: "",
    image: form.get("image").trim(),
    description: form.get("description").trim()
  };
}

function resetForm() {
  productForm.reset();
  productForm.elements.id.value = "";
  formTitle.textContent = "Add Product";
  adminMessage.textContent = "";
}

function editProduct(id) {
  const product = products.find(item => item.id === id);
  if (!product) return;
  productForm.elements.id.value = product.id;
  productForm.elements.name.value = product.name;
  productForm.elements.category.value = product.category;
  productForm.elements.price.value = product.price;
  productForm.elements.image.value = product.image;
  productForm.elements.description.value = product.description;
  formTitle.textContent = `Edit ${product.name}`;
  window.scrollTo({ top: 0, behavior: "smooth" });
}

async function saveProduct(event) {
  event.preventDefault();
  const id = productForm.elements.id.value;
  const payload = getPayload();

  if (id) {
    await fetch(`/shop-api/products/${id}`, {
      method: "PUT",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    adminMessage.textContent = "Product updated.";
  } else {
    await fetch("/shop-api/products", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });
    adminMessage.textContent = "Product added.";
  }

  resetForm();
  await loadProducts();
}

async function deleteProduct(id) {
  await fetch(`/shop-api/products/${id}`, { method: "DELETE" });
  adminMessage.textContent = "Product deleted.";
  await loadProducts();
}

async function uploadImage() {
  if (!imageUpload.files.length) return;
  const form = new FormData();
  form.append("file", imageUpload.files[0]);
  adminMessage.textContent = "Uploading image...";
  const response = await fetch("/shop-api/admin/uploads", {
    method: "POST",
    body: form
  });
  if (!response.ok) {
    adminMessage.textContent = "Image upload failed.";
    return;
  }
  const result = await response.json();
  productForm.elements.image.value = result.path;
  adminMessage.textContent = "Image uploaded.";
}

productForm.addEventListener("submit", saveProduct);
resetFormButton.addEventListener("click", resetForm);
refreshProductsButton.addEventListener("click", loadProducts);
imageUpload.addEventListener("change", uploadImage);
window.addEventListener("pageshow", () => loadProducts());
document.addEventListener("visibilitychange", () => {
  if (!document.hidden) loadProducts();
});

productList.addEventListener("click", event => {
  const editButton = event.target.closest("[data-edit]");
  const deleteButton = event.target.closest("[data-delete]");
  if (editButton) editProduct(Number(editButton.dataset.edit));
  if (deleteButton) deleteProduct(Number(deleteButton.dataset.delete));
});

loadProducts();
