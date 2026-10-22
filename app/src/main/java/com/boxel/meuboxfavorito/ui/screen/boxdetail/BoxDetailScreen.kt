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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Forum
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.boxel.meuboxfavorito.ui.navigation.AppBottomBar

data class RespostaComunidade(
    val id: Int,
    val autor: String,
    val tempoRelativo: String,
    val conteudo: String,
    val curtidas: Int,
    val avatarUrl: String? = null,
    val curtidoPorMim: Boolean = false
)

data class BoxDiscussionData(
    val id: Int,
    val badgeTema: String,
    val tituloDiscussao: String,
    val proprietarioNome: String,
    val proprietarioAvatar: String? = null,
    val tempoPublicacao: String,
    val spoilersLiberados: Boolean = true,
    val ratingEstrelas: Int = 4,
    val textoDiscussao: String,
    val curtidas: Int = 245,
    val comentariosContador: Int = 82
)

private fun getBoxDiscussion(boxId: Int): BoxDiscussionData {
    return BoxDiscussionData(
        id = boxId,
        badgeTema = "BLADE RUNNER 2049",
        tituloDiscussao = "Porque o Ryan gosling é tão bom?\nBlade Runner",
        proprietarioNome = "Alex Mercer",
        proprietarioAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&auto=format&fit=crop&q=80",
        tempoPublicacao = "Há 2 horas",
        spoilersLiberados = true,
        ratingEstrelas = 4,
        textoDiscussao = "Ryan Gosling em Blade Runner 2049 é a prova de que um homem só precisa de uma jaqueta maneira, silêncio absoluto e luzes neon para entregar a melhor crise existencial cibernética da história do cinema. Ninguém encara o vazio e chora na chuva com tanto estilo. Farma muito meu mano Ryan!!!",
        curtidas = 245,
        comentariosContador = 82
    )
}

@Composable
fun BoxDetailScreen(
    boxId: Int,
    onBack: () -> Unit,
    onMovieClick: (Int) -> Unit = {},
    onDiscussionClick: () -> Unit = {},
    onNavigate: (String) -> Unit = {}
) {
    val discussion = remember(boxId) { getBoxDiscussion(boxId) }
    var curtido by remember { mutableStateOf(false) }
    var curtidasCount by remember { mutableStateOf(discussion.curtidas) }
    var novoComentarioTexto by remember { mutableStateOf("") }

    val comentarios = remember {
        mutableStateListOf(
            RespostaComunidade(
                id = 1,
                autor = "Sarah Connor",
                tempoRelativo = "Há 45 min",
                conteudo = "Concordo totalmente. A cena em que ele olha para o holograma gigante rosa da Joi é a síntese do filme. A tecnologia prometendo intimidade em um mundo onde a conexão real foi destruída pelo corporativismo.",
                curtidas = 42,
                avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&auto=format&fit=crop&q=80"
            ),
            RespostaComunidade(
                id = 2,
                autor = "Rick D.",
                tempoRelativo = "Há 20 min",
                conteudo = "Exato! E o design de som nessa cena é ensurdecedor namoral. Hans Zimmer entregou tudo.",
                curtidas = 12,
                avatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150&auto=format&fit=crop&q=80"
            )
        )
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
            BoxDiscussionTopBar(onBack = onBack)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ─── Badge Tema e Título ───────────────────────────
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            shape = RoundedCornerShape(50),
                            color = Color(0xFF221F28),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF332D3B))
                        ) {
                            Text(
                                text = discussion.badgeTema,
                                color = Color(0xFFDEB878),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.2.sp,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = discussion.tituloDiscussao,
                            color = Color(0xFFEDE7DE),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }

                // ─── Card do Proprietário da Box ───────────────────
                item {
                    OwnerDiscussionCard(
                        data = discussion,
                        curtido = curtido,
                        curtidasCount = curtidasCount,
                        onCurtir = {
                            curtido = !curtido
                            curtidasCount = if (curtido) curtidasCount + 1 else curtidasCount - 1
                        }
                    )
                }

                // ─── Cabeçalho "Respostas da Comunidade" ───────────
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChatBubbleOutline,
                            contentDescription = null,
                            tint = Color(0xFFEDE7DE),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Respostas da Comunidade",
                            color = Color(0xFFEDE7DE),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                }

                // ─── Input Bar para Adicionar à Discussão ─────────
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar do usuário atual
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF222228)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ChatBubbleOutline,
                                contentDescription = null,
                                tint = Color(0xFF8C867C),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Row(
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF161619))
                                .border(1.dp, Color(0xFF26262B), RoundedCornerShape(10.dp))
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(modifier = Modifier.weight(1f)) {
                                if (novoComentarioTexto.isEmpty()) {
                                    Text(
                                        text = "Adicione à discussão...",
                                        color = Color(0xFF6E685E),
                                        fontSize = 13.sp
                                    )
                                }
                                BasicTextField(
                                    value = novoComentarioTexto,
                                    onValueChange = { novoComentarioTexto = it },
                                    singleLine = true,
                                    cursorBrush = SolidColor(Color(0xFFFFB82E)),
                                    textStyle = TextStyle(
                                        color = Color(0xFFEDE7DE),
                                        fontSize = 13.sp
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = {
                                    if (novoComentarioTexto.isNotBlank()) {
                                        comentarios.add(
                                            0,
                                            RespostaComunidade(
                                                id = System.currentTimeMillis().toInt(),
                                                autor = "Você",
                                                tempoRelativo = "Agora",
                                                conteudo = novoComentarioTexto.trim(),
                                                curtidas = 0
                                            )
                                        )
                                        novoComentarioTexto = ""
                                    }
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Enviar",
                                    tint = if (novoComentarioTexto.isNotBlank()) Color(0xFFFFB82E) else Color(0xFF6E685E),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }

                // ─── Comentários das Pessoas ──────────────────────
                items(comentarios) { comentario ->
                    CommunityCommentCard(comentario = comentario)
                }
            }

            // ─── Bottom Navigation Bar ────────────────────────────
            AppBottomBar(
                selectedRoute = "my_boxes",
                onNavigate = onNavigate
            )
        }
    }
}

@Composable
private fun BoxDiscussionTopBar(onBack: () -> Unit) {
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
            text = "Discussion",
            color = Color(0xFFEDE7DE),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )

        IconButton(onClick = { /* Opções extras */ }) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "Opções",
                tint = Color(0xFFEDE7DE)
            )
        }
    }
}

@Composable
private fun OwnerDiscussionCard(
    data: BoxDiscussionData,
    curtido: Boolean,
    curtidasCount: Int,
    onCurtir: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF161619),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222228))
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Barra amarela/dourada de destaque na lateral esquerda
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(200.dp)
                    .background(
                        Color(0xFFFFB82E),
                        shape = RoundedCornerShape(topStart = 14.dp, bottomStart = 14.dp)
                    )
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(16.dp)
            ) {
                // Header do proprietário
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF23232A))
                            .border(1.dp, Color(0xFF383842), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (data.proprietarioAvatar != null) {
                            AsyncImage(
                                model = data.proprietarioAvatar,
                                contentDescription = data.proprietarioNome,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = androidx.compose.ui.layout.ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = data.proprietarioNome.first().toString(),
                                color = Color(0xFFFFB82E),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = data.proprietarioNome,
                            color = Color(0xFFEDE7DE),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${data.tempoPublicacao} • ",
                                color = Color(0xFF7A7368),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Spoilers Liberados",
                                color = Color(0xFF7A7368),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 11.sp
                            )
                        }
                    }

                    // Estrelas de Avaliação (4 estrelas preenchidas, 1 vazia)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < data.ratingEstrelas) Color(0xFFFFB82E) else Color(0xFF35353C),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Texto da Discussão
                Text(
                    text = data.textoDiscussao,
                    color = Color(0xFFC8C2B8),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Barra de reações e ações
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Curtidas
                    Row(
                        modifier = Modifier
                            .clickable { onCurtir() }
                            .padding(end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (curtido) Icons.Default.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Curtir",
                            tint = if (curtido) Color(0xFFE05C5C) else Color(0xFF948D82),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = curtidasCount.toString(),
                            color = Color(0xFF948D82),
                            style = MaterialTheme.typography.labelMedium,
                            fontSize = 12.sp
                        )
                    }

                    // Comentários
                    Row(
                        modifier = Modifier.padding(end = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ChatBubbleOutline,
                            contentDescription = "Comentários",
                            tint = Color(0xFF948D82),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = data.comentariosContador.toString(),
                            color = Color(0xFF948D82),
                            style = MaterialTheme.typography.labelMedium,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Compartilhar
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Compartilhar",
                        tint = Color(0xFF948D82),
                        modifier = Modifier
                            .size(18.dp)
                            .clickable { /* Compartilhar */ }
                    )
                }
            }
        }
    }
}

@Composable
private fun CommunityCommentCard(
    comentario: RespostaComunidade
) {
    var curtido by remember { mutableStateOf(comentario.curtidoPorMim) }
    var curtidas by remember { mutableStateOf(comentario.curtidas) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF151518),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1F1F24))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header do comentário: avatar + nome + tempo relativo
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF222228)),
                    contentAlignment = Alignment.Center
                ) {
                    if (comentario.avatarUrl != null) {
                        AsyncImage(
                            model = comentario.avatarUrl,
                            contentDescription = comentario.autor,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = comentario.autor.first().toString(),
                            color = Color(0xFFFFB82E),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = comentario.autor,
                    color = Color(0xFFEDE7DE),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f)
                )

                Text(
                    text = comentario.tempoRelativo,
                    color = Color(0xFF6E685E),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Conteúdo do comentário
            Text(
                text = comentario.conteudo,
                color = Color(0xFFB5AFA4),
                style = MaterialTheme.typography.bodySmall,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Ações: Curtir (polegar para cima) e Responder
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.clickable {
                        curtido = !curtido
                        curtidas = if (curtido) curtidas + 1 else curtidas - 1
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (curtido) Icons.Default.ThumbUp else Icons.Outlined.ThumbUp,
                        contentDescription = "Curtir comentário",
                        tint = if (curtido) Color(0xFFFFB82E) else Color(0xFF7A7368),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = curtidas.toString(),
                        color = Color(0xFF7A7368),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "Responder",
                    color = Color(0xFF7A7368),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { /* Responder */ }
                )
            }
        }
    }
}
