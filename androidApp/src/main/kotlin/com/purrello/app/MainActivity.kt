package com.purrello.app

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.purrello.shared.App
import com.purrello.shared.di.handleDeepLinkPayload

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (savedInstanceState == null) handlePush(intent)
        setContent { App() }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handlePush(intent)
    }

    /** FCM data payload keys (type, petId, targetId) arrive as intent extras when a notification is tapped. */
    private fun handlePush(intent: Intent?) {
        val extras = intent?.extras ?: return
        val payload = extras.keySet().associateWith { key -> extras.getString(key).orEmpty() }
        if (payload.containsKey("type")) handleDeepLinkPayload(payload)
    }
}
