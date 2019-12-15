package com.vide.unifychatapplication.Fragments

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vide.unifychatapplication.Adapter.PhoneContactsAdapter
import com.vide.unifychatapplication.Model.ContactInfo
import com.vide.unifychatapplication.R

/*
 This class is used to display all the contacts from the phone
 which exists in the firebase database
 */
class ContactsFragment: Fragment {
    private val REQ_CONTACT_CODE:Int=100
    private val PICK_CONTACT_CODE:Int=101
    lateinit var listofItems:ArrayList<ContactInfo>
    var contactsAdapter: PhoneContactsAdapter? =null
    lateinit var mAuth: FirebaseAuth
    lateinit var myRecyclerView:RecyclerView

    constructor()
    {

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setContentView(R.layout.viewphonecontacts)
        mAuth = FirebaseAuth.getInstance()
        listofItems=ArrayList<ContactInfo>()
        Log.d("CustomViewRow","inside fragment oncreate")
    }

     override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
         Log.d("CustomViewRow","inside fragment oncreate view")

         //assign the adapter and layout to recycler view
         var inflate=inflater!!.inflate(R.layout.viewphonecontacts,container,false)

         contactsAdapter=
             PhoneContactsAdapter(this, listofItems)
         myRecyclerView= inflate.findViewById(R.id.contacts_recyclerView)
         myRecyclerView.layoutManager= LinearLayoutManager(activity)
         myRecyclerView.adapter=contactsAdapter
         return inflate
     }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d("CustomViewRow","inside fragment onActivityCreated view")
        checkPermission()
    }


    //check if the chat is having permission to read the contacts
    private fun checkPermission()
    {
        Log.d("FetchContacts","inside checkPermission")

        if(Build.VERSION.SDK_INT>=23)
        {
            if(ContextCompat.checkSelfPermission(activity!!,Manifest.permission.READ_CONTACTS)!=
                PackageManager.PERMISSION_GRANTED)
            {
                Log.d("FetchContacts","seeking permission")
                //request for the permission to grant the access to the contacts
                requestPermissions(arrayOf(Manifest.permission.READ_CONTACTS),REQ_CONTACT_CODE)

            }
        }
        else{
            //getContacts()
        }
        getContacts()
        Log.d("FetchContacts","Permission Already Granted ")

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
       // super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("FetchContacts","onRequestPermissionsResult")
        when(requestCode)
        {
            REQ_CONTACT_CODE ->{
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {
                    // getContacts()// pickContact()
                    //permission is granted to read the contacts
                    //Log.d("FetchContacts","Permission Granted in result")
                    Log.d("FetchContacts","permission result: Granted")
                    getContacts()
                }
                else{
                    Log.d("FetchContacts","permission result: Denied")
                    Toast.makeText(activity,"Cannot access phone contacts", Toast.LENGTH_LONG).show()
                }
            }
            else ->{
                //getContacts()

                Log.d("FetchContacts","permission result: Denied")
            }
        }

        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
       // getContacts()
    }

    //get the contacts from phone
     fun getContacts() {
        val cr = activity!!.contentResolver
//        Log.d("FetchContacts"," query ${cr!!.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)}")
        val cur = cr!!.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)

        if (cur!!.count > 0) {
            while (cur != null && cur.moveToNext()) {
                val id = cur.getString(
                    cur.getColumnIndex(ContactsContract.Contacts._ID)
                )
                val name = cur.getString(
                    cur.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                    )
                )

                if (cur.getInt(
                        cur.getColumnIndex(
                            ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0
                ) {
                    val pCur = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id), null
                    )
                    if (pCur!!.moveToNext()) {
                        val phoneNo = pCur.getString(
                            pCur.getColumnIndex(
                                ContactsContract.CommonDataKinds.Phone.NUMBER))

                        //check if fetched contact is in firebase
                                checkContactInFirebase(phoneNo,name,id)
                    }
                    pCur.close()
                }
            }
        }
        cur?.close()
    }

    //check if the contacts from pone are present in the database
    private fun checkContactInFirebase(phno:String,name:String,id:String) {

        var temp:String
        Log.d("FetchContacts","in func check contacts from firebase")
        var dbRef = FirebaseDatabase.getInstance().getReference("Users")
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

                //Log.d("DB","DB Error $p0")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (data in dataSnapshot.getChildren()) {
                    // Log.d("DB"," data exists $data")
                    if (data.child("phno").exists()) {
                        //check the format of the phone numer and reset it to +1 format
                        if(phno.startsWith("+1"))
                        {
                            temp =phno.replace("""[-, ,(,)]""".toRegex(), "")
                        }
                        else if(phno.startsWith("1"))
                        {
                            temp ="+"+phno.replace("""[-, ,(,)]""".toRegex(), "")
                        }
                        else if(phno.startsWith("+91"))
                        {
                            temp =phno.replace("""[-, ,(,)]""".toRegex(), "")
                        }
                        else
                        {
                            temp="+1"+phno.replace("""[-, ,(,)]""".toRegex(), "")
                        }

                        Log.d("FetchContacts","comparing ${data.child("phno").value} .. $temp")
                        if(data.child("phno").value!!.equals(temp)) {

                            var contactinfo=
                                ContactInfo(
                                    temp,
                                    name,
                                    id.toInt()
                                )
                            // if the contact is matched add it to the list
                            listofItems.add(contactinfo)
                            Log.d("FetchContacts","inside status data change ${data.child("phno").value} .. $temp")
                            contactsAdapter!!.notifyDataSetChanged()
                        }
                    }
                    else {
                        Log.d("FetchContacts","inside else on data change $phno")

                    }
                }

            }

        })
    }

}