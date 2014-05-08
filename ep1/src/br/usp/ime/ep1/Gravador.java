package br.usp.ime.ep1;

/**
 * Classe que recarrega o cache de todos os cardápios de todos os bandejões.
 */
public class Gravador {
	Util u;
			
	public Gravador() {
		u = new Util();
	}
	
	public void gravar() {
		
		u.gravarCardapioOffline("http://www.usp.br/coseas/cardapio.html", "ccentral.html");
		u.gravarCardapioOffline("http://www.usp.br/coseas/cardapiofisica.html", "cfisica.html");
		u.gravarCardapioOffline("http://www.usp.br/coseas/cardapioquimica.html", "cquimica.html");
		u.gravarCardapioOffline("http://www.usp.br/coseas/cardcocesp.html", "ccocesp.html");
		u.gravarCardapioOffline("http://www.usp.br/coseas/carddoc.html", "cprofessores.html");
		u.gravarCardapioOffline("http://www.usp.br/coseas/cardEscEnf.html", "cenfermagem.html");
		u.gravarCardapioOffline("http://www.usp.br/coseas/cardFSP.html", "csaude.html");
		u.gravarCardapioOffline("http://www.usp.br/coseas/cardFacDireito.html", "cdireito.html");
	}
	
}
