package com.imtamila.sparkoutmachinetask.employees.interfaces

import com.imtamila.sparkoutmachinetask.employees.data.ContactData

interface OnFavoriteChanged {
    fun onFavoriteChanged(contactData: ContactData, isFavorite: Boolean)
}