package constraint;

import misc.ArrayUtils;
import validate.ParameterException;

public class NumberConstraint extends AbstractConstraint<Number>{
	
	public NumberConstraint(String element, NumberOperator numberOperator, Number... parameters) throws ParameterException{
		super(element, numberOperator, parameters);
		if(parameters.length > 1 && parameters[0].doubleValue() > parameters[1].doubleValue()){
			ArrayUtils.swap(parameters, 0, 1);
		}
		
	}
	
	public static void main(String[] args) throws Exception {
		NumberConstraint c = new NumberConstraint("creditAmount", NumberOperator.NOT_IN_INTERVAL, 60, 13.75);
//		NumberConstraint c = new NumberConstraint("creditAmount", NumberOperator.EQUAL, 60.0);
		System.out.println(c);
		System.out.println(c.validate(10));
		System.out.println(c.getParameterType());
		System.out.println(c.getParameterClass());
	}

	@Override
	public NumberOperator getOperator() {
		return (NumberOperator) super.getOperator();
	}

	@Override
	public NumberConstraint clone() {
		NumberConstraint result = null;
		try {
			result = new NumberConstraint(new String(element), getOperator(), parameters.clone());
		} catch (ParameterException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	

}
