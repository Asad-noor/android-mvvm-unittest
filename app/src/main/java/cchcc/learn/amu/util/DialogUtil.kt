package cchcc.learn.amu.util

import android.app.Activity
import android.content.DialogInterface
import androidx.appcompat.app.AlertDialog


fun Activity.showAlert() {
    val alertDialog = AlertDialog.Builder(this).create()
    alertDialog.setTitle("Permission Rationale")
    alertDialog.setMessage("Show detail alert message about why this permission is needed.")
    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
        DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
    alertDialog.show()
}