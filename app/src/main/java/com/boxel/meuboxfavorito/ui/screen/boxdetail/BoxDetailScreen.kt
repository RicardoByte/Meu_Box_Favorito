package com.boxel.meuboxfavorito.ui.screen.boxdetail

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.CollectionsBookmark
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.boxel.meuboxfavorito.models.BoxDetailData
import com.boxel.meuboxfavorito.models.BoxPrivacyType
import com.boxel.meuboxfavorito.models.BoxReviewItem
import com.boxel.meuboxfavorito.models.TagStyle
import com.boxel.meuboxfavorito.ui.navigation.AppBottomBar
import com.boxel.meuboxfavorito.ui.view.BoxDetailViewModel

@Composable
fun BoxDetailScreen(
    boxId: Int,
    onBack: () -> Unit,
    onMovieClick: (Int) -> Unit = {},
    onDiscussionClick: (reviewId: Int) -> Unit = {},
    onNavigate: (String) -> Unit = {},
    viewModel: BoxDetailViewModel = viewModel()
) {
    LaunchedEffect(boxId) {
        viewModel.loadBox(boxId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val boxData = uiState.boxData

    if (boxData == null) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF0F0F11)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text("Carregando Box...", color = Color(0xFFC8A97E))
            }
        }
        return
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0F0F11)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ─── Top Bar ──────────────────────────────────────────────
            BoxTopBar(
                onBack = onBack,
                onShare = { /* Compartilhar */ }
            )

            // ─── Seletor de Demonstração (Pública, Privada, Colaborativa) ─
            BoxTypeDemoSelector(
                currentBoxId = uiState.boxId,
                onSelectBox = { selectedId ->
                    viewModel.loadBox(selectedId)
                }
            )

            // ─── Conteúdo Principal com Rolagem ────────────────────────
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // ─── 1. Header da Box (Criador, Título, Descrição, Ações)
                item {
                    BoxHeaderSection(
                        box = boxData,
                        isSaved = uiState.isSaved,
                        onToggleSave = {
                            viewModel.toggleSave()
                        }
                    )
                }

                // ─── 2. Banner de Aviso de Privacidade (se privada ou colaborativa)
                item {
                    PrivacyNoticeCard(privacyType = boxData.privacyType)
                }

                // ─── 3. Título da Seção "Resenhas & Debates" e Filtros
                item {
                    ResenhasSectionHeader(
                        totalReviews = boxData.reviews.size,
                        selectedFilter = uiState.selectedFilter,
                        onSelectFilter = { viewModel.onFilterChange(it) }
                    )
                }

                // ─── 4. Lista de Resenhas da Box
                items(
                    items = boxData.reviews,
                    key = { it.id }
                ) { review ->
                    ReviewCard(
                        review = review,
                        onLikeClick = {
                            viewModel.toggleLikeReview(review.id)
                        },
                        onParticiparClick = {
                            onDiscussionClick(review.id)
                        }
                    )
                }

                // ─── 5. Banner Inferior "Gostou da seleção desta Box?"
                item {
                    BoxBottomCtaBanner(
                        privacyType = boxData.privacyType,
                        onActionClick = {
                            // Ação contextual: sugerir filme ou adicionar nota
                        }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }

            // ─── Barra de Navegação Inferior ──────────────────────────
            AppBottomBar(
                selectedRoute = "my_boxes",
                onNavigate = onNavigate
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTES DE UI
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun BoxTopBar(
    onBack: () -> Unit,
    onShare: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Voltar",
                tint = Color(0xFFEDE7DE)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        IconButton(onClick = onShare) {
            Icon(
                imageVector = Icons.Outlined.Share,
                contentDescription = "Compartilhar",
                tint = Color(0xFFEDE7DE)
            )
        }

        IconButton(onClick = { /* Opções extras */ }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Mais opções",
                tint = Color(0xFFEDE7DE)
            )
        }
    }
}

@Composable
private fun BoxTypeDemoSelector(
    currentBoxId: Int,
    onSelectBox: (Int) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF161619),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26262B))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(
                text = "DEMONSTRAÇÃO DE BOXES:",
                color = Color(0xFF8C867C),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    DemoChip(
                        label = "1. Comédia (Pública)",
                        icon = Icons.Default.Public,
                        selected = currentBoxId == 1,
                        onClick = { onSelectBox(1) }
                    )
                }
                item {
                    DemoChip(
                        label = "2. Sci-Fi (Privada)",
                        icon = Icons.Default.Lock,
                        selected = currentBoxId == 2,
                        onClick = { onSelectBox(2) }
                    )
                }
                item {
                    DemoChip(
                        label = "3. Insta Cult (Colab)",
                        icon = Icons.Default.Groups,
                        selected = currentBoxId == 3,
                        onClick = { onSelectBox(3) }
                    )
                }
                item {
                    DemoChip(
                        label = "4. Terror A24 (Pública)",
                        icon = Icons.Default.Public,
                        selected = currentBoxId == 4,
                        onClick = { onSelectBox(4) }
                    )
                }
                item {
                    DemoChip(
                        label = "5. Animações (Colab)",
                        icon = Icons.Default.Groups,
                        selected = currentBoxId == 5,
                        onClick = { onSelectBox(5) }
                    )
                }
            }
        }
    }
}

@Composable
private fun DemoChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        color = if (selected) Color(0xFFFFB82E) else Color(0xFF202024),
        border = if (!selected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF33333A)) else null
    ) {
        Row(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (selected) Color(0xFF1A1209) else Color(0xFFBEB7AE),
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = if (selected) Color(0xFF1A1209) else Color(0xFFEDE7DE),
                fontSize = 11.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}

@Composable
private fun BoxHeaderSection(
    box: BoxDetailData,
    isSaved: Boolean,
    onToggleSave: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {

        // ─── Autor e Tag de Curadoria ─────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar com indicador
            Box(
                modifier = Modifier.size(48.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(Color(0xFF23232A))
                        .border(1.dp, Color(0xFF3B3B45), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    if (box.creatorAvatar != null) {
                        AsyncImage(
                            model = box.creatorAvatar,
                            contentDescription = box.creatorName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = box.creatorName.first().toString(),
                            color = Color(0xFFFFB82E),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }

                // Indicador circular no canto inferior
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFB82E))
                        .border(1.5.dp, Color(0xFF0F0F11), CircleShape)
                        .align(Alignment.BottomEnd)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = box.creatorName,
                        color = Color(0xFFEDE7DE),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    // Badge de Curadoria / Tipo de Box
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = when (box.privacyType) {
                            BoxPrivacyType.PUBLICA -> Color(0xFF262016)
                            BoxPrivacyType.PRIVADA -> Color(0xFF2A2215)
                            BoxPrivacyType.COLABORATIVA -> Color(0xFF14223A)
                        },
                        border = androidx.compose.foundation.BorderStroke(
                            0.8.dp,
                            when (box.privacyType) {
                                BoxPrivacyType.PUBLICA -> Color(0xFF6B5328)
                                BoxPrivacyType.PRIVADA -> Color(0xFF7A5F22)
                                BoxPrivacyType.COLABORATIVA -> Color(0xFF2E4B7A)
                            }
                        )
                    ) {
                        Text(
                            text = box.badgeCuradoria,
                            color = when (box.privacyType) {
                                BoxPrivacyType.PUBLICA -> Color(0xFFDEB878)
                                BoxPrivacyType.PRIVADA -> Color(0xFFFFB82E)
                                BoxPrivacyType.COLABORATIVA -> Color(0xFF7CA6FF)
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.sp,
                            letterSpacing = 0.8.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = box.creatorHandle,
                        color = Color(0xFFFFB82E),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = box.updatedTime,
                        color = Color(0xFF7A7368),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ─── Título Principal da Box ──────────────────────────────
        Text(
            text = box.title,
            color = Color(0xFFEDE7DE),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp,
            lineHeight = 30.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ─── Descrição da Box ─────────────────────────────────────
        Text(
            text = box.description,
            color = Color(0xFFB5AFA4),
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 13.sp,
            lineHeight = 19.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // ─── Estatísticas da Box (Filmes, Visualizações, Salvos) ───
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Movie,
                    contentDescription = null,
                    tint = Color(0xFF8C867C),
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = "${box.filmesCount} filmes",
                    color = Color(0xFF8C867C),
                    fontSize = 12.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Visibility,
                    contentDescription = null,
                    tint = Color(0xFF8C867C),
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = box.viewsCount,
                    color = Color(0xFF8C867C),
                    fontSize = 12.sp
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.BookmarkBorder,
                    contentDescription = null,
                    tint = Color(0xFF8C867C),
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(5.dp))
                Text(
                    text = box.savesCount,
                    color = Color(0xFF8C867C),
                    fontSize = 12.sp
                )
            }
        }

        // ─── Capas dos Filmes da Box ──────────────────────────────
        if (box.filmes.isNotEmpty()) {
            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = "FILMES NESTA BOX (${box.filmes.size}):",
                color = Color(0xFF8C867C),
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(box.filmes) { movie ->
                    Column(
                        modifier = Modifier.width(68.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier
                                .width(68.dp)
                                .height(96.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF1E1E22),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2C35))
                        ) {
                            if (!movie.poster_path.isNullOrBlank()) {
                                AsyncImage(
                                    model = "https://image.tmdb.org/t/p/w500${movie.poster_path}",
                                    contentDescription = movie.title,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = movie.title,
                            color = Color(0xFFBEB7AE),
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ─── Linha de Ações (Salvar Box, Compartilhar, Coleção) ────
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botão Principal "Salvar Box"
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .clickable { onToggleSave() },
                shape = RoundedCornerShape(10.dp),
                color = if (isSaved) Color(0xFF2A2314) else Color(0xFFFFB82E),
                border = if (isSaved) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB82E)) else null
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = if (isSaved) Color(0xFFFFB82E) else Color(0xFF1B1409),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isSaved) "Box Salva" else "Salvar Box",
                        color = if (isSaved) Color(0xFFFFB82E) else Color(0xFF1B1409),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }

            // Botão Compartilhar
            Surface(
                modifier = Modifier
                    .size(46.dp)
                    .clickable { /* Compartilhar */ },
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF1A1A1E),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2C33))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Compartilhar",
                        tint = Color(0xFFD1CCC4),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Botão Coleção
            Surface(
                modifier = Modifier
                    .size(46.dp)
                    .clickable { /* Coleções */ },
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF1A1A1E),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2C33))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.CollectionsBookmark,
                        contentDescription = "Coleção",
                        tint = Color(0xFFD1CCC4),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PrivacyNoticeCard(privacyType: BoxPrivacyType) {
    when (privacyType) {
        BoxPrivacyType.PRIVADA -> {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF1C1811),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF3B2E15))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFFFFB82E),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Esta Box é Privada. Apenas você pode ver suas resenhas, notas e anotações pessoais.",
                        color = Color(0xFFDEB878),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
        BoxPrivacyType.COLABORATIVA -> {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF111722),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1F314D))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Groups,
                        contentDescription = null,
                        tint = Color(0xFF7CA6FF),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Box Colaborativa: Membros convidados e a comunidade compartilham resenhas e debatem coletivamente.",
                        color = Color(0xFFA6C5FF),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
        BoxPrivacyType.PUBLICA -> {
            // Em box pública não precisa de alerta chamativo
        }
    }
}

@Composable
private fun ResenhasSectionHeader(
    totalReviews: Int,
    selectedFilter: String,
    onSelectFilter: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Outlined.ChatBubbleOutline,
            contentDescription = null,
            tint = Color(0xFFFFB82E),
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Text(
                text = "Resenhas & Debates",
                color = Color(0xFFEDE7DE),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 17.sp
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Chips de Filtro
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = Color(0xFF1A1A1E),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2C33))
        ) {
            Text(
                text = "$totalReviews ativas",
                color = Color(0xFF948D82),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        Surface(
            modifier = Modifier.clickable { onSelectFilter("Mais Relevantes") },
            shape = RoundedCornerShape(12.dp),
            color = if (selectedFilter == "Mais Relevantes") Color(0xFF282420) else Color.Transparent,
            border = if (selectedFilter == "Mais Relevantes") {
                androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB82E))
            } else null
        ) {
            Text(
                text = "Relevantes",
                color = if (selectedFilter == "Mais Relevantes") Color(0xFFFFB82E) else Color(0xFF7A7368),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun ReviewCard(
    review: BoxReviewItem,
    onLikeClick: () -> Unit,
    onParticiparClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onParticiparClick() },
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF151518),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF232329))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {

            // ─── Header da Resenha (Autor + Tag) ──────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF222228)),
                    contentAlignment = Alignment.Center
                ) {
                    if (review.authorAvatar != null) {
                        AsyncImage(
                            model = review.authorAvatar,
                            contentDescription = review.authorName,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = review.authorName.first().toString(),
                            color = Color(0xFFFFB82E),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.authorName,
                        color = Color(0xFFEDE7DE),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${review.authorHandle} • ${review.timeAgo}",
                        color = Color(0xFF7A7368),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp
                    )
                }

                // Tag Estilizada
                ReviewTagChip(tagText = review.tagText, tagStyle = review.tagStyle)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ─── Mini Card do Filme Vinculado ─────────────────────
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF1A1A1E),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF25252C))
            ) {
                Row(
                    modifier = Modifier.padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Pôster do filme
                    Box(
                        modifier = Modifier
                            .width(38.dp)
                            .height(52.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF272730)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (review.moviePosterUrl != null) {
                            AsyncImage(
                                model = review.moviePosterUrl,
                                contentDescription = review.movieTitle,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Movie,
                                contentDescription = null,
                                tint = Color(0xFF6B655B),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = review.movieTitle,
                            color = Color(0xFFEDE7DE),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = review.movieInfo,
                            color = Color(0xFF8E887E),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        // Estrelas de Avaliação
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            repeat(5) { index ->
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = if (index < review.rating.toInt()) Color(0xFFFFB82E) else Color(0xFF383842),
                                    modifier = Modifier.size(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "%.1f".format(review.rating),
                                color = Color(0xFFFFB82E),
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ─── Título da Resenha ────────────────────────────────
            Text(
                text = review.title,
                color = Color(0xFFEDE7DE),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // ─── Trecho / Corpo da Resenha ────────────────────────
            Text(
                text = review.body,
                color = Color(0xFFBDB7AC),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 12.sp,
                lineHeight = 18.sp
            )

            // Hashtags (se houver)
            if (review.hashtags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    review.hashtags.forEach { tag ->
                        Text(
                            text = tag,
                            color = Color(0xFF857E74),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ─── Rodapé da Resenha: Likes, Comentários e Botão de Debate ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Curtidas
                Row(
                    modifier = Modifier
                        .clickable { onLikeClick() }
                        .padding(end = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (review.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Curtir",
                        tint = if (review.isLiked) Color(0xFFE05C5C) else Color(0xFF948D82),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = review.likesCount.toString(),
                        color = Color(0xFF948D82),
                        fontSize = 12.sp
                    )
                }

                // Contador de Comentários
                Row(
                    modifier = Modifier.padding(end = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comentários",
                        tint = Color(0xFF948D82),
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = review.commentsCount.toString(),
                        color = Color(0xFF948D82),
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Botão "Participar do debate ->"
                Surface(
                    modifier = Modifier.clickable { onParticiparClick() },
                    shape = RoundedCornerShape(50),
                    color = Color(0xFF262118),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB82E))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Participar do debate",
                            color = Color(0xFFFFB82E),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "→",
                            color = Color(0xFFFFB82E),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ReviewTagChip(tagText: String, tagStyle: TagStyle) {
    val (bgColor, textColor, borderColor) = when (tagStyle) {
        TagStyle.HIGHLIGHT -> Triple(Color(0xFF381A5E), Color(0xFFD6A6FF), Color(0xFF5A2C94))
        TagStyle.TOP -> Triple(Color(0xFF0F3B35), Color(0xFF3DDAB4), Color(0xFF1E6359))
        TagStyle.VIRAL -> Triple(Color(0xFF451915), Color(0xFFFF784E), Color(0xFF702821))
        TagStyle.OPEN -> Triple(Color(0xFF1C222E), Color(0xFF98A6C2), Color(0xFF2E394E))
        TagStyle.PRIVATE -> Triple(Color(0xFF332612), Color(0xFFFFB82E), Color(0xFF5C4520))
        TagStyle.COLLABORATOR -> Triple(Color(0xFF152647), Color(0xFF7CA6FF), Color(0xFF274278))
    }

    Surface(
        shape = RoundedCornerShape(50),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(0.8.dp, borderColor)
    ) {
        Text(
            text = tagText,
            color = textColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
        )
    }
}

@Composable
private fun BoxBottomCtaBanner(
    privacyType: BoxPrivacyType,
    onActionClick: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF161619),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF25252C))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Movie,
                contentDescription = null,
                tint = Color(0xFFFFB82E),
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = when (privacyType) {
                    BoxPrivacyType.PRIVADA -> "Deseja registrar outra resenha?"
                    BoxPrivacyType.COLABORATIVA -> "Quer sugerir filmes ou resenhas nesta Box?"
                    BoxPrivacyType.PUBLICA -> "Gostou da seleção desta Box?"
                },
                color = Color(0xFFEDE7DE),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = when (privacyType) {
                    BoxPrivacyType.PRIVADA -> "Adicione suas impressões secretas sobre outros filmes para manter seu diário sempre atualizado."
                    BoxPrivacyType.COLABORATIVA -> "Esta box é aberta para a comunidade. Envie sugestões de títulos e compartilhe seus debates."
                    BoxPrivacyType.PUBLICA -> "Adicione filmes que marcaram suas tardes e compartilhe com outros cinéfilos da comunidade."
                },
                color = Color(0xFF8C867C),
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .clickable { onActionClick() },
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFFB82E)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = when (privacyType) {
                            BoxPrivacyType.PRIVADA -> "+ Adicionar Nova Resenha Privada"
                            BoxPrivacyType.COLABORATIVA -> "+ Sugerir Filme / Resenha na Box"
                            BoxPrivacyType.PUBLICA -> "+ Sugerir Filme na Box"
                        },
                        color = Color(0xFF1A1209),
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
