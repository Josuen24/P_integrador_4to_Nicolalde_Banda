# Emprendimiento - PUCE Market

## Propuesta de valor

Marketplace exclusivo para la comunidad PUCE que centraliza la compra y venta de artículos académicos y personales. Reduce la pérdida de publicaciones en chats informales, protege el acceso mediante Cognito y facilita acordar una entrega con chat privado.

## Business Model Canvas

| Bloque | Definición |
|---|---|
| Segmentos | Estudiantes, docentes y personal administrativo PUCE. |
| Propuesta de valor | Mercado confiable y privado para encontrar artículos cerca del campus. |
| Canales | Aplicación Android, difusión en grupos institucionales y ferias universitarias. |
| Relación | Autoservicio, chat entre participantes y soporte básico institucional. |
| Ingresos | Etapa inicial gratuita; futura membresía premium o publicación destacada, sin comisión de pago dentro de la app. |
| Recursos clave | App Android, API Spring Boot, PostgreSQL, AWS Cognito y equipo de desarrollo. |
| Actividades clave | Moderar catálogo, mantener seguridad, soporte y mejora de experiencia. |
| Socios | PUCE, comunidad estudiantil, AWS y responsables de bienestar universitario. |
| Costos | Infraestructura, dominio, soporte, mantenimiento y promoción. |

## Diferenciación e innovación

- Acceso restringido a comunidad verificada, no a público externo.
- Autorización por roles y propiedad: un usuario no modifica recursos ajenos.
- Chat privado ligado a la oferta, orientado a acordar retiro sin introducir pagos, notificaciones ni datos sensibles adicionales.
- Arquitectura reproducible con Docker y móvil nativo Android.

## Proyección financiera inicial (USD mensuales)

| Concepto | Estimado |
|---|---:|
| Hosting API/DB básico | 25 |
| Dominio y servicios operativos prorrateados | 5 |
| Monitoreo, backups y contingencia | 10 |
| Promoción universitaria | 20 |
| Total mensual estimado | 60 |

Escenario de sostenibilidad: 30 publicaciones destacadas a $2.50/mes generan $75, cubren la operación estimada y dejan $15 para mejora continua. La validación inicial se enfoca en adopción y confianza, no en procesar pagos.
