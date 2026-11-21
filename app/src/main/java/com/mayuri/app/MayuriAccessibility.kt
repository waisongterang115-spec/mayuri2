package com.mayuri.app

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent

class MayuriAccessibility : AccessibilityService() {
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}
}
