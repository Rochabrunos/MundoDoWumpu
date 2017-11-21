package entidades;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import mapa.Grid;
import mapa.Quadrado;
import quantificadoresDesempenho.MedDesempenho;

public class Agente {
	private Grid mapa;
	private int i;
	private int j;
	private  MedDesempenho performace = new MedDesempenho(); 
	private boolean flecha = true;
	private boolean wumpDescoberto = false;
	private static int qntdDescoberto = 0; 
	private int wumpusX = -1;
	private int wumpusY = -1;
	
	public Agente (int i, int j) {
		this.i = i;
		this.j = j;
		geraMapa();
		mapa.grid[i][j].setDescoberta();
		acao();
	}
	public void geraMapa() {
		Grid mapa = new Grid();
		try {
			if (mapa.grid[0][0].restricao()) {
				this.mapa = mapa;
				this.mapa.printMap();
			}else {
				geraMapa();
			}
		}catch (Exception e) {
			System.out.println("N�O FOI POSSIVEL GERAR O MAPA.");
		}
	}
	public boolean verificacao(int i, int j) {
		if( (mapa.grid[i][j].hasWumps() ||  mapa.grid[i][j].hasPoco())) {
			return false;
		}
		return true;
	}
	public void acao () {
		int desempenho=0, pesoColetar=-999999999, pesoMover=-999999999, pesoAtirar=-999999999, pesoMorrer=-999999999, atual = -999999999;
		String acao = null;
			if(mapa.grid[i][j].getTemPoco() || mapa.grid[i][j].getTemWump()) {
				System.out.println("Perdeu o jogo");
				System.exit(1);
			}
			if((mapa.grid[i][j].getPercepcoes()[0] || mapa.grid[i][j].getPercepcoes()[1])) return;
			if( mapa.grid[i][j].getPercepcoes()[2] == true) {
				pesoColetar = performace.desempenho("coletar");
				if( pesoColetar > atual) {
					atual = pesoColetar;
					acao = "coletar";
				}
			}
			if(wumpusX != -1 && wumpusY != -1) {
				pesoAtirar = performace.desempenho("atirar");
				if( pesoAtirar > atual) {
					atual = pesoAtirar;
					acao = "atirar";
				}
			}
			if(mapa.grid[i][j].getPercepcoes()[3] == false && verificacao(i, j+1)) {
				if(!mapa.grid[i][j+1].getDescuberta()) { // prioriza a descoberta de novos quadrados
					pesoMover = performace.desempenho("mover") +5;
				}else {
					pesoMover = performace.desempenho("mover") - mapa.grid[i][j+1].getPeso();
				}
				if(pesoMover > atual) {
					atual = pesoMover;
					acao = "cima";
				}
			}
			if(mapa.grid[i][j].getPercepcoes()[4] == false && verificacao(i+1, j)) {
				if(!mapa.grid[i+1][j].getDescuberta()) { // prioriza a descoberta de novos quadrados
					pesoMover = performace.desempenho("mover")+5;
				}else {
					pesoMover = performace.desempenho("mover") - mapa.grid[i+1][j].getPeso();
				}
				if(pesoMover > atual) {
					atual = pesoMover;
					acao = "direita";
				}
			}
			if(mapa.grid[i][j].getPercepcoes()[5] == false && verificacao(i, j-1)) {
				if(!mapa.grid[i][j-1].getDescuberta()) { // prioriza a descoberta de novos quadrados
					pesoMover = performace.desempenho("mover") + 5;
				}else {
					pesoMover = performace.desempenho("mover") - mapa.grid[i][j-1].getPeso();
				}
				if(pesoMover > atual) {
					atual = pesoMover;
					acao = "baixo";
				}
			}
			if(mapa.grid[i][j].getPercepcoes()[6] == false && verificacao(i-1, j)) {
				if(!mapa.grid[i-1][j].getDescuberta()) { // prioriza a descoberta de novos quadrados
					pesoMover = performace.desempenho("mover") + 5;
				}else {
					pesoMover = performace.desempenho("mover") - mapa.grid[i-1][j].getPeso();
				}
				if(pesoMover > atual) {
					atual = pesoMover;
					acao = "esquerda";
				}
			}
			
			switch(acao) {
			case "cima":
				moveToCima();
				performace.agir("mover");
				break;
			case "direita":
				moveToDireita();
				performace.agir("mover");
				break;
			case "baixo":
				moveToBaixo();
				performace.agir("mover");
				break;
			case "esquerda":
				moveToEsquerda();
				performace.agir("mover");
				break;
			case "coletar":
				performace.agir("coletar");
				mapa.grid[i][j].setPercepcoes(2, false);
				System.out.println("Ganhei o jogo. Sua pontua��o foi: " + performace.getPontuacao());
				System.exit(1);
				break;
			case "atirar":
				performace.agir("atirar");
				System.out.println("Atirando no Baiano.");
				if(mapa.shoot(wumpusX, wumpusY)) {
					System.out.println("Acertou Miseravel.");
					mapa.grid[wumpusX][wumpusY].shoot();
					wumpusX = -1;
					wumpusY = -1;
				}else {
					System.out.println("Errou!! ");
				}
				break;
			default:
				moveToDireita();
				performace.agir("mover");
				break;
			}
			
			acao();
			atual = pesoColetar = pesoMover = pesoAtirar = pesoMorrer=-999999999;
			acao = "";

	}
	
	public void deducaoWumps() {
		if(j<3) {
			deducaoWumps(i, j+1);
		}
		if(i<3) {
			deducaoWumps(i+1, j);
		}
		if(j>0) {
			deducaoWumps(i, j-1);
		}
		if(i>0) {
			deducaoWumps(i-1, j);
		}
	}
	public void deducaoWumps (int i, int j) {
	
		if(wumpDescoberto) {
			return;
		}
		// se o proximo quadrado ja foi visitado anteriormente e existe fedor então existe a possibilidade de se fazer alguma deduçao 
		if(!mapa.grid[i][j].getPercepcoes()[4] && mapa.grid[i+1][j].getDescuberta() && mapa.grid[i+1][j].getPercepcoes()[0]) {
			//	se o quadrado acima ja foi visitado anteriormente e existe fedor então existe um wump nele
			if(!mapa.grid[i][j].getPercepcoes()[3] && mapa.grid[i][j+1].getDescuberta() && mapa.grid[i][j+1].getPercepcoes()[0]) {
				// o quadrado ja esta marcado como o receptaculo do wump então não precisa de marca-lo novamente
				if(!mapa.grid[i+1][j+1].getConhecimento().hasWump()) {
					System.out.println("Descobri um WUMP no quadrado1 " + (i+1) + (j+1)  + "Porra");
					mapa.grid[i+1][j+1].getConhecimento().isWump();
					wumpDescoberto = true;
					mapa.definePercepcoes(i+1, j+1, 0, false);
					wumpusX = i+1;
					wumpusY = j+1;
				}
			// caso contrario verifica-se no quadrado abaixo
			}else if(!mapa.grid[i][j].getPercepcoes()[5] && mapa.grid[i][j-1].getDescuberta()&& mapa.grid[i][j-1].getPercepcoes()[0]) { 
				// o quadrado ja esta marcado como o receptaculo do wump então não precisa de marca-lo novamente
				if(!mapa.grid[i+1][j-1].getConhecimento().hasWump()) {
					System.out.println("Descobri um WUMP no quadrado2 " + (i+1) + (j-1)  + "Porra");
					mapa.grid[i+1][j-1].getConhecimento().isWump();
					wumpDescoberto = true;
					mapa.definePercepcoes(i+1, j-1, 0, false);
					wumpusX = i+1;
					wumpusY = j-1;
				}
			}
		}
		// se o quadrado anterior ja foi visitado anteriormente e existe fedor então existe a possibilidade de se fazer alguma deduçao 
		if(!mapa.grid[i][j].getPercepcoes()[6] && mapa.grid[i-1][j].getDescuberta()  && mapa.grid[i-1][j].getPercepcoes()[0]) { //entao quer dizer que ja visitei este quadrado
			// se o quadrado acima ja foi visitado anteriormente e existe fedor então existe um wump nele
			if(!mapa.grid[i][j].getPercepcoes()[3] && mapa.grid[i][j+1].getDescuberta()&& mapa.grid[i][j+1].getPercepcoes()[0]) {
				// o quadrado ja esta marcado como o receptaculo do wump então não precisa de marca-lo novamente
				if(!mapa.grid[i-1][j+1].getConhecimento().hasWump()) {
					System.out.println("Descobri um WUMP no quadrado3 " + (i-1) + (j+1) + "Porra");
					mapa.grid[i-1][j+1].getConhecimento().isWump();
					wumpDescoberto = true;
					mapa.definePercepcoes(i-1, j+1, 0, false);
					wumpusX = i-1;
					wumpusY = j+1;
				}
			// caso contrario verifica-se no quadrdo abaixo
			}else if(!mapa.grid[i][j].getPercepcoes()[5] && mapa.grid[i][j-1].getDescuberta()&& mapa.grid[i][j-1].getPercepcoes()[0]) { 
				// o quadrado ja esta marcado como o receptaculo do wump então não precisa de marca-lo novamente
				if(!mapa.grid[i-1][j-1].getConhecimento().hasWump()) {
					System.out.println("Descobri um WUMP no quadrado4 " + (i-1) + (j-1)  + "Porra");
					mapa.grid[i-1][j-1].getConhecimento().isWump();
					wumpDescoberto = true;
					mapa.definePercepcoes(i-1, j-1, 0, false);
					wumpusX = i-1;
					wumpusY = j-1;
				}
			}
		}
	}
	public void deducaoPoco() {
		if(j<3) 
		deducaoPoco(i, j+1);
		if(i<3) 
		deducaoPoco(i+1, j);
		if(j>0) 
		deducaoPoco(i, j-1);
		if(i>0) 
		deducaoPoco(i-1, j);
	}
	public void deducaoPoco (int i, int j) {
		// se o proximo quadrado ja foi visitado anteriormente e existe brisa então existe a possibilidade de se fazer alguma deduçao 
		if(!mapa.grid[i][j].getPercepcoes()[4] && mapa.grid[i+1][j].getDescuberta() && mapa.grid[i+1][j].getPercepcoes()[1]) {
			//	se o quadrado acima ja foi visitado anteriormente e existe brisa então existe um poco nele
			if(!mapa.grid[i][j].getPercepcoes()[3] && mapa.grid[i][j+1].getDescuberta() && mapa.grid[i][j+1].getPercepcoes()[1]) {
				// o quadrado ja esta marcado como o receptaculo do poco então não precisa de marca-lo novamente
				if(!mapa.grid[i+1][j+1].getConhecimento().hasPoco()) {
					System.out.println("Descobri um Poco no quadrado " + (i+1) + (j+1));
					mapa.grid[i+1][j+1].getConhecimento().isPoco();
					mapa.definePercepcoes(i+1, j+1, 1, false);
					mapa.refazPercepcoes();
				}
			// caso contrario verifica-se no quadrado abaixo
			}else if(!mapa.grid[i][j].getPercepcoes()[5] && mapa.grid[i][j-1].getDescuberta()&& mapa.grid[i][j-1].getPercepcoes()[1]) { 
				// o quadrado ja esta marcado como o receptaculo do wump então não precisa de marca-lo novamente
				if(!mapa.grid[i+1][j-1].getConhecimento().hasPoco()) {
					System.out.println("Descobri um Poco no quadrado " + (i+1) + (j-1));
					mapa.grid[i+1][j-1].getConhecimento().isPoco();
					mapa.definePercepcoes(i+1, j-1, 1, false);
					mapa.refazPercepcoes();
				}
			}
		}
		// se o quadrado anterior ja foi visitado anteriormente e existe brisa então existe a possibilidade de se fazer alguma deduçao 
		if(!mapa.grid[i][j].getPercepcoes()[6] && mapa.grid[i-1][j].getDescuberta()  && mapa.grid[i-1][j].getPercepcoes()[1]) { 
			// se o quadrado acima ja foi visitado anteriormente e existe brisa então existe um poco nele
			if(!mapa.grid[i][j].getPercepcoes()[3] && mapa.grid[i][j+1].getDescuberta()&& mapa.grid[i][j+1].getPercepcoes()[1]) {
				// o quadrado ja esta marcado como o receptaculo do poco então não precisa de marca-lo novamente
				if(!mapa.grid[i-1][j+1].getConhecimento().hasPoco()) {
					System.out.println("Descobri um Poco no quadrado " + (i-1) + (j+1));
					mapa.grid[i-1][j+1].getConhecimento().isPoco();
					mapa.definePercepcoes(i-1, j+1, 1, false);
					mapa.refazPercepcoes();
				}
			// caso contrario verifica-se no quadrdo abaixo
			}else if(!mapa.grid[i][j].getPercepcoes()[5] && mapa.grid[i][j-1].getDescuberta()&& mapa.grid[i][j-1].getPercepcoes()[1]) { 
				// o quadrado ja esta marcado como o receptaculo do poco então não precisa de marca-lo novamente
				if(!mapa.grid[i-1][j-1].getConhecimento().hasPoco()) {
					System.out.println("Descobri um Poco no quadrado " + (i-1) + (j-1));
					mapa.grid[i-1][j-1].getConhecimento().isPoco();
					mapa.definePercepcoes(i-1, j-1, 1, false);
					mapa.refazPercepcoes();
				}
			}
		}
	}

	public void moveToCima() {
		deducaoWumps();
		deducaoPoco ();
		if(mapa.grid[i][j].getPercepcoes()[0] || mapa.grid[i][j].getPercepcoes()[1])
			return;
		j = j + 1;
		mapa.grid[i][j].setDescoberta();
		qntdDescoberto++;
		//System.out.println("Movi para o quadrado" + i + " " + j);
		acao();
		j = j - 1;
		//System.out.println("Voltei " + i + " " + j);
	}
	public void moveToDireita() {
		deducaoWumps();
		deducaoPoco ();
		if(mapa.grid[i][j].getPercepcoes()[0] || mapa.grid[i][j].getPercepcoes()[1]) {
			return;
		}
		i = i + 1;
		mapa.grid[i][j].setDescoberta();
		qntdDescoberto++;
		//System.out.println("Movi para o quadrado" + i + " " + j);
		acao();
		i = i - 1;
		//System.out.println("Voltei " + i + " " + j);
	}
	public void moveToBaixo() {
		deducaoWumps();
		deducaoPoco ();
		if(mapa.grid[i][j].getPercepcoes()[0] || mapa.grid[i][j].getPercepcoes()[1])
			return;
		j = j - 1;
		//System.out.println("Movi para o quadrado" + i + " " + j);
		mapa.grid[i][j].setDescoberta();
		qntdDescoberto++;
		acao();
		j = j + 1;
		//System.out.println("Voltei " + i + " " + j);
	}
	public void moveToEsquerda() {
		deducaoWumps();
		deducaoPoco ();
		if(mapa.grid[i][j].getPercepcoes()[0] || mapa.grid[i][j].getPercepcoes()[1])
			return;
		i = i - 1;
		mapa.grid[i][j].setDescoberta();
		qntdDescoberto++;
		//System.out.println("Movi para o quadrado" + i + " " + j);
		acao();
		i = i + 1;	
		//System.out.println("Voltei " + i + " " + j);
	}	
}
