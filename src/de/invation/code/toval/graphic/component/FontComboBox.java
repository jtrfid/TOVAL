package de.invation.code.toval.graphic.component;

import java.awt.Component;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import de.invation.code.toval.validate.Validate;

public class FontComboBox extends JComboBox {
	
	private static final long serialVersionUID = -2088962747490626507L;

	public FontComboBox(DisplayMode displayMode){
		super();
		Validate.notNull(displayMode);
		GraphicsEnvironment e = GraphicsEnvironment.getLocalGraphicsEnvironment();
		if(displayMode == DisplayMode.FONT_NAME){
			Font[] availableFonts = e.getAllFonts();
			Arrays.sort(availableFonts, new LexicalFontComparator());
			setModel(new DefaultComboBoxModel(availableFonts));
		} else {
			setModel(new DefaultComboBoxModel(FontFamilyEnum.values()));
		}
	    setRenderer(new FontComboBoxCellRenderer(displayMode));
	}
	
	public FontComboBox(DisplayMode displayMode, String initialValue){
		this(displayMode);
		if(displayMode == DisplayMode.FONT_NAME){
			Font initialFont = Font.getFont(initialValue);
			setSelectedItem(initialFont);
		} else {
			setSelectedItem(initialValue);
		}
	}
	
	private class FontComboBoxCellRenderer extends JLabel implements ListCellRenderer {
		
		private static final long serialVersionUID = -8102511925600836012L;
		
		private DisplayMode displayMode = null;

		public FontComboBoxCellRenderer(DisplayMode displayMode){
			this.displayMode = displayMode;
			setOpaque(true);
	        setHorizontalAlignment(LEFT);
	        setVerticalAlignment(CENTER);
		}

		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			if(displayMode == DisplayMode.FONT_NAME){
				setText(((Font) value).getFontName());
			} else {
				setText(value.toString());
			}
			return this;
		}
		
	}
	
	public enum DisplayMode {
		FONT_NAME, FONT_FAMILY;
	}
	
	private enum FontFamilyEnum {
		Dialog, DialogInput, Monospaced, SansSerif, Serif;
	}
	
	private class LexicalFontComparator implements Comparator<Font> {
		@Override
		public int compare(Font o1, Font o2) {
			return o1.getFontName().compareTo(o2.getFontName());
		}
	}

}
