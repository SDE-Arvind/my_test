package rathour.arvind.com.okhttp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import rathour.arvind.com.okhttp.background.NetworkManagerTask;
import rathour.arvind.com.okhttp.beans.Example;
import rathour.arvind.com.okhttp.constants.MyConstants;
import rathour.arvind.com.okhttp.constants.MyUrls;
import rathour.arvind.com.okhttp.constants.ServerCallPurpose;
import rathour.arvind.com.okhttp.utils.ProgressDialogUtils;

public class MainActivity extends AppCompatActivity implements ServerResponseListener, DialogInterface.OnClickListener {
    TextView mText;
    private static final String TAG = "MainActivity";
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mText = (TextView) findViewById(R.id.textView);
        load();
    }

    private void load() {
        String requestJson = null;//"{\"name\":\"morpheus\",\"job\":\"leader\"}";
        String url = MyUrls.GET_USERS;
        mProgressDialog = new ProgressDialog(this);
        ProgressDialogUtils.startProgressDialog(mProgressDialog, getApplicationContext(), this);
        new NetworkManagerTask(url,
                requestJson,
                MyConstants.HTTP_GET,
                this,
                ServerCallPurpose.GET_LIST
        ).execute();
    }

    @Override
    public void responseFromServer(Object iClassObject, int iCallPurpose) {

        switch (iCallPurpose) {
            case ServerCallPurpose.GET_LIST:
                // get data from class and set on view
                Example ex = (Example) iClassObject;
                display(ex);
                ProgressDialogUtils.dismissProgressDialog(mProgressDialog);
                break;
        }
    }

    /**
     *  set data on view from model class
     * @param ex model class object
     */
    private void display(Example ex) {
        Log.d(TAG, "" + ex.getTotalPages());
        mText.setText("" + ex.getTotalPages() + " " + ex.getData().get(1).getFirstName());

    }

    @Override
    public void errorOccurred(int iResponseCode, Exception iException, int iCallPurpose) {
        Toast.makeText(getApplicationContext(), "" + iResponseCode, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

    }
}
