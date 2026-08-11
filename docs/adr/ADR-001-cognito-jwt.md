# ADR-001 - AWS Cognito como identidad y JWT para autorización

- Estado: Aceptado
- Fecha: 2026-08-11

## Contexto

PUCE Market requiere acceso exclusivo de usuarios verificados y roles BUYER/SELLER, sin duplicar credenciales ni crear tablas `users` o `roles` en PostgreSQL.

## Decisión

Usar AWS Cognito User Pool como proveedor de identidad. Android obtiene un `access_token`; Retrofit lo agrega como Bearer token; Spring Security valida el issuer y las claves públicas de Cognito. Los grupos `cognito:groups` se convierten en roles de Spring. El username viene del claim Cognito y las reglas de propiedad se aplican en services.

## Consecuencias

- Positivas: autenticación centralizada, no se almacenan contraseñas locales, roles verificables y respuestas 401/403 coherentes.
- Negativas: el entorno depende de configuración Cognito y los tokens expiran.
- Mitigación: configurar un App Client público Android con Authorization Code + PKCE y renovación de sesión.
