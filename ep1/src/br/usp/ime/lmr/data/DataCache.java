package br.usp.ime.lmr.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import br.usp.ime.lmr.exceptions.GDataAccessException;

import com.pras.SpreadSheet;
import com.pras.SpreadSheetFactory;
import com.pras.WorkSheet;
import com.pras.WorkSheetCell;
import com.pras.WorkSheetRow;

/**
 * Classe singleton para gerenciamento dos dados dos seminários. Os dados, originalmente
 * armazenados em uma planilha do Google Docs, são "baixados" e armazenados localmente
 * em uma base de dados SQLite para permitir o acesso mesmo em condições de ausência de
 * conexão. A atualização dos dados ocorre sempre que possível e de forma transparente ao
 * usuário.
 * 
 * Atenção: Esta classe depende da biblioteca gss-lib-2.2b1 de autoria de Prasanta Paul.
 * A biblioteca do Google Data ainda não é suportada no Android (vai entender...), e por
 * isso o autor construiu esse porte. Se desejado, o pacote jar, os códigos-fonte e diversos
 * exemplos de utilização podem ser baixados do site do projeto, listado abaixo. A versão
 * utilizada neste EP é a 2.2b1 de 31 de outubro de 2011, compatível com nívem mínimo 7 do
 * Android (ou seja, versão >= 2.1).
 * 
 * Sites de referência:
 * 
 * Projeto: http://code.google.com/p/google-spreadsheet-lib-android/
 * Blog do Autor: http://prasanta-paul.blogspot.com.br/2010/12/google-spreadsheet-library-for-android.html
 * 
 * @author Luiz Carlos Vieira
 * @version 2.0
 * 
 * Histórico de atualizações:
 * Versão 2.0 (14/04/12) - Armazenamento local em base de dados SQLite
 * Versão 1.0 (02/04/12) - Acesso ao Google Docs
 */
public class DataCache extends SQLiteOpenHelper {
	
	/** Nome do banco de dados de armazenamento local (cache) dos seminários. */
	private static final String DATABASE_NAME = "semidroid.db";
	
	/** Versão do banco de dados de armazenamento local dos seminários. */
	private static final int DATABASE_VERSION = 1;
	
	/** Nome da tabela para armazenamento dos dados dos seminários. */
	private final String TABLE_NAME = "Seminars";
	
	/** Nome da coluna Area. */
	private final String COLUMN_AREA = "Area";

	/** Nome da coluna Area. */
	private final String COLUMN_TITLE = "Title";

	/** Nome da coluna Area. */
	private final String COLUMN_DATETIME = "DateTime";

	/** Nome da coluna Area. */
	private final String COLUMN_LOCATION = "Location";

	/** Nome da coluna Area. */
	private final String COLUMN_PANELIST = "Panelist";

	/** Nome da coluna Area. */
	private final String COLUMN_RESPONSIBLE = "Responsible";

	/** Nome da coluna Area. */
	private final String COLUMN_ABSTRACT = "Abstract";
	
	/**
	 * Dados das colunas na tabela de seminários.
	 */
	private final String[][] TABLE_COLUMNS = { {COLUMN_AREA,        "integer not null"},
											   {COLUMN_TITLE,       "text not null"   },
											   {COLUMN_DATETIME,    "text not null"   },
											   {COLUMN_LOCATION,    "text not null"   },
											   {COLUMN_PANELIST,    "text not null"   },
											   {COLUMN_RESPONSIBLE, "text not null"   },
											   {COLUMN_ABSTRACT,    "text not null"   }
											 }; 
	
	/**
	 * Factory de acesso ao Google Docs.
	 */
	private SpreadSheetFactory m_spFactory;
	
	/** Resultado da operação para adição de um seminário (novo ou atualizado) à base de dados local. */
	public static enum ADD_OPERATION_RESULT {
											  ADDED, UPDATED,
											  EXISTED, ERROR
									 		};  

	/** Lista de listeners para os eventos de atualização do cache de dados local. */
	private ArrayList<UpdateListener> m_lsListeners;
									 		
	/** Instância singleton da classe */
	private static DataCache m_spInstance = null;
	
	/** Contexto da aplicação. */
	private Activity m_spContext; 
									 		
	/**
	 * Construtor protegido da classe.
	 * @param spContext Contexto da aplicação para acesso aos dados em cache.
	 */
	protected DataCache(Activity spContext) {
		super(spContext, DATABASE_NAME, null, DATABASE_VERSION);
		
		m_spContext = spContext;
		m_lsListeners = new ArrayList<UpdateListener>();
		
		// Abaixo estão hard-coded o usuário Google a senha criada para armazenar a planilha
		// no Google Docs
		m_spFactory = SpreadSheetFactory.getInstance("ime.seminars@gmail.com", "BlocoBSalaB01");		
		Log.i(getClass().getName(), "Objeto de cache de dados criado");
	}
	
	/**
	 * Método de acesso à instância singleton da classe durante a inicialização.
	 * @param spContext Contexto da aplicação para acesso aos dados em cache.
	 * @return Objeto DataCache singleton.
	 */
	public static DataCache getInstance(Activity spContext) {
		if(m_spInstance == null)
			m_spInstance = new DataCache(spContext);
		return m_spInstance;
	}
	
	/**
	 * Método de acesso geral à instância singleton da classe.
	 * @return Objeto DataCache singleton.
	 */
	public static DataCache getInstance() {
		return m_spInstance;
	}

	/**
	 * Registra um novo listener para receber os eventos de atualização do cache local.
	 * @param spListener Instância do UpdateListener para receber os eventos.
	 */
	public void registerListener(UpdateListener spListener) {
		m_lsListeners.add(spListener);
	}
	
	/**
	 * Desregistra um listener para que não mais receba os eventos de atualizçaão do cache local.
	 * @param spListener Instância do UpdateListener que não irá mais receber os eventos.
	 */
	public void unregisterListener(UpdateListener spListener) {
		m_lsListeners.remove(spListener);
	}
	
	/**
	 * Envia o evento de falha na atualização para todos os listeners registrados.
	 */
	private void sendFailureEvent() {
		m_spContext.runOnUiThread(new Runnable() {
		    public void run() {
				for(UpdateListener spListener: m_lsListeners) {
					spListener.onDataUpdateFailed();
				}
		    }
		});				
	}

	/**
	 * Envia o evento de atualização para todos os listeners registrados.
	 * @param dtUpdate Data e hora da atualização.
	 */
	private void sendUpdateEvent(final Date dtUpdate) {
		m_spContext.runOnUiThread(new Runnable() {
			private Date m_dtUpdate = dtUpdate;
		    public void run() {
				for(UpdateListener spListener: m_lsListeners) {
					spListener.onDataUpdated(m_dtUpdate);
				}
		    }
		});				
	}
	
	/**
	 * Evento de criação da base de dados. É chamado automaticamente na primeira vez
	 * para que a base de dados e as tabelas sejam criadas.
	 * @param db Instância do SQLiteDatabase para a criação do banco de dados.
	 */
	@Override
	public void onCreate(SQLiteDatabase db) {
		String sColumns = "";
		for(String[] asColumn: TABLE_COLUMNS)
		{
			if(!sColumns.equals(""))
				sColumns += " , ";
			sColumns += asColumn[0] + " " + asColumn[1];
		}
		
		db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + sColumns + ")");
		db.execSQL("CREATE TABLE INFO (LastUpdate text not null)");
	}

	/**
	 * Evento de atualização da base de dados. É chamado automaticamente quando uma nova versão
	 * de base de dados precisa ser instalada.
	 * @param db Instância do SQLiteDatabase para a criação do banco de dados.
	 * @param iOldVersion Número da versão da base de dados atualmente instalada no sistema.
	 * @param iNewVersion Número da nova versão da base de dados a ser instalada no sistema.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, int iOldVersion, int iNewVersion) {
		db.execSQL("DROP TABLE IF EXISTS INFO");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);		
	}
	
	/**
	 * Atualiza o cache de seminários a partir de uma planilha no Google Docs.
	 * O cache apenas é atualizado se o método executar com sucesso. No caso de falhas
	 * uma exceção é produzida e o cache anterior é mantido.
	 * @throws GDataAccessException Produz essa exceção no caso de falhas na leitura dos dados do Google Docs.
	 * @return Retorna verdadeiro se algum seminário foi atualizado no banco de dados local, e falso se nada foi atualizado.
	 */
	public boolean updateFromGoogleDocs() throws GDataAccessException {

		Log.i(getClass().getName(), "Iniciando atualização do cache de dados a partir do Google Docs");
		
		// Obtém acesso à planilha dos seminários no Google Docs
		
		ArrayList<SpreadSheet> lsSpreadSheets = m_spFactory.getSpreadSheet("IME - USP Seminars", true);

		if (lsSpreadSheets == null || lsSpreadSheets.size() == 0) {
			Log.e(getClass().getName(), "Falha no acesso ao Google Docs ou a planilha IME - USP Seminars não existe");
			sendFailureEvent();
			throw new GDataAccessException("Não foi possível conectar ao servidor de dados.");
		}
		SpreadSheet spSpreadSheet = lsSpreadSheets.get(0);

		// Obtém acesso à pasta Data dentro da planilha
		
		ArrayList<WorkSheet> lsWorkSheets = spSpreadSheet.getWorkSheet("Data", false);

		if (lsWorkSheets == null || lsWorkSheets.size() == 0) {
			Log.e(getClass().getName(), "Não foi possível acessar a pasta Data dentro da planilha de dados");
			sendFailureEvent();
			throw new GDataAccessException("Não foi possível acessar a base de dados.");
		}
		WorkSheet spWorkSheet = lsWorkSheets.get(0);

		Log.d(getClass().getName(), "Número de seminários registrados na planilha é: " + spWorkSheet.getRowCount());

		// Lê os dados da planilha e atualiza o cache de seminários

		ArrayList<WorkSheetRow> lsRows = spWorkSheet.getData(false);

		if (lsRows == null) {
			Log.e(getClass().getName(), "Não foi possível ler os registros da pasta Data na planilha");
			sendFailureEvent();
			throw new GDataAccessException("Não foi possível obter os dados dos seminários.");
		}
		
		boolean bRet = false;
		
		for (WorkSheetRow spRow : lsRows) {
			ArrayList<WorkSheetCell> lsCells;

			/*
			 * Nota importante: Não sei se é o Google Docs ou a API que faz isso
			 * mas no caso de textos longos (como o Abstract) uma mesma coluna
			 * pode ser devolvida mais do que uma vez (várias ocorrências) com os 
			 * textos complementares.
			 * No algoritmo abaixo isso está sendo considerado na verificação de
			 * que as células obtidas são no mínimo 8 (uma para cada campo de um
			 * seminário) e na adição complementar do Resumo. Para os demais campos
			 * considerou-se desnecessário essa complementação, uma vez que eles
			 * serão geralmente bastante pequenos.
			 */
			
			lsCells = spRow.getCells();
			if(lsCells == null || lsCells.size() < 8) {
				Log.e(getClass().getName(), "Não foi possível ler os dados da linha " + spRow.getRowIndex() + " na planilha");
				sendFailureEvent();
				throw new GDataAccessException("Não foi possível ler os dados dos seminários recebidos.");
			}
			
			Seminar spSeminar = new Seminar();
			
			for(int i = 1; i < lsCells.size(); i++) {
				String sColumn = lsCells.get(i).getName().toLowerCase();
				String sValue = lsCells.get(i).getValue();
				Log.d(getClass().getName(), "Coluna: [" + sColumn + "] Valor: [" + sValue + "]");				
				
				if(sColumn.equals("area"))
					spSeminar.setArea(sValue);
				else if(sColumn.equals("title"))
					spSeminar.setTitle(sValue);
				else if(sColumn.equals("datetime"))
					spSeminar.setDateTime(sValue);
				else if(sColumn.equals("location"))
					spSeminar.setLocation(sValue);
				else if(sColumn.equals("panelist"))
					spSeminar.setPanelist(sValue);
				else if(sColumn.equals("responsible"))
					spSeminar.setResponsible(sValue);
				else if(sColumn.equals("abstract")) {
					String sCurrent = spSeminar.getAbstract();
					spSeminar.setAbstract(sCurrent + sValue);
				}
				else
					Log.d(getClass().getName(), "Ignorando coluna desconhecida");
			}
			
			ADD_OPERATION_RESULT eRet = addSeminar(spSeminar);
			if(eRet == ADD_OPERATION_RESULT.ERROR) {
				Log.e(getClass().getName(), "Não foi possível adicionar atualizar o banco de dados local");
				sendFailureEvent();
				throw new GDataAccessException("Não foi possível atualizar o banco de dados local.");
			}
			else if(eRet != ADD_OPERATION_RESULT.EXISTED)
				bRet = true;
		}
		
		Log.i(getClass().getName(), "Atualização do cache de dados a partir do Google Docs concluída com sucesso.");
		Log.i(getClass().getName(), "O banco de dados local " + (bRet ? "foi": "não foi") + " atualizado.");
		
		if(bRet) {
			resetLastUpdate();
			sendUpdateEvent(getLastUpdate());
		}
		
		return bRet;
	}

	/**
	 * Consulta a data da última atualização do banco de dados local a partir da planilha do Google Docs.
	 * @return Data e hora da última atualização ou null se ainda não ouve uma atualização. 
	 */
	public Date getLastUpdate() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cur = db.rawQuery("Select LastUpdate from INFO", null);

		Date dtRet = null;
		if(cur.moveToFirst())
		{
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			try {
				dtRet = df.parse(cur.getString(0));
			} catch (ParseException e) {
				dtRet = null;
			}
		}

		cur.close();
		db.close();
		
		return dtRet;
	}
	
	/**
	 * Redefine a data da última atualização do banco de dados local com a data e hora atuais.
	 */
	public void resetLastUpdate() {

		Date dtPrev = getLastUpdate();
		
		Date dtNow = new Date();
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sNow = df.format(dtNow);

		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues cv = new ContentValues();
		cv.put("LastUpdate", sNow);

		if(dtPrev != null) {
			String sPrev = df.format(dtPrev);
			db.delete("INFO", "LastUpdate=?", new String[] {sPrev});
		}
		db.insert("INFO", null, cv);
		
		db.close();
	}
	
	/**
	 * Adiciona ou atualiza um seminário na base de dados local.
	 * @param spSeminar Instância de seminário para ser adicionado/atualizado.
	 * @return Retorna a enumeração ADD_OPERATION_RESULT indicando se seminário foi
	 * adicionado, atualizado, já existia ou se um erro ocorreu.
	 */
	public ADD_OPERATION_RESULT addSeminar(Seminar spSeminar) {
		Seminar spQuery = getSeminar(spSeminar.getArea(), spSeminar.getTitle());
		if(spQuery == null || !spQuery.equals(spSeminar))
		{
			SQLiteDatabase db = this.getWritableDatabase();
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String sDateTime = df.format(spSeminar.getDateTime());
			
			ContentValues cv = new ContentValues();
			cv.put(COLUMN_AREA,        spSeminar.getArea().toInteger());
			cv.put(COLUMN_TITLE,       spSeminar.getTitle());
			cv.put(COLUMN_DATETIME,    sDateTime);
			cv.put(COLUMN_LOCATION,    spSeminar.getLocation());
			cv.put(COLUMN_PANELIST,    spSeminar.getPanelist());
			cv.put(COLUMN_RESPONSIBLE, spSeminar.getResponsible());
			cv.put(COLUMN_ABSTRACT,    spSeminar.getAbstract());

			ADD_OPERATION_RESULT eRet;
			if(spQuery == null)
			{
				Log.d(getClass().toString(), "Inserindo novo seminário na base de dados:");
				Log.d(getClass().toString(), spSeminar.toString());
				long lRet = db.insert(TABLE_NAME, null, cv);
				if(lRet == -1)
					eRet = ADD_OPERATION_RESULT.ERROR;
				else
					eRet = ADD_OPERATION_RESULT.ADDED;
			}
			else
			{
				Log.d(getClass().toString(), "Atualizando seminário existente na base de dados:");
				Log.d(getClass().toString(), spSeminar.toString());
				int iRet = db.update(TABLE_NAME, cv, COLUMN_AREA + "=? and " + COLUMN_TITLE + "=?", 
					    			  new String []{String.valueOf(spSeminar.getArea().toInteger()), spSeminar.getTitle()});
				if(iRet == 0)
					eRet = ADD_OPERATION_RESULT.ERROR;
				else
					eRet = ADD_OPERATION_RESULT.UPDATED;
			}
			
			db.close();
			return eRet;
		}
		else
		{
			Log.d(getClass().toString(), "O seminário já existe na base de dados e não vai ser atualizado");
			return ADD_OPERATION_RESULT.EXISTED;
		}
	}
	
	/**
	 * Consulta um seminário existente pela área e título dados.
	 * @param eArea Area do seminário a ser consultado.
	 * @param sTitle Título do seminário a ser consultado.
	 * @return Objeto Seminar com os dados do seminário consultado ou null se
	 * o seminário não existe na base de dados.
	 */
	public Seminar getSeminar(SeminarArea eArea, String sTitle) {
		
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cur = db.query(TABLE_NAME, new String[] { COLUMN_AREA, COLUMN_TITLE,
														 COLUMN_DATETIME, COLUMN_LOCATION,
														 COLUMN_PANELIST, COLUMN_RESPONSIBLE,
														 COLUMN_ABSTRACT
													    },
                              COLUMN_AREA + "=? and " + COLUMN_TITLE + "=?", new String[]{String.valueOf(eArea.toInteger()), sTitle},
                              null, null, null);
		
		Seminar spRet = null;
		if(cur.moveToFirst())
		{
			spRet = new Seminar();
			spRet.setArea(SeminarArea.getEnumFromNumber(cur.getInt(0)));
			spRet.setTitle(cur.getString(1));
			spRet.setDateTime(cur.getString(2));
			spRet.setLocation(cur.getString(3));
			spRet.setPanelist(cur.getString(4));
			spRet.setResponsible(cur.getString(5));
			spRet.setAbstract(cur.getString(6));
		}	

		cur.close();
		db.close();
		return spRet;
	}
	
	/**
	 * Consulta os seminários existentes para uma área dada.
	 * @param eArea Área dos seminários a serem consultados.
	 * Se a área informada for SeminarArea.UNDEFINED, todos os seminários são devolvidos.
	 * @return Lista com os seminários encontrados.
	 */
	public ArrayList<Seminar> getSeminars(SeminarArea eArea) {
		SQLiteDatabase db = this.getReadableDatabase();

		String sQuery;
		String[] asParams;
		if(eArea != SeminarArea.UNDEFINED)
		{
			sQuery   = COLUMN_AREA + "=?";
			asParams = new String[]{String.valueOf(eArea.toInteger())}; 
		}
		else
		{
			sQuery   = null;
			asParams = null;
		}
		
		Cursor cur = db.query(TABLE_NAME, new String[] { COLUMN_AREA, COLUMN_TITLE,
														 COLUMN_DATETIME, COLUMN_LOCATION,
														 COLUMN_PANELIST, COLUMN_RESPONSIBLE,
														 COLUMN_ABSTRACT
													    },
                              sQuery, asParams,
                              null, null, COLUMN_DATETIME + " Asc");
		
		ArrayList<Seminar> lsRet = new ArrayList<Seminar>();
		boolean bData = cur.moveToFirst();
		while(bData)
		{
			Seminar spSeminar = new Seminar();
			spSeminar.setArea(SeminarArea.getEnumFromNumber(cur.getInt(0)));
			spSeminar.setTitle(cur.getString(1));
			spSeminar.setDateTime(cur.getString(2));
			spSeminar.setLocation(cur.getString(3));
			spSeminar.setPanelist(cur.getString(4));
			spSeminar.setResponsible(cur.getString(5));
			spSeminar.setAbstract(cur.getString(6));
			
			lsRet.add(spSeminar);
			bData = cur.moveToNext();
		}

		cur.close();
		db.close();
		return lsRet;
	}
	
	/**
	 * Consulta os seminários existentes para o dia de hoje.
	 * @return Lista com os seminários encontrados no dia de hoje.
	 */
	public ArrayList<Seminar> getTodaySeminars() {
		SQLiteDatabase db = this.getReadableDatabase();

		Date dtStart = new Date();
		dtStart.setHours(0);
		dtStart.setMinutes(0);
		dtStart.setSeconds(0);
		Date dtEnd = (Date) dtStart.clone();
		dtEnd.setHours(23);
		dtEnd.setMinutes(59);
		dtEnd.setSeconds(59);
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sStart = df.format(dtStart);
		String sEnd = df.format(dtEnd);
		
		Cursor cur = db.query(TABLE_NAME, new String[] { COLUMN_AREA, COLUMN_TITLE,
														 COLUMN_DATETIME, COLUMN_LOCATION,
														 COLUMN_PANELIST, COLUMN_RESPONSIBLE,
														 COLUMN_ABSTRACT
													    },
  						      COLUMN_DATETIME + ">=? and " + COLUMN_DATETIME + "<=?",
  						      new String[]{ sStart, sEnd },
                              null, null, COLUMN_DATETIME + " Asc");
		
		ArrayList<Seminar> lsRet = new ArrayList<Seminar>();
		boolean bData = cur.moveToFirst();
		while(bData)
		{
			Seminar spSeminar = new Seminar();
			spSeminar.setArea(SeminarArea.getEnumFromNumber(cur.getInt(0)));
			spSeminar.setTitle(cur.getString(1));
			spSeminar.setDateTime(cur.getString(2));
			spSeminar.setLocation(cur.getString(3));
			spSeminar.setPanelist(cur.getString(4));
			spSeminar.setResponsible(cur.getString(5));
			spSeminar.setAbstract(cur.getString(6));
			
			lsRet.add(spSeminar);
			bData = cur.moveToNext();
		}

		cur.close();
		db.close();
		return lsRet;
	}
	
	/**
	 * Consulta os seminários existentes de acordo com os parâmetros dados.
	 * @param eArea Área dos seminários a serem consultados.
	 * Se a área informada for SeminarArea.UNDEFINED, os seminários de todas as áreas são devolvidos.
	 * @param dtStart Data e hora para limite inicial da pesquisa. Se o valor informado for null, não
	 * é considerado na pesquisa.
	 * @param dtEnd Data e hora para limite final da pesquisa. Se o valor informado for null, não é
	 * considerado na pesquisa.
	 * @param sTitle Título (total ou parcial) para a pesquisa. Se o valor informado for null ou vazio
	 * (""), não é considerado na pesquisa.
	 * @return Lista com os seminários encontrados.
	 */
	public ArrayList<Seminar> getSeminars(SeminarArea eArea, Date dtStart, Date dtEnd, String sTitle) {
		SQLiteDatabase db = this.getReadableDatabase();

		String sQuery = "";
		ArrayList<String> lsParams = new ArrayList<String>();
		
		if(eArea != SeminarArea.UNDEFINED) {
			sQuery += COLUMN_AREA + "=?";
			lsParams.add(String.valueOf(eArea.toInteger()));
		}
		
		if(dtStart != null) {
			if(sQuery.length() > 0)
				sQuery += " and ";
			sQuery += COLUMN_DATETIME + ">=?";
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			lsParams.add(df.format(dtStart));
		}
		
		if(dtEnd != null) {
			if(sQuery.length() > 0)
				sQuery += " and ";
			sQuery += COLUMN_DATETIME + "<=?";
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			lsParams.add(df.format(dtEnd));
		}
		
		if(sTitle != null && sTitle.length() > 0) {
			if(sQuery.length() > 0)
				sQuery += " and ";
			sQuery += COLUMN_TITLE + " like ?";
			lsParams.add("%" + sTitle + "%");
		}
		
		String[] asParams = lsParams.toArray(new String[lsParams.size()]);
		Cursor cur = db.query(TABLE_NAME, new String[] { COLUMN_AREA, COLUMN_TITLE,
														 COLUMN_DATETIME, COLUMN_LOCATION,
														 COLUMN_PANELIST, COLUMN_RESPONSIBLE,
														 COLUMN_ABSTRACT
													    },
                              sQuery, asParams,
                              null, null, COLUMN_DATETIME + " Asc");
		
		ArrayList<Seminar> lsRet = new ArrayList<Seminar>();
		boolean bData = cur.moveToFirst();
		while(bData)
		{
			Seminar spSeminar = new Seminar();
			spSeminar.setArea(SeminarArea.getEnumFromNumber(cur.getInt(0)));
			spSeminar.setTitle(cur.getString(1));
			spSeminar.setDateTime(cur.getString(2));
			spSeminar.setLocation(cur.getString(3));
			spSeminar.setPanelist(cur.getString(4));
			spSeminar.setResponsible(cur.getString(5));
			spSeminar.setAbstract(cur.getString(6));
			
			lsRet.add(spSeminar);
			bData = cur.moveToNext();
		}

		cur.close();
		db.close();
		return lsRet;
	}
}