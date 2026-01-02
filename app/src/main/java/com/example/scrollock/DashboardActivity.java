package com.example.scrollock;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import java.util.*;
import java.util.stream.Collectors;
import android.content.SharedPreferences;

public class DashboardActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageView menuIcon;
    private TextView userName, usageTime, progressText;
    private ProgressBar circularProgress;
    private LinearLayout appUsageContainer;
    private Button btnEnableService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        drawerLayout = findViewById(R.id.drawer_layout);
        menuIcon = findViewById(R.id.menuIcon);
        userName = findViewById(R.id.userName);
        usageTime = findViewById(R.id.usageTime);
        progressText = findViewById(R.id.progressText);
        circularProgress = findViewById(R.id.circularProgress);
        appUsageContainer = findViewById(R.id.appUsageContainer);
        btnEnableService = findViewById(R.id.btnEnableService);

        // Load and display username
        loadUserName();

        // 🟩 Drawer menu toggle
        menuIcon.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START))
                drawerLayout.closeDrawer(GravityCompat.START);
            else
                drawerLayout.openDrawer(GravityCompat.START);
        });

        // 🟦 Drawer menu item clicks
        findViewById(R.id.menu_home).setOnClickListener(v ->
                drawerLayout.closeDrawer(GravityCompat.START)
        );

        findViewById(R.id.menu_blocker).setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, BlockerActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        findViewById(R.id.menu_profile).setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, ProfileActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        findViewById(R.id.menu_settings).setOnClickListener(v -> {
            startActivity(new Intent(DashboardActivity.this, SettingsActivity.class));
            drawerLayout.closeDrawer(GravityCompat.START);
        });

        findViewById(R.id.menu_contact).setOnClickListener(v ->
                Toast.makeText(this, "Contact Us clicked", Toast.LENGTH_SHORT).show()
        );

        findViewById(R.id.menu_rewards).setOnClickListener(v ->
                Toast.makeText(this, "Rewards clicked", Toast.LENGTH_SHORT).show()
        );

        findViewById(R.id.menu_logout).setOnClickListener(v ->
                Toast.makeText(this, "Logout clicked", Toast.LENGTH_SHORT).show()
        );

        // 🟧 Bottom navigation
        findViewById(R.id.nav_home).setOnClickListener(v ->
                Toast.makeText(this, "Already on Home", Toast.LENGTH_SHORT).show()
        );

        findViewById(R.id.nav_blocker).setOnClickListener(v ->
                startActivity(new Intent(DashboardActivity.this, BlockerActivity.class))
        );

        // 🟨 Accessibility service button
        btnEnableService.setOnClickListener(v -> {
            startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
            Toast.makeText(this, "Enable Scrollock accessibility service.", Toast.LENGTH_LONG).show();
        });

        // 🟦 Usage stats permission check
        if (!UsageStatsHelper.hasUsagePermission(this)) {
            Toast.makeText(this, "Grant usage permission.", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        } else {
            displayUsageData();
        }

        // 🟥 Handle back press
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    finish();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload name when returning from ProfileActivity
        loadUserName();
    }

    private void loadUserName() {
        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        String name = prefs.getString("userName", "User");
        userName.setText(name);
    }

    private void displayUsageData() {
        Map<String, Long> usageData = UsageStatsHelper.getAppUsageMap(this);

        if (usageData == null || usageData.isEmpty()) {
            usageTime.setText("No usage data available");
            circularProgress.setProgress(0);
            progressText.setText("0%");
            return;
        }

        long totalTimeMillis = 0;
        for (long t : usageData.values()) totalTimeMillis += t;
        long totalMinutes = totalTimeMillis / 1000 / 60;
        usageTime.setText(totalMinutes + " mins today");

        int progress = (int) Math.min((totalMinutes / (4.0 * 60)) * 100, 100);
        circularProgress.setProgress(progress);
        progressText.setText(progress + "%");

        appUsageContainer.removeAllViews();

        List<Map.Entry<String, Long>> sortedList = usageData.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toList());

        for (Map.Entry<String, Long> entry : sortedList) {
            String pkg = entry.getKey();
            long minutesUsed = entry.getValue() / 1000 / 60;
            if (minutesUsed == 0) continue;
            try {
                ApplicationInfo appInfo = getPackageManager().getApplicationInfo(pkg, 0);
                Drawable icon = getPackageManager().getApplicationIcon(appInfo);
                String appName = getPackageManager().getApplicationLabel(appInfo).toString();

                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setPadding(8, 8, 8, 8);

                ImageView iconView = new ImageView(this);
                iconView.setImageDrawable(icon);
                LinearLayout.LayoutParams iconParams = new LinearLayout.LayoutParams(100, 100);
                iconParams.setMarginEnd(16);
                iconView.setLayoutParams(iconParams);

                TextView appText = new TextView(this);
                appText.setText(appName + " — " + minutesUsed + " mins");
                appText.setTextSize(16);

                row.addView(iconView);
                row.addView(appText);
                appUsageContainer.addView(row);
            } catch (PackageManager.NameNotFoundException ignored) {
            }
        }
    }
}
