package com.example.arvindkumar.bluetoothconnection;

import android.Manifest;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.arvindkumar.bluetoothconnection.adapters.DeviceListAdapter;
import com.example.arvindkumar.bluetoothconnection.bgthreads.ClientThread;
import com.example.arvindkumar.bluetoothconnection.bgthreads.ServerThread;
import com.example.arvindkumar.bluetoothconnection.constants.Constants;
import com.example.arvindkumar.bluetoothconnection.constants.MessageType;
import com.example.arvindkumar.bluetoothconnection.utilitiy.ProgressData;
import com.example.arvindkumar.bluetoothconnection.utilitiy.Utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private Spinner mSpnrDevices;
    private Button mBtnSend;
    private ToggleButton mTbtnBluetooth;
    private ImageButton mBtnRefresh;
    private TextView mBluetoothStatus;
    private ListView mActiveDeviceList;
    private BluetoothAdapter mBluetoothAdapter;
    private List<BluetoothDevice> mBluetoothDevices;
    private Set<BluetoothDevice> mPairedDevices;
    private ProgressDialog mProgressDialog;
    private Handler mClientHandler;
    private Handler mServerHandler;
    private ClientThread mClientThread;
    private ServerThread mServerThread;
    private ProgressData mProgressData = new ProgressData();
    private DeviceListAdapter mDeviceAdapter;
    private static final int PICTURE_RESULT_CODE = 1234;
    private String mFilename;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            addPermission();
        }
        initView();
        initHandler();
        //check bluetooth state
        if (mBluetoothAdapter.isEnabled()) {
            showEnabled();
        } else {
            showDisabled();
        }
        // Register receiver to listen bluetooth state
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }


    /**
     * initialize views
     */
    private void initView() {
        mSpnrDevices = (Spinner) findViewById(R.id.spinner_select_device);
        mBtnSend = (Button) findViewById(R.id.btn_send);
        mActiveDeviceList = (ListView) findViewById(R.id.list_active_devices);

        mBtnRefresh = (ImageButton) findViewById(R.id.ibtn_refresh);
        mTbtnBluetooth = (ToggleButton) findViewById(R.id.btn_bluetooth_power);
        mBluetoothStatus = (TextView) findViewById(R.id.tv_bt_status);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mDeviceAdapter = new DeviceListAdapter(this);

        mBtnSend.setOnClickListener(this);
        mBtnRefresh.setOnClickListener(this);
        mTbtnBluetooth.setOnClickListener(this);
        mTbtnBluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivity(intent);
                } else {
                    mBluetoothAdapter.disable();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICTURE_RESULT_CODE) {
            Log.v(TAG, "File selection request");
            if (resultCode == RESULT_OK) {
                Log.v(TAG, "File selected from SD card");
                try {
                    Uri selectedImageUri = data.getData();
                    String selectedFilePath = getAbsolutePath(selectedImageUri);
                    byte[] fileBytes = createByteArray(selectedFilePath);
                    Log.v(TAG, "file path " + selectedFilePath);

                    // Invoke client thread to send
                    Bundle name = new Bundle();
                    name.putString("fileName", selectedFilePath.substring(selectedFilePath.lastIndexOf('/') + 1));
                    Message message = new Message();
                    message.obj = fileBytes;
                    message.setData(name);
                    mClientThread.mIncomingHandler.sendMessage(message);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }
            }
        }
    }


    /**
     * initialize handler to take response from client thread and server thread
     */
    private void initHandler() {
        mClientHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MessageType.READY_FOR_DATA: {
                        Log.d(TAG, "Client handler ready for data");
                        Intent selectFileIntent = new Intent(Intent.ACTION_GET_CONTENT);
                        selectFileIntent.setType("file/*");
                        selectFileIntent.addCategory(Intent.CATEGORY_OPENABLE);
                        startActivityForResult(Intent.createChooser(selectFileIntent, "Select File:"), PICTURE_RESULT_CODE);
                        break;
                    }
                    case MessageType.COULD_NOT_CONNECT: {
                        Utils.showToast(MainActivity.this, getString(R.string.couldnt_connect_device));
                        break;
                    }
                    case MessageType.SENDING_DATA: {
                        // show progress dialog on client side
                        mProgressDialog = new ProgressDialog(MainActivity.this);
                        mProgressDialog.setMessage("Sending data...");
                        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                        mProgressDialog.setCancelable(false);
                        mProgressDialog.setCanceledOnTouchOutside(false);

                        mProgressDialog.show();
                        break;
                    }
                    case MessageType.DATA_SENT_OK: {
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        Utils.showToast(MainActivity.this, getString(R.string.sent_success));
                        break;
                    }
                    case MessageType.DIGEST_DID_NOT_MATCH: {
                        Utils.showToast(MainActivity.this, getString(R.string.send_img_incorrect));
                        break;
                    }
                }
            }
        };

        mServerHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MessageType.DATA_RECEIVED: {
                        if (mProgressDialog != null) {
                            mProgressDialog.dismiss();
                            mProgressDialog = null;
                        }
                        saveToSDCard((byte[]) message.obj);
                        break;
                    }
                    case MessageType.DIGEST_DID_NOT_MATCH: {
                        Utils.showToast(MainActivity.this, getString(R.string.received_img_incorrect));
                        break;
                    }
                    case MessageType.DATA_PROGRESS_UPDATE: {
                        // some kind of update
                        Log.v(TAG, "progress update");
                        mProgressData = (ProgressData) message.obj;
                        double pctRemaining = 100 - (((double) mProgressData.remainingSize / mProgressData.totalSize) * 100);
                        if (mProgressDialog == null) {
                            mProgressDialog = new ProgressDialog(MainActivity.this);
                            mProgressDialog.setMessage("Receiving Data...");
                            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            mProgressDialog.setProgress(0);
                            mProgressDialog.setMax(100);
                            mProgressDialog.setCancelable(false);
                            mProgressDialog.setCanceledOnTouchOutside(false);

                            mProgressDialog.show();
                        }
                        mProgressDialog.setProgress((int) Math.floor(pctRemaining));
                        break;
                    }
                    case MessageType.INVALID_HEADER: {
                        Utils.showToast(MainActivity.this, getString(R.string.incorrect_header));
                        break;
                    }
                    case MessageType.NAME_RECEIVED: {
                        byte[] msg = (byte[]) message.obj;
                        String name = new String(msg);
                        mFilename = name;
                        Log.d("byte ", Arrays.toString(msg));
                        Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
                        break;
                    }
                }
            }
        };
    }

    /**
     * To give permission if device is marshmallow or onwards
     */
    private void addPermission() {

        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH);
        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.BLUETOOTH_ADMIN);
        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION);
        ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_COARSE_LOCATION);

        Log.d("TAG", "permission granted for marshmallow");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ibtn_refresh:
                loadDevices();
                break;
            case R.id.btn_send:
                DeviceData deviceData = (DeviceData) mSpnrDevices.getSelectedItem();
                if (deviceData != null) {
                    for (BluetoothDevice device : mBluetoothAdapter.getBondedDevices()) {
                        if (device.getAddress().contains(deviceData.getValue())) {
                            Log.v(TAG, "Starting client thread");
                            if (mClientThread != null) {
                                mClientThread.cancel();
                            }
                            // starting client thread
                            mClientThread = new ClientThread(device, mClientHandler);
                            mClientThread.start();
                        }
                    }
                }
                break;
        }
    }

    /**
     * show bluetooth status enable
     */
    private void showEnabled() {
        mBluetoothStatus.setText("Bluetooth is On");
        mBluetoothStatus.setTextColor(ContextCompat.getColor(this, R.color.colorLightGreen));
        mTbtnBluetooth.setBackgroundResource(R.drawable.on);
        mTbtnBluetooth.setChecked(false);
    }

    /**
     * show bluetooth status disable
     */
    private void showDisabled() {
        mBluetoothStatus.setText("Bluetooth is Off");
        mBluetoothStatus.setTextColor(Color.RED);
        mTbtnBluetooth.setBackgroundResource(R.drawable.off);
        mTbtnBluetooth.setChecked(true);
    }

    /**
     * get all paired bluetooth devices and show in spinner
     */
    private void loadDevices() {
        if (mBluetoothAdapter.isEnabled()) {
            mPairedDevices = mBluetoothAdapter.getBondedDevices();
            if (mPairedDevices != null) {
                ArrayList<DeviceData> deviceDataList = new ArrayList<DeviceData>();
                for (BluetoothDevice device : mPairedDevices) {
                    deviceDataList.add(new DeviceData(device.getName(), device.getAddress()));
                }
                ArrayAdapter<DeviceData> deviceArrayAdapter = new ArrayAdapter<DeviceData>(this, android.R.layout.simple_spinner_item, deviceDataList);
                deviceArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                mSpnrDevices.setAdapter(deviceArrayAdapter);
                // On server socket on device it is able to receive client request
                if (mServerThread == null) {
                    Log.v(TAG, "Starting server thread waiting for socket connection.  Able to accept data.");
                    mServerThread = new ServerThread(mBluetoothAdapter, mServerHandler);
                    mServerThread.start();
                }
            }
        } else {
            Utils.showToast(this, getString(R.string.bt_disable));
        }
    }

    /**
     * @param iFileUri uri of file
     * @return absolute path of file in string form
     * @throws URISyntaxException
     * @throws FileNotFoundException
     */
    public String getAbsolutePath(Uri iFileUri) throws URISyntaxException, FileNotFoundException {
        File file = new File(iFileUri.getPath().toString());
        return file.getAbsolutePath();
    }

    /**
     * convert input stream to byte array
     *
     * @param stream input stream
     * @return byte array
     * @throws IOException
     */
    public static byte[] readFully(InputStream stream) throws IOException {
        byte[] buffer = new byte[8192];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        int bytesRead;
        while ((bytesRead = stream.read(buffer)) != -1) {
            baos.write(buffer, 0, bytesRead);
        }
        return baos.toByteArray();
    }

    /**
     * get byte array of file using file absolute path
     *
     * @param sourcePath absolute path
     * @return byte array
     * @throws IOException
     */
    public static byte[] createByteArray(String sourcePath) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(sourcePath);
            return readFully(inputStream);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * broadcast receiver to listen bluetooth state change or find devices
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = null;
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_ON) {
                    Utils.showToast(MainActivity.this, "Enabled");
                    mBluetoothAdapter.startDiscovery();
                    showEnabled();
                } else {
                    showDisabled();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                mBluetoothDevices = new ArrayList<>();
                mProgressDialog = new ProgressDialog(MainActivity.this);
                mProgressDialog.setMessage("Scan device...");
                mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                mProgressDialog.show();
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
                if (mBluetoothDevices != null) {
                    // pass device list to device adapter
                    mDeviceAdapter.setData(mBluetoothDevices);
                    // on click listener of button click of list view item
                    mDeviceAdapter.setListener(new DeviceListAdapter.OnPairButtonClickListener() {
                        @Override
                        public void onPairButtonClick(int position) {
                            BluetoothDevice device = mBluetoothDevices.get(position);
                            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                                unpairDevice(device);
                            } else {
                                Utils.showToast(MainActivity.this, "Pairing...");
                                pairDevice(device);
                            }
                        }
                    });
                    // set adapter to listView when find all scanned devices
                    mActiveDeviceList.setAdapter(mDeviceAdapter);
                }
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
//                    Utils.showToast(MainActivity.this, "Found device " + device.getName());
                    mBluetoothDevices.add((BluetoothDevice) intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE));
                }
            } else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {

                final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
                final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);

                if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
                    Utils.showToast(MainActivity.this, "Paired");
                } else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED) {
                    Utils.showToast(MainActivity.this, "Unpaired");
                }
                // update list of device if any device change its state
                mDeviceAdapter.notifyDataSetChanged();
            }

        }
    };


    /**
     * store Bluetooth  device name and its mac address
     */
    class DeviceData {
        String spinnerText;
        String value;

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
    }

    /**
     * Pair new active device
     *
     * @param device
     */
    private void pairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("createBond", (Class[]) null);
            method.invoke(device, (Object[]) null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * remove paired device
     *
     * @param device
     */
    private void unpairDevice(BluetoothDevice device) {
        try {
            Method method = device.getClass().getMethod("removeBond", (Class[]) null);
            method.invoke(device, (Object[]) null);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * create directory
     *
     * @return directory object
     */
    private File getDirectory() {
        // Create a directory
        File directory = new File(Environment.getExternalStorageDirectory() + File.separator + Constants.DIRECTORY_NAME);
        directory.mkdir();
        return directory;
    }

    /**
     * save file to SD card
     *
     * @param file in byte form
     */
    private void saveToSDCard(byte[] file) {
        Log.d("TAG", "file saved in sd card");
        try {
            File imageFile = new File(getDirectory(), mFilename);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            outputStream.write(file);
            outputStream.flush();
            outputStream.close();
            Log.d(TAG, "file saved :" + imageFile.getAbsolutePath());
        } catch (Throwable e) {
            Log.d(TAG, e.toString());
        }
    }
}