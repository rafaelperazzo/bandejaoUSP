package br.usp.ime.ep1;



import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.webkit.WebView;
import android.widget.TextView;
import br.usp.ime.lmr.data.Seminar;


/**
 * Atividade de exibição da tela com os dados de um seminário selecionado.
 * @author Luiz Carlos Vieira
 * @version 1.0
 */
public class SeminarActivity extends Activity {
    /**
     * Método chamado quando a atividade é criada.
     * @param savedInstanceState Instância de Bundle com os dados de estado da atividade. 
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seminar);
        
		Seminar spSeminar = (Seminar) getIntent().getExtras().getSerializable("Seminar");
        
        TextView spView = (TextView) findViewById(R.id.s_title);
        spView.setText(spSeminar.getTitle());
        
        spView = (TextView) findViewById(R.id.s_area);
        spView.setText(spSeminar.getArea().toString());

        spView = (TextView) findViewById(R.id.s_datetime);
        spView.setText(spSeminar.getDateTimeAsTimestamp());
        
        spView = (TextView) findViewById(R.id.s_location);
        spView.setText(spSeminar.getLocation());
        
        spView = (TextView) findViewById(R.id.s_panelist);
        spView.setText(spSeminar.getPanelist());

        spView = (TextView) findViewById(R.id.s_responsible);
        spView.setText(spSeminar.getResponsible());
        
        WebView spWebView = (WebView) findViewById(R.id.s_abstract);
        String sData = "<html><head></head><body style='text-align:justify;color:gray;background-color:black;'>";
 		sData += spSeminar.getAbstract();
 		sData += "</body></html>";
 		try {
			String sB64Data = Base64.encodeToString(sData.getBytes("latin1"), Base64.DEFAULT);
			spWebView.loadData(sB64Data, "text/html; charset=latin1", "base64");
		} catch (Exception e) {
			spWebView.loadData(sData, "text/html", null);
		}
        
    }
}
