package com.ats.barstockexchange.util;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by maxadmin on 28/11/17.
 */

public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("--------------------", "-------------------------------------");
        Log.e(TAG, " refreshedToken: " + refreshedToken);
        storeToken(refreshedToken);
    }

    private void storeToken(String token) {
        SharedPrefManager.getmInstance(getApplicationContext()).saveDeviceToken(token);
    }

}
