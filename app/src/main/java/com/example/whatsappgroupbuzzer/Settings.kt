package com.example.whatsappgroupbuzzer

import android.content.Context

/** User-controlled matching rules. Group name matching is deliberately exact. */
data class BuzzerSettings(
    val enabled: Boolean,
    val groupName: String,
    val mentionText: String
)

class Settings(private val context: Context) {
    private val prefs = context.getSharedPreferences("buzzer_settings", Context.MODE_PRIVATE)

    fun read() = BuzzerSettings(
        enabled = prefs.getBoolean("enabled", true),
        groupName = prefs.getString("group_name", "")!!.trim(),
        mentionText = prefs.getString("mention_text", "")!!.trim()
    )

    fun save(settings: BuzzerSettings) {
        prefs.edit()
            .putBoolean("enabled", settings.enabled)
            .putString("group_name", settings.groupName.trim())
            .putString("mention_text", settings.mentionText.trim())
            .apply()
    }
}
