package de.invation.code.toval.graphic.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.Border;

import de.invation.code.toval.graphic.component.BoxLayoutPanel;
import de.invation.code.toval.graphic.renderer.AlternatingRowColorListCellRenderer;
import de.invation.code.toval.validate.Validate;


public class ValueEditingDialog extends AbstractDialog<Set<String>> {
	
	private static final long serialVersionUID = 2306027725394345926L;
	
	public static final Border DEFAULT_BORDER = BorderFactory.createEmptyBorder(5, 5, 5, 5);
	
	private JList listValues;
	private DefaultListModel listValueModel;
	private JButton btnAdd;
	private JButton btnRemove;

	protected ValueEditingDialog(Window owner, String title) {
		super(owner, title);
	}
	
	protected ValueEditingDialog(Window owner, String title, Collection<String> initialValues) {
		super(owner, title);
		setInitialValues(initialValues);
	}
	
	private void setInitialValues(Collection<String> initialValues){
		Validate.notNull(initialValues);
		Validate.notEmpty(initialValues);
		Validate.noNullElements(initialValues);
		
		listValueModel = new DefaultListModel();
		setDialogObject(new HashSet<String>());
		getDialogObject().addAll(initialValues);
	}
	
	@Override
	protected void setTitle() {}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(221, 282);
	}

	@Override
	protected void addComponents(){
		mainPanel().setLayout(new BorderLayout());
		
		JScrollPane scrollPane = new JScrollPane(getValueList());
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		mainPanel().add(scrollPane, BorderLayout.CENTER);
		
		JPanel buttonPanel = new BoxLayoutPanel();
		buttonPanel.add(getButtonAdd());
		buttonPanel.add(getButtonRemove());
		buttonPanel.add(Box.createHorizontalGlue());
		mainPanel().add(buttonPanel, BorderLayout.PAGE_END);
	}
	
	private JList getValueList(){
		if(listValues == null){
			listValues = new JList(listValueModel);
			listValues.setCellRenderer(new AlternatingRowColorListCellRenderer());
			listValues.setFixedCellHeight(20);
			listValues.setVisibleRowCount(10);
			listValues.getSelectionModel().setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
			listValues.setBorder(null);
			listValues.addKeyListener(new KeyListener() {
				
				@Override
				public void keyTyped(KeyEvent e) {}
				
				@Override
				public void keyReleased(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_DELETE || e.getKeyCode() == KeyEvent.VK_BACK_SPACE){
						removeSelectedItems();
					}
				}
				
				@Override
				public void keyPressed(KeyEvent e) {}
			});
			updateValueList();
		}
		return listValues;
	}
	
	private JButton getButtonAdd(){
		if(btnAdd == null){
			btnAdd = new JButton("Add...");
			btnAdd.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					List<String> newValues = null;
					try {
						newValues = DefineGenerateDialog.showDialog(ValueEditingDialog.this, "Add new values");
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(ValueEditingDialog.this, "<html>Cannot launch define/generate dialog.<br>Reason: " + e1.getMessage() + "</html>", "Internal Exception", JOptionPane.ERROR_MESSAGE);
					}
					if(newValues != null && !newValues.isEmpty()){
						getDialogObject().addAll(newValues);
						updateValueList();
					}
				}
			});
			btnAdd.setActionCommand("Add");
		}
		return btnAdd;
	}
	
	private JButton getButtonRemove(){
		if(btnRemove == null){
			btnRemove = new JButton("Remove");
			btnRemove.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					removeSelectedItems();
				}
			});
			btnRemove.setActionCommand("Remove");
		}
		return btnRemove;
	}
	
	private void removeSelectedItems(){
		if(listValues.getSelectedValues() == null)
			return;
		for(Object selectedObject: listValues.getSelectedValues()){
			getDialogObject().remove(selectedObject.toString());
		}
		updateValueList();
	}
	
	private void updateValueList(){
		listValueModel.clear();
		for(String value: getDialogObject()){
			listValueModel.addElement(value);
		}
	}

	@Override
	protected void okProcedure() {
		super.okProcedure();
	}

	@Override
	protected void closingProcedure() {
		setDialogObject(null);
		super.closingProcedure();
	}

	public static Set<String> showDialog(Window owner, String title) throws Exception{
		ValueEditingDialog editingDialog = new ValueEditingDialog(owner, title);
		editingDialog.setUpGUI();
		return editingDialog.getDialogObject();
	}
	
	public static Set<String> showDialog(Window owner, String title, Collection<String> values) throws Exception{
		ValueEditingDialog editingDialog = new ValueEditingDialog(owner, title, values);
		editingDialog.setUpGUI();
		return editingDialog.getDialogObject();
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(showDialog(null, "test", Arrays.asList("1", "2", "33")));
	}
}
