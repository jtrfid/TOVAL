package de.invation.code.toval.graphic.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;

import de.invation.code.toval.graphic.renderer.AlternatingRowColorListCellRenderer;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;


public class ValueChooserDialog extends AbstractDialog {
	
	private static final long serialVersionUID = 2306027725394345926L;
	
	public static final Border DEFAULT_BORDER = BorderFactory.createEmptyBorder(5, 5, 5, 5);
	public static final int DEFAULT_SELECTION_MODE = ListSelectionModel.SINGLE_SELECTION;

	private DefaultListModel stringListModel;
	private Collection<String> possibleValues;
	private int selectionMode;
	private JList stringList;
	private Border border;
	
	public ValueChooserDialog(Window owner, String title, Collection<String> possibleValues) throws Exception {
		this(owner, title, possibleValues, DEFAULT_SELECTION_MODE);
	}
	
	public ValueChooserDialog(Window owner, String title, Collection<String> possibleValues, int selectionMode) throws Exception {
		this(owner, title, possibleValues, selectionMode, DEFAULT_BORDER);
	}
	
	public ValueChooserDialog(Window owner, String title, Collection<String> possibleValues, Border border) throws Exception {
		this(owner, title, possibleValues, DEFAULT_SELECTION_MODE, border);
	}
	
	public ValueChooserDialog(Window owner, String title, Collection<String> possibleValues, int selectionMode, Border border) throws Exception {
		super(owner, new Object[]{title, possibleValues, selectionMode, border});
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void initialize(Object... parameters) throws Exception {
		Validate.notNull(parameters);
		Validate.notEmpty(parameters);
		if(parameters.length != 4)
			throw new ParameterException("Wrong number of parameters. Expected 4 but got " + parameters.length);

		Validate.noNullElements(parameters);
		Validate.type(parameters[0], String.class);
		setTitle((String) parameters[0]);
		Validate.type(parameters[1], Collection.class);
		Collection<String> possibleValues = null;
		try {
			possibleValues = (Collection<String>) parameters[1];
		}catch(Exception e){
			throw new ParameterException("Wrong parameter type. Expected Collection<String> as second parameter.");
		}
		setPossibleValues(possibleValues);
		
		Validate.type(parameters[2], Integer.class);
		this.selectionMode = (Integer) parameters[2];

		Validate.type(parameters[3], Border.class);
		this.border = (Border) parameters[3];
		
		stringListModel = new DefaultListModel();
	}
	
	private void setPossibleValues(Collection<String> possibleValues) throws ParameterException{
		Validate.notNull(possibleValues);
		Validate.notEmpty(possibleValues);
		Validate.noNullElements(possibleValues);
		
		this.possibleValues = possibleValues;
	}

	@Override
	protected void addComponents() throws Exception {
		mainPanel().setLayout(new BorderLayout());
		
		JScrollPane scrollPane = new JScrollPane(getValueList());
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		mainPanel().add(scrollPane);
	}

	@Override
	protected Border getBorder() {
		return border;
	}

	@Override
	protected void okProcedure() {
		if(!stringListModel.isEmpty()){
			List<String> values = new ArrayList<String>();
			for(Object o: stringList.getSelectedValues())
				values.add((String) o);
			setDialogObject(values);
			super.okProcedure();
		} else {
			JOptionPane.showMessageDialog(ValueChooserDialog.this, "Value list is empty.", "Invalid Parameter", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	protected void setTitle() {}
	
	private JList getValueList(){
		if(stringList == null){
			stringList = new JList(stringListModel);
			stringList.setCellRenderer(new AlternatingRowColorListCellRenderer());
			stringList.setFixedCellHeight(20);
			stringList.setVisibleRowCount(10);
			stringList.setPreferredSize(new Dimension(200,100));
			stringList.getSelectionModel().setSelectionMode(selectionMode);
			stringList.setBorder(null);
			for(String possibleValue: possibleValues){
				stringListModel.addElement(possibleValue);
			}
		}
		return stringList;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getDialogObject(){
		return (List<String>) super.getDialogObject();
	}
	
	public static List<String> showDialog(Window owner, String title, Collection<String> values) throws Exception{
		ValueChooserDialog activityDialog = new ValueChooserDialog(owner, title, values);
		return activityDialog.getDialogObject();
	}
	
	public static List<String> showDialog(Window owner, String title, Collection<String> values, int selectionMode) throws Exception{
		ValueChooserDialog activityDialog = new ValueChooserDialog(owner, title, values, selectionMode);
		return activityDialog.getDialogObject();
	}
	
	public static List<String> showDialog(Window owner, String title, Collection<String> values, Border border) throws Exception{
		ValueChooserDialog activityDialog = new ValueChooserDialog(owner, title, values, border);
		return activityDialog.getDialogObject();
	}
	
	public static List<String> showDialog(Window owner, String title, Collection<String> values, int selectionMode, Border border) throws Exception{
		ValueChooserDialog activityDialog = new ValueChooserDialog(owner, title, values, selectionMode, border);
		return activityDialog.getDialogObject();
	}
	
}
