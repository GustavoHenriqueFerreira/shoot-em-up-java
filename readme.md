
# Shoot 'em Up Java

Este é um jogo simples de **Shoot 'em Up** desenvolvido em **Java**, com foco em aprendizado de orientação a objetos, herança e controle de entidades em tempo real.

## 🎮 Como Rodar o Projeto

### ✅ Pré-requisitos

- Java Development Kit (JDK) 8 ou superior instalado.
- Terminal ou prompt de comando.

---

### 📦 Estrutura do Projeto

```
src/
├── game/
│   ├── entidades/
│   │   ├── Chefe/
│   │   ├── Inimigo/
│   │   ├── PowerUp/
│   │   ├── Projetil/
│   │   ├── Entidade.java
│   │   ├── Fundo.java
│   │   └── Jogador.java
│   ├── gerenciador/
│   ├── Main.java
│   └── TesteFasesChefes.java
├── config/
│   ├── game_config.txt
│   ├── fase1.txt
│   └── fase2.txt
```

---

### 🛠️ Compilação

Abra um terminal na raiz do projeto (onde está o `src/`) e execute:

```bash
javac -d out src/game/**/*.java
```

> Isso compilará todos os arquivos recursivamente para a pasta `out`.

---

### 🚀 Execução

Após compilar, execute o jogo com:

```bash
java -cp out game.Main
```

---

### 🎮 Controles do Jogo

- **Setas**: Movimentar o jogador
- **Control**: Atirar
- **ESC**: Encerrar o jogo

---

### 📝 Arquivos de Configuração

Os arquivos `.txt` na pasta `config/` definem:

- A vida inicial do jogador
- A sequência de fases
- Inimigos, chefes e power-ups por fase

---

### 📄 Licença

Este projeto é open-source, licenciado sob a [MIT License](https://opensource.org/licenses/MIT).  
Sinta-se à vontade para usar, modificar e distribuir.

---

### 💡 Dica

Se desejar mover `ProjetilTeleguiado` para `entidades/Projetil/`, lembre-se de usar **getters** para acessar atributos protegidos da classe `Entidade` quando estiver em outro pacote. 😉
