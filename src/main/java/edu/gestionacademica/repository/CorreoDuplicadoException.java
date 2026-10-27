package edu.gestionacademica.repository;

public class CorreoDuplicadoException extends RuntimeException {
    public CorreoDuplicadoException() {
        super("Ya existe un estudiante registrado con ese correo.");
    }
    public CorreoDuplicadoException(Throwable cause) {
        super("Ya existe un estudiante registrado con ese correo.", cause);
    }
}
