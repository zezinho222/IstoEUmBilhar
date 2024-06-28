package com.example.bilhar

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class splash_screen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val carambolaVisited = sharedPreferences.getBoolean("carambola_visited", false)
        val poolVisited = sharedPreferences.getBoolean("pool_visited", false)
        val snookerVisited = sharedPreferences.getBoolean("snooker_visited", false)
        val poolportuguesVisited = sharedPreferences.getBoolean("poolportugues_visited", false)
        val mainactivityVisited = sharedPreferences.getBoolean("mainactivity_visited", false)


        if (carambolaVisited) {
            // Se o utilizador já visitou o carambola.xml, direcione-o diretamente para lá
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, carambola::class.java))
                finish()
            }, 2000)

        } else if (poolVisited) {
            // Se o utilizador já visitou o pool.xml, direcione-o diretamente para lá
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, Pool::class.java))
                finish()
            }, 2000)

        } else if (snookerVisited) {
            // Se o utilizador já visitou o snooker.xml, direcione-o diretamente para lá
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, snooker::class.java))
                finish()
            }, 2000)

        } else if (poolportuguesVisited) {
            // Se o usuário já visitou o poolportugues.xml, direcione-o diretamente para lá
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, poolportugues::class.java))
                finish()
            }, 2000)

        } else {
            // Se o utilizador não visitou nenhuma das telas ainda, continue com o fluxo normal
            Handler(Looper.getMainLooper()).postDelayed({
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }, 2000)
        }


    }
}