# Sree Nithya Collections - Project Complete

## Status

The project is complete as a local full-stack Spring Boot microservices jewelry shopping application.

## Completed Features

- Customer-facing jewelry storefront
- Product catalog with search and category filters
- Cart drawer with add/remove items
- Checkout with mock payment method
- Orders saved with customer details, payment status, totals, and line items
- Customer signup/login/account pages
- Admin login
- Admin product add/edit/delete
- Admin image upload for product images
- Admin orders page
- H2 local databases for products, orders, and customer accounts
- MySQL-ready profiles
- Docker Compose setup for MySQL and all services
- Render single-service deployment setup
- Internal token protection between storefront and backend services
- Configurable admin credentials and internal token

## Run Locally

Open four terminals in this folder:

```powershell
mvn -pl product-service spring-boot:run
mvn -pl cart-service spring-boot:run
mvn -pl order-service spring-boot:run
mvn -pl storefront-service spring-boot:run
```

Open:

```text
http://localhost:8080
```

## Main URLs

```text
Storefront:      http://localhost:8080
Signup:          http://localhost:8080/signup
Login:           http://localhost:8080/login
Account:         http://localhost:8080/account
Admin Products:  http://localhost:8080/admin
Admin Orders:    http://localhost:8080/admin/orders
```

Demo admin:

```text
Username: admin
Password: admin123
```

## Build Verification

Final package build passed:

```powershell
mvn -q -DskipTests package
```

## Note

Real payment provider integration is represented by a mock payment flow. To go live, connect Stripe, Razorpay, or another provider with real merchant credentials.
