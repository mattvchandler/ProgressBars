# ProgressBars

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
     alt="Get it on F-Droid"
     height="80">](https://f-droid.org/packages/org.mattvchandler.progressbars/)
[<img src="https://play.google.com/intl/en_us/badges/images/generic/en-play-badge.png"
     alt="Get it on Google Play"
     height="80">](https://play.google.com/store/apps/details?id=org.mattvchandler.progressbars)

ProgressBars is a simple timer / countdown app for Android.

![screenshot](/metadata/en-US/images/phoneScreenshots/scrn_phone_land_1.png?raw=true)

### Basic Features
* Countdown/up to/from a specified time
* Percentage complete for a time interval
* Notifications on timer completion
* Swipe to delete timers
* Drag to reorder timers
* Show time remaining / elapsed in any combination of:
    * Years
    * Months
    * Weeks
    * Days
    * Hours
    * Minutes
    * Seconds

## Note for recent versions of Android
Android has had several changes in how notifications are handled, especially
restricting the ability to send a notification at an exact time, and what apps
are able to run in the background in the name of reducing battery usage. For
ProgressBars, this impacts the ability to send notifications for timer
completion reliably. I recommend giving ProgressBars Unrestricted battery usage
if you are going to rely on notifications from this app. ProgressBars consumes
very little battery with typical usage, even with optimizations turned off.

### Android 12/12L
By default, apps can't set exact alarms on Android 12/12L. Timer notifications
may go off seconds to minutes after the timer itself ends. If this will be a
problem for you, you will need to grant the special "Alarms & reminders"
permission to ProgressBars

* This limitation did not exist in android versions prior to 12
* Android 13, and later have a different permission mechanism to send exact alarms
