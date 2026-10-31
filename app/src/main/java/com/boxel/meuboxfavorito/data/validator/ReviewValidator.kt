package com.boxel.meuboxfavorito.data.validator

/**
 * Validador para inputs fornecidos na criação de resenhas e discussões.
 */
object ReviewValidator {

    fun validarTitulo(titulo: String): String? = when {
        titulo.isBlank() -> "A tese / título do debate é obrigatório"
        titulo.trim().length < 4 -> "Título muito curto (mínimo 4 caracteres)"
        titulo.trim().length > 120 -> "Título muito longo (máximo 120 caracteres)"
        else -> null
    }

    fun validarAnalise(texto: String): String? = when {
        texto.isBlank() -> "A análise crítica não pode ficar vazia"
        texto.trim().length < 10 -> "Escreva ao menos 10 caracteres em sua análise"
        else -> null
    }

    fun validarNota(nota: Float): String? = when {
        nota < 0.5f || nota > 5.0f -> "A nota deve estar entre 0.5 e 5.0 estrelas"
        else -> null
    }

    fun validarComentario(texto: String): String? = when {
        texto.isBlank() -> "O comentário não pode ser vazio"
        texto.trim().length < 2 -> "Comentário muito curto"
        else -> null
    }
}
