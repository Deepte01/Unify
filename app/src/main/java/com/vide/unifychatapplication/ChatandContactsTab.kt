package com.vide.unifychatapplication

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.vide.unifychatapplication.Adapter.ViewPagerAdapter
import com.vide.unifychatapplication.Fragments.ChatFragment
import com.vide.unifychatapplication.Fragments.ContactsFragment

class ChatandContactsTab : AppCompatActivity() {

    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null
    lateinit var  toolbar: Toolbar
    var mAuth=FirebaseAuth.getInstance()

    //this activity is used for displaying seperate tabs for contacts and chats
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chatand_contacts_tab)

        toolbar= findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayShowTitleEnabled(false)



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
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.chatand_contacts_navigation, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.action_logout -> {
                mAuth.signOut()
                val intent=Intent(this, PhoneAuthentication::class.java)
                //val intent=Intent(this, FetchPhoneContacts::class.java)
                intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                Toast.makeText(this,"Successfully Signed OUt!!", Toast.LENGTH_LONG).show()
                startActivity(intent)
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


}
