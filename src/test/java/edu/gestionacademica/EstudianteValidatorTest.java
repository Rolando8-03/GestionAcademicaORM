package edu.gestionacademica;

import edu.gestionacademica.model.EstudianteValidator;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EstudianteValidatorTest {
    @Test void normalizaCorreoYAdmiteTelefonoOpcional() {
        var estudiante = EstudianteValidator.crear(" Ana ", " Ruiz ", " ANA@UAM.EDU.NI ", "Derecho", "");
        assertEquals("Ana", estudiante.getNombres());
        assertEquals("ana@uam.edu.ni", estudiante.getCorreo());
        assertNull(estudiante.getTelefono());
    }
    @Test void rechazaCamposVacios() {
        assertThrows(IllegalArgumentException.class, () -> EstudianteValidator.crear(" ", "Ruiz", "a@b.com", "Derecho", ""));
        assertThrows(IllegalArgumentException.class, () -> EstudianteValidator.crear("Ana", "Ruiz", "a@b.com", null, ""));
    }
    @Test void rechazaCorreoIncompletoOTelefonoInvalido() {
        assertThrows(IllegalArgumentException.class, () -> EstudianteValidator.crear("Ana", "Ruiz", "ana@uam", "Derecho", ""));
        assertThrows(IllegalArgumentException.class, () -> EstudianteValidator.crear("Ana", "Ruiz", "a@b.com", "Derecho", "1234abcd"));
        assertThrows(IllegalArgumentException.class, () -> EstudianteValidator.crear("Ana", "Ruiz", "a@b.com", "Derecho", "123"));
    }
    @Test void rechazaLongitudesFueraDelMapeo() {
        assertThrows(IllegalArgumentException.class, () -> EstudianteValidator.crear("a".repeat(101), "Ruiz", "a@b.com", "Derecho", ""));
    }
}
