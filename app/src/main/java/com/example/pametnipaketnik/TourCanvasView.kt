package com.example.pametnipaketnik

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View
import projekt.TSP.Tour

class TourCanvasView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint()
    private var tour: Tour? = null

    fun setTour(tour: Tour?) {
        this.tour = tour
        invalidate() // Osveži pogled
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas ?: return

        paint.color = Color.WHITE
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)

        val currentTour = tour ?: return
        val points = currentTour.path

        // Poišči največje koordinate za prilagoditev merila
        val maxX = points.maxOfOrNull { it.x } ?: 1.0
        val maxY = points.maxOfOrNull { it.y } ?: 1.0

        val scaleX = width / maxX
        val scaleY = height / maxY

        paint.color = Color.RED
        paint.style = Paint.Style.FILL

        for (point in points) {
            val x = (point.x * scaleX).toFloat()
            val y = (point.y * scaleY).toFloat()
            canvas.drawCircle(x, y, 8f, paint)
        }

        paint.color = Color.BLACK
        paint.style = Paint.Style.STROKE

        for (i in points.indices) {
            val start = points[i]
            val end = if (i == points.size - 1) points[0] else points[i + 1]

            canvas.drawLine(
                (start.x * scaleX).toFloat(),
                (start.y * scaleY).toFloat(),
                (end.x * scaleX).toFloat(),
                (end.y * scaleY).toFloat(),
                paint
            )
        }
    }
}
