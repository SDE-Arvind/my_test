package rathour.arvind.com.okhttp.utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import rathour.arvind.com.okhttp.beans.Example;

/**
 * Created by Arvind Kumar on 06-Dec-16.
 */

public class JsonParsor {

    private static final Gson mGson = new Gson();

    public static Example createExampleBeans(String iResponse) {

        Example statesBeans = null;
        try {
            statesBeans = mGson.fromJson(iResponse, Example.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return statesBeans;
    }

}
