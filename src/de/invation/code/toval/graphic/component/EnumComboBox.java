package de.invation.code.toval.graphic.component;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import de.invation.code.toval.validate.Validate;

public class EnumComboBox<E extends Enum<E>> extends JComboBox{

	private static final long serialVersionUID = -3998550187333465250L;

	public EnumComboBox(Class<E> enumeration){
		Validate.notNull(enumeration);
		setModel(new DefaultComboBoxModel(enumeration.getEnumConstants()));
	}

	@SuppressWarnings("unchecked")
	@Override
	public E getSelectedItem() {
		return (E) super.getSelectedItem();
	}
	
	

}
