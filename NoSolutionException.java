package Practica_Laberinto;

public class NoSolutionException extends Exception{

	public NoSolutionException() {
		super("No existe solucion para este laberinto.");
	}

}
