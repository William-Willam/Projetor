package com.projector.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Serviço que usa o yt-dlp para extrair a URL real do stream do YouTube.
 */
public class YtDlpService {

    private static final String YT_DLP_PATH = "D:\\Program Files\\VLC\\yt-dlp.exe";
    private static final String DENO_PATH = "D:\\Users\\William\\.deno\\bin\\deno.exe";

    /**
     * Extrai a URL do melhor stream disponível para o vídeo/live.
     */
    public String extrairStreamUrl(String youtubeUrl) {
        try {
            ProcessBuilder pb = new ProcessBuilder(
                    YT_DLP_PATH,
                    "--js-runtimes", "deno:" + DENO_PATH,
                    "--no-update",
                    "-f", "best[ext=mp4]/best",
                    "-g",
                    youtubeUrl
            );

            pb.redirectErrorStream(true);
            Process process = pb.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );

            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
                System.out.println("[yt-dlp] " + line);
            }

            int exitCode = process.waitFor();
            System.out.println("[yt-dlp] Exit code: " + exitCode);

            if (exitCode == 0) {
                String result = output.toString().trim();
                String[] lines = result.split("\n");
                for (String l : lines) {
                    if (l.startsWith("http")) {
                        return l.trim();
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Erro ao executar yt-dlp: " + e.getMessage());
        }

        return null;
    }

    /**
     * Verifica se a URL é do YouTube.
     */
    public boolean isYouTube(String url) {
        return url != null && (
                url.contains("youtube.com") ||
                        url.contains("youtu.be")
        );
    }
}