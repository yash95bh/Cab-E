package com.eviort.cabedriver.NTFragment

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.eviort.cabedriver.NTHelper.ConnectionHelper
import com.eviort.cabedriver.R

class FragmentReward : Fragment() {
    var activity: Activity? = null

    // var context: Context? = null
    var txt_see_all: TextView? = null
    var driver_wallet_label: TextView? = null
    var txt_earnings_click_here: TextView? = null
    var isInternet: Boolean? = null
    var rootView: View? = null
    var helper: ConnectionHelper? = null
    var ic_dropdown: ImageView? = null
    var ll_earnings_detail: LinearLayout? = null
    var ll_earnings: LinearLayout? = null
    var lnrtimeperiod: LinearLayout? = null
    var ll_earnings_detail_all: LinearLayout? = null

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
        rootView = inflater.inflate(R.layout.fragment_rewards, container, false)
        return rootView
    }
}