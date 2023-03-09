package com.saaei12092021.office.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import android.util.TypedValue
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.saaei12092021.office.R

fun Context.bitmapDescriptorFromVector(
    vectorResId: Int,
    text: String,
): BitmapDescriptor? {
    return ContextCompat.getDrawable(this, vectorResId)?.mutate()?.run {
        setBounds(0, 0, intrinsicWidth, intrinsicHeight)
        val bitmap = this.toBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        val textPaint = Paint()
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.textSize = spToPx(9)
        textPaint.color = Color.WHITE
        val xPos = canvas.width / 2
        val yPos = canvas.height / 2.2
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                textPaint.typeface = resources.getFont(R.font.cairo_regular)
            }catch (e : Exception){}
        }
        canvas.drawText(text, xPos.toFloat(), yPos.toFloat(), textPaint)
//        draw(canvas)
        BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}

fun Context.spToPx(sp: Int): Float {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP,
        sp.toFloat(),
        this.resources.displayMetrics
    )
}
