package com.eviort.cabedriver.NTFragment

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.AsyncTask
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.eviort.cabedriver.NTActivity.MainActivity
import com.eviort.cabedriver.NTAdapter.PlaceSearchAdapter
import com.eviort.cabedriver.NTApplication
import com.eviort.cabedriver.NTApplication.trimMessage
import com.eviort.cabedriver.NTCustomView.NTButton
import com.eviort.cabedriver.NTCustomView.NTEditText
import com.eviort.cabedriver.NTCustomView.NTTextView
import com.eviort.cabedriver.NTHelper.*
import com.eviort.cabedriver.NTHelper.SharedHelper.getKey
import com.eviort.cabedriver.NTHelper.SharedHelper.putKey
import com.eviort.cabedriver.NTUtilites.GPSTracker
import com.eviort.cabedriver.NTUtilites.Utilities
import com.eviort.cabedriver.R
import com.eviort.cabedriver.swipebtn.SwipeButton
import com.google.android.gms.maps.model.*
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * A simple [Fragment] subclass.
 */
class FragmentStreetRide : Fragment(), View.OnClickListener {
    var helper: ConnectionHelper? = null
    var customDialog: LoadingDialog? = null
    var utils = Utilities()
    var sr_app_fare: NTTextView? = null
    var sr_pickuploc: NTTextView? = null
    var sr_droploc: NTTextView? = null
    var sr_first_name: NTEditText? = null
    var sr_last_name: NTEditText? = null
    public var lineOptions: PolylineOptions? = null
    var results: List<List<HashMap<String, String>>>? = null
    var path: List<HashMap<String, String>>? = null
    public var points: ArrayList<LatLng?>? = null
    var sr_email: NTEditText? = null
    var sr_phno: NTEditText? = null
    var search_place_recyclerView: RecyclerView? = null
    var ll_errorLayout: LinearLayout? = null
    var placesearchAdapter: PlaceSearchAdapter? = null
    var alertDialog: AlertDialog? = null
    var btn_start_streetride: NTButton? = null
    var ll_stop_one: RelativeLayout? = null
    var ll_stop_two: RelativeLayout? = null
    var ic_add_stop_plus: ImageView? = null
    var image_delete_stop_one: ImageView? = null
    var image_delete_stop_two: ImageView? = null
    var ic_swap: ImageView? = null
    var imgBack: ImageView? = null
    var enableButton: SwipeButton? = null
    var placeFields: List<Place.Field>? = null
    var AUTOCOMPLETE_DEST_REQUEST_CODE = 2
    var AUTOCOMPLETE_STOP1_REQUEST_CODE = 3
    var AUTOCOMPLETE_STOP2_REQUEST_CODE = 4
    var lastDLatitude = 0.0
    var lastDLongitude = 0.0
    var current_latitude: String? = null
    var current_longitude: String? = null
    var stop1_lat = ""
    var stop1_lng = ""
    var stop1_address = ""
    var stop2_lat = ""
    var stop2_lng = ""
    var stop2_address = ""
    private var stop1LatLng: LatLng? = null
    private var stop2LatLng: LatLng? = null
    private var sourceLatLng: LatLng? = null
    private var destLatLng: LatLng? = null
    var rootview: View? = null
    var stop1address: NTEditText? = null
    var stop2address: NTEditText? = null
    var activity: Activity? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.activity_street_ride, container, false)
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
        initialization(rootview)
        FragmentHome.isRunning = false
        return rootview
    }

    @Suppress("DEPRECATION")
    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        this.activity = activity
    }

    fun initialization(rootview: View?) {
        helper = ConnectionHelper(activity!!)
        sr_pickuploc = rootview!!.findViewById<View>(R.id.sr_pickuploc) as NTTextView
        sr_droploc = rootview.findViewById<View>(R.id.sr_droploc) as NTTextView
        sr_app_fare = rootview.findViewById<View>(R.id.sr_app_fare) as NTTextView
        sr_first_name = rootview.findViewById<View>(R.id.sr_first_name) as NTEditText
        sr_last_name = rootview.findViewById<View>(R.id.sr_last_name) as NTEditText
        sr_email = rootview.findViewById<View>(R.id.sr_email) as NTEditText
        sr_phno = rootview.findViewById<View>(R.id.sr_phno) as NTEditText
        ic_add_stop_plus = rootview.findViewById<View>(R.id.ic_add_stop_plus) as ImageView
        ic_swap = rootview.findViewById<View>(R.id.ic_swap) as ImageView
        stop1address = rootview.findViewById<View>(R.id.txt_stop_one) as NTEditText
        stop2address = rootview.findViewById<View>(R.id.txt_stop_two) as NTEditText
        image_delete_stop_one = rootview.findViewById<View>(R.id.image_delete_stop_one) as ImageView
        image_delete_stop_two = rootview.findViewById<View>(R.id.image_delete_stop_two) as ImageView
        println("Service Id = " + getKey(activity!!, "service_type"))
        ll_stop_one = rootview.findViewById<View>(R.id.ll_stop_one) as RelativeLayout
        ll_stop_two = rootview.findViewById<View>(R.id.ll_stop_two) as RelativeLayout
        /*  btn_start_streetride = (NTButton) rootview.findViewById(R.id.btn_start_streetride);
        btn_start_streetride.setOnClickListener(this);*/
        imgBack = rootview.findViewById<View>(R.id.backArrow) as ImageView
        imgBack!!.setOnClickListener(this)
        ic_add_stop_plus!!.setOnClickListener(this)
        ic_swap!!.setOnClickListener(this)
        stop1address!!.setOnClickListener(this)
        stop2address!!.setOnClickListener(this)
        image_delete_stop_one!!.setOnClickListener(this)
        image_delete_stop_two!!.setOnClickListener(this)

        //SharedHelper.getKey(this, "sr_pickuplocation_addr"));
        enableButton = rootview.findViewById<View>(R.id.btn_start_streetride) as SwipeButton

        enableButton!!.setOnStateChangeListener { active -> //   Toast.makeText(getContext(), "State: " + active, Toast.LENGTH_SHORT).show();

            if (active) {
                enableButton!!.setText(activity!!.getString(R.string.start))
                enableButton!!.background = activity!!.getDrawable(R.drawable.rounded_button_gray)

                if (sr_droploc!!.text.toString() == null || sr_droploc!!.text.toString()
                        .isEmpty()
                ) {
                    displayMessage("Enter Drop Location")
                } else {
                    showPathFromPickupToDrop(
                        current_latitude!!.toDouble(),
                        current_longitude!!.toDouble()
                    )
                }
            } else {

                enableButton!!.background = activity!!.getDrawable(R.drawable.rounded_button_gray)
                enableButton!!.setButtonBackground(activity!!.getDrawable(R.drawable.rounded_button_slide))
            }
        }
        sr_droploc!!.setOnClickListener {
            SearchAlert("drop")
            sr_droploc!!.isEnabled = false

        }
        val gps = GPSTracker(activity)
        current_latitude = gps.latitude.toString()
        current_longitude = gps.longitude.toString()
        sr_pickuploc!!.text = getAddress(current_latitude!!, current_longitude!!)
    }

    fun getAddress(strLatitude: String, strLongitude: String): String {
        val geocoder: Geocoder
        val addresses: List<Address>
        geocoder = Geocoder(requireActivity(), Locale.getDefault())
        val latitude = strLatitude.toDouble()
        val longitude = strLongitude.toDouble()
        var address = ""
        var city = ""
        var state: String? = ""
        try {
            addresses = geocoder.getFromLocation(
                latitude,
                longitude,
                1
            ) as List<Address> // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            address =
                addresses[0].getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            city = addresses[0].locality
            state = addresses[0].adminArea
            val country = addresses[0].countryName
            val postalCode = addresses[0].postalCode
            val knownName = addresses[0].featureName
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return if (address.length > 0 || city.length > 0) "$address, $city" else getString(R.string.no_address)
    }

    fun displayMessage(toastString: String) {
        Toast.makeText(activity, "" + toastString, Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View) {
        val id = v.id
        when (id) {
            R.id.btn_start_streetride ->
                if (sr_droploc!!.text.toString() == null || sr_droploc!!.text.toString()
                        .isEmpty()
                ) {
                    displayMessage("Enter Drop Location")
                } else {
                }

            R.id.backArrow -> pop()
            R.id.ic_add_stop_plus ->                 //  showCustomAlert(getActivity(), ALERT_WARNING, getResources().getString(R.string.app_name), "Login To Access Details.");
                if (ll_stop_one!!.visibility == View.VISIBLE) {
                    if (stop1address?.text.toString().length > 0) {
                        ll_stop_two!!.visibility = View.VISIBLE
                        ic_swap!!.visibility = View.VISIBLE
                        image_delete_stop_one!!.visibility = View.GONE
                    } else {
                        displayMessage("Please fill in stop1 address")
                    }
                } else {
                    ll_stop_one!!.visibility = View.VISIBLE
                    image_delete_stop_one!!.visibility = View.VISIBLE
                    ic_add_stop_plus!!.visibility = View.VISIBLE
                }

            R.id.ic_swap -> {
                val temp_stop = stop1address!!.text.toString()
                val temp_lat = stop1_lat
                val temp_lng = stop1_lng
                stop1address!!.setText(getKey(requireActivity(), "stop2_address"))
                putKey(requireActivity(), "stop1_address", stop2_address)
                putKey(requireActivity(), "stop1_latitude", stop2_lat)
                putKey(requireActivity(), "stop1_longitude", stop2_lng)
                stop2address!!.setText(temp_stop)
                putKey(requireActivity(), "stop2_address", temp_stop)
                putKey(requireActivity(), "stop2_latitude", temp_lat)
                putKey(requireActivity(), "stop2_longitude", temp_lng)
            }

            R.id.image_delete_stop_one -> {
                //  showCustomAlert(getActivity(), ALERT_WARNING, getResources().getString(R.string.app_name), "Login To Access Details.");
                ll_stop_one!!.visibility = View.GONE
                ll_stop_one!!.visibility = View.GONE
                stop1address!!.setText("")
                putKey(activity!!, "stop1address", "")
                stop1_lat = ""
                stop1_lng = ""
                stop1_address = ""
                stop1LatLng = null
                putKey(activity!!, "stop1_address", "")
                putKey(activity!!, "stop1_latitude", "")
                putKey(activity!!, "stop1_longitude", "")
                ic_swap!!.visibility = View.GONE

                ic_add_stop_plus!!.visibility = View.VISIBLE
            }

            R.id.txt_stop_one -> {
                SearchAlert("stop1")
                stop1address!!.isEnabled = false
            }

            R.id.txt_stop_two -> {
                SearchAlert("stop2")
                stop2address!!.isEnabled = false
            }

            R.id.image_delete_stop_two -> {
                //  showCustomAlert(getActivity(), ALERT_WARNING, getResources().getString(R.string.app_name), "Login To Access Details.");
                ll_stop_one!!.visibility = View.VISIBLE
                ll_stop_two!!.visibility = View.GONE
                stop2address!!.setText("")
                putKey(activity!!, "stop2address", "")
                image_delete_stop_one!!.visibility = View.VISIBLE
                ic_swap!!.visibility = View.VISIBLE
                ic_swap!!.visibility = View.GONE
                ic_add_stop_plus!!.visibility = View.VISIBLE
                stop2_lat = ""
                stop2_lng = ""
                stop2_address = ""
                stop2LatLng = null
                putKey(activity!!, "stop2_address", "")
                putKey(activity!!, "stop2_latitude", "")
                putKey(activity!!, "stop2_longitude", "")
            }
        }
    }

    fun isValidEmai(email: String?): Boolean {
        val pattern: Pattern
        val matcher: Matcher
        val EMAIL_PATTERN =
            "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
        pattern = Pattern.compile(EMAIL_PATTERN)
        matcher = pattern.matcher(email)
        return matcher.matches()
    }

    private fun openAutoCompleteLocation(loc: String) {
        try {
            // Initialize Places.
            /**
             * Initialize Places. For simplicity, the API key is hard-coded. In a production
             * environment we recommend using a secure mechanism to manage API keys.
             */
            if (!Places.isInitialized()) {
                Places.initialize(activity!!, resources.getString(R.string.google_map_api))
            }

            // Specify the fields to return.
            // Include address, ID, and name.
            placeFields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG)
            //        placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME,Place.Field.ADDRESS, Place.Field.LAT_LNG);
// Start the autocomplete intent.
            val intent = Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN, placeFields as MutableList<Place.Field>
            )

                //                .setTypeFilter(TypeFilter.ADDRESS)
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
                activity,
                Utilities.ALERT_WARNING,
                resources.getString(R.string.app_name),
                resources.getString(R.string.something_went_wrong)
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == AUTOCOMPLETE_DEST_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data)
                sr_droploc!!.text = place.address.toString()
                //                dest_address = place.getAddress().toString();
//                destLatLng = place.getLatLng();
                lastDLatitude = place.latLng!!.latitude
                lastDLongitude = place.latLng!!.longitude
                sr_app_fare!!.text = ""
                CalculateApproxFare()
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
                stop1address!!.setText(place.address.toString())
                stop1_address = place.address.toString()
                stop1LatLng = place.latLng
                stop1_lat = place.latLng!!.latitude.toString() + ""
                stop1_lng = place.latLng!!.longitude.toString() + ""
                putKey(activity!!, "stop1_address", stop1_address)
                putKey(activity!!, "stop1_latitude", stop1_lat)
                putKey(activity!!, "stop1_longitude", stop1_lng)

                // below code to Layout Inflater
                //  GetServiceList();
//                getAddressFromPlaceId(place.getId(), "destination");
//                Log.i("TAG", "Place: " + place.getName() + ", " + place.getId());
            }
        } else {
            if (resultCode == Activity.RESULT_OK) {
                val place = Autocomplete.getPlaceFromIntent(data)
                stop2address!!.setText(place.address.toString())
                stop2_address = place.address.toString()
                stop2LatLng = place.latLng
                stop2_lat = place.latLng!!.latitude.toString() + ""
                stop2_lng = place.latLng!!.longitude.toString() + ""
                putKey(activity!!, "stop2_address", stop2_address)
                putKey(activity!!, "stop2_latitude", stop2_lat)
                putKey(activity!!, "stop2_longitude", stop2_lng)

                // below code to Layout Inflater
                //  GetServiceList();
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

    private fun StartStreetRide() {
        customDialog = LoadingDialog(activity!!)
        if (customDialog != null) customDialog!!.showDialog()

        val `object` = JSONObject()
        try {
            `object`.put("s_latitude", current_latitude)
            `object`.put("s_longitude", current_longitude)
            `object`.put("d_latitude", lastDLatitude)
            `object`.put("d_longitude", lastDLongitude)
            `object`.put("s_address", sr_pickuploc!!.text.toString())
            `object`.put("d_address", sr_droploc!!.text.toString())
            `object`.put("service_type_id", getKey(activity!!, "service_type"))
            `object`.put("stop1_latitude", getKey(activity!!, "stop1_latitude"))
            `object`.put("stop1_longitude", getKey(activity!!, "stop1_longitude"))
            `object`.put("stop1_address", getKey(activity!!, "stop1_address"))
            `object`.put("stop2_latitude", getKey(activity!!, "stop2_latitude"))
            `object`.put("stop2_longitude", getKey(activity!!, "stop2_longitude"))
            `object`.put("stop2_address", getKey(activity!!, "stop2_address"))
            // object.put("service_type", "1");
            //`object`.put("distance", getKey(activity!!, "distance"))
            `object`.put("distance", getKey(requireActivity(), "distancevalue"))
            `object`.put("minutes", getKey(requireActivity(), "timevalue"))
            `object`.put("mobile", sr_phno!!.text.toString())
            `object`.put("email", sr_email!!.text.toString())
            `object`.put("first_name", sr_first_name!!.text.toString())
            //`object`.put("dial_code", "+677")
            `object`.put("name", "")
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        Log.v(
            "STREETRIDE_TRIP",
            URLHelper.STREETRIDE_TRIP_START + "  Pharams : " + `object`.toString()
        )
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            URLHelper.STREETRIDE_TRIP_START,
            `object`,
            Response.Listener { response ->
                Log.v(
                    "STREETRIDE_TRIP",
                    URLHelper.STREETRIDE_TRIP_START + "  response : " + response.toString()
                )
                if (customDialog != null && customDialog!!.isShowing) customDialog!!.hideDialog()
                putKey(activity!!, "stop1_latitude", "")
                putKey(activity!!, "stop1_longitude", "")
                putKey(activity!!, "stop1_address", "")
                putKey(activity!!, "stop2_latitude", "")
                putKey(activity!!, "stop2_longitude", "")
                putKey(activity!!, "stop2_address", "")
                putKey(activity!!, "service_type", "")
                putKey(activity!!, "distancevalue", "")
                putKey(activity!!, "timevalue", "")

                enableButton!!.toggleState()

                try {
                    if (response.getString("success") == "1") {
                        //  utils.showCustomAlert(activity, Utilities.ALERT_SUCCESS, resources.getString(R.string.app_name), response.getString("message"))
                        putKey(activity!!, "street_trip_id", response.optString("trip_id"))
                        putKey(
                            activity!!,
                            "status",
                            "streetride   //delete space to hide mobile number"
                        )
                        //  FragmentHome.isRunning = false;
                        pop()

                    } else {
                        if (response.getString("message").equals("Something Went Wrong")) {
                            utils.showCustomAlert(
                                activity,
                                Utilities.ALERT_WARNING,
                                resources.getString(R.string.app_name),
                                resources.getString(R.string.no_avaliable_service)
                            )

                        } else {
                            utils.showCustomAlert(
                                activity,
                                Utilities.ALERT_ERROR,
                                resources.getString(R.string.app_name),
                                response.getString("message")
                            )

                        }
                    }
                } catch (e: Exception) {
                    utils.showCustomAlert(
                        activity,
                        Utilities.ALERT_WARNING,
                        resources.getString(R.string.app_name),
                        resources.getString(R.string.something_went_wrong)
                    )
                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                if (customDialog != null && customDialog!!.isShowing) customDialog!!.hideDialog()
                try {
                    enableButton!!.toggleState()
                    var json: String? = null
                    var Message: String
                    val response = error.networkResponse
                    if (response != null && response.data != null) {
                        try {
                            val errorObj = JSONObject(String(response.data))
                            if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                                try {
                                    displayMessage(errorObj.optString("error"))
                                } catch (e: Exception) {
                                    displayMessage(getString(R.string.something_went_wrong))
                                }
                            } else if (response.statusCode == 401) {
                                try {
                                    if (errorObj.optString("message")
                                            .equals("invalid_token", ignoreCase = true)
                                    ) {
                                        //Call Refresh token
                                    } else {
                                        displayMessage(errorObj.optString("message"))
                                    }
                                } catch (e: Exception) {
                                    displayMessage(getString(R.string.something_went_wrong))
                                }
                            } else if (response.statusCode == 422) {
                                json = NTApplication.trimMessage(String(response.data))
                                if (json !== "" && json != null) {
                                    if (json.startsWith("The email")) {
                                    }
                                    displayMessage(json)
                                } else {
                                    displayMessage(getString(R.string.please_try_again))
                                }
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
                            StartStreetRide()
                        }
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
                println("DriverAPI Ride Token: " + "Bearer " + getKey(activity!!, "access_token"))
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    private fun CalculateApproxFare() {
        val url = (URLHelper.STREETRIDE_FARE
                + "?s_latitude=" + current_latitude
                + "&s_longitude=" + current_longitude
                + "&d_latitude=" + lastDLatitude
                + "&d_longitude=" + lastDLongitude)
        Utilities.PrintAPI_URL(url, "GET")
        val `object` = JSONObject()
        val jsonObjectRequest: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET, URLHelper.STREETRIDE_FARE
                    + "?s_latitude=" + current_latitude
                    + "&s_longitude=" + current_longitude
                    + "&d_latitude=" + lastDLatitude
                    + "&d_longitude=" + lastDLongitude,
            `object`, object : Response.Listener<JSONObject?> {
                override fun onResponse(response: JSONObject?) {
                    Utilities.printAPI_Response(response.toString())
                    if (response != null) {
                        run {
                            try {
                                if (response.getString("success") == "1") {
                                    val estimated_fare = response.optString("estimated_fare")
                                    val distance = response.optString("distance")
                                    val distance_unit = response.optString("distance_unit")
                                    val time = response.optString("time")
                                    val tax_price = response.optString("tax_price")
                                    // String base_price = response.optString("base_price");
//                        String booking_fare = response.optString("booking_fare");
                                    // String sro_levy = response.optString("sro_levy");
                                    putKey(
                                        activity!!,
                                        "distance",
                                        response.optString("distance")
                                    )
                                    sr_app_fare!!.text = """Estimated Fare : ${
                                        getKey(
                                            activity!!,
                                            "currency"
                                        )
                                    }$estimated_fare
Distance : $distance $distance_unit
Time : $time"""
                                } else {
                                    utils.showCustomAlert(
                                        activity,
                                        Utilities.ALERT_WARNING,
                                        resources.getString(R.string.app_name),
                                        response.getString("message")
                                    )
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            }, Response.ErrorListener { error ->
                var json: String? = null
                var Message: String
                val response = error.networkResponse
                if (response != null && response.data != null) {
                    try {
                        val errorObj = JSONObject(String(response.data))
                        displayMessage(response.statusCode.toString() + errorObj.optString("error") + errorObj.toString())
                        if (response.statusCode == 400 || response.statusCode == 405 || response.statusCode == 500) {
                            try {
                                displayMessage(errorObj.optString("message"))
                            } catch (e: Exception) {
                                displayMessage(getString(R.string.something_went_wrong))
                            }
                        } else if (response.statusCode == 401) {
                            displayMessage(getString(R.string.something_went_wrong))
                        } else if (response.statusCode == 422) {
                            json = NTApplication.trimMessage(String(response.data))
                            if (json !== "" && json != null) {
                                displayMessage(json)
                            } else {
                                displayMessage(getString(R.string.please_try_again))
                            }
                        } else if (response.statusCode == 503) {
                            displayMessage(getString(R.string.server_down))
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
                        CalculateApproxFare()
                    }
                }
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["X-localization"] = getKey(activity!!, "lang")!!
                headers["X-Requested-With"] = "XMLHttpRequest"
                headers["Authorization"] = "Bearer " + getKey(activity!!, "access_token")
                println(
                    "DriverAPI Fare Token: " + "Bearer " + getKey(
                        activity!!,
                        "access_token"
                    )
                )
                return headers
            }
        }
        NTApplication.getInstance().addToRequestQueue(jsonObjectRequest)
    }

    fun pop() {
//        val fm = getActivity()!!.supportFragmentManager
//        val count = fm.backStackEntryCount
//        for (i in 0..count) {
//            fm.popBackStackImmediate()
//        }
        // MainActivity.navController!!.navigate(R.id.fragmentHome)
        putKey(activity!!, "street_ride_status", "street_ride")
        val mainIntent = Intent(getActivity()!!, MainActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(mainIntent)
    }


    fun SearchAlert(desc: String) {
        val builder =
            androidx.appcompat.app.AlertDialog.Builder(requireActivity(), R.style.DialogTheme)
        // Get the layout inflater
        val inflater =
            requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.fragment_search, null)
        builder.setCancelable(false)
        builder.setView(layout)
        builder.create()
        alertDialog = builder.create()
        alertDialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val wmlp: WindowManager.LayoutParams = alertDialog!!.getWindow()!!.getAttributes()
        wmlp.gravity = Gravity.TOP
        val search_places = layout.findViewById<View>(R.id.search_places) as EditText
        search_places.requestFocus()
        search_places.setEnabled(true)

        val img_exit = layout.findViewById<View>(R.id.img_exit) as ImageView


        search_place_recyclerView =
            layout.findViewById<View>(R.id.search_place_recyclerView) as RecyclerView
        ll_errorLayout = layout.findViewById<View>(R.id.ll_errorLayout) as LinearLayout



        img_exit.setOnClickListener {
            alertDialog?.dismiss()
            sr_droploc!!.isEnabled = true
            stop1address!!.isEnabled = true
            stop2address!!.isEnabled = true


        }
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
                        search_place_recyclerView!!.visibility = View.GONE
                        //  getPlaces(search_places.text.toString(), desc)
                        // ll_errorLayout?.setVisibility(View.GONE)
                    } else {
                        if (search_places.text.toString().length > 3) {
                            search_place_recyclerView!!.visibility = View.VISIBLE
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
        alertDialog!!.getWindow()!!.setSoftInputMode(
            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE
        );
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
                        search_place_recyclerView!!.visibility = View.VISIBLE
                        ll_errorLayout!!.visibility = View.GONE
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
                        search_place_recyclerView!!.visibility = View.VISIBLE
                        //ll_errorLayout!!.visibility = View.GONE
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
                        search_place_recyclerView!!.visibility = View.GONE
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
                sr_droploc!!.isEnabled = true
                stop2address!!.isEnabled = true
                stop1address!!.isEnabled = true

                try {

                    val result = response.getJSONObject("result")

                    val place_array = result.getJSONArray("address_components")

                    val geometry = result.getJSONObject("geometry").getJSONObject("location")
                    val lat = geometry.getString("lat")
                    val lng = geometry.getString("lng")
                    val latlng = LatLng(lat.toDouble(), lng.toDouble())

                    if (desc.equals("stop1", ignoreCase = true)) {
                        stop1address!!.setText(address)
                        stop1_address = address

                        stop1_lat = lat
                        stop1_lng = lng
                        stop1LatLng = LatLng(stop1_lat.toDouble(), stop1_lng.toDouble())
                        putKey(activity!!, "stop1_address", stop1_address)
                        putKey(activity!!, "stop1_latitude", stop1_lat)
                        putKey(activity!!, "stop1_longitude", stop1_lng)
                    } else if (desc.equals("stop2", ignoreCase = true)) {
                        stop2address!!.setText(address)
                        stop2_address = address

                        stop2_lat = lat
                        stop2_lng = lng
                        stop2LatLng = LatLng(stop2_lat.toDouble(), stop2_lng.toDouble())
                        putKey(activity!!, "stop2_address", stop2_address)
                        putKey(activity!!, "stop2_latitude", stop2_lat)
                        putKey(activity!!, "stop2_longitude", stop2_lng)
                    } else {

                        sr_droploc!!.text = address
                        lastDLatitude = lat.toDouble()
                        lastDLongitude = lng.toDouble()
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
        val view = requireActivity().currentFocus
        if (view != null) {
            val inputManager =
                requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputManager.hideSoftInputFromWindow(
                view.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
        /*  InputMethodManager in = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        in.hideSoftInputFromWindow(activity.getCurrentFocus().getApplicationWindowToken(), 0);*/
    }


    fun showPathFromPickupToDrop(srclat: Double, srcLng: Double) {
//        sourceLatLng = currentLatLng;
        sourceLatLng = LatLng(srclat, srcLng)
        destLatLng = LatLng(lastDLatitude, lastDLongitude)
        if (getKey(activity!!, "stop1_latitude").equals("")) {
            stop1LatLng = LatLng(0.0, 0.0)
        } else {
            stop1LatLng = LatLng(
                getKey(activity!!, "stop1_latitude")!!.toDouble(),
                getKey(activity!!, "stop1_longitude")!!.toDouble()
            )
        }
        if (getKey(activity!!, "stop2_latitude")!!.equals("")) {
            stop2LatLng = LatLng(0.0, 0.0)
        } else {
            stop2LatLng = LatLng(
                getKey(activity!!, "stop2_latitude")!!.toDouble(),
                getKey(activity!!, "stop2_longitude")!!.toDouble()
            )
        }

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
        var obj = JSONObject(data)
        Log.d("ParserTask", "ttttt" + obj.optString("status"))
        // Toast.makeText(context!!,"t"+obj.optString("status"),Toast.LENGTH_SHORT).show()


        if (obj.optString("status").equals("OK")) {
            return data
        } else {
            return ""
        }

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
            results = result
            // Traversing through all the routes
            try {
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
                    lineOptions!!.addAll(points!!)
                }
            } catch (e: NullPointerException) {
                e.printStackTrace()
                path = null
                points = null
            }
            if (lineOptions != null && points != null) {
                StartStreetRide()
            } else {
                Log.d("onPostExecute", "without Polylines drawn")
                utils.showCustomAlert(
                    activity,
                    Utilities.ALERT_WARNING,
                    resources.getString(R.string.app_name),
                    resources.getString(R.string.no_avaliable_service)
                )

            }
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
        val key = "key=" + resources.getString(R.string.google_map_api)
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
}