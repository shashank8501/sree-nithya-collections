$root = Split-Path -Parent $MyInvocation.MyCommand.Path
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root'; mvn -pl product-service spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root'; mvn -pl cart-service spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root'; mvn -pl order-service spring-boot:run"
Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$root'; mvn -pl storefront-service spring-boot:run"
