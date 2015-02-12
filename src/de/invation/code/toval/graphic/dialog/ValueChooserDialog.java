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

	private DefaultListModel stringListModel = new DefaultListModel();
	private Collection<String> possibleValues;
	private int selectionMode;
	private JList stringList;
	
	protected ValueChooserDialog(Window owner, String title, Collection<String> possibleValues) throws Exception {
		super(owner, title);
		setPossibleValues(possibleValues);
	}
	
	protected ValueChooserDialog(Window owner, String title, Collection<String> possibleValues, int selectionMode) {
		super(owner, title);
		setPossibleValues(possibleValues);
		this.selectionMode = selectionMode;
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
		ValueChooserDialog dialog = new ValueChooserDialog(owner, title, values);
		dialog.setUpGUI();
		return dialog.getDialogObject();
	}
	
	public static List<String> showDialog(Window owner, String title, Collection<String> values, int selectionMode) throws Exception{
		ValueChooserDialog dialog = new ValueChooserDialog(owner, title, values, selectionMode);
		dialog.setUpGUI();
		return dialog.getDialogObject();
	}
	
}
