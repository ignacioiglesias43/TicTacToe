package com.cracks.tictactoe

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import androidx.activity.OnBackPressedDispatcher
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var turn = 1
    private var localPlayer: Player = Player("Hunter", "X")
    private var rivalPlayer: Player = Player("Morgana", "O")
    private var code: String? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPlayers()
        showTurn()
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("¿Seguro desea abandonar el juego?")
        builder.setPositiveButton("Sí") { _: DialogInterface, _: Int -> super.onBackPressed() }
        builder.setNegativeButton("No") { _: DialogInterface, _: Int ->  }
        builder.show()
    }

    private fun initPlayers() {
        localPlayer.setAttribute(PlayerAttribute.NAME, intent.getStringExtra("player_name")!!)
        localPlayer.setAttribute(PlayerAttribute.TOKEN, intent.getStringExtra("player_type")!!)
        rivalPlayer.setAttribute(PlayerAttribute.TOKEN, intent.getStringExtra("rival_type")!!)
        code = intent.getStringExtra("code")

        val docRef = db.collection("games").document(this.code!!)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d("TAG", "Current data: ${snapshot.data}")
                if(snapshot.data!!.isNotEmpty()) {
                    if(localPlayer.getAttribute(PlayerAttribute.TOKEN) == "X") {
                        if(snapshot.data!!.containsKey("player_two")) {
                            rivalPlayer.setAttribute(PlayerAttribute.NAME, snapshot.data?.get("player_two").toString())
                        }
                    } else {
                        if(snapshot.data!!.containsKey("player_one")){
                            rivalPlayer.setAttribute(PlayerAttribute.NAME, snapshot.data?.get("player_one").toString())
                        }
                    }
                    turn = snapshot.data?.get("turn_of") as Int
                }
            } else {
                Log.d("TAG", "Current data: null")
            }
        }
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

        selectedBtn.isEnabled = false
        turn = if(turn == 1) {
            localPlayer.addPosition(btnID)
            setIconResource(localPlayer.getAttribute(PlayerAttribute.TOKEN), selectedBtn)
            2
        } else {
            rivalPlayer.addPosition(btnID)
            setIconResource(rivalPlayer.getAttribute(PlayerAttribute.TOKEN), selectedBtn)
            1
        }
        showTurn();
        when {
            localPlayer.isWinner() -> {
                builder.setTitle("Ganador: ${localPlayer.getAttribute(PlayerAttribute.NAME)}")
                builder.show()
            }
            rivalPlayer.isWinner() -> {
                builder.setTitle("Ganador: ${rivalPlayer.getAttribute(PlayerAttribute.NAME)}")
                builder.show()
            }
            localPlayer.getPositions().size + rivalPlayer.getPositions().size == 9 -> {
                builder.setTitle("Empate")
                builder.show()
            }
        }
    }

    private fun reset() {
        val pos1 = localPlayer.getPositions()
        val pos2 = rivalPlayer.getPositions()

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
        localPlayer.setPositions(pos1)
        rivalPlayer.setPositions(pos2)
    }

    private fun showTurn() {
        val turnText = if(turn == 1) localPlayer.getAttribute(PlayerAttribute.NAME) else rivalPlayer.getAttribute(PlayerAttribute.NAME);
        actualPlayerText.text = turnText;
    }

    private fun setIconResource(token: String, button: ImageButton) {
        when(token) {
            "X" -> {button.setImageResource(R.drawable.ic_baseline_close_24)}
            "O" -> {button.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24)}
        }
    }
}
