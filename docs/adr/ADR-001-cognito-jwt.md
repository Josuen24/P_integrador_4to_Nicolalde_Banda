ADR-001 — AWS Cognito como identidad y JWT para autorización


Estado: Aceptado  
Fecha: 11/08/2026


Contexto:
PUCE Market requiere acceso exclusivo de usuarios verificados y diferenciación de roles BUYER y SELLER, sin guardar contraseñas ni duplicar usuarios/roles en PostgreSQL.


Decisión:
Se utiliza AWS Cognito User Pool como proveedor de identidad.
La aplicación Android obtiene un access_token de Cognito.
Retrofit envía el token como Bearer en cada petición protegida.
Spring Security valida el emisor (issuer) y las claves públicas de Cognito.
El claim cognito:groups se transforma en los roles BUYER y SELLER.
El username se obtiene desde el JWT y se usa para validar la propiedad de productos y solicitudes.


Consecuencias positivas:  
Autenticación centralizada y segura.  
No se almacenan contraseñas en la aplicación ni en PostgreSQL.  
Roles verificables desde Cognito.  
Respuestas coherentes: 401 sin token válido y 403 por rol o recurso no autorizado.


Consecuencias negativas:  
El proyecto depende de la configuración de AWS Cognito.  
Los tokens tienen vencimiento y deben renovarse.
