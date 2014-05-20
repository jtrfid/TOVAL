package de.invation.code.toval.reflect;

import java.lang.reflect.InvocationTargetException;

public class GenericReflection {
	
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(T obj) 
	throws InstantiationException,
		   IllegalAccessException,
		   InvocationTargetException,
		   NoSuchMethodException
	{
		Object newobj = obj.getClass().getConstructor().newInstance();
		return (T) newobj;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T newInstance(Class<T> classType) 
			throws InstantiationException,
				   IllegalAccessException,
				   InvocationTargetException,
				   NoSuchMethodException
			{
				Object newobj = classType.getConstructor().newInstance();
				return (T) newobj;
			}
	
	@SuppressWarnings("unchecked")
	public static <T> Class<? extends T> getComponentType(T[] a) {
		Class<?> k = a.getClass().getComponentType();
		return (Class<? extends T>) k;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T[] newArray(Class<? extends T> k, int size) {
		if(k.isPrimitive())
			throw new IllegalArgumentException("Argument cannot be primitive: "+k);
		Object a = java.lang.reflect.Array.newInstance(k, size);
		return (T[]) a;
	}

	public static <T> T[] newArray(T[] a, int size){
		return newArray(getComponentType(a), size);
	}
	
}
