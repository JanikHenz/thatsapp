package fhnw.emoba.thatsapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import fhnw.emoba.thatsapp.data.Chat
import fhnw.emoba.thatsapp.data.Screens
import fhnw.emoba.thatsapp.model.ThatsAppModel


@Composable
fun AppUI(model: ThatsAppModel) {
    val currentScreen = model.currentScreen.value
    val currentChat = model.currentChat.value
    val currentUserName = model.currentUsername.value
    val currentProfilePic = model.currentProfilePic.value
    val chats = model.chats
    val profilePics = model.profilePics
    val userID = model.userId
    val alreadyJoined = model.alreadyJoined.value

    Scaffold(
        topBar = {
            TopBar(
                currentScreen = currentScreen,
                onLeftIconClick = { model.handleClickOnArrow() },
                profilePic = currentProfilePic,
                onRightIconClick = { model.handeClickOnProfile() },
                currentChat = currentChat,
                alreadyJoined = alreadyJoined
            )
        },
        bottomBar = {
            if (currentScreen == Screens.InChat) {
                BottomBar(
                    onSendMessage = { text -> model.handleOnSendMessage(text) },
                    onSendLocation = { model.handleSendLocation()},
                    onSendImage = { model.handleSendPhoto() }
                )
            }
        },
        content = { padding ->
            when (currentScreen) {
                Screens.AllChats -> AllChats(padding = padding, chats = chats, onChatClick = { chat -> model.handleClickOnChat(chat) })
                Screens.InChat -> InChat(chat = currentChat, padding = padding, userID = userID, onReceiveGeoPosition = {geoPosition -> model.handleClickOnCoordinates(geoPosition)})
                Screens.Profile -> Profile(padding = padding, userName = currentUserName, profilePic = currentProfilePic, onUsernameChange = { username -> model.handleUserChanges(username, currentProfilePic) }, onProfilePicSelected = { imgId -> model.handleUserChanges(currentUserName, imgId) }, profilePics = profilePics, onJoinGroupClick = {model.handleOnJoinGroup()}, alreadyJoined = alreadyJoined)
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    currentScreen: Screens,
    onLeftIconClick: () -> Unit,
    profilePic: Int,
    onRightIconClick: () -> Unit,
    currentChat: Chat?,
    alreadyJoined: Boolean
) {
    TopAppBar(
        colors = TopAppBarColors(
            containerColor = Color.Black,
            scrolledContainerColor = Color.Red,
            navigationIconContentColor = Color.Black,
            titleContentColor = Color.White,
            actionIconContentColor = Color.Red
        ),
        title = {
            if (currentScreen != Screens.InChat) {
                Text(currentScreen.title)
            } else {
                currentChat?.let { Text(it.userName.value) }
            }
        },
        navigationIcon = {
            if (currentScreen != Screens.AllChats && alreadyJoined) {
                IconButton(onClick = onLeftIconClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        tint = Color(1, 180, 228),
                        contentDescription = "Back"
                    )
                }
            }
            // Removed "null", since an empty block or a Spacer could be used
            // else { Spacer(modifier = Modifier.width(0.dp)) }
        },
        actions = {
            IconButton(onClick = onRightIconClick) {
                if (currentScreen == Screens.AllChats) {
                    ProfilePic(profilePic)
                } else {
                    currentChat?.let {
                        ProfilePic(it.profilePic.toInt())
                    }
                }
            }
        }
    )
}

@Composable
fun ProfilePic(
    imgId: Int,
    size: Dp = 60.dp,
    borderColor: Color = Color.Gray,
    borderWidth: Dp = 2.dp
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .border(borderWidth, borderColor, CircleShape)
    ) {
        Image(
            painter = painterResource(id = imgId),
            contentDescription = "App Logo",
            modifier = Modifier.fillMaxSize()
        )
    }
}
