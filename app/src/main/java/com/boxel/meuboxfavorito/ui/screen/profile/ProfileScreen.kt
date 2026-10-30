package com.boxel.meuboxfavorito.ui.screen.profile

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.boxel.meuboxfavorito.data.repository.BoxDetailData
import com.boxel.meuboxfavorito.data.repository.BoxDetailRepository
import com.boxel.meuboxfavorito.data.repository.BoxPrivacyType
import com.boxel.meuboxfavorito.ui.navigation.AppBottomBar
import com.boxel.meuboxfavorito.ui.navigation.AppRoutes

private data class UserProfile(
    val displayName: String,
    val username: String,
    val bio: String,
    val joinYear: Int,
    val seguidores: Int
)

@Composable
fun ProfileScreen(
    onNavigate: (String) -> Unit
) {
    val userBoxes = remember { BoxDetailRepository.getUserBoxes() }

    val perfil = remember(userBoxes) {
        UserProfile(
            displayName = "Ricardo Byte",
            username = "@ricardobyte",
            bio = "Curando as melhores experiências sci-fi, suspense psicológico e neo-noir. Construindo a biblioteca definitiva de narrativas visuais.",
            joinYear = 2024,
            seguidores = 342
        )
    }

    val totalFilmes = remember(userBoxes) {
        userBoxes.sumOf { it.filmesCount }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF101011)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 90.dp)
            ) {
                // ─── Cabeçalho ───────────────────────────
                item {
                    ProfileHeader(
                        perfil = perfil,
                        onEditClick = { /* Edição de perfil */ },
                        onShareClick = { /* Compartilhar perfil */ }
                    )
                }

                // ─── Estatísticas ─────────────────────────
                item {
                    ProfileStats(
                        totalBoxes = userBoxes.size,
                        totalFilmes = totalFilmes,
                        seguidores = perfil.seguidores
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                // ─── Minhas Boxes ─────────────────────────
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Minhas Boxes",
                            color = Color(0xFFE8E4E0),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )

                        Text(
                            text = "Ver Todas",
                            color = Color(0xFFFFB82E),
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable {
                                onNavigate(AppRoutes.MY_BOXES)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (userBoxes.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                                .clickable { onNavigate(AppRoutes.CREATE_BOX) },
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF181611),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26262B))
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Folder,
                                    contentDescription = null,
                                    tint = Color(0xFFFFB82E),
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Você ainda não criou nenhuma box",
                                    color = Color(0xFFEAE5DF),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Toque aqui para criar sua primeira coleção de curadoria.",
                                    color = Color(0xFF8C8376),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }
                } else {
                    items(
                        items = userBoxes,
                        key = { it.id }
                    ) { box ->
                        ProfileBoxCard(
                            box = box,
                            onClick = {
                                onNavigate(AppRoutes.boxDetail(box.id))
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            AppBottomBar(
                selectedRoute = AppRoutes.PROFILE,
                onNavigate = onNavigate,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun ProfileHeader(
    perfil: UserProfile,
    onEditClick: () -> Unit,
    onShareClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(Color(0xFF2A2520))
                .border(width = 2.dp, color = Color(0xFFFFB82E), shape = CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Avatar",
                tint = Color(0xFFFFB82E),
                modifier = Modifier.size(44.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = perfil.displayName,
            color = Color(0xFFFFB82E),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "${perfil.username}  •  Entrou em ${perfil.joinYear}",
            color = Color(0xFF8C8376),
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = perfil.bio,
            color = Color(0xFFB0AA9E),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .height(42.dp)
                    .clickable { onEditClick() },
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFFFB82E)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        tint = Color(0xFF1A1209),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Editar Perfil",
                        color = Color(0xFF1A1209),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .size(42.dp)
                    .clickable { onShareClick() },
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF1E1C17)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Compartilhar",
                        tint = Color(0xFFD8D2C7),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ProfileStats(
    totalBoxes: Int,
    totalFilmes: Int,
    seguidores: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(label = "Boxes", value = totalBoxes.toString())
        StatDivider()
        StatItem(label = "Filmes", value = totalFilmes.toString())
        StatDivider()
        StatItem(label = "Seguidores", value = seguidores.toString())
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = Color(0xFFFFB82E),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color(0xFF8C8376),
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@Composable
private fun StatDivider() {
    Box(
        modifier = Modifier
            .width(1.dp)
            .height(36.dp)
            .background(Color(0xFF2A2722))
    )
}

@Composable
private fun ProfileBoxCard(
    box: BoxDetailData,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF181611),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF26262B))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(68.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2A2520)),
                contentAlignment = Alignment.Center
            ) {
                val imageUrl = box.coverUrl
                    ?: box.filmes.firstOrNull()?.poster_path?.let {
                        if (it.startsWith("http")) it else "https://image.tmdb.org/t/p/w500$it"
                    }

                if (imageUrl != null) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = box.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Folder,
                        contentDescription = null,
                        tint = Color(0xFFFFB82E),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = box.title,
                        color = Color(0xFFEAE5DF),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    val privacyIcon = when (box.privacyType) {
                        BoxPrivacyType.PRIVADA -> Icons.Default.Lock
                        else -> Icons.Default.Public
                    }
                    Icon(
                        imageVector = privacyIcon,
                        contentDescription = box.privacyType.label,
                        tint = Color(0xFF8C8376),
                        modifier = Modifier.size(13.dp)
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = box.description,
                    color = Color(0xFF8C8376),
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "▦ ${box.filmesCount} Filmes",
                        color = Color(0xFFFFB82E),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "•",
                        color = Color(0xFF555048),
                        fontSize = 10.sp
                    )
                    Text(
                        text = "${box.reviews.size} Resenhas",
                        color = Color(0xFF9E978C),
                        style = MaterialTheme.typography.labelSmall
                    )
                    Text(
                        text = "•",
                        color = Color(0xFF555048),
                        fontSize = 10.sp
                    )
                    Text(
                        text = box.privacyType.label,
                        color = Color(0xFFBEB7AE),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}
