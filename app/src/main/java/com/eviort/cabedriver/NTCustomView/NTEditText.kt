package com.eviort.cabedriver.NTCustomView

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import com.eviort.cabedriver.R

class NTEditText : AppCompatEditText {
    constructor(context: Context) : super(context) {
        applyCustomFont(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        applyCustomFont(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        applyCustomFont(context)
    }

    private fun applyCustomFont(context: Context) {
        /*  Typeface customFont = FontCache.getTypeface("fonts/OpenSans-Regular.ttf", context);
        setTypeface(customFont);*/
        val customFont = ResourcesCompat.getFont(getContext(), R.font.opensregular)
        typeface = customFont
    }
}