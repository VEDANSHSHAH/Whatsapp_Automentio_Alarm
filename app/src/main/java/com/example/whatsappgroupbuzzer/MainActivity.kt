package com.example.whatsappgroupbuzzer

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings as AndroidSettings
import android.view.Gravity
import android.view.View
import android.widget.*

class MainActivity : android.app.Activity() {
    private lateinit var enabled: Switch
    private lateinit var groupName: EditText
    private lateinit var mentionText: EditText
    private val settings by lazy { Settings(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (android.os.Build.VERSION.SDK_INT >= 33 &&
            checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 5)

        val pad = (20 * resources.displayMetrics.density).toInt()
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(pad, pad * 2, pad, pad)
        }
        layout.addView(TextView(this).apply {
            text = "Buzz only for mentions in one WhatsApp group"
            textSize = 22f
        })
        layout.addView(TextView(this).apply {
            text = "The group name must match the WhatsApp notification exactly. Use the text WhatsApp shows when you are mentioned (for example, @YourName)."
            setPadding(0, pad / 2, 0, pad)
        })
        enabled = Switch(this).apply { text = "Enable buzzer" }
        groupName = EditText(this).apply {
            hint = "Exact WhatsApp group name"
            inputType = android.text.InputType.TYPE_CLASS_TEXT
        }
        mentionText = EditText(this).apply {
            hint = "Your mention text, e.g. @Alex"
            inputType = android.text.InputType.TYPE_CLASS_TEXT
        }
        val save = Button(this).apply { text = "Save selected group" }
        val permission = Button(this).apply { text = "Grant notification access" }
        val stop = Button(this).apply { text = "Stop alarm now" }
        layout.addView(enabled)
        layout.addView(groupName)
        layout.addView(mentionText)
        layout.addView(save)
        layout.addView(permission)
        layout.addView(stop)
        layout.addView(Space(this), LinearLayout.LayoutParams(1, 0, 1f))
        layout.addView(TextView(this).apply {
            text = "WhatsApp message content is not read directly. Android only provides notification details to this app."
            gravity = Gravity.CENTER_HORIZONTAL
        })
        setContentView(layout)

        val current = settings.read()
        enabled.isChecked = current.enabled
        groupName.setText(current.groupName)
        mentionText.setText(current.mentionText)
        save.setOnClickListener {
            settings.save(BuzzerSettings(enabled.isChecked, groupName.text.toString(), mentionText.text.toString()))
            Toast.makeText(this, "Saved. Only this exact group can trigger the buzzer.", Toast.LENGTH_LONG).show()
        }
        permission.setOnClickListener {
            startActivity(Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"))
        }
        stop.setOnClickListener { BuzzerService.stop(this) }
    }
}
