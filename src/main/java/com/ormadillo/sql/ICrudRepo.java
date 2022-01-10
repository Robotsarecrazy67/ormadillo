package com.ormadillo.sql;

import java.util.List;
import java.util.Optional;
import com.ormadillo.util.MetaModel;

public interface ICrudRepo<T> {
	public abstract boolean create(MetaModel<Class<?>> metaModelList);
	public abstract boolean update(Object obj, String update_columns);
	public abstract boolean remove(Object obj);
	public abstract boolean save(Object obj);
	public abstract Optional<List<Object>> findAll(Class<?> clazz);
	Optional<List<Object>> findById(Class<?> clazz, int id);	
}
