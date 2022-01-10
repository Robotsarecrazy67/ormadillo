package orm.ormadillo.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import javax.sql.DataSource;
import org.apache.log4j.Logger;

import com.ormadillo.annotations.Entity;
import com.ormadillo.sql.CrudOps;
import com.ormadillo.util.ClassGrabber;
import com.ormadillo.util.ConnectionPool;
import com.ormadillo.util.MetaModel;

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
	private static List<MetaModel<Class<?>>> metaModelList;
	private boolean AUTOCOMMIT = false;
	private HashMap<Class<?>, HashSet<Object>> cache;
	CrudOps crud = new CrudOps(this);
	
	public Configuration() {
		addAllClassesToORM();
		createAllTables();
		cache = new HashMap<Class<?>, HashSet<Object>>();
	}
	
	static {
		Properties prop = new Properties();	// use to read from a properties file 			
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
	
	// This method doesn't technically follow SRP Single Responsibility Principle
	public Configuration addAnnotatedClass(Class<?> annotatedClass) {

		
		// first check if a linked List has been instantiated...
		// if not, instantiate it!
		if (metaModelList == null) {
			metaModelList = new LinkedList<MetaModel<Class<?>>>();
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
			e.printStackTrace();
			return null;
		}
	}
	
	public MetaModel<Class<?>> getMetamodel(Class<?> clazz){
		Optional<MetaModel<Class<?>>> option = null;
		if(metaModelList !=  null && !metaModelList.isEmpty()) {
			option = metaModelList.stream()
					.filter(e->e.getSimpleClassName().equals(clazz.getSimpleName()))
					.findFirst();
			return option.isPresent() ? option.get(): null;
		}
		return null; // unable to get metamodel
	}
	
	/*
	 * Dynamically adds all annotated classes to the ORM
	 */
	private void addAllClassesToORM() {
		ClassGrabber cg = new ClassGrabber();
		Set<Class<?>> classSet = cg.grabAllClasses(packageName);
		if(classSet != null) {
			for(Class<?> clazz: classSet) {	
				if(clazz.getAnnotation(Entity.class) != null) {
					addAnnotatedClass(clazz);
				}
			}
			logger.info("Loaded annotated classes into the Ormadillo");
		}
		else {
			logger.error("Unable to load classes into the Ormadillo.");
		}

	}
	
	/*
	 * Creates All tables that are present in the metamodel list
	 */
	private void createAllTables() {
		for(MetaModel<Class<?>> metaModel: metaModelList) {
			crud.create(metaModel);
		}
	}
	
	/*
	 * Updates the given object in the database
	 * @ param update_columns comma separated list for all columns in the object that need to be updated
	 * @ return boolean result of if we were successful or not
	 */
	public boolean updateObjectInDB(Object obj, String update_columns) {
		return crud.update(obj, update_columns);
	}
	
	/*
	 * Removes the given object from the database
	 * @param obj given object to remove from db
	 * @ return boolean result of if we were successful or not
	 */
	public boolean removeObjectFromDB(Object obj) {
		return crud.remove(obj);
	}
	
	/*
	 * Adds the given object to the database
	 */
	public boolean addObjectToDB(Object obj){
		return crud.save(obj);
	}
	
	public Optional<List<Object>> getListObjectFromDB(Class<?> clazz){
		Optional<List<Object>> result = crud.findAll(clazz);
		return result;
	}
	
	public Object getObjectFromDBById(Class<?> clazz, int id){
		Optional<List<Object>> result = crud.findById(clazz, id);

		if(result.get().isEmpty()) {
			logger.error(clazz.getSimpleName() + " with id " + id + " does not exist in the database");
			return null;
		}
		if(result!=null){
			logger.info("Retrieved " + clazz.getSimpleName() + " with id " + id );
		}
		return result.get().get(0);
	}
	
	public HashMap<Class<?>, HashSet<Object>> getCache(){
		return cache;
	}
	
	public void addAllFromDBToCache(Class<?> clazz) {
		List<Object> list = getListObjectFromDB(clazz).isPresent() ? getListObjectFromDB(clazz).get() : null;
		cache.put(clazz, (HashSet<Object>) list.stream().collect(Collectors.toSet()));
		if(list!=null){
			logger.info("Added all objects of the class " + clazz.getSimpleName() + " from the database to the cache");
		}
	}
	
	public void enableAutoCommit() {
		this.AUTOCOMMIT = true;
	}
	
	public boolean getAutoCommit() {
		return this.AUTOCOMMIT;
	}
	
	public void setTransaction() {
		crud.setTransaction();
	}
	
	public void commit() {
		crud.commit();
	}
	
	public void rollback() {
		crud.rollback();
	}
	
	public void rollback(String string) {
		crud.rollback(string);
	}
	
	public void setSavePoint(String savePoint) {
		crud.setSavepoint(savePoint);
	}
	
	public void releaseSavepoint(String savePoint) {
		crud.releaseSavepoint(savePoint);
	}
}
