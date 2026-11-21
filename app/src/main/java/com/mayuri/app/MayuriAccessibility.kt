package com.mayuri.app

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Path
import android.os.Build
import android.os.Bundle
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class MayuriAccessibility : AccessibilityService() {

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(c: Context?, i: Intent?) {
            val cmd = i?.getStringExtra("cmd") ?: return
            val arg = i.getStringExtra("arg") ?: ""
            when (cmd) {
                "back" -> performGlobalAction(GLOBAL_ACTION_BACK)
                "home" -> performGlobalAction(GLOBAL_ACTION_HOME)
                "recents" -> performGlobalAction(GLOBAL_ACTION_RECENTS)
                "notifications" -> performGlobalAction(GLOBAL_ACTION_NOTIFICATIONS)
                "scrolldown" -> swipe(540f, 1600f, 540f, 600f)
                "scrollup" -> swipe(540f, 600f, 540f, 1600f)
                "click" -> clickText(arg)
                "type" -> typeText(arg)
            }
        }
    }

    override fun onServiceConnected() {
        val f = IntentFilter("com.mayuri.CMD")
        if (Build.VERSION.SDK_INT >= 33) {
            registerReceiver(receiver, f, Context.RECEIVER_EXPORTED)
        } else {
            registerReceiver(receiver, f)
        }
    }

    override fun onDestroy() {
        try { unregisterReceiver(receiver) } catch (e: Exception) {}
        super.onDestroy()
    }

    private fun swipe(x1: Float, y1: Float, x2: Float, y2: Float) {
        val p = Path()
        p.moveTo(x1, y1)
        p.lineTo(x2, y2)
        val g = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(p, 0, 300)).build()
        dispatchGesture(g, null, null)
    }

    private fun clickText(t: String) {
        val root = rootInActiveWindow ?: return
        val nodes = root.findAccessibilityNodeInfosByText(t)
        for (n in nodes) {
            var x: AccessibilityNodeInfo? = n
            while (x != null && !x.isClickable) x = x.parent
            if (x != null) { x.performAction(AccessibilityNodeInfo.ACTION_CLICK); return }
        }
    }

    private fun typeText(t: String) {
        val root = rootInActiveWindow ?: return
        val f = root.findFocus(AccessibilityNodeInfo.FOCUS_INPUT) ?: return
        val b = Bundle()
        b.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, t)
        f.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, b)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}
}
