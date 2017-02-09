package rathour.arvind.com.encryption_decryption;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;


public class MainActivity extends AppCompatActivity {

    Button mButtonOk;
    TextView mTextViewPlan;
    TextView mTextViewEncry;
    TextView mTextViewDecry;
    EditText mEditTextInput;
    EditText mEditTextKey;


    static final String ALGO = "AES";// RC4 to var size of key

    // string length must be 16 char(128 bit)
    static String SecretKey = "asdfghjklqwertyu";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mButtonOk = (Button) findViewById(R.id.button_ok);
        mTextViewPlan = (TextView) findViewById(R.id.textView_plan);
        mTextViewEncry = (TextView) findViewById(R.id.textView_encrypted);
        mTextViewDecry = (TextView) findViewById(R.id.textView_decrypted);
        mEditTextInput = (EditText) findViewById(R.id.editText_input);
        mEditTextKey = (EditText) findViewById(R.id.editText_secret_key);


        mButtonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String text = mEditTextInput.getText().toString();
                if (mEditTextKey.length()==16) {
                    Log.e("","key not blank");
                    SecretKey = mEditTextKey.getText().toString();
                }
                mTextViewPlan.setText("Plan text : " + text);
                try {

                    mTextViewEncry.setText("encrypted : " + encrypt(text));

                    String dec = decrypt(encrypt(text));

                    mTextViewDecry.setText("decrypted :" + dec);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    String encrypt(String iPlanText) throws Exception {

        Key key = generateKey();

        // Encode the original data with AES
        byte[] encodedBytes = null;
        try {
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.ENCRYPT_MODE, key);
            encodedBytes = c.doFinal(iPlanText.getBytes());
        } catch (Exception e) {
            Log.e("TAG", "AES encryption error" + e);
        }
        String en = new BASE64Encoder().encode(encodedBytes);
        return en;
    }


    String decrypt(String iPlanText) throws Exception {
        Key key = generateKey();

        byte[] decValues = null;
        try {
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.DECRYPT_MODE, key);

            byte[] decodedBytes = new BASE64Decoder().decodeBuffer(iPlanText);
            decValues = c.doFinal(decodedBytes);
        } catch (Exception e) {
            Log.e("TAG", "AES decryption error" + e);
        }

//        mTextViewDecry.setText(new String(decodedBytes));

        return new String(decValues);
    }

    private Key generateKey() {

        Key key = new SecretKeySpec(SecretKey.getBytes(), ALGO);
        return key;

    }


}
