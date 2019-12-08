package com.vide.unifychatapplication

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.viewphonecontacts.*

class ContactsFragment: Fragment {
    private val REQ_CONTACT_CODE:Int=100
    private val PICK_CONTACT_CODE:Int=101
    lateinit var listofItems:ArrayList<ContactInfo>
    var contactsAdapter:PhoneContactsAdapter? =null
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

         var inflate=inflater!!.inflate(R.layout.viewphonecontacts,container,false)

         contactsAdapter=PhoneContactsAdapter(this,listofItems)
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


    private fun checkPermission()
    {
        if(Build.VERSION.SDK_INT>=23)
        {
            if(ActivityCompat.checkSelfPermission(activity!!,android.Manifest.permission.READ_CONTACTS)!=
                PackageManager.PERMISSION_GRANTED)
            {
                //request for the permission to grant the access to the contacts
                requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS),REQ_CONTACT_CODE)

            }
        }
        Log.d("FetchContacts","Permission Already Granted ")
        getContacts()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode)
        {
            REQ_CONTACT_CODE ->{
                if(grantResults[0]== PackageManager.PERMISSION_GRANTED)
                {
                    // getContacts()// pickContact()
                    Log.d("FetchContacts","Permission Granted in result")
                    checkPermission()
                }
                else{
                    Toast.makeText(activity,"Cannot access phone contacts", Toast.LENGTH_LONG).show()
                }
            }
            else ->{
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }

    }

    private fun getContacts() {
        val cr = activity!!.contentResolver
        val cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)

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
                                checkContactInFirebase(phoneNo,name,id)
                    }
                    pCur.close()
                }
            }
        }
        cur?.close()
    }

    private fun checkContactInFirebase(phno:String,name:String,id:String) {

        var status:Int=0
        var test:Boolean?= null
        Log.d("DB","in func")
        var dbRef = FirebaseDatabase.getInstance().getReference("Users")
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

                //Log.d("DB","DB Error $p0")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (data in dataSnapshot.getChildren()) {
                    // Log.d("DB"," data exists $data")
                    if (data.child("phno").exists()) {
                        var temp ="+"+phno.replace("""[-, ,(,)]""".toRegex(), "")
                        if(data.child("phno").value!!.equals(temp)) {

                            var contactinfo=ContactInfo(phno,name,id.toInt())
                            listofItems.add(contactinfo)
                            Log.d("DB","inside $status data change ${data.child("phno").value} .. $temp")
                            contactsAdapter!!.notifyDataSetChanged()
                        }
                    }
                    else {
                        Log.d("DB","inside else on data change $phno")

                    }
                }

            }

        })
    }

}