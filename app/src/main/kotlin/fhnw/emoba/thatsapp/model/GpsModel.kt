package fhnw.emoba.thatsapp.model

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import android.graphics.BitmapFactory
import androidx.activity.ComponentActivity
import androidx.annotation.DrawableRes
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.AndroidUriHandler
import fhnw.emoba.modules.module09.gps.data.GPSConnector
import fhnw.emoba.modules.module09.gps.data.GeoPosition

class GpsModel(
    private val activity: ComponentActivity,
    private val locator: GPSConnector
) {
    private val modelScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)


    val waypoint = mutableStateOf<GeoPosition?>(null)
    var notificationMessage by mutableStateOf("")

    fun rememberCurrentPosition(onNewLocation:      (geoPosition: GeoPosition) -> Unit) {
        modelScope.launch {
            locator.getLocation(
                onNewLocation = {
                    waypoint.value = it
                    onNewLocation(waypoint.value!!)
                    notificationMessage = "neuer Wegpunkt"
                },
                onFailure = {
                },
                onPermissionDenied = {
                    notificationMessage = "Keine Berechtigung."
                },
            )
        }
    }

    fun showOnMap(position: GeoPosition) =
        AndroidUriHandler(activity).openUri(position.asOpenStreetMapsURL())

    private fun loadImage(@DrawableRes id: Int) =
        BitmapFactory.decodeResource(activity.resources, id).asImageBitmap()

}