package fhnw.emoba.thatsapp

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import fhnw.emoba.EmobaApp
import fhnw.emoba.modules.module09.gps.data.GPSConnector
import fhnw.emoba.thatsapp.data.CameraAppConnector
import fhnw.emoba.thatsapp.model.FileIOModel
import fhnw.emoba.thatsapp.model.GpsModel
import fhnw.emoba.thatsapp.model.PhotoBooth
import fhnw.emoba.thatsapp.model.ThatsAppModel
import fhnw.emoba.thatsapp.ui.AppUI


object ThatsApp : EmobaApp {
    private lateinit var model: ThatsAppModel

    override fun initialize(activity: ComponentActivity) {
        val cameraAppConnector = CameraAppConnector(activity)
        val photoBooth = PhotoBooth(cameraAppConnector)
        val fileIOModel = FileIOModel(activity)
        val gpsConnector = GPSConnector(activity)
        val gpsModel= GpsModel(activity, gpsConnector)
        model = ThatsAppModel(activity,gpsModel,photoBooth,fileIOModel)
    }

    @Composable
    override fun CreateUI() {
        AppUI(model)
    }

}

