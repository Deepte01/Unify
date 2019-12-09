package com.vide.unifychatapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*


class MessageActivity : AppCompatActivity() {

    lateinit var  contactName:TextView
    lateinit var fuser:FirebaseAuth
    lateinit var dbRef:DatabaseReference
    lateinit var  toolbar: Toolbar
    lateinit var profile_image:ImageView
    lateinit var sendButton:ImageButton
    lateinit var typedMessage:EditText
    lateinit var receiverPhoneNumber:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)
        toolbar= findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title=""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener{
            finish()
        }
        fuser= FirebaseAuth.getInstance()
        contactName= findViewById(R.id.username)
        profile_image= findViewById(R.id.profile_image)
        sendButton= findViewById(R.id.sent_button)
        typedMessage= findViewById(R.id.text_message)

        contactName.text=intent!!.getStringExtra("userName")
        Log.d("MessageActivity","userID:  ")
        receiverPhoneNumber=intent!!.getStringExtra("phno")
        profile_image.setImageResource(R.mipmap.ic_launcher)

        //getPhoneNumber()

        sendButton.setOnClickListener{
            var message:String= typedMessage.text.toString()
            if(!message.equals(""))
            {

                getPhoneNumber(fuser!!.currentUser!!.uid,receiverPhoneNumber,message)
            }
            else
            {
                Toast.makeText(this,"The message cannot be empty",Toast.LENGTH_SHORT).show()
            }
            typedMessage.setText("")
        }

    }

    fun sendMessage(sender:String,receiver: String, message: String)
    {
        dbRef=FirebaseDatabase.getInstance().reference

        var hashMap= mutableMapOf<String,String>()
        hashMap.put("sender",sender!!)
        hashMap.put("receiver",receiver)
        hashMap.put("message",message)

        dbRef.child("Chats").push().setValue(hashMap)

    }
    fun getPhoneNumber(senderUid:String,receiverPhoneNumber:String,message:String)
    {
        var dbRef = FirebaseDatabase.getInstance().getReference("Users")
        Log.d("MessageActivity","in side getphone numer}")

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

                //Log.d("DB","DB Error $p0")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (data in dataSnapshot.children) {
                        Log.d("MessageActivity","uid ${data.value}")
                    if(data.key.toString().equals(senderUid))
                    {
                    //Log.d("MessageActivity","child1 ${data.key.toString()}")
                        var phno:String=data.child("phno").value.toString()
                    Log.d("MessageActivity","child2 ${data.child("phno").value}")
                        sendMessage(phno,receiverPhoneNumber,message)
                    }
                }
            }
        })
        Log.d("MessageActivity","final fetch ph.no")
    }
}
