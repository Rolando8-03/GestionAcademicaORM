package edu.gestionacademica.model;

import jakarta.persistence.*;

@Entity
@Table(name = "estudiantes", uniqueConstraints = @UniqueConstraint(
        name = "uk_estudiantes_correo", columnNames = "correo"))
public class Estudiante {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 100)
    private String nombres;
    @Column(nullable = false, length = 100)
    private String apellidos;
    @Column(nullable = false, length = 150)
    private String correo;
    @Column(nullable = false, length = 100)
    private String carrera;
    @Column(length = 20)
    private String telefono;

    public Estudiante() { }

    public Estudiante(String nombres, String apellidos, String correo, String carrera) {
        this(nombres, apellidos, correo, carrera, null);
    }

    public Estudiante(String nombres, String apellidos, String correo, String carrera, String telefono) {
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correo = correo;
        this.carrera = carrera;
        this.telefono = telefono;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getCarrera() { return carrera; }
    public void setCarrera(String carrera) { this.carrera = carrera; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
