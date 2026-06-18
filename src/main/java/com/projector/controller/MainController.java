package com.projector.controller;

import com.projector.service.PreviewService;
import com.projector.view.TelaStage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

/**
 * Controller da janela principal de controle.
 * Conecta os elementos do MainControl.fxml com a lógica da aplicação.
 */
public class MainController {

    // ═══════════════════════════════════
    // Elementos da interface (FXML)
    // ═══════════════════════════════════

    @FXML private Button btnPlay;
    @FXML private Button btnStop;
    @FXML private Button btnAdd;
    @FXML private Button btnRemove;
    @FXML private Button btnMoveUp;
    @FXML private Button btnMoveDown;

    @FXML private TextField txtUrl;
    @FXML private ListView<String> listPlaylist;
    @FXML private Label lblStatus;
    @FXML private Slider sliderVolume;
    @FXML private Label lblVolume;
    @FXML private StackPane previewPane;
    @FXML private Label lblPreview;
    private PreviewService previewService;

    private TelaStage telaStage;

    // ═══════════════════════════════════
    // Inicialização
    // ═══════════════════════════════════
    @FXML
    public void initialize() {
        setStatus("Aguardando...");

        previewService = new PreviewService();

        // Vincula o preview após o painel estar visível
        javafx.application.Platform.runLater(() -> {
            previewService.vincularPainel(previewPane);
        });

        // Listener do slider de volume
        sliderVolume.valueProperty().addListener((obs, oldVal, newVal) -> {
            int volume = newVal.intValue();
            lblVolume.setText(volume + "%");
            if (telaStage != null && telaStage.estaAberto()) {
                telaStage.getController().setVolume(volume);
            }
        });
    }

    // ═══════════════════════════════════
    // Ações dos botões
    // ═══════════════════════════════════

    @FXML
    private void onAddMedia() {
        String url = txtUrl.getText().trim();
        if (!url.isEmpty()) {
            listPlaylist.getItems().add(url);
            txtUrl.clear();
            setStatus("Mídia adicionada à fila.");
        }
    }

    @FXML
    private void onPlay() {
        String selected = listPlaylist.getSelectionModel().getSelectedItem();
        if (selected == null) {
            setStatus("Selecione uma mídia na fila.");
            return;
        }

        final String url = selected.replace("\"", "").trim();
        setStatus("⏳ Extraindo stream, aguarde...");
        btnPlay.setDisable(true);

        new Thread(() -> {
            try {
                // Se o telão não está aberto, abre primeiro
                if (telaStage == null || !telaStage.estaAberto()) {

                    // Abre o telão na thread JavaFX
                    javafx.application.Platform.runLater(() -> {
                        try {
                            telaStage = new TelaStage();
                            telaStage.mostrar();
                        } catch (Exception e) {
                            setStatus("Erro ao abrir telão: " + e.getMessage());
                        }
                    });

                    // Aguarda o telão estar pronto
                    Thread.sleep(1500);
                }

                // Reproduz UMA única vez
                boolean ok = telaStage.getController().reproduzir(url);

                // Reproduz também no preview
                previewService.reproduzir(url);

                javafx.application.Platform.runLater(() -> {
                    btnPlay.setDisable(false);
                    setStatus(ok ? "Reproduzindo: " + url : "Erro ao reproduzir.");
                });

            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    btnPlay.setDisable(false);
                    setStatus("Erro: " + e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    private void onStop() {
        if (telaStage != null && telaStage.estaAberto()) {
            previewService.parar();
            telaStage.getController().dispose();
            telaStage.fechar();
            telaStage = null;
        }
        setStatus("Reprodução encerrada.");
    }

    @FXML
    private void onRemove() {
        int index = listPlaylist.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            listPlaylist.getItems().remove(index);
            setStatus("Mídia removida da fila.");
        }
    }

    @FXML
    private void onMoveUp() {
        int index = listPlaylist.getSelectionModel().getSelectedIndex();
        if (index > 0) {
            String item = listPlaylist.getItems().remove(index);
            listPlaylist.getItems().add(index - 1, item);
            listPlaylist.getSelectionModel().select(index - 1);
        }
    }

    @FXML
    private void onMoveDown() {
        int index = listPlaylist.getSelectionModel().getSelectedIndex();
        if (index < listPlaylist.getItems().size() - 1) {
            String item = listPlaylist.getItems().remove(index);
            listPlaylist.getItems().add(index + 1, item);
            listPlaylist.getSelectionModel().select(index + 1);
        }
    }

    @FXML
    private void onOpenFile() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Selecionar Arquivo de Mídia");

        fileChooser.getExtensionFilters().addAll(
                new javafx.stage.FileChooser.ExtensionFilter(
                        "Arquivos de Vídeo",
                        "*.mp4", "*.avi", "*.mkv", "*.mov",
                        "*.wmv", "*.flv", "*.webm", "*.ts"
                ),
                new javafx.stage.FileChooser.ExtensionFilter(
                        "Todos os Arquivos", "*.*"
                )
        );

        java.io.File pastaInicial = new java.io.File(
                System.getProperty("user.home") + "/Videos"
        );
        if (pastaInicial.exists()) {
            fileChooser.setInitialDirectory(pastaInicial);
        }

        javafx.stage.Stage stage = (javafx.stage.Stage)
                btnAdd.getScene().getWindow();

        java.io.File arquivo = fileChooser.showOpenDialog(stage);

        if (arquivo != null) {
            listPlaylist.getItems().add(arquivo.getAbsolutePath());
            setStatus("Arquivo adicionado: " + arquivo.getName());
        }
    }

    // ═══════════════════════════════════
    // Utilitários
    // ═══════════════════════════════════

    private void setStatus(String message) {
        lblStatus.setText("● " + message);
    }
}