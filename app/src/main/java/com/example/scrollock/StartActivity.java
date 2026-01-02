package com.example.scrollock;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class StartActivity extends AppCompatActivity {

    Button btnGetStarted;
    List<ImageView> icons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btnGetStarted = findViewById(R.id.btnGetStarted);

        icons.add(findViewById(R.id.icon1));
        icons.add(findViewById(R.id.icon2));
        icons.add(findViewById(R.id.icon3));
        icons.add(findViewById(R.id.icon4));
        icons.add(findViewById(R.id.icon5));
        icons.add(findViewById(R.id.icon6));

        startArcAnimation();

        btnGetStarted.setOnClickListener(v -> {
            Intent intent = new Intent(StartActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void startArcAnimation() {
        float radius = 300f; // radius of the arc
        float centerX = 0f;
        float centerY = 0f;

        // 1️⃣ Step: Position icons in arc
        for (int i = 0; i < icons.size(); i++) {
            double angle = Math.toRadians(180 + i * 30); // spread across semi-circle
            float x = (float) (radius * Math.cos(angle));
            float y = (float) (radius * Math.sin(angle));

            icons.get(i).setTranslationX(x);
            icons.get(i).setTranslationY(y);
        }

        // 2️⃣ Step: Orbit animation
        List<Animator> orbitAnimators = new ArrayList<>();
        for (ImageView icon : icons) {
            ObjectAnimator rotate = ObjectAnimator.ofFloat(icon, "rotation", 0f, 360f);
            rotate.setDuration(2000);
            rotate.setRepeatCount(1);
            orbitAnimators.add(rotate);
        }

        AnimatorSet orbitSet = new AnimatorSet();
        orbitSet.playTogether(orbitAnimators);

        // 3️⃣ Step: Merge all icons to center
        List<Animator> mergeAnimators = new ArrayList<>();
        for (ImageView icon : icons) {
            ObjectAnimator moveX = ObjectAnimator.ofFloat(icon, "translationX", 0f);
            ObjectAnimator moveY = ObjectAnimator.ofFloat(icon, "translationY", 0f);
            ObjectAnimator fade = ObjectAnimator.ofFloat(icon, "alpha", 1f, 0f);
            moveX.setDuration(800);
            moveY.setDuration(800);
            fade.setDuration(800);
            mergeAnimators.add(moveX);
            mergeAnimators.add(moveY);
            mergeAnimators.add(fade);
        }

        AnimatorSet mergeSet = new AnimatorSet();
        mergeSet.playTogether(mergeAnimators);

        // 4️⃣ Step: Show “Get Started” button after merge
        mergeSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                ObjectAnimator fadeIn = ObjectAnimator.ofFloat(btnGetStarted, "alpha", 0f, 1f);
                fadeIn.setDuration(800);
                fadeIn.start();
            }

            @Override public void onAnimationStart(Animator animation) {}
            @Override public void onAnimationCancel(Animator animation) {}
            @Override public void onAnimationRepeat(Animator animation) {}
        });

        // Combine all animations
        AnimatorSet totalSet = new AnimatorSet();
        totalSet.playSequentially(orbitSet, mergeSet);
        totalSet.start();
    }
}
