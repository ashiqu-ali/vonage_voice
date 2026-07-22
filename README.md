<p align="center">
  <a href="https://pub.dev/packages/vonage_voice">
    <img src="https://img.shields.io/pub/v/vonage_voice?color=blueviolet"/>
  </a>
  <img src="https://img.shields.io/badge/Platform-Flutter-blue?logo=flutter"/>
  <a href="https://github.com/ashiqu-ali/vonage_voice/blob/main/LICENSE">
    <img src="https://img.shields.io/github/license/ashiqu-ali/vonage_voice"/>
  </a>
</p>

# vonage_voice

A Flutter plugin for making and receiving voice calls using the Vonage Client SDK. It integrates natively with Android's Telecom `ConnectionService` and iOS CallKit, delivering full-screen incoming call screens, background call delivery via FCM/PushKit, and complete in-call controls — all through a clean Dart API.

<p align="center">
  <a href="https://www.buymeacoffee.com/ashiqu.ali">
    <img src="https://img.buymeacoffee.com/button-api/?text=Buy%20me%20a%20coffee&emoji=%E2%98%95&slug=ashiqu.ali&button_colour=FFDD00&font_colour=000000&font_family=Lato&outline_colour=000000&coffee_colour=ffffff"/>
  </a>
</p>

---

## ✨ Features

- 📞 Place and receive VoIP calls on Android and iOS
- 🔔 Background incoming call delivery via FCM (Android) and PushKit (iOS)
- 📱 Native call screen — Android Telecom `ConnectionService` + iOS CallKit
- 🔒 Secure JWT-based authentication (no credentials stored in the app)
- 🔇 Mute / unmute microphone during a live call
- 🔊 Toggle speakerphone and Bluetooth audio routing
- ⌨️ Send DTMF tones for IVR navigation
- 📡 Rich `CallEvent` stream covering incoming, ringing, connected, reconnecting, ended, missed, and more
- ⚡ Drop-in migration path from the Twilio Voice Flutter plugin (`TwilioVoice.instance` → `VonageVoice.instance`)

---

## 🏗️ Architecture Flow

```
┌──────────────┐         ┌───────────────┐         ┌──────────────────┐
│ Your Backend │ ──JWT─▶ │  Flutter App  │ ──Call─▶│ Vonage Platform  │
└──────────────┘         └───────────────┘         └──────────────────┘
       ▲                        │                           │
       │                        ▼                           ▼
(Mint Auth Token)      (Native SDK / Telecom)       (Route Media / Push)
```

The Flutter client never talks to Vonage directly for authentication. Your own backend mints a short-lived JWT using your Vonage Application ID and `private.key`, then returns it to the app. The app passes that token to `VonageVoice.instance.setTokens()` to establish the session.

---

## 🛠️ Vonage Dashboard Setup

Before writing any code, configure a Vonage application:

1. **Create a Vonage Developer account** at [dashboard.nexmo.com](https://dashboard.nexmo.com).
2. Go to **Applications → Create a new application**.
3. Give it a name, then toggle **Voice** capability on.
4. Set your **Answer URL** and **Event URL** (HTTP endpoints on your backend that Vonage calls to fetch your NCCO and receive call state webhooks).
5. Click **Generate public and private key** — this downloads `private.key`. Store it securely on your backend; never ship it in a mobile app.
6. Save the application and note your **Application ID**.

---

## 📦 Installation

```bash
flutter pub add vonage_voice
```

### Android Setup

The plugin's `AndroidManifest.xml` is merged automatically, so manifest entries and static permissions are already declared. You must still complete the Firebase configuration, request runtime permissions, and enable system-level settings during your app's onboarding flow.

#### 1. Firebase Configuration

**Add the `google-services` plugin** to your app-level `build.gradle` (required for FCM incoming calls):

```groovy
// android/app/build.gradle
apply plugin: 'com.google.gms.google-services'
```

And in your project-level `build.gradle`:

```groovy
// android/build.gradle
buildscript {
    dependencies {
        classpath 'com.google.gms:google-services:4.4.0'
    }
}
```

**Add `google-services.json`** — Download it from your Firebase project and place it at `android/app/google-services.json`. This enables FCM so incoming calls are delivered when the app is in the background or killed.

> The plugin requires **minSdkVersion 24** or higher. Set this in `android/app/build.gradle`:
> ```groovy
> defaultConfig {
>     minSdkVersion 24
> }
> ```

#### 2. Runtime Permissions

The permissions below are declared in the plugin's manifest but **must be requested at runtime** by your app. Missing any of them will silently break call delivery or audio.

| Permission | API Level | Purpose |
|---|---|---|
| `RECORD_AUDIO` | All | Microphone access — required for call audio |
| `READ_PHONE_STATE` | All | Integration with Android Telecom services |
| `CALL_PHONE` | All | Required for placing outgoing calls through Telecom |
| `MANAGE_OWN_CALLS` | All | Essential for self-managed `ConnectionService` integration |
| `READ_PHONE_NUMBERS` | All | Device and OEM compatibility with PhoneAccount flows |
| `POST_NOTIFICATIONS` | API 33+ (Android 13+) | Required for incoming call and missed call notifications |

Request them during your onboarding flow using the plugin helpers:

```dart
await VonageVoice.instance.requestMicAccess();
await VonageVoice.instance.requestReadPhoneStatePermission();
await VonageVoice.instance.requestCallPhonePermission();
await VonageVoice.instance.requestManageOwnCallsPermission();
await VonageVoice.instance.requestReadPhoneNumbersPermission();
await VonageVoice.instance.requestNotificationPermission();
```

#### 3. System Settings

Beyond runtime permissions, several OS-level settings must be enabled for the native call stack to function reliably. These cannot be granted silently — each one opens a system settings page for the user to confirm.

| Setting | Why It Is Required | Plugin Method |
|---|---|---|
| **Phone Account Registration** | Android Telecom requires a registered `PhoneAccount` to route calls through `ConnectionService` | `registerPhoneAccount()` |
| **Phone Account Enabled** | Users can disable the account in system settings; calls will not surface if it is disabled | `isPhoneAccountEnabled()` / `openPhoneAccountSettings()` |
| **Battery Optimization Exemption** | Android will kill the app's background process on restricted OEMs, preventing incoming call delivery | `requestBatteryOptimizationExemption()` |
| **Overlay Permission** | Improves lock-screen launch reliability on Samsung, MIUI, and similar OEM skins | `openOverlaySettings()` |

> ⚠️ **OEM Background Restrictions:** Devices from Vivo, OPPO, Xiaomi, realme, and Samsung ship aggressive battery managers that will kill your app's background process even with `FOREGROUND_SERVICE` declared. Battery optimization exemption is **not optional** on these devices — without it, incoming calls will not be delivered in the killed state.

> 💡 **Tip:** Check each setting before prompting — only open the system page if the current state requires action. This avoids unnecessary interruptions during onboarding.

#### 4. Onboarding Code Example

Call this function once during your login or onboarding flow, **before** calling `setTokens()`:

```dart
Future<void> prepareAndroidOnboarding() async {
  // Step 1 — Runtime permissions
  await VonageVoice.instance.requestMicAccess();
  await VonageVoice.instance.requestReadPhoneStatePermission();
  await VonageVoice.instance.requestCallPhonePermission();
  await VonageVoice.instance.requestManageOwnCallsPermission();
  await VonageVoice.instance.requestReadPhoneNumbersPermission();
  await VonageVoice.instance.requestNotificationPermission();

  // Step 2 — Phone account (required for Telecom integration)
  if (!await VonageVoice.instance.hasRegisteredPhoneAccount()) {
    await VonageVoice.instance.registerPhoneAccount();
  }
  if (!await VonageVoice.instance.isPhoneAccountEnabled()) {
    await VonageVoice.instance.openPhoneAccountSettings();
  }

  // Step 3 — Battery optimization (critical for background/killed-state delivery)
  if (await VonageVoice.instance.isBatteryOptimized()) {
    await VonageVoice.instance.requestBatteryOptimizationExemption();
  }

  // Step 4 — Overlay permission (Samsung/MIUI lock-screen reliability)
  if (!await VonageVoice.instance.canDrawOverlays()) {
    await VonageVoice.instance.openOverlaySettings();
  }
}
```

### iOS Setup

**1. Add capabilities** in Xcode for your `Runner` target:
   - **Background Modes** → enable `Voice over IP` and `Remote notifications`
   - **Push Notifications**

**2. Add keys to `Info.plist`**:

```xml
<!-- ios/Runner/Info.plist -->
<key>NSMicrophoneUsageDescription</key>
<string>Microphone access is required for voice calls.</string>
```

**3. Minimum deployment target:** **iOS 15.0**.

> **iOS 15 is required by the Vonage Client SDK.** The Vonage Voice SDK 2.x
> (Swift Package and CocoaPod) declares an iOS 15 minimum, so this plugin
> targets iOS 15.0 on both integration paths. iOS 13/14 are no longer supported.

The plugin depends on `VonageClientSDKVoice ~> 2.3`, `CallKit`, `PushKit`, and
`AVFoundation` — all pulled in automatically by whichever integration path you use.

**4. Choose an integration path — CocoaPods or Swift Package Manager.**

The plugin supports **both**; pick whichever your app uses. You do not need to
change any Dart or Swift code — the choice only affects how the native
dependency is resolved.

<details open>
<summary><strong>CocoaPods</strong> (default)</summary>

No extra steps. `flutter build ios` / `flutter run` resolves the plugin and the
Vonage SDK through your app's `Podfile` automatically. Ensure your app's
`Podfile` targets iOS 15:

```ruby
# ios/Podfile
platform :ios, '15.0'
```
</details>

<details>
<summary><strong>Swift Package Manager</strong></summary>

Enable Flutter's SPM integration once, then build as usual:

```bash
flutter config --enable-swift-package-manager
flutter build ios
```

Flutter resolves the plugin's `Package.swift` and pulls the official Vonage
Swift Package (`VonageClientSDKVoice`) automatically. You can confirm it
appears under **Package Dependencies** in Xcode's Project Navigator.

To switch back to CocoaPods at any time:

```bash
flutter config --no-enable-swift-package-manager
```
</details>

> **Migration note for existing users:** nothing changes unless you opt into
> SPM — CocoaPods remains fully supported. The only behavioral change is the
> iOS 15 minimum, which comes from the Vonage SDK, not from SPM.

---

## 🚀 Quick Start

### 1. Initialize — Register JWT and device token

```dart
import 'dart:io';

import 'package:firebase_messaging/firebase_messaging.dart';
import 'package:vonage_voice/vonage_voice.dart';

Future<void> registerForCalls() async {
  final jwt = await yourBackend.getVonageJwt();

  String? deviceToken;
  if (Platform.isAndroid) {
    deviceToken = await FirebaseMessaging.instance.getToken();
  }

  await VonageVoice.instance.setTokens(
    accessToken: jwt,
    deviceToken: deviceToken,
    isSandbox: false, // set true for iOS development/debug builds
  );
}
```

### 2. Listen to call events

```dart
VonageVoice.instance.callEventsListener.listen((CallEvent event) {
  switch (event) {
    case CallEvent.incoming:
      final call = VonageVoice.instance.call.activeCall;
      print('Incoming call from ${call?.fromFormatted}');
      break;
    case CallEvent.connected:
      print('Call connected');
      break;
    case CallEvent.callEnded:
      print('Call ended');
      break;
    case CallEvent.missedCall:
      print('Missed call');
      break;
    default:
      break;
  }
});
```

### 3. Place an outgoing call

```dart
await VonageVoice.instance.call.place(
  from: 'alice',           // your Vonage user identity
  to: '+14155551234',      // E.164 phone number or Vonage user identity
  extraOptions: {          // optional — forwarded to your NCCO answer URL
    'displayName': 'Alice',
  },
);
```

### 4. Answer / hang up

```dart
// Answer an incoming call
await VonageVoice.instance.call.answer();

// Hang up (works for both inbound and outbound)
await VonageVoice.instance.call.hangUp();
```

### 5. In-call controls

```dart
// Mute / unmute microphone
await VonageVoice.instance.call.toggleMute(true);
await VonageVoice.instance.call.toggleMute(false);

// Toggle speakerphone
await VonageVoice.instance.call.toggleSpeaker(true);
await VonageVoice.instance.call.toggleSpeaker(false);
```

### 6. Handle token refresh

```dart
// Called when FCM (Android) or PushKit (iOS) rotates the push token
VonageVoice.instance.setOnDeviceTokenChanged((String newToken) {
  myBackend.updatePushToken(newToken);
});

// Proactively refresh an expiring JWT (keep session alive)
final freshJwt = await yourBackend.getVonageJwt();
await VonageVoice.instance.refreshSession(accessToken: freshJwt);
```

### 7. Sign out

```dart
await VonageVoice.instance.unregister();
```

---

## 📖 API Reference

### `VonageVoice.instance` — Session management

| Method | Parameters | Returns | Description |
|---|---|---|---|
| `setTokens` | `accessToken` *(required)*, `deviceToken`, `isSandbox` | `Future<bool?>` | Register Vonage JWT and push token. Must be called before any call activity. |
| `refreshSession` | `accessToken` *(required)* | `Future<bool?>` | Refresh an expiring JWT without tearing down the session. |
| `unregister` | — | `Future<bool?>` | Unregister push token and end the Vonage session. |
| `getDeviceId` | — | `Future<String?>` | The Vonage device ID assigned during registration, or `null` if not yet registered. Useful for targeting a specific device from your backend. |
| `callEventsListener` | — | `Stream<CallEvent>` | Stream of typed call state events from the native layer. |
| `setOnDeviceTokenChanged` | `OnDeviceTokenChanged` callback | `void` | Fires when the native push token is rotated by FCM or PushKit. |
| `showMissedCallNotifications` | `bool` (setter) | `void` | Enable/disable local missed call notifications. |

### `VonageVoice.instance.call` — Active call controls

| Method | Parameters | Returns | Description |
|---|---|---|---|
| `place` | `from` *(required)*, `to` *(required)*, `extraOptions` | `Future<bool?>` | Place an outbound call. |
| `answer` | — | `Future<bool?>` | Answer the current incoming call. |
| `hangUp` | — | `Future<bool?>` | Hang up the active call. |
| `toggleMute` | `isMuted: bool` | `Future<bool?>` | Mute or unmute the microphone. |
| `isMuted` | — | `Future<bool?>` | Returns `true` if the microphone is currently muted. |
| `toggleSpeaker` | `speakerIsOn: bool` | `Future<bool?>` | Route audio to/from speakerphone. |
| `activeCall` | — | `ActiveCall?` | The current call's state. Returns `null` when idle. |

### `ActiveCall` — Call state object

| Property | Type | Description |
|---|---|---|
| `from` | `String` | Raw caller identity (`client:` prefix stripped). |
| `fromFormatted` | `String` | Human-readable caller number or identity. |
| `to` | `String` | Raw callee identity. |
| `toFormatted` | `String` | Human-readable callee number or identity. |
| `callDirection` | `CallDirection` | `.incoming` or `.outgoing`. |
| `initiated` | `DateTime?` | When the call connected. Set after `CallEvent.connected`. |
| `customParams` | `Map<String, dynamic>?` | Custom params forwarded from your Vonage backend NCCO. |

### `CallEvent` — Event enum values

| Value | Description |
|---|---|
| `incoming` | Incoming call invite received |
| `ringing` | Outbound call is ringing |
| `connected` | Call media connected — both parties can speak |
| `reconnecting` | Call media is recovering from a network interruption |
| `reconnected` | Call media reconnected |
| `callEnded` | Call ended (local hangup or remote disconnect) |
| `mute` / `unmute` | Microphone muted/unmuted |
| `speakerOn` / `speakerOff` | Speakerphone toggled |
| `bluetoothOn` / `bluetoothOff` | Bluetooth audio routing changed |
| `answer` | Incoming call answered |
| `declined` | Incoming call declined by remote |
| `missedCall` | Incoming call ended before answered |
| `deviceLimitExceeded` | Registration failed — max devices limit reached |
| `permission` | A runtime permission result received |
| `audioRouteChanged` | Audio route changed (iOS only) |

---

## 🧯 Troubleshooting (iOS)

**"CocoaPods could not find compatible versions for VonageClientSDKVoice"**
Your app's `Podfile.lock` is pinned to an older SDK. Run
`pod update VonageClientSDKVoice` (or `pod install --repo-update`) in
`ios/`.

**SPM: `Package Dependencies` doesn't appear / plugin not found**
Make sure SPM is enabled (`flutter config --enable-swift-package-manager`),
then `flutter clean && flutter pub get && flutter build ios`.

**Deployment target errors (`iOS 13`/`iOS 14`)**
The Vonage 2.x SDK requires iOS 15. Set `platform :ios, '15.0'` in your
`Podfile` and raise the Runner target to iOS 15 in Xcode.

**Switching between CocoaPods and SPM**
Toggle with `flutter config --enable-swift-package-manager` /
`--no-enable-swift-package-manager`, then `flutter clean` before rebuilding.
Only one path is active per build — there is no duplicate-symbol risk.

## ❓ FAQ

**Do I have to migrate to Swift Package Manager?**
No. CocoaPods is fully supported and remains the default. SPM is additive.

**Will enabling SPM change plugin behavior?**
No. Both paths resolve the same Vonage SDK generation and register the same
plugin class — the choice only affects dependency resolution.

**Known limitation:** iOS 13 and 14 are no longer supported (Vonage 2.x SDK
requirement).

---

## 🤝 Contributing

This is an open-source, community-driven project. We welcome contributions, bug reports, and feature requests!
Feel free to fork the repository, open an issue, or submit a pull request.

## 🐛 Issues & Feedback

If you encounter any issues or build errors, please [open an issue](https://github.com/ashiqu-ali/vonage_voice/issues) on GitHub.

---

**Disclaimer:** This package is a community-driven project and is **not** an official SDK provided or maintained by Vonage.

<p align="center">
  Built with ❤️ by <a href="https://www.linkedin.com/in/ashiqu-ali">Ashiqu Ali</a>
</p>

## 🌐 Connect

<p align="center">
  <a href="https://www.linkedin.com/in/ashiqu-ali">
    <img src="https://cdn-icons-png.flaticon.com/512/174/174857.png" width="30"/>
  </a>
  &nbsp;&nbsp;
  <a href="https://ashiqu-ali.medium.com/">
    <img src="https://cdn-icons-png.flaticon.com/512/5968/5968906.png" width="30"/>
  </a>
  &nbsp;&nbsp;
  <a href="https://www.instagram.com/ashiqu_ali">
    <img src="https://cdn-icons-png.flaticon.com/512/174/174855.png" width="30"/>
  </a>
  &nbsp;&nbsp;
  <a href="https://x.com/ashiquali007">
    <img src="https://cdn-icons-png.flaticon.com/512/733/733579.png" width="30"/>
  </a>
</p>
