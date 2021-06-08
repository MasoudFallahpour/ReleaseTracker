# Release Tracker

![GitHub release (latest by date)](https://img.shields.io/github/v/release/masoodfallahpoor/ReleaseTracker?label=Latest%20version&style=flat-square)

Release Tracker is an Android app that shows the latest version of Github-hosted libraries, tools,
frameworks, or whatever!

By default it's pre-populated with a list of some popular Android libraries, and you can add your
own libraries too.

# Screenshots

![Screenshots](/screenshots/screenshots_1.png?raw=true "Screenshots")
![Screenshots](/screenshots/screenshots_2.png?raw=true "Screenshots")

# How it works
There is a [worker](https://developer.android.com/topic/libraries/architecture/workmanager) that runs 3 times a day. In each run, for each library, it connects to the [Github REST API](https://docs.github.com/en/free-pro-team@latest/rest) and fetches the latest version of the library.

# How to build
To build the app, clone the repository and import it into Android Studio. Then add the following line to `local.properties` with your own [Github personal access token](https://github.com/settings/tokens):

`accessToken = YOUR_ACCESS_TOKEN`

Now you're good to go. Good luck!

If you want to track the latest version of AndroidX libraries then take a look at [Jetpack Release Tracker](https://github.com/lmj0011/jetpack-release-tracker).

# Technology Stack
Release Tracker uses (almost) the latest and greatest technologies of Android development. The following list highlights
the tech stack of Release Tracker:
- MVVM
- Compose
- ViewModel
- Coroutines
- LiveData
- Room
- Navigation
- Dagger Hilt
- WorkManager
- Retrofit
- Timber

# Unit Tests

I've done my best to write unit tests for different parts of the app. I'll keep adding tests to
reach a decent amount of code coverage.

# Contributing

Pull requests are welcome! Please make your pull requests against the `dev` branch. If you have a
feature request or bug report, please open a new issue so it could be tracked.

License
=======

    Copyright 2020 Masood Fallahpoor.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
