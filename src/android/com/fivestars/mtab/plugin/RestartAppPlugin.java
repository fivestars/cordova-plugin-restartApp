package com.fivestars.mtab.plugin;

import java.lang.System;

import android.app.Activity;
import android.app.AlarmManager;
import android.content.Intent;
import android.app.PendingIntent;
import android.content.Context;
import android.content.pm.PackageManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.json.JSONArray;

import android.os.Build;
import android.util.Log;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;


public class RestartAppPlugin extends CordovaPlugin {

    private static final String EXECUTE_RESTART = "RESTART_APPLICATION";
    private static final long DEFAULT_DELAY = 2000L;
   
    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

        if (EXECUTE_RESTART.equals(action)) {
            if (args.length() == 2) {
                final long delay = args.getLong(0);
                final boolean exit = args.getBoolean(1);
                return restartApp(delay, exit);
            }
            return restartApp();
        }
        
        // the action doesn't exist
        return false;
    }

    private boolean restartApp() {
        // use the default delay and exit the app by default
        return restartApp(DEFAULT_DELAY, true);
    }

    private boolean restartApp(long delay, boolean exit) {
        Activity cordovaActivity = this.cordova.getActivity();
        Context appContext = cordovaActivity.getApplicationContext();

        PackageManager pm = cordovaActivity.getPackageManager();

        String pn = appContext.getPackageName();
        Log.d("RestartAppPlugin", "Restart: " + pn);

        //Intent intent = pm.getLaunchIntentForPackage(packageName);
        Intent intent = pm.getLaunchIntentForPackage(pn);
        if (intent == null) {
            Log.d("RestartAppPlugin", "Launch Intent not found");
            return false;
        }
        
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        cordovaActivity.finish();

        // Pending intent starts activity after 2 seconds
        AlarmManager mgr = (AlarmManager)appContext.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + delay, PendingIntent.getActivity(appContext, 0, intent, 0));

        if (exit) {
            // ToDo (bbil): Remove this once once cordova webviews are properly destroyed
            // Either cordova must be upgraded or webviews manually destroyed
            System.exit(2);
        }

        return true;
    }
}
