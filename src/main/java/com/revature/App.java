package com.revature;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.revature.models.Account;
import com.revature.models.Role;
import com.revature.models.Status;
import com.revature.models.User;
import com.revature.util.ColumnField;
import com.revature.util.Configuration;
import com.revature.util.ConnectionPool;
import com.revature.util.ForeignKeyField;
import com.revature.util.MetaModel;
import com.revature.util.PrimaryKeyField;

public class App {
	private static Configuration cfg = new Configuration();
	static Scanner scan = new Scanner(System.in);
	private static Logger logger = Logger.getLogger(App.class);
	private static Connection conn = null;

	public static void main(String[] args) {
		// Start the Application
			
			// We need to "load" the classes of our project into the ORM 
			// we need to convert them to meta models
			// we also need to establish a connection to the DB by feeding our
			// DB credentials to the ORM
			
			// Hibernate reads from a hibernate.cfg.xml file
			// You can also set db creds programatically like so 
			
			// The user might read the documentation and know to instantiate a Configuration object
			
			
			// Step 1 of operations with the Configuration Object is adding the annotated classes
			
			
			// for each annotated class, add it to the orm
//			cfg.addAllClassesToORM();
//			cfg.addAnnotatedClass(Account.class);
//			cfg.addAnnotatedClass(User.class);
			
			// this class is instantiated to read from a properties file 
			
			// Step 2 of operations with the Configuration Object is adding the DB creds and connecting
			cfg.getConnection();
			
			// iterate over each class that has been added to the configuration object and print info about it
			
			for (MetaModel<?> metamodel : cfg.getMetaModels()) {
				
				System.out.printf("Printing MetaModel for class: %s\n", metamodel.getClassName());
				PrimaryKeyField pk = metamodel.getPrimaryKey();
				List<ColumnField> columns = metamodel.getColumns();
				List<ForeignKeyField> foreignKeyFields = metamodel.getForeignKeys();
				
				System.out.printf("\t Found a primary key field named %s, of type %s, which maps to the column with name: %s\n", 
						pk.getName(), pk.getType(), pk.getColumnName());
				
				for (ColumnField column : columns) {
					System.out.printf("\t Found a column field named %s, of type %s, which maps to the column with name: %s\n", 
							column.getName(), column.getType(), column.getColumnName());
				}
				
				for (ForeignKeyField foreignKey : foreignKeyFields) {
					if(foreignKey != null) {
						System.out.printf("\t Found a foreign key field named %s, of type %s, which maps to the column with name: %s\n", 
							foreignKey.getName(), foreignKey.getType(), foreignKey.getColumnName());
					}
					
				}		
			}
			
			ConnectionPool jdbcObj = new ConnectionPool();
			PreparedStatement stmt = null;
			ResultSet rs;
			// surround everything ins a try/catch
			try {
				
				DataSource dataSource = cfg.getConnection();
				conn = dataSource.getConnection();
				
				// this is intializing the pool and setting it up with the amount of connections we specified
				jdbcObj.printDbStatus();
				
				// get the connection (from the pool)
				System.out.println("==============Making a new connection Object for a DB operation!===========");
				
				// print the dbStatus()
				jdbcObj.printDbStatus();
				
				// For each @entity you added to the orm, select everything from the table
				String sql = "SELECT * FROM "+cfg.getMetamodel(Account.class).getTableName();
				stmt = conn.prepareStatement(sql);
				rs = stmt.executeQuery();
				while(rs.next()) {
					System.out.println(rs.getString("username"));
				}
				String sql1 = "SELECT * FROM "+cfg.getMetamodel(User.class).getTableName();
				System.out.println(sql1);
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			jdbcObj.printDbStatus();

	}
}


