package com.musicbot;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class MusicBotY {
    private final GoogleDriveMusicProvider musicProvider;
    private final MusicPlayer musicPlayer;
    private final JFrame frame;
    private final JTextField searchField;
    private final JButton playButton;
    private final JButton stopButton;

    public MusicBotY(String credentialsPath) throws IOException, GeneralSecurityException {
        this.musicProvider = new GoogleDriveMusicProvider(credentialsPath);
        this.musicPlayer = new MusicPlayer(musicProvider);
        
        // GUIの設定
        frame = new JFrame("MusicBotY");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 150);
        frame.setLayout(new BorderLayout(10, 10));

        // 検索パネル
        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchField = new JTextField();
        searchPanel.add(searchField, BorderLayout.CENTER);
        frame.add(searchPanel, BorderLayout.NORTH);

        // ボタンパネル
        JPanel buttonPanel = new JPanel(new FlowLayout());
        playButton = new JButton("再生");
        stopButton = new JButton("停止");
        buttonPanel.add(playButton);
        buttonPanel.add(stopButton);
        frame.add(buttonPanel, BorderLayout.CENTER);

        // イベントリスナーの設定
        playButton.addActionListener(e -> searchAndPlay());
        stopButton.addActionListener(e -> musicPlayer.stop());

        frame.setVisible(true);
    }

    private void searchAndPlay() {
        String query = searchField.getText();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "検索キーワードを入力してください。");
            return;
        }

        try {
            musicProvider.searchAndPlay(query, url -> {
                if (url != null) {
                    musicPlayer.play(url);
                } else {
                    JOptionPane.showMessageDialog(frame, "音楽ファイルが見つかりませんでした。");
                }
            });
        } catch (IOException e) {
            JOptionPane.showMessageDialog(frame, "エラーが発生しました: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("使用方法: java MusicBotY <credentials_path>");
            return;
        }

        SwingUtilities.invokeLater(() -> {
            try {
                new MusicBotY(args[0]);
            } catch (IOException | GeneralSecurityException e) {
                System.err.println("エラーが発生しました: " + e.getMessage());
                System.exit(1);
            }
        });
    }
} 