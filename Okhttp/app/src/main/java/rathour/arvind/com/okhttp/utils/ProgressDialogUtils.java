package rathour.arvind.com.okhttp.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import rathour.arvind.com.okhttp.R;

/**
 * This is a utility for handling the progress dialog.
 */
public final class ProgressDialogUtils {

    public ProgressDialogUtils() {}

    /**
     * Sets up the progress dialog.
     * Starts the progress dialog.
     */
    public static void startProgressDialog(ProgressDialog iProgressDialog, Context iContext,
                                           DialogInterface.OnClickListener iListener) {
        iProgressDialog.setButton(
                DialogInterface.BUTTON_NEGATIVE,
                iContext.getResources().getString(R.string.cancel),
                iListener
        );
        iProgressDialog.setMessage(iContext.getString(R.string.msg_progress));
        iProgressDialog.setCancelable(false);
        iProgressDialog.show();
    }

    /**
     * Calls dismiss() method of progress dialog.
     *
     * @param iProgressDialog progress dialog on which dismiss() is to be called.
     */
    public static void dismissProgressDialog(ProgressDialog iProgressDialog) {
        iProgressDialog.dismiss();
    }

}
