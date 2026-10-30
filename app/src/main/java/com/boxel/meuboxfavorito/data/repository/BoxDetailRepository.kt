package com.boxel.meuboxfavorito.data.repository

import com.boxel.meuboxfavorito.data.remote.Moviefmt

enum class BoxPrivacyType(val label: String) {
    PUBLICA("Pública"),
    PRIVADA("Privada"),
    COLABORATIVA("Colaborativa")
}

enum class TagStyle {
    HIGHLIGHT,   // Purple (DESTAQUE)
    TOP,         // Teal (Top Resenha)
    VIRAL,       // Orange / Red (VIRAL)
    OPEN,        // Slate (Discussão Aberta)
    PRIVATE,     // Amber / Gold (NOTA PRIVADA / DIÁRIO)
    COLLABORATOR // Blue / Indigo (COLABORADOR)
}

data class RespostaComunidade(
    val id: Long,
    val autor: String,
    val tempoRelativo: String,
    val conteudo: String,
    val curtidas: Int,
    val avatarUrl: String? = null,
    val curtidoPorMim: Boolean = false
)

data class BoxReviewItem(
    val id: Int,
    val authorName: String,
    val authorHandle: String,
    val authorAvatar: String? = null,
    val timeAgo: String,
    val tagText: String,
    val tagStyle: TagStyle,
    val movieTitle: String,
    val movieInfo: String,
    val moviePosterUrl: String? = null,
    val rating: Float,
    val title: String,
    val body: String,
    val hashtags: List<String> = emptyList(),
    val likesCount: Int,
    val commentsCount: Int,
    val isLiked: Boolean = false,
    val replies: List<RespostaComunidade> = emptyList()
)

data class BoxDetailData(
    val id: Int,
    val title: String,
    val creatorName: String,
    val creatorHandle: String,
    val creatorAvatar: String? = null,
    val badgeCuradoria: String,
    val updatedTime: String,
    val description: String,
    val filmesCount: Int,
    val viewsCount: String,
    val savesCount: String,
    val isSaved: Boolean = false,
    val privacyType: BoxPrivacyType,
    val coverUrl: String? = null,
    val filmes: List<Moviefmt> = emptyList(),
    val reviews: List<BoxReviewItem> = emptyList()
)

object BoxDetailRepository {

    private val boxes = mutableMapOf<Int, BoxDetailData>()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        // ─────────────────────────────────────────────────────────────
        // FILMES COM POSTERS REAIS TMDB
        // ─────────────────────────────────────────────────────────────

        // Box 1: Comédia Fanfarrona
        val filmesComedia = listOf(
            Moviefmt(
                id = 11007,
                title = "Doze é Demais",
                poster_path = "/8TytUmOaJbI3O7pedgGErvdcVOV.jpg",
                release_date = "2003",
                vote_average = 6.3
            ),
            Moviefmt(
                id = 7518,
                title = "Os Sem-Floresta",
                poster_path = "/cNPhs8Gkm1SJ4vuZDnytZnj1EsY.jpg",
                release_date = "2006",
                vote_average = 6.6
            ),
            Moviefmt(
                id = 12153,
                title = "As Branquelas",
                poster_path = "/aJZOcorpgloDLkPP6ED0t9sXjNu.jpg",
                release_date = "2004",
                vote_average = 7.0
            ),
            Moviefmt(
                id = 8363,
                title = "Superbad: É Hoje",
                poster_path = "/rABcQMa8m67EJslknSlMfE3BP8L.jpg",
                release_date = "2007",
                vote_average = 7.3
            ),
            Moviefmt(
                id = 1273221,
                title = "Todo Mundo em Pânico",
                poster_path = "/y9yJd2qIIwhZcllHeKHsz5eRvNr.jpg",
                release_date = "2000",
                vote_average = 6.3
            ),
            Moviefmt(
                id = 18785,
                title = "Se Beber, Não Case!",
                poster_path = "/m0tQyMdp3fy5ooUOQkJMd1fQKBJ.jpg",
                release_date = "2009",
                vote_average = 7.3
            )
        )

        // Box 2: Sci-Fi Nerd
        val filmesSciFi = listOf(
            Moviefmt(
                id = 335984,
                title = "Blade Runner 2049",
                poster_path = "/49pANIZXRAdHUiWjjBv4vxPeqRC.jpg",
                release_date = "2017",
                vote_average = 7.6
            ),
            Moviefmt(
                id = 603,
                title = "Matrix",
                poster_path = "/lDqMDI3xpbB9UQRyeXfei0MXhqb.jpg",
                release_date = "1999",
                vote_average = 8.3
            ),
            Moviefmt(
                id = 62,
                title = "2001: Uma Odisséia no Espaço",
                poster_path = "/pVCmLuATJ0lQs4Vx1zUJUN0if2A.jpg",
                release_date = "1968",
                vote_average = 8.0
            ),
            Moviefmt(
                id = 693134,
                title = "Duna: Parte Dois",
                poster_path = "/VMy4UGsI2u3f4fALGeCqCdsQBb.jpg",
                release_date = "2024",
                vote_average = 8.1
            )
        )

        // Box 3: Insta Cult / Aesthetic
        val filmesInsta = listOf(
            Moviefmt(
                id = 157336,
                title = "Interestelar",
                poster_path = "/tR1XVa5bxgdh2bRw2u0DzrgkO2l.jpg",
                release_date = "2014",
                vote_average = 8.5
            ),
            Moviefmt(
                id = 64690,
                title = "Drive",
                poster_path = "/602vevIURmpDfzbnv5Ubi6wIkQm.jpg",
                release_date = "2011",
                vote_average = 7.6
            ),
            Moviefmt(
                id = 680,
                title = "Pulp Fiction: Tempo de Violência",
                poster_path = "/tptjnB2LDbuUWya9Cx5sQtv5hqb.jpg",
                release_date = "1994",
                vote_average = 8.5
            ),
            Moviefmt(
                id = 550,
                title = "Clube da Luta",
                poster_path = "/mCICnh7QBH0gzYaTQChBDDVIKdm.jpg",
                release_date = "1999",
                vote_average = 8.4
            )
        )

        // Box 4: Terror Psicológico & A24
        val filmesTerror = listOf(
            Moviefmt(
                id = 493922,
                title = "Hereditário",
                poster_path = "/x9tUYQj6WrdVwoKimSdoMzkDABS.jpg",
                release_date = "2018",
                vote_average = 7.3
            ),
            Moviefmt(
                id = 530385,
                title = "Midsommar: O Mal Não Espera a Noite",
                poster_path = "/hR4dXPlWq5Nekwjqbp3gFGeiiZS.jpg",
                release_date = "2019",
                vote_average = 7.1
            ),
            Moviefmt(
                id = 310131,
                title = "A Bruxa",
                poster_path = "/opuDnG7MX2eKb4s0r1a1GVJmUXE.jpg",
                release_date = "2016",
                vote_average = 7.0
            ),
            Moviefmt(
                id = 503919,
                title = "O Farol",
                poster_path = "/zdowK9GrOxK0YddhXuMPyxnTA02.jpg",
                release_date = "2019",
                vote_average = 7.5
            )
        )

        // Box 5: Animações que Tocam a Alma
        val filmesAnimacao = listOf(
            Moviefmt(
                id = 129,
                title = "A Viagem de Chihiro",
                poster_path = "/hhoKhsyJ3hFaxEm5pMdZRiTu2lJ.jpg",
                release_date = "2001",
                vote_average = 8.5
            ),
            Moviefmt(
                id = 354912,
                title = "Viva: A Vida é Uma Festa",
                poster_path = "/6oNm06TPz2vGiPc2I52oXW3JwPS.jpg",
                release_date = "2017",
                vote_average = 8.2
            ),
            Moviefmt(
                id = 14160,
                title = "Up: Altas Aventuras",
                poster_path = "/oo5gvGDIiPwbXc3R0snZIuOc517.jpg",
                release_date = "2009",
                vote_average = 8.0
            ),
            Moviefmt(
                id = 372058,
                title = "Your Name.",
                poster_path = "/bMOKAjTU1TNUjRSF7icldbii06u.jpg",
                release_date = "2016",
                vote_average = 8.5
            )
        )

        // ─────────────────────────────────────────────────────────────
        // 1. BOX PÚBLICA (Comédia Fanfarrona & Nostalgia)
        // ─────────────────────────────────────────────────────────────
        val boxPublica = BoxDetailData(
            id = 1,
            title = "Comédia Fanfarrona & Nostalgia",
            creatorName = "Lucas Mendes",
            creatorHandle = "@lucas_critico",
            creatorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80",
            badgeCuradoria = "CURADORIA OFICIAL",
            updatedTime = "Atualizado há 2 dias",
            description = "Seleção essencial das maiores bizarrices cômicas dos anos 2000 que moldaram nosso humor duvidoso e merecem teses sociológicas profundas.",
            filmesCount = 8,
            viewsCount = "3.4k visualizações",
            savesCount = "412 salvos",
            isSaved = false,
            privacyType = BoxPrivacyType.PUBLICA,
            coverUrl = "https://image.tmdb.org/t/p/w780/8TytUmOaJbI3O7pedgGErvdcVOV.jpg",
            filmes = filmesComedia,
            reviews = listOf(
                BoxReviewItem(
                    id = 101,
                    authorName = "Lucas Mendes",
                    authorHandle = "@lucas_critico",
                    authorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80",
                    timeAgo = "8h atrás",
                    tagText = "DESTAQUE",
                    tagStyle = TagStyle.HIGHLIGHT,
                    movieTitle = "Doze é Demais",
                    movieInfo = "2003 • Comédia / Família",
                    moviePosterUrl = "https://image.tmdb.org/t/p/w500/8TytUmOaJbI3O7pedgGErvdcVOV.jpg",
                    rating = 4.5f,
                    title = "Porque ter filho em 2026? Imagina 12?",
                    body = "“O verdadeiro terror contemporâneo não é Hereditário, é gerenciar doze herdeiros com uma panela de macarrão e um cão caótico. Steve Martin no ápice do desespero cômico suburbano.”",
                    hashtags = emptyList(),
                    likesCount = 482,
                    commentsCount = 128,
                    replies = listOf(
                        RespostaComunidade(
                            id = 1L,
                            autor = "Sarah Connor",
                            tempoRelativo = "Há 45 min",
                            conteudo = "Steve Martin carregou o gênero de comédia familiar dos anos 2000 nas costas! A dinâmica com a Bonnie Hunt era perfeita.",
                            curtidas = 42,
                            avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&auto=format&fit=crop&q=80"
                        ),
                        RespostaComunidade(
                            id = 2L,
                            autor = "Rick D.",
                            tempoRelativo = "Há 20 min",
                            conteudo = "A cena da cobra no quarto e da carne moida caindo é cinema puro kkkkkk Nunca mais fizeram comédia familiar assim.",
                            curtidas = 12,
                            avatarUrl = "https://images.unsplash.com/photo-1570295999919-56ceb5ecca61?w=150&auto=format&fit=crop&q=80"
                        )
                    )
                ),
                BoxReviewItem(
                    id = 102,
                    authorName = "Beatriz F.",
                    authorHandle = "@cinemanfaca",
                    authorAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150&auto=format&fit=crop&q=80",
                    timeAgo = "3 dias atrás",
                    tagText = "Top Resenha",
                    tagStyle = TagStyle.TOP,
                    movieTitle = "Os Sem-Floresta (Over the Hedge)",
                    movieInfo = "2006 • Animação / Sátira",
                    moviePosterUrl = "https://image.tmdb.org/t/p/w500/cNPhs8Gkm1SJ4vuZDnytZnj1EsY.jpg",
                    rating = 5.0f,
                    title = "Porque o esquilo de Sem Floresta é tão maneiro?",
                    body = "“Hammy em câmera lenta sob efeito do energético com cafeína pura quebrou as leis da física clássica antes mesmo do Mercúrio em Dias de um Futuro Esquecido. Cinema absoluto.”",
                    hashtags = listOf("#AnimaçãoCult", "#Anos2000", "#SteveCarell"),
                    likesCount = 315,
                    commentsCount = 94,
                    replies = listOf(
                        RespostaComunidade(
                            id = 3L,
                            autor = "Pedro N.",
                            tempoRelativo = "Há 1 dia",
                            conteudo = "Essa cena do Hammy com a cafeína alterando o espaço-tempo é a melhor representação de velocidade já feita no cinema de animação!",
                            curtidas = 28,
                            avatarUrl = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80"
                        )
                    )
                ),
                BoxReviewItem(
                    id = 103,
                    authorName = "Pedro N.",
                    authorHandle = "@pedro_cult",
                    authorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80",
                    timeAgo = "5 dias atrás",
                    tagText = "VIRAL",
                    tagStyle = TagStyle.VIRAL,
                    movieTitle = "As Branquelas (White Chicks)",
                    movieInfo = "2004 • Comédia Cult",
                    moviePosterUrl = "https://image.tmdb.org/t/p/w500/aJZOcorpgloDLkPP6ED0t9sXjNu.jpg",
                    rating = 5.0f,
                    title = "As Branquelas e a cena do restaurante: Poesia dramática moderna?",
                    body = "“Terry Crews cantando A Thousand Miles no carro transcedeu o cinema comercial e se tornou patrimônio imaterial da cultura pop mundial.”",
                    hashtags = emptyList(),
                    likesCount = 898,
                    commentsCount = 264,
                    replies = listOf(
                        RespostaComunidade(
                            id = 4L,
                            autor = "Julia S.",
                            tempoRelativo = "Há 2 dias",
                            conteudo = "'Como você soube? Foi adivinhação?' Terry Crews entregou tudo nessa cena! Clássico absoluto que todo brasileiro ama.",
                            curtidas = 65,
                            avatarUrl = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&auto=format&fit=crop&q=80"
                        )
                    )
                ),
                BoxReviewItem(
                    id = 104,
                    authorName = "Mateus Castro",
                    authorHandle = "@castro_cinema",
                    authorAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&auto=format&fit=crop&q=80",
                    timeAgo = "1 semana atrás",
                    tagText = "Discussão Aberta",
                    tagStyle = TagStyle.OPEN,
                    movieTitle = "Superbad: É Hoje",
                    movieInfo = "2007 • Comédia / Coming-of-age",
                    moviePosterUrl = "https://image.tmdb.org/t/p/w500/rABcQMa8m67EJslknSlMfE3BP8L.jpg",
                    rating = 4.9f,
                    title = "Superbad: McLovin é o anti-herói definitivo do século XXI?",
                    body = "“Nenhum documento de identidade falso na história da humanidade gerou tanta catarse coletiva e tanta lealdade policial bizarra em plena noite de Las Vegas suburbana.”",
                    hashtags = emptyList(),
                    likesCount = 649,
                    commentsCount = 107,
                    replies = listOf(
                        RespostaComunidade(
                            id = 5L,
                            autor = "Gabriel F.",
                            tempoRelativo = "Há 4 dias",
                            conteudo = "Um dos roteiros de comédia mais bem estruturados dos anos 2000. Seth Rogen e Evan Goldberg escreveram ouro puro.",
                            curtidas = 34,
                            avatarUrl = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&auto=format&fit=crop&q=80"
                        )
                    )
                )
            )
        )

        // ─────────────────────────────────────────────────────────────
        // 2. BOX PRIVADA (Filmes do nerd - Resenhas apenas dele)
        // ─────────────────────────────────────────────────────────────
        val boxPrivada = BoxDetailData(
            id = 2,
            title = "Filmes do nerd",
            creatorName = "Você",
            creatorHandle = "@ricardobyte",
            creatorAvatar = null,
            badgeCuradoria = "BOX PRIVADA 🔒",
            updatedTime = "Atualizado hoje",
            description = "Meu diário particular de resenhas e reflexões pessoais sobre sci-fi distópico, cinema de nicho e cyberpunk. Apenas visível por mim.",
            filmesCount = 11,
            viewsCount = "128 visualizações",
            savesCount = "1 salvo",
            isSaved = true,
            privacyType = BoxPrivacyType.PRIVADA,
            coverUrl = "https://image.tmdb.org/t/p/w780/49pANIZXRAdHUiWjjBv4vxPeqRC.jpg",
            filmes = filmesSciFi,
            reviews = listOf(
                BoxReviewItem(
                    id = 201,
                    authorName = "Você",
                    authorHandle = "@ricardobyte",
                    authorAvatar = null,
                    timeAgo = "Há 2 horas",
                    tagText = "NOTA PRIVADA",
                    tagStyle = TagStyle.PRIVATE,
                    movieTitle = "Blade Runner 2049",
                    movieInfo = "2017 • Ficção Científica / Cyberpunk",
                    moviePosterUrl = "https://image.tmdb.org/t/p/w500/49pANIZXRAdHUiWjjBv4vxPeqRC.jpg",
                    rating = 5.0f,
                    title = "Porque o Ryan Gosling é tão bom em Blade Runner?",
                    body = "“Ryan Gosling em Blade Runner 2049 é a prova de que um homem só precisa de uma jaqueta maneira, silêncio absoluto e luzes neon para entregar a melhor crise existencial cibernética da história do cinema. Ninguém encara o vazio e chora na chuva com tanto estilo.”",
                    hashtags = listOf("#Cyberpunk", "#Villeneuve", "#Diário"),
                    likesCount = 1,
                    commentsCount = 2,
                    replies = listOf(
                        RespostaComunidade(
                            id = 2011L,
                            autor = "Anotação Pessoal",
                            tempoRelativo = "Há 1 hora",
                            conteudo = "Lembrar de rever a cena com o holograma gigante da Joi. O simbolismo da busca por conexão artificial ainda é impecável.",
                            curtidas = 1
                        )
                    )
                ),
                BoxReviewItem(
                    id = 202,
                    authorName = "Você",
                    authorHandle = "@ricardobyte",
                    authorAvatar = null,
                    timeAgo = "Há 3 dias",
                    tagText = "DIÁRIO",
                    tagStyle = TagStyle.PRIVATE,
                    movieTitle = "Matrix",
                    movieInfo = "1999 • Ação / Ficção Científica",
                    moviePosterUrl = "https://image.tmdb.org/t/p/w500/lDqMDI3xpbB9UQRyeXfei0MXhqb.jpg",
                    rating = 5.0f,
                    title = "A alegoria da caverna e a pílula vermelha hoje",
                    body = "“Reassistindo Matrix em 2026: a metáfora do despertar nunca foi tão urgente na era de feeds e inteligência artificial onipresente. A fotografia verde e o figurino de couro continuam atemporais.”",
                    hashtags = listOf("#Filosofia", "#Wachowskis"),
                    likesCount = 1,
                    commentsCount = 1,
                    replies = listOf(
                        RespostaComunidade(
                            id = 2021L,
                            autor = "Anotação Pessoal",
                            tempoRelativo = "Há 2 dias",
                            conteudo = "Comparar a trilha sonora industrial com o ritmo acelerado dos combates em Hong Kong.",
                            curtidas = 1
                        )
                    )
                ),
                BoxReviewItem(
                    id = 203,
                    authorName = "Você",
                    authorHandle = "@ricardobyte",
                    authorAvatar = null,
                    timeAgo = "Há 1 semana",
                    tagText = "RASCUNHO",
                    tagStyle = TagStyle.OPEN,
                    movieTitle = "2001: Uma Odisséia no Espaço",
                    movieInfo = "1968 • Sci-Fi / Mistério",
                    moviePosterUrl = "https://image.tmdb.org/t/p/w500/pVCmLuATJ0lQs4Vx1zUJUN0if2A.jpg",
                    rating = 4.8f,
                    title = "O silêncio do monólito e a evolução da consciência",
                    body = "“Kubrick transformou a lentidão em pura tensão cósmica. A valsa de Strauss no espaço é o maior contraste da história do cinema, e HAL 9000 segue sendo a IA mais assustadora já concebida.”",
                    hashtags = listOf("#Kubrick", "#Clássico"),
                    likesCount = 1,
                    commentsCount = 0,
                    replies = emptyList()
                )
            )
        )

        // ─────────────────────────────────────────────────────────────
        // 3. BOX COLABORATIVA (Filmes pra postar no insta - Resenhas de outros membros)
        // ─────────────────────────────────────────────────────────────
        val boxColaborativa = BoxDetailData(
            id = 3,
            title = "Filmes pra postar no insta",
            creatorName = "Coletivo Cinéfilos",
            creatorHandle = "@cine_colab",
            creatorAvatar = "https://images.unsplash.com/photo-1522075469751-3a6694fb2f61?w=150&auto=format&fit=crop&q=80",
            badgeCuradoria = "COLABORATIVA 👥",
            updatedTime = "Atualizado há 1 dia",
            description = "Box colaborativa mantida pela comunidade: cinéfilos e criadores compartilham seus achados estéticos, resenhas coletivas e enquadramentos inesquecíveis.",
            filmesCount = 16,
            viewsCount = "5.8k visualizações",
            savesCount = "930 salvos",
            isSaved = false,
            privacyType = BoxPrivacyType.COLABORATIVA,
            coverUrl = "https://image.tmdb.org/t/p/w780/tR1XVa5bxgdh2bRw2u0DzrgkO2l.jpg",
            filmes = filmesInsta,
            reviews = listOf(
                BoxReviewItem(
                    id = 301,
                    authorName = "Sarah Connor",
                    authorHandle = "@sarah_cinema",
                    authorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&auto=format&fit=crop&q=80",
                    timeAgo = "Há 5 horas",
                    tagText = "COLABORADORA",
                    tagStyle = TagStyle.COLLABORATOR,
                    movieTitle = "Interestelar",
                    movieInfo = "2014 • Ficção Científica / Drama",
                    moviePosterUrl = "https://image.tmdb.org/t/p/w500/tR1XVa5bxgdh2bRw2u0DzrgkO2l.jpg",
                    rating = 5.0f,
                    title = "A relatividade do amor e a física de Christopher Nolan",
                    body = "“A cena das ondas gigantes no planeta de Miller me dá arrepios até hoje. Hans Zimmer na trilha eleva a ficção científica a outro nível emocional, tornando a saudade uma constante cosmológica.”",
                    hashtags = listOf("#SciFiAesthetic", "#HansZimmer", "#Nolan"),
                    likesCount = 520,
                    commentsCount = 114,
                    replies = listOf(
                        RespostaComunidade(
                            id = 3011L,
                            autor = "Lucas Mendes",
                            tempoRelativo = "Há 3 horas",
                            conteudo = "Essa cena na água rasa com o som do tique-taque marcando as horas na terra é genialidade pura de edição.",
                            curtidas = 48
                        )
                    )
                ),
                BoxReviewItem(
                    id = 302,
                    authorName = "Alex Mercer",
                    authorHandle = "@alex_reviews",
                    authorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&auto=format&fit=crop&q=80",
                    timeAgo = "Há 1 dia",
                    tagText = "MEMBRO ATIVO",
                    tagStyle = TagStyle.TOP,
                    movieTitle = "Drive",
                    movieInfo = "2011 • Neo-Noir / Suspense",
                    moviePosterUrl = "https://image.tmdb.org/t/p/w500/602vevIURmpDfzbnv5Ubi6wIkQm.jpg",
                    rating = 4.8f,
                    title = "Synthwave, silêncio e o anti-herói solitário",
                    body = "“Nicolas Winding Refn dirigiu o filme mais aesthetic dos anos 2010. Cada enquadramento noturno iluminado a neon e jaqueta de escorpião é uma obra de arte pronta pro feed.”",
                    hashtags = listOf("#NeonNoir", "#Synthwave", "#Cult"),
                    likesCount = 410,
                    commentsCount = 88,
                    replies = listOf(
                        RespostaComunidade(
                            id = 3021L,
                            autor = "Clara Dias",
                            tempoRelativo = "Há 18 horas",
                            conteudo = "A abertura com 'Nightcall' do Kavinsky estabeleceu todo o mood sonoro da década seguinte.",
                            curtidas = 29
                        )
                    )
                ),
                BoxReviewItem(
                    id = 303,
                    authorName = "Clara Dias",
                    authorHandle = "@claracine",
                    authorAvatar = "https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150&auto=format&fit=crop&q=80",
                    timeAgo = "Há 3 dias",
                    tagText = "COLABORADORA",
                    tagStyle = TagStyle.COLLABORATOR,
                    movieTitle = "Pulp Fiction",
                    movieInfo = "1994 • Crime / Comédia Ácida",
                    moviePosterUrl = "https://image.tmdb.org/t/p/w500/tptjnB2LDbuUWya9Cx5sQtv5hqb.jpg",
                    rating = 5.0f,
                    title = "Diálogos triviais que transformam o crime em literatura pop",
                    body = "“A conversa sobre o Royale with Cheese e a dança no Jack Rabbit Slim's provam que Tarantino entende a cultura pop e o ritmo da conversa humana como ninguém.”",
                    hashtags = listOf("#Tarantino", "#Anos90", "#Cineclube"),
                    likesCount = 672,
                    commentsCount = 142,
                    replies = listOf(
                        RespostaComunidade(
                            id = 3031L,
                            autor = "Ricardo B.",
                            tempoRelativo = "Há 2 dias",
                            conteudo = "A montagem não-cronológica foi uma revolução completa para a narrativa cinematográfica nos anos 90.",
                            curtidas = 41
                        )
                    )
                ),
                BoxReviewItem(
                    id = 304,
                    authorName = "Ricardo B.",
                    authorHandle = "@ricardobyte",
                    authorAvatar = null,
                    timeAgo = "Há 4 dias",
                    tagText = "SUA RESENHA",
                    tagStyle = TagStyle.HIGHLIGHT,
                    movieTitle = "Clube da Luta",
                    movieInfo = "1999 • Drama / Suspense Psicológico",
                    moviePosterUrl = "https://image.tmdb.org/t/p/w500/mCICnh7QBH0gzYaTQChBDDVIKdm.jpg",
                    rating = 4.9f,
                    title = "O colapso da identidade moderna na era do consumismo",
                    body = "“David Fincher capturou a crise existencial de fim de século com uma montagem frenética e direção de arte visceral. O plot twist final ainda é insuperável.”",
                    hashtags = listOf("#Fincher", "#Mindfuck", "#Colab"),
                    likesCount = 731,
                    commentsCount = 195,
                    replies = emptyList()
                )
            )
        )

        // ─────────────────────────────────────────────────────────────
        // 4. NOVA BOX PÚBLICA (Terror Psicológico & A24)
        // ─────────────────────────────────────────────────────────────
        val boxTerror = BoxDetailData(
            id = 4,
            title = "Terror Psicológico & A24",
            creatorName = "Guilherme S.",
            creatorHandle = "@gui_terror",
            creatorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80",
            badgeCuradoria = "CURADORIA OFICIAL",
            updatedTime = "Atualizado há 3 dias",
            description = "Uma jornada perturbadora pelas obras que redefiniram o horror moderno: traumas familiares, folclore sinistro e atmosferas sufocantes que não te deixam dormir.",
            filmesCount = 6,
            viewsCount = "4.2k visualizações",
            savesCount = "680 salvos",
            isSaved = false,
            privacyType = BoxPrivacyType.PUBLICA,
            coverUrl = "https://image.tmdb.org/t/p/w780/x9tUYQj6WrdVwoKimSdoMzkDABS.jpg",
            filmes = filmesTerror,
            reviews = listOf(
                BoxReviewItem(
                    id = 401,
                    authorName = "Guilherme S.",
                    authorHandle = "@gui_terror",
                    authorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80",
                    timeAgo = "1 dia atrás",
                    tagText = "DESTAQUE",
                    tagStyle = TagStyle.HIGHLIGHT,
                    movieTitle = "Hereditário",
                    movieInfo = "2018 • Terror / Drama Familiar",
                    moviePosterUrl = "https://image.tmdb.org/t/p/w500/x9tUYQj6WrdVwoKimSdoMzkDABS.jpg",
                    rating = 5.0f,
                    title = "O luto como horror existencial e a cabeça no poste",
                    body = "“Ari Aster não fez apenas um filme de sustos, construiu uma autópsia dolorosa sobre a decomposição de uma família. A atuação de Toni Collette deveria ter levado todos os prêmios daquele ano.”",
                    hashtags = listOf("#A24", "#TerrorModerno", "#ToniCollette"),
                    likesCount = 612,
                    commentsCount = 178,
                    replies = listOf(
                        RespostaComunidade(
                            id = 4011L,
                            autor = "Mariana R.",
                            tempoRelativo = "Há 12 horas",
                            conteudo = "O som do estalo com a língua da Charlie me dá calafrios até hoje. Uma aula de construção de pavor.",
                            curtidas = 54
                        )
                    )
                ),
                BoxReviewItem(
                    id = 402,
                    authorName = "Mariana R.",
                    authorHandle = "@cine_marianar",
                    authorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80",
                    timeAgo = "2 dias atrás",
                    tagText = "Top Resenha",
                    tagStyle = TagStyle.TOP,
                    movieTitle = "Midsommar: O Mal Não Espera a Noite",
                    movieInfo = "2019 • Terror Folclórico / Drama",
                    moviePosterUrl = "https://image.tmdb.org/t/p/w500/hR4dXPlWq5Nekwjqbp3gFGeiiZS.jpg",
                    rating = 4.8f,
                    title = "O terror solar e a catarse da dor em plena luz do dia",
                    body = "“Fazer um filme de terror em plena luz do sol da Escandinávia e ainda assim causar náusea psicológica é o maior feito estético de Aster. Florence Pugh é o coração pulsante da obra.”",
                    hashtags = listOf("#FolkHorror", "#FlorencePugh"),
                    likesCount = 490,
                    commentsCount = 112,
                    replies = emptyList()
                ),
                BoxReviewItem(
                    id = 403,
                    authorName = "Thiago L.",
                    authorHandle = "@thiago_dark",
                    authorAvatar = "https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&auto=format&fit=crop&q=80",
                    timeAgo = "4 dias atrás",
                    tagText = "VIRAL",
                    tagStyle = TagStyle.VIRAL,
                    movieTitle = "A Bruxa",
                    movieInfo = "2016 • Terror Sobrenatural / Época",
                    moviePosterUrl = "https://image.tmdb.org/t/p/w500/opuDnG7MX2eKb4s0r1a1GVJmUXE.jpg",
                    rating = 4.7f,
                    title = "Viver deliciosamente: o nascimento de uma bruxa",
                    body = "“Robert Eggers reconstruiu a linguagem arcaica da Nova Inglaterra do século XVII com rigor histórico implacável. Black Phillip se tornou ícone instantâneo.”",
                    hashtags = listOf("#Eggers", "#Witchcraft"),
                    likesCount = 530,
                    commentsCount = 89,
                    replies = emptyList()
                ),
                BoxReviewItem(
                    id = 404,
                    authorName = "Roberto K.",
                    authorHandle = "@robert_cine",
                    authorAvatar = "https://images.unsplash.com/photo-1535713875002-d1d0cf377fde?w=150&auto=format&fit=crop&q=80",
                    timeAgo = "1 semana atrás",
                    tagText = "Discussão Aberta",
                    tagStyle = TagStyle.OPEN,
                    movieTitle = "O Farol",
                    movieInfo = "2019 • Suspense Psicológico / Preto e Branco",
                    moviePosterUrl = "https://image.tmdb.org/t/p/w500/zdowK9GrOxK0YddhXuMPyxnTA02.jpg",
                    rating = 4.9f,
                    title = "Dois homens, uma sereia e o delírio monocromático",
                    body = "“Willem Dafoe e Robert Pattinson entregam o melhor duelo histriônico do cinema recente em proporção 1.19:1. O monólogo da lagosta é lendário.”",
                    hashtags = listOf("#Dafoe", "#Pattinson", "#Monocromo"),
                    likesCount = 422,
                    commentsCount = 76,
                    replies = emptyList()
                )
            )
        )

        // ─────────────────────────────────────────────────────────────
        // 5. NOVA BOX COLABORATIVA (Animações que Tocam a Alma)
        // ─────────────────────────────────────────────────────────────
        val boxAnimacao = BoxDetailData(
            id = 5,
            title = "Animações que Tocam a Alma",
            creatorName = "Camilla N.",
            creatorHandle = "@camilla_art",
            creatorAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150&auto=format&fit=crop&q=80",
            badgeCuradoria = "COLABORATIVA 👥",
            updatedTime = "Atualizado ontem",
            description = "Obras animadas que ultrapassam o entretenimento infantil e conversam diretamente com a maturidade, memória, despedida e afetos mais profundos.",
            filmesCount = 7,
            viewsCount = "6.1k visualizações",
            savesCount = "1.1k salvos",
            isSaved = true,
            privacyType = BoxPrivacyType.COLABORATIVA,
            coverUrl = "https://image.tmdb.org/t/p/w780/hhoKhsyJ3hFaxEm5pMdZRiTu2lJ.jpg",
            filmes = filmesAnimacao,
            reviews = listOf(
                BoxReviewItem(
                    id = 501,
                    authorName = "Camilla N.",
                    authorHandle = "@camilla_art",
                    authorAvatar = "https://images.unsplash.com/photo-1517841905240-472988babdf9?w=150&auto=format&fit=crop&q=80",
                    timeAgo = "6h atrás",
                    tagText = "COLABORADORA",
                    tagStyle = TagStyle.COLLABORATOR,
                    movieTitle = "A Viagem de Chihiro",
                    movieInfo = "2001 • Fantasia / Animação Japonesa",
                    moviePosterUrl = "https://image.tmdb.org/t/p/w500/hhoKhsyJ3hFaxEm5pMdZRiTu2lJ.jpg",
                    rating = 5.0f,
                    title = "Miyazaki e a transição da infância no mundo dos espíritos",
                    body = "“Chihiro não salva apenas os pais, resgata a própria identidade e o respeito à natureza em uma casa de banhos mística. A trilha de Joe Hisaishi no trem sobre as águas é o ápice da poesia visual.”",
                    hashtags = listOf("#Ghibli", "#Miyazaki", "#Hisaishi"),
                    likesCount = 780,
                    commentsCount = 143,
                    replies = listOf(
                        RespostaComunidade(
                            id = 5011L,
                            autor = "Felipe G.",
                            tempoRelativo = "Há 3 horas",
                            conteudo = "A cena do Sem-Rosto aprendendo sobre a ganância humana e a generosidade da Chihiro é inesquecível.",
                            curtidas = 37
                        )
                    )
                ),
                BoxReviewItem(
                    id = 502,
                    authorName = "Felipe G.",
                    authorHandle = "@felipe_anim",
                    authorAvatar = "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&auto=format&fit=crop&q=80",
                    timeAgo = "1 dia atrás",
                    tagText = "MEMBRO ATIVO",
                    tagStyle = TagStyle.TOP,
                    movieTitle = "Viva: A Vida é Uma Festa",
                    movieInfo = "2017 • Animação / Família / Fantasia",
                    moviePosterUrl = "https://image.tmdb.org/t/p/w500/6oNm06TPz2vGiPc2I52oXW3JwPS.jpg",
                    rating = 5.0f,
                    title = "A memória como a única ponte eterna entre mundos",
                    body = "“A Pixar conseguiu explicar o significado do esquecimento e da ancestralidade de uma forma que quebra qualquer adulto em pedaços quando Miguel canta 'Lembre de Mim' para a Mamá Inês.”",
                    hashtags = listOf("#Pixar", "#Coco", "#Emocionante"),
                    likesCount = 820,
                    commentsCount = 205,
                    replies = emptyList()
                ),
                BoxReviewItem(
                    id = 503,
                    authorName = "Juliana M.",
                    authorHandle = "@juliana_tales",
                    authorAvatar = "https://images.unsplash.com/photo-1494790108377-be9c29b29330?w=150&auto=format&fit=crop&q=80",
                    timeAgo = "2 dias atrás",
                    tagText = "VIRAL",
                    tagStyle = TagStyle.VIRAL,
                    movieTitle = "Up: Altas Aventuras",
                    movieInfo = "2009 • Animação / Aventura / Drama",
                    moviePosterUrl = "https://image.tmdb.org/t/p/w500/oo5gvGDIiPwbXc3R0snZIuOc517.jpg",
                    rating = 4.9f,
                    title = "Os primeiros 10 minutos mais dolorosos e belos do cinema",
                    body = "“Sem falar uma única palavra, a montagem da vida de Carl e Ellie resume o amor, os sonhos não realizados e a beleza de envelhecer ao lado de quem se ama. Cinema com 'C' maiúsculo.”",
                    hashtags = listOf("#PixarClassics", "#Up"),
                    likesCount = 940,
                    commentsCount = 188,
                    replies = emptyList()
                ),
                BoxReviewItem(
                    id = 504,
                    authorName = "Lucas Mendes",
                    authorHandle = "@lucas_critico",
                    authorAvatar = "https://images.unsplash.com/photo-1534528741775-53994a69daeb?w=150&auto=format&fit=crop&q=80",
                    timeAgo = "3 dias atrás",
                    tagText = "COLABORADOR",
                    tagStyle = TagStyle.COLLABORATOR,
                    movieTitle = "Your Name.",
                    movieInfo = "2016 • Animação / Romance / Ficção Científica",
                    moviePosterUrl = "https://image.tmdb.org/t/p/w500/bMOKAjTU1TNUjRSF7icldbii06u.jpg",
                    rating = 5.0f,
                    title = "O fio vermelho do destino cruzando o tempo e as estrelas",
                    body = "“Makoto Shinkai uniu a passagem de um cometa com o esquecimento da memória e a busca pelo outro. A direção de arte e a iluminação em cada céu de Tóquio beiram a perfeição.”",
                    hashtags = listOf("#Anime", "#MakotoShinkai", "#YourName"),
                    likesCount = 670,
                    commentsCount = 98,
                    replies = emptyList()
                )
            )
        )

        boxes[1] = boxPublica
        boxes[2] = boxPrivada
        boxes[3] = boxColaborativa
        boxes[4] = boxTerror
        boxes[5] = boxAnimacao
    }

    fun getBoxById(boxId: Int): BoxDetailData {
        return boxes[boxId] ?: boxes[1]!!
    }

    fun getAllBoxes(): List<BoxDetailData> {
        return boxes.values.toList()
    }

    fun getReviewById(boxId: Int, reviewId: Int): BoxReviewItem? {
        val box = getBoxById(boxId)
        return box.reviews.firstOrNull { it.id == reviewId }
            ?: box.reviews.firstOrNull()
    }

    fun toggleLikeReview(boxId: Int, reviewId: Int): BoxReviewItem? {
        val box = boxes[boxId] ?: return null
        val updatedReviews = box.reviews.map { review ->
            if (review.id == reviewId) {
                val newLiked = !review.isLiked
                val newLikes = if (newLiked) review.likesCount + 1 else review.likesCount - 1
                review.copy(isLiked = newLiked, likesCount = newLikes)
            } else review
        }
        val updatedBox = box.copy(reviews = updatedReviews)
        boxes[boxId] = updatedBox
        return updatedBox.reviews.firstOrNull { it.id == reviewId }
    }

    fun toggleSaveBox(boxId: Int): Boolean {
        val box = boxes[boxId] ?: return false
        val newSaved = !box.isSaved
        boxes[boxId] = box.copy(isSaved = newSaved)
        return newSaved
    }

    fun addCommentToReview(boxId: Int, reviewId: Int, content: String): RespostaComunidade? {
        val box = boxes[boxId] ?: return null
        val review = box.reviews.firstOrNull { it.id == reviewId } ?: return null

        val novoComentario = RespostaComunidade(
            id = System.currentTimeMillis(),
            autor = "Você",
            tempoRelativo = "Agora",
            conteudo = content.trim(),
            curtidas = 0
        )

        val updatedReviews = box.reviews.map { r ->
            if (r.id == reviewId) {
                r.copy(
                    commentsCount = r.commentsCount + 1,
                    replies = listOf(novoComentario) + r.replies
                )
            } else r
        }

        boxes[boxId] = box.copy(reviews = updatedReviews)
        return novoComentario
    }

    fun createBox(
        title: String,
        description: String,
        privacyType: BoxPrivacyType,
        coverUrl: String? = null,
        filmes: List<Moviefmt> = emptyList()
    ): BoxDetailData {
        val newId = (boxes.keys.maxOrNull() ?: 5) + 1
        val newBox = BoxDetailData(
            id = newId,
            title = title.trim(),
            creatorName = "Você",
            creatorHandle = "@ricardobyte",
            creatorAvatar = null,
            badgeCuradoria = "Sua Curadoria",
            updatedTime = "Criada por você agora há pouco.",
            description = description.ifBlank { "Coleção dedicada a obras que desafiam a percepção e exploram a mente humana. Criada por você agora há pouco." },
            filmesCount = filmes.size,
            viewsCount = "1 visualização",
            savesCount = "0 salvos",
            isSaved = false,
            privacyType = privacyType,
            coverUrl = coverUrl ?: "https://images.unsplash.com/photo-1574717024653-61fd2cf4d44d?w=1200&auto=format&fit=crop&q=80",
            filmes = filmes,
            reviews = emptyList()
        )
        boxes[newId] = newBox
        return newBox
    }

    fun getUserBoxes(): List<BoxDetailData> {
        return boxes.values.filter {
            it.creatorHandle == "@ricardobyte" || it.creatorName == "Você"
        }.reversed()
    }

    fun addReview(
        boxId: Int,
        movieTitle: String,
        movieInfo: String,
        moviePosterUrl: String?,
        rating: Float,
        title: String,
        body: String,
        hashtags: List<String> = emptyList(),
        isObraPrima: Boolean = false,
        isFavorito: Boolean = false,
        hasSpoilers: Boolean = false
    ): BoxReviewItem {
        val box = getBoxById(boxId)
        val newReviewId = (box.reviews.maxOfOrNull { it.id } ?: (boxId * 100)) + 1
        val tagText = when {
            isObraPrima -> "OBRA-PRIMA"
            box.privacyType == BoxPrivacyType.PRIVADA -> "NOTA PRIVADA"
            rating >= 4.5f -> "DESTAQUE"
            else -> "Top Resenha"
        }
        val tagStyle = when {
            box.privacyType == BoxPrivacyType.PRIVADA -> TagStyle.PRIVATE
            isObraPrima -> TagStyle.HIGHLIGHT
            else -> TagStyle.TOP
        }
        val newReview = BoxReviewItem(
            id = newReviewId,
            authorName = "Você",
            authorHandle = "@ricardobyte",
            authorAvatar = null,
            timeAgo = "Agora",
            tagText = tagText,
            tagStyle = tagStyle,
            movieTitle = movieTitle,
            movieInfo = movieInfo,
            moviePosterUrl = moviePosterUrl,
            rating = rating,
            title = title,
            body = body,
            hashtags = hashtags,
            likesCount = 0,
            commentsCount = 0,
            replies = emptyList()
        )
        val updatedReviews = listOf(newReview) + box.reviews
        val updatedFilmes = if (box.filmes.none { it.title.equals(movieTitle, ignoreCase = true) }) {
            box.filmes + Moviefmt(
                id = (movieTitle.hashCode() and 0x7FFFFFFF),
                title = movieTitle,
                poster_path = moviePosterUrl,
                release_date = movieInfo.split("•").firstOrNull()?.trim() ?: "2024",
                vote_average = (rating * 2).toDouble()
            )
        } else {
            box.filmes
        }
        val updatedBox = box.copy(
            reviews = updatedReviews,
            filmes = updatedFilmes,
            filmesCount = updatedFilmes.size
        )
        boxes[boxId] = updatedBox
        return newReview
    }
}
