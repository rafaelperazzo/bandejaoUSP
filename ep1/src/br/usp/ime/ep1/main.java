package br.usp.ime.ep1;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

/**
 * Dashboard da aplicação com os botões para as fucncionalidades desenvolvidas
 * @author Rafael Perazzo, Luiz Carlos e Maciel Caleb
 *
 */
public class main extends Activity implements OnClickListener, Runnable{
    
	
	private ProgressDialog dialog;
	
	
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button button = (Button)findViewById(R.id.btnBandeijao);
        button.setOnClickListener(this);
        Button btnOndeEstou = (Button)findViewById(R.id.btnOndeEstou);
        Button btnSemidroid = (Button)findViewById(R.id.btnSemidroid);
        ImageView imgBandejao = (ImageView)findViewById(R.id.imgBandejao);
        btnSemidroid.setOnClickListener(this);
        btnOndeEstou.setOnClickListener(this);
        imgBandejao.setOnClickListener(this);
        Util.iniciar(this);
    }

	@Override
	public void onClick(View v) {
		final View comp = v;
		if (v.getId()==R.id.imgBandejao) {
			dialog = ProgressDialog.show(this,"Carregando a aplicação", "Por favor aguarde....",true);
			new Thread(new Runnable() {
		        public void run() {
		        	comp.post(new Runnable() {
			            public void run() {
			            	try {	
				    			Intent i = new Intent(getApplicationContext(),bandeijao.class);
				    			startActivity(i);
				    		} 
				    		catch (Exception e) { 
				    			Log.e("THREAD", e.getMessage());
				    		}
				    		handler.sendEmptyMessage(0);
				    		dialog.dismiss();
			             	
			             }
			            });
		        	
		        }
		    }).start();

			
		}
		else if (v.getId()==R.id.btnOndeEstou) {
			Util u = new Util();
			if (u.possuiInternet(getApplicationContext())) {
				dialog = ProgressDialog.show(this,"Carregando a aplicação", "Por favor aguarde....",true);
				Thread thread = new Thread(this);
				thread.start();
			}
			else {
				u.MostraMensagem(this, "A aplicação de mapas necessita de conectividade para funcionar!",5);
			}			
		}
		else if(v.getId()==R.id.btnSemidroid) {
			Intent i = new Intent(getApplicationContext(),SemidroidActivity.class);
			startActivity(i);
		}
		
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.optionsmenu, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.menusair:
	            this.finish();
	            return true;
	        case R.id.menusobre:
	        {
	        	Intent i = new Intent(this,Sobre.class);
				startActivity(i);
				return true;
	        }
	        
	        case R.id.menuconfig:
	        {
	        	Intent i = new Intent(this,ConfiguracoesGerais.class);
				startActivity(i);
	        }
	        
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
		}
		};


	@Override
	public void run() {
		
		try {	
			Intent i = new Intent(this,OndeEstou.class);
			startActivity(i);
		} 
		catch (Exception e) { 
			Log.e("THREAD", e.getMessage());
		}
		handler.sendEmptyMessage(0);
		dialog.dismiss();
		
	}
    
}