package com.boxel.meuboxfavorito.ui.screen.myboxes

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.boxel.meuboxfavorito.BuildConfig
import com.boxel.meuboxfavorito.data.remote.Moviefmt
import com.boxel.meuboxfavorito.data.repository.BoxDetailRepository
import com.boxel.meuboxfavorito.data.repository.BoxPrivacyType
import com.boxel.meuboxfavorito.data.repository.MovieRepository
import com.boxel.meuboxfavorito.ui.feed.FeedViewModel
import kotlinx.coroutines.delay

private enum class Privacidade(val label: String) {
    PUBLICA("Pública"),
    PRIVADA("Privada"),
    COLABORATIVA("Colaborativa")
}

@Composable
fun CreateBoxScreen(
    onBack: () -> Unit,
    onBoxCreated: (boxId: Int) -> Unit,
    viewModel: FeedViewModel = viewModel()
) {
    val allMovies by viewModel.movies.collectAsState()
    val repository = remember { MovieRepository() }

    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var privacidade by remember { mutableStateOf(Privacidade.PUBLICA) }
    var colaborativa by remember { mutableStateOf(false) }
    var erroTitulo by remember { mutableStateOf(false) }

    // Capa da Box via TMDb
    var capaSelecionadaUrl by remember { mutableStateOf<String?>(null) }
    var filmeCapaNome by remember { mutableStateOf<String?>(null) }
    var buscaCapa by remember { mutableStateOf("") }
    var mostrandoBuscaCapa by remember { mutableStateOf(false) }
    var resultadosCapa by remember { mutableStateOf<List<Moviefmt>>(emptyList()) }
    var buscandoCapa by remember { mutableStateOf(false) }

    // Filmes adicionados
    var buscaFilme by remember { mutableStateOf("") }
    val filmesAdicionados = remember { mutableStateListOf<Moviefmt>() }
    var resultadosFilmesBusca by remember { mutableStateOf<List<Moviefmt>>(emptyList()) }
    var buscandoFilmes by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (allMovies.isEmpty()) {
            viewModel.loadPopularMovies(BuildConfig.TMDB_API_KEY)
        }
    }

    // Busca de Capas na API TMDb por nome
    LaunchedEffect(buscaCapa) {
        if (buscaCapa.length >= 2) {
            buscandoCapa = true
            delay(350)
            try {
                val res = repository.searchMovies(BuildConfig.TMDB_API_KEY, buscaCapa)
                resultadosCapa = res.filter { !it.backdrop_path.isNullOrBlank() || !it.poster_path.isNullOrBlank() }
            } catch (e: Exception) {
                resultadosCapa = allMovies.filter { it.title.contains(buscaCapa, ignoreCase = true) }
            } finally {
                buscandoCapa = false
            }
        } else {
            resultadosCapa = emptyList()
        }
    }

    // Busca de Filmes na API TMDb por nome
    LaunchedEffect(buscaFilme) {
        if (buscaFilme.length >= 2) {
            buscandoFilmes = true
            delay(350)
            try {
                val res = repository.searchMovies(BuildConfig.TMDB_API_KEY, buscaFilme)
                resultadosFilmesBusca = res.filter { m -> filmesAdicionados.none { it.id == m.id } }
            } catch (e: Exception) {
                resultadosFilmesBusca = allMovies
                    .filter { it.title.contains(buscaFilme, ignoreCase = true) }
                    .filter { m -> filmesAdicionados.none { it.id == m.id } }
            } finally {
                buscandoFilmes = false
            }
        } else {
            resultadosFilmesBusca = emptyList()
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
            CreateBoxTopBar(
                onBack = onBack,
                onOptions = { /* Mais opções */ }
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ─── Seção 1: Informações Básicas ─────────────────────
                item {
                    SectionCard(
                        icon = Icons.Default.Info,
                        title = "Informações Básicas"
                    ) {
                        CreateBoxTextField(
                            label = "TÍTULO DA BOX",
                            value = titulo,
                            placeholder = "Ex: Noir Essentials",
                            isError = erroTitulo,
                            onValueChange = {
                                titulo = it
                                erroTitulo = false
                            }
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        CreateBoxTextField(
                            label = "DESCRIÇÃO",
                            value = descricao,
                            placeholder = "Fale sobre a curadoria desta coleção...",
                            singleLine = false,
                            minLines = 3,
                            onValueChange = { descricao = it }
                        )
                    }
                }

                // ─── Seção 2: Capa da Box (com busca TMDb) ─────────────
                item {
                    SectionCard(
                        icon = Icons.Default.Image,
                        title = "Capa da Box"
                    ) {
                        if (capaSelecionadaUrl != null) {
                            // Capa selecionada exibida
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(150.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.dp, Color(0xFF2C2C33), RoundedCornerShape(10.dp))
                            ) {
                                AsyncImage(
                                    model = capaSelecionadaUrl,
                                    contentDescription = filmeCapaNome,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )

                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(
                                            Brush.verticalGradient(
                                                colors = listOf(
                                                    Color.Transparent,
                                                    Color(0xCC0F0F11)
                                                )
                                            )
                                        )
                                )

                                if (filmeCapaNome != null) {
                                    Text(
                                        text = "🎬 $filmeCapaNome",
                                        color = Color(0xFFF2ECE4),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(12.dp)
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp)
                                ) {
                                    Surface(
                                        modifier = Modifier.clickable {
                                            mostrandoBuscaCapa = !mostrandoBuscaCapa
                                        },
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xDD1F1F24)
                                    ) {
                                        Text(
                                            text = "Trocar",
                                            color = Color(0xFFFFB82E),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        modifier = Modifier.clickable {
                                            capaSelecionadaUrl = null
                                            filmeCapaNome = null
                                        },
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xDD1F1F24)
                                    ) {
                                        Text(
                                            text = "✕",
                                            color = Color(0xFFE05C5C),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        } else {
                            // Caixa de upload / busca vazia com borda tracejada
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF141416))
                                    .border(
                                        width = 1.dp,
                                        color = Color(0xFF2C2C32),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        mostrandoBuscaCapa = !mostrandoBuscaCapa
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(Color(0xFF1E1E22)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Image,
                                            contentDescription = null,
                                            tint = Color(0xFF8C867C),
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Clique para buscar capa no TMDb",
                                        color = Color(0xFFD8D2C7),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Busca filmes disponíveis por nome (1200x630px ou 16:9)",
                                        color = Color(0xFF6E685E),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        }

                        // Interface de Busca de Capa no TMDb
                        AnimatedVisibility(
                            visible = mostrandoBuscaCapa || capaSelecionadaUrl == null,
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(modifier = Modifier.padding(top = 10.dp)) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(44.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF111113))
                                        .border(1.dp, Color(0xFF26262B), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 12.dp),
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
                                        if (buscaCapa.isEmpty()) {
                                            Text(
                                                text = "Buscar filme no TMDb para a capa...",
                                                color = Color(0xFF555048),
                                                fontSize = 13.sp
                                            )
                                        }
                                        BasicTextField(
                                            value = buscaCapa,
                                            onValueChange = { buscaCapa = it },
                                            singleLine = true,
                                            cursorBrush = SolidColor(Color(0xFFFFB82E)),
                                            textStyle = TextStyle(
                                                color = Color(0xFFEDE7DE),
                                                fontSize = 13.sp
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    if (buscandoCapa) {
                                        CircularProgressIndicator(
                                            color = Color(0xFFFFB82E),
                                            modifier = Modifier.size(16.dp),
                                            strokeWidth = 2.dp
                                        )
                                    }
                                }

                                if (resultadosCapa.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Toque em uma capa para selecioná-la:",
                                        color = Color(0xFFC8A97E),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 11.sp
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LazyRow(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        items(resultadosCapa.take(8)) { filme ->
                                            val backdropUrl = filme.backdrop_path?.let { "https://image.tmdb.org/t/p/w780$it" }
                                                ?: filme.poster_path?.let { "https://image.tmdb.org/t/p/w500$it" }

                                            if (backdropUrl != null) {
                                                Column(
                                                    modifier = Modifier
                                                        .width(130.dp)
                                                        .clip(RoundedCornerShape(8.dp))
                                                        .background(Color(0xFF1A1A1E))
                                                        .clickable {
                                                            capaSelecionadaUrl = backdropUrl
                                                            filmeCapaNome = filme.title
                                                            mostrandoBuscaCapa = false
                                                            buscaCapa = ""
                                                        }
                                                ) {
                                                    AsyncImage(
                                                        model = backdropUrl,
                                                        contentDescription = filme.title,
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .height(75.dp),
                                                        contentScale = ContentScale.Crop
                                                    )
                                                    Text(
                                                        text = filme.title,
                                                        color = Color(0xFFDDD7CC),
                                                        style = MaterialTheme.typography.labelSmall,
                                                        maxLines = 1,
                                                        fontSize = 11.sp,
                                                        modifier = Modifier.padding(6.dp)
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

                // ─── Seção 3: Filmes ──────────────────────────────────
                item {
                    SectionCard(
                        icon = Icons.Default.Movie,
                        title = "Filmes"
                    ) {
                        // Campo de busca com ícone de pesquisa
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF111113))
                                .border(1.dp, Color(0xFF26262B), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color(0xFF6E685E),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                if (buscaFilme.isEmpty()) {
                                    Text(
                                        text = "Adicionar Filmes...",
                                        color = Color(0xFF555048),
                                        fontSize = 14.sp
                                    )
                                }
                                BasicTextField(
                                    value = buscaFilme,
                                    onValueChange = { buscaFilme = it },
                                    singleLine = true,
                                    cursorBrush = SolidColor(Color(0xFFFFB82E)),
                                    textStyle = TextStyle(
                                        color = Color(0xFFEDE7DE),
                                        fontSize = 14.sp
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            if (buscandoFilmes) {
                                CircularProgressIndicator(
                                    color = Color(0xFFFFB82E),
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            }
                        }

                        // Resultados da busca com animação
                        AnimatedVisibility(
                            visible = resultadosFilmesBusca.isNotEmpty(),
                            enter = expandVertically(),
                            exit = shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0xFF131316))
                                    .border(1.dp, Color(0xFF26262B), RoundedCornerShape(8.dp))
                            ) {
                                resultadosFilmesBusca.take(5).forEachIndexed { index, filme ->
                                    SearchResultItem(
                                        filme = filme,
                                        onAdd = {
                                            filmesAdicionados.add(filme)
                                            buscaFilme = ""
                                            resultadosFilmesBusca = emptyList()
                                        }
                                    )
                                    if (index < resultadosFilmesBusca.take(5).lastIndex) {
                                        Divider(color = Color(0xFF222228), thickness = 0.5.dp)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Filmes adicionados ou estado vazio
                        if (filmesAdicionados.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(120.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(48.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color(0xFF1A1A1E)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "🎬", fontSize = 22.sp)
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = "Nenhum filme adicionado ainda.",
                                        color = Color(0xFF948D82),
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "Busque acima para começar sua curadoria.",
                                        color = Color(0xFF6E685E),
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                            }
                        } else {
                            filmesAdicionados.forEach { filme ->
                                AddedMovieItem(
                                    filme = filme,
                                    onRemove = { filmesAdicionados.remove(filme) }
                                )
                                Divider(color = Color(0xFF1E1E24), thickness = 0.5.dp)
                            }
                        }
                    }
                }

                // ─── Seção 4: Configurações ───────────────────────────
                item {
                    SectionCard(
                        icon = Icons.Default.Settings,
                        title = "Configurações"
                    ) {
                        // Privacidade
                        Text(
                            text = "Privacidade",
                            color = Color(0xFFEDE7DE),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Quem pode ver esta Box?",
                            color = Color(0xFF6E685E),
                            style = MaterialTheme.typography.labelSmall
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        PrivacidadeSelector(
                            selecionada = privacidade,
                            onSelecionar = { privacidade = it }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Colaborativa com switch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Colaborativa",
                                    color = Color(0xFFEDE7DE),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Permitir que outros adicionem filmes.",
                                    color = Color(0xFF6E685E),
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }

                            Switch(
                                checked = colaborativa,
                                onCheckedChange = { colaborativa = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFFFFB82E),
                                    checkedTrackColor = Color(0xFF382B14),
                                    uncheckedThumbColor = Color(0xFF6E685E),
                                    uncheckedTrackColor = Color(0xFF1E1E24),
                                    uncheckedBorderColor = Color(0xFF2C2C33)
                                )
                            )
                        }
                    }
                }
            }

            // ─── Botão Concluir Criar Box ────────────────────────
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .clickable {
                        if (titulo.isBlank()) {
                            erroTitulo = true
                            return@clickable
                        }
                        val targetPrivacy = when (privacidade) {
                            Privacidade.PUBLICA -> BoxPrivacyType.PUBLICA
                            Privacidade.PRIVADA -> BoxPrivacyType.PRIVADA
                            Privacidade.COLABORATIVA -> BoxPrivacyType.COLABORATIVA
                        }
                        val newBox = BoxDetailRepository.createBox(
                            title = titulo,
                            description = descricao,
                            privacyType = targetPrivacy,
                            coverUrl = capaSelecionadaUrl,
                            filmes = filmesAdicionados.toList()
                        )
                        onBoxCreated(newBox.id)
                    },
                color = Color(0xFFFFB82E),
                shape = RoundedCornerShape(0.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "✓  Criar Box",
                        color = Color(0xFF1A1209),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun CreateBoxTopBar(
    onBack: () -> Unit,
    onOptions: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Voltar",
                tint = Color(0xFFEDE7DE)
            )
        }
        Text(
            text = "Criar Nova Box",
            color = Color(0xFFEDE7DE),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onOptions) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Opções",
                tint = Color(0xFFEDE7DE)
            )
        }
    }
}

@Composable
private fun SectionCard(
    icon: ImageVector,
    title: String,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF151518),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222228))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFFFFB82E),
                    modifier = Modifier.size(19.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    color = Color(0xFFEDE7DE),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            content()
        }
    }
}

@Composable
private fun CreateBoxTextField(
    label: String,
    value: String,
    placeholder: String,
    isError: Boolean = false,
    singleLine: Boolean = true,
    minLines: Int = 1,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = label,
            color = if (isError) Color(0xFFE05C5C) else Color(0xFFC8A97E),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            fontSize = 11.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF111113))
                .border(
                    1.dp,
                    if (isError) Color(0xFFE05C5C) else Color(0xFF26262B),
                    RoundedCornerShape(8.dp)
                )
                .padding(12.dp)
        ) {
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = singleLine,
                minLines = minLines,
                modifier = Modifier.fillMaxWidth(),
                cursorBrush = SolidColor(Color(0xFFFFB82E)),
                textStyle = TextStyle(
                    color = Color(0xFFEDE7DE),
                    fontSize = 14.sp
                ),
                decorationBox = { inner ->
                    if (value.isEmpty()) {
                        Text(text = placeholder, color = Color(0xFF555048), fontSize = 14.sp)
                    }
                    inner()
                }
            )
        }
        if (isError) {
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = "O título é obrigatório.",
                color = Color(0xFFE05C5C),
                style = MaterialTheme.typography.labelSmall
            )
        }
    }
}

@Composable
private fun SearchResultItem(
    filme: Moviefmt,
    onAdd: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAdd() }
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!filme.poster_path.isNullOrBlank()) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w92${filme.poster_path}",
                contentDescription = null,
                modifier = Modifier
                    .size(width = 32.dp, height = 46.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(width = 32.dp, height = 46.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF222228)),
                contentAlignment = Alignment.Center
            ) {
                Text("🎬", fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = filme.title,
                color = Color(0xFFEDE7DE),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp
            )
            if (!filme.release_date.isNullOrBlank()) {
                Text(
                    text = filme.release_date.take(4),
                    color = Color(0xFF7A7368),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }

        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Adicionar",
            tint = Color(0xFFFFB82E),
            modifier = Modifier.size(18.dp)
        )
    }
}

@Composable
private fun AddedMovieItem(
    filme: Moviefmt,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!filme.poster_path.isNullOrBlank()) {
            AsyncImage(
                model = "https://image.tmdb.org/t/p/w92${filme.poster_path}",
                contentDescription = null,
                modifier = Modifier
                    .size(width = 32.dp, height = 46.dp)
                    .clip(RoundedCornerShape(4.dp)),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .size(width = 32.dp, height = 46.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0xFF222228)),
                contentAlignment = Alignment.Center
            ) {
                Text("🎬", fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = filme.title,
                color = Color(0xFFEDE7DE),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp
            )
        }

        IconButton(onClick = onRemove) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Remover",
                tint = Color(0xFF8C867C),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun PrivacidadeSelector(
    selecionada: Privacidade,
    onSelecionar: (Privacidade) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clickable { expanded = true },
            shape = RoundedCornerShape(8.dp),
            color = Color(0xFF111113),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26262B))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = selecionada.label,
                    color = Color(0xFFEDE7DE),
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )
                Text(text = "▾", color = Color(0xFF8C867C), fontSize = 14.sp)
            }
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(Color(0xFF1A1A1E))
        ) {
            Privacidade.entries.forEach { opcao ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = opcao.label,
                            color = if (opcao == selecionada) Color(0xFFFFB82E) else Color(0xFFD8D2C7)
                        )
                    },
                    onClick = {
                        onSelecionar(opcao)
                        expanded = false
                    }
                )
            }
        }
    }
}
