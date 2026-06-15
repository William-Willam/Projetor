package com.projector.app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Classe principal do JavaFX.
 * Responsável por inicializar a aplicação e carregar a tela principal.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) throws IOException {

        // Carrega o arquivo FXML da janela de controle
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/projector/fxml/MainControl.fxml")
        );

        // Define o tamanho da janela do operador
        Scene scene = new Scene(loader.load(), 1000, 650);

        // Carrega o CSS
        scene.getStylesheets().add(
                getClass().getResource("/com/projector/css/style.css").toExternalForm()
        );

        stage.setTitle("ProjetorApp — Controle");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(600);
        stage.show();
    }
}