package com.ormadillo.sql;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;
import javax.sql.DataSource;
import org.apache.log4j.Logger;
import com.ormadillo.annotations.Column;
import com.ormadillo.annotations.Id;
import com.ormadillo.annotations.JoinColumn;
import com.ormadillo.util.MetaModel;
import orm.ormadillo.config.Configuration;

public class CrudOps implements ICrudRepo<Class<?>> {
	private Configuration cfg;
	private static Connection conn = null;
	private static Logger logger = Logger.getLogger(CrudOps.class);
	private static final String SPACE = " ";
	private static final String COMMA = ",";
	private static boolean autoBuildTables;
	private static final String TRUE = "true";
	private static final String COMMIT = "COMMIT;";
	private static final String BEGIN = "BEGIN;";
	private LinkedList<String> transactions;
	
	public CrudOps(Configuration config) {
		this.cfg = config;
	}
	
	static {
		Properties prop = new Properties();	// use to read from a properties file 			
		try {
				prop.load(new FileReader("src\\main\\resources\\application.properties"));
				autoBuildTables = prop.getProperty("autoBuildTables").equalsIgnoreCase(TRUE);
		}
		catch (FileNotFoundException error) {
			logger.error("Cannot locate application.properties file");
			error.printStackTrace();
		} 
		catch (IOException error) {
			logger.error("Something wrong with application.properties file");
			error.printStackTrace();
		}
	}
	
	@Override
	public boolean create(MetaModel<Class<?>> metaModel) {
		if(autoBuildTables) {
			String tableName = metaModel.getTableName();
			DataSource dataSource = cfg.getConnection();
			try {
					conn = dataSource.getConnection();
					Statement statement = conn.createStatement();
					String query = SqlBuilder.createTableIfDoesNotExist(metaModel);	
					return true;
				}
				catch (SQLException error) {
						logger.error("Unable to create table " + tableName);
						error.printStackTrace();
				}
		}
		return false;
	}
	
	@Override
	public boolean update(final Object obj, final String update_columns) {
		MetaModel<Class<?>> metaModel = MetaModel.of(obj.getClass());
		List<String> queries = new LinkedList<String>();
		String[] columns = update_columns.split(COMMA);
		for(String str: columns) {
			String update = SqlBuilder.update(obj, str);
			queries.add(update);
		}
		for(String query: queries) {
			String tableName = metaModel.getTableName();
			DataSource dataSource = cfg.getConnection();
			try {
				conn = dataSource.getConnection();
				Statement statement = conn.createStatement();
				if(cfg.getAutoCommit()) {
					statement.executeUpdate(query);
					logger.info("Updated columns " + update_columns + SPACE + tableName);
				}
				else {
					if(transactions != null) {
						transactions.add(query);
						logger.info("Added Update Table " + tableName + " query to the transaction block");
					}
					else {
						logger.error("Not in a transaction block");
					}
				}
				return true;
			}
			catch (SQLException error) {
				logger.error("Unable to update " + tableName);
				error.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public boolean remove(final Object obj) {
		MetaModel<Class<?>> metaModel = MetaModel.of(obj.getClass());
		String tableName = metaModel.getTableName();
		DataSource dataSource = cfg.getConnection();
		try {
			conn = dataSource.getConnection();
			Statement statement = conn.createStatement();
			String query = SqlBuilder.remove(obj);
			if(cfg.getAutoCommit()) {
				statement.executeUpdate(query);
				logger.info("Removed Object From " + tableName);
			}
			else {
				if(transactions != null) {
					transactions.add(query);
					logger.info("Remove Object From Table " + tableName + " query to the transaction block");
				}
				else {
					logger.error("Not in a transaction block");
				}
			}
			
			return true;
		}
		catch (SQLException error) {
					logger.error("Unable to remove obj from DB");
					error.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean save(final Object obj) {
		MetaModel<Class<?>> metaModel = MetaModel.of(obj.getClass());
		String tableName = metaModel.getTableName();
		DataSource dataSource = cfg.getConnection();
		try {
			conn = dataSource.getConnection();
			Statement statement = conn.createStatement();
			String query = SqlBuilder.save(obj);
			if(cfg.getAutoCommit()) {
				statement.executeUpdate(query);
				logger.info("Saved Object to " + tableName);
			}
			else {
				if(transactions != null) {
					transactions.add(query);
					logger.info("Add Save Object to Table " + tableName + " query to the transaction block");
				}
				else {
					logger.error("Not in a transaction block");
				}
			}
			
			return true;
		}
		catch (SQLException error) {
					logger.error("Unable to save obj to DB");
					error.printStackTrace();
		}
		return false;
	}

	/*
	 * Returns an optional list of all of the objects in the database of a given type
	 */
	@Override
	public Optional<List<Object>> findAll(Class<?> clazz) {
		if(cfg.getCache().containsKey(clazz)){
			logger.info("Retrieved a  List of " + clazz.getSimpleName() + "s from the local cache");
			return Optional.of(cfg.getCache().get(clazz).stream().collect(Collectors.toList()));
		}
		String sql = SqlBuilder.findAllObjectsInClass(clazz);
		return Optional.of(getObjects(clazz, sql));
	}
	
	/*
	 * Formats an object list from the fields of each object class
	 */
	private List<Object> getObjects(Class<?> clazz, String sql) {
		ResultSet rs;
		Object dto = null, value;
		String name = "";
		int numColumns;
		List<Object> foundList = new LinkedList<Object>();
		
	    List<Field> fields = Arrays.asList(clazz.getDeclaredFields());
	    for(Field field: fields) {
	        field.setAccessible(true);
	    }
		
		DataSource dataSource = cfg.getConnection();
		try {
			    conn = dataSource.getConnection();
			    Statement statement = conn.createStatement();
			    if((rs = statement.executeQuery(sql))!=null) {
				    ResultSetMetaData metaData = rs.getMetaData();
				    numColumns = metaData.getColumnCount();
			    }
				while(rs.next()) {
			        dto = clazz.getConstructor().newInstance();

			        for(Field field: fields) {
			        	if(field != null) {
				            Column col = field.getAnnotation(Column.class);
				            Id id = field.getAnnotation(Id.class);
				            JoinColumn fk = field.getAnnotation(JoinColumn.class);
				            if(id != null) {
				                name = id.columnName();
					            if((value = rs.getInt(name)) != null) {
					            	field.set(dto, value);
					            }
				            }
				            if(col!=null) {
				                name = col.columnName();
					            if((value = rs.getObject(name)) != null) {
					            	field.set(dto, value);
					            }
				            }
				            if(fk!=null) {
				            	name = fk.columnName();
					            if((value = rs.getInt(name)) != null) {
					            	field.set(dto, value);
					            }
				            }
			        	}
			        }
			        foundList.add(dto);
				}
		}
		catch (SQLException error) {
			logger.error("Unable to fetch obj from DB");
			error.printStackTrace();
		} catch (InstantiationException error) {
			logger.error("Unable to initialize obj from DB");
			error.printStackTrace();
		} catch (IllegalAccessException error) {
			logger.error("Unable to access that obj in DB");
			error.printStackTrace();
		} catch (IllegalArgumentException error) {
			logger.error("Invalid Argument");
			error.printStackTrace();
		} catch (InvocationTargetException error) {
			logger.error("Unable to fetch obj from DB");
			error.printStackTrace();
		} catch (NoSuchMethodException error) {
			logger.error("Unable to fetch obj from DB");
			error.printStackTrace();
		} catch (SecurityException error) {
			logger.error("Unable to fetch obj from DB");
			error.printStackTrace();}
		return foundList;
	}
	
	@Override
	public Optional<List<Object>> findById(Class<?> clazz, int id) {
		 String name = "";
		 Object value = null;
		 Map<Class<?>, HashSet<Object>> cache = cfg.getCache();
		 List<Object> foundList = new LinkedList<Object>();
		 if(cache.containsKey(clazz)) {
			for(Object obj: cache.get(clazz)) {
				if(checkId(obj, value, name, id)) {
					foundList.add(obj);
					return Optional.of(foundList);
				}
			    
			}
		 }
		
		String sql = SqlBuilder.findBy(clazz, id);
		return Optional.of(getObjects(clazz, sql));
	}

	/*
	 * Helper method to determine if an id exists within a given object
	 */
	private boolean checkId(Object obj, Object value, String name, int givenValue) {
		List<Field> fields = Arrays.asList(obj.getClass().getDeclaredFields());

        for(Field field: fields) {
        	if(field != null) {
        		field.setAccessible(true);
	            Id id = field.getAnnotation(Id.class);
	            if(id != null) {
	                name = id.columnName();
	                try {
						value = field.get(obj);
					} catch (IllegalArgumentException e) {
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
		            if(value.equals(givenValue)) {
		            	return true;
		            }
	            }
        	}
        }
		return false;
	}

	
	public void commit() {
		transactions.add(COMMIT);
		// iterate through transactions and build a commit string
		transactions.stream().forEach(e->System.out.println(e));
	}
	
	public void rollback() {
		transactions = null;
	}
	
	public void rollback(String savePoint) {
		if(transactions != null && !transactions.isEmpty() && transactions.contains(savePoint)) {
			Iterator<String> iter = transactions.descendingIterator();
			while(iter.hasNext() && !iter.next().equals(savePoint)) {
				iter.remove();
			}
			releaseSavepoint(savePoint);
		}
	}
	
	public void setSavepoint(String savePoint) {
		transactions.add(savePoint);
	}
	
	public void releaseSavepoint(String savePoint) {
		transactions.remove(savePoint);
	}

	public LinkedList<String> getTransactions(){
		return transactions;
	}
	
	public void setTransaction(){
		transactions = new LinkedList<String>();
		transactions.add(BEGIN);
	}
	
}
