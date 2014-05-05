package br.usp.ime.ep1;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

/**
 * Activity que representa a tela para envio de comentário ao Bandejão
 * @author Rafael Perazzo, Luiz Carlos e Maciel Caleb
 *
 */
public class PostarComentario extends Activity implements OnClickListener,OnItemSelectedListener{
	ProgressDialog dialog;
	Button postarComentario;
	Spinner comboFila;
	EditText txtComentarioPostado;
	
	
	/**
	 * Método inicia o spinner com as opções de tamanho de fila
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postarcomentarios);
        this.postarComentario = (Button) findViewById(R.id.btnPostarComentario);
        txtComentarioPostado = (EditText) findViewById(R.id.txtComentarioPostado);
        postarComentario.setOnClickListener(this);
        this.comboFila = (Spinner) findViewById(R.id.cmbTamanhoFila);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.fila_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        comboFila.setAdapter(adapter);
        comboFila.setOnItemSelectedListener(this);
    }
	
	/**
	 * OnClick do botão com as opções de cancelar e de enviar comentário escrito
	 */
	@Override
	public void onClick(View v) {
		
		if (v.getId()==R.id.btnPostarComentario) {
			//dialog = ProgressDialog.show(this,"Enviando comentário...", "Por favor aguarde....",true);
			final String comentario = this.txtComentarioPostado.getText().toString();
        	final String tamanhoFila = comboFila.getSelectedItem().toString();
        	final View comp = v;
			new Thread(new Runnable() {
		        public void run() {
		        	
		        	try {
		        		HttpClient client = new DefaultHttpClient();
		           		HttpPost post = new HttpPost(Util.SERVIDOR + Util.NOMESBANDEIJAO[Util.POSICAO]);
		            	List<NameValuePair> pairs = new ArrayList<NameValuePair>();
		            	pairs.add(new BasicNameValuePair("comentario.texto", comentario));
		            	pairs.add(new BasicNameValuePair("comentario.fila", tamanhoFila));
		        		post.setEntity(new UrlEncodedFormEntity(pairs));
						client.execute(post);
					} catch (UnsupportedEncodingException e1) {
						
						Log.e("POST", e1.getMessage());
					} catch (ClientProtocolException e) {
						
						Log.e("POST", e.getMessage());
					} catch (IOException e) {
						
						Log.e("POST", e.getMessage());
					}
					
					
					comp.post(new Runnable() {
			            public void run() {
			            	finish();
			            	Util u = new Util();
			             	u.MostraMensagem(getApplicationContext(), "Comentário postado com sucesso!");
			               	Intent i = new Intent(getApplicationContext(),bandeijao.class);
				    		startActivity(i);
				    		finish();
			                }
			            });
							
		        }
		    }).start();
		}
		
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
		
	}
	

}
