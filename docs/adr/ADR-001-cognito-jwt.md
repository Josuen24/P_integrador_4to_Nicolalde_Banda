# ADR-001 - AWS Cognito como identidad y JWT para autorización

- Estado: Aceptado
- Fecha: 2026-08-11
- Decisores: Equipo PUCE Market

## Contexto

PUCE Market necesita restringir las operaciones de compra y venta a miembros
verificados de la comunidad universitaria. El sistema requiere distinguir los
roles `BUYER` y `SELLER`, proteger recursos propios y evitar almacenar
contraseñas, usuarios o roles duplicados en PostgreSQL.

## Decisión

Se adopta **AWS Cognito User Pool** como proveedor de identidad. El backend se
configura como *OAuth2 Resource Server*: Spring Security valida el `issuer`, la
firma y la vigencia de cada JWT contra las claves públicas de Cognito.

El claim `cognito:groups` se convierte en las autoridades `ROLE_BUYER` y
`ROLE_SELLER`. El `username` se obtiene del JWT y se usa en la capa de servicios
para validar propiedad de productos y participación en solicitudes de compra.

La aplicación Android guarda el `access_token` de la sesión localmente y el
interceptor de Retrofit lo envía como `Authorization: Bearer <token>` a los
endpoints privados. El endpoint `GET /api/session` permite validar el token y
mostrar el perfil activo dentro de la aplicación.

## Alternativas descartadas

1. **Usuarios y contraseñas propios en PostgreSQL:** duplicaría credenciales,
   aumenta la superficie de seguridad y obliga a implementar recuperación y
   cifrado de contraseñas.
2. **JWT firmado localmente por Spring Boot:** reduce la integración con un
   proveedor institucional y traslada al proyecto la operación de identidad.
3. **Sin validación de roles:** permitiría publicar o responder ofertas a
   usuarios sin permiso.

## Consecuencias

### Positivas

- Autenticación centralizada y contraseñas fuera de la base de datos del
  proyecto.
- Roles verificables mediante un claim firmado.
- Respuestas consistentes: `401` para token ausente o inválido y `403` para rol
  incorrecto o falta de propiedad.
- La API permanece *stateless*, por lo que puede replicarse sin sesiones de
  servidor.

### Negativas y mitigación

- El entorno depende de la configuración del User Pool, App Client y grupos de
  Cognito; se documentan como variables de entorno y no se versionan secretos.
- Los tokens expiran; el usuario debe volver a autenticarse cuando pierdan
  vigencia.
- **Estado actual del MVP:** Android valida y conserva un `access_token` ya
  emitido por Cognito. La siguiente iteración integrará Authorization Code +
  PKCE para que la app realice el inicio de sesión y renovación de token sin
  pegado manual.
