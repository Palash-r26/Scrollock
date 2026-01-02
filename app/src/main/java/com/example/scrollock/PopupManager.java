package com.example.scrollock;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;

public class PopupManager {
    private static TextView popupView;

    public static void showPopup(Context context, String message) {
        try {
            if (popupView != null) return;

            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);

            popupView = new TextView(context);
            popupView.setText(message);
            popupView.setTextColor(Color.WHITE);
            popupView.setTextSize(18);
            popupView.setGravity(Gravity.CENTER);
            popupView.setPadding(60, 45, 60, 45);

            // Rounded black background
            GradientDrawable bg = new GradientDrawable();
            bg.setColor(Color.parseColor("#E6000000")); // deeper semi-transparent black
            bg.setCornerRadius(50);
            popupView.setBackground(bg);

            // Overlay layout params
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                            | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                    PixelFormat.TRANSLUCENT
            );
            params.gravity = Gravity.CENTER;

            // ⚡ Run instantly on main thread
            new Handler(Looper.getMainLooper()).post(() -> {
                try {
                    wm.addView(popupView, params);
                } catch (Exception ignored) {}
            });

            // Auto-remove after ~900ms (fast popup)
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                try {
                    if (popupView != null) {
                        wm.removeView(popupView);
                        popupView = null;
                    }
                } catch (Exception ignored) {}
            }, 900);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}