package rathour.arvind.com.okhttp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import rathour.arvind.com.okhttp.ServerResponseListener;
import rathour.arvind.com.okhttp.constants.MyConstants;

/**
 * This is a utility to manage network connection and operations.
 */
public class NetworkUtility {

    private static final String TAG = NetworkUtility.class.getSimpleName();

    /**
     * @return true if device is connected or connecting to network, false otherwise.
     */
    public static boolean isInternetConnected(Context iContext) {
        if (iContext != null) {
            ConnectivityManager manager = (ConnectivityManager) iContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = manager.getActiveNetworkInfo();
            return networkInfo != null && networkInfo.isConnected();
        }
        return false;
    }

    /**
     * @param iUrl
     * @param iRequestMethod
     * @param iRequestString
     * @param iListener
     * @param iCallPurpose
     * @return    response string in json format
     */
    public static String makeHttpRequest(String iUrl, int iRequestMethod, String iRequestString,
                                         ServerResponseListener iListener, int iCallPurpose) {
        RequestBody body = null;
        if (iRequestString != null) {
            MediaType JSON
                    = MediaType.parse("application/json; charset=utf-8");
            body = RequestBody.create(JSON, iRequestString);
            Log.d(TAG, "Request body Json" + iRequestString);
        }
        OkHttpClient client = new OkHttpClient();
        Request request = null;

        switch (iRequestMethod) {
            case MyConstants.HTTP_GET:
                request = new Request.Builder()
                        .url(iUrl)
                        .build();
                Log.d(TAG, "get request" + iUrl);
                break;
            case MyConstants.HTTP_POST:
                request = new Request.Builder()
                        .url(iUrl)
                        .post(body)
                        .build();
                Log.d(TAG, "Post request");
                break;
            case MyConstants.HTTP_DELETE:
                request = new Request.Builder()
                        .url(iUrl)
                        .delete(body)
                        .build();
                Log.d(TAG, "Delete request");
                break;
            default:
              Log.e(TAG,"Unknown request type");
        }

        Response response=null;
        try {
            response = client.newCall(request).execute();
            Log.d(TAG, response.toString());
            return response.body().string();
        } catch (IOException | NullPointerException e) {
            Log.d(TAG, e.toString());
            new ApiError().setCode(response.code()).setMessage(response.message());
        }
        return null;
    }
}
