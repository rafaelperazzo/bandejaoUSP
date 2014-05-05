package br.usp.ime.ep1;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;


/**
 * Activity que abre as opções do Bandejão: Mostrar cardápio
 * @author Rafael Perazzo, Luiz Carlos e Maciel Caleb
 *
 */

@SuppressLint({ "HandlerLeak", "SimpleDateFormat" })
//public class bandeijao extends Activity implements OnItemSelectedListener,OnClickListener, LocationListener {
public class bandeijao extends Activity implements OnItemSelectedListener,OnClickListener {
	
	private ProgressDialog dialog;
	private LocationManager locationManager;
	private boolean podeMostraCardapio = false;
	Spinner spinner;
	
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.bandeijao);
	        spinner = (Spinner) findViewById(R.id.cmbRestaurante);
	        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
	                this, R.array.restaurantes_array, android.R.layout.simple_spinner_item);
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        spinner.setAdapter(adapter);
	        spinner.setOnItemSelectedListener(this);
	        //ImageButton cardapio = (ImageButton) findViewById(R.id.btnMostrarCardapio);
	        ImageView imgMostrarCardapio = (ImageView)findViewById(R.id.imgMostrarCardapio);
	        //cardapio.setOnClickListener(this);
	        imgMostrarCardapio.setOnClickListener(this);
	        spinner.setSelection(1);
	        //locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
	        //locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, this);
	        EditText cardapioDia = (EditText) findViewById(R.id.textCardapio);
	        
	        //TextView statusBar = (TextView) findViewById(R.id.txtStatus);
	        //cardapioDia.setKeyListener(null); 
	        cardapioDia.setFocusable(false);
	        Util u = new Util();
	        TextView statusBar = (TextView) findViewById(R.id.txtStatus);
	        statusBar.setBackgroundColor(Color.parseColor("#FF9900"));
	        //http://www.immigration-usa.com/html_colors.html
	        statusBar.setText("Verificando atualizações...");
	        if (Util.existeCache(Util.sNOMESBANDEIJAO[Util.POSICAO])) {
	        
	        	cardapioDia.setText(Util.getCardapioDia(u.carregarCardapioOffline(Util.restauranteOFFLINE)));
	        	
	        }
	                
	    }
	    
	    /**
	     * Cria o Optionsmenu da Activity
	     */
	    public boolean onCreateOptionsMenu(Menu menu) {
			 
		    MenuInflater inflater = getMenuInflater();
		    inflater.inflate(R.menu.menubandeijao, menu);
		    return true;
		}
		
	    
	    /**
	     * Método chamado quando um item do menu é chamado.
	     */
		public boolean onOptionsItemSelected(MenuItem item) {
			
			switch (item.getItemId()) {
		    case R.id.menurecarregar:
		    	mostraMensagem("A atualização está sendo realizada e você será avisado ao término");
	    		new Thread(new Runnable() {
	    			
	    			public void run() {
	    				
	    				File f = new File(Environment.getExternalStorageDirectory(),"ccentral.html");
	    		    	if (f.exists()) {
	    		    		f.delete();
	    		    	}
	    		    	Util u = new Util();
	    		    	if (u.possuiInternet(getApplicationContext())) {
	    		    		recarregar();
	    		    	}
	    		    	else {
	    		    		mostraMensagem("Você não está conectado a internet para realizar esta operação!");
	    		    	}
	    			}
	    		}).start();
		    	
		    	return true;
		    	    	
		    default:
		    return super.onOptionsItemSelected(item);
		}
		 
		}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Remove o listener para não ficar atualizando mesmo depois de sair
		//locationManager.removeUpdates(this);
	}
		
	@Override
	protected void onResume() {
		super.onResume();
		// Registra o listener
		//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Remove o listener
		//locationManager.removeUpdates(this);
	}
	
	
	
	/**
	 * Método que é chamado quando um item da spinner é chamado
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int pos,
			long id) {
		 //http://stackoverflow.com/questions/11140285/how-to-use-runonuithread
		final Util u = new Util(); 
		
		final int posicao = pos;
		Util.POSICAO = pos;
		Util.restauranteOFFLINE = Util.sNOMESBANDEIJAO[pos];
		TextView refeicao = (TextView) findViewById(R.id.txtRefeicao);
		SimpleDateFormat sdf = new SimpleDateFormat("H");
		
		int hora = Integer.parseInt(sdf.format(new Date()));
		if ((hora<=13)||(hora>=23)) {
			refeicao.setText("ALMOÇO");
		}
		else refeicao.setText("JANTAR");
		if (pos!=0) {
			
			
			new Thread(new Runnable() {
				
				TextView statusBar = (TextView) findViewById(R.id.txtStatus);
				EditText cardapioDia = (EditText) findViewById(R.id.textCardapio);
				public void run() {
					
					if (u.possuiInternet(getApplicationContext())) {
						if (u.atualizar()) { //Se precisar atualizar o cache offline
							mostraMensagem("Atualizando cache... Por favor aguarde...");
							Gravador rec = new Gravador();
							rec.gravar();
							Util.carregarCardapioOffline(posicao);
							mostraMensagem("Dados atualizados com sucesso!");
							podeMostraCardapio = true;
							Util.codigoStatus=1;
							
			                    runOnUiThread(new Runnable() {

			                        @Override
			                        public void run() {
			                        	statusBar.setText("Seus cardapios estao atualizados");
			                        	statusBar.setBackgroundColor(Color.parseColor("#003300"));
			                        	statusBar.setTextColor(Color.parseColor("#FFFFFF"));
			                        	cardapioDia.setText(Util.getCardapioDia(u.carregarCardapioOffline(Util.restauranteOFFLINE)));
			                        }
			                    });
			                  
						}
						else { //Se não precisa atualizar, pois o cache está atualizado
							Util.carregarCardapioOffline(posicao);
							podeMostraCardapio = true;
							Util.codigoStatus=2;
							
			                    runOnUiThread(new Runnable() {

			                        @Override
			                        public void run() {
			                        	
			                        	statusBar.setText("Seus cardapios estao atualizados");
			                        	statusBar.setBackgroundColor(Color.parseColor("#003300"));
			                        	statusBar.setTextColor(Color.parseColor("#FFFFFF"));
			                        	cardapioDia.setText(Util.getCardapioDia(u.carregarCardapioOffline(Util.restauranteOFFLINE)));
			                        	
			                        }
			                    });
			                 
							
						}
					} 
					else { //Se não possui Internet
						if (Util.existeCache(Util.sNOMESBANDEIJAO[Util.POSICAO])) { // e possui cache...
							if (u.atualizar()) { //desatualizado
								mostraMensagem("Sem conectividade!! O cardápio está desatualizado!");
								//Mostra o último cache salvo.
								Util.carregarCardapioOffline(posicao);
								podeMostraCardapio = true;
								Util.codigoStatus=3;
								
				                    runOnUiThread(new Runnable() {

				                        @Override
				                        public void run() {
				                        	
				                        	statusBar.setText("Seus cardapios estao DESATUALIZADOS");
				                        	statusBar.setBackgroundColor(Color.parseColor("#FF0000"));
				                        	statusBar.setTextColor(Color.parseColor("#FFFFFF"));
				                        	cardapioDia.setText(Util.getCardapioDia(u.carregarCardapioOffline(Util.restauranteOFFLINE)));
				                        }
				                    });
				                    
				                
							}
							else { //atualizado
								Util.carregarCardapioOffline(posicao);
								podeMostraCardapio = true;
								Util.codigoStatus=4;
								
				                    runOnUiThread(new Runnable() {

				                        @Override
				                        public void run() {
				                        	
				                        	statusBar.setText("Seus cardapios estao atualizados");
				                        	statusBar.setBackgroundColor(Color.parseColor("#003300"));
				                        	statusBar.setTextColor(Color.parseColor("#FFFFFF"));
				                        	cardapioDia.setText(Util.getCardapioDia(u.carregarCardapioOffline(Util.restauranteOFFLINE)));
				                        }
				                    });
				                
							}
						}
						else { //e não possui cache
							mostraMensagem("Sem conectividade!! Impossivel mostrar cardapio!");
							setpodeMostrarCardapio(false);
							
			                    runOnUiThread(new Runnable() {

			                        @Override
			                        public void run() {
			                        	
			                        	statusBar.setText("Sem conexão com a Internet!!");
			                        	statusBar.setBackgroundColor(Color.parseColor("#FF0000"));
			                        	statusBar.setTextColor(Color.parseColor("#FFFFFF"));
			                 
			                        }
			                    });
			                    
						}
					}
					
				}
				
	        
			}).start();
		}
	}
	
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		Util.restauranteOFFLINE = "ccentral.html";		
	}

	/**
	 * Método onClick dos botões Cardápio, comentários e Postar Comentários
	 * 
	 *  */
	@Override
	public void onClick(View v) {
		//EditText cardapioDia = (EditText) findViewById(R.id.textCardapio);
		//Util u = new Util();
		if (v.getId()==R.id.imgMostrarCardapio) {
			if (spinner.getSelectedItemPosition()!=0) {
				dialog = ProgressDialog.show(this,"Carregando o cardápio da semana", "Por favor aguarde....",true);
				new Thread(new Runnable() {
					public void run() {
						carregarCardapio();
					}
				}).start();
			}
			else {
				mostraMensagem("Selecione um bandejão primeiro!");
			}
		}
		
	}
	
	/**
	 * Método chamado quando a posição do usuário é modificada. O GPS 
	 * que envia esta informação.
	 */
	
	/*
	@Override
	public void onLocationChanged(Location location) {
		Util.LONGITUDE = location.getLongitude();
		Util.LATITUDE = location.getLatitude();
		Spinner spinner = (Spinner) findViewById(R.id.cmbRestaurante);
		int maisPerto = Util.getRestauranteMaisPerto(this);
		spinner.setSelection(maisPerto);
		
	}
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onProviderEnabled(String provider) {
		
		
	}
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	*/
	/**
	 * Método set que define se a Activity pode ou não mostrar o cardápio
	 * @param podeMostrar Verdadeiro ou falso
	 */
	private void setpodeMostrarCardapio(boolean podeMostrar) {
		this.podeMostraCardapio = podeMostrar;
	}
	
	
	/**
	 * Método que recupera a informação se o cardápio poderá ou não
	 * ser mostrado, baseado na existência de cache para o mesmo. 
	 * @return
	 */
	public boolean getPodeMostrarCardapio() {
		return (this.podeMostraCardapio);
	}
	
	private void recarregar() {
		this.runOnUiThread(new Runnable() {
	        public void run() {
	        	Util u = new Util();
	    		Gravador rec = new Gravador();
	    		rec.gravar();
	    		u.MostraMensagem(getApplicationContext(),"Dados atualizados com sucesso");
	    		EditText cardapioDia = (EditText) findViewById(R.id.textCardapio);
	    		cardapioDia.setText(Util.getCardapioDia(u.carregarCardapioOffline(Util.restauranteOFFLINE)));
	    		TextView statusBar = (TextView) findViewById(R.id.txtStatus);
	    		statusBar.setText("Seus cardapios estao atualizados");
            	statusBar.setBackgroundColor(Color.parseColor("#003300"));
	          }
	        });
		
	}
	
	public void mostraMensagem(String s) {
		final String msg = s;
		this.runOnUiThread(new Runnable() {
	        public void run() {
	        	Util u = new Util();
	    		u.MostraMensagem(getApplicationContext(),msg);
	          }
	        });
		
	}
	
		
	private void carregarCardapio() {
		this.runOnUiThread(new Runnable() {
			            public void run() {
			            	if (getPodeMostrarCardapio()) {
			            		Intent i = new Intent(getApplicationContext(),cardapio.class);
								startActivity(i);
								
			            	}
			            	else {
								Util u = new Util();
								u.MostraMensagem(getApplicationContext(), "Impossível mostrar cardápio!! Sem conectividade ou o cache ainda não foi atualizado!");
			            	}
			             	
			             }
			            });
		handler.sendEmptyMessage(0);
		dialog.dismiss();
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
		}
		};
}
