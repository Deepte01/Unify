package com.vide.unifychatapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.vide.unifychatapplication.Adapter.MessageAdapter
import com.vide.unifychatapplication.Model.ChatInfo
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_message.*


class MessageActivity : AppCompatActivity() {

    lateinit var  contactName:TextView
    lateinit var fuser:FirebaseAuth
    lateinit var dbRef:DatabaseReference
    lateinit var  toolbar: Toolbar
    lateinit var profile_image:ImageView
    lateinit var sendButton:ImageButton
    lateinit var typedMessage:String
    lateinit var receiverPhoneNumber:String

    lateinit var currentPhoneNumber:String
   // lateinit var inputMessage:String

    //declaring the message adapter

    lateinit var messageAdapter:MessageAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var chatList: ArrayList<ChatInfo>

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

//        Log.d("MessageActivity"," typed message: ${typedMessage}")
        //inputMessage=typedMessage.text.toString()

        chatList= ArrayList<ChatInfo>()
        messageAdapter= MessageAdapter(this,chatList)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager= LinearLayoutManager(this)
        recyclerView.adapter=messageAdapter
        recyclerView.smoothScrollToPosition(messageAdapter.itemCount)
        recyclerView.setHasFixedSize(true)

        contactName.text=intent!!.getStringExtra("userName")
        Log.d("MessageActivity","userID:  ")
        receiverPhoneNumber=intent!!.getStringExtra("phno")
        profile_image.setImageResource(R.mipmap.ic_launcher)

        //getPhoneNumber()

        sendButton.setOnClickListener{
          //  var message:String= typedMessage.text.toString()
            typedMessage= text_message.text.toString()
            if(!typedMessage.equals(""))
            {
                Log.d("MessageActivity","inside sent btton ${fuser!!.currentUser!!.uid}, ${receiverPhoneNumber},$typedMessage")
                getPhoneNumber(fuser!!.currentUser!!.uid,receiverPhoneNumber,typedMessage)
                text_message.setText("")
            }
            else
            {
                Toast.makeText(this,"The message cannot be empty",Toast.LENGTH_SHORT).show()
            }
        }

       // getPhoneNumber(fuser!!.currentUser!!.uid,receiverPhoneNumber,typedMessage.toString())
        readMessages(fuser!!.currentUser!!.uid, receiverPhoneNumber)

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
   private fun getPhoneNumber(senderUid:String,receiverPhoneNumber:String,message:String)
    {
        var dbRef = FirebaseDatabase.getInstance().getReference("Users")
        Log.d("MessageActivity","in side getphone numer}")

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

                //Log.d("DB","DB Error $p0")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (data in dataSnapshot.children) {
                        Log.d("MessageActivity","uid ${senderUid}, data-key ${data.key.toString()}")
                    if(data.key.toString().equals(senderUid))
                    {
                    //Log.d("MessageActivity","child1 ${data.key.toString()}")
                        var phno:String=data.child("phno").value.toString()
                        currentPhoneNumber(phno)
                    Log.d("MessageActivity","child2 ${data.child("phno").value}")
                        sendMessage(phno,receiverPhoneNumber,message)
                    }
                }
            }
        })
        Log.d("MessageActivity","final fetch ph.no")
    }
    fun currentPhoneNumber(phno:String)
    {
        currentPhoneNumber=phno
    }



     fun readMessages(senderUid:String,userPhno:String)
    {
        var dbRef = FirebaseDatabase.getInstance().getReference("Users")
        Log.d("MessageActivity","in side getphone numer}")


        dbRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

                //Log.d("DB","DB Error $p0")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                chatList.clear()

                var myPhno:String=""
                for (data in dataSnapshot.children) {
                    Log.d("MessageActivity","data keu in read ${data.key.toString()}")
                    if(data.key.toString().equals(senderUid))
                    {
                        //Log.d("MessageActivity","child1 ${data.key.toString()}")
                        myPhno=data.child("phno").value.toString()
                        Log.d("MessageActivity","my phone num -> ${myPhno}")
                        saveMyPhoneNumber(myPhno,userPhno)
                    }
                }
            }
        })
        Log.d("MessageActivity","final fetch ph.no")
    }
    fun saveMyPhoneNumber(myPhno:String,userPhno:String)
    {
        currentPhoneNumber=myPhno
        dbRef=FirebaseDatabase.getInstance().getReference("Chats")

        dbRef.addValueEventListener(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                chatList.clear()
                var chat:ChatInfo?=null
                //typedMessage= text_message.text.toString()
                //Log.d("MessageActivity","typed message is: ${typedMessage}")
                for (data in dataSnapshot.children) {
                    //var chat:ChatInfo= data.getValue() as ChatInfo
                    if((data.child("sender").getValue()!!.equals(myPhno) && data.child("receiver").getValue()!!.equals(userPhno)))
                    {
                        chat=ChatInfo(myPhno,userPhno,data.child("message").getValue().toString())
                        Log.d("MessageActivity","chat Object $chat")
                        chatList.add(chat)
                        messageAdapter.notifyDataSetChanged()
                    }
                    else if(data.child("sender").getValue()!!.equals(userPhno) && data.child("receiver").getValue()!!.equals(myPhno))
                    {
                         chat=ChatInfo(userPhno,myPhno,data.child("message").getValue().toString())
                        Log.d("MessageActivity","chat Object $chat")
                        chatList.add(chat)
                        messageAdapter.notifyDataSetChanged()
                    }

                }
            }
        })

    }
}
