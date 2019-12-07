package com.vide.unifychatapplication
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.row_contact_info.view.*

class PhoneContactsAdapter(val context:Context, val contacts: ArrayList<ContactInfo>): RecyclerView.Adapter<CustomViewHolder>(){

    init {
        setHasStableIds(true)
    }


    override fun getItemCount(): Int {
        //return the count of list elements
        return contacts.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        // we need to create a view which can be used to show a row

        val layoutInflater= LayoutInflater.from(parent.context)
        val cellForRow= layoutInflater.inflate(R.layout.row_contact_info,parent,false)

        return CustomViewHolder(cellForRow)

    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        // bind the content to display for each row
        holder
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
        v.contactNameTxt.text=contact!!.contactName
        v.phoneNoTxt.text=contact!!.phoneNumber.toString()
    }

}
