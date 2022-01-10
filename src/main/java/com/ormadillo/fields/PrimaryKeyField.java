package com.ormadillo.fields;

import java.lang.reflect.Field;
import java.util.Objects;

import com.ormadillo.annotations.Column;
import com.ormadillo.annotations.Id;

public class PrimaryKeyField {
	
	private Field field; // from java.lang.reflect
	
	public PrimaryKeyField(Field field) {
		
		if(field.getAnnotation(Id.class) == null) {
			throw new IllegalStateException("Cannot create ColumnField object! Provided field, "
					+ getName() + "is not annotated with @Id");
		}
		this.field = field;
	}
	
	public String getName() {
		return field.getName();
	}
	
	// return the TYPE of the field that's annotated
	public Class<?> getType() {
		return field.getType();
	}
	
	// get columnName() -=- extract the column name attribute from the column annotation
	public String getColumnName() {
		return field.getAnnotation(Id.class).columnName();
	}
	
	public Object getValue(Object obj){
		field.setAccessible(true);
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
		PrimaryKeyField other = (PrimaryKeyField) obj;
		return Objects.equals(field, other.field);
	}

	
}
