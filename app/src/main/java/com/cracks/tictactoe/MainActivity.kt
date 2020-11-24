package com.cracks.tictactoe

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var turn = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun buttonClick(view: View) {
        /*1. Manejar el evento tap */
        /*2. Evaluar el tablero tras cada jugada */

        val selectedBtn = view as Button
        initGame(selectedBtn)
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
    private fun initGame(selectedBtn: Button) {
        val playerOne = Player("Hunter", "X")
        val playerTwo = Player("Morgana", "O")
        var btnID = getBtnID(selectedBtn)

        selectedBtn.isEnabled = false
        turn = if(turn == 1) {
            playerOne.getPositions().add(btnID)
            setIconResource(playerOne.getAttribute(PlayerAttribute.TOKEN), selectedBtn)
            2
        } else {
            setIconResource(playerTwo.getAttribute(PlayerAttribute.TOKEN), selectedBtn)
            playerTwo.getPositions().add(btnID)
            1
        }

        playerOne.isWinner()
    }

    private fun setIconResource(token: String, button: Button) {
        when(token) {
            "X" -> {button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_close_24,0,0,0)}
            "O" -> {button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_radio_button_unchecked_24,0,0,0)}
        }
    }
}