package com.musicbot;

import javazoom.jl.player.Player;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class MusicPlayer {
    private Player mp3Player;
    private M4APlayer m4aPlayer;
    private boolean isPlaying;
    private Thread playerThread;
    private final GoogleDriveMusicProvider musicProvider;

    public MusicPlayer(GoogleDriveMusicProvider musicProvider) {
        this.isPlaying = false;
        this.musicProvider = musicProvider;
        this.m4aPlayer = new M4APlayer();
    }

    public void play(String url, String extension) {
        try {
            if (isPlaying) {
                stop();
            }

            URLConnection connection = new URL(url).openConnection();
            connection.addRequestProperty("Authorization", "Bearer " + musicProvider.getAccessToken());

            if ("m4a".equalsIgnoreCase(extension)) {
                // M4Aファイルの再生
                m4aPlayer.play(url);
                isPlaying = true;
            } else {
                // MP3ファイルの再生
                InputStream audioStream = new BufferedInputStream(connection.getInputStream());
                mp3Player = new Player(audioStream);
                isPlaying = true;

                // 別スレッドで再生
                playerThread = new Thread(() -> {
                    try {
                        mp3Player.play();
                    } catch (Exception e) {
                        System.out.println("音楽の再生中にエラーが発生しました: " + e.getMessage());
                    } finally {
                        isPlaying = false;
                        mp3Player.close();
                    }
                });
                playerThread.start();
            }
        } catch (Exception e) {
            System.out.println("音楽の再生中にエラーが発生しました: " + e.getMessage());
        }
    }

    public void stop() {
        if (mp3Player != null) {
            mp3Player.close();
            if (playerThread != null) {
                playerThread.interrupt();
                try {
                    playerThread.join(1000);
                } catch (InterruptedException e) {
                    // 無視
                }
            }
        }
        if (m4aPlayer != null) {
            m4aPlayer.stop();
        }
        isPlaying = false;
    }

    public boolean isPlaying() {
        return isPlaying;
    }
} 