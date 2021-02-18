package com.punuo.sys.sdk.span

import android.graphics.Canvas
import android.graphics.Paint
import android.text.style.ReplacementSpan
import android.view.View
import android.view.ViewGroup


/**
 * Created by han.chen.
 * Date on 2021/1/18.
 **/
class MarkerViewSpan(var targetView: View) : ReplacementSpan() {
    init {
        targetView.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun getSize(paint: Paint, text: CharSequence?, start: Int, end: Int, fm: Paint.FontMetricsInt?): Int {
        val widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        val heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        targetView.measure(widthSpec, heightSpec)
        targetView.layout(0, 0, targetView.measuredWidth, targetView.measuredHeight)
        fm?.let {
            val height = targetView.measuredHeight
            it.ascent = -height / 2
            it.top = -height / 2
            it.descent = height / 2
            it.bottom = height / 2
        }
        return targetView.right
    }

    override fun draw(canvas: Canvas, text: CharSequence?, start: Int, end: Int, x: Float, top: Int, y: Int, bottom: Int, paint: Paint) {
        val fm = paint.fontMetricsInt
        val transY: Int = (y + fm.descent + y + fm.ascent) / 2 - targetView.measuredHeight / 2

        canvas.save()
        canvas.translate(x, transY.toFloat())
        targetView.draw(canvas)
        canvas.restore()
    }
}