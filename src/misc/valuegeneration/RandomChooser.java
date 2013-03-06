package misc.valuegeneration;

import java.util.List;
import java.util.Random;

import validate.ParameterException;
import validate.Validate;

public class RandomChooser<E extends Object> implements ValueChooser<E> {
	
	Random rand = new Random();

	@Override
	public E chooseValue(List<E> candidates) throws ParameterException {
		Validate.notNull(candidates);
		Validate.notEmpty(candidates);
		
		return candidates.get(rand.nextInt(candidates.size()));
	}

	@Override
	public boolean isValid() {
		return true;
	}

}
