package de.invation.code.toval.misc.soabase;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;


class SOABaseCellRenderer extends JLabel implements ListCellRenderer {

	private static final long serialVersionUID = -1210820666079991184L;

	public SOABaseCellRenderer() {
		setOpaque(true);
		setHorizontalAlignment(LEFT);
		setVerticalAlignment(CENTER);
	}

	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		 if (isSelected) {
	            setBackground(list.getSelectionBackground());
	            setForeground(list.getSelectionForeground());
	        } else {
	            setBackground(list.getBackground());
	            setForeground(list.getForeground());
	        }
		
		 if(value instanceof SOABase){
				setText(((SOABase) value).getName());
			} else {
				setText("undef.");
			}
		return this;
	}

}
