package com.boxel.meuboxfavorito.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@Composable
fun AppBottomBar(
    selectedRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF202021))
            .padding(
                horizontal = 24.dp,
                vertical = 10.dp
            ),

        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {

        BottomBarItem(
            icon = Icons.Default.Explore,
            text = "Explore",
            selected = selectedRoute == "explore",
            onClick = {
                onNavigate("explore")
            }
        )

        BottomBarItem(
            icon = Icons.Default.Folder,
            text = "Minhas Boxes",
            selected = selectedRoute == "my_boxes",
            onClick = {
                onNavigate("my_boxes")
            }
        )

        BottomBarItem(
            icon = Icons.Default.Person,
            text = "Perfil",
            selected = selectedRoute == "profile",
            onClick = {
                onNavigate("profile")
            }
        )
    }
}


@Composable
private fun BottomBarItem(
    icon: ImageVector,
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {

    Column(
        modifier = Modifier.clickable {
            onClick()
        },

        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icon,
            contentDescription = text,

            tint = if (selected) {
                Color(0xFFEAD7B0)
            } else {
                Color(0xFFB7B0A7)
            },

            modifier = Modifier.size(22.dp)
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = text,

            color = if (selected) {
                Color(0xFFEAD7B0)
            } else {
                Color(0xFFB7B0A7)
            }
        )
    }
}