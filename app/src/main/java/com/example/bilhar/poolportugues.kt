package com.example.bilhar

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.bilhar.databinding.ActivityPoolportuguesBinding
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class poolportugues : ComponentActivity() {

    private lateinit var binding: ActivityPoolportuguesBinding
    private var isEditMode = true

    private var offsetX = 75f
    private var offsetY = 75f

    @SuppressLint("ClickableViewAccessibility", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityPoolportuguesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("poolportugues_visited", true)
        editor.apply()

        val btnEditar = findViewById<ImageButton>(R.id.btn_editar)
        val btnApagar = findViewById<ImageButton>(R.id.btn_apagar)
        val drawingView = findViewById<DrawingView>(R.id.drawingView)

        drawingView.setOnTouchListener { _, _ ->
            true // Consume all touch events, regardless of edit mode
        }

        btnEditar.setOnClickListener {
            isEditMode = !isEditMode
            drawingView.isEnabled = isEditMode
            if (!isEditMode) {
                Toast.makeText(this, "Modo de edição ativado.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Modo de edição desativado.", Toast.LENGTH_SHORT).show()
            }
        }

        btnApagar.setOnClickListener {
            drawingView.clearDrawing()
            Toast.makeText(this, "Desenho apagado.", Toast.LENGTH_SHORT).show()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }

        // Configuração do ImageButton "Voltar"
        val btnVoltar = findViewById<ImageButton>(R.id.btn_voltar)
        btnVoltar.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }


        val btnShare = findViewById<ImageButton>(R.id.btn_share)
        btnShare.setOnClickListener {
            shareScreen()
        }


        val btnInfo = findViewById<ImageButton>(R.id.btn_info)
        btnInfo.setOnClickListener {
            Toast.makeText(this, "Mantenha a bola pressionada para arrastá-la pela mesa.", Toast.LENGTH_LONG).show()
            Toast.makeText(this, "Clique na bola para removê-la.", Toast.LENGTH_LONG).show()
        }


        // Obter as posições das guidelines
        val guideline5 = findViewById<Guideline>(R.id.guideline5)
        val guideline6 = findViewById<Guideline>(R.id.guideline6)
        val guideline7 = findViewById<Guideline>(R.id.guideline7)
        val guideline8 = findViewById<Guideline>(R.id.guideline8)


        // Configuração do ImageView "Bola branca"
        var imageView_BolaBranca = findViewById<ImageView>(R.id.imageBolaBranca)
        imageView_BolaBranca.elevation = 10f

        var dX_BolaBranca = 0f
        var dY_BolaBranca = 0f

        var lastTouchDownTimeBranca: Long = 0

        guideline5.post {
            val guideline5Position = guideline5.y
            val guideline6Position = guideline6.y
            val guideline7Position = guideline7.x
            val guideline8Position = guideline8.x

            imageView_BolaBranca.setOnTouchListener { view, event ->

                if (!isEditMode) {
                    // Modo de edição está ativado, não manipule o toque
                    return@setOnTouchListener false
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastTouchDownTimeBranca = System.currentTimeMillis()
                        dX_BolaBranca = view.x - event.rawX + offsetX
                        dY_BolaBranca = view.y - event.rawY + offsetY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (System.currentTimeMillis() - lastTouchDownTimeBranca > 100) {
                            var newX = event.rawX + dX_BolaBranca
                            var newY = event.rawY + dY_BolaBranca

                            // Verificar se a nova posição está dentro dos limites das guidelines
                            if (newX < guideline7Position) newX = guideline7Position
                            if (newX > guideline8Position - view.width) newX = guideline8Position - view.width
                            if (newY < guideline5Position) newY = guideline5Position
                            if (newY > guideline6Position - view.height) newY = guideline6Position - view.height

                            view.animate()
                                .x(newX)
                                .y(newY)
                                .setDuration(0)
                                .start()
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (System.currentTimeMillis() - lastTouchDownTimeBranca <= 100) {
                            imageView_BolaBranca.visibility = View.INVISIBLE
                        }
                    }
                    else -> return@setOnTouchListener false
                }
                true
            }
        }


        // Configuração do ImageView "Bola 1"
        var imageView_Bola1 = findViewById<ImageView>(R.id.imageBola1)
        imageView_Bola1.elevation = 10f

        var dX_Bola1 = 0f
        var dY_Bola1 = 0f

        var lastTouchDownTime1: Long = 0

        guideline5.post {
            val guideline5Position = guideline5.y
            val guideline6Position = guideline6.y
            val guideline7Position = guideline7.x
            val guideline8Position = guideline8.x

            imageView_Bola1.setOnTouchListener { view, event ->

                if (!isEditMode) {
                    return@setOnTouchListener false
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastTouchDownTime1 = System.currentTimeMillis()
                        dX_Bola1 = view.x - event.rawX + offsetX
                        dY_Bola1 = view.y - event.rawY + offsetY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (System.currentTimeMillis() - lastTouchDownTime1 > 100) {
                            var newX = event.rawX + dX_Bola1
                            var newY = event.rawY + dY_Bola1

                            // Verificar se a nova posição está dentro dos limites das guidelines
                            if (newX < guideline7Position) newX = guideline7Position
                            if (newX > guideline8Position - view.width) newX = guideline8Position - view.width
                            if (newY < guideline5Position) newY = guideline5Position
                            if (newY > guideline6Position - view.height) newY = guideline6Position - view.height

                            view.animate()
                                .x(newX)
                                .y(newY)
                                .setDuration(0)
                                .start()
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (System.currentTimeMillis() - lastTouchDownTime1 <= 100) {
                            imageView_Bola1.visibility = View.INVISIBLE
                        }
                    }
                    else -> return@setOnTouchListener false
                }
                true
            }
        }


        // Configuração do ImageView "Bola 2"
        var imageView_Bola2 = findViewById<ImageView>(R.id.imageBola2)
        imageView_Bola2.elevation = 10f

        var dX_Bola2 = 0f
        var dY_Bola2 = 0f

        var lastTouchDownTime2: Long = 0

        guideline5.post {
            val guideline5Position = guideline5.y
            val guideline6Position = guideline6.y
            val guideline7Position = guideline7.x
            val guideline8Position = guideline8.x

            imageView_Bola2.setOnTouchListener { view, event ->

                if (!isEditMode) {
                    return@setOnTouchListener false
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastTouchDownTime2 = System.currentTimeMillis()
                        dX_Bola2 = view.x - event.rawX + offsetX
                        dY_Bola2 = view.y - event.rawY + offsetY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (System.currentTimeMillis() - lastTouchDownTime2 > 100) {
                            var newX = event.rawX + dX_Bola2
                            var newY = event.rawY + dY_Bola2

                            // Verificar se a nova posição está dentro dos limites das guidelines
                            if (newX < guideline7Position) newX = guideline7Position
                            if (newX > guideline8Position - view.width) newX = guideline8Position - view.width
                            if (newY < guideline5Position) newY = guideline5Position
                            if (newY > guideline6Position - view.height) newY = guideline6Position - view.height

                            view.animate()
                                .x(newX)
                                .y(newY)
                                .setDuration(0)
                                .start()
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (System.currentTimeMillis() - lastTouchDownTime2 <= 100) {
                            imageView_Bola2.visibility = View.INVISIBLE
                        }
                    }
                    else -> return@setOnTouchListener false
                }
                true
            }
        }


        // Configuração do ImageView "Bola 3"
        var imageView_Bola3 = findViewById<ImageView>(R.id.imageBola3)
        imageView_Bola3.elevation = 10f

        var dX_Bola3 = 0f
        var dY_Bola3 = 0f

        var lastTouchDownTime3: Long = 0

        guideline5.post {
            val guideline5Position = guideline5.y
            val guideline6Position = guideline6.y
            val guideline7Position = guideline7.x
            val guideline8Position = guideline8.x

            imageView_Bola3.setOnTouchListener { view, event ->

                if (!isEditMode) {
                    return@setOnTouchListener false
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastTouchDownTime3 = System.currentTimeMillis()
                        dX_Bola3 = view.x - event.rawX + offsetX
                        dY_Bola3 = view.y - event.rawY + offsetY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (System.currentTimeMillis() - lastTouchDownTime3 > 100) {
                            var newX = event.rawX + dX_Bola3
                            var newY = event.rawY + dY_Bola3

                            // Verificar se a nova posição está dentro dos limites das guidelines
                            if (newX < guideline7Position) newX = guideline7Position
                            if (newX > guideline8Position - view.width) newX = guideline8Position - view.width
                            if (newY < guideline5Position) newY = guideline5Position
                            if (newY > guideline6Position - view.height) newY = guideline6Position - view.height

                            view.animate()
                                .x(newX)
                                .y(newY)
                                .setDuration(0)
                                .start()
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (System.currentTimeMillis() - lastTouchDownTime3 <= 100) {
                            imageView_Bola3.visibility = View.INVISIBLE
                        }
                    }
                    else -> return@setOnTouchListener false
                }
                true
            }
        }


        // Configuração do ImageView "Bola 4"
        var imageView_Bola4 = findViewById<ImageView>(R.id.imageBola4)
        imageView_Bola4.elevation = 10f

        var dX_Bola4 = 0f
        var dY_Bola4 = 0f

        var lastTouchDownTime4: Long = 0

        guideline5.post {
            val guideline5Position = guideline5.y
            val guideline6Position = guideline6.y
            val guideline7Position = guideline7.x
            val guideline8Position = guideline8.x

            imageView_Bola4.setOnTouchListener { view, event ->

                if (!isEditMode) {
                    return@setOnTouchListener false
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastTouchDownTime4 = System.currentTimeMillis()
                        dX_Bola4 = view.x - event.rawX + offsetX
                        dY_Bola4 = view.y - event.rawY + offsetY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (System.currentTimeMillis() - lastTouchDownTime4 > 100) {
                            var newX = event.rawX + dX_Bola4
                            var newY = event.rawY + dY_Bola4

                            // Verificar se a nova posição está dentro dos limites das guidelines
                            if (newX < guideline7Position) newX = guideline7Position
                            if (newX > guideline8Position - view.width) newX = guideline8Position - view.width
                            if (newY < guideline5Position) newY = guideline5Position
                            if (newY > guideline6Position - view.height) newY = guideline6Position - view.height

                            view.animate()
                                .x(newX)
                                .y(newY)
                                .setDuration(0)
                                .start()
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (System.currentTimeMillis() - lastTouchDownTime4 <= 100) {
                            imageView_Bola4.visibility = View.INVISIBLE
                        }
                    }
                    else -> return@setOnTouchListener false
                }
                true
            }
        }


        // Configuração do ImageView "Bola 5"
        var imageView_Bola5 = findViewById<ImageView>(R.id.imageBola5)
        imageView_Bola5.elevation = 10f

        var dX_Bola5 = 0f
        var dY_Bola5 = 0f

        var lastTouchDownTime5: Long = 0

        guideline5.post {
            val guideline5Position = guideline5.y
            val guideline6Position = guideline6.y
            val guideline7Position = guideline7.x
            val guideline8Position = guideline8.x

            imageView_Bola5.setOnTouchListener { view, event ->

                if (!isEditMode) {
                    return@setOnTouchListener false
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastTouchDownTime5 = System.currentTimeMillis()
                        dX_Bola5 = view.x - event.rawX + offsetX
                        dY_Bola5 = view.y - event.rawY + offsetY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (System.currentTimeMillis() - lastTouchDownTime5 > 100) {
                            var newX = event.rawX + dX_Bola5
                            var newY = event.rawY + dY_Bola5

                            // Verificar se a nova posição está dentro dos limites das guidelines
                            if (newX < guideline7Position) newX = guideline7Position
                            if (newX > guideline8Position - view.width) newX = guideline8Position - view.width
                            if (newY < guideline5Position) newY = guideline5Position
                            if (newY > guideline6Position - view.height) newY = guideline6Position - view.height

                            view.animate()
                                .x(newX)
                                .y(newY)
                                .setDuration(0)
                                .start()
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (System.currentTimeMillis() - lastTouchDownTime5 <= 100) {
                            imageView_Bola5.visibility = View.INVISIBLE
                        }
                    }
                    else -> return@setOnTouchListener false
                }
                true
            }
        }


        // Configuração do ImageView "Bola 6"
        var imageView_Bola6 = findViewById<ImageView>(R.id.imageBola6)
        imageView_Bola6.elevation = 10f

        var dX_Bola6 = 0f
        var dY_Bola6 = 0f

        var lastTouchDownTime6: Long = 0

        guideline5.post {
            val guideline5Position = guideline5.y
            val guideline6Position = guideline6.y
            val guideline7Position = guideline7.x
            val guideline8Position = guideline8.x

            imageView_Bola6.setOnTouchListener { view, event ->

                if (!isEditMode) {
                    return@setOnTouchListener false
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastTouchDownTime6 = System.currentTimeMillis()
                        dX_Bola6 = view.x - event.rawX + offsetX
                        dY_Bola6 = view.y - event.rawY + offsetY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (System.currentTimeMillis() - lastTouchDownTime6 > 100) {
                            var newX = event.rawX + dX_Bola6
                            var newY = event.rawY + dY_Bola6

                            // Verificar se a nova posição está dentro dos limites das guidelines
                            if (newX < guideline7Position) newX = guideline7Position
                            if (newX > guideline8Position - view.width) newX = guideline8Position - view.width
                            if (newY < guideline5Position) newY = guideline5Position
                            if (newY > guideline6Position - view.height) newY = guideline6Position - view.height

                            view.animate()
                                .x(newX)
                                .y(newY)
                                .setDuration(0)
                                .start()
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (System.currentTimeMillis() - lastTouchDownTime6 <= 100) {
                            imageView_Bola6.visibility = View.INVISIBLE
                        }
                    }
                    else -> return@setOnTouchListener false
                }
                true
            }
        }


        // Configuração do ImageView "Bola 7"
        var imageView_Bola7 = findViewById<ImageView>(R.id.imageBola7)
        imageView_Bola7.elevation = 10f

        var dX_Bola7 = 0f
        var dY_Bola7 = 0f

        var lastTouchDownTime7: Long = 0

        guideline5.post {
            val guideline5Position = guideline5.y
            val guideline6Position = guideline6.y
            val guideline7Position = guideline7.x
            val guideline8Position = guideline8.x

            imageView_Bola7.setOnTouchListener { view, event ->

                if (!isEditMode) {
                    return@setOnTouchListener false
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastTouchDownTime7 = System.currentTimeMillis()
                        dX_Bola7 = view.x - event.rawX + offsetX
                        dY_Bola7 = view.y - event.rawY + offsetX
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (System.currentTimeMillis() - lastTouchDownTime7 > 100) {
                            var newX = event.rawX + dX_Bola7
                            var newY = event.rawY + dY_Bola7

                            // Verificar se a nova posição está dentro dos limites das guidelines
                            if (newX < guideline7Position) newX = guideline7Position
                            if (newX > guideline8Position - view.width) newX = guideline8Position - view.width
                            if (newY < guideline5Position) newY = guideline5Position
                            if (newY > guideline6Position - view.height) newY = guideline6Position - view.height

                            view.animate()
                                .x(newX)
                                .y(newY)
                                .setDuration(0)
                                .start()
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (System.currentTimeMillis() - lastTouchDownTime7 <= 100) {
                            imageView_Bola7.visibility = View.INVISIBLE
                        }
                    }
                    else -> return@setOnTouchListener false
                }
                true
            }
        }


        // Configuração do ImageView "Bola 8"
        var imageView_Bola8 = findViewById<ImageView>(R.id.imageBola8)
        imageView_Bola8.elevation = 10f

        var dX_Bola8 = 0f
        var dY_Bola8 = 0f

        var lastTouchDownTime8: Long = 0

        guideline5.post {
            val guideline5Position = guideline5.y
            val guideline6Position = guideline6.y
            val guideline7Position = guideline7.x
            val guideline8Position = guideline8.x

            imageView_Bola8.setOnTouchListener { view, event ->

                if (!isEditMode) {
                    return@setOnTouchListener false
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastTouchDownTime8 = System.currentTimeMillis()
                        dX_Bola8 = view.x - event.rawX + offsetX
                        dY_Bola8 = view.y - event.rawY + offsetY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (System.currentTimeMillis() - lastTouchDownTime8 > 100) {
                            var newX = event.rawX + dX_Bola8
                            var newY = event.rawY + dY_Bola8

                            // Verificar se a nova posição está dentro dos limites das guidelines
                            if (newX < guideline7Position) newX = guideline7Position
                            if (newX > guideline8Position - view.width) newX = guideline8Position - view.width
                            if (newY < guideline5Position) newY = guideline5Position
                            if (newY > guideline6Position - view.height) newY = guideline6Position - view.height

                            view.animate()
                                .x(newX)
                                .y(newY)
                                .setDuration(0)
                                .start()
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (System.currentTimeMillis() - lastTouchDownTime8 <= 100) {
                            imageView_Bola8.visibility = View.INVISIBLE
                        }
                    }
                    else -> return@setOnTouchListener false
                }
                true
            }
        }


        // Configuração do ImageView "Bola 9"
        var imageView_Bola9 = findViewById<ImageView>(R.id.imageBola9)
        imageView_Bola9.elevation = 10f

        var dX_Bola9 = 0f
        var dY_Bola9 = 0f

        var lastTouchDownTime9: Long = 0

        guideline5.post {
            val guideline5Position = guideline5.y
            val guideline6Position = guideline6.y
            val guideline7Position = guideline7.x
            val guideline8Position = guideline8.x

            imageView_Bola9.setOnTouchListener { view, event ->

                if (!isEditMode) {
                    return@setOnTouchListener false
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastTouchDownTime9 = System.currentTimeMillis()
                        dX_Bola9 = view.x - event.rawX + offsetX
                        dY_Bola9 = view.y - event.rawY + offsetY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (System.currentTimeMillis() - lastTouchDownTime9 > 100) {
                            var newX = event.rawX + dX_Bola9
                            var newY = event.rawY + dY_Bola9

                            // Verificar se a nova posição está dentro dos limites das guidelines
                            if (newX < guideline7Position) newX = guideline7Position
                            if (newX > guideline8Position - view.width) newX = guideline8Position - view.width
                            if (newY < guideline5Position) newY = guideline5Position
                            if (newY > guideline6Position - view.height) newY = guideline6Position - view.height

                            view.animate()
                                .x(newX)
                                .y(newY)
                                .setDuration(0)
                                .start()
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (System.currentTimeMillis() - lastTouchDownTime9 <= 100) {
                            imageView_Bola9.visibility = View.INVISIBLE
                        }
                    }
                    else -> return@setOnTouchListener false
                }
                true
            }
        }


        // Configuração do ImageView "Bola 10"
        var imageView_Bola10 = findViewById<ImageView>(R.id.imageBola10)
        imageView_Bola10.elevation = 10f

        var dX_Bola10 = 0f
        var dY_Bola10 = 0f

        var lastTouchDownTime10: Long = 0

        guideline5.post {
            val guideline5Position = guideline5.y
            val guideline6Position = guideline6.y
            val guideline7Position = guideline7.x
            val guideline8Position = guideline8.x

            imageView_Bola10.setOnTouchListener { view, event ->

                if (!isEditMode) {
                    return@setOnTouchListener false
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastTouchDownTime10 = System.currentTimeMillis()
                        dX_Bola10 = view.x - event.rawX + offsetX
                        dY_Bola10 = view.y - event.rawY + offsetY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (System.currentTimeMillis() - lastTouchDownTime10 > 100) {
                            var newX = event.rawX + dX_Bola10
                            var newY = event.rawY + dY_Bola10

                            // Verificar se a nova posição está dentro dos limites das guidelines
                            if (newX < guideline7Position) newX = guideline7Position
                            if (newX > guideline8Position - view.width) newX = guideline8Position - view.width
                            if (newY < guideline5Position) newY = guideline5Position
                            if (newY > guideline6Position - view.height) newY = guideline6Position - view.height

                            view.animate()
                                .x(newX)
                                .y(newY)
                                .setDuration(0)
                                .start()
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (System.currentTimeMillis() - lastTouchDownTime10 <= 100) {
                            imageView_Bola10.visibility = View.INVISIBLE
                        }
                    }
                    else -> return@setOnTouchListener false
                }
                true
            }
        }


        // Configuração do ImageView "Bola 11"
        var imageView_Bola11 = findViewById<ImageView>(R.id.imageBola11)
        imageView_Bola11.elevation = 10f

        var dX_Bola11 = 0f
        var dY_Bola11 = 0f

        var lastTouchDownTime11: Long = 0

        guideline5.post {
            val guideline5Position = guideline5.y
            val guideline6Position = guideline6.y
            val guideline7Position = guideline7.x
            val guideline8Position = guideline8.x

            imageView_Bola11.setOnTouchListener { view, event ->

                if (!isEditMode) {
                    return@setOnTouchListener false
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastTouchDownTime11 = System.currentTimeMillis()
                        dX_Bola11 = view.x - event.rawX + offsetX
                        dY_Bola11 = view.y - event.rawY + offsetY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (System.currentTimeMillis() - lastTouchDownTime11 > 100) {
                            var newX = event.rawX + dX_Bola11
                            var newY = event.rawY + dY_Bola11

                            // Verificar se a nova posição está dentro dos limites das guidelines
                            if (newX < guideline7Position) newX = guideline7Position
                            if (newX > guideline8Position - view.width) newX = guideline8Position - view.width
                            if (newY < guideline5Position) newY = guideline5Position
                            if (newY > guideline6Position - view.height) newY = guideline6Position - view.height

                            view.animate()
                                .x(newX)
                                .y(newY)
                                .setDuration(0)
                                .start()
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (System.currentTimeMillis() - lastTouchDownTime11 <= 100) {
                            imageView_Bola11.visibility = View.INVISIBLE
                        }
                    }
                    else -> return@setOnTouchListener false
                }
                true
            }
        }


        // Configuração do ImageView "Bola 12"
        var imageView_Bola12 = findViewById<ImageView>(R.id.imageBola12)
        imageView_Bola12.elevation = 10f

        var dX_Bola12 = 0f
        var dY_Bola12 = 0f

        var lastTouchDownTime12: Long = 0

        guideline5.post {
            val guideline5Position = guideline5.y
            val guideline6Position = guideline6.y
            val guideline7Position = guideline7.x
            val guideline8Position = guideline8.x

            imageView_Bola12.setOnTouchListener { view, event ->

                if (!isEditMode) {
                    return@setOnTouchListener false
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastTouchDownTime12 = System.currentTimeMillis()
                        dX_Bola12 = view.x - event.rawX + offsetX
                        dY_Bola12 = view.y - event.rawY + offsetY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (System.currentTimeMillis() - lastTouchDownTime12 > 100) {
                            var newX = event.rawX + dX_Bola12
                            var newY = event.rawY + dY_Bola12

                            // Verificar se a nova posição está dentro dos limites das guidelines
                            if (newX < guideline7Position) newX = guideline7Position
                            if (newX > guideline8Position - view.width) newX = guideline8Position - view.width
                            if (newY < guideline5Position) newY = guideline5Position
                            if (newY > guideline6Position - view.height) newY = guideline6Position - view.height

                            view.animate()
                                .x(newX)
                                .y(newY)
                                .setDuration(0)
                                .start()
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (System.currentTimeMillis() - lastTouchDownTime12 <= 100) {
                            imageView_Bola12.visibility = View.INVISIBLE
                        }
                    }
                    else -> return@setOnTouchListener false
                }
                true
            }
        }


        // Configuração do ImageView "Bola 13"
        var imageView_Bola13 = findViewById<ImageView>(R.id.imageBola13)
        imageView_Bola13.elevation = 10f

        var dX_Bola13 = 0f
        var dY_Bola13 = 0f

        var lastTouchDownTime13: Long = 0

        guideline5.post {
            val guideline5Position = guideline5.y
            val guideline6Position = guideline6.y
            val guideline7Position = guideline7.x
            val guideline8Position = guideline8.x

            imageView_Bola13.setOnTouchListener { view, event ->

                if (!isEditMode) {
                    return@setOnTouchListener false
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastTouchDownTime13 = System.currentTimeMillis()
                        dX_Bola13 = view.x - event.rawX + offsetX
                        dY_Bola13 = view.y - event.rawY + offsetY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (System.currentTimeMillis() - lastTouchDownTime13 > 100) {
                            var newX = event.rawX + dX_Bola13
                            var newY = event.rawY + dY_Bola13

                            // Verificar se a nova posição está dentro dos limites das guidelines
                            if (newX < guideline7Position) newX = guideline7Position
                            if (newX > guideline8Position - view.width) newX = guideline8Position - view.width
                            if (newY < guideline5Position) newY = guideline5Position
                            if (newY > guideline6Position - view.height) newY = guideline6Position - view.height

                            view.animate()
                                .x(newX)
                                .y(newY)
                                .setDuration(0)
                                .start()
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (System.currentTimeMillis() - lastTouchDownTime13 <= 100) {
                            imageView_Bola13.visibility = View.INVISIBLE
                        }
                    }
                    else -> return@setOnTouchListener false
                }
                true
            }
        }


        // Configuração do ImageView "Bola 14"
        var imageView_Bola14 = findViewById<ImageView>(R.id.imageBola14)
        imageView_Bola14.elevation = 10f

        var dX_Bola14 = 0f
        var dY_Bola14 = 0f

        var lastTouchDownTime14: Long = 0

        guideline5.post {
            val guideline5Position = guideline5.y
            val guideline6Position = guideline6.y
            val guideline7Position = guideline7.x
            val guideline8Position = guideline8.x

            imageView_Bola14.setOnTouchListener { view, event ->

                if (!isEditMode) {
                    return@setOnTouchListener false
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastTouchDownTime14 = System.currentTimeMillis()
                        dX_Bola14 = view.x - event.rawX + offsetX
                        dY_Bola14 = view.y - event.rawY + offsetY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (System.currentTimeMillis() - lastTouchDownTime14 > 100) {
                            var newX = event.rawX + dX_Bola14
                            var newY = event.rawY + dY_Bola14

                            // Verificar se a nova posição está dentro dos limites das guidelines
                            if (newX < guideline7Position) newX = guideline7Position
                            if (newX > guideline8Position - view.width) newX = guideline8Position - view.width
                            if (newY < guideline5Position) newY = guideline5Position
                            if (newY > guideline6Position - view.height) newY = guideline6Position - view.height

                            view.animate()
                                .x(newX)
                                .y(newY)
                                .setDuration(0)
                                .start()
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (System.currentTimeMillis() - lastTouchDownTime14 <= 100) {
                            imageView_Bola14.visibility = View.INVISIBLE
                        }
                    }
                    else -> return@setOnTouchListener false
                }
                true
            }
        }


        // Configuração do ImageView "Bola 15"
        var imageView_Bola15 = findViewById<ImageView>(R.id.imageBola15)
        imageView_Bola15.elevation = 10f

        var dX_Bola15 = 0f
        var dY_Bola15 = 0f

        var lastTouchDownTime15: Long = 0


        guideline5.post {
            val guideline5Position = guideline5.y
            val guideline6Position = guideline6.y
            val guideline7Position = guideline7.x
            val guideline8Position = guideline8.x

            imageView_Bola15.setOnTouchListener { view, event ->

                if (!isEditMode) {
                    return@setOnTouchListener false
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastTouchDownTime15 = System.currentTimeMillis()
                        dX_Bola15 = view.x - event.rawX + offsetX
                        dY_Bola15 = view.y - event.rawY + offsetY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (System.currentTimeMillis() - lastTouchDownTime15 > 100) {
                            var newX = event.rawX + dX_Bola15
                            var newY = event.rawY + dY_Bola15

                            // Verificar se a nova posição está dentro dos limites das guidelines
                            if (newX < guideline7Position) newX = guideline7Position
                            if (newX > guideline8Position - view.width) newX = guideline8Position - view.width
                            if (newY < guideline5Position) newY = guideline5Position
                            if (newY > guideline6Position - view.height) newY = guideline6Position - view.height

                            view.animate()
                                .x(newX)
                                .y(newY)
                                .setDuration(0)
                                .start()
                        }
                    }
                    MotionEvent.ACTION_UP -> {
                        if (System.currentTimeMillis() - lastTouchDownTime15 <= 100) {
                            imageView_Bola15.visibility = View.INVISIBLE
                        }
                    }
                    else -> return@setOnTouchListener false
                }
                true
            }
        }


        val images = arrayOf(
            R.drawable.bola_branca,      // Bola branca
            R.drawable.pool_bola_1,      // Bola 1
            R.drawable.pool_bola_2,      // Bola 2
            R.drawable.pool_bola_3,      // Bola 3
            R.drawable.pool_bola_4,      // Bola 4
            R.drawable.pool_bola_5,      // Bola 5
            R.drawable.pool_bola_6,      // Bola 6
            R.drawable.pool_bola_7,      // Bola 7
            R.drawable.pool_bola_8,      // Bola 8
            R.drawable.pool_bola_9,      // Bola 9
            R.drawable.pool_bola_10,     // Bola 10
            R.drawable.pool_bola_11,     // Bola 11
            R.drawable.pool_bola_12,     // Bola 12
            R.drawable.pool_bola_13,     // Bola 13
            R.drawable.pool_bola_14,     // Bola 14
            R.drawable.pool_bola_15      // Bola 15
        )

        // Configuração do Spinner
        val spinner = findViewById<Spinner>(R.id.spinner)

        val items = arrayOf("Bola branca", "Bola 1", "Bola 2", "Bola 3", "Bola 4", "Bola 5", "Bola 6", "Bola 7",
            "Bola 8", "Bola 9", "Bola 10", "Bola 11", "Bola 12", "Bola 13", "Bola 14", "Bola 15")
        val adapter = CustomSpinnerAdapter(this, items, images)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                // Aqui você pode adicionar lógica para lidar com a seleção, por exemplo, mudar a imagem
                when (selectedItem) {
                    "Bola branca" -> {
                        imageView_BolaBranca.visibility = View.VISIBLE
                    }
                    "Bola 1" -> {
                        imageView_Bola1.visibility = View.VISIBLE
                    }
                    "Bola 2" -> {
                        imageView_Bola2.visibility = View.VISIBLE

                    }
                    "Bola 3" -> {
                        imageView_Bola3.visibility = View.VISIBLE

                    }
                    "Bola 4" -> {
                        imageView_Bola4.visibility = View.VISIBLE

                    }
                    "Bola 4" -> {
                        imageView_Bola4.visibility = View.VISIBLE

                    }
                    "Bola 5" -> {
                        imageView_Bola5.visibility = View.VISIBLE

                    }
                    "Bola 6" -> {
                        imageView_Bola6.visibility = View.VISIBLE

                    }
                    "Bola 7" -> {
                        imageView_Bola7.visibility = View.VISIBLE

                    }
                    "Bola 8" -> {
                        imageView_Bola8.visibility = View.VISIBLE

                    }
                    "Bola 9" -> {
                        imageView_Bola9.visibility = View.VISIBLE

                    }
                    "Bola 10" -> {
                        imageView_Bola10.visibility = View.VISIBLE

                    }
                    "Bola 11" -> {
                        imageView_Bola11.visibility = View.VISIBLE

                    }
                    "Bola 12" -> {
                        imageView_Bola12.visibility = View.VISIBLE

                    }
                    "Bola 13" -> {
                        imageView_Bola13.visibility = View.VISIBLE

                    }
                    "Bola 14" -> {
                        imageView_Bola14.visibility = View.VISIBLE

                    }
                    "Bola 15" -> {
                        imageView_Bola15.visibility = View.VISIBLE

                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }
    }

    private fun shareScreen() {
        val rootView: View = findViewById(R.id.main)
        val bitmap = getBitmapFromView(rootView)
        val file = saveBitmap(bitmap)

        if (file != null) {
            val uri = FileProvider.getUriForFile(this, "${packageName}.fileprovider", file)
            val intent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_STREAM, uri)
                type = "image/png"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            startActivity(Intent.createChooser(intent, "Share via"))
        }
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private fun saveBitmap(bitmap: Bitmap): File? {
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return try {
            val file = File(storageDir, "Pool português.png")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}