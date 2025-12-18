# Transactions Service

Microservicio reactivo.
El servicio permite crear transacciones, validar reglas de riesgo y emitir eventos en tiempo real.

El proyecto fue desarrollado y probado completamente en **entorno local**, utilizando MongoDB local y un módulo legado con JPA + H2 (en memoria).

---

## Requisitos del entorno

- Java 17
- Maven 3.9+
- MongoDB instalado y ejecutándose localmente (`localhost:27017`)
- No se utiliza Docker

---

## Tecnologías utilizadas

- Java 17  
- Spring Boot 3.5.8  
- Spring WebFlux  
- Spring Data MongoDB Reactive  
- Spring Data JPA  
- H2 Database (en memoria)  
- Project Reactor  
- Maven  

---

## Ejecución del proyecto (local)

### 1. Levantar MongoDB

En una consola:

```bash
mongod
```

Opcional (verificar conexión):

```bash
mongosh
```

---

### 2. Ejecutar la aplicación

Desde la raíz del proyecto:

```bash
mvn spring-boot:run
```

La aplicación se levanta en:

```
http://localhost:8084
```

Al iniciar la aplicación se cargan automáticamente:
- Cuentas iniciales en MongoDB
- Reglas de riesgo en H2 (JPA)

---

### 3. Ejecutar pruebas automáticas

```bash
mvn test
```

Se ejecuta el test reactivo `RiskServiceTest`, el cual valida el comportamiento del servicio de riesgo utilizando **StepVerifier**.

---

## Comandos de prueba (Endpoints)

### Crear transacción – Caso exitoso

```bash
curl -X POST http://localhost:8084/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "001-0001",
    "type": "DEBIT",
    "amount": 100
  }'
```

Resultado esperado:
- HTTP 201 (Created)
- Transacción creada correctamente

---

### Rechazo por regla de riesgo

```bash
curl -X POST http://localhost:8084/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "001-0001",
    "type": "DEBIT",
    "amount": 2000
  }'
```

Respuesta esperada:

```json
{
  "error": "risk_rejected"
}
```

---

### Rechazo por fondos insuficientes

```bash
curl -X POST http://localhost:8084/api/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "accountNumber": "001-0002",
    "type": "DEBIT",
    "amount": 801
  }'
```

Respuesta esperada:

```json
{
  "error": "insufficient_funds"
}
```

---

### Listar transacciones por cuenta

```bash
curl "http://localhost:8084/api/transactions?accountNumber=001-0001"
```

Resultado esperado:
- Lista de transacciones asociadas a la cuenta
- Ordenadas por fecha descendente

---

### Stream de transacciones (Server-Sent Events)

```bash
curl http://localhost:8084/api/stream/transactions
```

Mantener la conexión abierta y ejecutar una creación de transacción para observar los eventos en tiempo real.

---

## Verificación de datos en MongoDB

Usando **MongoDB Compass**:

- Base de datos: `bankx`
- Colecciones:
  - `accounts`
  - `transactions`

Las transacciones y balances se actualizan conforme se ejecutan los comandos de prueba.

---

## Manejo de errores

El servicio maneja errores de negocio de forma consistente:

- `account_not_found`
- `insufficient_funds`
- `risk_rejected`

---

## Testing

Se implementó un test reactivo mínimo:

- `RiskServiceTest`
- Uso de `@SpringBootTest`
- Validación de flujo reactivo con `StepVerifier`
- Ejecución correcta del módulo legado de riesgo

---

## Notas finales

- El proyecto fue ejecutado completamente en entorno local.
- No se utilizó Docker.
