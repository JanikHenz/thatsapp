package fhnw.emoba.thatsapp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fhnw.emoba.thatsapp.data.Chat
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card

@Composable
fun AllChats(padding:  PaddingValues, chats: List<Chat>, onChatClick: (Chat) -> Unit) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(padding).background(Color.Red)
    ) {
        items(chats) { chat ->
            ChatItem(chat = chat, onClick = { onChatClick(chat) })
        }
    }
}

@Composable
fun ChatItem(chat: Chat, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 0.dp, top = 8.dp, end = 0.dp, bottom = 8.dp),
        shape = RoundedCornerShape(8)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ProfilePic(
                imgId = chat.profilePic.toInt()
            )
            Text(
                text = chat.userName.value,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f).padding(20.dp)
            )
        }
    }
}
