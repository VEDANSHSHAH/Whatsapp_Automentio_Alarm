package com.example.whatsappgroupbuzzer

import android.app.Notification
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification

class WhatsAppMentionListener : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification) {
        if (sbn.packageName != WHATSAPP_PACKAGE) return
        val rules = Settings(this).read()
        if (!rules.enabled || rules.groupName.isBlank() || rules.mentionText.isBlank()) return

        val details = notificationDetails(sbn.notification)
        // Group title varies by notification style. We match against title/conversation title only,
        // never merely against message body, so a different group's message cannot cause a buzz.
        if (!details.groupTitles.any { it.equals(rules.groupName, ignoreCase = false) }) return
        if (!details.messageText.contains(rules.mentionText, ignoreCase = true)) return

        BuzzerService.start(this, rules.groupName)
    }

    private fun notificationDetails(notification: Notification): NotificationDetails {
        val extras = notification.extras
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val conversationTitle = extras.getCharSequence(Notification.EXTRA_CONVERSATION_TITLE)?.toString().orEmpty()
        val text = buildString {
            append(extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty())
            extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)?.forEach { append('\n').append(it) }
            extras.getParcelableArray(Notification.EXTRA_MESSAGES)?.forEach { raw ->
                @Suppress("DEPRECATION")
                val message = raw as? android.os.Bundle ?: return@forEach
                append('\n').append(message.getCharSequence("text").orEmpty())
            }
        }
        return NotificationDetails(listOf(title, conversationTitle).filter { it.isNotBlank() }, text)
    }

    private data class NotificationDetails(val groupTitles: List<String>, val messageText: String)

    private companion object { const val WHATSAPP_PACKAGE = "com.whatsapp" }
}
