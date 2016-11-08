package com.example.arvindkumar.bluetoothchatapplication;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by Arvind Kumar on 04-Nov-16.
 */
// stabled
public class ChatService {
    private static final String NAME_SECURE = "BluetoothChatSecure";
    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE = UUID
            .fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");

    // Member fields
    private final BluetoothAdapter mBluetoothAdapter;
    private final Handler mHandler;
    private AcceptThread mSecureAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int state;
    private Context mContext;

    // Constants that indicate the current connection state
    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1; // listening connection
    public static final int STATE_CONNECTING = 2; // initiate outgoing
    // connection
    public static final int STATE_CONNECTED = 3; // connected to remote device

    public ChatService(Handler handler, Context context) {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        state = STATE_NONE;
        mContext = context;
        this.mHandler = handler;
    }

    // Set the current state of the chat connection
    private synchronized void setState(int state) {
        this.state = state;
    }

    // get current connection state
    public synchronized int getState() {
        return state;
    }

    // start service
    public synchronized void start() {
        // Cancel any thread
        stopAllThreads();

        setState(STATE_LISTEN);
        // Start the thread to listen on a BluetoothServerSocket
        if (mSecureAcceptThread == null) {
            mSecureAcceptThread = new AcceptThread();
            mSecureAcceptThread.start();
        }
    }

    // initiate connection to remote device
    public synchronized void connect(BluetoothDevice device) {

        stopAllThreads();
        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(STATE_CONNECTING);
    }

    // manage Bluetooth connection
    public synchronized void connected(BluetoothSocket socket,
                                       BluetoothDevice device) {
        stopAllThreads();
        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        setState(STATE_CONNECTED);
//        Toast.makeText(mContext," Device Connected",Toast.LENGTH_SHORT).show();
        Log.e("TAG", "device connected");

    }

    // stop all threads
    public synchronized void stop() {

        stopAllThreads();
        setState(STATE_NONE);
    }

    private void stopAllThreads() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mSecureAcceptThread != null) {
            mSecureAcceptThread.cancel();
            mSecureAcceptThread = null;
        }
    }

    public void write(byte[] out) {
        ConnectedThread r;
        synchronized (this) {
            if (state != STATE_CONNECTED)
                return;
            r = mConnectedThread;
        }
        r.writeOutputStream(out);
    }

    private void connectionFailed() {
        // Start the service over to restart listening mode
        ChatService.this.start();
    }

    private void connectionLost() {
        // Start the service over to restart listening mode
        ChatService.this.start();
    }

    // runs while listening for incoming connections
    private class AcceptThread extends Thread {

        private final BluetoothServerSocket serverSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;
            try {
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(
                        NAME_SECURE, MY_UUID_SECURE);
            } catch (IOException e) {
            }
            serverSocket = tmp;
        }

        public void run() {
            BluetoothSocket socket = null;
            while (state != STATE_CONNECTED) {
                try {
                    socket = serverSocket.accept();
                } catch (IOException e) {
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (ChatService.this) {
                        switch (state) {
                            case STATE_LISTEN:
                            case STATE_CONNECTING:
                                // start the connected thread.
                                connected(socket, socket.getRemoteDevice());
                                break;
                            case STATE_NONE:
                            case STATE_CONNECTED:
                                // Either not ready or already connected. Terminate
                                // new socket.
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                }
                                break;
                        }
                    }
                }
            }
        }

        public void cancel() {
            try {
                serverSocket.close();
            } catch (IOException e) {
            }
        }
    }

    // runs while attempting to make an outgoing connection
    private class ConnectThread extends Thread {
        private final BluetoothSocket socket;
        private final BluetoothDevice device;

        public ConnectThread(BluetoothDevice device) {
            this.device = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID_SECURE);
            } catch (IOException e) {
            }
            socket = tmp;
        }

        public void run() {
            // Always cancel discovery because it will slow down a connection
            mBluetoothAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                socket.connect();
            } catch (IOException e) {
                try {
                    socket.close();
                } catch (IOException e2) {
                }
                connectionFailed();
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (ChatService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(socket, device);
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    // runs during a connection with a remote device
    private class ConnectedThread extends Thread {
        private final BluetoothSocket bluetoothSocket;
        private final InputStream inputStream;
        private final OutputStream outputStream;

        public ConnectedThread(BluetoothSocket socket) {
            this.bluetoothSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
            }

            inputStream = tmpIn;
            outputStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes = 0;
            int i = 0;

            mHandler.obtainMessage(MainActivity.MESSAGE_READ_START, bytes, -1,
                    buffer).sendToTarget();
            // Keep listening to the InputStream
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = inputStream.read(buffer);
                    Log.e("TAG ", "size " + bytes);
                    // Send the obtained bytes to the UI Activity
                    mHandler.obtainMessage(MainActivity.MESSAGE_READ, bytes, -1,
                            buffer).sendToTarget();

                } catch (Exception e) {
                    Log.e("TAG ", "Exception occur");
                    connectionLost();

                    // Start the service over to restart listening mode
//                    ChatService.this.start();
                    break;
                }
                if (bytes < 990) {
                    break;
                }
            }
            Log.e("TAG ", "send completed");
            mHandler.obtainMessage(MainActivity.MESSAGE_READ_COMPLETE, bytes, -1,
                    buffer).sendToTarget();

        }

        public void writeOutputStream(byte[] buffer) {
            try {
                outputStream.write(buffer);
            } catch (IOException e) {
            }
        }

        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
            }
        }
    }
}
