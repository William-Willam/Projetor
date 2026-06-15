package com.projector.view;

import com.projector.controller.TelaController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.geometry.Rectangle2D;

import java.io.IOException;

/**
 * Gerencia a janela do telão.
 * Abre em tela cheia no monitor externo quando disponível.
 */
public class TelaStage {

    private Stage stage;
    private TelaController controller;

    public TelaStage() throws IOException {

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/projector/fxml/Tela.fxml")
        );

        Scene scene = new Scene(loader.load());
        scene.getStylesheets().add(
                getClass().getResource("/com/projector/css/style.css").toExternalForm()
        );

        controller = loader.getController();

        stage = new Stage();
        stage.setTitle("ProjetorApp — Telão");
        stage.setScene(scene);
        stage.initStyle(StageStyle.UNDECORATED); // Sem barra de título

        // Tenta abrir no segundo monitor, senão usa o principal
        posicionarNoMonitor();
    }

    /**
     * Posiciona a janela no segundo monitor se disponível,
     * caso contrário usa o monitor principal.
     */
    private void posicionarNoMonitor() {
        var screens = Screen.getScreens();

        Screen alvo = screens.size() > 1
                ? screens.get(1)   // Segundo monitor (telão)
                : screens.get(0);  // Monitor principal

        Rectangle2D bounds = alvo.getBounds();
        stage.setX(bounds.getMinX());
        stage.setY(bounds.getMinY());
        stage.setWidth(bounds.getWidth());
        stage.setHeight(bounds.getHeight());
        stage.setFullScreen(true);
    }

    public void mostrar() {
        stage.show();

        // Aguarda a janela renderizar e vincula o player
        javafx.application.Platform.runLater(() -> {
            controller.vincularJanela(stage);
        });
    }

    public void fechar() {
        stage.close();
    }

    public boolean estaAberto() {
        return stage.isShowing();
    }

    public TelaController getController() {
        return controller;
    }

    public Stage getStage() {
        return stage;
    }
}