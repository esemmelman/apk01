# Founding Documents Quiz

A small, offline Android app with 20 multiple-choice questions: ten on the Bill of Rights and ten on the Declaration of Independence. Includes immediate explanations, answer review, topic scores, and a locally saved personal best. No account, ads, network permissions, or native libraries.

## Install

Download `Founding-Documents-Quiz.apk` from this repository's Releases onto your phone and open it. Android may ask you to allow installation from the browser or file manager used to open the APK. Requires Android 11 or later. Samsung security settings may require allowing sideloading. This is a self-signed APK, not a Play Store download.

## Build

Run `./build.ps1` in PowerShell with JDK 17, Android SDK platform 36, and build tools 36.0.0 installed. The SDK defaults to `C:/Android`; override with `-Sdk`. The script uses the JDK installation at `C:/Program Files/Java/jdk-17.0.18` for jar and keytool. Output: `dist/Founding-Documents-Quiz.apk`. A local signing key is stored outside the repository at `%USERPROFILE%/.android/apk01/quiz.jks`. Keep that key for future compatible updates. Its password is the standard local development password; this is a personal sideload build.

## Content sources

- https://www.archives.gov/founding-docs/bill-of-rights-transcript
- https://www.archives.gov/founding-docs/declaration-transcript

Questions summarize the documents for educational use.

## Validation

Compiled against Android API 36; DEX minimum API 30. APK signature and alignment verified. Question data checked for 20 questions, four choices per question, valid answers, and a 10/10 topic split. No phone or Android emulator was connected during the build; device installation and visual behavior remain untested.

Version 1.1 adds a colorful gradient background, rounded question cards, larger text (25sp answers and 35sp questions), taller answer buttons, and green/red answer feedback. It uses the original signing key so it can update version 1.0 in place. Device visual testing remains unperformed.
