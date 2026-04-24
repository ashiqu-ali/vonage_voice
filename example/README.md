# vonage_voice_example

## Integration Guide: Running the Demo App

This guide explains how to set up and run the `example/` demo for the `vonage_voice` plugin on a physical device.

---

### 1. Firebase Setup

- **Android:**
	- Download your `google-services.json` from the Firebase Console.
	- Place it in `example/android/app/google-services.json`.
- **iOS:**
	- Download your `GoogleService-Info.plist` from the Firebase Console.
	- Place it in `example/ios/Runner/GoogleService-Info.plist`.
- **Initialization:**
	- Ensure `Firebase.initializeApp()` is called in `example/lib/main.dart` before using any Firebase features.

---

### 2. Push Notification Setup

- **Android:**
	- FCM background message handling is implemented. Make sure your device has Google Play Services.
	- No extra steps are needed if `google-services.json` is present and Firebase is initialized.
- **iOS:**
	- Open `example/ios/Runner.xcworkspace` in Xcode.
	- Enable **PushKit** and **CallKit** capabilities for the `Runner` target (under "Signing & Capabilities").
	- Ensure push notification permissions are requested at runtime.

---

### 3. Mock Backend & JWT Handling

- The example app simulates a backend by parsing JWT tokens from a JSON structure.
- The expected JSON for `setTokens` is:

	```json
	{
		"user_jwt": "<Vonage JWT>",
		"app_id": "<Vonage App ID>"
	}
	```
- This mimics a real backend response. Update these values in the app as needed for your test user.

---

### 4. Environment Variables & Configuration

- If a `.env` file is present, update it with your Vonage App ID and JWT.
- If not, look for hardcoded config in `example/lib/` (e.g., `main.dart` or a config/constants file) and update the App ID/JWT values there.
- Ensure these credentials are valid for your Vonage project.

---

### 5. Onboarding Flow & Permissions

- The demo uses a `prepareAndroidOnboarding()` function to request all required permissions (microphone, notifications, etc.) on Android.
- This function is called during onboarding to ensure the app has the necessary access for voice and push features.
- On iOS, permissions are requested as needed at runtime.

---

**Run the app on a physical device for full push and call functionality.**
