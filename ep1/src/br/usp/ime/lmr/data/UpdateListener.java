package br.usp.ime.lmr.data;

import java.util.Date;

/**
 * Interface para recebimento de eventos de atualização dos dados de seminários.
 * @author Luiz Carlos Vieira
 * @version 1.0
 */
public interface UpdateListener {

	/**
	 * Evento indicativo de falha na atualização dos dados de seminários para o cache.
	 */
	public void onDataUpdateFailed();
	
	/**
	 * Evento indicativo de uma nova atualização dos dados de seminários para o cache.
	 * @param dtUpdate Data e hora da atualização.
	 */
	public void onDataUpdated(Date dtUpdate);
}
