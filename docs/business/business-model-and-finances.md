# Emprendimiento - PUCE Market

## Propuesta de valor

Marketplace exclusivo para la comunidad PUCE que centraliza la compra y venta de artículos académicos y personales. Reduce la pérdida de publicaciones en chats informales, protege el acceso mediante AWS Cognito y facilita coordinar la entrega directamente con la información de contacto verificado de la contraparte.

## Business Model Canvas

| Bloque | Definición |
|---|---|
| Segmentos | Estudiantes, docentes y personal administrativo PUCE. |
| Propuesta de valor | Mercado confiable y exclusivo para encontrar artículos cerca del campus con usuarios verificados. |
| Canales | Aplicación Android, difusión en grupos institucionales y ferias universitarias. |
| Relación | Autoservicio, gestión mediante solicitudes de compra y soporte básico institucional. |
| Ingresos | Etapa inicial gratuita; opciones de publicaciones destacadas y banners para emprendimientos locales. |
| Recursos clave | App Android, API Spring Boot, PostgreSQL, AWS Cognito y equipo de desarrollo. |
| Actividades clave | Moderar catálogo, mantener seguridad, soporte y mejora de experiencia móvil. |
| Socios | PUCE, comunidad estudiantil, AWS y responsables de bienestar universitario. |
| Costos | Infraestructura cloud, dominio, soporte, mantenimiento y promoción. |

## Diferenciación e innovación

- Acceso restringido a comunidad universitaria verificada, excluyendo al público externo para evitar fraudes.
- Autorización estricta por roles y propiedad: un usuario no puede modificar ni acceder a transacciones ajenas.
- Gestión de compra basada en solicitudes de oferta formales (Aceptada/Rechazada), revelando los datos de contacto únicamente tras la aceptación.
- Arquitectura reproducible con Docker y aplicación nativa Android en Kotlin.

## Proyección financiera inicial (USD mensuales)

### Estructura de Costos Estimada

| Concepto | Estimado (USD) |
|---|---:|
| Hosting API/DB básico (EC2/Docker) | 25 |
| Dominio y servicios operativos prorrateados | 5 |
| Monitoreo, backups y contingencia | 10 |
| Promoción universitaria | 20 |
| **Total costo mensual estimado** | **60** |

### Escenario de Sostenibilidad e Ingresos

$$\text{Ingresos} = 30 \text{ publicaciones destacadas} \times \$2.50 = \$75.00 \text{ USD/mes}$$

$$\text{Flujo Neto Mensual} = \text{Ingresos} - \text{Costos} = \$75.00 - \$60.00 = +\$15.00 \text{ USD/mes}$$

El escenario genera un margen superavitario de $15.00 USD para mejora continua y mantenimiento. La validación inicial se enfoca en adopción y confianza dentro del campus, sin procesar pagos dentro de la aplicación.