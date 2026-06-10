const orderList = document.querySelector("#orderList");
const refreshOrders = document.querySelector("#refreshOrders");

const money = value => `$${Number(value).toLocaleString("en-US")}`;

function formatDate(value) {
  if (!value) return "";
  return new Date(value).toLocaleString();
}

async function loadOrders() {
  const response = await fetch("/shop-api/admin/orders");
  const orders = await response.json();
  orderList.innerHTML = orders.slice().reverse().map(order => `
    <article class="order-row">
      <div class="order-row-head">
        <div>
          <h3>Order #${order.id}</h3>
          <p>${order.customerName} | ${order.email}</p>
          <p>${order.paymentMethod || "Payment"} | ${order.paymentStatus || "PAID"}</p>
          <p>${formatDate(order.createdAt)}</p>
        </div>
        <div class="order-total">
          <strong>${money(order.total)}</strong>
          <span>${order.status}</span>
        </div>
      </div>
      <div class="order-items">
        ${(order.items || []).map(item => `
          <div>
            <span>${item.name}</span>
            <strong>${item.quantity} x ${money(item.price)}</strong>
          </div>
        `).join("")}
      </div>
    </article>
  `).join("") || `<p class="empty-cart">No orders placed yet.</p>`;
}

refreshOrders.addEventListener("click", loadOrders);
loadOrders();
