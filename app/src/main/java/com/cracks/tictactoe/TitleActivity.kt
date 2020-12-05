package com.cracks.tictactoe

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_title.*

class TitleActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_title)
    }

    fun goGame(view: View){
        val name = editTextName.text.toString();
        if(name.trim().isNotEmpty()){
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("player", name)
            }
            startActivity(intent);
        }else{
            Snackbar.make(view,"Ingrese un nombre por favor", Snackbar.LENGTH_LONG).show()
        }
    }
}