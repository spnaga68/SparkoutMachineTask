package com.imtamila.sparkoutmachinetask.interfaces

import android.content.DialogInterface

interface DialogOnClickInterface {
    fun onPositiveButtonCLick(dialog: DialogInterface, alertType: Int)
    fun onNegativeButtonCLick(dialog: DialogInterface, alertType: Int)
}