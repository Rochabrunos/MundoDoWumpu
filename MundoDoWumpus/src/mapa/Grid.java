package mapa;

import reacoes.Percepcoes;

public class Grid {
	public Quadrado grid[][] = new Quadrado[4][4];
	
	
	public Grid () {
		while(!grid[0][0].restricao()) {
			geraGrid();
		}
		printMap();
	}
	// gera a matriz de espaços
	public void geraGrid() {
		
		for(int i = 0 ; i < 4 ; i++) {
			for( int j = 0 ; j < 4 ; j++) {
				grid[i][j] = null;
				grid[i][j] = new Quadrado(); 
			}
		}
		
		for(int i = 0 ; i < 4 ; i++) {
			for( int j = 0 ; j < 4 ; j++) {
				geraElementos(i, j);
			}
		}
	}
	//distribui os elementos sobre os quadrados
	public void geraElementos(int i, int j) {
		if( j == 3) { //up
			grid[i][j].setPercepcoes(3, true);
		}
		if (i == 3 ) { //right
			grid[i][j].setPercepcoes(4, true);
		}
		if (j == 0 ) { //bottom
			grid[i][j].setPercepcoes(5, true);
		}
		if( i == 0 ) { //left
			grid[i][j].setPercepcoes(6, true);
		}
		if((i == 0 && j == 0) || (i == 0 && j == 1)|| (i == 1 && j == 1)|| (i == 1 && j == 0)) return ;
		if ((!grid[i][j].getPercepcoes()[0] && !grid[i][j].getPercepcoes()[1]) ) {
			if(grid[i][j].geraPoco()) {
				System.out.println("POCO no quadrado " + i + " "+ j);
				definePercepcoes(i, j, 1, true);
			}
		} 
		if ((!grid[i][j].getPercepcoes()[0] && !grid[i][j].getPercepcoes()[1])) {
			if (grid[i][j].geraWump()) {
				System.out.println("WUMP no quadrado " + i + " "+ j);
				definePercepcoes(i, j, 0, true);
			}
		} 
		
		/*if(grid[i][j].geraOuro()) {
			System.out.println("ouro no quadrado " + i + " "+ j);
		}*/
				
	}
	//define a percepcao dos quadrados adjacentes
	public void definePercepcoes(int i , int j, int tipo, boolean valor) { //tipo[i] = fedor[1], brisa[2], resplendor[3], ....
		if( i > 0 ) { //quadrado anterior
			grid[i-1][j].setPercepcoes(tipo, valor);
		}
		if( i < 3 ) { //quadrado seguinte
			grid[i+1][j].setPercepcoes(tipo, valor);
		}
		if( j > 0 ) { //quadrado acima
			grid[i][j-1].setPercepcoes(tipo, valor);
		}
		if( j < 3 ) { //quadrado abaixo
			grid[i][j+1].setPercepcoes(tipo, valor);
		}
	}
	public boolean shoot(int i, int j) {
		return grid[i][j].shoot();
	}
	public void printMap() {
		for( int i = 0; i<4 ; i++) {
			for ( int j = 0; j<4 ;j++) {
			System.out.print(i + " " + j + " ");
		
			grid[i][j].printPercepcoes();
			System.out.print("\t");
			}
			System.out.println();
		}
	}
}
































