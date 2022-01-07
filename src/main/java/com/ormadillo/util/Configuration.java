package com.ormadillo.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

import com.ormadillo.annotations.Entity;

/**
 * The purpose of this class is to have the User only provide a few
 * things in order for the ORM to establish a connection and build the tables
 * based on a list of User-Defined classes that the user passes to the ORM to
 * Introspect and construct in the DB 
 *
 */
public class Configuration {
	private static Logger logger = Logger.getLogger(Configuration.class);
	private static String packageName;
	private static ConnectionPool conPool = new ConnectionPool(); 
	
	public Configuration() {
		addAllClassesToORM();
	}
	
	static {
		
		// this class is instantiated to read from a properties file 
		Properties prop = new Properties();				
		try {
				prop.load(new FileReader("src\\main\\resources\\application.properties"));
				packageName = prop.getProperty("packageName"); // Retrieve the model package name
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
	
	
	

	// this is the list of classes that the user wants our ORM to "scan" aka introspect and build 
	// as DB objects
	private List<MetaModel<Class<?>>> metaModelList;
	
	// This method doesn't technically follow SRP Single Responsibility Principle
	public Configuration addAnnotatedClass(Class annotatedClass) {

		
		// first check if a linked List has been instantiated...
		// if not, instantiate it!
		if (metaModelList == null) {
			metaModelList = new LinkedList<>();
		}
		
		// we need to call the method that transforms a class into an appropriate
		// data model that our ORM can introspect (a MetaModel)
		metaModelList.add(MetaModel.of(annotatedClass));
		
		return this; // returns the configuration object on which the addAnnotatedClass() method is being called
	}
	
	public List<MetaModel<Class<?>>> getMetaModels() {
		
		// in the case that the metaModelList of the Configuration object is empty, return an empty list.
		// otherwise, return the metaModelList.
		return (metaModelList == null) ? Collections.emptyList() : metaModelList;
	}
	
	public DataSource getConnection() {
		try {
			return conPool.setUpPool();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public MetaModel<Class<?>> getMetamodel(Class<?> clazz){
		Optional<MetaModel<Class<?>>> option = null;
	
		option = metaModelList.stream()
						.filter(e->e.getSimpleClassName().equals(clazz.getSimpleName()))
						.findFirst();

		return option.isPresent() ? option.get(): null;
	}
	
	public void addAllClassesToORM() {
		ClassGrabber cg = new ClassGrabber();
		Set<Class<?>> classSet = cg.grabAllClasses(packageName);
		
		for(Class<?> clazz: classSet) {	
			if(clazz.getAnnotation(Entity.class) != null) {
				addAnnotatedClass(clazz);
			}
		}
	}

}
