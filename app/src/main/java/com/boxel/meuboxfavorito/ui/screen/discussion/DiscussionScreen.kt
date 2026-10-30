package com.boxel.meuboxfavorito.ui.screen.discussion

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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.boxel.meuboxfavorito.data.repository.BoxDetailRepository
import com.boxel.meuboxfavorito.data.repository.BoxPrivacyType
import com.boxel.meuboxfavorito.data.repository.BoxReviewItem
import com.boxel.meuboxfavorito.data.repository.RespostaComunidade
import com.boxel.meuboxfavorito.ui.navigation.AppBottomBar

@Composable
fun DiscussionScreen(
    boxId: Int,
    reviewId: Int = 101,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit = {}
) {
    val boxData = remember(boxId) { BoxDetailRepository.getBoxById(boxId) }
    val initialReview = remember(boxId, reviewId) {
        BoxDetailRepository.getReviewById(boxId, reviewId) ?: boxData.reviews.first()
    }

    var review by remember(initialReview) { mutableStateOf(initialReview) }
    var curtido by remember(review) { mutableStateOf(review.isLiked) }
    var curtidasCount by remember(review) { mutableIntStateOf(review.likesCount) }
    var novoComentarioTexto by remember { mutableStateOf("") }

    val comentarios = remember(review) {
        mutableStateListOf<RespostaComunidade>().apply {
            addAll(review.replies)
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
            DiscussionTopBar(onBack = onBack)

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
                                text = review.movieTitle.uppercase(),
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
                            text = review.title,
                            color = Color(0xFFEDE7DE),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }

                // ─── Card Principal da Resenha / Autor ─────────────
                item {
                    ReviewDetailOwnerCard(
                        review = review,
                        curtido = curtido,
                        curtidasCount = curtidasCount,
                        comentariosCount = comentarios.size,
                        onCurtir = {
                            curtido = !curtido
                            curtidasCount = if (curtido) curtidasCount + 1 else curtidasCount - 1
                            BoxDetailRepository.toggleLikeReview(boxId, review.id)
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
                            text = if (boxData.privacyType == BoxPrivacyType.PRIVADA) {
                                "Minhas Notas & Anotações"
                            } else {
                                "Respostas da Comunidade"
                            },
                            color = Color(0xFFEDE7DE),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "${comentarios.size} respostas",
                            color = Color(0xFF8C867C),
                            fontSize = 12.sp
                        )
                    }
                }

                // ─── Input Bar para Adicionar Comentário ─────────
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF222228)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "V",
                                color = Color(0xFFFFB82E),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
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
                                        text = if (boxData.privacyType == BoxPrivacyType.PRIVADA) {
                                            "Adicionar nota pessoal..."
                                        } else {
                                            "Adicione à discussão..."
                                        },
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
                                        val novo = BoxDetailRepository.addCommentToReview(
                                            boxId = boxId,
                                            reviewId = review.id,
                                            content = novoComentarioTexto
                                        )
                                        if (novo != null) {
                                            comentarios.add(0, novo)
                                        }
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

                // ─── Comentários e Respostas ───────────────────────
                if (comentarios.isEmpty()) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 30.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Nenhuma resposta ainda. Seja o primeiro a debater!",
                                color = Color(0xFF6E685E),
                                fontSize = 13.sp
                            )
                        }
                    }
                } else {
                    items(comentarios, key = { it.id }) { comentario ->
                        CommunityCommentCard(comentario = comentario)
                    }
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
private fun DiscussionTopBar(onBack: () -> Unit) {
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
            text = "Discussão",
            color = Color(0xFFEDE7DE),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
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
private fun ReviewDetailOwnerCard(
    review: BoxReviewItem,
    curtido: Boolean,
    curtidasCount: Int,
    comentariosCount: Int,
    onCurtir: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = Color(0xFF161619),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222228))
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            // Barra dourada lateral de destaque
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
                // Header do autor da resenha
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
                                fontSize = 16.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = review.authorName,
                            color = Color(0xFFEDE7DE),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${review.timeAgo} • ",
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

                    // Estrelas de Avaliação
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = if (index < review.rating.toInt()) Color(0xFFFFB82E) else Color(0xFF35353C),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Texto Completo da Resenha
                Text(
                    text = review.body,
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
                            text = comentariosCount.toString(),
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
    var curtidas by remember { mutableIntStateOf(comentario.curtidas) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF151518),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1F1F24))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header do comentário: avatar + autor + tempo
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
                            contentScale = ContentScale.Crop
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

            // Ações: Curtir e Responder
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
