package com.example.bilhar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("carambola_visited", false)
        editor.putBoolean("pool_visited", false)
        editor.putBoolean("snooker_visited", false)
        editor.putBoolean("poolportugues_visited", false)
        editor.apply()

        val btnCarambola = findViewById<Button>(R.id.btn_Carambola)
        btnCarambola.setOnClickListener {
            val intent = Intent(this, carambola::class.java)
            startActivity(intent)
        }

        val btnPool = findViewById<Button>(R.id.btn_Pool)
        btnPool.setOnClickListener {
            val intent = Intent(this, Pool::class.java)
            startActivity(intent)
        }

        val btnSnooker = findViewById<Button>(R.id.btn_Snooker)
        btnSnooker.setOnClickListener {
            val intent = Intent(this, snooker::class.java)
            startActivity(intent)
        }

        val btnPoolPortugues = findViewById<Button>(R.id.btn_PoolPortugues)
        btnPoolPortugues.setOnClickListener {
            val intent = Intent(this, poolportugues::class.java)
            startActivity(intent)
        }

    }
}
