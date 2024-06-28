package com.example.bilhar

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import com.example.bilhar.databinding.ActivitySnookerBinding
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import android.widget.NumberPicker
import androidx.constraintlayout.widget.Guideline
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class snooker : ComponentActivity() {

    private lateinit var binding: ActivitySnookerBinding
    private var isEditMode = true

    private var offsetX = 75f
    private var offsetY = 75f

    @SuppressLint("ClickableViewAccessibility", "WrongViewCast")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySnookerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("snooker_visited", true)
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
                    MotionEvent.ACTION_UP -> {
                        if (System.currentTimeMillis() - lastTouchDownTime1 <= 100) {
                            imageView_BolaAmarela.visibility = View.INVISIBLE
                        }
                    }
                    else -> return@setOnTouchListener false
                }
                true
            }
        }


        // Configuração do ImageView "Bola Azul"
        var imageView_BolaAzul= findViewById<ImageView>(R.id.imageBolaAzul)
        imageView_BolaAzul.elevation = 10f

        var dX_BolaAzul = 0f
        var dY_BolaAzul = 0f

        var lastTouchDownTime2: Long = 0

        guideline5.post {
            val guideline5Position = guideline5.y
            val guideline6Position = guideline6.y
            val guideline7Position = guideline7.x
            val guideline8Position = guideline8.x

            imageView_BolaAzul.setOnTouchListener { view, event ->

                if (!isEditMode) {
                    return@setOnTouchListener false
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastTouchDownTime2 = System.currentTimeMillis()
                        dX_BolaAzul = view.x - event.rawX + offsetX
                        dY_BolaAzul = view.y - event.rawY + offsetY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (System.currentTimeMillis() - lastTouchDownTime2 > 100) {
                            var newX = event.rawX + dX_BolaAzul
                            var newY = event.rawY + dY_BolaAzul

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
                            imageView_BolaAzul.visibility = View.INVISIBLE
                        }
                    }
                    else -> return@setOnTouchListener false
                }
                true
            }
        }


        // Configuração do ImageView "Bola Castanha"
        var imageView_BolaCastanha = findViewById<ImageView>(R.id.imageBolaCastanha)
        imageView_BolaCastanha.elevation = 10f

        var dX_BolaCastanha = 0f
        var dY_BolaCastanha = 0f

        var lastTouchDownTime3: Long = 0

        guideline5.post {
            val guideline5Position = guideline5.y
            val guideline6Position = guideline6.y
            val guideline7Position = guideline7.x
            val guideline8Position = guideline8.x

            imageView_BolaCastanha.setOnTouchListener { view, event ->

                if (!isEditMode) {
                    return@setOnTouchListener false
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastTouchDownTime3 = System.currentTimeMillis()
                        dX_BolaCastanha = view.x - event.rawX + offsetX
                        dY_BolaCastanha = view.y - event.rawY + offsetY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (System.currentTimeMillis() - lastTouchDownTime3 > 100) {
                            var newX = event.rawX + dX_BolaCastanha
                            var newY = event.rawY + dY_BolaCastanha

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
                            imageView_BolaCastanha.visibility = View.INVISIBLE
                        }
                    }
                    else -> return@setOnTouchListener false
                }
                true
            }
        }


        // Configuração do ImageView "Bola Preta"
        var imageView_BolaPreta = findViewById<ImageView>(R.id.imageBolaPreta)
        imageView_BolaPreta.elevation = 10f

        var dX_BolaPreta = 0f
        var dY_BolaPreta = 0f

        var lastTouchDownTime4: Long = 0

        guideline5.post {
            val guideline5Position = guideline5.y
            val guideline6Position = guideline6.y
            val guideline7Position = guideline7.x
            val guideline8Position = guideline8.x

            imageView_BolaPreta.setOnTouchListener { view, event ->

                if (!isEditMode) {
                    return@setOnTouchListener false
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastTouchDownTime4 = System.currentTimeMillis()
                        dX_BolaPreta = view.x - event.rawX + offsetX
                        dY_BolaPreta = view.y - event.rawY + offsetY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (System.currentTimeMillis() - lastTouchDownTime4 > 100) {
                            var newX = event.rawX + dX_BolaPreta
                            var newY = event.rawY + dY_BolaPreta

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
                            imageView_BolaPreta.visibility = View.INVISIBLE
                        }
                    }
                    else -> return@setOnTouchListener false
                }
                true
            }
        }


        // Configuração do ImageView "Bola Rosa"
        var imageView_BolaRosa = findViewById<ImageView>(R.id.imageBolaRosa)
        imageView_BolaRosa.elevation = 10f

        var dX_BolaRosa = 0f
        var dY_BolaRosa = 0f

        var lastTouchDownTime5: Long = 0

        guideline5.post {
            val guideline5Position = guideline5.y
            val guideline6Position = guideline6.y
            val guideline7Position = guideline7.x
            val guideline8Position = guideline8.x

            imageView_BolaRosa.setOnTouchListener { view, event ->

                if (!isEditMode) {
                    return@setOnTouchListener false
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastTouchDownTime5 = System.currentTimeMillis()
                        dX_BolaRosa = view.x - event.rawX + offsetX
                        dY_BolaRosa = view.y - event.rawY + offsetY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (System.currentTimeMillis() - lastTouchDownTime5 > 100) {
                            var newX = event.rawX + dX_BolaRosa
                            var newY = event.rawY + dY_BolaRosa

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
                            imageView_BolaRosa.visibility = View.INVISIBLE
                        }
                    }
                    else -> return@setOnTouchListener false
                }
                true
            }
        }


        // Configuração do ImageView "Bola Verde"
        var imageView_BolaVerde = findViewById<ImageView>(R.id.imageBolaVerde)
        imageView_BolaVerde.elevation = 10f

        var dX_BolaVerde = 0f
        var dY_BolaVerde = 0f

        var lastTouchDownTime6: Long = 0

        guideline5.post {
            val guideline5Position = guideline5.y
            val guideline6Position = guideline6.y
            val guideline7Position = guideline7.x
            val guideline8Position = guideline8.x

            imageView_BolaVerde.setOnTouchListener { view, event ->

                if (!isEditMode) {
                    return@setOnTouchListener false
                }

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        lastTouchDownTime6 = System.currentTimeMillis()
                        dX_BolaVerde = view.x - event.rawX + offsetX
                        dY_BolaVerde = view.y - event.rawY + offsetY
                    }
                    MotionEvent.ACTION_MOVE -> {
                        if (System.currentTimeMillis() - lastTouchDownTime6 > 100) {
                            var newX = event.rawX + dX_BolaVerde
                            var newY = event.rawY + dY_BolaVerde

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
                            imageView_BolaVerde.visibility = View.INVISIBLE
                        }
                    }
                    else -> return@setOnTouchListener false
                }
                true
            }
        }


        val imageViews = arrayOf(
            findViewById<ImageView>(R.id.imageBolaVermelha1),
            findViewById<ImageView>(R.id.imageBolaVermelha2),
            findViewById<ImageView>(R.id.imageBolaVermelha3),
            findViewById<ImageView>(R.id.imageBolaVermelha4),
            findViewById<ImageView>(R.id.imageBolaVermelha5),
            findViewById<ImageView>(R.id.imageBolaVermelha6),
            findViewById<ImageView>(R.id.imageBolaVermelha7),
            findViewById<ImageView>(R.id.imageBolaVermelha8),
            findViewById<ImageView>(R.id.imageBolaVermelha9),
            findViewById<ImageView>(R.id.imageBolaVermelha10),
            findViewById<ImageView>(R.id.imageBolaVermelha11),
            findViewById<ImageView>(R.id.imageBolaVermelha12),
            findViewById<ImageView>(R.id.imageBolaVermelha13),
            findViewById<ImageView>(R.id.imageBolaVermelha14),
            findViewById<ImageView>(R.id.imageBolaVermelha15),
        )

        val dX = FloatArray(imageViews.size)
        val dY = FloatArray(imageViews.size)
        val lastTouchDownTime = LongArray(imageViews.size)

        guideline5.post {
            val guideline5Position = guideline5.y
            val guideline6Position = guideline6.y
            val guideline7Position = guideline7.x
            val guideline8Position = guideline8.x

            for (i in imageViews.indices) {
                imageViews[i].elevation = 10f

                imageViews[i].setOnTouchListener { view, event ->

                    if (!isEditMode) {
                        return@setOnTouchListener false
                    }

                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            lastTouchDownTime[i] = System.currentTimeMillis()
                            dX[i] = view.x - event.rawX + offsetX
                            dY[i] = view.y - event.rawY + offsetY
                        }
                        MotionEvent.ACTION_MOVE -> {
                            if (System.currentTimeMillis() - lastTouchDownTime[i] > 100) {
                                var newX = event.rawX + dX[i]
                                var newY = event.rawY + dY[i]

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
                            if (System.currentTimeMillis() - lastTouchDownTime[i] <= 100) {
                                imageViews[i].visibility = View.INVISIBLE
                            }
                        }
                        else -> return@setOnTouchListener false
                    }
                    true
                }
            }
        }



        val images = arrayOf(
            R.drawable.imagem_vazia,
            R.drawable.snooker_bola_branca,      // Bola branca
            R.drawable.snooker_bola_amarela,     // Bola amarela
            R.drawable.snooker_bola_azul,        // Bola azul
            R.drawable.snooker_bola_castanha,    // Bola castanha
            R.drawable.snooker_bola_preta,       // Bola preta
            R.drawable.snooker_bola_rosa,        // Bola rosa
            R.drawable.snooker_bola_verde,       // Bola verde
            R.drawable.snooker_bola_vermelha,    // Bola vermelha
        )

        // Configuração do Spinner
        val spinner = findViewById<Spinner>(R.id.spinner)

        val items = arrayOf("", "Bola branca", "Bola amarela", "Bola azul", "Bola castanha", "Bola preta", "Bola rosa", "Bola verde", "Bola vermelha")
        val adapter = CustomSpinnerAdapter(this, items, images)
        spinner.adapter = adapter
        spinner.setSelection(1)

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                // Aqui você pode adicionar lógica para lidar com a seleção, por exemplo, mudar a imagem
                when (selectedItem) {
                    "Bola branca" -> {
                        imageView_BolaBranca.visibility = View.VISIBLE
                    }
                    "Bola amarela" -> {
                        imageView_BolaAmarela.visibility = View.VISIBLE

                    }
                    "Bola azul" -> {
                        imageView_BolaAzul.visibility = View.VISIBLE

                    }
                    "Bola castanha" -> {
                        imageView_BolaCastanha.visibility = View.VISIBLE

                    }
                    "Bola preta" -> {
                        imageView_BolaPreta.visibility = View.VISIBLE

                    }
                    "Bola rosa" -> {
                        imageView_BolaRosa.visibility = View.VISIBLE

                    }
                    "Bola verde" -> {
                        imageView_BolaVerde.visibility = View.VISIBLE

                    }
                    "Bola vermelha" -> {
                        showNumberPickerDialog()
                        spinner.setSelection(0)
                    }
                }
            }

            private fun resetSpinnerSelection() {
                spinner.setSelection(0)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Do nothing
            }
        }

    }


    private fun showNumberPickerDialog() {
        val numberPicker = NumberPicker(this).apply {
            minValue = 1
            maxValue = 15
        }

        val dialog = AlertDialog.Builder(this, R.style.RoundedAlertDialog)
            .setTitle("Selecione o número de bolas vermelhas:")
            .setView(numberPicker)
            .setPositiveButton("OK") { dialog, which ->
                val numberOfBalls = numberPicker.value
                showRedBalls(numberOfBalls)
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }

    private fun showRedBalls(numberOfBalls: Int) {
        for (i in 1..15) {
            val resId = resources.getIdentifier("imageBolaVermelha$i", "id", packageName)
            val imageView = findViewById<ImageView>(resId)
            imageView?.visibility = if (i <= numberOfBalls) View.VISIBLE else View.GONE
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
            val file = File(storageDir, "Snooker.png")
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