package rathour.arvind.com.okhttp.utils;

/**
 * Created by Arvind Kumar on 06-Dec-16.
 */

public class ApiError {
    public String getMessage() {
        return mMessage;
    }

    public ApiError setMessage(String mMessage) {
        this.mMessage = mMessage;
        return this;
    }

    public int getCode() {
        return mCode;
    }

    public ApiError setCode(int mCode) {
        this.mCode = mCode;
        return this;
    }

    String mMessage;
    int mCode;

}
