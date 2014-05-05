package br.usp.ime.ep1;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;


/**
 * Atividade de exibição da tela de seleção de áreas de seminário para pesquisa.
 * @author Luiz Carlos Vieira
 * @version 1.0
 */
public class AreaActivity extends Activity {
    /**
     * Método chamado quando a atividade é criada.
     * @param savedInstanceState Instância de Bundle com os dados de estado da atividade. 
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.area);
        
		@SuppressWarnings("unchecked")
		ArrayList<String> lsAreas = (ArrayList<String>) getIntent().getExtras().getSerializable("Areas");
        
        ListView spAreas = (ListView) findViewById(R.id.seminar_areas);
        ArrayAdapter<String> spAdapter = new ArrayAdapter<String>(this, R.layout.option_item, lsAreas);
        spAreas.setAdapter(spAdapter);
        spAreas.setTextFilterEnabled(true);
        spAreas.setOnItemClickListener(new OptionHandler(this));
    }
}
