CREATE DATABASE IF NOT EXISTS sree_nithya_products;
CREATE DATABASE IF NOT EXISTS sree_nithya_orders;
CREATE DATABASE IF NOT EXISTS sree_nithya_storefront;
GRANT ALL PRIVILEGES ON sree_nithya_products.* TO 'aurora'@'%';
GRANT ALL PRIVILEGES ON sree_nithya_orders.* TO 'aurora'@'%';
GRANT ALL PRIVILEGES ON sree_nithya_storefront.* TO 'aurora'@'%';
FLUSH PRIVILEGES;
