package com.eviort.cabedriver.NTActivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.eviort.cabedriver.NTFragment.FragmentPersonalProfile
import com.eviort.cabedriver.R
import com.google.android.material.tabs.TabLayout

class ActivityProfile : AppCompatActivity() {
    var context: Context = this@ActivityProfile
    var activity: Activity = this@ActivityProfile
    var backArrow: ImageView? = null
    private var viewPager: ViewPager? = null
    private var tabLayout: TabLayout? = null
    private var tabTitles: Array<String>? = null
    var strTag: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        viewPager = findViewById<View>(R.id.viewpager) as ViewPager
        tabLayout = findViewById<View>(R.id.sliding_tabs) as TabLayout
        tabLayout!!.setupWithViewPager(viewPager)
        backArrow = findViewById<View>(R.id.backArrow) as ImageView
        backArrow!!.setOnClickListener { GoToMainActivity() }
        strTag = intent.extras!!.getString("tag")
        tabTitles = arrayOf("")
        viewPager!!.adapter = SampleFragmentPagerAdapter(tabTitles!!, supportFragmentManager,
                this)
        /*
        if (strTag != null) {
            if (strTag.equalsIgnoreCase("personal")) {*/viewPager!!.currentItem = 0
        /*     }
        }*/
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    inner class SampleFragmentPagerAdapter(private val tabTitles: Array<String>, fm: FragmentManager?, private val context: Context) : FragmentPagerAdapter(
            fm!!
    ) {
        val PAGE_COUNT = 1
        override fun getCount(): Int {
            return PAGE_COUNT
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> {
                    val fragment = FragmentPersonalProfile()
                    if (strTag == "edit") {
                        val args = Bundle()
                        args.putString("edit", "edit")
                        fragment.arguments = args
                        fragment
                    } else {
                        fragment
                    }
                }
                else -> FragmentPersonalProfile()
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            // Generate title based on item position
            return tabTitles[position]
        }
    }

    fun GoToMainActivity() {
        val mainIntent = Intent(activity, MainActivity::class.java)
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(mainIntent)
        activity.finish()
    }

    companion object {
        private const val TAG = "EditProfile"
    }
}