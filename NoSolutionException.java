package Practica_Laberinto;

public class NoSolutionException extends Exception{

	public NoSolutionException() {
		super("No existe solución para este laberinto.");
	}

}
