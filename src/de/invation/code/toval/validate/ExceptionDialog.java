package de.invation.code.toval.validate;

import java.awt.BorderLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import de.invation.code.toval.graphic.dialog.AbstractDialog;
import de.invation.code.toval.graphic.dialog.StringDialog;

public class ExceptionDialog extends AbstractDialog<Exception>{
	
	private static final long serialVersionUID = -2350754359368195069L;
	
	private JButton btnStackTrace;

	protected ExceptionDialog(Window owner, String title, Exception exception){
		super(owner, title);
		setIncludeCancelButton(false);
		Validate.notNull(exception);
		setDialogObject(exception);
	}

	private JButton getButtonStackTrace(){
		if(btnStackTrace == null){
			btnStackTrace = new JButton("Stack Trace");
			btnStackTrace.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						StringDialog.showDialog(ExceptionDialog.this, "Stack Trace", getStackTrace(), false);
					} catch (Exception e1) {
						internalExceptionMessage("Cannot launch StringDialog.\nReason: " + e1.getMessage());
					}
				}
			});
		}
		return btnStackTrace;
	}
	
	private String getStackTrace(){
		final StringWriter sw = new StringWriter();
		final PrintWriter pw = new PrintWriter(sw, true);
		getDialogObject().printStackTrace(pw);
		return sw.getBuffer().toString();
	}
	
	@Override
	protected List<JButton> getLefthandButtons() {
		List<JButton> buttons = new ArrayList<JButton>();
		buttons.add(getButtonStackTrace());
		return buttons;
	}

	public static void showException(String title, Exception exception) {
		showException(null, title, exception);
	}
	
	public static void showException(Window owner, String title, Exception exception) {
		try {
			ExceptionDialog dialog = new ExceptionDialog(owner, title, exception);
			dialog.setUpGUI();
		} catch(Exception e){
			JOptionPane.showMessageDialog(owner, "Cannmot launch ExceptionDialog.\nReason: " + e.getMessage(), "Internal Exception", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	protected void addComponents() throws Exception {
		mainPanel().setLayout(new BorderLayout());
		mainPanel().add(new JLabel(getDialogObject().getMessage()), BorderLayout.PAGE_START);
	}

	@Override
	protected void setTitle() {}

}
