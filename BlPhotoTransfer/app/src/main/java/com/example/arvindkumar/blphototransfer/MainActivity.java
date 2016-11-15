package com.example.arvindkumar.blphototransfer;

import android.Manifest;
import android.app.ProgressDialog;
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
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.arvindkumar.blphototransfer.btfxr.ClientThread;
import com.example.arvindkumar.blphototransfer.btfxr.Constants;
import com.example.arvindkumar.blphototransfer.btfxr.MessageType;
import com.example.arvindkumar.blphototransfer.btfxr.ProgressData;
import com.example.arvindkumar.blphototransfer.btfxr.ServerThread;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BTPHOTO/MainActivity";
    private Spinner mDeviceSpinner;
    private ProgressDialog mProgressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // permission for marshmallow
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH);
            ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_ADMIN);
            Log.d("TAG", "permission granted for marshmallow");

        }

        MainApplication.clientHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MessageType.READY_FOR_DATA: {
                        Intent selectPictureIntent = new Intent();
                        selectPictureIntent.setType("image/*");
                        selectPictureIntent.setAction(Intent.ACTION_PICK);
                        startActivityForResult(Intent.createChooser(selectPictureIntent, "Select Picture:"), MainApplication.PICTURE_RESULT_CODE);
                        break;
                    }

                    case MessageType.COULD_NOT_CONNECT: {
                        Toast.makeText(MainActivity.this, R.string.couldnt_connect_device, Toast.LENGTH_SHORT).show();
                        break;
                    }

                    case MessageType.SENDING_DATA: {
                        mProgressDialog = new ProgressDialog(MainActivity.this);
                        mProgressDialog.setMessage("Sending photo...");
                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        mProgressDialog.show();
                        break;
                    }

                    case MessageType.DATA_SENT_OK: {
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        Toast.makeText(MainActivity.this, R.string.sent_success, Toast.LENGTH_SHORT).show();
                        break;
                    }

                    case MessageType.DIGEST_DID_NOT_MATCH: {
                        Toast.makeText(MainActivity.this, R.string.send_img_incorrect, Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        };

        MainApplication.serverHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MessageType.DATA_RECEIVED: {
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        Bitmap image = BitmapFactory.decodeByteArray(((byte[]) message.obj), 0, ((byte[]) message.obj).length, options);
                        ImageView imageView = (ImageView) findViewById(R.id.iv_show_image);
                        imageView.setImageBitmap(image);
                        //save image to sd card
                        saveToSDCard(image);
                        break;
                    }

                    case MessageType.DIGEST_DID_NOT_MATCH: {
                        Toast.makeText(MainActivity.this, R.string.received_img_incorrect, Toast.LENGTH_SHORT).show();
                        break;
                    }

                    case MessageType.DATA_PROGRESS_UPDATE: {
                        // some kind of update
                        MainApplication.progressData = (ProgressData) message.obj;
                        double pctRemaining = 100 - (((double) MainApplication.progressData.remainingSize / MainApplication.progressData.totalSize) * 100);
                        if (mProgressDialog == null) {
                            mProgressDialog = new ProgressDialog(MainActivity.this);
                            mProgressDialog.setMessage("Receiving photo...");
                            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            mProgressDialog.setProgress(0);
                            mProgressDialog.setMax(100);
                            mProgressDialog.show();
                            mProgressDialog.setCancelable(false);
                        }
                        mProgressDialog.setProgress((int) Math.floor(pctRemaining));
                        break;
                    }

                    case MessageType.INVALID_HEADER: {
                        Toast.makeText(MainActivity.this, R.string.incorrect_header, Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        };

        if (MainApplication.pairedDevices != null) {
            if (MainApplication.serverThread == null) {
                Log.v(TAG, "Starting server thread waiting for socket connection.  Able to accept photos.");
                MainApplication.serverThread = new ServerThread(MainApplication.adapter, MainApplication.serverHandler);
                MainApplication.serverThread.start();
            }
        }

        if (MainApplication.pairedDevices != null) {
            ArrayList<DeviceData> deviceDataList = new ArrayList<DeviceData>();
            for (BluetoothDevice device : MainApplication.pairedDevices) {
                deviceDataList.add(new DeviceData(device.getName(), device.getAddress()));
            }

            ArrayAdapter<DeviceData> deviceArrayAdapter = new ArrayAdapter<DeviceData>(this, android.R.layout.simple_spinner_item, deviceDataList);
            deviceArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mDeviceSpinner = (Spinner) findViewById(R.id.deviceSpinner);
            mDeviceSpinner.setAdapter(deviceArrayAdapter);

            Button clientButton = (Button) findViewById(R.id.clientButton);
            clientButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DeviceData deviceData = (DeviceData) mDeviceSpinner.getSelectedItem();
                    for (BluetoothDevice device : MainApplication.adapter.getBondedDevices()) {
                        if (device.getAddress().contains(deviceData.getValue())) {
                            Log.v(TAG, "Starting client thread");
                            if (MainApplication.clientThread != null) {
                                MainApplication.clientThread.cancel();
                            }
                            MainApplication.clientThread = new ClientThread(device, MainApplication.clientHandler);
                            MainApplication.clientThread.start();
                        }
                    }
                }
            });
        } else {
            Toast.makeText(this, "Bluetooth is not enabled or supported on this device", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MainApplication.PICTURE_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                Log.v(TAG, "Photo acquired from SD card intent");
                try {
                    Uri selectedImageUri = data.getData();
                    String selectedImagePath = getPath(selectedImageUri);
                    Bitmap image = BitmapFactory.decodeFile(selectedImagePath);
                    ByteArrayOutputStream compressedImageStream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.JPEG, MainApplication.IMAGE_QUALITY, compressedImageStream);
                    byte[] compressedImage = compressedImageStream.toByteArray();
                    Log.v(TAG, "Compressed image size: " + compressedImage.length);
                    // Invoke client thread to send
                    Message message = new Message();
                    message.obj = compressedImage;
                    MainApplication.clientThread.mIncomingHandler.sendMessage(message);

                    // Display the image locally
                    ImageView imageView = (ImageView) findViewById(R.id.iv_show_image);
                    imageView.setImageBitmap(image);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
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

    class DeviceData {
        public DeviceData(String spinnerText, String value) {
            this.spinnerText = spinnerText;
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public String toString() {
            return spinnerText;
        }

        String spinnerText;
        String value;
    }

    private void saveToSDCard(Bitmap image) {
        Log.d("TAG", "image saved in sd card");
        Date todayDate = new Date();
        android.text.format.DateFormat.format(Constants.IMAGE_NAME_FORMAT, todayDate);
        try {
            // Create a directory
            File directory = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.DIRECTORY_NAME);
            directory.mkdir();

            File imageFile = new File(directory, todayDate.toString() + Constants.IMAGE_FORMAT);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
//            Use the compress method on the BitMap object to write image to the OutputStream
            image.compress(Bitmap.CompressFormat.JPEG, MainApplication.IMAGE_QUALITY, outputStream);
            outputStream.flush();
            outputStream.close();
            Log.d("TAG", "path :" + imageFile.getAbsolutePath());
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
