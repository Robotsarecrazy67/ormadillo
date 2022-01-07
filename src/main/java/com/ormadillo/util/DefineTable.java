package com.ormadillo.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;

public class DefineTable {
	/*
	 *  Class variables
	 */
	// set containing all the metamodels for the tables we have created
	private static Set<MetaModel<Class<?>>> metaModels; 
	private static Logger logger = Logger.getLogger(DefineTable.class);
	private static final String allTypesFileName = "src\\main\\resources\\allTypes.csv";
	private static HashMap<String, String> allTypes = new HashMap<String, String>();
	// constants
	private static String LINE = "";  
	private static final String COMMA = ","; 
	private static final int JAVA_TYPE = 0;
	private static final int SQL_TYPE = 1;
	
	static { // load statically from csv file
		try   
		{  
			//parsing a CSV file into BufferedReader class constructor  
			BufferedReader read = new BufferedReader(new FileReader(allTypesFileName));
			
			// read each line of the file if it exists
			while ((LINE = read.readLine()) != null) 
			{  
				String[] types = LINE.split(COMMA);    // use comma as separator  
				allTypes.put(types[JAVA_TYPE], types[SQL_TYPE]); // map the java and sql types
			}  
			
			logger.info("Successfully loaded all Java and SQL mapping types from File.");
		}   
		catch (IOException error)   
		{  
			logger.error("Unable to open file with that name.");
			error.printStackTrace();
		}  
	}
	
	public DefineTable() {

	}
	
	/*
	 * Gets the mapping of all java to sql types
	 * 
	 * @return HashMap containing all types 
	 */
	public static HashMap<String, String> getAllTypeMap(){
		return allTypes;
	}
	
	protected static void createTableIfDoesNotExist(MetaModel<Class<?>> metaModel) {
		if(metaModel != null) {
			String sql = "CREATE TABLE IF DOES NOT EXIST ?\r\n"
					   + "";
			
			metaModels.add(metaModel);
		}
	}
	
}
