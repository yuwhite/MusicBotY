package com.musicbot;

import javazoom.jl.player.Player;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class MusicPlayer {
    private Player player;
    private boolean isPlaying;
    private Thread playerThread;
    private final GoogleDriveMusicProvider musicProvider;

    public MusicPlayer(GoogleDriveMusicProvider musicProvider) {
        this.isPlaying = false;
        this.musicProvider = musicProvider;
    }

    public void play(String url) {
        try {
            if (isPlaying) {
                stop();
            }

            URLConnection connection = new URL(url).openConnection();
            connection.addRequestProperty("Authorization", "Bearer " + musicProvider.getAccessToken());
            InputStream audioStream = new BufferedInputStream(connection.getInputStream());
            
            player = new Player(audioStream);
            isPlaying = true;

            // 別スレッドで再生
            playerThread = new Thread(() -> {
                try {
                    player.play();
                } catch (Exception e) {
                    System.out.println("音楽の再生中にエラーが発生しました: " + e.getMessage());
                } finally {
                    isPlaying = false;
                    player.close();
                }
            });
            playerThread.start();

        } catch (Exception e) {
            System.out.println("音楽の再生中にエラーが発生しました: " + e.getMessage());
        }
    }

    public void stop() {
        if (player != null) {
            player.close();
            if (playerThread != null) {
                playerThread.interrupt();
                try {
                    playerThread.join(1000);
                } catch (InterruptedException e) {
                    // 無視
                }
            }
            isPlaying = false;
        }
    }

    public boolean isPlaying() {
        return isPlaying;
    }
} 