package Practica_Laberinto;

import java.util.ArrayList;
import java.util.List;

public class Casilla {
	private boolean[] paredes;
	private int valor;
	private int[] posicion;
	
	
	public int getValor() {
		return valor;
	}

	public void setValor(int valor) {
		this.valor = valor;
	}

	public Casilla (int[] posicion) {
		//Por ahora
		set_valor(0);
		set_posicion(posicion);
		paredes = new boolean[4];
		for (int i=0; i<paredes.length; i++) {
			cerrar_pared(i);
		}
	}
	
	public void abrir_pared(int id_pared) {
		paredes[id_pared]=true;
	}
	
	public void cerrar_todas_paredes() {
		for (int i=0; i<paredes.length; i++) {
			paredes[i]=false;
		}
	}
	public void cerrar_pared(int id_pared) {
		paredes[id_pared]=false;
	}
	
	public boolean[] getParedes() {
		return paredes;
	}

	public void setParedes(boolean[] paredes) {
		this.paredes = paredes;
	}

	public boolean get_pared(int id_pared) {
		return paredes[id_pared];
	}
	
	public void set_valor(int valor) {
		this.valor=valor;
	}
	
	public void set_posicion(int[] pos) {
		posicion = new int[2];
		this.posicion[0]=pos[0];
		this.posicion[1]=pos[1];
	}
	
	public int[] get_posicion() {
		return posicion;
	}
	
	public boolean equals(Casilla casilla) {
		if(casilla.get_posicion()==get_posicion()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public List<Boolean> cambiarArrayALista() {
		List<Boolean> aux = new ArrayList<Boolean>();
		aux.add(get_pared(0));
		aux.add(get_pared(1));
		aux.add(get_pared(2));
		aux.add(get_pared(3));
		
		return aux;
	}
	
	
	
	public String toString() {
		return "{"+get_posicion()[0]+","+get_posicion()[1]+"}: "+get_pared(0)+", "+get_pared(1)+", "+get_pared(2)+", "+get_pared(3)+")";
	}
}