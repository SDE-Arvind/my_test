package com.example.arvindrathour.firebasepushnotification;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * to refresh token
 *
 * Created by Arvind Rathour on 07-Apr-17.
 */



public class MyFirebaseIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        Log.e("token ","..");
        String token= FirebaseInstanceId.getInstance().getToken();
        Log.e("token ",token);
    }
}
