package br.usp.ime.ep1;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import br.usp.ime.lmr.data.DataCache;
import br.usp.ime.lmr.data.DataCacheUpdater;
import br.usp.ime.lmr.data.UpdateListener;


/**
 * Classe principal da aplicação de Seminários
 * MAC0463/5743 - Computação Móvel - EP 1
 * @author Luiz Carlos Vieira
 * @version 1.0 
 */
public class SemidroidActivity extends Activity implements UpdateListener {

	/** Timer para a atualização periódica do cache local. */
	private Timer m_spTimer;
	
	/** Tarefa para a atualização periódica do cache local. */
	private DataCacheUpdater m_spCacheUpdater;
	
	/** Monitor do estado da bateria. */
	private BatteryMonitor m_spMon;

    /**
     * Método chamado quando a atividade é criada.
     * @param savedInstanceState Instância de Bundle com os dados de estado da atividade. 
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.semidroid);
        
        // Cria o cache de dados
        DataCache spCache = DataCache.getInstance(this);
        spCache.registerListener(this);

        // Atualiza os campos da tela inicial
        Date dtLastUpdate = spCache.getLastUpdate();
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String sLastUpdate = dtLastUpdate != null ? df.format(dtLastUpdate) : "Nunca";
        
        TextView spHeader = (TextView) findViewById(R.id.Header);
        spHeader.setText("Seminários IME - Atualização: " + sLastUpdate);
        
        ListView spOptions = (ListView) findViewById(R.id.Options);
        ArrayAdapter<String> spAdapter = new ArrayAdapter<String>(this, R.layout.option_item, getResources().getStringArray(R.array.seminar_options));
        spOptions.setAdapter(spAdapter);
        spOptions.setTextFilterEnabled(true);
        spOptions.setOnItemClickListener(new OptionHandler(this));
        
        // Registra para ser notificado a respeito das atualizações da bateria
        m_spMon = new BatteryMonitor(this);
        registerReceiver(m_spMon, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }  

    /**
     * Configura o timer o intervalo de tempo da atualização automática dos dados do cache.
     * @param iMinutes Inteiro com o número de minutos para a atualização.
     * O número mínimo permitido é 15 minutos.
     */
    public void setupBackgroundUpdate(int iMinutes) {
    	if(m_spCacheUpdater != null)
    		m_spCacheUpdater.cancel();
    	if(m_spTimer != null)
    		m_spTimer.cancel();

    	if(iMinutes < 15)
    		iMinutes = 15;
    	
        m_spTimer = new Timer();
        m_spCacheUpdater = new DataCacheUpdater(this);
        
        m_spTimer.scheduleAtFixedRate(m_spCacheUpdater, 0, iMinutes * 60000);
    }
    
    /**
     * Evento de encerramento da atividade.
     */
    @Override
    protected void onDestroy() {
		m_spCacheUpdater.cancel();
		m_spTimer.cancel();
		unregisterReceiver(m_spMon);
    	super.onDestroy();
    }

    /**
     * Captura o evento de falha na atualização do cache local.
     */
	public void onDataUpdateFailed() {
		Toast spMessage = Toast.makeText(this, getString(R.string.error_updating_seminars), Toast.LENGTH_SHORT);
		spMessage.show();
	}

	/**
	 * Captura o evento de atualização do cache local.
	 * @param dtUpdate Data da última atualização. 
	 */
	public void onDataUpdated(Date dtUpdate) {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String sLastUpdate = dtUpdate != null ? df.format(dtUpdate) : "Nunca";

		Log.d(getClass().getName(), "**** Data atualização: " + sLastUpdate);
        
        TextView spHeader = (TextView) findViewById(R.id.Header);
        spHeader.setText("Seminários IME - Atualização: " + sLastUpdate);
        
		Toast spMessage = Toast.makeText(this, getString(R.string.seminars_updated), Toast.LENGTH_SHORT);
		spMessage.show();
	}
}