package com.eviort.cabedriver.NTActivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.eviort.cabedriver.NTApplication
import com.eviort.cabedriver.NTCustomView.NTBoldTextView
import com.eviort.cabedriver.NTCustomView.NTButton
import com.eviort.cabedriver.NTCustomView.NTCircularImageView
import com.eviort.cabedriver.NTCustomView.NTTextView
import com.eviort.cabedriver.NTHelper.LoadingDialog
import com.eviort.cabedriver.NTHelper.SharedHelper
import com.eviort.cabedriver.NTHelper.URLHelper
import com.eviort.cabedriver.NTUtilites.Utilities
import com.eviort.cabedriver.R
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class ActivityHistoryDetail : AppCompatActivity(), View.OnClickListener {
    var backArrow: ImageView? = null
    var lnrInvoice: LinearLayout? = null
    var booking_return: NTButton? = null
    var booking_same: NTButton? = null
    var tag: String? = ""
    var help: NTTextView? = null
    var tv_get_invoice: NTTextView? = null
    var jsonObject: JSONObject? = null
    var loadingDialog: LoadingDialog? = null
    var context: Context = this@ActivityHistoryDetail
    var activity: Activity = this@ActivityHistoryDetail
    var utilities = Utilities()
    var tripProviderRating: RatingBar? = null
    var tripProviderImg: NTCircularImageView? = null
    var tripProviderName: NTBoldTextView? = null
    var tv_totalFare: NTTextView? = null
    var tv_totalAccessFee: NTTextView? = null
    var tv_coupon_savings: NTTextView? = null
    var tv_rideFare: NTTextView? = null
    var tv_total_cash: NTTextView? = null
    var pay_mode: NTTextView? = null
    var tv_create_time: NTTextView? = null
    var tv_finish_time: NTTextView? = null
    var tv_total_bill: NTTextView? = null
    var tv_status: NTTextView? = null
    var tv_distance: NTTextView? = null
    var booking_id: NTTextView? = null
    var tv_service_name: NTTextView? = null
    var tripSource: NTTextView? = null
    var tripent_at: NTTextView? = null
    var tripstart_at: NTTextView? = null
    var tripDestination: NTTextView? = null
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_detail)
        Utilities.setLocale(context!!, SharedHelper.getKey(context!!, "lang"))

        findViewByIdAndInitialize()
        try {
            val intent = intent
            val post_details = intent.getStringExtra("post_value")
            tag = intent.getStringExtra("tag")
            jsonObject = JSONObject(post_details)
            Log.i("HistoryDetailsssss",jsonObject.toString())
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.US)
                val outputFormat = SimpleDateFormat("hh:mm a", Locale.US)
//                val outputFormat1 = SimpleDateFormat("yyyy EEE d MMM, h:mm a", Locale.US)
                val outputFormat1 = SimpleDateFormat("dd-MM-yy, h:mm a", Locale.US)
                val started_at = inputFormat.parse(jsonObject!!.optString("started_at"))
                val finished_at = inputFormat.parse(jsonObject!!.optString("finished_at"))
                val created_at = inputFormat.parse(jsonObject!!.optString("created_at"))
                tripstart_at!!.text =  outputFormat.format(started_at)
                tripent_at!!.text =  outputFormat.format(finished_at)
                tv_create_time!!.text =outputFormat1.format(created_at)
            } catch (e: ParseException) {
                e.printStackTrace()
                "Invalid Date"
            }
        } catch (e: Exception) {
            jsonObject = null
        }
        if (jsonObject != null) {
            if (tag.equals("past_trips", ignoreCase = true)) {
                pastTripDetail
            } else {
                upcomingDetails
            }
        }

        tripSource!!.setOnClickListener {
            tripSource!!.isSingleLine = !tripSource!!.isSingleLine
        }
        tripDestination!!.setOnClickListener {
            tripDestination!!.isSingleLine = !tripDestination!!.isSingleLine
        }
    }

    private fun findViewByIdAndInitialize() {
        backArrow = findViewById<View>(R.id.backArrow) as ImageView
        tripSource = findViewById<View>(R.id.tripSource) as NTTextView
        tripent_at = findViewById<View>(R.id.tripent_at) as NTTextView
        tripstart_at = findViewById<View>(R.id.tripstart_at) as NTTextView
        tripDestination = findViewById<View>(R.id.tripDestination) as NTTextView
        booking_id = findViewById<View>(R.id.booking_id) as NTTextView
        tripProviderName = findViewById<View>(R.id.tripProviderName) as NTBoldTextView
        tripProviderImg = findViewById<View>(R.id.tripProviderImg) as NTCircularImageView
        tripProviderRating = findViewById<View>(R.id.tripProviderRating) as RatingBar
        tv_totalFare = findViewById<View>(R.id.tv_totalFare) as NTTextView
        tv_totalAccessFee = findViewById<View>(R.id.tv_totalAccessFee) as NTTextView
        tv_coupon_savings = findViewById<View>(R.id.tv_coupon_savings) as NTTextView
        tv_rideFare = findViewById<View>(R.id.tv_ridefare) as NTTextView
        tv_total_cash = findViewById<View>(R.id.tv_total_cash) as NTTextView
        pay_mode = findViewById<View>(R.id.pay_mode) as NTTextView
        tv_create_time = findViewById<View>(R.id.tv_create_time) as NTTextView
        tv_total_bill = findViewById<View>(R.id.tv_total) as NTTextView
        tv_status = findViewById<View>(R.id.tv_status) as NTTextView
        tv_distance = findViewById<View>(R.id.tv_distance) as NTTextView
        tv_service_name = findViewById<View>(R.id.tv_service_name) as NTTextView
        help = findViewById<View>(R.id.help) as NTTextView
        tv_get_invoice = findViewById<View>(R.id.tv_get_invoice) as NTTextView
        help!!.setOnClickListener(this)
        tv_get_invoice!!.setOnClickListener(this)
        /*   booking_return.setOnClickListener(this);
        booking_same.setOnClickListener(this);*/backArrow!!.setOnClickListener(this)
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onResume() {
        super.onResume()
    }

    val pastTripDetail: Unit
        get() {
            loadingDialog = LoadingDialog(activity)
            if (loadingDialog != null) loadingDialog!!.showDialog()
            Utilities.PrintAPI_URL(URLHelper.PAST_TRIP_DETAIL + jsonObject!!.optString("id"), "GET")
            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URLHelper.PAST_TRIP_DETAIL + jsonObject!!.optString("id"), null, Response.Listener { response ->
                Log.v("PAST_TRIP_DETAIL",URLHelper.PAST_TRIP_DETAIL + jsonObject!!.optString("id")+" .... "+response.toString())
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                if (response != null) {
                    tripSource!!.text = response.optString("s_address")
                    tripDestination!!.text = response.optString("d_address")
                    booking_id!!.text = response.optString("booking_id")
                    if (response.optString("rating") == "null") {
                        tripProviderRating!!.visibility = View.GONE
                    } else {
                        tripProviderRating!!.visibility = View.VISIBLE
                        tripProviderRating!!.rating = response.optString("rating").toFloat()
                    }
                    tripProviderName!!.text = response.optString("user_name")
                    if (!response.optString("picture").equals("")) {
                        Picasso.with(context).load(response.optString("picture"))
                                .placeholder(R.drawable.ic_driver).error(R.drawable.ic_driver)
                                .into(tripProviderImg);
                    }
                    tv_totalFare!!.text = response.optJSONObject("payment").optString("currency") + response.optJSONObject("payment").optString("tip_fare")
                    tv_totalAccessFee!!.text = response.optJSONObject("payment").optString("currency") + response.optJSONObject("payment").optString("extra_fare")
                    tv_coupon_savings!!.text = response.optJSONObject("payment").optString("currency") + "0"
                    tv_rideFare!!.text = response.optJSONObject("payment").optString("currency") + response.optJSONObject("payment").optString("base_fare")
//                    tv_total_cash!!.text = response.optJSONObject("payment").optString("currency") + response.optJSONObject("payment").optString("cash")
                    pay_mode!!.text = response.optJSONObject("payment").optString("payment_mode")
                    tv_total_bill!!.text = response.optJSONObject("payment").optString("currency") + response.optJSONObject("payment").optString("total")
                    tv_status!!.text = response.optString("status")
                    tv_distance!!.text = response.optString("distance")
                    tv_service_name!!.text = response.optString("name")

//                    try {
//                        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")
//                        val outputFormat = SimpleDateFormat("hh:mm")
//                        tripstart_at!!.text = inputFormat.parse(response.optString("started_at"))
//                            ?.let { outputFormat.format(it) }
//                        tripent_at!!.text = inputFormat.parse(response.optString("finished_at"))
//                            ?.let { outputFormat.format(it) }
//
//                        val outputFormat1 = SimpleDateFormat("yyyy EEE d MMM, h:mm a", Locale.US)
//                        val date = inputFormat.parse(response.optString("created_at"))
//                        tv_create_time!!.text = outputFormat1.format(date)
//                    } catch (e: java.lang.Exception) {
//                        e.printStackTrace()
//                    }

                }
            }, Response.ErrorListener { error ->
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
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
                                }
                            } else if (response.statusCode == 401) {
//                            refreshAccessToken("PAST_TRIPS");
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
                        }
                    } else {
                        displayMessage(getString(R.string.please_try_again))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
                }
            }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    // headers["X-localization"] = SharedHelper.getKey(context, "lang")
                    headers["X-Requested-With"] = "XMLHttpRequest"
                    headers["Authorization"] = "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token")
                    return headers
                }
            }
            NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
        }
    val upcomingDetails: Unit
        get() {
            loadingDialog = LoadingDialog(activity)
            if (loadingDialog != null) loadingDialog!!.showDialog()
            Utilities.PrintAPI_URL(URLHelper.UPCOMING_TRIP_DETAILS + "?request_id=" + jsonObject!!.optString("id"), "GET")
            val jsonArrayRequest: JsonArrayRequest = object : JsonArrayRequest(URLHelper.UPCOMING_TRIP_DETAILS + "?request_id=" + jsonObject!!.optString("id"), Response.Listener { response ->
                Utilities.printAPI_Response(response.toString())

            }, Response.ErrorListener { error ->
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
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
                                }
                            } else if (response.statusCode == 401) {
//                            refreshAccessToken("UPCOMING_TRIPS");
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
                        }
                    } else {
                        displayMessage(getString(R.string.please_try_again))
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
                    headers["Authorization"] = "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token")
                    return headers
                }
            }
            NTApplication.getInstance().addToRequestQueue(jsonArrayRequest)
        }

    fun displayMessage(msg: String?) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.backArrow -> onBackPressed()
            R.id.help -> {
                val helpSupport = Intent(Intent.ACTION_VIEW, Uri.parse(URLHelper.HELP_URL))
                startActivity(helpSupport)
            }
            R.id.tv_get_invoice -> getInvoice()
            else -> {
            }
        }
    }

    private fun getInvoice() {
        loadingDialog = LoadingDialog(activity)
        if (loadingDialog != null) loadingDialog!!.showDialog()
        val `object` = JSONObject()
        try {
            `object`.put("request_id", jsonObject!!.optString("id"))
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URLHelper.GET_INVOICE, `object`, Response.Listener { response ->
            if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
            utilities.showAlert(activity, response.optString("message"))
        }, Response.ErrorListener { error ->
            if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
            try {
                utilities.showAlert(activity, error.toString())
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(activity, resources.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
            }
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                //headers["X-localization"] = SharedHelper.getKey(activity, "lang")
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "Bearer " + SharedHelper.getKey(activity, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    /* private void BookingReturnTrip(String trip_type) {

        loadingDialog = new LoadingDialog(ActivityHistoryDetail.this);
        if (loadingDialog != null)
            loadingDialog.showDialog();
        JSONObject object = new JSONObject();
        try {

            object.put("request_id", jsonObject.optString("id"));
            object.put("trip_type", trip_type);


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Utilities.PrintAPI_URL(URLHelper.REPEAT_REQUEST, object.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.REPEAT_REQUEST, object, new com.android.volley.Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.hideDialog();
                Utilities.PrintAPI_URL(URLHelper.REPEAT_REQUEST, response.toString());
                try {
                    if (response.getString("success").equals("1")) {
                        showCustomAlert(ActivityHistoryDetail.this, ALERT_SUCCESS, getResources().getString(R.string.app_name), response.optString("message"));
                    } else {
                        Utilities.showCustomAlert(ActivityHistoryDetail.this, ALERT_WARNING, getResources().getString(R.string.app_name), response.optString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new com.android.volley.Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.hideDialog();
                try {
                    Utilities.commonAlert(ActivityHistoryDetail.this, error.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ActivityHistoryDetail.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();

                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-localization", SharedHelper.getKey(ActivityHistoryDetail.this, "lang"));
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(ActivityHistoryDetail.this, "access_token"));
                Utilities.PrintAPI_URL(URLHelper.REPEAT_REQUEST, headers.toString());
                return headers;
            }
        };
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }*/
    fun showCustomAlert(context: Context, msg_type: Int, title: String?, desc: String?) {
// public void showCustomAlert(Context context,String msg_type,String msg,String desc){
        val builder = AlertDialog.Builder(context)
        // Get the layout inflater
        val inflater = context.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.layout_custom_alert, null)
        builder.setCancelable(false)
        builder.setView(layout)
        builder.create()
        val alertDialog = builder.create()
        val imageView = layout.findViewById<View>(R.id.alert_image) as ImageView
        val alert_title = layout.findViewById<View>(R.id.alert_title) as NTBoldTextView
        val alert_desc = layout.findViewById<View>(R.id.alert_desc) as NTTextView
        val btnOk = layout.findViewById<View>(R.id.btnOk) as NTBoldTextView
        var res: Drawable? = null
        when (msg_type) {
            Utilities.ALERT_ERROR -> {
                alert_title.setTextColor(context.resources.getColor(R.color.red))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    res = context.getDrawable(R.drawable.icon_error)
                }
            }
            Utilities.ALERT_WARNING -> {
                alert_title.setTextColor(context.resources.getColor(R.color.quantum_yellow))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    res = context.getDrawable(R.drawable.icon_warning)
                }
            }
            Utilities.ALERT_SUCCESS -> {
                alert_title.setTextColor(context.resources.getColor(R.color.green))
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    res = context.getDrawable(R.drawable.icon_success)
                }
            }
        }
        imageView.setImageDrawable(res)

        /* Glide.with(this)
                .load("IMAGE URL HERE")
                .into(imageView);
*/alert_title.text = title
        alert_desc.text = desc
        btnOk.setOnClickListener {
            if (alertDialog != null && alertDialog.isShowing) alertDialog.dismiss()
            val mainIntent = Intent(this@ActivityHistoryDetail, MainActivity::class.java)
            startActivity(mainIntent)
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }
        alertDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.window!!.attributes.windowAnimations = R.style.dialog_animation
        // alertDialog.getWindow().addFlags(WindowManager.LayoutParams.DIM_AMOUNT_CHANGED);
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }
}