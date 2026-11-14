package com.kaku.wthack

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private lateinit var status: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(28, 36, 28, 36)
            setBackgroundColor(0xFF101827.toInt())
        }

        val title = TextView(this).apply {
            text = "WTHack Demo"
            textSize = 28f
            setTextColor(0xFFFFFFFF.toInt())
            gravity = Gravity.CENTER
        }
        status = TextView(this).apply {
            text = "رادار تجريبي ببيانات محلية وهمية"
            textSize = 16f
            setTextColor(0xFFB8C7DC.toInt())
            gravity = Gravity.CENTER
            setPadding(0, 20, 0, 24)
        }
        val start = Button(this).apply {
            text = "تشغيل النافذة العائمة"
            setOnClickListener { startDemoOverlay() }
        }
        val stop = Button(this).apply {
            text = "إيقاف النافذة"
            setOnClickListener {
                stopService(Intent(this@MainActivity, DemoOverlayService::class.java))
                status.text = "تم إيقاف العرض التجريبي"
            }
        }
        root.addView(title)
        root.addView(status)
        root.addView(start)
        root.addView(stop)
        setContentView(root)
    }

    private fun startDemoOverlay() {
        if (!Settings.canDrawOverlays(this)) {
            status.text = "فعّل صلاحية الظهور فوق التطبيقات ثم ارجع للتطبيق"
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
            return
        }
        startService(Intent(this, DemoOverlayService::class.java))
        status.text = "العرض التجريبي يعمل"
        Toast.makeText(this, "تم تشغيل الرادار التجريبي", Toast.LENGTH_SHORT).show()
    }
}
