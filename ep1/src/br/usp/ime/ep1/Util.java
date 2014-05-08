package br.usp.ime.ep1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * 
 * @author Rafael Perazzo Barbosa Mota (perazzo@ime.usp.br)
 * Classe com várias rotinas úteis para diversas activities do Projeto
 * Android
 *
 */
public class Util{
	
	public static String RESTAURANTE = "http://www.usp.br/coseas/cardapio.html";
	public static String restauranteOFFLINE = "ccentral.html";
	public static double LONGITUDE = -46.736895;
	public static double LATITUDE = -23.567409;
	public static String MAISPROXIMO = "---";
	public static double dist = 0;
	public static int POSICAO = 1;
	public static String BANDEIJAO = "central";
	public static String pasta = "com.usp";
	public static String[] NOMES = {"Central","Central","Física","Química","Cocesp","Professores","Enfermagem","Saude","Direito"};
	public static String SERVIDOR = "http://valinhos.ime.usp.br:56080/usp-mobile/bandejao/";
	public static String[] NOMESBANDEIJAO = {"central","central","fisica","quimica","cocesp","professores","enfermagem","saude","direito"};
	public static String[] sNOMESBANDEIJAO = {"ccentral.html","ccentral.html","cfisica.html","cquimica.html","ccocesp.html","cprofessores.html","cenfermagem.html","csaude.html","cdireito.html"};
	public static int DISTANCIA = 0; //em metros ao portao
	public static String PORTAO_MAIS_PROXIMO = "3";
	public static int codigoStatus = 0;
	private static String codificacao = "UTF-8";
	
	public Util() {
		
	}
	
	/**
	 * Faz a inicialização do ambiente, criando pastas e arquivos necessarios
	 * para a correta inicialização da aplicação.
	 * @param c Contexto da aplicação
	 */
	public static boolean iniciar(Context c) {
		boolean resultado = true;
		File f = new File(Environment.getExternalStorageDirectory(),pasta);
		if (!f.exists()) {
			f.mkdirs();
		}
		File prop = new File(Environment.getExternalStorageDirectory() +"/"+ pasta + "/", "main.properties");
		File prop2 = new File(Environment.getExternalStorageDirectory() +"/"+ pasta + "/", "main.properties.temp");
		if (!prop2.exists()) { //Se o arquivo de propriedades existe
			try {
				prop2.createNewFile();
			} catch (IOException e) {
				Log.e("FILE",e.getMessage());
				resultado = false;
			}
		}
		if (prop.exists()) { //Se o arquivo de propriedades existe
			try {
	    	    InputStream inputStream = new FileInputStream(prop);
	    	    Properties properties = new Properties();
	    	    properties.load(inputStream);
	    	    Util.SERVIDOR = properties.getProperty("servidor","");
	    	    OutputStream os = new FileOutputStream(prop);
	    	    properties.save(os, "Salvo na inicialização");
	    	    inputStream.close();
	    	    os.close();
	    	} catch (IOException e) {
	    		Log.e("INICIO", e.getMessage());
	    		resultado = false;
	    	}
		}
		else { //Se não existe
			try {
				prop.createNewFile();
				Resources resources = c.getResources();
		    	AssetManager assetManager = resources.getAssets();
		    	Properties properties = null;
		    	try {
		    	    InputStream inputStream = assetManager.open("main.properties");
		    	    properties = new Properties();
		    	    properties.load(inputStream);
		    	    Util.SERVIDOR = properties.getProperty("servidor","");
		    	    File config = new File(Environment.getExternalStorageDirectory() +"/"+ Util.pasta + "/", "main.properties");
		    	    OutputStream os = new FileOutputStream(config);
		    	    properties.save(os, "Salvo na inicialização");
		    	    inputStream.close();
		    	    os.close();
		    	} catch (IOException e) {
		    		Log.e("INICIO", e.getMessage());
		    		resultado = false;
		    	}
			} catch (IOException e) {
				Log.e("INICIO", e.getMessage());
				resultado = false;
			}
		}
		return resultado;
	}
	
	/**
	 * Consulta a página do COSEAS para obter o cardápio desejado
	 * @param url URL do cardápio desejado
	 * @return String do HTML da página consultada
	 */
	public String getCardapio(String url) {
		String resultado ="";
		HttpClient c = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse response = null;
		try {
			response = c.execute(get);
		} catch (ClientProtocolException e) {
			Log.e("CARD",e.getMessage());
		} catch (IOException e) {
			Log.e("CARD",e.getMessage());
		}
		try {
			resultado = EntityUtils.toString(response.getEntity());
			
		} catch (ParseException e) {
			Log.e("CARD",e.getMessage());
		} catch (IOException e) {
			Log.e("CARD",e.getMessage());
		}
		return resultado;
	}
	
	
	/**
	 * Consulta uma URL qualquer
	 * @param url URL desejada
	 * @return String da URL solicitada
	 */
	public String getURL(String url) {
		String resultado ="";
		HttpClient c = new DefaultHttpClient();
		HttpGet get = new HttpGet(url);
		HttpResponse response = null;
		try {
			response = c.execute(get);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			resultado = EntityUtils.toString(response.getEntity());
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return resultado;
	}
	
	/**
	 * Verifica se a aplicação possui conectividade com a Internet
	 * @return Verdadeiro ou falso para a conectividade
	 */
	public boolean possuiInternet2() {
		boolean disponivel = false;
		URLConnection connection=null;
		try{
		    connection = new URL("http://www.ime.usp.br/~perazzo/ping.txt").openConnection();
		    connection.connect();
		    disponivel = true;
		} catch(final MalformedURLException e){
			disponivel = false;
		} catch(final IOException e){
		    disponivel = false;
		}
		return (disponivel);

	}
	
	/**
	 * Mostra uma mensagem do tipo TOAST (android) na tela
	 * @param context Contexto da aplicação
	 * @param msg Mensagem a ser mostrada
	 * @return resultado da operação
	 */
	public boolean MostraMensagem(Context context, CharSequence msg) {
		Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
		return true;
	}
	
	/**
	 * Mostra uma mensagem do tipo TOAST (android) na tela
	 * @param context Contexto da aplicação
	 * @param msg Mensagem a ser mostrada
	 * @param tempo Tempo que a mensagem fica exibida
	 * @return resultado da operação
	 */
	public boolean MostraMensagem(Context context, CharSequence msg, int tempo) {
		Toast.makeText(context, msg, tempo).show();
		return true;
	}
	
	/**
	 * Extrai uma tabela de uma página HTML
	 * @param html String HTML que contem a tabela
	 * @return HTML contendo apenas a tabela do HTML
	 */
	public String getTabela(String html) {
		String header ="";
		header = header + "<html><head>\n";
		header = header + "</head><body>";
		String tabela;
		int inicio = html.indexOf("<table");
		if (inicio==-1) {
			inicio = html.indexOf("<TABLE");
		}
		int fim = html.indexOf("</table>");
		if (fim==-1) {
			fim = html.indexOf("</TABLE>");
		}
		tabela = new String(html.substring(inicio,fim+9));
		tabela = header + tabela + "</body></html>";
		
		Document doc = Jsoup.parse(tabela);
		Element table = doc.select("table").first();
		Iterator<Element> ite = table.select("td").iterator();
		while(ite.hasNext()) {
			try {
				FileUtils.writeStringToFile(new File(Environment.getExternalStorageDirectory() +"/"+ pasta + "/", "tabela.txt"), ite.next().text() + "\n", true);
			} catch (IOException e) {
				Log.e("FILE",e.getMessage());
			}
			ite.next();
		}
	    		
		return tabela;
	}
	
	
	/**
	 * 
	 * @param html HTML do Cardapio
	 * @param dia Dia da semana - 1 - segunda, 2 - terca, ...
	 * @param refeicao Almoço ou Jantar
	 * @return Cardapio do dia
	 */
	public static String getCardapioDia(String html, int dia, boolean almoco) {
		//http://stackoverflow.com/questions/10771218/how-to-get-a-table-from-an-html-page-using-java
				String header ="";
				header = header + "<html><head>\n";
				header = header + "</head><body>";
				String tabela;
				String cardapio = "";
				int inicio = html.indexOf("<table");
				if (inicio==-1) {
					inicio = html.indexOf("<TABLE");
				}
				int fim = html.indexOf("</table>");
				if (fim==-1) {
					fim = html.indexOf("</TABLE>");
				}
				tabela = new String(html.substring(inicio,fim+9));
				tabela = header + tabela + "</body></html>";
				//Recuperando dia e hora da solicitação
				//Calendar ca1 = new GregorianCalendar();
				//int dia=ca1.get(Calendar.DAY_OF_WEEK);
				//SimpleDateFormat sdf = new SimpleDateFormat("H");
				//int hora = Integer.parseInt(sdf.format(new Date()));
				dia = dia +1;
				//if (dia==0) dia = 7;
				Document doc = Jsoup.parse(tabela);
				//RAFAEL
				Elements tableElements = doc.select("table");
				Elements tableRowElements = tableElements.select(":not(thead) tr");
				if ((POSICAO!=3)&&(POSICAO!=5)) { //RESTAURANTES EXCETO DA QUIMICA E PROFESSORES
				    cardapio = "";
					Element row = tableRowElements.get(dia);
		            Elements rowItems = row.select("td");
		            if (almoco) { //Almoco
		            	
		            		cardapio = rowItems.get(0).text();
		            }
		            else { //JANTAR
		            		cardapio = rowItems.get(1).text(); 
		            		if (cardapio.contains("OBSERVA")) {
		            			cardapio = "FECHADO";
		            		}
		            }
				}
				
				else { //quimica e professores
					if ((dia!=7) && (dia!=6)) {
						cardapio = "";
						Element row = tableRowElements.get(dia);
			            Elements rowItems = row.select("td");
			            if (almoco) { //ALMOÇO
			            	cardapio = rowItems.get(0).text();
			            }
			            else { //JANTAR
			            		cardapio = rowItems.get(1).text(); 
			            }
					}
					
					else {
						cardapio = "FECHADO";
					}
				}
				
				return cardapio;
	}
	
	@SuppressLint("SimpleDateFormat")
	public static String getCardapioDia(String html) {
		//http://stackoverflow.com/questions/10771218/how-to-get-a-table-from-an-html-page-using-java
		String header ="";
		header = header + "<html><head>\n";
		header = header + "</head><body>";
		String tabela;
		String cardapio = "";
		int inicio = html.indexOf("<table");
		if (inicio==-1) {
			inicio = html.indexOf("<TABLE");
		}
		int fim = html.indexOf("</table>");
		if (fim==-1) {
			fim = html.indexOf("</TABLE>");
		}
		tabela = new String(html.substring(inicio,fim+9));
		tabela = header + tabela + "</body></html>";
		//Recuperando dia e hora da solicitação
		Calendar ca1 = new GregorianCalendar();
		int dia=ca1.get(Calendar.DAY_OF_WEEK);
		SimpleDateFormat sdf = new SimpleDateFormat("H");
		int hora = Integer.parseInt(sdf.format(new Date()));
		dia = dia -1;
		if (dia==0) dia = 7;
		Document doc = Jsoup.parse(tabela);
		//RAFAEL
		Elements tableElements = doc.select("table");
		Elements tableRowElements = tableElements.select(":not(thead) tr");
		if ((POSICAO!=3)&&(POSICAO!=5)) { //RESTAURANTES EXCETO DA QUIMICA E PROFESSORES
		//for (int i = dia; i <= dia; i++) {
            cardapio = "";
			Element row = tableRowElements.get(dia);
            Elements rowItems = row.select("td");
            if ((hora<=13)||(hora>=20)) { //ALMOÇO
            	//for (int j = 0; j <= 0; j++) {
            		cardapio = rowItems.get(0).text();
            	//}
            }
            else { //JANTAR
            	//for (int j = 1; j <=1; j++) { 
            		cardapio = rowItems.get(1).text(); 
            		if (cardapio.contains("OBSERVA")) {
            			cardapio = "FECHADO";
            		}
            	//}
            }
         //}
		}
		
		else { //quimica e professores
			if ((dia!=7) && (dia!=6)) {
				cardapio = "";
				Element row = tableRowElements.get(dia);
	            Elements rowItems = row.select("td");
	            if ((hora<=13)||(hora>=20)) { //ALMOÇO
	            	//for (int j = 0; j <= 0; j++) {
	            		cardapio = rowItems.get(0).text();
	            	//}
	            }
	            else { //JANTAR
	            	//for (int j = 1; j <=1; j++) { 
	            		cardapio = rowItems.get(1).text(); 
	            	//}
	            }
			}
			
			else {
				cardapio = "FECHADO";
			}
		}
		return cardapio;
	}
	
	public boolean gravarCardapioOffline(String url, String arquivo) {
		boolean gravou = true;
		if (possuiInternet2()) {
			String resultado = "";
			HttpClient c = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			get.setHeader("Content-Type", "text/html");
			HttpResponse response = null;
						 
			try {
				response = c.execute(get);
			} catch (ClientProtocolException e) {
				Log.e("CARD", e.getMessage());
				gravou = false;
			} catch (IOException e) {
				Log.e("CARD", e.getMessage());
				gravou = false;
			}
			try {
				if (POSICAO>5) {
					resultado = EntityUtils.toString(response.getEntity(),codificacao);
				}
				else {
					resultado = EntityUtils.toString(response.getEntity(),"ISO-8859-1");
				}
				
								
			} catch (ParseException e) {
				Log.e("CARD", e.getMessage());
				gravou = false;
			} catch (IOException e) {
				Log.e("CARD", e.getMessage());
				gravou = false;
			}
			String filename = arquivo;
			File file = new File(Environment.getExternalStorageDirectory() +"/"+ pasta + "/", filename);
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					Log.e("CARD", e.getMessage());
					gravou = false;
				}
			}
			else {
				file.delete();
				try {
					file.createNewFile();
				} catch (IOException e) {
					Log.e("CARD", e.getMessage());
					gravou = false;
				}
			}
			
			try {
				if (POSICAO>5) {
					FileUtils.writeStringToFile(file, resultado,codificacao);
				}
				else {
					FileUtils.writeStringToFile(file, resultado,"ISO-8859-1");
				}
				
			} catch (IOException e) {
				Log.e("CARD", e.getMessage());
				gravou = false;
			}
		}	
		return gravou;
	}
	
	/**
	 * Carrega a String gravada em cache do dispositivo
	 * @param arquivo Arquivo de cache a ser carregado
	 * @return String contento o conteúdo do arquivo de cache
	 */
	public String carregarCardapioOffline(String arquivo) {
		String filename = arquivo;
		File file = new File(Environment.getExternalStorageDirectory() + "/" + pasta + "/", filename);
		String retorno = "";
		try {
			if (POSICAO>5) {
				retorno = FileUtils.readFileToString(file,codificacao);
			}
			else {
				retorno = FileUtils.readFileToString(file,"ISO-8859-1");
				//retorno = converterString(retorno);
			}
			
			//retorno = FileUtils.readFileToByteArray(file).toString();
		} catch (IOException e) {
			Log.e("FILE", e.getMessage());
		}
		
		//return converterString(retorno);
		return retorno;
	}
	
	/**
	 * Verifica a necessidade de atualização do cache do bandejão
	 * @return Verdadeiro caso seja necessário atualizar e falso caso contrário.
	 */
	@SuppressLint("SimpleDateFormat")
	public boolean atualizar() {
		File file = new File(Environment.getExternalStorageDirectory() + "/" + pasta + "/", "ccentral.html");
		File file2 = new File(Environment.getExternalStorageDirectory() + "/" + pasta + "/", "cfisica.html");
		File file3 = new File(Environment.getExternalStorageDirectory() + "/" + pasta + "/", "ccocesp.html");
		File file4 = new File(Environment.getExternalStorageDirectory() + "/" + pasta + "/", "cquimica.html");
		File file5 = new File(Environment.getExternalStorageDirectory() + "/" + pasta + "/", "cprofessores.html");
		File file6 = new File(Environment.getExternalStorageDirectory() + "/" + pasta + "/", "cenfermagem.html");
		File file7 = new File(Environment.getExternalStorageDirectory() + "/" + pasta + "/", "csaude.html");
		File file8 = new File(Environment.getExternalStorageDirectory() + "/" + pasta + "/", "cdireito.html");
		Date sSemanaArquivo = new Date(file.lastModified());
		Date sSemanaAgora = new Date();
		SimpleDateFormat formatador = new SimpleDateFormat("w");
		int semanaArquivo = Integer.parseInt(formatador.format(sSemanaArquivo));
		int semanaAgora = Integer.parseInt(formatador.format(sSemanaAgora));
		boolean f1 = file.exists();
		boolean f2 = file2.exists();
		boolean f3 = file3.exists();
		boolean f4 = file4.exists();
		boolean f5 = file5.exists();
		boolean f6 = file6.exists();
		boolean f7 = file7.exists();
		boolean f8 = file8.exists();
		boolean res = f1 &&  f2 && f3 && f4 && f5 && f6 && f7 && f8 ;
		if  ((semanaArquivo<semanaAgora)||(res==false)) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Verifica se existe cache para o bandejao
	 * @return Verdadeiro ou falso
	 */
	public boolean existeCache() {
		File file = new File(Environment.getExternalStorageDirectory() + "/" + pasta + "/", "ccentral.html");
		if (file.exists()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Verifica se um determinado arquivo está no cache do dispositivo.
	 * @param arquivo Arquivo de cache a ser verificado
	 * @return Verdadeiro ou falso
	 */
	public static boolean existeCache(String arquivo) {
		File file = new File(Environment.getExternalStorageDirectory() + "/" + pasta + "/", arquivo);
		if (file.exists()) {
			
			return true;
		}
		else {
			return false;
		}
	}
	
	/**
	 * Calcula a distância em linha reta entre dois pontos geográficos
	 * @param latitude Latitude da origem
	 * @param longitude Longitude da origem
	 * @param latitudePto Latitude do destino 
	 * @param longitudePto Longitude do destino
	 * @return
	 */
    public static double getDistancia(double latitude, double longitude, double latitudePto, double longitudePto){  
        double dlon, dlat, a, distancia;  
        dlon = Math.toRadians(longitudePto - longitude);  
        dlat = Math.toRadians(latitudePto - latitude);  
        a = Math.pow(Math.sin(dlat/2),2) + Math.cos(Math.toRadians(latitude)) * Math.cos(Math.toRadians(latitudePto)) * Math.pow(Math.sin(dlon/2),2);  
        distancia = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));  
        return 6378140 * distancia; /* 6378140 is the radius of the Earth in meters*/  
    }  
    
      
    /**
     * Retorna o arquivo de "settings" da aplicação
     * @param c Contexto da aplicação Androidcprofe
     * @param arquivo Nome do arquivo
     * @return Objeto Properties correspondente ao arquivo físico de propriedades (settings)
     */
    public static Properties getProperties(Context c, String arquivo) {
    	Resources resources = c.getResources();
    	AssetManager assetManager = resources.getAssets();
    	Properties properties = null;
    	try {
    	    InputStream inputStream = assetManager.open(arquivo);
    	    properties = new Properties();
    	    properties.load(inputStream);
    	} catch (IOException e) {
    	    e.printStackTrace();
    	}
    	return properties;
    }
    
    /**
     * Guarda em cache qual restaurante foi selecionado no spinner
     * @param pos Posição selecionada no spinner
     */
    public static boolean carregarCardapioOffline(int pos) {
		boolean resultado = true;
    	if (pos==0) {				
			Util.restauranteOFFLINE = "ccentral.html";
			Util.BANDEIJAO = "mock";
		}
		if (pos==1) {				
			Util.restauranteOFFLINE = "ccentral.html";
			Util.BANDEIJAO = "central";
		}
		if (pos==2) {
			Util.restauranteOFFLINE = "cfisica.html";
			Util.BANDEIJAO = "fisica";
		}
		if (pos==3) {
			Util.restauranteOFFLINE = "cquimica.html";
			Util.BANDEIJAO = "quimica";
		}
		if (pos==4) {
			Util.restauranteOFFLINE = "ccocesp.html";
			Util.BANDEIJAO = "cocesp";
		}
		if (pos==5) {
			Util.restauranteOFFLINE = "cprofessores.html";
			Util.BANDEIJAO = "professores";
		}
		if (pos==6) {
			Util.restauranteOFFLINE = "cenfermagem.html";
			Util.BANDEIJAO = "enfermagem";
		}
		if (pos==7) {
			Util.restauranteOFFLINE = "csaude.html";
			Util.BANDEIJAO = "saude";
		}
		if (pos==8) {
			Util.restauranteOFFLINE = "cdireito.html";
			Util.BANDEIJAO = "direito";
		}
		return resultado;
	}
    
    /**
     * Retorna o restaurante mais perto da posição atual do usuário
     * @param c Contexto da aplicação
     * @return Indice do bandejão mais perto, baseado nas coordenadas do GPS
     */
    public static int getRestauranteMaisPerto(Context c) {
    	Properties prop = getProperties(c,"main.properties");
    	int maisPerto = 0;
    	//CENTRAL É O MAIS PERTO?
    	double latitude = Double.parseDouble(prop.getProperty("latitude_central"));
    	double longitude = Double.parseDouble(prop.getProperty("longitude_central"));
    	double distancia = getDistancia(LATITUDE, LONGITUDE, latitude, longitude);
    	if ((getDistancia(LATITUDE, LONGITUDE, latitude, longitude))<distancia) {
    		distancia = getDistancia(LATITUDE, LONGITUDE, latitude, longitude);
    		maisPerto = Integer.parseInt(prop.getProperty("rcentral"));
    		MAISPROXIMO = "Restaurante Central";
    	}
    	//FISICA É O MAIS PERTO ?
    	latitude = Double.parseDouble(prop.getProperty("latitude_fisica"));
    	longitude = Double.parseDouble(prop.getProperty("longitude_fisica"));
    	if ((getDistancia(LATITUDE, LONGITUDE, latitude, longitude))<distancia) {
    		distancia = getDistancia(LATITUDE, LONGITUDE, latitude, longitude);
    		maisPerto = Integer.parseInt(prop.getProperty("rfisica"));
    		MAISPROXIMO = "Restaurante da Física";
    	}
    	//QUÍMICA É O MAIS PERTO?
    	latitude = Double.parseDouble(prop.getProperty("latitude_quimica"));
    	longitude = Double.parseDouble(prop.getProperty("longitude_quimica"));
    	if ((getDistancia(LATITUDE, LONGITUDE, latitude, longitude))<distancia) {
    		distancia = getDistancia(LATITUDE, LONGITUDE, latitude, longitude);
    		maisPerto = Integer.parseInt(prop.getProperty("rquimica"));
    		MAISPROXIMO = "Restaurante da Química";
    	}
    	//COCESP É O MAIS PERTO ?
    	latitude = Double.parseDouble(prop.getProperty("latitude_cocesp"));
    	longitude = Double.parseDouble(prop.getProperty("longitude_cocesp"));
    	if ((getDistancia(LATITUDE, LONGITUDE, latitude, longitude))<distancia) {
    		distancia = getDistancia(LATITUDE, LONGITUDE, latitude, longitude);
    		maisPerto = Integer.parseInt(prop.getProperty("rcocesp"));
    		MAISPROXIMO = "Restaurante COCESP";
    	}
    	//PROFESSORES É O MAIS PERTO?
    	latitude = Double.parseDouble(prop.getProperty("latitude_professores"));
    	longitude = Double.parseDouble(prop.getProperty("longitude_professores"));
    	if ((getDistancia(LATITUDE, LONGITUDE, latitude, longitude))<distancia) {
    		distancia = getDistancia(LATITUDE, LONGITUDE, latitude, longitude);
    		maisPerto = Integer.parseInt(prop.getProperty("rprofessores"));
    		MAISPROXIMO = "Restaurante dos Professores";
    	}
    	dist = distancia;
    	return maisPerto;
    }
    
    
    
    /**
     * Grava um csv interno no cache do sistema
     * @param contexto Contexto da aplicação
     * @param in InputStream de entrada
     * @param arquivo Arquivo que será gravado no cache
     * @return Retorna se a operação foi realizada com sucesso ou não.
     */
    public static boolean csv2arquivoCache(Context contexto,String arquivo) {
    	boolean resultado = true;
    	Resources resources = contexto.getResources();
    	AssetManager assetManager = resources.getAssets(); 
    	try {
    	    InputStream inputStream = assetManager.open(arquivo);
    	    File file = new File(Environment.getExternalStorageDirectory() + "/" + pasta + "/", arquivo);
    	    if (!file.exists()) {
    	    	file.createNewFile();
    	    }
    	    else {
    	    	file.delete();
    	    }
    	    
    	    OutputStream os = new FileOutputStream(file);
    	    //Novo - ****
    	    BufferedReader in = new BufferedReader(new InputStreamReader(inputStream,"UTF-8"));
    	    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(os,"UTF-8"));
    	    //****
    		
    	    	 
    	    String read;
    	    while ((read = in.readLine()) != null) {
    			out.write(read + "\r\n");
    		}
    		
    		out.flush();
    		out.close();
    		in.close();
    		inputStream.close();
    		os.close();
    	}
    	catch (FileNotFoundException e) {
			Log.e("ERRO CSV", e.getMessage());
			resultado = false;
		} catch (IOException e) {
			Log.e("ERRO IOEXCEPTION", e.getMessage());
			resultado = false;
		}
		return resultado;
    }
    
    /**
     * Grava o conteudo da URL no arquivo especificado
     * @param url URL a ser consultada
     * @param arquivo Nome do arquivo a ser gravado
     */
    public static boolean gravarEmArquivo(String url, String arquivo) {
    	boolean gravou = true;
    	Util u = new Util();
    	//u.gravarCardapioOffline(url, arquivo);
    	if (u.possuiInternet2()) {
			String resultado = "";
			HttpClient c = new DefaultHttpClient();
			HttpGet get = new HttpGet(url);
			get.setHeader("Content-Type", "text/html");
			HttpResponse response = null;
						 
			try {
				response = c.execute(get);
			} catch (ClientProtocolException e) {
				Log.e("GRAVA", e.getMessage());
				gravou = false;
			} catch (IOException e) {
				Log.e("GRAVA", e.getMessage());
				gravou = false;
			}
			try {
				resultado = EntityUtils.toString(response.getEntity());
								
			} catch (ParseException e) {
				Log.e("GRAVA", e.getMessage());
				gravou = false;
			} catch (IOException e) {
				Log.e("GRAVA", e.getMessage());
				gravou = false;
			}
			File file = new File(Environment.getExternalStorageDirectory() +"/"+ pasta + "/", arquivo);
			if (!file.exists()) {
				try {
					file.createNewFile();
				} catch (IOException e) {
					Log.e("GRAVA", e.getMessage());
					gravou = false;
				}
			}
			else {
				file.delete();
				try {
					file.createNewFile();
				} catch (IOException e) {
					Log.e("GRAVA", e.getMessage());
					gravou = false;
				}
			}
			
			try {
				FileUtils.writeStringToFile(file, resultado);
			} catch (IOException e) {
				Log.e("FILE",e.getMessage());
				gravou = false;
			}
		}
    	return gravou;
    }
    
    
    
    /**
     * Retira todos os caracteres acentuados da String original, para evi-
     * tar erros de codificação.
     * @param original String original
     * @return String com os caracteres substituídos.
     */
    public static String converterString(String original) {
    	String convertida = "";
    	
    	//ç
    	convertida = original.replace("\u00e7", "\u0063");
    	convertida = convertida.replace("\u00c7", "\u0043");
    	//á
    	convertida = convertida.replace("\u00c1", "\u0041");
    	convertida = convertida.replace("\u00e1", "\u0061");
    	//é
    	convertida = convertida.replace("\u00c9", "\u0045");
    	convertida = convertida.replace("\u00e9", "\u0065");
    	//í
    	convertida = convertida.replace("\u00cd", "\u0049");
    	convertida = convertida.replace("\u00ed", "\u0069");
    	//ó
    	convertida = convertida.replace("\u00f3", "\u006f");
    	convertida = convertida.replace("\u00c7", "\u0043");
    	//ã
    	convertida = convertida.replace("\u00c3", "\u0041");
    	convertida = convertida.replace("\u00e3", "\u0061");
    	//õ
    	convertida = convertida.replace("\u00d5", "\u004f");
    	convertida = convertida.replace("\u00f5", "\u006f");
    	//ô
    	convertida = convertida.replace("\u00f4", "\u004f");
    	convertida = convertida.replace("\u00d4", "\u006f");
    	
    	//ê
    	convertida = convertida.replace("\u00ca", "\u0045");
    	convertida = convertida.replace("\u00ea", "\u0065");
    	//à
    	convertida = convertida.replace("\u00c0", "\u0041");
    	convertida = convertida.replace("\u00e0", "\u0061");
    	//â
    	convertida = convertida.replace("\u00c2", "\u0041");
    	convertida = convertida.replace("\u00e2", "\u0061");
    	//String str = Normalizer.normalize(str, Normalizer.Form.NFD);
    	//str = str.replaceAll("[^\\p{ASCII}]", "");
    	
    	return convertida;
    }

    public boolean possuiInternet(Context context){
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		  if (connectivity != null) 
		  {
			  NetworkInfo[] info = connectivity.getAllNetworkInfo();
			  if (info != null) 
				  for (int i = 0; i < info.length; i++) 
					  if (info[i].getState() == NetworkInfo.State.CONNECTED)
					  {
						  return true;
					  }

		  }
		  return false;
	}
    
  
}