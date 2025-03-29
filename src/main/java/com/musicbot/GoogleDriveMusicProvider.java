package com.musicbot;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class GoogleDriveMusicProvider {
    private final Drive driveService;
    private static final java.io.File TOKENS_DIRECTORY_PATH = new java.io.File("tokens");
    private Credential credential;

    public GoogleDriveMusicProvider(String credentialsPath) 
            throws IOException, GeneralSecurityException {
        this.credential = initializeCredential(credentialsPath);
        this.driveService = initializeDriveService();
    }

    private Credential initializeCredential(String credentialsPath) throws IOException, GeneralSecurityException {
        // クライアントシークレットを読み込む
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
            GsonFactory.getDefaultInstance(), 
            new InputStreamReader(new FileInputStream(credentialsPath))
        );

        // 認証フローを構築
        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            clientSecrets,
            Collections.singleton(DriveScopes.DRIVE_READONLY))
            .setDataStoreFactory(new FileDataStoreFactory(TOKENS_DIRECTORY_PATH))
            .setAccessType("offline")
            .build();

        // ユーザー認証を実行
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
    }

    private Drive initializeDriveService() throws IOException, GeneralSecurityException {
        return new Drive.Builder(
            GoogleNetHttpTransport.newTrustedTransport(),
            GsonFactory.getDefaultInstance(),
            credential)
            .setApplicationName("MusicBot")
            .build();
    }

    public void searchAndPlay(String query, Consumer<String> onUrlFound) throws IOException {
        List<File> files = driveService.files().list()
                .setQ("name contains '" + query + "' and (mimeType contains 'audio/' or mimeType contains 'video/')")
                .setSpaces("drive")
                .execute()
                .getFiles();

        if (files.isEmpty()) {
            System.out.println("音楽ファイルが見つかりませんでした。");
            return;
        }

        File musicFile = files.get(0);
        String downloadUrl = String.format("https://www.googleapis.com/drive/v3/files/%s?alt=media", musicFile.getId());
        onUrlFound.accept(downloadUrl);
    }

    public String getAccessToken() throws IOException {
        credential.refreshToken();
        return credential.getAccessToken();
    }
} 