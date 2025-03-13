package com.example.vShare;


import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class FileUploadService {

    private static final String UPLOAD_URL = "https://file.io";
    private static final String TAG = "FileUploadService";

    public interface UploadCallback {
        void onSuccess(String downloadLink);
        void onError(String errorMessage);
    }

    public static void uploadFile(Context context, Uri fileUri, UploadCallback callback) {
        File file = FileUtils.getFileFromUri(context, fileUri);
        if (file == null) {
            callback.onError("File not found!");
            return;
        }

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), fileBody);

        Request request = new Request.Builder()
                .url(UPLOAD_URL)
                .post(new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addPart(filePart)
                        .build())
                .build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d(TAG, "Upload Successful: " + responseBody);
                    String downloadLink = extractDownloadLink(responseBody);
                    callback.onSuccess(downloadLink);
                } else {
                    callback.onError("Upload Failed: " + response.message());
                }
            } catch (IOException e) {
                Log.e(TAG, "Upload Error: ", e);
                callback.onError("Error: " + e.getMessage());
            }
        }).start();
    }

    private static String extractDownloadLink(String responseBody) {
        try {
            org.json.JSONObject jsonResponse = new org.json.JSONObject(responseBody);
            if (jsonResponse.getBoolean("success")) {
                return jsonResponse.getString("link");
            }
        } catch (org.json.JSONException e) {
            Log.e(TAG, "JSON Parsing Error", e);
        }
        return "Failed to extract link.";
    }
}

