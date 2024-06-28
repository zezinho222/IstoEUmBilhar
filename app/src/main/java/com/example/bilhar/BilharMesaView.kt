package com.example.bilhar

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class BilharMesaView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint()
    private val woodTexture: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.poolmadeira)
    private val clothTexture: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.poolpano) // Carregando a textura do pano
    private val borderThickness = 70f // Definindo a espessura da borda aqui

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        // Obter dimensões da View
        val viewWidth = width.toFloat()
        val viewHeight = height.toFloat()

        // Desenhar retângulo preto com fundo cinzento claro
        val backgroundRectPadding = 5f // Margem ao redor da mesa
        paint.color = Color.TRANSPARENT
        canvas.drawRect(
            0f,
            0f,
            viewWidth,
            viewHeight,
            paint
        )
        paint.color = Color.WHITE
        canvas.drawRect(
            backgroundRectPadding,
            backgroundRectPadding,
            viewWidth - backgroundRectPadding,
            viewHeight - backgroundRectPadding,
            paint
        )

        // Dimensões da mesa
        val tableWidth = viewWidth - 2 * backgroundRectPadding
        val tableHeight = viewHeight - 2 * backgroundRectPadding

        // Bordas laterais
        drawWoodTexture(canvas, backgroundRectPadding, backgroundRectPadding, backgroundRectPadding + borderThickness, viewHeight - backgroundRectPadding)
        drawWoodTexture(canvas, backgroundRectPadding, backgroundRectPadding, viewWidth - backgroundRectPadding, backgroundRectPadding + borderThickness)
        drawWoodTexture(canvas, backgroundRectPadding, viewHeight - backgroundRectPadding - borderThickness, viewWidth - backgroundRectPadding, viewHeight - backgroundRectPadding)
        drawWoodTexture(canvas, viewWidth - backgroundRectPadding - borderThickness, backgroundRectPadding, viewWidth - backgroundRectPadding, viewHeight - backgroundRectPadding)

        // Desenhar a área do pano
        drawClothTexture(canvas, backgroundRectPadding + borderThickness, backgroundRectPadding + borderThickness, viewWidth - backgroundRectPadding - borderThickness, viewHeight - backgroundRectPadding - borderThickness)

        // Calcular os tamanhos dos buracos com base nas dimensões da mesa
        val holeRadiusCorner = Math.min(tableWidth, tableHeight) * 0.05f // 5% do menor lado da mesa para os buracos dos cantos
        val holeRadius = Math.min(tableWidth, tableHeight) * 0.045f // 5% do menor lado da mesa para os buracos do meio
        val holeBorderThickness = holeRadiusCorner * 0.1f // Espessura do contorno branco dos buracos

        // Desenhar buracos com contorno branco
        // Canto superior esquerdo
        drawHoleWithBorder(canvas, backgroundRectPadding + borderThickness, backgroundRectPadding + borderThickness, holeRadiusCorner, holeBorderThickness)
        // Canto superior direito
        drawHoleWithBorder(canvas, viewWidth - backgroundRectPadding - borderThickness, backgroundRectPadding + borderThickness, holeRadiusCorner, holeBorderThickness)
        // Canto inferior esquerdo
        drawHoleWithBorder(canvas, backgroundRectPadding + borderThickness, viewHeight - backgroundRectPadding - borderThickness, holeRadiusCorner, holeBorderThickness)
        // Canto inferior direito
        drawHoleWithBorder(canvas, viewWidth - backgroundRectPadding - borderThickness, viewHeight - backgroundRectPadding - borderThickness, holeRadiusCorner, holeBorderThickness)
        // Meio das bordas longas
        drawHoleWithBorder(canvas, viewWidth / 2, backgroundRectPadding + borderThickness, holeRadius, holeBorderThickness)
        drawHoleWithBorder(canvas, viewWidth / 2, viewHeight - backgroundRectPadding - borderThickness, holeRadius, holeBorderThickness)
    }

    private fun drawWoodTexture(canvas: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        val src = android.graphics.Rect(0, 0, woodTexture.width, woodTexture.height)
        val dst = android.graphics.RectF(left, top, right, bottom)
        canvas.drawBitmap(woodTexture, src, dst, paint)
    }

    private fun drawClothTexture(canvas: Canvas, left: Float, top: Float, right: Float, bottom: Float) {
        val src = android.graphics.Rect(0, 0, clothTexture.width, clothTexture.height)
        val dst = android.graphics.RectF(left, top, right, bottom)
        canvas.drawBitmap(clothTexture, src, dst, paint)
    }

    private fun drawHoleWithBorder(canvas: Canvas, cx: Float, cy: Float, radius: Float, borderThickness: Float) {
        paint.color = Color.WHITE
        canvas.drawCircle(cx, cy, radius + borderThickness, paint)
        paint.color = Color.BLACK
        canvas.drawCircle(cx, cy, radius, paint)
    }
}
