package de.invation.code.toval.constraint;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;

import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;
import de.invation.code.toval.validate.ParameterException.ErrorCode;


public abstract class AbstractConstraint<T extends Object> {
	
	protected String element = null;
	protected Operator<? super T> operator = null;
	protected T[] parameters = null;
	
	public AbstractConstraint(String element, Operator<? super T> operator, T... parameters) throws ParameterException{
		super();
		setOperator(operator);
		setElement(element);
		setParameters(parameters);
	}
	
	public void setElement(String element) throws ParameterException{
		Validate.notNull(element);
		this.element = element;
	}
	
	public void setOperator(Operator<? super T> operator) throws ParameterException{
		Validate.notNull(operator);
		this.operator = operator;
	}
	
	public void setParameters(T... parameters) throws ParameterException{
		Validate.notNull(parameters);
		Validate.notEmpty(parameters);
		Validate.noNullElements(parameters);
		if(parameters.length != (operator.getRequiredArguments() - 1))
			throw new ParameterException(ErrorCode.INCONSISTENCY, "Operator " + operator + " requires " + (operator.getRequiredArguments() -1) + " arguments.");
		
		this.parameters = parameters;
	}
	
	public String getElement(){
		return element;
	}
	
	public Operator<? super T> getOperator(){
		return operator;
	}
	
	public T[] getParameters(){
		return parameters.clone();
	}
	
	@Override
	public String toString(){
		return String.format(operator.getStringFormat(), createParameterArray(element, parameters));
	}
	
	private Object[] createParameterArray(Object comparator, Object... constraintParameters){
		Object[] result = new Object[constraintParameters.length + 1];
		result[0] = comparator;
		for(int i=1; i< result.length; i++){
			result[i] = constraintParameters[i-1];
		}
		return result;
	}
	
	public Type getParameterType(){
		Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        return ((ParameterizedType) superclass).getActualTypeArguments()[0];
	}
	
	@SuppressWarnings("rawtypes")
	public Class getParameterClass(){
		Type actualTypeArgument = getParameterType();
	    if (actualTypeArgument instanceof ParameterizedType) {
	        return (Class<?>) ((ParameterizedType) actualTypeArgument).getRawType();
	    } else {
	        return (Class<?>) actualTypeArgument;
	    }
	}
	
	public boolean validate(Object value) throws ParameterException {
		return operator.validate(value, parameters);
	}
	
	public abstract AbstractConstraint<T> clone();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((element == null) ? 0 : element.hashCode());
		result = prime * result
				+ ((operator == null) ? 0 : operator.hashCode());
		result = prime * result + Arrays.hashCode(parameters);
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
		@SuppressWarnings("rawtypes")
		AbstractConstraint other = (AbstractConstraint) obj;
		if (element == null) {
			if (other.element != null)
				return false;
		} else if (!element.equals(other.element))
			return false;
		if (operator == null) {
			if (other.operator != null)
				return false;
		} else if (!operator.equals(other.operator))
			return false;
		if (!Arrays.equals(parameters, other.parameters))
			return false;
		return true;
	}
	
	

}
