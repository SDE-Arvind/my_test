package com.example.arvindrathour.customlayout;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyShape myShape = new MyShape();
        //pass height width ,radius, no of points
        CustomShape customShape = (CustomShape) findViewById(R.id.view);

        float height, width, redius;
        width = customShape.getLayoutParams().width / 2.0f;
        height = customShape.getLayoutParams().height / 2.0f;
        redius = width > height ? width : height;
        redius = redius / 2.0f;

        myShape.setPolygon(width, height, redius, 5);
//        myShape.setCircle(height,width,redius, Path.Direction.CW);

        customShape.setPath(myShape.getPath());
    }
}
