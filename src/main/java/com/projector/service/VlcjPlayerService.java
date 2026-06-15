package com.projector.service;

import javafx.stage.Stage;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

/**
 * Serviço de reprodução usando VLCJ.
 * Integra o VLC nativo com a janela JavaFX do telão.
 */
public class VlcjPlayerService {

    private MediaPlayerFactory factory;
    private EmbeddedMediaPlayer mediaPlayer;
    private boolean inicializado = false;

    public VlcjPlayerService() {
        try {
            factory = new MediaPlayerFactory();
            mediaPlayer = factory.mediaPlayers().newEmbeddedMediaPlayer();
            inicializado = true;
        } catch (Exception e) {
            System.err.println("Erro ao inicializar VLCJ: " + e.getMessage());
        }
    }

    /**
     * Vincula o player à janela do telão usando o handle nativo.
     */
    public void vincularJanela(Stage stage) {
        if (!inicializado) return;

        try {
            long hwnd = 0;
            System.out.println("Janelas disponíveis:");
            for (var w : com.sun.glass.ui.Window.getWindows()) {
                System.out.println("  - Título: " + w.getTitle() +
                        " | Handle: " + w.getNativeHandle());
                if (w.getTitle() != null && w.getTitle().contains("Telão")) {
                    hwnd = w.getNativeHandle();
                }
            }

            if (hwnd != 0) {
                var surface = factory.videoSurfaces().newVideoSurface(hwnd);
                mediaPlayer.videoSurface().set(surface);
                System.out.println("Vinculado! Handle: " + hwnd);
            } else {
                System.err.println("Handle não encontrado!");
            }

        } catch (Exception e) {
            System.err.println("Erro ao vincular: " + e.getMessage());
        }
    }

    /**
     * Reproduz uma URL.
     */
    public boolean reproduzir(String url) {
        if (!inicializado) return false;
        try {
            mediaPlayer.media().play(url);
            return true;
        } catch (Exception e) {
            System.err.println("Erro ao reproduzir: " + e.getMessage());
            return false;
        }
    }

    /**
     * Para a reprodução.
     */
    public void parar() {
        if (!inicializado) return;
        mediaPlayer.controls().stop();
    }

    /**
     * Pausa ou retoma.
     */
    public void pausar() {
        if (!inicializado) return;
        mediaPlayer.controls().pause();
    }

    /**
     * Verifica se está reproduzindo.
     */
    public boolean estaReproduzindo() {
        if (!inicializado) return false;
        return mediaPlayer.status().isPlaying();
    }

    /**
     * Libera os recursos ao fechar.
     */
    public void dispose() {
        if (mediaPlayer != null) mediaPlayer.release();
        if (factory != null) factory.release();
    }
}