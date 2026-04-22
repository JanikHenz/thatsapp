package fhnw.emoba.thatsapp.data

import android.graphics.Bitmap
import androidx.compose.runtime.MutableState
import fhnw.emoba.modules.module09.gps.data.GeoPosition
import org.json.JSONObject

enum class Screens(val title: String) {
    AllChats("AllChats"),
    InChat("InChat"),
    Profile("Profile")
}

data class Chat(
    var userName: MutableState<String>,
    var profilePic: String,
    val messages: MutableList<Message>,
    val topic: String,
    val userID: String
)

data class Message(
    val sender: String,
    val message: String,
    val imageUrl: String,
    val geoPosition: String,
    var img: Bitmap?,
    var gps: GeoPosition?,
    var profilePic: String
) : Messages {

    constructor(json: JSONObject) : this(
        json.getString("sender"),
        json.getString("message"),
        json.getString("imageUrl"),
        json.getString("geoPosition"),
        img = null,
        gps = null,
        json.getString("profilePic")
    )

    override fun asJsonString(): String {
        return """
            {"sender":  "$sender", 
             "message": "$message",
              "imageUrl": "$imageUrl",
              "geoPosition": "$geoPosition",
              "profilePic": "$profilePic"
            }
            """
    }
}