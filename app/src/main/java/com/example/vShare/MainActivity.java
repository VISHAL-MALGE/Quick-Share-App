package com.example.vShare;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button btnUploadFile, btnGenerateLink;
    private ProgressBar progressBar;
    private TextView fileLinkTextView;
    private ImageButton copy;
    private Uri selectedFileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnUploadFile = findViewById(R.id.btn_UploadFile);
        btnGenerateLink = findViewById(R.id.btn_GenerateLinK);
        progressBar = findViewById(R.id.progressBar);
        fileLinkTextView = findViewById(R.id.fileLinkTextView);
        copy = findViewById(R.id.copy); // Initialize ImageButton

        btnUploadFile.setOnClickListener(view -> pickFile());

        btnGenerateLink.setOnClickListener(view -> {
            if (selectedFileUri != null) {
                uploadFileToServer();
            } else {
                Toast.makeText(this, "Please select a file first.", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up Copy button
        copy.setOnClickListener(view -> {
            String fileLink = fileLinkTextView.getText().toString();
            if (!fileLink.isEmpty()) {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("File Link", fileLink);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(MainActivity.this, "Link copied to clipboard!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "No link available to copy.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void pickFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        filePickerLauncher.launch(intent);
    }

    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedFileUri = result.getData().getData();
                    btnGenerateLink.setEnabled(true);
                    Toast.makeText(this, "File Selected!", Toast.LENGTH_SHORT).show();
                }
            });

    private void uploadFileToServer() {
        progressBar.setVisibility(View.VISIBLE);
        FileUploadService.uploadFile(this, selectedFileUri, new FileUploadService.UploadCallback() {
            @Override
            public void onSuccess(String downloadLink) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    fileLinkTextView.setText(downloadLink);
                    fileLinkTextView.setVisibility(View.VISIBLE);

                    // Debug Toast to confirm
                    Toast.makeText(MainActivity.this, "Link generated!", Toast.LENGTH_SHORT).show();

                    // Make Copy button visible
                    copy.setVisibility(View.VISIBLE);
                });
            }

            @Override
            public void onError(String errorMessage) {
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
