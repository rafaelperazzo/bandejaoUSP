package br.usp.ime.ep1;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;



import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

/**
 * Activity que representa a tela de settings da aplicação. 
 * Opção de mudar o servidor de comentários do bandejão.
 * @author rafaelperazzo
 *
 */
public class ConfiguracoesGerais extends Activity implements OnClickListener{
	EditText txtServidor;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuracoesgerais);
        Button btnSalvar = (Button)findViewById(R.id.btnSalvar);
        btnSalvar.setOnClickListener(this);
        Button btnCancelar = (Button)findViewById(R.id.btnCancelar);
        btnCancelar.setOnClickListener(this);
        txtServidor = (EditText) findViewById(R.id.txtServidor);
        File f = new File(Environment.getExternalStorageDirectory() +"/"+ Util.pasta + "/", "main.properties");
		try {
			InputStream is = new FileInputStream(f);
			Properties prop = new Properties();
			prop.load(is);
			this.txtServidor.setText(prop.getProperty("servidor",""));
			if (!this.txtServidor.getText().toString().equals("")) {
				Util.SERVIDOR = this.txtServidor.getText().toString();
			}
			is.close();
			
		} catch (FileNotFoundException e) {
			Log.e("FILE",e.getMessage());
		} catch (IOException e) {
			Log.e("FILE",e.getMessage());
		}
    }

	@Override
	public void onClick(View v) {
		
		if (v.getId()==R.id.btnCancelar) {
			this.finish();
		}
		else if (v.getId()==R.id.btnSalvar) {
			File f = new File(Environment.getExternalStorageDirectory() +"/"+ Util.pasta + "/", "main.properties");
			File f2 = new File(Environment.getExternalStorageDirectory() +"/"+ Util.pasta + "/", "main.properties.temp");
			try {
				OutputStream os = new FileOutputStream(f2,true);
				InputStream is = new FileInputStream(f);
				Properties prop = new Properties();
				prop.load(is);
				prop.setProperty("servidor", this.txtServidor.getText().toString());
				prop.store(os, "Salvo por Configurações gerais");
				os.close();
				is.close();
				f.delete();
				f2.renameTo(f);
				
			} catch (FileNotFoundException e) {
				Log.e("FILE",e.getMessage());
			} catch (IOException e) {
				Log.e("FILE",e.getMessage());
			}
			this.finish();
		}
		
	}
	
}
