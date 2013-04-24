package constraint;

import validate.ParameterException;

public class StringConstraint extends AbstractConstraint<String> {

	public StringConstraint(String element, StringOperator stringOperator, String... parameters) throws ParameterException{
		super(element, stringOperator, parameters);
	}	

	public static void main(String[] args) throws Exception {
		StringConstraint c = new StringConstraint("name", StringOperator.NOT_EQUAL, "Gerd");
		System.out.println(c);
		System.out.println(c.validate(12));
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

}
