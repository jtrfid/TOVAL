package de.invation.code.toval.graphic.dialog;

import java.awt.Window;

import de.invation.code.toval.validate.Validate;

public abstract class AbstractEditCreateDialog<O> extends AbstractDialog<O> {

    private static final long serialVersionUID = 5737860895484822620L;

    private boolean editMode;
    private DialogObject<O> originalObject;

    protected AbstractEditCreateDialog(Window owner, Object... parameters) {
        super(owner);
        this.editMode = false;
        setDialogObject(newDialogObject(parameters));
    }

    protected AbstractEditCreateDialog(Window owner, String title, Object... parameters) {
        super(owner, title);
        this.editMode = false;
        setDialogObject(newDialogObject(parameters));
    }

    protected AbstractEditCreateDialog(Window owner, ButtonPanelLayout buttonLayout, Object... parameters) {
        super(owner, buttonLayout);
        this.editMode = false;
        setDialogObject(newDialogObject(parameters));
    }

    protected AbstractEditCreateDialog(Window owner, DialogObject<O> originalObject) {
        super(owner);
        this.editMode = true;
        Validate.notNull(originalObject);
        this.originalObject = originalObject;
        setDialogObject(originalObject.clone());
    }

    protected AbstractEditCreateDialog(Window owner, String title, DialogObject<O> originalObject) {
        this(owner, originalObject);
        Validate.notNull(title);
        setTitle(title);
    }

    protected boolean editMode() {
        return editMode;
    }

    protected abstract O newDialogObject(Object... parameters);

    /**
     * Validates field values and sets properties of the dialog object according
     * to them.<br>
     * Note: Properties of the original object are not changed.<br>
     * They are set on basis of the dialog object using the method
     * {@link DialogObject#takeoverValues(Object)} afterwards.
     */
    protected abstract void validateAndSetFieldValues() throws Exception;

    @Override
    protected void initializeContent() throws Exception {
        if (editMode) {
            prepareEditing();
        }
    }

    @Override
    protected void okProcedure() {
        try {
            validateAndSetFieldValues();
        } catch (Exception e) {
            invalidFieldContentMessage(e.getMessage());
            return;
        }
        if (editMode) {
            try {
                originalObject.takeoverValues(getDialogObject());
            } catch (Exception e) {
                errorMessage("Invalid Parameter", "Cannot save changes: " + e.getMessage());
            }
        }
        super.okProcedure();
    }

    protected abstract void prepareEditing() throws Exception;

    @Override
    protected void cancelProcedure() {
        if (!editMode) {
            setDialogObject(null);
        }
        super.cancelProcedure();
    }

}
