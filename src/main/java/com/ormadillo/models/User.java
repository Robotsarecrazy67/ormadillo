package com.ormadillo.models;

import java.io.Serializable;
import java.util.List;

import com.ormadillo.annotations.Column;
import com.ormadillo.annotations.Entity;
import com.ormadillo.annotations.Id;
import com.ormadillo.annotations.JoinColumn;

// Java Bean
@Entity(tableName="users")
public class User implements Serializable {

	// class variables
	@Id(columnName="user_id")
	private int id; // represented in our DB as a SERIAL PRIMARY KEY
	@Column(columnName="username", notNull=true, unique = true)
	private String username;
	@Column(columnName="pwd")
	private String password;

	// no-args constructor 
	public User() {
		
	}
	
	// fully parameterized constructor
	public User(int id, String username, String password) {
		super();
		this.id = id;
		this.username = username;
		this.password = password;
	}

	/*
	 * Constructor
	 */
	public User(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
	
	// Getters/Setters
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	/*
	 * Hash code function
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		result = prime * result + ((password == null) ? 0 : password.hashCode());
		result = prime * result + ((username == null) ? 0 : username.hashCode());
		return result;
	}

	/*
	 * Equals
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		if (id != other.id)
			return false;
		if (password == null) {
			if (other.password != null)
				return false;
		} else if (!password.equals(other.password))
			return false;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", password=" + password
				+ "]";
	}

	
	
	
	

}
