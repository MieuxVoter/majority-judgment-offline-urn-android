# Contributors' Guide

This is a standard Android project using Jetpack Compose.

We welcome merge requests, but they must respect the goals of this app.


## Goals

- Offline
- No trackers
- Fast to use
- Efficient
- Lightweight (~2.7 Mio at this time)
- …


## Translate

Translating the app happens on _Weblate_.

[Start translating the _Majority Judgment Urn_ app now !](https://hosted.weblate.org/projects/majority-judgment-offline-urn-android)


## Contribute

We follow the usual _git_ flow.

1. Fork the project
2. Clone the project
3. Create a branch
4. Hack
5. Submit a Merge Request


### Publish a new version

1. Edit both `versionCode` and `versionName` in `app/build.gradle.kts`.
2. Add a changelog in `metadata/en-US/changelogs`.
3. Commit.
4. Git Tag with a version name such as `v1.5.6` (we prefix with a `v`).
5. Push.
6. _???_
7. Profit!
