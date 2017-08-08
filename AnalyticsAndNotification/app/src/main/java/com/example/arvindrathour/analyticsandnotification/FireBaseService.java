package com.example.arvindrathour.analyticsandnotification;

import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Arvind Rathour on 04-May-17.
 */

public class FireBaseService extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d("GET ", "From: " + remoteMessage.getFrom());
//       send from FCM server  {
//            "to" : " c5vqIJYkTfg:APA91bEQ5xw7srEMPZmbpkjz26wzZjDCyA4e_580KFqiasLnNPtEzXCBNFiSMkxgRkW8FdaJ6TZKmtXGaABL-c-qeDxIHLc5nq4gXxCny7rwB5Klu0xWNQfsyjoZwmFAzVB3C152sg6B",
//                "notification" : {
//            "body" : "Hello ",
//                    "title" : "Spott Order",
//                    "icon" : "myicon"
//        },
//            "data": {
//                    "questionId": 1,
//                    "userDisplayName": "Test",
//                    "questionTitle": "Test",
//                    "latestComment": "Test"
//        }
//        }
        if (remoteMessage.getNotification() != null) {
            Log.e("dataChat",remoteMessage.getData().toString());
//            dataChat: {latestComment=Test, questionTitle=Test, userDisplayName=Test, questionId=1}

            try
            {
                Map<String, String> params = remoteMessage.getData();
                JSONObject object = new JSONObject(params);
                Log.e("JSON_OBJECT", object.toString());
//                JSON_OBJECT: {"questionId":"1","latestComment":"Test","questionTitle":"Test","userDisplayName":"Test"}
                Log.e("notification body", remoteMessage.getNotification().getBody());
                Log.e("data body", remoteMessage.getData().toString());
//                notification body: Hello
                Intent local = new Intent();
                local.setAction("com.hello.action");
                local.putExtra("data",remoteMessage.getNotification().getBody());
                this.sendBroadcast(local);


                Log.e("latestComment", object.get("latestComment").toString());
            }catch (Exception ignored){}
        }
    }
}
