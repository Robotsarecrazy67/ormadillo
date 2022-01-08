package com.ormadillo.models;

import java.io.Serializable;
import java.util.Objects;

import com.ormadillo.annotations.Column;
import com.ormadillo.annotations.Entity;
import com.ormadillo.annotations.Id;
import com.ormadillo.annotations.JoinColumn;

@Entity(tableName="accounts")
public class Account implements Serializable{

	private static final long serialVersionUID = 1L; // the compiler liked this


	/**
	 * Class Fields
	 */
	@Id(columnName="acc_id")
	private int id;
	@Column(columnName="balance")
    private double balance;
	@JoinColumn(columnName="acc_owner", notNull=true, references=User.class)
	private int accOwner;
	@Column(columnName="active", notNull=true, unique=false)
	private boolean active;
	
	public Account() { // no args constructor
		
	}

	/*
	 * Fully Parameterized Constructor
	 */
	public Account(int id, double balance, int accOwner, boolean pending) {
		super();
		this.id = id;
		this.balance = balance;
		this.accOwner = accOwner;
		this.active = pending;
	}
	
	// DB will create ID for us!
	public Account(double balance, int accOwner, boolean active) {
		super();
		this.balance = balance;
		this.accOwner = accOwner;
		this.active = active;
	}

	// Getters/Setters
	public int getId() {
		return id;
	}
	public boolean getStatus() {
		return active;
	}
	
	public void setStatus(boolean status) {
		this.active = status;
	}
	
	public void setId(int id) {
		this.id = id;
	}

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public int getAccOwner() {
		return accOwner;
	}

	public void setAccOwner(int accOwner) {
		this.accOwner = accOwner;
	}

	public Boolean isActive() {
		return this.active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return "Account [id=" + id + ", balance=" + balance + ", accOwner=" + accOwner + ", status=" + active + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(accOwner, balance, id, active);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Account other = (Account) obj;
		return accOwner == other.accOwner && Double.doubleToLongBits(balance) == Double.doubleToLongBits(other.balance)
				&& id == other.id && active == other.active;
	}

	
	
	
	
	

}
