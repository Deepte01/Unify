package com.vide.unifychatapplication.Adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/*
This class has two fragments i.e chat and contacts.
Each fragmet will have a recycler view
This is used for creating tab layout
 */
class ViewPagerAdapter(private val myContext: Context, fm: FragmentManager, private var totalTabs: Int) : FragmentPagerAdapter(fm) {

     var listofFrgaments= ArrayList<Fragment>()
    var listofTitles= ArrayList<String>()
    // this is for fragment tabs
    override fun getItem(position: Int): Fragment {
        return listofFrgaments[position]
    }

    // this counts total number of tabs
    override fun getCount(): Int {
        return listofFrgaments.size
    }

    fun AddFragment(frag: Fragment,title:String)
    {
        listofFrgaments.add(frag)
        listofTitles.add(title)

    }
}
