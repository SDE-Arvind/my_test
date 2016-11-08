package com.example.arvindkumar.bluetoothchatapplication;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/*
*      Application to send simple string between two bluetooth connected  devices
* */
public class MainActivity extends AppCompatActivity {
    public static final int MESSAGE_READ_START = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_READ_COMPLETE = 3;



    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_ENABLE_BT = 2;


    private EditText mEditTextSend;
    private Button mBtnSend;
    private ImageView mIvIcon;

    private StringBuffer mOutStringBuffer;
    private BluetoothAdapter mBluetoothAdapter = null;
    private ChatService mChatService = null;
    private byte[] data ;;
    private Handler handler = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            if(msg.what == MESSAGE_READ_START)
            {
                data= new byte[40000];
                Log.e("TAG","start sending");
            }else if (msg.what == MESSAGE_READ_COMPLETE) {
                ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(data);

                Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
               // BitmapDrawable ob = new BitmapDrawable(getResources(), bitmap);
                Toast.makeText(getApplicationContext(), "DATA RECEIVED", Toast.LENGTH_SHORT).show();
                Log.e("TAG","com "+arrayInputStream);
                Log.e("TAG","received bytes "+data);

                mIvIcon.setImageBitmap(bitmap);//setBackgroundDrawable(ob);
            } else if(msg.what == MESSAGE_READ ) {

                byte[] readBuf = (byte[]) msg.obj;
                Log.e("TAG","r buffer size "+readBuf.length);
//                Toast.makeText(getApplicationContext(), String.valueOf(readBuf.length), Toast.LENGTH_SHORT).show();
                if(readBuf!=null)
                System.arraycopy(data, 0, readBuf, 0, readBuf.length);
            }
            return true;
        }
    });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        getWidgetReferences();
//        bindEventHandler();

        mBtnSend.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                data=new byte[40000];
                Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.download);
                sendMessage(convertBitmapToByteArray(largeIcon));
            }
        });

    }

    private void getWidgetReferences() {
        mEditTextSend = (EditText) findViewById(R.id.etMain);
        mBtnSend = (Button) findViewById(R.id.btnSend);
        mIvIcon = (ImageView) findViewById(R.id.iv_icon);
    }
/*
    private void bindEventHandler() {
        mBtnSend.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                String message = mEditTextSend.getText().toString();
                sendMessage(message);
            }
        });
    }*/

    // to Enable bluetooth if not enable on app startup
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data);
                }
                break;
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    setupChat();
                } else {
                    Toast.makeText(this, R.string.bt_not_enabled_leaving,
                            Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    private void connectDevice(Intent data) {
        String address = data.getExtras().getString(
                DeviceListActivity.DEVICE_ADDRESS);
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        mChatService.connect(device);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent serverIntent = null;
        switch (item.getItemId()) {
            case R.id.secure_connect_scan:
                serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE_SECURE);
                return true;
        }
        return false;
    }

    private void sendMessage(String message) {

        if (message.length() > 0) {
            byte[] send = message.getBytes();
            mChatService.write(send);

            mOutStringBuffer.setLength(0);
            mEditTextSend.setText(mOutStringBuffer);
        }
    }

    private void sendMessage(byte[] message) {
        Log.e("TAG ","send b size"+ message.length);
        Log.e("TAG ","send byte"+ message);

        mChatService.write(message);
        mOutStringBuffer.setLength(0);
    }

    private void setupChat() {
        mChatService = new ChatService(handler, this);
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(
                    BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
        } else {
            if (mChatService == null)
                setupChat();
        }
    }

    @Override
    public synchronized void onResume() {
        super.onResume();
        if (mChatService != null) {
            if (mChatService.getState() == ChatService.STATE_NONE) {
                mChatService.start();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mChatService != null) {
            mChatService.stop();
        }
    }

    public byte[] convertBitmapToByteArray(Bitmap bitmap) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream(bitmap.getWidth() * bitmap.getHeight());
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, buffer);

        Toast.makeText(getApplicationContext(), String.valueOf(buffer.size()), Toast.LENGTH_SHORT).show();
        return buffer.toByteArray();
    }
}
