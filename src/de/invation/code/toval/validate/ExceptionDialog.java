package de.invation.code.toval.validate;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import de.invation.code.toval.graphic.dialog.AbstractDialog;
import de.invation.code.toval.graphic.dialog.StringDialog;

public class ExceptionDialog extends AbstractDialog<Exception>{
	
	private static final long serialVersionUID = -2350754359368195069L;
	
	public static final boolean DEFAULT_CONCAT_CAUSE_MESSAGES = false;
	private static final Dimension MIN_DIMENSION = new Dimension(400,300);
	
	private JButton btnStackTrace;
	private boolean concatCauseMessages = DEFAULT_CONCAT_CAUSE_MESSAGES;

	protected ExceptionDialog(Window owner, String title, Exception exception, boolean concatCauseMessages){
		super(owner, title);
		this.concatCauseMessages = concatCauseMessages;
		setIncludeCancelButton(false);
		Validate.notNull(exception);
		setDialogObject(exception);
	}

	@Override
	public Dimension getMinimumSize() {
		return MIN_DIMENSION;
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
		showException(title, exception, DEFAULT_CONCAT_CAUSE_MESSAGES);
	}
	
	public static void showException(String title, Exception exception, boolean concatCauseMessages) {
		showException(null, title, exception, concatCauseMessages);
	}
	
	public static void showException(Window owner, String title, Exception exception) {
		showException(owner, title, exception, DEFAULT_CONCAT_CAUSE_MESSAGES);
	}
	
	public static void showException(Window owner, String title, Exception exception, boolean concatCauseMessages) {
		try {
			ExceptionDialog dialog = new ExceptionDialog(owner, title, exception, concatCauseMessages);
			dialog.setUpGUI();
		} catch(Exception e){
			e.printStackTrace();
			JOptionPane.showMessageDialog(owner, "Cannot launch ExceptionDialog.\nReason: " + e.getMessage(), "Internal Exception", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	protected void addComponents() throws Exception {
		mainPanel().setLayout(new BorderLayout());
		if(concatCauseMessages){
			mainPanel().add(getConcatenatedCauseMessages(), BorderLayout.CENTER);
		} else {
			mainPanel().add(new JLabel(getDialogObject().getMessage()), BorderLayout.PAGE_START);
		}
	}
	
	private JComponent getConcatenatedCauseMessages(){
		List<String> messages = new ArrayList<String>();
		if(getDialogObject().getMessage() != null && !getDialogObject().getMessage().isEmpty())
			messages.add(getDialogObject().getMessage());
		Throwable cause = getDialogObject();
		while((cause = cause.getCause()) != null){
			if(cause.getMessage() != null && !cause.getMessage().isEmpty())
				messages.add(cause.getMessage());
		}
		JTextArea area = new JTextArea();
		for(String message: messages){
			area.append(message);
			area.append("\n");
		}
		area.setEditable(false);
		return new JScrollPane(area);
	}

	@Override
	protected void setTitle() {}
	
//	public static void main(String[] args) {
//		ExceptionDialog.showException("Tesrt", new IOException("Teste                                            g", new IOException("Test2")), false);
//	}

}
