package com.vide.unifychatapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class ChatandContactsTab : AppCompatActivity() {

    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null

    //this activity is used for displaying tabs for contacts and chats
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatand_contacts_tab)

        tabLayout = findViewById<TabLayout>(R.id.tablayout_id)
        viewPager = findViewById<ViewPager>(R.id.viewpager_id)
        Log.d("FragmentTabs","Inside Chat and contacts tab")

        //tabLayout!!.addTab(tabLayout!!.newTab().setText("Chats"))
        //tabLayout!!.addTab(tabLayout!!.newTab().setText("Contacts"))
        tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = ViewPagerAdapter(this, supportFragmentManager, tabLayout!!.tabCount)
        adapter.AddFragment(ChatFragment(),"Chats")
        adapter.AddFragment(ContactsFragment(),"Contacts")

        viewPager!!.adapter = adapter

        tabLayout!!.setupWithViewPager(viewPager)

        viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        tabLayout!!.getTabAt(0)!!.setIcon(R.drawable.ic_chat)
        tabLayout!!.getTabAt(1)!!.setIcon(R.drawable.ic_phone)

    }
}
