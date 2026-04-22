package fhnw.emoba.thatsapp.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.ComponentActivity
import androidx.annotation.DrawableRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import fhnw.emoba.R
import fhnw.emoba.thatsapp.data.downloadBitmapFromFileIO
import fhnw.emoba.thatsapp.data.uploadBitmapToFileIO

class FileIOModel(val activity: ComponentActivity) {
    private val modelScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    var originalCrew: Bitmap = loadImage(R.drawable.agnes)


    var fileioURL          by mutableStateOf<String?>(null)
    var uploadInProgress   by mutableStateOf(false)

    var downloadedCrew     by mutableStateOf<Bitmap?>(null)
    var downloadInProgress by mutableStateOf(false)
    var downloadMessage    by mutableStateOf("")


    fun uploadToFileIO(onSuccess: (link: String) -> Unit) {
        uploadInProgress = true
        fileioURL = null
        modelScope.launch {
            uploadBitmapToFileIO(bitmap    = originalCrew,
                                 onSuccess = { fileioURL = it
                                     println("Bild wurde hochgeladen")
                                             onSuccess(fileioURL!!)},
                                 onError   = {_, _ -> })  //todo: was machen wir denn nun?
            uploadInProgress = false
        }
    }

    fun downloadFromFileIO(onSuccess: (bitmap: Bitmap) -> Unit){
        if(fileioURL != null){
            downloadedCrew = null
            downloadInProgress = true
            modelScope.launch {
                downloadBitmapFromFileIO(url       = fileioURL!!,
                                         onSuccess = { downloadedCrew = it
                                                        println("Bild wurde heruntergeladen")
                                                     onSuccess(downloadedCrew!!)},
                                         onDeleted = { downloadMessage = "File is deleted"
                                                        println("File is deleted")},
                                         onError   = { downloadMessage = "Connection failed"
                                                        println("Connection failed")})
                downloadInProgress = false
            }
        }
    }

    private fun loadImage(@DrawableRes id: Int) = BitmapFactory.decodeResource(activity.resources, id)

}


