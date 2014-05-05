package br.usp.ime.ep1;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Monitor da quantidade de bateria disponível no dispositivo.
 * @author Luiz Carlos Vieira
 * @version 1.0
 */
public class BatteryMonitor extends BroadcastReceiver {

	/** Nível de carga da bateria, entre 0% e 100%. */
	private int m_iBatteryLevel;
	
	/**
	 * Contexto da aplicação.
	 */
	private SemidroidActivity m_spContext;

	/**
	 * Construtor default da classe.
	 * @param spContext Contexto da aplicação.
	 */
	public BatteryMonitor(SemidroidActivity spContext) {
		m_iBatteryLevel = 100;
		m_spContext = spContext;
	}
	
	/**
	 * Getter do nível da bateria.
	 * @return Nível de carga da bateria, entre 0% e 100%.
	 */
	public int getBatteryLevel() {
		return m_iBatteryLevel;
	}
	
	/**
	 * Evento de alteração do estado da bateria do dispositivo.
	 * @param spContext Contexto da aplicação.
	 * @param spIntent Intent chamado pelo SO ao produzir o evento, incluindo os parâmetros da bateria.
	 */
	@Override
	public void onReceive(Context spContext, Intent spIntent) {
		m_iBatteryLevel = spIntent.getIntExtra("level", 0);
		
		/* Calcula os minutos para a atualização em função do nível atual da bateria.
			f(x) = -0.75x + 90
			Bateria	  Minutos
			  100	    15
			   80	    30
			   60	    45
			   40	    60
			   20	    75
			    0	    90
		*/
		
		final int iMinutes = (int) (-0.75 * m_iBatteryLevel + 90);
		m_spContext.setupBackgroundUpdate(iMinutes);
		
		m_spContext.runOnUiThread(new Runnable() {
		    public void run() {
				Toast spMessage = Toast.makeText(m_spContext, m_spContext.getString(R.string.background_setup, m_iBatteryLevel, iMinutes), Toast.LENGTH_LONG);
				spMessage.show();
		    }
		});
	}

}
