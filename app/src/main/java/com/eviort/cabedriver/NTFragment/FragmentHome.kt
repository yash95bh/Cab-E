package com.eviort.cabedriver.NTFragment

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.app.NotificationManager
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Resources
import android.content.res.Resources.NotFoundException
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.location.Location
import android.location.LocationManager
import android.media.AudioManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.view.inputmethod.InputMethodManager
import android.widget.*
import android.widget.Chronometer.OnChronometerTickListener
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.Toast.makeText
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.GoogleMap.OnCameraMoveListener
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.eviort.cabedriver.NTActivity.ActivityBegin
import com.eviort.cabedriver.NTActivity.MainActivity
import com.eviort.cabedriver.NTActivity.MainActivity.Companion.disable
import com.eviort.cabedriver.NTActivity.MainActivity.Companion.hidelayouts
import com.eviort.cabedriver.NTActivity.MainActivity.Companion.hidelayoutsOffline
import com.eviort.cabedriver.NTActivity.MainActivity.Companion.refresh
import com.eviort.cabedriver.NTActivity.MainActivity.Companion.showlayouts
import com.eviort.cabedriver.NTActivity.MainActivity.Companion.showlayoutsOffline
import com.eviort.cabedriver.NTActivity.MainActivity.Companion.showlayoutsRefresh
import com.eviort.cabedriver.NTActivity.MainActivity.Companion.showlayoutsRefreshh
import com.eviort.cabedriver.NTActivity.WaitingForApproval
import com.eviort.cabedriver.NTAdapter.PlaceSearchAdapter
import com.eviort.cabedriver.NTApplication
import com.eviort.cabedriver.NTApplication.trimMessage
import com.eviort.cabedriver.NTCustomView.*
import com.eviort.cabedriver.NTHelper.*
import com.eviort.cabedriver.NTHelper.SharedHelper.getKey
import com.eviort.cabedriver.NTHelper.SharedHelper.putKey
import com.eviort.cabedriver.NTListeners.LatLngInterpolator
import com.eviort.cabedriver.NTListeners.LatLngInterpolator.Spherical
import com.eviort.cabedriver.NTModel.Driver
import com.eviort.cabedriver.NTModel.EmergencyContactData
import com.eviort.cabedriver.NTUtilites.*
import com.eviort.cabedriver.NTUtilites.LocationUpdatesService.LocalBinder
import com.eviort.cabedriver.R
import com.eviort.cabedriver.swipebtn.SwipeButton
import com.squareup.picasso.Picasso
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.math.RoundingMode
import java.net.HttpURLConnection
import java.net.URL
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*


class FragmentHome : Fragment(), View.OnClickListener, OnMapReadyCallback, OnMarkerDragListener,
    ConnectionCallbacks, OnConnectionFailedListener, OnCameraMoveListener, LocationListener {
    var handler = Handler()
    var Tagvalue = "FragmentHome"
    var value = 0
    private var pointss //added
            : ArrayList<LatLng>? = null
    var line //added
            : Polyline? = null
    var isPolyAdded = false
    var updatestatus = " "
    var CURRENT_TAG = Tagvalue
    var drawer: DrawerLayout? = null
    var placeFields: List<Place.Field>? = null
    var goOnlineBtn: ImageView? = null
    var lastlocation: Location? = null
    var latLng: LatLng? = null
    var bundle: Bundle? = null
    var timer: Timer? = null
    var timetaken: String? = null
    var pickedupmins: String? = null
    var livetracking = false
    var push = false
    var onboarding = true
    var firebaseHelper: FirebaseHelper? = null
    var firebaseHelperRide: FirebaseHelperRide? = null
    var distancecalc: String? = null
    var activity: Activity? = null

    private var lastLocation: Location? = null
    private var distanceInMeters = 0.00
    private var distanceInKm = 0.00

    companion object {
        @JvmField
        var isRunning = true
        var PreviousStatus = " "

        public var points: ArrayList<LatLng?>? = null
        public var lineOptions: PolylineOptions? = null

        // Tracks the bound state of the service.
        var mBound = false
        var results: List<List<HashMap<String, String>>>? = null
        var path: List<HashMap<String, String>>? = null
        var ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469
        val contactList: MutableList<EmergencyContactData> = ArrayList()

        // A reference to the service used to get location updates.
        var mService: LocationUpdatesService? = null
        const val MY_PERMISSIONS_REQUEST_LOCATION = 99
        var handleCheckStatus: Handler? = null
        var handlePickupStatus: Handler? = null

        // Monitors the state of the connection to the service.
        public val mServiceConnection: ServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                val binder = service as LocalBinder
                mService = binder.service
                mService?.requestLocationUpdates()
                mBound = true
            }

            override fun onServiceDisconnected(name: ComponentName) {
                mService = null
                mBound = false
            }
        }


    }

    // Live Track
    var countpicked = 0
    var countstarted = 0
    var countpickedfirst = 0
    var countstartedfirst = 0
    var droppedcount = 0
    private var locationFlag = true
    var current_location: Location? = null
    var googleMapHelper = GoogleMapHelper()
    var markerAnimationHelper = MarkerAnimationHelper()
    var latLngInterpolator: LatLngInterpolator? = null

    var providerid: String? = null
    var AUTOCOMPLETE_DEST_REQUEST_CODE = 2
    var AUTOCOMPLETE_STOP1_REQUEST_CODE = 3
    var AUTOCOMPLETE_STOP2_REQUEST_CODE = 4
    var res: Resources? = null

    // var ll_offline: CoordinatorLayout? = null
    var drawableCount: Drawable? = null
    var mProgress: ProgressBar? = null
    var profileBtn: NTButton? = null
    var errorBtn: NTButton? = null
    var imgMenu: ImageView? = null
    var mapfocus: ImageView? = null
    var NAV_DRAWER = 0

    var utils = Utilities()
    var CurrentStatus = " "

    var livestatus = " "
    var CurrentVechicle = " "
    var previousVechicle = "id"
    var previoustripid: String? = "id"
    var currenttripid = " "
    var imgSos: ImageView? = null
    var img_navbtn: ImageView? = null
    var ic_pickup_navigate: ImageView? = null
    var img_trips: ImageView? = null
    var img_earnings: ImageView? = null
    var img_street_ride: ImageView? = null
    var img_summary: ImageView? = null
    var online: ImageView? = null
    var search_place_recyclerView: androidx.recyclerview.widget.RecyclerView? = null
    var ll_errorLayout: LinearLayout? = null
    var placesearchAdapter: com.eviort.cabedriver.NTAdapter.PlaceSearchAdapter? = null
    var alertDialog: androidx.appcompat.app.AlertDialog? = null
    //Map Frame

    var mMap: GoogleMap? = null
    var loadingDialog: LoadingDialog? = null
    var mapFragment: SupportMapFragment? = null
    var ll_onTripView: LinearLayout? = null
    var ll_onRoute_to_pickup: LinearLayout? = null
    var ll_driver_details_title: LinearLayout? = null
    var ll_driver_edit_dest: LinearLayout? = null
    var tl_cancelView: TableLayout? = null
    var tl_invoice: TableLayout? = null
    var btn_goOnline: NTButton? = null
    var btn_goOffline: NTButton? = null
    var btn_viewDetails: NTButton? = null
    var btn_additional_cost: NTButton? = null
    var btn_statusUpdate: SwipeButton? = null

    // var btn_statusUpdate: NTButton? = null
    //  var btn_cancelRequest: NTButton? = null
    var btn_cancelWithoutRequest: NTButton? = null
    var Button_Status: String? = null
    var fl_tripdetails: FrameLayout? = null
    var close_detailview: ImageView? = null
    var show_hide_cancelview: ImageView? = null
    var ll_stop_one: LinearLayout? = null

    var isarr: Boolean = true
    var isstart: Boolean = true
    var ispick: Boolean = true
    var iscurr: Boolean = true

    var ll_stop_two: LinearLayout? = null
    var edit_dest: ImageView? = null
    var edit_stop1Address: ImageView? = null
    var edit_stop2Address: ImageView? = null
    var tv_tripid: NTTextView? = null
    var tv_bookingid: NTTextView? = null
    var tv_pickup: NTTextView? = null
    var tv_drop: NTTextView? = null
    var tv_userName: NTTextView? = null
    var tv_driverNotes: NTTextView? = null
    var tv_wait_to_arrive: NTTextView? = null
    var tv_pickup_location: NTTextView? = null
    var tv_stop_one: NTTextView? = null
    var tv_stop_two: NTTextView? = null
    var tv_dest_address: NTTextView? = null
    var time_to_destination: NTTextView? = null
    var time_to_pickup: NTTextView? = null
    var iv_userAvatar: ImageView? = null
    var iv_call_user: ImageView? = null
    var rv_userRating: RatingBar? = null

    // The BroadcastReceiver used to listen from broadcasts from the service.
    private var myReceiver: MyReceiver? = null

    // Tracks the bound state of the service.
    //  private var mBound = false
    var edit_toll_fare: NTTextView? = null
    var tv_toll: NTTextView? = null
    var edit_extra_fare: NTTextView? = null
    var edit_base_fare: NTTextView? = null
    var tv_extra_fare: NTTextView? = null
    var tv_estimate_fare: NTTextView? = null
    var tv_baseFare: NTTextView? = null
    var tv_flat_fee: NTTextView? = null
    var tv_distanceFare: NTTextView? = null
    var tv_minFare: NTTextView? = null
    var tv_waitingFare: NTTextView? = null
    var tv_waitingStopFare: NTTextView? = null
    var tv_taxFare: NTTextView? = null
    var tv_discountFare: NTTextView? = null
    var tv_totalFare: NTTextView? = null
    var payment_mode_label: NTTextView? = null
    var driver_status: NTTextView? = null

    //WAITING TIME
    // Waiting Time
    var waiting_pause_time_status: NTTextView? = null
    var waiting_start_time_status: NTTextView? = null

    // Waititng Time
    var chronometer: Chronometer? = null
    var chronometer_val: String? = null
    var running = false
    var pauseoffset: Long = 0
    var hh: String? = null
    var mm: String? = null
    var ss: String? = null
    var emer: Boolean = true

    //ACCEPT/REJECT trip
    var alert = 0
    var Trip_alert_Dialog: AlertDialog? = null
    var txtDriverMessage: NTTextView? = null
    var txt01Pickup: NTTextView? = null
    var txt01DropOff: NTTextView? = null
    var txt01Timer: MyDigitalFontTextView? = null
    var img01User: NTCircularImageView? = null
    var txt01UserName: NTTextView? = null
    var txtSchedule: NTTextView? = null
    var rat01UserRating: RatingBar? = null
    var providerfinalRating: RatingBar? = null
    var txtDriverMessagepopup: NTTextView? = null
    var btn_02_accept: ImageView? = null
    var btn_02_reject: ImageView? = null
    var countDownTimer: CountDownTimer? = null
    private var count: String? = null
    var token: String? = null
    var tokentype: String? = null
    var mPlayer: MediaPlayer? = null
    var fl_ratingView: FrameLayout? = null

    // var fl_completed_rating: FrameLayout? = null
    var txt_earnings: NTBoldTextView? = null
    var iv_rateOne: ImageView? = null
    var iv_rateTwo: ImageView? = null
    var iv_rateThree: ImageView? = null
    var iv_rateFour: ImageView? = null
    var iv_rateFive: ImageView? = null
    var iv_selectedRating: ImageView? = null
    var iv_show_rating: ImageView? = null
    var et_rateComment: NTEditText? = null
    var selectedRating = 5
    var btn_rate: SwipeButton? = null

    //Location
    var usernameTxt: NTBoldTextView? = null
    var backarrow_rating: ImageView? = null
    var img_profile: ImageView? = null
    var img_profile_rate: NTCircularImageView? = null

    private var googleApiClient: GoogleApiClient? = null
    var mLocationRequest: LocationRequest? = null
    private val UPDATE_INTERVAL: Long = 5000 /* 60 secs */
    private val FASTEST_INTERVAL: Long = 5000 /*  60 secs */
    var source_lat = ""
    var source_lng = ""
    var str_currency: String? = null
    private val locationManager: LocationManager? = null
    private val provider: String? = null
    var ha: Handler? = null
    var table_satutes: LinearLayout? = null
    var goTOViewDetails =
        Navigation.createNavigateOnClickListener(R.id.action_fragmentHome_to_fragmentViewDetails)
    var srouce_lat = 0.0
    var source_long = 0.0
    var des_lat = 0.0
    var des_long = 0.0
    var s_lat = 0.0
    var s_long = 0.0
    var rootView: View? = null
    var llMapElements: LinearLayout? = null
    var llMapView: LinearLayout? = null
    var gps: GPSTracker? = null
    var llwaitingTime: RelativeLayout? = null
    var user_name: NTBoldTextView? = null
    var user_name_below: NTTextView? = null
    var current_latitude = 0.0
    var current_longitude = 0.0
    var stop1_lat = ""
    var stop1_lng = ""
    var stop1_address = ""
    var stop2_lat = ""
    var stop2_lng = ""
    var stop2_address = ""
    var currentMarker: Marker? = null
    var start_rotation = 0f
    var zoomValue = 14
    private var sourceLatLng: LatLng? = null
    private var destLatLng: LatLng? = null
    private var stop1LatLng: LatLng? = null
    private var stop2LatLng: LatLng? = null
    private var stop1Marker: Marker? = null
    private var stop2Marker: Marker? = null
    private var sourceMarker: Marker? = null
    private var destinationMarker: Marker? = null
    private var srcLatitude = 0.0
    private var srcLongitude = 0.0
    private var destLatitude = 0.0
    private var destLongitude = 0.0
    private var stop1Latitude = 0.0
    private var stop1Longitude = 0.0
    private var stop2Latitude = 0.0
    private var stop2Longitude = 0.0
    private var currentLatLng: LatLng? = null
    private var mFusedLocationClient: FusedLocationProviderClient? = null

    private var redrawline_staus = true

    //notification
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    var zooomStatus = "one"
    var line_status = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_home, container, false)
        activity!!.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        checkoverlayPermission()
        //   pointss = ArrayList() //added
        initMap()
        try {
            System.gc()
            Runtime.getRuntime().gc()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        Utilities.setLocale(activity, getKey(activity!!, "lang"))
        myReceiver = MyReceiver()
        // startLocationUpdates();
        findViewElements(rootView)
        if (getKey(getActivity()!!, "street_ride_status") == "street_ride") {
            Log.i("street_ride_status", "street_ride")
//            putKey(activity!!, "street_ride_status", "")
            iv_call_user?.visibility = View.GONE
        } else {
            Log.i("street_ride_status", "other_ride")
            iv_call_user?.visibility = View.VISIBLE
        }
        setListener()
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity!!)
        token = getKey(activity!!, "access_token")
        tokentype = getKey(activity!!, "token_type")
        bundle = arguments
        if (bundle != null) {
            unlockScreen()
            push = bundle!!.getBoolean("push")
        }
        if (push) {
            isRunning = true
        }

        initMap()

        ha = Handler()
        UpdateLocationToServer()
        handleCheckStatus = Handler()

        //  handlePickupStatus = Handler()
        //check status every sec
        chronometer = llwaitingTime!!.findViewById<View>(R.id.wt) as Chronometer
        chronometer!!.format = "00:00:00"
        chronometer!!.onChronometerTickListener = OnChronometerTickListener { cArg ->
            val time = SystemClock.elapsedRealtime() - cArg.base
            val h = (time / 3600000).toInt()
            val m = (time - h * 3600000).toInt() / 60000
            val s = (time - h * 3600000 - m * 60000).toInt() / 1000
            hh = if (h < 10) "0$h" else h.toString() + ""
            mm = if (m < 10) "0$m" else m.toString() + ""
            ss = if (s < 10) "0$s" else s.toString() + ""
            cArg.text = "$hh:$mm:$ss"
        }
        chronometer!!.base = SystemClock.elapsedRealtime()
        resetChronometer()

        /*  img_summary!!.setImageResource(R.drawable.ic_summary_grey)
          img_trips!!.setImageResource(R.drawable.ic_trips)
          img_street_ride!!.setImageResource(R.drawable.ic_street_ride_grey)
          img_earnings!!.setImageResource(R.drawable.ic_earnings_grey)*/

        try {
            Utilities.printAPI_Response("Notfication Map Activity" + Utilities.r)
            if (Utilities.r != null) {
                Utilities.printAPI_Response("Notfication")
                Utilities.r.stop()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        btn_statusUpdate?.setOnStateChangeListener { active ->
            if (active) {
                updateTripStatus()
            }
        }

        try {
            Log.e("Status_noti", "enter1")
            // Sets up permissions request launcher.
            requestPermissionLauncher =
                registerForActivityResult(ActivityResultContracts.RequestPermission()) {
                    //   refreshUI()
                    if (it) {
                        Log.e("Status_noti", "enter2")
                        // showDummyNotification()
                        // loadHomeFragment()
                        initMap()
                    } else {
                        Log.e("Status_noti", "enter3")
                        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            distanceInKm = getKey(activity!!, "drop_distance")!!.toDouble()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return rootView
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity
    }

    // This method is to check and open the notification view in front even the mobile screen off.
    private fun unlockScreen() {
        val window = activity!!.window
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
    }

    fun handler() {
        /*Thread(Runnable {
            while (true) {
                MyTask().execute("")
                Thread.sleep(3000)
            }
        }).start()
*/
        handleCheckStatus!!.postDelayed(object : Runnable {
            override fun run() {
                try {
                    if (iscurr) {
                        UpdateLocationToServer()
                    }

                    // LocationToServer();
                    Utilities.print("Handler", "called in handler thread")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                handleCheckStatus!!.postDelayed(this, 3000)
            }
        }, 3000)
    }

    fun startChrnometer() {
        if (!running) {
            putKey(activity!!, "pauseoffset", "" + pauseoffset)
            val value = getKey(activity!!, "pauseoffset")!!.toLong()
            chronometer!!.base = SystemClock.elapsedRealtime() - value
            chronometer!!.start()
            running = true
        }
    }

    fun pauseChrnometer() {
        if (running) {
            chronometer!!.stop()
            pauseoffset = SystemClock.elapsedRealtime() - chronometer!!.base
            putKey(activity!!, "pauseoffset", "" + pauseoffset)
            running = false
        }
    }

    fun resetChronometer() {
        chronometer!!.base = SystemClock.elapsedRealtime()
        pauseoffset = 0
        putKey(activity!!, "pauseoffset", "" + pauseoffset)
    }


    private fun refreshToken() {
        loadingDialog = LoadingDialog(activity!!)
        if (loadingDialog != null) loadingDialog!!.showDialog()
        Utilities.PrintAPI_URL(URLHelper.REFRESH_TOKEN, "POST")
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            URLHelper.REFRESH_TOKEN,
            null,
            Response.Listener { if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog() },
            Response.ErrorListener { error ->
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                try {
                    if (error is TimeoutError) {
                        // makeText(activity!!, resources.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show()
                    } else if (error is NoConnectionError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_no_network),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is AuthFailureError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_auth_failure),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ServerError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_server_connection),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is NetworkError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_network),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ParseError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_parse),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        // utils.showCustomAlert(activity!!, utils.ALERT_ERROR, getResources().getString(R.string.app_name), error.getMessage());
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    makeText(
                        activity!!,
                        resources.getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] =
                    "" + getKey(activity!!, "token_type") + " " + getKey(activity!!, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    private fun initMap() {
        if (mMap == null) {
            val fm = childFragmentManager
            mapFragment = fm.findFragmentById(R.id.provider_map) as SupportMapFragment?
            mapFragment!!.getMapAsync(this)

            googleApiClient = GoogleApiClient.Builder(context!!)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build()
        }
    }

    override fun onLocationChanged(location: Location) {
        if (mMap != null) {
            if (currentMarker != null) {
                currentMarker!!.remove()
            }

            if (livestatus.equals("PICKEDUP")) {
                if (lastLocation == null) {
                    lastLocation = location
                    return
                }
                distanceInMeters += location.distanceTo(lastLocation!!)
                lastLocation = location
                distanceInKm = distanceInMeters / 1000
                putKey(activity!!, "drop_distance", distanceInKm!!.toString())

            }

            val myLocation = LatLng(location.latitude, location.longitude)
            val cameraPosition = CameraPosition.Builder().target(myLocation).zoom(17.0f).build()
            val markerOptions1 = MarkerOptions()
                .position(LatLng(location.latitude, location.longitude))
                .anchor(0.5f, 0.5f)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        getBitmapFromVectorDrawable(
                            activity,
                            R.drawable.provider_marker_icon
                        )
                    )
                )
            currentMarker = mMap!!.addMarker(markerOptions1)
            moveVechile(currentMarker, location)
            rotateMarker(currentMarker, location.bearing, start_rotation)
            if (path != null) {
                //  pointss?.add(LatLng(location.latitude, location.longitude));
                if (livestatus.equals("PICKEDUP") || livestatus.equals("STARTED")) {
                    if (location.latitude != 0.0) {
                        if (redrawline_staus) {
                            redrawline_staus = false
                            redrawLine(LatLng(location.latitude, location.longitude), location)
                        }
                    }
                }
            }
            if (locationFlag) {
                locationFlag = false
                mMap!!.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            }

            mMap!!.uiSettings.isZoomControlsEnabled = true

            currentLatLng = LatLng(location.latitude, location.longitude)


        }
    }

    /**
     * Receiver for broadcasts sent by [LocationUpdatesService].
     */
    private inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val location =
                intent.getParcelableExtra<Location>(LocationUpdatesService.EXTRA_LOCATION)
            if (location != null) {
                /*  Toast.makeText(activity!!, "LOCATION",
                       Toast.LENGTH_SHORT).show();*/
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return if (ContextCompat.checkSelfPermission(
                context!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else {
            requestPermissions()
            false
        }
    }

    private fun requestPermissions() {
        /* ActivityCompat.requestPermissions(
                activity!!, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MY_PERMISSIONS_REQUEST_LOCATION
        )*/

        try {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), MY_PERMISSIONS_REQUEST_LOCATION
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun findViewElements(rootview: View?) {
        //gps = new GPSTracker(activity!!);
        user_name = rootview!!.findViewById<View>(R.id.driverNameTxt) as NTBoldTextView
        user_name_below = rootview!!.findViewById<View>(R.id.rate_your_trip) as NTTextView
        llwaitingTime = rootview!!.findViewById<View>(R.id.llwaitingTime) as RelativeLayout
        imgMenu = rootview!!.findViewById<View>(R.id.imgMenu) as ImageView
        img_navbtn = rootview!!.findViewById<View>(R.id.img_navbtn) as ImageView
        ic_pickup_navigate = rootview!!.findViewById<View>(R.id.ic_pickup_navigate) as ImageView
        //ll_offline = rootview!!.findViewById<View>(R.id.ll_offline) as CoordinatorLayout
        imgSos = rootview!!.findViewById<View>(R.id.imgSos) as ImageView
        waiting_pause_time_status =
            llwaitingTime!!.findViewById<View>(R.id.waiting_pause_time_status) as NTTextView
        waiting_start_time_status =
            llwaitingTime!!.findViewById<View>(R.id.waiting_start_time_status) as NTTextView
        //  drawer = rootview!!.findViewById<View>(R.id.drawer_layout) as DrawerLayout
        /* drawer =requireActivity().findViewById<View>(R.id.drawer_layout) as DrawerLayout
         img_trips =requireActivity().findViewById<View>(R.id.img_trips) as ImageView
         //  online = rootview!!.findViewById<View>(R.id.online) as ImageView
         img_earnings = requireActivity().findViewById<View>(R.id.img_earnings) as ImageView
         img_street_ride = requireActivity().findViewById<View>(R.id.img_street_ride) as ImageView
         img_summary = requireActivity().findViewById<View>(R.id.img_summary) as ImageView*/
        close_detailview = rootview!!.findViewById<View>(R.id.close_detailview) as ImageView
        show_hide_cancelview = rootview!!.findViewById<View>(R.id.show_hide_cancelview) as ImageView
        tl_cancelView = rootview!!.findViewById<View>(R.id.tl_cancelView) as TableLayout
        // btn_cancelRequest = rootview!!.findViewById<View>(R.id.btn_cancelRequest) as NTButton
        btn_cancelWithoutRequest =
            rootview!!.findViewById<View>(R.id.btn_cancelWithoutRequest) as NTButton
        btn_goOnline = rootview!!.findViewById<View>(R.id.btn_goOnline) as NTButton
        btn_goOffline = rootview!!.findViewById<View>(R.id.btn_goOffline) as NTButton
        btn_viewDetails = rootview!!.findViewById<View>(R.id.btn_viewDetails) as NTButton
        btn_statusUpdate = rootview!!.findViewById<View>(R.id.btn_statusUpdate) as SwipeButton

        //  btn_statusUpdate = rootview!!.findViewById<View>(R.id.btn_statusUpdate) as NTButton
        mapfocus = rootview!!.findViewById<View>(R.id.mapfocus) as ImageView

//        Trip Details
        fl_tripdetails = rootview!!.findViewById<View>(R.id.fl_tripdetails) as FrameLayout
        //  fl_offline=(FrameLayout) rootview!!.findViewById(R.id.fl_offline);
        ll_onTripView = rootview!!.findViewById<View>(R.id.ll_onTripView) as LinearLayout
        btn_additional_cost = rootview!!.findViewById<View>(R.id.btn_additional_cost) as NTButton
        edit_stop1Address = rootview!!.findViewById<View>(R.id.edit_stop1_address) as ImageView
        edit_stop2Address = rootview!!.findViewById<View>(R.id.edit_stop2_address) as ImageView
        edit_dest = rootview!!.findViewById<View>(R.id.edit_dest_address) as ImageView
        ll_onRoute_to_pickup = rootview.findViewById<View>(R.id.ll_onRoutetoPickup) as LinearLayout
        ll_driver_details_title =
            rootview!!.findViewById<View>(R.id.llTravelwaitingTime) as LinearLayout
        ll_driver_edit_dest =
            rootview!!.findViewById<View>(R.id.llTravelEditDestination) as LinearLayout
        tv_pickup_location = rootview!!.findViewById<View>(R.id.txt_pickup_location) as NTTextView
        tv_wait_to_arrive = rootview!!.findViewById<View>(R.id.wait_to_arrive) as NTTextView
        tv_dest_address = rootview!!.findViewById<View>(R.id.dest_address) as NTTextView
        ll_stop_one = rootview!!.findViewById<View>(R.id.ll_stop_one) as LinearLayout
        ll_stop_two = rootview!!.findViewById<View>(R.id.ll_stop_two) as LinearLayout
        tv_stop_one = rootview!!.findViewById<View>(R.id.stop1_address) as NTTextView
        tv_stop_two = rootview!!.findViewById<View>(R.id.stop2_address) as NTTextView
        time_to_destination = rootview!!.findViewById<View>(R.id.time_to_destination) as NTTextView
        time_to_pickup = rootview!!.findViewById<View>(R.id.time_to_pickup) as NTTextView
        tv_tripid = rootview!!.findViewById<View>(R.id.tv_tripid) as NTTextView
        tv_bookingid = rootview!!.findViewById<View>(R.id.tv_bookingid) as NTTextView
        tv_pickup = rootview!!.findViewById<View>(R.id.tv_pickup) as NTTextView
        tv_drop = rootview!!.findViewById<View>(R.id.tv_drop) as NTTextView
        iv_userAvatar = rootview!!.findViewById<View>(R.id.iv_userAvatar) as ImageView
        tv_userName = rootview!!.findViewById<View>(R.id.tv_userName) as NTTextView
        tv_driverNotes = rootview!!.findViewById<View>(R.id.tv_driverNotes) as NTTextView
        providerfinalRating = rootview!!.findViewById<View>(R.id.providerfinalRating) as RatingBar
        usernameTxt = rootview!!.findViewById<View>(R.id.usernameTxt) as NTBoldTextView
        // img_profile = rootview!!.findViewById<View>(R.id.img_profile) as NTCircularImageView
        img_profile = rootview!!.findViewById<View>(R.id.img_profile) as ImageView
        img_profile_rate =
            rootview!!.findViewById<View>(R.id.img_profile_rate) as NTCircularImageView
        iv_call_user = rootview!!.findViewById<View>(R.id.iv_call_user) as ImageView
        tl_invoice = rootview!!.findViewById<View>(R.id.tl_invoice) as TableLayout
        edit_toll_fare = rootview!!.findViewById<View>(R.id.edit_toll_fare) as NTTextView
        tv_toll = rootview!!.findViewById<View>(R.id.tv_toll) as NTTextView
        edit_extra_fare = rootview!!.findViewById<View>(R.id.edit_extra_fare) as NTTextView
        edit_base_fare = rootview!!.findViewById<View>(R.id.edit_base_fare) as NTTextView
        tv_extra_fare = rootview!!.findViewById<View>(R.id.tv_extra_fare) as NTTextView
        tv_estimate_fare = rootview!!.findViewById<View>(R.id.tv_estimate_fare) as NTTextView
        tv_baseFare = rootview!!.findViewById<View>(R.id.tv_baseFare) as NTTextView
        tv_flat_fee = rootview!!.findViewById<View>(R.id.tv_flat_fee) as NTTextView

        tv_distanceFare = rootview!!.findViewById<View>(R.id.tv_distanceFare) as NTTextView
        tv_minFare = rootview!!.findViewById<View>(R.id.tv_minFare) as NTTextView
        tv_waitingFare = rootview!!.findViewById<View>(R.id.tv_waitingFare) as NTTextView
        tv_waitingStopFare = rootview!!.findViewById<View>(R.id.tv_waitingStopFare) as NTTextView
        tv_taxFare = rootview!!.findViewById<View>(R.id.tv_taxFare) as NTTextView
        tv_discountFare = rootview!!.findViewById<View>(R.id.tv_discountFare) as NTTextView
        tv_totalFare = rootview!!.findViewById<View>(R.id.tv_totalFare) as NTTextView
        payment_mode_label = rootview!!.findViewById<View>(R.id.payment_mode_label) as NTTextView
        driver_status = rootview!!.findViewById<View>(R.id.driver_status) as NTTextView
        fl_ratingView = rootview!!.findViewById<View>(R.id.fl_ratingView) as FrameLayout
        backarrow_rating = rootview!!.findViewById<View>(R.id.backArrow_rating) as ImageView
        txt_earnings = rootview!!.findViewById<View>(R.id.txt_earnings) as NTBoldTextView
        //  fl_completed_rating = rootview!!.findViewById<View>(R.id.fl_completed_rating) as FrameLayout
        btn_rate = rootview!!.findViewById<View>(R.id.btn_rate) as SwipeButton
        et_rateComment = rootview!!.findViewById<View>(R.id.et_rateComment) as NTEditText
        iv_rateOne = rootview!!.findViewById<View>(R.id.iv_rateOne) as ImageView
        // iv_show_rating = rootview!!.findViewById<View>(R.id.iv_show_rating) as ImageView
        iv_rateTwo = rootview!!.findViewById<View>(R.id.iv_rateTwo) as ImageView
        iv_rateThree = rootview!!.findViewById<View>(R.id.iv_rateThree) as ImageView
        iv_rateFour = rootview!!.findViewById<View>(R.id.iv_rateFour) as ImageView
        iv_rateFive = rootview!!.findViewById<View>(R.id.iv_rateFive) as ImageView
        iv_selectedRating = rootview!!.findViewById<View>(R.id.iv_selectedRating) as ImageView
        table_satutes = rootview!!.findViewById<View>(R.id.table_satutes) as LinearLayout
        llMapElements = rootview!!.findViewById<View>(R.id.llMapElements) as LinearLayout
        llMapView = rootview!!.findViewById<View>(R.id.llMapView) as LinearLayout
        initMap()
    }

    private fun setListener() {
        imgMenu!!.setOnClickListener(this)
        imgSos!!.setOnClickListener(this)
        img_navbtn!!.setOnClickListener(this)
        mapfocus!!.setOnClickListener(this)
        ic_pickup_navigate!!.setOnClickListener(this)
        waiting_start_time_status!!.setOnClickListener(this)
        waiting_pause_time_status!!.setOnClickListener(this)
        edit_toll_fare!!.setOnClickListener(this)
        edit_extra_fare!!.setOnClickListener(this)
        edit_base_fare!!.setOnClickListener(this)
        btn_goOnline!!.setOnClickListener(this)
        btn_goOffline!!.setOnClickListener(this)
        // btn_statusUpdate.setOnClickListener(this);
        edit_dest!!.setOnClickListener(this)
        edit_stop1Address!!.setOnClickListener(this)
        edit_stop2Address!!.setOnClickListener(this)
        btn_viewDetails!!.setOnClickListener(this)
        close_detailview!!.setOnClickListener(this)
        show_hide_cancelview!!.setOnClickListener(this)
        // btn_cancelRequest!!.setOnClickListener(this)
        btn_cancelWithoutRequest!!.setOnClickListener(this)
        iv_call_user!!.setOnClickListener(this)
        btn_rate!!.setOnClickListener(this)
        btn_rate!!.setOnStateChangeListener { b ->
            if (b) {
                updateRatingStatus(getKey(activity!!, "trip_id"))
            }
        }
        iv_rateOne!!.setOnClickListener(this)
        backarrow_rating!!.setOnClickListener(this)
        //  backarrow_rating.setOnClickListener(this);
        //iv_show_rating!!.setOnClickListener(this)
        iv_rateTwo!!.setOnClickListener(this)
        iv_rateThree!!.setOnClickListener(this)
        iv_rateFour!!.setOnClickListener(this)
        iv_rateFive!!.setOnClickListener(this)
        //        online.setOnClickListener(this);
        btn_additional_cost!!.setOnClickListener(this)
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            99 -> try {
                Log.e("Status_noti", "enter2")

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        if (ContextCompat.checkSelfPermission(
                                activity!!,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION
                            ) == PackageManager.PERMISSION_GRANTED
                        ) {
                            // Background Location Permission is granted so do your work here
                        } else {
                            // Ask for Background Location Permission
                            askPermissionForBackgroundUsage();
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            2 -> try {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    // User declined for Background Location Permission.
                    askPermissionForBackgroundUsage()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }


    fun goOnline() {
        val builder3 = AlertDialog.Builder(activity!!)
        val inflater3 =
            activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
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

    fun goOffline() {
        val builder4 = AlertDialog.Builder(activity!!)
        val inflater4 =
            activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
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

    fun sendSOS() {
        val builder = AlertDialog.Builder(activity!!)
        val inflater =
            activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.fragment_sos, null)
        builder.setCancelable(false)
        builder.setView(layout)
        builder.create()
        val alertDialog = builder.create()
        val img_exit = layout.findViewById<View>(R.id.img_exit) as ImageView
        val tv_header = layout.findViewById<View>(R.id.tv_header) as NTBoldTextView
        tv_header.setText(R.string.f_pop_sos)
        val btn_txtMessage = layout.findViewById<View>(R.id.btn_txtMessage) as NTButton
        val btn_whatsAppMessage = layout.findViewById<View>(R.id.btn_whatsAppMessage) as NTButton
        img_exit.setOnClickListener { alertDialog.dismiss() }
        btn_txtMessage.setOnClickListener {
            if (contactList.size > 0) {
                for (i in 0 until contactList.size) {
                    // val item = contactList.get(i) as View
                    try {

                        val message = "Emergency SOS"
                        val phno = contactList.get(i).number

                        val intent = Intent(Intent.ACTION_SENDTO).apply {
                            data = Uri.parse("smsto:" + phno)  // This ensures only SMS apps respond
                            putExtra("sms_body", message)

                        }
                        if (intent.resolveActivity(activity!!.packageManager) != null) {
                            startActivity(intent)
                        }


                    } catch (ex: java.lang.Exception) {

                    }

                }
            } else {
                utils.showAlert(activity!!, "Please add emergency contact")
            }


        }
        btn_whatsAppMessage.setOnClickListener {
            val isAppInstalled = appInstalledOrNot("com.whatsapp")
            if (isAppInstalled) {
                /*     val LaunchIntent = activity!!.packageManager
                             .getLaunchIntentForPackage("com.whatsapp")
                     startActivity(LaunchIntent)
                     Log.d("Application :", "Application is already installed.")
                     val smsNumber = "9659090965" // E164 format without '+' sign
                     val sendIntent = Intent(Intent.ACTION_SEND)
                     sendIntent.type = "text/plain"
                     sendIntent.putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
                     sendIntent.putExtra("jid", "$smsNumber@s.whatsapp.net") //phone number without "+" prefix
                     sendIntent.setPackage("com.whatsapp")
                     startActivity(sendIntent)*/

                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.setPackage("com.whatsapp")
                intent.putExtra(Intent.EXTRA_TEXT, "Emergency SOS")
                startActivity(intent)

            } else {
                Log.d("Application", "Application is not currently installed.")
                val builderr = AlertDialog.Builder(activity!!)
                val inflater =
                    activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val layout = inflater.inflate(R.layout.fragment_open_playstore, null)
                builderr.setCancelable(false)
                builderr.setView(layout)
                builderr.create()
                val alertDialog = builderr.create()
                val img_exit = layout.findViewById<View>(R.id.img_exit) as ImageView
                val tv_header = layout.findViewById<View>(R.id.tv_header) as NTBoldTextView
                tv_header.setText(R.string.f_pop_sos)
                val btn_no = layout.findViewById<View>(R.id.btn_no) as NTButton
                val btn_install = layout.findViewById<View>(R.id.btn_install) as NTButton
                img_exit.setOnClickListener { alertDialog.dismiss() }
                btn_no.setOnClickListener { alertDialog.dismiss() }
                btn_install.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse("market://details?id=com.whatsapp")
                    startActivity(intent)
                }
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                alertDialog.window!!.attributes.windowAnimations = R.style.dialog_animation
                alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
                alertDialog.show()
            }
        }
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog.window!!.attributes.windowAnimations = R.style.dialog_animation
        alertDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog.show()
    }

    override fun onClick(v: View) {
        val id = v.id
        when (id) {
            R.id.edit_toll_fare -> additionalFeeDialog("Toll")
            R.id.edit_extra_fare -> additionalFeeDialog("Extra")
            R.id.edit_base_fare -> additionalFeeDialog("Base")
            R.id.imgMenu -> if (NAV_DRAWER == 0) {
                if (drawer != null) drawer!!.openDrawer(Gravity.LEFT)
            } else {
                NAV_DRAWER = 0
                if (drawer != null) drawer!!.closeDrawers()
            }

            R.id.imgSos -> sendSOS()
            R.id.mapfocus -> {
                mapfocus()
//                try{
//                    if (NetworkUtil.getConnectivityStatus(activity) > 0) {
//                        val sgps = GPSTracker(activity!!)
//                        current_latitude = sgps.latitude
//                        current_longitude = sgps.longitude
//                        onLocationChanged(current_latitude, current_longitude)
//                    } else {
//                        showErrorLayout("INTERNET")
//                    }
//                }catch(e:Exception){
//                    e.printStackTrace()
//                }

            }

            R.id.waiting_pause_time_status -> {
                waiting_start_time_status?.visibility = View.VISIBLE
                waiting_pause_time_status?.visibility = View.GONE
                chronometer!!.background =
                    activity!!.resources.getDrawable(R.drawable.button_rounded_red)
                pauseChrnometer()
            }

            R.id.edit_dest_address -> SearchAlert("drop")
            R.id.edit_stop1_address -> SearchAlert("stop1")
            R.id.edit_stop2_address -> SearchAlert("stop2")
            R.id.waiting_start_time_status -> {
                waiting_pause_time_status?.visibility = View.VISIBLE
                waiting_start_time_status?.visibility = View.GONE
                chronometer!!.background =
                    activity!!.resources.getDrawable(R.drawable.button_rounded_green)
                startChrnometer()
            }

            R.id.img_navbtn -> {
                // below code used for only destination given through intent to open GoogleMaps

                /*  Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("http://maps.google.com/maps?daddr=11.020548,76.967039"));
                startActivity(intent);*/

                // below code used for both from to destination given through intent to open Google Maps
                val navigation = Uri.parse("google.navigation:q=$srcLatitude,$srcLongitude")
                val navigationIntent = Intent(Intent.ACTION_VIEW, navigation)
                navigationIntent.setPackage("com.google.android.apps.maps")
                startActivity(navigationIntent)
            }

            R.id.ic_pickup_navigate -> {
                val str_origin = "origin=$srcLatitude,$srcLongitude"
                val str_dest = "destination=$destLatitude,$destLongitude"
                val waypoints1 = "waypoints=$stop1Latitude,$stop1Longitude"
                val waypoints2 = "|$stop2Latitude,$stop2Longitude"
                val parameters: String
                parameters = if (stop1Latitude == 0.0) {
                    "$str_origin&$str_dest"
                } else if (stop2Latitude == 0.0) {
                    "$str_origin&$str_dest&$waypoints1"
                } else {
                    "$str_origin&$str_dest&$waypoints1$waypoints2"
                }
                val naviUri2 =
                    Uri.parse("https://www.google.com/maps/dir/?api=1&travelmode=driving&dir_action=navigate&$parameters")
                val intent = Intent(Intent.ACTION_VIEW, naviUri2)
                intent.setClassName(
                    "com.google.android.apps.maps",
                    "com.google.android.maps.MapsActivity"
                )
                startActivity(intent)
            }

            R.id.btn_viewDetails ->
                if (btn_viewDetails!!.text.toString().equals("WAY TO PICKUP", ignoreCase = true)) {
                    map(s_lat, s_long)
                } else if (btn_viewDetails!!.text.toString()
                        .equals("WAY TO DROPPED", ignoreCase = true)
                ) {
                    map(des_lat, des_long)
                }

            R.id.show_hide_cancelview -> if (tl_cancelView?.visibility == View.VISIBLE) {
                tl_cancelView?.visibility = View.GONE
            } else {
                tl_cancelView?.visibility = View.VISIBLE
            }

            R.id.btn_cancelWithoutRequest -> showCancelAlert(0)
            // R.id.btn_cancelRequest -> showLogoutDialog()
            R.id.close_detailview -> fl_tripdetails?.visibility = View.GONE
            R.id.iv_call_user -> try {
                openDialer()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            R.id.btn_additional_cost -> showAdditionalCost()
            R.id.online -> goOnline()
            R.id.backArrow_rating -> {
                selectedRating = 0
                et_rateComment!!.setText("")
                updateRatingStatus(getKey(activity!!, "trip_id"))
            }
            /*      R.id.iv_show_rating -> {
                hidelayouts()
                fl_ratingView?.visibility = View.VISIBLE
            }*/
//            R.id.iv_rateOne -> iv_selectedRating!!.setImageDrawable(resources.getDrawable(R.drawable.rate_one))
//            R.id.iv_rateTwo -> iv_selectedRating!!.setImageDrawable(resources.getDrawable(R.drawable.rate_two))
//            R.id.iv_rateThree -> iv_selectedRating!!.setImageDrawable(resources.getDrawable(R.drawable.rate_three))
//            R.id.iv_rateFour -> iv_selectedRating!!.setImageDrawable(resources.getDrawable(R.drawable.rate_four))
//            R.id.iv_rateFive -> iv_selectedRating!!.setImageDrawable(resources.getDrawable(R.drawable.rate_five))
        }
    }

    private fun map(des_lat: Double, des_long: Double) {
        val navigation = Uri.parse("google.navigation:q=$des_lat,$des_long")
        val navigationIntent = Intent(Intent.ACTION_VIEW, navigation)
        navigationIntent.setPackage("com.google.android.apps.maps")
        startActivity(navigationIntent)
    }

    private fun showCancelAlert(cancelType: Int) {
        val builder4 = AlertDialog.Builder(activity!!)
        val inflater4 =
            activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout4 = inflater4.inflate(R.layout.layout_alert_dialog, null)
        builder4.setCancelable(false)
        builder4.setView(layout4)
        builder4.create()
        val alertDialog4 = builder4.create()
        val tv_alert_title2 = layout4.findViewById<View>(R.id.tv_alert_title) as NTTextView
        val tv_alert_desc2 = layout4.findViewById<View>(R.id.tv_alert_desc) as NTTextView
        val tv_alert_positive = layout4.findViewById<View>(R.id.tv_alert_positive) as NTTextView
        val tv_alert_negative = layout4.findViewById<View>(R.id.tv_alert_negative) as NTTextView
        if (cancelType == 1) {
            tv_alert_title2.text = resources.getString(R.string.raise_cancel_request)
            tv_alert_desc2.text = resources.getString(R.string.raise_cancel_request_desc)
            tv_alert_positive.text = resources.getString(R.string.yes)
            tv_alert_negative.text = resources.getString(R.string.no)
        } else {
            tv_alert_title2.text = resources.getString(R.string.cancel_without_request)
            tv_alert_desc2.text = resources.getString(R.string.cancel_without_request_desc)
            tv_alert_positive.text = resources.getString(R.string.ok)
            tv_alert_negative.text = resources.getString(R.string.cancel)
        }
        tv_alert_positive.setOnClickListener { alertDialog4.dismiss() }
        tv_alert_negative.setOnClickListener { alertDialog4.dismiss() }
        alertDialog4.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog4.window!!.attributes.windowAnimations = R.style.dialog_animation
        alertDialog4.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog4.show()
    }

    fun showAdditionalCost() {
        val builder3 = AlertDialog.Builder(activity!!)
        val inflater3 =
            activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout3 = inflater3.inflate(R.layout.layout_alert_additional_dialog, null)
        builder3.setCancelable(false)
        builder3.setView(layout3)
        builder3.create()
        val alertDialog3 = builder3.create()
        val tv_alert_title = layout3.findViewById<View>(R.id.tv_alert_title) as NTTextView
        val tv_alert_okBtn = layout3.findViewById<View>(R.id.tv_alert_positive) as NTTextView
        val tv_alert_noBtn = layout3.findViewById<View>(R.id.tv_alert_negative) as NTTextView

        // tv_alert_title.setText(getResources().getString(R.string.alert_logout_title));
        //tv_alert_desc.setText(getResources().getString(R.string.logout_alert));
        tv_alert_okBtn.text = resources.getString(R.string.confirm)
        tv_alert_noBtn.text = resources.getString(R.string.cancel)
        tv_alert_okBtn.setOnClickListener { alertDialog3.dismiss() }
        tv_alert_noBtn.setOnClickListener { alertDialog3.dismiss() }
        alertDialog3.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog3.window!!.attributes.windowAnimations = R.style.dialog_animation
        alertDialog3.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog3.show()
    }

    // Custom method to open dialer app
    protected fun openDialer() {
        try {
            val intent = Intent(Intent.ACTION_DIAL)
            // Send phone number to intent as data
            intent.data = Uri.parse("tel:" + getKey(activity!!, "user_phone"))
            // Start the dialer app activity with number
            startActivity(intent)
        } catch (e: Exception) {
            utils.showCustomAlert(
                activity!!,
                Utilities.ALERT_WARNING,
                resources.getString(R.string.app_name),
                resources.getString(R.string.something_went_wrong)
            )
            e.printStackTrace()
        }
    }

    private fun updateTripStatus() {
        when (Button_Status) {
            "ARRIVED" -> {
                // btn_statusUpdate?.setHasActivationState(false)
                //btn_statusUpdate?.isEnabled = false
                // previousStatus = "STARTED"
                updateArrivedStatus(getKey(activity!!, "trip_id"))
            }

            "PICKEDUP" -> {
                // btn_statusUpdate?.setHasActivationState(false)
                //btn_statusUpdate?.isEnabled = false
                updatePickedupStatus(getKey(activity!!, "trip_id"))
            }

            "DROPPED" -> {
                // btn_statusUpdate?.setHasActivationState(false)
                //btn_statusUpdate?.isEnabled = false

                updateDroppedStatus(getKey(activity!!, "trip_id"))
            }

            "COMPLETED" -> {
                // btn_statusUpdate?.setHasActivationState(false)
                //btn_statusUpdate?.isEnabled = false
                updateCompletedStatus(getKey(activity!!, "trip_id"))
            }

            "COLLECT FARE" -> {
                //  btn_statusUpdate?.setHasActivationState(false)
                // btn_statusUpdate?.isEnabled = false
                updateEndStatus(getKey(activity!!, "trip_id"))
            }                //table_satutes.setVisibility(View.GONE);
            //fl_completed_rating.setVisibility(View.VISIBLE);

            "CONFIRM PAYMENT" -> {
                // btn_statusUpdate?.setHasActivationState(false)
                // btn_statusUpdate?.isEnabled = false
                updateConfirmpaymentStatus(getKey(activity!!, "trip_id"))
            }

            "RATE USER" ->                 //   fl_completed_rating.setVisibility(View.VISIBLE);
                txt_earnings!!.text = getKey(activity!!, "driver_earnings")
        }
    }

    fun LocationToServer() {
        val sgps = GPSTracker(activity!!)
        current_latitude = sgps.latitude
        current_longitude = sgps.longitude
        val `object` = JSONObject()
        try {
            `object`.put("latitude", current_latitude)
            `object`.put("longitude", current_longitude)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Utilities.PrintAPI_URL(URLHelper.UPDATE_LOCATION_TO_SERVER, `object`.toString())
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            URLHelper.UPDATE_LOCATION_TO_SERVER,
            `object`,
            Response.Listener { response -> Utilities.printAPI_Response(response.toString()) },
            Response.ErrorListener { error ->
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                try {
                    if (error is TimeoutError) {
                        //  makeText(activity!!, resources.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show()
                    } else if (error is NoConnectionError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_no_network),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is AuthFailureError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_auth_failure),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ServerError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_server_connection),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is NetworkError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_network),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ParseError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_parse),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        // utils.showCustomAlert(activity!!, utils.ALERT_ERROR, getResources().getString(R.string.app_name), error.getMessage());
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    makeText(
                        activity!!,
                        resources.getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] =
                    "" + getKey(activity!!, "token_type") + " " + getKey(activity!!, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    fun updateRatingStatus(tripId: String?) {

        btn_rate!!.toggleState()
        val `object` = JSONObject()
        try {
            `object`.put("rating", selectedRating.toString() + "")
            `object`.put("comment", et_rateComment!!.text.toString())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Utilities.PrintAPI_URL(URLHelper.RATING + tripId, `object`.toString())
//        loadingDialog = LoadingDialog(activity!!)
//        if (loadingDialog != null) loadingDialog!!.showDialog()
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            URLHelper.RATING + tripId,
            `object`,
            Response.Listener { response ->
                Utilities.printAPI_Response(response.toString())

                emer = true
                isarr = true
                ispick = true
                isarr = true
                mMap!!.clear()
                SharedHelper.putKey(activity!!, "a", "0")
                putKey(activity!!, "user_name", "")
                putKey(activity!!, "user_image", "")
                putKey(activity!!, "ride_status", "")
                putKey(requireContext()!!, "arrived_toggle", "0")
                putKey(requireContext()!!, "pickedup_toggle", "0")
                putKey(requireContext()!!, "drop_toggle", "0")
                countstarted = 0
                countpicked = 0
                droppedcount = 0
                CurrentStatus = " "

                putKey(activity!!, "driverOnlineFlag", "true")
                tl_invoice?.visibility = View.GONE
                fl_ratingView?.visibility = View.GONE
                fl_tripdetails?.visibility = View.GONE
                ll_onTripView?.visibility = View.GONE
                val mainActivity = activity as MainActivity?
                showlayouts()
                providerfinalRating!!.rating = 5.0f
                selectedRating = "5".toInt()
                et_rateComment!!.setText("")
                //  handler()
                val sgps = GPSTracker(activity!!)
                current_latitude = sgps.latitude
                current_longitude = sgps.longitude
                onLocationChanged(current_latitude, current_longitude)

                //   showlayoutsRefreshh()
                UpdateLocationToServer()


            },
            Response.ErrorListener { error ->
                //    if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                //   btn_statusUpdate?.toggleState()
                try {
                    if (error is TimeoutError) {
                        //  makeText(activity!!, resources.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show()
                    } else if (error is NoConnectionError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_no_network),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is AuthFailureError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_auth_failure),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ServerError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_server_connection),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is NetworkError) {
                        // Toast.makeText(activity!!, getResources().getString(R.string.error_network), Toast.LENGTH_LONG).show();
                    } else if (error is ParseError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_parse),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        // utils.showCustomAlert(activity!!, utils.ALERT_ERROR, getResources().getString(R.string.app_name), error.getMessage());
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    makeText(
                        activity!!,
                        resources.getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] =
                    "" + getKey(activity!!, "token_type") + " " + getKey(activity!!, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    private fun updateArrivedStatus(tripId: String?) {
        //  btn_statusUpdate?.setHasActivationState(false)
        Utilities.PrintAPI_URL(URLHelper.ARRIVED + tripId, "POST")
        loadingDialog = LoadingDialog(activity!!)
        if (loadingDialog != null) loadingDialog!!.showDialog()
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            URLHelper.ARRIVED + tripId,
            null,
            Response.Listener { response ->
                UpdateLocationToServer()
                Utilities.printAPI_Response(response.toString())

                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                try {
                    if (response.getString("success") == "1") {
                        //   arrived(response)
                        //   btn_statusUpdate?.setHasActivationState(true)
                        btn_statusUpdate?.toggleState()
                        UpdateLocationToServer()

                        resetChronometer()

                        waiting_start_time_status?.visibility = View.VISIBLE
                        waiting_pause_time_status?.visibility = View.GONE
                        chronometer!!.background =
                            activity!!.resources.getDrawable(R.drawable.button_rounded_red)
                        pauseChrnometer()
                        /* btn_statusUpdate.setText(activity!!.getString(R.string.slide_to_pickup));
                              btn_statusUpdate.setBackground(activity!!.getDrawable(R.drawable.rounded_button_gray));
                              btn_statusUpdate.setButtonBackground(activity!!.getDrawable(R.drawable.rounded_button_slide));*/
                    } else {
//not remove
                        btn_statusUpdate?.toggleState()
//                        utils.showCustomAlert(
//                            activity!!,
//                            Utilities.ALERT_ERROR,
//                            resources.getString(R.string.app_name),
//                            response.getString("message")
//                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                //      if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                try {
                    if (error is TimeoutError) {
                        //  makeText(activity!!, resources.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show()
                    } else if (error is NoConnectionError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_no_network),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is AuthFailureError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_auth_failure),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ServerError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_server_connection),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is NetworkError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_network),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ParseError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_parse),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        // utils.showCustomAlert(activity!!, utils.ALERT_ERROR, getResources().getString(R.string.app_name), error.getMessage());
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    makeText(
                        activity!!,
                        resources.getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] =
                    "" + getKey(activity!!, "token_type") + " " + getKey(activity!!, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    private fun updatePickedupStatus(tripId: String?) {
        //   btn_statusUpdate?.setHasActivationState(false)
        val gps = GPSTracker(activity!!)
        current_latitude = gps.latitude
        current_longitude = gps.longitude
        val `object` = JSONObject()
        val lat = current_latitude
        val longh = current_longitude

        val address = Utilities.getCompleteAddressString(activity!!, lat, longh)
        Utilities.PrintAPI_URL("updatePickedupStatus" + URLHelper.DROPPED + address, "POST")
        try {
            `object`.put("latitude", current_latitude)
            `object`.put("longitude", current_longitude)
            `object`.put("address", address)
            `object`.put("trip_waiting_time", "$hh:$mm:$ss")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Utilities.PrintAPI_URL(URLHelper.PICKEDUP + tripId, `object`.toString())
        loadingDialog = LoadingDialog(activity!!)
        if (loadingDialog != null) loadingDialog!!.showDialog()
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            URLHelper.PICKEDUP + tripId,
            `object`,
            Response.Listener { response ->
                UpdateLocationToServer()
                Utilities.PrintAPI_URL(URLHelper.PICKEDUP, response.toString())

                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                //   btn_statusUpdate?.setHasActivationState(true)
                //     pickedup(response)


                /*      waiting_pause_time_status?.visibility = View.GONE
                      waiting_start_time_status?.visibility = View.VISIBLE*/
                resetChronometer()
                btn_statusUpdate?.toggleState()

                waiting_start_time_status?.visibility = View.VISIBLE
                waiting_pause_time_status?.visibility = View.GONE
                chronometer!!.background =
                    activity!!.resources.getDrawable(R.drawable.button_rounded_red)
                pauseChrnometer()
                //   resetChronometer()

                /*  btn_statusUpdate.setText(activity!!.getString(R.string.slide_to_dropoff));
                      btn_statusUpdate.setBackground(activity!!.getDrawable(R.drawable.rounded_button_gray));
                      btn_statusUpdate.setButtonBackground(activity!!.getDrawable(R.drawable.rounded_button_slide));*/
            },
            Response.ErrorListener { error ->
                //    if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                try {
                    if (error is TimeoutError) {
                        //   makeText(activity!!, resources.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show()
                    } else if (error is NoConnectionError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_no_network),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is AuthFailureError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_auth_failure),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ServerError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_server_connection),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is NetworkError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_network),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ParseError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_parse),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        //  utils.showCustomAlert(activity!!, utils.ALERT_ERROR, getResources().getString(R.string.app_name), error.getMessage());
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    makeText(
                        activity!!,
                        resources.getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] =
                    "" + getKey(activity!!, "token_type") + " " + getKey(activity!!, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    private fun updateDroppedStatus(tripId: String?) {

        val gps = GPSTracker(activity!!)
        current_latitude = gps.latitude
        current_longitude = gps.longitude
        val `object` = JSONObject()
        val address =
            Utilities.getCompleteAddressString(activity!!, current_latitude, current_longitude)
        Utilities.PrintAPI_URL(URLHelper.DROPPED + address, "POST")
        try {
            `object`.put("latitude", current_latitude)
            `object`.put("longitude", current_longitude)
            `object`.put("address", address)
            `object`.put("stop_waiting_time", "$hh:$mm:$ss")
            `object`.put("distance", distanceInKm)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Utilities.PrintAPI_URL(
            URLHelper.DROPPED + getKey(context!!, "trip_id"),
            `object`.toString()
        )
        loadingDialog = LoadingDialog(activity!!)
        if (loadingDialog != null)
            loadingDialog!!.showDialog()
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            URLHelper.DROPPED + getKey(context!!, "trip_id"),
            `object`,
            Response.Listener { response ->
                UpdateLocationToServer()
                Utilities.printAPI_Response(response.toString())

                if (loadingDialog != null && loadingDialog!!.isShowing)
                    loadingDialog!!.hideDialog()
                //   btn_statusUpdate?.setHasActivationState(true)
//dropped(response,"0")
                btn_statusUpdate?.toggleState()
                UpdateLocationToServer()

                img_navbtn!!.setOnClickListener(null)
                edit_stop1Address!!.setOnClickListener(null)
                edit_stop2Address!!.setOnClickListener(null)
                edit_dest!!.setOnClickListener(null)
                ic_pickup_navigate!!.setOnClickListener(null)

                waiting_pause_time_status?.visibility = View.GONE
                waiting_start_time_status?.visibility = View.VISIBLE
                //  getDroppedTripDetail();
                resetChronometer()

                if (getKey(requireContext()!!, "st").equals("streetride")) {
                    btn_statusUpdate?.setHasActivationState(true)
                    btn_statusUpdate?.toggleState()
                }

            },
            Response.ErrorListener { error ->
//                if (loadingDialog != null && loadingDialog!!.isShowing)
//                    loadingDialog!!.hideDialog()
                try {
                    if (error is TimeoutError) {
                        //   makeText(activity!!, resources.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show()
                    } else if (error is NoConnectionError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_no_network),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is AuthFailureError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_auth_failure),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ServerError) {
                        //  Toast.makeText(activity!!, getResources().getString(R.string.error_server_connection), Toast.LENGTH_LONG).show();
                    } else if (error is NetworkError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_network),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ParseError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_parse),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        // utils.showCustomAlert(activity!!, utils.ALERT_ERROR, getResources().getString(R.string.app_name), error.getMessage());
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    makeText(
                        activity!!,
                        resources.getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] =
                    "" + getKey(activity!!, "token_type") + " " + getKey(activity!!, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    private fun updateCompletedStatus(tripId: String?) {
        tl_invoice?.visibility = View.GONE
        Utilities.PrintAPI_URL(URLHelper.COMPLETED + tripId, "POST")
        loadingDialog = LoadingDialog(activity!!)
        if (loadingDialog != null) loadingDialog!!.showDialog()
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            URLHelper.COMPLETED + tripId,
            null,
            Response.Listener { response ->
                UpdateLocationToServer()
                Utilities.printAPI_Response(response.toString())

                //   endtrip(response)
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                //  btn_statusUpdate?.setHasActivationState(true)
                btn_statusUpdate?.toggleState()
                UpdateLocationToServer()
                putKey(activity!!, "trip_id", "")

            },
            Response.ErrorListener { error ->
                //  if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                try {
                    if (error is TimeoutError) {
                        //makeText(activity!!, resources.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show()
                    } else if (error is NoConnectionError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_no_network),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is AuthFailureError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_auth_failure),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ServerError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_server_connection),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is NetworkError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_network),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ParseError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_parse),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        // utils.showCustomAlert(activity!!, utils.ALERT_ERROR, getResources().getString(R.string.app_name), error.getMessage());
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    makeText(
                        activity!!,
                        resources.getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] =
                    "" + getKey(activity!!, "token_type") + " " + getKey(activity!!, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    private fun updateConfirmpaymentStatus(tripId: String?) {
        Utilities.PrintAPI_URL(URLHelper.CONFIRM_PAYMENT + tripId, "POST")
        loadingDialog = LoadingDialog(activity!!)
        if (loadingDialog != null) loadingDialog!!.showDialog()
        val `object` = JSONObject()
        try {
            `object`.put("request_id", tripId!!.toInt())
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Utilities.PrintAPI_URL(URLHelper.CONFIRM_PAYMENT, `object`.toString())
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            URLHelper.CONFIRM_PAYMENT,
            `object`,
            Response.Listener { response ->
                UpdateLocationToServer()
                Utilities.printAPI_Response(response.toString())

                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                //   btn_statusUpdate?.setHasActivationState(true)
                btn_statusUpdate?.toggleState()
                UpdateLocationToServer()


                droppedcount = 0
                countstarted = 0
                countpicked = 0
                if (getKey(requireContext()!!, "st").equals("streetride")) {
                    btn_statusUpdate?.setHasActivationState(true)
                    btn_statusUpdate?.toggleState()
                }

            },
            Response.ErrorListener { error ->
                //    if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                try {
                    if (error is TimeoutError) {
                        //    makeText(activity!!, resources.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show()
                    } else if (error is NoConnectionError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_no_network),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is AuthFailureError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_auth_failure),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ServerError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_server_connection),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is NetworkError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_network),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ParseError) {
                        //Toast.makeText(activity!!, getResources().getString(R.string.error_parse), Toast.LENGTH_LONG).show();
                    } else {
                        // utils.showCustomAlert(activity!!, utils.ALERT_ERROR, getResources().getString(R.string.app_name), error.getMessage());
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    makeText(
                        activity!!,
                        resources.getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] =
                    "" + getKey(activity!!, "token_type") + " " + getKey(activity!!, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    private fun updateEndStatus(tripId: String?) {

        Utilities.PrintAPI_URL(URLHelper.END + tripId, "POST")
        loadingDialog = LoadingDialog(activity!!)
        if (loadingDialog != null) loadingDialog!!.showDialog()
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            URLHelper.END + tripId,
            null,
            Response.Listener { response ->

                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                if (response.optJSONObject("data") != null) {
                    //   btn_statusUpdate?.setHasActivationState(true)
                    btn_statusUpdate?.toggleState()
                    if (response.optJSONObject("data").optString("booking_by") != "STREET") {
                        Button_Status = "RATE USER"
                        tl_invoice?.visibility = View.GONE
                        txt_earnings!!.text = getKey(activity!!, "driver_earnings")

                        updateTripDetailUI()
                        UpdateLocationToServer()

                        // isRunning = true

                    } else {

                        //  isRunning = true
                        isarr = true
                        ispick = true
                        isarr = true
                        SharedHelper.putKey(activity!!, "a", "0")
                        SharedHelper.putKey(activity!!, "st", "")
                        putKey(requireContext()!!, "arrived_toggle", "0")
                        putKey(requireContext()!!, "pickedup_toggle", "0")
                        putKey(requireContext()!!, "drop_toggle", "0")
                        Button_Status = "STREET RIDE"
                        emer = true
                        btn_statusUpdate?.setHasActivationState(false)
                        putKey(activity!!, "trip_id", "")
                        putKey(activity!!, "ride_status", "")
                        putKey(activity!!, "user_name", "")
                        putKey(activity!!, "user_image", "")
                        countstarted = 0
                        countpicked = 0
                        droppedcount = 0
                        CurrentStatus = " "
                        // countstartedfirst=0;
                        //  countpickedfirst=0;

                        putKey(activity!!, "driverOnlineFlag", "true")
                        tl_invoice?.visibility = View.GONE
                        fl_ratingView?.visibility = View.GONE
                        fl_tripdetails?.visibility = View.GONE
                        ll_onTripView?.visibility = View.GONE

                        imgSos?.visibility = View.GONE
                        ll_driver_details_title?.visibility = View.GONE
                        ll_onRoute_to_pickup?.visibility = View.GONE
                        llwaitingTime?.visibility = View.GONE
                        ll_driver_edit_dest?.visibility = View.GONE

                        //  iv_call_user?.visibility = View.GONE

                        showlayouts()


                        /* val sgps = GPSTracker(activity!!)
                         current_latitude = sgps.latitude
                         current_longitude = sgps.longitude
                         onLocationChanged(current_latitude, current_longitude)*/

                        // handler()
                        showlayoutsRefreshh()
                        UpdateLocationToServer()
                    }
                }

            },
            Response.ErrorListener { error ->
                //     if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                try {
                    if (error is TimeoutError) {
                        //  makeText(activity!!, resources.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show()
                    } else if (error is NoConnectionError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_no_network),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is AuthFailureError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_auth_failure),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ServerError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_server_connection),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is NetworkError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_network),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ParseError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_parse),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        // utils.showCustomAlert(activity!!, utils.ALERT_ERROR, getResources().getString(R.string.app_name), error.getMessage());
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    makeText(
                        activity!!,
                        resources.getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] =
                    "" + getKey(activity!!, "token_type") + " " + getKey(activity!!, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    private fun GoToOfflineStatus() {
        val `object` = JSONObject()
        try {
            `object`.put("service_status", "offline")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Utilities.PrintAPI_URL(
            URLHelper.DRIVER_STATUS + "   " + "token_type : " + getKey(
                activity!!,
                "token_type"
            ) + " , " + " access_token :  " + getKey(activity!!, "access_token"),
            `object`.toString()
        )
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            URLHelper.DRIVER_STATUS,
            `object`,
            Response.Listener { response ->
                Utilities.printAPI_Response(response.toString())
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                if (response.toString() != null) {
                    mapfocus?.visibility = View.GONE
                    llMapView?.visibility = View.GONE
                    //  btn_goOnline.setVisibility(View.VISIBLE);
                    //  btn_goOffline.setVisibility(View.GONE);
                    putKey(activity!!, "driverOnlineFlag", "false")
                    putKey(activity!!, "driverRideFlag", "false")
                    firebaseHelper = FirebaseHelper(getKey(activity!!, "id")!!)
                    firebaseHelper!!.deleteDriver()
                    firebaseHelperRide = FirebaseHelperRide(getKey(activity!!, "id")!!)
                    firebaseHelperRide!!.deleteDriver()
                    imgMenu?.visibility = View.GONE
                    val mainActivity = activity as MainActivity?
                    showlayoutsOffline()
                }
            },
            Response.ErrorListener { error ->
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                try {
                    if (error is TimeoutError) {
                        //  makeText(activity!!, resources.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show()
                    } else if (error is NoConnectionError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_no_network),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is AuthFailureError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_auth_failure),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ServerError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_server_connection),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is NetworkError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_network),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ParseError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_parse),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        //utils.showCustomAlert(activity!!, utils.ALERT_ERROR, getResources().getString(R.string.app_name), error.getMessage());
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    makeText(
                        activity!!,
                        resources.getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] =
                    "" + getKey(activity!!, "token_type") + " " + getKey(activity!!, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    fun GOToOnlineStatus() {
        val `object` = JSONObject()
        try {
            `object`.put("service_status", "active")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Utilities.PrintAPI_URL(
            URLHelper.DRIVER_STATUS + "   " + "token_type : " + getKey(
                activity!!,
                "token_type"
            ) + " , " + " access_token :  " + getKey(activity!!, "access_token"),
            `object`.toString()
        )
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            URLHelper.DRIVER_STATUS,
            `object`,
            Response.Listener { response ->
                Utilities.printAPI_Response(response.toString())
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                if (response.toString() != null) {
                    hidelayoutsOffline()
                    //  handler();
                    llMapElements?.visibility = View.VISIBLE
                    llMapView?.visibility = View.VISIBLE
                    mapfocus?.visibility = View.VISIBLE
                    putKey(activity!!, "driverOnlineFlag", "true")
                }
            },
            Response.ErrorListener { error ->
                if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                try {
                    if (error is TimeoutError) {
                        //     makeText(activity!!, resources.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show()
                    } else if (error is NoConnectionError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_no_network),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is AuthFailureError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_auth_failure),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ServerError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_server_connection),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is NetworkError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_network),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ParseError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_parse),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        // utils.showCustomAlert(activity!!, utils.ALERT_ERROR, getResources().getString(R.string.app_name), error.getMessage());
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    makeText(
                        activity!!,
                        resources.getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] =
                    "" + getKey(activity!!, "token_type") + " " + getKey(activity!!, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    private fun appInstalledOrNot(uri: String): Boolean {
        val pm = activity!!.packageManager
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            return true
        } catch (e: PackageManager.NameNotFoundException) {
        }
        return false
    }

    /* override fun onAttach(context: Context?) {
         super.onAttach(context)
       //  val activity: Activity
         if (context is Activity) {
             activity = context
         }
     }*/

    override fun onMapReady(googleMap: GoogleMap) {
        setMapStyle(googleMap)
        mMap = googleMap
        setmapType(googleMap)
        if (mMap != null) {
            if (checkPermissions()) {
                mMap!!.uiSettings.isCompassEnabled = false
                mMap!!.isBuildingsEnabled = true
                val maxZoomLevel = 15.0f
                mMap!!.setMaxZoomPreference(maxZoomLevel)
            }
        }
    }

    private fun setmapType(googleMap: GoogleMap?) {
        if (googleMap != null) {
            if (getKey(activity!!, "map_type") != null && getKey(
                    activity!!,
                    "map_type"
                ) == "satellite"
            ) {
                mMap!!.mapType = GoogleMap.MAP_TYPE_SATELLITE
            } else {
                mMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
            }
        }
    }

    private fun setMapStyle(googleMap: GoogleMap?) {
        if (googleMap != null) {
            try {
                val success: Boolean
                success = if (getKey(activity!!, "map_mode") != null && getKey(
                        activity!!,
                        "map_mode"
                    ) == "night"
                ) {
                    googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                            activity!!, R.raw.style_map_night
                        )
                    )
                } else {
                    googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                            activity!!, R.raw.map_style_silver
                        )
                    )
                }
                if (!success) {
                    Utilities.print("Map:Style", "Style parsing failed.")
                } else {
                    Utilities.print("Map:Style", "Style Applied.")
                }
            } catch (e: NotFoundException) {
                Utilities.print("Map:Style", "Can't find style. Error: ")
            }
        }
    }

    private fun UpdateLocationToServer() {
        val sgps = GPSTracker(activity!!)
        current_latitude = sgps.latitude
        current_longitude = sgps.longitude

        srouce_lat = current_latitude
        source_long = current_longitude
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val currentDateandTime: String = sdf.format(Date())
        val `object` = JSONObject()
        try {
            `object`.put("latitude", current_latitude)
            `object`.put("longitude", current_longitude)
            `object`.put("active_time", currentDateandTime)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.v(
            "STATUS_API",
            URLHelper.UPDATE_LOCATION_GET_STATUS + "  Params : " + `object`.toString()
        )
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST, URLHelper.UPDATE_LOCATION_GET_STATUS, `object`,
            Response.Listener { response ->
                Log.v(
                    "STATUS_API",
                    URLHelper.UPDATE_LOCATION_GET_STATUS + "  response : " + response.toString()
                )
                if (response != null) {
                    iscurr = true
                    Log.e("currentstatus", CurrentStatus)
                    Log.e("livestatus", livestatus)

                    try {
                        if (getKey(activity!!, "wallet_flag").equals("1")) {
                            if (response.optString("wallet_status").equals("1")) {
                                putKey(activity!!, "wallet_flag", "")
                                utils.showCustomAlert(
                                    activity!!,
                                    Utilities.ALERT_WARNING,
                                    resources.getString(R.string.app_name),
                                    "Please recharge your wallet to make a trip."
                                )
                            }
                        }

                        when (response.optString("account_status")) {
                            "approved" -> {

                                //Log.e("status_start", "" + getKey(context!!, "signup_first_start"))

                                //   Log.e("status_a", "" + getKey(context!!, "a"))

                                var ride_value = response.optJSONObject("ride")

                                if (ride_value != null) {
                                    Log.e(
                                        "status_ride",
                                        "" + response.optJSONObject("ride")!!.optString("status")
                                    )
                                    if (response.optJSONObject("ride")!!
                                            .optString("status") == "SEARCHING"
                                    ) {
                                        iscurr = false
                                        Log.d(
                                            "TAG",
                                            "UpdateLocationTo-----------------------,mServer: "
                                        )
                                    } else {
                                        iscurr = true
                                    }
                                }

                                if (!getKey(activity!!, "signup_first_start").equals("start")) {
                                    if (response.optString("status").equals("offline")) {
                                        Log.e("status", "offlineee1")
                                        showlayoutsOffline()
                                    }
                                }

                                if (response.optString("account_status").equals("approved")) {
                                    if (response.optString("status").equals("active")) {
                                        /*if(response.optString("alert_status").equals("0")){
                                                    putKey(activity!!, "a", "0")
                                                }*/
                                        if (ride_value != null) {
                                            if (response.optJSONObject("ride")!!.optString("status")
                                                    .equals("SEARCHING")
                                            ) {
                                                if (response.optJSONObject("ride")!!.equals("")) {
                                                    putKey(activity!!, "count", "")
                                                    //   putKey(activity!!, "a", "0")
                                                } else {
                                                    putKey(
                                                        activity!!,
                                                        "count",
                                                        response.optJSONObject("ride")!!
                                                            .optString("time_left_to_respond")
                                                    )
                                                }

                                                if (!getKey(activity!!, "count").equals("")) {
                                                    if ((getKey(
                                                            activity!!,
                                                            "a"
                                                        ).equals("0")) || (getKey(
                                                            activity!!,
                                                            "a"
                                                        ).equals(""))
                                                    ) {
                                                        putKey(activity!!, "a", "1")
                                                        Fun_alert(response)
                                                    }
                                                }
                                            }
                                        } else {
                                        }
                                    }
                                }

                                CurrentVechicle = response.optInt("vehicle_number").toString()



                                when (response.optString("status")) {
                                    "riding" -> {
                                        putKey(
                                            activity!!,
                                            "trip_id",
                                            response.optString("trip_id")
                                        )
                                        setTripStatusButtonText(
                                            response.optString("data"),
                                            response.optInt("paid"), response
                                        )
                                        //  putKey(activity!!, "a", "1")
                                        ll_onTripView?.visibility = View.VISIBLE

                                        /*table_satutes?.visibility=View.VISIBLE*/
                                        //  btn_goOnline.setVisibility(View.GONE);
                                        // btn_goOffline.setVisibility(View.GONE);
                                        if (response.getJSONObject("user_info")
                                                .getString("user_name") != ""
                                        ) {
                                            user_name!!.text = response.getJSONObject("user_info")
                                                .getString("user_name")
                                            usernameTxt!!.text = response.getJSONObject("user_info")
                                                .getString("user_name")
                                            putKey(
                                                activity!!,
                                                "user_phone",
                                                response.getJSONObject("user_info")
                                                    .optString("user_mobile")
                                            )

                                            user_name_below!!.text =
                                                resources.getString(R.string.rate_txt) + " " + response.getJSONObject(
                                                    "user_info"
                                                ).getString("user_name")
                                        }
                                        if (response.getJSONObject("user_info")
                                                .getString("user_img") != ""
                                        ) {
                                            Picasso.with(context)
                                                .load(
                                                    response.getJSONObject("user_info")
                                                        .getString("user_img")
                                                ) // optional
                                                .error(R.drawable.ic_driver)
                                                .placeholder(R.drawable.ic_driver) // optional
                                                .into(img_profile_rate)
                                        }

                                        srcLatitude =
                                            java.lang.Double.valueOf(
                                                response.getJSONObject("trip_info")
                                                    .optString("s_latitude")
                                            )
                                        srcLongitude =
                                            java.lang.Double.valueOf(
                                                response.getJSONObject("trip_info")
                                                    .optString("s_longitude")
                                            )
                                        destLatitude =
                                            java.lang.Double.valueOf(
                                                response.getJSONObject("trip_info")
                                                    .optString("d_latitude")
                                            )
                                        destLongitude =
                                            java.lang.Double.valueOf(
                                                response.getJSONObject("trip_info")
                                                    .optString("d_longitude")
                                            )
                                        tv_pickup_location?.text =
                                            response.getJSONObject("trip_info")
                                                .optString("s_address")
                                        usernameTxt?.text = response.getJSONObject("user_info")
                                            .optString("user_name")
                                        if (response.getJSONObject("user_info")
                                                .getString("user_img") != URLHelper.base + "storage"
                                        ) {
                                            MainActivity.mainactivity?.let {
                                                Glide.with(it)
                                                    .load(
                                                        response.getJSONObject("user_info")
                                                            .optString("user_img")
                                                    )
                                                    .apply(
                                                        RequestOptions().placeholder(R.drawable.ic_driver)
                                                            .dontAnimate()//
                                                            .error(R.drawable.ic_driver)
                                                            .apply(RequestOptions.circleCropTransform())
                                                    ).into(img_profile!!)
                                            }

                                        } else {

                                        }

                                        des_lat = response.getJSONObject("trip_info")
                                            .optString("d_latitude").toFloat().toDouble()
                                        des_long = response.getJSONObject("trip_info")
                                            .optString("d_longitude").toFloat().toDouble()

                                        s_lat = response.getJSONObject("trip_info")
                                            .optString("s_latitude").toFloat().toDouble()
                                        s_long = response.getJSONObject("trip_info")
                                            .optString("s_longitude").toFloat().toDouble()

                                        providerid = response.optString("provider_id")

                                        putKey(
                                            activity!!,
                                            "ride_status",
                                            response.optString("data")
                                        )

                                        putKey(
                                            activity!!,
                                            "cancelproviderid",
                                            response.optString("provider_id")
                                        )
                                        putKey(
                                            activity!!,
                                            "canceltrip_id",
                                            response.optString("trip_id")
                                        )
                                        putKey(
                                            activity!!,
                                            "trip_id",
                                            response.optString("trip_id")
                                        )

                                        putKey(activity!!, "driverOnlineFlag", "false")
                                        putKey(activity!!, "driverRideFlag", "true")

                                        putKey(
                                            activity!!,
                                            "user_name",
                                            response.getJSONObject("user_info")
                                                .optString("user_name")
                                        )
                                        putKey(
                                            activity!!,
                                            "user_image",
                                            response.getJSONObject("user_info")
                                                .optString("user_img")
                                        )
                                        if (getKey(activity!!, "status") == "streetride") {
                                            putKey(activity!!, "status", "")
                                            countstarted = 0
                                            countpicked = 0
                                            droppedcount = 0
                                            previoustripid =
                                                getKey(activity!!, "street_trip_id")
                                            previousVechicle =
                                                response.optInt("vehicle_number").toString()
                                            // getKey(activity!!, "")
                                            //Toast.makeText(activity,"streetride",Toast.LENGTH_LONG).show();
                                        }

                                        //  CurrentStatus = response.optString("data");
                                        //CurrentVechicle = String.valueOf(response.optInt("vehicle_number"));
                                        alert = response.optInt("alert_status")
                                        if (mPlayer != null && mPlayer!!.isPlaying) {
                                            mPlayer!!.stop()
                                            mPlayer = null
                                        }
                                    }

                                    "active" -> {
                                        Log.e("status", "activie")


                                        putKey(activity!!, "driverOnlineFlag", "true")
                                        putKey(activity!!, "driverRideFlag", "false")
                                        providerid = response.optString("provider_id")
                                        currenttripid = response.optString("trip_id")

                                        firebaseHelperRide =
                                            FirebaseHelperRide(response.optString("provider_id"))
                                        firebaseHelperRide!!.deleteDriver()
                                        // setTripStatusButtonText(response.optString("data"), response.optInt("paid"));
                                        // setTripStatusButtonText(response.optString("data"), response.optInt("paid"));
                                        if (response.optString("data") == "SEARCHING") {
                                            //  if(response.optString("data").equals("SEARCHING")) {
                                            des_lat = response.getJSONObject("trip_info")
                                                .optString("d_latitude").toFloat()
                                                .toDouble()
                                            des_long = response.getJSONObject("trip_info")
                                                .optString("d_longitude").toFloat()
                                                .toDouble()
                                            s_lat = response.getJSONObject("trip_info")
                                                .optString("s_latitude").toFloat()
                                                .toDouble()
                                            s_long = response.getJSONObject("trip_info")
                                                .optString("s_longitude").toFloat()
                                                .toDouble()

                                            putKey(
                                                activity!!,
                                                "ride_status",
                                                response.optString("data")
                                            )

                                            putKey(
                                                activity!!,
                                                "trip_id",
                                                response.optString("trip_id")
                                            )

                                            /* if (response.optJSONObject("ride")!!.equals("")) {
                                                    putKey(activity!!, "count", "")
                                                } else {
                                                    putKey(activity!!, "count", response.optJSONObject("ride").optString("time_left_to_respond"))
                                                }

                                                if (!getKey(activity!!, "count").equals("")) {
                                                    if ((getKey(activity!!, "a").equals("0")) || (getKey(activity!!, "a").equals(""))) {
                                                        //putKey(activity!!, "Fun_alert", "1")
                                                        Fun_alert(response)
                                                        putKey(activity!!,"a","1")
                                                    }
                                                }
*/
                                            setTripStatusButtonText(
                                                response.optString("data"),
                                                response.optInt("paid"), response
                                            )
                                            CurrentStatus = response.optString("data")

                                        }
//
//
                                        else if (response.optString("alert_status") == "1") {
                                            CurrentStatus = response.optString("data")
                                            //  if(response.optString("data").equals("SEARCHING")) {
                                            des_lat = response.getJSONObject("ride")
                                                .optString("d_latitude").toFloat()
                                                .toDouble()
                                            des_long = response.getJSONObject("ride")
                                                .optString("d_longitude").toFloat()
                                                .toDouble()
                                            s_lat = response.getJSONObject("ride")
                                                .optString("s_latitude").toFloat()
                                                .toDouble()
                                            s_long = response.getJSONObject("ride")
                                                .optString("s_longitude").toFloat()
                                                .toDouble()

//Toast.makeText(context!!,""+getKey(activity!!, "a"),Toast.LENGTH_SHORT).show()
                                            putKey(
                                                activity!!,
                                                "ride_status",
                                                response.optString("data")
                                            )
                                            putKey(
                                                activity!!,
                                                "trip_id",
                                                response.optString("trip_id")
                                            )
                                            putKey(
                                                activity!!,
                                                "trip_type",
                                                response.optString("trip_type")
                                            )

                                            if (response.getJSONObject("ride").equals("")) {
                                                putKey(activity!!, "count", "")
                                            } else {
                                                putKey(
                                                    activity!!,
                                                    "count",
                                                    response.getJSONObject("ride")
                                                        .optString("time_left_to_respond")
                                                )

                                                if (!getKey(
                                                        activity!!,
                                                        "count"
                                                    ).equals("") && Trip_alert_Dialog == null
                                                ) {

                                                    if ((getKey(
                                                            activity!!,
                                                            "a"
                                                        ).equals("0")) || (getKey(
                                                            activity!!,
                                                            "a"
                                                        ).equals(""))
                                                    ) {
                                                        putKey(activity!!, "a", "1")
                                                        Fun_alert(response)

                                                    }
                                                }
                                            }

                                        } else if (response.optString("data") == "") {

                                            // ll_onRoute_to_pickup?.visibility(View.GONE)
                                            //imageView.showOrGone(true) //will make it visible
                                            if (response.optString("alert_status").equals("0")) {
                                                putKey(activity!!, "a", "0")
                                            }
                                            this.ll_onRoute_to_pickup?.showOrGone(false) //will make it gone
                                            ll_onTripView?.visibility = View.GONE
                                            iv_call_user?.visibility = View.GONE
                                            showlayouts()
                                            livestatus = " "
                                            tl_invoice?.visibility = View.GONE
                                            fl_ratingView?.visibility = View.GONE
                                            // fl_completed_rating.setVisibility(View.GONE);
                                            fl_tripdetails?.visibility = View.GONE
                                            CurrentStatus = response.optString("data")
                                            if (Trip_alert_Dialog != null) {
                                                Trip_alert_Dialog!!.dismiss()
                                                countDownTimer!!.cancel()
                                            }

                                        } else if (response.optString("data") == "CANCELLED") {
                                            // putKey(activity!!, "a", "0")
                                            mMap!!.clear()
                                            clearTripStatus(
                                                response.optString("trip_id"),
                                                response.optInt("provider_id").toString(),
                                                response.optString("cancel_reason"),
                                                response.optString("cancelled_by")
                                            )
                                            emer = true
                                            //   Toast.makeText(getActivity(),"yes",Toast.LENGTH_SHORT).show()
                                            if (response.optString("alert_status") == "0") {
                                                putKey(activity!!, "a", "0")
                                                putKey(activity!!, "arrived_toggle", "0")
                                                putKey(activity!!, "pickedup_toggle", "0")
                                                putKey(activity!!, "drop_toggle", "0")
                                            }
                                            if (response.optString("cancelled_by") != "") {
                                                PreviousStatus = "CANCELLED"
                                                CurrentStatus = " "
                                                mMap!!.clear()
                                                //sourceMarker!!.remove()
                                                // destinationMarker!!.remove()
                                                ll_onTripView?.visibility = View.GONE
                                                ll_driver_details_title?.visibility = View.GONE
                                                ll_driver_edit_dest?.visibility = View.GONE
                                                mMap!!.clear()
                                                showlayouts()
                                                this.ll_onRoute_to_pickup?.showOrGone(false)

                                                /* showlayoutsRefresh()
                                                     PreviousStatus = "CANCELLED"
                                                     this.ll_onRoute_to_pickup?.showOrGone(false)
                                                     ll_onTripView?.visibility = View.GONE
                                                     ll_driver_details_title?.visibility = View.GONE
                                                     ll_driver_edit_dest?.visibility = View.GONE
                                                     showlayouts()*/

//                                                    clearTripStatus(
//                                                        response.optString("trip_id"),
//                                                        response.optInt("provider_id").toString(),
//                                                        response.optString("cancel_reason"),
//                                                        response.optString("cancelled_by")
//                                                    )
                                            } else {
                                                if (mPlayer != null && mPlayer!!.isPlaying) {
                                                    mPlayer!!.stop()
                                                    mPlayer = null
                                                }
                                                if (Trip_alert_Dialog != null) {
                                                    Trip_alert_Dialog!!.dismiss()
                                                    countDownTimer!!.cancel()
                                                }

                                            }
                                            CurrentStatus = response.optString("data")
                                        } else {
                                            // handler();

                                            hidelayoutsOffline()

                                            if (mPlayer != null && mPlayer!!.isPlaying) {
                                                mPlayer!!.stop()
                                                mPlayer = null
                                            }
                                            if (Trip_alert_Dialog != null) {
                                                Trip_alert_Dialog!!.dismiss()
                                                countDownTimer!!.cancel()
                                            }
                                            CurrentStatus = response.optString("data")
                                        }
                                    }

                                    "offline" -> {
                                        Log.e("status", "offline1")
                                        showlayoutsOffline()
                                        Log.e("status", "offline2")
                                        putKey(activity!!, "driverOnlineFlag", "false")
                                        firebaseHelper = FirebaseHelper(providerid!!)
                                        firebaseHelper!!.deleteDriver()

                                        // handleCheckStatus.removeCallbacksAndMessages(null);
                                        // isRunning = false;

                                        // fl_ratingView.setVisibility(View.GONE);
                                        //   fl_completed_rating.setVisibility(View.GONE);
                                        mapfocus?.visibility = View.VISIBLE
                                        imgMenu?.visibility = View.GONE
                                        llMapView?.visibility = View.VISIBLE
                                        ll_onTripView?.visibility = View.GONE
                                    }
                                }
                            }

                            "onboarding" -> {
                                //  handleCheckStatus.removeMessages(0);
                                handleCheckStatus!!.removeCallbacksAndMessages(null)
                                if (onboarding) {
                                    onboarding = false
                                    val intent1 = Intent(activity, WaitingForApproval::class.java)
                                    activity!!.startActivity(intent1)
                                }
                            }

                            "banned" -> {
                                //  handleCheckStatus.removeMessages(0);
                                handleCheckStatus!!.removeCallbacksAndMessages(null)
                                if (onboarding) {
                                    onboarding = false
                                    val intent1 = Intent(activity, WaitingForApproval::class.java)
                                    activity!!.startActivity(intent1)
                                }
                            }
                        }
                        if (response.getJSONObject("user_info").optString("user_notes") != null) {
                            putKey(
                                activity!!,
                                "user_notes",
                                response.getJSONObject("user_info").optString("user_notes")
                            )
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            },
            Response.ErrorListener { error ->
                try {
                    var json: String? = null
                    var Message: String
                    val response = error.networkResponse
                    if (response != null && response.data != null) {
                        try {
                            val errorObj = JSONObject(String(response.data))
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    //utils.showToast(activity, errorObj.optString("message"));
                                } catch (e: Exception) {
                                    //Utilities.showToast(activity!!, getString(R.string.something_went_wrong))
                                }
                            } else if (response.statusCode == 401) {
                                //  refreshToken();
                                // SharedHelper.putKey(activity!!, "loggedIn", getString(R.string.False));
                                //  GoToBeginActivity();
                            } else if (response.statusCode == 422) {
                                json = NTApplication.trimMessage(String(response.data))
                                if (json !== "" && json != null) {
                                    // utils.showToast(activity!!, json);
                                } else {
                                    //  Utilities.showToast(activity!!, getString(R.string.please_try_again))
                                }
                            } else if (response.statusCode == 503) {
                                //  Utilities.showToast(activity!!, getString(R.string.server_down))
                            }
                        } catch (e: Exception) {
                            //  Utilities.showToast(activity!!, getString(R.string.something_went_wrong))
                        }
                    } else {
                        if (error is NoConnectionError) {
                            // Utilities.showToast(activity!!, getString(R.string.oops_connect_your_internet))
                        } else if (error is NetworkError) {
                            //  Utilities.showToast(activity!!, getString(R.string.oops_connect_your_internet))
                        } else if (error is TimeoutError) {
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    //  makeText(activity!!, activity!!.getString(R.string.something_went_wrong), Toast.LENGTH_LONG).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "$tokentype $token"
                Log.e("status_header", "" + headers.toString())
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)

    }


    private fun GoToBeginActivity() {
        val mainIntent = Intent(activity, ActivityBegin::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(mainIntent)
        activity!!.finish()
    }

    fun setTripStatusButtonText(ride_status: String?, paid: Int, response: JSONObject) {
        when (ride_status) {
            "END" -> {
                Trip_alert_Dialog = null
                tl_invoice?.visibility = View.GONE
                CurrentStatus = "END"
                endtrip(response)
            }

            "SEARCHING" -> {
                tl_invoice?.visibility = View.GONE
                livestatus = "SEARCHING"
                Button_Status = "SEARCHING"
//                Log.i("MyTest",response.getJSONObject("user_info").optString("user_name"))
                btn_statusUpdate?.setHasActivationState(true)
                btn_statusUpdate?.setText(resources.getString(R.string.slide_to_arrive))
                Log.e("currentstatus", CurrentStatus)
                Log.e("previoustripid", previoustripid!!)
                Log.e("currenttripid", currenttripid)
                if (CurrentStatus != "SEARCHING") {
                    Log.e("currentstatusin", CurrentStatus)
                    Log.e("previoustripidin", previoustripid!!)
                    Log.e("currenttripidin", currenttripid)

                }
                firebaseHelper = FirebaseHelper(getKey(activity!!, "id")!!)
                firebaseHelper!!.deleteDriver()
                putKey(activity!!, "driverRideFlag", "false")

            }

            "STARTED" -> {
                Trip_alert_Dialog = null
                SharedHelper.putKey(activity!!, "a", "1")
                if (isstart) {
                    updateTripDetailUI()
                    started(response)
                    usernameTxt!!.text = response.getJSONObject("user_info").getString("user_name")
                    putKey(
                        activity!!,
                        "user_phone",
                        response.getJSONObject("user_info").optString("user_mobile")
                    )
                    livestatus = "STARTED"
                    CurrentStatus = "STARTED"

                    Button_Status = "ARRIVED"
                    ll_onTripView?.visibility = View.VISIBLE
                    imgSos?.visibility = View.GONE
                    driver_status?.visibility = View.GONE
                    if (getKey(getActivity()!!, "street_ride_status") == "street_ride") {
//                        putKey(activity!!, "street_ride_status", "")
                        iv_call_user?.visibility = View.GONE
                    } else {
                        iv_call_user?.visibility = View.VISIBLE
                    }
//                    iv_call_user?.visibility = View.VISIBLE
                    llwaitingTime?.visibility = View.GONE
                    ll_onRoute_to_pickup?.visibility = View.VISIBLE
                    if (activity!!.isDestroyed && activity!!.isFinishing) {
                    } else {
                        disable()
                    }
                    btn_statusUpdate?.setHasActivationState(true)
                    btn_statusUpdate?.setText(resources.getString(R.string.slide_to_arrive))
                }
                putKey(activity!!, "driverOnlineFlag", "false")
                firebaseHelper = FirebaseHelper(providerid!!)
                firebaseHelper!!.deleteDriver()
                putKey(activity!!, "driverRideFlag", "true")
            }

            "ARRIVED" -> {
                if (isarr) {
                    updateTripDetailUI()
                    arrived(response)
                    livestatus = "ARRIVED"
                    CurrentStatus = "ARRIVED"
                    Button_Status = "PICKEDUP"
                    tl_invoice?.visibility = View.GONE

                    disable()
//                    imgSos?.visibility = View.VISIBLE
                    ll_onTripView?.visibility = View.VISIBLE
                    driver_status?.visibility = View.VISIBLE
                    driver_status!!.setText(resources.getString(R.string.wait_time))
                    btn_statusUpdate?.setText(resources.getString(R.string.slide_to_pickup))
                    tv_wait_to_arrive?.visibility = View.VISIBLE
                    ll_driver_details_title?.visibility = View.VISIBLE
                    ll_driver_edit_dest?.visibility = View.GONE
                    llwaitingTime?.visibility = View.VISIBLE
                    ll_onRoute_to_pickup?.visibility = View.GONE
                    if (getKey(getActivity()!!, "street_ride_status") == "street_ride") {
//                        putKey(activity!!, "street_ride_status", "")
                        iv_call_user?.visibility = View.GONE
                    } else {
                        iv_call_user?.visibility = View.VISIBLE
                    }
//                    iv_call_user?.visibility = View.VISIBLE
                    putKey(activity!!, "driverOnlineFlag", "false")
                    putKey(activity!!, "driverRideFlag", "true")
                    /*  if (!getKey(requireContext()!!, "arrived_toggle").equals("1")) {
                        btn_statusUpdate?.toggleState()
                        putKey(requireContext()!!, "arrived_toggle", "1")

                    }*/
                }

            }

            "PICKEDUP" -> {
                Log.i("ll_driver_edit", "ll_driver_edit")
                if (ispick) {
                    updateTripDetailUI()
                    pickedup(response)
                    livestatus = "PICKEDUP"
                    CurrentStatus = "PICKEDUP"
                    Button_Status = "DROPPED"
//                    imgSos?.visibility = View.VISIBLE
                    ll_onTripView?.visibility = View.VISIBLE
                    driver_status?.visibility = View.VISIBLE
                    driver_status!!.setText(resources.getString(R.string.wait_time))
                    btn_statusUpdate?.setText(resources.getString(R.string.slide_to_dropoff))
                    tl_invoice?.visibility = View.GONE
                    disable()
                    imgSos!!.visibility = View.GONE
                    Log.i("ll_driver_edit_dest", "ll_driver_edit_dest")
                    ll_driver_edit_dest?.visibility = View.VISIBLE
                    llwaitingTime?.visibility = View.VISIBLE
                    ll_driver_details_title?.visibility = View.GONE
                    ll_onRoute_to_pickup?.visibility = View.GONE
                    // btn_cancelRequest?.visibility = View.GONE
                    if (getKey(getActivity()!!, "street_ride_status") == "street_ride") {
                        putKey(activity!!, "street_ride_status", "")
                        iv_call_user?.visibility = View.GONE
                    } else {
                        iv_call_user?.visibility = View.VISIBLE
                    }
//                    iv_call_user?.visibility = View.VISIBLE
                    //btn_statusUpdate?.setHasActivationState(true)
                    /*if (getKey(getActivity()!!, "st").equals("streetride")) {


                    } else {
                        if (!getKey(requireContext()!!, "pickedup_toggle").equals("1")) {
                            btn_statusUpdate?.toggleState()
                            putKey(requireContext()!!, "pickedup_toggle", "1")
                        }
                    }
*/
                    putKey(activity!!, "driverOnlineFlag", "false")
                    putKey(activity!!, "driverRideFlag", "true")

                }
                if (SharedHelper.getKey(context!!, "fetch1")!!.isEmpty()) {
                    fetchdistanceandTimePickedup(latLng!!.latitude, latLng!!.longitude)
                    SharedHelper.putKey(context!!, "fetch1", "1")
                }

                firebaseHelper = FirebaseHelper(providerid!!)
                firebaseHelper!!.deleteDriver()

            }

            "DROPPED" -> {

//                updateTripDetailUI()
                livestatus = "DROPPED"
                CurrentStatus = "DROPPED"

                distanceInKm = 0.00
                putKey(activity!!, "drop_distance", "0.00")
                distanceInMeters = 0.00
                lastLocation = null

                dropped(response, "1")
                ll_onTripView?.visibility = View.VISIBLE
                tl_invoice?.visibility = View.VISIBLE
                SharedHelper.putKey(context!!, "fetch1", "")
                tl_invoice?.visibility = View.VISIBLE
                hidelayouts()
                putKey(activity!!, "driverOnlineFlag", "true")
                firebaseHelper = FirebaseHelper(providerid!!)
                firebaseHelper!!.deleteDriver()
                putKey(activity!!, "driverRideFlag", "false")

                /*if (getKey(getActivity()!!, "st").equals("streetride")) {

                }
                if (!getKey(requireContext()!!, "drop_toggle").equals("1")) {
                    btn_statusUpdate?.toggleState()
                    putKey(requireContext()!!, "drop_toggle", "1")
                }*/

            }

            "COMPLETED" -> {
                if (paid == 1) {
                    hidelayouts()
                    fl_ratingView?.visibility = View.VISIBLE
                    user_name!!.text = response.getJSONObject("user_info")
                        .getString("user_name")
                    user_name_below!!.text =
                        resources.getString(R.string.rate_txt) + " " + response.getJSONObject("user_info")
                            .getString("user_name")
                    providerfinalRating!!.onRatingBarChangeListener =
                        OnRatingBarChangeListener { ratingBar, rating, b ->
                            if (rating < 1.0f) {
                                providerfinalRating!!.rating = 5.0f
                                selectedRating = "5".toInt()
                            }
                            selectedRating = rating.toInt()
                        }
                } else {
                    Button_Status = "COLLECT FARE"
                    btn_statusUpdate?.setText(resources.getString(R.string.CollectFare))

                }

                //btn_statusUpdate?.setHasActivationState(true)
                //  btn_statusUpdate?.toggleState()
                // btn_statusUpdate?.isEnabled = true

                livestatus = "COMPLETED"

                imgSos?.visibility = View.GONE
                putKey(activity!!, "driverRideFlag", "false")
                putKey(activity!!, "driverOnlineFlag", "true")
                CurrentStatus = "COMPLETED"
                ll_driver_details_title?.visibility = View.GONE
                ll_onRoute_to_pickup?.visibility = View.GONE
                ll_driver_edit_dest?.visibility = View.GONE
                llwaitingTime?.visibility = View.GONE
                iv_call_user?.visibility = View.GONE
                resetChronometer()
                // btn_statusUpdate?.toggleState()
                resetChronometer()
                table_satutes?.visibility = View.VISIBLE
                tl_invoice?.visibility = View.GONE
                CurrentStatus = "COMPLETED"
                if (mMap != null) {
                    MapAnimator.getInstance().stopAnim()
                }
                firebaseHelperRide = FirebaseHelperRide(providerid!!)
                firebaseHelperRide!!.deleteDriver()

            }
        }
    }

    private fun updateTripDetailUI() {
        when (getKey(activity!!, "ride_status")) {
            "SEARCHING" -> {
                tl_invoice?.visibility = View.GONE
            }

            "STARTED" -> {
                Button_Status = "ARRIVED"
                ll_onTripView?.visibility = View.VISIBLE
                ll_onRoute_to_pickup?.visibility = View.VISIBLE
                if (getKey(getActivity()!!, "street_ride_status") == "street_ride") {
//                    putKey(activity!!, "street_ride_status", "")
                    iv_call_user?.visibility = View.GONE
                } else {
                    iv_call_user?.visibility = View.VISIBLE
                }
//              iv_call_user?.visibility = View.VISIBLE
                imgSos?.visibility = View.GONE
                driver_status?.visibility = View.GONE
                llwaitingTime?.visibility = View.GONE

                if (activity!!.isDestroyed && activity!!.isFinishing) {

                } else {
                    disable()
                }
            }

            "ARRIVED" -> {
                tl_invoice?.visibility = View.GONE

                disable()
            }

            "PICKEDUP" -> {
                tl_invoice?.visibility = View.GONE

                disable()
            }

            "DROPPED" -> {
                tl_invoice?.visibility = View.VISIBLE
                hidelayouts()

            }

            "END" -> {
                tl_invoice?.visibility = View.GONE

                CurrentStatus = "END"
            }

            "COMPLETED" -> {
                resetChronometer()
                table_satutes?.visibility = View.VISIBLE
                tl_invoice?.visibility = View.GONE
                CurrentStatus = "COMPLETED"
            }
        }
    }


    fun endtrip(response: JSONObject) {
        try {
            if (response != null) {

                //  btn_statusUpdate?.setHasActivationState(true)
                //   btn_statusUpdate?.toggleState()
                // btn_statusUpdate?.isEnabled = true
                tv_tripid!!.text = response.getJSONObject("trip_info").optString("id")
                tv_bookingid!!.text = response.getJSONObject("trip_info").optString("booking_id")
                tv_pickup!!.text = response.getJSONObject("trip_info").optString("s_address")
                tv_drop!!.text = response.getJSONObject("trip_info").optString("d_address")
                tv_driverNotes!!.text = response.optString("message")
                putKey(
                    activity!!,
                    "booking_by",
                    response.getJSONObject("trip_info").optString("booking_by")
                )
                srcLatitude = java.lang.Double.valueOf(
                    response.getJSONObject("trip_info").optString("s_latitude")
                )
                srcLongitude = java.lang.Double.valueOf(
                    response.getJSONObject("trip_info").optString("s_longitude")
                )
                destLatitude = java.lang.Double.valueOf(
                    response.getJSONObject("trip_info").optString("d_latitude")
                )
                destLongitude = java.lang.Double.valueOf(
                    response.getJSONObject("trip_info").optString("d_longitude")
                )

//                    User Details
                if (response.optString("payment_update") == "0") {

                    if (response.getJSONObject("payment").optString("payment_mode")
                            .equals("CORPORATE")
                    ) {
                        edit_toll_fare!!.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.icon_edit,
                            0
                        )
                        edit_extra_fare!!.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.icon_edit,
                            0
                        )
                        edit_base_fare!!.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.icon_edit,
                            0
                        )
                        btn_statusUpdate?.setText(resources.getString(R.string.cor))

                        imgSos!!.setOnClickListener(null)
                        iv_call_user!!.setOnClickListener(null)
                        img_navbtn!!.setOnClickListener(null)
                        edit_stop1Address!!.setOnClickListener(null)
                        edit_stop2Address!!.setOnClickListener(null)
                        edit_dest!!.setOnClickListener(null)
                        ic_pickup_navigate!!.setOnClickListener(null)

                        Button_Status = "CONFIRM PAYMENT"
                    } else {
                        edit_toll_fare!!.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.icon_edit,
                            0
                        )
                        edit_extra_fare!!.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.icon_edit,
                            0
                        )
                        edit_base_fare!!.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.icon_edit,
                            0
                        )
                        btn_statusUpdate?.setText(resources.getString(R.string.Confirmp))
                        imgSos!!.setOnClickListener(null)
                        iv_call_user!!.setOnClickListener(null)
                        img_navbtn!!.setOnClickListener(null)
                        edit_stop1Address!!.setOnClickListener(null)
                        edit_stop2Address!!.setOnClickListener(null)
                        edit_dest!!.setOnClickListener(null)
                        ic_pickup_navigate!!.setOnClickListener(null)

                        Button_Status = "CONFIRM PAYMENT"
                    }
                } else {
                    if (response.getJSONObject("payment").optString("payment_mode")
                            .equals("CORPORATE")
                    ) {
                        edit_toll_fare!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        edit_extra_fare!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        edit_base_fare!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        edit_toll_fare!!.setOnClickListener(null)
                        edit_extra_fare!!.setOnClickListener(null)
                        edit_base_fare!!.setOnClickListener(null)
                        imgSos!!.setOnClickListener(null)
                        iv_call_user!!.setOnClickListener(null)
                        img_navbtn!!.setOnClickListener(null)
                        edit_stop1Address!!.setOnClickListener(null)
                        edit_stop2Address!!.setOnClickListener(null)
                        edit_dest!!.setOnClickListener(null)
                        ic_pickup_navigate!!.setOnClickListener(null)
                        btn_statusUpdate?.setText("Corporate")

                        Button_Status = "COLLECT FARE"
                    } else {
                        edit_toll_fare!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        edit_extra_fare!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        edit_base_fare!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        edit_toll_fare!!.setOnClickListener(null)
                        edit_extra_fare!!.setOnClickListener(null)
                        edit_base_fare!!.setOnClickListener(null)
                        imgSos!!.setOnClickListener(null)
                        iv_call_user!!.setOnClickListener(null)
                        img_navbtn!!.setOnClickListener(null)
                        edit_stop1Address!!.setOnClickListener(null)
                        edit_stop2Address!!.setOnClickListener(null)
                        edit_dest!!.setOnClickListener(null)
                        ic_pickup_navigate!!.setOnClickListener(null)
                        btn_statusUpdate?.setText(resources.getString(R.string.CollectFare))

                        Button_Status = "COLLECT FARE"
                    }
                }

                try {
                    if (response.optJSONObject("trip_info").optJSONObject("payment") != null) {
                        tv_baseFare!!.text =
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("base_fare")
                        tv_flat_fee!!.text =
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("flat_fare")
                        tv_distanceFare!!.text =
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("distance_fare")
                        tv_minFare!!.text =
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("min_fare")
                        tv_waitingFare!!.text =
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("waiting_fare")
                        tv_waitingStopFare!!.text =
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("stop_waiting_fare")
                        tv_taxFare!!.text =
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("vat")

                        payment_mode_label!!.text =
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("payment_mode")
                        tv_discountFare!!.text =
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("discount")
                        tv_totalFare!!.text =
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("total")
                        txt_earnings!!.text =
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("earnings")
                        putKey(
                            activity!!,
                            "driver_earnings",
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("earnings")
                        )
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                txt_earnings!!.text = getKey(activity!!, "driver_earnings")
                try {
                    if (response.optJSONObject("user_info") != null) {
                        user_name!!.text =
                            response.getJSONObject("user_info").optString("user_name")
                        user_name_below!!.text =
                            resources.getString(R.string.rate_txt) + " " + response.getJSONObject("user_info")
                                .optString("user_name")
                        tv_userName!!.text =
                            response.getJSONObject("user_info").optString("user_name")
                        providerfinalRating!!.rating =
                            response.getJSONObject("user_info").optString("rating").toFloat()
                        putKey(
                            activity!!,
                            "user_phone",
                            response.getJSONObject("user_info").optString("user_mobile")
                        )


                        user_name_below!!.text =
                            resources.getString(R.string.rate_txt) + " " + response.getJSONObject("user_info")
                                .optString("user_name")
                        Picasso.with(context)
                            .load(response.getJSONObject("user_info").optString("user_img"))
                            .placeholder(R.drawable.ic_driver) // optional
                            .error(R.drawable.ic_driver) // optional
                            .into(iv_userAvatar)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun arrived(response: JSONObject) {
        try {
            if (response != null) {

                isarr = false
                //btn_statusUpdate?.setHasActivationState(true)
                // btn_statusUpdate?.isEnabled = true
//                tv_wait_to_arrive!!.text = resources.getString(R.string.wait_for) +" " +response.getJSONObject("user_info").optString("user_name") +" " + resources.getString(R.string.to_arrive)
                tv_wait_to_arrive!!.text =
                    resources.getString(R.string.wait_for) + " " + response.getJSONObject("user_info")
                        .optString("user_name")
                usernameTxt!!.text = response.getJSONObject("user_info").optString("user_name")
                putKey(
                    activity!!,
                    "user_phone",
                    response.getJSONObject("user_info").optString("user_mobile")
                )
                if (response.getJSONObject("user_info")
                        .getString("user_img") != URLHelper.base + "storage"
                ) {

                    Glide.with(activity!!)
                        .load(response.getJSONObject("user_info").optString("user_img"))
                        .apply(
                            RequestOptions().placeholder(R.drawable.ic_driver)
                                .dontAnimate().error(R.drawable.ic_driver)
                                .apply(RequestOptions.circleCropTransform())
                        ).into(img_profile!!)
                }

                if (sourceMarker != null) {
                    sourceMarker!!.remove()
                    val currentMarkerOptionss = MarkerOptions().title("Source").anchor(0.5f, 0.75f)
                        .position(sourceLatLng!!).icon(
                            BitmapDescriptorFactory.fromBitmap(
                                getBitmapFromVectorDrawable(
                                    activity!!,
                                    R.drawable.ic_person
                                )
                            )
                        )
                    sourceMarker = mMap!!.addMarker(currentMarkerOptionss)
                }

                srcLatitude = java.lang.Double.valueOf(
                    response.getJSONObject("trip_info").optString("s_latitude")
                )
                srcLongitude = java.lang.Double.valueOf(
                    response.getJSONObject("trip_info").optString("s_longitude")
                )
                destLatitude = java.lang.Double.valueOf(
                    response.getJSONObject("trip_info").optString("d_latitude")
                )
                destLongitude = java.lang.Double.valueOf(
                    response.getJSONObject("trip_info").optString("d_longitude")
                )
                if (!response.getJSONObject("trip_info").optString("stop1_latitude").isEmpty()) {
                    stop1Latitude = java.lang.Double.valueOf(
                        response.getJSONObject("trip_info").optString("stop1_latitude")
                    )
                    stop1Longitude = java.lang.Double.valueOf(
                        response.getJSONObject("trip_info").optString("stop1_longitude")
                    )
                }
                if (!response.getJSONObject("trip_info").optString("stop2_latitude").isEmpty()) {
                    stop2Latitude = java.lang.Double.valueOf(
                        response.getJSONObject("trip_info").optString("stop2_latitude")
                    )
                    stop2Longitude = java.lang.Double.valueOf(
                        response.getJSONObject("trip_info").optString("stop2_longitude")
                    )
                }
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun pickedup(response: JSONObject) {
        try {
            if (response != null) {
                ispick = false

                // btn_statusUpdate?.setHasActivationState(true)
                //  btn_statusUpdate?.toggleState()
                // btn_statusUpdate?.isEnabled = true

                srcLatitude = java.lang.Double.valueOf(
                    response.getJSONObject("trip_info").optString("s_latitude")
                )
                srcLongitude = java.lang.Double.valueOf(
                    response.getJSONObject("trip_info").optString("s_longitude")
                )
                destLatitude = java.lang.Double.valueOf(
                    response.getJSONObject("trip_info").optString("d_latitude")
                )
                destLongitude = java.lang.Double.valueOf(
                    response.getJSONObject("trip_info").optString("d_longitude")
                )
                /*destLatitude = java.lang.Double.valueOf(10.221286)
                destLongitude = java.lang.Double.valueOf(78.376575)*/
                tv_dest_address!!.text = response.getJSONObject("trip_info").optString("d_address")

                if (!response.getJSONObject("trip_info").optString("stop1_latitude").isEmpty()) {
                    stop1Latitude = java.lang.Double.valueOf(
                        response.getJSONObject("trip_info").optString("stop1_latitude")
                    )
                    stop1Longitude = java.lang.Double.valueOf(
                        response.getJSONObject("trip_info").optString("stop1_longitude")
                    )
                    tv_stop_one!!.text =
                        response.getJSONObject("trip_info").optString("stop1_address")
                } else {
                    tv_stop_one!!.text = resources.getString(R.string.stop_label_one)
                }
                if (!response.getJSONObject("trip_info").optString("stop2_latitude").isEmpty()) {
                    stop2Latitude = java.lang.Double.valueOf(
                        response.getJSONObject("trip_info").optString("stop2_latitude")
                    )
                    stop2Longitude = java.lang.Double.valueOf(
                        response.getJSONObject("trip_info").optString("stop2_longitude")
                    )
                    tv_stop_two!!.text =
                        response.getJSONObject("trip_info").optString("stop2_address")
                } else {
                    tv_stop_two!!.text = resources.getString(R.string.stop_label_two)
                }

                usernameTxt!!.text = response.getJSONObject("user_info").optString("user_name")
                putKey(
                    activity!!,
                    "user_phone",
                    response.getJSONObject("user_info").optString("user_mobile")
                )
                if (response.getJSONObject("user_info")
                        .getString("user_img") != URLHelper.base + "storage"
                ) {

                    Glide.with(activity!!)
                        .load(response.getJSONObject("user_info").optString("picture"))
                        .apply(
                            RequestOptions().placeholder(R.drawable.ic_driver)
                                .dontAnimate().error(R.drawable.ic_driver)
                                .apply(RequestOptions.circleCropTransform())
                        ).into(img_profile!!)

                }
                if (countpicked <= 3) {
                    countpicked++
                    showPathFromPickupToDrop(latLng!!.latitude, latLng!!.longitude)
                }
            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    public fun dropped(response: JSONObject, m: String) {
        try {
            if (response != null) {
                //btn_statusUpdate?.setHasActivationState(true)
                //   btn_statusUpdate?.toggleState()
                // btn_statusUpdate?.isEnabled = true

                countstarted = 0
                countpicked = 0
                Utilities.printAPI_Response(response.getJSONObject("trip_info").toString())
                tv_tripid!!.text = response.getJSONObject("trip_info").optString("id")
                tv_bookingid!!.text = response.getJSONObject("trip_info").optString("booking_id")
                if (m.equals("1")) {
                    tv_pickup!!.text = response.getJSONObject("trip_info").optString("s_address")
                    tv_drop!!.text = response.getJSONObject("trip_info").optString("d_address")
                    //    tv_driverNotes!!.text = response.getJSONObject("trip_info").optString("message")
                    srcLatitude = java.lang.Double.valueOf(
                        response.getJSONObject("trip_info").optString("s_latitude")
                    )
                    srcLongitude = java.lang.Double.valueOf(
                        response.getJSONObject("trip_info").optString("s_longitude")
                    )
                    destLatitude = java.lang.Double.valueOf(
                        response.getJSONObject("trip_info").optString("d_latitude")
                    )
                    destLongitude = java.lang.Double.valueOf(
                        response.getJSONObject("trip_info").optString("d_longitude")
                    )
                }
                if (response.optString("payment_update") == "0") {
                    if (response.getJSONObject("trip_info").getJSONObject("payment")
                            .optString("payment_mode").equals("CORPORATE")
                    ) {
                        edit_toll_fare!!.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.icon_edit,
                            0
                        )
                        edit_extra_fare!!.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.icon_edit,
                            0
                        )
                        edit_base_fare!!.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.icon_edit,
                            0
                        )
                        btn_statusUpdate?.setText(resources.getString(R.string.cor))

                        imgSos!!.setOnClickListener(null)
                        iv_call_user!!.setOnClickListener(null)

                        Button_Status = "CONFIRM PAYMENT"
                    } else {
                        edit_toll_fare!!.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.icon_edit,
                            0
                        )
                        edit_extra_fare!!.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.icon_edit,
                            0
                        )
                        edit_base_fare!!.setCompoundDrawablesWithIntrinsicBounds(
                            0,
                            0,
                            R.drawable.icon_edit,
                            0
                        )
                        imgSos!!.setOnClickListener(null)
                        iv_call_user!!.setOnClickListener(null)

                        btn_statusUpdate?.setText(resources.getString(R.string.Confirmp))

                        Button_Status = "CONFIRM PAYMENT"
                    }
                } else {
                    if (response.getJSONObject("trip_info").getJSONObject("payment")
                            .optString("payment_mode").equals("CORPORATE")
                    ) {
                        edit_toll_fare!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        edit_extra_fare!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        edit_base_fare!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        edit_toll_fare!!.setOnClickListener(null)
                        edit_extra_fare!!.setOnClickListener(null)
                        edit_base_fare!!.setOnClickListener(null)
                        imgSos!!.setOnClickListener(null)
                        iv_call_user!!.setOnClickListener(null)
                        btn_statusUpdate?.setText("Corporate")

                        Button_Status = "COLLECT FARE"
                    } else {
                        edit_toll_fare!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        edit_extra_fare!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        edit_base_fare!!.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
                        edit_toll_fare!!.setOnClickListener(null)
                        edit_extra_fare!!.setOnClickListener(null)
                        edit_base_fare!!.setOnClickListener(null)
                        imgSos!!.setOnClickListener(null)
                        iv_call_user!!.setOnClickListener(null)
                        btn_statusUpdate?.setText(resources.getString(R.string.CollectFare))

                        Button_Status = "COLLECT FARE"
                    }
                }
                mMap!!.clear()
                // Invoice Details
                try {
                    if (response.getJSONObject("trip_info").optJSONObject("payment") != null) {
                        str_currency = response.getJSONObject("trip_info").getJSONObject("payment")
                            .optString("currency")
                        tv_baseFare!!.text =
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("currency") + " " + response.getJSONObject("trip_info")
                                .getJSONObject("payment").optString("base_fare")
                        tv_flat_fee!!.text =
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("currency") + " " + response.getJSONObject("trip_info")
                                .getJSONObject("payment").optString("flat_fare")
                        tv_distanceFare!!.text =
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("currency") + " " + response.getJSONObject("trip_info")
                                .getJSONObject("payment").optString("distance_fare")
                        tv_minFare!!.text =
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("currency") + " " + response.getJSONObject("trip_info")
                                .getJSONObject("payment").optString("min_fare")
                        tv_waitingFare!!.text =
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("currency") + " " + response.getJSONObject("trip_info")
                                .getJSONObject("payment").optString("waiting_fare")
                        tv_waitingStopFare!!.text =
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("currency") + " " + response.getJSONObject("trip_info")
                                .getJSONObject("payment").optString("stop_waiting_fare")
                        tv_taxFare!!.text =
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("currency") + " " + response.getJSONObject("trip_info")
                                .getJSONObject("payment").optString("vat")
                        tv_discountFare!!.text =
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("currency") + " " + response.getJSONObject("trip_info")
                                .getJSONObject("payment").optString("discount")
                        tv_toll!!.text =
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("currency") + " " + response.getJSONObject("trip_info")
                                .getJSONObject("payment").optString("toll")
                        tv_extra_fare!!.text =
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("currency") + " " + response.getJSONObject("trip_info")
                                .getJSONObject("payment").optString("extra_fare")
                        payment_mode_label!!.text =
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("payment_mode")
                        tv_totalFare!!.text =
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("currency") + " " + response.getJSONObject("trip_info")
                                .getJSONObject("payment").optString("total")
//                        tv_estimate_fare!!.text = response.getJSONObject("trip_info").getJSONObject("payment").optString("currency") + " " + response.getString("estimated_fare")
//                        tv_toll!!.text = response.getJSONObject("trip_info").getJSONObject("payment").optString("currency") + " " + response.getJSONObject("trip_info").getJSONObject("payment").getString("toll")
//                        tv_extra_fare!!.text = response.getJSONObject("trip_info").getJSONObject("payment").optString("currency") + " " + response.getJSONObject("trip_info").getJSONObject("payment").getString("extra_fare")
                        txt_earnings!!.text =
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("earnings")
                        putKey(
                            activity!!,
                            "driver_earnings",
                            response.getJSONObject("trip_info").getJSONObject("payment")
                                .optString("earnings")
                        )
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
                txt_earnings!!.text = getKey(activity!!, "driver_earnings")
                try {
//                    if (response.getJSONObject("trip_info").getString("guest") == "0")
//                    {
//                        if (response.optJSONObject("user") != null) {
//                            tv_userName!!.text = response.getJSONObject("user").optString("name")
//
//                            putKey(activity!!, "user_phone", response.getJSONObject("user").optString("mobile"))
//                            Picasso.with(context)
//                                .load(response.getJSONObject("user").optString("picture"))
//                                .placeholder(R.drawable.placeholder) // optional
//                                .error(R.drawable.placeholder) // optional
//                                .into(iv_userAvatar)
//                        }
//
//
//                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun started(response: JSONObject) {
        try {
            if (response != null) {
                isstart = false
                srcLatitude = response.getJSONObject("trip_info").optDouble("s_latitude")
                srcLongitude = response.getJSONObject("trip_info").optDouble("s_longitude")
                destLatitude = response.getJSONObject("trip_info").optDouble("d_latitude")
                destLongitude = response.getJSONObject("trip_info").optDouble("d_longitude")
                tv_pickup_location?.text =
                    response.getJSONObject("trip_info").optString("s_address")
                usernameTxt?.text = response.getJSONObject("user_info").optString("user_name")
                if (response.getJSONObject("user_info")
                        .getString("user_img") != URLHelper.base + "storage"
                ) {
                    Log.i("StartedTripDetails", response.toString())

                    if (activity!!.isDestroyed && activity!!.isFinishing) {

                    } else {
                        MainActivity.mainactivity?.let {
                            Glide.with(it)
                                .load(response.getJSONObject("user_info").optString("user_img"))
                                .apply(
                                    RequestOptions().placeholder(R.drawable.ic_driver)
                                        .dontAnimate().error(R.drawable.ic_driver)
                                        .apply(RequestOptions.circleCropTransform())
                                ).into(img_profile!!)
                        }!!

                    }
                } else {
                }
                if (countstarted <= 3) {
                    countstarted++
                    if (latLng!!.latitude != null) {
                        showPathFromCurrentToPickup(latLng!!.latitude, latLng!!.longitude)
                    }
                }

            }
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun Fun_alert(statusResponses: JSONObject) {
        try {
            putKey(activity!!, "a", "1")
            val builder = AlertDialog.Builder(activity!!, R.style.myDialog)
            val inflater =
                activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout = inflater.inflate(R.layout.notification_trip_alert, null)
            builder.setCancelable(false)
            builder.setView(layout)
            Trip_alert_Dialog = builder.create()
            txtDriverMessagepopup = layout.findViewById<View>(R.id.txtDriverMessage) as NTTextView
            txt01Pickup = layout.findViewById<View>(R.id.txtPickup) as NTTextView
            // txt01DropOff = (NTTextView) layout.findViewById(R.id.txtDropOff);
            txt01Timer = layout.findViewById<View>(R.id.txt01Timer) as MyDigitalFontTextView
            img01User = layout.findViewById<View>(R.id.img01User) as NTCircularImageView
            txt01UserName = layout.findViewById<View>(R.id.txt01UserName) as NTTextView
            //   txtSchedule = (NTTextView) layout.findViewById(R.id.txtSchedule);
            rat01UserRating = layout.findViewById<View>(R.id.rat01UserRating) as RatingBar
            btn_02_accept = layout.findViewById<View>(R.id.btn_02_accept) as ImageView
            btn_02_reject = layout.findViewById<View>(R.id.btn_02_reject) as ImageView
            res = activity!!.resources
            drawableCount = res!!.getDrawable(R.drawable.circular)
            mProgress = layout.findViewById<View>(R.id.circularProgressbar) as ProgressBar
            val drawable = rat01UserRating!!.progressDrawable as LayerDrawable
            drawable.getDrawable(0).setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP)
            drawable.getDrawable(1)
                .setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP)
            drawable.getDrawable(2)
                .setColorFilter(Color.parseColor("#FFAB00"), PorterDuff.Mode.SRC_ATOP)
            btn_02_accept!!.setOnClickListener {
                try {
                    Trip_alert_Dialog?.dismiss()
                    if (mPlayer != null && mPlayer!!.isPlaying) {
                        mPlayer!!.stop()
                        mPlayer = null
                    }
                    putKey(activity!!, "count", "")
                    // putKey(activity!!,"a","0")

                    updateAcceptStatus(getKey(activity!!, "trip_id"))

                    countDownTimer!!.cancel()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            btn_02_reject!!.setOnClickListener {
                countDownTimer!!.cancel()
                if (mPlayer != null && mPlayer!!.isPlaying) {
                    mPlayer!!.stop()
                    mPlayer = null
                }
                putKey(activity!!, "count", "")
                iscurr = false
                updateRejectStatus(getKey(activity!!, "trip_id"))
            }
            Trip_alert_Dialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
            Trip_alert_Dialog?.getWindow()!!.attributes.windowAnimations = R.style.dialog_animation
            Trip_alert_Dialog?.getWindow()!!
                .setBackgroundDrawableResource(android.R.color.transparent)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Trip_alert_Dialog?.getWindow()!!
                    .setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Trip_alert_Dialog?.getWindow()!!
                    .setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT)
            }
            Trip_alert_Dialog?.show()
            setValuesTo_ll_01_contentLayer_accept_or_reject_now(statusResponses)

            //Toast.makeText(context, "Trip", Toast.LENGTH_SHORT).show();
            token = getKey(activity!!, "access_token")
            tokentype = getKey(activity!!, "token_type")
        } catch (e: Exception) {
            Log.d("TAG", "Fun_alert:-------------- " + e.printStackTrace())
            e.printStackTrace()
        }
        //   }
    }

    private fun updateAcceptStatus(tripId: String?) {

        Utilities.PrintAPI_URL(URLHelper.ACCEPTED + tripId, "POST")
        //  if (loadingDialog != null) loadingDialog!!.showDialog()
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            URLHelper.ACCEPTED + tripId,
            null,
            Response.Listener { response ->
                try {
                    UpdateLocationToServer()
                    //  if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                    if (response.getString("success") == "1") {
                        //  isRunning=true
                        Utilities.PrintAPI_URL(URLHelper.ACCEPTED + tripId, response.toString())

                        PreviousStatus = "ACCEPTED"
                        try {
                            MainActivity.navController!!.navigate(R.id.fragmentHome, bundle)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        MainActivity.navController!!.navigate(R.id.fragmentHome, bundle)

                        if (response.getString("booking_by")
                                .equals("DISPATCHER") || response.getString("booking_by")
                                .equals("CORPORATE")
                            || response.getString("booking_by")
                                .equals("HOTEL") || response.getString("booking_by")
                                .equals("WEB") || response.getString("booking_by").equals("APP")
                        ) {
                            refresh()

                        } else {

                        }

                        putKey(activity!!, "user_notes", "")
                        // utils.showCustomAlert(activity!!, Utilities.ALERT_ERROR, resources.getString(R.string.app_name), response.getString("message"))

                    } else {
                        utils.showCustomAlert(
                            requireActivity(),
                            Utilities.ALERT_ERROR,
                            resources.getString(R.string.app_name),
                            response.getString("message")
                        )
                    }

                    Utilities.printAPI_Response(response.toString())

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                try {
                    // if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                    var json: String? = null
                    var Message: String
                    val response = error.networkResponse
                    if (response != null && response.data != null) {
                        try {
                            val errorObj = JSONObject(String(response.data))
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    utils.showCustomAlert(
                                        activity!!,
                                        Utilities.ALERT_ERROR,
                                        resources.getString(R.string.app_name),
                                        errorObj.optString("error")
                                    )
                                } catch (e: Exception) {
                                    utils.showCustomAlert(
                                        activity!!,
                                        Utilities.ALERT_ERROR,
                                        resources.getString(R.string.app_name),
                                        getString(R.string.something_went_wrong)
                                    )
                                }
                            } else if (response.statusCode == 401) {
                                //SharedHelper.putKey(activity!!, "loggedIn", getString(R.string.False));
                            } else if (response.statusCode == 422) {
                                json = NTApplication.trimMessage(String(response.data))
                                if (json !== "" && json != null) {
                                    utils.showCustomAlert(
                                        activity!!,
                                        Utilities.ALERT_ERROR,
                                        resources.getString(R.string.app_name),
                                        json
                                    )
                                } else {
                                    utils.showCustomAlert(
                                        activity!!,
                                        Utilities.ALERT_ERROR,
                                        resources.getString(R.string.app_name),
                                        getString(R.string.please_try_again)
                                    )
                                }
                            } else if (response.statusCode == 503) {
                                utils.showCustomAlert(
                                    activity!!,
                                    Utilities.ALERT_ERROR,
                                    resources.getString(R.string.app_name),
                                    getString(R.string.server_down)
                                )
                            }
                        } catch (e: Exception) {
                            utils.showCustomAlert(
                                activity!!,
                                Utilities.ALERT_ERROR,
                                resources.getString(R.string.app_name),
                                getString(R.string.something_went_wrong)
                            )
                        }
                    } else {
                        if (error is NoConnectionError) {
                            utils.showCustomAlert(
                                activity!!,
                                Utilities.ALERT_ERROR,
                                resources.getString(R.string.app_name),
                                getString(R.string.oops_connect_your_internet)
                            )
                        } else if (error is NetworkError) {
                            utils.showCustomAlert(
                                activity!!,
                                Utilities.ALERT_ERROR,
                                resources.getString(R.string.app_name),
                                getString(R.string.oops_connect_your_internet)
                            )
                        } else if (error is TimeoutError) {
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    makeText(
                        activity!!,
                        activity!!.getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] =
                    "" + getKey(activity!!, "token_type") + " " + getKey(activity!!, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    private fun updateRejectStatus(tripId: String?) {
        Utilities.PrintAPI_URL(URLHelper.REJECT + tripId, "POST")
        //  if (loadingDialog != null) loadingDialog!!.showDialog()
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET,
            URLHelper.REJECT + tripId,
            null,
            Response.Listener { response ->
                try {
                    if (response.getString("success") == "1") {
                        Trip_alert_Dialog!!.dismiss()
                        SharedHelper.putKey(activity!!, "a", "0")
                        UpdateLocationToServer()

                        putKey(activity!!, "user_notes", "")
                    } else {
                        Trip_alert_Dialog!!.dismiss()
                        UpdateLocationToServer()
                    }
                    Utilities.printAPI_Response(response.toString())

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                try {
                    UpdateLocationToServer()
                    Trip_alert_Dialog!!.dismiss()
                    var json: String? = null
                    var Message: String
                    val response = error.networkResponse
                    if (response != null && response.data != null) {
                        try {
                            val errorObj = JSONObject(String(response.data))
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    utils.showCustomAlert(
                                        activity!!,
                                        Utilities.ALERT_ERROR,
                                        resources.getString(R.string.app_name),
                                        errorObj.optString("error")
                                    )
                                } catch (e: Exception) {
                                    utils.showCustomAlert(
                                        activity!!,
                                        Utilities.ALERT_ERROR,
                                        resources.getString(R.string.app_name),
                                        getString(R.string.something_went_wrong)
                                    )
                                }
                            } else if (response.statusCode == 401) {
                                //  SharedHelper.putKey(activity!!, "loggedIn", getString(R.string.False));
                            } else if (response.statusCode == 422) {
                                json = NTApplication.trimMessage(String(response.data))
                                if (json !== "" && json != null) {
                                    utils.showCustomAlert(
                                        activity!!,
                                        Utilities.ALERT_ERROR,
                                        resources.getString(R.string.app_name),
                                        json
                                    )
                                } else {
                                    utils.showCustomAlert(
                                        activity!!,
                                        Utilities.ALERT_ERROR,
                                        resources.getString(R.string.app_name),
                                        getString(R.string.please_try_again)
                                    )
                                }
                            } else if (response.statusCode == 503) {
                                utils.showCustomAlert(
                                    activity!!,
                                    Utilities.ALERT_ERROR,
                                    resources.getString(R.string.app_name),
                                    getString(R.string.server_down)
                                )
                            }
                        } catch (e: Exception) {
                            utils.showCustomAlert(
                                activity!!,
                                Utilities.ALERT_ERROR,
                                resources.getString(R.string.app_name),
                                getString(R.string.something_went_wrong)
                            )
                        }
                    } else {
                        if (error is NoConnectionError) {
                            utils.showCustomAlert(
                                activity!!,
                                Utilities.ALERT_ERROR,
                                resources.getString(R.string.app_name),
                                getString(R.string.oops_connect_your_internet)
                            )
                        } else if (error is NetworkError) {
                            utils.showCustomAlert(
                                activity!!,
                                Utilities.ALERT_ERROR,
                                resources.getString(R.string.app_name),
                                getString(R.string.oops_connect_your_internet)
                            )
                        } else if (error is TimeoutError) {
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    makeText(
                        activity!!,
                        activity!!.getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] =
                    "" + getKey(activity!!, "token_type") + " " + getKey(activity!!, "access_token")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }


    private fun openAutoCompleteLocation(loc: String) {
        try {
            // Initialize Places.
            /**
             * Initialize Places. For simplicity, the API key is hard-coded. In a production
             * environment we recommend using a secure mechanism to manage API keys.
             */
            if (!Places.isInitialized()) {
                //  Places.initialize(activity!!, SharedHelper.getKey(requireContext(), "map_key")!!)
            }

            // Specify the fields to return.
            // Include address, ID, and name.
            placeFields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG)
            //        placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS, Place.Field.LAT_LNG);
// Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, placeFields as MutableList<Place.Field>
            ) //                .setTypeFilter(TypeFilter.ADDRESS)
                //                .setCountry("in")
                //                .setCountry("br")
                .build(activity!!)
            if (loc.equals("stop1", ignoreCase = true)) {
                startActivityForResult(intent, AUTOCOMPLETE_STOP1_REQUEST_CODE)
            } else if (loc.equals("stop2", ignoreCase = true)) {
                startActivityForResult(intent, AUTOCOMPLETE_STOP2_REQUEST_CODE)
            } else {
                startActivityForResult(intent, AUTOCOMPLETE_DEST_REQUEST_CODE)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            utils.showCustomAlert(
                activity!!,
                Utilities.ALERT_WARNING,
                resources.getString(R.string.app_name),
                resources.getString(R.string.something_went_wrong)
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(activity)) {
                    makeText(activity!!, "Permission Granted", Toast.LENGTH_LONG).show()
                } else {
                    makeText(activity!!, "Permission Not Granted.", Toast.LENGTH_LONG).show()
                }
            }
        } else if (requestCode == AUTOCOMPLETE_DEST_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data)
                tv_dest_address!!.text = place.address.toString()
                putKey(activity!!, "new_edit_address", place.address.toString())
                putKey(activity!!, "new_edit_latitude", place.latLng!!.latitude.toString() + "")
                putKey(activity!!, "new_edit_longitude", place.latLng!!.longitude.toString() + "")
                updatedestination(
                    getKey(activity!!, "trip_id"),
                    place.address.toString(),
                    place.latLng!!.latitude,
                    place.latLng!!.longitude,
                    "",
                    null,
                    null,
                    "",
                    null,
                    null,
                    CurrentStatus,
                    "destination"
                )


//                getAddressFromPlaceId(place.getId(), "destination");
//                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(data)
                Log.i("TAG", status.statusMessage!!)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else if (requestCode == AUTOCOMPLETE_STOP1_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data)
                tv_stop_one!!.text = place.address.toString()
                putKey(activity!!, "new_stop1_address", place.address.toString())
                putKey(activity!!, "new_stop1_latitude", place.latLng!!.latitude.toString() + "")
                putKey(activity!!, "new_stop1_longitude", place.latLng!!.longitude.toString() + "")
                updatedestination(
                    getKey(activity!!, "trip_id"),
                    "",
                    null,
                    null,
                    place.address.toString(),
                    place.latLng!!.latitude,
                    place.latLng!!.longitude,
                    "",
                    null,
                    null,
                    CurrentStatus,
                    "stop1"
                )


//                getAddressFromPlaceId(place.getId(), "destination");
//                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(data)
                Log.i("TAG", status.statusMessage!!)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        } else {
            if (resultCode == Activity.RESULT_OK) {
                Log.d("TAG", "onActivityResul------------s gt: ")
                val place = Autocomplete.getPlaceFromIntent(data)
                tv_stop_two!!.text = place.address.toString()
                putKey(activity!!, "new_stop2_address", place.address.toString())
                putKey(activity!!, "new_stop2_latitude", place.latLng!!.latitude.toString() + "")
                putKey(activity!!, "new_stop2_longitude", place.latLng!!.longitude.toString() + "")
                updatedestination(
                    getKey(activity!!, "trip_id"),
                    "",
                    null,
                    null,
                    "",
                    null,
                    null,
                    place.address.toString(),
                    place.latLng!!.latitude,
                    place.latLng!!.longitude,
                    CurrentStatus,
                    "stop2"
                )


//                getAddressFromPlaceId(place.getId(), "destination");
//                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId());
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                val status = Autocomplete.getStatusFromIntent(data)
                Log.i("TAG", status.statusMessage!!)
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    fun updatedestination(
        id: String?,
        address: String?,
        latitude: Double?,
        longitude: Double?,
        stop1_address: String?,
        stop1latitude: Double?,
        stop1longitude: Double?,
        stop2_address: String?,
        stop2latitude: Double?,
        stop2longitude: Double?,
        Currentstatus: String?,
        addresstype: String?
    ) {
        val `object` = JSONObject()
        try {
            `object`.put("id", id)
            // object.put("addresstype", addresstype);
            `object`.put("d_address", address)
            `object`.put("d_latitude", latitude)
            `object`.put("d_longitude", longitude)
            `object`.put("stop1_address", stop1_address)
            `object`.put("stop1_latitude", stop1latitude)
            `object`.put("stop1_longitude", stop1longitude)
            `object`.put("stop2_address", stop2_address)
            `object`.put("stop2_latitude", stop2latitude)
            `object`.put("stop2_longitude", stop2longitude)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Utilities.print(URLHelper.UPDATE_DEST, `object`.toString())
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            URLHelper.UPDATE_DEST,
            `object`,
            Response.Listener { response ->
                Utilities.printAPI_Response(response.toString())
                destLatitude = response.optString("d_latitude").toDouble()
                destLongitude = response.optString("d_longitude").toDouble()
                if (!response.optString("stop1_latitude").isEmpty()) {
                    stop1Latitude = response.optString("stop1_latitude").toDouble()
                    stop1Longitude = response.optString("stop1_longitude").toDouble()
                }
                if (!response.optString("stop2_latitude").isEmpty()) {
                    stop2Latitude = response.optString("stop2_latitude").toDouble()
                    stop2Longitude = response.optString("stop2_longitude").toDouble()
                }
                showPathFromPickupToDropUpdated(
                    destLatitude,
                    destLongitude,
                    stop1latitude,
                    stop1longitude,
                    stop2latitude,
                    stop2longitude
                )
                mMap!!.clear()
                Utilities.print("CancelRequestResponse", response.toString())
            },
            Response.ErrorListener { error ->
                var json: String? = null
                var Message: String
                val response = error.networkResponse
                if (response != null && response.data != null) {
                    try {
                        val errorObj = JSONObject(String(response.data))
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                utils.showAlert(activity!!, errorObj.optString("message"))
                            } catch (e: Exception) {
                                utils.showAlert(
                                    activity!!,
                                    activity!!.getString(R.string.something_went_wrong)
                                )
                                e.printStackTrace()
                            }
                        } else if (response.statusCode == 401) {
                            GoToBeginActivity()
                        } else if (response.statusCode == 422) {
                            json = NTApplication.trimMessage(String(response.data))
                            if (json !== "" && json != null) {
                                utils.showAlert(activity!!, json)
                            } else {
                                utils.showAlert(
                                    activity!!,
                                    activity!!.getString(R.string.please_try_again)
                                )
                            }
                        } else if (response.statusCode == 503) {
                            utils.showAlert(activity!!, activity!!.getString(R.string.server_down))
                        } else {
                            utils.showAlert(
                                activity!!,
                                activity!!.getString(R.string.please_try_again)
                            )
                        }
                    } catch (e: Exception) {
                        utils.showAlert(
                            activity!!,
                            activity!!.getString(R.string.something_went_wrong)
                        )
                        e.printStackTrace()
                    }
                } else {
                    utils.showAlert(activity!!, activity!!.getString(R.string.please_try_again))
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = getKey(activity!!, "lang")!!
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Content-Type"] = "application/json"
                headers["Authorization"] = "Bearer " + getKey(activity!!, "access_token")
                Log.e("", "Access_Token" + getKey(activity!!, "access_token"))
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }


    fun updatedestinationdistance(
        id: String?,
        address: String?,
        latitude: Double?,
        longitude: Double?,
        stop1_address: String?,
        stop1latitude: Double?,
        stop1longitude: Double?,
        stop2_address: String?,
        stop2latitude: Double?,
        stop2longitude: Double?,
        Currentstatus: String?,
        addresstype: String?
    ) {
        val `object` = JSONObject()
        try {
            `object`.put("id", id)
            // object.put("addresstype", addresstype);
            `object`.put("d_address", address)
            `object`.put("d_latitude", latitude)
            `object`.put("d_longitude", longitude)
            `object`.put("stop1_address", stop1_address)
            `object`.put("stop1_latitude", stop1latitude)
            `object`.put("stop1_longitude", stop1longitude)
            `object`.put("stop2_address", stop2_address)
            `object`.put("stop2_latitude", stop2latitude)
            `object`.put("stop2_longitude", stop2longitude)
            `object`.put("distance", getKey(activity!!, "distancevalue"))
            `object`.put("minutes", getKey(activity!!, "timevalue"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Utilities.print(URLHelper.UPDATE_DEST, `object`.toString())
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            URLHelper.UPDATE_DEST,
            `object`,
            Response.Listener { response ->
                Utilities.printAPI_Response(response.toString())
                putKey(activity!!, "distancevalue", "")
                putKey(activity!!, "timevalue", "")
                destLatitude = response.optString("d_latitude").toDouble()
                destLongitude = response.optString("d_longitude").toDouble()
                if (!response.optString("stop1_latitude").isEmpty()) {
                    stop1Latitude = response.optString("stop1_latitude").toDouble()
                    stop1Longitude = response.optString("stop1_longitude").toDouble()
                }
                if (!response.optString("stop2_latitude").isEmpty()) {
                    stop2Latitude = response.optString("stop2_latitude").toDouble()
                    stop2Longitude = response.optString("stop2_longitude").toDouble()
                }

                updatestatus = " "
                //showPathFromPickupToDropUpdated(destLatitude, destLongitude, stop1latitude, stop1longitude, stop2latitude, stop2longitude)
                mMap!!.clear()
                Utilities.print("CancelRequestResponse", response.toString())
            },
            Response.ErrorListener { error ->
                var json: String? = null
                var Message: String
                val response = error.networkResponse
                if (response != null && response.data != null) {
                    try {
                        val errorObj = JSONObject(String(response.data))
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                utils.showAlert(activity!!, errorObj.optString("message"))
                            } catch (e: Exception) {
                                utils.showAlert(
                                    activity!!,
                                    activity!!.getString(R.string.something_went_wrong)
                                )
                                e.printStackTrace()
                            }
                        } else if (response.statusCode == 401) {
                            GoToBeginActivity()
                        } else if (response.statusCode == 422) {
                            json = NTApplication.trimMessage(String(response.data))
                            if (json !== "" && json != null) {
                                utils.showAlert(activity!!, json)
                            } else {
                                utils.showAlert(
                                    activity!!,
                                    activity!!.getString(R.string.please_try_again)
                                )
                            }
                        } else if (response.statusCode == 503) {
                            utils.showAlert(activity!!, activity!!.getString(R.string.server_down))
                        } else {
                            utils.showAlert(
                                activity!!,
                                activity!!.getString(R.string.please_try_again)
                            )
                        }
                    } catch (e: Exception) {
                        utils.showAlert(
                            activity!!,
                            activity!!.getString(R.string.something_went_wrong)
                        )
                        e.printStackTrace()
                    }
                } else {
                    utils.showAlert(activity!!, activity!!.getString(R.string.please_try_again))
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = getKey(activity!!, "lang")!!
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Content-Type"] = "application/json"
                headers["Authorization"] = "Bearer " + getKey(activity!!, "access_token")
                Log.e("", "Access_Token" + getKey(activity!!, "access_token"))
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapFragment!!.onLowMemory()
    }

    override fun onResume() {
//        updateTripStatus()
        UpdateLocationToServer()
        iv_call_user!!.setOnClickListener(this)
        try {
            if (isRunning) {
                handler()
                isRunning = false
            }
            if (ContextCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(
                    context!!,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            ) {
                ActivityCompat.requestPermissions(
                    activity!!,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    121
                )
            }
            try {
                System.gc()
                Runtime.getRuntime().gc()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                Log.e("status", "" + getKey(requireActivity(), "signup_first"))
                if (getKey(requireActivity(), "signup_first_start").equals("start")) {
                    SharedHelper.putKey(context!!, "signup_first_start", "stop")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (PreviousStatus.equals("CANCELLED")) {
                //showlayoutsRefresh()
                if (activity!!.isDestroyed && activity!!.isFinishing) {
                    showlayoutsRefreshh()
                } else {
                    initMap()
                    showlayouts()
                    //   mMap!!.clear()
                    ll_onRoute_to_pickup?.visibility = View.GONE
                    ll_onTripView?.visibility = View.GONE
                    ll_driver_details_title?.visibility = View.GONE
                    putKey(activity!!, "user_name", "")
                    putKey(activity!!, "user_image", "")
                    putKey(activity!!, "ride_status", "")
                    countstarted = 0
                    countpicked = 0
                    droppedcount = 0
                    CurrentStatus = " "
                    val sgps = GPSTracker(activity!!)
                    current_latitude = sgps.latitude
                    current_longitude = sgps.longitude
                    onLocationChanged(current_latitude, current_longitude)
                    PreviousStatus = " "
                }
            } else if (PreviousStatus.equals("ACCEPTED")) {

                livestatus = "STARTED"
                CurrentStatus = "STARTED"

                Button_Status = "ARRIVED"
                updateTripDetailUI()
                //  btn_statusUpdate?.setHasActivationState(true)
                btn_statusUpdate?.setText(resources.getString(R.string.slide_to_arrive))
                ll_onTripView?.visibility = View.VISIBLE
                ll_onRoute_to_pickup?.visibility = View.VISIBLE
                imgSos?.visibility = View.GONE
                driver_status?.visibility = View.GONE
                iv_call_user?.visibility = View.VISIBLE
                llwaitingTime?.visibility = View.GONE

                disable()

                PreviousStatus = "NOTACCEPTED"
            }

            if (googleApiClient == null) {
                googleApiClient = GoogleApiClient.Builder(activity!!)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build()
                googleApiClient!!.connect()
            } else {
                googleApiClient!!.connect()
            }
            if (emer) {
                emer = false
                GetEmergencyContactList()
            }
            if (ContextCompat.checkSelfPermission(
                    activity!!,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                == PackageManager.PERMISSION_GRANTED && googleApiClient != null && googleApiClient!!.isConnected
            ) {

                mLocationRequest = LocationRequest()
                mLocationRequest!!.interval = 3000
                mLocationRequest!!.fastestInterval = 3000
                mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
//                  startLocationUpdates()

                LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient!!,
                    mLocationRequest!!,
                    this
                )
                mFusedLocationClient!!.requestLocationUpdates(
                    mLocationRequest!!,
                    mLocationCallback,
                    Looper.myLooper()
                )

            }
            if (Utilities.clearSound) {
                val notificationManager =
                    activity!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancelAll()
            }
            try {
                Utilities.printAPI_Response("Notfication Map Activity" + Utilities.r)
                if (Utilities.r != null) {
                    Utilities.r.stop()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            LocalBroadcastManager.getInstance(activity!!).registerReceiver(
                myReceiver!!,
                IntentFilter(LocationUpdatesService.ACTION_BROADCAST)
            )
            googleApiClient!!.connect()
            setMapStyle(mMap)
            setmapType(mMap)
            mapFragment!!.onResume()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        super.onResume()
    }

    override fun onStart() {
        super.onStart()
        activity!!.bindService(
            Intent(activity, LocationUpdatesService::class.java), mServiceConnection,
            Context.BIND_AUTO_CREATE
        )

    }

    override fun onPause() {
        LocalBroadcastManager.getInstance(activity!!).unregisterReceiver(myReceiver!!)
        // LocationServices.getFusedLocationProviderClient(activity!!).removeLocationUpdates(LocationCallback())

        getKey(activity!!, "driverOnlineFlag") == "true"
        if (isRunning) {
            handleCheckStatus!!.removeCallbacksAndMessages(null)
            //   handleCheckStatus.removeCallbacksAndMessages(null);
            isRunning = true
        } else {
        }

        // handlePickupStatus.removeCallbacksAndMessages(null);
        super.onPause()
    }

    override fun onStop() {
        if (isRunning) {
            handleCheckStatus!!.removeCallbacksAndMessages(null)
            //    handleCheckStatus.removeCallbacksAndMessages(null);
            isRunning = true
        } else {
        }
        SharedHelper.putKey(context!!, "signup_first_start", "stop")

        if (mBound) {
            SharedHelper.putKey(activity!!, "showforegroundLocation", getString(R.string.True))
            // Unbind from the service. This signals to the service that this activity is no longer
            // in the foreground, and the service can respond by promoting itself to a foreground
            // service.
            activity!!.unbindService(mServiceConnection)
            mBound = false
        }
        // handlePickupStatus.removeCallbacksAndMessages(null);
        //   handlePickupStatus.removeCallbacksAndMessages(null);
        super.onStop()
        //   handleCheckStatus.removeCallbacksAndMessages(null);
    }

    override fun onConnected(bundle: Bundle?) {
//startLocationUpdates();
        mLocationRequest = LocationRequest()
        mLocationRequest!!.interval = 5000
        mLocationRequest!!.fastestInterval = 5000
        mLocationRequest!!.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        if ((ContextCompat.checkSelfPermission(
                activity!!,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
                    == PackageManager.PERMISSION_GRANTED) && googleApiClient != null && googleApiClient!!.isConnected
        ) {
            LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient!!,
                mLocationRequest!!,
                this
            )
            mFusedLocationClient!!.requestLocationUpdates(
                mLocationRequest!!,
                mLocationCallback,
                Looper.myLooper()
            )
        }
    }

    var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.size > 0) {
                //The last location in the list is the newest
                val location = locationList[locationList.size - 1]
                current_location = location
                if (locationResult.lastLocation != null) {
                    lastlocation = locationResult.lastLocation
                    val var3 = lastlocation?.getLatitude()
                    latLng = lastlocation?.getLatitude()
                        ?.let { LatLng(it, lastlocation?.getLongitude()!!) }
                    Log.e("Location", latLng!!.latitude.toString() + " , " + latLng!!.longitude)

                    /*
                    if (SharedHelper.getKey(activity,"driverOnlineFlag").equals("true")) {
                        firebaseHelper= new FirebaseHelper(SharedHelper.getKey(activity, "id"));
                        firebaseHelper.updateDriver(new Driver(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude),  SharedHelper.getKey(activity, "id"),String.valueOf(lastlocation.getBearing())));
                    }*/try {
                        Log.e(
                            "ELocation",
                            latLng!!.latitude.toString() + " , " + latLng!!.longitude
                        )
                        Log.e("EShared", getKey(activity!!, "driverOnlineFlag").toString())
                        Log.e("ESharedride", getKey(activity!!, "driverRideFlag").toString())
                        if (getKey(activity!!, "driverOnlineFlag") == "true" || getKey(
                                activity!!,
                                "driverOnlineFlag"
                            )!!.isEmpty()
                        ) {
                            firebaseHelper = FirebaseHelper(getKey(activity!!, "id")!!)
                            firebaseHelper!!.updateDriver(
                                Driver(
                                    latLng!!.latitude.toString(),
                                    latLng!!.longitude.toString(),
                                    getKey(activity!!, "id")!!,
                                    lastlocation?.getBearing().toString()
                                )
                            )
                            Log.e("CurrentStatus", CurrentStatus)
                        }
                        if (getKey(activity!!, "driverRideFlag") == "true") {
                            firebaseHelperRide = FirebaseHelperRide(getKey(activity!!, "id")!!)
                            firebaseHelperRide!!.updateDriver(
                                Driver(
                                    latLng!!.latitude.toString(),
                                    latLng!!.longitude.toString(),
                                    getKey(activity!!, "id")!!,
                                    lastlocation?.getBearing().toString()
                                )
                            )
                            sourceLatLng = LatLng(latLng!!.latitude, latLng!!.longitude)
                            // Log.e("CurrentStatus",CurrentStatus);
                            if (livestatus == "PICKEDUP") {
                                if (latLng != null) {
                                    if (zooomStatus.equals("one")) {
                                        zooomStatus = ""
                                        val cameraPosition = CameraPosition.Builder()
                                            .target(LatLng(latLng!!.latitude, latLng!!.longitude))
                                            .zoom(15f)
                                            .build()
                                        mMap!!.animateCamera(
                                            CameraUpdateFactory.newCameraPosition(
                                                cameraPosition
                                            )
                                        )
                                    }
                                }

                                fetchdistanceandTimePickedup(latLng!!.latitude, latLng!!.longitude)
                                try {
                                    firebaseHelperRide!!.updateDriver(
                                        Driver(
                                            latLng!!.latitude.toString(),
                                            latLng!!.longitude.toString(),
                                            getKey(activity!!, "id")!!,
                                            lastlocation?.getBearing().toString()
                                        )
                                    )
                                } catch (e: NullPointerException) {
                                    e.printStackTrace()
                                    //  firebaseHelperRide.updateDriver(new Driver(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude),  SharedHelper.getKey(context, "id"),String.valueOf(lastlocation.getBearing()),"0 Min"));
                                }


                            } else if (livestatus == "STARTED") {
                                try {
                                    firebaseHelperRide!!.updateDriver(
                                        Driver(
                                            latLng!!.latitude.toString(),
                                            latLng!!.longitude.toString(),
                                            getKey(activity!!, "id")!!,
                                            lastlocation?.getBearing().toString()
                                        )
                                    )
                                } catch (e: NullPointerException) {
                                    e.printStackTrace()
                                    //  firebaseHelperRide.updateDriver(new Driver(String.valueOf(latLng.latitude), String.valueOf(latLng.longitude),  SharedHelper.getKey(context, "id"),String.valueOf(lastlocation.getBearing()),"0 Min"));
                                }
                                fetchdistanceandTimeStarted(latLng!!.latitude, latLng!!.longitude)
                            } else {
                                MapAnimator.getInstance().stopAnim()
                                mMap!!.clear()
                            }
                        } else {
                            if (livestatus == "RATE" || livestatus == "COMPLETED") {
                                mMap!!.clear()
                                if (mMap != null) {
                                    MapAnimator.getInstance().stopAnim()
                                }
                            }
                            if (livestatus == "ARRIVED") {
                                try {
                                    firebaseHelper!!.updateDriver(
                                        Driver(
                                            latLng!!.latitude.toString(),
                                            latLng!!.longitude.toString(),
                                            getKey(activity!!, "id")!!,
                                            lastlocation?.getBearing().toString()
                                        )
                                    )
                                } catch (e: NullPointerException) {
                                    e.printStackTrace()
                                }
                            }
                            if (getKey(activity!!, "driverOnlineFlag") == "true") {
                                firebaseHelper = FirebaseHelper(getKey(activity!!, "id")!!)
                                firebaseHelper!!.updateDriver(
                                    Driver(
                                        latLng!!.latitude.toString(),
                                        latLng!!.longitude.toString(),
                                        getKey(activity!!, "id")!!,
                                        lastlocation?.getBearing().toString()
                                    )
                                )
                            } else {
                                if (livestatus == "ONLINE") {
//
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    //  showOrAnimateMarker(latLng!!)
                }
                //speed in km/h
                //speed = (int) ((location.getSpeed() * 3600) / 1000);
            }
        }
    }

    override fun onConnectionSuspended(i: Int) {}
    override fun onConnectionFailed(connectionResult: ConnectionResult) {}
    override fun onCameraMove() {
        if (currentMarker != null) {
            if (!mMap!!.projection.visibleRegion.latLngBounds.contains(currentMarker!!.position)) {
                Utilities.print("Current marker", "Current Marker is not visible")
                if (mapfocus?.visibility == View.GONE) {
                    mapfocus?.visibility = View.VISIBLE
                }
            } else {
                Utilities.print("Current marker", "Current Marker is visible")
                if (mapfocus?.visibility == View.VISIBLE) {
                    mapfocus?.visibility = View.GONE
                }
                if (mMap!!.cameraPosition.zoom < 16.0f) {
                    if (mapfocus?.visibility == View.GONE) {
                        mapfocus?.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onMarkerDragStart(marker: Marker) {}
    override fun onMarkerDrag(marker: Marker) {}
    override fun onMarkerDragEnd(marker: Marker) {}
    private fun mapfocus() {
        mMap!!.clear()
        val sgps = GPSTracker(requireActivity())
        current_latitude = sgps.latitude
        current_longitude = sgps.longitude
        Log.e("findlocation", current_latitude.toString() + " , " + current_longitude.toString())
        val crtLat: Double
        val crtLng: Double
        val current_lat = current_latitude.toString()
        val current_lng = current_longitude.toString()
//        databaseReference!!.addChildEventListener(valueEventListener!!)
        if (!current_lat.equals("", ignoreCase = true) && !current_lng.equals(
                "",
                ignoreCase = true
            )
        ) {
            crtLat = current_lat.toDouble()
            crtLng = current_lng.toDouble()
            val h = resources.getDimensionPixelSize(R.dimen._30sdp)
            val w = resources.getDimensionPixelSize(R.dimen._30sdp)
            val b = resources.getDrawable(R.drawable.marker) as BitmapDrawable
            val bb = b.bitmap
            val s = Bitmap.createScaledBitmap(bb, w, h, false)

            // Bitmap s = Bitmap.createScaledBitmap(bb, w, h, false);
            val currentMarkerOptionss = MarkerOptions()
            currentMarkerOptionss.anchor(0.5f, 0.75f)
                .position(LatLng(current_latitude, current_longitude))
                .draggable(true) //.rotation(getBearing(sourceLatLng,destLatLng))
                .icon(BitmapDescriptorFactory.fromBitmap(s))
            mMap!!.clear()
//            mMap!!.addMarker(currentMarkerOptionss)
            if (crtLat != null && crtLng != null) {
                val loc = LatLng(crtLat, crtLng)
                val cameraPosition = CameraPosition.Builder()
                    .target(LatLng(crtLat, crtLng)) // Sets the center of the map to location user
                    .zoom(17f) // Sets the zoom
                    .bearing(90f) // Sets the orientation of the camera to east
                    // .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                    .build() // Creates a CameraPosition from the builder
                mMap!!.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
                // mapfocus.setVisibility(View.VISIBLE);
            }
        }
    }

    fun onLocationChanged(current_latitude: Double, current_longitude: Double) {
        if (mMap != null) {
            mMap!!.clear()
            if (currentMarker != null) {
                currentMarker!!.remove()
            }
            val markerOptions1 = MarkerOptions().anchor(0.5f, 0.5f)
                .position(LatLng(current_latitude, current_longitude)).draggable(true)
                .rotation(lastlocation!!.bearing)
                .icon(
                    BitmapDescriptorFactory.fromBitmap(
                        getBitmapFromVectorDrawable(
                            activity,
                            R.drawable.provider_marker_icon
                        )
                    )
                )
            currentMarker = mMap!!.addMarker(markerOptions1)
            val myLocation = LatLng(current_latitude, current_longitude)
            val cameraPosition =
                CameraPosition.Builder().target(myLocation).zoom(mMap!!.cameraPosition.zoom).build()
            //   CameraPosition cameraPosition = new CameraPosition.Builder().target(myLocation).zoom(zoomValue).build();
            val location = Location("")
            location.latitude = current_latitude
            location.longitude = current_longitude
            moveVechile(currentMarker, location)
            rotateMarker(currentMarker, location.bearing, start_rotation)

            if (source_lat.equals("", ignoreCase = true) || source_lat.length < 0) {
                source_lat = current_latitude.toString()
            }
            if (source_lng.equals("", ignoreCase = true) || source_lng.length < 0) {
                source_lng = current_longitude.toString()
            }
            currentLatLng = LatLng(current_latitude, current_longitude)
        }
    }

    fun moveVechile(myMarker: Marker?, finalPosition: Location) {
        val startPosition = myMarker!!.position
        val handler = Handler()
        val start = SystemClock.uptimeMillis()
        val interpolator: Interpolator = AccelerateDecelerateInterpolator()
        val durationInMs = 5000f
        val hideMarker = false
        handler.post(object : Runnable {
            var elapsed: Long = 0
            var t = 0f
            var v = 0f
            override fun run() {
                elapsed = SystemClock.uptimeMillis() - start
                t = elapsed / durationInMs
                v = interpolator.getInterpolation(t)
                val currentPosition = LatLng(
                    startPosition.latitude * (1 - t) + finalPosition.latitude * t,
                    startPosition.longitude * (1 - t) + finalPosition.longitude * t
                )
                myMarker.position = currentPosition
                myMarker.rotation = finalPosition.bearing

                if (t < 1) {
                    handler.postDelayed(this, 50)
                } else {
                    myMarker.isVisible = !hideMarker
                }
            }
        })
    }

    fun rotateMarker(marker: Marker?, toRotation: Float, st: Float) {
        val handler = Handler()
        val start = SystemClock.uptimeMillis()
        val duration: Long = 1555
        val interpolator: Interpolator = LinearInterpolator()
        handler.post(object : Runnable {
            override fun run() {
                val elapsed = SystemClock.uptimeMillis() - start
                val t = interpolator.getInterpolation(elapsed.toFloat() / duration)
                val rot = t * toRotation + (1 - t) * st
                marker!!.rotation = if (-rot > 180) rot / 2 else rot
                start_rotation = if (-rot > 180) rot / 2 else rot
                if (t < 1.0) {
                    // Post again 16ms later.
//                    handler.postDelayed(this, 100)
                }
            }
        })
    }

    private fun bitmapDescriptorFromVector(
        context: Context,
        @DrawableRes vectorResId: Int
    ): BitmapDescriptor {
        val vectorDrawable = ContextCompat.getDrawable(context, vectorResId)
        vectorDrawable!!.setBounds(
            0,
            0,
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight
        )
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }

    private fun fetchdistanceandTimeStarted(current_latitude: Double, current_longitude: Double) {
        //  mMap!!.clear()
        sourceLatLng = LatLng(current_latitude, current_longitude)
        destLatLng = LatLng(srcLatitude, srcLongitude)
        val location1 = Location("")
        location1.latitude = current_latitude
        location1.longitude = current_longitude
        val location2 = Location("")
        location2.latitude = srcLatitude
        location2.longitude = srcLongitude

        getDistanceDuration(
            location1.latitude,
            location1.longitude,
            location2.latitude,
            location2.longitude
        )

//        val distance = (location2.distanceTo(location1) / 1000).toDouble()
//        val estimate = distance / 0.5
//        val total = estimate.toInt()
//        timetaken = if (total < 60) {
//            "$total Mins "
//            timetaken = "$total Mins "
//            time_to_pickup!!.setText(timetaken).toString()
//
//        } else {
//            val min = Integer.toString(total % 60)
//            (total / 60).toString() + " Hr" + min + " Mins"
//            timetaken = "$total Mins "
//            time_to_pickup!!.setText(timetaken).toString()
//        }

    }


    private fun showPathFromCurrentToPickup(current_latitude: Double, current_longitude: Double) {
        mMap!!.clear()
        sourceLatLng = LatLng(current_latitude, current_longitude)
        destLatLng = LatLng(srcLatitude, srcLongitude)
        val url = getUrlFromCurrentToPickup(
            sourceLatLng!!.latitude,
            sourceLatLng!!.longitude,
            destLatLng!!.latitude,
            destLatLng!!.longitude
        )
        val fetchUrl = FetchUrl()
        fetchUrl.execute(url)

    }

    private fun fetchdistanceandTimePickedup(srclat: Double, srcLng: Double) {
        //  sourceLatLng = currentLatLng;
        sourceLatLng = LatLng(srclat, srcLng)
        destLatLng = LatLng(destLatitude, destLongitude)
        stop1LatLng = LatLng(stop1Latitude, stop1Longitude)
        stop2LatLng = LatLng(stop2Latitude, stop2Longitude)

        if (sourceLatLng != null && destLatLng != null && stop1LatLng!!.latitude != 0.0 && stop2LatLng!!.latitude != 0.0) {
            Utilities.print("LatLng", "Source:$sourceLatLng Destination: $destLatLng")

            val locationdist1 = Location("")
            locationdist1.latitude = srcLatitude
            locationdist1.longitude = srcLongitude
            val locationdist2 = Location("")
            locationdist2.latitude = srclat
            locationdist2.longitude = srcLng
            val stop1locationdist = Location("")
            stop1locationdist.latitude = stop1Latitude
            stop1locationdist.longitude = stop1Longitude
            val stop2locationdist = Location("")
            stop2locationdist.latitude = stop2Latitude
            stop2locationdist.longitude = stop2Longitude
            // distancecalc = ((stop1locationdist.distanceTo(locationdist1)+stop2locationdist.distanceTo(stop1locationdist) + (locationdist2.distanceTo(stop2locationdist))) / 1000).toString()
            distancecalc = ((locationdist2.distanceTo(locationdist1) / 1000).toString())
            Log.e("distance2", distancecalc.toString())


            val location1 = Location("")
            location1.latitude = srclat
            location1.longitude = srcLng
            val location2 = Location("")
            location2.latitude = destLatitude
            location2.longitude = destLongitude
            val stop1location = Location("")
            stop1location.latitude = stop1Latitude
            stop1location.longitude = stop1Longitude
            val stop2location = Location("")
            stop2location.latitude = stop2Latitude
            stop2location.longitude = stop2Longitude

            getDistanceDuration(
                location1.latitude,
                location1.longitude,
                location2.latitude,
                location2.longitude
            )

//            val distance = ((stop1location.distanceTo(location1) + stop2location.distanceTo(stop1location) + (location2.distanceTo(stop2location))) / 1000).toDouble()
//            Log.e("distance", distance.toString())
//            val estimate = distance / 0.5
//            val total = estimate.toInt()
//            if (total < 60) {
//                timetaken = "$total Mins "
//                time_to_destination!!.text = timetaken
//            } else {
//                val min = Integer.toString(total % 60)
//                timetaken = (total / 60).toString() + " Hr" + min + " Mins"
//                time_to_destination!!.text = timetaken
//            }
        } else if (sourceLatLng != null && destLatLng != null && stop1LatLng!!.latitude != 0.0) {

            val locationdist1 = Location("")
            locationdist1.latitude = srcLatitude
            locationdist1.longitude = srcLongitude
            val locationdist2 = Location("")
            locationdist2.latitude = srclat
            locationdist2.longitude = srcLng
            val stop1locationdist = Location("")
            stop1locationdist.latitude = stop1Latitude
            stop1locationdist.longitude = stop1Longitude

            distancecalc = ((locationdist2.distanceTo(locationdist1) / 1000).toString())
            //   distancecalc =((stop1locationdist.distanceTo(locationdist1) + (locationdist2.distanceTo(stop1locationdist))) / 1000).toString()
            Log.e(
                "distancestop1",
                ((stop1locationdist.distanceTo(locationdist1)) / 1000).toString()
            )
            Log.e(
                "distancestopdest",
                ((locationdist2.distanceTo(stop1locationdist)) / 1000).toString()
            )
            Log.e("distance2", distancecalc.toString())

            val location1 = Location("")
            location1.latitude = srclat
            location1.longitude = srcLng
            val location2 = Location("")
            location2.latitude = destLatitude
            location2.longitude = destLongitude
            val stop1location = Location("")
            stop1location.latitude = stop1Latitude
            stop1location.longitude = stop1Longitude

            getDistanceDuration(
                location1.latitude,
                location1.longitude,
                location2.latitude,
                location2.longitude
            )

//            val distance = ((stop1location.distanceTo(location1) + (location2.distanceTo(stop1location))) / 1000).toDouble()
//            val estimate = distance / 0.5
//            val total = estimate.toInt()
//            if (total < 60) {
//                Log.e("time", total.toString())
//                timetaken = "$total Mins "
//                time_to_destination!!.text = timetaken
//            } else {
//                val min = Integer.toString(total % 60)
//                timetaken = (total / 60).toString() + " Hr" + min + " Mins"
//                time_to_destination!!.text = timetaken
//
//            }
        } else if (sourceLatLng != null && destLatLng != null) {
            Utilities.print("LatLng", "Source:$sourceLatLng Destination: $destLatLng")

            val locationdist1 = Location("")
            locationdist1.latitude = srcLatitude
            locationdist1.longitude = srcLongitude
            val locationdist2 = Location("")
            locationdist2.latitude = srclat
            locationdist2.longitude = srcLng


            distancecalc = ((locationdist2.distanceTo(locationdist1) / 1000).toString())
            Log.e("distance2", distancecalc.toString())


            val location1 = Location("")
            location1.latitude = srclat
            location1.longitude = srcLng
            val location2 = Location("")
            location2.latitude = destLatitude
            location2.longitude = destLongitude

            getDistanceDuration(
                location1.latitude,
                location1.longitude,
                location2.latitude,
                location2.longitude
            )

//            val distance = (location2.distanceTo(location1) / 1000).toDouble()
//            val estimate = distance / 0.5
//            val total = estimate.toInt()
//            if (total < 60) {
//                timetaken = "$total Mins "
//                time_to_destination!!.text = timetaken
//            } else {
//                val min = Integer.toString(total % 60)
//                timetaken = (total / 60).toString() + " Hr" + min + " Mins"
//                time_to_destination!!.text = timetaken
//            }
        }
    }

    fun getDistanceDuration(
        srcLocLat: Double,
        srcLocLng: Double,
        destLatitude: Double,
        destLongitude: Double
    ) {
        val URl =
            "https://maps.googleapis.com/maps/api/distancematrix/json?destinations=$srcLocLat,$srcLocLng&origins=$destLatitude,$destLongitude&mode=car&units=metric&key=" + resources.getString(
                R.string.google_map_api
            )
        val `object` = JSONObject()
        val jsonObjectRequest: JsonObjectRequest =
            object : JsonObjectRequest(Method.GET, URl, `object`, Response.Listener { response ->
                Log.v("getDistanceDuration", response.toString())
                try {
                    val durationArray = response.getJSONArray("rows")
                    val elementsArray = durationArray.getJSONObject(0).getJSONArray("elements")
                    val jsonObject = elementsArray.getJSONObject(0)
                    val durationObject = jsonObject.getJSONObject("duration")
                    val durationText = durationObject.getString("text")
                    Log.v("getDistanceDurationTime", durationText)

                    time_to_destination!!.text = durationText

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error -> //hideLoadingDialog();
                var json: String? = null
                var Message: String
                val response = error.networkResponse
                if (response != null && response.data != null) {
                    try {
                        search_place_recyclerView?.visibility = View.GONE
                        val errorObj = JSONObject(String(response.data))
                        println(
                            "PassengerAPI response error : " + error + " " + error.networkResponse + " " + response.statusCode + " " + errorObj.optString(
                                "error"
                            )
                        )
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"))
                            } catch (e: java.lang.Exception) {
                                displayMessage(getString(R.string.something_went_wrong))
                            }
                        } else if (response.statusCode == 401) {
                        } else if (response.statusCode == 422) {
                            json = trimMessage(String(response.data))
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
                    } catch (e: java.lang.Exception) {
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
            }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    //                headers.put("X-localization", SharedHelper.getKey(context, "lang"));
//                headers.put("X-Requested-With", "XMLHttpRequest");
//                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " "
//                        + SharedHelper.getKey(context, "access_token"));
//                utils.print("authoization", "" + SharedHelper.getKey(context, "token_type") + " "
//                        + SharedHelper.getKey(context, "access_token"));
                    Utilities.print("authoization", headers.toString())
                    return headers
                }
            }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    private fun showPathFromPickupToDrop(srclat: Double, srcLng: Double) {

//        sourceLatLng = currentLatLng;
        sourceLatLng = LatLng(srclat, srcLng)
        destLatLng = LatLng(destLatitude, destLongitude)
        stop1LatLng = LatLng(stop1Latitude, stop1Longitude)
        stop2LatLng = LatLng(stop2Latitude, stop2Longitude)

        if (sourceLatLng != null && destLatLng != null && stop1LatLng!!.latitude != 0.0 && stop2LatLng!!.latitude != 0.0) {
            Utilities.print("LatLng", "Source:$sourceLatLng Destination: $destLatLng")
            val url = getUrl(
                sourceLatLng!!.latitude,
                sourceLatLng!!.longitude,
                destLatLng!!.latitude,
                destLatLng!!.longitude,
                stop1LatLng!!.latitude,
                stop1LatLng!!.longitude,
                stop2LatLng!!.latitude,
                stop2LatLng!!.longitude
            )
            val fetchUrl = FetchUrl()
            fetchUrl.execute(url)

        } else if (sourceLatLng != null && destLatLng != null && stop1LatLng!!.latitude != 0.0) {
            val url = getUrl(
                sourceLatLng!!.latitude,
                sourceLatLng!!.longitude,
                destLatLng!!.latitude,
                destLatLng!!.longitude,
                stop1LatLng!!.latitude,
                stop1LatLng!!.longitude,
                0.0,
                0.0
            )
            val fetchUrl = FetchUrl()
            fetchUrl.execute(url)

        } else if (sourceLatLng != null && destLatLng != null) {
            Utilities.print("LatLng", "Source:$sourceLatLng Destination: $destLatLng")
            val url = getUrl(
                sourceLatLng!!.latitude,
                sourceLatLng!!.longitude,
                destLatLng!!.latitude,
                destLatLng!!.longitude,
                0.0,
                0.0,
                0.0,
                0.0
            )
            val fetchUrl = FetchUrl()
            fetchUrl.execute(url)

        }
    }

    private fun showPathFromPickupToDropUpdated(
        destlat: Double,
        destlng: Double,
        stop1latitude: Double?,
        stop1longitude: Double?,
        stop2latitude: Double?,
        stop2longitude: Double?
    ) {

//        sourceLatLng = currentLatLng;
        sourceLatLng = LatLng(srcLatitude, srcLongitude)
        destLatLng = LatLng(destlat, destlng)
        stop1LatLng = LatLng(stop1Latitude, stop1Longitude)
        stop2LatLng = LatLng(stop2Latitude, stop2Longitude)
        /*     stop1LatLng = LatLng(stop1latitude!!, stop1longitude!!)
             stop2LatLng = LatLng(stop2latitude!!, stop2longitude!!)*/

        if (sourceLatLng != null && destLatLng != null && stop1LatLng!!.latitude != 0.0 && stop2LatLng!!.latitude != 0.0) {
            Utilities.print("LatLng", "Source:$sourceLatLng Destination: $destLatLng")
            val url = getUrl(
                sourceLatLng!!.latitude,
                sourceLatLng!!.longitude,
                destLatLng!!.latitude,
                destLatLng!!.longitude,
                stop1LatLng!!.latitude,
                stop1LatLng!!.longitude,
                stop2LatLng!!.latitude,
                stop2LatLng!!.longitude
            )
            val fetchUrl = FetchUrl()
            fetchUrl.execute(url)

            val location1 = Location("")
            location1.latitude = srcLatitude
            location1.longitude = srcLongitude
            val location2 = Location("")
            location2.latitude = destLatitude
            location2.longitude = destLongitude
            val stop1location = Location("")
            stop1location.latitude = stop1Latitude
            stop1location.longitude = stop1Longitude
            val stop2location = Location("")
            stop2location.latitude = stop2Latitude
            stop2location.longitude = stop2Longitude

            getDistanceDuration(
                location1.latitude,
                location1.longitude,
                location2.latitude,
                location2.longitude
            )

//            val distance = ((stop1location.distanceTo(location1) + stop2location.distanceTo(stop1location) + (location2.distanceTo(stop2location))) / 1000).toDouble()
//            val estimate = distance / 0.5
//            val total = estimate.toInt()
//            if (total < 60) {
//                Log.e("time", total.toString())
//                timetaken = "$total Mins "
//                time_to_destination!!.text = timetaken
//            } else {
//                val min = Integer.toString(total % 60)
//                timetaken = (total / 60).toString() + " Hr" + min + " Mins"
//                time_to_destination!!.text = timetaken
//            }
        } else if (sourceLatLng != null && destLatLng != null && stop1LatLng!!.latitude != 0.0) {
            val url = getUrl(
                sourceLatLng!!.latitude,
                sourceLatLng!!.longitude,
                destLatLng!!.latitude,
                destLatLng!!.longitude,
                stop1LatLng!!.latitude,
                stop1LatLng!!.longitude,
                0.0,
                0.0
            )
            val fetchUrl = FetchUrl()
            fetchUrl.execute(url)

            val location1 = Location("")
            location1.latitude = srcLatitude
            location1.longitude = srcLongitude
            val location2 = Location("")
            location2.latitude = destLatitude
            location2.longitude = destLongitude
            val stop1location = Location("")
            stop1location.latitude = stop1Latitude
            stop1location.longitude = stop1Longitude

            getDistanceDuration(
                location1.latitude,
                location1.longitude,
                location2.latitude,
                location2.longitude
            )

//            val distance = ((stop1location.distanceTo(location1) + (location2.distanceTo(stop1location))) / 1000).toDouble()
//            val estimate = distance / 0.5
//            val total = estimate.toInt()
//            if (total < 60) {
//                Log.e("time", total.toString())
//                timetaken = "$total Mins "
//                time_to_destination!!.text = timetaken
//            } else {
//                val min = Integer.toString(total % 60)
//                timetaken = (total / 60).toString() + " Hr" + min + " Mins"
//                time_to_destination!!.text = timetaken
//            }
        } else if (sourceLatLng != null && destLatLng != null) {
            Utilities.print("LatLng", "Source:$sourceLatLng Destination: $destLatLng")
            val url = getUrl(
                sourceLatLng!!.latitude,
                sourceLatLng!!.longitude,
                destLatLng!!.latitude,
                destLatLng!!.longitude,
                0.0,
                0.0,
                0.0,
                0.0
            )
            val fetchUrl = FetchUrl()
            fetchUrl.execute(url)
            val location1 = Location("")
            location1.latitude = srcLatitude
            location1.longitude = srcLongitude
            val location2 = Location("")
            location2.latitude = destLatitude
            location2.longitude = destLongitude
            getDistanceDuration(
                location1.latitude,
                location1.longitude,
                location2.latitude,
                location2.longitude
            )

//            val distance = (location2.distanceTo(location1) / 1000).toDouble()
//            val estimate = distance / 0.5
//            val total = estimate.toInt()
//            if (total < 60) {
//                Log.e("time", total.toString())
//                timetaken = "$total Mins "
//                time_to_destination!!.text = timetaken
//            } else {
//                val min = Integer.toString(total % 60)
//                timetaken = (total / 60).toString() + " Hr" + min + " Mins"
//                time_to_destination!!.text = timetaken
//            }
        }
    }

    private fun getUrl(
        source_latitude: Double,
        source_longitude: Double,
        dest_latitude: Double,
        dest_longitude: Double,
        s1Lat: Double,
        s1Long: Double,
        s2Lat: Double,
        s2Long: Double
    ): String {

        // Origin of route
        val str_origin = "origin=$source_latitude,$source_longitude"

        // Destination of route
        val str_dest = "destination=$dest_latitude,$dest_longitude"
        val waypoints1 = "waypoints=via:$s1Lat,$s1Long"
        val waypoints2 = "|via:$s2Lat,$s2Long"
        val sensor = "sensor=false"
        val key = "key=" + activity!!.resources.getString(R.string.google_map_api)
        var parameters: String? = null
        parameters = if (s1Lat == 0.0) {
            "$str_origin&$str_dest&$sensor&$key"
        } else if (s2Lat == 0.0) {
            "$str_origin&$str_dest&$waypoints1&$sensor&$key"
        } else {
            "$str_origin&$str_dest&$waypoints1$waypoints2&$sensor&$key"
        }
        // String parameters = str_origin + "&"  + str_dest + "&" + sensor + "&" + key;
        // Output format
        val output = "json"

        // Building the url to the web service
        val url = "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
        Log.e("MYURL", url)
        return url
    }

    private fun getUrlFromCurrentToPickup(
        source_latitude: Double,
        source_longitude: Double,
        dest_latitude: Double,
        dest_longitude: Double
    ): String {

        // Origin of route
        val str_origin = "origin=$source_latitude,$source_longitude"

        // Destination of route
        val str_dest = "destination=$dest_latitude,$dest_longitude"
        val sensor = "sensor=false"
        val key = "key=" + activity!!.resources.getString(R.string.google_map_api)
        var parameters: String? = null
        parameters = "$str_origin&$str_dest&$sensor&$key"

        // String parameters = str_origin + "&"  + str_dest + "&" + sensor + "&" + key;
        // Output format
        val output = "json"


        // Building the url to the web service
        val url = "https://maps.googleapis.com/maps/api/directions/$output?$parameters"
        Log.e("MYURLC", url)
        return url
    }


    private inner class MyTask : AsyncTask<String?, Void?, String>() {
        override fun doInBackground(vararg p0: String?): String {
            if (iscurr) {
                //   iscurr = false
                UpdateLocationToServer()
            }
            return ""
        }
    }
    // Fetches data from url passed

    public inner class FetchUrl : AsyncTask<String?, Void?, String>() {
        override fun doInBackground(vararg url: String?): String {

            // For storing data from web service
            var data = ""
            try {
                // Fetching the data from web service
                data = url[0]?.let { downloadUrl(it) }.toString()
                Log.d("Background Task data", data)
            } catch (e: Exception) {
                Log.d("Background Task", e.toString())
            }
            return data
        }

        override fun onPostExecute(result: String) {
            super.onPostExecute(result)
            val parserTask = ParserTask()

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result)
        }


    }

    @Throws(IOException::class)
    private fun downloadUrl(strUrl: String): String {
        var data = ""
        var iStream: InputStream? = null
        var urlConnection: HttpURLConnection? = null
        try {
            val url = URL(strUrl)

            // Creating an http connection to communicate with url
            urlConnection = url.openConnection() as HttpURLConnection

            // Connecting to url
            urlConnection!!.connect()

            // Reading data from url
            iStream = urlConnection.inputStream
            val br = BufferedReader(InputStreamReader(iStream))
            val sb = StringBuffer()
            var line: String? = ""
            while (br.readLine().also { line = it } != null) {
                sb.append(line)
            }
            data = sb.toString()
            Log.d("downloadUrl", data)
            br.close()
        } catch (e: Exception) {
            Log.d("Exception", e.toString())
        } finally {
            iStream!!.close()
            urlConnection!!.disconnect()
        }
        return data
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private inner class ParserTask :
        AsyncTask<String?, Int?, List<List<HashMap<String, String>>>?>() {
        // Parsing the data in non-ui thread
        override fun doInBackground(vararg jsonData: String?): List<List<HashMap<String, String>>>? {
            val jObject: JSONObject
            var routes: List<List<HashMap<String, String>>>? = null
            try {
                jObject = JSONObject(jsonData[0])
                // Log.d("ParserTask", jsonData[0])
                val parser = DataParser()
                Log.d("ParserTask", parser.toString())

                // Starts parsing data
                routes = parser.parse(jObject)

                var routes: JSONArray = jObject.getJSONArray("routes")

                val routes1: JSONObject = routes.getJSONObject(0)

                val legs: JSONArray = routes1.getJSONArray("legs")

                val legs1: JSONObject = legs.getJSONObject(0)

                val distance = legs1.getJSONObject("distance")

                val duration = legs1.getJSONObject("duration")

                val distanceText = distance.getString("text")

                val durationText = (duration.getString("value").toInt()) / 60


                val splitdistance: List<String> = distanceText.split(" ")
                SharedHelper.putKey(activity!!, "distancevalue", splitdistance[0])

                SharedHelper.putKey(activity!!, "timevalue", durationText.toString())


                Log.d("ParserTask", "Executing routes")
                Log.d("ParserTask", routes.toString())
            } catch (e: Exception) {
                Log.d("ParserTask", e.toString())
                e.printStackTrace()
            }
            return routes
        }

        // Executes in UI thread, after the parsing process
        override fun onPostExecute(result: List<List<HashMap<String, String>>>?) {
            Log.v("onPostExecute_call", result.toString())
            results = result
            // Traversing through all the routes
            for (i in result!!.indices) {
                points = ArrayList()
                lineOptions = PolylineOptions()

                // Fetching i-th route
                path = result[i]

                // Fetching all the points in i-th route
                for (j in path!!.indices) {
                    val point = path!![j]
                    val lat = point["lat"]!!.toDouble()
                    val lng = point["lng"]!!.toDouble()
                    val position = LatLng(lat, lng)
                    points!!.add(position)
                }

                if (!CurrentStatus.equals(
                        "END",
                        ignoreCase = true
                    ) || !CurrentStatus.equals("DROPPED", ignoreCase = true)
                ) {
                    mMap!!.clear()
                    if (CurrentStatus == "STARTED") {
                        if (sourceLatLng != null) {
                            if (sourceMarker != null) sourceMarker!!.remove()

                            val currentMarkerOptionss = MarkerOptions().anchor(0.5f, 0.5f)
                                .position(sourceLatLng!!).draggable(true)
                                .icon(
                                    BitmapDescriptorFactory.fromBitmap(
                                        getBitmapFromVectorDrawable(
                                            activity!!,
                                            R.drawable.provider_marker_icon
                                        )
                                    )
                                )
                            sourceMarker = mMap!!.addMarker(currentMarkerOptionss)
                        }
                        //   if (LiveTracking != true) {
                        if (destLatLng != null) {
                            if (destinationMarker != null) destinationMarker!!.remove()
                            val destMarker = MarkerOptions().anchor(0.5f, 0.5f)
                                .position(destLatLng!!).icon(
                                    BitmapDescriptorFactory.fromBitmap(
                                        getBitmapFromVectorDrawable(
                                            activity!!,
                                            R.drawable.ic_person
                                        )
                                    )
                                )
                            destinationMarker = mMap!!.addMarker(destMarker)
                        }
                    } else if (CurrentStatus == "ARRIVED") {

                    } else if (CurrentStatus == "PICKEDUP") {
                        if (sourceLatLng != null) {
                            if (sourceMarker != null) sourceMarker!!.remove()
                            val currentMarkerOptionss = MarkerOptions().anchor(0.5f, 0.5f)
                                .position(sourceLatLng!!).draggable(true)
                                .icon(
                                    BitmapDescriptorFactory.fromBitmap(
                                        getBitmapFromVectorDrawable(
                                            activity!!,
                                            R.drawable.provider_marker_icon
                                        )
                                    )
                                )
                            sourceMarker = mMap!!.addMarker(currentMarkerOptionss)
                        }
                    } else if (CurrentStatus == "CANCELLED") {
                        //sourceMarker!!.remove()
                        destinationMarker!!.remove()
                    } else {

                        if (sourceLatLng != null) {
                            if (sourceMarker != null) sourceMarker!!.remove()

                            val currentMarkerOptionss = MarkerOptions().anchor(0.5f, 0.5f)
                                .position(sourceLatLng!!).draggable(true)
                                .icon(
                                    BitmapDescriptorFactory.fromBitmap(
                                        getBitmapFromVectorDrawable(
                                            activity!!,
                                            R.drawable.provider_marker_icon
                                        )
                                    )
                                )
                            // currentMarker = mMap.addMarker(currentMarkerOptionss);
                            sourceMarker = mMap!!.addMarker(currentMarkerOptionss)
                        }

                        //   if (LiveTracking != true) {
                        if (destLatLng != null) {
                            if (destinationMarker != null)
                                destinationMarker!!.remove()
                            val hss2 = 50
                            val wss2 = 50
                            val bss2 =
                                activity!!.resources.getDrawable(R.drawable.marker) as BitmapDrawable
                            val bbss2 = bss2.bitmap
                            val sss2 = Bitmap.createScaledBitmap(bbss2, wss2, hss2, false)
                            //.icon(BitmapDescriptorFactory.fromBitmap(ss));
                            val destMarker = MarkerOptions()
                                .anchor(0.5f, 0.75f)
                                .position(destLatLng!!).draggable(true)
                                .icon(BitmapDescriptorFactory.fromBitmap(sss2))
                            // currentMarker = mMap.addMarker(destMarker);
                            destinationMarker = mMap!!.addMarker(destMarker)
                            if (updatestatus == "DESTINATION") {

                                updatedestinationdistance(
                                    getKey(activity!!, "trip_id"),
                                    getKey(activity!!, "new_edit_address"),
                                    getKey(activity!!, "new_edit_latitude")!!.toDouble(),
                                    getKey(activity!!, "new_edit_longitude")!!.toDouble(),
                                    "",
                                    null,
                                    null,
                                    "",
                                    null,
                                    null,
                                    CurrentStatus,
                                    "destination"
                                )
                            }
                        }
                    }
                    if (stop1LatLng != null) {
                        if (stop1LatLng!!.latitude != 0.0) {
                            if (stop1Marker != null) stop1Marker!!.remove()
                            val hs1 = 30
                            val ws1 = 30
                            val bs1 =
                                activity!!.resources.getDrawable(R.drawable.marker) as BitmapDrawable
                            val bbs1 = bs1.bitmap
                            val ss1 = Bitmap.createScaledBitmap(bbs1, ws1, hs1, false)
                            //.icon(BitmapDescriptorFactory.fromBitmap(ss));
                            val s1Marker = MarkerOptions()
                                .anchor(0.5f, 0.75f)
                                .position(stop1LatLng!!).draggable(true)
                                .icon(BitmapDescriptorFactory.fromBitmap(ss1))
                            //  currentMarker = mMap.addMarker(s1Marker);
                            stop1Marker = mMap!!.addMarker(s1Marker)
                        }
                    }
                    if (stop2LatLng != null) {
                        if (stop2LatLng!!.latitude != 0.0) {
                            if (stop2Marker != null) stop2Marker!!.remove()
                            val hs2 = 30
                            val ws2 = 30
                            val bs2 =
                                activity!!.resources.getDrawable(R.drawable.marker) as BitmapDrawable
                            val bbs2 = bs2.bitmap
                            val ss2 = Bitmap.createScaledBitmap(bbs2, ws2, hs2, false)
                            //.icon(BitmapDescriptorFactory.fromBitmap(ss));
                            val s2Marker = MarkerOptions()
                                .anchor(0.5f, 0.75f)
                                .position(stop2LatLng!!).draggable(true)
                                .icon(BitmapDescriptorFactory.fromBitmap(ss2))
                            // currentMarker = mMap.addMarker(s2Marker);
                            stop2Marker = mMap!!.addMarker(s2Marker)
                        }
                    }
                    val builder = LatLngBounds.Builder()
                    if (sourceMarker != null) {
                        builder.include(sourceMarker!!.position)
                    }
                    if (destinationMarker != null) {
                        builder.include(destinationMarker!!.position)
                    }
                    if (stop1Marker != null) {
                        builder.include(stop1Marker!!.position)
                    }
                    if (stop2Marker != null) {
                        builder.include(stop2Marker!!.position)
                    }
                }
                mMap!!.uiSettings.isMapToolbarEnabled = false


                // Adding all the points in the route to LineOptions
                lineOptions!!.addAll(points!!)
                lineOptions!!.width(8f)
                lineOptions!!.color(Color.BLACK)
                lineOptions!!.startCap(SquareCap())
                lineOptions!!.endCap(SquareCap())
                lineOptions!!.jointType(JointType.ROUND)
            }

            /* val markerOptions1 = MarkerOptions()
                .position(LatLng(latLng.latitude, latLng.longitude))
                .anchor(0.5f, 0.5f).icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromVectorDrawable(activity!!, R.drawable.provider_marker_icon)))
            currentMarker = mMap!!.addMarker(markerOptions1)
            val location1 = Location("")
            location1.latitude = latLng.latitude
            location1.longitude = latLng.longitude
            moveVechile(currentMarker, location1)
            rotateMarker(currentMarker, location.bearing, start_rotation)*/

            // Drawing polyline in the Google Map for the i-th route
            if (lineOptions != null && points != null) {
                mMap!!.addPolyline(lineOptions!!)
                println("lineOptions_onPostExecute  $points")
            } else {
                Log.d("onPostExecute", "without Polylines drawn")

            }
        }
    }

    fun additionalFeeDialog(feeType: String) {
        val li = LayoutInflater.from(activity!!)
        val promptsView = li.inflate(R.layout.user_input_dialog_box, null)
        val alertDialogBuilder = androidx.appcompat.app.AlertDialog.Builder(
            activity!!
        )

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView)
        val tv_title = promptsView.findViewById<View>(R.id.tv_title) as NTTextView
        val currency = promptsView.findViewById<View>(R.id.et_DialogCurrency) as NTTextView
        val userInput = promptsView.findViewById<View>(R.id.et_DialogUserInput) as NTEditText
        val et_DialogDesc = promptsView.findViewById<View>(R.id.et_DialogDesc) as NTEditText
        et_DialogDesc.visibility = View.GONE
        var strTitle = ""
        val strDesc = ""
        when (feeType) {
            "Toll" -> strTitle = getString(R.string.EnterTollFee)
            "Extra" -> {
                strTitle = getString(R.string.EnterExtraFee)
                et_DialogDesc.visibility = View.VISIBLE
            }

            "Base" -> {
                strTitle = getString(R.string.EnterBaseFee)
            }
        }
        currency.text = "" + str_currency
        tv_title.text = strTitle

        // set dialog message
        alertDialogBuilder
            .setCancelable(false)
            .setPositiveButton(
                getString(R.string.ok)
            ) { dialog, id ->
                if (!userInput.text.toString().isEmpty()) {
                    updateAdditionalCharge(
                        feeType,
                        userInput.text.toString(),
                        et_DialogDesc.text.toString()
                    )
                } else {
                    userInput.requestFocus()
                    userInput.error = "Enter Amount"
                }
            }
            .setNegativeButton(
                getString(R.string.cancel)
            ) { dialog, id -> dialog.cancel() }

        // create alert dialog
        val alertDialog = alertDialogBuilder.create()

        // show it
        alertDialog.show()
        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).isAllCaps = false
        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).isAllCaps = false
    }

    fun updateAdditionalCharge(feetype: String, userInput: String?, desc: String?) {
        loadingDialog = LoadingDialog(activity!!)
        loadingDialog!!.showDialog()
        val `object` = JSONObject()
        try {
            `object`.put("request_id", getKey(activity!!, "trip_id"))
            if (feetype.equals("Toll", ignoreCase = true)) {
                `object`.put("toll_fee", userInput)
            } else if (feetype.equals("Extra", ignoreCase = true)) {
                `object`.put("extra_fee", userInput)
                `object`.put("extra_desc", desc)
            } else if (feetype.equals("Base", ignoreCase = true)) {
                `object`.put("base_fare", userInput)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Utilities.PrintAPI_URL(URLHelper.UPDATE_ADDITIONAL_FARE, `object`.toString())
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            URLHelper.UPDATE_ADDITIONAL_FARE,
            `object`,
            Response.Listener { response ->
                Utilities.printAPI_Response(response.toString())
                loadingDialog!!.hideDialog()
                if (response.optString("success").equals("1")) {
                    makeText(activity!!, getString(R.string.amt_added), Toast.LENGTH_SHORT).show()
                }
                UpdateLocationToServer() //.
                //.   droppedTripDetail
            },
            Response.ErrorListener { error ->
                loadingDialog!!.hideDialog()
                try {
                    var json: String? = null
                    var Message: String
                    val response = error.networkResponse
                    if (response != null && response.data != null) {
                        try {
                            val errorObj = JSONObject(String(response.data))
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    utils.showCustomAlert(
                                        activity!!,
                                        Utilities.ALERT_ERROR,
                                        resources.getString(R.string.app_name),
                                        errorObj.optString("message")
                                    )
                                } catch (e: Exception) {
                                    utils.showCustomAlert(
                                        activity!!,
                                        Utilities.ALERT_ERROR,
                                        resources.getString(R.string.app_name),
                                        getString(R.string.something_went_wrong)
                                    )
                                    e.printStackTrace()
                                }
                            } else if (response.statusCode == 401) {
                                GoToBeginActivity()
                            } else if (response.statusCode == 422) {
                                json = NTApplication.trimMessage(String(response.data))
                                if (json !== "" && json != null) {
                                    utils.showCustomAlert(
                                        activity!!,
                                        Utilities.ALERT_ERROR,
                                        resources.getString(R.string.app_name),
                                        json
                                    )
                                } else {
                                    utils.showCustomAlert(
                                        activity!!,
                                        Utilities.ALERT_ERROR,
                                        resources.getString(R.string.app_name),
                                        getString(R.string.please_try_again)
                                    )
                                }
                            } else if (response.statusCode == 503) {
                                utils.showCustomAlert(
                                    activity!!,
                                    Utilities.ALERT_ERROR,
                                    resources.getString(R.string.app_name),
                                    getString(R.string.server_down)
                                )
                            } else {
                                utils.showCustomAlert(
                                    activity!!,
                                    Utilities.ALERT_ERROR,
                                    resources.getString(R.string.app_name),
                                    getString(R.string.please_try_again)
                                )
                            }
                        } catch (e: Exception) {
                            utils.showCustomAlert(
                                activity!!,
                                Utilities.ALERT_ERROR,
                                resources.getString(R.string.app_name),
                                getString(R.string.something_went_wrong)
                            )
                            e.printStackTrace()
                        }
                    } else {
                        utils.showCustomAlert(
                            activity!!,
                            Utilities.ALERT_ERROR,
                            resources.getString(R.string.app_name),
                            getString(R.string.please_try_again)
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = getKey(activity!!, "lang")!!
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "Bearer " + getKey(activity!!, "access_token")
                Log.e("", "Access_Token" + getKey(activity!!, "access_token"))
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    fun showLogoutDialog() {
        val builder3 = AlertDialog.Builder(activity!!)
        val inflater3 =
            activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout3 = inflater3.inflate(R.layout.layout_alert_dialog, null)
        builder3.setCancelable(false)
        builder3.setView(layout3)
        builder3.create()
        val alertDialog3 = builder3.create()
        val tv_alert_title = layout3.findViewById<View>(R.id.tv_alert_title) as NTTextView
        val tv_alert_desc = layout3.findViewById<View>(R.id.tv_alert_desc) as NTTextView
        val tv_alert_positive = layout3.findViewById<View>(R.id.tv_alert_positive) as NTTextView
        val tv_alert_negative = layout3.findViewById<View>(R.id.tv_alert_negative) as NTTextView
        val check_reason = layout3.findViewById<View>(R.id.check_reason) as NTCheckbox
        val check_reason_without =
            layout3.findViewById<View>(R.id.check_reason_without) as NTCheckbox
        val reason_etxt = layout3.findViewById<View>(R.id.reason_etxt) as NTEditText
        check_reason.visibility = View.VISIBLE
        reason_etxt.visibility = View.VISIBLE
        check_reason_without.visibility = View.VISIBLE
        check_reason_without.text = resources.getString(R.string.raise_cancel_request_desc)
        check_reason.text = resources.getString(R.string.cancel_without_request)
        tv_alert_title.text = resources.getString(R.string.raise_cancel_request)
        tv_alert_desc.text = resources.getString(R.string.cancel_without_request_desc)
        tv_alert_positive.text = resources.getString(R.string.ok)
        tv_alert_negative.text = resources.getString(R.string.cancel)
        check_reason_without.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                check_reason.isChecked = false
                check_reason_without.isChecked = true
            }
        }
        check_reason.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                check_reason_without.isChecked = false
                check_reason.isChecked = true
            }
        }
        tv_alert_positive.setOnClickListener {
            if (!check_reason.isChecked && !check_reason_without.isChecked) {
                utils.showCustomAlert(
                    activity!!,
                    Utilities.ALERT_WARNING,
                    resources.getString(R.string.app_name),
                    "Check anyone reason,To cancel trip."
                )
            } else {
                if (check_reason_without.isChecked) {
                    cancelRequest(
                        getKey(activity!!, "Item_id"),
                        alertDialog3,
                        reason_etxt.text.toString(),
                        "1"
                    )
                } else if (check_reason.isChecked) {
                    cancelRequest(
                        getKey(activity!!, "Item_id"),
                        alertDialog3,
                        reason_etxt.text.toString(),
                        "0"
                    )
                }
            }
        }
        tv_alert_negative.setOnClickListener { alertDialog3.dismiss() }
        alertDialog3.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog3.window!!.attributes.windowAnimations = R.style.dialog_animation
        alertDialog3.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog3.show()
    }

    private fun cancelRequest(
        tripId: String?,
        alertDialog3: AlertDialog,
        reason: String,
        request: String
    ) {
        Utilities.PrintAPI_URL(URLHelper.CANCEL_TRIP + tripId, "POST")
        if (loadingDialog != null) loadingDialog!!.showDialog()
        val `object` = JSONObject()
        try {
            `object`.put("cancel_reason", reason)
            `object`.put("cancel_request", request)
            `object`.put("request_id", tripId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Utilities.PrintAPI_URL(URLHelper.CANCEL_TRIP, "Request params = $`object`")
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            URLHelper.CANCEL_TRIP,
            `object`,
            Response.Listener { response ->
                try {
                    if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                    Utilities.printAPI_Response("Cancel $response")
                    if (response.getString("success") == "1") {
                        val intentHistory = Intent(activity!!, MainActivity::class.java)
                        startActivity(intentHistory)
                        activity!!.finish()
                        alertDialog3.dismiss()
                    } else {
                        utils.showCustomAlert(
                            activity!!,
                            Utilities.ALERT_ERROR,
                            resources.getString(R.string.app_name),
                            response.getString("message")
                        )
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                try {
                    if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                    if (loadingDialog != null && loadingDialog!!.isShowing) loadingDialog!!.hideDialog()
                    var json: String? = null
                    var Message: String
                    val response = error.networkResponse
                    if (response != null && response.data != null) {
                        try {
                            val errorObj = JSONObject(String(response.data))
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    utils.showCustomAlert(
                                        activity!!,
                                        Utilities.ALERT_ERROR,
                                        resources.getString(R.string.app_name),
                                        errorObj.optString("error")
                                    )
                                } catch (e: Exception) {
                                    utils.showCustomAlert(
                                        activity!!,
                                        Utilities.ALERT_ERROR,
                                        resources.getString(R.string.app_name),
                                        getString(R.string.something_went_wrong)
                                    )
                                }
                            } else if (response.statusCode == 401) {
                                putKey(activity!!, "loggedIn", getString(R.string.False))
                            } else if (response.statusCode == 422) {
                                json = NTApplication.trimMessage(String(response.data))
                                if (json !== "" && json != null) {
                                    utils.showCustomAlert(
                                        activity!!,
                                        Utilities.ALERT_ERROR,
                                        resources.getString(R.string.app_name),
                                        json
                                    )
                                } else {
                                    utils.showCustomAlert(
                                        activity!!,
                                        Utilities.ALERT_ERROR,
                                        resources.getString(R.string.app_name),
                                        getString(R.string.please_try_again)
                                    )
                                }
                            } else if (response.statusCode == 503) {
                                utils.showCustomAlert(
                                    activity!!,
                                    Utilities.ALERT_ERROR,
                                    resources.getString(R.string.app_name),
                                    getString(R.string.server_down)
                                )
                            }
                        } catch (e: Exception) {
                            utils.showCustomAlert(
                                activity!!,
                                Utilities.ALERT_ERROR,
                                resources.getString(R.string.app_name),
                                getString(R.string.something_went_wrong)
                            )
                        }
                    } else {
                        if (error is NoConnectionError) {
                            utils.showCustomAlert(
                                activity!!,
                                Utilities.ALERT_ERROR,
                                resources.getString(R.string.app_name),
                                getString(R.string.oops_connect_your_internet)
                            )
                        } else if (error is NetworkError) {
                            utils.showCustomAlert(
                                activity!!,
                                Utilities.ALERT_ERROR,
                                resources.getString(R.string.app_name),
                                getString(R.string.oops_connect_your_internet)
                            )
                        } else if (error is TimeoutError) {
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    makeText(
                        activity!!,
                        resources.getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] =
                    "" + getKey(activity!!, "token_type") + " " + getKey(activity!!, "access_token")
                Utilities.PrintAPI_URL(URLHelper.CANCEL_TRIP, "Request Header params = $headers")
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    @Throws(JSONException::class)
    private fun setValuesTo_ll_01_contentLayer_accept_or_reject_now(status: JSONObject) {

        putKey(activity!!, "a", "1")
        txt01Pickup!!.text = status.getJSONObject("ride").optString("s_address")

        txt01UserName!!.text = status.getJSONObject("user_info").optString("user_name")
        if (status.getJSONObject("user_info").getString("rating") != null) {
            rat01UserRating!!.rating =
                java.lang.Float.valueOf(status.getJSONObject("user_info").optString("rating"))
        }

        count = if (getKey(activity!!, "count") != "") {
            getKey(activity!!, "count")
        } else {
            "0"
        }

        mProgress!!.progress = 0 // Main Progress
        mProgress!!.secondaryProgress = count!!.toInt() / 2 // Secondary Progress
        // mProgress!!.setMax(Integer.parseInt(count)); // Maximum Progress
        mProgress!!.progressDrawable = drawableCount
        countDownTimer = object : CountDownTimer((count!!.toInt() * 1000).toLong(), 1000) {
            @SuppressLint("SetTextI18n")
            override fun onTick(millisUntilFinished: Long) {
                txt01Timer!!.text = "" + millisUntilFinished / 1000
                //mProgress!!.setProgress((millisUntilFinished / 1000).toInt())
                if (mPlayer == null) {
                    mPlayer = MediaPlayer.create(activity, R.raw.alert_tone)
                } else {
                    if (!mPlayer!!.isPlaying) {
                        val mAudioManager =
                            activity!!.getSystemService(Context.AUDIO_SERVICE) as AudioManager
                        mPlayer!!.setVolume(
                            mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                                .toFloat(),
                            mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                                .toFloat()
                        )
                        mPlayer!!.start()
                    }
                }
            }

            override fun onFinish() {
                txt01Timer!!.text = "0"
                if (mMap != null) {
                    mMap!!.clear()
                }
                if (mPlayer != null && mPlayer!!.isPlaying) {
                    mPlayer!!.stop()
                    mPlayer = null
                }
                Trip_alert_Dialog!!.dismiss()
                updateRejectStatus(getKey(activity!!, "trip_id"))
                //  handleIncomingRequest("Reject", request_id);
            }
        }
        countDownTimer?.start()
        var userNote = status.getJSONObject("user_info").optString("user_notes")

        if (userNote.isEmpty()) {
            Log.v("userNote_empty", "this")
        } else if (userNote == null) {
            Log.v("userNote_null", "this")
        } else if (userNote == "") {
            Log.v("userNote_str", "this")
        } else if (userNote.isNullOrEmpty()) {
            Log.v("userNote_NullOrEmpty", "this")
        }

        if (userNote == "") {
            txtDriverMessagepopup!!.text = resources.getString(R.string.NoMessage)
        } else {
            txtDriverMessagepopup!!.text = userNote
        }
        if (status.getJSONObject("user_info").optString("user_img") != "") {
//                    new DownloadImageTask(img01User).execute(user.getString("picture"));
            //Glide.with(activity).load(URLHelper.base+"storage/"+user.getString("picture")).placeholder(R.drawable.ic_dummy_user).error(R.drawable.ic_dummy_user).into(img01User);
            if (status.getJSONObject("user_info").optString("user_img").startsWith("http")) {
                Log.e("status_img", "" + status.getJSONObject("user_info").optString("user_img"))
                Picasso.with(activity!!)
                    .load(status.getJSONObject("user_info").optString("user_img"))
                    .placeholder(R.drawable.ic_driver).error(R.drawable.ic_driver)
                    .into(img01User)
            } else {
                Picasso.with(activity!!)
                    .load(status.getJSONObject("user_info").optString("user_img"))
                    .placeholder(R.drawable.ic_driver).error(R.drawable.ic_driver)
                    .into(img01User)
            }

        } else {
            img01User!!.setBackgroundResource(R.drawable.placeholder)
        }
    }


    override fun onDestroy() {
        putKey(activity!!, "driverOnlineFlag", "false")
        putKey(activity!!, "driverRideFlag", "false")
        if (mPlayer != null && mPlayer!!.isPlaying) {
            mPlayer!!.stop()
            mPlayer = null
        }
        if (running) {
            chronometer!!.stop()
            pauseoffset = SystemClock.elapsedRealtime() - chronometer!!.base
            putKey(activity!!, "pauseoffset", "" + pauseoffset)
            running = false
        }

        //  activity!!.unbindService(mServiceConnection)

        // handler();
        //   ha.removeCallbacksAndMessages(null);
        super.onDestroy()
    }

    fun checkoverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(activity)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + activity!!.packageName)
                )
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE)
            }
        }
    }

    /*   // Monitors the state of the connection to the service.
       private val mServiceConnection: ServiceConnection = object : ServiceConnection {
           override fun onServiceConnected(name: ComponentName, service: IBinder) {
               val binder = service as LocalBinder
               mService = binder.service
               mService?.requestLocationUpdates()
               mBound = true
           }

           override fun onServiceDisconnected(name: ComponentName) {
               mService = null
               mBound = false
           }
       }*/

    ///LIVE
    private fun showOrAnimateMarker(latLng: LatLng) {
        if (currentMarker == null) {
            val markerOptions = MarkerOptions()
                .position(latLng)
                .anchor(0.5f, 0.5f).icon(
                    BitmapDescriptorFactory.fromBitmap(
                        getBitmapFromVectorDrawable(
                            activity!!,
                            R.drawable.provider_marker_icon
                        )
                    )
                )
            currentMarker = mMap!!.addMarker(markerOptions)
        } else {
            markerAnimationHelper.animateMarkerToGB(
                currentMarker!!,
                latLng,
                (Spherical() as LatLngInterpolator)
            )
        }
    }

    private fun distance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val theta = lon1 - lon2
        var dist =
            Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(
                deg2rad(lat2)
            ) * Math.cos(deg2rad(theta))
        dist = Math.acos(dist)
        dist = rad2deg(dist)
        dist = dist * 60 * 1.1515
        return dist
    }

    private fun deg2rad(deg: Double): Double {
        return deg * Math.PI / 180.0
    }

    private fun rad2deg(rad: Double): Double {
        return rad * 180.0 / Math.PI
    }


    fun showErrorLayout(type: String) {
        if (type.equals("INTERNET", ignoreCase = true)) {
            Utilities.displayNetworkerrorFragment(activity!!, Utilities.ENABLE_NETWORK)
        }

    }

    fun getBitmapFromVectorDrawable(context: Context?, drawableId: Int): Bitmap {
        var drawable = ContextCompat.getDrawable(context!!, drawableId)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = DrawableCompat.wrap(drawable!!).mutate()
        }
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    fun onBackPressed(): Boolean {
        // Toast.makeText(activity!!, "OnBackpress Click", Toast.LENGTH_LONG).show();
        return false
    }

    private fun redrawLine(latLng: LatLng, location: Location) {
        var index = 0;
        mMap?.clear()

        if (!CurrentStatus.equals("END", ignoreCase = true) || !CurrentStatus.equals(
                "DROPPED",
                ignoreCase = true
            )
        ) {
            Log.v("PolylineTrackerr", "one")
            //mMap!!.clear()
            if (CurrentStatus == "STARTED") {
                Log.v("PolylineTrackerr", "Start")
                if (sourceLatLng != null) {
                    if (sourceMarker != null) sourceMarker!!.remove()
                    val currentMarkerOptionss = MarkerOptions().anchor(0.5f, 0.5f)
                        .position(sourceLatLng!!).draggable(true)
                        .icon(
                            BitmapDescriptorFactory.fromBitmap(
                                getBitmapFromVectorDrawable(
                                    activity!!,
                                    R.drawable.provider_marker_icon
                                )
                            )
                        )
                    sourceMarker = mMap!!.addMarker(currentMarkerOptionss)
                    sourceMarker!!.remove()
                }

                if (destLatLng != null) {
                    if (destinationMarker != null) destinationMarker!!.remove()
                    val destMarker = MarkerOptions().title("Destination").anchor(0.5f, 0.75f)
                        .position(destLatLng!!).icon(
                            BitmapDescriptorFactory.fromBitmap(
                                getBitmapFromVectorDrawable(
                                    activity!!,
                                    R.drawable.ic_person
                                )
                            )
                        )
                    destinationMarker = mMap!!.addMarker(destMarker)
                }
            } else if (CurrentStatus == "ARRIVED") {
                Log.v("PolylineTrackerr", "arrived")
            } else if (CurrentStatus == "CANCELLED") {
                Log.v("PolylineTrackerr", "canceled")
                //sourceMarker!!.remove()
                // destinationMarker!!.remove()
            } else if (CurrentStatus == "") {
                destinationMarker!!.remove()
                Log.v("PolylineTrackerr", "empty")
            } else {
                Log.v("PolylineTrackerr", "else")
                if (sourceLatLng != null) {
                    if (sourceMarker != null) sourceMarker!!.remove()
                    val currentMarkerOptionss = MarkerOptions().anchor(0.5f, 0.5f)
                        .position(sourceLatLng!!).draggable(true)
                        .icon(
                            BitmapDescriptorFactory.fromBitmap(
                                getBitmapFromVectorDrawable(
                                    activity!!,
                                    R.drawable.provider_marker_icon
                                )
                            )
                        )
                    sourceMarker = mMap!!.addMarker(currentMarkerOptionss)
                    sourceMarker!!.remove()
                }
                if (destLatLng != null) {
                    if (destinationMarker != null)
                        destinationMarker!!.remove()
                    val hss2 = 50
                    val wss2 = 50
                    val bss2 = activity!!.resources.getDrawable(R.drawable.marker) as BitmapDrawable
                    val bbss2 = bss2.bitmap
                    val sss2 = Bitmap.createScaledBitmap(bbss2, wss2, hss2, false)
                    val destMarker = MarkerOptions()
                        .anchor(0.5f, 0.75f)
                        .position(destLatLng!!).draggable(true)
                        .icon(BitmapDescriptorFactory.fromBitmap(sss2))
                    destinationMarker = mMap!!.addMarker(destMarker)
                }
            }

            if (stop1LatLng != null) {
                if (stop1LatLng!!.latitude != 0.0) {
                    if (stop1Marker != null) stop1Marker!!.remove()
                    val hs1 = 30
                    val ws1 = 30
                    val bs1 = activity!!.resources.getDrawable(R.drawable.marker) as BitmapDrawable
                    val bbs1 = bs1.bitmap
                    val ss1 = Bitmap.createScaledBitmap(bbs1, ws1, hs1, false)

                    val s1Marker = MarkerOptions()
                        .anchor(0.5f, 0.75f)
                        .position(stop1LatLng!!).draggable(true)
                        .icon(BitmapDescriptorFactory.fromBitmap(ss1))
                    stop1Marker = mMap!!.addMarker(s1Marker)
                }
            }
            if (stop2LatLng != null) {
                if (stop2LatLng!!.latitude != 0.0) {
                    if (stop2Marker != null) stop2Marker!!.remove()
                    val hs2 = 30
                    val ws2 = 30
                    val bs2 = activity!!.resources.getDrawable(R.drawable.marker) as BitmapDrawable
                    val bbs2 = bs2.bitmap
                    val ss2 = Bitmap.createScaledBitmap(bbs2, ws2, hs2, false)
                    val s2Marker = MarkerOptions()
                        .anchor(0.5f, 0.75f)
                        .position(stop2LatLng!!).draggable(true)
                        .icon(BitmapDescriptorFactory.fromBitmap(ss2))
                    stop2Marker = mMap!!.addMarker(s2Marker)
                }
            }
            val builder = LatLngBounds.Builder()
            if (sourceMarker != null) {
                builder.include(sourceMarker!!.position)
            }

            if (destinationMarker != null) {
                builder.include(destinationMarker!!.position)
            }
            if (stop1Marker != null) {
                builder.include(stop1Marker!!.position)
            }
            if (stop2Marker != null) {
                builder.include(stop2Marker!!.position)
            }
        }
        Log.v("PolylineTrackerr", "fun")
        //clears all Markers and Polylines
        val options = PolylineOptions().width(8f).color(Color.BLACK).geodesic(true)

        for (i in path!!.indices) {
            val point = path!![i]
            val lat = point["lat"]!!.toDouble()
            val lng = point["lng"]!!.toDouble()
            val position = LatLng(lat, lng)
            val df = DecimalFormat("#.###")
            df.roundingMode = RoundingMode.CEILING
            if (df.format(latLng.latitude)
                    .equals(df.format(position.latitude)) && df.format(latLng.longitude)
                    .equals(df.format(position.longitude))
            ) {
                index = i;
                break;
            }
            if (destLatLng != null) {
                if (df.format(destLatLng?.latitude)
                        .equals(df.format(position.latitude)) && df.format(destLatLng?.longitude)
                        .equals(df.format(position.longitude))
                ) {
                    mMap?.clear()
                    break;
                }
            }
        }

        for (i in index..path!!.indices.endInclusive) {
            val point = path!![i]
            val lat = point["lat"]!!.toDouble()
            val lng = point["lng"]!!.toDouble()
            val position = LatLng(lat, lng)
            options.add(position)
        }

        val myLocation = LatLng(latLng.latitude, latLng.longitude)
        val markerOptions1 = MarkerOptions()
            .position(LatLng(latLng.latitude, latLng.longitude))
            .anchor(0.5f, 0.5f).icon(
                BitmapDescriptorFactory.fromBitmap(
                    getBitmapFromVectorDrawable(
                        activity!!,
                        R.drawable.provider_marker_icon
                    )
                )
            )
        currentMarker = mMap!!.addMarker(markerOptions1)
        val location1 = Location("")
        location1.latitude = latLng.latitude
        location1.longitude = latLng.longitude
        moveVechile(currentMarker, location1)
        rotateMarker(currentMarker, location.bearing, start_rotation)
        if (line_status) {
            mMap?.addPolyline(options)
        }
        isPolyAdded = true
        mMap!!.uiSettings.isMapToolbarEnabled = false
    }


    private fun GetEmergencyContactList() {
        /*  loadingDialog.showDialog();*/
        val `object` = JSONObject()
        Utilities.PrintAPI_URL(URLHelper.EMERGENCY_CONTACT_LIST, "GET")
        val jsonArrayRequest: JsonArrayRequest = object :
            JsonArrayRequest(URLHelper.EMERGENCY_CONTACT_LIST, Response.Listener { response ->
                Utilities.printAPI_Response(response.toString())
                emer = false
                /*  loadingDialog.hideDialog();*/
                var phoneNo: String? = null
                var name: String? = null
                var sos: String? = null
                try {
                    contactList.clear()
                    if (response != null && response.length() > 0) {
                        for (i in 0 until response.length()) {
                            val jsonObject = response.getJSONObject(i)
                            name = jsonObject.optString("contact_name")
                            phoneNo = jsonObject.optString("contact_number")
                            contactList.add(EmergencyContactData(name, phoneNo))
                            sos = getKey(activity!!, "sos_number")
                            contactList.add(EmergencyContactData("admin", sos.toString()))
                            // mAdapter.notifyDataSetChanged();
                        }
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error ->
                if (error is TimeoutError) {
                    // makeText(activity!!, activity!!.getString(R.string.error_network_timeout), Toast.LENGTH_LONG).show()
                } else if (error is NoConnectionError) {
                    makeText(
                        activity!!,
                        activity!!.getString(R.string.error_no_network),
                        Toast.LENGTH_LONG
                    ).show()
                } else if (error is AuthFailureError) {
                    makeText(
                        activity!!,
                        activity!!.getString(R.string.error_auth_failure),
                        Toast.LENGTH_LONG
                    ).show()
                } else if (error is ServerError) {
                    makeText(
                        activity!!,
                        activity!!.getString(R.string.error_server_connection),
                        Toast.LENGTH_LONG
                    ).show()
                } else if (error is NetworkError) {
                    makeText(
                        activity!!,
                        activity!!.getString(R.string.error_network),
                        Toast.LENGTH_LONG
                    ).show()
                } else if (error is ParseError) {
                    makeText(
                        activity!!,
                        activity!!.getString(R.string.error_parse),
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = getKey(activity!!, "lang")!!
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "Bearer " + getKey(activity!!, "access_token")
                Log.e("", "Access_Token" + getKey(activity!!, "access_token"))
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonArrayRequest)
    }


    fun SearchAlert(desc: String) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(activity!!, R.style.DialogTheme)
        // Get the layout inflater
        val inflater =
            activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.fragment_search, null)
        builder.setCancelable(false)
        builder.setView(layout)
        builder.create()
        alertDialog = builder.create()
        alertDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val wmlp: WindowManager.LayoutParams = alertDialog!!.getWindow()!!.getAttributes()
        wmlp.gravity = Gravity.TOP
        val search_places = layout.findViewById<View>(R.id.search_places) as EditText
        val img_exit = layout.findViewById<View>(R.id.img_exit) as ImageView


        search_place_recyclerView =
            layout.findViewById<View>(R.id.search_place_recyclerView) as RecyclerView
        ll_errorLayout = layout.findViewById<View>(R.id.ll_errorLayout) as LinearLayout



        img_exit.setOnClickListener { alertDialog?.dismiss() }
        try {
            alertDialog?.setOnCancelListener(DialogInterface.OnCancelListener { alertDialog?.dismiss() })
            search_places.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (search_places.text.toString().isEmpty()) {
                        search_place_recyclerView?.visibility = View.GONE
                        //  getPlaces(search_places.text.toString(), desc)
                        // ll_errorLayout?.setVisibility(View.GONE)
                    } else {
                        if (search_places.text.toString().length > 4) {
                            search_place_recyclerView?.visibility = View.VISIBLE
                            getPlaces(search_places.text.toString(), desc)
                        }
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            })
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        alertDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        alertDialog!!.getWindow()!!.getAttributes().windowAnimations = R.style.dialog_animation
        alertDialog!!.getWindow()!!.setBackgroundDrawableResource(android.R.color.transparent)
        alertDialog!!.show()
    }

    fun getPlaces(searchPlaces: String, desc: String) {
        val URl =
            "https://maps.googleapis.com/maps/api/place/autocomplete/json?input=" + searchPlaces + "&sensor=true&key=" + resources.getString(
                R.string.google_map_api
            )
        Utilities.printAPI_Response("$URl GET")
        val `object` = JSONObject()
        val jsonObjectRequest: JsonObjectRequest =
            object : JsonObjectRequest(Method.GET, URl, `object`, Response.Listener { response ->
                Utilities.printAPI_Response(response.toString())
                try {
                    val place_array = response.getJSONArray("predictions")
                    Utilities.printAPI_Response("place_array = " + place_array.toString() + "" + "place_array = " + place_array.length())
                    if (place_array.length() > 1) {
                        Utilities.printAPI_Response("place_array = " + place_array.toString() + "" + "place_array = " + place_array.length())
                        search_place_recyclerView?.visibility = View.VISIBLE
                        ll_errorLayout?.visibility = View.GONE
                        val mLayoutManager: RecyclerView.LayoutManager =
                            LinearLayoutManager(activity)
                        search_place_recyclerView!!.layoutManager = mLayoutManager
                        placesearchAdapter = PlaceSearchAdapter(
                            place_array,
                            desc,
                            alertDialog,
                            activity,
                            "List",
                            "addressID",
                            object : PlaceSearchAdapter.PlacesClickListener {
                                override fun onBtnClick(
                                    PLACE_ID: String,
                                    desc: String?,
                                    address: String
                                ) {
                                    hidekeyboard()
                                    getPlacesFetched(PLACE_ID, desc, address)
                                }
                            })
                        search_place_recyclerView!!.adapter = placesearchAdapter
                    } else {
                        search_place_recyclerView?.visibility = View.VISIBLE
                        //ll_errorLayout?.visibility = View.GONE
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error -> //hideLoadingDialog();
                var json: String? = null
                var Message: String
                val response = error.networkResponse
                if (response != null && response.data != null) {
                    try {
                        search_place_recyclerView?.visibility = View.GONE
                        val errorObj = JSONObject(String(response.data))
                        println(
                            "PassengerAPI response error : " + error + " " + error.networkResponse + " " + response.statusCode + " " + errorObj.optString(
                                "error"
                            )
                        )
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"))
                            } catch (e: java.lang.Exception) {
                                displayMessage(getString(R.string.something_went_wrong))
                            }
                        } else if (response.statusCode == 401) {
                        } else if (response.statusCode == 422) {
                            json = trimMessage(String(response.data))
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
                    } catch (e: java.lang.Exception) {
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
            }) {
                @Throws(AuthFailureError::class)
                override fun getHeaders(): Map<String, String> {
                    val headers = HashMap<String, String>()
                    //                headers.put("X-localization", SharedHelper.getKey(context, "lang"));
//                headers.put("X-Requested-With", "XMLHttpRequest");
//                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " "
//                        + SharedHelper.getKey(context, "access_token"));
//                utils.print("authoization", "" + SharedHelper.getKey(context, "token_type") + " "
//                        + SharedHelper.getKey(context, "access_token"));
                    Utilities.print("authoization", headers.toString())
                    return headers
                }
            }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }


    fun getPlacesFetched(PLACE_ID: String, desc: String?, address: String) {
        val encode_url =
            "https://maps.googleapis.com/maps/api/place/details/json?placeid=" + PLACE_ID + "&key=" + resources.getString(
                R.string.google_map_api
            )
        val `object` = JSONObject()
        val jsonObjectRequest: JsonObjectRequest = object :
            JsonObjectRequest(Method.GET, encode_url, `object`, Response.Listener { response ->

                try {
                    val result = response.getJSONObject("result")
                    val place_array = result.getJSONArray("address_components")
                    val geometry = result.getJSONObject("geometry").getJSONObject("location")
                    val lat = geometry.getString("lat")
                    val lng = geometry.getString("lng")
                    val latlng = LatLng(lat.toDouble(), lng.toDouble())

                    if (desc.equals("stop1", ignoreCase = true)) {
                        tv_stop_one!!.text = address
                        putKey(activity!!, "new_stop1_address", address)
                        putKey(activity!!, "new_stop1_latitude", lat.toString() + "")
                        putKey(activity!!, "new_stop1_longitude", lng.toString() + "")
                        updatedestination(
                            getKey(activity!!, "trip_id"),
                            "",
                            null,
                            null,
                            address,
                            lat!!.toDouble(),
                            lng!!.toDouble(),
                            "",
                            null,
                            null,
                            CurrentStatus,
                            "stop1"
                        )


                    } else if (desc.equals("stop2", ignoreCase = true)) {
                        tv_stop_two!!.text = address
                        putKey(activity!!, "new_stop2_address", address)
                        putKey(activity!!, "new_stop2_latitude", lat.toString() + "")
                        putKey(activity!!, "new_stop2_longitude", lng.toString() + "")

                        updatedestination(
                            getKey(activity!!, "trip_id"),
                            "",
                            null,
                            null,
                            "",
                            null,
                            null,
                            address,
                            lat!!.toDouble(),
                            lng!!.toDouble(),
                            CurrentStatus,
                            "stop2"
                        )


                    } else {
                        tv_dest_address!!.text = address
                        putKey(activity!!, "new_edit_address", address)
                        putKey(activity!!, "new_edit_latitude", lat + "")
                        putKey(activity!!, "new_edit_longitude", lng + "")
                        //  updatedestination(getKey(activity!!, "trip_id"), address, lat!!.toDouble(), lng!!.toDouble(), "", null, null, address, lat!!.toDouble(), lng!!.toDouble(), CurrentStatus,"ed")
                        updatestatus = "DESTINATION"
                        showPathFromPickupToDropUpdated(
                            lat!!.toDouble(),
                            lng!!.toDouble(),
                            0.0,
                            0.0,
                            0.0,
                            0.0
                        )


                    }

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }, Response.ErrorListener { error -> //hideLoadingDialog();
                var json: String? = null
                var Message: String


            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                //                headers.put("X-localization", SharedHelper.getKey(context, "lang"));
//                headers.put("X-Requested-With", "XMLHttpRequest");
//                headers.put("Authorization", "" + SharedHelper.getKey(context, "token_type") + " "
//                        + SharedHelper.getKey(context, "access_token"));
//                utils.print("authoization", "" + SharedHelper.getKey(context, "token_type") + " "
//                        + SharedHelper.getKey(context, "access_token"));

                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    fun hidekeyboard() {
        // Check if no view has focus:
        val view = activity!!.currentFocus
        if (view != null) {
            val inputManager =
                activity!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
        /*  InputMethodManager in = (InputMethodManager)activity!!.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(activity!!.getCurrentFocus().getApplicationWindowToken(), 0);*/
    }

    fun displayMessage(toastString: String?) {
        dispalyDialog(activity!!, resources.getString(R.string.app_name), toastString)
    }

    fun dispalyDialog(
        context: Context?,
        title: String?,
        message: String?
    ) {
        val builder = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        builder.setMessage(message)
            .setTitle(title)
            .setCancelable(false)
            .setPositiveButton("ok") { dialog, id -> dialog.dismiss() }
        val alert = builder.create()
        alert.show()
    }


    fun clearTripStatus(
        tripid: String,
        providerid: String?,
        reason: String,
        cancelled_by: String?
    ) {
        Utilities.PrintAPI_URL(
            URLHelper.CLEAR_STATUS + "request_id=" + tripid + "&provider_id=" + providerid,
            "GET"
        )
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            URLHelper.CLEAR_STATUS + "request_id=" + tripid + "&provider_id=" + providerid,
            null,
            Response.Listener { response ->
                try {
                    if (response != null) {
                        Utilities.printAPI_Response(response.toString())

                        if (response.getString("success") == "1") {
                            Trip_alert_Dialog = null
                            mMap!!.clear()
                            isarr = true
                            ispick = true
                            isarr = true
                            distanceInKm = 0.00
                            putKey(activity!!, "drop_distance", "0.00")
                            distanceInMeters = 0.00
                            lastLocation = null
                            SharedHelper.putKey(activity!!, "a", "0")
                            showlayoutsRefresh()
                            putKey(activity!!, "user_name", "")
                            putKey(activity!!, "user_image", "")
                            putKey(activity!!, "ride_status", "")
                            putKey(activity!!, "arrived_toggle", "0")
                            putKey(activity!!, "pickedup_toggle", "0")
                            putKey(activity!!, "drop_toggle", "0")
                            countstarted = 0
                            countpicked = 0
                            droppedcount = 0
                            CurrentStatus = " "
                            // countstartedfirst=0;
                            //  countpickedfirst=0;
                            putKey(activity!!, "driverRideFlag", "false")
                            putKey(activity!!, "driverOnlineFlag", "true")

                            //  handler()
                            val sgps = GPSTracker(activity!!)
                            current_latitude = sgps.latitude
                            current_longitude = sgps.longitude
//                        onLocationChanged(current_latitude, current_longitude)
                            initMap()

                            // UpdateLocationToServer()
                            putKey(activity!!, "a", "0")
                            showlayoutsRefresh()
                        }



                        when (cancelled_by) {
                            "DISPATCHER" -> utils.showCustomAlert(
                                getActivity(),
                                Utilities.ALERT_WARNING,
                                resources.getString(R.string.app_name),
                                resources.getString(R.string.reject_despatcher)
                            )

                            "PROVIDER" -> utils.showCustomAlert(
                                getActivity(),
                                Utilities.ALERT_WARNING,
                                resources.getString(R.string.app_name),
                                resources.getString(R.string.cancel_ride)
                            )

                            "USER" -> utils.showCustomAlert(
                                context,
                                Utilities.ALERT_WARNING,
                                resources.getString(R.string.saferide_driver),
                                resources.getString(R.string.cancel_ridee)
                            )

                            "NONE" -> utils.showCustomAlert(
                                getActivity(),
                                Utilities.ALERT_WARNING,
                                resources.getString(R.string.app_name),
                                resources.getString(R.string.cancel_ridee)
                            )

                            "REJECTED" -> utils.showCustomAlert(
                                getActivity(),
                                Utilities.ALERT_WARNING,
                                resources.getString(R.string.app_name),
                                resources.getString(R.string.reject_driver)
                            )
//            else -> utils.showCustomAlert(getActivity(), Utilities.ALERT_WARNING, resources.getString(R.string.app_name), resources.getString(R.string.cancel_ridee))
                        }

                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->

                try {
                    if (error is TimeoutError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_network_timeout),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is NoConnectionError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_no_network),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is AuthFailureError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_auth_failure),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ServerError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_server_connection),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is NetworkError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_network),
                            Toast.LENGTH_LONG
                        ).show()
                    } else if (error is ParseError) {
                        makeText(
                            activity!!,
                            resources.getString(R.string.error_parse),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        makeText(activity!!, error.message, Toast.LENGTH_LONG).show()

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    makeText(
                        activity!!,
                        resources.getString(R.string.something_went_wrong),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = "en"
                headers["Content-Type"] = "application/json"
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] =
                    "" + getKey(activity!!, "token_type") + " " + getKey(activity!!, "access_token")
                Log.e(
                    "AUTH",
                    getKey(activity!!, "token_type") + " " + getKey(activity!!, "access_token")
                )
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    fun View.showOrGone(show: Boolean) {
        visibility = if (show) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    fun View.showOrInvisible(show: Boolean) {
        visibility = if (show) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    private fun askPermissionForBackgroundUsage() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                activity!!,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
        ) {
            AlertDialog.Builder(activity!!)
                .setTitle("Permission Needed!")
                .setMessage("Background Location Permission Needed!, tap \"Allow all time in the next screen\"")
                .setPositiveButton(
                    "OK"
                ) { dialog, which ->
                    ActivityCompat.requestPermissions(
                        activity!!,
                        arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                        2
                    )
                }
                .setNegativeButton(
                    "CANCEL"
                ) { dialog, which ->
                    // User declined for Background Location Permission.
                }
                .create().show()
        } else {
            ActivityCompat.requestPermissions(
                activity!!,
                arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
                2
            )
        }
    }
}