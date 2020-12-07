package com.cracks.tictactoe

import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.androidParameters
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.dynamiclinks.ktx.shortLinkAsync
import com.google.firebase.dynamiclinks.ktx.socialMetaTagParameters
import com.google.firebase.ktx.Firebase
import com.google.firebase.dynamiclinks.ktx.component1
import com.google.firebase.dynamiclinks.ktx.component2
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_title.*
import java.text.SimpleDateFormat
import java.util.*

class TitleActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)
        tryToGetDynamicLink()
    }

    fun searchGame(view: View) {
        val name = editTextName.text.toString().trim()
        val code = editTextCode.text.toString().trim()
        if (name.trim().isNotEmpty() && code.trim().isNotEmpty()) {
            val data = hashMapOf(
                    "player_two" to name,
                    "two_status" to 1,
                )

            db.collection("games").document("$code")
                .set(data, SetOptions.merge())
                .addOnSuccessListener {
                    playGame(true, code)
                }
                .addOnFailureListener { e ->
                    Snackbar.make(view, "Hubo un error al iniciar la partida", Snackbar.LENGTH_LONG).show()
                    Log.w("Error ", "Error adding document", e)
                }
        } else {
            Snackbar.make(view, "Ingrese un nombre y el código, por favor", Snackbar.LENGTH_LONG).show()
        }
    }

    fun createGame(view: View) {
        val name = editTextName.text.toString().trim()
        val code = generateCode();
        if (name.trim().isNotEmpty() && code.trim().isNotEmpty()) {
            val game = hashMapOf(
                    "player_one" to name,
                    "turn_of" to "X",
                    "one_status" to 1
                )
            db.collection("games")
                    .document("$code")
                    .set(game)
                    .addOnSuccessListener {
                        generateDynamicLink(view, code)
                        playGame(false, code)
                        Log.d("Done", "DocumentSnapshot successfully written!")
                    }
                    .addOnFailureListener { e ->
                        Snackbar.make(view, "Hubo un error al iniciar la partida", Snackbar.LENGTH_LONG).show()
                        Log.w("Error ", "Error adding document", e)
                    }
        } else {
            Snackbar.make(view, "Ingrese un nombre, por favor", Snackbar.LENGTH_LONG).show()
        }
    }

    /* Esta funcion genera el link */
    private fun generateDynamicLink(view: View, code: String) {
        val name = editTextName.text.toString()

        Firebase.dynamicLinks.shortLinkAsync {
            link = Uri.parse("https://cracks.page.link?&code=$code")
            domainUriPrefix = "https://cracks.page.link"
            androidParameters("com.cracks.tictactoe") {
                minimumVersion = 125
            }
            socialMetaTagParameters {
                title = "Tic Tac Toe Online"
                description = "$name te está invitando a jugar tic tac toe con el código: $code"
                imageUrl = Uri.parse("https://images-na.ssl-images-amazon.com/images/I/41Yj12sPTtL.png")
            }
        }.addOnSuccessListener { (shortLink, flowchartLink) ->
            processShortLink(shortLink, flowchartLink)
        }.addOnFailureListener {
            Snackbar.make(view, "Hubo un error al iniciar la jugada, por favor intente de nuevo", Snackbar.LENGTH_SHORT).show()
        }
    }

    /* Esta funcion procesa el link y abre la opcion de compartirlo */
    private fun processShortLink(shortLink: Uri?, flowchartLink: Uri?) {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, shortLink.toString())
        sendIntent.type = "text/plain"
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    /* Esta funcion recibe el link e interpreta los parametros que llegan por GET */
    private fun tryToGetDynamicLink() {
        Firebase.dynamicLinks
                .getDynamicLink(intent)
                .addOnSuccessListener(this) { pendingDynamicLinkData ->
                    // Get deep link from result (may be null if no link is found)
                    var deepLink: Uri? = null
                    if (pendingDynamicLinkData != null) {
                        deepLink = pendingDynamicLinkData.link

                        if (deepLink != null) {
                            val gameCode = deepLink.getQueryParameter("code")
                            if (gameCode != null) {
                                editTextCode.setText(gameCode)
                            }
                        }
                    } else {
                        Log.w("Error: ", "pending NULL")
                    }
                }
                .addOnFailureListener(this) { e -> Log.w("Error: ", "getDynamicLink:onFailure", e) }
    }

    /* Funcion para iniciar el juego, envia los datos del nombre de cada jugador y su token */
    private fun playGame(isSearch: Boolean, code: String) {
        val name = editTextName.text.toString()

        val playerType = if (isSearch) "O" else "X"
        val rivalType = if (isSearch) "X" else "O"

        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("player_name", name)
            putExtra("player_type", playerType)
            putExtra("rival_type", rivalType)
            putExtra("code", "$code")
        }
        startActivity(intent)
    }

    //Genera el código del juego
    private fun generateCode(): String{
        val charPool : List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val code = (1..5)
                .map {i -> kotlin.random.Random.nextInt(0, charPool.size) }
                .map(charPool::get)
                .joinToString("");
        return code;
    }

}
