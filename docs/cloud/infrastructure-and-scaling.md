# Computación en la Nube - Arquitectura e Infraestructura

## 1. Análisis de Escalamiento (Vertical vs. Horizontal)

| Criterio | Escalamiento Vertical (Scale-Up) | Escalamiento Horizontal (Scale-Out) |
|---|---|---|
| **Definición** | Consiste en incrementar los recursos de hardware (CPU, memoria RAM, almacenamiento) del servidor o contenedor existente. | Consiste en añadir más instancias o réplicas del servicio/contenedor trabajando en paralelo detrás de un balanceador de carga. |
| **Ventajas** | - Configuración sencilla e inmediata.<br>- No requiere cambios en la arquitectura de la aplicación ni en el código. | - Alta disponibilidad y tolerancia a fallos.<br>- Flexibilidad para aumentar o reducir instancias según la demanda en tiempo real. |
| **Desventajas** | - Existe un límite físico de hardware.<br>- Puede requerir tiempo de inactividad (*downtime*) durante la ampliación. | - Incrementa la complejidad en la capa de red y en la gestión de sesiones/estado. |
| **Aplicación en PUCE Market** | **Base de Datos (PostgreSQL):** Inicialmente escala verticalmente ajustando el tamaño de la instancia en la nube (ej. de t3.small a t3.medium) para garantizar integridad. | **API Backend (Spring Boot):** Diseñada de forma *stateless* (sin estado local) mediante tokens JWT, lo que permite replicar contenedores en Docker/ECS según el tráfico. |

---

## 2. Arquitectura de Despliegue en la Nube / Contenedores

La aplicación adopta una **arquitectura en capas virtualizada** basada en contenedores Docker y servicios administrados de nube:

```text
                               ┌───────────────────────────┐
                               │   App Móvil Android       │
                               └─────────────┬─────────────┘
                                             │ (HTTPS / REST API)
                                             ▼
                               ┌───────────────────────────┐
                               │   Nginx / Load Balancer   │
                               └─────────────┬─────────────┘
                                             │
                       ┌─────────────────────┴─────────────────────┐
                       │                                           │
                       ▼                                           ▼
         ┌───────────────────────────┐               ┌───────────────────────────┐
         │ API Spring Boot (Inst 1)  │               │ API Spring Boot (Inst 2)  │
         │   (Contenedor Docker)     │               │   (Contenedor Docker)     │
         └─────────────┬─────────────┘               └─────────────┬─────────────┘
                       │                                           │
                       │ ──► [ AWS Cognito (Validación JWT) ] ◄───┤
                       │                                           │
                       └─────────────────────┬─────────────────────┘
                                             │ (SQL)
                                             ▼
                               ┌───────────────────────────┐
                               │   PostgreSQL (RDS/Docker) │
                               └───────────────────────────┘