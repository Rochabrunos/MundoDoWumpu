package database;

public class Informacao {
	public boolean hasWump; //salva o estado se existe os nao wump
	public boolean hasPoco; //salav o estado se existe ou nao poco
	
	/*define a informacoes como false */
	public Informacao() {
		hasWump = false;
		hasPoco = false;
	}
	//define que há wump no quadrado
	public void isWump() {
		hasWump = true;
	}
	//define que há poco no quadrado
	public void isPoco() {
		hasPoco = true;
	}
	//pergunta se há wump no quadrado
	public boolean hasWump() {
		return hasWump;
	}
	//pergunta se há poco no quadrado
	public boolean hasPoco() {
		return hasPoco;
	}
	
	public boolean condicoes() {
		return hasPoco || hasWump;
	}
}
