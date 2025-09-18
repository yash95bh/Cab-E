package com.eviort.cabedriver.NTFragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import android.webkit.WebView
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.eviort.cabedriver.NTActivity.MainActivity
import com.eviort.cabedriver.NTApplication
import com.eviort.cabedriver.NTCustomView.NTBoldTextView
import com.eviort.cabedriver.NTCustomView.NTTextView
import com.eviort.cabedriver.NTHelper.LoadingDialog
import com.eviort.cabedriver.NTHelper.SharedHelper.getKey
import com.eviort.cabedriver.NTHelper.SharedHelper.putKey
import com.eviort.cabedriver.NTHelper.URLHelper
import com.eviort.cabedriver.NTUtilites.Utilities
import com.eviort.cabedriver.R
import org.json.JSONObject
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class FragmentAbout : Fragment(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    var mail_customerCare: NTTextView? = null
    var name_customerCare: NTTextView? = null
    var call_customerCare: NTTextView? = null
    var visit_website: NTTextView? = null
    var faq: NTTextView? = null
    var tv_version: NTTextView? = null
    var helpSupport: NTTextView? = null
    var share_applink: NTTextView? = null
    var downloadUserApp: NTTextView? = null
    var terms_condition: NTTextView? = null
    var privacy_policy: NTTextView? = null
    var switch_map_mode: SwitchCompat? = null
    var tv_map_mode: NTTextView? = null
    var backArrow: ImageView? = null
    var phone: String? = ""
    var username = ""
    var email = ""
    var privacy_url = ""
    var terms_url = ""
    var website_url = ""
    var faq_url = ""

    //    ContactAdapter contactAdapter;
    var RESULT_PICK_CONTACT = 2017
    var loadingDialog: LoadingDialog? = null
    var utils = Utilities()
    var rootview: View? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_about, container, false)
        rootview?.setFocusableInTouchMode(true)
        rootview?.requestFocus()
        rootview?.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return@OnKeyListener true
                }
            }
            false
        })
        findViewById(rootview)
        setOnClickListener()
        help
        if (getKey(activity!!, "map_mode") == "night") {
            switch_map_mode!!.isChecked = true
            tv_map_mode!!.text = "Night mode"
        } else {
            tv_map_mode!!.text = "Day mode"
            switch_map_mode!!.isChecked = false
        }
        val packageManager = activity!!.packageManager
        val packageName = activity!!.packageName
        var myVersionName = "" // initialize String
        try {
            myVersionName = packageManager.getPackageInfo(packageName, 0).versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
//        tv_version!!.text = activity!!.resources.getString(R.string.version) + myVersionName
        val calendar = Calendar.getInstance()
        val year = calendar[Calendar.YEAR]
        tv_version!!.text = activity!!.resources.getString(R.string.version) + myVersionName
        return rootview
    }

    private fun setOnClickListener() {
        helpSupport!!.setOnClickListener(this)
        backArrow!!.setOnClickListener(this)
        mail_customerCare!!.setOnClickListener(this)
        call_customerCare!!.setOnClickListener(this)
        visit_website!!.setOnClickListener(this)
        faq!!.setOnClickListener(this)
        terms_condition!!.setOnClickListener(this)
        privacy_policy!!.setOnClickListener(this)
        share_applink!!.setOnClickListener(this)
        downloadUserApp!!.setOnClickListener(this)
        switch_map_mode!!.setOnCheckedChangeListener(this)
    }

    override fun onCheckedChanged(compoundButton: CompoundButton, isChecked: Boolean) {
        val switch_id = compoundButton.id
        if (isChecked) {
            putKey(activity!!, "map_mode", "night")
            tv_map_mode!!.text = "Night mode"
        } else {
            putKey(activity!!, "map_mode", "day")
            tv_map_mode!!.text = "Day mode"
        }
    }

    private fun findViewById(rootview: View?) {
        backArrow = rootview!!.findViewById<View>(R.id.backArrow) as ImageView
        name_customerCare = rootview.findViewById<View>(R.id.name_customerCare) as NTTextView
        mail_customerCare = rootview.findViewById<View>(R.id.mail_customerCare) as NTTextView
        call_customerCare = rootview.findViewById<View>(R.id.call_customerCare) as NTTextView
        visit_website = rootview.findViewById<View>(R.id.visit_website) as NTTextView
        faq = rootview.findViewById<View>(R.id.faq) as NTTextView
        tv_map_mode = rootview.findViewById<View>(R.id.tv_map_mode) as NTTextView
        helpSupport = rootview.findViewById<View>(R.id.helpSupport) as NTTextView
        switch_map_mode = rootview.findViewById<View>(R.id.switch_map_mode) as SwitchCompat
        tv_version = rootview.findViewById<View>(R.id.tv_version) as NTTextView
        share_applink = rootview.findViewById<View>(R.id.share_applink) as NTTextView
        downloadUserApp = rootview.findViewById<View>(R.id.downloadUserApp) as NTTextView
        terms_condition = rootview.findViewById<View>(R.id.terms_condition) as NTTextView
        privacy_policy = rootview.findViewById<View>(R.id.privacy_policy) as NTTextView
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // check whether the result is ok
        // Check for the request code, we might be usign multiple startActivityForReslut
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RESULT_PICK_CONTACT) {
//                contactPicked(data);
            }
        } else {
            Log.e("MainActivity", "Failed to pick contact")
        }
    }

    override fun onClick(view: View) {
        val id = view.id
        when (id) {
            R.id.backArrow -> pop()
            R.id.helpSupport -> {
                val helpSupport = Intent(Intent.ACTION_VIEW, Uri.parse(URLHelper.HELP_URL))
                startActivity(helpSupport)
            }
            R.id.terms_condition -> {
                /*navigateToURL(URLHelper.TERMS_URL);*/
                val builderr = AlertDialog.Builder(activity)
                val inflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val layout = inflater.inflate(R.layout.fragment_webview, null)
                builderr.setCancelable(false)
                builderr.setView(layout)
                builderr.create()
                val alertDialog = builderr.create()
                val img_exit = layout.findViewById<View>(R.id.img_exit) as ImageView
                val tv_header = layout.findViewById<View>(R.id.tv_header) as NTBoldTextView
                tv_header.setText(R.string.f_pop_web_terms)
                val webView = layout.findViewById<View>(R.id.webview) as WebView
                val webSettings = webView.settings
                webSettings.javaScriptEnabled = true
                webView.loadUrl(terms_url)
                img_exit.setOnClickListener { alertDialog.dismiss() }
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                alertDialog.window!!.attributes.windowAnimations = R.style.dialog_animation
                alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                alertDialog.show()
            }
            R.id.privacy_policy -> {
                /*  navigateToURL(URLHelper.POLICY_URL);*/
                val builder1 = AlertDialog.Builder(activity)
                val inflater1 = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val layout1 = inflater1.inflate(R.layout.fragment_webview, null)
                builder1.setCancelable(false)
                builder1.setView(layout1)
                builder1.create()
                val alertDialog1 = builder1.create()
                val img_exit1 = layout1.findViewById<View>(R.id.img_exit) as ImageView
                val tv_header1 = layout1.findViewById<View>(R.id.tv_header) as NTBoldTextView
                tv_header1.setText(R.string.f_pop_web_privacy)
                val webView1 = layout1.findViewById<View>(R.id.webview) as WebView
                val webSettings1 = webView1.settings
                webSettings1.javaScriptEnabled = true
                webView1.loadUrl(privacy_url)
                img_exit1.setOnClickListener { alertDialog1.dismiss() }
                alertDialog1.requestWindowFeature(Window.FEATURE_NO_TITLE)
                alertDialog1.window!!.attributes.windowAnimations = R.style.dialog_animation
                alertDialog1.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                alertDialog1.show()
            }
            R.id.share_applink -> navigateToShareScreen(URLHelper.APP_URL)
            R.id.downloadUserApp -> {
                val downloadIntent = Intent(Intent.ACTION_VIEW, Uri.parse(URLHelper.CUSTOMER_APP_URL))
                startActivity(downloadIntent)
            }
            R.id.visit_website -> {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(URLHelper.HELP_URL))
                startActivity(browserIntent)
            }
            R.id.faq -> {
                val builder2 = AlertDialog.Builder(activity)
                val inflater2 = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val layout2 = inflater2.inflate(R.layout.fragment_webview, null)
                builder2.setCancelable(false)
                builder2.setView(layout2)
                builder2.create()
                val alertDialog2 = builder2.create()
                val img_exit2 = layout2.findViewById<View>(R.id.img_exit) as ImageView
                val tv_header2 = layout2.findViewById<View>(R.id.tv_header) as NTBoldTextView
                // tv_header2.setText(R.string.f_pop_web_faq);
                val webView2 = layout2.findViewById<View>(R.id.webview) as WebView
                val webSettings2 = webView2.settings
                webSettings2.javaScriptEnabled = true
                webView2.loadUrl(faq_url)
                img_exit2.setOnClickListener { alertDialog2.dismiss() }
                alertDialog2.requestWindowFeature(Window.FEATURE_NO_TITLE)
                alertDialog2.window!!.attributes.windowAnimations = R.style.dialog_animation
                alertDialog2.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                alertDialog2.show()
            }
            R.id.mail_customerCare -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/html"
                intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name) + "-" + getString(R.string.help))
                intent.putExtra(Intent.EXTRA_TEXT, "Hello team")
                startActivity(Intent.createChooser(intent, "Send Email"))
            }
            R.id.call_customerCare -> if (phone != null && !phone.equals("null", ignoreCase = true) && !phone.equals("", ignoreCase = true) && phone!!.length > 0) {
                val intentCall = Intent(Intent.ACTION_DIAL)
                intentCall.data = Uri.parse("tel:$phone")
                startActivity(intentCall)
            } else {
                val builder = AlertDialog.Builder(activity, R.style.NTDialogTheme)
                builder.setTitle(getString(R.string.app_name))
                        .setIcon(R.mipmap.ic_launcher)
                        .setMessage(getString(R.string.sorry_for_inconvinent))
                        .setCancelable(false)
                        .setPositiveButton("ok"
                        ) { dialog, id -> dialog.dismiss() }
                val alert1 = builder.create()
                alert1.show()
            }
        }
    }

    fun navigateToShareScreen(shareUrl: String) {
        try {
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareUrl + " -via " + getString(R.string.app_name))
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(activity, "Share applications not found!", Toast.LENGTH_SHORT).show()
        }
    }

    private val isFinishing: Boolean
        private get() = false

    fun navigateToURL(shareUrl: String?) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(URLHelper.HELP_URL))
        startActivity(browserIntent)
    }

    //                            refreshAccessToken();
    val help: Unit
        get() {
            val loadingDialog = LoadingDialog(activity!!)
            loadingDialog.showDialog()
            val `object` = JSONObject()
            Utilities.PrintAPI_URL(URLHelper.HELP, "GET")
            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.GET, URLHelper.HELP, `object`, Response.Listener { response ->
                Utilities.printAPI_Response(response.toString())
                loadingDialog.hideDialog()
                username = response.optString("contact_name")
                phone = response.optString("contact_number")
                email = response.optString("contact_email")
                terms_url = response.optString("terms")
                privacy_url = response.optString("privacy")
                faq_url = response.optString("faq")
                website_url = response.optString("website")
                call_customerCare!!.text = phone
                name_customerCare!!.text = username
                mail_customerCare!!.text = email
            }, Response.ErrorListener { error ->
                loadingDialog.hideDialog()
                try {
                    var json: String? = null
                    var Message: String
                    val response = error.networkResponse
                    if (response != null && response.data != null) {
                        try {
                            val errorObj = JSONObject(String(response.data))
                            println("PassengerAPI response error : " + error + " " + error.networkResponse + " " + response.statusCode + " " + errorObj.optString("error"))
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    displayMessage(errorObj.optString("message"))
                                } catch (e: Exception) {
                                    displayMessage(getString(R.string.something_went_wrong))
                                    e.printStackTrace()
                                }
                            } else if (response.statusCode == 401) {
//                            refreshAccessToken();
                            } else if (response.statusCode == 422) {
                                json = NTApplication.trimMessage(String(response.data))
                                if (json !== "" && json != null) {
                                    displayMessage(json)
                                } else {
                                    displayMessage(getString(R.string.please_try_again))
                                }
                            } else if (response.statusCode == 503) {
                                displayMessage(getString(R.string.server_down))
                            } else {
                                displayMessage(getString(R.string.please_try_again))
                            }
                        } catch (e: Exception) {
                            displayMessage(getString(R.string.something_went_wrong))
                            e.printStackTrace()
                        }
                    } else {
                        if (error is NoConnectionError) {
                            displayMessage(getString(R.string.oops_connect_your_internet))
                        } else if (error is NetworkError) {
                            displayMessage(getString(R.string.oops_connect_your_internet))
                        } else if (error is TimeoutError) {
                            help
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(activity, resources.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
                }
            }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["X-localization"] = getKey(activity!!, "lang")!!
                    headers["X-Requested-With"] = "XMLHttpRequest"
                    headers["Authorization"] = "Bearer " + getKey(activity!!, "access_token")
                    Log.e("", "Access_Token" + getKey(activity!!, "access_token"))
                    return headers
                }
            }
            NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
        }

    fun displayMessage(msg: String?) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    companion object {
        private const val REQUEST_LOCATION = 1450
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