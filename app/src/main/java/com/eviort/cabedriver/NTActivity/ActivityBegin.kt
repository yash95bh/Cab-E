package com.eviort.cabedriver.NTActivity

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eviort.cabedriver.NTCustomView.NTButtonBold
import com.eviort.cabedriver.NTHelper.SharedHelper
import com.eviort.cabedriver.NTUtilites.NetworkUtil
import com.eviort.cabedriver.NTUtilites.Utilities
import com.eviort.cabedriver.R

class ActivityBegin : AppCompatActivity() {
    //var btn_english: NTButton? = null
    //var btn_swahali: NTButton? = null
    var mainLayout_activity_begin: FrameLayout? = null

    //var mainLayout_activity_begin1: FrameLayout? = null
    var btn_signin: NTButtonBold? = null
    var btn_signup: NTButtonBold? = null
    var context: Context = this@ActivityBegin
    var errorLayout: LinearLayout? = null
    var mainLayout: FrameLayout? = null
    var utils = Utilities()
    private val UPDATE_INTERVAL = (10 * 1000).toLong()
    private val FASTEST_INTERVAL: Long = 2000
    var msg: String? = null
    var connectivityReceiver: BroadcastReceiver? = null

    //987654325

    override fun onCreate(savedInstanceState: Bundle?) {
        Utilities.setLocale(context!!, SharedHelper.getKey(context!!, "lang"))
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_begin)

        checkPermission()
        AssignFindViewById()

        if (NetworkUtil.getConnectivityStatus(this@ActivityBegin) > 0) {
        } else {
            showErrorLayout("INTERNET")
        }

        btn_signin!!.setOnClickListener {
            val signInIntent = Intent(this@ActivityBegin, ActivitySignIn::class.java)
            startActivity(signInIntent)
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }

        btn_signup!!.setOnClickListener {
            //SharedHelper.putKey(context, "access_token"," ")
            val signInIntent = Intent(this@ActivityBegin, ActivitySignUp::class.java)
            startActivity(signInIntent)
            overridePendingTransition(R.anim.slide_in, R.anim.slide_out)
        }

        /* btn_english!!.setOnClickListener {
             mainLayout_activity_begin!!.visibility=View.VISIBLE
             mainLayout_activity_begin1!!.visibility=View.GONE

         }
         btn_swahali!!.setOnClickListener {
             mainLayout_activity_begin!!.visibility=View.VISIBLE
             mainLayout_activity_begin1!!.visibility=View.GONE

         }*/
    }

    private fun AssignFindViewById() {
        /*btn_english = findViewById<View>(R.id.btn_english) as NTButton
        btn_swahali = findViewById<View>(R.id.btn_swahali) as NTButton*/
        btn_signup = findViewById<View>(R.id.btn_signup) as NTButtonBold
        btn_signin = findViewById<View>(R.id.btn_signin) as NTButtonBold
        mainLayout_activity_begin =
            findViewById<View>(R.id.mainLayout_activity_begin) as FrameLayout

        //mainLayout_activity_begin1 = findViewById<View>(R.id.mainLayout_activity_begin1) as FrameLayout
        mainLayout = findViewById<View>(R.id.mainLayout_activity_begin) as FrameLayout
        errorLayout = findViewById<View>(R.id.network_error_activity_begin) as LinearLayout
    }

    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                val intent = Intent(
                    Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:$packageName")
                )
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE)
            }
        }
    }

    fun showMainLayout() {

    }

    fun showErrorLayout(type: String) {
        if (type.equals("INTERNET", ignoreCase = true)) {
            Utilities.displayNetworkerror(this@ActivityBegin, errorLayout, Utilities.ENABLE_NETWORK)
        }
        errorLayout!!.visibility = View.VISIBLE
        mainLayout!!.visibility = View.GONE
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
//        btn_signup!!.background = resources.getDrawable(R.drawable.rounded_button_primary)
//        btn_signup!!.setTextColor(resources.getColor(R.color.white))
//        btn_signin!!.background = resources.getDrawable(R.drawable.rounded_button_primary)
//        btn_signin!!.setTextColor(resources.getColor(R.color.white))
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE")
        connectivityReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val cm = context
                    .getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork = cm.activeNetworkInfo
                val isConnected = (activeNetwork != null
                        && activeNetwork.isConnectedOrConnecting)
                if (!isConnected) {
                    showErrorLayout("INTERNET")
                } else {
                    showMainLayout()
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(
                connectivityReceiver, intentFilter,
                Context.RECEIVER_EXPORTED
            )
        } else {
            registerReceiver(connectivityReceiver, intentFilter)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(connectivityReceiver)
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

    companion object {
        var ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469
    }
}