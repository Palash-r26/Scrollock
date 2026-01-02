package com.example.scrollock;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsageStatsHelper {

    public static boolean hasUsagePermission(Context context) {
        try {
            AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOps.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.getPackageName()
            );
            return mode == AppOpsManager.MODE_ALLOWED;
        } catch (Exception e) {
            Log.e("UsageStatsHelper", "Permission check failed", e);
            return false;
        }
    }

    public static Map<String, Long> getAppUsageMap(Context context) {
        UsageStatsManager usageStatsManager =
                (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        if (usageStatsManager == null) {
            Log.e("UsageStatsHelper", "UsageStatsManager is null");
            return Collections.emptyMap();
        }

        long endTime = System.currentTimeMillis();
        long startTime = endTime - (1000L * 60 * 60 * 24); // Last 24 hours

        List<UsageStats> stats;
        try {
            stats = usageStatsManager.queryUsageStats(
                    UsageStatsManager.INTERVAL_DAILY, startTime, endTime
            );
        } catch (Exception e) {
            Log.e("UsageStatsHelper", "queryUsageStats() failed", e);
            return Collections.emptyMap();
        }

        if (stats == null || stats.isEmpty()) {
            Log.w("UsageStatsHelper", "No usage data returned");
            return Collections.emptyMap();
        }

        Map<String, Long> usageMap = new HashMap<>();
        for (UsageStats u : stats) {
            if (u != null && u.getTotalTimeInForeground() > 0) {
                usageMap.put(u.getPackageName(), u.getTotalTimeInForeground());
            }
        }

        Log.d("UsageStatsHelper", "Fetched usage for " + usageMap.size() + " apps");
        return usageMap;
    }
}
