package com.example.arvind.mytrip;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

import static android.text.TextUtils.isEmpty;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity implements OnClickListener {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
//    private static UserLoginTask mAuthTask = null;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    // UI references.
    private AutoCompleteTextView mPhoneNumberView;
    private AutoCompleteTextView otp;
    private Button mSendOtp;
    private Button mReSendOtp;
    private Button mVerifyOtp;
    ProgressDialog mProgressDialog;

    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private FirebaseAuth mAuth;


    AlertDialog.Builder builder1 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mPhoneNumberView = (AutoCompleteTextView) findViewById(R.id.phone);
        otp = (AutoCompleteTextView) findViewById(R.id.otp);


        builder1 = new AlertDialog.Builder(this);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Please wait ");
//        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        mSendOtp = (Button) findViewById(R.id.send_otp_button);
        mReSendOtp = (Button) findViewById(R.id.resend_otp_button);
        mVerifyOtp = (Button) findViewById(R.id.verify_otp_button);

        mSendOtp.setOnClickListener(this);
        mReSendOtp.setOnClickListener(this);
        mVerifyOtp.setOnClickListener(this);

        mAuth = FirebaseAuth.getInstance();
        // Initialize phone auth callbacks
        // [START phone_auth_callbacks]
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
//                mProgressDialog.cancel();
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verificaiton without
                //     user action.
                Log.d("TAG", "onVerificationCompleted:" + credential);
                builder1.setMessage("Otp sent to your mobile");
                AlertDialog alert11 = builder1.create();
                alert11.show();
                mProgressDialog.cancel();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("TAG", "onVerificationFailed", e);

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    mPhoneNumberView.setError("Invalid phone number.");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("TAG", "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;
            }
        };
        // [END phone_auth_callbacks]


    }


    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_otp_button:
                if (isEmpty(mPhoneNumberView.getText().toString())) {
                    Toast.makeText(this, "Enter Phone Number first", Toast.LENGTH_LONG).show();
                    return;
                }
               mProgressDialog.show();
                startPhoneNumberVerification(mPhoneNumberView.getText().toString());
                break;

            case R.id.resend_otp_button:
                if (isEmpty(mPhoneNumberView.getText().toString())) {
                    Toast.makeText(this, "Enter Phone Number first", Toast.LENGTH_LONG).show();
                    return;
                }
                resendVerificationCode(mPhoneNumberView.getText().toString(), mResendToken);

                break;

            case R.id.verify_otp_button:
                verifyPhoneNumberWithCode(mVerificationId, otp.getText().toString());
                break;
        }
    }


//    /**
//     * Represents an asynchronous login/registration task used to authenticate
//     * the user.
//     */
//    public static class UserLoginTask extends AsyncTask<Void, Void, Boolean> {
//
//        UserLoginTask(String email, String password) {
//
//        }
//
//        @Override
//        protected Boolean doInBackground(Void... params) {
//            // TODO: attempt authentication against a network service.
//
//            try {
//                // Simulate network access.
//                Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                return false;
//            }
//
//            // TODO: register the new account here.
//            return true;
//        }
//
//        @Override
//        protected void onPostExecute(final Boolean success) {
//            mAuthTask = null;
//            if (success) {
//
//            } else {
//
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            mAuthTask = null;
//        }
//    }


    private void startPhoneNumberVerification(String phoneNumber) {

        if (phoneNumber == null) {
            Log.e("tag", "phone number is null");
            Toast.makeText(this, "something is missing", Toast.LENGTH_LONG).show();
            return;
        }
//        mProgressDialog.show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks

    }


    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        if (phoneNumber == null || token == null) {
            Log.e("tag", "phone " + phoneNumber + " token " + token);

            Toast.makeText(this, "something is missing", Toast.LENGTH_LONG).show();
            return;
        }

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }


    private void verifyPhoneNumberWithCode(String verificationId, String code) {

        if (verificationId == null || code == null) {
            Log.e("tag", "verificationId " + verificationId + " code " + code);
            Toast.makeText(this, "something is missing", Toast.LENGTH_LONG).show();
            return;
        }
        // [START verify_with_code]
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        // [END verify_with_code]
        signInWithPhoneAuthCredential(credential);
    }


    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success" + mAuth.getCurrentUser().getDisplayName());

                            FirebaseUser user = task.getResult().getUser();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                otp.setError("Invalid code.");
                            }
                        }
                    }
                });
    }
}

