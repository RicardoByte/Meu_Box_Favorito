package com.boxel.meuboxfavorito.ui.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RegisterScreen(
    onBack: () -> Unit,
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var nomeCompleto by remember { mutableStateOf("") }
    var nomeUsuario by remember { mutableStateOf("cinemaniaco") }
    var email by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var senhaVisivel by remember { mutableStateOf(false) }
    var confirmarSenha by remember { mutableStateOf("") }
    var termosAceitos by remember { mutableStateOf(true) }

    val todosGeneros = listOf(
        "Ficção Científica",
        "Noir",
        "Cult",
        "Terror",
        "Drama",
        "Clássicos",
        "Cinema de Autor"
    )
    val generosSelecionados = remember {
        mutableStateListOf("Noir", "Terror")
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFF0D0D0E)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
        ) {
            // ─── Top Bar ──────────────────────────────────────────
            RegisterTopBar(onBack = onBack)

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ─── Hero Header Card ─────────────────────────────
                item {
                    RegisterHeroCard()
                }

                // ─── Nome Completo ────────────────────────────────
                item {
                    FormField(
                        label = "NOME COMPLETO",
                        badge = "Obrigatório",
                        badgeColor = Color(0xFFC8A97E)
                    ) {
                        CustomTextInput(
                            value = nomeCompleto,
                            onValueChange = { nomeCompleto = it },
                            placeholder = "Ex: Arthur Fleck",
                            leadingIcon = Icons.Outlined.Person
                        )
                    }
                }

                // ─── Nome de Usuário ──────────────────────────────
                item {
                    FormField(
                        label = "NOME DE USUÁRIO",
                        badge = "✓ Disponível",
                        badgeColor = Color(0xFF4EBA75)
                    ) {
                        CustomTextInput(
                            value = nomeUsuario,
                            onValueChange = { nomeUsuario = it },
                            placeholder = "cinemaniaco",
                            prefix = "@",
                            trailingIcon = {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Disponível",
                                    tint = Color(0xFF4EBA75),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        )
                    }
                }

                // ─── E-mail ───────────────────────────────────────
                item {
                    FormField(
                        label = "E-MAIL",
                        badgeColor = Color(0xFF8C8376)
                    ) {
                        CustomTextInput(
                            value = email,
                            onValueChange = { email = it },
                            placeholder = "curador@cinema.com",
                            leadingIcon = Icons.Outlined.Email,
                            keyboardType = KeyboardType.Email
                        )
                    }
                }

                // ─── Senha ────────────────────────────────────────
                item {
                    FormField(
                        label = "SENHA",
                        badgeColor = Color(0xFF8C8376)
                    ) {
                        CustomTextInput(
                            value = senha,
                            onValueChange = { senha = it },
                            placeholder = "Mínimo 8 caracteres",
                            leadingIcon = Icons.Outlined.Lock,
                            visualTransformation = if (senhaVisivel) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(
                                    onClick = { senhaVisivel = !senhaVisivel },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = if (senhaVisivel) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                        contentDescription = "Mostrar senha",
                                        tint = Color(0xFF8C8376),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Barra de força da senha
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SEGURANÇA DA SENHA",
                                color = Color(0xFF8C8376),
                                style = MaterialTheme.typography.labelSmall,
                                letterSpacing = 1.sp,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "Forte",
                                color = Color(0xFFFFB82E),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            repeat(3) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Color(0xFFFFB82E))
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFF26262B))
                            )
                        }
                    }
                }

                // ─── Confirmar Senha ──────────────────────────────
                item {
                    FormField(
                        label = "CONFIRMAR SENHA",
                        badgeColor = Color(0xFF8C8376)
                    ) {
                        CustomTextInput(
                            value = confirmarSenha,
                            onValueChange = { confirmarSenha = it },
                            placeholder = "Repita sua senha",
                            leadingIcon = Icons.Outlined.Check,
                            visualTransformation = PasswordVisualTransformation()
                        )
                    }
                }

                // ─── Seus Gêneros Favoritos ───────────────────────
                item {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SEUS GÊNEROS FAVORITOS",
                                color = Color(0xFFC8A97E),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontSize = 11.sp
                            )
                            Text(
                                text = "Opcional",
                                color = Color(0xFF8C8376),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 11.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Personalize seu feed de estreias e recomendações cinematográficas.",
                            color = Color(0xFF9E988F),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            todosGeneros.forEach { genero ->
                                val selecionado = generosSelecionados.contains(genero)
                                GenreChip(
                                    label = genero,
                                    selected = selecionado,
                                    onToggle = {
                                        if (selecionado) {
                                            generosSelecionados.remove(genero)
                                        } else {
                                            generosSelecionados.add(genero)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }

                // ─── Termos e Condições ───────────────────────────
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { termosAceitos = !termosAceitos }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = termosAceitos,
                            onCheckedChange = { termosAceitos = it },
                            colors = CheckboxDefaults.colors(
                                checkedColor = Color(0xFFFFB82E),
                                checkmarkColor = Color(0xFF14120E),
                                uncheckedColor = Color(0xFF5E5A52)
                            )
                        )

                        Spacer(modifier = Modifier.width(4.dp))

                        val termsText = buildAnnotatedString {
                            append("Li e concordo com os ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFEDE7DE))) {
                                append("Termos de Uso")
                            }
                            append(" e com a ")
                            withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Color(0xFFEDE7DE))) {
                                append("Política de Privacidade")
                            }
                            append(" do Meu Box Favorito.")
                        }

                        Text(
                            text = termsText,
                            color = Color(0xFF9E988F),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                    }
                }

                // ─── Botão Concluir Cadastro ──────────────────────
                item {
                    Button(
                        onClick = onRegisterSuccess,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFFFB82E)
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "Concluir Cadastro",
                                color = Color(0xFF1A1209),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "→",
                                color = Color(0xFF1A1209),
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }

                // ─── Link Já tem conta? Entrar ────────────────────
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Já tem uma conta? ",
                            color = Color(0xFF9E988F),
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Entrar ›",
                            color = Color(0xFFEDE7DE),
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            fontSize = 13.sp,
                            modifier = Modifier.clickable { onNavigateToLogin() }
                        )
                    }
                }

                // ─── Footer: Criptografia de ponta a ponta ────────
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = null,
                            tint = Color(0xFF5E584F),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "CRIPTOGRAFIA DE PONTA A PONTA",
                            color = Color(0xFF5E584F),
                            style = MaterialTheme.typography.labelSmall,
                            letterSpacing = 1.2.sp,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RegisterTopBar(onBack: () -> Unit) {
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
            text = "Criar Conta",
            color = Color(0xFFEDE7DE),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )

        // Avatar ícone circular
        Box(
            modifier = Modifier
                .padding(end = 8.dp)
                .size(36.dp)
                .clip(CircleShape)
                .background(Color(0xFF2C2518))
                .border(1.dp, Color(0xFF4A3E26), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color(0xFFFFB82E),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun RegisterHeroCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF231B2D),
                        Color(0xFF151419)
                    )
                )
            )
            .border(
                width = 1.dp,
                color = Color(0xFF332A42),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(20.dp)
    ) {
        Column {
            // Badge OBSIDIAN CINEMA
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF2B2138)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "🎬", fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "OBSIDIAN CINEMA",
                        color = Color(0xFFDCC896),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Junte-se à Comunidade",
                color = Color(0xFFEDE7DE),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Crie suas boxes, compartilhe resenhas e debata cinema com curadoria de verdade.",
                color = Color(0xFFAFA9B8),
                style = MaterialTheme.typography.bodySmall,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun FormField(
    label: String,
    badge: String? = null,
    badgeColor: Color = Color(0xFF8C8376),
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = Color(0xFFC8A97E),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                fontSize = 11.sp
            )
            if (badge != null) {
                Text(
                    text = badge,
                    color = badgeColor,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        content()
    }
}

@Composable
private fun CustomTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector? = null,
    prefix: String? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardType: KeyboardType = KeyboardType.Text,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(Color(0xFF141316))
            .border(1.dp, Color(0xFF26252B), RoundedCornerShape(10.dp))
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (leadingIcon != null) {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = Color(0xFF7A7368),
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
        }

        if (prefix != null) {
            Text(
                text = prefix,
                color = Color(0xFF7A7368),
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.width(6.dp))
        }

        Box(modifier = Modifier.weight(1f)) {
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    color = Color(0xFF555048),
                    fontSize = 14.sp
                )
            }
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = visualTransformation,
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                cursorBrush = SolidColor(Color(0xFFFFB82E)),
                textStyle = TextStyle(
                    color = Color(0xFFEDE7DE),
                    fontSize = 14.sp
                )
            )
        }

        if (trailingIcon != null) {
            Spacer(modifier = Modifier.width(8.dp))
            trailingIcon()
        }
    }
}

@Composable
private fun GenreChip(
    label: String,
    selected: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier.clickable { onToggle() },
        shape = RoundedCornerShape(20.dp),
        color = if (selected) Color(0xFF5B22D4) else Color(0xFF1A1A1E),
        border = if (selected) null else androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2C2C32))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (selected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
            }
            Text(
                text = label,
                color = if (selected) Color.White else Color(0xFFC5BDB2),
                style = MaterialTheme.typography.labelMedium,
                fontSize = 12.sp,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
            )
        }
    }
}
