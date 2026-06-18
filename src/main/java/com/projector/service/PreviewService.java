package com.projector.service;

import javafx.scene.layout.StackPane;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

/**
 * Serviço de pré-visualização na janela de controle.
 * Usa um segundo player VLCJ vinculado ao painel de preview.
 */
public class PreviewService {

    private MediaPlayerFactory factory;
    private EmbeddedMediaPlayer mediaPlayer;
    private boolean inicializado = false;

    public PreviewService() {
        try {
            factory = new MediaPlayerFactory();
            mediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer();
            inicializado = true;
        } catch (Exception e) {
            System.err.println("Erro ao inicializar PreviewService: " + e.getMessage());
        }
    }

    /**
     * Vincula o player ao painel de pré-visualização.
     * Deve ser chamado após o painel estar visível.
     */
    public void vincularPainel(StackPane previewPane) {
        if (!inicializado) return;

        try {
            long hwnd = 0;
            for (var w : com.sun.glass.ui.Window.getWindows()) {
                if (w.getTitle() != null && w.getTitle().contains("Controle")) {
                    hwnd = w.getNativeHandle();
                    break;
                }
            }

            if (hwnd != 0) {
                // Obtém as coordenadas do painel dentro da janela
                var bounds = previewPane.localToScreen(
                        previewPane.getBoundsInLocal()
                );

                System.out.println("Preview painel vinculado!");

                var surface = factory.videoSurfaces().newVideoSurface(hwnd);
                mediaPlayer.videoSurface().set(surface);
            }

        } catch (Exception e) {
            System.err.println("Erro ao vincular preview: " + e.getMessage());
        }
    }

    /**
     * Reproduz a URL no preview.
     */
    public void reproduzir(String url) {
        if (!inicializado) return;
        try {
            mediaPlayer.media().play(url);
        } catch (Exception e) {
            System.err.println("Erro no preview: " + e.getMessage());
        }
    }

    /**
     * Para o preview.
     */
    public void parar() {
        if (!inicializado) return;
        mediaPlayer.controls().stop();
    }

    /**
     * Libera recursos.
     */
    public void dispose() {
        if (mediaPlayer != null) mediaPlayer.release();
        if (factory != null) factory.release();
    }
}