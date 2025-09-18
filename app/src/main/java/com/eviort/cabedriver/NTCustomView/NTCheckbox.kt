package com.eviort.cabedriver.NTCustomView

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.widget.CheckBox
import androidx.core.content.res.ResourcesCompat
import com.eviort.cabedriver.R

@SuppressLint("AppCompatCustomView")
class NTCheckbox : CheckBox {
    constructor(context: Context) : super(context) {
        applyCustomFont(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        applyCustomFont(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        applyCustomFont(context)
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        applyCustomFont(context)
    }

    private fun applyCustomFont(context: Context) {
        /*  Typeface customFont = FontCache.getTypeface("fonts/Roboto-Light.ttf", context);
        setTypeface(customFont);*/
        val customFont = ResourcesCompat.getFont(getContext(), R.font.opensregular)
        typeface = customFont
    }
}