package com.example.scrollock;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class BlockerActivity extends AppCompatActivity {

    private boolean isActive = false;
    private Button btnActivate;
    private TextView activationStatus;
    private static final int REQ_OVERLAY_PERMISSION = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocker);

        btnActivate = findViewById(R.id.btnActivate);
        activationStatus = findViewById(R.id.activationStatus);

        updateUiForState();

        btnActivate.setOnClickListener(v -> toggleBlocking());
    }

    private void updateUiForState() {
        if (isActive) {
            activationStatus.setText("BLOCKER ACTIVE");
            activationStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_green_dark));
            btnActivate.setText("DEACTIVATE");
            btnActivate.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_green_dark));
        } else {
            activationStatus.setText("BLOCKER INACTIVE");
            activationStatus.setTextColor(ContextCompat.getColor(this, android.R.color.holo_red_dark));
            btnActivate.setText("ACTIVATE");
            btnActivate.setBackgroundTintList(ContextCompat.getColorStateList(this, android.R.color.holo_red_dark));
        }
    }

    private void toggleBlocking() {
        if (!isActive) {
            // Check overlay permission if required
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQ_OVERLAY_PERMISSION);
                Toast.makeText(this, "Grant overlay permission then press ACTIVATE again.", Toast.LENGTH_LONG).show();
                return;
            }

            // start service
            try {
                Intent svcIntent = new Intent(this, BlockingService.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ContextCompat.startForegroundService(this, svcIntent);
                } else {
                    startService(svcIntent);
                }
                isActive = true;
                Toast.makeText(this, "Blocker started", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to start blocker: " + e.getMessage(), Toast.LENGTH_LONG).show();
                isActive = false;
            }

        } else {
            // stop service
            try {
                stopService(new Intent(this, BlockingService.class));
                isActive = false;
                Toast.makeText(this, "Blocker stopped", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to stop blocker: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
        updateUiForState();
    }

    @Override
    @SuppressWarnings("deprecation")
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_OVERLAY_PERMISSION) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "Overlay permission granted. Press ACTIVATE.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Overlay permission still not granted.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
