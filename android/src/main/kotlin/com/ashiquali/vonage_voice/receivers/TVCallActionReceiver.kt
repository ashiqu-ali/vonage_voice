package com.ashiquali.vonage_voice.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import com.ashiquali.vonage_voice.constants.Constants
import com.ashiquali.vonage_voice.service.TVConnectionService

class TVCallActionReceiver : android.content.BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val callId = intent.getStringExtra(_root_ide_package_.com.ashiquali.vonage_voice.constants.Constants.EXTRA_CALL_ID) ?: return

        val serviceIntent = Intent(context, _root_ide_package_.com.ashiquali.vonage_voice.service.TVConnectionService::class.java).apply {
            action = when (intent.action) {
                _root_ide_package_.com.ashiquali.vonage_voice.constants.Constants.ACTION_NOTIFICATION_ANSWER  -> _root_ide_package_.com.ashiquali.vonage_voice.constants.Constants.ACTION_ANSWER
                _root_ide_package_.com.ashiquali.vonage_voice.constants.Constants.ACTION_NOTIFICATION_DECLINE -> _root_ide_package_.com.ashiquali.vonage_voice.constants.Constants.ACTION_HANGUP
                else -> return
            }
            putExtra(_root_ide_package_.com.ashiquali.vonage_voice.constants.Constants.EXTRA_CALL_ID, callId)
        }
        // Must use startForegroundService — on Android 10+ the
        // notification tap runs in a background context.
        ContextCompat.startForegroundService(context, serviceIntent)
    }
}