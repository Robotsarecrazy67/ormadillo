package com.ormadillo.sql;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import com.ormadillo.fields.ColumnField;
import com.ormadillo.fields.ForeignKeyField;
import com.ormadillo.fields.PrimaryKeyField;
import com.ormadillo.util.MetaModel;

/*
 * Class is responsible for defining SQL Queries that can be executed to the Database
 */
public class SqlBuilder {
	/*
	 *  Class variables
	 */
	// set containing all the metamodels for the tables we have created
	private static Set<MetaModel<Class<?>>> metaModels; 
	// initialize a logger 
	private static Logger logger = Logger.getLogger(SqlBuilder.class);
	// csv file name for all mappable types
	private static final String allTypesFileName = "src\\main\\resources\\allTypes.csv";
	// maps java types to sql types
	private static HashMap<String, String> allTypes = new HashMap<String, String>();
	private static BufferedReader read;
	// constants to improve readability
	private static String LINE = "";
	private static String SPACE = " ";
	private static final String COMMA = ","; 
	private static final String SEMICOLON = ";";
	private static final int JAVA_TYPE = 0;
	private static final int SQL_TYPE = 1;
	private static final String INTEGER = "integer";
	private static final String REFERENCES = "REFERENCES";
	private static final String NOTNULL = "NOT NULL";
	private static final String OP = "(";
	private static final String CP = ")";
	private static final String NEWLINE = "\r\n";
	private static final String SERIALPRIMARYKEY = "SERIAL PRIMARY KEY";
	private static final String UNIQUE = "UNIQUE";
	private static final String CREATETABLE = "CREATE TABLE IF NOT EXISTS";
	private static final String UPDATE = "UPDATE";
	private static final String DELETE = "DELETE";
	private static final String ON = "ON";
	private static final String WHERE = "WHERE";
	private static final String SET = "SET";
	private static final String EQUALS = "=";
	private static final String SINGLEQUOTE = "'";
	private static final String CASCADE = "CASCADE";
	private static final String FROM = "FROM";
	private static final String INSERT = "INSERT";
	private static final String INTO = "INTO";
	private static final String VALUES = "VALUES";
	private static final String CONFLICT = "CONFLICT";
	private static final String DO = "DO";
	private static final String NOTHING = "NOTHING";
	private static final String ASTERISK = "*";
	private static final String ALL = "ALL";
	private static final String SELECT = "SELECT";
	
	static { // load statically from csv file
		try   
		{  
			//parsing a CSV file into BufferedReader class constructor  
			read = new BufferedReader(new FileReader(allTypesFileName));
			
			// read each line of the file if it exists
			while ((LINE = read.readLine()) != null) 
			{  
				String[] types = LINE.split(COMMA);    // use comma as separator  
				allTypes.put(types[JAVA_TYPE], types[SQL_TYPE]); // map the java and sql types
			}  
			
			read.close(); // close the BufferedReader
			logger.info("Successfully loaded all Java and SQL mapping types from File.");
		}   
		catch (IOException error)   
		{  
			logger.error("Unable to open file with that name.");
			error.printStackTrace();
		} 
		// initialize meta models list
		metaModels = new HashSet<MetaModel<Class<?>>>();
	}
	
	private SqlBuilder() {
		
	}
	
	/*
	 * Gets the mapping of all java to sql types
	 * 
	 * @return HashMap containing all types 
	 */
	public static HashMap<String, String> getAllTypeMap(){
		return allTypes;
	}
	/*
	 * Gets a list containing the metamodels
	 */
	public static Set<MetaModel<Class<?>>> getMetaModels(){
		return metaModels;
	}
	
	/*
	 * Builds the SQL String from the Given MetaModel for a creation of a table in the db
	 * @param metamodel for the given class
	 * @return fully formed SQL string
	 */
	protected static String createTableIfDoesNotExist(MetaModel<Class<?>> metaModel) {
		// initialize sql string
		String createTableSQL = "";
		// save the table name to a variable
		String tableName = metaModel.getTableName();
		// save the primary key to a variable
		PrimaryKeyField pk = metaModel.getPrimaryKey();
		// save the primary key column name
		String primaryKeyColumnName = pk.getColumnName();
		// get the set of all the columns in the meta model
		List<ColumnField> columns = metaModel.getColumns();
		// get the set of all the foreign keys in the meta model
		Set<ForeignKeyField> foreignKeyFields = metaModel.getForeignKeys();
		// if there is at least one foreign key
		if (!foreignKeyFields.isEmpty()) {
			// iterate through foreign key set
			for (ForeignKeyField foreignKey : foreignKeyFields) {
				MetaModel<Class<?>> reference = MetaModel.of(foreignKey.getReference()); // class reference
				// recursive call to ensure references are created first
				createTableSQL += createTableIfDoesNotExist(reference); 
			}
		}
		/*
		 * SQL Build
		 */
		createTableSQL += CREATETABLE + SPACE + tableName + SPACE + OP + NEWLINE;
		createTableSQL += primaryKeyColumnName + SPACE + SERIALPRIMARYKEY;
		for (ColumnField column : columns) {
			String columnName = column.getColumnName();
			String columnType = allTypes.get(column.getType().getSimpleName().toLowerCase());
			createTableSQL += COMMA + NEWLINE + columnName + SPACE + columnType;
			if (column.isNotNull()) {
				createTableSQL += SPACE + NOTNULL;
			}
			if (column.isUnique()) {
				createTableSQL += SPACE + UNIQUE;
			}
		}
		for (ForeignKeyField foreignKey : foreignKeyFields) {
			if (foreignKey != null) {
				MetaModel<Class<?>> reference = MetaModel.of(foreignKey.getReference());
				String refTableName = reference.getTableName();
				String refPKColumnName = reference.getPrimaryKey().getColumnName();
				String fkColumnName = foreignKey.getColumnName();
				String integer = allTypes.get(INTEGER);
				createTableSQL += COMMA + NEWLINE + fkColumnName + SPACE + integer;
				if (foreignKey.isNotNull()) {
					createTableSQL += SPACE + NOTNULL; // not null constraint
				}
				createTableSQL += SPACE + REFERENCES + SPACE + refTableName + OP + refPKColumnName + CP;
				createTableSQL += SPACE + ON + SPACE + UPDATE + SPACE + CASCADE + SPACE + ON + SPACE + DELETE + SPACE + CASCADE;
			}
		}
		createTableSQL += CP + SEMICOLON; // end statement
		metaModels.add(metaModel);
		return (createTableSQL);
	}
	
	/*
	 * Builds the SQL String from the Given MetaModel for a updating of a table in the db
	 * @param metamodel for the given class
	 * @return fully formed SQL string
	 */
	protected static String update(Object obj, String str) {
		String updateTableSQL = "";
		
		Field field = null;
		Object value = null;
		Object id = null;
		boolean isString = false;
		MetaModel<Class<?>> model =  MetaModel.of(obj.getClass());
		String tableName = model.getTableName();
		String pkColumnName = model.getPrimaryKey().getColumnName();
		String pk = "";
		String name = "";
		Class<?> clazz = null;
		try {
			clazz = Class.forName(model.getClassName());
		} catch (ClassNotFoundException error) {
			logger.error("That class could not be found.");
			error.printStackTrace();
		}
		for(ColumnField column: model.getColumns()) {
			isString = column.getType().equals(String.class);
			if(column.getName().equalsIgnoreCase(str)) {
				name = column.getColumnName();
			}
				try {
					field = clazz.getDeclaredField(column.getName());
					field.setAccessible(true);
					value = field.get(obj);
					pk = model.getPrimaryKey().getName();
					field = clazz.getDeclaredField(pk);
					field.setAccessible(true);
					id = field.get(obj);
				} catch (NoSuchFieldException e) {
					logger.error("No such field with the given name " + str);
					e.printStackTrace();
				} catch (SecurityException e) {
					logger.error("Not allowed to access that field.");
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					logger.error("Invalid format for update string.");
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					logger.error("Invalid format for update string.");
					e.printStackTrace();
				}
		}
		updateTableSQL += UPDATE + SPACE + tableName + NEWLINE;
		updateTableSQL += SPACE + SET + SPACE + name + SPACE + EQUALS + SPACE;
		if(isString) {
			updateTableSQL += SINGLEQUOTE + value + SINGLEQUOTE;
		}
		else {
			updateTableSQL += value;
		}
		updateTableSQL += SPACE + WHERE + SPACE + pkColumnName;
		updateTableSQL += SPACE + EQUALS + SPACE + id + SEMICOLON;
		return updateTableSQL;
	}
	
	protected static String remove(Object obj) {
		String removeStringSql = "";
		MetaModel<Class<?>> model =  MetaModel.of(obj.getClass());
		// save the table name to a variable
		String tableName = model.getTableName();
		Field field = null;
		Object id = null;
		PrimaryKeyField pk = model.getPrimaryKey();
		String primaryKeyColumnName = pk.getColumnName();
		Class<?> clazz = null;
		try {
			clazz = Class.forName(model.getClassName());
		} 
		catch (ClassNotFoundException error) {
			logger.error("That class could not be found.");
			error.printStackTrace();
		}
		try {
					field = clazz.getDeclaredField(pk.getName());
					field.setAccessible(true);
					id = field.get(obj);
				} catch (NoSuchFieldException e) {
					logger.error("No such field with the given name.");
					e.printStackTrace();
				} catch (SecurityException e) {
					logger.error("Not allowed to access that field.");
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					logger.error("Invalid format for update string.");
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					logger.error("Invalid format for update string.");
					e.printStackTrace();
				}
		
	
		removeStringSql += DELETE + SPACE + FROM + SPACE + tableName + NEWLINE;
		removeStringSql += WHERE + SPACE + primaryKeyColumnName + SPACE + EQUALS + SPACE + id;
		removeStringSql += SEMICOLON;
		return removeStringSql;
	}

	protected static String save(Object obj) {
		// initialize temporary variables
		String saveStringSql = "";
		String valStr = "";
		Field field = null;
		Object id = null;
		Class<?> clazz = null;
		boolean isString = false;
		
		// initialize variables to hold pertinent information
		MetaModel<Class<?>> model =  MetaModel.of(obj.getClass());
		// save the table name to a variable
		String tableName = model.getTableName();
		List<String> columnNames = model.getColumnNameList();
		// get the set of all the columns in the meta model
		List<ColumnField> columnSet = model.getColumns();
		List<String> valueList = new LinkedList<String>();
		String columns = columnNames.stream().collect(Collectors.joining(","));

		/*
		 * Build the SQL String
		 */
		saveStringSql += INSERT + SPACE + INTO + SPACE + tableName + OP + columns + CP;
		saveStringSql += NEWLINE + VALUES + SPACE + OP;
		
		for(ColumnField column: columnSet) {
			isString = column.getType().equals(String.class);
			try {
				clazz = Class.forName(model.getClassName());
			} 
			catch (ClassNotFoundException error) {
				logger.error("That class could not be found.");
				error.printStackTrace();
			}
			try {
					valStr = "";
					field = clazz.getDeclaredField(column.getName()); // get the field from the columnfield
					field.setAccessible(true); // set accessible to true
					id = field.get(obj); // get the value of the object at the specified field
					if(isString) { // Strings in SQL need single quotes
						valStr += SINGLEQUOTE + id + SINGLEQUOTE;
					}
					else {
						valStr += id;
					}
					valueList.add(valStr);
			} 
			catch (NoSuchFieldException error) {
				logger.error("No such field with the given name.");
					error.printStackTrace();
			} 
			catch (SecurityException error) {
				logger.error("Not allowed to access that field.");
				error.printStackTrace();
			} 
			catch (IllegalArgumentException error) {
				logger.error("Invalid format.");
				error.printStackTrace();
			} 
			catch (IllegalAccessException error) {
				logger.error("Invalid format.");
				error.printStackTrace();
			}
		}
		for(ForeignKeyField key: model.getForeignKeys()) {
			try {
				clazz = Class.forName(model.getClassName());
			} 
			catch (ClassNotFoundException error) {
				logger.error("That class could not be found.");
				error.printStackTrace();
			}
			try {
				valStr = "";
				field = clazz.getDeclaredField(key.getName()); // get the field from the columnfield
				field.setAccessible(true); // set accessible to true
				id = field.get(obj); // get the value of the object at the specified field
				if(isString) { // Strings in SQL need single quotes
					valStr += SINGLEQUOTE + id + SINGLEQUOTE;
				}
				else {
					valStr += id;
				}
				valueList.add(valStr);
			} 
			catch (NoSuchFieldException error) {
				logger.error("No such field with the given name.");
					error.printStackTrace();
			} 
			catch (SecurityException error) {
				logger.error("Not allowed to access that field.");
				error.printStackTrace();
			} 
			catch (IllegalArgumentException error) {
				logger.error("Invalid format.");
				error.printStackTrace();
			} 
			catch (IllegalAccessException error) {
				logger.error("Invalid format.");
				error.printStackTrace();
			}
		}
		

		String values = valueList.stream().collect(Collectors.joining(","));
		saveStringSql += values;
		saveStringSql += CP + SPACE + ON + SPACE + CONFLICT + SPACE + DO + SPACE;
		saveStringSql += NOTHING + SEMICOLON;
		return saveStringSql;
	}

	public static String findAllObjectsInClass(Class<?> clazz) {
		String findAllClassSql = "";
		MetaModel<Class<?>> model =  MetaModel.of(clazz);
		// save the table name to a variable
		String tableName = model.getTableName();
		findAllClassSql += SELECT + SPACE + ALL + SPACE + ASTERISK + SPACE + FROM + SPACE + tableName;
		findAllClassSql += SEMICOLON;
		return findAllClassSql;
	}

	public static String findBy(Class<?> clazz, int id) {
		String findBySql = "";
		MetaModel<Class<?>> model =  MetaModel.of(clazz);
		// save the table name to a variable
		String tableName = model.getTableName();
		String pkName = model.getPrimaryKey().getColumnName();
		findBySql += SELECT + SPACE + ASTERISK + SPACE + FROM + SPACE + tableName + SPACE;
		findBySql += WHERE + SPACE + pkName + EQUALS  + id+ SEMICOLON;
		return findBySql;
	}
	
	
}
