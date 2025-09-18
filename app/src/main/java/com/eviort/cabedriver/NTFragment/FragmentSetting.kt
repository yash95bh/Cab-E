package com.eviort.cabedriver.NTFragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.Switch
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.eviort.cabedriver.NTActivity.ActivityBegin
import com.eviort.cabedriver.NTApplication
import com.eviort.cabedriver.NTCustomView.NTButton
import com.eviort.cabedriver.NTCustomView.NTTextView
import com.eviort.cabedriver.NTHelper.LoadingDialog
import com.eviort.cabedriver.NTHelper.SharedHelper.getKey
import com.eviort.cabedriver.NTHelper.SharedHelper.putKey
import com.eviort.cabedriver.NTHelper.URLHelper
import com.eviort.cabedriver.NTUtilites.Utilities
import com.eviort.cabedriver.R
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class FragmentSetting : Fragment(), View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    var btn_logout: NTButton? = null
    var switch_map_mode: Switch? = null
    var swich_satellite_mode: Switch? = null
    var backArrow: ImageView? = null
    var rooview: View? = null
    var loadingDialog: LoadingDialog? = null
    var utils = Utilities()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rooview = inflater.inflate(R.layout.fragment_setting, container, false)
        val activity: Activity? = activity
        val context: Context? = getActivity()
        loadingDialog = LoadingDialog(getActivity()!!)
        findviewById()
        setOnClickListener()
        FragmentHome.isRunning = false
        if (getKey(context!!, "map_mode") == "night") {
            switch_map_mode!!.isChecked = true
        } else {
            switch_map_mode!!.isChecked = false
        }
        if (getKey(context, "map_type") == "satellite") {
            swich_satellite_mode!!.isChecked = true
        } else {
            swich_satellite_mode!!.isChecked = false
        }
        return rooview
    }

    private fun setOnClickListener() {
        switch_map_mode!!.setOnCheckedChangeListener(this)
        swich_satellite_mode!!.setOnCheckedChangeListener(this)
        backArrow!!.setOnClickListener(this)
        btn_logout!!.setOnClickListener(this)
    }

    private fun findviewById() {
        backArrow = rooview!!.findViewById<View>(R.id.frg_setting_backArrow) as ImageView
        btn_logout = rooview!!.findViewById<View>(R.id.btn_logout) as NTButton
        switch_map_mode = rooview!!.findViewById<View>(R.id.switch_map_mode) as Switch
        swich_satellite_mode = rooview!!.findViewById<View>(R.id.switch_satellite_mode) as Switch
    }

    override fun onClick(view: View) {
        val id = view.id
        when (id) {
            R.id.frg_setting_backArrow -> pop()
            R.id.btn_logout -> showLogoutDialog()
        }
    }

    override fun onCheckedChanged(compoundButton: CompoundButton, isChecked: Boolean) {
        when (compoundButton.id) {
            R.id.switch_map_mode -> if (isChecked) {
                putKey(activity!!, "map_mode", "night")
            } else {
                putKey(activity!!, "map_mode", "day")
            }
            R.id.switch_satellite_mode -> if (isChecked) {
                putKey(activity!!, "map_type", "satellite")
            } else {
                putKey(activity!!, "map_type", "normal")
            }
        }
    }

    fun showLogoutDialog() {
        val builder3 = AlertDialog.Builder(activity)
        val inflater3 = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout3 = inflater3.inflate(R.layout.layout_alert, null)
        builder3.setCancelable(false)
        builder3.setView(layout3)
        builder3.create()
        val alertDialog3 = builder3.create()
        val tv_alert_title = layout3.findViewById<View>(R.id.tv_alert_title) as NTTextView
        val tv_alert_desc = layout3.findViewById<View>(R.id.tv_alert_desc) as NTTextView
        val tv_alert_okBtn = layout3.findViewById<View>(R.id.tv_alert_okBtn) as NTTextView
        val tv_alert_noBtn = layout3.findViewById<View>(R.id.tv_alert_noBtn) as NTTextView
        tv_alert_title.text = resources.getString(R.string.alert_logout_title)
        tv_alert_desc.text = resources.getString(R.string.logout_alert)
        tv_alert_okBtn.text = resources.getString(R.string.ok)
        tv_alert_noBtn.text = resources.getString(R.string.cancel)
        tv_alert_okBtn.setOnClickListener {
            logout()
            alertDialog3.dismiss()
        }
        tv_alert_noBtn.setOnClickListener { alertDialog3.dismiss() }
        alertDialog3.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog3.window!!.attributes.windowAnimations = R.style.dialog_animation
        alertDialog3.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog3.show()
    }

    private fun logout() {
        if (loadingDialog != null) loadingDialog!!.showDialog()
        Utilities.PrintAPI_URL(URLHelper.LOGOUT, "POST")
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URLHelper.LOGOUT, null, Response.Listener {
            if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
            val beginIntent = Intent(activity, ActivityBegin::class.java)
            beginIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(beginIntent)
            putKey(activity!!, "current_status", "")
            putKey(activity!!, "loggedIn", getString(R.string.False))
            putKey(activity!!, "email", "")
            activity!!.finish()
        }, Response.ErrorListener { error ->
            try {
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                if (error is TimeoutError) {
                    Toast.makeText(activity, resources.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show()
                } else if (error is NoConnectionError) {
                    Toast.makeText(activity, resources.getString(R.string.error_no_network), Toast.LENGTH_LONG).show()
                } else if (error is AuthFailureError) {
                    Toast.makeText(activity, resources.getString(R.string.error_auth_failure), Toast.LENGTH_LONG).show()
                } else if (error is ServerError) {
                    Toast.makeText(activity, resources.getString(R.string.error_server_connection), Toast.LENGTH_LONG).show()
                } else if (error is NetworkError) {
                    Toast.makeText(activity, resources.getString(R.string.error_network), Toast.LENGTH_LONG).show()
                } else if (error is ParseError) {
                    //Toast.makeText(activity, resources.getString(R.string.error_parse), Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(activity, resources.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
            }
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "" + getKey(activity!!, "token_type") + " " + getKey(activity!!, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    private fun isFinishing(): Boolean {
        return false
    }

    fun pop() {
        val fm = getActivity()!!.supportFragmentManager
        val count = fm.backStackEntryCount
        for (i in 0..count) {
            fm.popBackStackImmediate()
        }
    }
}