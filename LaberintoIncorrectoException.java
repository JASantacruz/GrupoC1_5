package sesion_1;

@SuppressWarnings("serial")
public class LaberintoIncorrectoException extends Exception{

	public LaberintoIncorrectoException(String celda_error) {
		super("El formato del laberinto es incorrecto (alguna celda es incorrecta o no existe). Origen del error: "+celda_error);
	}

}
