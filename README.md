Wallet Pocket API
Sistema reactivo para la gestión de billeteras financieras, bolsillos y transacciones. Desarrollado bajo los principios de Arquitectura Limpia, utilizando Spring WebFlux para procesamiento asíncrono y persistencia reactiva en R2DBC.

Stack Tecnológico
Lenguaje: Java 21
Framework: Spring Boot 3.2.2 (WebFlux)
Persistencia: R2DBC (PostgreSQL/H2)
Infraestructura: AWS (ECS/Fargate, ECR)
Mensajería: RabbitMQ (Eventos de alto valor con Resilience4j)
Documentación: OpenAPI (Swagger 3)
Gestión de Dependencias: Gradle 8.8

Instalación y Configuración Local
Prerrequisitos
JDK 21 instalado.
Docker & Docker Compose instalado.
AWS CLI configurado (aws configure).

1. Clonar el repositorio
git clone https://github.com/samiralvaradopragma/api-wallet-pocket/tree/feature/wallet-pocket
cd wallet-pocket

2. Levantar la infraestructura local
Para levantar RabbitMQ (con consola en http://localhost:15672):
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management

3.Compilar la aplicación
Desde la raíz del proyecto, compila el módulo de servicio:
./gradlew :applications:app-service:bootJar -x test

4.Despliegue en AWS (ECR + ECS)
  4.1 Autenticación en ECR
   aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 945100415747.dkr.ecr.us-east-2.amazonaws.com
  4.2  Construir imagen Docker
   docker build --network=host -t wallet-pocket-api .
   docker tag wallet-pocket-api:latest 945100415747.dkr.ecr.us-east-2.amazonaws.com/wallet-pocket-api:latest
  4.3 Subir imagen a AWS ECR
  docker push 945100415747.dkr.ecr.us-east-2.amazonaws.com/wallet-pocket-api:latest

5. Terraform
Para actualizar la infraestructura (inyectando variables de RabbitMQ):
terraform init
terraform apply -auto-approve

Documentación de la API (Swagger)
Una vez que la aplicación esté corriendo, accede a la documentación interactiva para probar los endpoints:

URL: http://localhost:8080/webjars/swagger-ui/index.html.

Endpoints Principales:
POST /api/wallets: Crea una nueva cuenta.
POST /api/wallets/{walletId}/pockets: Añade un bolsillo.
POST /api/wallets/{walletId}/pockets/{pocketId}/transactions: Registra transacción (Dispara eventos RabbitMQ si supera el umbral).
PUT /api/wallets/{walletId}/pockets/{pocketId}/transactions/{transactionId}: Modifica monto.
GET /api/wallets/{walletId}/pockets/max-expenses: Reporte de gastos máximos.

Pruebas
Para garantizar la calidad del sistema, ejecuta la suite de pruebas unitarias y de integración
# Probar dominio y casos de uso
./gradlew :domain:model:test :domain:usecase:test
# Probar adaptadores de persistencia e infraestructura
./gradlew :infrastructure:driven-adapters:r2dbc-repository:test
./gradlew :infrastructure:entry-points:reactive-web:test
