package com.cracks.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var turn = 1
    var playerOne: Player = Player("Hunter", "X")
    var playerTwo: Player = Player("Morgana", "O")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
    }

    fun buttonClick(view: View) {
        /*1. Manejar el evento tap */
        /*2. Evaluar el tablero tras cada jugada */

        val selectedBtn = view as Button
        initGame(selectedBtn, view)
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

    /*TODO Terminar de validar el juego*/
    private fun initGame(selectedBtn: Button, view: View) {
        var btnID = getBtnID(selectedBtn)

        selectedBtn.isEnabled = false
        turn = if(turn == 1) {
            playerOne.addPosition(btnID);
            setIconResource(playerOne.getAttribute(PlayerAttribute.TOKEN), selectedBtn)
            2
        } else {
            playerTwo.addPosition(btnID);
            setIconResource(playerTwo.getAttribute(PlayerAttribute.TOKEN), selectedBtn)
            1
        }

        if(playerOne.isWinner()){
            val snack = Snackbar.make(view,"Gano el uno", Snackbar.LENGTH_LONG);
            snack.show();
        }
        if(playerTwo.isWinner()){
            val snack = Snackbar.make(view,"Gano el dos", Snackbar.LENGTH_LONG);
            snack.show();
        }
    }

    private fun setIconResource(token: String, button: Button) {
        when(token) {
            "X" -> {button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_close_24,0,0,0)}
            "O" -> {button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_radio_button_unchecked_24,0,0,0)}
        }
    }
}