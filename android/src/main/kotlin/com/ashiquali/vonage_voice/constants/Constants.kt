package com.ashiquali.vonage_voice.constants

object Constants {

    // ── Method channel argument keys ──────────────────────────────────────
    const val PARAM_JWT          = "jwt"
    const val PARAM_FCM_TOKEN    = "deviceToken"
    const val PARAM_TO           = "to"
    const val PARAM_FROM         = "from"
    const val PARAM_CALL_ID      = "callId"
    const val PARAM_DIGITS       = "digits"
    const val PARAM_SPEAKER_IS_ON = "speakerIsOn"
    const val PARAM_BLUETOOTH_ON  = "bluetoothOn"
    const val PARAM_MUTED        = "muted"
    const val PARAM_DEFAULT_CALLER = "defaultCaller"
    const val PARAM_CLIENT_ID    = "id"
    const val PARAM_CLIENT_NAME  = "name"
    const val PARAM_SHOW         = "show"
    const val PARAM_SHOULD_REJECT = "shouldReject"
    const val PARAM_DEVICE_ID      = "deviceId"

    // ── SharedPreferences ─────────────────────────────────────────────────
    const val PREFS_NAME                  = "com.ashiquali.vonage_voicePreferences"
    const val KEY_DEFAULT_CALLER          = "defaultCaller"
    const val KEY_REJECT_ON_NO_PERMISSIONS = "rejectOnNoPermissions"
    const val KEY_SHOW_NOTIFICATIONS      = "show-notifications"
    const val CLIENT_ID_PREFIX             = "client_"

    // ── Default display values ────────────────────────────────────────────
    const val DEFAULT_UNKNOWN_CALLER = "Unknown"

    // ── Foreground service notification ───────────────────────────────────
    const val NOTIFICATION_ID           = 101
    const val NOTIFICATION_CHANNEL_ID   = "vonage_voice_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Vonage Voice Calls"
    const val INCOMING_CALL_CHANNEL_ID   = "vonage_incoming_call_channel_v2"
    const val INCOMING_CALL_CHANNEL_NAME = "Vonage Incoming Calls"
    /** Old channel ID that had sound/vibration — delete on startup. */
    const val INCOMING_CALL_CHANNEL_ID_OLD = "vonage_incoming_call_channel"

    // ── Intent actions: Flutter → TVConnectionService ─────────────────────
    const val ACTION_INCOMING_CALL      = "com.ashiquali.vonage_voice.ACTION_INCOMING_CALL"
    const val ACTION_CANCEL_CALL_INVITE = "com.ashiquali.vonage_voice.ACTION_CANCEL_CALL_INVITE"
    const val ACTION_PLACE_OUTGOING_CALL = "com.ashiquali.vonage_voice.ACTION_PLACE_OUTGOING_CALL"
    const val ACTION_ANSWER             = "com.ashiquali.vonage_voice.ACTION_ANSWER"
    const val ACTION_HANGUP             = "com.ashiquali.vonage_voice.ACTION_HANGUP"
    const val ACTION_SEND_DIGITS        = "com.ashiquali.vonage_voice.ACTION_SEND_DIGITS"
    const val ACTION_TOGGLE_SPEAKER     = "com.ashiquali.vonage_voice.ACTION_TOGGLE_SPEAKER"
    const val ACTION_TOGGLE_BLUETOOTH   = "com.ashiquali.vonage_voice.ACTION_TOGGLE_BLUETOOTH"
    const val ACTION_TOGGLE_MUTE        = "com.ashiquali.vonage_voice.ACTION_TOGGLE_MUTE"
    const val ACTION_CLEANUP            = "com.ashiquali.vonage_voice.ACTION_CLEANUP"

    // ── Intent extra keys ─────────────────────────────────────────────────
    const val EXTRA_CALL_ID         = "EXTRA_CALL_ID"
    const val EXTRA_CALL_FROM       = "EXTRA_CALL_FROM"
    const val EXTRA_CALL_TO         = "EXTRA_CALL_TO"
    const val EXTRA_JWT             = "EXTRA_JWT"
    const val EXTRA_OUTGOING_PARAMS = "EXTRA_OUTGOING_PARAMS"
    const val EXTRA_DIGITS          = "EXTRA_DIGITS"
    const val EXTRA_SPEAKER_STATE   = "EXTRA_SPEAKER_STATE"
    const val EXTRA_BLUETOOTH_STATE = "EXTRA_BLUETOOTH_STATE"
    const val EXTRA_MUTE_STATE      = "EXTRA_MUTE_STATE"
    const val EXTRA_FCM_DATA        = "EXTRA_FCM_DATA"
    const val EXTRA_CALL_DIRECTION   = "EXTRA_CALL_DIRECTION"

    // ── LocalBroadcast actions: service → VonageVoicePlugin ──────────────
    const val BROADCAST_CALL_RINGING           = "com.ashiquali.vonage_voice.CALL_RINGING"
    const val BROADCAST_CALL_CONNECTED         = "com.ashiquali.vonage_voice.CALL_CONNECTED"
    const val BROADCAST_CALL_ANSWERED          = "com.ashiquali.vonage_voice.CALL_ANSWERED"
    const val BROADCAST_CALL_ENDED             = "com.ashiquali.vonage_voice.CALL_ENDED"
    const val BROADCAST_CALL_INVITE            = "com.ashiquali.vonage_voice.CALL_INVITE"
    const val BROADCAST_CALL_INVITE_CANCELLED  = "com.ashiquali.vonage_voice.CALL_INVITE_CANCELLED"
    const val BROADCAST_CALL_RECONNECTING      = "com.ashiquali.vonage_voice.CALL_RECONNECTING"
    const val BROADCAST_CALL_RECONNECTED       = "com.ashiquali.vonage_voice.CALL_RECONNECTED"
    const val BROADCAST_CALL_FAILED            = "com.ashiquali.vonage_voice.CALL_FAILED"
    const val BROADCAST_MUTE_STATE             = "com.ashiquali.vonage_voice.MUTE_STATE"
    const val BROADCAST_SPEAKER_STATE          = "com.ashiquali.vonage_voice.SPEAKER_STATE"
    const val BROADCAST_BLUETOOTH_STATE        = "com.ashiquali.vonage_voice.BLUETOOTH_STATE"
    const val BROADCAST_SYSTEM_DISCONNECT      = "com.ashiquali.vonage_voice.SYSTEM_DISCONNECT"
    const val BROADCAST_NEW_FCM_TOKEN          = "com.ashiquali.vonage_voice.NEW_FCM_TOKEN"
    const val BROADCAST_PERMISSION_RESULT      = "com.ashiquali.vonage_voice.PERMISSION_RESULT"
    /** Sent by TVConnectionService when the real callId is available after placeholder startup. */
    const val BROADCAST_REAL_CALL_READY        = "com.ashiquali.vonage_voice.REAL_CALL_READY"

    const val ACTION_NOTIFICATION_ANSWER  = "com.ashiquali.vonage_voice.NOTIFICATION_ANSWER"
    const val ACTION_NOTIFICATION_DECLINE = "com.ashiquali.vonage_voice.NOTIFICATION_DECLINE"
}