package com.boxel.meuboxfavorito.ui.screen.feed

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.boxel.meuboxfavorito.BuildConfig
import com.boxel.meuboxfavorito.data.repository.BoxDetailRepository
import com.boxel.meuboxfavorito.models.Moviefmt
import com.boxel.meuboxfavorito.ui.navigation.AppBottomBar
import com.boxel.meuboxfavorito.ui.view.FeedViewModel
data class CommunityBox(
    val id: Int,
    val titulo: String,
    val usuario: String,
    val descricao: String,
    val filmes: List<Moviefmt>,
    val curtidas: Int,
    val destaque: Boolean = false
)

@Composable
fun CommunityFeedScreen(
    viewModel: FeedViewModel = viewModel(),
    onNavigate: (String) -> Unit,
    onBoxClick: (Int) -> Unit = {}
) {

    val movies by viewModel.movies.collectAsState()
    val carregando by viewModel.carregando.collectAsState()
    val erro by viewModel.erro.collectAsState()

    var mostrarBusca by remember {
        mutableStateOf(false)
    }

    var searchText by remember {
        mutableStateOf("")
    }

    LaunchedEffect(Unit) {

        if (movies.isEmpty()) {
            viewModel.loadPopularMovies(
                apiKey = BuildConfig.TMDB_API_KEY
            )
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF101011)
    ) {

        Column(
            modifier = Modifier.fillMaxSize()
        ) {

            CommunityTopBar(
                mostrarBusca = mostrarBusca,
                searchText = searchText,
                onSearchTextChange = {
                    searchText = it
                },
                onSearchClick = {
                    mostrarBusca = !mostrarBusca
                }
            )

            // Conteúdo ocupa todo o espaço disponível
            // entre o TopBar e a BottomBar
            Box(
                modifier = Modifier.weight(1f)
            ) {

                when {

                    carregando -> {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {

                            CircularProgressIndicator(
                                color = Color(0xFFEAD7B0)
                            )
                        }
                    }

                    erro != null -> {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = erro ?: "Erro ao carregar filmes.",
                                color = Color.White,
                                modifier = Modifier.padding(24.dp)
                            )
                        }
                    }

                    movies.isEmpty() -> {

                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {

                            Text(
                                text = "Nenhum filme encontrado.",
                                color = Color.White
                            )
                        }
                    }

                    else -> {

                        CommunityContent(
                            movies = movies,
                            searchText = searchText,
                            onBoxClick = onBoxClick
                        )
                    }
                }
            }

            //A BottomBar
            AppBottomBar(
                selectedRoute = "explore",
                onNavigate = onNavigate
            )
        }
    }
}

@Composable
private fun CommunityTopBar(
    mostrarBusca: Boolean,
    searchText: String,
    onSearchTextChange: (String) -> Unit,
    onSearchClick: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF101011))
            .padding(
                horizontal = 16.dp,
                vertical = 10.dp
            )
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = {
                    // Menu será implementado posteriormente
                }
            ) {

                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Menu",
                    tint = Color(0xFFEAD7B0)
                )
            }

            Text(
                text = "Feed da Comunidade",
                modifier = Modifier.weight(1f),
                color = Color(0xFFEAD7B0),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = onSearchClick
            ) {

                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Pesquisar",
                    tint = Color(0xFFEAD7B0)
                )
            }
        }

        if (mostrarBusca) {

            androidx.compose.material3.OutlinedTextField(
                value = searchText,
                onValueChange = onSearchTextChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                singleLine = true,
                placeholder = {
                    Text(
                        text = "Buscar no feed..."
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Composable
private fun CommunitySectionTitle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    actionText: String? = null
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color(0xFFEAD7B0),
            modifier = Modifier.size(24.dp)
        )

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Text(
            text = title,
            color = Color(0xFFE8E5E8),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        if (actionText != null) {

            Text(
                text = actionText,
                color = Color(0xFFEAD7B0),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun CommunityContent(
    movies: List<Moviefmt>,
    searchText: String,
    onBoxClick: (Int) -> Unit = {}
) {

    val communityBoxes = remember(movies) {
        createCommunityBoxes(movies)
    }

    val filteredBoxes = if (searchText.isBlank()) {
        communityBoxes
    } else {
        communityBoxes.filter { box ->

            box.titulo.contains(
                searchText,
                ignoreCase = true
            ) ||
                    box.usuario.contains(
                        searchText,
                        ignoreCase = true
                    ) ||
                    box.filmes.any { movie ->

                        movie.title.contains(
                            searchText,
                            ignoreCase = true
                        )
                    }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 16.dp,
            bottom = 100.dp
        ),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {

        // =========================
        // EM ALTA
        // =========================

        item {

            CommunitySectionTitle(
                icon = Icons.Default.LocalFireDepartment,
                title = "Em Alta",
                actionText = "VER TODOS"
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(
                    end = 16.dp
                )
            ) {

                val destaques = filteredBoxes.filter {
                    it.destaque
                }

                items(destaques) { box ->

                    HighlightBoxCard(
                        box = box,
                        onClick = { onBoxClick(box.id) }
                    )
                }
            }
        }

        // =========================
        // RECENTES
        // =========================

        item {

            CommunitySectionTitle(
                icon = Icons.Default.AccessTime,
                title = "Recentes"
            )
        }

        if (filteredBoxes.isEmpty()) {

            item {

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 50.dp),
                    contentAlignment = Alignment.Center
                ) {

                    Text(
                        text = "Nenhum resultado encontrado.",
                        color = Color.LightGray
                    )
                }
            }

        } else {

            items(filteredBoxes) { box ->

                RecentBoxCard(
                    box = box,
                    onClick = { onBoxClick(box.id) }
                )
            }
        }
    }
}

@Composable
private fun HighlightBoxCard(
    box: CommunityBox,
    onClick: () -> Unit = {}
) {

    val cover = box.filmes.firstOrNull {
        !it.poster_path.isNullOrBlank()
    }

    Box(
        modifier = Modifier
            .width(280.dp)
            .height(410.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF1A1A1C))
            .clickable { onClick() }
    ) {

        if (cover != null) {

            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500${cover.poster_path}",
                contentDescription = box.titulo,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xEE101011)
                        ),
                        startY = 150f
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(50))
                        .background(Color(0xFF37373A)),
                    contentAlignment = Alignment.Center
                ) {

                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = Color.LightGray,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(
                    modifier = Modifier.width(8.dp)
                )

                Text(
                    text = box.usuario,
                    color = Color(0xFFD9D4C8),
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = box.titulo,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {

                Text(
                    text = "🎬 ${box.filmes.size} Filmes",
                    color = Color(0xFFD8D2C6),
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "♡ ${formatLikes(box.curtidas)}",
                    color = Color(0xFFD8D2C6),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun RecentBoxCard(
    box: CommunityBox,
    onClick: () -> Unit = {}
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF1A1A1C)
    ) {

        Column {

            CommunityImageCollage(
                movies = box.filmes
            )

            Column(
                modifier = Modifier.padding(16.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color(0xFF37373A)),
                        contentAlignment = Alignment.Center
                    ) {

                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(17.dp)
                        )
                    }

                    Spacer(
                        modifier = Modifier.width(8.dp)
                    )

                    Text(
                        text = box.usuario,
                        color = Color(0xFFD8D2C6),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Text(
                    text = box.titulo,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(8.dp)
                )

                Text(
                    text = box.descricao,
                    color = Color(0xFFD0CBC2),
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(
                    modifier = Modifier.height(18.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        Text(
                            text = "🎬 ${box.filmes.size} Filmes",
                            color = Color(0xFFD8D2C6),
                            style = MaterialTheme.typography.bodySmall
                        )

                        Text(
                            text = "♡ ${formatLikes(box.curtidas)}",
                            color = Color(0xFFD8D2C6),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    Text(
                        text = "+",
                        color = Color(0xFFEAD7B0),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Light
                    )
                }
            }
        }
    }
}

@Composable
private fun CommunityImageCollage(
    movies: List<Moviefmt>
) {

    val validMovies = movies
        .filter {
            !it.poster_path.isNullOrBlank()
        }
        .take(4)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(190.dp)
    ) {

        validMovies.forEach { movie ->

            AsyncImage(
                model = "https://image.tmdb.org/t/p/w500${movie.poster_path}",
                contentDescription = movie.title,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        if (validMovies.isEmpty()) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF242426)),
                contentAlignment = Alignment.Center
            ) {

                Text(
                    text = "Sem imagens",
                    color = Color.Gray
                )
            }
        }
    }
}

private fun createCommunityBoxes(
    movies: List<Moviefmt>
): List<CommunityBox> {
    val box1 = BoxDetailRepository.getBoxById(1)
    val box2 = BoxDetailRepository.getBoxById(2)
    val box3 = BoxDetailRepository.getBoxById(3)
    val box4 = BoxDetailRepository.getBoxById(4)
    val box5 = BoxDetailRepository.getBoxById(5)

    return listOf(
        CommunityBox(
            id = 1,
            titulo = box1.title,
            usuario = box1.creatorHandle,
            descricao = box1.description,
            filmes = box1.filmes,
            curtidas = 3400,
            destaque = true
        ),

        CommunityBox(
            id = 4,
            titulo = box4.title,
            usuario = box4.creatorHandle,
            descricao = box4.description,
            filmes = box4.filmes,
            curtidas = 4200,
            destaque = true
        ),

        CommunityBox(
            id = 3,
            titulo = box3.title,
            usuario = box3.creatorHandle,
            descricao = box3.description,
            filmes = box3.filmes,
            curtidas = 930
        ),

        CommunityBox(
            id = 5,
            titulo = box5.title,
            usuario = box5.creatorHandle,
            descricao = box5.description,
            filmes = box5.filmes,
            curtidas = 1100
        ),

        CommunityBox(
            id = 2,
            titulo = box2.title,
            usuario = box2.creatorHandle,
            descricao = box2.description,
            filmes = box2.filmes,
            curtidas = 420
        )
    )
}

private fun formatLikes(
    likes: Int
): String {

    return when {

        likes >= 1000 -> {
            val value = likes / 1000f

            if (value % 1f == 0f) {
                "${value.toInt()}k"
            } else {
                "%.1fk".format(value)
            }
        }

        else -> {
            likes.toString()
        }
    }
}
