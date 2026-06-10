# Sree Nithya Collections Microservices

Spring Boot jewelry shopping website with a microservices backend.

## Services

- `product-service` on port `8081`: product catalog and filtering
- `cart-service` on port `8082`: in-memory cart API, calls product service
- `order-service` on port `8083`: order confirmation API with saved order line items
- `storefront-service` on port `8080`: Thymeleaf webpage and API proxy for the browser
- Customer signup/login is handled by `storefront-service`
- Admin product image uploads are saved under `storefront-service/uploads`

## Database

The project uses H2 database files through Spring Data JPA for local development:

- Product database: `product-service/data/product-db`
- Order database: `order-service/data/order-db`
- Customer database: `storefront-service/data/storefront-db`

The product service seeds the jewelry catalog automatically the first time it starts. Orders are stored after checkout.

H2 console is enabled for the database-backed services:

- Product DB console: `http://localhost:8081/h2-console`
- Order DB console: `http://localhost:8083/h2-console`

Use these JDBC URLs in the H2 console:

```text
jdbc:h2:file:./data/product-db
jdbc:h2:file:./data/order-db
```

Username is `sa` and password is blank.

## Run

Open four terminals from this folder:

```powershell
mvn -pl product-service spring-boot:run
mvn -pl cart-service spring-boot:run
mvn -pl order-service spring-boot:run
mvn -pl storefront-service spring-boot:run
```

Then open:

```text
http://localhost:8080
```

Admin product page:

```text
http://localhost:8080/admin
```

The admin page can add, edit, and delete products in the product database. New products appear on the storefront catalog after refresh.

Admin orders page:

```text
http://localhost:8080/admin/orders
```

The orders page shows placed orders with customer details, status, totals, and line items.

Customer pages:

```text
http://localhost:8080/signup
http://localhost:8080/login
http://localhost:8080/account
```

Demo admin login:

```text
Username: admin
Password: admin123
```

You can override demo credentials and the internal service token with environment variables:

```powershell
$env:ADMIN_USERNAME="admin"
$env:ADMIN_PASSWORD="change-me"
$env:INTERNAL_API_TOKEN="change-this-token"
```

## Client Demo Checklist

1. Open `http://localhost:8080`.
2. Show the jewelry homepage and product catalog.
3. Use category filters such as Rings, Necklaces, Earrings, and Bracelets.
4. Search for a product.
5. Add a product to the cart.
6. Enter a demo name and email, then place an order.
7. Show the mock payment method on checkout.
8. Open `http://localhost:8080/signup` and create a customer account.
9. Open `http://localhost:8080/admin`.
10. Add a new product using an existing image path such as `/assets/ruby-ring.svg`, or upload an image.
11. Refresh the storefront and show the new product.
12. Open `http://localhost:8080/admin/orders` and show the saved order details.

Useful demo image paths:

```text
/assets/ruby-ring.svg
/assets/diamond-ring.svg
/assets/aqua-necklace.svg
/assets/violet-earrings.svg
/assets/emerald-bracelet.svg
```

## Build

```powershell
mvn clean package
```

The app uses Java 11 and Spring Boot 2.7.18 to match the installed runtime on this machine.

## MySQL Profile

The database-backed services include a MySQL profile. Set `SPRING_PROFILES_ACTIVE=mysql` and provide database URLs:

```powershell
$env:SPRING_PROFILES_ACTIVE="mysql"
$env:PRODUCT_DB_URL="jdbc:mysql://localhost:3306/sree_nithya_products"
$env:ORDER_DB_URL="jdbc:mysql://localhost:3306/sree_nithya_orders"
$env:STOREFRONT_DB_URL="jdbc:mysql://localhost:3306/sree_nithya_storefront"
```

## Docker Compose

After building jars with `mvn clean package`, run:

```powershell
docker compose up --build
```

Docker Compose starts MySQL and all four services.

## Render Deployment

This project includes a Render Blueprint:

```text
render.yaml
```

Fast deployment steps:

1. Push the `jewelry-microservices` folder to a GitHub repository.
2. In Render, choose **New > Blueprint**.
3. Select the GitHub repository.
4. Render will detect `render.yaml` and create four web services:
   - `sree-nithya-product-service`
   - `sree-nithya-cart-service`
   - `sree-nithya-order-service`
   - `sree-nithya-collections`
5. Set `ADMIN_PASSWORD` when Render asks for it.
6. Deploy.
7. Open the storefront service URL:

```text
https://sree-nithya-collections.onrender.com
```

Render deployment notes:

- The Render free plan may spin down inactive services, so first requests can be slow.
- The included Render profile uses H2 files under `/tmp`, which is fine for a demo but not persistent production storage.
- For production, connect Render PostgreSQL or an external MySQL database and update the service environment variables.
- Change `INTERNAL_API_TOKEN` from `demo-internal-token` before real public use.

## Production Notes

- Replace H2 with MySQL or PostgreSQL before deployment.
- Keep `ADMIN_PASSWORD` and `INTERNAL_API_TOKEN` outside source code.
- Enable stronger CSRF handling before exposing admin write APIs publicly.
- Put services behind a gateway or private network so only the storefront can call internal service ports.
- Replace the demo payment method with Stripe, Razorpay, or another real payment provider when credentials are available.
