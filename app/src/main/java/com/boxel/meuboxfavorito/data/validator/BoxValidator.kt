package com.boxel.meuboxfavorito.data.validator

/**
 * Validador para inputs fornecidos na criação de novas coleções/Boxes.
 */
object BoxValidator {

    fun validarTitulo(titulo: String): String? = when {
        titulo.isBlank() -> "O título da Box é obrigatório"
        titulo.trim().length < 3 -> "O título deve ter ao menos 3 caracteres"
        titulo.trim().length > 80 -> "O título não pode exceder 80 caracteres"
        else -> null
    }

    fun validarDescricao(descricao: String): String? = when {
        descricao.length > 500 -> "A descrição não pode exceder 500 caracteres"
        else -> null
    }
}
