package com.eviort.cabedriver;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class NTApplication extends Application {

    public static final String TAG = NTApplication.class
            .getSimpleName();
    private static NTApplication mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    public static synchronized NTApplication getInstance() {
        return mInstance;
    }

    public static String trimMessage(String json) {
        String trimmedString = "";

        try {
            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> iter = jsonObject.keys();
            while (iter.hasNext()) {
                String key = iter.next();
                try {
                    JSONArray value = jsonObject.getJSONArray(key);
                    for (int i = 0, size = value.length(); i < size; i++) {
                        Log.e("Errors in Form", "" + value.getString(i));
                        trimmedString += value.getString(i);
                        if (i < size - 1) {
                            trimmedString += '\n';
                        }
                    }
                } catch (JSONException e) {

                    trimmedString += jsonObject.optString(key);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        Log.e("Trimmed", "" + trimmedString);

        return trimmedString;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;


        final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

        // set in-app defaults
        Map<String, Object> remoteConfigDefaults = new HashMap();
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_REQUIRED, false);
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_CURRENT_VERSION, "3.0.0");
        remoteConfigDefaults.put(ForceUpdateChecker.KEY_UPDATE_URL,
                "https://play.google.com/store/apps/details?id=com.eviort.cabedriver");

        firebaseRemoteConfig.setDefaultsAsync(remoteConfigDefaults);
        firebaseRemoteConfig.fetch(60) // fetch every minutes
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "remote config is fetched.");
                            firebaseRemoteConfig.activate();
                        }
                    }
                });

             /*   initCalligraphyConfig();

                final FirebaseRemoteConfig firebaseRemoteConfig = FirebaseRemoteConfig.getInstance();

                // set in-app defaults
                Map<String, Object> remoteConfigDefaults = new HashMap();
                remoteConfigDefaults.put(NTForceUpdateChecker.KEY_UPDATE_REQUIRED, false);
                remoteConfigDefaults.put(NTForceUpdateChecker.KEY_CURRENT_VERSION, "1.0.4");
                remoteConfigDefaults.put(NTForceUpdateChecker.KEY_UPDATE_URL,
                        "https://play.google.com/store/apps/details?id=com.elite.passenger");
                firebaseRemoteConfig.setDefaults(remoteConfigDefaults);
                firebaseRemoteConfig.fetch(60) // fetch every minutes
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                                Log.d(TAG, "remote config is fetched.");
                                                firebaseRemoteConfig.activateFetched();
                                        }
                                }
                        });
        }

        private void initCalligraphyConfig() {
                CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath(getResources().getString(R.string.Light))
                        .setFontAttrId(R.attr.fontPath)
                        .build()
                );*/
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void cancelRequestInQueue(String tag) {
        getRequestQueue().cancelAll(tag);
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the no_user tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        req.setRetryPolicy(new DefaultRetryPolicy(
                30000, // Timeout in milliseconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Default is 1 retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // Backoff multiplier
        ));
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        req.setRetryPolicy(new DefaultRetryPolicy(
                30000, // Timeout in milliseconds
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, // Default is 1 retry
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT // Backoff multiplier
        ));
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }


}
