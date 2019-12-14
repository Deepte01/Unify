package com.vide.unifychatapplication.Model

import android.R.id

/*
This is a datastructure used for retrieving the users from database
 */

class UserInfo{
    private var userId:String?=null
    private var username:String?=null
    private var phno:String?=null


    constructor(userId:String, username:String,phno:String)
    {
        this.userId=userId
        this.username=username
        this.phno=phno

    }
    constructor()
    {

    }

    fun getuserId(): String {
        return userId!!
    }


    fun getUsername(): String {
        return username!!
    }


    fun getPhno(): String {
        return phno!!
    }

}
