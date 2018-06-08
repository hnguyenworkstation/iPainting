package com.app.hoocons.ipainting.Helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * Created by hungnguyen on 6/7/18.
 * At: 6:44 PM
 */

public class AppUtils {
    public static void startGalleryIntent(final Activity activity, final int code) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), code);
    }
}
