package br.usp.ime.lmr.exceptions;

/**
 * Classe de exceção para indicar falha no acesso aos dados na planilha do Google Docs
 * @author Luiz Carlos Vieira
 * @version 1.0
 */
public class GDataAccessException extends Exception {
	/**
	 * Versão para serialização da execeção.
	 */
	private static final long serialVersionUID = 4868216385321608625L;

	/**
	 * Construtor padrão da classe.
	 */
	public GDataAccessException() {
		super();
	}
	
	/**
	 * Construtor para indicação de uma mensagem de texto específica.
	 * @param sMessage String com a mensagem específica para a exceção.
	 */
	public GDataAccessException(String sMessage) {
	   super(sMessage);
	}
}