package Practica_Laberinto;

public class Nodo implements Comparable <Nodo> {
	private static int count = 0;
	private int id;
	private Casilla estado;
	private double valor;
	private int profundidad;
	private int costo;
	private double heuristica;
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
	
	public Nodo(Nodo padre, boolean flag){
		//Este constructor se usa cuando no queremos incrementar el id (para poder utilizar nodos auxiliares sin afectar a dicha variable)
		this.id = -1;
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
	
	public double getValor() {
		return valor;
	}
	
	public void setValor(double valor) {
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
	
	public double getHeuristica() {
		return heuristica;
	}
	
	public void setHeuristica(double heuristica) {
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
		if(n.getValor()<this.getValor()) {
			r=+1;
		}else if(n.getValor()> this.getValor()) {
			r=-1;
		}else if(n.getValor()== this.getValor()) {
			if(n.getEstado().get_posicion()[0]> this.getEstado().get_posicion()[0]) {
				r=-1;
			}else if(n.getEstado().get_posicion()[0] < this.getEstado().get_posicion()[0]) {
				r=+1;
			}else if(n.getEstado().get_posicion()[0]== this.getEstado().get_posicion()[0]) {
				if(n.getEstado().get_posicion()[1]> this.getEstado().get_posicion()[1]) {
					r=-1;
				}else if(n.getEstado().get_posicion()[1]< this.getEstado().get_posicion()[1]) {
					r=+1;
				}else if(n.getEstado().get_posicion()[1]==this.getEstado().get_posicion()[1]) {
					if(n.getID() > this.getID()) {
						r=-1;
					}
					else if(n.getID() < this.getID()) {
						r=+1;
					}
				}
			}
		}
		return r;
	}
	
}
