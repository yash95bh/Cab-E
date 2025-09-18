package com.eviort.cabedriver.NTFragment

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.eviort.cabedriver.NTActivity.MainActivity
import com.eviort.cabedriver.NTApplication
import com.eviort.cabedriver.NTCustomView.NTBoldTextView
import com.eviort.cabedriver.NTCustomView.NTCircularImageView
import com.eviort.cabedriver.NTCustomView.NTTextView
import com.eviort.cabedriver.NTHelper.LoadingDialog
import com.eviort.cabedriver.NTHelper.SharedHelper.getKey
import com.eviort.cabedriver.NTHelper.URLHelper
import com.eviort.cabedriver.NTUtilites.Utilities
import com.eviort.cabedriver.R
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class FragmentCashoutList : Fragment() {
    var activity: Activity? = null
    var mycontext: Context? = null
    var recyclerView: RecyclerView? = null
    var Backarrow: ImageView? = null
    var spinner_pastTrip: Spinner? = null
    var loadingDialog: LoadingDialog? = null
    var utils = Utilities()
    var time_period = "1"
    var rootview: View? = null
    var pastTripTitle: NTBoldTextView? = null
    var pastTripDescription: NTTextView? = null
    var historyListAdapter: HistoryListAdapter? = null
    var ll_errorLayout: LinearLayout? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_cashout_list, container, false)
        Log.d("FragmentPastTrips", "FragmentPastTrips")
        findViewByIdAndInitialize()
        Backarrow!!.setOnClickListener {
            MainActivity.navController!!.navigate(R.id.fragmentEarnings)
        }

//        historyListAdapter = new HistoryListAdapter(getActivity());
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//        recyclerView.setAdapter(historyListAdapter);
        getHistoryList()
        return rootview
    }

    private fun getHistoryList() {
        loadingDialog = LoadingDialog(getActivity()!!)
        if (loadingDialog != null) loadingDialog!!.showDialog()
        Utilities.PrintAPI_URL(URLHelper.GET_CASHOUT_HISTORY, "GET")
        println("PassengerAPI Header: " + "X-Requested-With : XMLHttpRequest " + "Authorization : " + getKey(mycontext!!, "token_type") + " " + getKey(mycontext!!, "access_token"))
        val jsonArrayRequest: JsonArrayRequest = object : JsonArrayRequest(URLHelper.GET_CASHOUT_HISTORY, Response.Listener { response ->
            Utilities.printAPI_Response(response.toString())
            Utilities.PrintAPI_URL(URLHelper.GET_PAST_HISTORY, response.toString())

            if (response != null) {
                historyListAdapter = getActivity()?.let { HistoryListAdapter(it, response) }
                //  recyclerView.setHasFixedSize(true);
                val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(getActivity()!!.applicationContext)
                recyclerView!!.layoutManager = mLayoutManager
                recyclerView!!.itemAnimator = DefaultItemAnimator()
                if (historyListAdapter != null && historyListAdapter!!.itemCount > 0) {
                    ll_errorLayout!!.visibility = View.GONE
                    recyclerView!!.adapter = historyListAdapter
                } else {
                    ll_errorLayout!!.visibility = View.VISIBLE
                }
            } else {
                ll_errorLayout!!.visibility = View.VISIBLE
            }
            if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
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
                    if (error is NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet))
                    } else if (error is NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet))
                    } else if (error is TimeoutError) {
                        getHistoryList()
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
        NTApplication.getInstance().addToRequestQueue(jsonArrayRequest)
    }

    private fun findViewByIdAndInitialize() {
        recyclerView = rootview!!.findViewById<View>(R.id.recyclerView) as RecyclerView
        ll_errorLayout = rootview!!.findViewById<View>(R.id.ll_errorLayout) as LinearLayout
        spinner_pastTrip = rootview!!.findViewById<View>(R.id.spinner_pastTrip) as Spinner
        pastTripTitle = rootview!!.findViewById<View>(R.id.error_title) as NTBoldTextView
        pastTripDescription = rootview!!.findViewById<View>(R.id.error_description) as NTTextView
        Backarrow = rootview!!.findViewById<View>(R.id.earbackArrow) as ImageView
        pastTripTitle!!.text = getActivity()!!.resources.getString(R.string.no_request)
//        pastTripDescription!!.text = getActivity()!!.resources.getString(R.string.no_offer_desc)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mycontext = context
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity
    }

    override fun onDetach() {
        super.onDetach()
    }

    inner class HistoryListAdapter(private val activity: FragmentActivity, var jsonArray: JSONArray) : RecyclerView.Adapter<HistoryListAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
            val itemView = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.layout_cashout_list, viewGroup, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            try {
//                if (jsonArray.optJSONObject(position) != null) {
//                    Picasso.with(activity).load(jsonArray.optJSONObject(position).optString("static_map")).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(holder.tripImg);
//                }
                holder.booking_id.text = getString(R.string.request_id) + " : " + jsonArray.optJSONObject(position).optString("request_id")

                if (jsonArray.optJSONObject(position).optString("status") != null &&
                        !jsonArray.optJSONObject(position).optString("status").equals("", ignoreCase = true)) {
                    val status = jsonArray.optJSONObject(position).optString("status")
                    when (status) {
                        "REQUESTED" -> {
                            holder.trip_status.setTextColor(resources.getColor(R.color.quantum_yellow))
                            holder.trip_status.text = getString(R.string.REQUESTED)
                            if (!jsonArray.optJSONObject(position).optString("created", "").isEmpty()) {
                                val form = jsonArray.optJSONObject(position).optString("created")
                                try {
                                    holder.tripDate.text = form
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            if (!jsonArray.optJSONObject(position).optString("picture").equals("")) {
                                Picasso.with(activity).load(jsonArray.optJSONObject(position).optString("picture")).placeholder(R.drawable.ic_driver).error(R.drawable.ic_driver).into(holder.driver_image)
                            } else {

                            }
                        }
                        "REJECTED" -> {
                            holder.trip_status.setTextColor(resources.getColor(R.color.red))
                            holder.trip_status.text = getString(R.string.REJECTED)
                            if (!jsonArray.optJSONObject(position).optString("created", "").isEmpty()) {
                                val form = jsonArray.optJSONObject(position).optString("created")
                                try {
                                    holder.tripDate.text = form
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            Picasso.with(activity).load(R.drawable.ic_driver).placeholder(R.drawable.ic_driver).error(R.drawable.ic_driver).into(holder.driver_image)
                        }
                        "APPROVED" -> {
                            holder.trip_status.setTextColor(resources.getColor(R.color.green))
                            holder.trip_status.text = getString(R.string.APPROVED)
                            if (!jsonArray.optJSONObject(position).optString("created", "").isEmpty()) {
                                val form = jsonArray.optJSONObject(position).optString("created")
                                try {
                                    holder.tripDate.text = form
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            if (!jsonArray.optJSONObject(position).optString("picture").equals("")) {
                                Picasso.with(activity).load(jsonArray.optJSONObject(position).optString("picture")).placeholder(R.drawable.ic_driver).error(R.drawable.ic_driver).into(holder.driver_image)
                            } else {

                            }
                        }

                    }
                }
                holder.srcAddress.text = jsonArray.optJSONObject(position).optString("s_address")
                holder.destAddress.text = "$ " + jsonArray.optJSONObject(position).optString("amount")
                holder.type.text = jsonArray.optJSONObject(position).optString("name")

            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                val serviceObj = jsonArray.getJSONObject(position).optJSONObject("service_type")
                if (serviceObj != null) {
                    // holder.car_name.text = serviceObj.optString("name")
                    Picasso.with(activity).load(serviceObj.optString("image")).placeholder(R.drawable.ic_driver).error(R.drawable.ic_driver).into(holder.driver_image)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        override fun getItemCount(): Int {
            return jsonArray.length()
        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var srcAddress: NTTextView
            var destAddress: NTTextView

            // var car_name: NTTextView
            var booking_id: NTBoldTextView
            var trip_status: NTBoldTextView
            var type: NTBoldTextView
            var tripDate: NTBoldTextView
            var tripAmount: NTBoldTextView
            var tripTime: NTBoldTextView
            var driver_image: NTCircularImageView

            init {
                srcAddress = itemView.findViewById<View>(R.id.srcAddress) as NTTextView
                destAddress = itemView.findViewById<View>(R.id.destAddress) as NTTextView
                tripDate = itemView.findViewById<View>(R.id.tripDate) as NTBoldTextView
                tripTime = itemView.findViewById<View>(R.id.tripTime) as NTBoldTextView
                tripAmount = itemView.findViewById<View>(R.id.tripAmount) as NTBoldTextView
                //  car_name = itemView.findViewById<View>(R.id.car_name) as NTTextView
                type = itemView.findViewById<View>(R.id.Type) as NTBoldTextView
                booking_id = itemView.findViewById<View>(R.id.booking_id) as NTBoldTextView
                trip_status = itemView.findViewById<View>(R.id.tripstatus) as NTBoldTextView
                driver_image = itemView.findViewById<View>(R.id.driver_image) as NTCircularImageView

            }
        }
    }

    @Throws(ParseException::class)
    private fun getMonth(date: String): String {
        val d = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date)
        val cal = Calendar.getInstance()
        cal.time = d
        return SimpleDateFormat("MMM").format(cal.time)
    }

    @Throws(ParseException::class)
    private fun getDate(date: String): String {
        val d = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date)
        val cal = Calendar.getInstance()
        cal.time = d
        return SimpleDateFormat("dd").format(cal.time)
    }

    @Throws(ParseException::class)
    private fun getYear(date: String): String {
        val d = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date)
        val cal = Calendar.getInstance()
        cal.time = d
        return SimpleDateFormat("yyyy").format(cal.time)
    }

    @Throws(ParseException::class)
    private fun getTime(date: String): String {
        val d = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH).parse(date)
        val cal = Calendar.getInstance()
        cal.time = d
        return SimpleDateFormat("hh:mm a").format(cal.time)
    }

    fun displayMessage(msg: String?) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show()
    }
}


