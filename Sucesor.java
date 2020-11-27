package Sesion1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
@SuppressWarnings("unused")
public class Sucesor {
	private List<String> mov;
	private Casilla estado;
	private int coste;
	private List<Nodo> nodos;
	
	public Sucesor() {
		
	}
	
	public Sucesor(Casilla estado) {
		this.mov = new ArrayList<String>();
		this.estado = estado;
		this.nodos = new ArrayList<Nodo>();
	}

	public Casilla getEstado() {
		return estado;
	}

	public void setEstado(Casilla estado) {
		this.estado = estado;
	}

	public List<String> getMov() {
		return mov;
	}

	public void setMov(List<String> mov) {
		this.mov = mov;
	}

	public int getCoste() {
		return coste;
	}

	public void setCoste(int coste) {
		this.coste = coste;
	}
	
	public List<Nodo> getNodos() {
		return nodos;
	}

	public void setNodos(List<Nodo> nodos) {
		this.nodos = nodos;
	}

	@Override
	public String toString() {
		String sucesores = "SUC(("+getEstado().get_posicion()[0]+","+getEstado().get_posicion()[1]+"))=";
		for(int i = 0; i < nodos.size(); i++) {
			 sucesores += "['"+getMov().get(i)+"', ("+nodos.get(i).getEstado().get_posicion()[0]+", "+nodos.get(i).getEstado().get_posicion()[1]+"), "+getCoste()+"]  ";
		}
		return sucesores;
	}
}
