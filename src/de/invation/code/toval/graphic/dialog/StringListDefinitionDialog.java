package de.invation.code.toval.graphic.dialog;
import java.awt.Window;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import de.invation.code.toval.graphic.util.SpringUtilities;
import de.invation.code.toval.misc.StringUtils;


public class StringListDefinitionDialog extends AbstractDialog {

	private static final long serialVersionUID = 6102535150943274087L;
	
	public static final Border DEFAULT_BORDER = BorderFactory.createEmptyBorder(5, 5, 5, 5);
	
	private JTextField inputField;
	
	protected StringListDefinitionDialog(Window owner, String title) {
		super(owner, title);
	}

	@Override
	protected void addComponents() throws Exception {
		setResizable(true);
		mainPanel().setLayout(new SpringLayout());
		JLabel lblNumber = new JLabel("Number:");
		lblNumber.setHorizontalAlignment(SwingConstants.RIGHT);
		mainPanel().add(lblNumber);
		mainPanel().add(getInputField());
		SpringUtilities.makeCompactGrid(mainPanel(), 1, 2, 0, 0, 5, 0);
	}

	private JTextField getInputField(){
		if(inputField == null){
			inputField = new JTextField("Valid separators: (semi-)colon, space");
			inputField.setColumns(10);
		}
		return inputField;
	}

	@Override
	protected void setTitle() {}
	
	@Override
	protected void okProcedure() {
		if(inputField.getText().isEmpty()){
			JOptionPane.showMessageDialog(StringListDefinitionDialog.this, "Cannot proceed with empty String.", "Invalid Parameter", JOptionPane.ERROR_MESSAGE);
			return;
		}
		int colons = StringUtils.countOccurrences(inputField.getText(), ',');
		int semicolons = StringUtils.countOccurrences(inputField.getText(), ';');
		if(colons > 0 && semicolons > 0){
			JOptionPane.showMessageDialog(StringListDefinitionDialog.this, "String contains more than one possible delimiter.", "Invalid Parameter", JOptionPane.ERROR_MESSAGE);
			return;
		}
		String delimiter = " ";
		if(colons>0)
			delimiter = ",";
		if(semicolons>0)
			delimiter = ";";
		
		setDialogObject(new ArrayList<String>());
		StringTokenizer tokenizer = new StringTokenizer(inputField.getText(), delimiter);
		while(tokenizer.hasMoreTokens()){
			String token = tokenizer.nextToken();
			if(!delimiter.equals(" "))
				token = StringUtils.removeSurrounding(token, ' ');
			getDialogObject().add(token);
		}
		super.okProcedure();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected List<String> getDialogObject() {
		return (List<String>) super.getDialogObject();
	}
	
	public static List<String> showDialog(Window owner, String title) throws Exception{
		StringListDefinitionDialog dialog = new StringListDefinitionDialog(owner, title);
		dialog.setUpGUI();
		return dialog.getDialogObject();
	}

}
