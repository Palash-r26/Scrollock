package com.example.scrollock;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;

public class BlockingService extends AccessibilityService {

    private static final String TAG = "BlockingService";
    private KeywordBlocker keywordBlocker;

    @Override
    public void onCreate() {
        super.onCreate();
        keywordBlocker = new KeywordBlocker();
        Log.d(TAG, "BlockingService created, KeywordBlocker initialized");
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (keywordBlocker != null && event != null) {
            keywordBlocker.handleEvent(this, event);
        }
    }

    @Override
    public void onInterrupt() {
        Log.d(TAG, "BlockingService interrupted");
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS
                | AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
        setServiceInfo(info);
        Log.d(TAG, "BlockingService connected and configured ✅");
    }
}