package br.usp.ime.ep1;

import java.util.ArrayList;



import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import br.usp.ime.lmr.data.Seminar;


/**
 * Atividade de exibição da tela de itens em uma lista selecionável.
 * @author Luiz Carlos Vieira
 * @version 1.0
 */
public class DataActivity extends Activity {
    /**
     * Método chamado quando a atividade é criada.
     * @param savedInstanceState Instância de Bundle com os dados de estado da atividade. 
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.data);
        
        @SuppressWarnings("unchecked")
		ArrayList<Seminar> lsSeminars = (ArrayList<Seminar>) getIntent().getExtras().getSerializable("Seminars");

        ListView spSeminars = (ListView) findViewById(R.id.items);
        spSeminars.setTextFilterEnabled(true);
        
        if(lsSeminars.size() == 0) {
	        ArrayAdapter<String> spAdapter = new ArrayAdapter<String>(this, R.layout.option_item);
	        spSeminars.setAdapter(spAdapter);
	        spAdapter.add(getString(R.string.no_seminar));
        }
        else
        {
	        spSeminars.setOnItemClickListener(new OptionHandler(this, lsSeminars));
			
	        SeminarAdapter spAdapter = new SeminarAdapter(this, R.layout.data_item, lsSeminars);
	        spSeminars.setAdapter(spAdapter);
        }
    }
}