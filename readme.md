
# Shoot 'em Up Java

Este é um jogo simples de estilo *Shoot 'em Up* desenvolvido em Java, onde o jogador enfrenta ondas de inimigos e chefes em múltiplas fases.

---

## 🎮 Como Rodar o Projeto

### ✔️ Pré-requisitos

- Java Development Kit (JDK) 8 ou superior instalado.
- Um terminal de comando (como o CMD, PowerShell, Terminal Linux/macOS ou integrado do VS Code).

---

### 📦 Configuração

1. Clone ou descompacte o projeto `shoot-em-up-java` em um diretório de sua escolha.
2. Navegue até o diretório raiz do projeto (onde está o arquivo `Main.java`).

---

### 🛠️ Compilação

No terminal, execute o seguinte comando para compilar todos os arquivos `.java`:

```bash
javac -d out -cp src src/game/*.java src/game/lib/*.java src/game/gerenciador/*.java src/game/entidades/*.java src/game/entidades/Inimigo/*.java src/game/entidades/PowerUp/*.java src/game/entidades/Projetil/*.java src/game/entidades/Chefe/*.java
```

> Isso compilará os arquivos na pasta `out/`.

---

### ▶️ Execução

Após compilar, execute o jogo com:

```bash
java -cp out game.Main
```

---

## ⌨️ Controles do Jogo

- **Setas direcionais**: Movimentação do jogador.
- **Control (CTRL)**: Disparo de projéteis.
- **ESC**: Sair do jogo.

---

## 📁 Estrutura do Projeto

```
shoot-em-up-java/
├── src/
│   └── game/
│       ├── Main.java
│       ├── Fundo.java
│       ├── Jogador.java
│       ├── gerenciador/
│       │   ├── GerenciadorJogo.java
│       │   └── ConfiguracaoFase.java
│       ├── entidades/
│       │   ├── Entidade.java
│       │   ├── Chefe/
│       │   ├── Inimigo/
│       │   ├── PowerUp/
│       │   └── Projetil/
│       └── lib/
│           └── GameLib.java
├── out/                  # Arquivos compilados vão para cá
├── config/               # Configurações das fases e jogador
├── README.md
```

---

## 📝 Licença

Este projeto é de código aberto, sob a licença MIT. Sinta-se à vontade para estudar, modificar e distribuir.
