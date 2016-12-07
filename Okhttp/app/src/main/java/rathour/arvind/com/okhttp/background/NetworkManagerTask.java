package rathour.arvind.com.okhttp.background;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import rathour.arvind.com.okhttp.ServerResponseListener;
import rathour.arvind.com.okhttp.beans.Example;
import rathour.arvind.com.okhttp.utils.JsonParsor;
import rathour.arvind.com.okhttp.utils.NetworkUtility;

/**
 * This is the base class for all the requests made to the server.
 * It is used for doing network operations in the background.
 */
public class NetworkManagerTask extends AsyncTask<Void, Void, Object> {

    private final String mUrl;
    private final String mRequest;
    private final int mRequestMethod;
    private final ServerResponseListener mListener;
    private final int mServerCallPurpose;
    private static final String TAG = "Network Manager";

    public NetworkManagerTask(String mUrl, String mRequest, int mRequestMethod,
                              ServerResponseListener mListener, int mServerCallPurpose) {
        this.mUrl = mUrl;
        this.mRequest = mRequest;
        this.mRequestMethod = mRequestMethod;
        this.mListener = mListener;
        this.mServerCallPurpose = mServerCallPurpose;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Object doInBackground(Void... params) {
        String response=null;
//        if(NetworkUtility.isInternetConnected(this)) {
           response = NetworkUtility.makeHttpRequest(mUrl, mRequestMethod, mRequest, mListener, mServerCallPurpose);
//        }else {
            // no internet connection
//        }
        Log.d(TAG, "do_in_back res:" + response);

        // set model class data from incoming json
        Example example = JsonParsor.createExampleBeans(response);
        Log.d(TAG, "back " + example.getPerPage());
        return example;
    }

    @Override
    protected void onPostExecute(Object iResponse) {
        Log.d(TAG, "post execute called res:" + iResponse);
        if (iResponse != null) {
            mListener.responseFromServer(iResponse, mServerCallPurpose);
        }
    }
}
