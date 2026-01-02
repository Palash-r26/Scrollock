package com.example.scrollock;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class EditProfileActivity extends AppCompatActivity {

    private EditText edtName, edtEmail;
    private Button btnSave;
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        btnSave = findViewById(R.id.btnSave);
        backArrow = findViewById(R.id.backArrow);

        SharedPreferences prefs = getSharedPreferences("UserPrefs", MODE_PRIVATE);
        edtName.setText(prefs.getString("userName", ""));
        edtEmail.setText(prefs.getString("userEmail", ""));

        backArrow.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> {
            String newName = edtName.getText().toString().trim();
            String newEmail = edtEmail.getText().toString().trim();

            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("userName", newName);
            editor.putString("userEmail", newEmail);
            editor.apply();

            finish(); // go back to ProfileActivity
        });
    }
}
