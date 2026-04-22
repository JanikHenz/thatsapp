package fhnw.emoba.thatsapp.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import fhnw.emoba.thatsapp.data.Chat
import fhnw.emoba.thatsapp.data.Message
import kotlin.io.encoding.ExperimentalEncodingApi
import android.util.Base64
import fhnw.emoba.modules.module09.gps.data.GeoPosition
import java.io.ByteArrayInputStream

@Composable
fun InChat(
    chat: Chat?, // Spezifischer Chat
    padding: PaddingValues,
    userID: String,
    onReceiveGeoPosition: (GeoPosition) -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(Color.Blue)
    ) {
        // Nachrichtenliste
        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(chat?.messages ?: emptyList()) { message ->
                MessageItem(message, userID, onReceiveGeoPosition)
            }
        }
    }
}

@OptIn(ExperimentalEncodingApi::class)
@Composable
fun MessageItem(message: Message, userID: String,onReceiveGeoPosition: (GeoPosition) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
        horizontalArrangement = if (message.sender == userID) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .background(
                    if (message.sender == userID) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(12.dp)
                )
                .padding(8.dp)
        ) {
            if (message.geoPosition.isNotEmpty()) {
                Text(
                    text = message.geoPosition,
                    modifier = Modifier.clickable { if (message.geoPosition.isNotEmpty()){
                        message.gps?.let { onReceiveGeoPosition(it) }
                    }
                    },
                    color = if (message.sender == userID) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                )
            } else if (message.imageUrl.isNotEmpty()) {
                Column {
                    Text(text = message.imageUrl)
                    Text(text = message.img.toString())
                    message.img?.asImageBitmap()?.let { Image(bitmap = it, contentDescription = null) }
                }
            } else {
                Text(
                    text = message.message,
                    color = if (message.sender == userID) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
                )
            }

        }
    }
}


@Composable
fun BottomBar(
    onSendMessage: (String) -> Unit,
    onSendImage: () -> Unit,
    onSendLocation: () -> Unit
) {
    var text by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Textfeld für Nachrichten
        TextField(
            value = text,
            onValueChange = { text = it },
            placeholder = { Text("Nachricht eingeben...") },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = Color.DarkGray,
                focusedContainerColor = Color.DarkGray,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                unfocusedPlaceholderColor = Color.White
            )
        )

        // Button für Kamera
        Button(
            onClick = onSendImage,
            modifier = Modifier.padding(end = 4.dp)
        ) {
            Text("📷")
        }

        // Button für Standort
        Button(
            onClick = onSendLocation,
            modifier = Modifier.padding(end = 4.dp)
        ) {
            Text("📍")
        }

        // Button zum Senden einer Textnachricht
        Button(
            onClick = {
                if (text.isNotBlank()) {
                    onSendMessage(text)
                    text = "" // Textfeld leeren
                }
            }
        ) {
            Text("Senden")
        }
    }
}
