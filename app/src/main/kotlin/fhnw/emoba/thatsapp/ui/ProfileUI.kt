package fhnw.emoba.thatsapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items

@Composable
fun Profile(
    padding: PaddingValues,
    userName: String,
    profilePic: Int,
    onUsernameChange: (String) -> Unit,
    onProfilePicSelected: (Int) -> Unit,
    profilePics: List<Int>,
    onJoinGroupClick: () -> Unit,
    alreadyJoined: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Benutzername-Eingabe
        TextField(
            value = userName,
            onValueChange = { onUsernameChange(it) },
            label = { Text("Benutzername") },
            modifier = Modifier.fillMaxWidth()
        )

        // Auswahl der Profilbilder
        Text("Profilbild auswählen", style = MaterialTheme.typography.titleSmall)

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(profilePics) { p ->
                ProfilePicOption(
                    profilePic = p,
                    isSelected = p == profilePic,
                    onClick = { onProfilePicSelected(p) }
                )
            }
        }

        // Spacer zum Ausdehnen des Layouts
        Spacer(modifier = Modifier.weight(1f))
        if (!alreadyJoined){
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp), // Optional: Abstand zum unteren Rand
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "JOIN GROUP",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clip(MaterialTheme.shapes.medium)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .clickable { onJoinGroupClick() }
                        .padding(vertical = 12.dp, horizontal = 24.dp)
                )
            }
        }
    }
}

@Composable
fun ProfilePicOption(
    profilePic: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(64.dp)
            .clip(CircleShape)
            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.3f) else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = profilePic),
            contentDescription = "Profilbild",
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
        )
    }
}
