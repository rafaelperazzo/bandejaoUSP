package br.usp.ime.ep1;



import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
/**
 * Activity que representa o cardápio da semana do restaurante selecionado
 * @author Rafael Perazzo, Luiz Carlos, Maciel Caleb
 *
 */
public class cardapio extends Activity {
	WebView webview;
	Util u;
	
	/**
	 * Abre uma webview para mostrar o cardápio selecionado
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cardapio);
        webview = (WebView) findViewById(R.id.webviewcardapio);
        webview.getSettings().setJavaScriptEnabled(true);
        u = new Util();
        if (Util.POSICAO>5) {
        	String header = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
    		webview.loadData(header + u.getTabela(u.carregarCardapioOffline(Util.restauranteOFFLINE)), "text/html; charset=UTF-8",null);
        }
        else {
        	String header = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>";
    		webview.loadData(header + u.getTabela(u.carregarCardapioOffline(Util.restauranteOFFLINE)), "text/html; charset=ISO-8859-1",null);
        }
        
    }
}
