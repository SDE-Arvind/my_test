package com.example.arvindkumar.bluetoothconnection.bgthreads;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.arvindkumar.bluetoothconnection.ProgressData;
import com.example.arvindkumar.bluetoothconnection.constants.Constants;
import com.example.arvindkumar.bluetoothconnection.constants.MessageType;
import com.example.arvindkumar.bluetoothconnection.utilitiy.Utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 * data downloading , check MSB,LSB and check data is accurate or corrupted
 * send signal to main thread on server side
 */
class DataTransferThread extends Thread {
    private final String TAG = "DataTransferThread";
    private final BluetoothSocket mSocket;
    private Handler mServerHandler;

    /**
     * @param iSocket  connection socket
     * @param iHandler  server handler
     */
    public DataTransferThread(BluetoothSocket iSocket, Handler iHandler) {
        this.mSocket = iSocket;
        this.mServerHandler = iHandler;
    }

    public void run() {
        try {
            InputStream inputStream = mSocket.getInputStream();
            boolean waitingForHeader = true;
            ByteArrayOutputStream dataOutputStream = new ByteArrayOutputStream();
            byte[] headerBytes = new byte[22];
            byte[] digest = new byte[16];
            int headerIndex = 0;
            ProgressData progressData = new ProgressData();

            while (true) {
                if (waitingForHeader) {
                    byte[] header = new byte[1];
                    inputStream.read(header, 0, 1);
                    Log.v(TAG, "Received Header Byte: " + header[0]);
                    headerBytes[headerIndex++] = header[0];

                    if (headerIndex == 22) {
                        if ((headerBytes[0] == Constants.HEADER_MSB) && (headerBytes[1] == Constants.HEADER_LSB)) {
                            Log.v(TAG, "Header   Received.  Now obtaining length");
                            //get size of file which is receiving
                            byte[] dataSizeBuffer = Arrays.copyOfRange(headerBytes, 2, 6);
                            progressData.totalSize = Utils.byteArrayToInt(dataSizeBuffer);
                            progressData.remainingSize = progressData.totalSize;

                            Log.v(TAG, "Data size: " + progressData.totalSize);
                            // get digest for data security check
                            digest = Arrays.copyOfRange(headerBytes, 6, 22);
                            waitingForHeader = false;
                            // send data progress on main activity (server side)
                            sendProgress(progressData);
                        } else {
                            Log.e(TAG, "Did not receive correct header.  Closing mSocket");
                            mSocket.close();
                            mServerHandler.sendEmptyMessage(MessageType.INVALID_HEADER);
                            break;
                        }
                    }
                } else {
                    // Read the data from the stream in chunks
                    byte[] buffer = new byte[Constants.CHUNK_SIZE];
                    Log.v(TAG, "Waiting for data.  Expecting " + progressData.remainingSize + " more bytes.");
                    int bytesRead = inputStream.read(buffer);
                    Log.v(TAG, "Read " + bytesRead + " bytes into buffer");
                    //copy chunk of data in ByteArrayOutPutStream
                    dataOutputStream.write(buffer, 0, bytesRead);
                    progressData.remainingSize -= bytesRead;
                    sendProgress(progressData);
                    if (progressData.remainingSize <= 0) {
                        Log.v(TAG, "Expected data has been received.");
                        break;
                    }
                }
            }

             // convert ByteArrayOutputStream to byte array
            final byte[] data = dataOutputStream.toByteArray();
             // check the integrity of the data
            if (Utils.digestMatch(data, digest)) {
                Log.v(TAG, "Digest matches OK.");
                Message message = new Message();
                message.obj = data;
                message.what = MessageType.DATA_RECEIVED;
                mServerHandler.sendMessage(message);

                // Send the digest back to the client as a confirmation
                Log.v(TAG, "Sending back digest for confirmation");
                OutputStream outputStream = mSocket.getOutputStream();
                outputStream.write(digest);
            } else {
                Log.e(TAG, "Digest did not match.  Corrupt transfer?");
                mServerHandler.sendEmptyMessage(MessageType.DIGEST_DID_NOT_MATCH);
            }

            Log.v(TAG, "Closing server mSocket");
            mSocket.close();

        } catch (Exception ex) {
            Log.d(TAG, ex.toString());
        }
    }

    /**
     *  send progress to main activity
     * @param iProgressData  total size and remaining size of data
     */
    private void sendProgress(ProgressData iProgressData) {
        Message message = new Message();
        message.obj = iProgressData;
        message.what = MessageType.DATA_PROGRESS_UPDATE;
        mServerHandler.sendMessage(message);
    }
}
