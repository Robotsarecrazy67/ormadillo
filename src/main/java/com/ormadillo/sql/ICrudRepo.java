package com.ormadillo.sql;

public interface ICrudRepo<T> {
	public abstract boolean update(T t);
	
	public abstract boolean remove(T t);
	
	public abstract boolean save(T t);
	
	public abstract void findAll();
	
	public abstract void findById(int id);	
}
