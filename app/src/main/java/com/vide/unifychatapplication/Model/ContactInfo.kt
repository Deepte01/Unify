package com.vide.unifychatapplication.Model


/*
This is a datastructure used for creating users in firebase database
 */
class ContactInfo {
    var phoneNumber: String? = null
    var contactName: String? = null
    var contactId:Int?=null
    //var itemImage: Uri? = null

    constructor(phoneNumber: String, contactName: String, contactId:Int) {
        this.phoneNumber = phoneNumber
        this.contactName = contactName
        this.contactId = contactId
    }
}
