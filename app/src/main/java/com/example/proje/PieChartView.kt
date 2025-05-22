package com.example.proje

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class PieChartView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyle: Int = 0
) : View(context, attrs, defStyle) {

    private var data: List<Pair<Float, String>> = listOf()
    private val colors = listOf(
        Color.parseColor("#FF6384"), Color.parseColor("#36A2EB"),
        Color.parseColor("#FFCE56"), Color.parseColor("#66BB6A"),
        Color.parseColor("#FFA726"), Color.parseColor("#AB47BC")
    )
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textSize = 32f
        textAlign = Paint.Align.CENTER
        isFakeBoldText = true
        setShadowLayer(2f, 1f, 1f, Color.DKGRAY)
    }

    fun setData(data: List<Pair<Float, String>>) {
        this.data = data
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (data.isEmpty()) return

        val total = data.sumOf { it.first.toDouble() }
        var startAngle = -90f
        val size = min(width, height).toFloat()
        val radius = size / 2 * 0.8f
        val cx = width / 2f
        val cy = height / 2f

        for ((index, item) in data.withIndex()) {
            val angle = 360f * (item.first.toFloat() / total.toFloat())
            paint.color = colors[index % colors.size]
            canvas.drawArc(
                cx - radius, cy - radius, cx + radius, cy + radius,
                startAngle, angle, true, paint
            )

            // Kategori ismini yerleştirme
            val midAngle = startAngle + angle / 2
            val labelRadius = radius * 0.65f // Yazı biraz içte olsun
            val rad = Math.toRadians(midAngle.toDouble())
            val labelX = (cx + labelRadius * cos(rad)).toFloat()
            val labelY = (cy + labelRadius * sin(rad)).toFloat() + 12 // Yazı ortalaması düzeltme

            // Kısa/küçük slice'lar için yazı boyutunu otomatik kısalt
            val displayText = if (angle > 22) item.second else item.second.take(4)

            canvas.drawText(displayText, labelX, labelY, textPaint)

            startAngle += angle
        }
    }
}
