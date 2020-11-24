package com.cracks.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun buttonClick(view: View) {
        /*1. Manejar el evento tap */
        /*2. Evaluar el tablero tras cada jugada */

        val selectedBtn = view as Button
        var btnID = getBtnID(selectedBtn)
        selectedBtn.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_close_24, 0, 0, 0)

    }

    private fun getBtnID(btnInstance: Button): Int {
        when (btnInstance) {
            button1 -> return 1
            button2 -> return 2
            button3 -> return 3
            button4 -> return 4
            button5 -> return 5
            button6 -> return 6
            button7 -> return 7
            button8 -> return 8
            button9 -> return 9
        }
        return 0
    }
}