package com.ormadillo.fields;

import java.lang.reflect.Field;
import java.util.Objects;

import com.ormadillo.annotations.JoinColumn;

/*
 * Manager for the @JoinColumn Annotation 
 */
public class ForeignKeyField {
	
	private Field field; // from java.lang.reflect
	
	public ForeignKeyField(Field field) {
		
		if(field.getAnnotation(JoinColumn.class) == null) {
			throw new IllegalStateException("Cannot create ColumnField object! Provided field, "
					+ getName() + "is not annotated with @JoinColumn");
		}
		this.field = field;
	}
	
	public String getName() {
		return field.getName();
	}
	
	public Class<Integer> getType() {
		return Integer.TYPE;
	}
	
	public String getColumnName() {
		return field.getAnnotation(JoinColumn.class).columnName();
	}
	public boolean isNotNull() {
		return field.getAnnotation(JoinColumn.class).notNull();
	}
	
	public Class<?> getReference(){
		return field.getAnnotation(JoinColumn.class).references();
	}
	
	public Object getValue(Object obj){
		try {
			return field.get(obj);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int hashCode() {
		return Objects.hash(field);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ForeignKeyField other = (ForeignKeyField) obj;
		return Objects.equals(field, other.field);
	}
	
	
}
