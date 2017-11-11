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
	
	public Agente (int i, int j) {
		this.i = i;
		this.j = j;
		mapa = new Grid();
		mapa.grid[i][j].setDescoberta();
		acao();
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
			if(qntdDescoberto == 16) {
				System.out.println(qntdDescoberto);
				System.exit(1);
			}
			if((mapa.grid[i][j].getPercepcoes()[0] || mapa.grid[i][j].getPercepcoes()[1])) return;
			if( mapa.grid[i][j].getPercepcoes()[2] == true) {
				pesoColetar = performace.desempenho("coletar");
				if( pesoColetar > atual) {
					atual = pesoColetar;
					acao = "coletar";
				}
				System.out.println("Você ganhou o jogo!");
				System.exit(1);
			}
			if(mapa.grid[i][j].getPercepcoes()[3] == false && verificacao(i, j+1) && !(mapa.grid[i][j].getPercepcoes()[0] || mapa.grid[i][j].getPercepcoes()[1])) {
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
			if(mapa.grid[i][j].getPercepcoes()[4] == false && verificacao(i+1, j) && !(mapa.grid[i][j].getPercepcoes()[0] || mapa.grid[i][j].getPercepcoes()[1])) {
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
			if(mapa.grid[i][j].getPercepcoes()[5] == false && verificacao(i, j-1) && !(mapa.grid[i][j].getPercepcoes()[0] || mapa.grid[i][j].getPercepcoes()[1])) {
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
			if(mapa.grid[i][j].getPercepcoes()[6] == false && verificacao(i-1, j) && !(mapa.grid[i][j].getPercepcoes()[0] || mapa.grid[i][j].getPercepcoes()[1])) {
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
			
			System.out.println(acao + " " + atual);
			
			switch(acao) {
			case "cima":
				moveToCima();
				performace.desempenho("mover");
				break;
			case "direita":
				moveToDireita();
				performace.desempenho("mover");
				break;
			case "baixo":
				moveToBaixo();
				performace.desempenho("mover");
				break;
			case "esquerda":
				moveToEsquerda();
				performace.desempenho("mover");
				break;
			}
			
			acao();
			atual = pesoColetar = pesoMover = pesoAtirar = pesoMorrer=-999999999;
			acao = "";

	}
	public void deducaoWumps () {
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
				}
			// caso contrario verifica-se no quadrado abaixo
			}else if(!mapa.grid[i][j].getPercepcoes()[5] && mapa.grid[i][j-1].getDescuberta()&& mapa.grid[i][j-1].getPercepcoes()[0]) { 
				// o quadrado ja esta marcado como o receptaculo do wump então não precisa de marca-lo novamente
				if(!mapa.grid[i+1][j-1].getConhecimento().hasWump()) {
					System.out.println("Descobri um WUMP no quadrado2 " + (i+1) + (j-1)  + "Porra");
					mapa.grid[i+1][j-1].getConhecimento().isWump();
					wumpDescoberto = true;
					mapa.definePercepcoes(i+1, j-1, 0, false);
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
				}
			// caso contrario verifica-se no quadrdo abaixo
			}else if(!mapa.grid[i][j].getPercepcoes()[5] && mapa.grid[i][j-1].getDescuberta()&& mapa.grid[i][j-1].getPercepcoes()[0]) { 
				// o quadrado ja esta marcado como o receptaculo do wump então não precisa de marca-lo novamente
				if(!mapa.grid[i-1][j-1].getConhecimento().hasWump()) {
					System.out.println("Descobri um WUMP no quadrado4 " + (i-1) + (j-1)  + "Porra");
					mapa.grid[i-1][j-1].getConhecimento().isWump();
					wumpDescoberto = true;
					mapa.definePercepcoes(i-1, j-1, 0, false);
				}
			}
		}
		// se o quadrado superior ja foi visitado anteriormente e existe fedor então existe a possibilidade de se fazer alguma deduçao 
		if(!mapa.grid[i][j].getPercepcoes()[3] && mapa.grid[i][j+1].getDescuberta()  && mapa.grid[i][j+1].getPercepcoes()[0]) { //entao quer dizer que ja visitei este quadrado
			// se o proximo quadrado ja foi visitado anteriormente e existe fedor então existe um wump nele
			if(!mapa.grid[i][j].getPercepcoes()[4] && mapa.grid[i+1][j].getDescuberta()&& mapa.grid[i+1][j].getPercepcoes()[0]) {
				// o quadrado ja esta marcado como o receptaculo do wump então não precisa de marca-lo novamente
				if(!mapa.grid[i+1][j+1].getConhecimento().hasWump()) {
					System.out.println("Descobri um WUMP no quadrado5 " + (i+1) + (j+1)  + "Porra");
					mapa.grid[i+1][j+1].getConhecimento().isWump();
					wumpDescoberto = true;
				}
			// caso contrario verifica-se no quadrdo anterior
			}else if(!mapa.grid[i][j].getPercepcoes()[6] && mapa.grid[i-1][j].getDescuberta()&& mapa.grid[i-1][j].getPercepcoes()[0]) { 
				// o quadrado ja esta marcado como o receptaculo do wump então não precisa de marca-lo novamente
				if(!mapa.grid[i-1][j+1].getConhecimento().hasWump()) {
					System.out.println("Descobri um WUMP no quadrado6 " + (i-1) + (j+1));
					mapa.grid[i-1][j+1].getConhecimento().isWump();
					wumpDescoberto = true;
				}
			}
		}
		// se o quadrado inferior ja foi visitado anteriormente e existe fedor então existe a possibilidade de se fazer alguma deduçao 
		if(!mapa.grid[i][j].getPercepcoes()[5] && mapa.grid[i][j-1].getDescuberta()  && mapa.grid[i][j-1].getPercepcoes()[0]) { //entao quer dizer que ja visitei este quadrado
			// se o próximo quadrado ja foi visitado anteriormente e existe fedor então existe um wump nele
			if(!mapa.grid[i][j].getPercepcoes()[4] && mapa.grid[i+1][j].getDescuberta()&& mapa.grid[i+1][j].getPercepcoes()[0]) {
				// o quadrado ja esta marcado como o receptaculo do wump então não precisa de marca-lo novamente
				if(!mapa.grid[i+1][j-1].getConhecimento().hasWump()) {
					System.out.println("Descobri um WUMP no quadrado7 " + (i+1) + (j-1)  + "Porra");
					mapa.grid[i+1][j-1].getConhecimento().isWump();
					wumpDescoberto = true;
				}
			// caso contrario verifica-se no quadrdo anterior
			}else if(!mapa.grid[i][j].getPercepcoes()[6] && mapa.grid[i-1][j].getDescuberta()&& mapa.grid[i-1][j].getPercepcoes()[0]) { 
				// o quadrado ja esta marcado como o receptaculo do wump então não precisa de marca-lo novamente
				if(!mapa.grid[i-1][j-1].getConhecimento().hasWump()) {
					System.out.println("Descobri um WUMP no quadrado8 " + (i-1) + (j-1) + "Porra");
					mapa.grid[i-1][j-1].getConhecimento().isWump();
					wumpDescoberto = true;
				}
			}
		}
//		Scanner in = new Scanner(System.in);
//		System.out.println(in.next());
	}
	
	public void deducaoPoco () {
		// se o proximo quadrado ja foi visitado anteriormente e existe brisa então existe a possibilidade de se fazer alguma deduçao 
		if(!mapa.grid[i][j].getPercepcoes()[4] && mapa.grid[i+1][j].getDescuberta() && mapa.grid[i+1][j].getPercepcoes()[1]) {
			//	se o quadrado acima ja foi visitado anteriormente e existe brisa então existe um poco nele
			if(!mapa.grid[i][j].getPercepcoes()[3] && mapa.grid[i][j+1].getDescuberta() && mapa.grid[i][j+1].getPercepcoes()[1]) {
				// o quadrado ja esta marcado como o receptaculo do poco então não precisa de marca-lo novamente
				if(!mapa.grid[i+1][j+1].getConhecimento().hasPoco()) {
					System.out.println("Descobri um Poco no quadrado " + (i+1) + (j+1)  + "Porra");
					mapa.grid[i+1][j+1].getConhecimento().isPoco();
					mapa.definePercepcoes(i+1, j+1, 1, false);
				}
			// caso contrario verifica-se no quadrado abaixo
			}else if(!mapa.grid[i][j].getPercepcoes()[5] && mapa.grid[i][j-1].getDescuberta()&& mapa.grid[i][j-1].getPercepcoes()[1]) { 
				// o quadrado ja esta marcado como o receptaculo do wump então não precisa de marca-lo novamente
				if(!mapa.grid[i+1][j-1].getConhecimento().hasPoco()) {
					System.out.println("Descobri um Poco no quadrado " + (i+1) + (j-1)  + "Porra");
					mapa.grid[i+1][j-1].getConhecimento().isPoco();
					mapa.definePercepcoes(i+1, j-1, 1, false);
				}
			}
		}
		// se o quadrado anterior ja foi visitado anteriormente e existe brisa então existe a possibilidade de se fazer alguma deduçao 
		if(!mapa.grid[i][j].getPercepcoes()[6] && mapa.grid[i-1][j].getDescuberta()  && mapa.grid[i-1][j].getPercepcoes()[1]) { 
			// se o quadrado acima ja foi visitado anteriormente e existe brisa então existe um poco nele
			if(!mapa.grid[i][j].getPercepcoes()[3] && mapa.grid[i][j+1].getDescuberta()&& mapa.grid[i][j+1].getPercepcoes()[1]) {
				// o quadrado ja esta marcado como o receptaculo do poco então não precisa de marca-lo novamente
				if(!mapa.grid[i-1][j+1].getConhecimento().hasPoco()) {
					System.out.println("Descobri um Poco no quadrado " + (i-1) + (j+1) + "Porra");
					mapa.grid[i-1][j+1].getConhecimento().isPoco();
					mapa.definePercepcoes(i-1, j+1, 1, false);
				}
			// caso contrario verifica-se no quadrdo abaixo
			}else if(!mapa.grid[i][j].getPercepcoes()[5] && mapa.grid[i][j-1].getDescuberta()&& mapa.grid[i][j-1].getPercepcoes()[1]) { 
				// o quadrado ja esta marcado como o receptaculo do poco então não precisa de marca-lo novamente
				if(!mapa.grid[i-1][j-1].getConhecimento().hasPoco()) {
					System.out.println("Descobri um Poco no quadrado " + (i-1) + (j-1)  + "Porra");
					mapa.grid[i-1][j-1].getConhecimento().isPoco();
					mapa.definePercepcoes(i-1, j-1, 1, false);
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
		System.out.println("Movi para o quadrado" + i + " " + j);
		acao();
		j = j - 1;
		System.out.println("Voltei " + i + " " + j);
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
		System.out.println("Movi para o quadrado" + i + " " + j);
		acao();
		i = i - 1;
		System.out.println("Voltei " + i + " " + j);
	}
	public void moveToBaixo() {
		deducaoWumps();
		deducaoPoco ();
		if(mapa.grid[i][j].getPercepcoes()[0] || mapa.grid[i][j].getPercepcoes()[1])
			return;
		j = j - 1;
		System.out.println("Movi para o quadrado" + i + " " + j);
		mapa.grid[i][j].setDescoberta();
		qntdDescoberto++;
		acao();
		j = j + 1;
		System.out.println("Voltei " + i + " " + j);
	}
	public void moveToEsquerda() {
		deducaoWumps();
		deducaoPoco ();
		if(mapa.grid[i][j].getPercepcoes()[0] || mapa.grid[i][j].getPercepcoes()[1])
			return;
		i = i - 1;
		mapa.grid[i][j].setDescoberta();
		qntdDescoberto++;
		System.out.println("Movi para o quadrado" + i + " " + j);
		acao();
		i = i + 1;	
		System.out.println("Voltei " + i + " " + j);
	}	
}
