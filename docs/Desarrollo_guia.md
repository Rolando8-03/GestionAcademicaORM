# Desarrollo de la práctica guiada

Rolando Enrique Mayorga Mena · 1S1-ELK-S · 27 de octubre de 2026

## Fase I Fundamentos

ORM significa mapeo objeto relacional. Relaciona una clase con una tabla, sus atributos con columnas y cada objeto persistido con una fila. Hibernate implementa este mapeo y utiliza JDBC para comunicarse con la base de datos.

Con JDBC se escriben las consultas SQL, se asignan parámetros y se convierten manualmente los resultados en objetos. Con Hibernate se configuran entidades y se trabaja principalmente con objetos y HQL. Conocer SQL permite revisar las consultas generadas, entender las restricciones y mejorar el rendimiento.

@Entity identifica una clase persistente que Hibernate debe mapear.

## Fase II Organización

El controlador atiende la interfaz y delega la persistencia al repositorio. Separar las consultas permite mantener y probar la lógica de acceso a datos sin mezclarla con los controles JavaFX.

## Fase III Configuración

hbm2ddl.auto=create recrea el esquema al construir SessionFactory y puede eliminar información previa. update intenta adaptar el esquema existente y conservar sus datos; no reemplaza un sistema de migraciones para producción.

El dialecto adapta la generación de SQL al motor PostgreSQL. Si el servidor utiliza otro puerto, se debe indicar en la URL; una URL con puerto incorrecto impide la conexión.

Se corrigen dos errores del ejemplo de la guía: PostgreSQL no acepta GO como separador y la clase del controlador es org.postgresql.Driver.

## Fase IV Anotaciones

- @Entity declara la entidad persistente.
- @Table define el nombre estudiantes y la restricción única del correo.
- @Id marca la clave primaria.
- @GeneratedValue con IDENTITY delega la generación del identificador a PostgreSQL.
- @Column configura nombre implícito, longitud y aceptación de valores nulos.

La restricción uk_estudiantes_correo impide duplicar correos normalizados por la aplicación. El teléfono admite null para conservar compatibilidad con registros previos al reto.

## Fase V Persistencia

SessionFactory se construye una vez y crea sesiones. Session representa una unidad de trabajo con la base y no se comparte entre hilos. Transaction delimita una operación atómica. persist incorpora la entidad a la sesión para insertarla; con IDENTITY el INSERT puede ejecutarse inmediatamente para obtener el ID. rollback deshace la transacción si ocurre un error.

El repositorio confirma con commit, revierte los errores antes de cerrar la sesión y libera los recursos con try-with-resources. Cerrar correctamente evita conexiones retenidas y operaciones pendientes. SessionFactory se cierra al salir de la aplicación.

## Fase VI Interfaz

Guardar valida los campos, construye la entidad y ejecuta el repositorio en un Task. Al completarse, informa el resultado, limpia el formulario y vuelve a consultar. La tabla muestra ID, nombres, apellidos, correo, carrera y teléfono. Consultar recupera los estudiantes o aplica el filtro seleccionado.

## Fase VII Retos

1. Se agrega telefono con @Column(length = 20). En una base nueva aparece al generar estudiantes. Para demostrar una actualización sobre una tabla existente, parta de una versión previa de la entidad sin telefono, registre un estudiante y cierre la aplicación. Restaure el atributo y reinicie con update. Verifique la nueva columna y que el registro anterior siga presente. Una captura de la tabla nueva demuestra la columna final, pero no demuestra por sí sola una migración.
2. El correo se normaliza a minúsculas. La aplicación muestra un mensaje claro si ya existe y conserva los datos del formulario para corregirlos. La restricción única protege también inserciones concurrentes.
3. La consulta `from Estudiante e where e.carrera = :carrera order by e.id` utiliza un parámetro enlazado con setParameter, sin concatenar el texto del filtro.

## Evidencias para completar en el equipo

No se incluyen capturas simuladas. Realice estas comprobaciones con su PostgreSQL e interfaz:

1. Inicie la aplicación y ejecute sql/02_verificar.sql en gestionacademica. Capture la tabla generada, sus columnas y la restricción única.
2. Registre un estudiante con los cuatro campos obligatorios y un teléfono. Capture la confirmación y la fila visible.
3. Cierre y abra la aplicación; consulte de nuevo. Capture la fila recuperada para demostrar persistencia.
4. Intente guardar el mismo correo en mayúsculas. Capture el aviso de duplicado y compruebe que no se creó una segunda fila.
5. Registre otra carrera, aplique el filtro y capture los resultados correspondientes.
6. Pruebe campos vacíos, correo incompleto y teléfono con letras. Debe aparecer una advertencia sin guardar.
7. Si necesita mostrar el cambio de esquema del reto 1, siga la secuencia de actualización indicada en Fase VII.

## Reflexión

Hibernate facilita el trabajo repetitivo de convertir filas en objetos y mantiene el acceso a datos separado de la interfaz. Frente a JDBC, permite concentrarse en las entidades y sus relaciones, aunque requiere comprender el SQL que genera y el manejo de sesiones. Su principal ventaja en esta práctica es registrar y consultar estudiantes con menos código de mapeo. Una limitación es que una configuración o consulta mal diseñada puede generar operaciones innecesarias. Para una siguiente versión, incorporaría paginación y migraciones versionadas para controlar el crecimiento de los registros y los cambios de estructura.

## Documentación consultada

- Hibernate ORM 6.6: https://docs.hibernate.org/orm/6.6/quickstart/html_single/
- OpenJFX y Maven: https://openjfx.io/openjfx-docs/#maven
- PostgreSQL JDBC: https://jdbc.postgresql.org/documentation/use/
