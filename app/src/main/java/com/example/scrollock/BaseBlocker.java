package com.example.scrollock;

import android.accessibilityservice.AccessibilityService;

public class BaseBlocker {
    private long lastTrigger = 0;
    protected static final long COOLDOWN_MS = 2000;

    protected boolean canTrigger() {
        long now = System.currentTimeMillis();
        if (now - lastTrigger > COOLDOWN_MS) {
            lastTrigger = now;
            return true;
        }
        return false;
    }

    protected void performRedirectAction(AccessibilityService service) {
        service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
    }
}