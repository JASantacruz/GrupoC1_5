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

@SuppressWarnings({"unused", "unchecked", "resource", "static-access"})

public class Principal implements Constantes {
	public static final Scanner TECLADO = new Scanner (System.in);
	public static void main(String[] args) throws LaberintoIncorrectoException {
		int n_filas=0, n_columnas=0, opcion=0;		
		Casilla[][] matriz = null;
		Problema problema = new Problema();
		boolean seguir = true, error=false;
		String ruta, estrategia="";
		LinkedList<Casilla> casillas_solucion = new LinkedList<Casilla>();
		LinkedList<Casilla> casillas_frontera = new LinkedList<Casilla>();
		LinkedList<Casilla> casillas_visitadas = new LinkedList<Casilla>();

		do {
			do {
				mostrar_opciones();
				try {
					opcion = TECLADO.nextInt();
					if (opcion<1 || opcion>3) throw new IOException();
					error=false;
				}catch(Exception e) {
					System.out.println("ERROR El valor introducido deber ser un numero entero del 1 al 3.");
					error=true;
				}
				TECLADO.nextLine();
			}while(error);
			switch(opcion) {
			case 1:
				try {
					crear_matriz(n_filas, n_columnas, matriz, problema);
				}catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("Se ha creado el laberinto correctamente.");
				break;
			case 2:
				System.out.println("Por favor, escriba la ruta del archivo.");
				ruta = TECLADO.nextLine();
				try {
					importar_problema(problema, ruta);
					System.out.println("Se ha importado correctamente el laberinto.");
					buscar_error(problema.getLaberinto());
					crear_imagen(problema.getLaberinto());
					estrategia = menu_estrategia();
					while(!estrategia.equals("EXIT")) {
						casillas_solucion = algoritmo_busqueda(problema, 1000000, estrategia, casillas_frontera, casillas_visitadas);
						pintar_laberinto_debug(casillas_solucion, casillas_frontera, casillas_visitadas, problema.getLaberinto(), estrategia);
						casillas_frontera = new LinkedList<Casilla>();
						casillas_solucion = new LinkedList<Casilla>();
						casillas_visitadas = new LinkedList<Casilla>();

						estrategia = menu_estrategia();
					}					
				}catch (LaberintoIncorrectoException e){
					System.out.println("ERROR "+e.getMessage());
				}catch(NoSolutionException e) {
					System.out.println("ERROR "+e.getMessage());
				}catch(IOException e) {
					System.out.println("ERROR "+e.getMessage());
				}catch(ParseException e) {
					e.printStackTrace();
				}
				break;
			case 3:
				System.out.println("Hasta pronto!");
				seguir=false;
			}
		}while(seguir);
	}

	public static void mostrar_opciones(){
		System.out.println("Elija una de las opciones:\n\n1) Crear un laberinto.\n2) Importar un laberinto.\n3) Salir.\n\n");
	}
	public static void mostrar_opciones_estrategia() {
		System.out.println("Por favor, elija una estrategia:\n");
		System.out.println("1- Anchura.\n2- Profundidad acotada (1000000).\n3- Coste uniforme.\n4- Voraz.\n5- A*.\n6- Salir");
	}

	public static String menu_estrategia() {
		int opcion;
		String estrategia="";
		boolean seguir = false;
		do {
			try {
				mostrar_opciones_estrategia();
				opcion = TECLADO.nextInt();
				if (opcion<1 || opcion>6) throw new IOException();
				estrategia = traducir_estrategia(opcion);
			}catch (Exception e) {
				System.out.println("El valor introducido debe ser un numero entero entre el 1 y el 6.");
				seguir = true;
			}
		}while (seguir);
		return estrategia;
	}
	public static String traducir_estrategia(int opcion) {
		switch(opcion) {
		case 1:
			return "BREATH";
		case 2:
			return "DEPTH";
		case 3:
			return "UNIFORM";
		case 4:
			return "GREEDY";
		case 5:
			return "A";
		case 6:
			return "EXIT";
		default:
			return "";
		}
	}

	public static int numero_filas_columnas (String filas_o_columnas) {
		int numero=0;
		boolean valido = false;
		System.out.println("Introduzca el numero de "+filas_o_columnas+" permitido.");
		do{
			try {				
				numero=TECLADO.nextInt();
				if(numero<1) throw new InputMismatchException();
				valido=true;
			}catch (InputMismatchException e) {
				System.out.println("El tipo de entrada no ha sido valido. Intente otra vez.");
				valido=false;
				TECLADO.nextLine();
			}
		}while(!valido);
		return numero;
	}

	public static void crear_matriz(int n_filas, int n_columnas, Casilla[][] matriz, Problema problema) throws IOException, LaberintoIncorrectoException {
		n_filas=numero_filas_columnas("filas");	
		n_columnas=numero_filas_columnas("columnas");
		matriz = new Casilla[n_filas][n_columnas];

		problema.setLaberinto(new Laberinto());

		problema.getLaberinto().setListaCasillas(matriz);
		problema.getLaberinto().setColumnas(n_columnas);
		problema.getLaberinto().setFilas(n_filas);
		problema.getLaberinto().inicializar_matriz();
		algoritmo_wilson(problema.getLaberinto());
		crear_imagen(problema.getLaberinto());
		crear_json_maze(problema.getLaberinto());
		buscar_error(problema.getLaberinto());
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
		int coordenada_i, coordenada_j,aux=0;
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
		int alto_casilla = 20; 
		int ancho_casilla = 20; 
		int width = ancho_casilla*laberinto.getColumnas();
		int height = alto_casilla*laberinto.getFilas();
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D lab = img.createGraphics();
		lab.setColor(Color.white);
		lab.fillRect(1, 1, width-2, height-2); 
		lab.setColor(Color.black);
		for(int i = 0; i < laberinto.getFilas(); i++) {
			for(int j = 0; j < laberinto.getColumnas(); j++) {
				switch(laberinto.getListaCasillas()[i][j].getValor()) {
				case 0:
					lab.setColor(new Color(184, 184, 184));
					lab.fillRect(j*ancho_casilla, i*alto_casilla, ancho_casilla, alto_casilla);
					break;
				case 1:
					lab.setColor(new Color(192, 128, 128));
					lab.fillRect(j*ancho_casilla, i*alto_casilla, ancho_casilla, alto_casilla);
					break;
				case 2:
					lab.setColor(new Color(128, 192, 128));
					lab.fillRect(j*ancho_casilla, i*alto_casilla, ancho_casilla, alto_casilla);
					break;
				case 3:
					lab.setColor(new Color(128, 128, 192));
					lab.fillRect(j*ancho_casilla, i*alto_casilla, ancho_casilla, alto_casilla);
					break;
				}
				for(int k = 0; k < 4; k++) 
					if(laberinto.getListaCasillas()[i][j].get_pared(k)==false) {
						lab.setColor(Color.black);
						switch(k) {
						case 0:
							lab.drawLine(j*ancho_casilla, i*alto_casilla, (j*ancho_casilla)+ancho_casilla, i*alto_casilla); //pinta arriba
							break;
						case 1:
							lab.drawLine((j*ancho_casilla)+ancho_casilla, i*alto_casilla, (j*ancho_casilla)+ancho_casilla, (i*alto_casilla)+alto_casilla); //pinta derecha
							break;
						case 2:
							lab.drawLine((j*ancho_casilla)+ancho_casilla, (i*alto_casilla)+alto_casilla, j*ancho_casilla, (i*alto_casilla)+alto_casilla); //pinta abajo
							break;
						case 3:
							lab.drawLine(j*ancho_casilla, (i*alto_casilla)+alto_casilla, j*ancho_casilla, i*alto_casilla); //pinta derecha
							break;
						}
					}
			}
		}
		lab.dispose();
		File file = new File("laberinto"+laberinto.getFilas()+"x"+laberinto.getColumnas()+".jpg");
		ImageIO.write(img, "jpg", file);
	}
	private static void pintar_laberinto_debug(LinkedList<Casilla> casillas_solucion, LinkedList<Casilla> casillas_frontera, LinkedList<Casilla> casillas_visitadas, Laberinto laberinto, String estrategia) throws IOException {
		int alto_casilla = 20; 
		int ancho_casilla = 20; 
		int width = ancho_casilla*laberinto.getColumnas();
		int height = alto_casilla*laberinto.getFilas();

		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D lab = img.createGraphics();
		lab.setColor(Color.white);
		lab.fillRect(1, 1, width-2, height-2); 
		for(int i = 0; i < laberinto.getFilas(); i++) {
			for(int j = 0; j < laberinto.getColumnas(); j++) {
				switch(laberinto.getListaCasillas()[i][j].getValor()) {
				case 0:
					lab.setColor(new Color(184, 184, 184));
					lab.fillRect(j*ancho_casilla, i*alto_casilla, ancho_casilla, alto_casilla);
					break;
				case 1:
					lab.setColor(new Color(192, 128, 128));
					lab.fillRect(j*ancho_casilla, i*alto_casilla, ancho_casilla, alto_casilla);
					break;
				case 2:
					lab.setColor(new Color(128, 192, 128));
					lab.fillRect(j*ancho_casilla, i*alto_casilla, ancho_casilla, alto_casilla);
					break;
				case 3:
					lab.setColor(new Color(128, 128, 192));
					lab.fillRect(j*ancho_casilla, i*alto_casilla, ancho_casilla, alto_casilla);
					break;
				}
			}
		}
		for(int i = 0; i < casillas_frontera.size(); i++) {
			int[] pos = casillas_frontera.get(i).get_posicion();
			lab.setColor(Color.blue);
			lab.fillRect(pos[1]*ancho_casilla, pos[0]*alto_casilla, ancho_casilla, alto_casilla);
		}

		for(int i = 0; i < casillas_visitadas.size(); i++) {
			int[] pos = casillas_visitadas.get(i).get_posicion();
			lab.setColor(Color.green);
			lab.fillRect(pos[1]*ancho_casilla, pos[0]*alto_casilla, ancho_casilla, alto_casilla);
		}


		for(int i = 0; i < casillas_solucion.size(); i++) {
			int[] pos = casillas_solucion.get(i).get_posicion();
			lab.setColor(Color.red);
			lab.fillRect(pos[1]*ancho_casilla, pos[0]*alto_casilla, ancho_casilla, alto_casilla);
		}

		for(int i = 0; i < laberinto.getFilas(); i++) {
			for(int j = 0; j < laberinto.getColumnas(); j++) {
				for(int k = 0; k < 4; k++) 
					if(laberinto.getListaCasillas()[i][j].get_pared(k)==false) {
						lab.setColor(Color.black);
						switch(k) {
						case 0:
							lab.drawLine(j*ancho_casilla, i*alto_casilla, (j*ancho_casilla)+ancho_casilla, i*alto_casilla); 
							break;
						case 1:
							lab.drawLine((j*ancho_casilla)+ancho_casilla, i*alto_casilla, (j*ancho_casilla)+ancho_casilla, (i*alto_casilla)+alto_casilla); 
							break;
						case 2:
							lab.drawLine((j*ancho_casilla)+ancho_casilla, (i*alto_casilla)+alto_casilla, j*ancho_casilla, (i*alto_casilla)+alto_casilla); 
							break;
						case 3:
							lab.drawLine(j*ancho_casilla, (i*alto_casilla)+alto_casilla, j*ancho_casilla, i*alto_casilla); 
							break;
						}
					}
			}
		}

		lab.dispose();
		File file = new File("solution_"+laberinto.getFilas()+"x"+laberinto.getColumnas()+"_"+estrategia+".jpg");
		ImageIO.write(img, "jpg", file);
	}


	public static void crear_json_maze(Laberinto lab) throws FileNotFoundException {
		JSONObject json1 = new JSONObject();
		JSONObject json2 = new JSONObject(); 
		JSONObject json3;
		LinkedList<LinkedList<Integer>>auxiliar = new LinkedList<LinkedList<Integer>>();
		String aux = "laberinto"+lab.getFilas()+"x"+lab.getColumnas()+"_maze.json";

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
		for (int i=0; i<lab.getMove().size(); i++) {
			LinkedList<Integer> lista_auxiliar = new LinkedList<Integer>();
			lista_auxiliar.add(lab.getMove().get(i)[0]);
			lista_auxiliar.add(lab.getMove().get(i)[1]);
			auxiliar.add(lista_auxiliar);
		}
		json1.put("mov", auxiliar);
		json1.put("id_mov", lab.getId_move());
		json1.put("cells",json2);

		try(PrintWriter puntoJson = new PrintWriter(aux)){
			puntoJson.println(json1);
		}
		crear_json(lab,aux);
	}
	public static void crear_json(Laberinto lab,String ruta) throws FileNotFoundException {
		JSONObject json = new JSONObject();
		json.put("INITIAL", "("+lab.getListaCasillas()[0][0].get_posicion()[0]+", "+lab.getListaCasillas()[0][0].get_posicion()[1]+")");
		json.put("OBJETIVE", "("+lab.getListaCasillas()[lab.getListaCasillas().length-1][lab.getListaCasillas()[0].length-1].get_posicion()[0]+", "+lab.getListaCasillas()[lab.getListaCasillas().length-1][lab.getListaCasillas()[0].length-1].get_posicion()[1]+")");
		json.put("MAZE", ruta);
		try(PrintWriter puntoJson = new PrintWriter("laberinto"+lab.getFilas()+"x"+lab.getColumnas()+".json")){
			puntoJson.println(json);
		}
	}

	public static void importar_problema(Problema problema, String ruta) throws IOException, ParseException {
		JSONObject json = null;
		JSONParser jparse = new JSONParser();
		String jason = "";
		String linea, rutaMaze, inicio, destino;
		int cont =0;
		File archivo = new File (ruta);
		FileReader fr = new FileReader (archivo);
		BufferedReader br = new BufferedReader(fr);
		for (int x=ruta.length()-1;x>=0;x--) {
			if(ruta.charAt(x)==('\\')) {
				break;
			}
			cont++;
		}  
		rutaMaze = ruta.substring(0,ruta.length()-1-cont)+"\\";
		while ((linea = br.readLine()) != null) {
			jason += linea;
		}
		json = (JSONObject)jparse.parse(jason);
		inicio = (String)json.get("INITIAL"); 
		destino = (String)json.get("OBJETIVE");
		rutaMaze += (String)json.get("MAZE");
		importar_laberinto(problema,rutaMaze,inicio,destino);		
	}
	public static void importar_laberinto(Problema problema, String ruta, String inicio, String destino) throws IOException, ParseException {
		String jason = "",aux1 = "",aux4 = "";
		int[]aux2 = new int[2];
		JSONObject json1 = null;
		JSONObject json2 = new JSONObject();
		JSONObject json3 = null;
		JSONParser jparse = new JSONParser();
		Casilla[][] listaCasillas;
		boolean[] aux3 = new boolean[4];
		String linea;
		File archivo = new File (ruta);
		FileReader fr = new FileReader (archivo);
		BufferedReader br = new BufferedReader(fr);

		while ((linea = br.readLine()) != null) {
			jason += linea;
		}
		json1 = (JSONObject)jparse.parse(jason);
		problema.getLaberinto().setColumnas(((Long)json1.get("cols")).intValue());
		problema.getLaberinto().setFilas(((Long)json1.get("rows")).intValue());
		problema.getLaberinto().crearMatriz();
		problema.getLaberinto().inicializar_matriz();
		problema.getLaberinto().setId_move((List<String>)json1.get("mov"));
		problema.getLaberinto().setMove((List<int[]>)json1.get("id_mov"));
		problema.getLaberinto().setMax_n(((Long)json1.get("max_n")).intValue());
		json2 = (JSONObject)json1.get("cells");
		for(int i = 0;i<problema.getLaberinto().getListaCasillas().length;i++) {
			for(int j = 0;j<problema.getLaberinto().getListaCasillas()[0].length;j++) {
				aux1= "("+i+", "+j+")";
				json3 = (JSONObject)json2.get(aux1);
				problema.getLaberinto().getListaCasillas()[i][j].setValor(((Long)json3.get("value")).intValue());
				aux3= cambiar_list_to_boolean((JSONArray)json3.get("neighbors"));
				problema.getLaberinto().getListaCasillas()[i][j].setParedes(aux3);
				if(aux1.equals(inicio)) {
					problema.getOrigen().setValor(((Long)json3.get("value")).intValue());
					problema.getOrigen().setParedes(aux3);
					aux2[0]=i;
					aux2[1]=j;
					problema.getOrigen().set_posicion(aux2);
				}else if(aux1.equals(destino)) {
					problema.getDestino().setValor(((Long)json3.get("value")).intValue());
					problema.getDestino().setParedes(aux3);
					aux2[0]=i;
					aux2[1]=j;
					problema.getDestino().set_posicion(aux2);
				}
			}
		}
	}

	public static boolean[] cambiar_list_to_boolean(JSONArray lista) {
		boolean[] aux = new boolean[4];

		aux[0] = (boolean) lista.get(0);
		aux[1] = (boolean) lista.get(1);
		aux[2] = (boolean) lista.get(2);
		aux[3] = (boolean) lista.get(3);

		return aux;	
	}
	public static void buscar_error (Laberinto lab) throws LaberintoIncorrectoException{
		for (int i=0; i<lab.getFilas(); i++) {
			for (int j=0; j<lab.getColumnas(); j++) {
				if (i==0) {
					if (lab.getListaCasillas()[i][j].get_pared(0)
							|| (lab.getListaCasillas()[i][j].get_pared(2) != lab.getListaCasillas()[i+1][j].get_pared(0))) 
						throw new LaberintoIncorrectoException("["+i+","+j+"]");

					if (j==0) {
						if ((lab.getListaCasillas()[i][j].get_pared(1) != lab.getListaCasillas()[i][j+1].get_pared(3))
								||lab.getListaCasillas()[i][j].get_pared(3)) 
							throw new LaberintoIncorrectoException("["+i+","+j+"]");
					}else if (j==lab.getColumnas()-1) {
						if (lab.getListaCasillas()[i][j].get_pared(1)
								||(lab.getListaCasillas()[i][j].get_pared(3) != lab.getListaCasillas()[i][j-1].get_pared(1))) 
							throw new LaberintoIncorrectoException("["+i+","+j+"]");
					}else {
						if ((lab.getListaCasillas()[i][j].get_pared(1) != lab.getListaCasillas()[i][j+1].get_pared(3))
								||(lab.getListaCasillas()[i][j].get_pared(3) != lab.getListaCasillas()[i][j-1].get_pared(1))) 
							throw new LaberintoIncorrectoException("["+i+","+j+"]");
					}
				}else if (i==lab.getFilas()-1) {
					if ((lab.getListaCasillas()[i][j].get_pared(0)!= lab.getListaCasillas()[i-1][j].get_pared(2))
							|| lab.getListaCasillas()[i][j].get_pared(2)) 
						throw new LaberintoIncorrectoException("["+i+","+j+"]");

					if (j==0) {
						if ((lab.getListaCasillas()[i][j].get_pared(1) != lab.getListaCasillas()[i][j+1].get_pared(3))
								||lab.getListaCasillas()[i][j].get_pared(3)) 
							throw new LaberintoIncorrectoException("["+i+","+j+"]");
					}else if (j==lab.getColumnas()-1) {
						if (lab.getListaCasillas()[i][j].get_pared(1)
								||(lab.getListaCasillas()[i][j].get_pared(3) != lab.getListaCasillas()[i][j-1].get_pared(1))) 
							throw new LaberintoIncorrectoException("["+i+","+j+"]");
					}else {
						if ((lab.getListaCasillas()[i][j].get_pared(1) != lab.getListaCasillas()[i][j+1].get_pared(3))
								||(lab.getListaCasillas()[i][j].get_pared(3) != lab.getListaCasillas()[i][j-1].get_pared(1))) 
							throw new LaberintoIncorrectoException("["+i+","+j+"]");
					}
				}else {					
					if ((lab.getListaCasillas()[i][j].get_pared(0)!= lab.getListaCasillas()[i-1][j].get_pared(2))
							|| (lab.getListaCasillas()[i][j].get_pared(2) != lab.getListaCasillas()[i+1][j].get_pared(0))) 
						throw new LaberintoIncorrectoException("["+i+","+j+"]");

					if (j==0) {
						if ((lab.getListaCasillas()[i][j].get_pared(1) != lab.getListaCasillas()[i][j+1].get_pared(3))
								||lab.getListaCasillas()[i][j].get_pared(3))
							throw new LaberintoIncorrectoException("["+i+","+j+"]");
					}else if (j==lab.getColumnas()-1) {
						if (lab.getListaCasillas()[i][j].get_pared(1)
								||(lab.getListaCasillas()[i][j].get_pared(3) != lab.getListaCasillas()[i][j-1].get_pared(1))) 
							throw new LaberintoIncorrectoException("["+i+","+j+"]");
					}else {
						if ((lab.getListaCasillas()[i][j].get_pared(1) != lab.getListaCasillas()[i][j+1].get_pared(3))
								||(lab.getListaCasillas()[i][j].get_pared(3) != lab.getListaCasillas()[i][j-1].get_pared(1))) 
							throw new LaberintoIncorrectoException("["+i+","+j+"]");
					}
				}
			}
		}
	}
	public static LinkedList<Casilla> algoritmo_busqueda (Problema problema, int profundidad_max, String st, LinkedList<Casilla> casillas_frontera, LinkedList<Casilla> casillas_visitadas) throws NoSolutionException {

		LinkedList<Nodo> hijos = new LinkedList<Nodo>();
		LinkedList<Casilla> camino = new LinkedList<Casilla>();
		PriorityQueue<Nodo> frontera = new PriorityQueue<Nodo>();

		boolean solucion=false;

		Nodo nodo = new Nodo();
		nodo.setPadre(null);
		nodo.setEstado(problema.getOrigen());
		nodo.setCosto(0);
		nodo.setProfundidad(0);
		nodo.setAccion(null);
		nodo.setHeuristica(calcular_heuristica(problema, nodo));
		nodo.setValor(calcular_valor(st, nodo, profundidad_max));

		frontera.add(nodo);

		while(!frontera.isEmpty() && !solucion) {
			nodo = frontera.poll();
			if (problema.getDestino().equals(nodo.getEstado())) {
				solucion=true;
			}else if( !casillas_visitadas.contains(nodo.getEstado()) && nodo.getProfundidad()<profundidad_max) {
				casillas_visitadas.add(nodo.getEstado());
				expandir_nodo(problema, nodo, st, hijos, profundidad_max);
				while (!hijos.isEmpty()) {
					frontera.add(hijos.poll());
				}
			}
		}

		if (solucion) {
			while (nodo.getPadre()!=null) {
				camino.add(nodo.getEstado());
				nodo=nodo.getPadre();
			}
			camino.add(nodo.getEstado());
			while (!frontera.isEmpty()) {
				casillas_frontera.add(frontera.poll().getEstado());
			}
		}else {
			throw new NoSolutionException();
		}
		return camino;
	}
	public static void expandir_nodo (Problema problema, Nodo nodo, String st, LinkedList<Nodo> hijos, int profundidad_max){
		Sucesor sucesores = new Sucesor();
		Nodo hijo;
		sucesores = problema.crear_sucesor(nodo, problema.getLaberinto());
		for (int i=0 ; i<sucesores.getNodos().size() ; i++) {
			hijo = new Nodo();
			hijo.setEstado(sucesores.getNodos().get(i).getEstado());
			hijo.setPadre(nodo);
			hijo.setAccion(sucesores.getMov().get(i));
			hijo.setProfundidad(nodo.getProfundidad()+1);
			hijo.setCosto(nodo.getCosto()+sucesores.getNodos().get(i).getEstado().getValor()+1);
			hijo.setHeuristica(calcular_heuristica(problema, hijo));
			hijo.setValor(calcular_valor(st, hijo, profundidad_max));
			hijos.add(hijo);
		}
	}
	public static int calcular_heuristica(Problema problema, Nodo nodo) {
		return Math.abs(nodo.getEstado().get_posicion()[0]-problema.getDestino().get_posicion()[0]) + Math.abs(nodo.getEstado().get_posicion()[1]-problema.getDestino().get_posicion()[1]);
	}	
	public static int calcular_valor(String st, Nodo nodo, int profundidad_max) {
		int valor=0;
		switch (st) {
		case "BREADTH":
			valor=nodo.getID();
			break;
		case "DEPTH":
			valor=nodo.getProfundidad();
			valor=profundidad_max-valor;
			break;
		case "UNIFORM":
			valor=nodo.getCosto();
			break;
		case "GREEDY":
			valor=nodo.getHeuristica();
			break;
		case "A":
			valor=nodo.getHeuristica()+nodo.getCosto();
			break;
		}
		return valor;
	}

}