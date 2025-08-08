package com.finqube.iso20022.examples.fx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Minimal JavaFX example application.
 */
public class ExamplesJavaFxApp extends Application {

    /**
     * Starts the JavaFX application and renders a simple window.
     *
     * @param primaryStage the primary stage for this application
     */
    @Override
    public void start(Stage primaryStage) {
        Label label = new Label("Spring Finqube JavaFX Example");
        Button quit = new Button("Quit");
        quit.setOnAction(e -> primaryStage.close());
        VBox root = new VBox(10, label, quit);
        primaryStage.setTitle("Finqube JavaFX");
        primaryStage.setScene(new Scene(root, 360, 160));
        primaryStage.show();
    }

    /**
     * Main entry point for launching the JavaFX example.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
