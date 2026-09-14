# Pokémon OOP Java — Processing

Projeto acadêmico desenvolvido em **Java** para aplicação prática de conceitos de **Programação Orientada a Objetos**, utilizando **Processing** como camada gráfica.

A temática é inspirada em jogos clássicos de captura e evolução de criaturas. O projeto não pretende reproduzir integralmente nenhum jogo oficial: o foco é demonstrar organização de classes, persistência de dados, tratamento de exceções e construção de uma aplicação interativa com interface gráfica, áudio, sprites e animações.

> **Aviso:** projeto educacional e não comercial, sem vínculo oficial com Nintendo, Game Freak ou The Pokémon Company. Pokémon e seus personagens pertencem aos respectivos detentores de direitos.

## Funcionalidades

- Interface gráfica em Processing com execução em tela cheia e escala adaptável;
- sistema de múltiplos saves, criação, carregamento e exclusão de progresso;
- captura de Pokémon com diferentes tipos de Pokébola e probabilidades;
- inventário com rolagem visual;
- Pokédex e gerenciamento dos Pokémon capturados;
- loja e sistema monetário;
- Doce Raro e Pedra da Evolução;
- recompensa de **$800** por captura e evolução bem-sucedidas;
- sprites associados aos Pokémon;
- efeitos sonoros e músicas;
- animação de evolução com transição entre as formas;
- grito do Pokémon antes da evolução e grito da nova forma ao término da transformação;
- janelas modais para avisos e confirmações sem abandonar a tela atual;
- persistência local do progresso.

## Tecnologias

- **Java**
- **Processing Core 4.3.1**
- Java AWT/Java2D
- Programação Orientada a Objetos
- Persistência em arquivos locais
- Reprodução e gerenciamento de áudio em Java

## Conceitos de POO utilizados

O projeto utiliza encapsulamento, herança, polimorfismo, interfaces, classes abstratas, tratamento de exceções e Singleton.

Algumas das classes principais são:

```text
Pokemon.java
PokemonEvolutivo.java
Jogador.java
BancoPokemon.java
Item.java
ItemEvolutivo.java
Pokebola.java
PokebolaComum.java
SuperPokebola.java
UltraPokebola.java
Doce.java
PedraEvolucao.java
Usavel.java
GerenciadorPersistencia.java
AudioManager.java
JogoProcessing.java
```

A lógica do jogo permanece separada da camada visual. `JogoProcessing.java` concentra a apresentação e interação gráfica, enquanto as demais classes representam as regras e entidades do sistema.

## Estrutura

```text
.
├── assets/
│   ├── audio/
│   │   └── cries/
│   ├── cenarios/
│   ├── itens/
│   ├── pokemon/
│   └── pokemon_hd/
├── lib/
├── saves/
├── .vscode/
├── Main.java
├── JogoProcessing.java
├── AudioManager.java
├── BancoPokemon.java
├── Jogador.java
├── Pokemon.java
├── PokemonEvolutivo.java
├── Item.java
├── ...
├── baixar_processing.bat
└── executar_processing.bat
```

## Como executar

### Requisitos

- Windows;
- JDK instalado;
- conexão com a internet na primeira execução para baixar o Processing Core.

Confira o Java:

```powershell
java -version
javac -version
```

Clone o repositório e entre na pasta:

```powershell
git clone https://github.com/MiguelAroot/PokeCapture-Java-Simulator
cd PokeCapture-Java-Simulator
```

Na primeira execução, baixe a dependência do Processing:

```powershell
.\baixar_processing.bat
```

Depois execute o projeto:

```powershell
.\executar_processing.bat
```

O script baixa o **Processing Core 4.3.1** para `lib/`, compila as classes Java e inicia o programa.

## Sistema de captura

O jogador encontra um Pokémon e escolhe uma Pokébola disponível no inventário. Cada tipo possui uma chance própria de captura. O item é consumido na tentativa e, quando a captura é bem-sucedida, o Pokémon é registrado e o jogador recebe **$800**.

## Evolução

Pokémon compatíveis podem evoluir utilizando os itens previstos pela lógica do projeto. A sequência visual e sonora inclui o grito da forma atual, a animação e música de evolução, o grito da nova forma e o efeito final de evolução. Uma evolução concluída concede **$800** ao jogador.

## Saves

O menu inicial permite criar um novo jogo, carregar saves existentes e excluir progressos. Os dados são mantidos localmente na pasta `saves/`, que não é versionada pelo Git para evitar publicar saves pessoais.

## Recursos visuais e sonoros

Os recursos ficam separados da lógica Java dentro de `assets/`. Os sprites são associados aos números dos Pokémon e há uma versão ampliada em `pokemon_hd/` para apresentação em tela cheia. Os gritos ficam em `assets/audio/cries/`.

## Possíveis melhorias futuras

- expansão do banco de Pokémon;
- novas linhas evolutivas e itens;
- sistema de níveis e experiência;
- sistema de batalha;
- opções de volume e configurações;
- novos cenários e transições;
- maior separação arquitetural entre apresentação e regras de negócio.

## Objetivo acadêmico

O objetivo central é utilizar uma aplicação interativa para demonstrar conceitos estudados em **Programação Orientada a Objetos em Java**, combinando modelagem de classes, regras de negócio, persistência, exceções e interface gráfica.

## Licença e propriedade intelectual

O código deste repositório foi produzido para fins acadêmicos. Os nomes, personagens, sprites, sons e demais elementos relacionados à franquia Pokémon permanecem propriedade de seus respectivos detentores de direitos. O repositório não representa um produto oficial e não possui finalidade comercial.
