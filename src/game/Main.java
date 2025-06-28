package game;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import game.config.GameConfig;
import game.config.PhaseConfig;
import game.config.PhaseConfig.EnemySpawn;
import game.config.PhaseConfig.PowerUpSpawn;
import game.config.PhaseConfig.BossSpawn;
import game.entities.Entidade;
import game.entities.Jogador;
import game.entities.ProjetilJogador;
import game.entities.ProjetilInimigo;
import game.entities.InimigoTipo1;
import game.entities.InimigoTipo2;
import game.entities.PowerUp;
import game.entities.PowerUpVida;
import game.entities.PowerUpTiroRapido;
import game.entities.ChefeTipo1;
import game.entities.ChefeTipo2;

/***********************************************************************/
/*                                                                     */
/* Para jogar:                                                         */
/*                                                                     */
/*    - cima, baixo, esquerda, direita: movimentação do player.        */
/*    - control: disparo de projéteis.                                 */
/*    - ESC: para sair do jogo.                                        */
/*                                                                     */
/***********************************************************************/

public class Main {
	
	/* Constantes relacionadas aos estados que os elementos   */
	/* do jogo (player, projeteis ou inimigos) podem assumir. */
	
	public static final int INACTIVE = 0;
	public static final int ACTIVE = 1;
	public static final int EXPLODING = 2;
	
	/* Espera, sem fazer nada, até que o instante de tempo atual seja */
	/* maior ou igual ao instante especificado no parâmetro "time.    */
	
	public static void busyWait(long time){
		
		while(System.currentTimeMillis() < time) Thread.yield();
	}
	
	/* Método principal */
	
	public static void main(String [] args){

		/* Indica que o jogo está em execução */

		boolean running = true;

		/* variáveis usadas no controle de tempo efetuado no main loop */
		
		long delta;
		long currentTime = System.currentTimeMillis();

		GameConfig gameConfig = null;
		try {
			gameConfig = new GameConfig("game_config.txt"); // Nome do arquivo de configuração principal
		} catch (IOException e) {
			System.err.println("Erro ao carregar a configuração do jogo: " + e.getMessage());
			System.exit(1);
		}

		// Instanciando o jogador com vidas do arquivo de configuração
		Jogador player = new Jogador(GameLib.WIDTH / 2, GameLib.HEIGHT * 0.90, 12.0, gameConfig.getPlayerLives());

		// Listas de entidades
		ArrayList<ProjetilJogador> playerProjectiles = new ArrayList<ProjetilJogador>();
		ArrayList<ProjetilInimigo> enemyProjectiles = new ArrayList<ProjetilInimigo>();
		ArrayList<InimigoTipo1> enemiesType1 = new ArrayList<InimigoTipo1>();
		ArrayList<InimigoTipo2> enemiesType2 = new ArrayList<InimigoTipo2>();
		ArrayList<PowerUp> powerUps = new ArrayList<PowerUp>();
		ChefeTipo1 boss1 = null;
		ChefeTipo2 boss2 = null;

		/* estrelas que formam o fundo de primeiro plano */
		
		double [] background1_X = new double[20];
		double [] background1_Y = new double[20];
		double background1_speed = 0.070;
		double background1_count = 0.0;
		
		/* estrelas que formam o fundo de segundo plano */
		
		double [] background2_X = new double[50];
		double [] background2_Y = new double[50];
		double background2_speed = 0.045;
		double background2_count = 0.0;
		
		/* inicializações */
		
		for(int i = 0; i < background1_X.length; i++){
			
			background1_X[i] = Math.random() * GameLib.WIDTH;
			background1_Y[i] = Math.random() * GameLib.HEIGHT;
		}
		
		for(int i = 0; i < background2_X.length; i++){
			
			background2_X[i] = Math.random() * GameLib.WIDTH;
			background2_Y[i] = Math.random() * GameLib.HEIGHT;
		}
						
		/* iniciado interface gráfica */
		
		GameLib.initGraphics();
		//GameLib.initGraphics_SAFE_MODE();  // chame esta versão do método caso nada seja desenhado na janela do jogo.
		
		/*************************************************************************************************/
		/*                                                                                               */
		/* Main loop do jogo                                                                             */
		/* -----------------                                                                             */
		/*                                                                                               */
		/* O main loop do jogo executa as seguintes operações:                                           */
		/*                                                                                               */
		/* 1) Verifica se há colisões e atualiza estados dos elementos conforme a necessidade.           */
		/*                                                                                               */
		/* 2) Atualiza estados dos elementos baseados no tempo que correu entre a última atualização     */
		/*    e o timestamp atual: posição e orientação, execução de disparos de projéteis, etc.         */
		/*                                                                                               */
		/* 3) Processa entrada do usuário (teclado) e atualiza estados do player conforme a necessidade. */
		/*                                                                                               */
		/* 4) Desenha a cena, a partir dos estados dos elementos.                                        */
		/*                                                                                               */
		/* 5) Espera um período de tempo (de modo que delta seja aproximadamente sempre constante).      */
		/*                                                                                               */
		/*************************************************************************************************/
		
		int currentPhaseIndex = 0;
		PhaseConfig currentPhase = null;
		long phaseStartTime = currentTime;

		while(running){
		
			// Carrega a próxima fase se necessário
			if (currentPhase == null || (boss1 != null && boss1.getEstado() == Entidade.INATIVA) || (boss2 != null && boss2.getEstado() == Entidade.INATIVA)) {
				if (currentPhaseIndex < gameConfig.getPhaseFiles().size()) {
					try {
						currentPhase = new PhaseConfig(gameConfig.getPhaseFiles().get(currentPhaseIndex));
						phaseStartTime = currentTime; // Reinicia o tempo da fase
						currentPhaseIndex++;
						// Limpa entidades da fase anterior
						enemiesType1.clear();
						enemiesType2.clear();
						powerUps.clear();
						boss1 = null;
						boss2 = null;
					} catch (IOException e) {
						System.err.println("Erro ao carregar a fase: " + e.getMessage());
						running = false;
					}
				} else {
					// Todas as fases concluídas, jogo terminado
					running = false;
					System.out.println("Jogo concluído! Parabéns!");
				}
			}

			if (!running) break; // Sai do loop se o jogo terminou

			/* Usada para atualizar o estado dos elementos do jogo    */
			/* (player, projéteis e inimigos) "delta" indica quantos  */
			/* ms se passaram desde a última atualização.             */
			
			delta = System.currentTimeMillis() - currentTime;
			
			/* Já a variável "currentTime" nos dá o timestamp atual.  */
			
			currentTime = System.currentTimeMillis();
			
			/***************************/
			/* Verificação de colisões */
			/***************************/
						
			if(player.getEstado() == Entidade.ATIVA){
				
				// colisões player - projeteis (inimigo) 
				for(int i = 0; i < enemyProjectiles.size(); i++){
					ProjetilInimigo ep = enemyProjectiles.get(i);
					if(ep.getEstado() == Entidade.ATIVA && player.estaColidindo(ep.getCoordenadaX(), ep.getCoordenadaY(), ep.getRaio())){
						player.explodir(currentTime);
						ep.desativar();
					}
				}
			
				// colisões player - inimigos tipo 1
				for(int i = 0; i < enemiesType1.size(); i++){
					InimigoTipo1 e1 = enemiesType1.get(i);
					if(e1.getEstado() == Entidade.ATIVA && player.estaColidindo(e1.getCoordenadaX(), e1.getCoordenadaY(), e1.getRaio())){
						player.explodir(currentTime);
						e1.explodir(currentTime);
					}
				}
				
				// colisões player - inimigos tipo 2
				for(int i = 0; i < enemiesType2.size(); i++){
					InimigoTipo2 e2 = enemiesType2.get(i);
					if(e2.getEstado() == Entidade.ATIVA && player.estaColidindo(e2.getCoordenadaX(), e2.getCoordenadaY(), e2.getRaio())){
						player.explodir(currentTime);
						e2.explodir(currentTime);
					}
				}

				// colisões player - power-ups
				for(int i = 0; i < powerUps.size(); i++){
					PowerUp pu = powerUps.get(i);
					if(pu.getEstado() == Entidade.ATIVA && player.estaColidindo(pu.getCoordenadaX(), pu.getCoordenadaY(), pu.getRaio())){
						pu.aplicarEfeito(player);
						pu.desativar();
					}
				}

				// colisões player - chefe 1
				if (boss1 != null && boss1.getEstado() == Entidade.ATIVA && player.estaColidindo(boss1.getCoordenadaX(), boss1.getCoordenadaY(), boss1.getRaio())) {
					player.explodir(currentTime);
					boss1.sofrerDano(10); // Exemplo de dano ao chefe
				}

				// colisões player - chefe 2
				if (boss2 != null && boss2.getEstado() == Entidade.ATIVA && player.estaColidindo(boss2.getCoordenadaX(), boss2.getCoordenadaY(), boss2.getRaio())) {
					player.explodir(currentTime);
					boss2.sofrerDano(10); // Exemplo de dano ao chefe
				}
			}
			
			// colisões projeteis (player) - inimigos
			for(int k = 0; k < playerProjectiles.size(); k++){
				ProjetilJogador pp = playerProjectiles.get(k);
				
				for(int i = 0; i < enemiesType1.size(); i++){
					InimigoTipo1 e1 = enemiesType1.get(i);
										
					if(pp.getEstado() == Entidade.ATIVA && e1.getEstado() == Entidade.ATIVA && e1.estaColidindo(pp.getCoordenadaX(), pp.getCoordenadaY(), pp.getRaio())){
						pp.desativar();
						e1.explodir(currentTime);
					}
				}
				
				for(int i = 0; i < enemiesType2.size(); i++){
					InimigoTipo2 e2 = enemiesType2.get(i);
					
					if(pp.getEstado() == Entidade.ATIVA && e2.getEstado() == Entidade.ATIVA && e2.estaColidindo(pp.getCoordenadaX(), pp.getCoordenadaY(), pp.getRaio())){
						pp.desativar();
						e2.explodir(currentTime);
					}
				}

				// colisões projeteis (player) - chefe 1
				if (boss1 != null && boss1.getEstado() == Entidade.ATIVA && pp.getEstado() == Entidade.ATIVA && boss1.estaColidindo(pp.getCoordenadaX(), boss1.getCoordenadaY(), pp.getRaio())) {
					pp.desativar();
					boss1.sofrerDano(1); // Cada projétil causa 1 de dano
				}

				// colisões projeteis (player) - chefe 2
				if (boss2 != null && boss2.getEstado() == Entidade.ATIVA && pp.getEstado() == Entidade.ATIVA && boss2.estaColidindo(pp.getCoordenadaX(), pp.getCoordenadaY(), pp.getRaio())) {
					pp.desativar();
					boss2.sofrerDano(1); // Cada projétil causa 1 de dano
				}
			}
				
			/***************************/
			/* Atualizações de estados */
			/***************************/
			
			// Atualiza o jogador
			player.atualizar(delta, currentTime);

			// Lógica de tiro do jogador
			if (GameLib.iskeyPressed(GameLib.KEY_CONTROL) && currentTime > player.getProximoTiro()) {
				playerProjectiles.add(new ProjetilJogador(player.getCoordenadaX(), player.getCoordenadaY() - player.getRaio(), 2.0, 0.0, -1.0));
				player.setProximoTiro(currentTime + 100);
			}

			// Atualiza projéteis do jogador
			for (int i = 0; i < playerProjectiles.size(); i++) {
				ProjetilJogador pp = playerProjectiles.get(i);
				pp.atualizar(delta, currentTime);
				if (pp.getEstado() == Entidade.INATIVA) {
					playerProjectiles.remove(i);
					i--;
				}
			}

			// Atualiza projéteis inimigos
			for (int i = 0; i < enemyProjectiles.size(); i++) {
				ProjetilInimigo ep = enemyProjectiles.get(i);
				ep.atualizar(delta, currentTime);
				if (ep.getEstado() == Entidade.INATIVA) {
					enemyProjectiles.remove(i);
					i--;
				}
			}
			
			// Lógica de spawn de inimigos e power-ups baseada na configuração da fase
			if (currentPhase != null) {
				for (EnemySpawn es : currentPhase.getEnemySpawns()) {
					if (currentTime - phaseStartTime >= es.when && es.type != -1) { // -1 para indicar que já foi spawnado
						if (es.type == 1) {
							enemiesType1.add(new InimigoTipo1(
								es.x,
								es.y,
								9.0,
								0.3 + Math.random() * 0.2,
								3 * Math.PI / 2,
								0.0
							));
						} else if (es.type == 2) {
							enemiesType2.add(new InimigoTipo2(
								es.x,
								es.y,
								12.0,
								0.2,
								(es.x < GameLib.WIDTH / 2) ? Math.PI * 0.70 : Math.PI * 0.30,
								0.0,
								es.x,
								0
							));
						}
						es.type = -1; // Marca como spawnado
					}
				}

				for (PowerUpSpawn pus : currentPhase.getPowerUpSpawns()) {
					if (currentTime - phaseStartTime >= pus.when && pus.type != -1) {
						if (pus.type == 1) {
							powerUps.add(new PowerUpVida(pus.x, pus.y, 10.0));
						} else if (pus.type == 2) {
							powerUps.add(new PowerUpTiroRapido(pus.x, pus.y, 10.0));
						}
						pus.type = -1; // Marca como spawnado
					}
				}

				BossSpawn bs = currentPhase.getBossSpawn();
				if (bs != null && currentTime - phaseStartTime >= bs.when && bs.type != -1) {
					if (bs.type == 1) {
						boss1 = new ChefeTipo1(bs.x, bs.y, 20.0, 0.1, Math.PI / 2, 0.0, bs.hp);
					} else if (bs.type == 2) {
						boss2 = new ChefeTipo2(bs.x, bs.y, 25.0, 0.15, Math.PI / 2, 0.001, bs.hp);
					}
					bs.type = -1; // Marca como spawnado
				}
			}

			// Atualiza inimigos tipo 1
			for (int i = 0; i < enemiesType1.size(); i++) {
				InimigoTipo1 e1 = enemiesType1.get(i);
				e1.atualizar(delta, currentTime);
				if (e1.getEstado() == Entidade.INATIVA) {
					enemiesType1.remove(i);
					i--;
				} else if (e1.getEstado() == Entidade.ATIVA && currentTime > e1.getProximoTiro() && e1.getCoordenadaY() < player.getCoordenadaY()) {
					enemyProjectiles.add(new ProjetilInimigo(
						e1.getCoordenadaX(),
						e1.getCoordenadaY(),
						2.0,
						Math.cos(e1.getAngulo()) * 0.45,
						Math.sin(e1.getAngulo()) * 0.45 * (-1.0)
					));
					e1.setProximoTiro((long) (currentTime + 200 + Math.random() * 500));
				}
			}

			// Atualiza inimigos tipo 2
			for (int i = 0; i < enemiesType2.size(); i++) {
				InimigoTipo2 e2 = enemiesType2.get(i);
				e2.atualizar(delta, currentTime);
				if (e2.getEstado() == Entidade.INATIVA) {
					enemiesType2.remove(i);
					i--;
				}
			}

			// Atualiza power-ups
			for (int i = 0; i < powerUps.size(); i++) {
				PowerUp pu = powerUps.get(i);
				pu.atualizar(delta, currentTime);
				if (pu.getEstado() == Entidade.INATIVA) {
					powerUps.remove(i);
					i--;
				}
			}

			// Atualiza chefe 1
			if (boss1 != null) {
				boss1.atualizar(delta, currentTime);
				if (boss1.getEstado() == Entidade.ATIVA && currentTime > boss1.getProximoTiro()) {
					enemyProjectiles.add(new ProjetilInimigo(
						boss1.getCoordenadaX(),
						boss1.getCoordenadaY(),
						2.0,
						0.0,
						0.5 // Velocidade do projétil do chefe
					));
					boss1.setProximoTiro((long) (currentTime + 1000 + Math.random() * 500));
				}
			}

			// Atualiza chefe 2
			if (boss2 != null) {
				boss2.atualizar(delta, currentTime);
				if (boss2.getEstado() == Entidade.ATIVA && currentTime > boss2.getProximoTiro()) {
					double[] angles = { Math.PI/2 + Math.PI/8, Math.PI/2, Math.PI/2 - Math.PI/8 };
					for (double angle : angles) {
						double a = angle + Math.random() * Math.PI/6 - Math.PI/12;
						double vx = Math.cos(a) * 0.45;
						double vy = Math.sin(a) * 0.45;
						enemyProjectiles.add(new ProjetilInimigo(boss2.getCoordenadaX(), boss2.getCoordenadaY(), 2.0, vx, vy));
					}
					boss2.setProximoTiro((long) (currentTime + 700 + Math.random() * 300));
				}
			}

			/* estrelas que formam o fundo de primeiro plano */
			
			background1_count += background1_speed * delta;
			
			for(int i = 0; i < background1_X.length; i++){
				
				background1_Y[i] += background1_speed * delta;
				if(background1_Y[i] > GameLib.HEIGHT) background1_Y[i] -= GameLib.HEIGHT;
			}
			
			/* estrelas que formam o fundo de segundo plano */
			
			background2_count += background2_speed * delta;
			
			for(int i = 0; i < background2_X.length; i++){
				
				background2_Y[i] += background2_speed * delta;
				if(background2_Y[i] > GameLib.HEIGHT) background2_Y[i] -= GameLib.HEIGHT;
			}
			
			/*****************/
			/* Desenho da cena */
			/*****************/

			// Desenha o jogador
			player.desenhar(currentTime);

			// Desenha projéteis do jogador
			for (ProjetilJogador pp : playerProjectiles) {
				pp.desenhar(currentTime);
			}

			// Desenha projéteis inimigos
			for (ProjetilInimigo ep : enemyProjectiles) {
				ep.desenhar(currentTime);
			}

			// Desenha inimigos tipo 1
			for (InimigoTipo1 e1 : enemiesType1) {
				e1.desenhar(currentTime);
			}

			// Desenha inimigos tipo 2
			for (InimigoTipo2 e2 : enemiesType2) {
				e2.desenhar(currentTime);
			}

			// Desenha power-ups
			for (PowerUp pu : powerUps) {
				pu.desenhar(currentTime);
			}

			// Desenha chefe 1
			if (boss1 != null) {
				boss1.desenhar(currentTime);
			}

			// Desenha chefe 2
			if (boss2 != null) {
				boss2.desenhar(currentTime);
			}
			
			/* estrelas que formam o fundo de primeiro plano */
			
			GameLib.setColor(new Color(0x202020));
			for(int i = 0; i < background1_X.length; i++){
				
				GameLib.fillRect(background1_X[i], background1_Y[i], 3, 3);
			}
			
			/* estrelas que formam o fundo de segundo plano */
			
			GameLib.setColor(new Color(0x101010));
			for(int i = 0; i < background2_X.length; i++){
				
				GameLib.fillRect(background2_X[i], background2_Y[i], 2, 2);
			}
			
			/* atualiza e redesenha a tela */
			
			GameLib.display();
			
			/* Espera um período de tempo (de modo que delta seja aproximadamente sempre constante) */
			
			busyWait(currentTime + 5);
		}
		
		System.exit(0);
	}
}


