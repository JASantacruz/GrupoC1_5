package Sesion1;

public class NoSolutionException extends Exception{

	public NoSolutionException() {
		super("No existe soluci�n para este laberinto.");
	}

}
