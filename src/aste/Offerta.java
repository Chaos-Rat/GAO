package aste;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * Il client dovrebbe aspettarsi un'array di lunghezza massima di 256 byte
 */
public class Offerta implements Serializable {
	public int idUtente;
	public float valore;
	public LocalDateTime dataOra;
	public static final int MAX_SERIALIZED_SIZE = 146;
	
	private static final long serialVersionUID = -6694998038211513079L;

	public static byte[] toByteArray(Offerta offerta) {
		ByteArrayOutputStream byteOutput = new ByteArrayOutputStream();
		ObjectOutputStream objectOutput;
		
		try {
			objectOutput = new ObjectOutputStream(byteOutput);
			objectOutput.writeObject(offerta);
			objectOutput.flush();
		} catch (IOException e) {
			throw new Error("[" +
				Thread.currentThread().getName() +
				"]: Sì è verificato un errore nella conversione a byte[] dell'offerta. "
				+ e.getMessage()
			);
		}

		return byteOutput.toByteArray();
	}

	public static Offerta fromByteArray(byte[] array) throws IOException, ClassNotFoundException, ClassCastException {
		ByteArrayInputStream byteInput = new ByteArrayInputStream(array);
		ObjectInputStream objectInput = new ObjectInputStream(byteInput);
		
		return (Offerta)objectInput.readObject();
	}
}
