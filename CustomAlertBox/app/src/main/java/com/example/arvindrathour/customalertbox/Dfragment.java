package com.example.arvindrathour.customalertbox;


import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class Dfragment extends DialogFragment{

    String mBrandName;
    boolean download;
    boolean activate;
    boolean contact;

    public Dfragment() {
        // Required empty public constructor
    }

    public void setBrandsDetail(String brandname,boolean optionOne,boolean optionTwo,boolean optionThree) {
        // Required empty public constructor
        mBrandName=brandname;
        download =optionOne;
        activate =optionTwo;
        contact =optionThree;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
                setStyle(android.support.v4.app.DialogFragment.STYLE_NORMAL, R.style.PlaceOrderPopUpTheme);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView=inflater.inflate(R.layout.fragment_dfragment, container, false);

         getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
         setCancelable(false);


        TextView downloadTextView=(TextView)  rootView.findViewById(R.id.download);
        TextView activateTextView=(TextView)  rootView.findViewById(R.id.activate);
        TextView contactTextView=(TextView)  rootView.findViewById(R.id.conmtact);

        if(!download)
            downloadTextView.setVisibility(View.GONE);

        if(!activate)
            activateTextView.setVisibility(View.GONE);

        if(!contact)
            contactTextView.setVisibility(View.GONE);


        downloadTextView.setText(String.format(getString(R.string.download_the_s_app), mBrandName));
        activateTextView.setText(String.format(getString(R.string.activate_your_s_merchant_details), mBrandName));
        contactTextView.setText(String.format(getString(R.string.contact_s_to_sign_up_as_a_merchant), mBrandName));



        ImageButton cancel=(ImageButton) rootView.findViewById(R.id.image_button_cancel);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().cancel();
            }
        });
        return rootView;
    }

}
