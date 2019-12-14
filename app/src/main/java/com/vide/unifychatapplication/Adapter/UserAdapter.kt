package com.vide.unifychatapplication.Adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.vide.unifychatapplication.Fragments.ChatFragment
import com.vide.unifychatapplication.MessageActivity
import com.vide.unifychatapplication.Model.ChatInfo
import com.vide.unifychatapplication.Model.UserInfo
import com.vide.unifychatapplication.R
import kotlinx.android.synthetic.main.row_contact_info.view.*

// This is used for creating user's list on the chats fragment, which has a recycler view
class UserAdapter(val context: ChatFragment, val userChatList: ArrayList<UserInfo>): RecyclerView.Adapter<UserViewHolder>(){

    init {
        Log.d("UserAdapter","User Adapter called")
    }

    override fun getItemCount(): Int {
        //return the count of list elements
        return userChatList.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        // we need to create a view which can in which the user infromation can be shown

        val layoutInflater= LayoutInflater.from(parent.context)
        val cellForRow= layoutInflater.inflate(R.layout.row_contact_info,parent,false)
        Log.d("UserAdapter","in side on create view holder")
        return UserViewHolder(cellForRow)

    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        // bind the content to display for each row
        val chat=userChatList[position]
        holder.setData(chat)

        holder.itemView.setOnClickListener{
            // when a user is cliked in the chat fragment, that particular chat should open
            var intent= Intent(it.context, MessageActivity::class.java)
            intent.putExtra("userName",chat.getUsername())
            intent.putExtra("phno",chat.getPhno())
            it.context.startActivity(intent)
        }
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

}


class UserViewHolder(val v: View): RecyclerView.ViewHolder(v) {

    //var chatMessage:TextView?=null
    fun setData(chat: UserInfo?)
    {
        // set a row of the recycler view with contact name and phone number
        v.contactNameTxt.text=chat!!.getUsername()
        v.phoneNoTxt.text=""

    }
}

