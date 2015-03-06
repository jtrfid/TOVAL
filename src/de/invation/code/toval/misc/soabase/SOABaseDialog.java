package de.invation.code.toval.misc.soabase;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;

import de.invation.code.toval.graphic.dialog.AbstractDialog;
import de.invation.code.toval.graphic.dialog.DefineGenerateDialog;
import de.invation.code.toval.graphic.dialog.StringDialog;
import de.invation.code.toval.graphic.renderer.AlternatingRowColorListCellRenderer;
import de.invation.code.toval.misc.ArrayUtils;
import de.invation.code.toval.validate.ParameterException;



public class SOABaseDialog extends AbstractDialog {

	private static final long serialVersionUID = 1348084157428660980L;
	public static final Dimension PREFERRED_SIZE = new Dimension(499, 350);
	public static final Dimension PREFERRED_FIELD_SIZE = new Dimension(160, 300);

	private JPanel componentsPanel;
	
	private JList activityList;
	private JList subjectList;
	private JList ObjectList;
	
	private DefaultListModel activityListModel;
	private DefaultListModel subjectListModel;
	private DefaultListModel ObjectListModel;
	
	private JTextField txtContextName;
	
	private JButton btnAddActivities;
	private JButton btnAddSubjects;
	private JButton btnAddObjects;
	private JButton btnShowContext;
	
	private AbstractAction addActivitiesAction;
	private AbstractAction addSubjectsAction;
	private AbstractAction addObjectsAction;
	
	protected boolean activitiesAssigned;
	protected boolean subjectsAssigned;
	protected boolean ObjectsAssigned;
	
	private SOABase originalContext;
	
	public SOABaseDialog(Window owner) throws Exception {
		super(owner);
		initialize();
	}
	
	public SOABaseDialog(Window owner, SOABase context) throws Exception {
		super(owner);
		this.editMode = true;
		this.originalContext = context;
		initialize();
	}
	
	protected void initialize() {
		if(editMode){
			setDialogObject(originalContext.clone());
		} else {
			setDialogObject(new SOABase(SOABase.DEFAULT_NAME));
		}
		activityListModel = new DefaultListModel();
		subjectListModel = new DefaultListModel();
		ObjectListModel = new DefaultListModel();
		addActivitiesAction = new AddActivitiesAction();
		addSubjectsAction = new AddSubjectsAction();
		addObjectsAction = new AddObjectsAction();
		activitiesAssigned = false;
		subjectsAssigned = false;
		ObjectsAssigned = false;
	}
	
	protected SOABase originalContext(){
		return originalContext;
	}
	
	@Override
	protected Border getBorder() {
		return BorderFactory.createEmptyBorder(10, 10, 10, 10);
	}

	@Override
	protected void addComponents() {
		mainPanel().setLayout(new BorderLayout());
		JPanel namePanel = new JPanel(new FlowLayout());
		namePanel.add(new JLabel("Name:"));
		namePanel.add(getContextNameField());
		mainPanel().add(namePanel, BorderLayout.PAGE_START);
		mainPanel().add(getComponentsPanel(), BorderLayout.CENTER);
		Component componentsExtension = getComponentsExtensionPanel();
		if(componentsExtension != null){
			mainPanel().add(componentsExtension, BorderLayout.PAGE_END);
		}
//		contentPane.add(getImportActivitiesButton());
//		contentPane.add(getShowContextButton());
	}
	
	@Override
	protected void prepareEditing(){
		txtContextName.setText(getDialogObject().getName());
		updateActivityList(true);
		updateSubjectList(true);
		updateObjectList(true);
	}
	
	private Component getComponentsPanel() {
		if(componentsPanel == null){
			componentsPanel = new JPanel(new BorderLayout());
			componentsPanel.add(new JSeparator(JSeparator.HORIZONTAL), BorderLayout.PAGE_START);
			
			JPanel gridPanel = new JPanel();
			gridPanel.setLayout(new GridLayout(1, 3, 10, 0));
			
			gridPanel.add(new ActivitiesPanel());
			gridPanel.add(new SubjectsPanel());
			gridPanel.add(new ObjectsPanel());
			
			componentsPanel.add(gridPanel, BorderLayout.CENTER);
		}
		return componentsPanel;
	}
	
	protected Component getComponentsExtensionPanel() {
		return null;
	}

	@Override
	protected SOABase getDialogObject() {
		return (SOABase) super.getDialogObject();
	}
	
	@Override
	protected void setTitle() {
		if(editMode){
			setTitle("Edit Context");
		} else {
			setTitle("New Context");
		}
	}
	
	@Override
	public Dimension getPreferredSize() {
		return PREFERRED_SIZE;
	}
	
	//------- BUTTONS --------------------------------------------------------------------------------------------

	@Override
	protected void okProcedure() {
		if(getDialogObject() == null || getDialogObject().isEmpty()){
			JOptionPane.showMessageDialog(SOABaseDialog.this, "Empty context.", "Invalid Parameter", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if(getDialogObject().getName().isEmpty()){
			JOptionPane.showMessageDialog(SOABaseDialog.this, "Context name cannot be empty.", "Invalid Parameter", JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			getDialogObject().setName(txtContextName.getText());
		} catch (Exception e1) {
			JOptionPane.showMessageDialog(SOABaseDialog.this, "Cannot set context name.\nReason: " + e1.getMessage(), "Invalid Parameter", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if(originalContext != null){
			try{
				originalContext.takeoverValues(getDialogObject(), true);
			}catch(Exception e){
				JOptionPane.showMessageDialog(SOABaseDialog.this, "Cannot store context changes.\nReason: " + e.getMessage(), "Invalid Parameter", JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
		dispose();
	}

	@Override
	protected void cancelProcedure(){	
		setDialogObject(null);
		dispose();
	}
	
	private JButton getAddActivitiesButton(){
		if(btnAddActivities == null){
			btnAddActivities = new JButton();
			btnAddActivities.setAction(addActivitiesAction);
		}
		return btnAddActivities;
	}
	
//	private JButton getImportActivitiesButton(){
//		if(btnImportActivities == null){
//			btnImportActivities = new JButton("Import activities");
//			btnImportActivities.addActionListener(new ActionListener() {
//				public void actionPerformed(ActionEvent e) {
//					if(getDialogObject() != null)
//						JOptionPane.showMessageDialog(ContextDialog.this, "Importing activities will reset all context properties.", "Warning", JOptionPane.WARNING_MESSAGE);
//					PTNet ptNet = null;
//					try {
//						ptNet = PetriNetDialog.showPetriNetDialog(ContextDialog.this);
//					} catch (ParameterException e1) {
//						JOptionPane.showMessageDialog(ContextDialog.this, "<html>Cannot launch Petri net dialog.<br>Reason: " + e1.getMessage() + "</html>", "Internal Exception", JOptionPane.ERROR_MESSAGE);
//					}
//					if(ptNet != null){
//						if(ptNet.getTransitions().isEmpty())
//							JOptionPane.showMessageDialog(ContextDialog.this, "Cannot import activities: Petri net contains no transitions.", "Invalid Argument", JOptionPane.ERROR_MESSAGE);
//						try{
//							newContext(PNUtils.getLabelSetFromTransitions(ptNet.getTransitions(), false));
//						}catch(ParameterException ex){
//							JOptionPane.showMessageDialog(ContextDialog.this, "Cannot extract activity names from Petri net transitions.", "Internal Error", JOptionPane.ERROR_MESSAGE);
//						}
//					}
//				}
//			});
//		}
//		return btnImportActivities;
//	}
	
	@Override
	protected List<JButton> getLefthandButtons() {
		List<JButton> lhb = super.getLefthandButtons();
		lhb.add(getShowContextButton());
		return lhb;
	}
	
	private JButton getShowContextButton(){
		if(btnShowContext == null){
			btnShowContext = new JButton("Show Context");
			btnShowContext.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if(getDialogObject() != null){
						try {
							StringDialog.showDialog(SOABaseDialog.this, "Context: " + getDialogObject().getName(), getDialogObject().toString(), false);
						} catch (Exception e1) {
							JOptionPane.showMessageDialog(SOABaseDialog.this, "Cannot launch StringDialog.\nReason:" + e1.getMessage(), "Error on launching StringDialog", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			});
		}
		return btnShowContext;
	}
	
	private JButton getAddSubjectsButton(){
		if(btnAddSubjects == null){
			btnAddSubjects = new JButton();
			btnAddSubjects.setAction(addSubjectsAction);
		}
		return btnAddSubjects;
	}

	private JButton getAddObjectsButton(){
		if(btnAddObjects == null){
			btnAddObjects = new JButton();
			btnAddObjects.setAction(addObjectsAction);
		}
		return btnAddObjects;
	}
	
	//------- OTHER GUI COMPONENTS ----------------------------------------------------------------------------------------
	
	private JTextField getContextNameField(){
		if(txtContextName == null){
			txtContextName = new JTextField();
			txtContextName.setText(SOABase.DEFAULT_NAME);
			txtContextName.setColumns(10);
		}
		return txtContextName;
	}
	
	private JList getActivityList(){
		if(activityList == null){
			activityList = new JList(activityListModel);
			activityList.setCellRenderer(new AlternatingRowColorListCellRenderer());
			activityList.setFixedCellHeight(20);
			activityList.setVisibleRowCount(10);
			activityList.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			activityList.setBorder(null);
			
			activityList.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent e) {}
				
				@Override
				public void keyReleased(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
						if(activityList.getSelectedValues() != null){
							try {
								getDialogObject().removeActivities(ArrayUtils.toStringList(activityList.getSelectedValues()));
								if((activityListModel.size() - activityList.getSelectedIndices().length == 0)){
									activitiesAssigned = false;
								}
							} catch (Exception e1) {
								JOptionPane.showMessageDialog(SOABaseDialog.this, "Cannot remove activities.\nReason: " + e1.getMessage(), "Internal Error", JOptionPane.ERROR_MESSAGE);
								return;
							}
							updateActivityList(true);
						}
					}
				}
				
				@Override
				public void keyPressed(KeyEvent e) {}
			});
		}
		return activityList;
	}
	
	private JList getSubjectList(){
		if(subjectList == null){
			subjectList = new JList(subjectListModel);
			subjectList.setCellRenderer(new AlternatingRowColorListCellRenderer());
			subjectList.setFixedCellHeight(20);
			subjectList.setVisibleRowCount(10);
			subjectList.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			subjectList.setBorder(null);
			
			subjectList.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent e) {}
				
				@Override
				public void keyReleased(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
						if(subjectList.getSelectedValues() != null){
							try {
								getDialogObject().removeSubjects(ArrayUtils.toStringList(subjectList.getSelectedValues()));
								if((subjectListModel.size() - subjectList.getSelectedIndices().length == 0)){
									subjectsAssigned = false;
								}
							} catch (Exception e1) {
								JOptionPane.showMessageDialog(SOABaseDialog.this, "Cannot remove subjects.\nReason: " + e1.getMessage(), "Internal Error", JOptionPane.ERROR_MESSAGE);
								return;
							}
							updateSubjectList(true);
						}
					}
				}
				
				@Override
				public void keyPressed(KeyEvent e) {}
			});
		}
		return subjectList;
	}
	
	private JList getObjectList(){
		if(ObjectList == null){
			ObjectList = new JList(ObjectListModel);
			ObjectList.setCellRenderer(new AlternatingRowColorListCellRenderer());
			ObjectList.setFixedCellHeight(20);
			ObjectList.setVisibleRowCount(10);
			ObjectList.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			ObjectList.setBorder(null);
			
			ObjectList.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent e) {}
				
				@Override
				public void keyReleased(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
						if(ObjectList.getSelectedValues() != null){
							try {
								getDialogObject().removeObjects(ArrayUtils.toStringList(ObjectList.getSelectedValues()));
								if((ObjectListModel.size() - ObjectList.getSelectedIndices().length == 0)){
									ObjectsAssigned = false;
								}
							} catch (Exception e1) {
								JOptionPane.showMessageDialog(SOABaseDialog.this, "Cannot remove Objects:\nReason: " + e1.getMessage(), "Internal Error", JOptionPane.ERROR_MESSAGE);
								return;
							}
							updateObjectList(true);
						}
					}
				}
				
				@Override
				public void keyPressed(KeyEvent e) {}
			});
		}
		return ObjectList;
	}
	
	
	//------- FUNCTIONALITY ----------------------------------------------------------------------------------------------
	
	private void updateActivityList(boolean setSelection){
		activityListModel.clear();
		if(getDialogObject() != null){
			List<String> activities = new ArrayList<String>(getDialogObject().getActivities());
			Collections.sort(activities);
			for(String activity: activities){
				activityListModel.addElement(activity);
			}
		}
		if(!activityListModel.isEmpty() && setSelection)
			activityList.setSelectedIndex(0);
	}
	
	private void updateSubjectList(boolean setSelection){
		subjectListModel.clear();
		if(getDialogObject() != null){
			List<String> subjects = new ArrayList<String>(getDialogObject().getSubjects());
			Collections.sort(subjects);
			for(String subject: subjects){
				subjectListModel.addElement(subject);
			}
		}
		if(!subjectListModel.isEmpty() && setSelection)
			subjectList.setSelectedIndex(0);
	}
	
	private void updateObjectList(boolean setSelection){
		ObjectListModel.clear();
		if(getDialogObject() != null){
			List<String> objects = new ArrayList<String>(getDialogObject().getObjects());
			Collections.sort(objects);
			for(String Object: objects){
				ObjectListModel.addElement(Object);
			}
		}
		if(!ObjectListModel.isEmpty() && setSelection)
			ObjectList.setSelectedIndex(0);
	}
	
	
	//------- ACTIONS -----------------------------------------------------------------------------------------------------------
	
	private class AddObjectsAction extends AbstractAction {
	
		private static final long serialVersionUID = 7917057947622752928L;

		public AddObjectsAction(){
			super("Add " + getDialogObject().getObjectDescriptorPlural());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			List<String> objects = null;
			try {
				objects = DefineGenerateDialog.showDialog(SOABaseDialog.this, getDialogObject().getObjectDescriptorPlural());
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(SOABaseDialog.this, "<html>Cannot launch value chooser dialog dialog.<br>Reason: " + e2.getMessage() + "</html>", "Internal Exception", JOptionPane.ERROR_MESSAGE);
			}
			if(objects != null){
				try {
					getDialogObject().addObjects(objects);
				} catch (ParameterException e1) {
					JOptionPane.showMessageDialog(SOABaseDialog.this, "Cannot add " + getDialogObject().getObjectDescriptorPlural().toLowerCase() + " to context.", "Inconsistency Exception", JOptionPane.ERROR_MESSAGE);
					return;
				}
				ObjectsAssigned = true;
				updateObjectList(false);
			}
		}
		
	}
	
	private class AddSubjectsAction extends AbstractAction {
		
		private static final long serialVersionUID = -4148251659616210607L;
		
		public AddSubjectsAction(){
			super("Add " + getDialogObject().getSubjectDescriptorPlural());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			List<String> subjects = null;
			try {
				subjects = DefineGenerateDialog.showDialog(SOABaseDialog.this, getDialogObject().getSubjectDescriptorPlural());
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(SOABaseDialog.this, "<html>Cannot launch value chooser dialog dialog.<br>Reason: " + e2.getMessage() + "</html>", "Internal Exception", JOptionPane.ERROR_MESSAGE);
			}
			if(subjects != null){
				try {
					getDialogObject().addSubjects(subjects);
				} catch (ParameterException e1) {
					JOptionPane.showMessageDialog(SOABaseDialog.this, "Cannot add " + getDialogObject().getSubjectDescriptorPlural().toLowerCase() + " to context.", "Inconsistency Exception", JOptionPane.ERROR_MESSAGE);
					return;
				}
				subjectsAssigned = true;
				updateSubjectList(false);
			}
		}
		
	}
	
	private class AddActivitiesAction extends AbstractAction {
		
		private static final long serialVersionUID = 1979108778175746934L;
		
		public AddActivitiesAction(){
			super("Add " + getDialogObject().getActivityDescriptorPlural());
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			List<String> activities = null;
			try {
				activities = DefineGenerateDialog.showDialog(SOABaseDialog.this, getDialogObject().getActivityDescriptorPlural());
			} catch (Exception e2) {
				JOptionPane.showMessageDialog(SOABaseDialog.this, "<html>Cannot launch value chooser dialog dialog.<br>Reason: " + e2.getMessage() + "</html>", "Internal Exception", JOptionPane.ERROR_MESSAGE);
			}
			if(activities != null){
				try {
					getDialogObject().addActivities(activities);
				} catch (ParameterException e1) {
					JOptionPane.showMessageDialog(SOABaseDialog.this, "Cannot add " + getDialogObject().getActivityDescriptorPlural().toLowerCase() + " to context: \n" + e1.getMessage(), "Inconsistency Exception", JOptionPane.ERROR_MESSAGE);
					return;
				}
				activitiesAssigned = true;
				updateActivityList(false);
			}
		}
		
	}
	
	private abstract class ContextContentPanel extends JPanel {
		
		public ContextContentPanel(JList content, String description, JButton button){
			super(new BorderLayout());
			add(new JLabel(description + ":"), BorderLayout.PAGE_START);
			JScrollPane scrollPane1 = new JScrollPane();
			scrollPane1.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
			scrollPane1.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
			add(scrollPane1, BorderLayout.CENTER);
			scrollPane1.setViewportView(content);
			add(button, BorderLayout.PAGE_END);
		}
	}
	
	private class ActivitiesPanel extends ContextContentPanel {
		private static final long serialVersionUID = 4842826076367497019L;

		public ActivitiesPanel() {
			super(getActivityList(), getDialogObject().getActivityDescriptorPlural(), getAddActivitiesButton());
		}
	}
	
	private class SubjectsPanel extends ContextContentPanel {
		private static final long serialVersionUID = -4111381147809133847L;

		public SubjectsPanel() {
			super(getSubjectList(), getDialogObject().getSubjectDescriptorPlural(), getAddSubjectsButton());
		}
	}
	
	private class ObjectsPanel extends ContextContentPanel {
		private static final long serialVersionUID = -2779219101533314867L;

		public ObjectsPanel() {
			super(getObjectList(), getDialogObject().getObjectDescriptorPlural(), getAddObjectsButton());
		}
	}
	
	//------- STARTUP ---------------------------------------------------------------------------------------------------------------
	
	public static SOABase showDialog(Window parentWindow) throws Exception {
		SOABaseDialog contextDialog = new SOABaseDialog(parentWindow);
		contextDialog.setUpGUI();
		return contextDialog.getDialogObject();
	}

	public static void showDialog(Window parentWindow, SOABase context) throws Exception {
		SOABaseDialog contextDialog = new SOABaseDialog(parentWindow, context);
		contextDialog.setUpGUI();
	}

	public static void main(String[] args) throws Exception {
		SOABase c = new SOABase("GerdContext");
		c.setActivities(Arrays.asList("act1","act2"));
		SOABaseDialog.showDialog(null, c);
		System.out.println(c);
	}
}
