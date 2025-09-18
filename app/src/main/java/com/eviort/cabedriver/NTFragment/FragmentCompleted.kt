package com.eviort.cabedriver.NTFragment

import android.content.Intent
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
import org.json.JSONArray
import org.json.JSONException
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class FragmentCompleted : Fragment() {
    var spinner_completed: Spinner? = null
    var rv_completed: RecyclerView? = null
    var adapter: CompletedAdapter? = null
    var ll_error_layout: LinearLayout? = null
    var loadingDialog: LoadingDialog? = null
    var utils = Utilities()
    var completedAdapter: CompletedAdapter? = null
    var pastTripTitle: NTBoldTextView? = null
    var pastTripDescription: NTTextView? = null
    var rootView: View? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_completed, container, false)
        spinner_completed = rootView?.findViewById<View>(R.id.spinner_pastTrip) as Spinner
        rv_completed = rootView?.findViewById<View>(R.id.rv_completed) as RecyclerView
        ll_error_layout = rootView?.findViewById<View>(R.id.ll_error_layout) as LinearLayout
        pastTripTitle = rootView?.findViewById<View>(R.id.error_title) as NTBoldTextView
        pastTripDescription = rootView?.findViewById<View>(R.id.error_description) as NTTextView

//        adapter = new CompletedAdapter(getActivity());
//        rv_completed.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
//        rv_completed.setAdapter(adapter);
        completedTripList
        return rootView
    }

    //utils.showCustomAlert(getActivity(), utils.ALERT_ERROR, getResources().getString(R.string.app_name), error.getMessage());
    private val completedTripList: Unit
        private get() {
            if (loadingDialog != null) {
                loadingDialog!!.showDialog()
            }
            Utilities.PrintAPI_URL(URLHelper.PAST_TRIP_LIST, "GET")
            val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.GET, URLHelper.PAST_TRIP_LIST, null, Response.Listener { response ->
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                Utilities.printAPI_Response(response.toString())
                try {
                    val jsonArray = response.getJSONArray("data")
                    if (jsonArray.length() > 0) {
                        ll_error_layout!!.visibility = View.GONE
                        rv_completed!!.visibility = View.VISIBLE
                        completedAdapter = CompletedAdapter(activity, jsonArray)
                        rv_completed!!.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                        rv_completed!!.adapter = completedAdapter
                    } else {
                        ll_error_layout!!.visibility = View.VISIBLE
                        rv_completed!!.visibility = View.GONE
                        pastTripTitle!!.text = resources.getString(R.string.no_upcoming_trip)
                        pastTripDescription!!.text = resources.getString(R.string.no_upcoming_trip_desc)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                try {
                    if (error is TimeoutError) {
                        Toast.makeText(activity, resources.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show()
                    } else if (error is NoConnectionError) {
                        Toast.makeText(activity, resources.getString(R.string.error_no_network), Toast.LENGTH_LONG).show()
                    } else if (error is AuthFailureError) {
                        Toast.makeText(activity, resources.getString(R.string.error_auth_failure), Toast.LENGTH_LONG).show()
                    } else if (error is ServerError) {
                        Toast.makeText(activity, resources.getString(R.string.error_server_connection), Toast.LENGTH_LONG).show()
                    } else if (error is NetworkError) {
                        Toast.makeText(activity, resources.getString(R.string.error_network), Toast.LENGTH_LONG).show()
                    } else if (error is ParseError) {
                        //Toast.makeText(activity, resources.getString(R.string.error_parse), Toast.LENGTH_LONG).show()
                    } else {
                        //utils.showCustomAlert(getActivity(), utils.ALERT_ERROR, getResources().getString(R.string.app_name), error.getMessage());
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(activity, resources.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
                }
            }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["X-localization"] = "en"
                    headers["Content-Type"] = "application/json"
                    headers["X-Requested-With"] = "XMLHttpRequest"
                    headers["Authorization"] = "" + getKey(activity!!, "token_type") + " " + getKey(activity!!, "access_token")
                    return headers
                }
            }
            NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
        }

    inner class CompletedAdapter(private val activity: FragmentActivity?, private val jsonArray: JSONArray) : RecyclerView.Adapter<CompletedAdapter.MyViewHolder>() {
        override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): MyViewHolder {
            val itemView = LayoutInflater.from(viewGroup.context)
                    .inflate(R.layout.layout_completed_list, viewGroup, false)
            return MyViewHolder(itemView)
        }

        override fun onBindViewHolder(myViewHolder: MyViewHolder, i: Int) {
            Utilities.print("Title: ", "" + jsonArray.optJSONObject(i).optString("s_address") + " image: " + jsonArray.optJSONObject(i).optString("d_address"))
            myViewHolder.tv_srcAddress.text = jsonArray.optJSONObject(i).optString("s_address")
            myViewHolder.tv_destAddress.text = jsonArray.optJSONObject(i).optString("d_address")
            myViewHolder.tv_booking_id.text = jsonArray.optJSONObject(i).optString("booking_id")
            myViewHolder.tv_tripDate.text = utils.parseDateToddMMyyyy(jsonArray.optJSONObject(i).optString("created_at"))
            myViewHolder.tv_type.text = jsonArray.optJSONObject(i).optString("service_name")
            //            Picasso.with(getActivity()).load(jsonArray.optJSONObject(i).optString("service_image")).placeholder(R.drawable.placeholder).error(R.drawable.placeholder).into(myViewHolder.service_image);
            myViewHolder.itemView.setOnClickListener {
                val id = jsonArray.optJSONObject(i).optString("id")
                Log.d("driverAPI ID :", " $id")
                putKey(getActivity()!!, "Item_id", id)
                val intentHistory = Intent(activity, ActivityHistoryDetail::class.java)
                intentHistory.putExtra("trip_tag", "COMPLETED")
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

            init {
                service_image = itemView.findViewById<View>(R.id.service_image) as ImageView
                tv_type = itemView.findViewById<View>(R.id.tv_type) as NTBoldTextView
                tv_booking_id = itemView.findViewById<View>(R.id.tv_booking_id) as NTBoldTextView
                tv_tripDate = itemView.findViewById<View>(R.id.tv_tripDate) as NTBoldTextView
                tv_destAddress = itemView.findViewById<View>(R.id.tv_destAddress) as NTTextView
                tv_srcAddress = itemView.findViewById<View>(R.id.tv_srcAddress) as NTTextView
                //                itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//
//                        Intent completeIntent = new Intent(getActivity() , ActivityCompletedDetail.class);
//                        startActivity(completeIntent);
//
//                    }
//                });
            }
        }
    }
}