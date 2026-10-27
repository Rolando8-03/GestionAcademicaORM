package edu.gestionacademica;

import edu.gestionacademica.config.HibernateUtil;
import edu.gestionacademica.controller.EstudianteController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    private EstudianteController controller;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("estudiante-view.fxml"));
        Scene scene = new Scene(loader.load(), 1180, 760);
        scene.getStylesheets().add(MainApp.class.getResource("estudiante.css").toExternalForm());
        controller = loader.getController();
        stage.setTitle("Gestión Académica");
        stage.setMinWidth(980);
        stage.setMinHeight(670);
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }

    @Override
    public void stop() {
        if (controller != null) controller.cerrar();
        HibernateUtil.shutdown();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
