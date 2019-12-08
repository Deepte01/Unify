package com.vide.unifychatapplication
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_contact_info.view.*

class PhoneContactsAdapter(val context: ContactsFragment, val contacts: ArrayList<ContactInfo>): RecyclerView.Adapter<CustomViewHolder>(){

    init {
        Log.d("CustomViewRow","PhoneContactsAdapter called")
    }

    override fun getItemCount(): Int {
        //return the count of list elements
        return contacts.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        // we need to create a view which can be used to show a row

        val layoutInflater= LayoutInflater.from(parent.context)
        val cellForRow= layoutInflater.inflate(R.layout.row_contact_info,parent,false)
        Log.d("CustomViewRow","in side on create view holder")
        return CustomViewHolder(cellForRow)

    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        // bind the content to display for each row
        val contact=contacts[position]
        holder.setData(contact)
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


}

class CustomViewHolder(val v:View):RecyclerView.ViewHolder(v) {
    //assign values to the Contact view
    fun setData(contact:ContactInfo?)
    {
        Log.d("CustomViewRow","${contact!!.contactName}")
        v.contactNameTxt.text=contact!!.contactName
        v.phoneNoTxt.text=contact!!.phoneNumber.toString()
    }

}
