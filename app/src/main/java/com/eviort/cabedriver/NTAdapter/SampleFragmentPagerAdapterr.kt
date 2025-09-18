package com.eviort.cabedriver.NTAdapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.navigation.Navigation
import androidx.viewpager.widget.ViewPager
import com.eviort.cabedriver.NTFragment.FragmentAccepted
import com.eviort.cabedriver.NTFragment.FragmentCompleted
import com.eviort.cabedriver.NTFragment.FragmentOffer
import com.eviort.cabedriver.R

class SampleFragmentPagerAdapterr     //    public CharSequence getPageTitle(int position) {
//        // Generate title based on item position
//        return tabTitles[position];
//    }
(private val context: Context, private val tabcount: Int, supportFragmentManager: FragmentManager?) : FragmentStatePagerAdapter(
        supportFragmentManager!!
) {
    private val viewPager: ViewPager? = null
    var gotoOffer = Navigation.createNavigateOnClickListener(R.id.action_fragmentHistory_to_fragmentOffer)
    override fun getCount(): Int {
        return tabcount
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> FragmentOffer()
            1 -> FragmentAccepted()
            2 -> FragmentCompleted()
            else -> FragmentOffer()
        }
    } //    @Override
}