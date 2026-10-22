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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.boxel.meuboxfavorito.ui.navigation.AppBottomBar
import com.boxel.meuboxfavorito.ui.navigation.AppRoutes

// ─────────────────────────────────────────────────────────
// Modelo de dados – virá do backend próprio no futuro
// TODO: substituir por dados reais do usuário autenticado
// ─────────────────────────────────────────────────────────
private data class UserProfile(
    val displayName: String,
    val username: String,
    val bio: String,
    val joinYear: Int,
    val totalBoxes: Int,
    val totalFilmes: Int,
    val seguidores: Int
)

private data class ProfileBox(
    val id: Int,
    val title: String,
    val descricao: String,
    val quantidade: Int,
    // TODO: substituir por URL real da capa vinda do backend
    val coverEmoji: String
)

@Composable
fun ProfileScreen(
    onNavigate: (String) -> Unit
) {

    // TODO: substituir por ViewModel + dados reais
    val perfil = UserProfile(
        displayName = "Cinematic Explorer",
        username = "@cinephile_x",
        bio = "Curando as melhores experiências sci-fi e neo-noir. Construindo a biblioteca definitiva de narrativas visualmente deslumbrantes.",
        joinYear = 2023,
        totalBoxes = 12,
        totalFilmes = 184,
        seguidores = 342
    )

    val minhasBoxes = listOf(
        ProfileBox(id = 1, title = "Noir Essentials", descricao = "Curadoria de clássicos neo-noir.", quantidade = 12, coverEmoji = "🎭"),
        ProfileBox(id = 2, title = "Cyberpunk 2077 Mood", descricao = "Estética neon e distopia urbana.", quantidade = 8, coverEmoji = "🤖"),
        ProfileBox(id = 3, title = "Sci-Fi Classics", descricao = "O melhor da ficção científica espacial.", quantidade = 15, coverEmoji = "🚀")
    )

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
                        onEditClick = {
                            // TODO: navegar para tela de edição de perfil
                        },
                        onShareClick = {
                            // TODO: compartilhar perfil
                        }
                    )
                }

                // ─── Estatísticas ─────────────────────────
                item {
                    ProfileStats(perfil = perfil)
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

                items(count = minhasBoxes.size) { index ->
                    val box = minhasBoxes[index]
                    ProfileBoxCard(
                        box = box,
                        onClick = {
                            // TODO: navegar para BoxDetailScreen quando tivermos ID real
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // ─── Sincronização Externa ────────────────
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    ExternalSyncCard()
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

        // Avatar placeholder
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
            // TODO: substituir por AsyncImage com URL do avatar do usuário
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

            // Botão Editar Perfil
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

            // Botão Compartilhar
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
    perfil: UserProfile
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        StatItem(label = "Boxes", value = perfil.totalBoxes.toString())
        StatDivider()
        StatItem(label = "Filmes", value = perfil.totalFilmes.toString())
        StatDivider()
        StatItem(label = "Seguidores", value = perfil.seguidores.toString())
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
    box: ProfileBox,
    onClick: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF181611)
    ) {

        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // Capa placeholder
            // TODO: substituir por AsyncImage com URL real da capa da box
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2A2520)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = box.coverEmoji, style = MaterialTheme.typography.headlineMedium)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = box.title,
                    color = Color(0xFFEAE5DF),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = box.descricao,
                    color = Color(0xFF8C8376),
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "▦  ${box.quantidade} Filmes",
                    color = Color(0xFF6A6460),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

@Composable
private fun ExternalSyncCard() {

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFF181611)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SwapHoriz,
                    contentDescription = null,
                    tint = Color(0xFFFFB82E),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sincronização Externa",
                    color = Color(0xFFEAE5DF),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Mantenha seu histórico atualizado.",
                color = Color(0xFF8C8376),
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Botão Importar do Letterboxd
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .clickable {
                        // TODO: integrar importação do Letterboxd
                    },
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF1E1C17)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = Color(0xFFD8D2C7),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Importar do Letterboxd",
                        color = Color(0xFFD8D2C7),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    }
}
