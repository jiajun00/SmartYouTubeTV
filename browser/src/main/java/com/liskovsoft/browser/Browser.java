package com.liskovsoft.browser;

import android.app.Application;
import android.content.res.AssetManager;
import android.os.StrictMode;
import android.webkit.CookieSyncManager;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import java.io.IOException;
import java.io.InputStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.Properties;

public class Browser extends Application {
    // Set to true to enable verbose logging.
    final static boolean LOGV_ENABLED = false;

    // Set to true to enable extra debug logging.
    final static boolean LOGD_ENABLED = true;

    // TODO: do something constant values might be wrong
    final static String EXTRA_SHARE_FAVICON = "share_favicon";
    final static String EXTRA_SHARE_SCREENSHOT = "share_screenshot";
    public static boolean acitivityRestored;

    private static Bus sBus;
    private static Properties sProperties;

    @Override
    public void onCreate() {
        super.onCreate();

        // create CookieSyncManager with current Context
        CookieSyncManager.createInstance(this);
        BrowserSettings.initialize(getApplicationContext());
        //Preloader.initialize(getApplicationContext());

        // Leave empty, Play Market use built in exception handling mechanism.
        // Don't uncomment. Already used Crashlytics in parent app.
        // Setup handler for uncaught exceptions.
        //mHandler = new SimpleUncaughtExceptionHandler(getApplicationContext());
        //Thread.setDefaultUncaughtExceptionHandler(mHandler);

        initProperties();
    }

    private void initProperties() {
        if (sProperties != null) {
            return;
        }
        try {
            sProperties = new Properties();
            AssetManager assetManager = this.getAssets();
            InputStream inputStream = assetManager.open("browser.properties");
            sProperties.load(inputStream);
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static EngineType sEngineType = EngineType.WebView;

    public enum EngineType {
        WebView, XWalk
    }

    public static void setEngineType(EngineType engineType) {
        sEngineType = engineType;
    }

    public static EngineType getEngineType() {
        return sEngineType;
    }

    public static Bus getBus() {
        if (sBus == null) {
            sBus = new Bus(ThreadEnforcer.ANY);
        }
        return sBus;
    }

    public static String getProperty(String key) {
        if (sProperties == null) {
            return null;
        }
        return sProperties.getProperty(key);
    }

    private UncaughtExceptionHandler mHandler;

}

