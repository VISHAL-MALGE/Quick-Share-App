package com.example.vShare;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.vShare.R;

public class loader extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loader);  // Set the loader.xml layout

        // Use a Handler to wait for a few seconds and then start MainActivity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(loader.this, MainActivity.class);
            startActivity(intent);
            finish();  // Close the LoaderActivity so the user cannot go back to it
        }, 3000);  // Delay of 3 seconds (3000 milliseconds)
    }
}
