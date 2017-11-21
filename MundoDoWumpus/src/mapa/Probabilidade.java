package mapa;

import java.util.Random;
//essa classe possui a probabilidade dos elementos ocorrerem
public class Probabilidade {
	private double probWums = 0.1;
	private double probPoco = 0.4;
	private double probOuro = 0.1;
	
	public boolean hasWump(int isWump) {
		if (isWump == 1) return false;
		
		Random i = new Random();
		
		if( i.nextFloat() < probWums) {
			return true;
		}
		
		return false;
	}
	
	public boolean temPoco() {
		Random i = new Random();
		
		if( i.nextFloat() < probPoco) {
			return true;
		}
		
		return false;
	}
	
	public boolean temOuro(int isOuro) {
		if (isOuro == 1) return false;
		
		Random i = new Random();
		
		if( i.nextFloat() < probOuro) {
			return true;
		}
		
		return false;
	}
}
