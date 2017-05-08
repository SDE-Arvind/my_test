package com.example.arvindrathour.analyticsandnotification;

import android.app.Application;
import android.content.Context;

/**
 * Created by Arvind Rathour on 08-May-17.
 */

public class App extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();

        context=this;
    }

    public static Context getCotext(){
        return context;
    }
}
