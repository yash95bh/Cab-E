package com.eviort.cabedriver.NTActivity

//import com.google.firebase.iid.FirebaseInstanceId
import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.eviort.cabedriver.NTApplication
import com.eviort.cabedriver.NTCustomView.NTButton
import com.eviort.cabedriver.NTCustomView.NTButtonBold
import com.eviort.cabedriver.NTCustomView.NTCircularImageView
import com.eviort.cabedriver.NTCustomView.NTEditText
import com.eviort.cabedriver.NTCustomView.NTTextView
import com.eviort.cabedriver.NTFragment.FragmentHome
import com.eviort.cabedriver.NTHelper.AppHelper
import com.eviort.cabedriver.NTHelper.ConnectionHelper
import com.eviort.cabedriver.NTHelper.LoadingDialog
import com.eviort.cabedriver.NTHelper.SharedHelper
import com.eviort.cabedriver.NTHelper.URLHelper
import com.eviort.cabedriver.NTHelper.VolleyMultipartRequest
import com.eviort.cabedriver.NTUtilites.Utilities
import com.eviort.cabedriver.R
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.hbb20.CountryCodePicker
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_signin.*
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import java.util.regex.Pattern

class ActivitySignUp : AppCompatActivity() {
    var context: Context = this@ActivitySignUp
    var TAG = "ActivitySignUp"
    var signup_username: NTEditText? = null
    var signup_password: NTEditText? = null
    var signup_confirm_password: NTEditText? = null
    var signup_mail: NTEditText? = null
    var signup_phone: NTEditText? = null
    var signup_firstname: NTEditText? = null
    var signup_lastname: NTEditText? = null
    var signup_refer_code: NTEditText? = null
    var dial_code: CountryCodePicker? = null
    var tv_otp: NTButton? = null
    var sign_up_next_btn: NTButtonBold? = null
    var tv_signUp: NTButton? = null
    var btn_terms: NTButton? = null
    var resend_timer: NTTextView? = null
    var txt_resendotp: NTTextView? = null
    var otp_next: LinearLayout? = null
    var lnrTitle: LinearLayout? = null
    var signup_next: RelativeLayout? = null
    var rlt_layout: RelativeLayout? = null
    var signin_otp_layout: RelativeLayout? = null
    var show_hide_signin_passowrd: ImageView? = null
    var show_hide_confirm_passowrd: ImageView? = null
    var loadingDialog: LoadingDialog? = null
    var imgBack: ImageView? = null
    var backArrow: ImageView? = null
    var utils = Utilities()
    var device_token: String? = null
    var device_UDID: String? = null
    var isInternet: Boolean? = null
    var helper: ConnectionHelper? = null
    var signinfrommain: Boolean? = null
    var ll_signup: LinearLayout? = null
    var str_getotp = ""
    var str_otp = ""
    var txt_timer: NTTextView? = null
    var text1: NTEditText? = null
    var text2: NTEditText? = null
    var text3: NTEditText? = null
    var text4: NTEditText? = null
    var text5: NTEditText? = null
    var text6: NTEditText? = null
    var check_tc: CheckBox? = null

    var progile_img: NTCircularImageView? = null
    var selectedImageUri: Uri? = null
    var selectedImageBitmap: Bitmap? = null
    var imageFrom: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        device_UDID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        loadingDialog = LoadingDialog(this@ActivitySignUp)
        //        isInternet = helper.isConnectingToInternet();
        findViewElements()
        GetToken()


        imgBack!!.setOnClickListener {
            if (otp_next!!.isVisible) {
                signup_next!!.visibility = View.VISIBLE
                ll_signup!!.visibility = View.VISIBLE
                lnrTitle!!.visibility = View.VISIBLE
                otp_next!!.visibility = View.GONE
                //   rlt_layout!!.visibility=View.GONE
                signin_otp_layout!!.visibility = View.GONE
            } else {
                onBackPressed()
            }
        }

        backArrow!!.setOnClickListener {
            if (signin_otp_layout!!.isVisible) {
                signup_next!!.visibility = View.VISIBLE
                ll_signup!!.visibility = View.VISIBLE
                lnrTitle!!.visibility = View.VISIBLE
                otp_next!!.visibility = View.GONE
                //   rlt_layout!!.visibility=View.GONE
                signin_otp_layout!!.visibility = View.GONE
            } else {
                onBackPressed()
            }
        }

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


        tv_signUp!!.setOnClickListener {
            val ps = Pattern.compile(".*[0-9].*")
            val firstName = ps.matcher(signup_firstname!!.text.toString())
            val lastName = ps.matcher(signup_lastname!!.text.toString())
            if (signup_firstname!!.text.toString() == "") {
                signup_firstname!!.error = getString(R.string.first_name_empty)
            } else if (signup_lastname!!.text.toString() == "") {
                signup_lastname!!.error = getString(R.string.last_name_empty)
            } else if (signup_mail!!.text.toString().isEmpty() || signup_mail!!.text.toString()
                    .equals(getString(R.string.sample_mail_id), ignoreCase = true)
            ) {
                signup_mail!!.error = getString(R.string.email_validation)
            } else if (!Utilities.isValidEmail(signup_mail!!.text.toString())) {
                signup_mail!!.error = getString(R.string.not_valid_email)
            } else if (!android.util.Patterns.PHONE.matcher(signup_phone!!.text.toString())
                    .matches()
            ) {
                signup_phone!!.error = getString(R.string.not_valid_mobile)
            } else if (signup_phone!!.text.toString() == "") {
                signup_phone!!.error = getString(R.string.mobile_number_empty)
            } else if (!check_tc!!.isChecked) {
                Toast.makeText(context, getString(R.string.terms_conditon_msg), Toast.LENGTH_LONG)
                    .show()
            }
            /*else if (imageFrom == ""){
                Toast.makeText(context,getString(R.string.profile_img_err), Toast.LENGTH_LONG).show()
            }*/
            else {
                SharedHelper.putKey(context, "signup_number", signup_phone!!.text.toString())
                SharedHelper.putKey(
                    context,
                    "dial_code",
                    dial_code!!.selectedCountryCodeWithPlus.toString()
                )
                sendotp()
            }

            //getSignUpData();
        }


        sign_up_next_btn!!.setOnClickListener {
            str_getotp =
                text1!!.text.toString() + text2!!.text.toString() + text3!!.text.toString() + text4!!.text.toString() + text5!!.text.toString() + text6!!.text.toString()

            if (str_otp == str_getotp) {
//                dpWithSignup()
                newSignup()
            } else if (str_getotp!!.length < 6) {
                Toast.makeText(context, "Please Enter OTP", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(context, "OTP not matched", Toast.LENGTH_LONG).show()
            }
        }

        btn_terms!!.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(URLHelper.TERMS_URL))
            startActivity(browserIntent)
        }
        show_hide_confirm_passowrd!!.setOnClickListener { v -> ShowHidePass(v) }
        show_hide_signin_passowrd!!.setOnClickListener { v -> ShowHidePass(v) }

        progile_img!!.setOnClickListener {
            selectImage()
        }
    }

    private fun selectImage() {
        val options = arrayOf<CharSequence>(
            getString(R.string.take_photo),
            getString(R.string.gal_txt),
            getString(R.string.cancel)
        )
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.add_photo))
        builder.setItems(options) { dialog, item ->
            if (options[item] == getString(R.string.take_photo)) {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.CAMERA,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ),
                        556
                    )
                } else {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivityForResult(intent, 556)
                    }
                }
            } else if (options[item] == getString(R.string.gal_txt)) {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 555)

            } else if (options[item] == getString(R.string.cancel)) {
                dialog.dismiss()
            }
        }
        builder.show()
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
            var number = SharedHelper.getKey(context, "signup_number")
            var dialcode = SharedHelper.getKey(context, "dial_code")
            Log.d(TAG, "value224 : $number")
            if (signinfrommain == true) {
                `object`.put("dial_code", dialcode)
                `object`.put("email", signup_mail!!.text.toString())
                `object`.put("mobile", number)
            } else {
                `object`.put("dial_code", dialcode)
                `object`.put("mobile", number)
//                `object`.put("email", signup_mail!!.text.toString())
            }
//            SharedHelper.putKey(context, "mobilenumber", et_emailId!!.getText().toString())
//             object.put("dial_code", selected_country_code);
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Utilities.PrintAPI_URL(URLHelper.LOGIN_WITH_OTP, `object`.toString())
        Log.i("SIGNUP_WITH_OTP_REQ", `object`.toString())
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, URLHelper.LOGIN_WITH_OTP, `object`,
            Response.Listener { response ->
                Log.i("SIGNUP_WITH_OTP_REQ", response.toString())
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()

                if (response.getString("success").equals("1")) {
                    try {
                        str_otp = response.getString("otp").toString()
                        if (str_otp.isNotBlank()) Toast.makeText(this, str_otp, Toast.LENGTH_SHORT)
                            .show()

                        signup_next!!.visibility = View.GONE
                        lnrTitle!!.visibility = View.GONE
                        ll_signup!!.visibility = View.GONE
                        //otp_next!!.visibility=View.VISIBLE
                        //   rlt_layout!!.visibility=View.VISIBLE
                        signin_otp_layout!!.visibility = View.VISIBLE
                        // txt_timer?.let { it1 -> reverseTimer(120, it1) }

                    } catch (e: java.lang.Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Utilities.dispalyDialog(
                        this@ActivitySignUp,
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

    private fun isValidEmail(email: String): Boolean {
        val EMAIL_PATTERN = ("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
        val pattern = Pattern.compile(EMAIL_PATTERN)
        val matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun findViewElements() {
        tv_otp = findViewById<View>(R.id.tv_otp) as NTButton
        sign_up_next_btn = findViewById<View>(R.id.sign_up_next_btn) as NTButtonBold
        tv_signUp = findViewById<View>(R.id.tv_signUp) as NTButton
        text1 = findViewById<View>(R.id.text1) as NTEditText
        text2 = findViewById<View>(R.id.text2) as NTEditText
        text3 = findViewById<View>(R.id.text3) as NTEditText
        text4 = findViewById<View>(R.id.text4) as NTEditText
        text5 = findViewById<View>(R.id.text5) as NTEditText
        text6 = findViewById<View>(R.id.text6) as NTEditText
        //    txt_timer = findViewById<View>(R.id.txt_timer) as NTTextView

        // txt_resendotp = findViewById<View>(R.id.textview_otp) as NTTextView
        //      resend_timer = findViewById<View>(R.id.resend_timer) as NTTextView
        btn_terms = findViewById<View>(R.id.btn_terms) as NTButton
        signup_firstname = findViewById<View>(R.id.signup_user_firstname) as NTEditText
        signup_lastname = findViewById<View>(R.id.signup_user_lastname) as NTEditText
        signup_password = findViewById<View>(R.id.signup_password) as NTEditText
        dial_code = findViewById<View>(R.id.dial_code) as CountryCodePicker
        signup_confirm_password = findViewById<View>(R.id.signup_confirm_password) as NTEditText
        signup_mail = findViewById<View>(R.id.signup_mail) as NTEditText
        signup_phone = findViewById<View>(R.id.signup_phone) as NTEditText
        imgBack = findViewById<View>(R.id.imgBack) as ImageView
        backArrow = findViewById<View>(R.id.backArrow) as ImageView
        show_hide_signin_passowrd = findViewById<View>(R.id.show_hide_signin_passowrd) as ImageView
        show_hide_confirm_passowrd =
            findViewById<View>(R.id.show_hide_confirm_passowrd) as ImageView

        signup_next = findViewById<View>(R.id.signup_next) as RelativeLayout
        ll_signup = findViewById<View>(R.id.ll_signup) as LinearLayout
        otp_next = findViewById<View>(R.id.otp_next) as LinearLayout
        lnrTitle = findViewById<View>(R.id.lnrTitle) as LinearLayout
        //  rlt_layout = findViewById<View>(R.id.rlt_layout) as RelativeLayout
        signin_otp_layout = findViewById<View>(R.id.signin_otp_layout) as RelativeLayout

        //  System.out.println("Country Code:" +ccp.getSelectedCountryCode());
        //ccp.getSele
        //check box
        check_tc = findViewById<View>(R.id.chk_terms) as CheckBox
        progile_img = findViewById<View>(R.id.progile_img) as NTCircularImageView
    }

    private fun getSignUpData() {

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val access_token = task.result
            SharedHelper.putKey(context, "device_token", access_token)
        })
        SharedHelper.putKey(context, "wallet_flag", "1")

//            FirebaseInstanceId.getInstance().instanceId
//                .addOnCompleteListener(OnCompleteListener { task ->
//                    if (!task.isSuccessful) {
//                        Log.w("SIGNUP", "getInstanceId failed", task.exception)
//                        return@OnCompleteListener
//                    }
//
//                    // Get new Instance ID token
//                    val access_token = task.result!!.token
//                    SharedHelper.putKey(context, "device_token", access_token)
//                })
        if (loadingDialog != null) loadingDialog!!.showDialog()
        val `object` = JSONObject()
        try {
            `object`.put("device_type", "android")
            `object`.put("device_token", SharedHelper.getKey(context, "device_token"))
            `object`.put("device_id", device_UDID)
            `object`.put("login_by", "manual")
            `object`.put("name", signup_firstname!!.text.toString())
            `object`.put("email", signup_mail!!.text.toString())
            `object`.put("otp", str_getotp)
            `object`.put("dial_code", dial_code!!.selectedCountryCodeWithPlus.toString())
            `object`.put("mobile", signup_phone!!.text.toString())
            `object`.put("last_name", signup_lastname!!.text.toString())
            `object`.put("client_id", URLHelper.client_id)
            `object`.put("client_secret", URLHelper.client_secret)
//            `object`.put("password", signup_password!!.text.toString())
//            `object`.put("password_confirmation", signup_confirm_password!!.text.toString())

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Utilities.print(URLHelper.SIGNUP, `object`.toString())
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, URLHelper.SIGNUP, `object`,
            Response.Listener { response ->
                if (loadingDialog != null && loadingDialog!!.isShowing) {
                    loadingDialog!!.hideDialog()
                }
                Utilities.printAPI_Response(response.toString())
                if (response != null) {
                    if (response.optString("success") == "1") {
                        /*  Intent otpPage = new Intent(ActivitySignIn.this, LoginWithOTP.class);
                              startActivity(otpPage);*/

                        SharedHelper.putKey(context, "loggedIn", "true")
                        SharedHelper.putKey(
                            applicationContext,
                            "token_type",
                            response.optString("token_type")
                        )
                        SharedHelper.putKey(context, "access_token", response.optString("token"))
                        SharedHelper.putKey(context, "signup_first_start", "start")
                        profile
                    } else {
                        Utilities.dispalyDialog(
                            this@ActivitySignUp,
                            resources.getString(R.string.app_name),
                            response.getString("message")
                        )
                    }
                }
            },
            Response.ErrorListener { error ->
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
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
                                        this@ActivitySignUp,
                                        errorObj.optString("message")
                                    )
                                } catch (e: java.lang.Exception) {
                                    Utilities.showToast(
                                        this@ActivitySignUp,
                                        getString(R.string.something_went_wrong)
                                    )
                                }
                            } else if (response.statusCode == 401) {
                                Utilities.showToast(
                                    this@ActivitySignUp,
                                    getString(R.string.something_went_wrong)
                                )
                                // displayMessage(errorObj.optString("message"));
                                //                            refreshAccessToken("PAST_TRIPS");
                            } else if (response.statusCode == 422) {
                                json = NTApplication.trimMessage(String(response.data))
                                if (json !== "" && json != null) {
                                    Utilities.showToast(this@ActivitySignUp, json)
                                } else {
                                    Utilities.showToast(
                                        this@ActivitySignUp,
                                        getString(R.string.please_try_again)
                                    )
                                }
                            } else if (response.statusCode == 503) {
                                Utilities.showToast(
                                    this@ActivitySignUp,
                                    getString(R.string.server_down)
                                )
                            } else {
                                Utilities.showToast(
                                    this@ActivitySignUp,
                                    getString(R.string.please_try_again)
                                )
                            }
                        } catch (e: java.lang.Exception) {
                            Utilities.showToast(
                                this@ActivitySignUp,
                                getString(R.string.something_went_wrong)
                            )
                        }
                    } else {
                        Utilities.showToast(
                            this@ActivitySignUp,
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

    private fun dpWithSignup() {
        if (loadingDialog != null) loadingDialog!!.showDialog()
        val volleyMultipartRequest: VolleyMultipartRequest = object :
            VolleyMultipartRequest(Method.POST, URLHelper.SIGNUP, Response.Listener { response ->
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()

                if (response != null) {
                    val res = String(response.data)
                    Log.v("DP_With_Signupdata", res.toString())
                    try {
                        val jsonObject = JSONObject(res)
                        if (jsonObject.optString("success") == "1") {
                            /*  Intent otpPage = new Intent(ActivitySignIn.this, LoginWithOTP.class);
                                  startActivity(otpPage);*/
                            SharedHelper.putKey(context, "loggedIn", "true")
                            SharedHelper.putKey(
                                applicationContext,
                                "token_type",
                                jsonObject.optString("token_type")
                            )
                            SharedHelper.putKey(
                                context,
                                "access_token",
                                jsonObject.optString("token")
                            )
                            SharedHelper.putKey(context, "signup_first_start", "start")
                            profile
                        } else {
                            Utilities.dispalyDialog(
                                this@ActivitySignUp,
                                resources.getString(R.string.app_name),
                                jsonObject.getString("message")
                            )
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }

            }, Response.ErrorListener { error ->
                try {
                    if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                    var json: String? = null
                    var Message: String
                    val response = error.networkResponse
                    if (response != null && response.data != null) {
                        try {
                            val errorObj = JSONObject(String(response.data))
                            Log.v("Signup_error", errorObj.toString())
                            Log.v("Signup_error1", errorObj.optString("message"))
                            if (response.javaClass == TimeoutError::class.java) {
                                return@ErrorListener
                            }
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    Toast.makeText(
                                        this,
                                        errorObj.optString("message"),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        this,
                                        getString(R.string.something_went_wrong),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else if (response.statusCode == 401) {
                            } else if (response.statusCode == 422) {
                                json = NTApplication.trimMessage(String(response.data))
                                if (json !== "" && json != null) {
                                    Toast.makeText(this, json, Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(
                                        this,
                                        getString(R.string.please_try_again),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else if (response.statusCode == 503) {
                                Toast.makeText(
                                    this,
                                    getString(R.string.server_down),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this,
                                    getString(R.string.please_try_again),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(
                                this,
                                getString(R.string.something_went_wrong),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        if (error is NoConnectionError) {
                            Toast.makeText(
                                this,
                                getString(R.string.oops_connect_your_internet),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (error is NetworkError) {
                            Toast.makeText(
                                this,
                                getString(R.string.oops_connect_your_internet),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else if (error is TimeoutError) {

                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(
                        this,
                        getString(R.string.something_went_wrong),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            public override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["otp"] = str_getotp
                params["device_type"] = "android"
                params["device_token"] = SharedHelper.getKey(context, "device_token").toString()
                params["device_id"] = device_UDID.toString()
                params["login_by"] = "manual"
                params["name"] = signup_firstname!!.text.toString()
                params["last_name"] = signup_lastname!!.text.toString()
                params["email"] = signup_mail!!.text.toString()
                params["dial_code"] = dial_code!!.selectedCountryCodeWithPlus.toString()
                params["mobile"] = signup_phone!!.text.toString()
                params["client_id"] = URLHelper.client_id.toString()
                params["client_secret"] = URLHelper.client_secret.toString()

                Log.v("SignupData_params", params.toString())
                return params
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-Requested-With"] = "XMLHttpRequest"
                return headers
            }

            @Throws(AuthFailureError::class)
            override fun getByteData(): Map<String, DataPart> {
                val params: MutableMap<String, DataPart> = HashMap()
                params["avatar"] = DataPart(
                    "userImage.jpg",
                    AppHelper.getFileDataFromDrawable(progile_img!!.drawable),
                    "image/jpeg"
                )
                Log.v("SignupData_params", params.toString())
                return params
            }
        }
        NTApplication.getInstance().addToRequestQueue(volleyMultipartRequest)
    }

    private fun newSignup() {
        if (loadingDialog != null) loadingDialog!!.showDialog()
        val `object` = JSONObject()
        try {
            `object`.put("otp", str_getotp)
            `object`.put("device_type", "android")
            `object`.put("device_token", SharedHelper.getKey(context, "device_token").toString())
            `object`.put("login_by", "manual")
            `object`.put("name", signup_firstname!!.text.toString())
            `object`.put("last_name", signup_lastname!!.text.toString())
            `object`.put("email", signup_mail!!.text.toString())
            `object`.put("dial_code", dial_code!!.selectedCountryCodeWithPlus.toString())
            `object`.put("mobile", signup_phone!!.text.toString())
            `object`.put("client_id", URLHelper.client_id.toString())
            `object`.put("client_secret", URLHelper.client_secret.toString())
            `object`.put("device_id", device_UDID)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Utilities.print(URLHelper.SIGNUP, `object`.toString() + "Value = " + `object`.toString())
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, URLHelper.SIGNUP, `object`,
            Response.Listener { response ->
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                Utilities.printAPI_Response(response.toString())
                if (response != null) {
                    Log.v("DP_With_Signupdata", response.toString())
                    try {
                        if (response.optString("success") == "1") {
                            /*  Intent otpPage = new Intent(ActivitySignIn.this, LoginWithOTP.class);
                                  startActivity(otpPage);*/
                            SharedHelper.putKey(context, "loggedIn", "true")
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
                            SharedHelper.putKey(context, "signup_first_start", "start")
                            profile
                        } else {
                            Utilities.dispalyDialog(
                                this@ActivitySignUp,
                                resources.getString(R.string.app_name),
                                response.getString("message")
                            )
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            },
            Response.ErrorListener { error ->
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                try {
                    var json: String? = null
                    var Message: String
                    val response = error.networkResponse
                    if (response != null && response.data != null) {
                        try {
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {

                            } else if (response.statusCode == 401) {
                                Utilities.showToast(
                                    this@ActivitySignUp,
                                    getString(R.string.something_went_wrong)
                                )
                                // displayMessage(errorObj.optString("message"));
                                //                            refreshAccessToken("PAST_TRIPS");
                            } else if (response.statusCode == 422) {
                                json = NTApplication.trimMessage(String(response.data))
                                if (json !== "" && json != null) {
                                    Utilities.showToast(this@ActivitySignUp, json)
                                } else {
                                    Utilities.showToast(
                                        this@ActivitySignUp,
                                        getString(R.string.please_try_again)
                                    )
                                }
                            } else if (response.statusCode == 503) {
                                Utilities.showToast(
                                    this@ActivitySignUp,
                                    getString(R.string.server_down)
                                )
                            } else {
                                Utilities.showToast(
                                    this@ActivitySignUp,
                                    getString(R.string.please_try_again)
                                )
                            }
                        } catch (e: java.lang.Exception) {
                            Utilities.showToast(
                                this@ActivitySignUp,
                                getString(R.string.something_went_wrong)
                            )
                        }
                    } else {
                        Utilities.showToast(
                            this@ActivitySignUp,
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
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    private fun login(signup_mail: String, signup_confirm_password: String) {


        if (SharedHelper.getKey(context, "device_token") == null || SharedHelper.getKey(
                context,
                "device_token"
            )!!.isEmpty()
        ) {

            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                val access_token = task.result
                SharedHelper.putKey(context, "device_token", access_token)
            })

        }
//            FirebaseInstanceId.getInstance().instanceId
//                .addOnCompleteListener(OnCompleteListener { task ->
//                    if (!task.isSuccessful) {
//                        Log.w("SIGNUP", "getInstanceId failed", task.exception)
//                        return@OnCompleteListener
//                    }
//
//                    // Get new Instance ID token
//                    val access_token = task.result!!.token
//                    SharedHelper.putKey(context, "device_token", access_token)
//                })

        if (loadingDialog != null) loadingDialog!!.showDialog()
        val `object` = JSONObject()
        try {
            `object`.put("grant_type", "password")
            `object`.put("mobile", signup_mail)
            `object`.put("otp", signup_confirm_password)
            `object`.put("scope", "")
            `object`.put("device_type", "android")
            `object`.put("device_id", device_UDID)
            `object`.put("device_token", SharedHelper.getKey(context, "device_token"))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Utilities.print(URLHelper.LOGIN, `object`.toString() + "Value = " + `object`.toString())
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, URLHelper.LOGIN, `object`,
            Response.Listener { response ->
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                Utilities.printAPI_Response(response.toString())
                if (response != null) {

                }
            },
            Response.ErrorListener { error ->
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
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
                                        this@ActivitySignUp,
                                        errorObj.optString("message")
                                    )
                                } catch (e: java.lang.Exception) {
                                    Utilities.showToast(
                                        this@ActivitySignUp,
                                        getString(R.string.something_went_wrong)
                                    )
                                }
                            } else if (response.statusCode == 401) {
                                Utilities.showToast(
                                    this@ActivitySignUp,
                                    getString(R.string.something_went_wrong)
                                )
                                // displayMessage(errorObj.optString("message"));
                                //                            refreshAccessToken("PAST_TRIPS");
                            } else if (response.statusCode == 422) {
                                json = NTApplication.trimMessage(String(response.data))
                                if (json !== "" && json != null) {
                                    Utilities.showToast(this@ActivitySignUp, json)
                                } else {
                                    Utilities.showToast(
                                        this@ActivitySignUp,
                                        getString(R.string.please_try_again)
                                    )
                                }
                            } else if (response.statusCode == 503) {
                                Utilities.showToast(
                                    this@ActivitySignUp,
                                    getString(R.string.server_down)
                                )
                            } else {
                                Utilities.showToast(
                                    this@ActivitySignUp,
                                    getString(R.string.please_try_again)
                                )
                            }
                        } catch (e: java.lang.Exception) {
                            Utilities.showToast(
                                this@ActivitySignUp,
                                getString(R.string.something_went_wrong)
                            )
                        }
                    } else {
                        Utilities.showToast(
                            this@ActivitySignUp,
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
                "DriverAPI Header: " + "X-Requested-With : XMLHttpRequest " + "Authorization : " + SharedHelper.getKey(
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
                        if ((loadingDialog != null) && (loadingDialog!!.isShowing)) loadingDialog!!.hideDialog()
                        println("DriverAPI response : $response")
                        FragmentHome.isRunning = true;
                        SharedHelper.putKey(context, "id", response.optString("id"))
                        SharedHelper.putKey(context, "first_name", response.optString("name"))
                        SharedHelper.putKey(context, "email", response.optString("email"))
                        if (response.optString("avatar").startsWith("http")) SharedHelper.putKey(
                            context,
                            "picture",
                            response.optString("avatar")
                        ) else SharedHelper.putKey(
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
                        if (response.optString("account_status")
                                .equals("onboarding", ignoreCase = true)
                        ) {

                            val intent = Intent(this@ActivitySignUp, WaitingForApproval::class.java)
                            startActivity(intent)
                        } else {
                            val intent = Intent(this@ActivitySignUp, MainActivity::class.java)
                            startActivity(intent)
                        }
                    }
                },
                object : Response.ErrorListener {
                    override fun onErrorResponse(error: VolleyError) {
                        try {
                            if ((loadingDialog != null) && (loadingDialog!!.isShowing)) loadingDialog!!.hideDialog()
                            var json: String? = null
                            var Message: String
                            val response = error.networkResponse
                            if (response != null && response.data != null) {
                                try {
                                    val errorObj = JSONObject(String(response.data))
                                    if ((response.statusCode == 400) || (response.statusCode == 405) || (response.statusCode == 500)) {
                                        try {
                                            Utilities.showToast(
                                                this@ActivitySignUp,
                                                errorObj.optString("message")
                                            )
                                        } catch (e: Exception) {
                                            Utilities.showToast(
                                                this@ActivitySignUp,
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
                                            Utilities.showToast(this@ActivitySignUp, json)
                                        } else {
                                            Utilities.showToast(
                                                this@ActivitySignUp,
                                                getString(R.string.please_try_again)
                                            )
                                        }
                                    } else if (response.statusCode == 503) {
                                        Utilities.showToast(
                                            this@ActivitySignUp,
                                            getString(R.string.server_down)
                                        )
                                    }
                                } catch (e: Exception) {
                                    Utilities.showToast(
                                        this@ActivitySignUp,
                                        getString(R.string.something_went_wrong)
                                    )
                                }
                            } else {
                                if (error is NoConnectionError) {
                                    Utilities.showToast(
                                        this@ActivitySignUp,
                                        getString(R.string.oops_connect_your_internet)
                                    )
                                } else if (error is NetworkError) {
                                    Utilities.showToast(
                                        this@ActivitySignUp,
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

    override fun onResume() {
        super.onResume()
    }

    override fun onBackPressed() {
        try {
            super@ActivitySignUp.onBackPressed()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun GetToken() {
        try {
            if (SharedHelper.getKey(context, "device_token") != "" && SharedHelper.getKey(
                    context,
                    "device_token"
                ) != null
            ) {
                device_token = SharedHelper.getKey(context, "device_token")
                Utilities.print(TAG, "GCM Registration Token: $device_token")
            } else {
                device_token = "COULD NOT GET FCM TOKEN"
                Utilities.print(TAG, "Failed to complete token refresh: $device_token")
            }
        } catch (e: Exception) {
            device_token = "COULD NOT GET FCM TOKEN"
            Utilities.print(TAG, "Failed to complete token refresh")
        }
        try {
            device_UDID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
            Utilities.print(TAG, "Device UDID:$device_UDID")
        } catch (e: Exception) {
            device_UDID = "COULD NOT GET UDID"
            e.printStackTrace()
            Utilities.print(TAG, "Failed to complete device UDID")
        }
    }

    fun ShowHidePass(view: View) {
        if (view.id == R.id.show_hide_signin_passowrd) {
            if (signup_password!!.transformationMethod == PasswordTransformationMethod.getInstance()) {
                show_hide_signin_passowrd!!.setImageResource(R.drawable.ic_icon_eye_show)
                //Show Password
                signup_password!!.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                show_hide_signin_passowrd!!.setImageResource(R.drawable.ic_eye_icon_hide)
                signup_password!!.setSelection(signup_password!!.text!!.length)

                //Hide Password
                signup_password!!.transformationMethod = PasswordTransformationMethod.getInstance()
            }
        } else if (view.id == R.id.show_hide_confirm_passowrd) {
            if (signup_confirm_password!!.transformationMethod == PasswordTransformationMethod.getInstance()) {
                show_hide_confirm_passowrd!!.setImageResource(R.drawable.ic_icon_eye_show)
                //Show Password
                signup_confirm_password!!.transformationMethod =
                    HideReturnsTransformationMethod.getInstance()
            } else {
                show_hide_confirm_passowrd!!.setImageResource(R.drawable.ic_eye_icon_hide)
                signup_confirm_password!!.setSelection(signup_confirm_password!!.text!!.length)

                //Hide Password
                signup_confirm_password!!.transformationMethod =
                    PasswordTransformationMethod.getInstance()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 555 && resultCode == RESULT_OK) {
            if (data != null) {
                imageFrom = "gallery"
                selectedImageUri = data.data
                Picasso.with(context).load(selectedImageUri).into(progile_img);
            }
        } else if (requestCode == 556 && resultCode == RESULT_OK) {
            imageFrom = "camera"
            selectedImageBitmap = data?.extras!!["data"] as Bitmap?
            progile_img!!.setImageBitmap(selectedImageBitmap)
        }
    }
}