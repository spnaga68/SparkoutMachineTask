package com.imtamila.sparkoutmachinetask.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.imtamila.sparkoutmachinetask.R
import com.imtamila.sparkoutmachinetask.interfaces.DialogOnClickInterface

object CommonAlertDialog {

    /**
     * Function to create and show alert dialog
     *
     * @param context - Context using which dialog will created and showed
     * @param dialogOnClickInterface - Common interface for positive and negative button click
     * @param alertMessage - Message to be displayed in alert dialog
     * @param positiveButtonText - Positive button text where default value is OK
     * @param negativeButtonText - Negative button text where default value is Cancel; If value is empty next button won't be shown
     * @param isCancelable - Boolean which determines the alert dialog is cancelable or not; default value is true
     * @param alertType - Integer value using which positive button and negative button click will be handled
     * @param alertTitle - Title for alert dialog; default value is empty, if value is empty means title won't be shown
     *
     * @return AlertDialog
     */
    fun alertDialog(
        context: Context,
        dialogOnClickInterface: DialogOnClickInterface,
        alertMessage: String,
        positiveButtonText: String = context.resources.getString(R.string.ok),
        negativeButtonText: String = context.resources.getString(R.string.cancel),
        isCancelable: Boolean = true,
        alertType: Int = 0,
        alertTitle: String = ""
    ): AlertDialog {
        val builder = AlertDialog.Builder(context)
        builder.apply {
            if (alertTitle.isNotEmpty())
                setTitle(alertTitle)

            setMessage(alertMessage)
            setCancelable(isCancelable)
            if (positiveButtonText.isEmpty()) {
                setPositiveButton(R.string.ok) { dialog, id ->
                    dialogOnClickInterface.onPositiveButtonCLick(dialog, alertType)
                }
            } else {
                setPositiveButton(positiveButtonText) { dialog, id ->
                    dialogOnClickInterface.onPositiveButtonCLick(dialog, alertType)
                }
            }
            if (negativeButtonText.isNotEmpty()) {
                setNegativeButton(negativeButtonText) { dialog, id ->
                    dialogOnClickInterface.onNegativeButtonCLick(dialog, alertType)
                }
            }
        }
        val alertDialog = builder.create()
        alertDialog.show()
        return alertDialog
    }
}