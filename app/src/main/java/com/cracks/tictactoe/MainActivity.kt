package com.cracks.tictactoe

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var turn = 1
    private val player: Player = Player("player", "X");
    var playerOne: Player = Player("Hunter", "X")
    var playerTwo: Player = Player("Morgana", "O")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        player.setAttribute(PlayerAttribute.NAME, intent.getStringExtra("player")!!);
        showTurn();
    }

    fun buttonClick(view: View) {
        /*1. Manejar el evento tap */
        /*2. Evaluar el tablero tras cada jugada */
        val selectedBtn = view as ImageButton
        initGame(selectedBtn, view)
    }

    private fun getBtnID(btnInstance: ImageButton): Int {
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

    private fun getBtnInstance(btnID: Int): ImageButton? {
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

    private fun initGame(selectedBtn: ImageButton, view: View) {
        var btnID = getBtnID(selectedBtn)
        val builder = AlertDialog.Builder(this)
        builder.setMessage("¿Deseas volver a jugar?")
        builder.setPositiveButton("Sí") { _: DialogInterface, _: Int -> reset() }
        builder.setNegativeButton("No") { _: DialogInterface, _: Int -> finish() }

        /* TODO: Modificar la interfaz para mostrar un label con el turno actual */
        selectedBtn.isEnabled = false
        turn = if(turn == 1) {
            player.addPosition(btnID)
            setIconResource(playerOne.getAttribute(PlayerAttribute.TOKEN), selectedBtn)
            2
        } else {
            playerTwo.addPosition(btnID)
            setIconResource(playerTwo.getAttribute(PlayerAttribute.TOKEN), selectedBtn)
            1
        }
        showTurn();
        when {
            player.isWinner() -> {
                builder.setTitle("Ganador: ${player.getAttribute(PlayerAttribute.NAME)}")
                builder.show()
            }
            playerTwo.isWinner() -> {
                builder.setTitle("Ganador: ${playerTwo.getAttribute(PlayerAttribute.NAME)}")
                builder.show()
            }
            player.getPositions().size + playerTwo.getPositions().size == 9 -> {
                builder.setTitle("Empate")
                builder.show()
            }
        }
    }

    private fun reset() {
        val pos1 = player.getPositions()
        val pos2 = playerTwo.getPositions()

        for(pos in pos1) {
            val btn = getBtnInstance(pos)
            btn?.isEnabled = true
            btn?.setImageResource(android.R.color.transparent)
        }

        for(pos in pos2) {
            val btn = getBtnInstance(pos)
            btn?.isEnabled = true
            btn?.setImageResource(android.R.color.transparent)
        }
        pos1.clear()
        pos2.clear()
        playerOne.setPositions(pos1)
        playerTwo.setPositions(pos2)
    }

    private fun showTurn() {
        val turnText = if(turn == 1) player.getAttribute(PlayerAttribute.NAME) else playerTwo.getAttribute(PlayerAttribute.NAME);
        actualPlayerText.text = turnText;
    }

    private fun setIconResource(token: String, button: ImageButton) {
        when(token) {
            "X" -> {button.setImageResource(R.drawable.ic_baseline_close_24)}
            "O" -> {button.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24)}
        }
    }
}