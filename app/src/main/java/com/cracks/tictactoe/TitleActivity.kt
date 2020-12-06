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

    fun searchGame(view: View){
        val name = editTextName.text.toString();
        if (validate(name, view)){
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("player", name);
                putExtra("type", 'O');
            }
            startActivity(intent);
        }
    }

    fun createGame(view: View){
        val name = editTextName.text.toString();
        if (validate(name, view)){
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("player", name);
                putExtra("type", 'X');
            }
            startActivity(intent);
        }
    }

    fun validate(name: String, view: View): Boolean{
        if(name.trim().isNotEmpty()){
            return true
        }else{
            Snackbar.make(view,"Ingrese un nombre por favor", Snackbar.LENGTH_LONG).show();
            return false;
        }
    }
}