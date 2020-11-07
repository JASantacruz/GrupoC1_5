package Sesion1;

import java.util.Arrays;
import java.util.LinkedList;
@SuppressWarnings("unused")
public class Sucesor {
	private char mov;
	private Casilla estado;
	private int coste;
	
	private LinkedList<Nodo> nodos;
	
	public Sucesor(char mov, Casilla estado, int coste) {
		this.mov = mov;
		this.estado = estado;
		this.coste = coste;
		this.nodos = new LinkedList<Nodo>();
	}

	public Casilla getEstado() {
		return estado;
	}

	public void setEstado(Casilla estado) {
		this.estado = estado;
	}

	public char getMov() {
		return mov;
	}

	public void setMov(char mov) {
		this.mov = mov;
	}

	public int getCoste() {
		return coste;
	}

	public void setCoste(int coste) {
		this.coste = coste;
	}

	@Override
	public String toString() {
		String sucesores = "SUC(("+getEstado().get_posicion();
		for(int i = 0; i < nodos.size(); i++) {
			 sucesores += "['"+getMov()+"', ("+nodos.get(i).getEstado().get_posicion()[0]+", "+nodos.get(i).getEstado().get_posicion()[1]+"), "+getCoste()+"]\t";
		}
		return sucesores;
	}
}
