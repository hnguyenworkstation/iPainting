package com.app.hoocons.ipainting;

import android.app.Application;
import android.content.Context;
import android.os.StrictMode;

/**
 * Created by hungnguyen on 6/6/18.
 * At: 10:23 PM
 */

public class BaseApplication extends Application {
    private static BaseApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = BaseApplication.this;

        /* Fixing URI exposed */
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }

    public static synchronized BaseApplication getInstance() {
        return mInstance;
    }
}
