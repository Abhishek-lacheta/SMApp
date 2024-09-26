package com.example.project01.dialogs

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AlertDialog
import com.example.project01.activity.LoginActivity

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

     fun signupSuccessDialog(context: Context, onDismiss: (() -> Unit)? = null) {
        AlertDialog.Builder(context)
            .setTitle("SignUp Successful")
            .setMessage("Welcome")
            .setPositiveButton("OK") { dialog, _ ->
                onDismiss?.invoke()
                dialog.dismiss()
            }
            .show()
    }

    fun signupFailureDialog(context: Context) {
        AlertDialog.Builder(context)
            .setTitle("SignUp Failed")
            .setMessage("Incorrect email or password. Please try again.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }


    fun ChangegePassSuccessDialog(context: Context, onDismiss: (() -> Unit)? = null) {
        AlertDialog.Builder(context)
            .setTitle("Congratulations!!!")
            .setMessage("Your Password Change Successfully")
            .setPositiveButton("OK") { dialog, _ ->
                onDismiss?.invoke()
                dialog.dismiss()
            }
            .show()
    }
    fun ForgotSuccessDialog(context: Context, onDismiss: (() -> Unit)? = null) {
        AlertDialog.Builder(context)
            .setTitle("Please check your email")
            .setMessage("Provide you link to reset your password")
            .setPositiveButton("OK") { dialog, _ ->
                onDismiss?.invoke()
                dialog.dismiss()
            }
            .show()
    }

    fun LogoutConfirmationDialog(context: Context, onDismiss: (() -> Unit)? = null) {
        AlertDialog.Builder(context)
            .setTitle("Confirm Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { dialog, _ ->
                onDismiss?.invoke()
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ -> dialog.dismiss() }
            .create()
            .show()
    }
}
