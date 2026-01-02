//package com.example.scrollock;
//
//import android.accessibilityservice.AccessibilityService;
//import android.accessibilityservice.AccessibilityServiceInfo;
//import android.app.ActivityManager;
//import android.content.Context;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.os.Handler;
//import android.os.Looper;
//import android.util.Log;
//import android.view.accessibility.AccessibilityEvent;
//import android.view.accessibility.AccessibilityNodeInfo;
//
//import java.util.List;
//
//public class FocusAccessibilityService extends AccessibilityService {
//
//    private static final String TAG = "FocusAccessibilityService";
//    private long lastBlockTime = 0;
//    private static final long COOLDOWN_MS = 2500;
//
//    @Override
//    protected void onServiceConnected() {
//        super.onServiceConnected();
//        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
//        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
//        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
//        info.packageNames = new String[]{"com.google.android.youtube", "com.instagram.android"};
//        info.flags = AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS | AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS;
//        setServiceInfo(info);
//        Log.d(TAG, "Accessibility Service connected and configured!");
//    }
//
//    @Override
//    public void onAccessibilityEvent(AccessibilityEvent event) {
//        if (event == null) return;
//
//        AccessibilityNodeInfo root = getRootInActiveWindow();
//        if (root == null) return;
//
//        String pkg = event.getPackageName() == null ? "" : event.getPackageName().toString();
//        boolean shouldBlock = false;
//        String message = "";
//
//        if (pkg.equals("com.google.android.youtube")) {
//            shouldBlock = detectYouTubeShorts(root);
//            message = "🚫 YouTube Shorts Blocked!";
//        } else if (pkg.equals("com.instagram.android")) {
//            shouldBlock = detectInstagramReels(root);
//            message = "🚫 Instagram Reels Blocked!";
//        }
//
//        if (shouldBlock) triggerBlock(this, message, pkg);
//
//        root.recycle();
//    }
//
//    private boolean detectYouTubeShorts(AccessibilityNodeInfo root) {
//        try {
//            List<AccessibilityNodeInfo> overlay =
//                    root.findAccessibilityNodeInfosByViewId("com.google.android.youtube:id/reel_player_overlay");
//            if (overlay != null && !overlay.isEmpty()) {
//                Log.d(TAG, "Detected Shorts by overlay ID ✅");
//                return true;
//            }
//
//            String allText = getAllNodeText(root).toLowerCase();
//            if (allText.contains("shorts") &&
//                    (allText.contains("subscribe") || allText.contains("likes") || allText.contains("comments"))) {
//                Log.d(TAG, "Detected Shorts by text ✅");
//                return true;
//            }
//
//        } catch (Exception e) {
//            Log.e(TAG, "Error detecting YouTube Shorts", e);
//        }
//        return false;
//    }
//
//    private boolean detectInstagramReels(AccessibilityNodeInfo root) {
//        try {
//            List<AccessibilityNodeInfo> reels = root.findAccessibilityNodeInfosByViewId("com.instagram.android:id/clips_viewer");
//            if (reels != null && !reels.isEmpty()) return true;
//        } catch (Exception ignored) {}
//
//        String allText = getAllNodeText(root).toLowerCase();
//        return allText.contains("reel") || allText.contains("reels");
//    }
//
//    private String getAllNodeText(AccessibilityNodeInfo node) {
//        if (node == null) return "";
//        StringBuilder sb = new StringBuilder();
//        if (node.getText() != null) sb.append(node.getText()).append(" ");
//        for (int i = 0; i < node.getChildCount(); i++) {
//            sb.append(getAllNodeText(node.getChild(i)));
//        }
//        return sb.toString();
//    }
//
//    private void triggerBlock(Context ctx, String message, String pkg) {
//        long now = System.currentTimeMillis();
//        if (now - lastBlockTime < COOLDOWN_MS) return;
//        lastBlockTime = now;
//
//        Log.d(TAG, "Blocking detected: " + pkg);
//        PopupManager.showPopup(ctx.getApplicationContext(), message);
//
//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//            boolean backResult = performGlobalAction(GLOBAL_ACTION_BACK);
//            Log.d(TAG, "BACK pressed = " + backResult);
//
//            new Handler(Looper.getMainLooper()).postDelayed(() -> {
//                String currentPkg = getForegroundPackage();
//                Log.d(TAG, "Current foreground after BACK: " + currentPkg);
//
//                if (currentPkg == null) return;
//
//                if (pkg.equals(currentPkg)) {
//                    Log.d(TAG, "✅ Stayed inside " + pkg + " home.");
//                    return;
//                }
//
//                if (currentPkg.contains("launcher") || currentPkg.contains("home")) {
//                    Log.d(TAG, "🚀 Relaunching app home for " + pkg);
//                    bringAppToFront(ctx, pkg);
//                }
//
//            }, 350);
//        }, 120);
//    }
//
//    private String getForegroundPackage() {
//        try {
//            AccessibilityNodeInfo root = getRootInActiveWindow();
//            if (root != null && root.getPackageName() != null)
//                return root.getPackageName().toString();
//        } catch (Exception ignored) {}
//
//        try {
//            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//            List<ActivityManager.RunningAppProcessInfo> procs = am.getRunningAppProcesses();
//            if (procs != null && !procs.isEmpty()) {
//                for (ActivityManager.RunningAppProcessInfo p : procs) {
//                    if (p.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
//                        return p.processName;
//                }
//            }
//        } catch (Exception ignored) {}
//
//        return null;
//    }
//
//    private void bringAppToFront(Context ctx, String pkg) {
//        try {
//            PackageManager pm = ctx.getPackageManager();
//            Intent launchIntent = pm.getLaunchIntentForPackage(pkg);
//            if (launchIntent != null) {
//                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                ctx.startActivity(launchIntent);
//                Log.d(TAG, "Restarted app: " + pkg);
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "Error bringing app to front: " + e.getMessage());
//        }
//    }
//
//    @Override
//    public void onInterrupt() {
//        Log.e(TAG, "Accessibility Service has been interrupted!");
//    }
//}
