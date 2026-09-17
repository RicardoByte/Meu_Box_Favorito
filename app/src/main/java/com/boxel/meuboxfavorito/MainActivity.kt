package com.boxel.meuboxfavorito

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.boxel.meuboxfavorito.ui.theme.MeuBoxFavoritoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MeuBoxFavoritoTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { innerPadding ->
                    TelaFilmesPopulares(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}


data class Filme(
    val id: Int,
    val titulo: String,
    val sinopse: String,
    val posterPath: String,
    val dataLancamento: String,
    val notaMedia: Double
) {
    val urlPoster: String
        get() = if (posterPath.isNotBlank()) "https://image.tmdb.org/t/p/w342$posterPath" else ""
}


 */
sealed interface EstadoUi {
    object Carregando : EstadoUi
    data class Sucesso(val filmes: List<Filme>) : EstadoUi
    data class Erro(val mensagem: String) : EstadoUi
}

private const val TMDB_API_KEY = "297254462557e30d1f85705ea8e31167"


suspend fun buscarFilmesPopulares(): List<Filme> = withContext(Dispatchers.IO) {
    val urlApi =
        "https://api.themoviedb.org/3/movie/popular" +
            "?api_key=$TMDB_API_KEY&language=pt-BR&page=1"
    val url = URL(urlApi)

    val conexao = url.openConnection() as HttpURLConnection

    try {
    
        conexao.requestMethod = "GET"
        conexao.connectTimeout = 10000 
        conexao.readTimeout = 10000    
        conexao.setRequestProperty("Accept", "application/json")
     
        val codigoResposta = conexao.responseCode
        if (codigoResposta != HttpURLConnection.HTTP_OK) {
            throw Exception("Falha na conexão HTTP. Código de resposta: $codigoResposta")
        }

        val leitor = BufferedReader(InputStreamReader(conexao.inputStream))
        val respostaTexto = StringBuilder()
        var linha: String?

        while (leitor.readLine().also { linha = it } != null) {
            respostaTexto.append(linha)
        }
        leitor.close()

        val jsonRaiz = JSONObject(respostaTexto.toString())
        val jsonArray = jsonRaiz.getJSONArray("results")
        val listaFilmes = mutableListOf<Filme>()

        for (i in 0 until jsonArray.length()) {
            val itemJson = jsonArray.getJSONObject(i)

            val filme = Filme(
                id = itemJson.getInt("id"),
                titulo = itemJson.optString("title", "Sem título"),
                sinopse = itemJson.optString("overview", "Sinopse não disponível."),
                posterPath = itemJson.optString("poster_path", ""),
                dataLancamento = itemJson.optString("release_date", "—"),
                notaMedia = itemJson.optDouble("vote_average", 0.0)
            )
            listaFilmes.add(filme)
        }

        return@withContext listaFilmes
    } finally {
        conexao.disconnect()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaFilmesPopulares(modifier: Modifier = Modifier) {
    
    val coroutineScope = rememberCoroutineScope()

    var estadoUi by remember { mutableStateOf<EstadoUi>(EstadoUi.Carregando) }

    fun carregarDados() {
        coroutineScope.launch {
            estadoUi = EstadoUi.Carregando
            try {
                val filmes = buscarFilmesPopulares()
                estadoUi = EstadoUi.Sucesso(filmes)
            } catch (e: Exception) {
                estadoUi = EstadoUi.Erro(e.localizedMessage ?: "Ocorreu um erro desconhecido.")
            }
        }
    }

    LaunchedEffect(Unit) {
        carregarDados()
    }

    Column(modifier = modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Movie,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Text(text = "Meu Box Favorito — Em Alta")
                }
            },
            actions = {
                IconButton(onClick = { carregarDados() }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Recarregar Lista"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
            )
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            when (val estado = estadoUi) {
                is EstadoUi.Carregando -> {
                    TelaCarregando()
                }
                is EstadoUi.Sucesso -> {
                    TelaListaFilmes(filmes = estado.filmes)
                }
                is EstadoUi.Erro -> {
                    TelaErro(
                        mensagem = estado.mensagem,
                        onTentarNovamente = { carregarDados() }
                    )
                }
            }
        }
    }
}

@Composable
fun TelaCarregando() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Buscando filmes em alta no TMDb...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun TelaErro(mensagem: String, onTentarNovamente: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Ops! Algo deu errado ao carregar a API:",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.error,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = mensagem,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onTentarNovamente) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Tentar Novamente")
        }
    }
}

@Composable
fun TelaListaFilmes(filmes: List<Filme>) {
    if (filmes.isEmpty()) {
        Text(text = "Nenhum filme encontrado.")
    } else {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            items(filmes) { filme ->
                ItemFilme(filme = filme)
            }
        }
    }
}

@Composable
fun ItemFilme(filme: Filme) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = String.format(Locale.getDefault(), "%.1f", filme.notaMedia),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = filme.dataLancamento,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = filme.titulo,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = filme.sinopse,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
