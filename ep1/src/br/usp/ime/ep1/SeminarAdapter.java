package br.usp.ime.ep1;

import java.text.SimpleDateFormat;
import java.util.ArrayList;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import br.usp.ime.lmr.data.Seminar;


/**
 * Adaptador para fácil exibição de seminários em um ListView.
 * @author Luiz Carlos Vieira
 * @version 1.0
 */
public class SeminarAdapter extends ArrayAdapter<Seminar> {

	/** Lista de seminários para serem exibidos. */
	private ArrayList<Seminar> m_lsSeminars;
	
	/** View para exibição dos dados de cada item da lista. */
	private ViewHolder m_spViewHolder;
	
	/** Contexto da aplicação. */
	private Context m_spContext;
	
	/**
	 * Innerclasse que representa os dados exibidos para cada item da lista.
	 * @author Luiz Carlos Vieira
	 * @version 1.0
	 */
	private class ViewHolder {
        
		/** Título do item. */
		TextView title;
        
		/** Subtítulo do item */
        TextView subtitle; 
    }

	/**
	 * Construtor da classe.
	 * @param spContext Contexto da aplicação.
	 * @param iTextViewResID Identificador do TextView utilizado para exibir os itens da lista.
	 * @param lsItems ArrayList com os seminários a serem exibidos.
	 */
    public SeminarAdapter(Context spContext, int iTextViewResID, ArrayList<Seminar> lsItems) {
        super(spContext, iTextViewResID, lsItems);
        m_lsSeminars = lsItems;
        m_spContext = spContext;
    }

    /**
     * Evento de acesso aos itens da lista.
     * @param iPos Inteiro com a posição (de 0 a n) do item desejado.
     * @param spConvertView View do item (quando já criada).
     * @param spParent View da lista, pai do item sendo solicitado.
     */
    @Override
    public View getView(int iPos, View spConvertView, ViewGroup spParent) {
        View v = spConvertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) m_spContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.data_item, null);
            m_spViewHolder = new ViewHolder();
            m_spViewHolder.title = (TextView) v.findViewById(R.id.title);
            m_spViewHolder.subtitle = (TextView) v.findViewById(R.id.subtitle);
            v.setTag(m_spViewHolder);
        }
        else
        	m_spViewHolder = (ViewHolder) v.getTag(); 

        Seminar spSeminar = m_lsSeminars.get(iPos);

        if (spSeminar != null) {
        	m_spViewHolder.title.setText(spSeminar.getTitle());
        	SimpleDateFormat df = new SimpleDateFormat("EEE, dd/MM/yyyy HH:mm");
        	m_spViewHolder.subtitle.setText(df.format(spSeminar.getDateTime()));
        }

        return v;
    }	
}
