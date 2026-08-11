# ADR-002 - Chat privado vinculado a una oferta

- Estado: Aceptado
- Fecha: 2026-08-11

## Contexto

Comprador y vendedor necesitan acordar la entrega sin exponer conversaciones a terceros ni introducir notificaciones.

## Decisión

Crear `Conversation` con relación uno a uno a `PurchaseRequest` y `Message` con relación uno a muchos a Conversation. La conversación se crea al enviar la oferta. Solo buyerUsername y sellerUsername de la oferta pueden leer o enviar mensajes; solo el autor elimina su mensaje.

## Consecuencias

- Se preserva la trazabilidad de la negociación y se evita una tabla local de usuarios.
- No hay notificaciones en tiempo real: Android actualiza al entrar/enviar mensajes.
- Se requiere validar autorización por participante en la capa service.
