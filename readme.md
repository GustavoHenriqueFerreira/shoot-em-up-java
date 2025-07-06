Shoot 'em Up Java

Este é um jogo simples de "Shoot 'em Up" desenvolvido em Java.

Como Rodar o Projeto

Pré-requisitos

Para compilar e executar este projeto, você precisará ter o Java Development Kit (JDK) instalado em sua máquina. Recomenda-se a versão 8 ou superior.

Configuração

1.
Descompacte o arquivo shoot-em-up-java.zip em um diretório de sua escolha.

2.
Navegue até o diretório raiz do projeto descompactado.

Compilação

Para compilar o projeto, abra um terminal no diretório raiz do projeto e execute o seguinte comando:

Bash


javac src/game/*.java src/game/entities/*.java


Execução

Após a compilação, você pode executar o jogo com o seguinte comando, a partir do diretório raiz do projeto:

Bash


java -cp src game.Main


Controles do Jogo

•
Setas (Cima, Baixo, Esquerda, Direita): Movimentação do jogador.

•
Control: Disparo de projéteis.

•
ESC: Sair do jogo.

Estrutura do Projeto

•
src/: Contém o código-fonte do jogo.

•
game/: Classes principais do jogo, como Main.java e GameManager.java.

•
game/entities/: Classes que representam as entidades do jogo (jogador, inimigos, projéteis, etc.).



•
config/: Arquivos de configuração do jogo (ex: game_config.txt, fase1.txt, fase2.txt).