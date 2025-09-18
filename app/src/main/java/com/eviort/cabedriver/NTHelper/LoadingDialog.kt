package com.eviort.cabedriver.NTHelper

import android.app.Activity
import android.app.Dialog
import android.view.Window
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.DrawableImageViewTarget
import com.eviort.cabedriver.R

class LoadingDialog     //..we need the context else we can not create the dialog so get context in constructor
(var activity: Activity) {
    var dialog: Dialog? = null
    fun showDialog() {
        dialog = Dialog(activity, R.style.DialogTheme)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.window!!.setBackgroundDrawableResource(R.color.transparent_color)
        //...set cancelable false so that it's never get hidden
        dialog!!.setCancelable(false)
        //...that's the layout i told you will inflate later
        dialog!!.setContentView(R.layout.custom_loading_dialog)

        //...initialize the imageView form infalted layout
        val gifImageView = dialog!!.findViewById<ImageView>(R.id.custom_loading_imageView)

        /*
        it was never easy to load gif into an ImageView before Glide or Others library
        and for doing this we need DrawableImageViewTarget to that ImageView
        */
        val imageViewTarget = DrawableImageViewTarget(gifImageView)

        //...now load that gif which we put inside the drawble folder here with the help of Glide
        Glide.with(activity)
                .load(R.drawable.loading)
                .apply(RequestOptions()
                        .placeholder(R.drawable.loading)
                        .centerCrop())
                .into(imageViewTarget)

        //...finaly show it
        dialog!!.show()
    }

    val isShowing: Boolean
        get() = dialog!!.isShowing

    //..also create a method which will hide the dialog when some work is done
    fun hideDialog() {
        dialog!!.dismiss()
    }
}