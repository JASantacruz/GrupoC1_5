package Practica_Laberinto;

public class LaberintoIncorrectoException extends Exception{

	public LaberintoIncorrectoException(String celda_error) {
		super("El formato del laberinto es incorrecto (alguna celda es incorrecta o no existe). Origen del error: "+celda_error);
	}

}
