package br.usp.ime.ep1;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.TextView;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/**
 * Activity que mostra os comentários do bandejão escolhido
 * @author rafaelperazzo
 *
 */
public class Comentarios extends Activity {
	

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comentarios);
        TextView comentarios = (TextView)findViewById(R.id.txtComentarios);
        Util u = new Util();
        TextView titulo = (TextView)findViewById(R.id.txtTitulo);
        titulo.setText(titulo.getText() + " " + Util.NOMES[Util.POSICAO]);
        if (Util.existeCache(Util.BANDEIJAO + ".json")) { //Existe cache ?
			//Mostra o último cache
        	comentarios.setText(this.getComentarios(Util.BANDEIJAO + ".json"));
        	
		}
		else {
			u.MostraMensagem(this, "Sem conexão com a internet: Impossível mostrar comentários!");
		}
    }
	
	public String getComentarios(String arquivo) {
    	String name = "";
    	
    	try {
    			JsonFactory jfactory = new JsonFactory();
    			JsonParser jParser = jfactory.createJsonParser(new File(Environment.getExternalStorageDirectory() +"/"+ Util.pasta + "/", arquivo));
    			String anterior = "-";
    			while (jParser.nextToken() != JsonToken.END_OBJECT) {
    		 
    				String fieldname = jParser.getCurrentName();   		 
    				if ("list".equals(fieldname)) {
    		 
    				  jParser.nextToken();
    		 
    				  while (jParser.nextToken() != JsonToken.END_ARRAY) {
    		 
    		                     
    				      if ((!jParser.getText().equals("{"))&&(!jParser.getText().equals("}"))){
    				    	  if (jParser.getText().equals("texto")) {
        						  name = name + "Comentário: ";
        						  anterior = "comentario";
        					  }
    				    	  else if (jParser.getText().equals("hora")) {
    				    		  name = name + "Hora: ";
    				    		  anterior = "hora";
        					  }
    				    	  else if (jParser.getText().equals("fila")) {
        						  name = name + "Tamanho da Fila: ";
        						  anterior = "tamanho";
        					  }
    				    	  else {
    				    		  if (anterior.equals("hora")) {
    	    				    	  SimpleDateFormat formato1 = new SimpleDateFormat("ss-mm-HH-dd-MM-yy");  
    	    				    	  SimpleDateFormat formato2 = new SimpleDateFormat("dd/MM/yyyy - HH:mm");  
    	    				    	  String data = jParser.getText(); //26072007 para 26/07/2007  
    	    				    	  try {
										name = name + formato2.format(formato1.parse(data)) + "\n";
										anterior = "-";
									} catch (ParseException e) {
										Log.e("ERRO", e.getMessage());
									}
    	    				      } 
    				    		  else if(anterior.equals("tamanho")) {
    	    				    	  name = name + jParser.getText() + "\n";
        				    		  anterior = "-";
    	    				      	}
    	    				      else if (anterior.equals("comentario")) {
    	    				    	  name = name + jParser.getText() + "\n";
        				    		  anterior = "-";
    	    				      }
    				    		  
    				    	  } 
    				      }
    				      if (jParser.getText().equals("}")) {
				    		  name = name + "\n";
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
    		 
    		      Log.e("JSON", e.getMessage());
    		 
    		     }
    		    
    	     return name;
    	}
}
