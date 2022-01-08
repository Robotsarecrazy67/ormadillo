package com.ormadillo.sql;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

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
					statement.executeUpdate(query);
					logger.info("Created Table " + tableName);
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
				statement.executeUpdate(query);
				logger.info("Updated columns " + update_columns + SPACE + tableName);
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
			statement.executeUpdate(query);
			logger.info("Removed Object From " + tableName);
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
			//statement.executeUpdate(query);
			logger.info("Saved Object to " + tableName);
			return true;
		}
		catch (SQLException error) {
					logger.error("Unable to save obj to DB");
					error.printStackTrace();
		}
		return false;
	}

	@Override
	public List<Class<?>> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void findBy(int id) {
		// TODO Auto-generated method stub
		
	}



}
