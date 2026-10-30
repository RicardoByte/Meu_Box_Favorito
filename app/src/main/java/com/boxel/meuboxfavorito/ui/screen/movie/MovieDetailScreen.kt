package com.boxel.meuboxfavorito.ui.screen.movie

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.boxel.meuboxfavorito.BuildConfig
import com.boxel.meuboxfavorito.data.remote.Moviefmt
import com.boxel.meuboxfavorito.ui.feed.FeedViewModel

@Composable
fun MovieDetailScreen(
    movieId: Int,
    onBack: () -> Unit,
    // TODO: substituir FeedViewModel por MovieDetailViewModel dedicado
    // que busca GET /movie/{id} diretamente no TMDb
    viewModel: FeedViewModel = viewModel()
) {

    val allMovies by viewModel.movies.collectAsState()
    var salvo by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (allMovies.isEmpty()) {
            viewModel.loadPopularMovies(BuildConfig.TMDB_API_KEY)
        }
    }

    // TODO: substituir por chamada direta ao endpoint GET /movie/{movieId}
    val filme = remember(movieId, allMovies) {
        allMovies.firstOrNull { it.id == movieId }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF101011)
    ) {

        if (filme == null) {
            MovieDetailSkeleton(onBack = onBack)
        } else {
            MovieDetailContent(
                filme = filme,
                salvo = salvo,
                onBack = onBack,
                onSalvar = { salvo = !salvo }
            )
        }
    }
}

@Composable
private fun MovieDetailContent(
    filme: Moviefmt,
    salvo: Boolean,
    onBack: () -> Unit,
    onSalvar: () -> Unit
) {

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {

        // ─── Backdrop / Poster ───────────────────────
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            ) {

                val imageUrl = filme.backdrop_path?.let {
                    "https://image.tmdb.org/t/p/w780$it"
                } ?: filme.poster_path?.let {
                    "https://image.tmdb.org/t/p/w500$it"
                }

                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = filme.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF2A2520))
                    )
                }

                // Gradient inferior
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color(0xFF101011)
                                ),
                                startY = 160f
                            )
                        )
                )

                // Botões no topo
                Row(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = onSalvar) {
                        Icon(
                            imageVector = if (salvo) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Salvar",
                            tint = if (salvo) Color(0xFFFFB82E) else Color.White
                        )
                    }
                }
            }
        }

        // ─── Informações do filme ─────────────────────
        item {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            ) {

                Text(
                    text = filme.title,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {

                    if (!filme.release_date.isNullOrBlank()) {
                        Text(
                            text = filme.release_date.take(4),
                            color = Color(0xFF8C8376),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB82E),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = String.format("%.1f", filme.vote_average),
                            color = Color(0xFFD8D2C7),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sinopse
                Text(
                    text = "Sinopse",
                    color = Color(0xFFE8E4E0),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = filme.overview.ifBlank { "Sinopse não disponível." },
                    color = Color(0xFFAFA99E),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(20.dp))

                Divider(color = Color(0xFF1E1C18))

                Spacer(modifier = Modifier.height(20.dp))

                // Botão "Adicionar a uma Box"
                // TODO: mostrar sheet com lista de boxes do usuário para adicionar
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .clickable { /* TODO: abrir seleção de box */ },
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFFB82E)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "＋  Adicionar a uma Box",
                            color = Color(0xFF1A1209),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MovieDetailSkeleton(
    onBack: () -> Unit
) {

    Column(modifier = Modifier.fillMaxSize()) {

        // Topo com botão voltar
        IconButton(
            onClick = onBack,
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Voltar",
                tint = Color.White
            )
        }

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = "Carregando filme...",
                color = Color(0xFF8C8376),
                style = MaterialTheme.typography.bodyMedium
            )
            // TODO: substituir por Shimmer loading quando tivermos
            // MovieDetailViewModel buscando diretamente por ID
        }
    }
}
