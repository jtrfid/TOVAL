package de.invation.code.toval.graphic.component;

import java.util.Arrays;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import de.invation.code.toval.validate.Validate;

public class EnumComboBox<E extends Enum<E>> extends JComboBox{

	private static final long serialVersionUID = -3998550187333465250L;

	public EnumComboBox(Class<E> enumeration){
		this(enumeration, false);
	}
	
	public EnumComboBox(Class<E> enumeration, boolean allowNull){
		Validate.notNull(enumeration);
		int padding = allowNull ? 1 : 0;
		Object[] objects = Arrays.copyOf(enumeration.getEnumConstants(), enumeration.getEnumConstants().length + padding);
		setModel(new DefaultComboBoxModel(objects));
	}

	@SuppressWarnings("unchecked")
	@Override
	public E getSelectedItem() {
		try{
			return (E) super.getSelectedItem();
		} catch(Exception e){
			return null;
		}
	}
	
	

}
