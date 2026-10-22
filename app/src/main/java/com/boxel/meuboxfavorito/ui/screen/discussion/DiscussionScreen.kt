package com.boxel.meuboxfavorito.ui.screen.discussion

import androidx.compose.runtime.Composable
import com.boxel.meuboxfavorito.ui.screen.boxdetail.BoxDetailScreen

@Composable
fun DiscussionScreen(
    boxId: Int,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit = {}
) {
    BoxDetailScreen(
        boxId = boxId,
        onBack = onBack,
        onNavigate = onNavigate
    )
}
