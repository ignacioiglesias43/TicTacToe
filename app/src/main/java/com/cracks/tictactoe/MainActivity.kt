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
    private var localPlayer: Player = Player("Player 1", "X")
    private var rivalPlayer: Player = Player("Player 2", "O")
    private var code: String? = null
    var loadingDialog: LoadingDialog? = null;
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initPlayers()
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("¿Seguro desea abandonar el juego?")
        builder.setPositiveButton("Sí") { _: DialogInterface, _: Int ->
            exitGame(localPlayer);
            super.onBackPressed()
        }
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
        loadingDialog = LoadingDialog(this, "Esperando respuesta del invitado...")
        loadingDialog!!.startLoadingAnimation()

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

            if (snapshot != null && snapshot.exists()) {
                Log.d("TAG", "Current data: ${snapshot.data}")
                if(snapshot.data!!.isNotEmpty()) {
                    if(localPlayer.getAttribute(PlayerAttribute.TOKEN) == "X") {
                        if(snapshot.data!!.containsKey("player_two") && snapshot.data?.get("two_status").toString() == "1") {
                            rivalPlayer.setAttribute(PlayerAttribute.NAME, snapshot.data?.get("player_two").toString())
                            loadingDialog!!.dismiss()
                        }
                    } else {
                        if(snapshot.data!!.containsKey("player_one") && snapshot.data?.get("one_status").toString() == "1"){
                            rivalPlayer.setAttribute(PlayerAttribute.NAME, snapshot.data?.get("player_one").toString())
                            loadingDialog!!.dismiss()
                        }
                    }

                    turn = snapshot.data?.get("turn_of").toString()
                    if(snapshot.data!!.containsKey("move")) {
                        val btn = snapshot.data?.get("move").toString().toInt();
                        if(btn!=0){
                            if(localPlayer.getAttribute(PlayerAttribute.TOKEN) == turn){
                                doMove(rivalPlayer, btn)
                            }else{
                                doMove(localPlayer, btn)
                            }
                        }
                    }

                    //Alguno de los dos abandono
                    if(snapshot.data?.get("two_status").toString() == "0" || snapshot.data?.get("one_status").toString() == "0"){
                        finish();
                        Snackbar.make(findViewById(android.R.id.content), "El Rival abandonó la partida", Snackbar.LENGTH_LONG).show()
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
        validateGame();
    }

    //Cada movimiento se valida si alguien ganó
    private fun validateGame(){
        val builder = AlertDialog.Builder(this)
        builder.setMessage("¿Deseas volver a jugar?")
        builder.setPositiveButton("Sí") { _: DialogInterface, _: Int -> gameOver(localPlayer, 2) }
        builder.setNegativeButton("No") { _: DialogInterface, _: Int -> gameOver(localPlayer, 0) }
        Log.println(Log.INFO, "COUNT", "Player one: ${localPlayer.getPositions().size}, Player two: ${rivalPlayer.getPositions().size}")

        when {
            localPlayer.isWinner() -> {
                builder.setTitle("Ganador: ${localPlayer.getAttribute(PlayerAttribute.NAME)}")
                gameOver(builder);
            }
            rivalPlayer.isWinner() -> {
                builder.setTitle("Ganador: ${rivalPlayer.getAttribute(PlayerAttribute.NAME)}")
                gameOver(builder);
            }
            localPlayer.getPositions().size + rivalPlayer.getPositions().size > 8 -> {
                builder.setTitle("Empate")
                gameOver(builder);
            }
        }
    }

    //Asigna un valor 2 que significa esperando al estado de los jugadores
    private fun gameOver(builder: AlertDialog.Builder){
        reset();
        val data = hashMapOf(
                "one_status" to 2,
                "two_status" to 2,
                "move" to 0
        )
        db.collection("games").document(code!!)
                .set(data, SetOptions.merge())
                .addOnSuccessListener {
                    builder.show()
                }.addOnFailureListener{ e ->
                    finish();
                    Snackbar.make(findViewById(android.R.id.content), "Ocurrio un error inesperado", Snackbar.LENGTH_LONG).show()
                    Log.w("Error ", "Error adding document", e)
                }
    }

    //Si el jugador elige jugar de nuevo o salir debe notificar al otro jugador
    private fun gameOver(player: Player, status: Int){
        var data = if(status==2){
            if (player.getAttribute(PlayerAttribute.TOKEN)=="X") hashMapOf("one_status" to 1)
            else hashMapOf("two_status" to 1)
        }else{
            if (player.getAttribute(PlayerAttribute.TOKEN)=="X") hashMapOf("one_status" to 0)
            else hashMapOf("two_status" to 0)
        }
        db.collection("games").document(code!!)
                .set(data, SetOptions.merge())
                .addOnSuccessListener {
                    if(status == 2) loadingDialog!!.startLoadingAnimation()
                    else finish();
                }.addOnFailureListener{ e ->
                    finish();
                    Snackbar.make(findViewById(android.R.id.content), "Ocurrio un error inesperado", Snackbar.LENGTH_LONG).show()
                    Log.w("Error ", "Error adding document", e)
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

    private fun reset() {
        val pos1 = localPlayer.getPositions()
        val pos2 = rivalPlayer.getPositions()

        pos1.addAll(pos2);
        for(pos in pos1) {
            val btn = getBtnInstance(pos)
            btn?.isEnabled = true
            btn?.setImageResource(android.R.color.transparent)
        }

        pos1.clear()
        pos2.clear()
    }

    private fun exitGame(player: Player){
        var numPlayer = if (player.getAttribute(PlayerAttribute.TOKEN) == "X") "one_status" else "two_status"
        var data = hashMapOf(
                numPlayer to 0
            )

        db.collection("games").document(code!!)
                .set(data, SetOptions.merge())
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
