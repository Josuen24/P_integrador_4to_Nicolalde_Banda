# ADR-002 - Priorización del MVP de PUCE Market

- Estado: Aceptado
- Fecha: 2026-08-12
- Decisores: Equipo PUCE Market

## Contexto

El problema identificado es que las publicaciones de compra y venta de la
comunidad PUCE se pierden en grupos informales, no tienen trazabilidad y no
garantizan que los participantes pertenezcan a la institución. El tiempo de
desarrollo exige definir un producto mínimo viable que resuelva el flujo
principal antes de incorporar funcionalidades complementarias.

## Decisión

Se prioriza el backlog con el método **MoSCoW**, considerando valor para el
usuario, riesgo de seguridad, dependencia técnica y tiempo disponible.

### Must have - incluidos en el MVP

| Capacidades priorizadas | Justificación |
|---|---|
| Catálogo, categorías y búsqueda pública | Permiten descubrir ofertas y validan el valor básico del marketplace. |
| Autenticación y roles con Cognito | Aseguran que las operaciones privadas sean de miembros verificados. |
| Publicación y gestión de productos | Permiten que un vendedor cree la oferta que será visible en el catálogo. |
| Solicitud de compra, aceptación y rechazo | Implementan el flujo de negocio central y conservan su trazabilidad. |
| Persistencia PostgreSQL, Flyway y Docker | Hacen reproducible el entorno y preservan la información de negocio. |

### Should have - incluidos cuando apoyan el flujo principal

| Capacidades priorizadas | Justificación |
|---|---|
| Mis productos, Mis ventas y Mis compras | Dan visibilidad a cada actor sobre sus propias acciones. |
| Cancelación de solicitudes y marcado como vendido | Controlan estados posteriores sin ampliar el dominio. |
| Validaciones, manejo de errores y pruebas unitarias | Reducen fallas en los casos críticos del MVP. |

### Could have - diferidos deliberadamente

| Capacidades diferidas | Motivo de la decisión |
|---|---|
| Chat interno y notificaciones push | Requieren tiempo real, persistencia adicional y mayor complejidad operativa. El MVP coordina el retiro mediante WhatsApp. |
| Pagos, facturación, envíos y carrito | No son necesarios para resolver la intermediación dentro del campus e implican riesgos legales y financieros. |
| Carga de imágenes, reputación y recomendaciones | Mejoran la experiencia, pero no bloquean el flujo de publicar, ofertar y decidir una compra. |
| Inicio de sesión Android con PKCE integrado | Es la evolución de seguridad y experiencia; el MVP valida el token Cognito ya emitido. |

## Consecuencias

### Positivas

- El equipo entrega un flujo completo y demostrable: catálogo -> oferta ->
  decisión del vendedor -> coordinación de entrega.
- Las decisiones de alcance son coherentes con las historias Must/Should del
  backlog y con el tiempo disponible.
- Se evita crear módulos incompletos de mensajería, pagos o logística.

### Negativas

- El usuario realiza la coordinación final fuera de la aplicación.
- Algunas mejoras de experiencia, como autenticación PKCE integrada e imágenes,
  quedan como trabajo futuro.

## Evidencia de implementación

- Requerimientos y backlog: `docs/analysis/requirements-and-backlog.md`.
- Dominio implementado: `Category`, `Product` y `PurchaseRequest`.
- Roles y protección: `SecurityConfig`, controladores y servicios del backend.
- Flujo móvil: catálogo, publicación, ofertas, Mis ventas y Mis compras.
