# PUCE Market - Requerimientos, casos de uso y backlog

## Objetivo y alcance

PUCE Market permite a miembros autenticados de la comunidad universitaria publicar productos, buscar ofertas, solicitar compras y coordinar su entrega mediante los datos de contacto verificados. No procesa pagos, envíos, facturación ni notificaciones masivas.

## Actores

- Visitante: consulta catálogo y categorías sin autenticarse.
- BUYER: busca productos, envía solicitudes de compra y consulta sus compras.
- SELLER: publica y administra sus productos, acepta o rechaza solicitudes de compra.
- AWS Cognito: autentica usuarios y entrega los grupos `BUYER` y `SELLER` en el JWT.

## Requerimientos funcionales

| ID | Requerimiento | Prioridad | Criterio de aceptación |
|---|---|---|---|
| RF-01 | Consultar catálogo y categorías | Must | Un visitante obtiene productos disponibles y categorías sin token. |
| RF-02 | Buscar productos | Must | El catálogo se filtra por texto y/o categoría. |
| RF-03 | Publicar producto | Must | Un SELLER autenticado registra nombre, descripción, precio y categoría. |
| RF-04 | Gestionar producto propio | Should | Un SELLER puede listar, editar, marcar vendido y eliminar únicamente sus productos. |
| RF-05 | Enviar solicitud de compra | Must | Un BUYER puede enviar precio y mensaje de oferta sobre un producto ajeno disponible. |
| RF-06 | Gestionar solicitudes de compra | Must | El SELLER propietario puede aceptar o rechazar las solicitudes recibidas. |
| RF-07 | Consultar solicitudes propias | Should | BUYER consulta y cancela únicamente solicitudes pendientes propias. |
| RF-08 | Contacto posterior | Could | Al aceptarse la compra, SELLER/BUYER obtienen el contacto verificado de la contraparte. |

## Requerimientos no funcionales

| ID | Requerimiento | Verificación |
|---|---|---|
| RNF-01 | Seguridad JWT | Cognito firma los JWT; Spring valida issuer, firma y roles. |
| RNF-02 | Autorización por propiedad | Servicio devuelve 403 cuando el usuario no es dueño/participante de la transacción. |
| RNF-03 | API REST | DTOs, códigos 200/201/204/401/403/404/409 y manejo global de errores. |
| RNF-04 | Persistencia reproducible | PostgreSQL y migraciones Flyway se levantan mediante Docker Compose. |
| RNF-05 | Usabilidad móvil | Android presenta estados de carga/error, formularios y navegación persistente. |
| RNF-06 | Calidad | Pruebas unitarias para validaciones, mappers, repositorios y servicios. |

## Tabla de Casos de Uso (Matriz de Trazabilidad)

| ID CU | Nombre del Caso de Uso | Actor Principal | Precondición y Flujo Resumido | Requerimientos Asociados |
|---|---|---|---|---|
| **CU-01** | Autenticar Usuario | Estudiante / Docente | **Precondición:** Credenciales en Cognito.<br>**Flujo:** Ingresa credenciales en Android, Retrofit envía petición y recibe token JWT válido. | RF-01, RNF-01 |
| **CU-02** | Publicar Producto | Vendedor (SELLER) | **Precondición:** Autenticado en grupo SELLER.<br>**Flujo:** Completa formulario Android, `POST /api/products`, backend asigna usuario del JWT y guarda el producto. | RF-02, RF-03, RNF-05 |
| **CU-03** | Enviar Solicitud de Compra | Comprador (BUYER) | **Precondición:** BUYER autenticado y producto ajeno `AVAILABLE`.<br>**Flujo:** Envía oferta a `POST /api/products/{id}/requests`; backend registra solicitud como `PENDING`. | RF-04, RF-05 |
| **CU-04** | Aceptar o Rechazar Compra | Vendedor (SELLER) | **Precondición:** SELLER dueño del producto y solicitud `PENDING`.<br>**Flujo:** Executa `PATCH /api/purchase-requests/{id}/accept`; reserva producto y rechaza otras solicitudes. | RF-06 |
| **CU-05** | Consultar Datos de Contacto | Comprador / Vendedor | **Precondición:** Solicitud de compra en estado `ACCEPTED`.<br>**Flujo:** El sistema facilita la información de contacto verificado de ambas partes para coordinar entrega. | RF-08 |

## Backlog para Jira

| Épica | Historia de usuario | Prioridad | Tarea técnica asociada |
|---|---|---|---|
| EP-01 Catálogo | HU-01: Como visitante quiero ver productos para descubrir ofertas PUCE. | Must | API catálogo, Retrofit y CatalogScreen. |
| EP-01 Catálogo | HU-02: Como visitante quiero buscar por texto/categoría para encontrar productos. | Must | Endpoint search y buscador en App. |
| EP-02 Venta | HU-03: Como vendedor quiero publicar un producto para ofrecerlo. | Must | Formulario, validaciones y POST protegido. |
| EP-02 Venta | HU-04: Como vendedor quiero administrar mis productos para mantener mi catálogo. | Should | Endpoints me/PUT/DELETE/sold. |
| EP-03 Compra | HU-05: Como comprador quiero enviar una solicitud de compra para ofertar por un producto. | Must | Request DTO, reglas de estado y POST. |
| EP-03 Compra | HU-06: Como vendedor quiero aceptar/rechazar solicitudes para decidir la venta. | Must | PATCH y actualización de estado. |
| EP-04 Plataforma | HU-07: Como equipo quiero levantar el sistema con Docker para reproducir el entorno. | Must | Compose, Flyway y guía de ejecución. |

## Definition of Done

Una historia se considera terminada cuando tiene criterios de aceptación comprobables, prueba relevante, código revisado en merge request, migración si modifica datos, documentación actualizada y evidencia de demo.

## Decisión de priorización

La priorización Must/Should/Could del backlog se formaliza en
[`ADR-002 - Priorización del MVP`](../adr/ADR-002-priorizacion-mvp.md). El
equipo priorizó catálogo, autenticación, publicación y solicitudes de compra
porque constituyen el flujo mínimo que resuelve el problema; chat interno,
pagos, notificaciones e imágenes se difieren como evolución posterior.
