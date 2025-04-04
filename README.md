# MusicBotY

DiscordでGoogle Driveから音楽を再生できるボットです。

## 機能

- Google Driveから音楽ファイルを検索して再生
- スラッシュコマンドによる操作
  - `/play <検索キーワード>` - 音楽を再生
  - `/stop` - 再生を停止
  - `/skip` - 現在の曲をスキップ
  - `/list` - 現在再生中の曲を表示

## セットアップ手順

1. 必要な環境
   - Java 17以上
   - Maven
   - Discord Bot Token
   - Google Drive API認証情報

2. Discord Botの設定
   - [Discord Developer Portal](https://discord.com/developers/applications)で新しいアプリケーションを作成
   - Botセクションでボットを作成し、トークンを取得
   - 必要な権限を有効化（Send Messages, Connect, Speak, Use Voice Activity）
   - `MusicBot.java`の`TOKEN`変数に取得したトークンを設定

3. Google Drive APIの設定
   - [Google Cloud Console](https://console.cloud.google.com/)で新しいプロジェクトを作成
   - Drive APIを有効化
   - 認証情報を作成し、JSONファイルとしてダウンロード
   - `MusicBot.java`の`CREDENTIALS_FILE_PATH`変数にJSONファイルのパスを設定

4. ビルドと実行
   ```bash
   mvn clean package
   java -jar target/MusicBotY-1.0-SNAPSHOT.jar
   ```

## 注意事項

- Google Driveの音楽ファイルは、ボットがアクセスできるように共有設定が必要です
- サポートされている音楽形式: MP3, WAV, OGG, FLAC, M4A, AAC
- ボットを使用するには、Discordサーバーに招待する必要があります
