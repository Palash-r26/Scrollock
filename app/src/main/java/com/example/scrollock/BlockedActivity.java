package com.example.scrollock;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class BlockedActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blocking_overlay);
    }
}
