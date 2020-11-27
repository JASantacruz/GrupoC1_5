package Sesion1;

public class Problema implements Constantes{

	private Laberinto laberinto;
	private Casilla origen;
	private Casilla destino;
	
	public Problema() {

	}
	public Problema(Laberinto laberinto, Casilla origen, Casilla destino) {
		super();
		setLaberinto(laberinto);
		setOrigen(origen);
		setDestino(destino);
	}
	public Laberinto getLaberinto() {
		return laberinto;
	}
	public void setLaberinto(Laberinto laberinto) {
		this.laberinto = laberinto;
	}
	public Casilla getOrigen() {
		return origen;
	}
	public void setOrigen(Casilla origen) {
		this.origen = origen;
	}
	public Casilla getDestino() {
		return destino;
	}
	public void setDestino(Casilla destino) {
		this.destino = destino;
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
}
