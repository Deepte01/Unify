package com.vide.unifychatapplication

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vide.unifychatapplication.Adapter.PhoneContactsAdapter
import com.vide.unifychatapplication.Model.ContactInfo
import kotlinx.android.synthetic.main.viewphonecontacts.*


class FetchPhoneContacts : AppCompatActivity() {

    private val REQ_CONTACT_CODE:Int=100
    private val PICK_CONTACT_CODE:Int=101
    lateinit var listofItems:ArrayList<ContactInfo>
    var contactsAdapter: PhoneContactsAdapter? =null
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.viewphonecontacts)
        mAuth = FirebaseAuth.getInstance()

        listofItems=ArrayList<ContactInfo>()
      //  contactsAdapter=PhoneContactsAdapter(this,listofItems)

        contacts_recyclerView.layoutManager= LinearLayoutManager(this)
        contacts_recyclerView.adapter=contactsAdapter
        checkPermission()
    }
    override fun onStart() {
        super.onStart()
        if(mAuth.currentUser==null)
        {
            val intent=Intent(this, PhoneAuthentication::class.java)
            intent.flags=Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)

        }
        else{
            Toast.makeText(this,"Already Signed In!!",Toast.LENGTH_LONG).show()
        }
    }

    private fun checkPermission()
    {
        if(Build.VERSION.SDK_INT>=23)
        {
            if(ActivityCompat.checkSelfPermission(this,android.Manifest.permission.READ_CONTACTS)!=
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
                if(grantResults[0]==PackageManager.PERMISSION_GRANTED)
                {
                   // getContacts()// pickContact()
                    Log.d("Fetch Contacts","Permission Granted in result")
                    checkPermission()
                }
                else{
                    Toast.makeText(this,"Cannot access phone contacts",Toast.LENGTH_LONG).show()
                }
            }
            else ->{
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }

    }

    private fun getContacts() {
        val cr = contentResolver
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
//                        if(checkContactInFirebase(phoneNo))
//                        {
                                checkContactInFirebase(phoneNo,name,id)

//                            var contactinfo=ContactInfo(phoneNo,name,id.toInt())
//                            listofItems.add(contactinfo)
//                        }


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
//        var query= dbRef.orderByChild("phno")
//            .equalTo(phno.replace("""[-, ,(,)]""".toRegex(), ""))

      //  Log.d("DB","DB ${query}")
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
                                var contactinfo= ContactInfo(
                                    phno,
                                    name,
                                    id.toInt()
                                )
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
