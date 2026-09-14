# 🎬 Meu Box Favorito

Aplicativo Android de catalogação e comunidade de filmes e séries, organizado em torno de **Boxes**: listas personalizáveis, colaborativas e temáticas criadas pelos próprios usuários.

Projeto desenvolvido para a disciplina de **Engenharia de Software (2026)**.

**Integrantes:** Ricardo, Felipe Yuki, João Lucas Barbora

---

## 📌 Sobre o projeto

O Meu Box Favorito nasce para preencher lacunas deixadas por plataformas como **Letterboxd**, **Filmow** e **TV Time**:

| Concorrente | Problema | Nossa solução |
|---|---|---|
| Filmow | Interface datada, bugs sistêmicos | Arquitetura moderna, código limpo, dark mode nativo |
| Letterboxd | Lentidão, catálogo restrito, resenhas poluídas | Catálogo multi-API (indies/nacionais) + Sistema Híbrido de Resenhas |
| TV Time | Travamentos, navegação confusa (filmes x séries) | Boxes unificadas, performance otimizada |

O foco do app está na **opinião e na discussão** em torno dos filmes — não apenas em catalogar títulos, mas reunir reflexões, críticas e debates da comunidade.

### Diferenciais
- **Modo Visitante** — uso completo do app sem cadastro, com perfil temporário salvo em cache local
- **Boxes Dinâmicas** — listas públicas, privadas ou colaborativas, misturando filmes e séries, com identidade visual própria
- **Catálogo Multi-API** — cruzamento de fontes (TMDb/IMDb) para cobrir produções independentes e nacionais
- **Sistema Híbrido de Resenhas** — críticas detalhadas em texto separadas de reações/chat em tempo real
- **Boxes Colaborativas** — convites por link, estilo Discord, para listas privadas ou em conjunto
- **Importação de dados** — migração facilitada de listas vindas de outras plataformas

---

## 🏗️ Arquitetura

Padrão **MVVM**, com separação clara entre UI, ViewModel, Repository e fontes de dados.

```
                    app/
              MeuBoxFavoritoApp.kt (@HiltAndroidApp)
                       │
        ┌──────────────┴──────────────┐
        │                              │
     di/                            ui/
  (Hilt Modules:              ┌─────┬─────┬──────┬──────────┐
  NetworkModule,            login/ home/ boxes/ detalhes/ components/
  DatabaseModule)             │     │     │      │
                          ViewModel (por tela) → observa State
                                     │
                              data/repository/
                       (MovieRepository, BoxRepository, UserRepository)
                            ┌────────┴────────┐
                       data/remote/       data/local/ + datastore/
                    (Retrofit → TMDb API)  (Room: entities/DAOs; preferências)
```

**Fluxo unidirecional:** Tela (Compose) → observa `StateFlow` do ViewModel → ViewModel chama o Repository → Repository decide entre cache local (Room) ou API remota (Retrofit/TMDb) → dado volta pelo mesmo caminho.

---

## 🛠️ Stack Tecnológica

**UI**
- Jetpack Compose — UI declarativa e reativa
- Material 3 (Material You) — componentes visuais oficiais
- Coil — carregamento e cache de imagens (capas/pôsteres)

**Arquitetura**
- MVVM (Model-View-ViewModel)
- Kotlin Coroutines & Flow — processamento assíncrono

**Injeção de dependência**
- Hilt (Dagger Hilt)

**Rede**
- Retrofit + OkHttp — consumo da API do TMDb
- Kotlinx Serialization — parsing de JSON

**Persistência**
- Room Database — Boxes do usuário e cache do modo visitante
- DataStore (Preferences) — tema, flags de configuração, tokens

**Qualidade**
- LeakCanary — detecção de vazamentos de memória

---

## 📁 Estrutura de pastas

```
app/
 ├─ di/                    # Módulos Hilt (Network, Database)
 ├─ ui/
 │   ├─ login/             # LoginScreen + LoginViewModel
 │   ├─ home/              # HomeScreen + HomeViewModel
 │   ├─ boxes/             # BoxesScreen + BoxesViewModel
 │   ├─ detalhes/          # DetailsScreen + DetailsViewModel
 │   └─ components/        # Componentes reutilizáveis (buttons, cards...)
 └─ data/
     ├─ remote/            # TMDbApi (Retrofit) + DTOs
     ├─ local/             # AppDatabase, Entities, DAOs (Room)
     ├─ datastore/         # Preferências (tema, primeiro acesso)
     └─ repository/        # MovieRepository, BoxRepository, UserRepository
```

---

## 🗺️ Modelo de acesso

| | Usuário cadastrado | Modo visitante (sem cadastro) |
|---|---|---|
| Perfil | Edição completa, identidade visual | Temporário, salvo só no cache local |
| Boxes | Públicas, privadas ou colaborativas | Visualização de boxes públicas |
| Persistência | Nuvem | Local (Room/DataStore), com aviso de dado temporário |

---

## 🗓️ Cronograma (3 meses)

**Mês 1 — Fundação, Base de Dados e Autenticação**
Setup do projeto, login/modo visitante, integração com TMDb, Home básica com filmes populares.

**Mês 2 — Core do App (Boxes e Detalhes)**
Criação/edição de Boxes (pública/privada/local), página de detalhes do filme, primeira versão do sistema de resenhas (críticas em texto).

**Mês 3 — Refinamento, Testes e Publicação**
Testes em diferentes tamanhos de tela, polimento do dark mode, geração de APK/AAB e submissão à Play Store.

> Divisão detalhada de tarefas por trilha/integrante disponível em `Divisao_de_Tarefas_Meu_Box_Favorito.md`.

---

## 🚀 Rodando o projeto

1. Clone o repositório
2. Abra no Android Studio (versão recente, com suporte a Compose)
3. Adicione sua chave da API do TMDb no local apropriado (ex: `local.properties` ou `BuildConfig`)
4. Sincronize o Gradle e rode em um emulador ou dispositivo físico

---

## 📄 Licença

Projeto acadêmico — Engenharia de Software, 2026.
