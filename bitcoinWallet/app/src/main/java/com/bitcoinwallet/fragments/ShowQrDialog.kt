package com.bitcoinwallet.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.bitcoinwallet.R
import kotlinx.android.synthetic.main.dialog_show_qr_code.*
import net.glxn.qrgen.android.QRCode

class ShowQrDialog(val address: String) : DialogFragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_show_qr_code, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog.window.setBackgroundDrawable(ColorDrawable(0))

        closeBtn.setOnClickListener { dismiss() }

        val bitmapQR = QRCode.from(address).withSize(200, 200).bitmap()

        val roundedQR = getRoundedCornersOnBitmap(bitmapQR,20)

        img_view_qr_code.setImageBitmap(roundedQR)

        txt_address.text = address

        // copy address on click
        qrAddressLayout.setOnClickListener {
            Toast.makeText(context, "Address Copied", Toast.LENGTH_SHORT).show()
            val clipboardManager = ContextCompat.getSystemService(context!!, ClipboardManager::class.java)
            val clip = ClipData.newPlainText("BTC ADDRESS", address)
            clipboardManager?.primaryClip = clip
        }
    }

    private fun getRoundedCornersOnBitmap(bitmap: Bitmap, pixels: Int) : Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val paint = Paint()
        val shader = BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        paint.isAntiAlias = true
        paint.shader = shader
        val rect = Rect(0,0, bitmap.width,bitmap.height)
        val rectf = RectF(rect)

        canvas.drawRoundRect(rectf, pixels.toFloat(), pixels.toFloat(), paint)
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)
        return output
    }
}