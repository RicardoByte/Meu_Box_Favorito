package com.boxel.meuboxfavorito.data.validator

import android.util.Patterns
import com.boxel.meuboxfavorito.models.ForcaSenha
import com.boxel.meuboxfavorito.models.RegisterFormState

/**
 * Objeto contendo todas as regras de validação do formulário de cadastro.
 *
 * Funções puras e sem efeito colateral — fáceis de testar unitariamente.
 * Cada função retorna a mensagem de erro ou null se válido.
 */
object RegisterValidator {

    // ─── Regras de campo ─────────────────────────────────────────────────

    fun validarNomeCompleto(valor: String): String? = when {
        valor.isBlank() -> "Nome completo é obrigatório"
        valor.trim().length < 3 -> "Nome muito curto (mínimo 3 caracteres)"
        valor.trim().split(" ").size < 2 -> "Informe ao menos nome e sobrenome"
        !valor.all { it.isLetter() || it.isWhitespace() } -> "Apenas letras são permitidas"
        else -> null
    }

    fun validarNomeUsuario(valor: String): String? = when {
        valor.isBlank() -> "Nome de usuário é obrigatório"
        valor.length < 3 -> "Mínimo de 3 caracteres"
        valor.length > 30 -> "Máximo de 30 caracteres"
        !valor.matches(Regex("^[a-zA-Z0-9_.]+$")) ->
            "Use apenas letras, números, _ ou ."
        valor.startsWith(".") || valor.startsWith("_") ->
            "Não pode começar com . ou _"
        else -> null
    }

    fun validarEmail(valor: String): String? = when {
        valor.isBlank() -> "E-mail é obrigatório"
        !Patterns.EMAIL_ADDRESS.matcher(valor).matches() -> "E-mail inválido"
        else -> null
    }

    fun validarSenha(valor: String): String? = when {
        valor.isBlank() -> "Senha é obrigatória"
        valor.length < 8 -> "Mínimo de 8 caracteres"
        !valor.any { it.isDigit() } -> "Inclua ao menos um número"
        !valor.any { it.isUpperCase() } -> "Inclua ao menos uma letra maiúscula"
        else -> null
    }

    fun validarConfirmarSenha(senha: String, confirmacao: String): String? = when {
        confirmacao.isBlank() -> "Confirme sua senha"
        confirmacao != senha -> "As senhas não coincidem"
        else -> null
    }

    fun validarTermos(aceito: Boolean): String? =
        if (!aceito) "Aceite os Termos de Uso para continuar" else null

    // ─── Força da senha ──────────────────────────────────────────────────

    fun calcularForcaSenha(senha: String): ForcaSenha {
        if (senha.isEmpty()) return ForcaSenha.VAZIA

        var pontos = 0
        if (senha.length >= 8) pontos++
        if (senha.length >= 12) pontos++
        if (senha.any { it.isUpperCase() } && senha.any { it.isLowerCase() }) pontos++
        if (senha.any { it.isDigit() }) pontos++
        if (senha.any { !it.isLetterOrDigit() }) pontos++

        return when {
            pontos <= 1 -> ForcaSenha.FRACA
            pontos == 2 -> ForcaSenha.MEDIA
            pontos == 3 -> ForcaSenha.FORTE
            else -> ForcaSenha.MUITO_FORTE
        }
    }

    // ─── Validação completa do formulário ────────────────────────────────

    fun validarFormulario(state: RegisterFormState): RegisterFormState {
        return state.copy(
            nomeCompletoErro = validarNomeCompleto(state.nomeCompleto),
            nomeUsuarioErro = validarNomeUsuario(state.nomeUsuario),
            emailErro = validarEmail(state.email),
            senhaErro = validarSenha(state.senha),
            confirmarSenhaErro = validarConfirmarSenha(state.senha, state.confirmarSenha),
            termosErro = validarTermos(state.termosAceitos),
            submetido = true
        )
    }
}
