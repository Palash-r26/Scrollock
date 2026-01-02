package com.example.scrollock;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    ImageButton menuIcon;
    TextView focusPoints, tvInstagram, tvYouTube, tvWhatsApp;
    Button btnEnable;

    private final Handler handler = new Handler(); // For live updates
    private Runnable usageUpdater;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (UsageStatsHelper.hasUsagePermission(this)) {
            Map<String, Long> usageMap = UsageStatsHelper.getAppUsageMap(this);
            for (Map.Entry<String, Long> entry : usageMap.entrySet()) {
                Log.d("UsageStats", entry.getKey() + " used for " + entry.getValue() + " ms");
            }
        } else {
            Log.e("UsageStats", "Usage permission not granted!");
        }

        drawerLayout = findViewById(R.id.drawer_layout);
        menuIcon = findViewById(R.id.menu_icon);
        focusPoints = findViewById(R.id.focusPoints);
        btnEnable = findViewById(R.id.btnEnable);

        tvInstagram = findViewById(R.id.tvInstagram);
        tvYouTube = findViewById(R.id.tvYouTube);
        tvWhatsApp = findViewById(R.id.tvWhatsApp);

        // Sidebar toggle
        menuIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
            else
                drawerLayout.openDrawer(GravityCompat.START);
        });

        // Example button click
        btnEnable.setOnClickListener(v ->
                Toast.makeText(this, "Blocking Service Enabled!", Toast.LENGTH_SHORT).show()
        );

        // Sidebar menu
        findViewById(R.id.menu_profile).setOnClickListener(v ->
                Toast.makeText(this, "Profile Clicked", Toast.LENGTH_SHORT).show());
        findViewById(R.id.menu_rewards).setOnClickListener(v ->
                Toast.makeText(this, "Rewards Clicked", Toast.LENGTH_SHORT).show());
        findViewById(R.id.menu_settings).setOnClickListener(v ->
                Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show());
        findViewById(R.id.menu_contact).setOnClickListener(v ->
                Toast.makeText(this, "Contact Us Clicked", Toast.LENGTH_SHORT).show());

        // Permission check
        if (!hasUsageAccessPermission()) {
            Toast.makeText(this, "Please grant Usage Access permission", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            intent.setData(Uri.parse("package:" + getPackageName()));
            startActivity(intent);
        } else {
            startUsageUpdater();
        }
    }

    private boolean hasUsageAccessPermission() {
        try {
            AppOpsManager appOps = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(getPackageName(), 0);
            int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                    appInfo.uid, appInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);
        } catch (Exception e) {
            return false;
        }
    }

    private void startUsageUpdater() {
        usageUpdater = new Runnable() {
            @Override
            public void run() {
                updateAppUsageData();
                handler.postDelayed(this, 5000); // refresh every 5 seconds
            }
        };
        handler.post(usageUpdater);
    }

    private void updateAppUsageData() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        if (usageStatsManager == null) return;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        long startTime = calendar.getTimeInMillis();
        long endTime = System.currentTimeMillis();

        List<UsageStats> usageStatsList = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, startTime, endTime);

        long instagramTime = 0, youtubeTime = 0, whatsappTime = 0;

        if (usageStatsList != null) {
            for (UsageStats usageStats : usageStatsList) {
                String pkg = usageStats.getPackageName().toLowerCase();
                if (pkg.contains("instagram")) instagramTime += usageStats.getTotalTimeInForeground();
                else if (pkg.contains("youtube")) youtubeTime += usageStats.getTotalTimeInForeground();
                else if (pkg.contains("whatsapp")) whatsappTime += usageStats.getTotalTimeInForeground();
            }
        }

        // Convert milliseconds → hours/minutes
        tvInstagram.setText("Instagram: " + formatTime(instagramTime));
        tvYouTube.setText("YouTube: " + formatTime(youtubeTime));
        tvWhatsApp.setText("WhatsApp: " + formatTime(whatsappTime));
    }

    private String formatTime(long millis) {
        long totalMinutes = millis / 60000;
        long hours = totalMinutes / 60;
        long minutes = totalMinutes % 60;
        return hours > 0 ? hours + "h " + minutes + "m" : minutes + " min";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (usageUpdater != null) handler.removeCallbacks(usageUpdater);
    }
}
