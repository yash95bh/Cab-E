package com.eviort.cabedriver.NTActivity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.eviort.cabedriver.NTApplication
import com.eviort.cabedriver.NTCustomView.*
import com.eviort.cabedriver.NTFragment.FragmentHome
import com.eviort.cabedriver.NTHelper.LoadingDialog
import com.eviort.cabedriver.NTHelper.SharedHelper
import com.eviort.cabedriver.NTHelper.URLHelper
import com.eviort.cabedriver.NTUtilites.Utilities
import com.eviort.cabedriver.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.messaging.FirebaseMessaging
import com.hbb20.CountryCodePicker
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.regex.Pattern

class ActivitySignIn() : AppCompatActivity() {
    //sign_in_otp
    var lnrTitle: LinearLayout? = null
    var otp_layout: RelativeLayout? = null
    var signin_backArrow: ImageView? = null
    var backArrow: ImageView? = null

    //otp
    var text1: NTEditText? = null
    var text2: NTEditText? = null
    var text3: NTEditText? = null
    var text4: NTEditText? = null
    var text5: NTEditText? = null
    var text6: NTEditText? = null
    var nextbtn: NTButtonBold? = null
    var show_hide_signin_passowrd: ImageView? = null

    //signin_phone
    var country_choose_layout: LinearLayout? = null
    var signup_click_layout: LinearLayout? = null
    var phone_layout: RelativeLayout? = null
    var signin_phone: NTEditText? = null
    var dial_code: NTTextView? = null
    var signin_btn: NTButtonBold? = null
    var btn_signup: NTButton? = null
    var signin_mail: NTEditText? = null
    var signin_password: NTEditText? = null

    //strings
    var device_UDID: String? = null
    var device_token: String? = null
    var str_getotp = ""
    var str_otp = ""
    var email = ""
    var mailId_alert: String? = null
    var loadingDialog: LoadingDialog? = null
    var utils = Utilities()
    var context: Context = this@ActivitySignIn
    var ccPicker: CountryCodePicker? = null

    //forgotpass
    var et_current_password: NTEditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        loadingDialog = LoadingDialog(this@ActivitySignIn)
        checkoverlayPermission()
        AssignFindviewById()

        try {
            if (getIntent().extras!!.getString("screen") != null) {
                if (getIntent().extras!!.getString("screen").equals("otp")) {
                    phone_layout!!.visibility = View.GONE
                    otp_layout!!.visibility = View.VISIBLE
                    lnrTitle!!.visibility = View.VISIBLE
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        GetToken()
        device_UDID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        text1!!.addTextChangedListener(GenericTextWatcher(text1!!, text2))
        text2!!.addTextChangedListener(GenericTextWatcher(text2!!, text3))
        text3!!.addTextChangedListener(GenericTextWatcher(text3!!, text4))
        text4!!.addTextChangedListener(GenericTextWatcher(text4!!, text5))
        text5!!.addTextChangedListener(GenericTextWatcher(text5!!, text6))
        text6!!.addTextChangedListener(GenericTextWatcher(text6!!, null))


        text1!!.setOnKeyListener(GenericKeyEvent(text1!!, null))
        text2!!.setOnKeyListener(GenericKeyEvent(text2!!, text1))
        text3!!.setOnKeyListener(GenericKeyEvent(text3!!, text2))
        text4!!.setOnKeyListener(GenericKeyEvent(text4!!, text3))
        text5!!.setOnKeyListener(GenericKeyEvent(text5!!, text4))
        text6!!.setOnKeyListener(GenericKeyEvent(text6!!, text5))

        show_hide_signin_passowrd!!.setOnClickListener { v -> ShowHidePass(v) }

        nextbtn!!.setOnClickListener {
            str_getotp =
                text1!!.text.toString() + text2!!.text.toString() + text3!!.text.toString() + text4!!.text.toString() + text5!!.text.toString() + text6!!.text.toString()
            if (str_getotp!!.length < 6) {
                Toast.makeText(context, getString(R.string.otp_validation), Toast.LENGTH_LONG)
                    .show()
            } else {
                login(signin_phone!!.text.toString(), str_getotp)
            }
        }

        backArrow!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                otp_layout!!.visibility = View.GONE
                lnrTitle!!.visibility = View.GONE
                phone_layout!!.visibility = View.VISIBLE
            }
        })
        signin_backArrow!!.setOnClickListener {
            val signInIntent = Intent(this@ActivitySignIn, ActivityBegin::class.java)
            startActivity(signInIntent)
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }
        signup_click_layout!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                val signInIntent = Intent(this@ActivitySignIn, ActivityForgotPassword::class.java)
                startActivity(signInIntent)
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }
        })

        signin_btn!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                if (signin_mail!!.text.toString().isEmpty()) {
                    signin_mail!!.error = getString(R.string.mobile_validation)
                }
//                else if (signin_password!!.text.toString() == "") {
//                    signin_password!!.error = getString(R.string.password_validation)
//                } else if (signin_password!!.length() < 6) {
//                    signin_password!!.error = getString(R.string.password_size)
//                }
                else {
                    sendotp()
//                    login("","")
                    utils.hideKeypad(context, v)
                }
            }
        })

        btn_signup!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                hideSoftKeyboard(this@ActivitySignIn)
                val signInIntent = Intent(this@ActivitySignIn, ActivityForgotPassword::class.java)
                startActivity(signInIntent)
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            }
        })

    }

    class GenericKeyEvent internal constructor(
        private val currentView: EditText,
        private val previousView: EditText?
    ) : View.OnKeyListener {
        override fun onKey(p0: View?, keyCode: Int, event: KeyEvent?): Boolean {
            if (event!!.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DEL && currentView.id != R.id.text1 && currentView.text.isEmpty()) {
                //If current is empty then previous EditText's number will also be deleted
                previousView!!.text = null
                previousView.requestFocus()
                return true
            }
            return false
        }
    }

    class GenericTextWatcher internal constructor(
        private val currentView: View,
        private val nextView: View?
    ) : TextWatcher {
        override fun afterTextChanged(editable: Editable) { // TODO Auto-generated method stub
            val text = editable.toString()
            when (currentView.id) {
                R.id.text1 -> if (text.length == 1) nextView!!.requestFocus()
                R.id.text2 -> if (text.length == 1) nextView!!.requestFocus()
                R.id.text3 -> if (text.length == 1) nextView!!.requestFocus()
                R.id.text4 -> if (text.length == 1) nextView!!.requestFocus()
                R.id.text5 -> if (text.length == 1) nextView!!.requestFocus()
                //R.id.text6 -> if (text.length == 1) nextView!!.requestFocus()
                //You can use EditText4 same as above to hide the keyboard
            }
        }

        override fun beforeTextChanged(
            arg0: CharSequence,
            arg1: Int,
            arg2: Int,
            arg3: Int
        ) { // TODO Auto-generated method stub
        }

        override fun onTextChanged(
            arg0: CharSequence,
            arg1: Int,
            arg2: Int,
            arg3: Int
        ) { // TODO Auto-generated method stub
        }
    }

    private fun sendotp() {
        if (loadingDialog != null) loadingDialog!!.showDialog()
        val `object` = JSONObject()
        try {
            SharedHelper.putKey(
                context,
                "dial_code",
                ccPicker?.selectedCountryCodeWithPlus.toString()
            )
            var number = SharedHelper.getKey(context, "signup_number")
            var dialcode = SharedHelper.getKey(context, "dial_code")
            Log.d("TAG", "value224 : $number")
            `object`.put("dial_code", ccPicker?.selectedCountryCodeWithPlus.toString())
            `object`.put("mobile", signin_mail!!.text.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        Utilities.PrintAPI_URL(URLHelper.SIGNUP_WITH_OTP, `object`.toString())
        Log.d("TAG", "sendotp: ======" + `object`.toString())
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, URLHelper.SIGNUP_WITH_OTP, `object`,
            Response.Listener { response ->
                Utilities.printAPI_Response(response.toString())
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()

                if (response.getString("success").equals("1")) {
                    try {
                        str_otp = response.getString("otp").toString()
                        if (str_otp.isNotBlank()) Toast.makeText(this, str_otp, Toast.LENGTH_SHORT)
                            .show()

                        Log.e("TAG", "--" + str_otp)
                        //Toast.makeText(context, "SMS retrieving starts", Toast.LENGTH_LONG).show()
                        Utilities.print("SignInResponse", response.getString("otp"))
                        /*val signInIntent = Intent(this@ActivitySignIn, ActivitySignIn::class.java)
                        signInIntent.putExtra("screen","otp")
                        startActivity(signInIntent)*/
                        overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
                        phone_layout!!.visibility = View.GONE
                        otp_layout!!.visibility = View.VISIBLE
                        lnrTitle!!.visibility = View.VISIBLE
                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Utilities.dispalyDialog(
                        this@ActivitySignIn,
                        resources.getString(R.string.app_name),
                        response.getString("message")
                    )
                }
            },
            Response.ErrorListener { error ->
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()

                try {
                    if (error is TimeoutError) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_network_timeout),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is NoConnectionError) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_no_network),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is AuthFailureError) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_auth_failure),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ServerError) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_server_connection),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is NetworkError) {
                        Toast.makeText(
                            context,
                            context.getString(R.string.error_network),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ParseError) {
                        // Toast.makeText(context, context.getString(R.string.error_parse), Toast.LENGTH_LONG).show()
                    } else {
                        try {
                            val err_response = JSONObject(error.toString())
//                                Utilities.showCustomAlert(context, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), err_response.optString("message"))
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, "Something went wrong", Toast.LENGTH_LONG).show()
                }

            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = SharedHelper.getKey(context, "lang")!!
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "" + SharedHelper.getKey(context, "token_type")
                    .toString() + " " + SharedHelper.getKey(context, "access_token")
                System.out.println(
                    "PassengerAPI Token : " + SharedHelper.getKey(
                        context,
                        "token_type"
                    ).toString() + " " + SharedHelper.getKey(context, "access_token")
                )
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    fun loadFragment(fragment: Fragment?, aBackstack: String?) {
        // load fragment
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, (fragment)!!)
        transaction.addToBackStack(aBackstack)
        transaction.commit()
    }

    fun checkoverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + packageName)
                )
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE)
            }
        }
    }

    fun showForgetPasswordDialog() {
        val builder = AlertDialog.Builder(context)
        val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.layout_change_password, null)
        builder.setCancelable(false)
        builder.setView(layout)
        builder.create()
        val alertDialog = builder.create()
        val tv_password_title = layout.findViewById<View>(R.id.tv_password_title) as NTBoldTextView
        val img_exit = layout.findViewById<View>(R.id.img_exit) as ImageView
        val btn_save = layout.findViewById<View>(R.id.btn_save) as NTButton
        et_current_password = layout.findViewById<View>(R.id.et_emailId) as NTEditText
        val tl_current_password =
            layout.findViewById<View>(R.id.tl_current_password) as TextInputLayout
        val tl_change_password =
            layout.findViewById<View>(R.id.tl_change_password) as TextInputLayout
        val tl_confirm_password =
            layout.findViewById<View>(R.id.tl_confirm_password) as TextInputLayout
        tv_password_title.text = "Forget Password"
        tl_current_password.visibility = View.GONE
        tl_current_password.hint = "Enter MailId"
        tl_change_password.visibility = View.GONE
        tl_confirm_password.visibility = View.GONE
        email = et_current_password!!.text.toString().trim { it <= ' ' }
        img_exit.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                utils.hideKeypad(this@ActivitySignIn, v)
                alertDialog.dismiss()
            }
        })
        btn_save.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                utils.hideKeypad(this@ActivitySignIn, v)
                email = et_current_password!!.text.toString().trim { it <= ' ' }
                if (et_current_password!!.text.toString().isEmpty()) {
                    et_current_password!!.requestFocus()
                    et_current_password!!.error = "Email Id Field Required."
                } else if (!isValidEmail(email)) {
                    et_current_password!!.requestFocus()
                    et_current_password!!.error = "Enter Valid Email Address"
                } else {
                    forgetPassword(email, alertDialog)
                }
            }
        })
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.window!!.attributes.windowAnimations = R.style.dialog_animation
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

    private fun forgetPassword(email: String, alertDialog: AlertDialog) {
        if (loadingDialog != null) loadingDialog!!.showDialog()
        val `object` = JSONObject()
        try {
            `object`.put("email", email)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Utilities.PrintAPI_URL(URLHelper.FORGET_PASSWORD, `object`.toString())
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            URLHelper.FORGET_PASSWORD,
            `object`,
            object : Response.Listener<JSONObject> {
                override fun onResponse(response: JSONObject) {
                    if ((loadingDialog != null) && (loadingDialog!!.isShowing)) loadingDialog!!.hideDialog()
                    Utilities.printAPI_Response(response.toString())
                    try {
                        if ((response.getString("success") == "0")) {
                            utils.showCustomAlert(
                                context,
                                Utilities.ALERT_ERROR,
                                resources.getString(R.string.app_name),
                                "This Email Address Not Register By Book Taxi."
                            )
                            //utils.showAlert(context, "This Email Address Not Register By Book Taxi.");
                        } else {
                            alertDialog.dismiss()
                            val value = response.getJSONObject("provider")
                            showResetPasswordDialog(
                                response.optString("message"),
                                value.optString("id"),
                                value.optString("otp")
                            )
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    try {
                        if ((loadingDialog != null) && (loadingDialog!!.isShowing)) loadingDialog!!.hideDialog()
                        if (error is TimeoutError) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.error_network_timeout),
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (error is NoConnectionError) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.error_no_network),
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (error is AuthFailureError) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.error_auth_failure),
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (error is ServerError) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.error_server_connection),
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (error is NetworkError) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.error_network),
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (error is ParseError) {
                            // Toast.makeText(context, context.getString(R.string.error_parse), Toast.LENGTH_LONG).show()
                        } else {
                            utils.showCustomAlert(
                                context,
                                Utilities.ALERT_ERROR,
                                resources.getString(R.string.app_name),
                                error.message
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            context,
                            context.getString(R.string.something_went_wrong),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }) {
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                //                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    fun showResetPasswordDialog(message: String?, id: String, otp: String) {
        val builder = AlertDialog.Builder(context)
        val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.layout_reset_password, null)
        builder.setCancelable(false)
        builder.setView(layout)
        builder.create()
        val alertDialog = builder.create()
        val tv_resetDesc: NTTextView
        val img_exit = layout.findViewById<View>(R.id.img_exit) as ImageView
        val btn_save = layout.findViewById<View>(R.id.btn_save) as NTButton
        val et_otp: NTEditText
        val change_password: NTEditText
        val et_confirm_password: NTEditText
        val new_password: String
        val confirm_password: String
        tv_resetDesc = layout.findViewById<View>(R.id.tv_resetDesc) as NTTextView
        et_otp = layout.findViewById<View>(R.id.et_otp) as NTEditText
        val tl_current_password =
            layout.findViewById<View>(R.id.tl_change_password) as TextInputLayout
        tv_resetDesc.text = otp
        tv_resetDesc.visibility = View.INVISIBLE
        change_password = layout.findViewById<View>(R.id.et_password) as NTEditText
        et_confirm_password = layout.findViewById<View>(R.id.et_confirm_password) as NTEditText
        img_exit.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                utils.hideKeypad(this@ActivitySignIn, v)
                alertDialog.dismiss()
            }
        })
        btn_save.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                utils.hideKeypad(this@ActivitySignIn, v)
                if (et_otp.text.toString().isEmpty()) {
                    et_otp.requestFocus()
                    et_otp.error = "OTP Field Required."
                } else if (change_password.text.toString().isEmpty()) {
                    change_password.requestFocus()
                    change_password.error = "Password Field Required."
                } else if (et_confirm_password.text.toString().isEmpty()) {
                    et_confirm_password.requestFocus()
                    et_confirm_password.error = "Confirm Password Field Required."
                } else if (!et_confirm_password.text.toString()
                        .equals(change_password.text.toString(), ignoreCase = true)
                ) {
                    et_confirm_password.requestFocus()
                    et_confirm_password.error = "Password Does Not Matched."
                } else if ((et_otp.text.toString() == otp)) {
                    resetPassword(
                        id,
                        change_password.text.toString(),
                        et_confirm_password.text.toString(),
                        alertDialog
                    )
                } else {
                    utils.showCustomAlert(
                        context,
                        Utilities.ALERT_WARNING,
                        resources.getString(R.string.app_name),
                        "OTP Not Matched"
                    )
                }
            }
        })
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.window!!.attributes.windowAnimations = R.style.dialog_animation
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

    private fun resetPassword(id: String, s: String, toString: String, alertDialog: AlertDialog) {
        if (loadingDialog != null) loadingDialog!!.showDialog()
        val `object` = JSONObject()
        try {
            `object`.put("id", id)
            `object`.put("password", s)
            `object`.put("password_confirmation", toString)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Utilities.PrintAPI_URL(URLHelper.RESET_PASSWORD, `object`.toString())
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            URLHelper.RESET_PASSWORD,
            `object`,
            object : Response.Listener<JSONObject> {
                override fun onResponse(response: JSONObject) {
                    if ((loadingDialog != null) && (loadingDialog!!.isShowing)) loadingDialog!!.hideDialog()
                    Utilities.printAPI_Response(response.toString())
                    try {
                        if ((response.getString("message") == "Password Updated")) {
                            alertDialog.dismiss()
                            val builder = AlertDialog.Builder(this@ActivitySignIn)
                            val inflater =
                                context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                            val layout = inflater.inflate(R.layout.layout_signup_success, null)
                            builder.setCancelable(false)
                            builder.setView(layout)
                            builder.create()
                            val sccess_des =
                                layout.findViewById<View>(R.id.sccess_des) as NTTextView
                            sccess_des.text = resources.getString(R.string.success_password_desc)
                            val alertDialog = builder.create()
                            val btn_ok = layout.findViewById<View>(R.id.btn_ok) as NTButton
                            btn_ok.setOnClickListener(object : View.OnClickListener {
                                override fun onClick(v: View) {
                                    utils.hideKeypad(this@ActivitySignIn, v)
                                    alertDialog.dismiss()
                                    startActivity(
                                        Intent(
                                            this@ActivitySignIn,
                                            ActivitySignIn::class.java
                                        )
                                    )
                                    finish()
                                }
                            })
                            alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                            alertDialog.window!!.attributes.windowAnimations =
                                R.style.dialog_animation
                            alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                            alertDialog.show()
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    try {
                        if (error is TimeoutError) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.error_network_timeout),
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (error is NoConnectionError) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.error_no_network),
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (error is AuthFailureError) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.error_auth_failure),
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (error is ServerError) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.error_server_connection),
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (error is NetworkError) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.error_network),
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (error is ParseError) {
                            // Toast.makeText(context, context.getString(R.string.error_parse), Toast.LENGTH_LONG).show()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            context,
                            context.getString(R.string.something_went_wrong),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] =
                    "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(
                        context,
                        "access_token"
                    )
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    fun GetToken() {
        try {
            if (SharedHelper.getKey(context, "device_token") != "" && SharedHelper.getKey(
                    context,
                    "device_token"
                ) != null
            ) {
                device_token = SharedHelper.getKey(context, "device_token")
                // utils.print(TAG, "GCM Registration Token: " + device_token);
            } else {
                device_token = "COULD NOT GET FCM TOKEN"
                // utils.print(TAG, "Failed to complete token refresh: " + device_token);
            }
        } catch (e: Exception) {
            device_token = "COULD NOT GET FCM TOKEN"
            // utils.print(TAG, "Failed to complete token refresh");
        }
        try {
            device_UDID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            //   utils.print(TAG, "Device UDID:" + device_UDID);
        } catch (e: Exception) {
            device_UDID = "COULD NOT GET UDID"
            e.printStackTrace()
            //  utils.print(TAG, "Failed to complete device UDID");
        }
    }/*    Intent intent = new Intent(activity, MainActivity.class);
                        activity.startActivity(intent);*/

    //                Intent loginIntent = new Intent(ActivitySignIn.this,MainActivity.class);
//                startActivity(loginIntent);
//  utils.showAlert(ActivitySignIn.this,response.getString("message"));
    // object.put("vehicle_no", vehicle_no);

    private val loginDetails: Unit
        private get() {
            if (loadingDialog != null) loadingDialog!!.showDialog()
            val `object` = JSONObject()
            try {
                `object`.put("device_id", device_UDID)
                `object`.put("device_type", "android")
                `object`.put("device_token", SharedHelper.getKey(context, "device_token"))
                `object`.put("mobile", signin_phone!!.text.toString())
                `object`.put("otp", str_getotp)
                // object.put("vehicle_no", vehicle_no);
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            Utilities.PrintAPI_URL(URLHelper.VERIFYOTP, `object`.toString())
            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
                Method.POST,
                URLHelper.VERIFYOTP,
                `object`,
                object : Response.Listener<JSONObject> {
                    override fun onResponse(response: JSONObject) {
                        Utilities.printAPI_Response(response.toString())
                        if ((loadingDialog != null) && (loadingDialog!!.isShowing)) loadingDialog!!.hideDialog()
                        try {
                            if (response.getString("success").equals("0", ignoreCase = true)) {
                                //  utils.showAlert(ActivitySignIn.this,response.getString("message"));
                                utils.showCustomAlert(
                                    context,
                                    Utilities.ALERT_ERROR,
                                    resources.getString(R.string.app_name),
                                    response.getString("message")
                                )
                            } else {
                                SharedHelper.putKey(
                                    context,
                                    "access_token",
                                    response.optString("token")
                                )
                                /*    Intent intent = new Intent(activity, MainActivity.class);
               activity.startActivity(intent);*/
                                profile
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        //                Intent loginIntent = new Intent(ActivitySignIn.this,MainActivity.class);
//                startActivity(loginIntent);
                    }
                },
                object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError) {
                        try {
                            if ((loadingDialog != null) && (loadingDialog!!.isShowing)) loadingDialog!!.hideDialog()
                            val response = error.networkResponse
                            if (response != null && response.statusCode == 400) {
                                try {
                                    val errorObj = JSONObject(String(response.data))
                                    utils.showCustomAlert(
                                        context,
                                        Utilities.ALERT_ERROR,
                                        resources.getString(R.string.app_name),
                                        errorObj.getString("error")
                                    )
                                } catch (e: JSONException) {
                                    e.printStackTrace()
                                    utils.showCustomAlert(
                                        context,
                                        Utilities.ALERT_ERROR,
                                        resources.getString(R.string.app_name),
                                        context.getString(R.string.something_went_wrong)
                                    )
                                }
                            } else if (error is TimeoutError) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.error_network_timeout),
                                    Toast.LENGTH_LONG
                                ).show()
                            } else if (error is NoConnectionError) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.error_no_network),
                                    Toast.LENGTH_LONG
                                ).show()
                            } else if (error is AuthFailureError) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.error_auth_failure),
                                    Toast.LENGTH_LONG
                                ).show()
                            } else if (error is ServerError) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.error_server_connection),
                                    Toast.LENGTH_LONG
                                ).show()
                            } else if (error is NetworkError) {
                                Toast.makeText(
                                    context,
                                    context.getString(R.string.error_network),
                                    Toast.LENGTH_LONG
                                ).show()
                            } else if (error is ParseError) {
                                //Toast.makeText(context, context.getString(R.string.error_parse), Toast.LENGTH_LONG).show()
                            } else {
                                utils.showCustomAlert(
                                    context,
                                    Utilities.ALERT_ERROR,
                                    resources.getString(R.string.app_name),
                                    error.message
                                )
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(
                                context,
                                context.getString(R.string.something_went_wrong),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["X-localization"] = "en"
                    headers["Content-Type"] = "application/json"
                    headers["X-Requested-With"] = "XMLHttpRequest"
                    return headers
                }
            }
            NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
        }

    private fun login(signup_mail: String, signup_confirm_password: String) {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val access_token = task.result
            SharedHelper.putKey(context, "device_token", access_token)
        })
        SharedHelper.putKey(context, "wallet_flag", "1")

        if (loadingDialog != null) loadingDialog!!.showDialog()
        val `object` = JSONObject()
        try {
            // `object`.put("grant_type", "password")
            `object`.put("mobile", signin_mail!!.text.toString())
            `object`.put("dial_code", ccPicker?.selectedCountryCodeWithPlus.toString())
//            `object`.put("password", signin_password!!.text.toString())
//            `object`.put("scope", "")
            `object`.put("device_type", "android")
            `object`.put("device_id", device_UDID)
            `object`.put("device_token", SharedHelper.getKey(context, "device_token"))
            `object`.put("otp", str_getotp)
            `object`.put("client_id", URLHelper.client_id)
            `object`.put("login_by", "manual")
            `object`.put("client_secret", URLHelper.client_secret)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.i("LOGIN_REQ", URLHelper.LOGIN + "   " + `object`.toString())
        Utilities.print(URLHelper.LOGIN, `object`.toString())
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, URLHelper.LOGIN, `object`,
            Response.Listener { response ->
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                Utilities.printAPI_Response(response.toString())
                if (response != null) {
                    if (response.getString("success").equals("1")) {
                        try {
                            SharedHelper.putKey(context, "loggedIn", "true")
                            SharedHelper.putKey(context, "signup_first_start", "stop")
                            SharedHelper.putKey(context, "a", "0")
                            SharedHelper.putKey(
                                applicationContext,
                                "token_type",
                                response.optString("token_type")
                            )
                            SharedHelper.putKey(
                                context,
                                "access_token",
                                response.optString("token")
                            )
                            profile

                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Utilities.dispalyDialog(
                            this@ActivitySignIn,
                            resources.getString(R.string.app_name),
                            response.getString("message")
                        )
                    }
                }
            },
            Response.ErrorListener { error ->
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                Log.d("TAG", "----------------------------: " + error.toString())
                try {
                    var json: String? = null
                    var Message: String
                    val response = error.networkResponse
                    if (response != null && response.data != null) {
                        try {
                            val errorObj = JSONObject(String(response.data))
                            println(
                                "PassengerAPI response error : " + error + " " + error.networkResponse + " " + response.statusCode + " " + errorObj.optString(
                                    "error"
                                )
                            )
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    Utilities.showToast(
                                        this@ActivitySignIn,
                                        errorObj.optString("message")
                                    )
                                } catch (e: java.lang.Exception) {
                                    Utilities.showToast(
                                        this@ActivitySignIn,
                                        getString(R.string.something_went_wrong)
                                    )
                                }
                            } else if (response.statusCode == 401) {
                                Utilities.showToast(
                                    this@ActivitySignIn,
                                    getString(R.string.something_went_wrong)
                                )
                                // displayMessage(errorObj.optString("message"));
                                //                            refreshAccessToken("PAST_TRIPS");
                            } else if (response.statusCode == 422) {
                                json = NTApplication.trimMessage(String(response.data))
                                if (json !== "" && json != null) {
                                    Utilities.showToast(this@ActivitySignIn, json)
                                } else {
                                    Utilities.showToast(
                                        this@ActivitySignIn,
                                        getString(R.string.please_try_again)
                                    )
                                }
                            } else if (response.statusCode == 503) {
                                Utilities.showToast(
                                    this@ActivitySignIn,
                                    getString(R.string.server_down)
                                )
                            } else {
                                Utilities.showToast(
                                    this@ActivitySignIn,
                                    getString(R.string.please_try_again)
                                )
                            }
                        } catch (e: java.lang.Exception) {
                            Utilities.showToast(
                                this@ActivitySignIn,
                                getString(R.string.something_went_wrong)
                            )
                        }
                    } else {
                        Utilities.showToast(
                            this@ActivitySignIn,
                            getString(R.string.please_try_again)
                        )
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        context,
                        context.getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                //                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token"));
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    val profile: Unit
        get() {
            Utilities.PrintAPI_URL(URLHelper.GET_PROFILE_DETAILS, "GET")
            println(
                "conductorAPI Header: " + "X-Requested-With : XMLHttpRequest " + "Authorization : " + SharedHelper.getKey(
                    context,
                    "access_token"
                )
            )
            val `object` = JSONObject()
            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
                Method.GET,
                URLHelper.GET_PROFILE_DETAILS,
                `object`,
                object : Response.Listener<JSONObject> {
                    override fun onResponse(response: JSONObject) {

                        println("conductorAPI response : $response")
                        FragmentHome.isRunning = true;
                        SharedHelper.putKey(context, "id", response.optString("id"))
                        SharedHelper.putKey(context, "first_name", response.optString("name"))
                        SharedHelper.putKey(context, "email", response.optString("email"))
                        if (response.optString("avatar").startsWith("http"))
                            SharedHelper.putKey(context, "picture", response.optString("avatar"))
                        else SharedHelper.putKey(
                            context,
                            "picture",
                            URLHelper.base + "storage/" + response.optString("avatar")
                        )
                        SharedHelper.putKey(context, "gender", response.optString("gender"))
                        SharedHelper.putKey(context, "mobile", response.optString("mobile"))
                        SharedHelper.putKey(
                            context,
                            "approval_status",
                            response.optString("status")
                        )
                        SharedHelper.putKey(context, "currency", response.optString("currency"))
                        SharedHelper.putKey(context, "sos_number", response.optString("sos_number"))

                        //                    SharedHelper.putKey(context, "wallet_balance", response.optString("wallet_balance"));
//                    SharedHelper.putKey(context, "payment_mode", response.optString("payment_mode"));
                        // SharedHelper.putKey(context, "currency", response.optString("currency"));
                        SharedHelper.putKey(context, "loggedIn", getString(R.string.True))
                        if (response.optJSONObject("service") != null) {
                            try {
                                val service = response.optJSONObject("service")
                                if (service.optJSONObject("service_type") != null) {
                                    val serviceType = service.optJSONObject("service_type")
                                    SharedHelper.putKey(
                                        context,
                                        "service",
                                        serviceType.optString("name")
                                    )
                                    SharedHelper.putKey(
                                        context,
                                        "service_type",
                                        serviceType.optString("id")
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        if (response.optString("status").equals("new", ignoreCase = true)) {
//                    Intent intent = new Intent(activity, WaitingForApproval.class);
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        } else {
                            val intent = Intent(context, MainActivity::class.java)
                            context.startActivity(intent)
                        }
                    }
                },
                object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError) {
                        try {

                            var json: String? = null
                            var Message: String
                            val response = error.networkResponse
                            if (response != null && response.data != null) {
                                try {
                                    val errorObj = JSONObject(String(response.data))
                                    if ((response.statusCode == 400) || (response.statusCode == 405) || (response.statusCode == 500)) {
                                        try {
                                            Utilities.showToast(
                                                this@ActivitySignIn,
                                                errorObj.optString("message")
                                            )
                                        } catch (e: Exception) {
                                            Utilities.showToast(
                                                this@ActivitySignIn,
                                                getString(R.string.something_went_wrong)
                                            )
                                        }
                                    } else if (response.statusCode == 401) {
                                        SharedHelper.putKey(
                                            context,
                                            "loggedIn",
                                            getString(R.string.False)
                                        )
                                    } else if (response.statusCode == 422) {
                                        json = NTApplication.trimMessage(String(response.data))
                                        if (json !== "" && json != null) {
                                            Utilities.showToast(this@ActivitySignIn, json)
                                        } else {
                                            Utilities.showToast(
                                                this@ActivitySignIn,
                                                getString(R.string.please_try_again)
                                            )
                                        }
                                    } else if (response.statusCode == 503) {
                                        Utilities.showToast(
                                            this@ActivitySignIn,
                                            getString(R.string.server_down)
                                        )
                                    }
                                } catch (e: Exception) {
                                    Utilities.showToast(
                                        this@ActivitySignIn,
                                        getString(R.string.something_went_wrong)
                                    )
                                }
                            } else {
                                if (error is NoConnectionError) {
                                    Utilities.showToast(
                                        this@ActivitySignIn,
                                        getString(R.string.oops_connect_your_internet)
                                    )
                                } else if (error is NetworkError) {
                                    Utilities.showToast(
                                        this@ActivitySignIn,
                                        getString(R.string.oops_connect_your_internet)
                                    )
                                } else if (error is TimeoutError) {
                                    profile
                                }
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                            Toast.makeText(
                                context,
                                context.getString(R.string.something_went_wrong),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    //headers["X-localization"] = SharedHelper.getKey(context, "lang")
                    headers["X-Requested-With"] = "XMLHttpRequest"
                    headers["Authorization"] =
                        "Bearer " + SharedHelper.getKey(context, "access_token")
                    return headers
                }
            }
            NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
        }

    private fun isValidEmail(email: String): Boolean {
        val EMAIL_PATTERN = ("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
        val pattern = Pattern.compile(EMAIL_PATTERN)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun GetForgetPassword() {
        if (loadingDialog != null) loadingDialog!!.showDialog()
        val `object` = JSONObject()
        try {
            `object`.put("email", mailId_alert)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Utilities.PrintAPI_URL(URLHelper.FORGET_PASSWORD, `object`.toString())
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            URLHelper.FORGET_PASSWORD,
            `object`,
            object : Response.Listener<JSONObject?> {
                override fun onResponse(response: JSONObject?) {
                    if ((loadingDialog != null) && (loadingDialog!!.isShowing)) loadingDialog!!.hideDialog()
                }
            },
            object : Response.ErrorListener {
                override fun onErrorResponse(error: VolleyError) {
                    try {
                        if ((loadingDialog != null) && (loadingDialog!!.isShowing)) loadingDialog!!.hideDialog()
                        if (error is TimeoutError) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.error_network_timeout),
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (error is NoConnectionError) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.error_no_network),
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (error is AuthFailureError) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.error_auth_failure),
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (error is ServerError) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.error_server_connection),
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (error is NetworkError) {
                            Toast.makeText(
                                context,
                                context.getString(R.string.error_network),
                                Toast.LENGTH_LONG
                            ).show()
                        } else if (error is ParseError) {
                            // Toast.makeText(context, context.getString(R.string.error_parse), Toast.LENGTH_LONG).show()
                        } else {
                            utils.showCustomAlert(
                                context,
                                Utilities.ALERT_ERROR,
                                resources.getString(R.string.app_name),
                                error.message
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            context,
                            context.getString(R.string.something_went_wrong),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] =
                    "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(
                        context,
                        "access_token"
                    )
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    private fun AssignFindviewById() {
        //sign-in-otp
        lnrTitle = findViewById<View>(R.id.lnrTitle) as LinearLayout
        otp_layout = findViewById<View>(R.id.signin_otp_layout) as RelativeLayout
        backArrow = findViewById<View>(R.id.backArrow) as ImageView
        signin_backArrow = findViewById<View>(R.id.signin_backArrow) as ImageView

        //otp
        text1 = findViewById<View>(R.id.text1) as NTEditText
        text2 = findViewById<View>(R.id.text2) as NTEditText
        text3 = findViewById<View>(R.id.text3) as NTEditText
        text4 = findViewById<View>(R.id.text4) as NTEditText
        text5 = findViewById<View>(R.id.text5) as NTEditText
        text6 = findViewById<View>(R.id.text6) as NTEditText
        nextbtn = findViewById<View>(R.id.sign_in_next_btn) as NTButtonBold
        //signin-phone
        country_choose_layout = findViewById<View>(R.id.country_choose_layout) as LinearLayout
        signup_click_layout = findViewById<View>(R.id.signup_click_layout) as LinearLayout
        phone_layout = findViewById<View>(R.id.signin_phone_layout) as RelativeLayout
        signin_phone = findViewById<View>(R.id.signin_phone) as NTEditText
        signin_mail = findViewById<View>(R.id.signin_mail) as NTEditText
        signin_password = findViewById<View>(R.id.signin_password) as NTEditText
        show_hide_signin_passowrd = findViewById<View>(R.id.show_hide_signin_passowrd) as ImageView

        // signin_phone!!.setKeyListener(DigitsKeyListener.getInstance("0123456789"))
        dial_code = findViewById<View>(R.id.dial_code) as NTTextView
        ccPicker = findViewById<View>(R.id.countryPicker) as CountryCodePicker
        signin_btn = findViewById<View>(R.id.sign_in_btn) as NTButtonBold
        btn_signup = findViewById<View>(R.id.btn_signup) as NTButton
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    Toast.makeText(context, "Permission Granted", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Permission Not Granted.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun ShowHidePass(view: View) {
        if (signin_password!!.transformationMethod == PasswordTransformationMethod.getInstance()) {
            show_hide_signin_passowrd!!.setImageResource(R.drawable.ic_icon_eye_show)
            //Show Password
            signin_password!!.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            show_hide_signin_passowrd!!.setImageResource(R.drawable.ic_eye_icon_hide)
            signin_password!!.setSelection(signin_password!!.text!!.length)

            //Hide Password
            signin_password!!.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }

    companion object {
        var ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469
        fun hideSoftKeyboard(activity: Activity) {
            /* val inputMethodManager = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
             inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)*/
            val view = activity!!.currentFocus
            if (view != null) {
                val inputManager =
                    activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(
                    view.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS
                )
            }
        }
    }
}