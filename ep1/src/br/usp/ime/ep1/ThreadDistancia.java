package br.usp.ime.ep1;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.ProgressDialog;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/*
 * Classe que retorna a distância  entre duas 
 * localidades utilizando a API Google Directions.
 */
public class ThreadDistancia extends Thread {
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
		}
		};
	private String origem;
	private String destino;
	private ProgressDialog dialog;
	private int distancia = 0;
	
	public ThreadDistancia(String origem, String destino, ProgressDialog dialog) {
		this.origem = origem;
		this.destino = destino;
		this.dialog = dialog;
	}
	
	@Override
	public void run() {
		this.setDistancia(this.getDistancia(origem, destino));
		handler.sendEmptyMessage(0);
		dialog.dismiss();
	}
	
	/**
	 * Retorna a distância em metros da origem até o destino.
	 * @param origem Coordenadas de origem (latitude,longitude)
	 * @param destino Coordenadas de destino (latitude,longitude)
	 * @return
	 */
	private int getDistancia(String origem, String destino) {
		//Consulta A API para pegar o JSON de resposta
		String url = "http://maps.googleapis.com/maps/api/directions/json?origin=" + origem +"&destination=" + destino + "&sensor=true";
		HttpClient c = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse response = null;
		String resultado = "";
		int distancia=0;
		try {
			response = c.execute(get);
		} catch (ClientProtocolException e) {
			Log.e("URL",e.getMessage());
		} catch (IOException e) {
			Log.e("URL",e.getMessage());
		}
		try {
			resultado = EntityUtils.toString(response.getEntity());
		} catch (ParseException e) {
			Log.e("URL",e.getMessage());
		} catch (IOException e) {
			Log.e("URL",e.getMessage());
		}
		File f = new File(Environment.getExternalStorageDirectory() + "/" + Util.pasta + "/" + "temp.json");
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
		try {
			FileUtils.write(f, resultado,"UTF-8");
		} catch (IOException e) {
			Log.e("FILE",e.getMessage());
		}
		
		//Pegar distancia do arquivo recebido
		JsonFactory jfactory = new JsonFactory();
		JsonParser jParser;
		try {
			jParser = jfactory.createJsonParser(new File(Environment.getExternalStorageDirectory() +"/"+ Util.pasta + "/", "temp.json"));
			int cont = 0;
			
			while (jParser.nextToken() != JsonToken.END_OBJECT) {
				String fieldname = jParser.getCurrentName();   		 
				if ("routes".equals(fieldname)) {
				  cont++;
				  jParser.nextToken(); 
				  
				  while (jParser.nextToken() != JsonToken.END_ARRAY) {
		              if ((!jParser.getText().equals("{"))&&(!jParser.getText().equals("}"))){
				    	  if (jParser.getText().equals("value")&&(cont==1)) {
							  jParser.nextToken();
				    		  distancia = jParser.getIntValue();
				    		  cont++;
						  } 
				      }
				  }
				}
			  }
			  jParser.close();
		} catch (JsonParseException e) {
			Log.e("JSON",e.getMessage());
		} catch (IOException e) {
			Log.e("JSON",e.getMessage());
		}
		return distancia;
	}

	public void setDistancia(int distancia) {
		this.distancia = distancia;
	}

	
	public int getDistancia() {
		return distancia;
	}
	
	
	
}
