package com.revature;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Scanner;
import javax.sql.DataSource;
import org.apache.log4j.Logger;

import com.revature.fields.ColumnField;
import com.revature.fields.ForeignKeyField;
import com.revature.fields.PrimaryKeyField;
import com.revature.models.Account;
import com.revature.models.User;
import com.revature.util.Configuration;
import com.revature.util.ConnectionPool;
import com.revature.util.MetaModel;

public class App {
	private static Configuration cfg = new Configuration();
	static Scanner scan = new Scanner(System.in);
	private static Logger logger = Logger.getLogger(App.class);
	private static Connection conn = null;

	public static void main(String[] args) {
		// Create a connection
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
			
		try {
				DataSource dataSource = cfg.getConnection();
				conn = dataSource.getConnection();
					
				// this is initializing the pool and setting it up with the amount of connections we specified
				jdbcObj.printDbStatus();
					
				// get the connection (from the pool)
				System.out.println("==============Making a new connection Object for a DB operation!===========");
					
				// print the dbStatus()
				jdbcObj.printDbStatus();
					
				// For each @entity you added to the orm, select everything from the table
				String accounts = cfg.getMetamodel(Account.class).getTableName();
				System.out.println(accounts);
				String sql = "SELECT * FROM "+ accounts;
				stmt = conn.prepareStatement(sql);
				rs = stmt.executeQuery();
				while(rs.next()) {
					int id = rs.getInt("acc_id");
					System.out.println("++++++++++++++++++++++++++++++++++++++");
					System.out.println("+ Id:            +    " + id);
					System.out.println("+ Balance:       +    " + rs.getDouble("balance"));
					System.out.println("+ Owner Id:      +    " + rs.getInt("acc_owner"));
					System.out.println("+ Status:        +    " + rs.getObject("status"));
					System.out.println("++++++++++++++++++++++++++++++++++++++");
				}
				String users = cfg.getMetamodel(User.class).getTableName();
				sql = "SELECT * FROM "+users;
				System.out.println(users);
				stmt = conn.prepareStatement(sql);
				rs = stmt.executeQuery();
				while(rs.next()) {
					System.out.println(rs.getString("username"));
					System.out.println(rs.getString("pwd"));
				}
			
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			
			jdbcObj.printDbStatus();
	}
}


