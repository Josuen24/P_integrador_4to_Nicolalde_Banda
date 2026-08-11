# PUCE Market - Requerimientos, casos de uso y backlog

## Objetivo y alcance

PUCE Market permite a miembros autenticados de la comunidad universitaria publicar productos, buscar ofertas, negociar una compra y coordinar su entrega mediante un chat privado. No procesa pagos, envíos, facturación, notificaciones ni perfiles de usuario locales.

## Actores

- Visitante: consulta catálogo y categorías sin autenticarse.
- BUYER: busca productos, envía ofertas y conversa con el vendedor de una oferta propia.
- SELLER: publica y administra sus productos, acepta o rechaza ofertas y conversa con el comprador.
- AWS Cognito: autentica usuarios y entrega los grupos `BUYER` y `SELLER` en el JWT.

## Requerimientos funcionales

| ID | Requerimiento | Prioridad | Criterio de aceptación |
|---|---|---|---|
| RF-01 | Consultar catálogo y categorías | Must | Un visitante obtiene productos disponibles y categorías sin token. |
| RF-02 | Buscar productos | Must | El catálogo se filtra por texto y/o categoría. |
| RF-03 | Publicar producto | Must | Un SELLER autenticado registra nombre, descripción, precio y categoría. |
| RF-04 | Gestionar producto propio | Should | Un SELLER puede listar, editar, marcar vendido y eliminar únicamente sus productos. |
| RF-05 | Enviar oferta | Must | Un BUYER puede enviar precio y mensaje sobre un producto ajeno disponible. |
| RF-06 | Gestionar ofertas | Must | El SELLER propietario puede aceptar o rechazar las ofertas recibidas. |
| RF-07 | Chat privado | Must | Al enviar una oferta se crea una conversación privada entre BUYER y SELLER; solo ellos ven y envían mensajes. |
| RF-08 | Consultar ofertas propias | Should | BUYER consulta y cancela únicamente ofertas pendientes propias. |
| RF-09 | Contacto posterior | Could | SELLER obtiene el enlace externo de WhatsApp del BUYER autorizado. |

## Requerimientos no funcionales

| ID | Requerimiento | Verificación |
|---|---|---|
| RNF-01 | Seguridad JWT | Cognito firma los JWT; Spring valida issuer, firma y roles. |
| RNF-02 | Autorización por propiedad | Servicio devuelve 403 cuando usuario no es dueño/participante. |
| RNF-03 | API REST | DTOs, códigos 200/201/204/401/403/404/409 y manejo global de errores. |
| RNF-04 | Persistencia reproducible | PostgreSQL y migraciones Flyway se levantan mediante Docker Compose. |
| RNF-05 | Usabilidad móvil | Android Compose presenta estados de carga/error, formularios y navegación persistente. |
| RNF-06 | Calidad | Pruebas unitarias para validaciones, mappers, repositorios y servicios. |

## Casos de uso resumidos

### CU-01 Publicar producto

Precondición: usuario autenticado en grupo SELLER. Flujo: completa formulario Android, Retrofit envía `POST /api/products`, backend toma el username del JWT, valida datos y persiste el producto. Resultado: 201 Created; de lo contrario 401/403/400.

### CU-02 Enviar oferta y abrir chat

Precondición: BUYER autenticado y producto AVAILABLE ajeno. Flujo: envía precio/mensaje a `POST /api/products/{id}/requests`; backend registra la oferta y crea una conversación con comprador y vendedor. Resultado: solo esos participantes pueden usar `GET/POST /api/conversations/...`.

### CU-03 Aceptar oferta

Precondición: SELLER dueño del producto y oferta PENDING. Flujo: `PATCH /api/purchase-requests/{id}/accept`; backend reserva el producto y rechaza otras ofertas pendientes. Resultado: chat existente permite coordinar retiro.

## Backlog para Jira

| Épica | Historia de usuario | Prioridad | Tarea técnica asociada |
|---|---|---|---|
| EP-01 Catálogo | HU-01: Como visitante quiero ver productos para descubrir ofertas PUCE. | Must | API catálogo, Retrofit y CatalogScreen. |
| EP-01 Catálogo | HU-02: Como visitante quiero buscar por texto/categoría para encontrar productos. | Must | Endpoint search y buscador Compose. |
| EP-02 Venta | HU-03: Como vendedor quiero publicar un producto para ofrecerlo. | Must | Formulario, validaciones y POST protegido. |
| EP-02 Venta | HU-04: Como vendedor quiero administrar mis productos para mantener mi catálogo. | Should | Endpoints me/PUT/DELETE/sold. |
| EP-03 Compra | HU-05: Como comprador quiero enviar una oferta para negociar un producto. | Must | Request DTO, reglas de estado y POST. |
| EP-03 Compra | HU-06: Como vendedor quiero aceptar/rechazar ofertas para decidir la venta. | Must | PATCH y actualización de estado. |
| EP-04 Chat | HU-07: Como participante quiero conversar en privado para acordar entrega. | Must | Conversation, Message, autorización por participante y UI Chat. |
| EP-05 Plataforma | HU-08: Como equipo quiero levantar el sistema con Docker para reproducir el entorno. | Must | Compose, Flyway y guía de ejecución. |

## Definition of Done

Una historia se considera terminada cuando tiene criterios de aceptación comprobables, prueba relevante, código revisado en merge request, migración si modifica datos, documentación actualizada y evidencia de demo.
