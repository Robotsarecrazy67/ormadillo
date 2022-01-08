package com.ormadillo.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

/*
 * Class is responsible for grabbing classes contained within Java packages
 */
public class ClassGrabber {
	// Constants
	private static Logger logger = Logger.getLogger(ClassGrabber.class);
	private static final String DOT = "[.]";
	private static final String PATH_SEPARATOR = "/";
	private static final String CLASS = ".class";
	
	/*
	 * Default Constructor
	 */
	public ClassGrabber(){
		// no argument constructor
	}
	
	/*
	 * Accesses all the classes within a package and add them to a Set
	 * @param packageName - name of the package
	 * @returns Set containing all of the classes within the package
	 */
    public Set<Class<?>> grabAllClasses(String packageName) {
    	
    	// converts the package name into a url string
    	String url = packageName.replaceAll(DOT, PATH_SEPARATOR);
    	
    	// read the classes as a string of urls
        InputStream classes = ClassLoader.getSystemClassLoader()
        								 .getResourceAsStream(url); 
        
        if(classes==null) {
        	logger.error("Unable to find models package with the specified name.");
        	return null;
        }
        else {
        	        BufferedReader allClasses = new BufferedReader(new InputStreamReader(classes));
        return allClasses.lines() // each line contains a Class.class
          .filter(clazz -> clazz.endsWith(CLASS)) // filter out the .class
          .map(clazz -> grabClass(clazz, packageName)) // across all classes, get the names of each class
          .collect(Collectors.toSet()); // collect the results to a set
        }

    }
 
    /*
     * Grabs a class from a package with the given name
     * @param className - name of the class
     * @param packageName - name of the package
     * @return Class object with the given name
     */
    private Class<?> grabClass(String className, String packageName) {
        try {
            return Class.forName(packageName + "."
              + className.substring(0, className.lastIndexOf('.'))); // return the class with the given name
        } catch (ClassNotFoundException error) {
        	error.getStackTrace();
        	return null; // class does not exist
        }
    }
}
