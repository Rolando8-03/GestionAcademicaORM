-- Ejecutar conectado a gestionacademica después de iniciar la aplicación.
SELECT table_name FROM information_schema.tables
WHERE table_schema = 'public' AND table_name = 'estudiantes';

SELECT column_name, data_type, character_maximum_length, is_nullable
FROM information_schema.columns
WHERE table_schema = 'public' AND table_name = 'estudiantes'
ORDER BY ordinal_position;

SELECT id, nombres, apellidos, correo, carrera, telefono
FROM public.estudiantes ORDER BY id;

SELECT conname, pg_get_constraintdef(oid) AS definicion
FROM pg_constraint WHERE conrelid = 'public.estudiantes'::regclass;
