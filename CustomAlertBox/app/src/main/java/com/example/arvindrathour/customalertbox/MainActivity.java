package com.example.arvindrathour.customalertbox;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View dialogOpen=(View) findViewById(R.id.button_dialog_open);
        dialogOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dfragment dFragment = new Dfragment();
                dFragment.setBrandsDetail("Coca-cola",false,true,true);
                // Show DialogFragment
                dFragment.show(getFragmentManager(), "Dialog Fragment");
            }
        });
    }
}
