package com.boxel.meuboxfavorito.ui.screen.profile

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.boxel.meuboxfavorito.models.BoxDetailData
import com.boxel.meuboxfavorito.models.BoxPrivacyType
import com.boxel.meuboxfavorito.models.UserAccount
import com.boxel.meuboxfavorito.ui.navigation.AppBottomBar
import com.boxel.meuboxfavorito.ui.navigation.AppRoutes
import com.boxel.meuboxfavorito.ui.view.ProfileViewModel

@Composable
fun ProfileScreen(
    onNavigate: (String) -> Unit,
    viewModel: ProfileViewModel = viewModel()
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val userBoxes by viewModel.userBoxes.collectAsState()
    val totalFilmes by viewModel.totalFilmes.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

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
                        user = currentUser,
                        onEditClick = { viewModel.setShowEditDialog(true) },
                        onLogoutClick = {
                            viewModel.logout()
                            onNavigate(AppRoutes.AUTH)
                        }
                    )
                }

                // ─── Estatísticas ─────────────────────────
                item {
                    ProfileStats(
                        totalBoxes = userBoxes.size,
                        totalFilmes = totalFilmes,
                        seguidores = currentUser?.seguidores ?: 342
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

    if (uiState.showEditDialog) {
        EditProfileDialog(
            currentUser = currentUser,
            onDismiss = { viewModel.setShowEditDialog(false) },
            onSave = { novoNome, novaBio, novoAvatar ->
                viewModel.updateProfile(novoNome, novaBio, novoAvatar)
            }
        )
    }
}

@Composable
private fun ProfileHeader(
    user: UserAccount?,
    onEditClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val displayName = user?.nomeCompleto ?: "Ricardo Byte"
    val username = "@${user?.nomeUsuario ?: "ricardobyte"}"
    val bio = user?.bio ?: "Curando as melhores experiências sci-fi, suspense psicológico e neo-noir."
    val joinYear = user?.joinYear ?: 2024
    val avatarUrl = user?.avatarUrl

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
            if (!avatarUrl.isNullOrBlank()) {
                AsyncImage(
                    model = avatarUrl,
                    contentDescription = "Avatar",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Avatar",
                    tint = Color(0xFFFFB82E),
                    modifier = Modifier.size(44.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = displayName,
            color = Color(0xFFFFB82E),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "$username  •  Entrou em $joinYear",
            color = Color(0xFF8C8376),
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = bio,
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
                    .clickable { onLogoutClick() },
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFF1E1C17),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2822))
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Sair",
                        tint = Color(0xFFE05C5C),
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

/**
 * Diálogo interativo para edição dos inputs do perfil do usuário com validação e persistência.
 */
@Composable
private fun EditProfileDialog(
    currentUser: UserAccount?,
    onDismiss: () -> Unit,
    onSave: (nomeCompleto: String, bio: String, avatarUrl: String?) -> Unit
) {
    var nome by remember { mutableStateOf(currentUser?.nomeCompleto ?: "Ricardo Byte") }
    var bio by remember { mutableStateOf(currentUser?.bio ?: "") }
    var avatar by remember { mutableStateOf(currentUser?.avatarUrl ?: "") }
    var erroNome by remember { mutableStateOf<String?>(null) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            color = Color(0xFF19181B),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2A30))
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Editar Perfil",
                        color = Color(0xFFF2ECE4),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Fechar",
                        tint = Color(0xFF8C867C),
                        modifier = Modifier
                            .size(20.dp)
                            .clickable { onDismiss() }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Campo Nome
                Text(
                    text = "NOME DE EXIBIÇÃO",
                    color = Color(0xFFC8A97E),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF101012))
                        .border(
                            1.dp,
                            if (erroNome != null) Color(0xFFE05C5C) else Color(0xFF2C2A30),
                            RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
                ) {
                    BasicTextField(
                        value = nome,
                        onValueChange = {
                            nome = it
                            erroNome = null
                        },
                        singleLine = true,
                        cursorBrush = SolidColor(Color(0xFFFFB82E)),
                        textStyle = TextStyle(color = Color(0xFFEDE7DE), fontSize = 14.sp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                if (erroNome != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = erroNome!!,
                        color = Color(0xFFE05C5C),
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Campo Bio
                Text(
                    text = "BIOGRAFIA CINEMATOGRÁFICA",
                    color = Color(0xFFC8A97E),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF101012))
                        .border(1.dp, Color(0xFF2C2A30), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    BasicTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        singleLine = false,
                        minLines = 3,
                        cursorBrush = SolidColor(Color(0xFFFFB82E)),
                        textStyle = TextStyle(color = Color(0xFFEDE7DE), fontSize = 14.sp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Campo Avatar URL
                Text(
                    text = "URL DA FOTO DE PERFIL (OPCIONAL)",
                    color = Color(0xFFC8A97E),
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF101012))
                        .border(1.dp, Color(0xFF2C2A30), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    BasicTextField(
                        value = avatar,
                        onValueChange = { avatar = it },
                        singleLine = true,
                        cursorBrush = SolidColor(Color(0xFFFFB82E)),
                        textStyle = TextStyle(color = Color(0xFFEDE7DE), fontSize = 14.sp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Botão Salvar
                Button(
                    onClick = {
                        if (nome.isBlank() || nome.trim().length < 3) {
                            erroNome = "O nome deve ter no mínimo 3 caracteres"
                            return@Button
                        }
                        onSave(nome, bio, avatar.ifBlank { null })
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFB82E))
                ) {
                    Text(
                        text = "Salvar Alterações",
                        color = Color(0xFF1A1209),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}
