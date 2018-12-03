package org.uqbar.prestamos.util


import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.graphics.RectF
import android.net.Uri
import android.provider.ContactsContract
import android.util.Log
import android.widget.ImageView

import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream

import org.uqbar.prestamos.model.Contacto

/**
 * Created by fernando on 10/27/16.
 */
object ImageUtil {

    internal var DEFAULT_CONTACT_URI = "defaultContact.png"

    /**
     * Si la imagen es de un proyecto debe estar en el directorio assets (o bien un directorio ubicable)
     */
    fun convertToImage(activity: Activity, path: String): ByteArray {
        val inputFoto = activity.assets.open(path)
        return convertToByteArray(inputFoto)
    }

    fun convertToByteArray(inputFoto: InputStream): ByteArray {
        val bmpFoto = BitmapFactory.decodeStream(inputFoto)
        val foto = ByteArrayOutputStream()
        bmpFoto.compress(Bitmap.CompressFormat.PNG, 100, foto)
        return foto.toByteArray()
    }

    /**
     * Si la imagen es de un proyecto debe estar en el directorio assets (o bien un directorio ubicable)
     */
    fun convertToImage(activity: Activity, uri: Uri): ByteArray {
        val fotoStream = ContactsContract.Contacts.openContactPhotoInputStream(activity.contentResolver, uri)
        if (fotoStream == null) {
            Log.w("Librex", "Esta URI no fue encontrada: $uri")
            return convertToImage(activity, DEFAULT_CONTACT_URI)
        }
        val inputFoto = BufferedInputStream(fotoStream)
        return convertToByteArray(inputFoto)
    }


    /** Gracias a https://inducesmile.com/android/how-to-make-circular-imageview-and-rounded-corner-imageview-in-android/  */
    fun getRoundedCornerBitmap(bitmap: Bitmap, pixels: Int): Bitmap {
        val output = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)

        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, bitmap.width, bitmap.height)
        val rectF = RectF(rect)
        val roundPx = pixels.toFloat()

        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)

        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        canvas.drawBitmap(bitmap, rect, rect, paint)

        return output
    }

    fun assignImage(contacto: Contacto, imgContacto: ImageView) {
        val fotoContacto = contacto.foto
        if (fotoContacto !== null) {
            val bm = BitmapFactory.decodeByteArray(fotoContacto, 0, fotoContacto.size)
            imgContacto.setImageBitmap(getRoundedCornerBitmap(bm, 50))
        }
    }

}