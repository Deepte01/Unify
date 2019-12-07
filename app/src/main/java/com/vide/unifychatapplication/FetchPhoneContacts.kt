package com.vide.unifychatapplication

import android.app.Activity
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.vide.unifychatapplication.ContactInfo
import com.vide.unifychatapplication.R
import kotlinx.android.synthetic.main.phonecontactstest.*
import kotlinx.android.synthetic.main.viewphonecontacts.*

class FetchPhoneContacts : AppCompatActivity() {

    private val REQ_CONTACT_CODE:Int=100
    private val PICK_CONTACT_CODE:Int=101
    lateinit var listofItems:ArrayList<ContactInfo>
    var contactsAdapter:PhoneContactsAdapter? =null
    lateinit var mAuth: FirebaseAuth


    var builder = StringBuilder()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.phonecontactstest)
        setContentView(R.layout.viewphonecontacts)
        mAuth = FirebaseAuth.getInstance()

        listofItems=ArrayList<ContactInfo>()
        contactsAdapter=PhoneContactsAdapter(this,listofItems)

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

    private fun getContacts(): StringBuilder {
       val builder = StringBuilder()
       val resolver: ContentResolver = contentResolver;
       val cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null,
           null)

       if (cursor!!.count > 0) {
           while (cursor.moveToNext()) {

               val id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
               val name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))
               val phoneNumber = (cursor.getString(
                   cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))).toInt()

               if (phoneNumber > 0) {
                   val cursorPhone = contentResolver.query(
                       ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                       null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", arrayOf(id), null)

                   if(cursorPhone!!.count > 0) {
                       while (cursorPhone.moveToNext()) {
                           val phoneNumValue = cursorPhone.getString(
                               cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                           builder.append("Contact: ").append(name).append(", Phone Number: ").append(
                               phoneNumValue).append("\n\n")
                           Log.e("Name ===>",phoneNumValue)
                           var contactinfo=ContactInfo(phoneNumValue,name,id.toInt())
                           listofItems.add(contactinfo)
                       }
                   }
                   cursorPhone.close()
               }

               //Log.d("FetchContacts:","Loaded: $builder")
           }
           contactsAdapter!!.notifyDataSetChanged()

       }
       else {
         Toast.makeText(this,"No contacts Available!!",Toast.LENGTH_SHORT).show()
       }
       cursor.close()
       return builder
   }


}
