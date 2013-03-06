package constraint;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import validate.ParameterException;
import validate.ParameterException.ErrorCode;
import validate.Validate;

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

}
