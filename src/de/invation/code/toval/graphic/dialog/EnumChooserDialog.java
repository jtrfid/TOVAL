package de.invation.code.toval.graphic.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.util.ArrayList;
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


public class EnumChooserDialog<E extends Enum<E>> extends AbstractDialog<List<E>> {
	
	private static final long serialVersionUID = 8280110976725420192L;
	
	public static final Border DEFAULT_BORDER = BorderFactory.createEmptyBorder(5, 5, 5, 5);
	public static final int DEFAULT_SELECTION_MODE = ListSelectionModel.SINGLE_SELECTION;

	private DefaultListModel enumListModel = new DefaultListModel();
	private Class<E> enumeration;
	private int selectionMode;
	private JList enumList;
	
	protected EnumChooserDialog(Window owner, String title, Class<E> enumeration) throws Exception {
		super(owner, title);
		setPossibleValues(enumeration);
	}
	
	protected EnumChooserDialog(Window owner, String title, Class<E> enumeration, int selectionMode) {
		super(owner, title);
		setPossibleValues(enumeration);
		this.selectionMode = selectionMode;
	}
	
	private void setPossibleValues(Class<E> enumeration) throws ParameterException{
		Validate.notNull(enumeration);
		this.enumeration = enumeration;
	}

	@Override
	protected void addComponents() throws Exception {
		mainPanel().setLayout(new BorderLayout());
		
		JScrollPane scrollPane = new JScrollPane(getValueList());
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		mainPanel().add(scrollPane);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void okProcedure() {
		if(!enumListModel.isEmpty()){
			List<E> values = new ArrayList<E>();
			for(Object o: enumList.getSelectedValues())
				values.add((E) o);
			setDialogObject(values);
			super.okProcedure();
		} else {
			JOptionPane.showMessageDialog(EnumChooserDialog.this, "Value list is empty.", "Invalid Parameter", JOptionPane.ERROR_MESSAGE);
		}
	}

	@Override
	protected void setTitle() {}
	
	private JList getValueList(){
		if(enumList == null){
			enumList = new JList(enumListModel);
			enumList.setCellRenderer(new AlternatingRowColorListCellRenderer());
			enumList.setFixedCellHeight(20);
			enumList.setVisibleRowCount(10);
			enumList.setPreferredSize(new Dimension(200,100));
			enumList.getSelectionModel().setSelectionMode(selectionMode);
			enumList.setBorder(null);
			for(E possibleValue: enumeration.getEnumConstants()){
				enumListModel.addElement(possibleValue);
			}
		}
		return enumList;
	}
	
	public static <E extends Enum<E>> List<E> showDialog(Window owner, String title, Class<E> enumeration) throws Exception{
		EnumChooserDialog<E> dialog = new EnumChooserDialog<E>(owner, title, enumeration);
		dialog.setUpGUI();
		return dialog.getDialogObject();
	}
	
	public static <E extends Enum<E>> List<E> showDialog(Window owner, String title, Class<E> enumeration, int selectionMode) throws Exception{
		EnumChooserDialog<E> dialog = new EnumChooserDialog<E>(owner, title, enumeration, selectionMode);
		dialog.setUpGUI();
		return dialog.getDialogObject();
	}
	
}
