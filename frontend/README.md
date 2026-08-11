# PUCE Market Android

Cliente móvil en Kotlin + Jetpack Compose, siguiendo los laboratorios de la materia: `models`, `services` (Retrofit), `viewmodels` (StateFlow) y `ui` (Compose).

## Ejecutar

1. Abre esta carpeta con Android Studio.
2. Copia `local.properties.example` como `local.properties`.
3. Para el emulador usa `http://10.0.2.2:8080/`. Para un teléfono físico usa la IP LAN del computador y confirma que Spring Boot escucha en esa red.
4. Ejecuta el backend y luego la app.

El catálogo es público. Para publicar o enviar solicitudes, ingresa con el `access_token` emitido por AWS Cognito.

## Alcance respecto al PDF

Las pantallas de catálogo, búsqueda, detalle/solicitud y publicación consumen endpoints existentes. El PDF propone chat, pero el backend actual no tiene endpoints de conversaciones/mensajes; el flujo posterior a aceptar solicitudes usa el contacto de WhatsApp del backend.
