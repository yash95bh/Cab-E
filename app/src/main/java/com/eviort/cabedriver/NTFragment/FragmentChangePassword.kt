package com.eviort.cabedriver.NTFragment

import android.content.Intent
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.eviort.cabedriver.NTActivity.ActivitySignIn
import com.eviort.cabedriver.NTApplication
import com.eviort.cabedriver.NTCustomView.NTButton
import com.eviort.cabedriver.NTCustomView.NTEditText
import com.eviort.cabedriver.NTHelper.LoadingDialog
import com.eviort.cabedriver.NTHelper.SharedHelper
import com.eviort.cabedriver.NTHelper.URLHelper
import com.eviort.cabedriver.NTUtilites.Utilities
import com.eviort.cabedriver.R
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class FragmentChangePassword : Fragment() {
    var et_current_password: NTEditText? = null
    var et_change_password: NTEditText? = null
    var et_confirm_password: NTEditText? = null
    var btn_save: NTButton? = null
    var show_hide_current_passowrd: ImageView? = null
    var show_hide_change_passowrd: ImageView? = null
    var show_hide_confirm_passowrd: ImageView? = null
    var utilities: Utilities? = null
    var backArrow: ImageView? = null
    var loadingDialog: LoadingDialog? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_change_password, container, false)
        view.isFocusableInTouchMode = true
        view.requestFocus()
        view.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return@OnKeyListener true
                }
            }
            false
        })
        utilities = Utilities()
        loadingDialog = activity?.let { LoadingDialog(it) }
        backArrow = view.findViewById<View>(R.id.backArrow) as ImageView
        et_current_password = view.findViewById<View>(R.id.et_current_password) as NTEditText
        et_change_password = view.findViewById<View>(R.id.et_change_password) as NTEditText
        et_confirm_password = view.findViewById<View>(R.id.et_confirm_password) as NTEditText
        btn_save = view.findViewById<View>(R.id.btn_save) as NTButton
        show_hide_current_passowrd = view.findViewById<View>(R.id.show_hide_current_passowrd) as ImageView
        show_hide_change_passowrd = view.findViewById<View>(R.id.show_hide_change_passowrd) as ImageView
        show_hide_confirm_passowrd = view.findViewById<View>(R.id.show_hide_confirm_passowrd) as ImageView
        btn_save = view.findViewById<View>(R.id.btn_save) as NTButton
        show_hide_current_passowrd!!.setOnClickListener { v -> ShowHidePass(v) }
        show_hide_change_passowrd!!.setOnClickListener { v -> ShowHidePass(v) }
        show_hide_confirm_passowrd!!.setOnClickListener { v -> ShowHidePass(v) }
        btn_save!!.setOnClickListener { //utilities.hideKeypad(getActivity(), v);
            if (et_current_password!!.text.toString() != null && et_current_password!!.text.toString().equals("", ignoreCase = true)) {
                utilities!!.NTToast(activity, resources.getString(R.string.pp_crnt_password), Toast.LENGTH_SHORT)
            } else if (et_change_password!!.text.toString() == null || et_change_password!!.text.toString().equals("", ignoreCase = true)) {
                utilities!!.NTToast(activity, resources.getString(R.string.pp_chng_password), Toast.LENGTH_SHORT)
            } else if (et_change_password!!.text.toString().length <= 5) {
                utilities!!.NTToast(activity, resources.getString(R.string.pp_password_minimum), Toast.LENGTH_SHORT)
            } else if (et_confirm_password!!.text.toString() == null || et_confirm_password!!.text.toString().equals("", ignoreCase = true)) {
                utilities!!.NTToast(activity, resources.getString(R.string.pp_confm_password), Toast.LENGTH_SHORT)
            } else if (et_confirm_password!!.text.toString().length <= 5) {
                utilities!!.NTToast(activity, resources.getString(R.string.pp_cnfrm_password_minimum), Toast.LENGTH_SHORT)
            } else if (!et_change_password!!.text.toString().equals(et_confirm_password!!.text.toString())) {
                utilities!!.NTToast(activity, resources.getString(R.string.pp_password_not_match), Toast.LENGTH_SHORT)
            } else {
                passwordAPI
            }
        }
        backArrow!!.setOnClickListener { activity!!.supportFragmentManager.popBackStack() }
        return view
    }

    //alertDialog.dismiss();
    private val passwordAPI: Unit
        private get() {
            if (loadingDialog != null) loadingDialog!!.showDialog()
            val `object` = JSONObject()
            try {
                `object`.put("password", et_change_password!!.text.toString())
                `object`.put("password_confirmation", et_confirm_password!!.text.toString())
                `object`.put("password_old", et_current_password!!.text.toString())
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            Utilities.PrintAPI_URL(URLHelper.PROFILE_CHANGE_PASSWORD, `object`.toString())
            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URLHelper.PROFILE_CHANGE_PASSWORD, `object`, Response.Listener { response ->
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                try {
                    if (response.getString("success") == "1") {
                        utilities!!.showCustomAlert(activity, Utilities.ALERT_SUCCESS, resources.getString(R.string.app_name), response.getString("message"))
                        startActivity(Intent(activity, ActivitySignIn::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                        activity!!.finish()
                        //alertDialog.dismiss();
                    } else {
                        utilities!!.showCustomAlert(activity, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), response.getString("message"))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                Utilities.PrintAPI_URL(URLHelper.PROFILE_CHANGE_PASSWORD, response.toString())
            }, Response.ErrorListener { error ->
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                try {
                    var json: String? = null
                    var Message: String
                    val response = error.networkResponse
                    if (response != null && response.data != null) {
                        try {
                            val errorObj = JSONObject(String(response.data))
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    utilities!!.showCustomAlert(activity, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), errorObj.optString("message"))
                                } catch (e: Exception) {
                                    utilities!!.showCustomAlert(activity, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), getString(R.string.something_went_wrong))
                                }
                            } else if (response.statusCode == 401) {
                                activity?.let { SharedHelper.putKey(it, "loggedIn", getString(R.string.False)) }
                            } else if (response.statusCode == 422) {
                                json = NTApplication.trimMessage(String(response.data))
                                if (json !== "" && json != null) {
                                    utilities!!.showCustomAlert(activity, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), json)
                                } else {
                                    utilities!!.showCustomAlert(activity, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), getString(R.string.please_try_again))
                                }
                            } else if (response.statusCode == 503) {
                                utilities!!.showCustomAlert(activity, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), getString(R.string.server_down))
                            }
                        } catch (e: Exception) {
                            utilities!!.showCustomAlert(activity, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), getString(R.string.something_went_wrong))
                        }
                    } else {
                        if (error is NoConnectionError) {
                            utilities!!.showCustomAlert(activity, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), getString(R.string.oops_connect_your_internet))
                        } else if (error is NetworkError) {
                            utilities!!.showCustomAlert(activity, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), getString(R.string.oops_connect_your_internet))
                        } else if (error is TimeoutError) {
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(activity, activity!!.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
                }
            }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["X-localization"] = "en"
                    headers["Content-Type"] = "application/json"
                    headers["X-Requested-With"] = "XMLHttpRequest"
                    headers["Authorization"] = "" + activity?.let { SharedHelper.getKey(it, "token_type") } + " " + activity?.let { SharedHelper.getKey(it, "access_token") }
                    return headers
                }
            }
            NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun ShowHidePass(view: View) {
        if (view.id == R.id.show_hide_current_passowrd) {
            if (et_current_password!!.transformationMethod == PasswordTransformationMethod.getInstance()) {
                show_hide_current_passowrd!!.setImageResource(R.drawable.ic_eye_icon_hide)
                //Show Password
                et_current_password!!.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                show_hide_current_passowrd!!.setImageResource(R.drawable.ic_icon_eye_show)
                et_current_password!!.setSelection(et_current_password!!.text!!.length)

                //Hide Password
                et_current_password!!.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        } else if (view.id == R.id.show_hide_change_passowrd) {
            if (et_change_password!!.transformationMethod == PasswordTransformationMethod.getInstance()) {
                show_hide_change_passowrd!!.setImageResource(R.drawable.ic_eye_icon_hide)
                //Show Password
                et_change_password!!.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                show_hide_change_passowrd!!.setImageResource(R.drawable.ic_icon_eye_show)
                et_change_password!!.setSelection(et_change_password!!.text!!.length)

                //Hide Password
                et_change_password!!.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        } else if (view.id == R.id.show_hide_confirm_passowrd) {
            if (et_confirm_password!!.transformationMethod == PasswordTransformationMethod.getInstance()) {
                show_hide_confirm_passowrd!!.setImageResource(R.drawable.ic_icon_eye_show)
                //Show Password
                et_confirm_password!!.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                show_hide_confirm_passowrd!!.setImageResource(R.drawable.ic_eye_icon_hide)
                et_confirm_password!!.setSelection(et_confirm_password!!.text!!.length)

                //Hide Password
                et_confirm_password!!.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
    }
}