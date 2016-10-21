package com.example.arvindkumar.mailimage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b= (Button) findViewById(R.id.button);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Date now = new Date();
                android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);

                try {

                    File wallpaperDirectory = new File("/sdcard/Wallpaper2/");
                    wallpaperDirectory.mkdir();
                    Log.e("tag","directory created");

                    // image naming and path  to include sd card  appending name you choose for file
                    String mPath = "" + now + ".jpg";   // Environment.getExternalStorageDirectory().toString() + wallpaperDirectory

                    // create bitmap screen capture
                    View v1 = getWindow().getDecorView().getRootView();
                    v1.setDrawingCacheEnabled(true);
                    Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
                    v1.setDrawingCacheEnabled(false);

                    File imageFile = new File(wallpaperDirectory,mPath);

//                    view = OnclickView.getRootView();
//
//                    view.setDrawingCacheEnabled(true);
//
//                    bitmap = view.getDrawingCache();
//
//                    ScreenShotHold.setImageBitmap(bitmap);

                    FileOutputStream outputStream = new FileOutputStream(imageFile);
                    int quality = 100;
                    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
                    outputStream.flush();
                    outputStream.close();

                    openScreenshot(imageFile);
                } catch (Throwable e) {
                    // Several error may come out with file handling or OOM
                    Log.e("tag","exce");
                    e.printStackTrace();
                }
            }
        });
    }
    private void openScreenshot(File imageFile) {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

        Uri uri = Uri.fromFile(imageFile);
        emailIntent.setType("application/image");
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Test Subject");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "From My App");
        emailIntent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }
}
