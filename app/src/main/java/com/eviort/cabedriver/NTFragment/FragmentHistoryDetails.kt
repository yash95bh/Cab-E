package com.eviort.cabedriver.NTFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.eviort.cabedriver.R

/**
 * A simple [Fragment] subclass.
 */
class FragmentHistoryDetails : Fragment() {
    var backArrow: ImageView? = null
    var lnrInvoice: LinearLayout? = null
    var btnViewInvoice: Button? = null
    var btnCall: Button? = null
    var goToFragmentInvoice = Navigation.createNavigateOnClickListener(R.id.action_fragmentHistoryDetails_to_fragmentInvoice)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        backArrow = view.findViewById<View>(R.id.backArrow) as ImageView
        btnViewInvoice = view.findViewById<View>(R.id.btnViewInvoice) as Button
        btnViewInvoice!!.setOnClickListener(goToFragmentInvoice)
        backArrow!!.setOnClickListener { activity!!.onBackPressed() }
    }
}