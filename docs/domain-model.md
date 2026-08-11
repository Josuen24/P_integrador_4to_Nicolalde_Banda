# Modelo de dominio - PUCE Market

```mermaid
erDiagram
    CATEGORY ||--o{ PRODUCT : clasifica
    PRODUCT ||--o{ PURCHASE_REQUEST : recibe

    CATEGORY {
        bigint id PK
        string name UK
        string description
        boolean active
        instant createdAt
        instant updatedAt
    }
    PRODUCT {
        bigint id PK
        string name
        string description
        decimal price
        ProductStatus status
        string sellerUsername
        bigint categoryId FK
        instant createdAt
        instant updatedAt
    }
    PURCHASE_REQUEST {
        bigint id PK
        decimal offeredPrice
        string message
        PurchaseRequestStatus status
        string buyerUsername
        bigint productId FK
        instant createdAt
        instant updatedAt
        instant respondedAt
    }
```

No existen tablas de usuarios, roles, perfiles, conversaciones, mensajes, notificaciones ni teléfonos. La identidad del comprador y vendedor se conserva como el `username` procedente del JWT, que será validado en la capa de seguridad.
