package com.cracks.tictactoe

import android.app.Activity
import android.app.AlertDialog

class LoadingDialog {
    private var activity: Activity? = null
    private var dialog: AlertDialog? = null

    constructor(activity: Activity) {
        this.activity = activity
    }

    fun startLoadingAnimation() {
        val builder = AlertDialog.Builder(activity!!)
        val inflater = activity?.layoutInflater
        builder.setView(inflater?.inflate(R.layout.custom_dialog, null))
        builder.setCancelable(false)
        dialog = builder.create()
        dialog!!.window?.setBackgroundDrawableResource(android.R.color.transparent)
        this.dialog!!.show()
    }

    fun dismiss() {
        this.dialog!!.dismiss()
    }
}