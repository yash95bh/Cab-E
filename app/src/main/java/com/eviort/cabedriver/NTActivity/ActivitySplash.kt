package com.eviort.cabedriver.NTActivity

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.eviort.cabedriver.ForceUpdateChecker
import com.eviort.cabedriver.ForceUpdateChecker.OnUpdateNeededListener
import com.eviort.cabedriver.NTApplication
import com.eviort.cabedriver.NTCustomView.NTTextView
import com.eviort.cabedriver.NTHelper.LoadingDialog
import com.eviort.cabedriver.NTHelper.SharedHelper
import com.eviort.cabedriver.NTHelper.URLHelper
import com.eviort.cabedriver.NTUtilites.Utilities
import com.eviort.cabedriver.R
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class ActivitySplash : AppCompatActivity(), OnUpdateNeededListener {

    var isLogin = true
    var activity: Activity = this@ActivitySplash
    var context: Context = this@ActivitySplash
    var retryCount = 0
    var imageView: ImageView? = null
    var loadingDialog: LoadingDialog? = null
    var utils = Utilities()
    var alert: AlertDialog? = null
    var mverDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        /*SharedHelper.putKey(context, "lang", "es")
        Utilities.setLocale(context,SharedHelper.getKey(context,"lang"))*/
        SharedHelper.putKey(context, "token_type", "Bearer ")
        SharedHelper.putKey(context, "wallet_flag", "1")
        SharedHelper.putKey(context, "signup_first_start", "stop")

        imageView = findViewById<View>(R.id.activity_splash_imageView) as ImageView
        val animation = AnimationUtils.loadAnimation(applicationContext, R.anim.splash_screen_animation)
        imageView!!.startAnimation(animation)
        //  mapkey
        loadingDialog = LoadingDialog(this@ActivitySplash)

        Handler().postDelayed({
            ForceUpdateChecker.with(activity).onUpdateNeeded(this)
                    .check()

            if (!ForceUpdateChecker.appUpdate) {
                if (SharedHelper.getKey(context, "loggedIn").equals(getString(R.string.True), ignoreCase = true)) {
                    profile
                } else {
                    GoToBeginActivity()
                }
            }
        }, SPLASH_TIME_OUT.toLong())


//        if (Build.VERSION.SDK_INT > 9) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }
    }

    val profile: Unit
        get() {
            retryCount++
            Utilities.PrintAPI_URL(URLHelper.GET_PROFILE_DETAILS, "GET")
            println("DriverAPI Header: " + "X-Requested-With : XMLHttpRequest " + "Authorization : " + SharedHelper.getKey(context, "access_token"))
            val `object` = JSONObject()
            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.GET, URLHelper.GET_PROFILE_DETAILS, `object`, Response.Listener { response ->
                println("DriverAPI response : $response")
                SharedHelper.putKey(context, "id", response.optString("id"))
                SharedHelper.putKey(context, "first_name", response.optString("name"))
                SharedHelper.putKey(context, "email", response.optString("email"))
                if (response.optString("avatar").startsWith("http")) SharedHelper.putKey(context, "picture", response.optString("avatar")) else SharedHelper.putKey(context, "picture", URLHelper.base + "storage/" + response.optString("avatar"))
                SharedHelper.putKey(context, "gender", response.optString("gender"))
                SharedHelper.putKey(context, "mobile", response.optString("mobile"))
                SharedHelper.putKey(context, "approval_status", response.optString("status"))
                SharedHelper.putKey(context, "sos_number", response.optString("sos_number"))
                //                    SharedHelper.putKey(context, "wallet_balance", response.optString("wallet_balance"));
//                    SharedHelper.putKey(context, "payment_mode", response.optString("payment_mode"));
                SharedHelper.putKey(context, "loggedIn", getString(R.string.True))

                if (response.optJSONObject("service") != null) {
                    try {
                        val service = response.optJSONObject("service")
                        if (service.optJSONObject("service_type") != null) {
                            val serviceType = service.optJSONObject("service_type")
                            SharedHelper.putKey(context, "service", serviceType.optString("name"))
                            SharedHelper.putKey(context, "service_type", serviceType.optString("id"))
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                if (response.optString("status").equals("new", ignoreCase = true)) {
//                    Intent intent = new Intent(activity, WaitingForApproval.class);
                    val intent = Intent(activity, MainActivity::class.java)
                    activity.startActivity(intent)
                } else {
                    GoToMainActivity()
                }

            }, Response.ErrorListener { error ->
                try {
                    if (retryCount < 5) {
                        profile
                    } else {
                        GoToBeginActivity()
                    }
                    var json: String? = null
                    var Message: String
                    val response = error.networkResponse
                    if (response != null && response.data != null) {
                        try {
                            val errorObj = JSONObject(String(response.data))
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
//                                    Utilities.showToast(activity, errorObj.optString("message"))
                                } catch (e: Exception) {
//                                    Utilities.showToast(activity, getString(R.string.something_went_wrong))
                                }
                            } else if (response.statusCode == 401) {
                                SharedHelper.putKey(context, "loggedIn", getString(R.string.False))
                                // if (retryCount > 5)
                                //  GoToBeginActivity();
                            } else if (response.statusCode == 422) {
                                json = NTApplication.trimMessage(String(response.data))
                                if (json !== "" && json != null) {
//                                    Utilities.showToast(activity, json)
                                } else {
//                                    Utilities.showToast(activity, getString(R.string.please_try_again))
                                }
                            } else if (response.statusCode == 503) {
//                                Utilities.showToast(activity, getString(R.string.server_down))
                            }
                        } catch (e: Exception) {
//                            Utilities.showToast(activity, getString(R.string.something_went_wrong))
                        }
                    } else {
                        if (error is NoConnectionError) {
//                            Utilities.showToast(activity, getString(R.string.oops_connect_your_internet))
                        } else if (error is NetworkError) {
//                            Utilities.showToast(activity, getString(R.string.oops_connect_your_internet))
                        } else if (error is TimeoutError) {
                            profile
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
                }
            }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    //headers["X-localization"] = SharedHelper.getKey(context, "lang")
                    headers["X-Requested-With"] = "XMLHttpRequest"
                    headers["Authorization"] = "Bearer " + SharedHelper.getKey(context, "access_token")
                    headers.toString()
                    return headers
                }
            }
            NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
        }

    private val mapkey: Unit
        private get() {
            Utilities.PrintAPI_URL(URLHelper.GET_MAP_KEY, "GET")
            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(URLHelper.GET_MAP_KEY, null, Response.Listener { response ->
                Utilities.printAPI_Response(response.toString())
                if (response != null) {
                    Log.e("Mapkey", response.optString("android_key"))
                    SharedHelper.putKey(context, "map_key", response.optString("android_key"))
                }
            }, Response.ErrorListener { error ->
                Utilities.printAPI_Response(error.toString())
                try {
                    if (error is TimeoutError) {
                        Toast.makeText(context, context.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show()
                    } else if (error is NoConnectionError) {
                        Toast.makeText(context, context.getString(R.string.error_no_network), Toast.LENGTH_LONG).show()
                    } else if (error is AuthFailureError) {
                        Toast.makeText(context, context.getString(R.string.error_auth_failure), Toast.LENGTH_LONG).show()
                    } else if (error is ServerError) {
                        Toast.makeText(context, context.getString(R.string.error_server_connection), Toast.LENGTH_LONG).show()
                    } else if (error is NetworkError) {
                        Toast.makeText(context, context.getString(R.string.error_network), Toast.LENGTH_LONG).show()
                    } else if (error is ParseError) {
                        //Toast.makeText(context, context.getString(R.string.error_parse), Toast.LENGTH_LONG).show()
                    }
                    GoToBeginActivity()
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this@ActivitySplash, resources.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
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

    private val authDetails: Unit
        private get() {
            val `object` = JSONObject()
            try {
                `object`.put("device_id", "")
                `object`.put("device_type", "")
                `object`.put("device_token", "")
                `object`.put("email", "")
                `object`.put("password", "")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            Utilities.PrintAPI_URL(URLHelper.GET_OAUTH_TOKEN, "POST")
            if (loadingDialog != null) loadingDialog!!.showDialog()
            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URLHelper.GET_OAUTH_TOKEN, `object`, Response.Listener { response ->
                Utilities.printAPI_Response(response.toString())
                SharedHelper.putKey(context, "token_type", response.optString("token_type"))
                SharedHelper.putKey(context, "access_token", response.optString("access_token"))
                SharedHelper.putKey(context, "refresh_token", response.optString("refresh_token"))
            }, Response.ErrorListener { error ->
                try {
                    if (error is TimeoutError) {
                        Toast.makeText(context, context.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show()
                    } else if (error is NoConnectionError) {
                        Toast.makeText(context, context.getString(R.string.error_no_network), Toast.LENGTH_LONG).show()
                    } else if (error is AuthFailureError) {
                        Toast.makeText(context, context.getString(R.string.error_auth_failure), Toast.LENGTH_LONG).show()
                    } else if (error is ServerError) {
                        Toast.makeText(context, context.getString(R.string.error_server_connection), Toast.LENGTH_LONG).show()
                    } else if (error is NetworkError) {
                        Toast.makeText(context, context.getString(R.string.error_network), Toast.LENGTH_LONG).show()
                    } else if (error is ParseError) {
                        //Toast.makeText(context, context.getString(R.string.error_parse), Toast.LENGTH_LONG).show()
                    } else {
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
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

    private fun GoToMainActivity() {
        val mainIntent = Intent(activity, MainActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(mainIntent)
        activity.finish()
    }

    private fun GoToBeginActivity() {
        val mainIntent = Intent(activity, ActivityBegin::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(mainIntent)
        activity.finish()
    }

    override fun onResume() {
        super.onResume()
    }

    companion object {
        private const val SPLASH_TIME_OUT = 4000
    }

    override fun onUpdateNeeded(updateUrl: String?) {
        versionAlert(activity, updateUrl)
    }

    fun versionAlert(mContext: Context, updateUrl: String?) {
        try {
            if (mverDialog != null) mverDialog!!.dismiss()
            val view = View.inflate(mContext, R.layout.netcon_lay, null)
            mverDialog =
                    Dialog(mContext, R.style.NewDialog)
            mverDialog!!.setContentView(view)
            mverDialog!!.setCancelable(false)
            if (!mverDialog!!.isShowing()) mverDialog!!.show()
            val title_text: NTTextView = mverDialog!!.findViewById(R.id.title_text) as NTTextView
            val message_text: NTTextView =
                    mverDialog!!.findViewById(R.id.message_text) as NTTextView
            val button_success =
                    mverDialog!!.findViewById(R.id.button_success) as Button
            val button_failure =
                    mverDialog!!.findViewById(R.id.button_failure) as Button
            val view_center_buttons =
                    mverDialog!!.findViewById(R.id.view_center_buttons) as View
            view_center_buttons.visibility = View.GONE
            button_failure.visibility = View.GONE
            title_text.setText("" + activity.resources.getString(R.string.version_up_title))
            message_text.setText("" + activity.resources.getString(R.string.version_up_message))
            button_success.text = "" + activity.resources.getString(R.string.version_up_now)
            button_failure.text = "" + activity.resources.getString(R.string.version_up_later)
            button_success.setOnClickListener { // TODO Auto-generated method stub
                //                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + "com.unicotaxi.driver"));
                ////                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + mContext.getPackageName()));
                //                    mContext.startActivity(intent);
                redirectStore(updateUrl)
            }
            button_failure.setOnClickListener { // TODO Auto-generated method stub
                mverDialog!!.dismiss()
                finish()
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }

    private fun redirectStore(updateUrl: String?) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }
}