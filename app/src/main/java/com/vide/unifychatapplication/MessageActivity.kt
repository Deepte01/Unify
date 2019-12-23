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

/*
This class is used for displaying the sender and receiver chats.
This class uses a layout which has a recycler view
 */

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

        //get the layout and label the action bar with user name of the contact
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

        //assign the recycler view adapter
        //create a list for displaying chats
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

        /*when the message is typed and hit the send button by the user
         the message should be added to the database.
         */
        sendButton.setOnClickListener{
            //  var message:String= typedMessage.text.toString()
            typedMessage= text_message.text.toString()
            if(!typedMessage.equals(""))
            {
                Log.d("MessageActivity","inside sent btton ${fuser!!.currentUser!!.uid}, ${receiverPhoneNumber},$typedMessage")
                //get the current phone number of the user
                getPhoneNumber(fuser!!.currentUser!!.uid,receiverPhoneNumber,typedMessage)
                text_message.setText("")
            }
            else
            {
                Toast.makeText(this,"The message cannot be empty",Toast.LENGTH_SHORT).show()
            }
        }

        // getPhoneNumber(fuser!!.currentUser!!.uid,receiverPhoneNumber,typedMessage.toString())
        //constantly read the messages present in the database
        readMessages(fuser!!.currentUser!!.uid, receiverPhoneNumber)

    }

    //create a hashmap and insert the data in to database
    fun sendMessage(sender:String,receiver: String, message: String)
    {
        dbRef=FirebaseDatabase.getInstance().reference

        //create a hashmap to add the sender, receiver and message info to the database
        var hashMap= mutableMapOf<String,String>()
        hashMap.put("sender",sender!!)
        hashMap.put("receiver",receiver)
        hashMap.put("message",message)

        dbRef.child("Chats").push().setValue(hashMap)

    }

    //this is a asynchronous process so, call sendMessage from this method.
    private fun getPhoneNumber(senderUid:String,receiverPhoneNumber:String,message:String)
    {
        //this method is used for fetching the phone number of the current user
        var dbRef = FirebaseDatabase.getInstance().getReference("Users")
        Log.d("MessageActivity","in side getphone numer}")

        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onCancelled(p0: DatabaseError) {

                //Log.d("DB","DB Error $p0")
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (data in dataSnapshot.children) {
                    Log.d("MessageActivity","uid ${senderUid}, data-key ${data.key.toString()}")

                    //check the uid of the current user in the list of database users
                    if(data.key.toString().equals(senderUid))
                    {
                        //Log.d("MessageActivity","child1 ${data.key.toString()}")
                        var phno:String=data.child("phno").value.toString()

                        //set the global variable phone number to phno value using a function
                        currentPhoneNumber(phno)
                        Log.d("MessageActivity","child2 ${data.child("phno").value}")
                        //call method to insert the sender, reciever and message into database
                        sendMessage(phno,receiverPhoneNumber,message)
                    }
                }
            }
        })
        Log.d("MessageActivity","final fetch ph.no")
    }
    fun currentPhoneNumber(phno:String)
    {
        //use this method for assigning the current user's phone number to the global variable.
        currentPhoneNumber=phno
    }



    //to read messages frm the chats branch of the database, first check the phone number of the current user
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
                        //phone number is found, now call the method to read the messages
                        Log.d("MessageActivity","my phone num -> ${myPhno}, rev phone num ->${userPhno}")
                        saveMyPhoneNumber(myPhno,userPhno)
                    }
                }
            }
        })
        Log.d("MessageActivity","final fetch ph.no")
    }

    //this method reads the gets the chat from "Chats" database and converts each chat into a Chat object
    //the chat object is added to the array list, to display sequence of messages
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