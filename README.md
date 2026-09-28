# Group Mention Buzzer

An Android app that starts a looping alarm only when WhatsApp's notification identifies both:

1. the one exact group name selected in the app; and
2. the configured mention text, such as `@Alex`.

## Use

1. Open the app and enter the WhatsApp group name exactly as it appears in its notification title.
2. Enter the mention text WhatsApp displays for you (for example, `@Alex`).
3. Tap **Save selected group**.
4. Tap **Grant notification access**, find *Group Mention Buzzer*, and allow access.
5. Send a test mention from the selected group. The persistent alarm notification has a **Stop alarm** action.

The app uses Android's notification listener API only; it does not access WhatsApp chats directly. If your phone hides WhatsApp message previews, Android will not provide enough text for mention matching.

## Get an APK without Android Studio

This project includes a GitHub Actions workflow that builds a debug APK in the cloud. The first build can be run manually after uploading this folder to a GitHub repository:

1. Create an empty repository on GitHub and upload these project files.
2. Open the repository's **Actions** tab and select **Build Android APK**.
3. Click **Run workflow** and wait for it to complete.
4. Open that run and download the `group-mention-buzzer-debug-apk` artifact. It contains `app-debug.apk`.

The artifact is retained for 14 days. Future pushes also create a new APK automatically.

## Optional local build

No Android Studio is required, but a local build needs Java 17 and the Android SDK. On Windows, run `gradlew.bat assembleDebug`; on macOS/Linux, run `./gradlew assembleDebug`. The APK is written to `app/build/outputs/apk/debug/app-debug.apk`.
