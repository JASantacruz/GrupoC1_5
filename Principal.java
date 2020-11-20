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

public class Principal implements Constantes {
	public static final Scanner TECLADO = new Scanner (System.in);
	public static void main(String[] args) throws LaberintoIncorrectoException {
		int n_filas=0, n_columnas=0, opcion=0;		
		Casilla[][] matriz = null;
		Laberinto lab = new Laberinto();
		boolean seguir = true, error=false;
		String ruta;
		do {
			do {
				mostrar_opciones();
				try {
					opcion = TECLADO.nextInt();
					if (opcion<1 || opcion>3) throw new IOException();
					error=false;
				}catch(Exception e) {
					System.out.println("ERROR El valor introducido deber ser un n�mero entero del 1 al 3.");
					error=true;
				}
				TECLADO.nextLine();
			}while(error);
			switch(opcion) {
			case 1:
				try {
					crear_matriz(n_filas, n_columnas, matriz, lab);
				}catch (IOException e) {
					e.printStackTrace();
				}
				System.out.println("Se ha creado el laberinto correctamente.");
				break;
			case 2:
				System.out.println("Por favor, escriba la ruta del archivo.");
				ruta = TECLADO.nextLine();
				try {
					importarProblema(lab, ruta);
					buscar_error(lab);
					crear_imagen(lab);
					anyadirAFrontera(lab);
				}catch (FileNotFoundException e){
					System.out.println("ERROR No se ha encontrado el archivo.");
				}catch (LaberintoIncorrectoException e){
					System.out.println("ERROR "+e.getMessage());
				}catch (Exception e) {
					System.out.println("ERROR");
				}
				System.out.println("Se ha importado correctamente el laberinto.");
				break;
			case 3:
				System.out.println("�Hasta pronto!");
				seguir=false;
			}
		}while(seguir);
	}

	public static void mostrar_opciones(){
		System.out.println("Elija una de las opciones:\n\n1) Crear un laberinto.\n2) Importar un laberinto.\n3) Salir.\n\n");
	}

	public static void crear_matriz(int n_filas, int n_columnas, Casilla[][] matriz, Laberinto lab) throws IOException, LaberintoIncorrectoException {
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
		crear_json_maze(lab);
		buscar_error(lab);
		anyadirAFrontera(lab);
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
		System.out.println("Introduzca el n�mero de "+filas_o_columnas+" permitido.");
		do{
			try {				
				numero=TECLADO.nextInt();
				if(numero<1) throw new InputMismatchException();
				valido=true;
			}catch (InputMismatchException e) {
				System.out.println("El tipo de entrada no ha sido v�lido. Intente otra vez.");
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
		File file = new File("laberinto"+laberinto.getFilas()+"x"+laberinto.getColumnas()+".jpg");
		ImageIO.write(img, "jpg", file);
	}
	@SuppressWarnings("unchecked")
	public static void crear_json_maze(Laberinto lab) throws FileNotFoundException {
		JSONObject json1 = new JSONObject();
		JSONObject json2 = new JSONObject(); 
		JSONObject json3;
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
		json1.put("mov", lab.getMove());
		json1.put("id_mov", lab.getId_move());
		json1.put("cells",json2);

		try(PrintWriter puntoJson = new PrintWriter(aux)){
			puntoJson.println(json1);
		}
		crearJson(lab,aux);
	}
	@SuppressWarnings("unchecked")
	public static void crearJson(Laberinto lab,String ruta) throws FileNotFoundException {
		JSONObject json = new JSONObject();
		json.put("INITIAL", "("+lab.getListaCasillas()[0][0].get_posicion()[0]+", "+lab.getListaCasillas()[0][0].get_posicion()[1]+")");
		json.put("OBJETIVE", "("+lab.getListaCasillas()[lab.getListaCasillas().length-1][lab.getListaCasillas()[0].length-1].get_posicion()[0]+", "+lab.getListaCasillas()[lab.getListaCasillas().length-1][lab.getListaCasillas()[0].length-1].get_posicion()[1]+")");
		json.put("MAZE", ruta);
		try(PrintWriter puntoJson = new PrintWriter("laberinto"+lab.getFilas()+"x"+lab.getColumnas()+".json")){
			puntoJson.println(json);
		}
	}
	@SuppressWarnings({ "unused", "unchecked" })
	public static void importarProblema(Laberinto lab, String ruta) throws IOException, ParseException {
		JSONObject json = null;
		JSONParser jparse = new JSONParser();
		String jason = "";
		String linea, rutaMaze, inicio, destino;
		File archivo = new File (ruta);
		FileReader fr = new FileReader (archivo);
		BufferedReader br = new BufferedReader(fr);
		while ((linea = br.readLine()) != null) {
			jason += linea;
		}
		json = (JSONObject)jparse.parse(jason);
		inicio = (String)json.get("INITIAL"); 
		destino = (String)json.get("OBJETIVE");
		rutaMaze = (String)json.get("MAZE");
		importarLaberinto(lab,rutaMaze);		
	}
	public static void importarLaberinto(Laberinto laberinto, String ruta) throws IOException, ParseException {
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

		File archivo = new File (ruta);
		FileReader fr = new FileReader (archivo);
		BufferedReader br = new BufferedReader(fr);
		while ((linea = br.readLine()) != null) {
			jason += linea;
		}
		json1 = (JSONObject)jparse.parse(jason);
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
	public static void anyadirAFrontera(Laberinto lab) throws FileNotFoundException {
		PriorityQueue<Nodo> frontera = new PriorityQueue<Nodo>();
		LinkedList<Sucesor> sucesores = new LinkedList<Sucesor>();
		Nodo aux;
		Random aleatorio = new Random();
		int fila,columna;
		for(int i =0;i<10;i++) {
			fila = aleatorio.nextInt(lab.getFilas());
			columna = aleatorio.nextInt(lab.getColumnas());
			aux = new Nodo();
			aux.setEstado(lab.getListaCasillas()[fila][columna]);
			frontera.add(aux);
		}
		for(int i=0;i<10;i++) {
			aux = frontera.remove();
			sucesores.add(crearSucesor(aux,lab));
		}
		imprimirFrontera(sucesores,lab);
	}
	public static Sucesor crearSucesor(Nodo nodo,Laberinto lab) {
		Sucesor s = new Sucesor(nodo.getEstado());
		for(int i =0;i<nodo.getEstado().getParedes().length;i++) {
			if(nodo.getEstado().get_pared(i)==true)
				switch(i) {
				case 0:
					s.getMov().add(MOV_N);
					s.getNodos().add(anyadirNodo(nodo, lab,MOV_N));
					break;
				case 1:
					s.getMov().add(MOV_E);
					s.getNodos().add(anyadirNodo(nodo, lab,MOV_E));
					break;
				case 2:
					s.getMov().add(MOV_S);
					s.getNodos().add(anyadirNodo(nodo, lab,MOV_S));
					break;
				case 3:
					s.getMov().add(MOV_O);
					s.getNodos().add(anyadirNodo(nodo, lab,MOV_O));
					break;
				}
		}
		return s;
	}
	public static Nodo anyadirNodo(Nodo nodo,Laberinto lab,String mov) {
		Casilla aux = new Casilla();
		Nodo hijo = new Nodo(nodo);
		switch(mov) {
		case MOV_N:
			aux = lab.getListaCasillas()[nodo.getEstado().get_posicion()[0]-1][nodo.getEstado().get_posicion()[1]];
			hijo.setEstado(aux);
			break;
		case MOV_E:
			aux = lab.getListaCasillas()[nodo.getEstado().get_posicion()[0]][nodo.getEstado().get_posicion()[1]+1];
			hijo.setEstado(aux);
			break;
		case MOV_S:
			aux = lab.getListaCasillas()[nodo.getEstado().get_posicion()[0]+1][nodo.getEstado().get_posicion()[1]];
			hijo.setEstado(aux);
			break;
		case MOV_O:
			aux = lab.getListaCasillas()[nodo.getEstado().get_posicion()[0]][nodo.getEstado().get_posicion()[1]-1];
			hijo.setEstado(aux);
			break;
		}
		hijo.setAccion(mov);
		//hijo.setProfundidad(nodo.getProfundidad()+1);
		hijo.setCosto(nodo.getCosto()+COSTE);
		return hijo;
	}
	public static void imprimirFrontera(LinkedList<Sucesor> sucesores,Laberinto lab) throws FileNotFoundException {
		Sucesor s;
		try(PrintWriter puntoJson = new PrintWriter("Sucesores"+lab.getFilas()+"x"+lab.getColumnas()+".txt")){
			while(!sucesores.isEmpty()) {
				s=sucesores.remove();
				puntoJson.println(s.toString());
			}
		}
	}
	public static int calcular_heuristica(Problema problema, Nodo nodo) {
		//return Math.abs(actual.get_posicion()[0]-destino.get_posicion()[0]) + Math.abs(actual.get_posicion()[1]-destino.get_posicion()[1]);
		return 0;
	}
	public static LinkedList<Casilla> algoritmo_busqueda (Problema problema, int profundidad, String st) throws NoSolutionException {
		LinkedList<Casilla> visitado = new LinkedList<Casilla>();
		LinkedList<Nodo> hijos = new LinkedList<Nodo>();
		LinkedList<Casilla> camino = new LinkedList<Casilla>();
		PriorityQueue<Nodo> frontera = new PriorityQueue<Nodo>();
		boolean solucion=false;
		Nodo nodo = new Nodo();
		nodo.setPadre(null);
		nodo.setEstado(null/*problema.get_estado_inicial()*/);
		nodo.setCosto(0);
		nodo.setProfundidad(0);
		nodo.setAccion(null);
		nodo.setHeuristica(calcular_heuristica(problema, nodo));
		nodo.setValor(calcular_valor(st, nodo));
		frontera.add(nodo);
		while(!frontera.isEmpty() && !solucion) {
			nodo = frontera.poll();
			if (/*problema.get_objetivo().equals(nodo.getEstado())*/nodo.getAccion().equals("ESTO ES UNA PRUEBA NO VALE")) {
				solucion=true;
			}else if(visitado.contains(nodo.getEstado()) && nodo.getProfundidad()<profundidad) {
				visitado.add(nodo.getEstado());
				expandir_nodo(problema, nodo, st, hijos);
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
		}else throw new NoSolutionException();
		return camino;
		
	}
	public static int calcular_valor(String st, Nodo nodo) {
		int valor=0;
		switch (st) {
		case "BREADTH":
			valor=nodo.getID();
				break;
		case "DEPTH":
			valor=nodo.getProfundidad();
			valor*=-1;
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
	public static void expandir_nodo (Problema problema, Nodo nodo, String st, LinkedList<Nodo> hijos){
		LinkedList<Nodo> nodos = new LinkedList<Nodo>();
		Sucesor sucesores = new Sucesor();
		Nodo hijo;
		//problema.sucesores(nodo.getEstado()--> ESTO ES LO DE CREAR SUCESOR PERO EN LA CLASE PROBLEMA
		for (int i=0 ; i<sucesores.getNodos().size() ; i++) {
			hijo = new Nodo();
			hijo.setEstado(sucesores.getNodos().get(i).getEstado());
			hijo.setPadre(nodo);
			hijo.setAccion(sucesores.getMov().get(i));
			hijo.setProfundidad(nodo.getProfundidad()+1);
			hijo.setCosto(nodo.getCosto()+sucesores.getNodos().get(i).getEstado().getValor()+1);
			hijo.setHeuristica(calcular_heuristica(problema, hijo));
			hijo.setValor(calcular_valor(st, hijo));
			nodos.add(hijo);
		}
	}
}