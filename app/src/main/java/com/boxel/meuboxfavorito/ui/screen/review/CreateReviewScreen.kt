package com.boxel.meuboxfavorito.ui.screen.review

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.boxel.meuboxfavorito.models.BoxDetailData
import com.boxel.meuboxfavorito.models.Moviefmt
import com.boxel.meuboxfavorito.models.ReviewMovieSelection
import com.boxel.meuboxfavorito.ui.view.CreateReviewViewModel

@Composable
fun CreateReviewScreen(
    initialBoxId: Int,
    onBack: () -> Unit,
    onReviewPublished: (boxId: Int) -> Unit,
    viewModel: CreateReviewViewModel = viewModel()
) {
    LaunchedEffect(initialBoxId) {
        viewModel.initialize(initialBoxId)
    }

    val uiState by viewModel.uiState.collectAsState()
    var boxDropdownExpanded by remember { mutableStateOf(false) }
    var showAddTagField by remember { mutableStateOf(false) }
    var subtopicosEnabled by remember { mutableStateOf(true) }
    var visibilidadeExpanded by remember { mutableStateOf(false) }
    var visibilidadeSelecionada by remember { mutableStateOf("Público na Box e Feed") }

    fun publicar() {
        val publishedBoxId = viewModel.publishReview()
        if (publishedBoxId != null) {
            onReviewPublished(publishedBoxId)
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0F0F11)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            // ─── Top Bar ──────────────────────────────────────────
            CreateReviewTopBar(
                onCancel = onBack,
                onPublish = { publicar() }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ─── 1. Box de Destino ────────────────────────────────
                item {
                    DestinationBoxCard(
                        box = uiState.selectedBox ?: BoxDetailData(
                            id = 1,
                            title = "Noir Essentials",
                            creatorName = "Você",
                            creatorHandle = "@voce",
                            badgeCuradoria = "CURADORIA OFICIAL",
                            updatedTime = "agora",
                            description = "",
                            filmesCount = 0,
                            viewsCount = "0",
                            savesCount = "0",
                            privacyType = com.boxel.meuboxfavorito.models.BoxPrivacyType.PUBLICA
                        ),
                        allBoxes = uiState.allBoxes,
                        expanded = boxDropdownExpanded,
                        onExpandedChange = { boxDropdownExpanded = it },
                        onSelectBox = {
                            viewModel.onSelectBox(it)
                            boxDropdownExpanded = false
                        }
                    )
                }

                // ─── 2. Filme Selecionado ─────────────────────────────
                item {
                    SelectedMovieCard(
                        movie = uiState.selectedMovie,
                        onChangeMovie = { viewModel.setShowMovieSearchDialog(true) }
                    )
                }

                // ─── 3. Veredito & Classificação ──────────────────────
                item {
                    VerdictRatingCard(
                        rating = uiState.rating,
                        onRatingChange = { viewModel.onRatingChange(it) },
                        isObraPrima = uiState.isObraPrima,
                        onToggleObraPrima = { viewModel.toggleObraPrima() },
                        isFavorito = uiState.isFavorito,
                        onToggleFavorito = { viewModel.toggleFavorito() }
                    )
                }

                // ─── 4. Tese do Debate • Título Provocativo ──────────
                item {
                    DebateTitleSection(
                        title = uiState.debateTitle,
                        onTitleChange = { viewModel.onDebateTitleChange(it) }
                    )
                }

                // ─── 5. Sua Análise Crítica ───────────────────────────
                item {
                    AnalysisSection(
                        text = uiState.analysisText,
                        onTextChange = { viewModel.onAnalysisTextChange(it) },
                        hasSpoilers = uiState.hasSpoilers,
                        onToggleSpoilers = { viewModel.toggleSpoilers() },
                        hashtags = uiState.hashtags,
                        onRemoveTag = { viewModel.removeTag(it) },
                        newTagInput = uiState.currentTagInput,
                        onNewTagChange = { viewModel.onTagInputChange(it) },
                        showAddTagField = showAddTagField,
                        onToggleAddTag = { showAddTagField = !showAddTagField },
                        onAddTag = {
                            viewModel.addTag(uiState.currentTagInput)
                            showAddTagField = false
                        }
                    )
                }

                // ─── 6. Subtópicos e Visibilidade ─────────────────────
                item {
                    DebateSettingsCard(
                        subtopicosEnabled = subtopicosEnabled,
                        onSubtopicosChange = { subtopicosEnabled = it },
                        visibilidade = visibilidadeSelecionada,
                        visibilidadeExpanded = visibilidadeExpanded,
                        onVisibilidadeExpandedChange = { visibilidadeExpanded = it },
                        onSelectVisibilidade = {
                            visibilidadeSelecionada = it
                            visibilidadeExpanded = false
                        }
                    )
                }

                // ─── 7. Botão Grande de Publicar ──────────────────────
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp)
                                .clickable { publicar() },
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFFFB82E)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxSize(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = null,
                                    tint = Color(0xFF1A1209),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Publicar Resenha",
                                    color = Color(0xFF1A1209),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "PONTUAÇÃO DE REPUTAÇÃO: +25 XP CINÉFILO",
                            color = Color(0xFFFFB82E),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            fontSize = 10.sp
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    // Modal para buscar e selecionar filme
    if (uiState.showMovieSearchDialog) {
        MovieSearchDialog(
            query = uiState.searchQuery,
            results = uiState.searchResults,
            searching = uiState.isSearching,
            onQueryChange = { viewModel.onSearchQueryChange(it) },
            onDismiss = { viewModel.setShowMovieSearchDialog(false) },
            onMovieSelected = { movie ->
                viewModel.onSelectMovie(
                    ReviewMovieSelection(
                        title = movie.title,
                        director = "Dir. TMDb Cinema",
                        duration = "2h 00m",
                        year = movie.release_date?.take(4) ?: "2024",
                        posterUrl = movie.poster_path?.let {
                            if (it.startsWith("http")) it else "https://image.tmdb.org/t/p/w500$it"
                        } ?: "https://image.tmdb.org/t/p/w500/6yoghtyTpznpBik8EngEmJskVUO.jpg",
                        genres = listOf("Cinema", "Favorito")
                    )
                )
            }
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// COMPONENTES AUXILIARES
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun CreateReviewTopBar(
    onCancel: () -> Unit,
    onPublish: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Cancelar",
            color = Color(0xFFBEB7AE),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { onCancel() }
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "Nova Resenha",
            color = Color(0xFFEDE7DE),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        Surface(
            modifier = Modifier
                .clickable { onPublish() },
            shape = RoundedCornerShape(18.dp),
            color = Color(0xFFFFB82E)
        ) {
            Text(
                text = "Publicar",
                color = Color(0xFF1A1209),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 7.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(Color(0xFF2A2520))
                .border(1.dp, Color(0xFFFFB82E), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Perfil",
                tint = Color(0xFFFFB82E),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun DestinationBoxCard(
    box: BoxDetailData,
    allBoxes: List<BoxDetailData>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelectBox: (BoxDetailData) -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExpandedChange(!expanded) },
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF151518),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222228))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2B2312)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Folder,
                    contentDescription = null,
                    tint = Color(0xFFFFB82E),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "BOX DE DESTINO",
                    color = Color(0xFFC8A97E),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = box.title,
                    color = Color(0xFFEDE7DE),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "Selecionar Box",
                tint = Color(0xFFBEB7AE)
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
                modifier = Modifier.background(Color(0xFF1B1B20))
            ) {
                allBoxes.forEach { b ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = b.title,
                                color = if (b.id == box.id) Color(0xFFFFB82E) else Color(0xFFEDE7DE),
                                fontWeight = if (b.id == box.id) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        onClick = { onSelectBox(b) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectedMovieCard(
    movie: ReviewMovieSelection,
    onChangeMovie: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF151518),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222228))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Poster com badge de ano
            Box(
                modifier = Modifier
                    .width(80.dp)
                    .height(115.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF222228))
            ) {
                AsyncImage(
                    model = movie.posterUrl,
                    contentDescription = movie.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp),
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xCC000000)
                ) {
                    Text(
                        text = movie.year,
                        color = Color(0xFFFFB82E),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "FILME SELECIONADO",
                        color = Color(0xFFC8A97E),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Row(
                        modifier = Modifier.clickable { onChangeMovie() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.SwapHoriz,
                            contentDescription = null,
                            tint = Color(0xFFBEB7AE),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Alterar",
                            color = Color(0xFFBEB7AE),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = movie.title,
                    color = Color(0xFFEDE7DE),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${movie.director} • ${movie.duration}",
                    color = Color(0xFF8C867C),
                    style = MaterialTheme.typography.bodySmall,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    movie.genres.forEach { genre ->
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF222228)
                        ) {
                            Text(
                                text = "🎬 $genre",
                                color = Color(0xFFC8C2B8),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun VerdictRatingCard(
    rating: Float,
    onRatingChange: (Float) -> Unit,
    isObraPrima: Boolean,
    onToggleObraPrima: () -> Unit,
    isFavorito: Boolean,
    onToggleFavorito: () -> Unit
) {
    val ratingLabel = when {
        rating >= 5.0f -> "OBRA-PRIMA"
        rating >= 4.5f -> "QUASE PERFEITO"
        rating >= 4.0f -> "MUITO BOM"
        rating >= 3.5f -> "BOM"
        rating >= 3.0f -> "REGULAR"
        else -> "DECEPCIONANTE"
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF151518),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222228))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Veredito & Classificação",
                    color = Color(0xFFEDE7DE),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )

                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = String.format("%.1f", rating),
                        color = Color(0xFFFFB82E),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = " / 5.0",
                        color = Color(0xFF6E685E),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Linha das 5 estrelas interativas
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (i in 1..5) {
                        val starValue = i.toFloat()
                        val isFilled = rating >= starValue
                        val isHalf = rating >= (starValue - 0.5f) && rating < starValue

                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Nota $i",
                            tint = if (isFilled || isHalf) Color(0xFFFFB82E) else Color(0xFF33333C),
                            modifier = Modifier
                                .size(30.dp)
                                .clickable {
                                    // Alterna entre nota inteira e meia nota
                                    if (rating == starValue) {
                                        onRatingChange(starValue - 0.5f)
                                    } else {
                                        onRatingChange(starValue)
                                    }
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = ratingLabel,
                    color = Color(0xFFC8A97E),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Toggles de Selo Obra-Prima e Favorito
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Surface(
                    modifier = Modifier.clickable { onToggleObraPrima() },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isObraPrima) Color(0xFF3B2F15) else Color(0xFF1E1E24),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isObraPrima) Color(0xFFFFB82E) else Color(0xFF2C2C33)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "• Selo Obra-Prima",
                            color = if (isObraPrima) Color(0xFFFFB82E) else Color(0xFF9E978C),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Surface(
                    modifier = Modifier.clickable { onToggleFavorito() },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isFavorito) Color(0xFF32192A) else Color(0xFF1E1E24),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isFavorito) Color(0xFFE2559C) else Color(0xFF2C2C33)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "♥ Favorito Pessoal",
                            color = if (isFavorito) Color(0xFFFF7BB8) else Color(0xFF9E978C),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DebateTitleSection(
    title: String,
    onTitleChange: (String) -> Unit
) {
    Column {
        Text(
            text = "TESE DO DEBATE • TÍTULO PROVOCATIVO",
            color = Color(0xFFC8A97E),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF131316))
                .border(1.dp, Color(0xFF26262B), RoundedCornerShape(10.dp))
                .padding(horizontal = 14.dp, vertical = 12.dp)
        ) {
            BasicTextField(
                value = title,
                onValueChange = onTitleChange,
                singleLine = true,
                cursorBrush = SolidColor(Color(0xFFFFB82E)),
                textStyle = TextStyle(
                    color = Color(0xFFEDE7DE),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                ),
                decorationBox = { inner ->
                    if (title.isEmpty()) {
                        Text(
                            text = "Ex: O final de Seven ainda é insuperável no cinema...",
                            color = Color(0xFF555048),
                            fontSize = 14.sp
                        )
                    }
                    inner()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun AnalysisSection(
    text: String,
    onTextChange: (String) -> Unit,
    hasSpoilers: Boolean,
    onToggleSpoilers: () -> Unit,
    hashtags: List<String>,
    onRemoveTag: (String) -> Unit,
    newTagInput: String,
    onNewTagChange: (String) -> Unit,
    showAddTagField: Boolean,
    onToggleAddTag: () -> Unit,
    onAddTag: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "SUA ANÁLISE CRÍTICA",
                color = Color(0xFFC8A97E),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontSize = 11.sp
            )

            Text(
                text = "${text.length} caracteres",
                color = Color(0xFF6E685E),
                fontSize = 11.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Área de Texto com Barra de Ferramentas Integrada
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFF131316))
                .border(1.dp, Color(0xFF26262B), RoundedCornerShape(10.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                BasicTextField(
                    value = text,
                    onValueChange = onTextChange,
                    minLines = 6,
                    cursorBrush = SolidColor(Color(0xFFFFB82E)),
                    textStyle = TextStyle(
                        color = Color(0xFFEDE7DE),
                        fontSize = 13.5.sp,
                        lineHeight = 20.sp
                    ),
                    decorationBox = { inner ->
                        if (text.isEmpty()) {
                            Text(
                                text = "Escreva sua resenha detalhada, reflexões, observações sobre direção, roteiro ou fotografia...",
                                color = Color(0xFF555048),
                                fontSize = 13.5.sp
                            )
                        }
                        inner()
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Divider(color = Color(0xFF1F1F24), thickness = 0.5.dp)

            // Toolbar inferior (Aspas, Hash, Link, Spoilers)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        onTextChange("\"$text\"")
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.FormatQuote,
                        contentDescription = "Citar",
                        tint = Color(0xFF8C867C),
                        modifier = Modifier.size(18.dp)
                    )
                }

                IconButton(
                    onClick = onToggleAddTag,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tag,
                        contentDescription = "Tag",
                        tint = Color(0xFF8C867C),
                        modifier = Modifier.size(17.dp)
                    )
                }

                IconButton(
                    onClick = { /* Inserir link */ },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = "Link",
                        tint = Color(0xFF8C867C),
                        modifier = Modifier.size(17.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Botão "Contém Spoilers"
                Surface(
                    modifier = Modifier.clickable { onToggleSpoilers() },
                    shape = RoundedCornerShape(16.dp),
                    color = if (hasSpoilers) Color(0xFF381515) else Color(0xFF1E1E24),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (hasSpoilers) Color(0xFF992E2E) else Color(0xFF2C2C33)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = if (hasSpoilers) Color(0xFFFF5C5C) else Color(0xFF8C867C),
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Contém Spoilers",
                            color = if (hasSpoilers) Color(0xFFFF7E7E) else Color(0xFFBEB7AE),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Campo para adicionar nova hashtag
        AnimatedVisibility(visible = showAddTagField) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF17171C))
                        .border(1.dp, Color(0xFF2C2C35), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    BasicTextField(
                        value = newTagInput,
                        onValueChange = onNewTagChange,
                        singleLine = true,
                        cursorBrush = SolidColor(Color(0xFFFFB82E)),
                        textStyle = TextStyle(color = Color(0xFFEDE7DE), fontSize = 12.sp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(onDone = { onAddTag() }),
                        decorationBox = { inner ->
                            if (newTagInput.isEmpty()) {
                                Text("Digite uma tag (ex: #Suspense)...", color = Color(0xFF555048), fontSize = 12.sp)
                            }
                            inner()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    modifier = Modifier.clickable { onAddTag() },
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFFFB82E)
                ) {
                    Text(
                        text = "Adicionar",
                        color = Color(0xFF1A1209),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp)
                    )
                }
            }
        }

        // Linha de Hashtags
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(hashtags) { tag ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFF1E1E24),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2C33))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = tag,
                            color = Color(0xFFBEB7AE),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Remover tag",
                            tint = Color(0xFF8C867C),
                            modifier = Modifier
                                .size(12.dp)
                                .clickable { onRemoveTag(tag) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DebateSettingsCard(
    subtopicosEnabled: Boolean,
    onSubtopicosChange: (Boolean) -> Unit,
    visibilidade: String,
    visibilidadeExpanded: Boolean,
    onVisibilidadeExpandedChange: (Boolean) -> Unit,
    onSelectVisibilidade: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF151518),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222228))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Switch subtópicos
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Abrir subtópicos de debate",
                        color = Color(0xFFEDE7DE),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Permite que membros dividam análises sobre direção, atuação e roteiro separadamente",
                        color = Color(0xFF8C867C),
                        style = MaterialTheme.typography.bodySmall,
                        fontSize = 12.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Switch(
                    checked = subtopicosEnabled,
                    onCheckedChange = onSubtopicosChange,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFFFFB82E),
                        checkedTrackColor = Color(0xFF453315),
                        uncheckedThumbColor = Color(0xFF6E685E),
                        uncheckedTrackColor = Color(0xFF1E1E24),
                        uncheckedBorderColor = Color(0xFF2C2C33)
                    )
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = Color(0xFF222228), thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(14.dp))

            // Visibilidade
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🌐", fontSize = 14.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Visibilidade",
                        color = Color(0xFFBEB7AE),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box {
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFF1B1B20))
                            .clickable { onVisibilidadeExpandedChange(!visibilidadeExpanded) }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = visibilidade,
                            color = Color(0xFFEDE7DE),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = Color(0xFFBEB7AE),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = visibilidadeExpanded,
                        onDismissRequest = { onVisibilidadeExpandedChange(false) },
                        modifier = Modifier.background(Color(0xFF1B1B20))
                    ) {
                        listOf(
                            "Público na Box e Feed",
                            "Apenas na Box",
                            "Privado (Apenas Você)"
                        ).forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(text = opt, color = Color(0xFFEDE7DE), fontSize = 13.sp) },
                                onClick = { onSelectVisibilidade(opt) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MovieSearchDialog(
    query: String,
    results: List<Moviefmt>,
    searching: Boolean,
    onQueryChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onMovieSelected: (Moviefmt) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF161619),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2C33))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Buscar Filme",
                        color = Color(0xFFEDE7DE),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Fechar",
                            tint = Color(0xFFBEB7AE)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Campo de busca
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF101013))
                        .border(1.dp, Color(0xFF2C2C33), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFFFFB82E),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(modifier = Modifier.weight(1f)) {
                        if (query.isEmpty()) {
                            Text(
                                text = "Nome do filme (ex: Seven, Parasita)...",
                                color = Color(0xFF555048),
                                fontSize = 13.sp
                            )
                        }
                        BasicTextField(
                            value = query,
                            onValueChange = onQueryChange,
                            singleLine = true,
                            cursorBrush = SolidColor(Color(0xFFFFB82E)),
                            textStyle = TextStyle(color = Color(0xFFEDE7DE), fontSize = 13.sp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    if (searching) {
                        CircularProgressIndicator(
                            color = Color(0xFFFFB82E),
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (results.isEmpty() && query.length >= 2 && !searching) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Nenhum filme encontrado para \"$query\"",
                            color = Color(0xFF8C867C),
                            fontSize = 13.sp
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(results) { movie ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF1E1E24))
                                    .clickable { onMovieSelected(movie) }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val poster = movie.poster_path?.let {
                                    if (it.startsWith("http")) it else "https://image.tmdb.org/t/p/w200$it"
                                }
                                Box(
                                    modifier = Modifier
                                        .size(width = 40.dp, height = 55.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0xFF282830))
                                ) {
                                    if (poster != null) {
                                        AsyncImage(
                                            model = poster,
                                            contentDescription = movie.title,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = movie.title,
                                        color = Color(0xFFEDE7DE),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${movie.release_date?.take(4) ?: "Ano N/A"} • ★ ${String.format("%.1f", movie.vote_average)}",
                                        color = Color(0xFF8C867C),
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
