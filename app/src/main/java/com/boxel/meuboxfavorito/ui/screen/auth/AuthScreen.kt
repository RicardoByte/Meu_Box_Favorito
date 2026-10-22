package com.boxel.meuboxfavorito.ui.screen.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AuthScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onExploreWithoutAccount: () -> Unit
) {
    var emailOuUsuario by remember { mutableStateOf("") }
    var senha by remember { mutableStateOf("") }
    var erroLogin by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0C0C0E),
                        Color(0xFF141318),
                        Color(0xFF0C0C0E)
                    )
                )
            )
            .imePadding()
    ) {
        // Brilho de fundo suave centralizado
        Box(
            modifier = Modifier
                .size(360.dp)
                .align(Alignment.Center)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            Color(0x18FFB82E),
                            Color.Transparent
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // ─── Logo com ícone cinematográfico ─────────────────
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(Color(0xFF1B1B1F))
                    .border(
                        width = 1.dp,
                        color = Color(0xFF2F2F38),
                        shape = RoundedCornerShape(18.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "🎬", fontSize = 28.sp)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ─── Título do App ───────────────────────────────────
            Text(
                text = "Meu Box",
                color = Color(0xFFF2ECE4),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Sua coleção cinematográfica premium.",
                color = Color(0xFF948D82),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // ─── Card de Formulário Principal ────────────────────
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF141416),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF222228))
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Botão Destaque: Criar nova conta
                    Button(
                        onClick = onNavigateToRegister,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFF7C880)
                        )
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.PersonAdd,
                                contentDescription = null,
                                tint = Color(0xFF1A1209),
                                modifier = Modifier.size(19.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Criar nova conta",
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

                    // Divisor elegante "OU ENTRAR"
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Color(0xFF26262D))
                        )
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 8.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1E1E24))
                                .padding(horizontal = 10.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "OU ENTRAR",
                                color = Color(0xFF908A80),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontSize = 10.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(Color(0xFF26262D))
                        )
                    }

                    // Campo: E-mail ou Usuário
                    Column {
                        Text(
                            text = "E-mail ou Usuário",
                            color = Color(0xFFC0B8AC),
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF101012))
                                .border(1.dp, Color(0xFF23232A), RoundedCornerShape(10.dp))
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Email,
                                contentDescription = null,
                                tint = Color(0xFF6E685E),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                if (emailOuUsuario.isEmpty()) {
                                    Text(
                                        text = "seu@email.com",
                                        color = Color(0xFF4A463F),
                                        fontSize = 14.sp
                                    )
                                }
                                BasicTextField(
                                    value = emailOuUsuario,
                                    onValueChange = {
                                        emailOuUsuario = it
                                        erroLogin = false
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    cursorBrush = SolidColor(Color(0xFFFFB82E)),
                                    textStyle = TextStyle(
                                        color = Color(0xFFEDE7DE),
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }
                    }

                    // Campo: Senha
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Senha",
                                color = Color(0xFFC0B8AC),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Esqueceu?",
                                color = Color(0xFFC8A97E),
                                style = MaterialTheme.typography.labelSmall,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable { /* Esqueceu a senha */ }
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF101012))
                                .border(1.dp, Color(0xFF23232A), RoundedCornerShape(10.dp))
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = Color(0xFF6E685E),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Box(modifier = Modifier.weight(1f)) {
                                if (senha.isEmpty()) {
                                    Text(
                                        text = "••••••••",
                                        color = Color(0xFF4A463F),
                                        fontSize = 14.sp
                                    )
                                }
                                BasicTextField(
                                    value = senha,
                                    onValueChange = {
                                        senha = it
                                        erroLogin = false
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true,
                                    visualTransformation = PasswordVisualTransformation(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                    cursorBrush = SolidColor(Color(0xFFFFB82E)),
                                    textStyle = TextStyle(
                                        color = Color(0xFFEDE7DE),
                                        fontSize = 14.sp
                                    )
                                )
                            }
                        }
                    }

                    AnimatedVisibility(
                        visible = erroLogin,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Text(
                            text = "Por favor, preencha seu e-mail e senha.",
                            color = Color(0xFFE05C5C),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    // Botão Entrar
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clickable {
                                if (emailOuUsuario.isBlank() || senha.isBlank()) {
                                    erroLogin = true
                                } else {
                                    onLoginSuccess()
                                }
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF25252B),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF35353E))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Entrar",
                                color = Color(0xFFEDE7DE),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "→",
                                color = Color(0xFFEDE7DE),
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }

        // ─── Explorar sem conta (Rodapé Flutuante) ────────────
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp)
                .clickable { onExploreWithoutAccount() },
            shape = RoundedCornerShape(50),
            color = Color(0xFF17171B),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF282830))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Explorar sem conta",
                    color = Color(0xFF9E988F),
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.Outlined.Explore,
                    contentDescription = null,
                    tint = Color(0xFF9E988F),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
