# Guía de contribución

## Flujo de trabajo

- `main`: versión estable demostrable.
- `develop`: integración de cambios aprobados.
- `feature/KAN-<id>-descripcion`: una historia o tarea concreta de Jira.

1. Crear una rama desde `develop`.
2. Realizar cambios pequeños y comprobables.
3. Ejecutar las pruebas del backend: `cd backend; .\gradlew.bat test`.
4. Abrir un Pull Request hacia `develop` con el identificador de Jira en el título.
5. Un integrante distinto revisa el Pull Request antes de integrarlo.

## Convenciones

- Commits: `feat(modulo): ...`, `fix(modulo): ...`, `docs: ...`, `test: ...`.
- No subir tokens, secretos de Cognito, contraseñas ni `local.properties`.
- Cada persona debe usar su propia cuenta de GitHub y autoría Git real.

## Reparto de trabajo sugerido

| Área | Responsable propuesto | Evidencia |
| --- | --- | --- |
| Backend, seguridad Cognito y base de datos | Juan Banda | Ramas `feature/backend-*`, pruebas Gradle, Pull Requests. |
| Android, UX y flujos por rol | Compañero/a | Ramas `feature/frontend-*`, capturas y Pull Requests. |
| Integración, documentación y demostración | Ambos | Revisión cruzada de PR, Jira actualizado y prueba integral. |

El reparto debe ajustarse a lo que cada integrante realmente realice y quedar respaldado por sus commits, MR y tareas asignadas.