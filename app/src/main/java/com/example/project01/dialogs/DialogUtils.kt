package com.example.project01.dialogs

import android.content.Context
import androidx.appcompat.app.AlertDialog

object DialogUtils {

    fun loginSuccessDialog(context: Context, onDismiss: (() -> Unit)? = null) {
        AlertDialog.Builder(context)
            .setTitle("Login Successful")
            .setMessage("Welcome")
            .setPositiveButton("OK") { dialog, _ ->
                onDismiss?.invoke()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }

    fun loginFailureDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Login Failed")
            .setMessage("Incorrect email or password. Please try again.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

    }

     fun ChangegePassSuccessDialog(context: Context,onDismiss: (() -> Unit)? = null) {
        AlertDialog.Builder(context)
            .setTitle("Congratulations!!!")
            .setMessage("Your Password Change Successfully")
            .setPositiveButton("OK") { dialog, _ ->
                onDismiss?.invoke()
                dialog.dismiss()
            }
            .show()
    }

    fun ChangePassFailureDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("Failed")
            .setMessage("Please Enter Write Password")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

    }


}