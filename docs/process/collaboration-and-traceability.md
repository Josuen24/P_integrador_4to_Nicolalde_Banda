# Trazabilidad de colaboración

## Herramientas

- Repositorio: GitLab `SlippyJs/pucemarket`.
- Gestión del trabajo: Jira, proyecto `KAN`.
- Integración continua: GitLab CI ejecuta las pruebas del backend en cada rama y Merge Request.

## Relación Jira - GitLab

Cada historia se implementa en una rama `feature/KAN-<id>-descripcion` y se integra por Merge Request a `develop`. El título del MR debe incluir `KAN-<id>`.

Ejemplo: `KAN-12 feat(frontend): mostrar compras del comprador`.

## Revisión cruzada

Antes de fusionar un Merge Request:

1. El autor verifica compilación y pruebas.
2. El otro integrante revisa el cambio y deja un comentario o aprobación.
3. Se integra a `develop`.
4. Las entregas demostrables pasan de `develop` a `main` mediante un Merge Request.

Esta evidencia permite demostrar colaboración auténtica, control de versiones y calidad del software.