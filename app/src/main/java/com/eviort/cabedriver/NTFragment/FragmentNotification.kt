package com.eviort.cabedriver.NTFragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.SparseBooleanArray
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.eviort.cabedriver.NTActivity.MainActivity
import com.eviort.cabedriver.NTApplication
import com.eviort.cabedriver.NTCustomView.NTBoldTextView
import com.eviort.cabedriver.NTCustomView.NTTextView
import com.eviort.cabedriver.NTHelper.LoadingDialog
import com.eviort.cabedriver.NTHelper.SharedHelper.getKey
import com.eviort.cabedriver.NTHelper.URLHelper
import com.eviort.cabedriver.NTUtilites.*
import com.eviort.cabedriver.R
import com.google.android.material.snackbar.Snackbar
import org.json.JSONArray
import org.json.JSONObject
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class FragmentNotification : Fragment() {
    var errorLayout: LinearLayout? = null
    var recyclerView: RecyclerView? = null
    var imgBack: ImageView? = null
    var rootview: View? = null
    var activity: Activity? = null

    //  var context: Context= requireContext()
    var customDialog: LoadingDialog? = null
    var utils = Utilities()

    /* override fun onAttach(context: Context) {
         super.onAttach(context)
         this.context = context
     }
 */
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        Utilities.setLocale(activity, getKey(activity!!, "lang"))
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_notification, container, false)
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
        errorLayout = rootview?.findViewById<View>(R.id.errorLayout) as LinearLayout
        recyclerView = rootview?.findViewById<View>(R.id.recyclerView) as RecyclerView
        imgBack = rootview?.findViewById<View>(R.id.backArrow) as ImageView
        FragmentHome.isRunning = false
        notificationContent
        imgBack!!.setOnClickListener { pop() }
        return rootview
    }

    // customDialog.setCancelable(false);
    val notificationContent: Unit
        get() {
            customDialog = LoadingDialog(getActivity()!!)
            // customDialog.setCancelable(false);
            if (customDialog != null) customDialog!!.showDialog()
            Utilities.print(URLHelper.GET_NOTIFICATION + getKey(activity!!, "id"), "GET")
            val jsonArrayRequest: JsonObjectRequest = object : JsonObjectRequest(URLHelper.GET_NOTIFICATION + getKey(activity!!, "id"), null, Response.Listener { response ->
                if (customDialog != null && customDialog!!.isShowing) customDialog!!.hideDialog()
                Utilities.printAPI_Response("Notification = $response")
                val requestStatusCheck = response.optJSONArray("pushMessage")
                Utilities.printAPI_Response("Notification = $requestStatusCheck")
                if (requestStatusCheck.length() > 0) {
                    recyclerView!!.visibility = View.VISIBLE
                    errorLayout!!.visibility = View.GONE
                    val notificationListAdapter = NotificationListAdapter(requestStatusCheck)
                    recyclerView!!.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
                    recyclerView!!.adapter = notificationListAdapter
                } else {
                    recyclerView!!.visibility = View.GONE
                    errorLayout!!.visibility = View.VISIBLE
                }
            }, Response.ErrorListener { error ->
                if (customDialog != null && customDialog!!.isShowing) customDialog!!.hideDialog()
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
                        } else if (response.statusCode == 401 || response.statusCode == 422) {
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
                        notificationContent
                    }
                }
            }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    headers["X-localization"] = getKey(activity!!, "lang")!!
                    headers["X-Requested-With"] = "XMLHttpRequest"
                    headers["Authorization"] = "" + "Bearer " + getKey(activity!!, "access_token")
                    return headers
                }
            }
            NTApplication.getInstance().addToRequestQueue(jsonArrayRequest)
        }

    private inner class NotificationListAdapter(var jsonArray: JSONArray) : RecyclerView.Adapter<NotificationListAdapter.MyViewHolder>() {
        private val selectedItems: SparseBooleanArray? = null
        var selectedPosition = 0
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
            @SuppressLint("InflateParams") val view = LayoutInflater.from(getActivity()).inflate(R.layout.notification_list_layout, null)
            return MyViewHolder(view)
        }

        override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
            holder.txtTitle.text = jsonArray.optJSONObject(position).optString("title")
            holder.txtDescription.text = jsonArray.optJSONObject(position).optString("message")
            holder.date_picker_day.text = parseDateToEEEddMMYYY(jsonArray.optJSONObject(position).optString("created_at"))


//            holder.txtLink.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//
//                   Integer.parseInt(view.getTag().toString());
//                    SharedHelper.putKey(context, "service_type", "" + jsonArray.optJSONObject(Integer.parseInt(view.getTag().toString())).optString("id"));
//
//                    notifyDataSetChanged();
//                    utils.print("service_type", "" + SharedHelper.getKey(context, "service_type"));
//                    utils.print("Service name", "" + SharedHelper.getKey(context, "name"));
//
//                }
//            });
        }

        override fun getItemCount(): Int {
            return jsonArray.length()
        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var txtTitle: NTBoldTextView
            var txtDescription: NTTextView
            var date_picker_day: NTTextView

            init {
                txtTitle = itemView.findViewById<View>(R.id.txtTitle) as NTBoldTextView
                txtDescription = itemView.findViewById<View>(R.id.txtDescription) as NTTextView
                date_picker_day = itemView.findViewById<View>(R.id.date_picker_day) as NTTextView
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Utilities.setLocale(activity, getKey(context!!, "lang"))
    }

    fun parseDateToEEEddMMYYY(time: String?): String? {
        val inputPattern = "yyyy-MM-dd'T'hh:mm:ss.SSSSSS'Z'"
        val outputPattern = "EEE, dd MMM yyyy hh:mm a"
        val inputFormat = SimpleDateFormat(inputPattern)
        val outputFormat = SimpleDateFormat(outputPattern)
        var date: Date? = null
        var str: String? = null
        try {
            date = inputFormat.parse(time)
            str = outputFormat.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        return str
    }

    fun displayMessage(toastString: String?) {
        Snackbar.make(rootview!!, toastString!!, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show()
    }

    fun pop() {
        /*val fm = getActivity()!!.supportFragmentManager
        val count = fm.backStackEntryCount
        for (i in 0..count) {
            fm.popBackStackImmediate()
        }*/
        val mainIntent = Intent(getActivity()!!, MainActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(mainIntent)
    }
}