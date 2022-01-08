package com.ormadillo.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import javax.sql.DataSource;
import org.apache.commons.dbcp.ConnectionFactory;
import org.apache.commons.dbcp.DriverManagerConnectionFactory;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.pool.impl.GenericObjectPool;
import org.apache.log4j.Logger;

/**
 * COnnection Pooling means that connections are REUSED rather than created
 * each time a connection is requested.
 * 
 * In order to facilitate connection reuse, a memory cache of database connections
 * called a CONNECTION POOL, is maintained by a connection pooling module 
 * as a layer on top of any standard JDBC driver product.
 * 
 *
 */
public class ConnectionPool {

	private static int MAXCONNECTIONS;
	// Initialize a logger
	private static Logger logger = Logger.getLogger(ConnectionPool.class);
	private static Properties prop = new Properties(); 
	private static String JDBC_DRIVER;
	private static String JDBC_DB_URL;
	private static String JDBC_USER;
	private static String JDBC_PASS;
	
	static {
		// this class is instantiated to read from a properties file 
		// imported from java.util					
		try {
			prop.load(new FileReader("src\\main\\resources\\application.properties"));
			JDBC_DB_URL = prop.getProperty("url"); // Retrieve the URL
			JDBC_USER =  prop.getProperty("username"); // Retrieve the DB Username
			JDBC_PASS = prop.getProperty("password"); // Retrieve the DB Password
			JDBC_DRIVER = prop.getProperty("driver");
			MAXCONNECTIONS = Integer.valueOf(prop.getProperty("maxConnections"));
		}
		catch (FileNotFoundException error) {
			logger.error("Cannot locate application.properties file");
			error.printStackTrace();
		} catch (IOException error) {
			logger.error("Something wrong with application.properties file");
			error.printStackTrace();
		}
	}
	
	public ConnectionPool() {
		logger.info("Adding Connection to pool.");
	}
	
	// Generated Getters/Setters
	public String getJDBC_DRIVER() {
		return JDBC_DRIVER;
	}


	public void setJDBC_DRIVER(String jDBC_DRIVER) {
		JDBC_DRIVER = jDBC_DRIVER;
	}


	public String getJDBC_DB_URL() {
		return JDBC_DB_URL;
	}


	public void setJDBC_DB_URL(String jDBC_DB_URL) {
		JDBC_DB_URL = jDBC_DB_URL;
	}


	public String getJDBC_USER() {
		return JDBC_USER;
	}


	public void setJDBC_USER(String jDBC_USER) {
		JDBC_USER = jDBC_USER;
	}


	public String getJDBC_PASS() {
		return JDBC_PASS;
	}


	public void setJDBC_PASS(String jDBC_PASS) {
		JDBC_PASS = jDBC_PASS;
	}

	private static GenericObjectPool gPool = null;
	
	public DataSource setUpPool () throws Exception {

	// We use the DataSource Interface to create a connection object that participates in Connection Pooling
		Class.forName(JDBC_DRIVER);
		
		// create an instnace of the GenericObjectPOol that holds our Pool of connection objects
		gPool = new GenericObjectPool();
		gPool.setMaxActive(MAXCONNECTIONS);
		
		// Create a connectionFacotry object which will be used by the pool object to creats the connections (which are all objects)
		ConnectionFactory cf = new DriverManagerConnectionFactory(JDBC_DB_URL, JDBC_USER, JDBC_PASS);
		

		// Create a PoolableConnectionFactory that will wrap around the Connection
		// Object created by the above connectionFactory
		// in order to add pooling functionality.
		PoolableConnectionFactory pcf = new PoolableConnectionFactory(cf, gPool, null, null, false, true);
		
		return new PoolingDataSource(gPool);
	}
	
	
	public static GenericObjectPool getConnectionPool() {
		return gPool;
	}
	
	// for our own benefitlet's create a method to print the connection pool status
	public void printDbStatus() {
		
		System.out.println("Max: " + getConnectionPool().getMaxActive() + "; Active: " + getConnectionPool().getNumActive() +
				"; Idle: " + getConnectionPool().getNumIdle());
	}
	
	
	
	
	
	

}