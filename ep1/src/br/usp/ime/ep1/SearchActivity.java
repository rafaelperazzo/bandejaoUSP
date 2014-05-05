package br.usp.ime.ep1;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;



import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import br.usp.ime.lmr.data.DataCache;
import br.usp.ime.lmr.data.Seminar;
import br.usp.ime.lmr.data.SeminarArea;


/**
 * Atividade da tela de busca de seminários. 
 * @author Luiz Carlos Vieira
 * @version 1.0
 */
public class SearchActivity extends Activity {
	
	/** Constante para o diálogo de seleção da data inicial. */
	private static final int START_DATE_DIALOG_ID = 0;
	
	/** Constante para o diálogo de seleção da hora inicial. */
	private static final int START_TIME_DIALOG_ID = 1;	

	/** Constante para o diálogo de seleção da data final. */
	private static final int END_DATE_DIALOG_ID = 2;
	
	/** Constante para o diálogo de seleção da hora final. */
	private static final int END_TIME_DIALOG_ID = 3;	
	
	/** Dia inicial da pesquisa. */
	private int m_iStartDay;
	
	/** Mês inicial da pesquisa. */
	private int m_iStartMonth;
	
	/** Ano inicial da pesquisa. */
	private int m_iStartYear;

	/** Hora inicial da pesquisa. */
	private int m_iStartHour;
	
	/** Minuto inicial da pesquisa. */
	private int m_iStartMinute;

	/** Dia final da pesquisa. */
	private int m_iEndDay;
	
	/** Mês final da pesquisa. */
	private int m_iEndMonth;
	
	/** Ano final da pesquisa. */
	private int m_iEndYear;

	/** Hora final da pesquisa. */
	private int m_iEndHour;
	
	/** Minuto final da pesquisa. */
	private int m_iEndMinute;
	
	/** Evento de atualização da data inicial. */
	private DatePickerDialog.OnDateSetListener m_onStartDateChanged;
	
	/** Evento de atualização da hora inicial. */
	private TimePickerDialog.OnTimeSetListener m_onStartTimeChanged;
	
	/** Evento de atualização da data final. */
	private DatePickerDialog.OnDateSetListener m_onEndDateChanged;

	/** Evento de atualização da hora final. */
	private TimePickerDialog.OnTimeSetListener m_onEndTimeChanged;	
	
	/**
     * Método chamado quando a atividade é criada.
     * @param savedInstanceState Instância de Bundle com os dados de estado da atividade. 
     * */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        
        m_iStartDay    = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        m_iStartMonth  = Calendar.getInstance().get(Calendar.MONTH);
        m_iStartYear   = Calendar.getInstance().get(Calendar.YEAR);
        m_iStartHour   = 0;
        m_iStartMinute = 0;

        m_iEndDay       = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        m_iEndMonth     = Calendar.getInstance().get(Calendar.MONTH);
        m_iEndYear      = Calendar.getInstance().get(Calendar.YEAR);
        m_iEndHour      = 23;
        m_iEndMinute    = 59;
        
        updateDateTimeFields();
        
        Spinner spAreas = (Spinner) findViewById(R.id.search_area);
        ArrayList<String> lsAreas = SeminarArea.getListOfValues();
        lsAreas.add(0, getString(R.string.search_all_areas));
        ArrayAdapter<String> spAdapter = new ArrayAdapter<String>(this, R.layout.area_item, lsAreas);
        spAreas.setAdapter(spAdapter);
        spAreas.setSelection(0);
        
        Button spGo = (Button) findViewById(R.id.search_go);
        spGo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                search();
            }
        });
        
        Button spStartDate = (Button) findViewById(R.id.search_start_date);
        spStartDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(START_DATE_DIALOG_ID);
            }
        });
        
        Button spStartTime = (Button) findViewById(R.id.search_start_time);
        spStartTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(START_TIME_DIALOG_ID);
            }
        });

        Button spEndDate = (Button) findViewById(R.id.search_end_date);
        spEndDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(END_DATE_DIALOG_ID);
            }
        });
        
        Button spEndTime = (Button) findViewById(R.id.search_end_time);
        spEndTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                showDialog(END_TIME_DIALOG_ID);
            }
        });
        
    	m_onStartDateChanged = new DatePickerDialog.OnDateSetListener() {
			public void onDateSet(DatePicker spView, int iYear, int iMonth, int iDay) {
			    m_iStartYear  = iYear;
			    m_iStartMonth = iMonth;
			    m_iStartDay   = iDay;
			    updateDateTimeFields();
			}
        };
    	
    	m_onStartTimeChanged = new TimePickerDialog.OnTimeSetListener() {
			public void onTimeSet(TimePicker spView, int iHour, int iMinute) {
			    m_iStartHour   = iHour;
			    m_iStartMinute = iMinute;
			    updateDateTimeFields();
			}
        };	
    	
    	m_onEndDateChanged = new DatePickerDialog.OnDateSetListener() {
			public void onDateSet(DatePicker spView, int iYear, int iMonth, int iDay) {
			    m_iEndYear  = iYear;
			    m_iEndMonth = iMonth;
			    m_iEndDay   = iDay;
			    updateDateTimeFields();
			}
        };

    	m_onEndTimeChanged = new TimePickerDialog.OnTimeSetListener() {
			public void onTimeSet(TimePicker spView, int iHour, int iMinute) {
			    m_iEndHour   = iHour;
			    m_iEndMinute = iMinute;
			    updateDateTimeFields();
			}
        };
    }
    
    /**
     * Atualiza os campos de data e hora inicial e final com os valores atuais editados
     * pelo usuário.
     */
    private void updateDateTimeFields() {
    	Calendar spStart = Calendar.getInstance();
    	spStart.set(m_iStartYear, m_iStartMonth, m_iStartDay, m_iStartHour, m_iStartMinute);

    	Calendar spEnd = Calendar.getInstance();
    	spEnd.set(m_iEndYear, m_iEndMonth, m_iEndDay, m_iEndHour, m_iEndMinute);

		SimpleDateFormat dfDate = new SimpleDateFormat("EEE, dd/MM/yyyy");
		SimpleDateFormat dfTime = new SimpleDateFormat("HH:mm");

		Button spStartDate = (Button) findViewById(R.id.search_start_date);
        spStartDate.setText(dfDate.format(spStart.getTime()));
        Button spStartTime = (Button) findViewById(R.id.search_start_time);
        spStartTime.setText(dfTime.format(spStart.getTime()));

        Button spEndDate = (Button) findViewById(R.id.search_end_date);
        spEndDate.setText(dfDate.format(spEnd.getTime()));
        Button spEndTime = (Button) findViewById(R.id.search_end_time);
        spEndTime.setText(dfTime.format(spEnd.getTime()));
    }
    
    /**
     * Callback para criação dos diálogos de entrada de data e hora apropriados.
     */
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        	case START_DATE_DIALOG_ID:
        		return new DatePickerDialog(this, m_onStartDateChanged, m_iStartYear, m_iStartMonth, m_iStartDay);
        	case START_TIME_DIALOG_ID:
        		return new TimePickerDialog(this, m_onStartTimeChanged, m_iStartHour, m_iStartMinute, true);
        	case END_DATE_DIALOG_ID:
        		return new DatePickerDialog(this, m_onEndDateChanged, m_iEndYear, m_iEndMonth, m_iEndDay);
        	case END_TIME_DIALOG_ID:
        		return new TimePickerDialog(this, m_onEndTimeChanged, m_iEndHour, m_iEndMinute, true);
        }
        return null;
    }
    
    /**
     * Executa a busca dos seminários segundo os critérios informados.
     */
    private void search() {
    	Calendar spStart = Calendar.getInstance();
    	spStart.set(m_iStartYear, m_iStartMonth, m_iStartDay, m_iStartHour, m_iStartMinute);
    	Date dtStart = spStart.getTime();

    	Calendar spEnd = Calendar.getInstance();
    	spEnd.set(m_iEndYear, m_iEndMonth, m_iEndDay, m_iEndHour, m_iEndMinute);
    	Date dtEnd = spEnd.getTime();
    	
        Spinner spAreas = (Spinner) findViewById(R.id.search_area);
        SeminarArea eArea = SeminarArea.getEnumFromNumber(spAreas.getSelectedItemPosition() - 1);
    	
        EditText spTitle = (EditText) findViewById(R.id.search_title);
        String sTitle = spTitle.getText().toString();
    	
		ArrayList<Seminar> lsSeminars = DataCache.getInstance().getSeminars(eArea, dtStart, dtEnd, sTitle);
		Bundle spParams = new Bundle();
        
        spParams.putSerializable("Seminars", lsSeminars);
		
        Intent spIntent = new Intent(this, DataActivity.class);
        spIntent.putExtras(spParams);
        startActivity(spIntent);
    }
}
