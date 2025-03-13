package com.example.vShare;


import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

    public static File getFileFromUri(Context context, Uri uri) {
        File file = null;
        try {
            Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                InputStream inputStream = context.getContentResolver().openInputStream(uri);
                file = new File(context.getCacheDir(), displayName);
                FileOutputStream outputStream = new FileOutputStream(file);

                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                outputStream.close();
                inputStream.close();
                cursor.close();
            }
        } catch (IOException e) {
            Log.e("FileUtils", "Error: " + e.getMessage(), e);
        }
        return file;
    }
}
