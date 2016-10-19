package presenter;

import java.beans.XMLDecoder;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class PropertiesLoader {
	private static PropertiesLoader instance;
	private Properties properties;
	
	private PropertiesLoader() 
	{
		try {
			XMLDecoder decoder = new XMLDecoder(getClass().getClassLoader().getResourceAsStream("properties.xml"));
			properties = (Properties)decoder.readObject();
			decoder.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static PropertiesLoader getInstance() {
		if (instance == null) 
			instance = new PropertiesLoader();
		return instance;
	}
	
	public Properties getProperties() {
		return properties;
	}
}
