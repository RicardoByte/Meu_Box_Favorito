package com.boxel.meuboxfavorito.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
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
import com.boxel.meuboxfavorito.ui.navigation.AppBottomBar


private data class MyBox(
    val id: Int,
    val title: String,
    val quantidade: Int,
    val tipo: BoxType,
    val filmes: List<Moviefmt>
)


private enum class BoxType {
    PUBLICA,
    PRIVADA,
    COLABORATIVA
}


@Composable
fun MyBoxesScreen(
    viewModel: FeedViewModel = viewModel(),
    onNavigate: (String) -> Unit
) {

    val movies by viewModel.movies.collectAsState()
    val carregando by viewModel.carregando.collectAsState()
    val erro by viewModel.erro.collectAsState()

    var searchText by remember {
        mutableStateOf("")
    }

    var filtroSelecionado by remember {
        mutableStateOf("Todas")
    }

    LaunchedEffect(Unit) {

        if (movies.isEmpty()) {
            viewModel.loadPopularMovies(
                apiKey = BuildConfig.TMDB_API_KEY
            )
        }
    }

    val boxes = remember(movies) {
        createMyBoxes(movies)
    }

    val boxesFiltradas = remember(
        boxes,
        filtroSelecionado,
        searchText
    ) {

        boxes.filter { box ->

            val passaFiltro = when (filtroSelecionado) {

                "Criadas por mim" -> {
                    box.tipo == BoxType.PRIVADA ||
                            box.tipo == BoxType.PUBLICA
                }

                "Colaborativas" -> {
                    box.tipo == BoxType.COLABORATIVA
                }

                else -> true
            }

            val passaBusca =
                searchText.isBlank() ||
                        box.title.contains(
                            searchText,
                            ignoreCase = true
                        )

            passaFiltro && passaBusca
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF181819)
    ) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            Column(
                modifier = Modifier.fillMaxSize()
            ) {

                MyBoxesTopBar()

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .navigationBarsPadding(),

                    contentPadding = PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        top = 10.dp,
                        bottom = 110.dp
                    ),

                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {

                    item {

                        Text(
                            text = "Minhas Boxes",
                            color = Color(0xFFE9E5E9),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(
                            modifier = Modifier.height(4.dp)
                        )

                        Text(
                            text = "Gerencie e organize suas coleções de filmes.",
                            color = Color(0xFFB9B0A6),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(
                            modifier = Modifier.height(10.dp)
                        )

                        MyBoxesSearchBar(
                            value = searchText,
                            onValueChange = {
                                searchText = it
                            }
                        )

                        Spacer(
                            modifier = Modifier.height(14.dp)
                        )

                        MyBoxesFilters(
                            selecionado = filtroSelecionado,
                            onSelecionar = {
                                filtroSelecionado = it
                            }
                        )

                        Spacer(
                            modifier = Modifier.height(18.dp)
                        )
                    }

                    if (carregando) {

                        item {

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(300.dp),
                                contentAlignment = Alignment.Center
                            ) {

                                Text(
                                    text = "Carregando filmes...",
                                    color = Color.LightGray
                                )
                            }
                        }

                    } else if (erro != null) {

                        item {

                            Text(
                                text = erro ?: "Erro ao carregar filmes.",
                                color = Color.White,
                                modifier = Modifier.padding(16.dp)
                            )
                        }

                    } else {

                        items(boxesFiltradas) { box ->

                            MyBoxCard(
                                box = box
                            )
                        }

                        item {

                            CreateNewBoxCard()
                        }
                    }
                }
            }

            AppBottomBar(
                selectedRoute = "my_boxes",
                onNavigate = onNavigate,
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            FloatingAddButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = 22.dp,
                        bottom = 78.dp
                    )
            )
        }
    }
}


/*
 * CABEÇALHO
 */

@Composable
private fun MyBoxesTopBar() {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF181819))
            .padding(
                horizontal = 16.dp,
                vertical = 12.dp
            ),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = Icons.Default.Folder,
            contentDescription = "Meu Box",
            tint = Color(0xFFEAD7B0),
            modifier = Modifier.size(24.dp)
        )

        Spacer(
            modifier = Modifier.width(12.dp)
        )

        Text(
            text = "Meu Box",
            color = Color(0xFFEAD7B0),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = {}
        ) {

            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Pesquisar",
                tint = Color(0xFFE5DFD5)
            )
        }
    }
}


/*
 * CAMPO DE BUSCA
 */

@Composable
private fun MyBoxesSearchBar(
    value: String,
    onValueChange: (String) -> Unit
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .border(
                width = 1.dp,
                color = Color(0xFF393639),
                shape = RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 12.dp),

        verticalAlignment = Alignment.CenterVertically
    ) {

        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = Color(0xFFBEB7AE),
            modifier = Modifier.size(20.dp)
        )

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        androidx.compose.foundation.text.BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color(0xFFE5DFD5)
            ),
            decorationBox = { innerTextField ->

                if (value.isEmpty()) {

                    Text(
                        text = "Buscar nas minhas boxes...",
                        color = Color(0xFFAAA39A)
                    )
                }

                innerTextField()
            }
        )
    }
}


/*
 * FILTROS
 */

@Composable
private fun MyBoxesFilters(
    selecionado: String,
    onSelecionar: (String) -> Unit
) {

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        FilterButton(
            text = "Todas",
            selecionado = selecionado == "Todas",
            onClick = {
                onSelecionar("Todas")
            }
        )

        FilterButton(
            text = "Criadas por mim",
            selecionado = selecionado == "Criadas por mim",
            onClick = {
                onSelecionar("Criadas por mim")
            }
        )

        FilterButton(
            text = "Colaborativas",
            selecionado = selecionado == "Colaborativas",
            onClick = {
                onSelecionar("Colaborativas")
            }
        )
    }
}


@Composable
private fun FilterButton(
    text: String,
    selecionado: Boolean,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier.clickable {
            onClick()
        },

        shape = RoundedCornerShape(20.dp),

        color = if (selecionado) {
            Color(0xFFFFB82E)
        } else {
            Color(0xFF181819)
        },

        border = if (!selecionado) {
            androidx.compose.foundation.BorderStroke(
                1.dp,
                Color(0xFF393639)
            )
        } else {
            null
        }
    ) {

        Text(
            text = text,
            color = if (selecionado) {
                Color(0xFF28221A)
            } else {
                Color(0xFFBEB7AE)
            },

            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,

            modifier = Modifier.padding(
                horizontal = 18.dp,
                vertical = 8.dp
            )
        )
    }
}


/*
 * CARD DA BOX
 */

@Composable
private fun MyBoxCard(
    box: MyBox
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.74f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF222224))
            .clickable {}
    ) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            MovieCollage(
                movies = box.filmes
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color(0xEE101011)
                            )
                        )
                    )
            )

            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(14.dp)
            ) {

                BoxTypeBadge(
                    tipo = box.tipo
                )
            }

            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {

                Text(
                    text = box.title,
                    color = Color(0xFFECE8E8),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Text(
                        text = "▦",
                        color = Color(0xFFD8D0C5)
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Text(
                        text = "${box.quantidade} Filmes",
                        color = Color(0xFFD8D0C5),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}


/*
 * IMAGENS DOS FILMES
 */

@Composable
private fun MovieCollage(
    movies: List<Moviefmt>
) {

    val posters = movies
        .filter {
            !it.poster_path.isNullOrBlank()
        }
        .take(4)

    if (posters.isEmpty()) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF29292B)),
            contentAlignment = Alignment.Center
        ) {

            Text(
                text = "Sem imagens",
                color = Color.Gray
            )
        }

        return
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {

            posters.take(2).forEach { movie ->

                AsyncImage(
                    model = "https://image.tmdb.org/t/p/w500${movie.poster_path}",
                    contentDescription = movie.title,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }
        }

        if (posters.size > 2) {

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {

                posters.drop(2).take(2).forEach { movie ->

                    AsyncImage(
                        model = "https://image.tmdb.org/t/p/w500${movie.poster_path}",
                        contentDescription = movie.title,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.Transparent,
                        Color(0xEE101011)
                    )
                )
            )
    )
}


/*
 * BADGE PÚBLICA / PRIVADA / COLABORATIVA
 */

@Composable
private fun BoxTypeBadge(
    tipo: BoxType
) {

    val texto = when (tipo) {

        BoxType.PUBLICA ->
            "◉ Público"

        BoxType.PRIVADA ->
            "▣ Privado"

        BoxType.COLABORATIVA ->
            "♧ Colaborativo"
    }

    val background = when (tipo) {

        BoxType.PUBLICA ->
            Color(0xAA28282A)

        BoxType.PRIVADA ->
            Color(0xAA28282A)

        BoxType.COLABORATIVA ->
            Color(0xFF7548C9)
    }

    Surface(
        color = background,
        shape = RoundedCornerShape(20.dp)
    ) {

        Text(
            text = texto,
            color = Color(0xFFEDE7E0),
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(
                horizontal = 10.dp,
                vertical = 5.dp
            )
        )
    }
}


/*
 * CRIAR NOVA BOX
 */

@Composable
private fun CreateNewBoxCard() {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp)
            .clip(RoundedCornerShape(12.dp))
            .border(
                width = 1.dp,
                color = Color(0xFF4A4642),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable {},

        contentAlignment = Alignment.Center
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF464548)),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Criar Box",
                    tint = Color(0xFFD8D1C7),
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(
                modifier = Modifier.height(14.dp)
            )

            Text(
                text = "Criar Nova Box",
                color = Color(0xFFEAD7B0),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }
    }
}


/*
 * BOTÃO FLUTUANTE
 */

@Composable
private fun FloatingAddButton(
    modifier: Modifier = Modifier
) {

    Box(
        modifier = modifier
            .size(58.dp)
            .clip(CircleShape)
            .background(Color(0xFFFFB82E))
            .clickable {},

        contentAlignment = Alignment.Center
    ) {

        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Adicionar Box",
            tint = Color(0xFF332817),
            modifier = Modifier.size(28.dp)
        )
    }
}

/*
 * DADOS DAS BOXES
 *
 * Os filmes vêm da TMDb.
 * Os nomes/quantidades/tipos pertencem ao aplicativo.
 */

private fun createMyBoxes(
    movies: List<Moviefmt>
): List<MyBox> {

    if (movies.isEmpty()) {
        return emptyList()
    }

    val grupo1 = movies
        .take(4)

    val grupo2 = movies
        .drop(4)
        .take(4)
        .ifEmpty {
            grupo1
        }

    val grupo3 = movies
        .drop(8)
        .take(4)
        .ifEmpty {
            grupo1
        }

    return listOf(

        MyBox(
            id = 1,
            title = "Terror Trash",
            quantidade = 24,
            tipo = BoxType.PUBLICA,
            filmes = grupo1
        ),

        MyBox(
            id = 2,
            title = "Filmes do nerd",
            quantidade = 11,
            tipo = BoxType.PRIVADA,
            filmes = grupo2
        ),

        MyBox(
            id = 3,
            title = "Filmes pra postar no insta",
            quantidade = 8,
            tipo = BoxType.COLABORATIVA,
            filmes = grupo3
        )
    )
}