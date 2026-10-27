package edu.gestionacademica.controller;

import edu.gestionacademica.model.Estudiante;
import edu.gestionacademica.model.EstudianteValidator;
import edu.gestionacademica.repository.CorreoDuplicadoException;
import edu.gestionacademica.repository.EstudianteRepository;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class EstudianteController {
    @FXML private TextField txtNombres, txtApellidos, txtCorreo, txtTelefono;
    @FXML private ComboBox<String> cmbCarrera, cmbFiltroCarrera;
    @FXML private Button btnGuardar, btnConsultar, btnLimpiar;
    @FXML private TableView<Estudiante> tablaEstudiantes;
    @FXML private TableColumn<Estudiante, Long> colId;
    @FXML private TableColumn<Estudiante, String> colNombres, colApellidos, colCorreo, colCarrera, colTelefono;
    @FXML private VBox formulario;
    @FXML private ProgressIndicator progreso;
    @FXML private Label lblEstado, lblCantidad;
    private static final String TODAS = "Todas las carreras";
    private final EstudianteRepository repository = new EstudianteRepository();
    private final ExecutorService executor = Executors.newSingleThreadExecutor(r -> {
        Thread thread = new Thread(r, "persistencia-estudiantes");
        thread.setDaemon(true);
        return thread;
    });
    private boolean cerrado;

    @FXML
    private void initialize() {
        List<String> carreras = List.of("Ingeniería de Sistemas de Información", "Ingeniería Electrónica",
                "Contabilidad y Finanzas", "Administración de Empresas", "Derecho", "Medicina");
        cmbCarrera.setItems(FXCollections.observableArrayList(carreras));
        cmbCarrera.setEditable(true);
        cmbFiltroCarrera.getItems().add(TODAS);
        cmbFiltroCarrera.getItems().addAll(carreras);
        cmbFiltroCarrera.setValue(TODAS);
        colId.setCellValueFactory(c -> new ReadOnlyObjectWrapper<>(c.getValue().getId()));
        colNombres.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getNombres()));
        colApellidos.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getApellidos()));
        colCorreo.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getCorreo()));
        colCarrera.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getCarrera()));
        colTelefono.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTelefono()));
        tablaEstudiantes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        tablaEstudiantes.setPlaceholder(new Label("No hay estudiantes para mostrar."));
        progreso.managedProperty().bind(progreso.visibleProperty());
        consultar();
    }

    @FXML
    private void guardar() {
        final Estudiante estudiante;
        try {
            estudiante = EstudianteValidator.crear(txtNombres.getText(), txtApellidos.getText(),
                    txtCorreo.getText(), cmbCarrera.getEditor().getText(), txtTelefono.getText());
        } catch (IllegalArgumentException error) {
            mostrar(Alert.AlertType.WARNING, "Revise los datos", error.getMessage());
            return;
        }
        ejecutar("Guardando estudiante…", () -> { repository.guardar(estudiante); return estudiante; }, saved -> {
            limpiar();
            if (!cmbCarrera.getItems().contains(saved.getCarrera())) cmbCarrera.getItems().add(saved.getCarrera());
            if (!cmbFiltroCarrera.getItems().contains(saved.getCarrera())) cmbFiltroCarrera.getItems().add(saved.getCarrera());
            cmbFiltroCarrera.setValue(TODAS);
            mostrar(Alert.AlertType.INFORMATION, "Registro guardado", "El estudiante se registró correctamente.");
            consultar();
        });
    }

    @FXML
    private void consultar() {
        String filtro = cmbFiltroCarrera.getValue();
        ejecutar("Consultando estudiantes…", () -> filtro == null || TODAS.equals(filtro)
                ? repository.listar() : repository.filtrarPorCarrera(filtro), registros -> {
            tablaEstudiantes.getItems().setAll(registros);
            registros.stream().map(Estudiante::getCarrera).distinct().forEach(carrera -> {
                if (!cmbFiltroCarrera.getItems().contains(carrera)) cmbFiltroCarrera.getItems().add(carrera);
            });
            lblCantidad.setText(registros.size() + (registros.size() == 1 ? " estudiante" : " estudiantes"));
            lblEstado.setText("Consulta completada");
        });
    }

    @FXML
    private void limpiar() {
        txtNombres.clear(); txtApellidos.clear(); txtCorreo.clear(); txtTelefono.clear();
        cmbCarrera.getSelectionModel().clearSelection();
        cmbCarrera.getEditor().clear();
        txtNombres.requestFocus();
    }

    private <T> void ejecutar(String mensaje, Callable<T> action, Consumer<T> success) {
        if (cerrado) return;
        ocupado(true);
        lblEstado.setText(mensaje);
        Task<T> task = new Task<>() {
            @Override protected T call() throws Exception { return action.call(); }
        };
        task.setOnSucceeded(event -> {
            if (cerrado) return;
            ocupado(false);
            success.accept(task.getValue());
        });
        task.setOnFailed(event -> {
            if (cerrado) return;
            ocupado(false);
            task.getException().printStackTrace();
            lblEstado.setText("La operación no se completó");
            mostrar(Alert.AlertType.ERROR, "No se pudo completar la operación", mensajeError(task.getException()));
        });
        executor.execute(task);
    }

    private void ocupado(boolean value) {
        formulario.setDisable(value);
        btnGuardar.setDisable(value); btnConsultar.setDisable(value); btnLimpiar.setDisable(value);
        cmbFiltroCarrera.setDisable(value);
        progreso.setVisible(value);
    }

    private String mensajeError(Throwable error) {
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            if (cause instanceof CorreoDuplicadoException) return cause.getMessage();
            if (cause instanceof SQLException sql) {
                if ("28P01".equals(sql.getSQLState())) return "PostgreSQL rechazó el usuario o la contraseña. Revise la configuración local.";
                if ("3D000".equals(sql.getSQLState())) return "No existe la base gestionacademica. Ejecute sql/01_crear_base.sql desde la base postgres.";
                if (sql.getSQLState() != null && sql.getSQLState().startsWith("08"))
                    return "No hay conexión con PostgreSQL. Revise el servicio, la dirección y el puerto configurado.";
            }
            if (cause instanceof IllegalStateException && cause.getMessage() != null
                    && cause.getMessage().startsWith("Configure el usuario")) return cause.getMessage();
        }
        return "Revise PostgreSQL y la configuración de conexión. Consulte la consola para conocer el detalle técnico.";
    }

    private void mostrar(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title); alert.setHeaderText(null); alert.setContentText(message);
        if (tablaEstudiantes.getScene() != null) alert.initOwner(tablaEstudiantes.getScene().getWindow());
        alert.showAndWait();
    }

    public void cerrar() { cerrado = true; executor.shutdownNow(); }
}
