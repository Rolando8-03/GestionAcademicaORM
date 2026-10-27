package edu.gestionacademica.model;

import java.util.Locale;
import java.util.regex.Pattern;

public final class EstudianteValidator {
    private static final Pattern CORREO = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");
    private EstudianteValidator() { }

    public static Estudiante crear(String nombres, String apellidos, String correo, String carrera, String telefono) {
        nombres = requerido(nombres, "Los nombres", 100);
        apellidos = requerido(apellidos, "Los apellidos", 100);
        correo = requerido(correo, "El correo", 150).toLowerCase(Locale.ROOT);
        carrera = requerido(carrera, "La carrera", 100);
        telefono = telefono == null ? "" : telefono.strip();
        if (!CORREO.matcher(correo).matches()) throw new IllegalArgumentException("Ingrese un correo válido, por ejemplo: estudiante@uam.edu.ni.");
        if (!telefono.isBlank() && !telefono.matches("[0-9]{8,20}"))
            throw new IllegalArgumentException("El teléfono debe contener entre 8 y 20 dígitos, sin espacios ni guiones.");
        return new Estudiante(nombres, apellidos, correo, carrera, telefono.isBlank() ? null : telefono);
    }

    private static String requerido(String value, String campo, int maximo) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(campo + " es un campo obligatorio.");
        String clean = value.strip();
        if (clean.length() > maximo) throw new IllegalArgumentException(campo + " admite como máximo " + maximo + " caracteres.");
        return clean;
    }
}
