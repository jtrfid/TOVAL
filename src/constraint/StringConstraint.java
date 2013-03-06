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

}
