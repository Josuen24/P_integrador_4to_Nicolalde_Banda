# PUCE Market

Monorepo del Proyecto Integrador **PUCE Market**.

| Módulo | Tecnología | Propósito |
| --- | --- | --- |
| `backend/` | Kotlin, Spring Boot, PostgreSQL | API REST, reglas de negocio, JWT de Amazon Cognito y chat privado. |
| `frontend/` | Kotlin, Android Jetpack Compose | Aplicación móvil para catálogo, ofertas, publicaciones y chat. |

## Inicio local

1. Iniciar PostgreSQL desde la raíz del repositorio:

   ```powershell
   docker compose up -d postgres
   ```

2. Abrir `backend/` en IntelliJ IDEA y ejecutar `PuceMarketApplication`.
3. Abrir `frontend/` en Android Studio y ejecutar `app` en un emulador.

El emulador Android se conecta al backend local mediante `http://10.0.2.2:8080/`.

## Flujo de ramas

- `main`: versión integrada y estable.
- `develop`: integración de trabajo en curso.
- `feature/KAN-<n>-descripcion`: trabajo asociado a una historia o tarea de Jira.

No se versionan contraseñas, tokens, `local.properties` ni artefactos de compilación.
