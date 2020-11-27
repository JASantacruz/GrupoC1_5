package Sesion1;

public class Nodo implements Comparable <Nodo> {
	private static int count = 0;
	private int id;
	private Casilla estado;
	private int valor;
	private int profundidad;
	private int costo;
	private int heuristica;
	private String accion;
	private Nodo padre;
	
	
	public Nodo(){
		this.id=count++;
		this.valor = 0;
		this.profundidad = 0;
	}
	
	public Nodo(Nodo padre){
		this.id=count++;
		this.valor = 0;
		this.profundidad = padre.getProfundidad()+1;
		this.padre=padre;
	}
	
	
	public int getID() {
		return id;
	}
	
	public void setID(int iD) {
		id = iD;
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
	
	public String getAccion() {
		return accion;
	}
	
	public void setAccion(String accion) {
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
				+ ", " + getProfundidad() + ", "+ getHeuristica() + ", " + Math.abs(getValor()) + "]";
	}

	@Override
	public int compareTo(Nodo n) {
		int r=0;
		if(n.getValor()<getValor()) {
			r=+1;
		}else if(n.getValor()> getValor()) {
			r=-1;
		}else if(n.getValor()== getValor()) {
			if(n.getEstado().get_posicion()[0]> getEstado().get_posicion()[0]) {
				r=-1;
			}else if(n.getEstado().get_posicion()[0]< getEstado().get_posicion()[0]) {
				r=+1;
			}else if(n.getEstado().get_posicion()[0]== getEstado().get_posicion()[0]) {
				if(n.getEstado().get_posicion()[1]> getEstado().get_posicion()[1]) {
					r=-1;
				}else if(n.getEstado().get_posicion()[1]< getEstado().get_posicion()[1]) {
					r=+1;
				}
			}
		}
		return r;
	}
	
}
