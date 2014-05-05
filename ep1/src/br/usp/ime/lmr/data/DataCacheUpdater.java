package br.usp.ime.lmr.data;

import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import br.usp.ime.lmr.exceptions.GDataAccessException;
import br.usp.ime.ep1.R;

/**
 * Thread de atualização automática (e transparente para o usuário) dos dados
 * de seminários em cache na base de dados local.
 * @author Luiz Carlos Vieira
 * @version 1.0
 */
public class DataCacheUpdater extends TimerTask {

	/** Contexto para obtenção de informações do sistema. */
	private Activity m_spContext;
	
	/** Indicação sobre a execução da terefa de atualização estar ou não em andamento. */
	private static boolean m_bRunning = false; 
	
	/** Indica se deve ou não avisar (com um Toast) quando a atualização não foi necessária. */
	private boolean m_bWarnUpdateUnnecessary;
	
	/**
	 * Construtor da classe.
	 * @param spContext Contexto para obtenção de informações do sistema.
	 */
	public DataCacheUpdater(Activity spContext) {
		m_spContext = spContext;
		m_bWarnUpdateUnnecessary = false;
	}

	/**
	 * Construtor da classe que permite indicar para avisar o usuário sobre a atualização não ser necessária.
	 * @param spContext Contexto para obtenção de informações do sistema.
	 */
	public DataCacheUpdater(Activity spContext, boolean bWarnUpdateUnnecessary) {
		m_spContext = spContext;
		m_bWarnUpdateUnnecessary = bWarnUpdateUnnecessary;
	}
	
	/**
	 * Indica se a aplicação tem conexão à Internet disponível.
	 * @return Verdadeiro se a conexão à Internet está disponível, e falso caso contrário.
	 */
	public boolean isNetworkAvailable() {
	    ConnectivityManager spConnMgr = (ConnectivityManager) m_spContext.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo spNetInfo = spConnMgr.getActiveNetworkInfo();
	    return spNetInfo != null && spNetInfo.isAvailable() && spNetInfo.isConnected();
	}

	/**
	 * Indica se há uma terefa de atualização da base de dados local em andamento.
	 * @return Verdadeiro se há uma tarefa em andamento e falso caso contrário.
	 */
	public static boolean isRunning() {
		return m_bRunning;
	}
	
	/**
	 * Execução da atualização
	 */
	@Override
	public void run() {
		if(isNetworkAvailable())
		{
			if(m_bRunning) // Não permite que duas tarefas de atualização executem ao mesmo tempo
				return;
			
			m_bRunning = true;
			
			boolean bUpdated = false;
			try {
				bUpdated = DataCache.getInstance().updateFromGoogleDocs();
			} catch (GDataAccessException e) {
				m_bRunning = false; 
				return;
			}
			
			if(!bUpdated && m_bWarnUpdateUnnecessary) {
				m_spContext.runOnUiThread(new Runnable() {
				    public void run() {
						Toast spMessage = Toast.makeText(m_spContext, m_spContext.getString(R.string.seminars_update_unnecessary), Toast.LENGTH_SHORT);
						spMessage.show();
				    }
				});				
			}
			
			m_bRunning = false;
		}
		else {
			m_spContext.runOnUiThread(new Runnable() {
			    public void run() {
					Toast spMessage = Toast.makeText(m_spContext, m_spContext.getString(R.string.connection_not_available), Toast.LENGTH_LONG);
					spMessage.show();
			    }
			});
		}
	}
}
