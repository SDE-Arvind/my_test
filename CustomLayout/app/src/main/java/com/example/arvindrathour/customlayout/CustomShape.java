package com.example.arvindrathour.customlayout;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by Arvind Rathour on 25-Apr-17.
 */

public class CustomShape extends LinearLayout {
    private  Path mPath;
    public CustomShape(Context iContext) {
        super(iContext);
        init(iContext, null, 0);
    }

    public CustomShape(Context iContext, AttributeSet iAttrs) {
        super(iContext, iAttrs);
        init(iContext, iAttrs, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public CustomShape(Context iContext, AttributeSet iAttrs, int iDefStyle) {
        super(iContext, iAttrs, iDefStyle);
        init(iContext, iAttrs, iDefStyle);
    }

    private void init(Context iContext, AttributeSet iAttrs, int iDefStyle) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public void setPath( Path iPath){
        mPath=iPath;

    }
    @Override
    protected void dispatchDraw(Canvas iCanvas) {
        int count = iCanvas.save();
        final Path path = new Path();

        // set shape of layout
//        path.addOval(new RectF(0, 0, iCanvas.getWidth(), iCanvas.getHeight()), Path.Direction.CW);

        path.set(mPath);
        iCanvas.clipPath(path, Region.Op.REPLACE);
        super.dispatchDraw(iCanvas);
        iCanvas.restoreToCount(count);
    }

}
