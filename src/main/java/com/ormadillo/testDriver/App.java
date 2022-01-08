package com.ormadillo.testDriver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import javax.sql.DataSource;

import com.ormadillo.fields.ColumnField;
import com.ormadillo.fields.ForeignKeyField;
import com.ormadillo.fields.PrimaryKeyField;
import com.ormadillo.models.Account;
import com.ormadillo.models.User;
import com.ormadillo.sql.SqlBuilder;
import com.ormadillo.util.ConnectionPool;
import com.ormadillo.util.MetaModel;

import orm.ormadillo.config.Configuration;

public class App {
	private static Configuration cfg = new Configuration();
	static Scanner scan = new Scanner(System.in);
	private static Connection conn = null;

	public static void main(String[] args) {
		// Create a connection
		cfg.getConnection();
		List<MetaModel<Class<?>>> set = cfg.getMetaModels();
		User user = new User(3,  "berry", "crippled");
		User user2 = new User("phill", "hello");
		
		// iterate over each class that has been added to the configuration object and print info about it
			
//		for (MetaModel<?> metamodel : set) {
//				System.out.printf("Printing MetaModel for class: %s\n", metamodel.getClassName());
//				PrimaryKeyField pk = metamodel.getPrimaryKey();
//				Set<ColumnField> columns = metamodel.getColumns();
//				Set<ForeignKeyField> foreignKeyFields = metamodel.getForeignKeys();
//					
//				System.out.printf("\t Found a primary key field named %s, of type %s, which maps to the column with name: %s\n", 
//						pk.getName(), pk.getType(), pk.getColumnName());
//					
//				for (ColumnField column : columns) {
//					System.out.printf("\t Found a column field named %s, of type %s, which maps to the column with name: %s\n", 
//							column.getName(), column.getType(), column.getColumnName());
//				}
//					
//				for (ForeignKeyField foreignKey : foreignKeyFields) {
//					if(foreignKey != null) {
//						System.out.printf("\t Found a foreign key field named %s, of type %s, which maps to the column with name: %s\n", 
//						foreignKey.getName(), foreignKey.getType(), foreignKey.getColumnName());
//					}
//						
//				}		
//			}
//			
//		ConnectionPool jdbcObj = new ConnectionPool();
//		PreparedStatement stmt = null;
//		ResultSet rs;
//			
//		try {
//				DataSource dataSource = cfg.getConnection();
//				conn = dataSource.getConnection();
//					
//				// this is initializing the pool and setting it up with the amount of connections we specified
//				jdbcObj.printDbStatus();
//					
//				// get the connection (from the pool)
//				System.out.println("==============Making a new connection Object for a DB operation!===========");
//					
//				// print the dbStatus()
//				jdbcObj.printDbStatus();
//					
//				// For each @entity you added to the orm, select everything from the table
//				String accounts = cfg.getMetamodel(Account.class).getTableName();
//				System.out.println(accounts);
//				String sql = "SELECT * FROM "+ accounts;
//				stmt = conn.prepareStatement(sql);
//				rs = stmt.executeQuery();
//				while(rs.next()) {
//					int id = rs.getInt("acc_id");
//					System.out.println("++++++++++++++++++++++++++++++++++++++");
//					System.out.println("+ Id:            +    " + id);
//					System.out.println("+ Balance:       +    " + rs.getDouble("balance"));
//					System.out.println("+ Owner Id:      +    " + rs.getInt("acc_owner"));
//					System.out.println("+ Active:        +    " + rs.getObject("active"));
//					System.out.println("++++++++++++++++++++++++++++++++++++++");
//				}
//				String users = cfg.getMetamodel(User.class).getTableName();
//				sql = "SELECT * FROM "+users;
//				System.out.println(users);
//				stmt = conn.prepareStatement(sql);
//				rs = stmt.executeQuery();
//				while(rs.next()) {
//					System.out.println(rs.getString("username"));
//					System.out.println(rs.getString("pwd"));
//				}
//			
//			} 
//		catch (SQLException error) {
//				error.printStackTrace();
//			}
//			
//			jdbcObj.printDbStatus();		
//		for(Map.Entry<String, String> key: DefineTable.getAllTypeMap().entrySet()) {
//			System.out.println(key.getKey() + " = " + key.getValue());
//		}
////			
//		
//		
		
		cfg.updateObjectInDB(user, "username,password");
		//cfg.removeObjectFromDB(user);
		cfg.addObjectToDB(user2);
	}
}


