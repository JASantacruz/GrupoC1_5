package Sesion_1;


import java.util.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import com.google.gson.Gson;
import java.awt.*;


import java.io.*;

public class Principal {
	public static final Scanner TECLADO = new Scanner (System.in);

	public static void main(String[] args) throws IOException {
		int n_filas;
		int n_columnas;
		Casilla[][] matriz;
		
		n_filas=numero_filas_columnas("filas");	
		n_columnas=numero_filas_columnas("columnas");
		Laberinto lab = new Laberinto();
		//lab = importarLaberinto();
		matriz = new Casilla[n_filas][n_columnas];
		lab.setListaCasillas(matriz);
		lab.setColumnas(n_columnas);
		lab.setFilas(n_filas);
		lab.inicializar_matriz();
		algoritmo_wilson(lab);
		
		System.out.println("\n\n\nResultado final:");
		mostrar_matriz(lab);
		crear_imagen(lab, n_filas, n_columnas);
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
		System.out.println("Introduzca el numero de "+filas_o_columnas+" permitido.");
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

		public static void crear_imagen(Laberinto laberinto, int n_filas, int n_columnas) throws IOException {
			int width = 500;
			int height = 500;
			int alto_casilla = height / n_filas; //Alto de cada casilla
			int ancho_casilla = width / n_columnas; //Ancho de cada casilla
			BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics2D lab = img.createGraphics();
			lab.setColor(Color.white);
			lab.fillRect(1, 1, width-2, height-2); //Para que el grosor de cada borde sea de 1 px
			lab.setColor(Color.black);
			for(int i = 0; i < n_filas; i++) 
				for(int j = 0; j < n_columnas; j++) 
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
		Gson gson = new Gson();
		String json = gson.toJson(lab);
		
		try(PrintWriter puntoJson = new PrintWriter("laberintoFinal.json")){
			puntoJson.println(json);
		}
	}
	
	/*public static Laberinto importarLaberinto() {
		Laberinto laberinto;
		Gson gson = new Gson();
		
		laberinto = gson.fromJson("D:\\Descargas\\laberinto.json", Laberinto.class);
		return laberinto;
	}*/


}