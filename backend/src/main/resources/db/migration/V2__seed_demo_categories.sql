INSERT INTO categories (name, description, active) VALUES
    ('Tecnología', 'Dispositivos, accesorios y equipos tecnológicos.', TRUE),
    ('Libros y apuntes', 'Libros, guías y material académico.', TRUE),
    ('Hogar', 'Artículos útiles para el hogar y la residencia.', TRUE),
    ('Deportes', 'Implementos y accesorios deportivos.', TRUE),
    ('Moda', 'Ropa, calzado y accesorios.', TRUE)
ON CONFLICT (name) DO NOTHING;
