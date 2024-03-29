package com.example.bluetooth;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kakao.push.KakaoFirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends KakaoFirebaseInstanceIdService {

    private static final String TAG = "MyFirebase";

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(refreshedToken);
    }

    private void sendRegistrationToServer(String token){
        //기타 작업으로 활용
    }
}