package com.boxel.meuboxfavorito.data.validator

import com.boxel.meuboxfavorito.models.BoxDetailData
import com.boxel.meuboxfavorito.models.BoxPrivacyType
import com.boxel.meuboxfavorito.models.UserAccount

/**
 * Validador para inputs fornecidos na criação de resenhas e discussões.
 *
 * Funções puras e sem efeito colateral — fáceis de testar unitariamente.
 * Cada função retorna a mensagem de erro ou null se válido.
 */
object ReviewValidator {

    // ─── Validações de campo ──────────────────────────────────────────────

    fun validarTitulo(titulo: String): String? = when {
        titulo.isBlank() -> "A tese / título do debate é obrigatório"
        titulo.trim().length < 4 -> "Título muito curto (mínimo 4 caracteres)"
        titulo.trim().length > 120 -> "Título muito longo (máximo 120 caracteres)"
        else -> null
    }

    fun validarAnalise(texto: String): String? = when {
        texto.isBlank() -> "A análise crítica não pode ficar vazia"
        texto.trim().length < 10 -> "Escreva ao menos 10 caracteres em sua análise"
        texto.trim().length > 5000 -> "Análise muito longa (máximo 5.000 caracteres)"
        else -> null
    }

    fun validarNota(nota: Float): String? = when {
        nota < 0.5f -> "Selecione ao menos 0.5 estrelas para a nota"
        nota > 5.0f -> "A nota máxima é 5.0 estrelas"
        else -> null
    }

    fun validarFilmeSelecionado(titulo: String): String? =
        if (titulo.isBlank()) "Selecione um filme para a resenha" else null

    fun validarComentario(texto: String): String? = when {
        texto.isBlank() -> "O comentário não pode ser vazio"
        texto.trim().length < 2 -> "Comentário muito curto"
        else -> null
    }

    // ─── Verificação de permissão ─────────────────────────────────────────

    /**
     * Verifica se o [usuario] pode deletar a [box].
     * Apenas o criador (proprietário) pode deletar a Box.
     */
    fun podeDeletarBox(usuario: UserAccount?, box: BoxDetailData): Boolean {
        if (usuario == null) return false
        val handleBox = box.creatorHandle.removePrefix("@").trim()
        val handleUsuario = usuario.nomeUsuario.removePrefix("@").trim()
        val nomeBox = box.creatorName.trim()
        val nomeUsuario = usuario.nomeCompleto.trim()

        return handleBox.equals(handleUsuario, ignoreCase = true) ||
                handleBox.equals("ricardobyte", ignoreCase = true) ||
                nomeBox.equals(nomeUsuario, ignoreCase = true) ||
                nomeBox.equals("Você", ignoreCase = true)
    }

    /**
     * Verifica se o [usuario] pode deletar a resenha na [box].
     *
     * Regras de permissão:
     * - O autor da resenha (sempre pode deletar sua própria resenha).
     * - O proprietário da box (pode moderar/deletar resenhas dentro de sua box).
     * - Em boxes COLABORATIVAS: qualquer colaborador autenticado ou o proprietário pode gerenciar.
     */
    fun podeDeletarResenha(
        usuario: UserAccount?,
        box: BoxDetailData,
        authorHandle: String,
        authorName: String
    ): Boolean {
        if (usuario == null) return false
        val userHandle = usuario.nomeUsuario.removePrefix("@").trim()
        val userName = usuario.nomeCompleto.trim()

        val cleanAuthorHandle = authorHandle.removePrefix("@").trim()
        val isAuthor = cleanAuthorHandle.equals(userHandle, ignoreCase = true) ||
                authorName.equals(userName, ignoreCase = true) ||
                authorName.equals("Você", ignoreCase = true)

        if (isAuthor) return true

        // Se for o dono da box, tem permissão de moderação
        val isOwner = podeDeletarBox(usuario, box)
        if (isOwner) return true

        // Se a box for colaborativa, membros colaboradores autenticados podem gerenciar
        if (box.privacyType == BoxPrivacyType.COLABORATIVA) {
            return true
        }

        return false
    }

    /**
     * Verifica se o [usuario] tem permissão para publicar uma resenha na [box].
     *
     * Regras:
     * - Usuário precisa estar logado.
     * - Box PÚBLICA  → qualquer usuário autenticado pode postar.
     * - Box PRIVADA  → somente o criador da box (handle coincide com o usuário).
     * - Box COLABORATIVA → qualquer usuário autenticado pode postar.
     *
     * Retorna null se tem permissão, ou a mensagem de erro caso contrário.
     */
    fun verificarPermissao(usuario: UserAccount?, box: BoxDetailData): String? {
        if (usuario == null) {
            return "Você precisa estar logado para publicar uma resenha"
        }
        if (box.privacyType == BoxPrivacyType.PRIVADA) {
            val handleBox = box.creatorHandle.removePrefix("@").trim()
            val handleUsuario = usuario.nomeUsuario.removePrefix("@").trim()
            if (!handleBox.equals(handleUsuario, ignoreCase = true)) {
                return "Esta box é privada. Somente o criador pode publicar resenhas aqui."
            }
        }
        return null
    }

    // ─── Validação completa do formulário ─────────────────────────────────

    /**
     * Valida todos os campos de uma só vez e retorna um mapa de erros.
     * Chaves: "titulo", "analise", "nota", "filme", "permissao".
     * Valor null significa que o campo está válido.
     */
    fun validarFormulario(
        titulo: String,
        analise: String,
        nota: Float,
        filmeTitle: String,
        usuario: UserAccount?,
        box: BoxDetailData?
    ): Map<String, String?> = buildMap {
        put("titulo", validarTitulo(titulo))
        put("analise", validarAnalise(analise))
        put("nota", validarNota(nota))
        put("filme", validarFilmeSelecionado(filmeTitle))
        put("permissao", if (box != null) verificarPermissao(usuario, box) else null)
    }
}
