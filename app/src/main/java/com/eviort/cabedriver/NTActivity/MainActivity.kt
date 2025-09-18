package com.eviort.cabedriver.NTActivity

import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.graphics.Typeface
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.text.Spannable
import android.text.SpannableString
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.eviort.cabedriver.NTApplication
import com.eviort.cabedriver.NTCustomView.NTBoldTextView
import com.eviort.cabedriver.NTCustomView.NTCircularImageView
import com.eviort.cabedriver.NTCustomView.NTTextView
import com.eviort.cabedriver.NTFragment.FragmentHome
import com.eviort.cabedriver.NTFragment.FragmentHome.Companion.mServiceConnection
import com.eviort.cabedriver.NTHelper.LoadingDialog
import com.eviort.cabedriver.NTHelper.SharedHelper
import com.eviort.cabedriver.NTHelper.URLHelper
import com.eviort.cabedriver.NTUtilites.CustomTypefaceSpan
import com.eviort.cabedriver.NTUtilites.GPSTracker
import com.eviort.cabedriver.NTUtilites.LocationUpdatesService
import com.eviort.cabedriver.NTUtilites.Utilities
import com.eviort.cabedriver.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtil
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    var goOnlineBtn: ImageView? = null
    private var timer: Timer? = null
    var context: Context = this@MainActivity
    var activity: Activity = this@MainActivity
    var navItemIndex = 0

    var CURRENT_TAG = TAG_HOME
    private var navigationView: NavigationView? = null
    private var drawer: DrawerLayout? = null
    private var navHeader: View? = null
    var push = false
    private var imgProfile: NTCircularImageView? = null
    private var imgMainProfile: NTCircularImageView? = null
    private var txtViewProfile: TextView? = null
    private var txtName: NTBoldTextView? = null
    private val toolbar: Toolbar? = null
    var gps: GPSTracker? = null
    private var mHandler: Handler? = null
    private val drawerToggle: ActionBarDrawerToggle? = null
    private val shouldLoadHomeFragOnBackPress = true
    var mainLayout: LinearLayout? = null
    var errorLayout: LinearLayout? = null
    var ll_street_ride: LinearLayout? = null
    var ll_offline: CoordinatorLayout? = null
    var status = 0
    var img_trips: ImageView? = null
    var img_earnings: ImageView? = null
    var img_street_ride: ImageView? = null
    var img_summary: ImageView? = null
    var loadingDialog: LoadingDialog? = null
    var footer_container: LinearLayout? = null
    var extras: Bundle? = null
    private var notificationManager: NotificationManager? = null

    companion object {
        var navController: NavController? = null
        var mainactivity: AppCompatActivity? = null
        private const val TAG_HOME = "home"
        private const val TAG_PAYMENT = "payments"
        var ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469
        var fl_offline: RelativeLayout? = null
        var fl_online: LinearLayout? = null
        var ll_bottomMain: LinearLayout? = null
        var img_menu: ImageView? = null
        var offline: ImageView? = null
        var rv_tool: RelativeLayout? = null

        @JvmStatic
        fun refresh() {

            /*   val bundle = Bundle()
               bundle.putBoolean("push", false)
               navController!!.navigate(R.id.fragmentHome, bundle)*/
            val id = navController!!.currentDestination?.id
            navController!!.popBackStack(id!!, true)
            navController!!.navigate(id)
        }

        @JvmStatic
        fun showlayoutsOffline() {
            rv_tool!!.visibility = View.GONE
            ll_bottomMain!!.visibility = View.GONE
            fl_offline!!.visibility = View.VISIBLE
        }

        @JvmStatic
        fun showlayoutsOnline() {
            fl_offline!!.visibility = View.GONE
            rv_tool!!.visibility = View.VISIBLE
            ll_bottomMain!!.visibility = View.VISIBLE
            fl_online!!.visibility = View.VISIBLE
            Handler().postDelayed({
                hidelayoutsOffline()
            }, 5000)
        }

        @JvmStatic
        fun hidelayoutsOffline() {
            rv_tool!!.visibility = View.VISIBLE
            ll_bottomMain!!.visibility = View.VISIBLE
            fl_offline!!.visibility = View.GONE
            fl_online!!.visibility = View.GONE
        }

        @JvmStatic
        fun showlayouts() {
            img_menu!!.isEnabled = true
            offline!!.isEnabled = true
            rv_tool!!.visibility = View.VISIBLE
            ll_bottomMain!!.visibility = View.VISIBLE
        }

        @JvmStatic
        fun showlayoutsRefresh() {
            val id = navController!!.currentDestination?.id
            navController!!.popBackStack(id!!, true)
            navController!!.navigate(id)
            img_menu!!.isEnabled = true
            offline!!.isEnabled = true
            rv_tool!!.visibility = View.VISIBLE
            ll_bottomMain!!.visibility = View.VISIBLE
        }

        @JvmStatic
        fun showlayoutsRefreshh() {
            val id = navController!!.currentDestination?.id
            navController!!.popBackStack(id!!, true)
            navController!!.navigate(id)
            val bundle = Bundle()
            bundle.putBoolean("push", true)
            navController!!.navigate(R.id.fragmentHome, bundle)
            img_menu!!.isEnabled = true
            offline!!.isEnabled = true
            rv_tool!!.visibility = View.VISIBLE
            ll_bottomMain!!.visibility = View.VISIBLE
        }

        @JvmStatic
        fun disable() {
            img_menu!!.isEnabled = false
            offline!!.isEnabled = false
            ll_bottomMain!!.visibility = View.GONE
        }

        @JvmStatic
        fun hidelayouts() {
            rv_tool!!.visibility = View.GONE
            ll_bottomMain!!.visibility = View.GONE
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        setContentView(R.layout.activity_main)
        mainactivity = this@MainActivity
        mHandler = Handler()
        extras = intent.extras
        try {
            System.gc()
            Runtime.getRuntime().gc()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        loadingDialog = LoadingDialog(this@MainActivity)
        rv_tool = findViewById<View>(R.id.rv_tool) as RelativeLayout
        drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        navigationView = findViewById<View>(R.id.nav_view) as NavigationView
        mainLayout = findViewById<View>(R.id.mainActivity_mainLayout) as LinearLayout
        errorLayout = findViewById<View>(R.id.mainActivity_error_layout) as LinearLayout
        ll_street_ride = findViewById<View>(R.id.ll_street_ride) as LinearLayout
        ll_bottomMain = findViewById<View>(R.id.ll_bottomMain) as LinearLayout
        offline = findViewById<View>(R.id.offline) as ImageView
        navHeader = navigationView!!.getHeaderView(0)
        fl_offline = findViewById<View>(R.id.fl_offline) as RelativeLayout
        fl_online = findViewById<View>(R.id.fl_online) as LinearLayout
        img_menu = findViewById<View>(R.id.img_menu) as ImageView
        footer_container = findViewById<View>(R.id.footer_container) as LinearLayout
        img_trips = findViewById<View>(R.id.img_trips) as ImageView
        img_earnings = findViewById<View>(R.id.img_earnings) as ImageView
        img_street_ride = findViewById<View>(R.id.img_street_ride) as ImageView
        img_summary = findViewById<View>(R.id.img_summary) as ImageView
        ll_offline = findViewById<View>(R.id.ll_offline) as CoordinatorLayout
        txtName = navHeader!!.findViewById<View>(R.id.usernameTxtt) as NTBoldTextView
        txtViewProfile = navHeader!!.findViewById<View>(R.id.view_profile) as TextView
        imgProfile = navHeader!!.findViewById<View>(R.id.img_profile) as NTCircularImageView
        imgMainProfile = findViewById<View>(R.id.img_main_profile) as NTCircularImageView
        if (SharedHelper.getKey(this, "profile_one")!!.isEmpty()) {
            getProfile()
        } else {
            loadNavHeader()
        }

        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager!!.cancelAll()
        try {
            Utilities.printAPI_Response("Notfication Main Activity" + Utilities.r)
            if (Utilities.r != null) {
                Utilities.printAPI_Response("Notfication Main Activity")
                Utilities.r.stop()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        if (extras != null) {
            push = extras!!.getBoolean("push")
            unlockScreen()
        }
        drawer!!.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        // loadNavHeader();
        txtViewProfile!!.setOnClickListener {
            drawer!!.closeDrawers()
            val intentProfile = Intent(activity, ActivityProfile::class.java)
            intentProfile.putExtra("tag", "personal")
            startActivity(intentProfile)
        }
        gps = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            GPSTracker(this@MainActivity)
        } else {
            GPSTracker(this@MainActivity)
        }
        if (gps!!.canGetLocation()) {
            status = GooglePlayServicesUtil
                    .isGooglePlayServicesAvailable(applicationContext)
            if (status == ConnectionResult.SUCCESS) {
                val current_lattitude = gps!!.latitude
                val current_longitude = gps!!.longitude
                Log.d("dashlatlongon", "" + current_lattitude + "-"
                        + current_longitude)
            }
        } else {
            //gps!!.showSettingsAlert()
            /* showErrorLayout("GPS");*/
            requestDeviceLocationSettings()
        }

        fl_offline!!.isClickable = false
        ll_offline!!.isClickable = false
        goOnlineBtn = fl_offline!!.findViewById<View>(R.id.online) as ImageView
        goOnlineBtn!!.setOnClickListener { goOnline() }
        footer_container!!.setOnClickListener {
            val downloadIntent = Intent(Intent.ACTION_VIEW, Uri.parse(URLHelper.CUSTOMER_APP_URL))
            startActivity(downloadIntent)
        }
        fl_offline!!.setOnTouchListener(object : OnTouchListener {
            private var mDownX = 0f
            private var mDownY = 0f
            private val SCROLL_THRESHOLD = 10f
            private var isOnClick = false
            override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                when (motionEvent.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_DOWN -> {
                        mDownX = motionEvent.x
                        mDownY = motionEvent.y
                        isOnClick = true
                    }
                    MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> if (isOnClick) {


                        //TODO onClick code
                    } else {

                        //fl_offline.dispatchTouchEvent(motionEvent);
                    }
                    MotionEvent.ACTION_MOVE -> if (Math.abs(mDownX - motionEvent.x) > SCROLL_THRESHOLD || Math.abs(mDownY - motionEvent.y) > SCROLL_THRESHOLD) {
                        isOnClick = false

                        // fl_offline.dispatchTouchEvent(motionEvent);
                    }
                    else -> {
                    }
                }
                return true
            }
        })
        setOnclicklistener()
        setUpNavigationView()
    }

    fun loadNavHeader() {
        if (SharedHelper.getKey(context, "first_name") != "null") {
            txtName!!.text = SharedHelper.getKey(context, "first_name")
        } else {
            txtName!!.text = "Name"
        }
        if (!SharedHelper.getKey(context, "picture")?.isEmpty()!!) {
            Picasso.with(context)
                    .load(SharedHelper.getKey(context, "picture"))
                    .placeholder(R.drawable.ic_driver_black) // optional
                    .into(imgProfile)
            Picasso.with(context)
                    .load(SharedHelper.getKey(context, "picture"))
                    .placeholder(R.drawable.ic_driver_black) // optional
                    .into(imgMainProfile)
        } else {
            Picasso.with(context)
                    .load(R.drawable.ic_driver_black)
                    .placeholder(R.drawable.ic_driver_black) // optional
                    .into(imgProfile)
            Picasso.with(context)
                    .load(SharedHelper.getKey(context, "picture"))
                    .placeholder(R.drawable.ic_driver_black) // optional
                    .into(imgMainProfile)
        }
    }

    private fun unlockScreen() {
        val window = this.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
    }

    fun getProfile() {
        //  utils.PrintAPI_URL(URLHelper.GET_PROFILE_DETAILS, "GET");
        println("DriverAPI Header: " + "X-Requested-With : XMLHttpRequest " + "Authorization : " + SharedHelper.getKey(context, "access_token"))
        val `object` = JSONObject()
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.GET, URLHelper.GET_PROFILE_DETAILS, `object`, Response.Listener { response ->
            println("DriverAPI response : $response")
            SharedHelper.putKey(context, "id", response.optString("id"))
            SharedHelper.putKey(context, "first_name", response.optString("name"))
            SharedHelper.putKey(context, "email", response.optString("email"))
            if (response.optString("avatar").startsWith("http"))
                SharedHelper.putKey(context, "picture", response.optString("avatar"))
            else
                SharedHelper.putKey(context, "picture", URLHelper.base + "storage/" + response.optString("avatar"))
            SharedHelper.putKey(context, "gender", response.optString("gender"))
            SharedHelper.putKey(context, "mobile", response.optString("mobile"))
            SharedHelper.putKey(context, "currency", response.optString("currency"))
            SharedHelper.putKey(context, "approval_status", response.optString("status"))
            //                    SharedHelper.putKey(context, "wallet_balance", response.optString("wallet_balance"));
//                    SharedHelper.putKey(context, "payment_mode", response.optString("payment_mode"));
            SharedHelper.putKey(context, "loggedIn", getString(R.string.True))
            loadNavHeader()
            SharedHelper.putKey(this, "profile_one", "1")
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
        }, Response.ErrorListener { error ->
            try {
                var json: String? = null
                var Message: String
                val response = error.networkResponse
                if (response != null && response.data != null) {
                    try {
                        val errorObj = JSONObject(String(response.data))
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                Utilities.showToast(this@MainActivity, errorObj.optString("message"))
                            } catch (e: Exception) {
                                Utilities.showToast(this@MainActivity, getString(R.string.something_went_wrong))
                            }
                        } else if (response.statusCode == 401) {
                            SharedHelper.putKey(context, "loggedIn", getString(R.string.False))
                        } else if (response.statusCode == 422) {
                            json = NTApplication.trimMessage(String(response.data))
                            if (json !== "" && json != null) {
                                Utilities.showToast(this@MainActivity, json)
                            } else {
                                Utilities.showToast(this@MainActivity, getString(R.string.please_try_again))
                            }
                        } else if (response.statusCode == 503) {
                            Utilities.showToast(this@MainActivity, getString(R.string.server_down))
                        }
                    } catch (e: Exception) {
                        Utilities.showToast(this@MainActivity, getString(R.string.something_went_wrong))
                    }
                } else {
                    if (error is NoConnectionError) {
                        Utilities.showToast(this@MainActivity, getString(R.string.oops_connect_your_internet))
                    } else if (error is NetworkError) {
                        Utilities.showToast(this@MainActivity, getString(R.string.oops_connect_your_internet))
                    } else if (error is TimeoutError) {
                        getProfile()
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
                //  headers["X-localization"] = SharedHelper.getKey(context, "lang")
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "Bearer " + SharedHelper.getKey(context, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    private fun setOnclicklistener() {
        img_menu!!.setOnClickListener(this)
        img_trips!!.setOnClickListener(this)
        img_summary!!.setOnClickListener(this)
        img_earnings!!.setOnClickListener(this)
        img_street_ride!!.setOnClickListener(this)
        offline!!.setOnClickListener(this)
        // ll_street_ride.setOnClickListener(this);
    }

    fun requestDeviceLocationSettings() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
            val state = locationSettingsResponse.locationSettingsStates

            val label =
                    "GPS >> (Present: ${state!!.isGpsPresent}  | Usable: ${state.isGpsUsable} ) \n\n" +
                            "Network >> ( Present: ${state!!.isNetworkLocationPresent} | Usable: ${state.isNetworkLocationUsable} ) \n\n" +
                            "Location >> ( Present: ${state!!.isLocationPresent} | Usable: ${state.isLocationUsable} )"
            Log.e("status",""+"success_location")
          //  load()
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                            this@MainActivity,
                            100
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                    // textView.text = sendEx.message.toString()
                }
            }
        }
    }
    fun showLogoutDialog() {
        val builder3 = AlertDialog.Builder(this@MainActivity)
        val inflater3 = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout3 = inflater3.inflate(R.layout.layout_alert, null)
        builder3.setCancelable(false)
        builder3.setView(layout3)
        builder3.create()
        val alertDialog3 = builder3.create()
        val tv_alert_title = layout3.findViewById<View>(R.id.tv_alert_title) as NTTextView
        val tv_alert_desc = layout3.findViewById<View>(R.id.tv_alert_desc) as NTTextView
        val tv_alert_okBtn = layout3.findViewById<View>(R.id.tv_alert_okBtn) as NTTextView
        val tv_alert_noBtn = layout3.findViewById<View>(R.id.tv_alert_noBtn) as NTTextView
        tv_alert_title.text = resources.getString(R.string.alert_logout_title)
        tv_alert_desc.text = resources.getString(R.string.logout_alert)
        tv_alert_okBtn.text = resources.getString(R.string.ok)
        tv_alert_noBtn.text = resources.getString(R.string.cancel)
        tv_alert_okBtn.setOnClickListener {
         //   SharedHelper.putKey(context, "access_token", " ")
            logout()
            alertDialog3.dismiss()
        }
        tv_alert_noBtn.setOnClickListener { alertDialog3.dismiss() }
        alertDialog3.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog3.window!!.attributes.windowAnimations = R.style.dialog_animation
        alertDialog3.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog3.show()
    }

    private fun logout() {
        if (loadingDialog != null) loadingDialog!!.showDialog()
        Utilities.PrintAPI_URL(URLHelper.LOGOUT, "POST")
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URLHelper.LOGOUT, null, Response.Listener {
            if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
            val beginIntent = Intent(this@MainActivity, ActivityBegin::class.java)
            beginIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK or
                    Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(beginIntent)
            SharedHelper.putKey(activity, "current_status", "")
            SharedHelper.putKey(activity, "loggedIn", getString(R.string.False))
            SharedHelper.putKey(activity, "email", "")
            SharedHelper.putKey(activity, "showforegroundLocation", getString(R.string.False))
         //   SharedHelper.putKey(context, "access_token"," ")
            FragmentHome.handleCheckStatus?.removeCallbacksAndMessages(null)
            activity!!.bindService(
                    Intent(activity, LocationUpdatesService::class.java), mServiceConnection,
                    Context.BIND_AUTO_CREATE
            )
            activity.finish()
        }, Response.ErrorListener { error ->
            try {
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
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
                    Toast.makeText(activity, resources.getString(R.string.error_parse), Toast.LENGTH_LONG).show()
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
                headers["Authorization"] = "" + SharedHelper.getKey(activity, "token_type") + " " + SharedHelper.getKey(activity, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    private fun setUpNavigationView() {
        navController = Navigation.findNavController(this, R.id.navHostFragment)
        navigationView!!.itemIconTintList = null
        /*  NavigationUI.setupActionBarWithNavController(this, navController, drawer);*/NavigationUI.setupWithNavController(navigationView!!, navController!!)
        navigationView!!.setNavigationItemSelectedListener(this@MainActivity)
    }

    private fun applyFontToMenuItem(mi: MenuItem) {
        val font = Typeface.createFromAsset(assets, "fonts/OpenSans-Regular.ttf")
        val mNewTitle = SpannableString(mi.title)
        mNewTitle.setSpan(CustomTypefaceSpan("", font), 0, mNewTitle.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        mi.title = mNewTitle
    }

    override fun onBackPressed() {

        if (CURRENT_TAG.equals("home")) {
            return;
        } else {
            super@MainActivity.onBackPressed()
        }

    }

    override fun onDestroy() {
        SharedHelper.putKey(activity, "showforegroundLocation", getString(R.string.False))
        activity!!.bindService(
                Intent(activity, LocationUpdatesService::class.java), mServiceConnection,
                Context.BIND_AUTO_CREATE
        )
        super.onDestroy()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(Navigation.findNavController(this, R.id.navHostFragment), drawer)
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        try {
            menuItem.isChecked = true
            drawer!!.closeDrawers()
            val id = menuItem.itemId
            when (id) {
                R.id.nav_past_trip -> navController!!.navigate(R.id.fragmentHistory)
                R.id.nav_street_ride -> navController!!.navigate(R.id.fragmentStreetRide)
                // R.id.nav_wallet -> navController!!.navigate(R.id.fragmentWallet)
                R.id.nav_earnings -> navController!!.navigate(R.id.fragmentEarnings)
                R.id.nav_summary -> navController!!.navigate(R.id.fragmentSummary)

                R.id.nav_about -> navController!!.navigate(R.id.fragmentAbout)
//                R.id.nav_language -> navController!!.navigate(R.id.fragmentLanguage)
                // R.id.nav_emergency -> navController!!.navigate(R.id.fragmentEmergency)
                R.id.nav_notification -> navController!!.navigate(R.id.fragmentNotification)
                /*R.id.nav_upload_docs -> {
                    val intent1 = Intent(activity, UploadDocument::class.java)
                    activity!!.startActivity(intent1)
                }*/
                R.id.nav_logout -> showLogoutDialog()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return true
    }

    override fun onClick(v: View) {
        val id = v.id
        when (id) {
            R.id.img_trips -> {
                img_trips!!.setImageResource(R.drawable.ic_trips)
                img_street_ride!!.setImageResource(R.drawable.ic_street_ride_grey)
                img_earnings!!.setImageResource(R.drawable.ic_earnings_grey)
                img_summary!!.setImageResource(R.drawable.ic_summary_grey)
                val bundle = Bundle()
                bundle.putBoolean("push", push)
                navController!!.navigate(R.id.fragmentHome, bundle)
            }
            R.id.img_earnings -> {
                img_earnings!!.setImageResource(R.drawable.ic_earnings)
                img_trips!!.setImageResource(R.drawable.ic_trips_grey)
                img_street_ride!!.setImageResource(R.drawable.ic_street_ride_grey)
                img_summary!!.setImageResource(R.drawable.ic_summary_grey)
                navController!!.navigate(R.id.fragmentEarnings)
            }
            R.id.img_street_ride -> {
                img_earnings!!.setImageResource(R.drawable.ic_earnings_grey)
                img_trips!!.setImageResource(R.drawable.ic_trips_grey)
                img_summary!!.setImageResource(R.drawable.ic_summary_grey)
                img_street_ride!!.setImageResource(R.drawable.ic_street_ride)
                navController!!.navigate(R.id.fragmentStreetRide)
            }
            R.id.img_summary -> {
                img_earnings!!.setImageResource(R.drawable.ic_earnings_grey)
                img_trips!!.setImageResource(R.drawable.ic_trips_grey)
                img_street_ride!!.setImageResource(R.drawable.ic_street_ride_grey)
                img_summary!!.setImageResource(R.drawable.ic_summary)
                navController!!.navigate(R.id.fragmentSummary)
            }
            R.id.img_menu -> {
                //  getProfile()
                drawer!!.openDrawer(Gravity.LEFT)
            }
            R.id.offline -> goOffline()
        }
    }

    fun goOffline() {
        val builder4 = AlertDialog.Builder(this@MainActivity)
        val inflater4 = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout4 = inflater4.inflate(R.layout.alert_driver_detail, null)
        builder4.setCancelable(false)
        builder4.setView(layout4)
        builder4.create()
        val alertDialog4 = builder4.create()
        val tv_alert_title2 = layout4.findViewById<View>(R.id.tv_alert_title) as NTTextView
        val tv_alert_desc2 = layout4.findViewById<View>(R.id.tv_alert_desc) as NTTextView
        val tv_alert_okBtn2 = layout4.findViewById<View>(R.id.tv_alert_okBtn) as NTTextView
        val tv_alert_noBtn2 = layout4.findViewById<View>(R.id.tv_alert_noBtn) as NTTextView
        tv_alert_title2.text = resources.getString(R.string.f_alert_offline_title)
        tv_alert_desc2.text = resources.getString(R.string.f_alert_offline_description)
        tv_alert_okBtn2.text = resources.getString(R.string.f_go_offline)
        tv_alert_noBtn2.text = resources.getString(R.string.f_alert_no)
        tv_alert_okBtn2.setOnClickListener {
            GoToOfflineStatus()
            alertDialog4.dismiss()
        }
        tv_alert_noBtn2.setOnClickListener { alertDialog4.dismiss() }
        alertDialog4.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog4.window!!.attributes.windowAnimations = R.style.dialog_animation
        alertDialog4.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog4.show()
    }

    private fun GoToOfflineStatus() {
        val `object` = JSONObject()
        try {
            `object`.put("service_status", "offline")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Utilities.PrintAPI_URL(URLHelper.DRIVER_STATUS + "   " + "token_type : " + SharedHelper.getKey(context, "token_type") + " , " + " access_token :  " + SharedHelper.getKey(activity, "access_token"), `object`.toString())
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URLHelper.DRIVER_STATUS, `object`, Response.Listener { response ->
            Utilities.printAPI_Response(response.toString())
            if (response.toString() != null) {
                // navController.navigate(R.id.fragmentOffline);

                //fl_offline.bringToFront();
                showlayoutsOffline()

            }
        }, Response.ErrorListener { error ->
            try {
                if (error is TimeoutError) {
                    Toast.makeText(context, resources.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show()
                } else if (error is NoConnectionError) {
                    Toast.makeText(context, resources.getString(R.string.error_no_network), Toast.LENGTH_LONG).show()
                } else if (error is AuthFailureError) {
                    Toast.makeText(context, resources.getString(R.string.error_auth_failure), Toast.LENGTH_LONG).show()
                } else if (error is ServerError) {
                    Toast.makeText(context, resources.getString(R.string.error_server_connection), Toast.LENGTH_LONG).show()
                } else if (error is NetworkError) {
                    Toast.makeText(context, resources.getString(R.string.error_network), Toast.LENGTH_LONG).show()
                } else if (error is ParseError) {
                    Toast.makeText(context, resources.getString(R.string.error_parse), Toast.LENGTH_LONG).show()
                } else {
                    //utils.showCustomAlert(activity, utils.ALERT_ERROR, getResources().getString(R.string.app_name), error.getMessage());
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(context, resources.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
            }
        }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "" + SharedHelper.getKey(context, "token_type") + " " + SharedHelper.getKey(context, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }


    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName"))
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    Toast.makeText(context, "Permission Granted", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(context, "Permission Not Granted.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun goOnline() {
        val builder3 = AlertDialog.Builder(this@MainActivity)
        val inflater3 = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
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
        Utilities.PrintAPI_URL(URLHelper.DRIVER_STATUS + "   " + "token_type : " + SharedHelper.getKey(activity, "token_type") + " , " + " access_token :  " + SharedHelper.getKey(activity, "access_token"), `object`.toString())
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(Method.POST, URLHelper.DRIVER_STATUS, `object`, Response.Listener { response ->
            Utilities.printAPI_Response(response.toString())
            if (response.toString() != null) {
                if (response.optString("status").equals("active", ignoreCase = true)) {
                    SharedHelper.putKey(context, "driverOnlineFlag", "true")
                    // hidelayoutsOffline()
                    showlayoutsOnline()
                }
            }
        }, Response.ErrorListener { error ->
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
                    Toast.makeText(activity, resources.getString(R.string.error_parse), Toast.LENGTH_LONG).show()
                } else {
                    // utils.showCustomAlert(activity, utils.ALERT_ERROR, getResources().getString(R.string.app_name), error.getMessage());
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
                headers["Authorization"] = "" + SharedHelper.getKey(activity, "token_type") + " " + SharedHelper.getKey(activity, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    override fun onResume() {
        super.onResume()
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        try{
            Picasso.with(context)
                .load(SharedHelper.getKey(context, "picture"))
                .placeholder(R.drawable.ic_driver_black) // optional
                .into(imgMainProfile)

            Picasso.with(context)
                .load(SharedHelper.getKey(context, "picture"))
                .placeholder(R.drawable.ic_driver_black) // optional
                .into(imgProfile)

        }catch (e:Exception){
            e.printStackTrace()
        }

        }

    override fun onPause() {
        super.onPause()
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        /*  timer = Timer()
          //  Log.i("Main", "Invoking logout timer");
          val logoutTimeTask = LogOutTimerTask()
          timer!!.schedule(logoutTimeTask, (30 * 60000).toLong()) //auto logout in 30 minutes*/
    }

    private inner class LogOutTimerTask : TimerTask() {
        override fun run() {
            //  Toast.makeText(activity,"INACTIVE",Toast.LENGTH_SHORT).show();
            GoToOfflineStatus()
        }
    }


}