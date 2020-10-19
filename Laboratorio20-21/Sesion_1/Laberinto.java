package Sesion_1;

import java.util.List;

public class Laberinto {
	
	private int columnas;
	private int filas;
	private int max_n;
	private String []id_move;
	private Casilla [][] listaCasillas;
	
	public Laberinto() {
		
	}
	
	public Laberinto(int columnas, int filas, Casilla[][] listaCasillas) {
		super();
		this.columnas = columnas;
		this.filas = filas;
		this.listaCasillas = listaCasillas;
		
	}

	public int getColumnas() {
		return columnas;
	}

	public void setColumnas(int columnas) {
		this.columnas = columnas;
	}

	public int getFilas() {
		return filas;
	}

	public void setFilas(int filas) {
		this.filas = filas;
	}

	public Casilla[][] getListaCasillas() {
		return listaCasillas;
	}

	public void setListaCasillas(Casilla[][] listaCasillas) {
		this.listaCasillas = listaCasillas;
	}

	public void inicializar_matriz(){
		int posicion[] = new int[2];
		for (int i=0; i<listaCasillas.length; i++) {
			for (int j=0; j<listaCasillas[0].length; j++) {
				posicion[0]=i;
				posicion[1]=j;
				listaCasillas[i][j] = new Casilla(posicion);
				
			}
		}
	}
}
