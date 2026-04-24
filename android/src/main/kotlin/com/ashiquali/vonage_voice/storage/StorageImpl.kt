package com.ashiquali.vonage_voice.storage

import android.content.Context
import android.content.SharedPreferences
import com.ashiquali.vonage_voice.constants.Constants

/**
 * Concrete implementation of [Storage] backed by Android [SharedPreferences].
 *
 * All data is stored under a single named preference file:
 *   "com.ashiquali.vonage_voicePreferences"
 *
 * Usage in VonageVoicePlugin:
 *   private lateinit var storage: Storage
 *   storage = StorageImpl(context)
 */
class StorageImpl(context: Context) : Storage {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        _root_ide_package_.com.ashiquali.vonage_voice.constants.Constants.PREFS_NAME,
        Context.MODE_PRIVATE
    )

    // ── Default caller ────────────────────────────────────────────────────

    override fun getDefaultCaller(): String =
        prefs.getString(_root_ide_package_.com.ashiquali.vonage_voice.constants.Constants.KEY_DEFAULT_CALLER, _root_ide_package_.com.ashiquali.vonage_voice.constants.Constants.DEFAULT_UNKNOWN_CALLER)
            ?: _root_ide_package_.com.ashiquali.vonage_voice.constants.Constants.DEFAULT_UNKNOWN_CALLER

    override fun setDefaultCaller(name: String) {
        prefs.edit().putString(_root_ide_package_.com.ashiquali.vonage_voice.constants.Constants.KEY_DEFAULT_CALLER, name).apply()
    }

    // ── Caller identity registry ──────────────────────────────────────────

    /**
     * Caller IDs are stored directly as keys in SharedPreferences.
     * The key is the caller ID string, the value is the display name.
     *
     * Example stored entry:
     *   key   = "user_123"
     *   value = "Alice Johnson"
     */
    override fun addRegisteredClient(id: String, name: String) {
        prefs.edit().putString(_root_ide_package_.com.ashiquali.vonage_voice.constants.Constants.CLIENT_ID_PREFIX + id, name).apply()
    }

    override fun getRegisteredClient(id: String): String? =
        prefs.getString(_root_ide_package_.com.ashiquali.vonage_voice.constants.Constants.CLIENT_ID_PREFIX + id, null)

    override fun removeRegisteredClient(id: String) {
        prefs.edit().remove(_root_ide_package_.com.ashiquali.vonage_voice.constants.Constants.CLIENT_ID_PREFIX + id).apply()
    }

    // ── Behaviour flags ───────────────────────────────────────────────────

    override fun shouldRejectCallOnNoPermissions(): Boolean =
        prefs.getBoolean(_root_ide_package_.com.ashiquali.vonage_voice.constants.Constants.KEY_REJECT_ON_NO_PERMISSIONS, false)

    override fun setRejectCallOnNoPermissions(shouldReject: Boolean) {
        prefs.edit().putBoolean(_root_ide_package_.com.ashiquali.vonage_voice.constants.Constants.KEY_REJECT_ON_NO_PERMISSIONS, shouldReject).apply()
    }

    override fun shouldShowNotifications(): Boolean =
        prefs.getBoolean(_root_ide_package_.com.ashiquali.vonage_voice.constants.Constants.KEY_SHOW_NOTIFICATIONS, true)

    override fun setShowNotifications(show: Boolean) {
        prefs.edit().putBoolean(_root_ide_package_.com.ashiquali.vonage_voice.constants.Constants.KEY_SHOW_NOTIFICATIONS, show).apply()
    }
}