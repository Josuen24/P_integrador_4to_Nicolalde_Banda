# Infraestructura Docker y escalamiento

## Arquitectura actual

```text
Android (emulador o dispositivo)
        | HTTP + Bearer JWT
Spring Boot API (contenedor o IntelliJ)
        | JDBC
PostgreSQL 16 (contenedor Docker, volumen persistente)
        |
AWS Cognito User Pool (JWT, grupos BUYER/SELLER)
```

`docker-compose.yml` define PostgreSQL, healthcheck, volumen persistente y una imagen de API. Flyway aplica las migraciones al iniciar. Los perfiles `dev` y `docker` permiten ejecutar la API desde IntelliJ o completamente contenida.

## Escalamiento vertical y horizontal

| Tipo | Aplicación en PUCE Market | Ventaja | Desventaja |
|---|---|---|---|
| Vertical | Aumentar CPU/RAM de la VM de PostgreSQL o API. | Configuración simple, útil para una primera etapa. | Tiene límite físico, mayor costo por VM y punto único de falla. |
| Horizontal | Replicar la API Spring Boot detrás de un balanceador. | Atiende más solicitudes y mejora disponibilidad. | Requiere balanceador, observabilidad y sesiones sin estado. |

La API es apta para escalar horizontalmente porque la sesión se resuelve con JWT Cognito y el estado de negocio se persiste en PostgreSQL. Para una etapa futura, PostgreSQL puede usar réplica administrada y backups; las notificaciones no son parte del alcance.

## Procedimiento de ejecución reproducible

```powershell
cd C:\Users\DETPC\OneDrive\Desktop\pucemarketapi
docker compose up -d postgres
```

Luego se ejecuta `PuceMarketApplicationKt` con el perfil `dev` desde IntelliJ. Para contener todo:

```powershell
docker compose up -d --build
```

## Evidencias para demo

1. Mostrar Docker Desktop o `docker compose ps` con PostgreSQL saludable.
2. Mostrar Flyway creando `categories`, `products`, `purchase_requests`, `conversations` y `messages`.
3. Probar Android -> API -> PostgreSQL creando producto, oferta y mensaje.
4. Explicar que JWT elimina afinidad de sesión y permite replicar la API.
