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
 * Thred que consulta, se houver, a coordenada do último carro da fila
 * @author Rafael Perazzo, Luiz Carlos e Maciel Caleb
 *
 */
public class ThreadFila extends Thread {
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
		}
		};
		private ProgressDialog dialog;
		String url;
		String arquivo;
		
		public ThreadFila(String url, String arquivo,ProgressDialog dialog) {
			this.dialog = dialog;
			this.url = url;
			this.arquivo = arquivo;
		}
		
	@Override
	public void run() {
		
		this.getFila(url, arquivo);
		handler.sendEmptyMessage(0);
		dialog.dismiss();
		
	}
	/**
	 * Método que grava em arquivo a posição do último carro na fila
	 * @param url URL informando qual o portão desejado
	 * @param arquivo Arquivo que será gravado localmente
	 */
	private void getFila(String url, String arquivo) {
		String resultado = "";
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
