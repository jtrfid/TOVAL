package de.invation.code.toval.misc.soabase;

import java.util.Collection;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;

import de.invation.code.toval.validate.Validate;

public class SOABaseComboBox extends JComboBox {

	private static final long serialVersionUID = -7002182269586188L;

	public SOABaseComboBox(Collection<SOABase> contexts){
		super();
		Validate.notNull(contexts);
		setModel((new DefaultComboBoxModel(contexts.toArray())));
		setRenderer(new SOABaseCellRenderer());
	}

	@Override
	public SOABase getSelectedItem() {
		return (SOABase) super.getSelectedItem();
	}

	
}
