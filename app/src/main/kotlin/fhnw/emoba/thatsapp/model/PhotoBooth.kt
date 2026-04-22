package fhnw.emoba.thatsapp.model

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import fhnw.emoba.thatsapp.data.CameraAppConnector

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue


class PhotoBooth(private val cameraAppConnector: CameraAppConnector) {

    var photo by mutableStateOf<Bitmap?>(null)

    var notificationMessage by mutableStateOf("")

    fun takePhoto(onSuccess:  (Bitmap) -> Unit) {
        cameraAppConnector.getBitmap(onSuccess  = { photo = it
                                                    onSuccess(photo!!)
                                                    println("Bild wurde erstellt")
                                                  },
                                     onCanceled = { notificationMessage = "Kein neues Bild" })
    }
}