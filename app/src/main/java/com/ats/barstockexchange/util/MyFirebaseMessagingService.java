package com.ats.barstockexchange.util;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ats.barstockexchange.R;
import com.ats.barstockexchange.activity.HomeActivity;
import com.ats.barstockexchange.activity.LoginActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by maxadmin on 28/11/17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0 || !remoteMessage.getNotification().equals(null)) {
            try {
                JSONObject json = new JSONObject(remoteMessage.getData());
                sendPushNotification(json);
            } catch (Exception e) {
                e.printStackTrace();
            }

            super.onMessageReceived(remoteMessage);

        }
    }

    @Override
    public void handleIntent(Intent intent) {
        super.handleIntent(intent);
    }

    private void sendPushNotification(JSONObject json) {

        //Log.e(TAG, "--------------------------------JSON String" + json.toString());
        try {
            //JSONObject data = json.getJSONObject("data");
            String title = json.getString("title");
            String message = json.getString("body");
            String imageUrl = "";
            int tag = json.getInt("tag");

            MyNotificationManager mNotificationManager = new MyNotificationManager(getApplicationContext());
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            intent.putExtra("FcmTag", tag);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            mNotificationManager.showSmallNotification(title, message, intent);
//            if (imageUrl.equals("null")) {
//                mNotificationManager.showSmallNotification(title, message, intent);
//            } else {
//                mNotificationManager.showBigNotification(title, message, imageUrl, intent);
//            }

        } catch (JSONException e) {
            //Log.e(TAG, "Json Exception: -----------" + e.getMessage());
        } catch (Exception e) {
            //Log.e(TAG, "Exception: ------------" + e.getMessage());
            e.printStackTrace();
        }

    }
}

