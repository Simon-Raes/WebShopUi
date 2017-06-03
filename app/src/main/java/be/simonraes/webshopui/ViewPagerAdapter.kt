package be.simonraes.webshopui

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

/**
 * Created by SimonRaes on 27/05/17.
 */
class ViewPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    override fun getItem(position: Int) = PagerFragment()

    override fun getCount() = 20
}