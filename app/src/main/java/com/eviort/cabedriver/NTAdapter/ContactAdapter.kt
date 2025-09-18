package com.eviort.cabedriver.NTAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.eviort.cabedriver.NTModel.EmergencyContactData
import com.eviort.cabedriver.R

class ContactAdapter(private val contactList: List<EmergencyContactData>) : RecyclerView.Adapter<ContactAdapter.MyViewHolder>() {
    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        // each data item is just a string in this case
        var tvName: TextView
        var tvPhNo: TextView

        init {
            tvName = view.findViewById<View>(R.id.ec_name) as TextView
            tvPhNo = view.findViewById<View>(R.id.ec_phoneno) as TextView
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.emergency_contact_list, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val emergencyContact = contactList[position]
        holder.tvName.text = emergencyContact.name
        holder.tvPhNo.text = emergencyContact.number
    }

    override fun getItemCount(): Int {
        return contactList.size
    }


}