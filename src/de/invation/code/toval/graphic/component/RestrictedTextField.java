package de.invation.code.toval.graphic.component;

import java.awt.FlowLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.text.Document;

import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;

public class RestrictedTextField extends JTextField {

	private static final long serialVersionUID = 3020973663591477811L;
	
	private String oldValue = null;
	private Restriction restriction = null;

	public RestrictedTextField(Restriction restriction, Document doc, String text, int columns) {
		super(doc, text, columns);
		initialize(restriction);
	}

	public RestrictedTextField(Restriction restriction, int columns) {
		super(columns);
		initialize(restriction);
	}

	public RestrictedTextField(Restriction restriction, String text, int columns) {
		super(text, columns);
		initialize(restriction);
	}

	public RestrictedTextField(Restriction restriction, String text) {
		super(text);
		initialize(restriction);
	}

	public RestrictedTextField(Restriction restriction){
		super();
		initialize(restriction);
	}
	
	private void initialize(Restriction restriction){
		this.restriction = restriction;
		this.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				super.focusGained(e);
				oldValue = getText();
			}

			public void focusLost(FocusEvent e) {
				super.focusLost(e);
				validateInput();
			}
		});
		
		this.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				super.keyTyped(e);
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					validateInput();
				}
			}
			
		});
	}
	
	private void validateInput() {
		try {
			switch (restriction) {
			case NEGATIVE_DOUBLE:
				Validate.negativeDouble(getText());
				break;
			case NEGATIVE_INTEGER:
				Validate.negativeInteger(getText());
				break;
			case POSITIVE_DOUBLE:
				Validate.positiveDouble(getText());
				break;
			case POSITIVE_INTEGER:
				Validate.positiveInteger(getText());
				break;
			case ZERO_OR_NEGATIVE_DOUBLE:
				Validate.notPositiveDouble(getText());
				break;
			case ZERO_OR_NEGATIVE_INTEGER:
				Validate.notPositiveInteger(getText());
				break;
			case ZERO_OR_POSITIVE_DOUBLE:
				Validate.notNegativeDouble(getText());
				break;
			case ZERO_OR_POSITIVE_INTEGER:
				Validate.notNegativeInteger(getText());
				break;
			case NOT_EMPTY:
				Validate.notEmpty(getText());
				break;
			case NONE:
				break;
			}
		} catch (ParameterException e) {
			setText(oldValue);
		}
		valueChanged(oldValue, getText());
	}
	
	protected void valueChanged(String oldValue, String newValue){}
	
	public enum Restriction {
		POSITIVE_INTEGER, NEGATIVE_INTEGER, POSITIVE_DOUBLE, NEGATIVE_DOUBLE,
		ZERO_OR_POSITIVE_INTEGER, ZERO_OR_NEGATIVE_INTEGER, ZERO_OR_POSITIVE_DOUBLE, ZERO_OR_NEGATIVE_DOUBLE,
		NOT_EMPTY, NONE;
	}
	
	public static void main(String[] args) {
		JPanel panel = new JPanel(new FlowLayout());
		RestrictedTextField t1 = new RestrictedTextField(Restriction.POSITIVE_INTEGER, "20");
		RestrictedTextField t2 = new RestrictedTextField(Restriction.POSITIVE_DOUBLE, "20.5");
		panel.add(t1);
		panel.add(t2);
		new DisplayFrame(panel, true);
	}

}
