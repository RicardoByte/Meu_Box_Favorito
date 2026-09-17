package com.boxel.meuboxfavorito.ui.feed

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.*
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.*
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.boxel.meuboxfavorito.BuildConfig
import com.boxel.meuboxfavorito.data.remote.Moviefmt

private val API_KEY = BuildConfig.TMDB_API_KEY

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(viewModel: FeedViewModel = viewModel()) {
    val movies by viewModel.movies.collectAsState()
    val carregando by viewModel.carregando.collectAsState()
    val erro by viewModel.erro.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }
    var selectedMovie by remember { mutableStateOf<Moviefmt?>(null) }

    val filteredMovies = remember(movies, searchQuery) {
        if (searchQuery.isBlank()) movies
        else movies.filter { it.title.contains(searchQuery, ignoreCase = true) }
    }

    LaunchedEffect(Unit) {
        viewModel.loadPopularMovies(apiKey = API_KEY)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Meu Box Favorito",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Consumindo API TMDb",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { isSearchActive = !isSearchActive }) {
                        Icon(
                            imageVector = if (isSearchActive) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "Buscar filme"
                        )
                    }
                    IconButton(
                        onClick = { viewModel.loadPopularMovies(apiKey = API_KEY) },
                        enabled = !carregando
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Atualizar dados da API"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
                )
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                carregando -> {
                    LoadingView()
                }

                erro != null -> {
                    ErrorView(
                        errorMessage = erro ?: "Erro desconhecido",
                        onRetry = { viewModel.loadPopularMovies(apiKey = API_KEY) }
                    )
                }

                else -> {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Barra de busca expansível
                        AnimatedVisibility(visible = isSearchActive) {
                            OutlinedTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp),
                                placeholder = { Text("Filtrar por título...") },
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = null)
                                },
                                trailingIcon = {
                                    if (searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { searchQuery = "" }) {
                                            Icon(Icons.Default.Close, contentDescription = "Limpar busca")
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp)
                            )
                        }

                        if (filteredMovies.isEmpty()) {
                            EmptyView(hasFilter = searchQuery.isNotEmpty())
                        } else {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(12.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.fillMaxSize()
                            ) {
                                item(span = { GridItemSpan(2) }) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 4.dp, vertical = 4.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Filmes em Destaque",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Surface(
                                            color = MaterialTheme.colorScheme.primaryContainer,
                                            shape = RoundedCornerShape(16.dp)
                                        ) {
                                            Text(
                                                text = "${filteredMovies.size} itens",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                            )
                                        }
                                    }
                                }

                                items(filteredMovies, key = { it.id }) { movie ->
                                    MovieCard(
                                        movie = movie,
                                        onClick = { selectedMovie = movie }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // BottomSheet de Detalhes do Filme & Inspeção da API
            selectedMovie?.let { movie ->
                MovieDetailBottomSheet(
                    movie = movie,
                    onDismiss = { selectedMovie = null }
                )
            }
        }
    }
}

@Composable
fun MovieCard(
    movie: Moviefmt,
    onClick: () -> Unit
) {
    ElevatedCard(
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                if (!movie.poster_path.isNullOrEmpty()) {
                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w500/${movie.poster_path}",
                        contentDescription = "Pôster de ${movie.title}",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sem pôster",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Gradiente escuro sutil na base do pôster
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                            )
                        )
                )

                // Selo de Nota (Top Right)
                Surface(
                    color = Color.Black.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(bottomStart = 8.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = Color(0xFFFFB800),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = String.format("%.1f", movie.vote_average),
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Ano de lançamento (Bottom Left sobre o pôster)
                if (!movie.release_date.isNullOrBlank()) {
                    Text(
                        text = movie.release_date.take(4),
                        color = Color.White.copy(alpha = 0.9f),
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (movie.overview.isNotBlank()) movie.overview else "Sinopse não disponível.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailBottomSheet(
    movie: Moviefmt,
    onDismiss: () -> Unit
) {
    var showApiJson by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 32.dp)
        ) {
            // Imagem de Backdrop (ou poster caso não haja backdrop)
            val backdropUrl = movie.backdrop_path ?: movie.poster_path
            if (!backdropUrl.isNullOrBlank()) {
                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w780/$backdropUrl",
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color(0xFFFFB800),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${String.format("%.1f", movie.vote_average)} / 10",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    if (!movie.release_date.isNullOrBlank()) {
                        Text(
                            text = "Lançamento: ${movie.release_date}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Sinopse",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (movie.overview.isNotBlank()) movie.overview else "Nenhuma sinopse fornecida pela API.",
                    style = MaterialTheme.typography.bodyMedium,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Área para Visualizar Dados Técnicos do Consumo da API
                ElevatedCard(
                    colors = CardDefaults.elevatedCardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Metadados da API (TMDb)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            TextButton(onClick = { showApiJson = !showApiJson }) {
                                Text(if (showApiJson) "Ocultar" else "Ver Detalhes")
                            }
                        }

                        AnimatedVisibility(visible = showApiJson) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp)
                                    .background(
                                        MaterialTheme.colorScheme.surface,
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Text("• ID do Filme: ${movie.id}", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                                Text("• Endpoint: /movie/popular", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                                Text("• Poster Path: ${movie.poster_path ?: "null"}", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                                Text("• Backdrop Path: ${movie.backdrop_path ?: "null"}", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                                Text("• Votos: ${movie.vote_average}", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                                Text("• Gêneros IDs: ${movie.genre_ids}", fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            strokeWidth = 3.dp,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Consultando api.themoviedb.org...",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Aguardando resposta do servidor",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ErrorView(
    errorMessage: String,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.CloudOff,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Falha ao conectar com a API",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = errorMessage,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Tentar Novamente")
        }
    }
}

@Composable
fun EmptyView(hasFilter: Boolean) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = if (hasFilter) "Nenhum filme corresponde à pesquisa." else "Nenhum filme retornado pela API.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}