package com.cracks.tictactoe

import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private var turn = "X"
    private var localPlayer: Player = Player("Hunter", "X")
    private var rivalPlayer: Player = Player("Morgana", "O")
    private var code: String? = null
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPlayers()
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("¿Seguro desea abandonar el juego?")
        builder.setPositiveButton("Sí") { _: DialogInterface, _: Int -> super.onBackPressed() }
        builder.setNegativeButton("No") { _: DialogInterface, _: Int ->  }
        builder.show()
    }

    fun buttonClick(view: View) {
        /*1. Manejar el evento tap */
        /*2. Evaluar el tablero tras cada jugada */
        val selectedBtn = view as ImageButton
        //Validación para saber si el jugador en turno puede pulsar un botón
        if(localPlayer.getAttribute(PlayerAttribute.TOKEN) == turn)
            initGame(selectedBtn, view)
    }

    private fun initPlayers() {
        val loadingDialog = LoadingDialog(this, "Esperando respuesta del invitado...")

        loadingDialog.startLoadingAnimation()
        localPlayer.setAttribute(PlayerAttribute.NAME, intent.getStringExtra("player_name")!!)
        localPlayer.setAttribute(PlayerAttribute.TOKEN, intent.getStringExtra("player_type")!!)
        rivalPlayer.setAttribute(PlayerAttribute.TOKEN, intent.getStringExtra("rival_type")!!)
        code = intent.getStringExtra("code")
        keyTextView.text = "CLAVE: $code"

        val docRef = db.collection("games").document(this.code!!)
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("TAG", "Listen failed.", e)
                return@addSnapshotListener
            }
            /* Se supone que esta funcion snapShot espera por actualizaciones en tiempo real, si funca para detectar cuando llega el usuario invitado */
            if (snapshot != null && snapshot.exists()) {
                Log.d("TAG", "Current data: ${snapshot.data}")
                if(snapshot.data!!.isNotEmpty()) {
                    if(localPlayer.getAttribute(PlayerAttribute.TOKEN) == "X") {
                        if(snapshot.data!!.containsKey("player_two")) {
                            rivalPlayer.setAttribute(PlayerAttribute.NAME, snapshot.data?.get("player_two").toString())
                            loadingDialog.dismiss()
                        }
                    } else {
                        if(snapshot.data!!.containsKey("player_one")){
                            rivalPlayer.setAttribute(PlayerAttribute.NAME, snapshot.data?.get("player_one").toString())
                            loadingDialog.dismiss()
                        }
                    }

                    turn = snapshot.data?.get("turn_of").toString()
                    if(snapshot.data!!.containsKey("move")) {
                        val btn = snapshot.data?.get("move").toString().toInt();
                        if(localPlayer.getAttribute(PlayerAttribute.TOKEN) == turn){
                            doMove(rivalPlayer, btn)
                        }else{
                            doMove(localPlayer, btn)
                        }
                    }
                    showTurn()
                }
            } else {
                Log.d("TAG", "Current data: null")
            }
        }
    }

    //En vez de validar el nuevo movimiento en el click lo validamos desde el onSnapshot
    private fun doMove(player: Player, move: Int){
        player.addPosition(move);
        setIconResource(player.getAttribute(PlayerAttribute.TOKEN), getBtnInstance(move)!!)
        gameOver();
    }

    //Cada movimiento se valida si alguien ganó
    private fun gameOver(){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("¿Deseas volver a jugar?")
        builder.setPositiveButton("Sí") { _: DialogInterface, _: Int -> reset() }
        //TODO Hay que extender el NO para que al jugador A le diga que el jugador B ha abandonado
        builder.setNegativeButton("No") { _: DialogInterface, _: Int -> finish() }

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

    private fun initGame(selectedBtn: ImageButton, view: View) {
        var btnID = getBtnID(selectedBtn)
        selectedBtn.isEnabled = false

        turn = if(turn == "X") "O" else "X";
        var data = hashMapOf(
            "move" to btnID.toString(),
            "turn_of" to turn
        )

        db.collection("games").document(code!!)
                .set(data, SetOptions.merge())
                .addOnFailureListener { e ->
                    Snackbar.make(view, "Hubo un error al cargar su movimiento", Snackbar.LENGTH_LONG).show()
                    Log.w("Error ", "Error adding document", e)
                }
    }

    //TODO Este metodo debe validar que los dos jugadores esten listos para rejugar
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
        val turnText = if(localPlayer.getAttribute(PlayerAttribute.TOKEN) == turn) localPlayer.getAttribute(PlayerAttribute.NAME) else rivalPlayer.getAttribute(PlayerAttribute.NAME)
        actualPlayerText.text = turnText
    }

    private fun setIconResource(token: String, button: ImageButton) {
        when(token) {
            "X" -> {button.setImageResource(R.drawable.ic_baseline_close_24)}
            "O" -> {button.setImageResource(R.drawable.ic_baseline_radio_button_unchecked_24)}
        }
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
}
