package de.invation.code.toval.constraint;

import de.invation.code.toval.misc.ArrayUtils;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.ParameterException.ErrorCode;

public class NumberConstraint extends AbstractConstraint<Number>{
	
	public NumberConstraint(String element, NumberOperator numberOperator, Number... parameters) throws ParameterException{
		super(element, numberOperator, parameters);
		if(parameters.length > 1 && parameters[0].doubleValue() > parameters[1].doubleValue()){
			ArrayUtils.swap(parameters, 0, 1);
		}
		
	}
	
public static NumberConstraint parse(String constraint) throws ParameterException {
		
		//Find element
		int endElementIndex = constraint.indexOf(" ");
		if(endElementIndex == -1)
			throw new ParameterException(ErrorCode.INCOMPATIBILITY);
		String element = constraint.substring(0, endElementIndex);
//		System.out.println("Element: %" + element + "%");
		
		//Find operator
		if(constraint.length()-endElementIndex <2)
			throw new ParameterException(ErrorCode.INCOMPATIBILITY);
		String restString = constraint.substring(endElementIndex+1);
//		System.out.println("Rest: %" + restString + "%");
		int endOperatorIndex = restString.indexOf(" ");
		if(endOperatorIndex == -1)
			throw new ParameterException(ErrorCode.INCOMPATIBILITY);
		String operatorString = restString.substring(0, endOperatorIndex);
		NumberOperator operator = NumberOperator.parse(operatorString);
		if(operator == null && !operatorString.equals("?"))
			throw new ParameterException(ErrorCode.INCOMPATIBILITY);
		if(restString.length()-endOperatorIndex <2)
			throw new ParameterException(ErrorCode.INCOMPATIBILITY);
		restString = restString.substring(endOperatorIndex+1);
//		System.out.println("Rest after operator: %" + restString + "%");
		
		boolean inInterval = restString.startsWith("[") && restString.endsWith("]");
		boolean notInInterval = restString.startsWith("]") && restString.endsWith("[");
		
		Double parameter1 = null;
		Double parameter2 = null;
		if((inInterval || notInInterval) && operatorString.equals("?")){
			operator = inInterval ? NumberOperator.IN_INTERVAL : NumberOperator.NOT_IN_INTERVAL;
			//Extract parameters
			if(restString.length()<5)
				throw new ParameterException(ErrorCode.INCOMPATIBILITY);
			String parameter1String = restString.substring(1, restString.indexOf(";"));
			String parameter2String = restString.substring(restString.indexOf(";")+1, restString.length()-1);
			try{
				parameter1 = Double.valueOf(parameter1String);
				parameter2 = Double.valueOf(parameter2String);
			} catch(Exception e){
				throw new ParameterException(ErrorCode.INCOMPATIBILITY);
			}
//			System.out.println("%"+parameter1String+"%");
//			System.out.println("%"+parameter2String+"%");
		} else {
			try{
				parameter1 = Double.valueOf(restString);
			} catch(Exception e){
				throw new ParameterException(ErrorCode.INCOMPATIBILITY);
			}
		}
		if(parameter2 == null){
			return new NumberConstraint(element, operator, parameter1);
		} else {
			return new NumberConstraint(element, operator, parameter1, parameter2);
		}
}

	@Override
	public NumberOperator getOperator() {
		return (NumberOperator) super.getOperator();
	}

	@Override
	public NumberConstraint clone() {
		NumberConstraint result = null;
		try {
			result = new NumberConstraint(new String(element), getOperator(),parameters.clone());
		} catch (ParameterException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void main(String[] args) throws Exception {
		NumberConstraint c = new NumberConstraint("creditAmount", NumberOperator.NOT_IN_INTERVAL, 60, 13.75);
//		NumberConstraint c = new NumberConstraint("creditAmount", NumberOperator.EQUAL, 60.0);
		System.out.println(c);
		System.out.println(c.validate(10));
		System.out.println(c.getParameterType());
		System.out.println(c.getParameterClass());
	}

	
	

}
