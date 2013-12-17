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

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.border.Border;

import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;

public abstract class AbstractDialog extends JDialog {
	
	private static final long serialVersionUID = -5864654213215817665L;
	
	private static final ButtonPanelLayout DEFAULT_BUTTON_LAYOUT = ButtonPanelLayout.LEFT_RIGHT;
	
	private final JPanel panelContent = new JPanel();
	protected JPanel panelButtons = null;
	
	protected JButton btnOK = null;
	protected JButton btnCancel = null;
	protected Object dialogObject;
	protected boolean editMode;
	protected ButtonPanelLayout buttonLayout = DEFAULT_BUTTON_LAYOUT;
	
	public AbstractDialog(Window owner) throws Exception {
		this(owner, new Object[0]);
	}
	
	public AbstractDialog(Window owner, ButtonPanelLayout buttonLayout) throws Exception{
		this(owner, buttonLayout, new Object[0]);
	}
	
	protected AbstractDialog(Window owner, Object[] parameters) throws Exception{
		this(owner, false, parameters);
	}
	
	protected AbstractDialog(Window owner, ButtonPanelLayout buttonLayout, Object[] parameters) throws Exception{
		this(owner, false, buttonLayout, parameters);
	}
	
	protected AbstractDialog(Window owner, boolean editMode, Object[] parameters) throws Exception{
		this(owner, editMode, DEFAULT_BUTTON_LAYOUT, parameters);
	}
	
	protected AbstractDialog(Window owner, Boolean editMode, ButtonPanelLayout buttonLayout, Object[] parameters) throws Exception{
		super(owner);
		Validate.notNull(editMode);
		Validate.notNull(buttonLayout);
		Validate.notNull(parameters);
		this.setResizable(true);
		this.setModal(true);
		this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
		
		this.editMode = editMode;
		
		this.addWindowListener(new WindowListener() {
			
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
		
		initialize(parameters);
		
		setTitle();
		
		mainPanel().setBorder(getBorder());
		
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mainPanel(), BorderLayout.CENTER);
		getContentPane().add(getButtonPanel(), BorderLayout.SOUTH);
		getRootPane().setDefaultButton(getDefaultButton());
		
		addComponents();
		
		if(editMode){
			prepareEditing();
		}

		pack();
		this.setLocationRelativeTo(owner);
		this.setVisible(true);
	}
	
	protected abstract Border getBorder();
	
	@Override
	public Dimension getMinimumSize() {
		return getPreferredSize();
	}
	
	
	protected Object getDialogObject(){
		return dialogObject;
	}
	
	protected void setDialogObject(Object value){
		this.dialogObject = value;
	}
	
	protected void prepareEditing() throws Exception {};
	
	protected void initialize(Object... parameters) throws Exception {};
	
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
		result.add(getCancelButton());
		return result;
	}
	
	protected JButton getOKButton(){
		if(btnOK == null){
			btnOK = new JButton("OK");
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
		if(!editMode){
			setDialogObject(null);
		}
		dispose();
	}
	
	protected void closingProcedure(){
		cancelProcedure();
	}
	
	public enum ButtonPanelLayout {
		CENTERED, LEFT_RIGHT;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	protected void validateParameters(Object[] parameters, Class... parameterTypes) throws ParameterException{
		Validate.notNull(parameters);
		Validate.notNull(parameterTypes);
		Validate.notEmpty(parameters);
		Validate.notEmpty(parameterTypes);
		if(parameters.length < parameterTypes.length)
			throw new ParameterException("Wrong number of parameters. Expected " + parameterTypes.length + " but got " + parameters.length);
		Validate.noNullElements(parameters);
		Validate.noNullElements(parameterTypes);
		for(int i=0; i<parameterTypes.length; i++){
			Validate.type(parameters[i], parameterTypes[i]);
		}
	}

}
