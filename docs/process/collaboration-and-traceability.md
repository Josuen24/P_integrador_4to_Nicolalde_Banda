# Trazabilidad de colaboración

## Herramientas

- Repositorio: GitHub `Josuen24/P_integrador_4to_Nicolalde_Banda`.
- Gestión del trabajo: Jira, proyecto `KAN`.
- Integración continua: GitHub Actions ejecuta las pruebas del backend en cada rama y Pull Request.

## Relación Jira - GitHub

Cada historia se implementa en una rama `feature/KAN-<id>-descripcion` y se integra por Pull Request a `develop`. El título del PR debe incluir `KAN-<id>`.

Ejemplo: `KAN-12 feat(frontend): mostrar compras del comprador`.

## Revisión cruzada

Antes de fusionar un Pull Request:

1. El autor verifica compilación y pruebas.
2. El otro integrante revisa el cambio y deja un comentario o aprobación.
3. Se integra a `develop`.
4. Las entregas demostrables pasan de `develop` a `main` mediante un Pull Request.

Esta evidencia permite demostrar colaboración auténtica, control de versiones y calidad del software.