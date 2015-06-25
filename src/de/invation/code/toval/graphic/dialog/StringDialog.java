package de.invation.code.toval.graphic.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class StringDialog extends AbstractDialog<String> {

	private static final long serialVersionUID = 4501959307493776929L;
	
	private static final Dimension PREFERRED_SIZE = new Dimension(400,300);
	
	private JTextArea textArea;
	private boolean isEditable = false;

	protected StringDialog(Window parent, String text) {
		super(parent);
		setDialogObject(text);
	}
	
	protected StringDialog(Window parent, String text, boolean isEditable) {
		super(parent);
		setDialogObject(text);
		this.isEditable = isEditable;
	}
	
	protected StringDialog(Window parent, String title, String text) {
		super(parent, title);
		setDialogObject(text);
	}
	
	protected StringDialog(Window parent, String title, String text, boolean isEditable) {
		super(parent, title);
		setDialogObject(text);
		this.isEditable = isEditable;
	}
        
    @Override
    protected void initialize() {
        setIncludeCancelButton(isEditable);
        if (isEditable) {
            setButtonPanelLayout(ButtonPanelLayout.LEFT_RIGHT);
        } else {
            setButtonPanelLayout(ButtonPanelLayout.CENTERED);
        }
    }

	@Override
	protected void addComponents() throws Exception {
		mainPanel().setLayout(new BorderLayout());
		JScrollPane scrollPane = new JScrollPane();
		mainPanel().add(scrollPane, BorderLayout.CENTER);
		textArea = new JTextArea();
		textArea.setEditable(isEditable);
		textArea.setText(getDialogObject());
		scrollPane.setViewportView(textArea);
	}

	@Override
	protected void setTitle() {}

	@Override
	protected void okProcedure() {
		setDialogObject(textArea.getText());
		super.okProcedure();
	}

	@Override
	public Dimension getMinimumSize() {
		return PREFERRED_SIZE;
	}

	@Override
	public Dimension getPreferredSize() {
		return PREFERRED_SIZE;
	}

	public static String showDialog(Window owner, String text) throws Exception{
		StringDialog dialog = new StringDialog(owner, text);
		dialog.setUpGUI();
		return dialog.getDialogObject();
	}
	
	public static String showDialog(Window owner, String text, boolean isEditable) throws Exception{
		StringDialog dialog = new StringDialog(owner, text, isEditable);
		dialog.setUpGUI();
		return dialog.getDialogObject();
	}
	
	public static String showDialog(Window owner, String title, String text) throws Exception{
		StringDialog dialog = new StringDialog(owner, title, text);
		dialog.setUpGUI();
		return dialog.getDialogObject();
	}
	
	public static String showDialog(Window owner, String title, String text, boolean isEditable) throws Exception{
		StringDialog dialog = new StringDialog(owner, title, text, isEditable);
		dialog.setUpGUI();
		return dialog.getDialogObject();
	}
	
	public static void main(String[] args) throws Exception {
		String result = StringDialog.showDialog(null, "Testtile", "DFas ist ein Textm, den es anzuschauen und zu verbesern gilt.");
		System.out.println(result);
	}

}
