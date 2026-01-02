package com.example.scrollock;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        LinearLayout itemLanguage = findViewById(R.id.item_language);
        LinearLayout itemPrivacy = findViewById(R.id.item_privacy);
        LinearLayout itemTerms = findViewById(R.id.item_terms);
        LinearLayout itemAbout = findViewById(R.id.item_about);

        itemLanguage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com/language"));
            startActivity(intent);
        });

        itemPrivacy.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com/privacy"));
            startActivity(intent);
        });

        itemTerms.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com/terms"));
            startActivity(intent);
        });

        itemAbout.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://example.com/about"));
            startActivity(intent);
        });
    }
}
