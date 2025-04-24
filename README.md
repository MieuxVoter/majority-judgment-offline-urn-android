# Majority Judgment Offline Mobile Urn

[![Release](https://img.shields.io/github/v/release/MieuxVoter/majority-judgment-offline-urn-android?sort=semver&style=for-the-badge)](https://github.com/MieuxVoter/majority-judgment-offline-urn-android/releases)
[![License](https://img.shields.io/github/license/MieuxVoter/majority-judgment-offline-urn-android?style=for-the-badge)](./LICENSE)
[![GitHub Build Status](https://img.shields.io/github/actions/workflow/status/MieuxVoter/majority-judgment-offline-urn-android/android_build.yml?style=for-the-badge)](https://github.com/MieuxVoter/majority-judgment-offline-urn-android/actions/workflows/android_build.yml)
[![Weblate project translated](https://img.shields.io/weblate/progress/majority-judgment-offline-urn-android?server=https%3A%2F%2Fhosted.weblate.org&style=for-the-badge)](https://hosted.weblate.org/projects/majority-judgment-offline-urn-android/application/)
![GitHub top language](https://img.shields.io/github/languages/top/MieuxVoter/majority-judgment-offline-urn-android?style=for-the-badge)
[![F-Droid Version](https://img.shields.io/f-droid/v/com.illiouchine.jm?style=for-the-badge)](https://f-droid.org/en/packages/com.illiouchine.jm)
[![Join the Discord chat at https://discord.gg/rAAQG9S](https://img.shields.io/discord/705322981102190593.svg?style=for-the-badge)](https://discord.gg/rAAQG9S)

An application for Android 8.1 and greater that helps groups decide about things when offline, using a single phone.

> If you're looking for an online app, try https://app.mieuxvoter.fr/

## Features

- *Mobile Urn*: Set up a local, offline poll on your phone.
- *Vote with Subtlety*: Grade each proposal and so never throw a vote away.
- *Majority Judgment*: One of the most elegant ranking systems out there.
- *Libre Software*: We accept merge requests.
- *No anti-features*: no tracking, no ads.

<!--suppress CheckImageSize -->
<p>
  <img src="metadata/en-US/images/phoneScreenshots/1.png" alt="Screenshot of the onboarding screen of the application." width="270" />
  <img src="metadata/en-US/images/phoneScreenshots/2.png" alt="Screenshot of the voting screen of the application." width="270" />
  <img src="metadata/en-US/images/phoneScreenshots/3.png" alt="Screenshot of the results screen of the application." width="270" />
  <img src="metadata/en-US/images/phoneScreenshots/4.png" alt="Screenshot of the home screen of the application, in dark theme." width="270" />
</p>


## Tips

Since Android 11, you may use [app pinning](https://support.google.com/android/answer/9455138?hl=en) with this app if participants are expected to have gorilla fingers or sneaky paws.


## Download

### F-Droid

This Android app is available on [F-Droid](https://f-droid.org/en/packages/com.illiouchine.jm).

> This is the **recommended way to install**, as you'll benefit from automatic updates.

### Google Play

[Publication on Google Play](https://github.com/MieuxVoter/majority-judgment-offline-urn-android/issues/100) is in the works. 

[Try the open beta](https://play.google.com/apps/testing/fr.mieuxvoter.urn).

### Direct Download

[Download the latest release](https://github.com/MieuxVoter/majority-judgment-offline-urn-android/releases/latest/download/app-release.apk) of the app.

You can also download the debug `apk` from [the releases](https://github.com/MieuxVoter/majority-judgment-offline-urn-android/releases).

> Both are built by our Continuous Integration (CI) on each release.


## Contribute

> Follow the usual _git_ flow:
> Fork, clone, branch, hack, push and create a merge request.

### Code

This is an unremarkable _Android_ project in _Kotlin_, made with _Jetpack Compose_.

#### Tests

There are some rudimentary integration tests in _Gherkin_.
Run them from _Android Studio_, or with:

    ./gradlew clean connectedCheck

### Translations

We're using the amazing _Weblate_ for translations : https://hosted.weblate.org/projects/majority-judgment-offline-urn-android/application/

> You can add a new language or edit existing translations without ever touching any code.

If you're a nerd and do want to handle code, the `XML` translation files are in `app/src/main/res/values-<language>`.


## Inspiration

There used to be a similar app called _"Le Choix Commun"_.

The [comic by Marjolaine Leray](https://marjolaineleray.com/wp-content/uploads/2023/04/BD-MajorityJudgment-MarjolaineLeray-EN.pdf) is a good read about Majority Judgment.


## Additional info

Check out https://mieuxvoter.fr for more information about Majority Judgment.

