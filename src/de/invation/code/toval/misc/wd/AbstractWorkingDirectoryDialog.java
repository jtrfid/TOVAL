/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.invation.code.toval.misc.wd;

import de.invation.code.toval.debug.OutputMode;
import de.invation.code.toval.debug.SimpleDebugger;
import de.invation.code.toval.file.FileUtils;
import de.invation.code.toval.graphic.dialog.AbstractDialog;
import de.invation.code.toval.graphic.renderer.AlternatingRowColorListCellRenderer;
import de.invation.code.toval.properties.PropertyException;
import de.invation.code.toval.validate.ExceptionDialog;
import de.invation.code.toval.validate.ExceptionListener;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;

/**
 *
 * @author stocker
 */
public abstract class AbstractWorkingDirectoryDialog<E> extends AbstractDialog<String> implements PropertyChangeListener, ExceptionListener {

    private static final long serialVersionUID = 2306027725394345926L;
    
    public static final Dimension PREFERRED_SIZE = new Dimension(500, 300);

    private final JPanel contentPanel = new JPanel();

    private JList listKnownDirectories;
    private JButton btnOK;
    private JButton btnCancel;
    private DefaultListModel modelListKnownDirectories = new DefaultListModel();

    private NewWorkingDirectoryAction newDirectoryAction;
    private OpenWorkingDirectoryAction openDirectoryAction;

    private List<String> directories = new ArrayList<>();
    private AbstractWorkingDirectoryProperties<E> properties;
    private SimpleDebugger debugger;

    protected AbstractWorkingDirectoryDialog(Window owner, AbstractWorkingDirectoryProperties<E> properties) throws Exception{
        this(owner, properties, null);
    }

    protected AbstractWorkingDirectoryDialog(Window owner, AbstractWorkingDirectoryProperties<E> properties, SimpleDebugger debugger) throws Exception {
        super(owner);
        Validate.notNull(properties);
        this.properties = properties;
        this.debugger = debugger;
    }

    @Override
    protected void setTitle() {
        setTitle("Please choose a " + properties.getWorkingDirectoryDescriptor().toLowerCase());
    }
    
    @Override
    public Dimension getPreferredSize(){
        return PREFERRED_SIZE;
    }

    @Override
    protected void addComponents() throws Exception {
        mainPanel().setLayout(new BorderLayout());
        JScrollPane scrollPane = new JScrollPane(getListKnownDirectories());
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        mainPanel().add(scrollPane, BorderLayout.CENTER);
    }

    @Override
    protected List<JButton> getLefthandButtons() {
        newDirectoryAction = new NewWorkingDirectoryAction(AbstractWorkingDirectoryDialog.this, properties, debugger);
        newDirectoryAction.addPropertyChangeListener(AbstractWorkingDirectoryDialog.this);
        newDirectoryAction.addExceptionListener(AbstractWorkingDirectoryDialog.this);
        openDirectoryAction = new OpenWorkingDirectoryAction(AbstractWorkingDirectoryDialog.this, properties, debugger);
        openDirectoryAction.addPropertyChangeListener(AbstractWorkingDirectoryDialog.this);
        openDirectoryAction.addExceptionListener(AbstractWorkingDirectoryDialog.this);
        List<JButton> buttons = super.getLefthandButtons();
        JButton newDirectoryButton = new JButton(newDirectoryAction);
        buttons.add(newDirectoryButton);
        JButton openDirectoryButton = new JButton(openDirectoryAction);
        buttons.add(openDirectoryButton);
        return buttons;
    }

    @Override
    protected void okProcedure() {
        if (!modelListKnownDirectories.isEmpty()) {
            if (listKnownDirectories.getSelectedValue() == null) {
                JOptionPane.showMessageDialog(AbstractWorkingDirectoryDialog.this, "Please choose a " + properties.getWorkingDirectoryDescriptor().toLowerCase(), "Invalid Parameter", JOptionPane.ERROR_MESSAGE);
                return;
            }
            setDialogObject(directories.get(listKnownDirectories.getSelectedIndex()));
            super.okProcedure();
        } else {
            JOptionPane.showMessageDialog(AbstractWorkingDirectoryDialog.this, "No known entries, please create new " + properties.getWorkingDirectoryDescriptor().toLowerCase(), "Invalid Parameter", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }

    private JList getListKnownDirectories() {
        if (listKnownDirectories == null) {
            listKnownDirectories = new JList(modelListKnownDirectories) {
                private static final long serialVersionUID = -5571466323700430126L;

                @Override
                public String getToolTipText(MouseEvent event) {
                    int index = locationToIndex(event.getPoint());
                    if (index != -1) {
                        return directories.get(index);
                    }
                    return super.getToolTipText(event);
                }

            };
            listKnownDirectories.setCellRenderer(new AlternatingRowColorListCellRenderer());
            listKnownDirectories.setFixedCellHeight(20);
            listKnownDirectories.setVisibleRowCount(10);
            listKnownDirectories.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            listKnownDirectories.setBorder(null);

            updateListKnownDirectories();
        }
        return listKnownDirectories;
    }

    private void updateListKnownDirectories() {
        modelListKnownDirectories.clear();
        try {
            for (String knownDirectory : properties.getKnownWorkingDirectories()) {
                try {
                    modelListKnownDirectories.addElement(FileUtils.getDirName(knownDirectory));
                    directories.add(knownDirectory);
                } catch (ParameterException e) {
                    //Directory no longer available. Do not put into directories list
                }
            }
        } catch (Exception e) {
            ExceptionDialog.showException(AbstractWorkingDirectoryDialog.this, "Working Directory Exception", new Exception("Cannot update list of known " + properties.getWorkingDirectoryDescriptor().toLowerCase(), e), true);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof NewWorkingDirectoryAction || evt.getSource() instanceof OpenWorkingDirectoryAction) {
            if (evt.getPropertyName().equals(AbstractWorkingDirectoryProperties.PROPERTY_NAME_WORKING_DIRECTORY)) {
                setDialogObject(evt.getNewValue().toString());
            } else if (evt.getPropertyName().equals(AbstractWorkingDirectoryAction.PROPERTY_NAME_SUCCESS)) {
                dispose();
            }
        }
    }

    @Override
    public void exceptionOccurred(Object sender, Exception e){
        ExceptionDialog.showException(AbstractWorkingDirectoryDialog.this, "Exception", e, true, true);
    }

}