package de.invation.code.toval.constraint;

import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.ParameterException.ErrorCode;

public class StringConstraint extends AbstractConstraint<String> {

	public StringConstraint(String element, StringOperator stringOperator, String... parameters) throws ParameterException{
		super(element, stringOperator, parameters);
	}	

	public static StringConstraint parse(String constraint) throws ParameterException {
		
		//Find element
		int endElementIndex = constraint.indexOf(" ");
		if(endElementIndex == -1)
			throw new ParameterException(ErrorCode.INCOMPATIBILITY);
		String element = constraint.substring(0, endElementIndex);
		
		//Find operator
		if(constraint.length()-endElementIndex <2)
			throw new ParameterException(ErrorCode.INCOMPATIBILITY);
		String restString = constraint.substring(endElementIndex+1);
		int endOperatorIndex = restString.indexOf(" ");
		if(endOperatorIndex == -1)
			throw new ParameterException(ErrorCode.INCOMPATIBILITY);
		String operatorString = restString.substring(0, endOperatorIndex);
		StringOperator operator = StringOperator.parse(operatorString);
		if(operator == null)
			throw new ParameterException(ErrorCode.INCOMPATIBILITY);
		
		//Find parameter
		if(restString.length()-endOperatorIndex <2)
			throw new ParameterException(ErrorCode.INCOMPATIBILITY);
		String parameter = restString.substring(endOperatorIndex+1);
		
		return new StringConstraint(element, operator, parameter);
	}
	

	@Override
	public StringOperator getOperator() {
		return (StringOperator) super.getOperator();
	}

	@Override
	public StringConstraint clone() {
		StringConstraint result = null;
		try {
			result = new StringConstraint(new String(element), getOperator(), parameters.clone());
		} catch (ParameterException e) {
			e.printStackTrace();
		}
		return result;
	}

	public static void main(String[] args) throws Exception {
		StringConstraint c = new StringConstraint("name", StringOperator.NOT_EQUAL, "Gerd");
		System.out.println(c);
		System.out.println(c.validate(12));
	}
	
}
