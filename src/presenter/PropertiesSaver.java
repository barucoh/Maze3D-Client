package presenter;

import java.beans.XMLEncoder;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class PropertiesSaver {
	private static PropertiesSaver instance;
	
	private PropertiesSaver() {
	}
	
	public static PropertiesSaver getInstance() {
		if (instance == null)
			instance = new PropertiesSaver();
		return instance;
	}
	
	public static void saveProperties(String fileName, Properties properties) {
		XMLEncoder xmlEncoder = null;
		try {
			xmlEncoder = new XMLEncoder(new FileOutputStream(fileName + "-properties.xml"));
			xmlEncoder.writeObject(properties);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		finally {
			xmlEncoder.close();
		}
	}
}
