# Sandip WA Automation

A small Android starter app that selects a phone contact, lets you write a message, and prepares a limited sequence of WhatsApp message drafts.

## Safety and behavior
- This app does **not** send messages unattended.
- Each tap opens WhatsApp with the selected number and prefilled text.
- You must review and tap **Send** in WhatsApp yourself.
- The count is limited to 20 prepared drafts per sequence.
- Only use it with people who are comfortable receiving your messages. Stop if they ask you to stop.

## Build an APK using GitHub (phone-friendly)
1. Create a new GitHub repository named `Sandip-WA-Automation`.
2. Upload all files and folders from this project ZIP to the repository root.
3. Open the repository's **Actions** tab.
4. Select **Build Android APK** and run the workflow, or push to `main`.
5. Open the completed workflow run and download the `Sandip-WA-Automation-debug` artifact.
6. Extract the artifact ZIP and install `app-debug.apk` on your Android phone. You may need to allow installation from your browser or file manager.

## Notes
- The app uses Android's contact picker, so it does not request broad contacts permission.
- Local 10-digit Indian numbers are prefixed with `91`. For other countries, verify the number format before opening WhatsApp.
- This is a starter project. It has not been built or tested on a physical device in this environment.
