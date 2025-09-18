package com.eviort.cabedriver.NTFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.eviort.cabedriver.R

/**
 * A simple [Fragment] subclass.
 */
class FragmentFAQ : Fragment() {
    var backArrow: ImageView? = null
    var rootview: View? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_faq, container, false)
        findViewById()
        backArrow!!.setOnClickListener { activity!!.onBackPressed() }
        return rootview
    }

    private fun findViewById() {
        backArrow = rootview!!.findViewById<View>(R.id.backArrow) as ImageView
    }
}