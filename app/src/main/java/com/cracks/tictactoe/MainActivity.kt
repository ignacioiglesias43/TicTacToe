package com.cracks.tictactoe

import android.app.AlertDialog
import android.content.DialogInterface
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

    private fun getBtnInstance(btnID: Int): Button? {
        when (btnID) {
            1 -> return button1
            2 -> return button2
            3 -> return button3
            4 -> return button4
            5 -> return button5
            6 -> return button6
            7 -> return button7
            8 -> return button8
            9 -> return button9
        }
        return null
    }

    private fun initGame(selectedBtn: Button, view: View) {
        var btnID = getBtnID(selectedBtn)
        val builder = AlertDialog.Builder(this)
        builder.setMessage("¿Deseas volver a jugar?")
        builder.setPositiveButton("Sí") { _: DialogInterface, _: Int -> reset() }
        builder.setNegativeButton("No") { _: DialogInterface, _: Int -> Log.println(Log.INFO,"GG", "ok") }
        /* TODO: Modificar la interfaz para mostrar un label con el turno actual */
        showTurn(view)
        selectedBtn.isEnabled = false
        turn = if(turn == 1) {
            playerOne.addPosition(btnID)
            setIconResource(playerOne.getAttribute(PlayerAttribute.TOKEN), selectedBtn)
            2
        } else {
            playerTwo.addPosition(btnID)
            setIconResource(playerTwo.getAttribute(PlayerAttribute.TOKEN), selectedBtn)
            1
        }

        when {
            playerOne.isWinner() -> {
                builder.setTitle("Ganador: ${playerOne.getAttribute(PlayerAttribute.NAME)}")
                builder.show()
            }
            playerTwo.isWinner() -> {
                builder.setTitle("Ganador: ${playerTwo.getAttribute(PlayerAttribute.NAME)}")
                builder.show()
            }
            playerOne.getPositions().size + playerTwo.getPositions().size == 9 -> {
                builder.setTitle("Empate")
                builder.show()
            }
        }
    }

    private fun reset() {
        val pos1 = playerOne.getPositions()
        val pos2 = playerTwo.getPositions()

        for(pos in pos1) {
            val btn = getBtnInstance(pos)
            btn?.isEnabled = true
            btn?.setCompoundDrawablesWithIntrinsicBounds(0, 0,0,0)
        }

        for(pos in pos2) {
            val btn = getBtnInstance(pos)
            btn?.isEnabled = true
            btn?.setCompoundDrawablesWithIntrinsicBounds(0, 0,0,0)
        }
        pos1.clear()
        pos2.clear()
        playerOne.setPositions(pos1)
        playerTwo.setPositions(pos2)
    }

    private fun showTurn(view: View) {
        val turnText = if(turn == 1) "Hunter" else "Morgana"

        Snackbar.make(view,"Turno de $turnText", Snackbar.LENGTH_LONG).show()
    }

    private fun setIconResource(token: String, button: Button) {
        when(token) {
            "X" -> {button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_close_24,0,0,0)}
            "O" -> {button.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_baseline_radio_button_unchecked_24,0,0,0)}
        }
    }
}