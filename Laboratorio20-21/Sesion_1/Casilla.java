package Sesion_1;



public class Casilla {
	private boolean[] paredes;
	private int valor;
	private int[] posicion;
	
	
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
	
	public String toString() {
		return "{"+get_posicion()[0]+","+get_posicion()[1]+"}: "+get_pared(0)+", "+get_pared(1)+", "+get_pared(2)+", "+get_pared(3)+")";
	}
}
