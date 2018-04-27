package com.example.arvindrathour.facebooklogin;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONObject;

import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {

    CallbackManager callbackManager;
    TextView mTextView;
    LoginButton mLoginButton;
    ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        mTextView = findViewById(R.id.text_view);
        mLoginButton = findViewById(R.id.login_button);
        mImageView = findViewById(R.id.imageView);
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        mTextView.setText("Logged in: ");

                        GraphRequest request = GraphRequest.newMeRequest(
                                loginResult.getAccessToken(),
                                new GraphRequest.GraphJSONObjectCallback() {
                                    @Override
                                    public void onCompleted(JSONObject object, GraphResponse response) {

                                        response.getError();

                                        Log.e("TAG",object.toString());

                                        try {
                                            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                            StrictMode.setThreadPolicy(policy);
                                            String profilePicUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");

                                            URL fb_url = new URL(profilePicUrl);//small | noraml | large
                                            HttpsURLConnection conn1 = (HttpsURLConnection) fb_url.openConnection();
                                            HttpsURLConnection.setFollowRedirects(true);
                                            conn1.setInstanceFollowRedirects(true);
                                            Bitmap fb_img = BitmapFactory.decodeStream(conn1.getInputStream());
                                            mImageView.setImageBitmap(fb_img);
                                        } catch (Exception ex) {
                                            ex.printStackTrace();
                                        }
                                    }
                                });
                        Bundle parameters = new Bundle();
                        parameters.putString("fields", "id,picture");
                        request.setParameters(parameters);
                        request.executeAsync();

                        Log.e("TAG", "https://graph.facebook.com/" + loginResult.getAccessToken().getUserId() + "/picture?type=large&width=1080");
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        mTextView.setText("Login Canceled ");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code\
                        mTextView.setText("Login Error: " + exception.getMessage());
                    }
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
