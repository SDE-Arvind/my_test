package com.example.arvindkumar.blphototransfer.btfxr;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class ServerThread extends Thread {
    private final String TAG = "android-btxfr/ServerThread";
    private final BluetoothServerSocket mServerSocket;
    private Handler mHandler;

    public ServerThread(BluetoothAdapter adapter, Handler handler) {
        this.mHandler = handler;
        BluetoothServerSocket tempSocket = null;
        try {
            tempSocket = adapter.listenUsingInsecureRfcommWithServiceRecord(Constants.NAME, UUID.fromString(Constants.UUID_STRING));
        } catch (IOException ioe) {
            Log.e(TAG, ioe.toString());
        }
        mServerSocket = tempSocket;
    }

    public void run() {
        BluetoothSocket socket = null;
        if (mServerSocket == null) {
            Log.d(TAG, "Server socket is null - something went wrong with Bluetooth stack initialization?");
            return;
        }
        while (true) {
            try {
                Log.v(TAG, "Opening new server socket");
                socket = mServerSocket.accept();

                try {
                    Log.v(TAG, "Got connection from client.  Spawning new data transfer thread.");
                    DataTransferThread dataTransferThread = new DataTransferThread(socket, mHandler);
                    dataTransferThread.start();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            } catch (IOException ioe) {
                Log.v(TAG, "Server socket was closed - likely due to cancel method on server thread");
                break;
            }
        }
    }

    public void cancel() {
        try {
            Log.v(TAG, "Trying to close the server socket");
            mServerSocket.close();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
}
