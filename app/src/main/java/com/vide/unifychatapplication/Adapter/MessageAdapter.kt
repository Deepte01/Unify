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
//import kotlinx.android.synthetic.main.chatpane_left.view.*
//import kotlinx.android.synthetic.main.chatpane_right.view.*

// This adapter is used for bidning the list of chats with the layout

class MessageAdapter(val context: MessageActivity, val chatList: ArrayList<ChatInfo>): RecyclerView.Adapter<MessageViewHolder>(){

    private val MSG_TYPE_LEFT:Int =0
    private val MSG_TYPE_RIGHT:Int =1
    var  mAuth: FirebaseAuth= FirebaseAuth.getInstance()
    var currentUserPhno:String?=null

    init {
        Log.d("MessageAdapter","MessageAdapter called")
        checkPhoneumberInFirebase(mAuth!!.currentUser!!.uid)
    }

    //return the size of the chatList array list
    override fun getItemCount(): Int {
        //return the count of list elements
        return chatList.size
    }

    //set the type of the layout used for displaying the chat messages
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        // we need to create a view which can be used to show a row
        val layoutInflater= LayoutInflater.from(parent.context)
        val cellForRow:View

        Log.d("MessageAdapter", "type view rt -> $viewType")
        if(viewType==MSG_TYPE_LEFT) {
            // if the message is from sender, assign left chatpane layout
            cellForRow= layoutInflater.inflate(R.layout.chatpane_left, parent, false)
            Log.d("MessageAdapter", "type view right side")
            return MessageViewHolder(cellForRow)
        }
        else
        {
            // if the message is from receiver, assign right chatpane layout
            cellForRow= layoutInflater.inflate(R.layout.chatpane_right, parent, false)
            Log.d("MessageAdapter", "type view left side")
            return MessageViewHolder(cellForRow)
        }

    }

    //set the view holder to the MessageViewHolder class
    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        // bind the content to display for each row
        val chat=chatList[position]
        holder.setData(chat)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    //check if the current user is a sender or a receiver and return MSG_TYPE
    override fun getItemViewType(position: Int): Int {
        //mAuth=FirebaseAuth.getInstance()
        var currentUser= mAuth!!.currentUser
        //checkPhoneumberInFirebase(currentUser!!.uid)
        Log.d("MessageAdapter","value of cur ph.no $currentUserPhno")
        //check if the current user is a sender or a receiver
        if(chatList.get(position).getSender().equals(currentUserPhno))
        {
            Log.d("MessageAdapter","current User is sender ${chatList.get(position).getSender()}")
        return MSG_TYPE_RIGHT
        }
        else {
            Log.d("MessageAdapter","current user is receiver")
            return MSG_TYPE_LEFT
        }
    }


    //get the phone number of the current user from "Users" branch
    private fun checkPhoneumberInFirebase(userId:String) {


        var dbRef = FirebaseDatabase.getInstance().getReference("Users")
        //fetch the current user's phone number using the function below
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (data in dataSnapshot.getChildren()) {
                    if (data.key.toString().equals(userId)) {
                        assignCurrentUserPhno(data.child("phno").getValue().toString())
                        break
                    }
                }
            }
            override fun onCancelled(p0: DatabaseError) {

                //Log.d("DB","DB Error $p0")
            }
        })
    }

    private fun assignCurrentUserPhno(phno:String)
    {
        currentUserPhno=phno
    }

}


class MessageViewHolder(val v: View): RecyclerView.ViewHolder(v) {

    var chatMessage:TextView?=null
    fun setData(chat: ChatInfo?)
    {
        //Log.d("CustomViewRow","${contact!!.contactName}")
        //v.chat_message.text=chat!!.getMessage()!!
        chatMessage=v.findViewById(R.id.chat_message)
        chatMessage!!.text=chat!!.getMessage()

    }
}

