# スマホだけでGitHub ActionsからAPKを作る

1. GitHubで新しいPublicまたはPrivateリポジトリ `swipe-panel-android` を作る（READMEの自動作成はオフ）。
2. このZIPを展開し、**中のファイルとフォルダ**をリポジトリのルートにアップロードする。`.github/workflows/build-apk.yml` も含める。GitHubモバイルWebのアップロードで隠しフォルダが選べない場合は、PC表示やGitHub Webのファイル作成画面で `.github/workflows/build-apk.yml` を作成する。
3. `Actions` タブで `Build Swipe Panel APK` を開き、`Run workflow` → `Run workflow` を押す。mainへのpushでも自動起動。
4. 緑のチェックになったら実行結果を開き、Artifactsの `SwipePanel-debug-APK` をダウンロード。ZIP内の `app-debug.apk` をインストールする。
5. インストール後、アプリで「他のアプリの上に表示」「ユーザー補助サービス」を有効にする。ユーザー補助サービスはスワイプ実行に使うため、信頼できる自作アプリだけに許可する。

注意: このAPKはデバッグ署名。将来の正式リリースで署名鍵が変わると上書き更新できない。ビルドの成功や実機動作は未検証。
