package edu.gestionacademica.repository;

import edu.gestionacademica.config.HibernateUtil;
import edu.gestionacademica.model.Estudiante;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class EstudianteRepository {
    private final Supplier<SessionFactory> factory;

    public EstudianteRepository() { this(HibernateUtil::getSessionFactory); }
    public EstudianteRepository(Supplier<SessionFactory> factory) { this.factory = factory; }

    public void guardar(Estudiante estudiante) {
        estudiante.setCorreo(estudiante.getCorreo().strip().toLowerCase(Locale.ROOT));
        try (Session session = factory.get().openSession()) {
            Transaction tx = session.beginTransaction();
            try {
                Long coincidencias = session.createQuery(
                        "select count(e) from Estudiante e where lower(e.correo) = :correo", Long.class)
                        .setParameter("correo", estudiante.getCorreo()).getSingleResult();
                if (coincidencias > 0) throw new CorreoDuplicadoException();
                session.persist(estudiante);
                tx.commit();
            } catch (RuntimeException error) {
                try { if (tx.isActive()) tx.rollback(); }
                catch (RuntimeException rollbackError) { error.addSuppressed(rollbackError); }
                if (esCorreoDuplicado(error)) throw new CorreoDuplicadoException(error);
                throw error;
            }
        }
    }

    public List<Estudiante> listar() {
        try (Session session = factory.get().openSession()) {
            return session.createQuery("from Estudiante e order by e.id", Estudiante.class).getResultList();
        }
    }

    public List<Estudiante> filtrarPorCarrera(String carrera) {
        try (Session session = factory.get().openSession()) {
            return session.createQuery("from Estudiante e where e.carrera = :carrera order by e.id", Estudiante.class)
                    .setParameter("carrera", carrera).getResultList();
        }
    }

    private boolean esCorreoDuplicado(Throwable error) {
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            if (cause instanceof ConstraintViolationException violation
                    && "23505".equals(violation.getSQLState())) {
                String constraint = violation.getConstraintName();
                return constraint != null && constraint.toLowerCase(Locale.ROOT).contains("uk_estudiantes_correo");
            }
        }
        return false;
    }
}
