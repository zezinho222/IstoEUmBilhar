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
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.FileProvider
import com.example.bilhar.databinding.ActivityCarambolaBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class carambola : ComponentActivity() {

    private lateinit var binding: ActivityCarambolaBinding
    private var isEditMode = true

    private var offsetX = 75f
    private var offsetY = 75f

    @SuppressLint("ClickableViewAccessibility", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityCarambolaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("carambola_visited", true)
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
                    else -> return@setOnTouchListener false
                }
                true
            }
        }


        // Configuração do ImageView "Bola Amarela"
        var imageView_BolaAmarela = findViewById<ImageView>(R.id.imageBolaAmarela)
        imageView_BolaAmarela.elevation = 10f

        var dX_BolaAmarela = 0f
        var dY_BolaAmarela = 0f

        var lastTouchDownTime1: Long = 0

        guideline5.post {
            val guideline5Position = guideline5.y
            val guideline6Position = guideline6.y
            val guideline7Position = guideline7.x
            val guideline8Position = guideline8.x

            imageView_BolaAmarela.setOnTouchListener { view, event ->

                if (!isEditMode) {
                    return@setOnTouchListener false
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastTouchDownTime1 = System.currentTimeMillis()
                        dX_BolaAmarela = view.x - event.rawX + offsetX
                        dY_BolaAmarela = view.y - event.rawY + offsetY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (System.currentTimeMillis() - lastTouchDownTime1 > 100) {
                            var newX = event.rawX + dX_BolaAmarela
                            var newY = event.rawY + dY_BolaAmarela

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
                    else -> return@setOnTouchListener false
                }
                true
            }
        }


        // Configuração do ImageView "Bola vermelha"
        var imageView_BolaVermelha = findViewById<ImageView>(R.id.imageBolaVermelha)
        imageView_BolaVermelha.elevation = 10f

        var dX_BolaVermelha = 0f
        var dY_BolaVermelha = 0f

        var lastTouchDownTime2: Long = 0

        guideline5.post {
            val guideline5Position = guideline5.y
            val guideline6Position = guideline6.y
            val guideline7Position = guideline7.x
            val guideline8Position = guideline8.x

            imageView_BolaVermelha.setOnTouchListener { view, event ->

                if (!isEditMode) {
                    return@setOnTouchListener false
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastTouchDownTime2 = System.currentTimeMillis()
                        dX_BolaVermelha = view.x - event.rawX + offsetX
                        dY_BolaVermelha = view.y - event.rawY + offsetY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (System.currentTimeMillis() - lastTouchDownTime2 > 100) {
                            var newX = event.rawX + dX_BolaVermelha
                            var newY = event.rawY + dY_BolaVermelha

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
                    else -> return@setOnTouchListener false
                }
                true
            }
        }


        val translationInDp = -50
        val scale = resources.displayMetrics.density
        val translationInPx = translationInDp * scale

        imageView_BolaBranca.translationX = translationInPx // Aplica a translação no eixo X


        val translationInDp2 = 50
        val translationInPx2 = translationInDp2 * scale

        imageView_BolaVermelha.translationX = translationInPx2 // Aplica a translação no eixo X


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
            val file = File(storageDir, "Carambola.png")
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