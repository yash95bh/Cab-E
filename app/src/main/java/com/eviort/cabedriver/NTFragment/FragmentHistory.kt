package com.eviort.cabedriver.NTFragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.Navigation
import androidx.viewpager.widget.ViewPager
import com.eviort.cabedriver.NTActivity.MainActivity
import com.eviort.cabedriver.NTAdapter.SampleFragmentPagerAdapterr
import com.eviort.cabedriver.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener

/**
 * A simple [Fragment] subclass.
 */
class FragmentHistory : Fragment(), View.OnClickListener {
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null

    // var context: Context? = null
    var strTag: String? = null
    var backArrow: ImageView? = null
    var rootview: View? = null
    var dashBoard: FrameLayout? = null
    var pagerAdapter: SampleFragmentPagerAdapterr? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        rootview = inflater.inflate(R.layout.fragment_history, container, false)
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
        // context = activity
        AssignmentFindViewById(rootview)
        FragmentHome.isRunning = false
        backArrow = rootview?.findViewById<View>(R.id.backArrow) as ImageView
        backArrow!!.setOnClickListener(this)
        val navController = Navigation.findNavController(activity!!, R.id.navHostFragment)

        // tabTitles = new String[]{getString(R.string.offered), getString(R.string.accepted),getString(R.string.past_trip)};


        // tabLayout.addTab(tabLayout.newTab().setText(R.string.offered));
        // tabLayout.addTab(tabLayout.newTab().setText(R.string.accepted));
        tabLayout!!.addTab(tabLayout!!.newTab().setText(R.string.past_trip))
        tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL
        tabLayout!!.tabMode = TabLayout.MODE_FIXED

        // ha = new Handler();
        //Handlerfunction();
        val frag: Fragment = FragmentPastTrips()
        val ft = activity!!.supportFragmentManager.beginTransaction()
        ft.replace(R.id.dashBoard, frag)
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
        ft.commit()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            pagerAdapter = SampleFragmentPagerAdapterr(activity!!, tabLayout!!.tabCount, (activity as AppCompatActivity?)!!.supportFragmentManager)
            viewPager!!.adapter = pagerAdapter
        }

//        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
//
//                tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//            }
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//            }
//        });
//
        if (tabLayout!!.tabCount > 0) {
            viewPager!!.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))
        } else {
            Toast.makeText(activity, "Tab Count Error ", Toast.LENGTH_SHORT).show()
        }
        tabLayout!!.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                viewPager!!.setCurrentItem(tab.position, true)
                println("Tab Position:" + tab.position)
                var fragment: Fragment? = null
                fragment = when (tab.position) {
                    0 -> FragmentPastTrips()
                    1 -> FragmentAccepted()
                    2 -> FragmentCompleted()
                    else -> FragmentPastTrips()
                }
                val ft = activity!!.supportFragmentManager.beginTransaction()
                ft.replace(R.id.dashBoard, fragment)
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                ft.commit()
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {
                viewPager!!.setCurrentItem(tab.position, true)
                println("Tab Position:" + tab.position)
                var fragment: Fragment? = null
                fragment = when (tab.position) {
                    0 -> FragmentPastTrips()
                    1 -> FragmentAccepted()
                    2 -> FragmentCompleted()
                    else -> FragmentPastTrips()
                }
                val ft = activity!!.supportFragmentManager.beginTransaction()
                ft.replace(R.id.dashBoard, fragment!!)
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                ft.commit()
            }
        })
        return rootview
    }

    private fun AssignmentFindViewById(rootview: View?) {
        tabLayout = rootview!!.findViewById<View>(R.id.sliding_tabs) as TabLayout
        viewPager = rootview.findViewById<View>(R.id.viewpager) as ViewPager
        dashBoard = rootview.findViewById<View>(R.id.dashBoard) as FrameLayout

        //tabLayout.setupWithViewPager(viewPager);
    }

    override fun onClick(v: View) {
        val id = v.id
        when (id) {
            R.id.backArrow -> pop()
        }
    }


    fun pop() {
       /* val fm = activity!!.supportFragmentManager
        val count = fm.backStackEntryCount
        for (i in 0..count) {
            fm.popBackStackImmediate()
        }*/
        val mainIntent = Intent(getActivity()!!, MainActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(mainIntent)
    }
}