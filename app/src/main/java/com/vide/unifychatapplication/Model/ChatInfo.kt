package com.vide.unifychatapplication.Model

import android.content.BroadcastReceiver

/*
This is a datastructure used for creating chats in firebase database
 */
class ChatInfo{
    private lateinit var sender:String
    private lateinit var receiver: String
    private lateinit var message:String

    constructor(sender:String, receiver: String,  message:String)
    {
        this.sender=sender
        this.receiver=receiver
        this.message=message
    }

    constructor()
    {

    }
    fun getSender():String
    {
        return this.sender
    }
    fun getReceiver():String
    {
        return this.receiver
    }
    fun getMessage():String
    {
        return this.message
    }
}