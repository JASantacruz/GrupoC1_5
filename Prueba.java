package Practica_Laberinto;


import java.util.*;
import java.util.List;

import javax.imageio.ImageIO;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.awt.image.BufferedImage;
import java.awt.*;


import java.io.*;

public class Prueba {
	public static final Scanner TECLADO = new Scanner (System.in);

	public static void main(String[] args) throws IOException, ParseException {
		int n_filas;
		int n_columnas;
		Casilla[][] matriz;


		Laberinto lab = new Laberinto();
		//importarLaberinto(lab);
		n_filas=numero_filas_columnas("filas");	
		n_columnas=numero_filas_columnas("columnas");
		matriz = new Casilla[n_filas][n_columnas];
		lab.setListaCasillas(matriz);
		lab.setColumnas(n_columnas);
		lab.setFilas(n_filas);
		lab.inicializar_matriz();
		algoritmo_wilson(lab);

		System.out.println("\n\n\nResultado final:");
		mostrar_matriz(lab);
		crear_imagen(lab);
		crear_json(lab);


	}
	public static void mostrar_matriz(Laberinto lab) {
		for(int i=0; i<lab.getListaCasillas().length; i++) {
			for(int j=0; j<lab.getListaCasillas()[0].length; j++) {
				System.out.println(lab.getListaCasillas()[i][j].toString());
			}
		}
	}
	public static int numero_filas_columnas (String filas_o_columnas) {
		int numero=0;
		boolean valido = false;
		System.out.println("Introduzca el número de "+filas_o_columnas+" permitido.");
		do{
			try {				
				numero=TECLADO.nextInt();
				if(numero<1) throw new InputMismatchException();
				valido=true;
			}catch (InputMismatchException e) {
				System.out.println("El tipo de entrada no ha sido válido. Intente otra vez.");
				valido=false;
				TECLADO.nextLine();
			}
		}while(!valido);
		return numero;
	}

	public static void algoritmo_wilson(Laberinto lab){
		LinkedList<Casilla> casillas_elegidas = new LinkedList<Casilla>();
		Stack<Casilla> camino_seguido = new Stack<Casilla>();
		elegir_destino(casillas_elegidas, lab.getListaCasillas());
		do {
			elegir_posicion_inicio(casillas_elegidas, lab, camino_seguido);
			crear_camino(casillas_elegidas, lab, camino_seguido);
		}while (casillas_elegidas.size()<(lab.getListaCasillas().length*lab.getListaCasillas()[0].length));


	}
	public static void elegir_destino(LinkedList<Casilla> casillas_elegidas, Casilla[][] matriz) {
		Random aleatorio = new Random();
		int coordenada_i = aleatorio.nextInt(matriz.length);
		int coordenada_j = aleatorio.nextInt(matriz[0].length);
		casillas_elegidas.add(matriz[coordenada_i][coordenada_j]);
	}
	public static void elegir_posicion_inicio(LinkedList<Casilla> casillas_elegidas, Laberinto lab, Stack<Casilla> camino_seguido) {
		Random aleatorio = new Random();
		int coordenada_i, coordenada_j;
		Casilla casilla_candidata;
		do {
			coordenada_i = aleatorio.nextInt(lab.getListaCasillas().length);
			coordenada_j = aleatorio.nextInt(lab.getListaCasillas()[0].length);
			casilla_candidata = lab.getListaCasillas()[coordenada_i][coordenada_j];
		}while (casillas_elegidas.contains(casilla_candidata));
		camino_seguido.add(casilla_candidata);
	}
	public static void crear_camino(LinkedList<Casilla> casillas_elegidas, Laberinto lab, Stack<Casilla> camino_seguido) {
		Random aleatorio = new Random(); 
		Casilla actual, problematica, aux1 = null,aux2 = null;
		int proxima_direccion;
		actual=camino_seguido.pop();
		while(!casillas_elegidas.contains(actual)) {
			do {
				proxima_direccion = aleatorio.nextInt(4);
			}while (!proxima_direccion_es_posible(proxima_direccion, lab, actual, aux1));

			actual.abrir_pared(proxima_direccion);
			aux1=actual;
			camino_seguido.add(actual);
			actual = cambiar_actual(proxima_direccion, lab, actual);
			abrir_pared_origen(proxima_direccion, lab, actual);

			if(camino_seguido.contains(actual)) {
				problematica = actual;
				cerrar_pared_origen(proxima_direccion, actual);
				actual = camino_seguido.peek();
				while(!problematica.equals(actual)) {
					actual.cerrar_todas_paredes();
					aux2 = actual;
					actual = camino_seguido.pop();
				}
				actual.cerrar_pared(cual_es_direccion_problematica(aux2,problematica));
			}
		}
		while(camino_seguido.size()>0) {
			casillas_elegidas.add(camino_seguido.pop());
		}

	}

	public static boolean proxima_direccion_es_posible(int proxima_direccion, Laberinto lab, Casilla actual, Casilla aux) {
		int posicion[] = new int[2];
		posicion = actual.get_posicion();
		try {
			switch (proxima_direccion) {
			case 0:
				if(posicion[0]==0 || (actual.get_posicion()[0]-1==aux.get_posicion()[0] && (actual.get_posicion()[1])==aux.get_posicion()[1])) return false;
				break;
			case 1:
				if(posicion[1]==(lab.getListaCasillas()[0].length-1) || ((actual.get_posicion()[0])==aux.get_posicion()[0] && (actual.get_posicion()[1]+1)==aux.get_posicion()[1])) return false;
				break;
			case 2:
				if(posicion[0]==(lab.getListaCasillas().length-1) || (actual.get_posicion()[0]+1==aux.get_posicion()[0] && (actual.get_posicion()[1])==aux.get_posicion()[1])) return false;
				break;
			case 3:
				if(posicion[1]==0 || ((actual.get_posicion()[0])==aux.get_posicion()[0] && (actual.get_posicion()[1]-1)==aux.get_posicion()[1])) return false;
			}
		} catch (NullPointerException e) {
			return true;
		}
		return true;
	}
	public static Casilla cambiar_actual(int proxima_direccion, Laberinto lab, Casilla actual) {
		int posicion[] = new int[2];
		posicion = actual.get_posicion();
		switch (proxima_direccion) {
		case 0:
			actual=lab.getListaCasillas()[posicion[0]-1][posicion[1]];
			break;
		case 1:
			actual=lab.getListaCasillas()[posicion[0]][posicion[1]+1];
			break;
		case 2:
			actual=lab.getListaCasillas()[posicion[0]+1][posicion[1]];
			break;
		case 3:
			actual=lab.getListaCasillas()[posicion[0]][posicion[1]-1];
			break;
		}
		return actual;
	}
	public static void abrir_pared_origen(int proxima_direccion, Laberinto lab, Casilla actual) {
		switch(proxima_direccion) {
		case 0:
			actual.abrir_pared(2);
			break;
		case 1:
			actual.abrir_pared(3);
			break;
		case 2:
			actual.abrir_pared(0);
			break;
		case 3:
			actual.abrir_pared(1);
			break;
		}
	}
	public static void cerrar_pared_origen(int proxima_direccion, Casilla actual) {
		switch(proxima_direccion) {
		case 0:
			actual.cerrar_pared(2);
			break;
		case 1:
			actual.cerrar_pared(3);
			break;
		case 2:
			actual.cerrar_pared(0);
			break;
		case 3:
			actual.cerrar_pared(1);
			break;
		}
	}

	public static int cual_es_direccion_problematica(Casilla actual, Casilla problematica) {
		int direccion_i, direccion_j;
		direccion_i = actual.get_posicion()[0] - problematica.get_posicion()[0]; //arriba=-1, abajo=1
		direccion_j = actual.get_posicion()[1] - problematica.get_posicion()[1]; //izq=-1, drcha=1
		if (direccion_i == 0) {
			return 2-direccion_j;
		}else {
			return 1+direccion_i;
		}
	}

	public static void crear_imagen(Laberinto laberinto) throws IOException {
		int width = 500;
		int height = 500;
		int alto_casilla = height / laberinto.getFilas(); //Alto de cada casilla
		int ancho_casilla = width / laberinto.getColumnas(); //Ancho de cada casilla
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D lab = img.createGraphics();
		lab.setColor(Color.white);
		lab.fillRect(1, 1, width-2, height-2); //Para que el grosor de cada borde sea de 1 px
		lab.setColor(Color.black);
		for(int i = 0; i < laberinto.getFilas(); i++) 
			for(int j = 0; j < laberinto.getColumnas(); j++) 
				for(int k = 0; k < 4; k++) 
					if(laberinto.getListaCasillas()[i][j].get_pared(k)==false) {
						switch(k) {
						case 0:
							lab.drawLine(j*ancho_casilla, i*alto_casilla, (j*ancho_casilla)+ancho_casilla, i*alto_casilla); //pinta arriba
							break;
						case 1:
							lab.drawLine((j*ancho_casilla)+ancho_casilla, i*alto_casilla, (j*ancho_casilla)+ancho_casilla, (i*alto_casilla)+alto_casilla); //pinta derecha
							break;
						}
					}
		lab.dispose();
		File file = new File("laberintoFinal.jpg");
		ImageIO.write(img, "jpg", file);
	}

	public static void crear_json(Laberinto lab) throws FileNotFoundException {
		JSONObject json1 = new JSONObject();
		JSONObject json2 = new JSONObject(); 
		JSONObject json3;
		for(int i = 0;i<lab.getListaCasillas().length;i++) {
			for(int j = 0;j<lab.getListaCasillas()[0].length;j++) {
				json3 = new JSONObject(); 
				json3.put("value", lab.getListaCasillas()[i][j].getValor());
				json3.put("neighbors", lab.getListaCasillas()[i][j].cambiarArrayALista());
				json2.put("("+lab.getListaCasillas()[i][j].get_posicion()[0]+", "+lab.getListaCasillas()[i][j].get_posicion()[1]+")",json3);
			}
		}
		json1.put("rows", lab.getFilas());
		json1.put("cols", lab.getColumnas());
		json1.put("max_n", lab.getMax_n());
		json1.put("mov", lab.getMove());
		json1.put("id_mov", lab.getId_move());
		json1.put("cells",json2);
		System.out.println(json1.toString());

		try(PrintWriter puntoJson = new PrintWriter("laberintoFinalATope.json")){
			puntoJson.println(json1);
		}
	}

	public static void importarLaberinto(Laberinto laberinto) throws IOException, ParseException {
		String jason = "";
		int[]aux2 = new int[2];
		JSONObject json1 = null;
		JSONObject json2 = new JSONObject();
		JSONObject json3 = null;
		JSONParser jparse = new JSONParser();
		String aux1 = "";
		Casilla[][] listaCasillas;
		boolean[] aux3 = new boolean[4];
		String linea;
		String aux4 = "";
		
		File archivo = new File ("C:\\Users\\ferna\\eclipse-workspace\\Laberinto\\puzzle_10x20.json");
		FileReader fr = new FileReader (archivo);
		BufferedReader br = new BufferedReader(fr);
		while ((linea = br.readLine()) != null) {
            jason += linea;
        }
		System.out.println(jason);
		json1 = (JSONObject)jparse.parse(jason);
		System.out.println(json1.toString());
		laberinto.setColumnas(((Long)json1.get("cols")).intValue());
		laberinto.setFilas(((Long)json1.get("rows")).intValue());
		laberinto.crearMatriz();
		laberinto.inicializar_matriz();
		laberinto.setId_move((List<String>)json1.get("mov"));
		laberinto.setMove((List<int[]>)json1.get("id_mov"));
		laberinto.setMax_n(((Long)json1.get("max_n")).intValue());	
		json2 = (JSONObject)json1.get("cells");		
		for(int i = 0;i<laberinto.getListaCasillas().length;i++) {
			for(int j = 0;j<laberinto.getListaCasillas()[0].length;j++) {
				aux1= "("+i+", "+j+")";
				json3 = (JSONObject)json2.get(aux1);
				laberinto.getListaCasillas()[i][j].setValor(((Long)json3.get("value")).intValue());
				aux3= cambiarListToBoolean((JSONArray)json3.get("neighbors"));
				laberinto.getListaCasillas()[i][j].setParedes(aux3);
			}
		}
	}
	public static boolean[] cambiarListToBoolean(JSONArray lista) {
		boolean[] aux = new boolean[4];
		System.out.println(lista.toString());
		
		aux[0] = (boolean) lista.get(0);
		aux[1] = (boolean) lista.get(1);
		aux[2] = (boolean) lista.get(2);
		aux[3] = (boolean) lista.get(3);
		
		return aux;	
	}
	
	
}
