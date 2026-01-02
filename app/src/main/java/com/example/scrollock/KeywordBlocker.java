package com.example.scrollock;

import android.accessibilityservice.AccessibilityService;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.List;

public class KeywordBlocker {

    private static final String TAG = "KeywordBlocker";
    private long lastBlockTime = 0;
    private static final long COOLDOWN_MS = 2500;

    public void handleEvent(AccessibilityService service, AccessibilityEvent event) {
        if (service == null || event == null) return;

        int type = event.getEventType();
        if (type != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
                type != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) return;

        AccessibilityNodeInfo root = service.getRootInActiveWindow();
        if (root == null) return;

        String pkg = event.getPackageName() == null ? "" : event.getPackageName().toString();
        boolean shouldBlock = false;
        String message = "";

        if (pkg.equals("com.google.android.youtube")) {
            shouldBlock = detectYouTubeShorts(root);
            message = "🚫 YouTube Shorts Blocked";
        } else if (pkg.equals("com.instagram.android")) {
            shouldBlock = detectInstagramReels(root);
            message = "🚫 Instagram Reels Blocked";
        }

        long now = System.currentTimeMillis();
        if (shouldBlock && now - lastBlockTime > COOLDOWN_MS) {
            lastBlockTime = now;
            triggerBlock(service, message, pkg);
        }

        root.recycle();
    }

    private boolean detectYouTubeShorts(AccessibilityNodeInfo root) {
        try {
            List<AccessibilityNodeInfo> overlay =
                    root.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/reel_player_overlay");
            if (overlay != null && !overlay.isEmpty()) {
                Log.d(TAG, "Detected Shorts by overlay ID ✅");
                return true;
            }

            String allText = getAllNodeText(root).toLowerCase();
            if (allText.contains("shorts") &&
                    (allText.contains("subscribe") || allText.contains("likes") || allText.contains("comments"))) {
                Log.d(TAG, "Detected Shorts by text ✅");
                return true;
            }

        } catch (Exception e) {
            Log.e(TAG, "Error detecting YouTube Shorts", e);
        }
        return false;
    }

    private boolean detectInstagramReels(AccessibilityNodeInfo root) {
        try {
            List<AccessibilityNodeInfo> reels = root.findAccessibilityNodeInfosByViewId("com.instagram.android:id/clips_viewer");
            if (reels != null && !reels.isEmpty()) return true;
        } catch (Exception ignored) {}

        String allText = getAllNodeText(root).toLowerCase();
        return allText.contains("reel") || allText.contains("reels");
    }

    private String getAllNodeText(AccessibilityNodeInfo node) {
        if (node == null) return "";
        StringBuilder sb = new StringBuilder();
        if (node.getText() != null) sb.append(node.getText()).append(" ");
        for (int i = 0; i < node.getChildCount(); i++) {
            sb.append(getAllNodeText(node.getChild(i)));
        }
        return sb.toString();
    }

    private void triggerBlock(AccessibilityService service, String message, String pkg) {
        Log.d(TAG, "🚫 Blocking detected for: " + pkg);
        PopupManager.showPopup(service.getApplicationContext(), message);

        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            AccessibilityNodeInfo root = service.getRootInActiveWindow();
            if (root == null) return;

            boolean stillInShorts = false;
            if (pkg.equals("com.google.android.youtube")) {
                stillInShorts = detectYouTubeShorts(root);
            } else if (pkg.equals("com.instagram.android")) {
                stillInShorts = detectInstagramReels(root);
            }

            root.recycle();

            if (stillInShorts) {
                Log.d(TAG, "🟡 Still in shorts/reels → perform one back");
                service.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
            } else {
                Log.d(TAG, "✅ Already exited reels/shorts → no back");
            }

        }, 350);
    }

    private String getForegroundPackage(AccessibilityService service) {
        try {
            AccessibilityNodeInfo root = service.getRootInActiveWindow();
            if (root != null && root.getPackageName() != null)
                return root.getPackageName().toString();
        } catch (Exception ignored) {}

        try {
            android.app.ActivityManager am = (android.app.ActivityManager)
                    service.getSystemService(android.content.Context.ACTIVITY_SERVICE);
            java.util.List<android.app.ActivityManager.RunningAppProcessInfo> procs = am.getRunningAppProcesses();
            if (procs != null && !procs.isEmpty()) {
                for (android.app.ActivityManager.RunningAppProcessInfo p : procs) {
                    if (p.importance == android.app.ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
                        return p.processName;
                }
            }
        } catch (Exception ignored) {}

        return null;
    }

    private void bringAppToFront(android.content.Context ctx, String pkg) {
        try {
            android.content.pm.PackageManager pm = ctx.getPackageManager();
            android.content.Intent launchIntent = pm.getLaunchIntentForPackage(pkg);
            if (launchIntent != null) {
                launchIntent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK | android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP);
                ctx.startActivity(launchIntent);
                Log.d(TAG, "Restarted app: " + pkg);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error bringing app to front: " + e.getMessage());
        }
    }
}
