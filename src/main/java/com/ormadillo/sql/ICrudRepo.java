package com.ormadillo.sql;

import java.util.List;

import com.ormadillo.util.MetaModel;

import orm.ormadillo.config.Configuration;

public interface ICrudRepo<T> {
	public abstract boolean create(MetaModel<Class<?>> metaModelList);
	public abstract boolean update(final Object obj, final String update_columns);
	
	public abstract boolean remove(final Object obj);
	
	public abstract boolean save(final Object obj);
	
	public abstract List<Class<?>> findAll();
	
	public abstract void findBy(int id);	
}
