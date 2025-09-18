package com.eviort.cabedriver.NTFragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.eviort.cabedriver.NTActivity.ActivityHistoryDetail
import com.eviort.cabedriver.NTApplication
import com.eviort.cabedriver.NTCustomView.NTBoldTextView
import com.eviort.cabedriver.NTCustomView.NTTextView
import com.eviort.cabedriver.NTHelper.LoadingDialog
import com.eviort.cabedriver.NTHelper.SharedHelper.getKey
import com.eviort.cabedriver.NTHelper.SharedHelper.putKey
import com.eviort.cabedriver.NTHelper.URLHelper
import com.eviort.cabedriver.NTUtilites.Utilities
import com.eviort.cabedriver.R
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class FragmentOffer : Fragment() {
    var activity: Activity? = null

    var recyclerView: RecyclerView? = null
    var ll_error_layout: LinearLayout? = null
    var spinner_pastTrip: Spinner? = null
    var loadingDialog: LoadingDialog? = null
    var utils = Utilities()
    var time_period = "1"
    var rootview: View? = null
    var pastTripTitle: NTBoldTextView? = null
    var pastTripDescription: NTTextView? = null
    var historyListAdapter: HistoryListAdapter? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_offer, container, false)
        loadingDialog = LoadingDialog(getActivity()!!)
        findViewByIdAndInitialize()
        offerDetails
        return rootview
    }

    val offerDetails: Unit
        public get() {
            if (loadingDialog != null) {
                loadingDialog!!.showDialog()
            }
            Utilities.PrintAPI_URL(URLHelper.OFFER_LIST, "GET")
            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.GET, URLHelper.OFFER_LIST, null, Response.Listener { response ->
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                try {
                    if (response.getString("success") == "1") {
                        val jsonArray = response.getJSONArray("data")
                        Utilities.PrintAPI_URL(URLHelper.OFFER_LIST, response.toString() + "jsonArray length= " + jsonArray.length())
                        if (jsonArray.length() > 0) {
                            ll_error_layout!!.visibility = View.GONE
                            recyclerView!!.visibility = View.VISIBLE
                            historyListAdapter = getActivity()?.let { HistoryListAdapter(it, jsonArray) }
                            recyclerView!!.layoutManager = LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false)
                            recyclerView!!.adapter = historyListAdapter
                        } else {
                            ll_error_layout!!.visibility = View.VISIBLE
                            recyclerView!!.visibility = View.GONE
                            pastTripTitle!!.text = resources.getString(R.string.no_offer)
                            pastTripDescription!!.text = resources.getString(R.string.no_offer_desc)
                        }
                    } else {
                        utils.showCustomAlert(getActivity(), Utilities.ALERT_WARNING, resources.getString(R.string.app_name), response.getString("message"))
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                try {
                    if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                    var json: String? = null
                    var Message: String
                    val response = error.networkResponse
                    if (response != null && response.data != null) {
                        try {
                            val errorObj = JSONObject(String(response.data))
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    utils.showCustomAlert(activity, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), errorObj.optString("error"))
                                } catch (e: Exception) {
                                    utils.showCustomAlert(activity, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), getString(R.string.something_went_wrong))
                                }
                            } else if (response.statusCode == 401) {
                                putKey(activity!!, "loggedIn", getString(R.string.False))
                            } else if (response.statusCode == 422) {
                                json = NTApplication.trimMessage(String(response.data))
                                if (json !== "" && json != null) {
                                    utils.showCustomAlert(activity, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), json)
                                } else {
                                    utils.showCustomAlert(activity, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), getString(R.string.please_try_again))
                                }
                            } else if (response.statusCode == 503) {
                                utils.showCustomAlert(activity, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), getString(R.string.server_down))
                            }
                        } catch (e: Exception) {
                            utils.showCustomAlert(activity, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), getString(R.string.something_went_wrong))
                        }
                    } else {
                        if (error is NoConnectionError) {
                            utils.showCustomAlert(activity, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), getString(R.string.oops_connect_your_internet))
                        } else if (error is NetworkError) {
                            utils.showCustomAlert(activity, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), getString(R.string.oops_connect_your_internet))
                        } else if (error is TimeoutError) {
                        }
                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                    utils.showCustomAlert(activity, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), activity!!.getString(R.string.something_went_wrong))
                }
            }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["X-localization"] = "en"
                    headers["Content-Type"] = "application/json"
                    headers["X-Requested-With"] = "XMLHttpRequest"
                    headers["Authorization"] = "" + getKey(getActivity()!!, "token_type") + " " + getKey(getActivity()!!, "access_token")
                    Utilities.PrintAPI_URL(URLHelper.OFFER_LIST, "Heaser=$headers")
                    return headers
                }
            }
            NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
        }

    private fun findViewByIdAndInitialize() {
        recyclerView = rootview!!.findViewById<View>(R.id.recyclerView) as RecyclerView
        spinner_pastTrip = rootview!!.findViewById<View>(R.id.spinner_pastTrip) as Spinner
        pastTripTitle = rootview!!.findViewById<View>(R.id.error_title) as NTBoldTextView
        pastTripDescription = rootview!!.findViewById<View>(R.id.error_description) as NTTextView
        ll_error_layout = rootview!!.findViewById<View>(R.id.ll_error_layout) as LinearLayout
    }


    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity
    }

    override fun onDetach() {
        super.onDetach()
    }

    inner class HistoryListAdapter(private val activity: FragmentActivity, private val jsonArray: JSONArray) : RecyclerView.Adapter<HistoryListAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
            val itemView = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.layout_history_list, viewGroup, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
            Utilities.print("schedule_at = " + jsonArray.optJSONObject(i).optString("schedule_at") + "Title: ", "" + jsonArray.optJSONObject(i).optString("s_address") + " image: " + jsonArray.optJSONObject(i).optString("d_address"))
            if (jsonArray.optJSONObject(i).optString("s_address") == "") {
                myViewHolder.tv_srcAddress.visibility = View.GONE
                myViewHolder.view.visibility = View.GONE
                myViewHolder.srouce_image.visibility = View.GONE
            } else {
                myViewHolder.tv_srcAddress.visibility = View.VISIBLE
                myViewHolder.view.visibility = View.VISIBLE
                myViewHolder.srouce_image.visibility = View.VISIBLE
                myViewHolder.tv_srcAddress.text = jsonArray.optJSONObject(i).optString("s_address")
            }
            myViewHolder.tv_destAddress.text = jsonArray.optJSONObject(i).optString("d_address")
            myViewHolder.tv_booking_id.text = jsonArray.optJSONObject(i).optString("booking_id")
            if (jsonArray.optJSONObject(i).optString("schedule_at").equals("", ignoreCase = true) || jsonArray.optJSONObject(i).optString("schedule_at").equals("null", ignoreCase = true)) {
                myViewHolder.tv_tripDate.text = jsonArray.optJSONObject(i).optString("assigned_at")
            } else {
                myViewHolder.tv_tripDate.text = jsonArray.optJSONObject(i).optString("schedule_at")
            }
            myViewHolder.tv_type.text = jsonArray.optJSONObject(i).optString("service_name")
            Picasso.with(activity).load(jsonArray.optJSONObject(i).optString("service_image")).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(myViewHolder.service_image)
            myViewHolder.itemView.setOnClickListener {
                val id = jsonArray.optJSONObject(i).optString("id")
                Log.d("driverAPI ID :", " $id")
                putKey(getActivity()!!, "Item_id", id)
                val intentHistory = Intent(activity, ActivityHistoryDetail::class.java)
                intentHistory.putExtra("trip_tag", "NEW")
                startActivity(intentHistory)
            }
        }

        override fun getItemCount(): Int {
            return jsonArray.length()
        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var tv_destAddress: NTTextView
            var tv_srcAddress: NTTextView
            var tv_booking_id: NTBoldTextView
            var tv_tripDate: NTBoldTextView
            var tv_type: NTBoldTextView
            var service_image: ImageView
            var srouce_image: ImageView
            var view: View

            init {
                view = itemView.findViewById(R.id.view) as View
                srouce_image = itemView.findViewById<View>(R.id.srouce_image) as ImageView
                service_image = itemView.findViewById<View>(R.id.service_image) as ImageView
                tv_type = itemView.findViewById<View>(R.id.tv_type) as NTBoldTextView
                tv_booking_id = itemView.findViewById<View>(R.id.tv_booking_id) as NTBoldTextView
                tv_tripDate = itemView.findViewById<View>(R.id.tv_tripDate) as NTBoldTextView
                tv_destAddress = itemView.findViewById<View>(R.id.tv_destAddress) as NTTextView
                tv_srcAddress = itemView.findViewById<View>(R.id.tv_srcAddress) as NTTextView
            }
        }
    }
}