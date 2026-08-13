# Arquitectura Android por capas

El cliente Android de PUCE Market se organiza siguiendo el patrón trabajado en
los laboratorios: separación entre modelo, acceso a servicios, estado de
presentación y UI Compose.

```text
ui/screens + ui/navigation
            |
            v
       viewmodels
            |
            v
         services
            |
            v
      API REST Spring Boot
            |
            v
        models (DTOs)
```

## Paquetes

| Paquete | Responsabilidad |
|---|---|
| `models/` | DTOs usados por Retrofit: producto, categoría, solicitud y sesión. No contiene lógica de interfaz. |
| `services/` | `ApiService` declara endpoints, `RetrofitClient` configura HTTP/Bearer JWT y `TokenStore` conserva localmente token y roles. |
| `viewmodels/` | `MarketViewModel` usa `StateFlow`, ejecuta corrutinas y administra carga, errores, catálogo, solicitudes y sesión. |
| `ui/theme/` | Tema y colores institucionales de PUCE. |
| `ui/navigation/` | Orquesta destinos y barra inferior según los roles `BUYER` y `SELLER`. |
| `ui/screens/` | Composables por responsabilidad: catálogo/detalle y sesión/publicación/compras/ventas. Solo solicitan acciones al ViewModel. |

## Flujo de una petición

1. El usuario interactúa con una pantalla Compose en `ui/screens`.
2. La pantalla invoca una acción del `MarketViewModel`.
3. El ViewModel llama al contrato Retrofit `ApiService`.
4. `RetrofitClient` agrega el token Cognito si existe y realiza la petición HTTP.
5. La respuesta se deserializa en un objeto de `models`.
6. El ViewModel actualiza un `StateFlow`; la pantalla se recompone mostrando el
   resultado, carga o error.

## Autorización visual

- Visitante: catálogo, categorías y búsqueda pública.
- `BUYER`: detalle, oferta y **Mis compras**.
- `SELLER`: publicación y **Mis ventas**.

La interfaz no sustituye la seguridad: Spring Boot vuelve a validar JWT, rol y
propiedad en cada endpoint privado.
