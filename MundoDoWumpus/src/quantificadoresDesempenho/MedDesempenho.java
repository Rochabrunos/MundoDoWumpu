package quantificadoresDesempenho;

import java.util.HashMap;

//implementa a medida de desempenho do agente humano no mundo do humpus
public class MedDesempenho {
	private final HashMap<String, Integer> penalizacao = new HashMap<String, Integer>(){{
			put("mover", -1);
			put("atirar" , -10);
			put("morrer", -1000);
			put("coletar", 1000);	
	}};
	private int valor = 0;
	
	//obtem o valor individial de determinada acao
	
	public int desempenho(String acao) {
		return (valor+penalizacao.get(acao));
	}
	
	public void agir (String acao) {
		valor+=penalizacao.get(acao);
	}
}
