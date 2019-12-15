package com.vide.unifychatapplication

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.vide.unifychatapplication.Adapter.ViewPagerAdapter
import com.vide.unifychatapplication.Fragments.ChatFragment
import com.vide.unifychatapplication.Fragments.ContactsFragment

class ChatandContactsTab : AppCompatActivity() {

    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null

    //this activity is used for displaying seperate tabs for contacts and chats
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatand_contacts_tab)
        var mAuth=FirebaseAuth.getInstance()

        tabLayout = findViewById<TabLayout>(R.id.tablayout_id)
        viewPager = findViewById<ViewPager>(R.id.viewpager_id)
        Log.d("FragmentTabs","Inside Chat and contacts tab")

        //tabLayout!!.addTab(tabLayout!!.newTab().setText("Chats"))
        //tabLayout!!.addTab(tabLayout!!.newTab().setText("Contacts"))
        tabLayout!!.tabGravity = TabLayout.GRAVITY_FILL

        val adapter = ViewPagerAdapter(
            this,
            supportFragmentManager,
            tabLayout!!.tabCount
        )
        var callChatsFrag:ContactsFragment= ContactsFragment()
        var bundle:Bundle= Bundle()
        bundle.putString("uid",mAuth.uid)
        callChatsFrag.arguments=bundle

        // add chat fragments and contact fragments to the tabs which are recyler views
        adapter.AddFragment(ChatFragment(),"Chats")
        adapter.AddFragment(ContactsFragment(),"Contacts")

        viewPager!!.adapter = adapter

        tabLayout!!.setupWithViewPager(viewPager)

        viewPager!!.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))

        //assigning icons to display the type pf tabs
        tabLayout!!.getTabAt(0)!!.setIcon(R.drawable.ic_chat)
        tabLayout!!.getTabAt(1)!!.setIcon(R.drawable.ic_phone)

    }
}
