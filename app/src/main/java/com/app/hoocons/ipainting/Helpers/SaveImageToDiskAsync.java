package com.app.hoocons.ipainting.Helpers;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import com.app.hoocons.ipainting.ViewHolders.OnImageSavedCallBack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by hungnguyen on 6/7/18.
 * At: 9:31 PM
 */

public class SaveImageToDiskAsync extends AsyncTask<Void, Void, Void> {
    private Bitmap mImageBitmap;
    private OnImageSavedCallBack callBackListener;
    private boolean isSuccess;
    private String savedFilePath;

    public SaveImageToDiskAsync(Bitmap mImageBitmap, OnImageSavedCallBack callBackListener) {
        this.mImageBitmap = mImageBitmap;
        this.callBackListener = callBackListener;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        // Checking the root path and make new folder (if not existed)
        String rootPath = Environment.getExternalStorageDirectory().toString();
        File newDir = new File(rootPath + "/iPainting/");
        newDir.mkdirs();

        File savedFile = new File(newDir,
                "ipainting" + String.valueOf(Calendar.getInstance().getTimeInMillis()) + ".png");
        try {
            // Try to write the bitmap to file on disk
            if (!savedFile.exists()) {
                savedFile.createNewFile();
            }
            FileOutputStream ostream = new FileOutputStream(savedFile);
            mImageBitmap.compress(Bitmap.CompressFormat.PNG, 10, ostream);
            ostream.flush();
            ostream.close();

            isSuccess = true;
        } catch (IOException | SecurityException e) {
            e.printStackTrace();

            isSuccess = false;
        } finally {
            savedFilePath = savedFile.toString();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        callBackListener.onImageSaved(savedFilePath, isSuccess);
        super.onPostExecute(aVoid);
    }
}
