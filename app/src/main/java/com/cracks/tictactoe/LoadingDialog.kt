package com.cracks.tictactoe

import android.app.Activity
import android.app.AlertDialog
import kotlinx.android.synthetic.main.custom_dialog.*

class LoadingDialog {
    private var activity: Activity? = null
    private var dialog: AlertDialog? = null
    private var txt = " "

    constructor(activity: Activity, txt: String) {
        this.activity = activity
        this.txt = txt
    }

    constructor(activity: Activity) {
        this.activity = activity
    }

    fun startLoadingAnimation() {
        val builder = AlertDialog.Builder(activity!!)
        val inflater = activity?.layoutInflater
        builder.setView(inflater?.inflate(R.layout.custom_dialog, null))
        builder.setCancelable(false)
        dialog = builder.create()
        dialog?.textView?.text = txt
        dialog!!.window?.setBackgroundDrawableResource(android.R.color.transparent)
        this.dialog!!.show()
    }

    fun dismiss() {
        this.dialog!!.dismiss()
    }
}