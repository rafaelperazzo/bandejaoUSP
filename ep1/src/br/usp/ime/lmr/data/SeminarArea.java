package br.usp.ime.lmr.data;

import java.util.ArrayList;

/**
 * Enumeration que representa as áreas de seminário conhecidas pelo sistema.
 * @author Luiz Carlos Vieira
 * @version 1.0
 */
public enum SeminarArea {

	/**
	 * Área de seminário não definida.
	 */
	UNDEFINED (-1) {
		/**
		 * Obtém uma representação textual (string) da área de seminários
		 * representada pelo valor da enumeration.
		 * @return String com o texto representativo do valor da enumeration.
		 */
		@Override public String toString() {		
			return "<NÃO DEFINIDO>";
		}
	},

	/**
	 * Área de Combinatória Extremal e Métodos Probabilísticos.
	 */
	COMBINATORY (0) {
		/**
		 * Obtém uma representação textual (string) da área de seminários
		 * representada pelo valor da enumeration.
		 * @return String com o texto representativo do valor da enumeration.
		 */
		@Override public String toString() {		
			return "Combinatória Extremal e Métodos Probabilísticos";
		}
	},

	/**
	 * Área de Computação Musical.
	 */
	MUSIC (1) {
		/**
		 * Obtém uma representação textual (string) da área de seminários
		 * representada pelo valor da enumeration.
		 * @return String com o texto representativo do valor da enumeration.
		 */
		@Override public String toString() {		
			return "Computação Musical";
		}
	},

	/**
	 * Área de Otimização Contínua.
	 */
	OPTIMIZATION (2) {
		/**
		 * Obtém uma representação textual (string) da área de seminários
		 * representada pelo valor da enumeration.
		 * @return String com o texto representativo do valor da enumeration.
		 */
		@Override public String toString() {		
			return "Otimização Contínua";
		}
	},
	
	/**
	 * Área de Otimização Combinatória e Grafos.
	 */
	GRAPHS (3) {
		/**
		 * Obtém uma representação textual (string) da área de seminários
		 * representada pelo valor da enumeration.
		 * @return String com o texto representativo do valor da enumeration.
		 */
		@Override public String toString() {		
			return "Otimização Combinatória e Grafos";
		}
	},

	/**
	 * Área de Programação para GPGPU.
	 */
	GPGPU (4) {
		/**
		 * Obtém uma representação textual (string) da área de seminários
		 * representada pelo valor da enumeration.
		 * @return String com o texto representativo do valor da enumeration.
		 */
		@Override public String toString() {		
			return "Programação para GPGPU";
		}
	},

	/**
	 * Área de Teoria da Computação e Combinatória.
	 */
	COMPUTATION (5) {
		/**
		 * Obtém uma representação textual (string) da área de seminários
		 * representada pelo valor da enumeration.
		 * @return String com o texto representativo do valor da enumeration.
		 */
		@Override public String toString() {		
			return "Teoria da Computação e Combinatória";
		}
	},

	/**
	 * Área LIAMF.
	 */
	LIAMF (6) {
		/**
		 * Obtém uma representação textual (string) da área de seminários
		 * representada pelo valor da enumeration.
		 * @return String com o texto representativo do valor da enumeration.
		 */
		@Override public String toString() {		
			return "LIAMF";
		}
	}, 
	
	/**
	 * Define a área de seminário como bioinformática e visão computacional.
	 */
	BIOVISION (7) {
		/**
		 * Obtém uma representação textual (string) da área de seminários
		 * representada pelo valor da enumeration.
		 * @return String com o texto representativo do valor da enumeration.
		 */
		@Override public String toString() {		
			return "Bioinformática e Visão Computacional";
		}
	},
	
	
	/**
	 * Área de Sistemas de Software.
	 */
	SYSTEMS (8) {
		/**
		 * Obtém uma representação textual (string) da área de seminários
		 * representada pelo valor da enumeration.
		 * @return String com o texto representativo do valor da enumeration.
		 */
		@Override public String toString() {		
			return "Sistemas de Software";
		}
	},
	
	/**
	 * Área de Segurança de Dados.
	 */
	SECURITY (9) {
		/**
		 * Obtém uma representação textual (string) da área de seminários
		 * representada pelo valor da enumeration.
		 * @return String com o texto representativo do valor da enumeration.
		 */
		@Override public String toString() {		
			return "Segurança de Dados";
		}
	},

	/**
	 * Área de Martingais a Teroria da Confiabilidade.
	 */
	MARTIGALES (10) {
		/**
		 * Obtém uma representação textual (string) da área de seminários
		 * representada pelo valor da enumeration.
		 * @return String com o texto representativo do valor da enumeration.
		 */
		@Override public String toString() {		
			return "Martingais a Teroria da Confiabilidade";
		}
	},

	/**
	 * Área de Estabilidade de Órbitas e Aplicações.
	 */
	ORBITS (11) {
		/**
		 * Obtém uma representação textual (string) da área de seminários
		 * representada pelo valor da enumeration.
		 * @return String com o texto representativo do valor da enumeration.
		 */
		@Override public String toString() {		
			return "Estabilidade de Órbitas e Aplicações";
		}
	},

	/**
	 * Define a área de seminário como probabilidade e métodos estocásticos.
	 */
	LINE (12) {
		/**
		 * Obtém uma representação textual (string) da área de seminários
		 * representada pelo valor da enumeration.
		 * @return String com o texto representativo do valor da enumeration.
		 */
		@Override public String toString() {		
			return "Laboratório de Informática na Educação";
		}
	};

	/** Valor inteiro representativo do valor da enumeração. */
	private int m_iArea;
	
	/**
	 * Construtor da enumeração.
	 * @param iArea Valor inteiro representativo do valor da enumeração.
	 */
	private SeminarArea(int iArea) {
		m_iArea = iArea;
	}
	
	/**
	 * Obtém a representação numérica da área de seminários representada pelo
	 * valor da enumeration.
	 * @return Inteiro com a representação numérica do valor da enumeração.
	 */
	public int toInteger() {
		return m_iArea;
	}
	
	/**
	 * Obtém o valor da enumeration para o texto representativo dado.
	 * @param sValue String com o texto representativo da enumeration.
	 * @return Instância de SeminarArea com o valor da enumeration correspondente,
	 * ou SeminarArea.UNDEFINED se o texto não for reconhecido. 
	 */
	public static SeminarArea getEnumFromText(String sValue) {
		if(sValue.equals(COMBINATORY.toString()))
			return SeminarArea.COMBINATORY;
		else if(sValue.equals(MUSIC.toString()))
			return SeminarArea.MUSIC;
		else if(sValue.equals(OPTIMIZATION.toString()))
			return SeminarArea.OPTIMIZATION;
		else if(sValue.equals(GRAPHS.toString()))
			return SeminarArea.GRAPHS;
		else if(sValue.equals(GPGPU.toString()))
			return SeminarArea.GPGPU;
		else if(sValue.equals(COMPUTATION.toString()))
			return SeminarArea.COMPUTATION;
		else if(sValue.equals(LIAMF.toString()))
			return SeminarArea.LIAMF;
		else if(sValue.equals(BIOVISION.toString()))
			return SeminarArea.BIOVISION;
		else if(sValue.equals(SYSTEMS.toString()))
			return SeminarArea.SYSTEMS;
		else if(sValue.equals(SECURITY.toString()))
			return SeminarArea.SECURITY;
		else if(sValue.equals(MARTIGALES.toString()))
			return SeminarArea.MARTIGALES;
		else if(sValue.equals(ORBITS.toString()))
			return SeminarArea.ORBITS;
		else if(sValue.equals(LINE.toString()))
			return SeminarArea.LINE;
		else
			return SeminarArea.UNDEFINED;
	}
	
	/**
	 * Obtém o valor da enumeration para o número representativo dado.
	 * @param iValue Inteiro com o número representativo da enumeration.
	 * @return Instância de SeminarArea com o valor da enumeration correspondente,
	 * ou SeminarArea.UNDEFINED se o número não for reconhecido. 
	 */
	public static SeminarArea getEnumFromNumber(int iValue) {
		if(iValue == COMBINATORY.toInteger())
			return SeminarArea.COMBINATORY;
		else if(iValue == MUSIC.toInteger())
			return SeminarArea.MUSIC;
		else if(iValue == OPTIMIZATION.toInteger())
			return SeminarArea.OPTIMIZATION;
		else if(iValue == GRAPHS.toInteger())
			return SeminarArea.GRAPHS;
		else if(iValue == GPGPU.toInteger())
			return SeminarArea.GPGPU;
		else if(iValue == COMPUTATION.toInteger())
			return SeminarArea.COMPUTATION;
		else if(iValue == LIAMF.toInteger())
			return SeminarArea.LIAMF;
		else if(iValue == BIOVISION.toInteger())
			return SeminarArea.BIOVISION;
		else if(iValue == SYSTEMS.toInteger())
			return SeminarArea.SYSTEMS;
		else if(iValue == SECURITY.toInteger())
			return SeminarArea.SECURITY;
		else if(iValue == MARTIGALES.toInteger())
			return SeminarArea.MARTIGALES;
		else if(iValue == ORBITS.toInteger())
			return SeminarArea.ORBITS;
		else if(iValue == LINE.toInteger())
			return SeminarArea.LINE;
		else
			return SeminarArea.UNDEFINED;
	}
	
	/**
	 * Obtém uma lista com os nomes de todos as áreas.
	 * @return ArrayList<String> com os nomes de todas as áreas.
	 */
	public static ArrayList<String> getListOfValues() {
		ArrayList<String> lsAreas = new ArrayList<String>();
		lsAreas.add(COMBINATORY.toString());
		lsAreas.add(MUSIC.toString());
		lsAreas.add(OPTIMIZATION.toString());
		lsAreas.add(GRAPHS.toString());
		lsAreas.add(GPGPU.toString());
		lsAreas.add(COMPUTATION.toString());
		lsAreas.add(LIAMF.toString());
		lsAreas.add(BIOVISION.toString());
		lsAreas.add(SYSTEMS.toString());
		lsAreas.add(SECURITY.toString());
		lsAreas.add(MARTIGALES.toString());
		lsAreas.add(ORBITS.toString());
		lsAreas.add(LINE.toString());
		return lsAreas;
	}
}
