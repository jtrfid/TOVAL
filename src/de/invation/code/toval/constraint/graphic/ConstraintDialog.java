package de.invation.code.toval.constraint.graphic;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import de.invation.code.toval.constraint.AbstractConstraint;
import de.invation.code.toval.constraint.ConstraintType;
import de.invation.code.toval.constraint.NumberConstraint;
import de.invation.code.toval.constraint.NumberOperator;
import de.invation.code.toval.constraint.Operator;
import de.invation.code.toval.constraint.OperatorFormats;
import de.invation.code.toval.constraint.StringConstraint;
import de.invation.code.toval.constraint.StringOperator;
import de.invation.code.toval.graphic.dialog.AbstractEditCreateDialog;
import de.invation.code.toval.graphic.util.SpringUtilities;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import javax.swing.SpringLayout;

public class ConstraintDialog extends AbstractEditCreateDialog<AbstractConstraint> {

    private static final long serialVersionUID = -4145097630293671723L;

    private JPanel panelArguments = null;
    private JPanel panelHeader = null;

    private JComboBox comboConstraintType = null;
    private JComboBox<Operator<?>> comboOperator = null;
    private JComboBox<String> comboAttributeName = null;
    private DefaultComboBoxModel<String> comboAttributeNameModel = new DefaultComboBoxModel<>();
    
    private Map<String, JTextField> argumentFields = new HashMap<String, JTextField>();
    private ConstraintType currentConstraintView = ConstraintType.NUMBER_CONSTRAINT;

    public ConstraintDialog(Window owner, AbstractConstraint constraint, Set<String> attributeCandidates) {
        super(owner, constraint);
        setAttributeCandidates(attributeCandidates);
    }

    public ConstraintDialog(Window owner, AbstractConstraint constraint) {
        super(owner, constraint);
        comboAttributeNameModel.addElement(constraint.getElement());
    }

    public ConstraintDialog(Window owner, Set<String> attributeCandidates) {
        super(owner);
        setAttributeCandidates(attributeCandidates);
    }
    
    private void setAttributeCandidates(Set<String> attributeCandidates){
        Validate.notNull(attributeCandidates);
        Validate.notEmpty(attributeCandidates);
        Validate.noNullElements(attributeCandidates);
        for(String attributeCandidate: attributeCandidates){
            Validate.notEmpty(attributeCandidate);
            comboAttributeNameModel.addElement(attributeCandidate);
        }
    }

    @Override
    protected void addComponents() throws Exception {

        mainPanel().setLayout(new BorderLayout());
        mainPanel().add(getPanelHeader(), BorderLayout.PAGE_START);
        mainPanel().add(getPanelArguments(), BorderLayout.CENTER);

        if (editMode()) {
            if (getDialogObject() instanceof NumberConstraint) {
                setView(ConstraintType.NUMBER_CONSTRAINT);
                getComboConstraintType().setSelectedIndex(0);
            } else {
                setView(ConstraintType.STRING_CONSTRAINT);
                getComboConstraintType().setSelectedIndex(1);
            }
        } else {
            setView(ConstraintType.NUMBER_CONSTRAINT);
            getComboConstraintType().setSelectedIndex(0);
        }
    }
    
    private JPanel getPanelHeader(){
        if(panelHeader == null){
            panelHeader = new JPanel();
            panelHeader.setLayout(new SpringLayout());
            panelHeader.add(new JLabel("Type:", JLabel.TRAILING));
            panelHeader.add(getComboConstraintType());
            panelHeader.add(new JLabel("Attribute:", JLabel.TRAILING));
            panelHeader.add(getComboAttributeName());
            panelHeader.add(new JLabel("Operator:", JLabel.TRAILING));
            panelHeader.add(getComboOperator());
            SpringUtilities.makeCompactGrid(panelHeader, 3, 2, 0, 0, 5, 5);
        }
        return panelHeader;
    }
    
    private JPanel getPanelArguments(){
        if(panelArguments == null){
            panelArguments = new JPanel();
            panelArguments.setLayout(new SpringLayout());
        }
        return panelArguments;
    }

    private void updateArgumentPanel() {
        panelArguments.removeAll();
        argumentFields.clear();
        Object[] parameters = null;
        if (getDialogObject() != null) {
            if (getDialogObject() instanceof NumberConstraint) {
                parameters = ((NumberConstraint) getDialogObject()).getParameters();
            } else if (getDialogObject() instanceof StringConstraint) {
                parameters = ((StringConstraint) getDialogObject()).getParameters();
            }
        }
        Operator<?> selectedOperator = getSelectedOperator();
        for (int i = 1; i < selectedOperator.getRequiredArguments(); i++) {
            JLabel argumentLabel = new JLabel(selectedOperator.getArgumentNames()[i] + ": ");
            argumentLabel.setHorizontalAlignment(JLabel.TRAILING);
            panelArguments.add(argumentLabel);
            JTextField argumentField = new JTextField();
            argumentField.setPreferredSize(new Dimension(100, 30));
            if (parameters != null && parameters.length == selectedOperator.getRequiredArguments() - 1) {
                argumentField.setText(parameters[i - 1].toString());
            }
            panelArguments.add(argumentField);
            argumentFields.put(selectedOperator.getArgumentNames()[i], argumentField);
        }
        SpringUtilities.makeCompactGrid(panelArguments, selectedOperator.getRequiredArguments()-1, 2, 0, 0, 5, 5);
        panelArguments.repaint();
        ConstraintDialog.this.pack();
    }

    private JComboBox getComboOperator() {
        if (comboOperator == null) {
            comboOperator = new JComboBox<>();
            comboOperator.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    comboOperator.setToolTipText(OperatorFormats.getDescriptor(getSelectedOperator()));
                    updateArgumentPanel();
                }
            });
        }
        return comboOperator;
    }
    
    private Operator<?> getSelectedOperator() {
        if (currentConstraintView == ConstraintType.NUMBER_CONSTRAINT) {
            return NumberOperator.values()[comboOperator.getSelectedIndex()];
        } else if(currentConstraintView == ConstraintType.STRING_CONSTRAINT){
            return StringOperator.values()[comboOperator.getSelectedIndex()];
        }
        return null;
    }

    private JComboBox<String> getComboConstraintType() {
        if (comboConstraintType == null) {
            comboConstraintType = new JComboBox<>();
            comboConstraintType.setEnabled(!editMode());
            comboConstraintType.addItemListener(new ItemListener() {

                @Override
                public void itemStateChanged(ItemEvent e) {
                    if (comboConstraintType.getSelectedIndex() == 0) {
                        setView(ConstraintType.NUMBER_CONSTRAINT);
                    } else {
                        setView(ConstraintType.STRING_CONSTRAINT);
                    }
                }
            });
            comboConstraintType.setModel(new DefaultComboBoxModel(new String[]{"Number Constraint", "String Constraint"}));
        }
        return comboConstraintType;
    }

    private JComboBox<String> getComboAttributeName() {
        if (comboAttributeName == null) {
            comboAttributeName = new JComboBox<>();
            comboAttributeName.setModel(comboAttributeNameModel);
        }
        return comboAttributeName;
    }

    private void setView(ConstraintType constraintType) {
        this.currentConstraintView = constraintType;
        update();
    }

    private void update() {
        if (currentConstraintView == ConstraintType.NUMBER_CONSTRAINT) {
            comboOperator.setModel(new DefaultComboBoxModel(NumberOperator.values()));
        } else if (currentConstraintView == ConstraintType.STRING_CONSTRAINT) {
            comboOperator.setModel(new DefaultComboBoxModel(StringOperator.values()));
        }
        if(getDialogObject() != null){
            comboOperator.setSelectedItem(getDialogObject().getOperator());
        } else {
            comboOperator.setSelectedIndex(0);
        }
        comboOperator.setToolTipText(OperatorFormats.getDescriptor(getSelectedOperator()));
        updateArgumentPanel();
    }

    @Override
    protected AbstractConstraint<?> newDialogObject(Object... parameters) {
        return null;
    }

    @Override
    protected boolean validateAndSetFieldValues() throws Exception {
        if (getComboAttributeName().getSelectedItem() == null) {
            throw new ParameterException("Missing attribute name");
        }
        String selectedAttribute = getComboAttributeName().getSelectedItem().toString();

        Operator<?> selectedOperator = getSelectedOperator();
        List<String> arguments = new ArrayList<String>(selectedOperator.getRequiredArguments());
        for (String argument : selectedOperator.getArgumentNames()) {
            if (argumentFields.containsKey(argument)) {
                if (argumentFields.get(argument).getText().equals("")) {
                    throw new ParameterException("Missing value for argument \"" + argument + "\"");
                }
                arguments.add(argumentFields.get(argument).getText());
            }
        }
        
        if (currentConstraintView == ConstraintType.NUMBER_CONSTRAINT) {
            Number[] numberArguments = new Number[arguments.size()];
            for (int i = 0; i < arguments.size(); i++) {
                try {
                    numberArguments[i] = Integer.parseInt(arguments.get(i));
                } catch (Exception integerException) {
                    try {
                        numberArguments[i] = Double.parseDouble(arguments.get(i));
                    } catch (Exception doubleException) {
                        throw new ParameterException("Argument \"" + arguments.get(i) + "\" does not seem to be a number.");
                    }
                }
            }
            try {
                if (getDialogObject() == null) {
                    setDialogObject(new NumberConstraint(selectedAttribute, (NumberOperator) selectedOperator, numberArguments));
                } else {
                    ((NumberConstraint) getDialogObject()).setElement(selectedAttribute);
                    ((NumberConstraint) getDialogObject()).setOperator((NumberOperator) selectedOperator);
                    ((NumberConstraint) getDialogObject()).setParameters(numberArguments);
                }
            } catch (Exception e1) {
                throw new ParameterException("Cannot create constraint, please check argument values", e1);
            }
        } else {
            String[] stringArguments = new String[arguments.size()];
            for (int i = 0; i < arguments.size(); i++) {
                stringArguments[i] = arguments.get(i);
            }
            try {
                if (getDialogObject() == null) {
                    setDialogObject(new StringConstraint(selectedAttribute, (StringOperator) selectedOperator, stringArguments));
                } else {
                    ((StringConstraint) getDialogObject()).setElement(selectedAttribute);
                    ((StringConstraint) getDialogObject()).setOperator((StringOperator) selectedOperator);
                    ((StringConstraint) getDialogObject()).setParameters(stringArguments);
                }
            } catch (Exception e1) {
                throw new ParameterException("Cannot create constraint, please check argument values", e1);
            }
        }
        return true;
    }

    @Override
    protected void prepareEditing() throws Exception {
        comboOperator.setSelectedItem(getDialogObject().getOperator());
    }

    @Override
    protected void setTitle() {
        if(editMode()){
            setTitle("Edit Cosntraint");
        } else {
            setTitle("Create Constraint");
        }
    }
    
    public static AbstractConstraint<?> showDialog(Window owner, String attribute) throws Exception{
        return showDialog(owner, new HashSet<>(Arrays.asList(attribute)));
    }
    
    public static AbstractConstraint<?> showDialog(Window owner, Set<String> attributes) throws Exception{
        ConstraintDialog constraintDialog = new ConstraintDialog(owner, attributes);
        constraintDialog.setUpGUI();
        return constraintDialog.getDialogObject();
    }

    public static boolean showDialog(Window owner, AbstractConstraint<?> constraint) throws Exception{
        ConstraintDialog constraintDialog = new ConstraintDialog(owner, constraint);
        constraintDialog.setUpGUI();
        return constraintDialog.getDialogObject() != null;
    }
    
    public static void main(String[] args) throws Exception{
        NumberConstraint test = new NumberConstraint("Gerd", NumberOperator.NOT_EQUAL, 2);
        System.out.println(ConstraintDialog.showDialog(null, test));
    }

}
