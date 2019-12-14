package com.vide.unifychatapplication.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.auth.UserInfo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vide.unifychatapplication.Adapter.PhoneContactsAdapter
import com.vide.unifychatapplication.Adapter.UserAdapter
import com.vide.unifychatapplication.Model.ChatInfo
import com.vide.unifychatapplication.Model.ContactInfo
import com.vide.unifychatapplication.Model.UserInfo
import com.vide.unifychatapplication.R


class ChatFragment: Fragment {
     constructor()
    {

    }
    init {
        mAuth= FirebaseAuth.getInstance()
        dbRef= FirebaseDatabase.getInstance()
      //  getPhoneNumber(mAuth.currentUser!!.uid)
    }

    lateinit var listofUsers:ArrayList<String>
    var userAdapter: UserAdapter? =null
    lateinit var mAuth:FirebaseAuth
    lateinit var myRecyclerView: RecyclerView
    lateinit var dbRef:FirebaseDatabase
    lateinit var currentPhoneNumber:String
    lateinit var mUserInfo: ArrayList<UserInfo>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        /*
        Assign the recycler view adapter and create a list of UserInfo objects
        to display in the chat Tab.
         */
        super.onCreateView(inflater, container, savedInstanceState)
        var inflate=inflater!!.inflate(R.layout.chat_fragment,container,false)

        //var bundle: Bundle? =this!!.arguments
        //var uid= bundle!!.getString("uid")

        mUserInfo= ArrayList<UserInfo>()
        userAdapter=UserAdapter(this, mUserInfo)

        myRecyclerView= inflate.findViewById(R.id.chats_recyclerView)
        myRecyclerView.layoutManager= LinearLayoutManager(activity)
        myRecyclerView.adapter=userAdapter

        Log.d("ChatFragmentmAuthcuruser", "${mAuth.currentUser}")


        listofUsers=ArrayList<String>()
        getPhoneNumber(mAuth.currentUser!!.uid)
        return inflate
    }

    /* read all the chats present in the database to decide who is the sender and
    who is the receiver
     */
    private  fun readChats()
    {
        dbRef.getReference("Users").addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

                //Log.d("DB","DB Error $p0")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                    mUserInfo.clear()
               // Log.d("CharFragment","list of receivers: ${listofUsers}")

                for (data in dataSnapshot.children) {
                    var userinfo:UserInfo?= data.getValue(UserInfo::class.java)

                    for(num in listofUsers)
                    {
                        if(userinfo!!.getPhno().equals(num))
                        {
                            mUserInfo.add(userinfo)
                        }

                    }
                }
                userAdapter!!.notifyDataSetChanged()
            }
        })

    }

    //get the phone number of the current user
    private fun getPhoneNumber(senderUid:String)
    {

        Log.d("ChatFragment","in side getphone numer}")

        dbRef.getReference("Users").addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

                //Log.d("DB","DB Error $p0")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (data in dataSnapshot.children) {
                    Log.d("ChatFragment","uid ${senderUid}, data-key ${data.key.toString()}")
                    if(data.key.toString().equals(senderUid))
                    {
                        var phno:String=data.child("phno").value.toString()
                        currentPhoneNumber=phno
                        Log.d("ChatFragment","child ${data.child("phno").value}")
                    }
                }
                getChatUsers()
            }
        })
    }

    //get list of users with whom the current user had a chat

    private fun getChatUsers()
    {
        Log.d("ChatFragment","getting chat users list!")
        Log.d("currentphnonumber: ","${currentPhoneNumber}")
        dbRef.getReference("Chats").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(dataSnapshot: DataSnapshot) {
           /// listofUsers.clear()
                for(data in dataSnapshot.children)
                {
                    Log.d("sender: ","${data.child("sender").getValue().toString()}")
                    Log.d("receiver: ","${data.child("receiver").getValue().toString()}")
                    val reciever:String= data.child("receiver").getValue().toString()
                    if(!listofUsers.contains(reciever)){
                    listofUsers.add(data.child("receiver").getValue().toString())
                    }
                }
                readChats()

            }

        })
    }

}