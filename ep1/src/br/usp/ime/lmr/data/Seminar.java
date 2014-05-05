package br.usp.ime.lmr.data;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Classe que representa os dados de um seminário.
 * @author Luiz Carlos Vieira
 * @version 1.0
 */
public class Seminar implements Serializable {

	/**
	 * Versão para verificação de compatilibidade de dados serializados.
	 */
	private static final long serialVersionUID = -5932200609324528606L;

	/**
	 * Armazena a área de concentração do seminário.
	 */
	private SeminarArea m_eArea;
	
	/**
	 * Armazena o título do seminário.
	 */
	private String m_sTitle;
	
	/**
	 * Armazena a data e a hora do seminário.
	 */
	private Date m_dtDateTime;
	
	/**
	 * Armazena a localidade do seminário (bloco, sala, etc).
	 */
	private String m_sLocation;
	
	/**
	 * Armanzena o nome do palestrante do seminário.
	 */
	private String m_sPanelist;
	
	/**
	 * Armazena o nome do responsável pelo seminário.
	 */
	private String m_sResponsible;
	
	/**
	 * Armazena o resumo do seminário.
	 */
	private String m_sAbstract;
	
	/**
	 * Construtor padrão da classe.
	 */
	public Seminar() {
		m_eArea = SeminarArea.UNDEFINED;
		m_sTitle = "";
		m_dtDateTime = new Date();
		m_sLocation = "";
		m_sPanelist = "";
		m_sResponsible = "";
		m_sAbstract = "";
	}
	
	/**
	 * Construtor que aceita os dados iniciais para a classe.
	 * @param eArea Valor de SeminarArea com a área de concentração do seminário.
	 * @param sTitle String com o título do seminário.
	 * @param dtDateTime Date com a hora e a data do seminário.
	 * @param sLocation String com a localidade do seminário.
	 * @param sPanelist String com o palestrante do seminário.
	 * @param sResponsible String com o responsável pelo seminário.
	 * @param sAbstract String com o resumo do seminário.
	 */
	public Seminar(SeminarArea eArea, String sTitle, Date dtDateTime, String sLocation,
			       String sPanelist, String sResponsible, String sAbstract) {
		m_eArea = eArea;
		m_sTitle = sTitle;
		m_dtDateTime = dtDateTime;
		m_sLocation = sLocation;
		m_sPanelist = sPanelist;
		m_sResponsible = sResponsible;
		m_sAbstract = sAbstract;
	}

	/**
	 * Obtém uma representação textual do seminário.
	 * @return String com a representação textual do seminário.
	 */
	@Override public String toString() {
		return "Seminário: Area [" + m_eArea.toString() + "] Título [" + m_sTitle + "]";
	}
	
	/**
	 * Método para comparação de seminários.
	 * @param spOther Seminário dado para comparação a esta instância.
	 * @return Retorna true se os dois seminários são exatamente iguais, e falso caso contrário.
	 */
	public boolean equals(Seminar spOther) {
		return spOther.getArea() == m_eArea &&
			   spOther.getTitle().equals(m_sTitle) &&
			   spOther.getDateTime().equals(m_dtDateTime) &&
			   spOther.getLocation().equals(m_sLocation) &&
			   spOther.getPanelist().equals(m_sPanelist) &&
			   spOther.getResponsible().equals(m_sResponsible) &&
			   spOther.getAbstract().equals(m_sAbstract);
	}
	
	/**
	 * Getter da área de concentração do seminário.
	 * @return Valor da enumeration SeminarArea com a área do seminário.
	 */
	public SeminarArea getArea() {
		return m_eArea;
	}

	/**
	 * Setter da área de concentração do seminário.
	 * @param eArea Valor da enumeration SeminarArea com a nova área do seminário.
	 */
	public void setArea(SeminarArea eArea) {
		m_eArea = eArea;
	}
	
	/**
	 * Setter da área de concentração do seminário a partir de texto representativo.
	 * @param sArea String com o texto representativo da área do seminário.
	 */
	public void setArea(String sArea) {
		m_eArea = SeminarArea.getEnumFromText(sArea);
	}
	
	/**
	 * Getter do título do seminário.
	 * @return String com o título do seminário.
	 */
	public String getTitle() {
		return m_sTitle;
	}

	/**
	 * Setter do título do seminário.
	 * @param sTitle String com o novo título para o seminário.
	 */
	public void setTitle(String sTitle) {
		m_sTitle = sTitle;
	}

	/**
	 * Getter da data e hora do seminário.
	 * @return Data e hora do seminário.
	 */
	public Date getDateTime() {
		return m_dtDateTime;
	}

	/**
	 * Getter da data e hora do seminário como timestamp.
	 * @return String representando a data e a hora do seminário em formato dd/MM/yyyy HH:mm.
	 */
	public String getDateTimeAsTimestamp() {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");		
		return df.format(m_dtDateTime);
	}
	
	/**
	 * Setter da data e hora do seminário.
	 * @param dtDateTime Date com a nova data e hora para o seminário em formato GMT.
	 */
	public void setDateTime(Date dtDateTime) {
		m_dtDateTime = dtDateTime;
	}
	
	/**
	 * Setter da data e hora do seminário como uma string.
	 * @param sLocalDateTime String com a nova data e hora para o seminário em formato local.
	 */
	public void setDateTime(String sDateTime) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		Date dtNewDate = null;
		try {
			dtNewDate = df.parse(sDateTime);
		} catch (ParseException e) {
			df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
			try {
				dtNewDate = df.parse(sDateTime);
			} catch (ParseException e1) {
				dtNewDate = null;
			}
		}
		
		if(dtNewDate != null)
			m_dtDateTime = dtNewDate;
	}
	
	/**
	 * Getter da localidade do seminário.
	 * @return String com a localidade do seminário.
	 */
	public String getLocation() {
		return m_sLocation;
	}

	/**
	 * Setter da localidade do seminário.
	 * @param sLocation String com a nova localidade para o seminário.
	 */
	public void setLocation(String sLocation) {
		m_sLocation = sLocation;
	}
	
	/**
	 * Getter do palestrante do seminário.
	 * @return String com o palestrante do seminário.
	 */
	public String getPanelist() {
		return m_sPanelist;
	}

	/**
	 * Setter do palestrante do seminário.
	 * @param sPanelist String com o novo palestrante para o seminário.
	 */
	public void setPanelist(String sPanelist) {
		m_sPanelist = sPanelist;
	}
	
	/**
	 * Getter do responsável pelo seminário.
	 * @return String com o responsável pelo seminário.
	 */
	public String getResponsible() {
		return m_sResponsible;
	}

	/**
	 * Setter do responsável pelo seminário.
	 * @param sResponsible String com o novo responsável pelo seminário.
	 */
	public void setResponsible(String sResponsible) {
		m_sResponsible = sResponsible;
	}
	
	/**
	 * Getter do resumo do seminário.
	 * @return String com o resumo do seminário.
	 */
	public String getAbstract() {
		return m_sAbstract;
	}

	/**
	 * Setter do resumo do seminário.
	 * @param sAbstract String com o novo resumo para o seminário.
	 */
	public void setAbstract(String sAbstract) {
		m_sAbstract = sAbstract;
	}
}
