package com.angelatech.yeyelive;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;


public class SaveInfo {
	
	private final String INFO_PROP = "info_prop";
	private Properties prop = new Properties();
	
	
	public void save(String key,String value,String extraCommentStr){
		try {
			prop.setProperty(key,value);
			prop.store(new FileOutputStream(INFO_PROP),extraCommentStr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getValue(String key){
		try {
			prop.load(new FileInputStream(INFO_PROP));
			String value = prop.getProperty(key);
			return value;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	
	

}
