package com.vide.unifychatapplication.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vide.unifychatapplication.MessageActivity
import com.vide.unifychatapplication.Model.ChatInfo
import com.vide.unifychatapplication.R


class MessageAdapter(val context: MessageActivity, val chatList: ArrayList<ChatInfo>): RecyclerView.Adapter<MessageViewHolder>(){

    private val MSG_TYPE_LEFT:Int =0
    private val MSG_TYPE_RIGHT:Int =1
    lateinit var  mAuth: FirebaseAuth
    var currentUserPhno:String?=null

    init {
        Log.d("MessageAdapter","MessageAdapter called")
    }

    override fun getItemCount(): Int {
        //return the count of list elements
        return chatList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        // we need to create a view which can be used to show a row
        val layoutInflater= LayoutInflater.from(parent.context)
        val cellForRow:View

        Log.d("MessageAdapter", "type view rt -> $viewType")
        if(viewType==MSG_TYPE_LEFT) {
            cellForRow= layoutInflater.inflate(R.layout.chatpane_left, parent, false)
            Log.d("MessageAdapter", "type view right side")
            return MessageViewHolder(cellForRow)
        }
        else
        {
            cellForRow= layoutInflater.inflate(R.layout.chatpane_right, parent, false)
            Log.d("MessageAdapter", "type view left side")
            return MessageViewHolder(cellForRow)
        }

    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        // bind the content to display for each row
        val chat=chatList[position]
        holder.textmessage!!.text=chat.getMessage()

    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        mAuth=FirebaseAuth.getInstance()
        var currentUser= mAuth!!.currentUser
        checkPhoneumberInFirebase(currentUser!!.uid)
        Log.d("MessageAdapter","value of cur ph.no $currentUserPhno")
        if(chatList.get(position).getSender().equals(currentUserPhno))
        {
        return MSG_TYPE_LEFT
        }
        else
        return MSG_TYPE_RIGHT
    }

    private fun checkPhoneumberInFirebase(userId:String) {


        var dbRef = FirebaseDatabase.getInstance().getReference("Users")
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.getChildren()) {
                    if (data.key.toString().equals(userId)) {
                        currentUserPhno=data.child("phno").toString()
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {

                //Log.d("DB","DB Error $p0")
            }
        })
    }

}


class MessageViewHolder(val v: View): RecyclerView.ViewHolder(v) {
    //assign values to the chat view
    var textmessage: TextView? =null
   init {
       textmessage= v!!.findViewById(R.id.text_message)
   }

}

