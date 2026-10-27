package edu.gestionacademica.config;

import edu.gestionacademica.model.Estudiante;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class HibernateUtil {
    private static SessionFactory factory;
    private static boolean closed;

    private HibernateUtil() { }

    public static synchronized SessionFactory getSessionFactory() {
        if (closed) throw new IllegalStateException("La aplicación se está cerrando.");
        if (factory == null) factory = crear();
        return factory;
    }

    private static SessionFactory crear() {
        Properties properties = new Properties();
        try {
            try (InputStream in = HibernateUtil.class.getClassLoader()
                    .getResourceAsStream("hibernate.properties")) {
                if (in == null) throw new IllegalStateException("Falta hibernate.properties.");
                properties.load(in);
            }
            Path local = Path.of("config", "hibernate-local.properties");
            if (Files.exists(local)) {
                try (InputStream in = Files.newInputStream(local)) { properties.load(in); }
            }
            override(properties, "DB_URL", "hibernate.connection.url");
            override(properties, "DB_USER", "hibernate.connection.username");
            override(properties, "DB_PASSWORD", "hibernate.connection.password");
            if (properties.getProperty("hibernate.connection.username", "").isBlank()
                    || properties.getProperty("hibernate.connection.password", "").isBlank()) {
                throw new IllegalStateException("Configure el usuario y la contraseña en config/hibernate-local.properties o en DB_USER y DB_PASSWORD.");
            }
            return new Configuration().addProperties(properties)
                    .addAnnotatedClass(Estudiante.class).buildSessionFactory();
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo iniciar Hibernate. Revise la configuración y el servicio PostgreSQL.", e);
        }
    }

    private static void override(Properties properties, String variable, String key) {
        String value = System.getenv(variable);
        if (value != null) properties.setProperty(key, value);
    }

    public static synchronized void shutdown() {
        closed = true;
        if (factory != null && !factory.isClosed()) factory.close();
    }
}
