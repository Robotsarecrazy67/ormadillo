package com.ormadillo.fields;

import java.lang.reflect.Field;
import java.util.Objects;

import com.ormadillo.annotations.Column;

/**
 * The purpose of this class is to extract
 * fields of a class marked with the @Column
 * annotation which I've defined in my annotations
 * package.
 * 
 * I'll use this class to extract the data type of those
 * fields so I get a better idea of what SQL TYPE constraints
 * would best represent that column in a database.
 */
public class ColumnField {
	// this class models the column we're setting up from a class' fields
	
	private Field field; // from java.lang.reflect
	
	
	public ColumnField(Field field) {
		
		if(field.getAnnotation(Column.class) == null) {

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

	
	// get columnName() -=- extract the column name attribute from the column annotation
	public String getColumnName() {
		return field.getAnnotation(Column.class).columnName();
	}
	
	public boolean isNotNull() {
		return field.getAnnotation(Column.class).notNull();
	}
	
	public boolean isUnique() {
		return field.getAnnotation(Column.class).unique();
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
		ColumnField other = (ColumnField) obj;
		return Objects.equals(field, other.field);
	}
	
	
}
