package com.eviort.cabedriver.NTUtilites;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.eviort.cabedriver.R;

@SuppressLint("AppCompatCustomView")
public class MyDigitalFontTextView extends TextView {
    public MyDigitalFontTextView(Context context) {
        super(context);
        applyCustomFont(context);
    }

    public MyDigitalFontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        applyCustomFont(context);
    }

    public MyDigitalFontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        applyCustomFont(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyDigitalFontTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        applyCustomFont(context);
    }

    private void applyCustomFont(Context context) {
    /*    Typeface customFont = FontCache.getTypeface("fonts/Digital-7.ttf", context);
        setTypeface(customFont);*/
        Typeface customFont = ResourcesCompat.getFont(getContext(), R.font.digital);
        setTypeface(customFont);
    }
}

