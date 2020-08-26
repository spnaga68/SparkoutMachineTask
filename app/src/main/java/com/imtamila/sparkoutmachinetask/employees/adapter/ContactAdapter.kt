package com.imtamila.sparkoutmachinetask.employees.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.imtamila.sparkoutmachinetask.databinding.ContactsListItemBinding
import com.imtamila.sparkoutmachinetask.employees.data.ContactData
import com.imtamila.sparkoutmachinetask.employees.interfaces.OnFavoriteChanged

const val ADAPTER_TYPE_CONTACT_LIST = 0
const val ADAPTER_TYPE_FAVORITES_LIST = 1

class ContactAdapter(
    private val context: Context,
    val listener: OnFavoriteChanged,
    val adapterType: Int = ADAPTER_TYPE_CONTACT_LIST
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var contactsList: ArrayList<ContactData>? = null

    fun submitList(contactsList: ArrayList<ContactData>) {
        this.contactsList = contactsList
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, position: Int): RecyclerView.ViewHolder {
        val binding =
            ContactsListItemBinding.inflate(LayoutInflater.from(context), viewGroup, false)
        return DataViewHolder(binding)
    }


    override fun getItemCount(): Int = contactsList?.size ?: 0


    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        contactsList?.get(position)?.let {
            with(viewHolder as DataViewHolder) {
                bind(it, position)
                itemView.tag = it
            }
        }
    }

    inner class DataViewHolder(private val contactsListItemBinding: ContactsListItemBinding) :
        RecyclerView.ViewHolder(contactsListItemBinding.root) {
        fun bind(
            contactData: ContactData,
            position: Int
        ) {
            with(contactsListItemBinding) {
                this.contactData = contactData
                executePendingBindings()
                ivFavouriteImage.isActivated = contactData.isFavourite == 1
                if (adapterType == ADAPTER_TYPE_CONTACT_LIST) {
                    ivFavouriteImage.setOnClickListener {
                        if (ivFavouriteImage.tag == null) {
                            ivFavouriteImage.tag = "selected"
                            ivFavouriteImage.isActivated = true

                            listener.onFavoriteChanged(contactData, true)
                            contactsList?.set(position,
                                contactData.apply { isFavourite = 1 })
                        } else {
                            ivFavouriteImage.tag = null
                            ivFavouriteImage.isActivated = false
                            listener.onFavoriteChanged(contactData, false)
                            contactsList?.set(position,
                                contactData.apply { isFavourite = 0 })
                        }
                    }
                } else ivFavouriteImage.setOnClickListener(null)
            }
        }
    }
}