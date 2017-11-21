package mapa;

import java.util.Random;

import database.Informacao;
import entidades.Conteudo;
import reacoes.Percepcoes;

public class Quadrado extends Probabilidade{
	private boolean percepcoesAtuais[] =  {
			false, false,false, false,false, false,false, false,
	}; // fedor, brisa, resplendor, impacto(up, left, bottom, right), grito
	private static int qtdWump = 0;
	private static int qtdOuro = 0;
	private static int qtdPoco = 0;
	private boolean temWump = false;
	private boolean temPoco = false;
	private int peso = 0;
	private Informacao conhecimento = new Informacao(); // informacao adquirida pelo agente durante a execução
	private boolean descoberta = false;
	
	public void refazMapa() {
		qtdWump = 0;
		qtdOuro = 0;
		qtdPoco = 0;
	}
	public boolean geraWump() {
		if (hasWump(qtdWump)) {
			qtdWump++;
			temWump = true;
			return true;
		}
		return false;
	}
	
	public boolean geraPoco() {
		if (temPoco() && qtdPoco<3) {
			temPoco = true;
			qtdPoco++;
			return true;
		}
		return false;
	}
	
	public boolean geraOuro() {
		if( temOuro(qtdOuro)) {
			percepcoesAtuais[2] = true;
			qtdOuro++;
			return true;
		}
		return false;
	}
	
	public boolean[] getPercepcoes() {
		return percepcoesAtuais;
	}
	
	public void setPercepcoes(int pos, boolean valor) {
		if (percepcoesAtuais != null) {
			percepcoesAtuais[pos] = valor;
		}else {
			System.out.println("Null");
		}
	}

	public static boolean restricao() {
		return (qtdWump==1 && qtdOuro==1 && qtdPoco==3);
	}
	
	public boolean hasWumps() {
		return conhecimento.hasWump();
	}
	
	public boolean hasPoco() {
		return conhecimento.hasPoco();
	}
	
	public boolean getTemPoco() {
		return temPoco;
	}
	
	public boolean getTemWump() {
		return temWump;
	}
	public boolean shoot () {
		if(temWump) {
			temWump = false;
			conhecimento.mataWumpus();
			return true;
		}
		return false;
	}
	
	public boolean getDescuberta() {
		return descoberta;
	}
	
	public void setDescoberta () {
		peso++;
		descoberta = true;
	}
	
	public void setDescoberta (boolean value) {
		descoberta = value;
	}

	public Informacao getConhecimento() {
		return conhecimento;
	}

	public void setConhecimento(Informacao conhecimento) {
		this.conhecimento = conhecimento;
	}
	
	public void printPercepcoes() {
		if(percepcoesAtuais[0]) {
		System.out.print("Fedor ");
		}
		if(percepcoesAtuais[1]) {
			System.out.print("Brisa ");
		}
	}
	
	public int getPeso() {
		return peso;
	}
}