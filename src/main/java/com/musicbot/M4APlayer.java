package com.musicbot;

import javafx.application.Platform;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.embed.swing.JFXPanel;

public class M4APlayer {
    private MediaPlayer mediaPlayer;
    private boolean isPlaying;
    private static boolean jfxInitialized = false;

    public M4APlayer() {
        initializeJavaFX();
    }

    private void initializeJavaFX() {
        if (!jfxInitialized) {
            // JavaFXを初期化
            new JFXPanel();
            jfxInitialized = true;
        }
    }

    public void play(String url) {
        if (isPlaying) {
            stop();
        }

        Platform.runLater(() -> {
            try {
                Media media = new Media(url);
                mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setOnEndOfMedia(() -> {
                    isPlaying = false;
                    stop();
                });
                mediaPlayer.play();
                isPlaying = true;
            } catch (Exception e) {
                System.out.println("M4Aファイルの再生中にエラーが発生しました: " + e.getMessage());
                isPlaying = false;
            }
        });
    }

    public void stop() {
        if (mediaPlayer != null) {
            Platform.runLater(() -> {
                mediaPlayer.stop();
                mediaPlayer.dispose();
                isPlaying = false;
            });
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }
} 