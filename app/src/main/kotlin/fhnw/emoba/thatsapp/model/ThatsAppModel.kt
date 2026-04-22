package fhnw.emoba.thatsapp.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.runtime.*
import fhnw.emoba.R
import fhnw.emoba.thatsapp.data.Chat
import fhnw.emoba.thatsapp.data.Message
import fhnw.emoba.thatsapp.data.Screens
import java.util.UUID
import androidx.activity.ComponentActivity
import fhnw.emoba.modules.module09.gps.data.GeoPosition
import fhnw.emoba.thatsapp.data.MqttConnector
import org.json.JSONObject


class ThatsAppModel(
    private val activity: ComponentActivity,
    private val gpsModel: GpsModel,
    private val photoBooth: PhotoBooth,
    private val fileIOModel: FileIOModel
) {
    val userId = UUID.randomUUID().toString()

    var subscribeSelf: String by mutableStateOf("fhnw/emoba/thats/" + userId)

    var currentChat = mutableStateOf<Chat?>(null)


    private val mqttBroker = "d33f461c9c344d6483ae4a162fcbb6c9.s1.eu.hivemq.cloud"
    private val mqttConnector = MqttConnector(mqttBroker)

    var publishJoin: String by mutableStateOf("fhnw/emoba/thats/join/" + userId)

    var subscribeJoins: String by mutableStateOf("fhnw/emoba/thats/join/#")

    var currentScreen = mutableStateOf(Screens.Profile)

    var currentUsername = mutableStateOf("User")
    var currentProfilePic = mutableStateOf(R.drawable.empty_profile_pic)
    val chats = mutableStateListOf<Chat>()

    var alreadyJoined = mutableStateOf(false)

    val profilePics = listOf(
        R.drawable.empty_profile_pic,
        R.drawable.agnes,
        R.drawable.bob,
        R.drawable.edith,
        R.drawable.eduardo,
        R.drawable.gru,
        R.drawable.kevin,
        R.drawable.margo,
        R.drawable.minion_america,
        R.drawable.stuart
    )

    fun connect() {
        mqttConnector.connect(
            onConnectionFailed = {
                alreadyJoined.value = false
            },
            onConnectionSucceed = {
                subscribe(topic = subscribeJoins, onNewMessage = {
                    var receivedInfo = Message(it)

                    val newUserId = receivedInfo.sender

                    val existingChat = chats.find { chat -> chat.userID.contains(newUserId) }

                    if (existingChat == null && newUserId != userId) {
                        val newChat = Chat(
                            userID = newUserId,
                            topic = "fhnw/emoba/thats/" + newUserId,
                            messages = mutableStateListOf(),
                            profilePic = receivedInfo.profilePic,
                            userName = mutableStateOf(receivedInfo.message)
                        )
                        chats.add(newChat)
                    } else {

                        existingChat!!.userName = mutableStateOf(receivedInfo.message)
                        existingChat.profilePic = receivedInfo.profilePic
                        println("Chat mit User $newUserId existiert bereits.")
                    }
                })
                subscribe(topic = subscribeSelf, onNewMessage = {
                    var receivedMessage = Message(it)
                    val newUserId = receivedMessage.sender
                    val rightChat = chats.find { chat -> chat.userID.contains(newUserId) }
                    receivedMessage.img = handleReceivePhoto(receivedMessage)
                    handleReceiveLocation(receivedMessage)
                    println("TEEEEEEST ${receivedMessage.img.toString()}")
                    rightChat?.messages!!.add(receivedMessage)
                })
                publish(
                    topic = publishJoin,
                    retain = true,
                    sender = userId,
                    profilePic = currentProfilePic.value.toString(),
                    geoPosition = "",
                    imageURL = "",
                    message = currentUsername.value
                )
                alreadyJoined.value = true
            }
        )
    }

    fun subscribe(topic: String, onNewMessage: (JSONObject) -> Unit) {
        mqttConnector.subscribe(
            topic = topic,
            onNewMessage = { message -> onNewMessage(message) }
        )
    }


    fun publish(
        topic: String,
        retain: Boolean,
        sender: String = userId,
        profilePic: String = currentProfilePic.value.toString(),
        geoPosition: String,
        imageURL: String,
        message: String,
    ) {
        var ownMessage = Message(
            sender = sender,
            profilePic = profilePic,
            geoPosition = geoPosition,
            imageUrl = imageURL,
            message = message,
            img = null,
            gps = null
        )
        mqttConnector.publish(
            topic = topic,
            retain = retain,
            message = ownMessage,
            onPublished = {
                ownMessage.img = photoBooth.photo
                ownMessage.gps = gpsModel.waypoint.value
                currentChat.value?.messages?.add(ownMessage)
            })
    }

    fun handleOnSendMessage(text: String) {
            publish(
                topic = currentChat.value!!.topic,
                retain = false,
                message = text,
                imageURL = "",
                geoPosition = "",
                profilePic = "",
                sender = userId
            )
    }

    fun handleSendPhoto() {
        photoBooth.takePhoto(onSuccess = {
            fileIOModel.originalCrew = photoBooth.photo!!
            fileIOModel.uploadToFileIO(onSuccess = {
                publish(
                    topic = currentChat.value?.topic ?: "failed topic",
                    retain = false,
                    message = "",
                    imageURL = it,
                    geoPosition = "",
                    profilePic = "",
                    sender = userId
                )
                println("Bild wurde gesendet. ImageURL: +$it")
            })
        })
    }

    fun handleSendLocation() {
        gpsModel.rememberCurrentPosition(onNewLocation = {
            val geoPosition =
                "${gpsModel.waypoint.value?.longitude},${gpsModel.waypoint.value?.latitude},${gpsModel.waypoint.value?.altitude}"
            publish(
                topic = currentChat.value?.topic ?: "failed topic",
                retain = false,
                message = gpsModel.waypoint.value?.dms()!!,
                imageURL = "",
                geoPosition = geoPosition,
                profilePic = "",
                sender = userId
            )
        })
    }

    fun handleReceiveLocation(receivedMessage: Message) {
        if (receivedMessage.geoPosition.isNotEmpty()) {
            val split = receivedMessage.geoPosition.split(",")
            receivedMessage.gps = GeoPosition(
                split[0].toDouble(),
                split[1].toDouble(),
                split[2].toDouble()
            )
        }
    }

    fun handleClickOnCoordinates(geoPosition: GeoPosition){
        gpsModel.showOnMap(geoPosition)
    }

    fun handleReceivePhoto(receivedMessage: Message):Bitmap {
        var output: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

        if (receivedMessage.imageUrl.isNotEmpty()) {
            fileIOModel.fileioURL = receivedMessage.imageUrl
            println("Bild entdeckt")
            fileIOModel.downloadFromFileIO(onSuccess = {
                println("it: $it")
                output = it
            })
        }
        return output
    }

    fun handleClickOnArrow() {
        if (currentScreen.value == Screens.Profile) {
            publish(
                topic = publishJoin,
                retain = false,
                message = currentUsername.value,
                imageURL = "",
                geoPosition = "",
                profilePic = currentProfilePic.value.toString(),
                sender = userId
            )
        }
        currentScreen.value = Screens.AllChats
    }

    fun handeClickOnProfile() {
        if (currentScreen.value == Screens.AllChats) {
            currentScreen.value = Screens.Profile
        }
    }

    fun handleClickOnChat(chat: Chat) {
        currentScreen.value = Screens.InChat
        currentChat.value = chat
    }

    fun handleUserChanges(username: String, imgId: Int) {
        currentUsername.value = username
        currentProfilePic.value = imgId
    }

    fun handleOnJoinGroup() {
        connect()
        currentScreen.value = Screens.AllChats
    }
}

