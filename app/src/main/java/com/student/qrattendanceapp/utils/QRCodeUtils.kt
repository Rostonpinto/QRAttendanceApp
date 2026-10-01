package com.student.qrattendanceapp.utils


import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import org.json.JSONObject

object QRCodeUtils {

    fun generateQRCode(sessionId: Int, classId: Int, expiryMinutes: Int = 5): Pair<Bitmap, String> {
        val generatedAt = System.currentTimeMillis()
        val expiresAt = generatedAt + (expiryMinutes * 60 * 1000)
        val qrData = JSONObject().apply {
            put("session_id", sessionId)
            put("class_id", classId)
            put("generated_at", generatedAt)
            put("expires_at", expiresAt)
        }.toString()
        val hints = mapOf(EncodeHintType.MARGIN to 2)
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(qrData, BarcodeFormat.QR_CODE, 512, 512, hints)
        val bitmap = Bitmap.createBitmap(512, 512, Bitmap.Config.RGB_565)
        for (x in 0 until 512) for (y in 0 until 512)
            bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
        return Pair(bitmap, qrData)
    }

    fun decodeQRData(qrString: String): QRData? {
        return try {
            val json = JSONObject(qrString)
            QRData(
                sessionId = json.getInt("session_id"),
                classId = json.getInt("class_id"),
                generatedAt = json.getLong("generated_at"),
                expiresAt = json.getLong("expires_at")
            )
        } catch (e: Exception) { null }
    }

    fun isQRExpired(expiresAt: Long) = System.currentTimeMillis() > expiresAt

    data class QRData(val sessionId: Int, val classId: Int, val generatedAt: Long, val expiresAt: Long)
}