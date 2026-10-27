package edu.gestionacademica;

import edu.gestionacademica.model.Estudiante;
import edu.gestionacademica.repository.CorreoDuplicadoException;
import edu.gestionacademica.repository.EstudianteRepository;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.*;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class EstudianteRepositoryTest {
    private SessionFactory factory;
    private EstudianteRepository repository;

    @BeforeEach void crearBaseTemporal() {
        factory = new Configuration().addAnnotatedClass(Estudiante.class)
                .setProperty("hibernate.connection.driver_class", "org.h2.Driver")
                .setProperty("hibernate.connection.url", "jdbc:h2:mem:" + UUID.randomUUID() + ";MODE=PostgreSQL")
                .setProperty("hibernate.connection.username", "sa")
                .setProperty("hibernate.connection.password", "")
                .setProperty("hibernate.hbm2ddl.auto", "create-drop")
                .setProperty("hibernate.show_sql", "false").buildSessionFactory();
        repository = new EstudianteRepository(() -> factory);
    }
    @AfterEach void cerrarBaseTemporal() { if (factory != null) factory.close(); }

    @Test void guardaGeneraIdYRecuperaTodosLosCampos() {
        var estudiante = new Estudiante("Ana", "Ruiz", "ana@uam.edu.ni", "Derecho", "88881234");
        repository.guardar(estudiante);
        assertNotNull(estudiante.getId());
        var guardado = repository.listar().getFirst();
        assertEquals("Ana", guardado.getNombres());
        assertEquals("Ruiz", guardado.getApellidos());
        assertEquals("ana@uam.edu.ni", guardado.getCorreo());
        assertEquals("Derecho", guardado.getCarrera());
        assertEquals("88881234", guardado.getTelefono());
    }
    @Test void evitaDuplicadoYPermiteGuardarDespuesDelRollback() {
        repository.guardar(new Estudiante("Ana", "Ruiz", "ana@uam.edu.ni", "Derecho"));
        assertThrows(CorreoDuplicadoException.class, () -> repository.guardar(
                new Estudiante("Otra", "Persona", " ANA@UAM.EDU.NI ", "Medicina")));
        repository.guardar(new Estudiante("Luis", "Mena", "luis@uam.edu.ni", "Medicina"));
        assertEquals(2, repository.listar().size());
    }
    @Test void filtraConParametrosSinInterpretarElTextoComoHql() {
        repository.guardar(new Estudiante("Ana", "Ruiz", "ana@uam.edu.ni", "Derecho"));
        repository.guardar(new Estudiante("Luis", "Mena", "luis@uam.edu.ni", "Medicina"));
        assertEquals(1, repository.filtrarPorCarrera("Derecho").size());
        assertTrue(repository.filtrarPorCarrera("Derecho' or '1'='1").isEmpty());
    }
    @Test void baseImpideCorreoDuplicadoFueraDelRepositorio() {
        repository.guardar(new Estudiante("Ana", "Ruiz", "ana@uam.edu.ni", "Derecho"));
        try (var session = factory.openSession()) {
            var tx = session.beginTransaction();
            assertThrows(RuntimeException.class, () -> {
                session.persist(new Estudiante("Otra", "Persona", "ana@uam.edu.ni", "Medicina"));
                tx.commit();
            });
            if (tx.isActive()) tx.rollback();
        }
        assertEquals(1, repository.listar().size());
    }
}
