package sesion_1;

@SuppressWarnings("serial")
public class NoSolutionException extends Exception{

	public NoSolutionException() {
		super("No existe solucion para este laberinto.");
	}

}
