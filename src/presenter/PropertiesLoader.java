package presenter;

import java.beans.XMLDecoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PropertiesLoader {
	private static PropertiesLoader instance;
	private Properties properties;
	
	private PropertiesLoader(String fileName) throws Exception 
	{
		try {
			XMLDecoder decoder = new XMLDecoder(new FileInputStream(fileName + "-properties.xml"));
			properties = (Properties)decoder.readObject();
			decoder.close();
		} catch (Exception e) {
			throw e;
		}
	}
	
	public static PropertiesLoader getInstance(String fileName) throws Exception {
		if (instance == null) 
			instance = new PropertiesLoader(fileName);
		return instance;
	}
	
	public Properties getProperties() {
		return properties;
	}
}
