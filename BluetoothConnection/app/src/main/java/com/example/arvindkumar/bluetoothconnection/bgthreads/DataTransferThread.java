package com.example.arvindkumar.bluetoothconnection.bgthreads;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.example.arvindkumar.bluetoothconnection.utilitiy.ProgressData;
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
            byte[] digest = new byte[Constants.DIGEST_SIZE];
            int headerIndex = 0;
            ProgressData progressData = new ProgressData();
            byte prefix[]=new byte[Constants.PREFIX_SIZE];
            inputStream.read(prefix,0,Constants.PREFIX_SIZE);
            int headerSize =Utils.byteArrayToInt(prefix);
            byte[] headerBytes = new byte[headerSize];                         //+22
            Log.d("header size",""+ headerSize);

            while (true) {
                if (waitingForHeader) {
                    byte[] header = new byte[1];
                    inputStream.read(header,0,1);
//                    Log.v(TAG, "Received Header Byte: " + header[0]);
                    headerBytes[headerIndex++] = header[0];
                    if (headerIndex == headerSize) {
                        if ((headerBytes[0] == Constants.HEADER_MSB) && (headerBytes[1] == Constants.HEADER_LSB)) {
                            int readerHead=2;
                            Log.v(TAG, "Header   Received.  Now obtaining length");
                            //get size of file which is receiving
                            byte[] dataSizeBuffer = Arrays.copyOfRange(headerBytes, readerHead,readerHead+Constants.FILE_SIZE);
                            progressData.totalSize = Utils.byteArrayToInt(dataSizeBuffer);
                            progressData.remainingSize = progressData.totalSize;

                            readerHead+=Constants.FILE_SIZE;
                            Log.v(TAG, "Data size: " + progressData.totalSize);
                            // get digest
                            digest = Arrays.copyOfRange(headerBytes, readerHead,readerHead+Constants.DIGEST_SIZE);
                            Log.d( "digest ",Arrays.toString(digest));

                            readerHead+=Constants.DIGEST_SIZE;
                            //get file name
                            byte[] fileName =Arrays.copyOfRange(headerBytes,readerHead, headerSize);
                            Message message = new Message();
                            message.obj = fileName;
                            message.what = MessageType.NAME_RECEIVED;
                            mServerHandler.sendMessage(message);

                            waitingForHeader = false;
                            // send fileName progress on main activity (server side)
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
//                        inputStream.close();
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
//                outputStream.close();
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
