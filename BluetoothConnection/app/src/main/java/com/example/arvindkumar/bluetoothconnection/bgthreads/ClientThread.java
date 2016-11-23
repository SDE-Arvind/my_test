package com.example.arvindkumar.bluetoothconnection.bgthreads;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.example.arvindkumar.bluetoothconnection.constants.Constants;
import com.example.arvindkumar.bluetoothconnection.constants.MessageType;
import com.example.arvindkumar.bluetoothconnection.utilitiy.Utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * This thread run on client side
 * send socket connection request to server
 * send header,trailer and get response from server
 */
public class ClientThread extends Thread {
    private final String TAG = "ClientThread";
    private final BluetoothSocket mSocket;
    private final Handler mClientHandler;
    public Handler mIncomingHandler;

    /**
     * @param iDevice  bluetooth device  for socket connection
     * @param iHandler client handler reference from main thread
     */
    public ClientThread(BluetoothDevice iDevice, Handler iHandler) {
        BluetoothSocket tempSocket = null;
        this.mClientHandler = iHandler;
        try {
            tempSocket = iDevice.createRfcommSocketToServiceRecord(UUID.fromString(Constants.UUID_STRING));
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        this.mSocket = tempSocket;
    }

    public void run() {
        try {
            Log.v(TAG, "Opening client mSocket");
            mSocket.connect();
            Log.v(TAG, "Connection established");

        } catch (IOException ioe) {
            mClientHandler.sendEmptyMessage(MessageType.COULD_NOT_CONNECT);
            Log.d(TAG, ioe.toString());
            try {
                mSocket.close();
            } catch (IOException ce) {
                Log.e(TAG, "Socket close exception: " + ce.toString());
            }

        }

        Looper.prepare();
        mIncomingHandler = new Handler() {
            // handle message which is send by main thread
            @Override
            public void handleMessage(Message iMessage) {
                if (iMessage.obj != null) {
                    Log.v(TAG, "Handle data sending");
                    byte[] payload = (byte[]) iMessage.obj;
                    Bundle b = iMessage.getData();
                    String filename = b.getString("fileName");
                    try {
                        mClientHandler.sendEmptyMessage(MessageType.SENDING_DATA);
                        OutputStream outputStream = mSocket.getOutputStream();
                        // write size of header
                        outputStream.write(Utils.intToByteArray((22 + filename.length())));

                        // Send the header control first
                        outputStream.write(Constants.HEADER_MSB);              /* 1 byte */
                        outputStream.write(Constants.HEADER_LSB);              /* 1 byte */

                        // write size of file
                        outputStream.write(Utils.intToByteArray(payload.length)); /* 4 byte */
                        // write size of file name
//                        outputStream.write(Utils.intToByteArray(filename.length()));  /* 4 byte */

                        // write digest using MD5 algorithm
                        byte[] digest = Utils.getDigest(payload);
                        outputStream.write(digest);                              /* 16 byte */
                        //writing file name
                        outputStream.write(filename.getBytes());
                        // now write the data
                        outputStream.write(payload);
                        outputStream.flush();
                        Log.v(TAG, "Data sent.  Waiting for return digest as confirmation");

                  /*      data send ,waiting for server response                     */
                        InputStream inputStream = mSocket.getInputStream();
                        byte[] incomingDigest = new byte[16];
                        int incomingIndex = 0;
                        try {
                            //  get digest from server and match with original
                            while (true) {
                                byte[] header = new byte[1];
                                // read 1 byte from inputStream to header
                                inputStream.read(header, 0, 1);
                                incomingDigest[incomingIndex++] = header[0];
                                if (incomingIndex == 16) {
                                    if (Utils.digestMatch(payload, incomingDigest)) {
                                        Log.v(TAG, "Digest matched OK.  Data was received OK.");
                                        // send message "data send successfully" from background thread to main thread
                                        ClientThread.this.mClientHandler.sendEmptyMessage(MessageType.DATA_SENT_OK);
                                    } else {
                                        Log.e(TAG, "Digest did not match.  Might want to resend.");
                                        // send response "data sending fail" from background thread to main thread
                                        ClientThread.this.mClientHandler.sendEmptyMessage(MessageType.DIGEST_DID_NOT_MATCH);
                                    }
                                    break;
                                }
                            }
                        } catch (Exception ex) {
                            Log.e(TAG, ex.toString());
                        }
                        Log.v(TAG, "Closing the client mSocket.");
                        mSocket.close();
                    } catch (Exception e) {
                        Log.e(TAG, e.toString());
                    }
                }
            }
        };
        mClientHandler.sendEmptyMessage(MessageType.READY_FOR_DATA);
        Looper.loop();
    }

    /**
     * close socket connection of client - server
     */
    public void cancel() {
        try {
            if (mSocket.isConnected()) {
                mSocket.close();
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }
}