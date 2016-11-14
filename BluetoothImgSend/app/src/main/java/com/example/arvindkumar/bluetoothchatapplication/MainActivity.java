package com.example.arvindkumar.bluetoothchatapplication;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
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
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/*
*      Application to send small image between two bluetooth connected  devices
* */
public class MainActivity extends AppCompatActivity implements MyConstants {

    private EditText mEditTextSend;
    private Button mBtnSend;
    private ImageView mIvIcon;
    private Button mBtnChoose;

    private StringBuffer mOutStringBuffer;
    private BluetoothAdapter mBluetoothAdapter = null;
    private ChatService mChatService = null;
    private byte[] data = new byte[40000];
    private List<List<Byte>> list = new ArrayList<>();
    private int mSize = 0;
    Bitmap mImageBitmap;
    private Handler handler = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {

            if (msg.what == MESSAGE_READ_START) {
                Log.d("TAG", "start sending");
            } else if (msg.what == MESSAGE_READ_COMPLETE) {
                Toast.makeText(getApplicationContext(), "DATA RECEIVED :", Toast.LENGTH_LONG).show();
                Bitmap bitmap = convertByteArrayToBitmap(data);
//                bitmap = Bitmap.createScaledBitmap(bitmap, 160, 160, true);
                mIvIcon.setImageBitmap(bitmap);
                saveToSDCard(bitmap);
                Log.d("TAG", "data output: " + Arrays.toString(data));
                data = new byte[40000];
                mSize=0;
            } else if (msg.what == MESSAGE_READ) {
                byte[] readBuf = (byte[]) msg.obj;
                System.arraycopy(readBuf, 0, data, mSize, readBuf.length);
                mSize += readBuf.length;
                Log.d("TAG", "data byte: " + Arrays.toString(data));
                Log.d("TAG", "read buffer size: " + readBuf.length);
                Log.d("TAG", "data buffer size: " + mSize);
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
        mImageBitmap = BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher);

        // permission for marshmallow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            Log.d("TAG","permission granted");
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH);
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_ADMIN);
        }


        mBtnSend.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                // compress image to solve Socket overflow problem
                mImageBitmap = Bitmap.createScaledBitmap(mImageBitmap, 20, 20, true);
                sendMessage(convertBitmapToByteArray(mImageBitmap));
            }
        });

        mBtnChoose.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooser = new Intent();
                chooser.setType("image/*");
                chooser.setAction(Intent.ACTION_PICK);
                startActivityForResult(Intent.createChooser(chooser, "Select Picture:"), REQUEST_SELECT_IMAGE);
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putByteArray("image", convertBitmapToByteArray(mImageBitmap));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageBitmap = convertByteArrayToBitmap(savedInstanceState.getByteArray("image"));
        mIvIcon.setImageBitmap(mImageBitmap);
    }

    private void getWidgetReferences() {
        mBtnSend = (Button) findViewById(R.id.btn_send);
        mBtnChoose = (Button) findViewById(R.id.btn_choose_image);

        mIvIcon = (ImageView) findViewById(R.id.iv_icon);
    }

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
                break;
            case REQUEST_SELECT_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    Uri selectedImageUri = data.getData();
                    String selectedImagePath = getPath(selectedImageUri);
                    mImageBitmap = BitmapFactory.decodeFile(selectedImagePath);
                    mIvIcon.setImageBitmap(mImageBitmap);
                } else {
                    Toast.makeText(this, R.string.not_select_image,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public String getPath(Uri selectedImageUri) {
        String res = null;
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor = this.getContentResolver().query(selectedImageUri, filePathColumn, null, null, null);
        cursor.moveToFirst();
        int column_index = cursor.getColumnIndex(filePathColumn[0]);
        res = cursor.getString(column_index);
        cursor.close();
        return res;
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

    private void sendMessage(byte[] message) {
        Log.d("TAG ", "send b size" + message.length);
        Log.d("TAG ", "send byte" + message);
        Log.d("TAG ", "send byte data " + Arrays.toString(message));

        mChatService.write(message);
        mOutStringBuffer.setLength(0);
    }

    private void setupChat() {
        mChatService = new ChatService(handler, this);
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

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
        return buffer.toByteArray();
    }

    public Bitmap convertByteArrayToBitmap(byte[] bytes) {
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bytes);
        Bitmap bitmap = BitmapFactory.decodeStream(arrayInputStream);
        return bitmap;
    }
    private void saveToSDCard(Bitmap image) {
        Log.d("TAG", "image saved in sd card");
        Date todayDate = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", todayDate);
        try {
            // Create a directory
            File directory = new File(Environment.getExternalStorageDirectory()+ File.separator + "bt_received_file");
            directory.mkdir();

            File imageFile = new File(directory,todayDate.toString()+".jpg");
            FileOutputStream outputStream = new FileOutputStream(imageFile);
//            Use the compress method on the BitMap object to write image to the OutputStream
            image.compress(Bitmap.CompressFormat.JPEG, MyConstants.IMAGW_QUALITY, outputStream);
            outputStream.flush();
            outputStream.close();
            Log.d("TAG", "path :"+imageFile.getAbsolutePath());

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
