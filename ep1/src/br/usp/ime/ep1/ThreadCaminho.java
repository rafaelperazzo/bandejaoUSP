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

/**
 * Classe que representa uma thread para preparar um caminho de uma determinada localização
 * até o portão desejado.
 * @author Rafael Perazzo, Luiz Carlos e Maciel Caleb
 *
 */
public class ThreadCaminho extends Thread {

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
		}
		};
	private ProgressDialog dialog;
	private String arquivo;
	private int portao;
	private String origem;
	
	public ThreadCaminho(String arquivo, int portao, String origem, ProgressDialog dialog) {
		this.dialog = dialog;
		this.arquivo = arquivo;
		this.portao = portao;
		this.origem = origem;
	}
	
	@Override
	public void run() {
		this.preparaCaminhoJSON(arquivo, portao, origem);
		handler.sendEmptyMessage(0);
		dialog.dismiss();
	}
	
	/**
	 * Prepara o arquivo com o caminho da origem até o portão desejado
	 * @param arquivo Arquivo a ser gerado
	 * @param portao Portão destino
	 * @param origem Localização origem (final da fila)
	 */
	private void preparaCaminhoJSON(String arquivo, int portao, String origem) {
		String resultado = "";
		String destino = "";
		
		if (portao==1) {
			destino = "-23.565529,-46.711979";
		}
		if (portao==2) {
			destino = "-23.550696,-46.732289";
		}
		if (portao==3) {
			destino = "-23.569175,-46.741318";
		}
		String url = "http://maps.googleapis.com/maps/api/directions/json?origin=" + origem +"&destination=" + destino + "&sensor=true";
		HttpClient c = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse response = null;
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
		try {
			FileUtils.write(f, resultado,"UTF-8");
		} catch (IOException e) {
			Log.e("FILE",e.getMessage());
		}
	}
}
