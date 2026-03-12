# ACME - API REST Wrapper SOAP para Envío de Pedidos

## Descripción

Servicio REST que actúa como wrapper de un servicio SOAP para el ciclo de abastecimiento de la compañía ACME. Recibe peticiones JSON, las transforma a XML SOAP, invoca el endpoint externo de envío de pedidos y retorna la respuesta transformada a JSON.

## Tecnologías

- Java 21
- Spring Boot 4.0.3
- Gradle
- Docker

## Arquitectura

```
Cliente REST (JSON) → EnvioPedidoController → EnvioPedidoService → Endpoint SOAP (XML)
                                                      ↓
                   JSON Response ← Transformación XML→JSON ← SOAP Response (XML)
```

### Componentes

| Componente | Responsabilidad |
|---|---|
| `EnvioPedidoController` | Expone el endpoint REST `POST /api/enviar-pedido` |
| `EnvioPedidoService` | Transformación JSON↔XML y comunicación con el servicio SOAP |
| `RestClientConfig` | Configuración del cliente HTTP (`RestClient`) |
| DTOs (`dto/`) | Objetos de transferencia para request y response |

## Estructura del proyecto

```
src/main/java/avalart/acme/
├── AcmeApplication.java
├── config/
│   └── RestClientConfig.java
├── controller/
│   └── EnvioPedidoController.java
├── dto/
│   ├── EnviarPedidoRequest.java
│   ├── EnviarPedidoResponse.java
│   ├── PedidoData.java
│   └── PedidoRespuestaData.java
└── service/
    └── EnvioPedidoService.java
```

## Endpoint API

### POST /api/enviar-pedido

**Request Body (JSON):**

```json
{
  "enviarPedido": {
    "numPedido": "75630275",
    "cantidadPedido": "1",
    "codigoEAN": "00110000765191002104587",
    "nombreProducto": "Armario INVAL",
    "numDocumento": "1113987400",
    "direccion": "CR 72B 45 12 APT 301"
  }
}
```

**Response Body (JSON):**

```json
{
  "enviarPedidoRespuesta": {
    "codigoEnvio": "80375472",
    "estado": "Entregado exitosamente al cliente"
  }
}
```

## Mapeo de campos

### Request: JSON → XML SOAP

| Campo JSON | Tag XML |
|---|---|
| `numPedido` | `pedido` |
| `cantidadPedido` | `Cantidad` |
| `codigoEAN` | `EAN` |
| `nombreProducto` | `Producto` |
| `numDocumento` | `Cedula` |
| `direccion` | `Direccion` |

### Response: XML SOAP → JSON

| Tag XML | Campo JSON |
|---|---|
| `Codigo` | `codigoEnvio` |
| `Mensaje` | `estado` |

## Ejecución local

### Compilar

```bash
./gradlew build
```

### Ejecutar

```bash
./gradlew bootRun
```

La aplicación estará disponible en `http://localhost:8080`.

### Ejemplo con curl

```bash
curl -X POST http://localhost:8080/api/enviar-pedido \
  -H "Content-Type: application/json" \
  -d '{
    "enviarPedido": {
      "numPedido": "75630275",
      "cantidadPedido": "1",
      "codigoEAN": "00110000765191002104587",
      "nombreProducto": "Armario INVAL",
      "numDocumento": "1113987400",
      "direccion": "CR 72B 45 12 APT 301"
    }
  }'
```

## Ejecución con Docker

### Build y run con Docker Compose

```bash
docker compose up --build
```

### Build manual

```bash
docker build -t acme-api .
docker run -p 8080:8080 acme-api
```

## Tests

### Ejecutar todos los tests

```bash
./gradlew test
```

### Tests incluidos

- **EnvioPedidoServiceTest**: Tests unitarios para la transformación JSON→XML, parseo XML→JSON y flujo completo del servicio.
- **EnvioPedidoControllerTest**: Tests de integración con MockMvc para validar el endpoint REST, incluyendo happy path y validación de errores.
