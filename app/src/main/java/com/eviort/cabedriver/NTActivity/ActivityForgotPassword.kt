package com.eviort.cabedriver.NTActivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.*
import android.os.StrictMode.ThreadPolicy
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.eviort.cabedriver.NTApplication
import com.eviort.cabedriver.NTCustomView.NTBoldTextView
import com.eviort.cabedriver.NTCustomView.NTButton
import com.eviort.cabedriver.NTCustomView.NTTextView
import com.eviort.cabedriver.NTHelper.ConnectionHelper
import com.eviort.cabedriver.NTHelper.LoadingDialog
import com.eviort.cabedriver.NTHelper.SharedHelper
import com.eviort.cabedriver.NTHelper.URLHelper
import com.eviort.cabedriver.NTUtilites.Utilities
import com.eviort.cabedriver.R
import com.goodiebag.pinview.Pinview
import com.google.android.material.textfield.TextInputLayout
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.concurrent.TimeUnit

class ActivityForgotPassword : AppCompatActivity() {
    var context: Context = this@ActivityForgotPassword
    var backArrow: ImageView? = null
    var back: NTTextView? = null
    var signIn: NTTextView? = null
    var resendOTP: NTTextView? = null
    var resent_tv: NTTextView? = null
    var send_via_mail: NTTextView? = null
    var titleText: TextView? = null
    var nextICON: NTButton? = null
    var btn_reset: NTButton? = null
    var newPasswordLayout: TextInputLayout? = null
    var confirmPasswordLayout: TextInputLayout? = null
    var OtpLay: TextInputLayout? = null
    var newPassowrd: EditText? = null
    var confirmPassword: EditText? = null
    var OTP: EditText? = null
    var email: EditText? = null
    var loadingDialog: LoadingDialog? = null
    var validation = ""
    var str_newPassword: String? = null
    var str_confirmPassword: String? = null
    var id: String? = null
    var str_email = ""
    var str_otp: String? = null
    var server_opt: String? = null
    var helper: ConnectionHelper? = null
    var isInternet: Boolean? = null
    var note_txt: TextView? = null
    var ll_reset_password: LinearLayout? = null
    var ll_forgot_password: LinearLayout? = null
    var fromActivity =true
    var pinView: Pinview? = null
    var profile_title: NTBoldTextView? = null
    var show_hide_new_passowrd: ImageView? = null
    var show_hide_confirm_passowrd: ImageView? = null
    var utils = Utilities()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utilities.setLocale(context, SharedHelper.getKey(context, "lang"))
        setContentView(R.layout.activity_forgot_password)


        try {
            if(ll_reset_password!!.visibility == View.VISIBLE) {
                pinView!!.setCursorColor(Color.BLACK)
                pinView!!.showCursor(true)
            }else{
                hideSoftKeyboard(this@ActivityForgotPassword)
            }

            /*pinView!!.setCursorColor(Color.BLACK)
            pinView!!.showCursor(true)*/
            val intent = intent
            if (intent != null) {
                if (getIntent().extras!!.getBoolean("isFromMailActivity")) {
                    fromActivity = true
                }
                if (getIntent().extras!!.getBoolean("isfromprofile")) {
                    fromActivity = false
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        findViewById()
        if (Build.VERSION.SDK_INT > 15) {
            val policy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        show_hide_confirm_passowrd!!.setOnClickListener { v -> ShowHidePass(v) }
        show_hide_new_passowrd!!.setOnClickListener { v -> ShowHidePass(v) }
        nextICON!!.setOnClickListener {
            str_email = email!!.text.toString()
            if (validation.equals("", ignoreCase = true)) {
                if (email!!.text.toString() == "") {
                    displayMessage(getString(R.string.mobile_validation))
                }  else {
                    if (isInternet!!) {
                        forgetPassword()
                    } else {
                        displayMessage(getString(R.string.something_went_wrong_net))
                    }
                }
            } else {
            }
        }
        btn_reset!!.setOnClickListener {
            str_newPassword = newPassowrd!!.text.toString()
            str_confirmPassword = confirmPassword!!.text.toString()
            str_otp = pinView!!.value.toString()
            if (str_otp == "") {
                displayMessage(getString(R.string.otp_validation))
            } else if (!str_otp.equals(server_opt, ignoreCase = true)) {
                displayMessage(getString(R.string.inncorrect_otp))
            } else if (str_newPassword == "" || str_newPassword.equals(getString(R.string.new_password), ignoreCase = true)) {
                displayMessage(getString(R.string.password_validation))
            } else if (str_newPassword!!.length < 6) {
                displayMessage(getString(R.string.password_size))
            } else if (str_confirmPassword == "" || str_confirmPassword.equals(getString(R.string.confirm_password), ignoreCase = true) || !str_newPassword.equals(str_confirmPassword, ignoreCase = true)) {
                displayMessage(getString(R.string.confirm_password_validation))
            } else if (str_confirmPassword!!.length < 6) {
                displayMessage(getString(R.string.password_size))
            } else {
                if (isInternet!!) {
                    resetpassword()
                } else {
                    displayMessage(getString(R.string.something_went_wrong_net))
                }
            }
        }
        backArrow!!.setOnClickListener {
            onBackPressed()
        }
    }

    private fun forgetPassword() {
        loadingDialog = LoadingDialog(this@ActivityForgotPassword)
        if (loadingDialog != null) loadingDialog!!.showDialog()
        val `object` = JSONObject()
        try {
            `object`.put("mobile", str_email)
            //`object`.put("email", str_email)
            Log.e("ActivityForgotPassword", "" + `object`)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Utilities.PrintAPI_URL(URLHelper.FORGET_PASSWORD, `object`.toString())
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URLHelper.FORGET_PASSWORD, `object`, Response.Listener { response ->
            Utilities.printAPI_Response(response.toString())

            if (response.getString("success").equals("1")) {
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                Log.v("ForgetPasswordResponse", response.toString())
                validation = "reset"
                val userObject = response.optJSONObject("provider")
                id = userObject.optInt("id").toString()
                ll_forgot_password!!.visibility = View.GONE
                ll_reset_password!!.visibility = View.VISIBLE
                nextICON!!.visibility = View.GONE
                server_opt = userObject.optString("otp")
                email!!.isFocusable = false
                email!!.isFocusableInTouchMode = false
                email!!.isClickable = false

                btn_reset!!.visibility = View.VISIBLE
                newPassowrd!!.visibility = View.VISIBLE
                confirmPassword!!.visibility = View.VISIBLE
                pinView!!.visibility = View.VISIBLE
                send_via_mail!!.text = resources.getString(R.string.send_via_mobile) + " " + str_email
                profile_title!!.text = resources.getString(R.string.reset_password)

            } else {
                utils.showAlert(this@ActivityForgotPassword, "This Mobile number is Not Registered By Pikap Driver")
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()

            }

        }, Response.ErrorListener { error ->
            try {
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                var json: String? = null
                var Message: String
                val response = error.networkResponse
                Log.e("MyTest", "" + error)
                // Log.e("MyTestError1", "" + response.statusCode);
                if (response != null && response.data != null) {
                    try {
                        val errorObj = JSONObject(String(response.data))
                        println("PassengerAPI response error : " + error + " " + error.networkResponse + " " + response.statusCode + " " + errorObj.optString("error"))
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"))
                            } catch (e: Exception) {
                                displayMessage("Something went wrong.")
                            }
                        } else if (response.statusCode == 401) {
                            try {
                                if (errorObj.optString("message").equals("invalid_token", ignoreCase = true)) {

                                } else {
                                    displayMessage(errorObj.optString("message"))
                                }
                            } catch (e: Exception) {
                                displayMessage("Something went wrong.")
                            }
                        } else if (response.statusCode == 422) {
                            json = NTApplication.trimMessage(String(response.data))
                            if (json !== "" && json != null) {
                                displayMessage(json)
                            } else {
                                displayMessage("Please try again.")
                            }
                        } else {
                            displayMessage("Please try again.")
                        }
                    } catch (e: Exception) {
                        displayMessage("Something went wrong.")
                    }
                } else {
                    if (error is NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet))
                    } else if (error is NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet))
                    } else if (error is TimeoutError) {
                        forgetPassword()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                displayMessage("Something went wrong.")
            }
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                //headers["X-localization"] = SharedHelper.getKey(context, "lang")
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    private fun resetpassword() {
        loadingDialog = LoadingDialog(this@ActivityForgotPassword)
        if (loadingDialog != null) loadingDialog!!.showDialog()
        loadingDialog!!.showDialog()
        val `object` = JSONObject()
        try {
            `object`.put("id", id)
            `object`.put("password", str_newPassword)
            `object`.put("password_confirmation", str_confirmPassword)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Utilities.PrintAPI_URL(URLHelper.RESET_PASSWORD, `object`.toString())
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URLHelper.RESET_PASSWORD, `object`, Response.Listener { response ->
            Utilities.printAPI_Response(response.toString())
            if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
            Log.v("ResetPasswordResponse", response.toString())
            try {
                val object1 = JSONObject(response.toString())
                Toast.makeText(context, object1.optString("message"), Toast.LENGTH_SHORT).show()
                startActivity(Intent(this@ActivityForgotPassword, ActivitySignIn::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finish()
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }, Response.ErrorListener { error ->
            if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
            var json: String? = null
            var Message: String
            val response = error.networkResponse
            Log.e("MyTest", "" + error)
            Log.e("MyTestError", "" + error.networkResponse)
            Log.e("MyTestError1", "" + response!!.statusCode)
            if (response != null && response.data != null) {
                try {
                    val errorObj = JSONObject(String(response.data))
                    println("PassengerAPI response error : " + error + " " + error.networkResponse + " " + response.statusCode + " " + errorObj.optString("error"))
                    if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                        try {
                            displayMessage(errorObj.optString("message"))
                        } catch (e: Exception) {
                            displayMessage("Something went wrong.")
                        }
                    } else if (response.statusCode == 401) {
                        try {
                            if (errorObj.optString("message").equals("invalid_token", ignoreCase = true)) {
                                //refreshAccessToken("RESET_PASSWORD");
                            } else {
                                displayMessage(errorObj.optString("message"))
                            }
                        } catch (e: Exception) {
                            displayMessage("Something went wrong.")
                        }
                    } else if (response.statusCode == 422) {
                        json = NTApplication.trimMessage(String(response.data))
                        if (json !== "" && json != null) {
                            displayMessage(json)
                        } else {
                            displayMessage("Please try again.")
                        }
                    } else {
                        displayMessage("Please try again.")
                    }
                } catch (e: Exception) {
                    displayMessage("Something went wrong.")
                }
            } else {
                if (error is NoConnectionError) {
                    displayMessage(getString(R.string.oops_connect_your_internet))
                } else if (error is NetworkError) {
                    displayMessage(getString(R.string.oops_connect_your_internet))
                } else if (error is TimeoutError) {
                    resetpassword()
                }
            }
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                // headers["X-localization"] = SharedHelper.getKey(context, "lang")
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Content-Type"] = "application/json"
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    fun findViewById() {
        pinView = findViewById<View>(R.id.pinview) as Pinview
        ll_reset_password = findViewById<View>(R.id.ll_reset_password) as LinearLayout
        ll_forgot_password = findViewById<View>(R.id.ll_forgot_password) as LinearLayout
        resendOTP = findViewById<View>(R.id.resend_timer) as NTTextView
        resent_tv = findViewById<View>(R.id.resend_timer1) as NTTextView
        send_via_mail = findViewById<View>(R.id.send_via_mail) as NTTextView
        profile_title = findViewById<View>(R.id.profile_title) as NTBoldTextView
        show_hide_new_passowrd = findViewById<View>(R.id.show_hide_new_passowrd) as ImageView
        show_hide_confirm_passowrd = findViewById<View>(R.id.show_hide_confirm_passowrd) as ImageView
        timer()

        pinView!!.setPinViewEventListener { pinview, fromUser ->

        }
        email = findViewById<View>(R.id.et_emailId) as EditText
        nextICON = findViewById<View>(R.id.btn_send) as NTButton
        btn_reset = findViewById<View>(R.id.btn_resend) as NTButton
        backArrow = findViewById<View>(R.id.backArrow) as ImageView
        newPassowrd = findViewById<View>(R.id.new_password) as EditText
        confirmPassword = findViewById<View>(R.id.confirm_password) as EditText
        helper = ConnectionHelper(context)
        isInternet = helper!!.isConnectingToInternet
       /* str_email = SharedHelper.getKey(this@ActivityForgotPassword, "email").toString()
        email!!.setText(str_email)*/
    }

    private fun resendOTP() {
        /*RESEND OTP API CALL  */
        if (loadingDialog != null) loadingDialog!!.showDialog()
        val `object` = JSONObject()
        try {
            `object`.put("email", SharedHelper.getKey(this@ActivityForgotPassword, "email"))
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URLHelper.RESEND_OTP, `object`, Response.Listener { response ->
            if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
            Utilities.printAPI_Response(response.toString())
            if (response != null) {
                try {
                    if (response.getString("message").equals("1", ignoreCase = true)) {
                        resendOTP!!.visibility = View.VISIBLE
                        resent_tv!!.visibility = View.GONE
                        timer()
                    } else {
                        if (response.getString("message").equals("0", ignoreCase = true)) {
                            Utilities.dispalyDialog(this@ActivityForgotPassword, resources.getString(R.string.app_name), response.getString("success"))
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        }, Response.ErrorListener { error ->
            if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
            try {
                if (error is TimeoutError) {
                    Toast.makeText(this@ActivityForgotPassword, this@ActivityForgotPassword.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show()
                } else if (error is NoConnectionError) {
                    Toast.makeText(
                            this@ActivityForgotPassword, this@ActivityForgotPassword.getString(R.string.error_no_network), Toast.LENGTH_LONG).show()
                } else if (error is AuthFailureError) {
                    Toast.makeText(this@ActivityForgotPassword, this@ActivityForgotPassword.getString(R.string.error_auth_failure), Toast.LENGTH_LONG).show()
                } else if (error is ServerError) {
                    Toast.makeText(this@ActivityForgotPassword, this@ActivityForgotPassword.getString(R.string.error_server_connection), Toast.LENGTH_LONG).show()
                } else if (error is NetworkError) {
                    Toast.makeText(this@ActivityForgotPassword, this@ActivityForgotPassword.getString(R.string.error_network), Toast.LENGTH_LONG).show()
                } else if (error is ParseError) {
                    // Toast.makeText(this@ActivityForgotPassword, this@ActivityForgotPassword.getString(R.string.error_parse), Toast.LENGTH_LONG).show()
                } else {
                    try {
                        val err_response = JSONObject(error.toString())
                        Utilities.dispalyDialog(this@ActivityForgotPassword, resources.getString(R.string.app_name), err_response.optString("message"))
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@ActivityForgotPassword, resources.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
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

    fun timer() {
        object : CountDownTimer(300000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val sec = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))

                resendOTP!!.text = "Resend OTP in " + "00 :" + String.format(" %02d", sec)
                if (sec == 1L) {
                    Handler().postDelayed({ resendOTP!!.text = "00:00" }, 1000)
                }
            }

            override fun onFinish() {
                resendOTP!!.text = ""
                resendOTP!!.visibility = View.GONE
                resendOTP()
            }
        }.start()
    }

    fun displayMessage(toastString: String) {
        Log.e("displayMessage", "" + toastString)
        utils.showAlert(this@ActivityForgotPassword, toastString)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onRestart() {
        super.onRestart()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onBackPressed() {
        if (fromActivity!!) {
            hideSoftKeyboard(this@ActivityForgotPassword)
            val mainIntent = Intent(this@ActivityForgotPassword, ActivitySignIn::class.java)
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(mainIntent)
            finish()
        } else {
            email!!.isFocusable = false
            email!!.isFocusableInTouchMode = false
            email!!.isClickable = false
            val intentProfile = Intent(this@ActivityForgotPassword, ActivityProfile::class.java)
            intentProfile.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intentProfile.putExtra("tag", "edit")
            startActivity(intentProfile)
        }
    }

    fun ShowHidePass(view: View) {
        if (view.id == R.id.show_hide_new_passowrd) {
            if (newPassowrd!!.transformationMethod == PasswordTransformationMethod.getInstance()) {
                show_hide_new_passowrd!!.setImageResource(R.drawable.ic_icon_eye_show)
                //Show Password
                newPassowrd!!.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                show_hide_new_passowrd!!.setImageResource(R.drawable.ic_eye_icon_hide)
                newPassowrd!!.setSelection(newPassowrd!!.text.length)

                //Hide Password
                newPassowrd!!.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        } else if (view.id == R.id.show_hide_confirm_passowrd) {
            if (confirmPassword!!.transformationMethod == PasswordTransformationMethod.getInstance()) {
                show_hide_confirm_passowrd!!.setImageResource(R.drawable.ic_icon_eye_show)
                //Show Password
                confirmPassword!!.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                show_hide_confirm_passowrd!!.setImageResource(R.drawable.ic_eye_icon_hide)
                confirmPassword!!.setSelection(confirmPassword!!.text.length)

                //Hide Password
                confirmPassword!!.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        }
    }

    companion object {

        fun hideSoftKeyboard(activity: Activity) {
           /* val inputMethodManager = activity.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus!!.windowToken, 0)*/
            val view = activity!!.currentFocus
            if (view != null) {
                val inputManager = activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inputManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
            }
        }
    }
}