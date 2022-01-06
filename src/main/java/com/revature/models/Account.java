package com.revature.models;

import java.io.Serializable;

import com.revature.annotations.Column;
import com.revature.annotations.Entity;
import com.revature.annotations.Id;
import com.revature.annotations.JoinColumn;

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
	@JoinColumn(columnName="acc_owner")
	private int accOwner;
	@Column(columnName="status")
	private Status status;
	
	public Account() { // no args constructor
		
	}

	/*
	 * Fully Parameterized Constructor
	 */
	public Account(int id, double balance, int accOwner, Status pending) {
		super();
		this.id = id;
		this.balance = balance;
		this.accOwner = accOwner;
		this.status = pending;
	}
	
	// DB will create ID for us!
	public Account(double balance, int accOwner, Status active) {
		super();
		this.balance = balance;
		this.accOwner = accOwner;
		this.status = active;
	}

	// Getters/Setters
	public int getId() {
		return id;
	}
	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status status) {
		this.status = status;
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
		if(this.status == Status.Open) {
			return true;
		}
		return false;
	}

	public void setActive(Status active) {
		this.status = active;
	}

	@Override
	public String toString() {
		return "Account [id=" + id + ", balance=" + balance + ", accOwner=" + accOwner + ", status=" + status + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + accOwner;
		result = prime * result + (status != null ? 1231 : 1237);
		long temp;
		temp = Double.doubleToLongBits(balance);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + id;
		return result;
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
		if (accOwner != other.accOwner)
			return false;
		if (status != other.status)
			return false;
		if (Double.doubleToLongBits(balance) != Double.doubleToLongBits(other.balance))
			return false;
		if (id != other.id)
			return false;
		return true;
	}
	
	
	
	
	

}
