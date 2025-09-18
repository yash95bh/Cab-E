package com.eviort.cabedriver.NTFragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.eviort.cabedriver.NTActivity.ActivityBegin
import com.eviort.cabedriver.NTActivity.MainActivity
import com.eviort.cabedriver.NTApplication
import com.eviort.cabedriver.NTCustomView.NTBoldTextView
import com.eviort.cabedriver.NTCustomView.NTButton
import com.eviort.cabedriver.NTCustomView.NTTextView
import com.eviort.cabedriver.NTHelper.ConnectionHelper
import com.eviort.cabedriver.NTHelper.LoadingDialog
import com.eviort.cabedriver.NTHelper.SharedHelper.getKey
import com.eviort.cabedriver.NTHelper.SharedHelper.putKey
import com.eviort.cabedriver.NTHelper.URLHelper
import com.eviort.cabedriver.NTUtilites.MyAxisValueFormatter
import com.eviort.cabedriver.NTUtilites.Utilities
import com.eviort.cabedriver.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IAxisValueFormatter
import com.google.android.material.snackbar.Snackbar
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class FragmentWallet : Fragment(), View.OnClickListener {
    var activity: Activity? = null
    var mycontext: Context? = null
    var txt_see_all: LinearLayout? = null
    var cashout_details: ImageView? = null
    var driver_wallet_label: TextView? = null
    var txt_earnings_click_here: TextView? = null
    var tv_earnings_distance: NTTextView? = null
    var tv_earnings_trip_fare: NTTextView? = null
    var tv_commission_fee: NTTextView? = null
    var tv_earnigs_tips: NTTextView? = null
    var barChart: BarChart? = null
    var barData: BarData? = null
    var barDataSet: BarDataSet? = null
    var barEntries: ArrayList<*>? = null
    var today: NTBoldTextView? = null
    var historyListTodayAdapter: HistoryTodayListAdapter? = null
    var earningsAdapter: EarningsAdapter? = null
    var rcvRides: RecyclerView? = null
    var rcvtodayCompleted: RecyclerView? = null
    var isInternet: Boolean? = null
    var rootView: View? = null
    var backArrow: ImageView? = null
    var utilities = Utilities()
    var last_date: NTTextView? = null

    var date: ArrayList<*>? = null
    var earnings: ArrayList<*>? = null
    var helper: ConnectionHelper? = null
    var ic_dropdown: ImageView? = null
    var TotalEarnings = ""
    var tv_total_earnings: NTBoldTextView? = null
    var lblTarget: NTBoldTextView? = null
    var tv_earnings_detail: NTBoldTextView? = null
    var ll_earnings_detail: LinearLayout? = null
    var ll_cashout_detail: LinearLayout? = null
    var ll_earnings: LinearLayout? = null
    var lnrtimeperiod: LinearLayout? = null
    var ll_earnings_detail_all: LinearLayout? = null
    var error_title: NTTextView? = null
    var errorLayout: RelativeLayout? = null
    var sp_timeperiod: Spinner? = null
    var payout_button: NTButton? = null
    private var spinnerSelected = "0"
    private var spinnerTouch = 0
    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mycontext = context
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_wallet, container, false)
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
        FragmentHome.isRunning = false
        findViewByIdAndInitialize()
        setOnclicklisteners()

        getProviderEarnings(4.toString())
        gettodayCompletedList()
        // getEntries();
        //  getProviderEarnings();
        //  getProviderEarningsDetail();
        val adapter = ArrayAdapter.createFromResource(mycontext!!, R.array.time_period, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        val d = Date()
        val s = DateFormat.format("MMMM d, yyyy ", d.time)
        sp_timeperiod!!.adapter = adapter

//        sp_statistics.getBackground().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_ATOP);
        sp_timeperiod!!.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                if (++spinnerTouch > 1) {
                    spinnerSelected = when (i) {
                        0 -> "0"
                        1 -> "1"
                        2 -> "2"
                        3 -> "3"
                        4 -> "4"
                        else -> "0"
                    }
                }
                if (isInternet!!) {
                    getProviderEarnings(spinnerSelected)
                    getProviderEarningsDetail(spinnerSelected)
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        }
        return rootView
    }

    /* private void getEntries() {
        barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(2f, 0));
        barEntries.add(new BarEntry(4f, 1));
        barEntries.add(new BarEntry(6f, 1));
        barEntries.add(new BarEntry(8f, 3));
        barEntries.add(new BarEntry(7f, 4));
        barEntries.add(new BarEntry(3f, 3));
    }*/
    private fun setOnclicklisteners() {
        cashout_details!!.setOnClickListener(this)
        txt_see_all!!.setOnClickListener(this)
        txt_earnings_click_here!!.setOnClickListener(this)
        backArrow!!.setOnClickListener(this)
        payout_button!!.setOnClickListener(this)
        ic_dropdown!!.setOnClickListener(this)
    }


    fun findViewByIdAndInitialize() {
        helper = ConnectionHelper(activity!!)
        backArrow = rootView!!.findViewById<View>(R.id.backArrow) as ImageView
        last_date = rootView!!.findViewById<View>(R.id.last_date) as NTTextView

        payout_button = rootView!!.findViewById<View>(R.id.payout_button) as NTButton
        sp_timeperiod = rootView!!.findViewById<View>(R.id.sp_timeperiod) as Spinner
        barChart = rootView!!.findViewById<View>(R.id.barchart) as BarChart
        lblTarget = rootView!!.findViewById<View>(R.id.lblTarget) as NTBoldTextView
        rcvRides = rootView!!.findViewById<View>(R.id.rcvRides) as RecyclerView
        rcvtodayCompleted = rootView!!.findViewById<View>(R.id.rcvtodayCompleted) as RecyclerView
        errorLayout = rootView!!.findViewById<View>(R.id.errorLayout) as RelativeLayout
        tv_total_earnings = rootView!!.findViewById<View>(R.id.tv_total_earnings) as NTBoldTextView
        error_title = rootView!!.findViewById<View>(R.id.error_title) as NTTextView
        ic_dropdown = rootView!!.findViewById<View>(R.id.ic_dropdown) as ImageView
        ll_earnings_detail_all = rootView!!.findViewById<View>(R.id.ll_earnings_detail_all) as LinearLayout
        txt_earnings_click_here = rootView!!.findViewById<View>(R.id.txt_earnings_click_here) as NTBoldTextView
        driver_wallet_label = rootView!!.findViewById<View>(R.id.driver_wallet_label) as TextView
        lnrtimeperiod = rootView!!.findViewById<View>(R.id.lnrtimeperiod) as LinearLayout
        ll_earnings = rootView!!.findViewById<View>(R.id.ll_earnings) as LinearLayout
        tv_earnings_detail = rootView!!.findViewById<View>(R.id.tv_earnings_detail) as NTBoldTextView
        tv_earnings_distance = rootView!!.findViewById<View>(R.id.tv_earnings_distance) as NTTextView
        tv_earnings_trip_fare = rootView!!.findViewById<View>(R.id.tv_earnings_trip_fare) as NTTextView
        tv_commission_fee = rootView!!.findViewById<View>(R.id.tv_commission_fee) as NTTextView
        tv_earnigs_tips = rootView!!.findViewById<View>(R.id.tv_earnigs_tips) as NTTextView
        ll_earnings_detail = rootView!!.findViewById<View>(R.id.ll_earnings_detail) as LinearLayout
        ll_cashout_detail = rootView!!.findViewById<View>(R.id.ll_cashout_detail) as LinearLayout
        cashout_details = rootView!!.findViewById<View>(R.id.cashout_details) as ImageView
        txt_see_all = rootView!!.findViewById<View>(R.id.txt_see_all) as LinearLayout
        isInternet = helper!!.isConnectingToInternet
    }

    fun displayMessage(toastString: String?) {
        Snackbar.make(view!!, toastString!!, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
    }

    fun GoToBeginActivity() {
        putKey(activity!!, "loggedIn", getString(R.string.False))
        val mainIntent = Intent(activity, ActivityBegin::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(mainIntent)
        activity!!.finish()
    }

    override fun onDetach() {
        super.onDetach()
    }

    override fun onClick(v: View) {
        val id = v.id
        when (id) {
            R.id.cashout_details -> {
                MainActivity.navController!!.navigate(R.id.fragmentCashoutDetails)
            }
            R.id.txt_see_all -> {
                ll_earnings!!.visibility = View.GONE
                driver_wallet_label!!.visibility = View.GONE
                lnrtimeperiod!!.visibility = View.VISIBLE
                ll_earnings_detail!!.visibility = View.VISIBLE
                ic_dropdown!!.visibility = View.VISIBLE
                backArrow!!.visibility = View.VISIBLE
            }
            R.id.txt_earnings_click_here -> ll_earnings_detail_all!!.visibility = View.VISIBLE
            R.id.backArrow -> {
                MainActivity.navController!!.navigate(R.id.fragmentHome)
            }
            R.id.ic_dropdown -> (rootView!!.findViewById<View>(R.id.sp_timeperiod) as Spinner).performClick()

            R.id.payout_button -> {

            }
        }

    }

    private fun Sendpaymentrequest() {
        val `object` = JSONObject()
        try {
            `object`.put("amount", TotalEarnings)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URLHelper.CASHOUT, `object`, Response.Listener { response ->
            Utilities.printAPI_Response(response.toString())
            if (response.getString("success").equals("1")) {
                try {
                    Utilities().showCustomAlert(activity!!, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), resources.getString(R.string.cashout_successfully))
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            } else if (response.getString("success").equals("2")) {
                Utilities().showCustomAlert(activity!!, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), response.getString("message"))

            } else {
                Utilities().showCustomAlert(activity!!, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), resources.getString(R.string.req_process))
            }

        }, Response.ErrorListener { error ->
            //   btn_statusUpdate?.toggleState()
            try {
                if (error is TimeoutError) {
                    //  makeText(activity!!, resources.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show()
                } else if (error is NoConnectionError) {
                    Toast.makeText(activity!!, resources.getString(R.string.error_no_network), Toast.LENGTH_LONG).show()
                } else if (error is AuthFailureError) {
                    Toast.makeText(activity!!, resources.getString(R.string.error_auth_failure), Toast.LENGTH_LONG).show()
                } else if (error is ServerError) {
                    Toast.makeText(activity!!, resources.getString(R.string.error_server_connection), Toast.LENGTH_LONG).show()
                } else if (error is NetworkError) {
                    // Toast.makeText(activity!!, getResources().getString(R.string.error_network), Toast.LENGTH_LONG).show();
                } else if (error is ParseError) {
                    // Toast.makeText(activity!!, resources.getString(R.string.error_parse), Toast.LENGTH_LONG).show()
                } else {
                    // utils.showCustomAlert(activity!!, utils.ALERT_ERROR, getResources().getString(R.string.app_name), error.getMessage());
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(activity!!, resources.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
            }
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = getKey(activity!!, "lang")!!
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "" + getKey(activity!!, "token_type") + " " + getKey(activity!!, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)

    }

    private fun getProviderEarningsDetail(timeperiod: String) {
        val customDialog = LoadingDialog(getActivity()!!)
        //   customDialog.(false);
        customDialog.showDialog()
        Utilities.print(URLHelper.EARNINGS_DETAIL + "?type=" + timeperiod, "")
        println("DriverAPI Header: " + "X-Requested-With : XMLHttpRequest " + "Authorization : " + getKey(getContext()!!, "access_token"))
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.GET, URLHelper.EARNINGS_DETAIL + "?type=" + timeperiod, null, Response.Listener { response ->
            customDialog.hideDialog()
            Utilities.printAPI_Response(response.toString())
            if (response.optJSONArray("earnings") != null) {
                barEntries = ArrayList<BarEntry>()
                for (i in 0 until response.optJSONArray("earnings").length()) {
                    (barEntries as ArrayList<BarEntry>).add(BarEntry(java.lang.Float.valueOf(Utilities.parseDate(response.optJSONArray("earnings").optJSONObject(i).optString("date"))),
                            java.lang.Float.valueOf(response.optJSONArray("earnings").optJSONObject(i).optString("earnings"))))
                }
                barDataSet = BarDataSet(barEntries as ArrayList<BarEntry>, "")
                barData = BarData(barDataSet)
                barChart!!.data = barData
                barData!!.setDrawValues(false)
                barDataSet!!.color = resources.getColor(R.color.button_blue)
                barChart!!.xAxis.setDrawGridLines(false)
                barChart!!.axisLeft.setDrawGridLines(false)
                barChart!!.axisRight.setDrawGridLines(false)
                barChart!!.setPinchZoom(false)
                barChart!!.setDrawGridBackground(false)
                barChart!!.setDrawValueAboveBar(false)

                //IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(barChart);
                val xAxis = barChart!!.xAxis
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                // xAxis.setTypeface(tfLight);
                xAxis.setDrawGridLines(false)
                xAxis.granularity = 1f // only intervals of 1 day
                xAxis.labelCount = 7
                //xAxis.setValueFormatter(xAxisFormatter);
                val custom: IAxisValueFormatter = MyAxisValueFormatter()
                val leftAxis = barChart!!.axisLeft
                // leftAxis.setTypeface(tfLight);
                leftAxis.setLabelCount(8, false)
                leftAxis.valueFormatter = custom
                leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART)
                leftAxis.spaceTop = 15f
                leftAxis.axisMinimum = 0f // this replaces setStartAtZero(true)
                val rightAxis = barChart!!.axisRight
                rightAxis.setDrawGridLines(false)
                rightAxis.setDrawLabels(false)
                rightAxis.isEnabled = false
                //  rightAxis.setTypeface(tfLight);// this replaces setStartAtZero(true)

                /*  Legend l = barChart.getLegend();
      l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
      l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
      l.setOrientation(Legend.LegendOrientation.VERTICAL);
      l.setDrawInside(false);
     l.setForm(Legend.LegendForm.SQUARE);
      l.setFormSize(9f);
      l.setTextSize(11f);
      l.setXEntrySpace(4f);*/
                val l = barChart!!.legend
                l.verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
                l.horizontalAlignment = Legend.LegendHorizontalAlignment.LEFT
                l.form = Legend.LegendForm.SQUARE
                l.formSize = 9f
                l.textSize = 11f
                l.xEntrySpace = 4f

                /*  XYMarkerView mv = new XYMarkerView(activity, xAxisFormatter);
      mv.setChartView(barChart); // For bounds control
      barChart.setMarker(mv);*/
                // Set the marker to the chart
                barChart!!.setBackgroundColor(resources.getColor(R.color.colorGreyMoreLighter))
                barDataSet!!.valueTextColor = Color.BLACK
                barDataSet!!.valueTextSize = 18f
                barDataSet!!.valueTextSize = 18f
                barChart!!.legend.isEnabled = false
                barChart!!.description.isEnabled = false
                // barChart.setVisibleXRangeMaximum(25);
                barChart!!.animateXY(2000, 2000)
                /* date = new ArrayList();
    earnings= new ArrayList();
  
      for (int i = 0; i < response.optJSONArray("earnings").length(); i++) {
  
          if (!date.contains(response.optJSONArray("earnings").optJSONObject(i).optString("date"))) {
              date.add(response.optJSONArray("earnings").optJSONObject(i).optString("date"));
          }
      }
      for (int i = 0; i < response.optJSONArray("earnings").length(); i++) {
  
          if (!earnings.contains(response.optJSONArray("earnings").optJSONObject(i).optString("earnings"))) {
              earnings.add(response.optJSONArray("earnings").optJSONObject(i).optString("earnings"));
          }
      }*/earningsAdapter = EarningsAdapter(response)
                val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(getActivity()!!.applicationContext)
                rcvRides!!.layoutManager = mLayoutManager
                rcvRides!!.itemAnimator = DefaultItemAnimator()
                if (earningsAdapter != null && earningsAdapter!!.itemCount > 0) {
                    rcvRides!!.visibility = View.VISIBLE
                    errorLayout!!.visibility = View.GONE
                    rcvRides!!.adapter = earningsAdapter
                } else {
                    errorLayout!!.visibility = View.VISIBLE
                    rcvRides!!.visibility = View.GONE
                }
            } else {
                errorLayout!!.visibility = View.VISIBLE
                rcvRides!!.visibility = View.GONE
            }
        }, Response.ErrorListener { error ->
            customDialog.hideDialog()
            var json: String? = null
            var Message: String
            val response = error.networkResponse
            if (response != null && response.data != null) {
                try {
                    val errorObj = JSONObject(String(response.data))
                    if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                        try {
                            displayMessage(errorObj.optString("message"))
                        } catch (e: Exception) {
                            displayMessage(getString(R.string.something_went_wrong))
                            e.printStackTrace()
                        }
                    } else if (response.statusCode == 401) {
                        GoToBeginActivity()
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
                }
            }
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = getKey(getActivity()!!, "lang")!!
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "Bearer " + getKey(getContext()!!, "access_token")
                Log.e("", "Access_Token" + getKey(getContext()!!, "access_token"))
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    fun getProviderEarnings(timeperiod: String) {
        val customDialog = LoadingDialog(getActivity()!!)
        //   customDialog.(false);
        customDialog.showDialog()
        Utilities.print(URLHelper.EARNINGS + "?type=" + timeperiod, "")
        println("DriverAPI Header: " + "X-Requested-With : XMLHttpRequest " + "Authorization : " + getKey(getContext()!!, "access_token"))
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.GET, URLHelper.EARNINGS + "?type=" + timeperiod, null, Response.Listener { response ->
            customDialog.hideDialog()
            Utilities.printAPI_Response(response.toString())
            TotalEarnings = response.optString("earnings")
            lblTarget!!.text = getKey(activity!!, "currency") + " " + response.optString("earnings")
            tv_earnings_detail!!.text = getKey(activity!!, "currency") + " " + response.optString("earnings")
            tv_earnings_distance!!.text = response.optString("kilometer") + " " + "Km"
            tv_earnings_trip_fare!!.text = getKey(activity!!, "currency") + " " + response.optString("total_fare")
            tv_commission_fee!!.text = getKey(activity!!, "currency") + " " + response.optString("commision")
            tv_earnigs_tips!!.text = getKey(activity!!, "currency") + " " + response.optString("tip")
        }, Response.ErrorListener { error ->
            customDialog.hideDialog()
            var json: String? = null
            var Message: String
            val response = error.networkResponse
            if (response != null && response.data != null) {
                try {
                    val errorObj = JSONObject(String(response.data))
                    if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                        try {
                            displayMessage(errorObj.optString("message"))
                        } catch (e: Exception) {
                            displayMessage(getString(R.string.something_went_wrong))
                            e.printStackTrace()
                        }
                    } else if (response.statusCode == 401) {
                        GoToBeginActivity()
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
                }
            }
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = getKey(getActivity()!!, "lang")!!
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "Bearer " + getKey(getContext()!!, "access_token")
                Log.e("", "Access_Token" + getKey(getContext()!!, "access_token"))
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    private fun gettodayCompletedList() {


        println("PassengerAPI Header: " + "X-Requested-With : XMLHttpRequest " + "Authorization : " + getKey(mycontext!!, "token_type") + " " + getKey(mycontext!!, "access_token"))
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(URLHelper.WALLET_HISTORY, Response.Listener { response ->
            Utilities.printAPI_Response(response.toString())
            Log.d("TAG", "gettodayCompletedList-----------: " + response.toString())
            if (response != null) {
                val jsonArray = response.getJSONArray("data")
                tv_total_earnings!!.text = getKey(activity!!, "currency") + " " + response.optString("wallet_balance")

                historyListTodayAdapter = HistoryTodayListAdapter(getActivity(), jsonArray, last_date!!)
                //  recyclerView.setHasFixedSize(true);
                val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(getActivity()!!.applicationContext)
                rcvtodayCompleted!!.layoutManager = mLayoutManager
                rcvtodayCompleted!!.itemAnimator = DefaultItemAnimator()
                if (historyListTodayAdapter != null && historyListTodayAdapter!!.itemCount > 0) {
                    error_title!!.visibility = View.GONE
                    rcvtodayCompleted!!.adapter = historyListTodayAdapter
                } else {
                    error_title!!.visibility = View.VISIBLE
                    rcvtodayCompleted!!.visibility = View.GONE
                }
            } else {
                error_title!!.visibility = View.VISIBLE
                rcvtodayCompleted!!.visibility = View.GONE
            }
        }, Response.ErrorListener { error ->
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
                    if (error is NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet))
                    } else if (error is NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet))
                    } else if (error is TimeoutError) {
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(getActivity(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
            }
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = getKey(mycontext!!, "lang")!!
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "" + getKey(mycontext!!, "token_type") + " " + getKey(mycontext!!, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    inner class EarningsAdapter(var jsonResponse: JSONObject) : RecyclerView.Adapter<EarningsAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.earnings_item, parent, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            val jsonArray = jsonResponse.optJSONArray("earnings")
            try {
                val jsonObject = jsonArray.getJSONObject(position)
                /* try {
                   // holder.lblTimeDistance.setText(getDate(jsonObject.optString("created_at"))+ "\n"+getTime(jsonObject.optString("assigned_at"))+ "\n"+jsonObject.optString("distance")+" Km");
                } catch (ParseException e) {
                    e.printStackTrace();
                }*/

//                holder.lblTips.setText(jsonObject.optString("distance")+" Km");
                holder.lblTimeDistance.text = """${Utilities.parseDateMonth(jsonArray.getJSONObject(position).optString("date"))}
${jsonArray.getJSONObject(position).optString("distance")} KM"""
                holder.lblTips.text = getKey(mycontext!!, "currency") + " " + jsonArray.getJSONObject(position).optString("tip")
                holder.lblAmount.text = getKey(mycontext!!, "currency") + " " + jsonArray.getJSONObject(position).optString("trip_fare")
                if (jsonArray.getJSONObject(position).optString("commision") == null) {
                    holder.lblCommission.text = getKey(mycontext!!, "currency") + " " + "0"
                } else {
                    holder.lblCommission.text = getKey(mycontext!!, "currency") + " " + jsonArray.getJSONObject(position).optString("commision")
                }
                if (jsonArray.getJSONObject(position).optString("earnings") == null) {
                    holder.lblEarnings.text = getKey(mycontext!!, "currency") + " " + "0"
                } else {
                    holder.lblEarnings.text = getKey(mycontext!!, "currency") + " " + jsonArray.getJSONObject(position).optString("earnings")
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun getItemCount(): Int {
            return jsonResponse.optJSONArray("earnings").length()
        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var lblTimeDistance: NTTextView
            var lblTips: NTTextView
            var lblAmount: NTTextView
            var lblCommission: NTTextView
            var lblEarnings: NTTextView

            init {
                lblTimeDistance = itemView.findViewById<View>(R.id.lblTimeDistance) as NTTextView
                lblTips = itemView.findViewById<View>(R.id.lblTips) as NTTextView
                lblAmount = itemView.findViewById<View>(R.id.lblAmount) as NTTextView
                lblCommission = itemView.findViewById<View>(R.id.lblCommission) as NTTextView
                lblEarnings = itemView.findViewById<View>(R.id.lblEarnings) as NTTextView
            }
        }
    }

    inner class HistoryTodayListAdapter(private val activity: FragmentActivity?, var jsonArray: JSONArray, var lastdate: NTTextView) : RecyclerView.Adapter<HistoryTodayListAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
            val itemView = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.layout_wallet_history_list, viewGroup, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            try {
//                if (jsonArray.optJSONObject(position) != null) {
//                    Picasso.with(activity).load(jsonArray.optJSONObject(position).optString("static_map")).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(holder.tripImg);
//                }

                lastdate!!.text = utilities.parseDateMonth1(jsonArray.optJSONObject(0).optString("created"))

//                holder.date.text = jsonArray.optJSONObject(position).optString("created")
                holder.amount.text = "R " + jsonArray.optJSONObject(position).optString("amount")
                holder.mode.text = jsonArray.optJSONObject(position).optString("mode")
                holder.date.text = utilities.parseDateMonth2(jsonArray.optJSONObject(position).optString("created"))

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        override fun getItemCount(): Int {
            return jsonArray.length()
        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var date: NTBoldTextView
            var amount: NTBoldTextView
            var mode: NTBoldTextView


            init {
                date = itemView.findViewById<View>(R.id.date) as NTBoldTextView
                amount = itemView.findViewById<View>(R.id.amount) as NTBoldTextView
                mode = itemView.findViewById<View>(R.id.mode) as NTBoldTextView

            }
        }
    }

    fun pop() {
//        val fm = getActivity()!!.supportFragmentManager
//        val count = fm.backStackEntryCount
//        for (i in 0..count) {
//            fm.popBackStackImmediate()
//        }
        MainActivity.navController!!.navigate(R.id.fragmentHome)
    }
}

