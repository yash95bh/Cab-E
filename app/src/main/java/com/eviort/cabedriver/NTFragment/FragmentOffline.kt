package com.eviort.cabedriver.NTFragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.Toast
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.eviort.cabedriver.NTActivity.ActivityBegin
import com.eviort.cabedriver.NTActivity.MainActivity
import com.eviort.cabedriver.NTApplication
import com.eviort.cabedriver.NTCustomView.NTTextView
import com.eviort.cabedriver.NTHelper.ConnectionHelper
import com.eviort.cabedriver.NTHelper.LoadingDialog
import com.eviort.cabedriver.NTHelper.SharedHelper.getKey
import com.eviort.cabedriver.NTHelper.SharedHelper.putKey
import com.eviort.cabedriver.NTHelper.URLHelper
import com.eviort.cabedriver.NTUtilites.Utilities
import com.eviort.cabedriver.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.material.snackbar.Snackbar
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class FragmentOffline : Fragment(), OnMapReadyCallback {
    var activity: Activity? = null
    var mycontext: Context? = null
    var helper: ConnectionHelper? = null
    var isInternet: Boolean? = null
    var rootView: View? = null
    var customDialog: LoadingDialog? = null
    var token: String? = null
    var goOnlineBtn: ImageView? = null

    //menu icon
    var menuIcon: ImageView? = null
    var NAV_DRAWER = 0
    var drawer: DrawerLayout? = null
    var mMap: GoogleMap? = null
    var mapFragment: SupportMapFragment? = null
    var utils = Utilities()
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
        rootView = inflater.inflate(R.layout.fragment_offline, container, false)
        findViewByIdAndInitialize()
        return rootView
    }

    fun findViewByIdAndInitialize() {
        helper = ConnectionHelper(activity!!)
        isInternet = helper!!.isConnectingToInternet
        token = getKey(activity!!, "access_token")
        goOnlineBtn = rootView!!.findViewById<View>(R.id.online) as ImageView
        goOnlineBtn!!.setOnClickListener { goOnline() }
        drawer = activity!!.findViewById<View>(R.id.drawer_layout) as DrawerLayout

        /* menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NAV_DRAWER == 0) {
                    drawer.openDrawer(Gravity.LEFT);
                }else {
                    NAV_DRAWER = 0;
                    drawer.closeDrawers();
                }
            }
        });*/
    }

    private fun initMap() {
        val fm = childFragmentManager
        mapFragment = fm.findFragmentById(R.id.provider_map) as SupportMapFragment?
        mapFragment!!.getMapAsync(this)
    }

    fun goOnline() {
        val builder3 = AlertDialog.Builder(getActivity())
        val inflater3 = getActivity()!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout3 = inflater3.inflate(R.layout.alert_driver_detail, null)
        builder3.setCancelable(false)
        builder3.setView(layout3)
        builder3.create()
        val alertDialog3 = builder3.create()
        val tv_alert_title = layout3.findViewById<View>(R.id.tv_alert_title) as NTTextView
        val tv_alert_desc = layout3.findViewById<View>(R.id.tv_alert_desc) as NTTextView
        val tv_alert_okBtn = layout3.findViewById<View>(R.id.tv_alert_okBtn) as NTTextView
        val tv_alert_noBtn = layout3.findViewById<View>(R.id.tv_alert_noBtn) as NTTextView
        tv_alert_title.text = resources.getString(R.string.f_alert_online_title)
        tv_alert_desc.text = resources.getString(R.string.f_alert_online_description)
        tv_alert_okBtn.text = resources.getString(R.string.f_go_online)
        tv_alert_noBtn.text = resources.getString(R.string.f_alert_no)
        tv_alert_okBtn.setOnClickListener {
            GOToOnlineStatus()
            alertDialog3.dismiss()
        }
        tv_alert_noBtn.setOnClickListener { alertDialog3.dismiss() }
        alertDialog3.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog3.window!!.attributes.windowAnimations = R.style.dialog_animation
        alertDialog3.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog3.show()
    }

    private fun GOToOnlineStatus() {
        val `object` = JSONObject()
        try {
            `object`.put("service_status", "active")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Utilities.PrintAPI_URL(URLHelper.DRIVER_STATUS + "   " + "token_type : " + getKey(getActivity()!!, "token_type") + " , " + " access_token :  " + getKey(getActivity()!!, "access_token"), `object`.toString())
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URLHelper.DRIVER_STATUS, `object`, Response.Listener { response ->
            Utilities.printAPI_Response(response.toString())
            if (response.toString() != null) {
                if (response.optString("status").equals("active", ignoreCase = true)) {
                    val intent = Intent(activity, MainActivity::class.java)
                    activity!!.startActivity(intent)
                }
            }
        }, Response.ErrorListener { error ->
            try {
                if (error is TimeoutError) {
                    Toast.makeText(getActivity(), resources.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show()
                } else if (error is NoConnectionError) {
                    Toast.makeText(getActivity(), resources.getString(R.string.error_no_network), Toast.LENGTH_LONG).show()
                } else if (error is AuthFailureError) {
                    Toast.makeText(getActivity(), resources.getString(R.string.error_auth_failure), Toast.LENGTH_LONG).show()
                } else if (error is ServerError) {
                    Toast.makeText(getActivity(), resources.getString(R.string.error_server_connection), Toast.LENGTH_LONG).show()
                } else if (error is NetworkError) {
                    Toast.makeText(getActivity(), resources.getString(R.string.error_network), Toast.LENGTH_LONG).show()
                } else if (error is ParseError) {
                    //  Toast.makeText(getActivity(), resources.getString(R.string.error_parse), Toast.LENGTH_LONG).show()
                } else {
                    // utils.showCustomAlert(getActivity(), utils.ALERT_ERROR, getResources().getString(R.string.app_name), error.getMessage());
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(getActivity(), resources.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
            }
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "" + getKey(getActivity()!!, "token_type") + " " + getKey(getActivity()!!, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    fun displayMessage(toastString: String) {
        Utilities.print("displayMessage", "" + toastString)
        Snackbar.make(view!!, toastString, Snackbar.LENGTH_SHORT)
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

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        //setmapType(googleMap);
        if (mMap != null) {

            // if (checkPermissions()) {
            mMap!!.uiSettings.isCompassEnabled = false
            mMap!!.isBuildingsEnabled = true
            //                mMap.setMyLocationEnabled(true);
            // }
        }
    }
}