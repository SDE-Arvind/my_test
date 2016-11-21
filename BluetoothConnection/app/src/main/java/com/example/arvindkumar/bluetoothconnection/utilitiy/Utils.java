package com.example.arvindkumar.bluetoothconnection.utilitiy;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.security.MessageDigest;
import java.util.Arrays;

/**
 *      have some utility methods
 */
public class Utils {
    private final static String TAG = "Utils";

    /**
     *  convert integer to byte array
     * @param iNumber  integer number
     * @return   byte array of integer number
     */
    public static byte[] intToByteArray(int iNumber) {
        byte[] outByteArray = new byte[4];
        outByteArray[3] = (byte) (iNumber & 0xFF);
        outByteArray[2] = (byte) ((iNumber >> 8) & 0xFF);
        outByteArray[1] = (byte) ((iNumber >> 16) & 0xFF);
        outByteArray[0] = (byte) ((iNumber >> 24) & 0xFF);
        return outByteArray;
    }

    /**
     * convert byte array to integer
     * @param iByteArray    byte array
     * @return   integer value of byte array
     */
    public static int byteArrayToInt(byte[] iByteArray) {
        return (iByteArray[3] & 0xFF) + ((iByteArray[2] & 0xFF) << 8) + ((iByteArray[1] & 0xFF) << 16) + ((iByteArray[0] & 0xFF) << 24);
    }

    /**
     *  compare two byte array
     *
     * @param iImageData
     * @param iDigestData
     * @return      if both are equal then true otherwise false
     */
    public static boolean digestMatch(byte[] iImageData, byte[] iDigestData) {
        return Arrays.equals(getDigest(iImageData), iDigestData);
    }

    /**
     *
     * @param iImageData
     * @return
     */
    public static byte[] getDigest(byte[] iImageData) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            return messageDigest.digest(iImageData);
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
            throw new UnsupportedOperationException("MD5 algorithm not available on this device.");
        }
    }

    public static void showToast(Context context, String msg)
    {
        Toast.makeText(context,msg,Toast.LENGTH_SHORT).show();
    }
}
