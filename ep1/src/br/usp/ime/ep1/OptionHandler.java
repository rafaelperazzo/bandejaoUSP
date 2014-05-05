package br.usp.ime.ep1;

import java.util.ArrayList;
import java.util.Timer;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;
import br.usp.ime.lmr.data.DataCache;
import br.usp.ime.lmr.data.DataCacheUpdater;
import br.usp.ime.lmr.data.Seminar;
import br.usp.ime.lmr.data.SeminarArea;


/**
 * Classe auxiliar para execução de ações a partir da seleção em itens de listas.
 * @author Luiz Carlos Vieira
 * @version 1.0
 */
public class OptionHandler implements OnItemClickListener {

	/** Atividade com o contexto da aplicação. */
	private Activity m_spContext;
	
	/** Seminários filtrados para exibição. */
	private ArrayList<Seminar> m_lsSeminars;
	
	/**
	 * Construtor da classe para consultas gerais (em dados do cache).
	 * @param spContext Atividade com o contexto da aplicação.
	 */
	public OptionHandler(Activity spContext) {
		m_spContext  = spContext;
		m_lsSeminars = null;
	}
	
	/**
	 * Construtor da classe para consultas de seminários já filtrados.
	 * @param spContext Atividade com o contexto da aplicação.
	 * @param lsSeminars Lista de seminários para exibição e consulta.
	 */
	public OptionHandler(Activity spContext, ArrayList<Seminar> lsSeminars) {
		m_spContext  = spContext;
		m_lsSeminars = lsSeminars;
	}

	/**
	 * Evento de captura do clique em um item de uma lista.
	 * @param spParent View pai daquela em que o clique foi efetuado.
	 * @param spView View na qual o clique foi efetuado.
	 * @param iPosition Posição (de 0 a n) do item que foi selecionado no clique.
	 * @param lID Identificador do TextView utilizado para exibir o item selecionado.
	 */
	public void onItemClick(AdapterView<?> spParent, View spView, int iPosition, long lID) {
		
		/* Tratamento das opções principais da primeira tela */
		if(spParent.getId() == R.id.Options) {

			ArrayList<Seminar> lsSeminars;
			Bundle spParams;
			Intent spIntent;
			
			switch(iPosition) {
				case 0: // Seminários de Hoje					
			        lsSeminars = DataCache.getInstance().getTodaySeminars();
			        spParams = new Bundle();
			        
			        spParams.putSerializable("Seminars", lsSeminars);
					
			        spIntent = new Intent(m_spContext, DataActivity.class);
			        spIntent.putExtras(spParams);
			        m_spContext.startActivity(spIntent);
					break;

				case 1: // Seminários por Área
					ArrayList<String> lsAreas = SeminarArea.getListOfValues();
					spParams = new Bundle();
			        spParams.putSerializable("Areas", lsAreas);
					
			        spIntent = new Intent(m_spContext, AreaActivity.class);
			        spIntent.putExtras(spParams);
			        m_spContext.startActivity(spIntent);
					break;

				case 2: // Pesquisar Seminário
			        spIntent = new Intent(m_spContext, SearchActivity.class);
			        m_spContext.startActivity(spIntent);
					break;

				case 3: // Requisitar Atualização
					if(!DataCacheUpdater.isRunning()) { 
				        Timer spTimer = new Timer();
				        DataCacheUpdater spCacheUpdater = new DataCacheUpdater(m_spContext, true);
				        spTimer.schedule(spCacheUpdater, 0);
						m_spContext.runOnUiThread(new Runnable() {
						    public void run() {
								Toast spMessage = Toast.makeText(m_spContext, m_spContext.getString(R.string.seminars_update_requested), Toast.LENGTH_SHORT);
								spMessage.show();
						    }
						});				
					}
					else {
						m_spContext.runOnUiThread(new Runnable() {
						    public void run() {
								Toast spMessage = Toast.makeText(m_spContext, m_spContext.getString(R.string.seminars_update_ongoing), Toast.LENGTH_SHORT);
								spMessage.show();
						    }
						});				
					}
					break;
			}
			
		}

		/* Tratamento de listagens de seminários */
		else if(spParent.getId() == R.id.items) {

			// Sanity check
			if(m_lsSeminars == null) {
				Log.e(getClass().getName(), "Dados de seminários não disponíveis!");
				return;
			}
			
			Seminar spSeminar = m_lsSeminars.get(iPosition);
	        Bundle spParams = new Bundle();
	        spParams.putSerializable("Seminar", spSeminar);
			
	        Intent i = new Intent(m_spContext, SeminarActivity.class);
	        i.putExtras(spParams);
	        m_spContext.startActivity(i);
		}
		
		/* Tratamento das listagens de áreas */
		else if(spParent.getId() == R.id.seminar_areas) {
			ArrayList<Seminar> lsSeminars;
			Bundle spParams;
			Intent spIntent;
			
			SeminarArea eArea = SeminarArea.getEnumFromNumber(iPosition);
	        lsSeminars = DataCache.getInstance().getSeminars(eArea);
	        spParams = new Bundle();
	        
	        spParams.putSerializable("Seminars", lsSeminars);
			
	        spIntent = new Intent(m_spContext, DataActivity.class);
	        spIntent.putExtras(spParams);
	        m_spContext.startActivity(spIntent);			
		}
	}
}
