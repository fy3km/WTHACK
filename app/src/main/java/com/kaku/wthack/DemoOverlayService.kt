package com.kaku.wthack

import android.app.Service
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout

class DemoOverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private var overlay: View? = null

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        showOverlay()
    }

    private fun showOverlay() {
        if (overlay != null) return
        val panel = RadarDemoView(this)
        val params = WindowManager.LayoutParams(
            300,
            330,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 18
            y = 120
        }

        var downX = 0f
        var downY = 0f
        var startX = 0
        var startY = 0
        panel.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    downX = event.rawX
                    downY = event.rawY
                    startX = params.x
                    startY = params.y
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = startX - (event.rawX - downX).toInt()
                    params.y = startY + (event.rawY - downY).toInt()
                    windowManager.updateViewLayout(panel, params)
                    true
                }
                else -> true
            }
        }
        overlay = panel
        windowManager.addView(panel, params)
    }

    override fun onDestroy() {
        overlay?.let {
            runCatching { windowManager.removeView(it) }
        }
        overlay = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

private class RadarDemoView(context: android.content.Context) : View(context) {
    private val background = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xE61A2638.toInt() }
    private val line = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = 0xFF5C789B.toInt()
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    private val friendly = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFF43D17D.toInt() }
    private val demoTarget = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFFFB547.toInt() }
    private val text = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 18f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawRoundRect(0f, 0f, width.toFloat(), height.toFloat(), 22f, 22f, background)
        canvas.drawText("RADAR • DEMO DATA", 18f, 30f, text)
        val cx = width / 2f
        val cy = height * 0.57f
        val radius = width * 0.36f
        canvas.drawCircle(cx, cy, radius, line)
        canvas.drawCircle(cx, cy, radius * 0.66f, line)
        canvas.drawCircle(cx, cy, radius * 0.33f, line)
        canvas.drawLine(cx - radius, cy, cx + radius, cy, line)
        canvas.drawLine(cx, cy - radius, cx, cy + radius, line)
        canvas.drawCircle(cx, cy, 7f, friendly)
        canvas.drawCircle(cx - radius * 0.48f, cy - radius * 0.35f, 7f, demoTarget)
        canvas.drawCircle(cx + radius * 0.55f, cy + radius * 0.18f, 7f, demoTarget)
        canvas.drawCircle(cx + radius * 0.12f, cy - radius * 0.72f, 7f, demoTarget)
        val caption = Paint(text).apply { textSize = 13f; color = 0xFFB8C7DC.toInt() }
        canvas.drawText("Orange dots are generated locally", 18f, height - 18f, caption)
    }
}
