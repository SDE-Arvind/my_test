package com.example.arvindkumar.bluetoothconnection.bgthreads;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;

import com.example.arvindkumar.bluetoothconnection.constants.Constants;

import java.io.IOException;
import java.util.UUID;

/**
 *  create server socket on device and wait for client request
 *
 */
public class ServerThread extends Thread {
    private final String TAG = "ServerThread";
    private final BluetoothServerSocket mServerSocket;
    private Handler mServerHandler;

    /**
     * @param iAdapter    bluetooth adapter
     * @param iHandler    server handler reference from main thread
     */
    public ServerThread(BluetoothAdapter iAdapter, Handler iHandler) {
        this.mServerHandler = iHandler;
        BluetoothServerSocket tempSocket = null;
        try {
            tempSocket = iAdapter.listenUsingInsecureRfcommWithServiceRecord(Constants.NAME, UUID.fromString(Constants.UUID_STRING));
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
                // blocker    ....waiting for client request
                socket = mServerSocket.accept();

                try {
                    Log.v(TAG, "Got connection from client.  Spawning new data transfer thread.");
                    DataTransferThread dataTransferThread = new DataTransferThread(socket, mServerHandler);
                    dataTransferThread.start();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
            } catch (IOException ioe) {
                Log.v(TAG, "Server socket was closed - likely due to cancel method on server thread");
                cancel();
                break;
            }
        }
    }

    /**
     *  close server socket
     */
    public void cancel() {
        try {
            Log.v(TAG, "Trying to close the server socket");
            mServerSocket.close();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
}
