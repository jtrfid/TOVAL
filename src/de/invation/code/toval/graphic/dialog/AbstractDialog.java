package de.invation.code.toval.graphic.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import de.invation.code.toval.validate.Validate;

public abstract class AbstractDialog<O> extends JDialog {
	
	private static final long serialVersionUID = -5864654213215817665L;
	
	private static final ButtonPanelLayout DEFAULT_BUTTON_LAYOUT = ButtonPanelLayout.LEFT_RIGHT;
	public static final Border DEFAULT_BORDER = BorderFactory.createEmptyBorder(5, 5, 5, 5);
	
	private final JPanel panelContent = new JPanel();
	protected JPanel panelButtons = null;
	
	protected JButton btnOK = null;
	protected JButton btnCancel = null;
	protected O dialogObject;
	protected ButtonPanelLayout buttonLayout = DEFAULT_BUTTON_LAYOUT;
	protected Window owner = null;
	
	protected String okButtonText = "OK";
	protected boolean includeCancelButton = true;
	
	protected AbstractDialog(Window owner) {
		super(owner);
		this.owner = owner;
		initialize();
	}
	
	protected AbstractDialog(Window owner, String title) {
		this(owner);
		Validate.notNull(title);
		setTitle(title);
	}
	
	protected AbstractDialog(Window owner, ButtonPanelLayout buttonLayout) {
		this(owner);
		Validate.notNull(buttonLayout);
		this.buttonLayout = buttonLayout;
	}
	
	protected void initialize(){}
	
	protected void setUpGUI() throws Exception{
		this.setResizable(true);
		this.setModal(true);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		addListeners();
		
		setTitle();
		
		mainPanel().setBorder(getBorder());
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mainPanel(), BorderLayout.CENTER);
		getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
		getRootPane().setDefaultButton(getDefaultButton());
		
		addComponents();
		
		initializeContent();

		pack();
		this.setLocationRelativeTo(owner);
		this.setVisible(true);
	}
	
	protected void initializeContent() throws Exception {}

	private void addListeners() {
		addWindowListener(new WindowListener() {	
			@Override
			public void windowOpened(WindowEvent e) {}
			@Override
			public void windowIconified(WindowEvent e) {}
			@Override
			public void windowDeiconified(WindowEvent e) {}
			@Override
			public void windowDeactivated(WindowEvent e) {}
			@Override
			public void windowClosing(WindowEvent e) {
				closingProcedure();
			}
			@Override
			public void windowClosed(WindowEvent e) {}
			@Override
			public void windowActivated(WindowEvent e) {}
		});
		
		addComponentListener(new ComponentAdapter(){
	        public void componentResized(ComponentEvent e){
	            Dimension d = AbstractDialog.this.getSize();
	            Dimension minD = AbstractDialog.this.getMinimumSize();
	            if(d.width<minD.width)
	            	d.width=minD.width;
	            if(d.height<minD.height)
	            	d.height=minD.height;
	            AbstractDialog.this.setSize(d);
	        }
	    });
	}
	
	public void setOKButtonText(String buttonText){
		Validate.notNull(buttonText);
		okButtonText = buttonText;
		if(btnOK != null){
			btnOK.setText(buttonText);
		}
	}
	
	public void setIncludeCancelButton(boolean includeCancelButton){
		this.includeCancelButton = includeCancelButton;
	}

	protected Border getBorder(){
		return DEFAULT_BORDER;
	}
	
	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	
	
	protected O getDialogObject(){
		return dialogObject;
	}
	
	protected void setDialogObject(O object){
		this.dialogObject = object;
	}
	
	protected abstract void addComponents() throws Exception;
	
	protected abstract void setTitle(); 
	
	protected JPanel mainPanel(){
		return panelContent;
	}
	
	protected JPanel getButtonPanel(){
		if(panelButtons == null) {
			panelButtons= new JPanel();
			BoxLayout l = new BoxLayout(panelButtons, BoxLayout.PAGE_AXIS);
			panelButtons.setLayout(l);
			JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
			panelButtons.add(separator);
			panelButtons.add(Box.createHorizontalStrut(5));
			JPanel buttons = new JPanel();
			BoxLayout layout = new BoxLayout(buttons, BoxLayout.LINE_AXIS);
			buttons.setLayout(layout);
			switch (buttonLayout) {
			case CENTERED:
				buttons.add(Box.createHorizontalGlue());
				for(JButton button: getButtons()){
					buttons.add(button);
				}
				buttons.add(Box.createHorizontalGlue());
				break;
			case LEFT_RIGHT:
				for(JButton lefthandButton: getLefthandButtons()){
					buttons.add(lefthandButton);
				}
				buttons.add(Box.createHorizontalGlue());
				for(JButton righthandButton: getRighthandButtons()){
					buttons.add(righthandButton);
				}
				break;
			}
			panelButtons.add(buttons);
		}
		return panelButtons;
	}
	
	protected List<JButton> getButtons(){
		List<JButton> buttons = getLefthandButtons();
		buttons.addAll(getRighthandButtons());
		return buttons;
	}
	
	protected List<JButton> getLefthandButtons(){
		return new ArrayList<JButton>();
	}
	
	protected List<JButton> getRighthandButtons(){
		ArrayList<JButton> result = new ArrayList<JButton>();
		result.add(getOKButton());
		if(includeCancelButton)
			result.add(getCancelButton());
		return result;
	}
	
	protected JButton getOKButton(){
		if(btnOK == null){
			btnOK = new JButton(okButtonText);
			btnOK.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					okProcedure();
				}
			});
			btnOK.setActionCommand("OK");
		}
		return btnOK;
	}
	
	protected JButton getCancelButton(){
		if(btnCancel == null){
			btnCancel = new JButton("Cancel");
			btnCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					cancelProcedure();
				}
			});
			btnCancel.setActionCommand("Cancel");
		}
		return btnCancel;
	}
	
	protected JButton getDefaultButton(){
		return btnOK;
	}
	
	protected void okProcedure(){
		dispose();
	}
	
	protected void cancelProcedure(){
		dispose();
	}
	
	protected void closingProcedure(){
		cancelProcedure();
	}
	
	public enum ButtonPanelLayout {
		CENTERED, LEFT_RIGHT;
	}
	
	protected void errorMessage(String title, String message){
		JOptionPane.showMessageDialog(AbstractDialog.this, message, title, JOptionPane.ERROR_MESSAGE);
	}
	
	protected void invalidFieldContentMessage(String message){
		errorMessage("Invalid Parameter", "Invalid field content.\nCause: " + message);
	}
	
	protected void internalExceptionMessage(String message){
		errorMessage("Internal Exception", message);
	}

}
