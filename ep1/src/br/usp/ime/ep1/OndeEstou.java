package br.usp.ime.ep1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.csvreader.CsvReader;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;
import com.google.android.maps.OverlayItem;

/**
 * Esta classe implementa a Activity que mostra a posição do usuário(baseado nas coordenadas GPS)
 * dentro de um mapa Google. Oferece as opções de ver os pontos de interesse,
 * linhas de circulares, fila dos portões e envio de coordenada para compartilhar
 * a posição dentro da fila de um dos portões, que é selecionado automaticamente, 
 * de acordo com a proximidade.
 * @author Rafael Perazzo, Luiz Carlos e Maciel Caleb
 *
 */
public class OndeEstou extends MapActivity implements LocationListener{
	
	private MapView mapa;
	private LocationManager locationManager;
	private MapController controlador;
	private MyLocationOverlay ondeEstou;
	private ProgressDialog dialog;
	
	/**
	 * Este método faz a inicialização da Activity, como preparar o mapa com os overlays,
	 * ativar o GPS, e preparar o optionsMenu. 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.ondeestou);
	    mapa = (MapView) findViewById(R.id.mapa);
	    mapa.setBuiltInZoomControls(true);
	    mapa.invalidate();
	    controlador = mapa.getController();
	    controlador.setZoom(16);
	    
	    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0, 0, this);
		// Centraliza o mapa na última localização conhecida
		Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		// Se existe última localização converte para GeoPoint
		if (loc != null) {
			GeoPoint ponto = new GeoPoint((int)(Util.LATITUDE*1E6),(int)(Util.LONGITUDE*1E6));
			controlador.setCenter(ponto);
		}
		mapa.getController().animateTo(new GeoPoint(-23560722,-46735286));
		ondeEstou = new MyLocationOverlay(this, mapa);
		mapa.getOverlays().add(ondeEstou);
		mapa.setClickable(true);
	    
	    this.preparaOverlays();
}
	/**
	 * Ativa o options menu
	 */
	public boolean onCreateOptionsMenu(Menu menu) {
		 
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menuondeestou, menu);
	    return true;
	}
	
	/**
	 * Implementa as ações a serem realizadas ao clicar sobre um item de menu particular.
	 * As opções são: Limpar mapa, Mapa do circular 1, Mapa do Circular 2,
	 * redesenhar overlays, Fila p1, Fila p2, Fila p3 e Enviar Coordenada de final de fila.
	 */
	@SuppressLint("SimpleDateFormat")
	public boolean onOptionsItemSelected(MenuItem item) {
		Util u = new Util();
		switch (item.getItemId()) {
	    case R.id.menulimpar:
	    	mapa.invalidate();
	    	mapa.getOverlays().clear();
	    	return true;
	    case R.id.menucircular1:
	    	mapa.invalidate();
	    	mapa.getOverlays().clear();
	    	this.preparaOverlays();
	    	this.desenharCaminho("linha1.csv",Color.BLUE,2);
	    	return true;
	    
	    case R.id.menucircular2:
	    	mapa.invalidate();
	    	mapa.getOverlays().clear();
	    	this.preparaOverlays();
	    	this.desenharCaminho("linha2.csv",Color.RED,2);
	    	return true;
	    
	    case R.id.menuredesenhar:
	    	mapa.invalidate();
	    	mapa.getOverlays().clear();
	    	this.preparaOverlays();
	    	return true;
	    
	    case R.id.menup1: {
	    	dialog = ProgressDialog.show(this,"Buscando dados", "Por favor aguarde....",true);
	    	ThreadFila tf = new ThreadFila("http://www.ime.usp.br/~perazzo/getdados.php?portao=1", "p1.txt",dialog);
	    	tf.start();
	    	try {
				tf.join();
			} catch (InterruptedException e) {
				Log.e("THREAD", e.getMessage());
			}
	    	String origem = this.getCSVinfo("p1.txt");
	    	if (!origem.equals("")) {
	    		dialog = ProgressDialog.show(this,"Preparando dados", "Por favor aguarde....",true);
	    		ThreadCaminho tc = new ThreadCaminho("p1_path.json", 1,origem,dialog);
	    		tc.start();
	    		try {
					tc.join();
				} catch (InterruptedException e) {
					Log.e("THREAD",e.getMessage());
				}
		    	this.preparaCaminhoCSV("p1_path.json","p1.csv");
		    	u.MostraMensagem(this, String.valueOf(Util.DISTANCIA) + " metros");
		    	mapa.invalidate();
		    	mapa.getOverlays().clear();
		    	this.preparaOverlays();
		    	ImagemOverlay carro = new ImagemOverlay(new GeoPoint(this.getCSVInfo("p1.txt", 0), this.getCSVInfo("p1.txt", 1)),R.drawable.carro,"Último Carro da Fila",this);
		    	mapa.getOverlays().add(carro);
	    	
	    		//dialog = ProgressDialog.show(this,"Buscando dados", "Por favor aguarde....");
	    		//this.preparaPortao(1);
	    		if (Util.DISTANCIA>100) {
		    		this.desenharFila("p1.csv",Color.RED,2);
		    	}
		    	else {
		    		this.desenharFila("p1.csv",Color.GREEN,2);
		    	}
	    	
	    	}
	    	else {
	    		u.MostraMensagem(getApplicationContext(), "Não existem informações sobre a fila neste portão!");
	    	}
	    	return true;
	    }
	    case R.id.menup2: {
	    	dialog = ProgressDialog.show(this,"Buscando dados", "Por favor aguarde....",true);
	    	ThreadFila tf = new ThreadFila("http://www.ime.usp.br/~perazzo/getdados.php?portao=2", "p2.txt",dialog);
	    	tf.start();
	    	try {
				tf.join();
			} catch (InterruptedException e) {
				Log.e("THREAD", e.getMessage());
			}
	    	
	    	String origem = this.getCSVinfo("p2.txt");
	    	if (!origem.equals("")) {
	    		dialog = ProgressDialog.show(this,"Preparando dados", "Por favor aguarde....",true);
	    		ThreadCaminho tc = new ThreadCaminho("p2_path.json", 2,origem,dialog);
	    		tc.start();
	    		try {
					tc.join();
				} catch (InterruptedException e) {
					Log.e("THREAD",e.getMessage());
				}
	    		
		    	this.preparaCaminhoCSV("p2_path.json","p2.csv");
		    	mapa.invalidate();
		    	mapa.getOverlays().clear();
		    	this.preparaOverlays();
		    	ImagemOverlay carro = new ImagemOverlay(new GeoPoint(this.getCSVInfo("p2.txt", 0), this.getCSVInfo("p2.txt", 1)),R.drawable.carro,"Último Carro da Fila",this);
		    	mapa.getOverlays().add(carro);
		    	u.MostraMensagem(this, String.valueOf(Util.DISTANCIA) + " metros");
		    	
	    		//dialog = ProgressDialog.show(this,"Buscando dados", "Por favor aguarde....");
	    		//this.preparaPortao(2);
		    	if (Util.DISTANCIA>100) {
		    		this.desenharFila("p2.csv",Color.RED,2);
		    	}
		    	else {
		    		this.desenharFila("p2.csv",Color.GREEN,2);
		    	}
	    	}
	    	else {
	    		u.MostraMensagem(getApplicationContext(), "Não existem informações sobre a fila neste portão!");
	    	}
	    	return true;
	    }
	    
	    case R.id.menup3: {
	    	dialog = ProgressDialog.show(this,"Buscando dados", "Por favor aguarde....",true);
	    	ThreadFila tf = new ThreadFila("http://www.ime.usp.br/~perazzo/getdados.php?portao=3", "p3.txt",dialog);
	    	tf.start();
	    	try {
				tf.join();
			} catch (InterruptedException e) {
				Log.e("THREAD", e.getMessage());
			}
	    	
	    	String origem = this.getCSVinfo("p3.txt");
	    	if (!origem.equals("")) {
	    		dialog = ProgressDialog.show(this,"Preparando dados", "Por favor aguarde....",true);
	    		ThreadCaminho tc = new ThreadCaminho("p3_path.json", 3,origem,dialog);
	    		tc.start();
	    		try {
					tc.join();
				} catch (InterruptedException e) {
					Log.e("THREAD",e.getMessage());
				}
		    	this.preparaCaminhoCSV("p3_path.json","p3.csv");
		    	mapa.invalidate();
		    	mapa.getOverlays().clear();
		    	this.preparaOverlays();
		    	ImagemOverlay carro = new ImagemOverlay(new GeoPoint(this.getCSVInfo("p3.txt", 0), this.getCSVInfo("p3.txt", 1)),R.drawable.carro,"Último Carro da Fila",this);
		    	mapa.getOverlays().add(carro);
		    	u.MostraMensagem(this, String.valueOf(Util.DISTANCIA) + " metros");
		    	
	    		//dialog = ProgressDialog.show(this,"Buscando dados", "Por favor aguarde....");
	    		//this.preparaPortao(3);
	    		if (Util.DISTANCIA>100) {
		    		this.desenharFila("p3.csv",Color.RED,2);
		    	}
		    	else {
		    		this.desenharFila("p3.csv",Color.GREEN,2);
		    	}
	    	}
	    	else {
	    		u.MostraMensagem(getApplicationContext(), "Não existem informações sobre a fila neste portão!");
	    	}
	    	return true;
	    }
	    
	    case R.id.menuenviarlocalizacao: {
	    	String latitude = String.valueOf(Util.LATITUDE);
	    	String longitude = String.valueOf(Util.LONGITUDE);
	    	String origem = latitude + "," + longitude;
	    	String portao = "1";
	    	int distancia;
	    	Properties prop = Util.getProperties(this, "main.properties");
	    	String destino = prop.getProperty("latitude_p1") + "," + prop.getProperty("longitude_p1");
	    	//**************
	    	dialog = ProgressDialog.show(this,"Enviando dados", "Por favor aguarde....",true);
	    	if (u.possuiInternet(getApplicationContext())) {
	    		ThreadDistancia tr = new ThreadDistancia(origem, destino, dialog);
	    		tr.start();
	    		try {
					tr.join();
				} catch (InterruptedException e) {
					Log.e("THREAD", e.getMessage());
				}
	    		distancia = tr.getDistancia();
	    		destino = prop.getProperty("latitude_p2") + "," + prop.getProperty("longitude_p2");
	    		ThreadDistancia tr2 = new ThreadDistancia(origem, destino, dialog);
	    		tr2.start();
	    		try {
					tr2.join();
				} catch (InterruptedException e) {
					Log.e("THREAD", e.getMessage());
				}
	    		
	    		if (distancia>tr2.getDistancia()) {
	    			portao = "2";
	    			distancia = tr2.getDistancia();
	    		}
	    		destino = prop.getProperty("latitude_p3") + "," + prop.getProperty("longitude_p3");
	    		ThreadDistancia tr3 = new ThreadDistancia(origem, destino, dialog);
	    		tr3.start();
	    		try {
					tr3.join();
				} catch (InterruptedException e) {
					Log.e("THREAD", e.getMessage());
				}
	    		if (distancia>tr3.getDistancia()) {
	    			portao = "3";
	    			distancia = tr3.getDistancia();
	    		}
	    	
	    		Util.PORTAO_MAIS_PROXIMO = portao;
	    		SimpleDateFormat formatador = new SimpleDateFormat("HH:mm");
	    		String hora = formatador.format(new Date());
	    		if (distancia<1700) {
	    			this.enviarLocalizacao(latitude, longitude, hora, portao);
	    		}
	    		else {
	    			u.MostraMensagem(this, "Você está mesmo na fila do portão ?");
	    		}
	    	}
	    	else {
	    		u.MostraMensagem(this, "Você está sem conectividade!!!");
	    	}
	    	return true;
	    }
	    	
	    case R.id.tamanhodafila: {
	    	u.MostraMensagem(this, String.valueOf(Util.DISTANCIA) + " metros");
	    	return true;
	    }
	    
	    default:
	    return super.onOptionsItemSelected(item);
	}
	 
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// Registra o listener
		ondeEstou.enableMyLocation();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// Remove o listener
		ondeEstou.disableMyLocation();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// Remove o listener para não ficar atualizando mesmo depois de sair
		locationManager.removeUpdates(this);
	}
	
	@Override
	protected boolean isRouteDisplayed() {
		
		return false;
	}

	
	/**
	 * Método que atualiza as coordenadas a cada nova informação fornecida
	 * pelo GPS
	 */
	@Override
	public void onLocationChanged(Location location) {
		
		Util.LONGITUDE = location.getLongitude();
		Util.LATITUDE = location.getLatitude();
		GeoPoint meuLocal = new GeoPoint((int)(Util.LATITUDE*1E6),(int)(Util.LONGITUDE*1E6));
		controlador.animateTo(meuLocal);
		mapa.invalidate();
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		
		
	}
	
	/**
	 * Desenha um caminho atraves de um arquivo CSV de coordenadas obtidos atraves
	 * do Google Maps
	 * @param arquivo Nome do arquivo CSV
	 * @param cor Cor da Linha
	 * @param espessura Espessura da linha
	 */
	public void desenharCaminho(String arquivo, int cor, int espessura) {
		Resources resources = this.getResources();
    	AssetManager assetManager = resources.getAssets(); 
    	try {
    	    InputStream inputStream = assetManager.open(arquivo);
    	    File file = new File(Environment.getExternalStorageDirectory() + "/" + Util.pasta + "/", arquivo);
    	    if (!file.exists()) {
    	    	file.createNewFile();
    	    }
    	    else {
    	    	file.delete();
    	    }
    	    OutputStream os = new FileOutputStream(file);
    	    //Novo - ****
    	    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
    	    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
    	    //****
    	    String read;
    	    while ((read = in.readLine()) != null) {
    			out.write(read + "\r\n");
    		}
    		
    		out.flush();
    		out.close();
    		in.close();
    		inputStream.close();
    		os.close();
    		
    		//Lendo o CSV
    		CsvReader coordenadas = new CsvReader(Environment.getExternalStorageDirectory() + "/" + Util.pasta + "/" + arquivo);
    		coordenadas.setDelimiter(',');
			coordenadas.readHeaders();
			coordenadas.readRecord();
			int latitude1 =Integer.parseInt(coordenadas.get("latitude")) ;
			int longitude1 = Integer.parseInt(coordenadas.get("longitude"));
			int latitude2;
			int longitude2;
			OverlayPath caminho;
			
			while (coordenadas.readRecord())
			{
				latitude2 =Integer.parseInt(coordenadas.get("latitude")) ;
				longitude2 = Integer.parseInt(coordenadas.get("longitude"));
				caminho = new OverlayPath(new GeoPoint(latitude1,longitude1),new GeoPoint(latitude2,longitude2),cor,espessura);
				mapa.getOverlays().add(caminho);
				latitude1 = latitude2;
				longitude1 = longitude2;
			}
	
			coordenadas.close();
			
		} catch (FileNotFoundException e) {
			Log.e("ERRO CSV", e.getMessage());
		} catch (IOException e) {
			Log.e("ERRO IOEXCEPTION", e.getMessage());
		}
    		    		
	}
	
	/**
	 * Carrega um arquivo CSV com overlays no mapa
	 * @param arquivo Nome do arquivo CSV
	 * @param imagem Icone de Imagem do overlay
	 */
	private void carregarOverlays(String arquivo, Drawable imagem) {
		
		//Gravando Cache
		Util.csv2arquivoCache(this,arquivo);
		
		//Lendo o CSV
		CsvReader coordenadas = null;
		try {
			coordenadas = new CsvReader(Environment.getExternalStorageDirectory() + "/" + Util.pasta + "/" + arquivo);
			coordenadas.setDelimiter(',');
			coordenadas.readHeaders();
			int latitude;
			int longitude;
			String codigo;
			String descricao;
			List<OverlayItem> pontos = new ArrayList<OverlayItem>();
			while (coordenadas.readRecord())
			{
				latitude =Integer.parseInt(coordenadas.get("latitude")) ;
				longitude = Integer.parseInt(coordenadas.get("longitude"));
				codigo = coordenadas.get("codigo");
				descricao = coordenadas.get("descricao");
				descricao = Util.converterString(descricao);
				pontos.add(new OverlayItem(new GeoPoint(latitude, longitude),codigo, descricao));
			}
			coordenadas.close();
			ImagensOverlay pontosOverlay = new ImagensOverlay(this,pontos,imagem);
			mapa.getOverlays().add(pontosOverlay);
		} catch (FileNotFoundException e) {
			Log.e("CSV:","Arquivo não encontrado: " + e.getMessage());
		} catch (IOException e) {
			Log.e("CARROVERLAYS:",e.getMessage());
		}
		
	}
	
	/**
	 * Método que prepara e insere todos os overlays definidos. 
	 */
	private void preparaOverlays() {
		// Define a imagem de cada overlay
		Drawable imagem = getResources().getDrawable(R.drawable.ponto);
		Drawable hospital = getResources().getDrawable(R.drawable.hospital);
		//Drawable livro = getResources().getDrawable(R.drawable.livro);
		Drawable restaurante = getResources().getDrawable(R.drawable.restaurante);
		Drawable onibus = getResources().getDrawable(R.drawable.onibus);
		Drawable portaria = getResources().getDrawable(R.drawable.portaria);
		Drawable posto = getResources().getDrawable(R.drawable.posto);
		Drawable policia = getResources().getDrawable(R.drawable.policia);
		
		this.carregarOverlays("pontos.csv", imagem);
		this.carregarOverlays("hospitais.csv", hospital);
		this.carregarOverlays("paradas.csv", onibus);
		this.carregarOverlays("portarias.csv", portaria);
		this.carregarOverlays("restaurantes.csv", restaurante);
		this.carregarOverlays("postos.csv", posto);
		this.carregarOverlays("seguranca.csv", policia);
	}
	
	/**
	 * Método que prepara um arquivo CSV com as coordenadas do caminho do final da fila
	 * informado até a coordenada do Portão mais próximo.
	 * @param arquivoJSON Arquivo JSON obtivo através da API Google Directions
	 * @param arquivo Arquivo CSV gerado a partir do JSON.
	 */
	private void preparaCaminhoCSV(String arquivoJSON, String arquivo) {
    	
    	try {
    			
    			File f = new File(Environment.getExternalStorageDirectory() + "/" + Util.pasta + "/" + arquivo);
    			if (!f.exists()) {
    				try {
    					f.createNewFile();
    				} catch (IOException e) {
    					Log.e("FILE",e.getMessage());
    				}
    			}
    			else {
    				f.delete();
    			}
    			FileUtils.write(f, "latitude,longitude\n",true);
    			int cont=0; //Conta apenas a primeira ocorrencia da distância
    			int cont2 = 0; //Descarta as duas primeiras coordenadas
    			int cont3 = 0;
    			String latitudeLongitudeInicio = "";
    			String latitudeLongitudeFim = "";
    			JsonFactory jfactory = new JsonFactory();
    			JsonParser jParser = jfactory.createJsonParser(new File(Environment.getExternalStorageDirectory() +"/"+ Util.pasta + "/", arquivoJSON));
    			// loop until token equal to "}"
    			while (jParser.nextToken() != JsonToken.END_OBJECT) {
    				String fieldname = jParser.getCurrentName();   		 
    				if ("routes".equals(fieldname)) {
    				  cont++;
    				  jParser.nextToken(); // current token is "[", move next
    				  // messages is array, loop until token equal to "]"
    				  while (jParser.nextToken() != JsonToken.END_ARRAY) {
    		              if ((!jParser.getText().equals("{"))&&(!jParser.getText().equals("}"))){
    				    	  if (jParser.getText().equals("value")&&(cont==1)) {
        						  jParser.nextToken();
    				    		  Util.DISTANCIA = jParser.getIntValue();
    				    		  cont++;
        					  }
    				    	  else if (jParser.getText().equals("lat")) {
    				    		  cont2++;
    				    		  if (cont2>4) {
    				    			  jParser.nextToken();
        				    		  DecimalFormat formatador = new DecimalFormat("0");
        				    		  String n = formatador.format(jParser.getDoubleValue()*1000000);
        				    		  if ((cont2%2)==1) {
        				    			  latitudeLongitudeFim = latitudeLongitudeFim + n + ",";
        				    		  }
        				    		  else {
        				    			  //FileUtils.write(f, String.valueOf(n) + ",",true);
        				    			  latitudeLongitudeInicio = latitudeLongitudeInicio + n + ",";
        				    		  }
        			
    				    		  }
    				    		  		    		  
    				    		  
        					  }
    				    	  else if (jParser.getText().equals("lng")) {
    				    		  cont3++;
    				    		  if (cont3>4) {
    				    			  jParser.nextToken();
        				    		  DecimalFormat formatador = new DecimalFormat("0");
        				    		  String n = formatador.format(jParser.getDoubleValue()*1000000);
        				    		  if ((cont3%2)==1) {
        				    			  latitudeLongitudeFim = latitudeLongitudeFim + n + "\n";
        				    		  }
        				    		  else {
        				    			  latitudeLongitudeInicio = latitudeLongitudeInicio + n + "\n";
        				    			  FileUtils.write(f, String.valueOf(latitudeLongitudeInicio),true);
        				    			  FileUtils.write(f, String.valueOf(latitudeLongitudeFim),true);
            				    		  latitudeLongitudeFim = "";
            				    		  latitudeLongitudeInicio = "";
        				    		  }
        				    		  
    				    		  }
    				    		  	  						  
        					  }
    				    	  
    				      }
    				      
    				  }
    		 
    				}
    		 
    			  }
    			  jParser.close();
    		 
    		     } catch (JsonGenerationException e) {
    		 
    			  Log.e("JSON", e.getMessage());
    		 
    		     } catch (JsonParseException e) {
    		 
    		      Log.e("JSON", e.getMessage());
    		 
    		     } catch (IOException e) {
    		 
    		      Log.e("ARQUIVO", e.getMessage());
    		 
    		     }
    	}
	
	/**
	 * Método que desenha a fila do portão, a partir do arquivo CSV informado
	 * @param arquivo Arquivo CSV com as coordenadas do caminho
	 * @param cor Cor do caminho
	 * @param espessura Espessura da linha
	 */
	private void desenharFila(String arquivo, int cor, int espessura) {
		//Lendo o CSV
		CsvReader coordenadas = null;
		try {
			coordenadas = new CsvReader(Environment.getExternalStorageDirectory() + "/" + Util.pasta + "/" + arquivo);
			coordenadas.setDelimiter(',');
			coordenadas.readHeaders();
     		coordenadas.readRecord();
     		int latitude1 =Integer.parseInt(coordenadas.get("latitude")) ;
    		int longitude1 = Integer.parseInt(coordenadas.get("longitude"));
    		int latitude2;
    		int longitude2;
    		OverlayPath caminho;
    		
    		while (coordenadas.readRecord())
    		{
    			latitude2 =Integer.parseInt(coordenadas.get("latitude")) ;
    			longitude2 = Integer.parseInt(coordenadas.get("longitude"));
    			caminho = new OverlayPath(new GeoPoint(latitude1,longitude1),new GeoPoint(latitude2,longitude2),cor,espessura);
    			mapa.getOverlays().add(caminho);
    			ImagemOverlay carro = new ImagemOverlay(new GeoPoint(latitude1,longitude1),R.drawable.carro,"Último Carro da Fila",this);
    			ImagemOverlay carro2 = new ImagemOverlay(new GeoPoint(latitude2,longitude2),R.drawable.carro,"Último Carro da Fila",this);
		    	mapa.getOverlays().add(carro);
		    	mapa.getOverlays().add(carro2);
    			latitude1 = latitude2;
    			longitude1 = longitude2;
    		}
		} catch (FileNotFoundException e) {
			Log.e("FILA", e.getMessage());
		} catch (IOException e) {
			Log.e("FILA", e.getMessage());
		}
		coordenadas.close();
	}
	
	/**
	 * Método que devolve as coordenadas no formato latitude,longitude
	 * @param arquivo Arquivo CSV
	 * @return Coordenadas no formato latitude,longitude
	 */
	private String getCSVinfo(String arquivo) {
		//Lendo o CSV
		CsvReader coordenadas = null;
		String resultado = "";
		try {
			coordenadas = new CsvReader(Environment.getExternalStorageDirectory() + "/" + Util.pasta + "/" + arquivo);
			coordenadas.setDelimiter(',');
			coordenadas.readHeaders();
     		boolean possuiInformacao = coordenadas.readRecord();
     		if (possuiInformacao) {
     			String latitude =coordenadas.get("latitude") ;
        		String longitude = coordenadas.get("longitude");
        		resultado = latitude + "," + longitude; 
     		}
     		else {
     			return ("");
     		}
     		    			
		} catch (FileNotFoundException e) {
			Log.e("CSV", e.getMessage());
		} catch (IOException e) {
			Log.e("CSV", e.getMessage());
		}
		coordenadas.close();
		return resultado;
	}
	
	/**
	 * Metodo que devolve uma informação, latitude ou longitude no formato E6
	 * @param arquivo Arquivo CSV com as informações
	 * @param info Longitude (1) ou latitude(0)
	 * @return A informação solicitada.
	 */
	private int getCSVInfo(String arquivo, int info) {
		//Lendo o CSV
		CsvReader coordenadas = null;
		int resultado = -1;
		try {
			coordenadas = new CsvReader(Environment.getExternalStorageDirectory() + "/" + Util.pasta + "/" + arquivo);
			coordenadas.setDelimiter(',');
			coordenadas.readHeaders();
     		boolean possuiInformacao = coordenadas.readRecord();
     		if (possuiInformacao) {
     			if (info==0) {
     				double latitude = Double.parseDouble((coordenadas.get("latitude")));
     				return (int)(latitude*1000000);
     			}
     			else {
     				double longitude = Double.parseDouble(coordenadas.get("longitude"));
     				return (int)(longitude*1000000);
     			}
        		
     		}
     		    			
		} catch (FileNotFoundException e) {
			Log.e("CSV", e.getMessage());
		} catch (IOException e) {
			Log.e("CSV", e.getMessage());
		}
		coordenadas.close();
		return resultado;
	}
	
	/**
	 * Metodo que envia a localização do usuário para o servidor
	 * @param latitude Latitude
	 * @param longitude Longitude
	 * @param hora Hora enviada
	 * @param portao Qual portão
	 */
	private void enviarLocalizacao(String latitude, String longitude, String hora, String portao) {
		
		try {
			HttpClient c = new DefaultHttpClient();
			HttpGet get = new HttpGet("http://www.ime.usp.br/~perazzo/gravar.php?portao=" + portao + "&hora=" + hora + "&latitude=" + latitude + "&longitude=" + longitude);
			c.execute(get);
		} catch (ClientProtocolException e) {
			Log.e("URL",e.getMessage());
		} catch (IOException e) {
			Log.e("URL",e.getMessage());
		}
		catch (Exception e) {
			Log.e("URL",e.getMessage());
		}
	}
	
}

