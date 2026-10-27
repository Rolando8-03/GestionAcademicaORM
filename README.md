# Gestión Académica ORM

Proyecto Maven de la práctica guiada de Hibernate, JavaFX y PostgreSQL.

Autor: Rolando Enrique Mayorga Mena · Grupo 1S1-ELK-S

## Requisitos

- JDK 21.
- PostgreSQL 15 o posterior, en ejecución.
- IntelliJ IDEA con Maven integrado, o Maven 3.9 para ejecutar desde terminal.
- Internet durante la primera carga para descargar dependencias.

Versiones fijadas para reproducir la guía: JavaFX 21.0.6, Hibernate 6.6.11.Final y PostgreSQL JDBC 42.7.4. No es necesario instalar el SDK de JavaFX por separado.

## Preparación

1. Descomprima el ZIP y abra la carpeta GestionAcademicaORM en IntelliJ, seleccionando su pom.xml si se solicita.
2. Configure Project SDK y Maven Runner JRE con JDK 21. Recargue el proyecto Maven.
3. Desde pgAdmin, conéctese a postgres y ejecute sql/01_crear_base.sql una sola vez. No cree estudiantes manualmente.
4. Copie config/hibernate-local.properties.example como config/hibernate-local.properties. Complete el usuario y la contraseña reales de su PostgreSQL. El usuario debe tener permiso para crear y modificar tablas en el esquema public. Si usa otro puerto, modifique 5432 en la URL.
5. En la ventana Maven de IntelliJ ejecute Plugins → javafx → javafx:run. También puede ejecutar `mvn javafx:run` desde la carpeta que contiene pom.xml, o ejecutar.bat si tiene Maven en PATH.

El directorio de trabajo debe ser la raíz del proyecto: allí se busca config/hibernate-local.properties. No basta con abrir el archivo FXML ni con hacer doble clic en un JAR. El proyecto utiliza classpath y no necesita module-info.java.

Como alternativa, configure DB_URL, DB_USER y DB_PASSWORD en las variables de entorno de la ejecución Maven. Las variables prevalecen sobre la configuración local; esta prevalece sobre src/main/resources/hibernate.properties. Las credenciales reales no deben incluirse en Git ni en un ZIP de entrega.

## Uso

Complete nombres, apellidos, correo y carrera; teléfono es opcional. Puede elegir una carrera o escribir una nueva. Pulse Guardar estudiante. Tras confirmar el registro, el formulario se limpia y se muestran todos los estudiantes, ordenados por ID.

Seleccione una carrera en el filtro y pulse Consultar. Seleccione Todas las carreras y vuelva a consultar para recuperar la lista completa. Las operaciones de PostgreSQL se ejecutan con Task en un hilo de trabajo.

El correo se guarda en minúsculas y sin espacios exteriores. Se comprueba antes de insertar y también está protegido por una restricción UNIQUE. Si se repite, se muestra un aviso. El teléfono acepta entre 8 y 20 dígitos cuando se proporciona.

## Contenido

- model: entidad y validación del formulario.
- repository: operaciones Hibernate y consulta HQL parametrizada.
- controller: eventos, tareas de segundo plano y presentación de resultados.
- config: creación y cierre de SessionFactory.
- src/main/resources: FXML, CSS y configuración base sin claves reales.
- sql: creación de la base y consultas de comprobación.
- docs/Desarrollo_guia.md: respuestas, reflexión y procedimiento de evidencias.
- src/test: pruebas de validación y persistencia sobre H2 en modo PostgreSQL.

## Comprobación local

Ejecute `mvn clean test`. Las pruebas H2 se ejecutan sin PostgreSQL y verifican la lógica de persistencia; no sustituyen la comprobación del motor PostgreSQL. Las capturas de pgAdmin y de JavaFX deben tomarse en su equipo siguiendo docs/Desarrollo_guia.md.

## Problemas habituales

- Si falta configuración, complete el archivo local y vuelva a consultar. La inicialización permite reintentar.
- Si se rechaza la contraseña, use las credenciales de PostgreSQL, no las de Windows.
- Si no existe gestionacademica, ejecute únicamente el script 01 desde postgres.
- Si hay un error de permisos en public, use el propietario de la base o conceda permisos desde una cuenta administradora.
- Si aparece invalid target release 21, cambie el JDK de Maven a 21.
- Si el puerto es distinto, actualice la URL completa de conexión.

Para una conexión remota, configure `sslmode=verify-full` y el certificado de la autoridad correspondiente en la URL del controlador.
