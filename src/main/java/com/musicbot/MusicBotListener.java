package com.musicbot;

import java.io.IOException;

public class MusicBotListener {
    private final GoogleDriveMusicProvider musicProvider;
    private final MusicPlayer musicPlayer;

    public MusicBotListener(String credentialsPath) {
        try {
            this.musicProvider = new GoogleDriveMusicProvider(credentialsPath);
            this.musicPlayer = new MusicPlayer(musicProvider);
        } catch (Exception e) {
            throw new RuntimeException("初期化中にエラーが発生しました: " + e.getMessage(), e);
        }
    }

    public void onCommand(String command) {
        if (command.startsWith("!play ")) {
            String query = command.substring(6);
            try {
                musicProvider.searchAndPlay(query, url -> {
                    if (url != null) {
                        musicPlayer.play(url);
                    } else {
                        System.out.println("音楽ファイルが見つかりませんでした。");
                    }
                });
            } catch (IOException e) {
                System.out.println("エラーが発生しました: " + e.getMessage());
            }
        } else if (command.equals("!stop")) {
            if (musicPlayer.isPlaying()) {
                musicPlayer.stop();
                System.out.println("再生を停止しました。");
            } else {
                System.out.println("再生中の音楽はありません。");
            }
        }
    }
} 