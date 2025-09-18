package com.eviort.cabedriver.NTFragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.eviort.cabedriver.NTActivity.ActivityHistoryDetail
import com.eviort.cabedriver.NTActivity.ActivityHistoryScheduledDetail
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

class FragmentPastTrips : Fragment() {
    var activity: Activity? = null
    var mycontext: Context? = null
    var recyclerView: RecyclerView? = null
    var spinner_pastTrip: Spinner? = null
    var loadingDialog: LoadingDialog? = null
    var utils = Utilities()
    var time_period = "1"
    var spinnerSelected = "0"
    var spinnerTouch = 0
    var rootview: View? = null
    var pastTripTitle: NTBoldTextView? = null
    var pastTripDescription: NTTextView? = null
    var historyListAdapter: HistoryListAdapter? = null
    var ll_errorLayout: LinearLayout? = null
    var sp_timeperiod_image: ImageView? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_past_trips, container, false)
        rootview?.setFocusableInTouchMode(true)
        rootview?.requestFocus()
        rootview?.setOnKeyListener(View.OnKeyListener { v, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return@OnKeyListener true
                }
            }
            false
        })
        Log.d("FragmentPastTrips", "FragmentPastTrips")
        findViewByIdAndInitialize()
        val adapter = ArrayAdapter.createFromResource(
                requireActivity(),
                R.array.time_period,
                android.R.layout.simple_spinner_item
        )

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner_pastTrip?.setAdapter(adapter)

        sp_timeperiod_image?.setOnClickListener {
            (rootview!!.findViewById(R.id.spinner_pastTrip) as Spinner).performClick()
        }
        spinner_pastTrip?.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
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
                    historyList
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {}
        })
        historyList
        return rootview
    }

    private val historyList: Unit
        private get() {
            loadingDialog = LoadingDialog(requireActivity())
            if (loadingDialog != null) loadingDialog!!.showDialog()
            Utilities.PrintAPI_URL(URLHelper.GET_PAST_HISTORY, "POST")
            val jsonArrayRequest: JsonArrayRequest = object : JsonArrayRequest(URLHelper.GET_PAST_HISTORY, Response.Listener { response ->
                Utilities.printAPI_Response(response.toString())
                if (response != null) {
                    historyListAdapter = HistoryListAdapter(getActivity(), response)
                    //  recyclerView.setHasFixedSize(true);
                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(requireActivity().applicationContext)
                    recyclerView!!.layoutManager = mLayoutManager
                    recyclerView!!.itemAnimator = DefaultItemAnimator()
                    if (historyListAdapter != null && historyListAdapter!!.itemCount > 0) {
                        ll_errorLayout!!.visibility = View.GONE
                        recyclerView!!.visibility = View.VISIBLE
                        recyclerView!!.adapter = historyListAdapter
                    } else {
                        ll_errorLayout!!.visibility = View.VISIBLE
                        recyclerView!!.visibility = View.GONE
                    }
                } else {
                    ll_errorLayout!!.visibility = View.VISIBLE
                    recyclerView!!.visibility = View.GONE
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
                            historyList
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

                override fun getMethod(): Int {
                    return Method.POST
                }

                override fun getBody(): ByteArray {
                    val param = HashMap<String, String>()
                    param.put("type", spinnerSelected)
                    return JSONObject(param.toString()).toString().toByteArray()
                }

                override fun getParams(): MutableMap<String, String> {
                    val param = HashMap<String, String>()
                    param.put("type", spinnerSelected)
                    //  param["type"]=
                    return param
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
        pastTripTitle!!.text = requireActivity().resources.getString(R.string.no_past_trip)
        sp_timeperiod_image = rootview!!.findViewById(R.id.sp_timeperiod_image) as ImageView
        pastTripDescription!!.text = requireActivity().resources.getString(R.string.no_past_trip_desc)
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

    inner class HistoryListAdapter(private val activity: FragmentActivity?, var jsonArray: JSONArray) : RecyclerView.Adapter<HistoryListAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
            val itemView = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.layout_history_list, viewGroup, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            try {
                if (jsonArray.optJSONObject(position).optString("status") != null &&
                        !jsonArray.optJSONObject(position).optString("status").equals("", ignoreCase = true)) {
                    val status = jsonArray.optJSONObject(position).optString("status")
                    try {
                        holder.booking_id!!.text = " : "+jsonArray.optJSONObject(position).optString("booking_id")
                    }catch (e:Exception){
                        e.printStackTrace()
                    }
                    when (status) {
                        "SEARCHING" -> {
                            holder.tripAmount.visibility = View.GONE
                            holder.trip_status.text = getString(R.string.searching_status)
                            if (!jsonArray.optJSONObject(position).optString("created_at", "").isEmpty()) {
                                val form = jsonArray.optJSONObject(position).optString("created_at")
                                try {
                                    holder.tripDate.text = form
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            if (!jsonArray.optJSONObject(position).optString("profile").equals("")) {
                                Picasso.with(activity).load(jsonArray.optJSONObject(position).optString("profile")).placeholder(R.drawable.ic_driver).error(R.drawable.ic_driver).into(holder.driver_image)
                            } else {

                            }
                        }
                        "CANCELLED" -> {
                            holder.tripAmount.visibility = View.GONE
                            holder.trip_status.setTextColor(resources.getColor(R.color.red))
                            holder.trip_status.text = getString(R.string.cancelled_status)
                            if (!jsonArray.optJSONObject(position).optString("created_at", "").isEmpty()) {
                                val form = jsonArray.optJSONObject(position).optString("created_at")
                                try {
                                    holder.tripDate.text = form
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            Picasso.with(activity).load(R.drawable.ic_driver).placeholder(R.drawable.ic_driver).error(R.drawable.ic_driver).into(holder.driver_image)
                        }
                        "ACCEPTED" -> {
                            holder.tripAmount.visibility = View.GONE
                            holder.trip_status.setTextColor(resources.getColor(R.color.green))
                            holder.trip_status.text = getString(R.string.accepted_status)
                            if (!jsonArray.optJSONObject(position).optString("created_at", "").isEmpty()) {
                                val form = jsonArray.optJSONObject(position).optString("created_at")
                                try {
                                    holder.tripDate.text = form
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            if (!jsonArray.optJSONObject(position).optString("profile").equals("")) {
                                Picasso.with(activity).load(jsonArray.optJSONObject(position).optString("profile")).placeholder(R.drawable.ic_driver).error(R.drawable.ic_driver).into(holder.driver_image)
                            } else {

                            }
                        }
                        "STARTED" -> {
                            holder.tripAmount.visibility = View.GONE
                            holder.trip_status.setTextColor(resources.getColor(R.color.green))
                            holder.trip_status.text = getString(R.string.started_status)
                            if (!jsonArray.optJSONObject(position).optString("created_at", "").isEmpty()) {
                                val form = jsonArray.optJSONObject(position).optString("created_at")
                                try {
                                    holder.tripDate.text = form
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            if (!jsonArray.optJSONObject(position).optString("profile").equals("")) {
                                Picasso.with(activity).load(jsonArray.optJSONObject(position).optString("profile")).placeholder(R.drawable.ic_driver).error(R.drawable.ic_driver).into(holder.driver_image)
                            } else {

                            }
                        }
                        "ARRIVED" -> {
                            holder.tripAmount.visibility = View.GONE
                            holder.trip_status.setTextColor(resources.getColor(R.color.green))
                            holder.trip_status.text = getString(R.string.arrived_status)
                            if (!jsonArray.optJSONObject(position).optString("created_at", "").isEmpty()) {
                                val form = jsonArray.optJSONObject(position).optString("created_at")
                                try {
                                    holder.tripDate.text = form
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            if (!jsonArray.optJSONObject(position).optString("profile").equals("")) {
                                Picasso.with(activity).load(jsonArray.optJSONObject(position).optString("profile")).placeholder(R.drawable.ic_driver).error(R.drawable.ic_driver).into(holder.driver_image)
                            } else {

                            }
                        }
                        "PICKEDUP" -> {
                            holder.tripAmount.visibility = View.GONE
                            holder.trip_status.setTextColor(resources.getColor(R.color.green))
                            holder.trip_status.text = getString(R.string.pickedup_status)
                            if (!jsonArray.optJSONObject(position).optString("created_at", "").isEmpty()) {
                                val form = jsonArray.optJSONObject(position).optString("created_at")
                                try {
                                    holder.tripDate.text = form
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            if (!jsonArray.optJSONObject(position).optString("profile").equals("")) {
                                Picasso.with(activity).load(jsonArray.optJSONObject(position).optString("profile")).placeholder(R.drawable.ic_driver).error(R.drawable.ic_driver).into(holder.driver_image)
                            } else {

                            }
                        }
                        "DROPPED" -> {
                            holder.tripAmount.visibility = View.GONE
                            holder.trip_status.setTextColor(resources.getColor(R.color.green))
                            holder.trip_status.text = getString(R.string.dropped_status)
                            if (!jsonArray.optJSONObject(position).optString("created_at", "").isEmpty()) {
                                val form = jsonArray.optJSONObject(position).optString("created_at")
                                try {
                                    holder.tripDate.text = form
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            if (!jsonArray.optJSONObject(position).optString("profile").equals("")) {
                                Picasso.with(activity).load(jsonArray.optJSONObject(position).optString("profile")).placeholder(R.drawable.ic_driver).error(R.drawable.ic_driver).into(holder.driver_image)
                            } else {

                            }
                        }
                        "COMPLETED" -> {
                            holder.trip_status.setTextColor(resources.getColor(R.color.green))
                            holder.trip_status.text = getString(R.string.completed_status)
                            holder.tripAmount.visibility = View.VISIBLE
                            holder.tripAmount.text = jsonArray.optJSONObject(position).optString("currency") + "" + jsonArray.optJSONObject(position).optString("estimated_fare")
                            if (!jsonArray.optJSONObject(position).optString("finished_at", "").isEmpty()) {
                                val form = jsonArray.optJSONObject(position).optString("created_at")
                                try {
                                    holder.tripDate.text = form
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            if (!jsonArray.optJSONObject(position).optString("profile").equals("")) {
                                Picasso.with(activity).
                                load(jsonArray.optJSONObject(position).
                                optString("profile")).
                                placeholder(R.drawable.ic_driver).
                                error(R.drawable.ic_driver).
                                centerInside().
                                into(holder.driver_image)
                            } else {

                            }
                        }
                        "END" -> {
                            holder.trip_status.setTextColor(resources.getColor(R.color.green))
                            holder.trip_status.text = getString(R.string.end_status)
                            if (!jsonArray.optJSONObject(position).optString("created_at", "").isEmpty()) {
                                val form = jsonArray.optJSONObject(position).optString("created_at")
                                try {
                                    holder.tripDate.text = form
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                            if (!jsonArray.optJSONObject(position).optString("profile").equals("")) {
                                Picasso.with(activity).load(jsonArray.optJSONObject(position).optString("profile")).placeholder(R.drawable.ic_driver).error(R.drawable.ic_driver).into(holder.driver_image)
                            } else {

                            }
                        }
                        "SCHEDULED" -> {
                            holder.trip_status.setTextColor(resources.getColor(R.color.white))
                            holder.trip_status.setBackgroundColor(resources.getColor(R.color.green))
                            holder.trip_status.text = getString(R.string.scheduled_status)
                            if (!jsonArray.optJSONObject(position).optString("profile").equals("")) {
                                Picasso.with(activity).load(jsonArray.optJSONObject(position).optString("profile")).placeholder(R.drawable.ic_driver).error(R.drawable.ic_driver).into(holder.driver_image)
                            } else {

                            }
                            if (!jsonArray.optJSONObject(position).optString("created_at", "").isEmpty()) {
                                val form = jsonArray.optJSONObject(position).optString("created_at")
                                try {
                                    holder.tripDate.text = form
                                } catch (e: Exception) {
                                    e.printStackTrace()
                                }
                            }
                        }
                    }
                }
                holder.srcAddress.text = jsonArray.optJSONObject(position).optString("s_address")
                holder.destAddress.text = jsonArray.optJSONObject(position).optString("d_address")
                holder.type.text = jsonArray.optJSONObject(position).optString("name")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            try {
                val serviceObj = jsonArray.getJSONObject(position).optJSONObject("service_type")
                if (serviceObj != null) {
                    //  holder.car_name.text = serviceObj.optString("name")
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
            var type: NTBoldTextView

            //  var car_name: NTBoldTextView
            var booking_id: NTBoldTextView? = null
            var trip_status: NTBoldTextView
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
                //   car_name = itemView.findViewById<View>(R.id.car_name) as NTBoldTextView
                type = itemView.findViewById<View>(R.id.Type) as NTBoldTextView
                booking_id = itemView.findViewById(R.id.booking_id)
                trip_status = itemView.findViewById<View>(R.id.tripstatus) as NTBoldTextView
                driver_image = itemView.findViewById<View>(R.id.driver_image) as NTCircularImageView
                itemView.setOnClickListener {
                    val status = jsonArray.optJSONObject(adapterPosition).optString("status")
                    if (status.equals("COMPLETED", ignoreCase = true) || status.equals("END", ignoreCase = true)) {
                        val intent = Intent(getActivity(), ActivityHistoryDetail::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        Log.e("Intent", "" + jsonArray.optJSONObject(adapterPosition).toString())
                        intent.putExtra("post_value", jsonArray.optJSONObject(adapterPosition).toString())
                        intent.putExtra("tag", "past_trips")
                        startActivity(intent)
                    } else if (status.equals("SCHEDULED", ignoreCase = true)) {
                        val intent = Intent(getActivity(), ActivityHistoryScheduledDetail::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        Log.e("Intent", "" + jsonArray.optJSONObject(adapterPosition).toString())
                        intent.putExtra("post_value", jsonArray.optJSONObject(adapterPosition).toString())
                        intent.putExtra("tag", "past_trips")
                        startActivity(intent)
                    }
                }
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