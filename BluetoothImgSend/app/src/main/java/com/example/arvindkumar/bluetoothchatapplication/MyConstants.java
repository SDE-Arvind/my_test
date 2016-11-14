package com.example.arvindkumar.bluetoothchatapplication;

/**
 * Created by Arvind Kumar on 10-Nov-16.
 */

public interface MyConstants {
    int MESSAGE_READ_START = 1;
    int MESSAGE_READ = 2;
    int MESSAGE_READ_COMPLETE = 3;

    int REQUEST_CONNECT_DEVICE_SECURE = 1;
    int REQUEST_ENABLE_BT = 2;
    int REQUEST_SELECT_IMAGE = 3;

    // MyConstants that indicate the current connection state
    int STATE_NONE = 0;
    int STATE_LISTEN = 1; // listening connection
    int STATE_CONNECTING = 2; // initiate outgoing
    int STATE_CONNECTED = 3; // connected to remote device

    int IMAGW_QUALITY =100 ;
}
