package aste;

import java.io.Serializable;

public class Risposta implements Serializable {
	public TipoRisposta tipoRisposta;
	public Object[] payload;
	
	private static final long serialVersionUID = -1993048461030574444L;

	public static enum TipoRisposta {
		OK,
		ERRORE
	}

	public static enum TipoErrore {
		RICHIESTA_INVALIDA,
		CAMPI_INVALIDI,
		OPERAZIONE_INVALIDA,
		GENERICO
	}
}
