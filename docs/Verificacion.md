# Verificación del proyecto

- Compilación con JDK 21: correcta.
- Maven clean test: 8 pruebas aprobadas, sin fallos ni errores.
- Persistencia: creación del esquema, inserción y recuperación de los campos sobre H2 en modo PostgreSQL.
- Duplicados: rechazo del correo repetido y nueva inserción después del rollback.
- Restricción de base: rechazo de inserciones duplicadas fuera del repositorio.
- Filtro: consulta HQL parametrizada por carrera.
- Validación: obligatorios, correo, teléfono y longitud de campos.
- FXML y CSS: carga y renderización comprobadas con JavaFX en entorno sin pantalla.

La conexión con PostgreSQL y las evidencias de pgAdmin deben comprobarse en el equipo del estudiante. No se incluyen capturas que simulen esas pruebas.
