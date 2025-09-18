package com.eviort.cabedriver.NTFragment

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import com.eviort.cabedriver.NTActivity.MainActivity
import com.eviort.cabedriver.NTCustomView.NTButton
import com.eviort.cabedriver.NTCustomView.NTTextView
import com.eviort.cabedriver.NTHelper.SharedHelper.getKey
import com.eviort.cabedriver.NTHelper.SharedHelper.putKey
import com.eviort.cabedriver.NTUtilites.Utilities
import com.eviort.cabedriver.R

/**
 * A simple [Fragment] subclass.
 */
class FragmentLanguage : Fragment(), View.OnClickListener {
    var rootView: View? = null
    var btn_change_language: NTButton? = null
    var imgBack: ImageView? = null
    var rg_language: RadioGroup? = null
    var rb_lang_arabic: RadioButton? = null
    var rb_lang_english: RadioButton? = null
    var rb_lang_french: RadioButton? = null
    var rb_lang_spanish: RadioButton? = null
    var rb_lang_german: RadioButton? = null
    var lang = ""
    var lang_english: NTTextView? = null
    var lang_spanish: NTTextView? = null
    var lang_french: NTTextView? = null
    var lang_arabic: NTTextView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_language, container, false)
        rootView?.setFocusableInTouchMode(true)
        rootView?.requestFocus()
        rootView?.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return@OnKeyListener true
                }
            }
            false
        })
        btn_change_language = rootView?.findViewById<View>(R.id.btn_change_language) as NTButton
        imgBack = rootView?.findViewById<View>(R.id.backArrow) as ImageView
        FragmentHome.isRunning = false
        rg_language = rootView?.findViewById<View>(R.id.rg_language) as RadioGroup
        rb_lang_arabic = rootView?.findViewById<View>(R.id.rb_lang_arabic) as RadioButton
        rb_lang_english = rootView?.findViewById<View>(R.id.rb_lang_english) as RadioButton
        rb_lang_french = rootView?.findViewById<View>(R.id.rb_lang_french) as RadioButton
        rb_lang_spanish = rootView?.findViewById<View>(R.id.rb_lang_spanish) as RadioButton
        rb_lang_german = rootView?.findViewById<View>(R.id.rb_lang_german) as RadioButton
        lang_arabic = rootView?.findViewById<View>(R.id.txt_arabic) as NTTextView
        lang_english = rootView?.findViewById<View>(R.id.txt_english) as NTTextView
        lang_spanish = rootView?.findViewById<View>(R.id.txt_spanish) as NTTextView
        lang_english!!.setOnClickListener(this)
        lang_arabic!!.setOnClickListener(this)
        lang_spanish!!.setOnClickListener(this)
        if (getKey(activity!!, "lang") == "ar") {
            rb_lang_arabic!!.isChecked = true
            rb_lang_english!!.isChecked = false
            rb_lang_french!!.isChecked = false
            rb_lang_spanish!!.isChecked = false
            rb_lang_german!!.isChecked = false
            lang = "ar"
        } else if (getKey(activity!!, "lang") == "fr") {
            rb_lang_arabic!!.isChecked = false
            rb_lang_english!!.isChecked = false
            rb_lang_french!!.isChecked = true
            rb_lang_spanish!!.isChecked = false
            rb_lang_german!!.isChecked = false
            lang = "fr"
        } else if (getKey(activity!!, "lang") == "es") {
            rb_lang_arabic!!.isChecked = false
            rb_lang_english!!.isChecked = false
            rb_lang_french!!.isChecked = false
            rb_lang_spanish!!.isChecked = true
            rb_lang_german!!.isChecked = false
            lang = "es"
        } else if (getKey(activity!!, "lang") == "gr") {
            rb_lang_arabic!!.isChecked = false
            rb_lang_english!!.isChecked = false
            rb_lang_french!!.isChecked = false
            rb_lang_spanish!!.isChecked = false
            rb_lang_german!!.isChecked = true
            lang = "gr"
        } else {
            lang = ""
            rb_lang_arabic!!.isChecked = false
            rb_lang_english!!.isChecked = true
            rb_lang_french!!.isChecked = false
            rb_lang_spanish!!.isChecked = false
            rb_lang_german!!.isChecked = false
        }
        rg_language!!.setOnCheckedChangeListener { group, checkedId ->
            val rb = group.findViewById<View>(checkedId) as RadioButton
            if (null != rb && checkedId > -1) {

                // checkedId is the RadioButton selected
                lang = when (checkedId) {
                    R.id.rb_lang_arabic -> "ar"
                    R.id.rb_lang_english -> "en"
                    R.id.rb_lang_french -> "fr"
                    R.id.rb_lang_spanish -> "es"
                    R.id.rb_lang_german -> "gr"
                    else -> "en"
                }
            }
        }
        btn_change_language!!.setOnClickListener { //  Utilities.dispalyDialog(getActivity(), getResources().getString(R.string.app_name), getResources().getString(R.string.demo_error));
            if (lang.equals("")) {
                Utilities.dispalyDialog(activity, resources.getString(R.string.app_name), resources.getString(R.string.lang_desc))
            } else if (lang == "en") {
                putKey(activity!!, "lang", lang)
                Utilities.setLocale(activity, getKey(activity!!, "lang"))
                val refresh = Intent(activity, MainActivity::class.java)
                startActivity(refresh)
            }
            else if (lang == "es") {
                putKey(activity!!, "lang", lang)
                Utilities.setLocale(activity, getKey(activity!!, "lang"))
                val refresh = Intent(activity, MainActivity::class.java)
                startActivity(refresh)

            }
            else {
                putKey(requireActivity(), "lang", "en")
                Utilities.dispalyDialog(activity, resources.getString(R.string.app_name), resources.getString(R.string.demo_error))
            }
        }
        imgBack!!.setOnClickListener { //                getFragmentManager().popBackStackImmediate();
            pop()
        }
        return rootView
    }

    override fun onClick(v: View) {
        val id = v.id
        when (id) {
            R.id.txt_english -> {
                lang_english!!.setBackgroundColor(activity!!.resources.getColor(R.color.button_blue))
                lang_arabic!!.setBackgroundColor(activity!!.resources.getColor(R.color.white))
                lang_spanish!!.setBackgroundColor(activity!!.resources.getColor(R.color.white))
                lang = "en"
            }
            R.id.txt_arabic -> {
                lang_arabic!!.setBackgroundColor(activity!!.resources.getColor(R.color.button_blue))
                lang_english!!.setBackgroundColor(activity!!.resources.getColor(R.color.white))
                lang_spanish!!.setBackgroundColor(activity!!.resources.getColor(R.color.white))
                lang_french!!.setBackgroundColor(activity!!.resources.getColor(R.color.white))
                lang = "ar"
            }
            R.id.txt_spanish -> {
                lang_spanish!!.setBackgroundColor(activity!!.resources.getColor(R.color.button_blue))
                lang_arabic!!.setBackgroundColor(activity!!.resources.getColor(R.color.white))
                lang_english!!.setBackgroundColor(activity!!.resources.getColor(R.color.white))
                lang = "es"
            }

        }
    }

    fun pop() {
       /* val fm = getActivity()!!.supportFragmentManager
        val count = fm.backStackEntryCount
        for (i in 0..count) {
            fm.popBackStackImmediate()
        }*/
        val mainIntent = Intent(getActivity()!!, MainActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(mainIntent)
    }
}