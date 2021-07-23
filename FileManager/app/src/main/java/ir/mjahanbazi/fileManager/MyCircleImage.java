/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ir.mjahanbazi.fileManager;

import android.content.Context;
import android.util.AttributeSet;

public class MyCircleImage extends de.hdodenhof.circleimageview.CircleImageView {

    public MyCircleImage(Context context) {
        super(context);
        init();
    }

    public MyCircleImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyCircleImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }


    private void init() {
//        super.setScaleType(ImageView.ScaleType.FIT_CENTER);
    }

}
