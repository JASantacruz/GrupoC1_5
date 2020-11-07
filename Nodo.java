package Sesion1;

public class Nodo {
	private int ID;
	private Casilla estado;
	private int valor;
	private int profundidad;
	private int costo;
	private int heuristica;
	private char accion;
	private Nodo padre;
	
	public Nodo(int iD, Casilla estado, int valor, int profundidad, int costo, int heuristica, char accion,
			Nodo padre) {
		super();
		ID = iD;
		this.estado = estado;
		this.valor = valor;
		this.profundidad = profundidad;
		this.costo = costo;
		this.heuristica = heuristica;
		this.accion = accion;
		this.padre = padre;
	}
	
	public int getID() {
		return ID;
	}
	
	public void setID(int iD) {
		ID = iD;
	}
	
	public Casilla getEstado() {
		return estado;
	}
	
	public void setEstado(Casilla estado) {
		this.estado = estado;
	}
	
	public int getValor() {
		return valor;
	}
	
	public void setValor(int valor) {
		this.valor = valor;
	}
	
	public int getProfundidad() {
		return profundidad;
	}
	
	public void setProfundidad(int profundidad) {
		this.profundidad = profundidad;
	}
	
	public int getCosto() {
		return costo;
	}
	
	public void setCosto(int costo) {
		this.costo = costo;
	}
	
	public int getHeuristica() {
		return heuristica;
	}
	
	public void setHeuristica(int heuristica) {
		this.heuristica = heuristica;
	}
	
	public char getAccion() {
		return accion;
	}
	
	public void setAccion(char accion) {
		this.accion = accion;
	}
	
	public Nodo getPadre() {
		return padre;
	}
	
	public void setPadre(Nodo padre) {
		this.padre = padre;
	}
	
	@Override
	public String toString() {
		return "["+ getID() + "] [" + getCosto() + ", (" + getEstado().get_posicion()[0] + ", "+getEstado().get_posicion()[1]+"), " +getPadre().getID()+", " + getAccion()
				+ ", " + getProfundidad() + ", "+ getHeuristica() + ", " + getValor() + "]";
	}
	
}
