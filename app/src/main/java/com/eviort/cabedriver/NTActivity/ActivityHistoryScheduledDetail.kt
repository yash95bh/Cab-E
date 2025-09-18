package com.eviort.cabedriver.NTActivity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.eviort.cabedriver.NTApplication
import com.eviort.cabedriver.NTCustomView.*
import com.eviort.cabedriver.NTHelper.LoadingDialog
import com.eviort.cabedriver.NTHelper.SharedHelper
import com.eviort.cabedriver.NTHelper.URLHelper
import com.eviort.cabedriver.NTUtilites.Utilities
import com.eviort.cabedriver.R
import org.json.JSONObject
import java.util.*

class ActivityHistoryScheduledDetail : AppCompatActivity(), View.OnClickListener {
    var backArrow: ImageView? = null
    var lnrInvoice: LinearLayout? = null
    var booking_return: NTButton? = null
    var booking_same: NTButton? = null
    var tag: String? = ""
    var jsonObject: JSONObject? = null
    var loadingDialog: LoadingDialog? = null
    var context: Context = this@ActivityHistoryScheduledDetail
    var activity: Activity = this@ActivityHistoryScheduledDetail
    var utils = Utilities()
    var tripProviderRating: RatingBar? = null
    var tripProviderImg: NTCircularImageView? = null
    var tripProviderName: NTBoldTextView? = null
    var tripComments: NTBoldTextView? = null
    var payment_mode: NTBoldTextView? = null
    var tv_totalFare: NTTextView? = null
    var tv_total_cash: NTTextView? = null
    var tv_create_time: NTTextView? = null
    var cancel: NTTextView? = null
    var tv_finish_time: NTTextView? = null
    var tv_total_bill: NTTextView? = null
    var tv_status: NTTextView? = null
    var tv_distance: NTTextView? = null
    var booking_id: NTTextView? = null
    var tv_service_name: NTTextView? = null
    var tripSource: NTTextView? = null
    var tripDestination: NTTextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history_scheduled_detail)
        findViewByIdAndInitialize()
        try {
            val intent = intent
            val post_details = intent.getStringExtra("post_value")
            tag = intent.getStringExtra("tag")
            jsonObject = JSONObject(post_details)
        } catch (e: Exception) {
            jsonObject = null
        }
        getPastTripDetail()
        /* if (jsonObject != null) {

            if (tag.equalsIgnoreCase("past_trips")) {

                getPastTripDetail();

            } else {

                getUpcomingDetails();
            }
        }*/
    }

    private fun findViewByIdAndInitialize() {
        backArrow = findViewById<View>(R.id.backArrow) as ImageView
        tripSource = findViewById<View>(R.id.tripSource) as NTTextView
        tripDestination = findViewById<View>(R.id.tripDestination) as NTTextView
        booking_id = findViewById<View>(R.id.tv_booking_id) as NTTextView
        tv_service_name = findViewById<View>(R.id.car_name) as NTTextView
        cancel = findViewById<View>(R.id.cancel) as NTTextView
        tv_create_time = findViewById<View>(R.id.tv_create_time) as NTTextView
        backArrow!!.setOnClickListener(this)
        cancel!!.setOnClickListener(this)
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

    fun getPastTripDetail() {
        loadingDialog = LoadingDialog(activity)
        if (loadingDialog != null) loadingDialog!!.showDialog()
        Utilities.PrintAPI_URL(URLHelper.PAST_TRIP_DETAIL + jsonObject!!.optString("id"), "GET")
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URLHelper.PAST_TRIP_DETAIL + jsonObject!!.optString("id"), null, Response.Listener { response ->
            Utilities.printAPI_Response(response.toString())
            if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
            if (response != null) {
                tripSource!!.text = response.optString("s_address")
                tripDestination!!.text = response.optString("d_address")
                booking_id!!.text = response.optString("booking_id")
                tv_service_name!!.text = response.optString("name")
                // Picasso.with(context).load(response.optString("avatar")).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(tripProviderImg);
                tv_create_time!!.text = Utilities.parseDateMonth(response.optString("created_at"))
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
                //  headers["X-localization"] = SharedHelper.getKey(context, "lang")
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    fun getUpcomingDetails() {
        loadingDialog = LoadingDialog(activity)
        if (loadingDialog != null) loadingDialog!!.showDialog()
        Utilities.PrintAPI_URL(URLHelper.UPCOMING_TRIP_DETAILS + "?request_id=" + jsonObject!!.optString("id"), "GET")
        val jsonArrayRequest: JsonArrayRequest = object : JsonArrayRequest(URLHelper.UPCOMING_TRIP_DETAILS + "?request_id=" + jsonObject!!.optString("id"), Response.Listener { response ->
            Utilities.printAPI_Response(response.toString())
            //                if (response != null && response.length() > 0) {
//                    if (response.optJSONObject(0) != null) {
//                        Picasso.with(activity).load(response.optJSONObject(0).optString("static_map")).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(tripImg);
////                    tripDate.setText(response.optJSONObject(0).optString("assigned_at"));
//                        paymentType.setText(response.optJSONObject(0).optString("payment_mode"));
//                        String form = response.optJSONObject(0).optString("schedule_at");
//                        JSONObject providerObj = response.optJSONObject(0).optJSONObject("provider");
//                        if (response.optJSONObject(0).optString("booking_id") != null &&
//                                !response.optJSONObject(0).optString("booking_id").equalsIgnoreCase("")) {
//                            booking_id.setText(response.optJSONObject(0).optString("booking_id"));
//                        }
//                        if (providerObj != null) {
//                            driver = new Driver();
//                            driver.setFname(providerObj.optString("first_name"));
//                            driver.setLname(providerObj.optString("last_name"));
//                            driver.setMobile(providerObj.optString("mobile"));
//                            driver.setEmail(providerObj.optString("email"));
//                            driver.setImg(providerObj.optString("avatar"));
//                            driver.setRating(providerObj.optString("rating"));
//                        }
//                        try {
//                            tripDate.setText(getDate(form) + "th " + getMonth(form) + " " + getYear(form) + "\n" + getTime(form));
//                        } catch (ParseException e) {
//                            e.printStackTrace();
//                        }
//                        if (response.optJSONObject(0).optString("payment_mode").equalsIgnoreCase("CASH")) {
//                            if (response.optJSONObject(0).optString("payment_id").equalsIgnoreCase("WALLET")) {
//                                paymentType.setText("WALLET");
//                                paymentTypeImg.setImageResource(R.drawable.wallet_icon);
//                            } else {
//                                paymentTypeImg.setImageResource(R.drawable.money_icon);
//                            }
//                        } else {
//                            paymentTypeImg.setImageResource(R.drawable.visa);
//                        }
//
//                        if (response.optJSONObject(0).optJSONObject("provider") != null) {
//                            if (response.optJSONObject(0).optJSONObject("provider").optString("avatar") != null)
//                                Picasso.with(activity).load(URLHelper.base + "storage/" + response.optJSONObject(0).optJSONObject("provider").optString("avatar"))
//                                        .placeholder(R.drawable.car_select).error(R.drawable.car_select).into(tripProviderImg);
//
//                            tripProviderRating.setRating(Float.parseFloat(response.optJSONObject(0).optJSONObject("provider").optString("rating")));
//                            tripProviderName.setText(response.optJSONObject(0).optJSONObject("provider").optString("first_name") + " " + response.optJSONObject(0).optJSONObject("provider").optString("last_name"));
//                        }
//
//                        if (response.optJSONObject(0).optString("s_address") == null || response.optJSONObject(0).optString("d_address") == null || response.optJSONObject(0).optString("d_address").equals("") || response.optJSONObject(0).optString("s_address").equals("")) {
//                            sourceAndDestinationLayout.setVisibility(View.GONE);
//                            viewLayout.setVisibility(View.GONE);
//                        } else {
//                            tripSource.setText(response.optJSONObject(0).optString("s_address"));
//                            tripDestination.setText(response.optJSONObject(0).optString("d_address"));
//                        }
//
//                        try {
//                            JSONObject serviceObj = response.optJSONObject(0).optJSONObject("service_type");
//                            if (serviceObj != null) {
////                            holder.car_name.setText(serviceObj.optString("name"));
//                                if (tag.equalsIgnoreCase("past_trips")) {
//                                    tripAmount.setText(SharedHelper.getKey(context, "currency") + serviceObj.optString("price"));
//                                } else {
//                                    tripAmount.setVisibility(View.GONE);
//                                }
//                                Picasso.with(activity).load(serviceObj.optString("image"))
//                                        .placeholder(R.drawable.loading).error(R.drawable.loading)
//                                        .into(tripProviderImg);
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//
//
//                    }
//                    if ((loadingDialog != null) && (loadingDialog.isShowing()))
//                        loadingDialog.hideDialog();
//                    parentLayout.setVisibility(View.VISIBLE);
//
//                }
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
            R.id.cancel -> showCancelAlert()
            else -> {
            }
        }
    }

    private fun showCancelAlert() {
        val builder4 = AlertDialog.Builder(activity)
        val inflater4 = activity.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout4 = inflater4.inflate(R.layout.layout_alert_dialog, null)
        builder4.setCancelable(false)
        builder4.setView(layout4)
        builder4.create()
        val alertDialog4 = builder4.create()
        val tv_alert_title2 = layout4.findViewById<View>(R.id.tv_alert_title) as NTTextView
        val tv_alert_desc2 = layout4.findViewById<View>(R.id.tv_alert_desc) as NTTextView
        val tv_alert_positive = layout4.findViewById<View>(R.id.tv_alert_positive) as NTTextView
        val tv_alert_negative = layout4.findViewById<View>(R.id.tv_alert_negative) as NTTextView
        tv_alert_title2.text = activity.getString(R.string.cancel_ride)
        tv_alert_desc2.text = activity.getString(R.string.cancel_ride_desc)
        tv_alert_positive.text = activity.getString(R.string.ok)
        tv_alert_negative.text = activity.getString(R.string.cancel)
        tv_alert_positive.setOnClickListener {
            showreasonDialog()
            alertDialog4.dismiss()
        }
        tv_alert_negative.setOnClickListener { alertDialog4.dismiss() }
        alertDialog4.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog4.window!!.attributes.windowAnimations = R.style.dialog_animation
        alertDialog4.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog4.show()
    }

    private fun showreasonDialog() {
        val builder = AlertDialog.Builder(activity, R.style.NTDialogTheme)
        val view = LayoutInflater.from(activity).inflate(R.layout.cancel_sch_dialog, null)
        val reasonEtxt = view.findViewById<View>(R.id.reason_etxt) as NTEditText
        val submitBtn = view.findViewById<View>(R.id.submit_btn) as NTTextView
        val cancelBtn = view.findViewById<View>(R.id.dont_cancel) as NTTextView
        builder //.setIcon(R.mipmap.ic_launcher)
                //.setTitle(R.string.app_name)
                .setView(view)
                .setCancelable(true)
        val alert = builder.create()
        submitBtn.setOnClickListener {
            cancelRequest(reasonEtxt.text.toString())
            alert.dismiss()
        }
        cancelBtn.setOnClickListener { alert.dismiss() }
        alert.show()
    }

    fun cancelRequest(reason: String?) {
        if (loadingDialog != null) loadingDialog!!.showDialog()
        val `object` = JSONObject()
        try {
            `object`.put("request_id", jsonObject!!.optString("id"))
            `object`.put("cancel_reason", reason)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Utilities.PrintAPI_URL(URLHelper.CANCEL_TRIP, `object`.toString())
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URLHelper.CANCEL_TRIP, `object`, Response.Listener { response ->
            Utilities.printAPI_Response(response.toString())
            val mainIntent = Intent(activity, MainActivity::class.java)
            mainIntent.putExtra("message", response.optString("message"))
            activity.startActivity(mainIntent)
            activity.overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
            //activity.finish();
            if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
        }, Response.ErrorListener { error ->
            if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
            try {
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
                                    utils.showAlert(activity, errorObj.optString("message"))
                                } catch (e: Exception) {
                                    utils.showAlert(activity, activity.resources.getString(R.string.something_went_wrong))
                                }
                            } else if (response.statusCode == 401) {
//                            refreshAccessToken("PAST_TRIPS");
                            } else if (response.statusCode == 422) {
                                json = NTApplication.trimMessage(String(response.data))
                                if (json !== "" && json != null) {
                                    utils.showAlert(activity, json)
                                } else {
                                    utils.showAlert(activity, activity.resources.getString(R.string.please_try_again))
                                }
                            } else if (response.statusCode == 503) {
                                utils.showAlert(activity, activity.resources.getString(R.string.server_down))
                            } else {
                                utils.showAlert(activity, activity.resources.getString(R.string.please_try_again))
                            }
                        } catch (e: Exception) {
                            utils.showAlert(activity, activity.resources.getString(R.string.something_went_wrong))
                        }
                    } else {
                        if (error is NoConnectionError) {
                            utils.showAlert(activity, activity.resources.getString(R.string.oops_connect_your_internet))
                        } else if (error is NetworkError) {
                            utils.showAlert(activity, activity.resources.getString(R.string.oops_connect_your_internet))
                        } else if (error is TimeoutError) {
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(activity, activity.resources.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, context.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
            }
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                //headers["X-localization"] = SharedHelper.getKey(activity, "lang")
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "" + SharedHelper.getKey(activity, "token_type") + " " + SharedHelper.getKey(activity, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    /* private void BookingReturnTrip(String trip_type) {

        loadingDialog = new LoadingDialog(ActivityHistoryScheduledDetail.this);
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
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URLHelper.REPEAT_REQUEST, object, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.hideDialog();
                Utilities.PrintAPI_URL(URLHelper.REPEAT_REQUEST, response.toString());
                try {
                    if (response.getString("success").equals("1")) {
                        showCustomAlert(ActivityHistoryScheduledDetail.this, ALERT_SUCCESS, getResources().getString(R.string.app_name), response.optString("message"));
                    } else {
                        Utilities.showCustomAlert(ActivityHistoryScheduledDetail.this, ALERT_WARNING, getResources().getString(R.string.app_name), response.optString("message"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if ((loadingDialog != null) && (loadingDialog.isShowing()))
                    loadingDialog.hideDialog();
                try {
                    Utilities.commonAlert(ActivityHistoryScheduledDetail.this, error.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(ActivityHistoryScheduledDetail.this, getResources().getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show();

                }
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-localization", SharedHelper.getKey(ActivityHistoryScheduledDetail.this, "lang"));
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("Authorization", "Bearer " + SharedHelper.getKey(ActivityHistoryScheduledDetail.this, "access_token"));
                Utilities.PrintAPI_URL(URLHelper.REPEAT_REQUEST, headers.toString());
                return headers;
            }
        };
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest);
    }*/
    fun showCustomAlert(context: Context, msg_type: Int, title: String?, desc: String?) {
// public void showCustomAlert(Context context,String msg_type,String msg,String desc){
        val builder = androidx.appcompat.app.AlertDialog.Builder(context)
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
            val mainIntent = Intent(this@ActivityHistoryScheduledDetail, MainActivity::class.java)
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