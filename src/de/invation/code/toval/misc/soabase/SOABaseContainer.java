/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.invation.code.toval.misc.soabase;

import de.invation.code.toval.constraint.AbstractConstraint;
import de.invation.code.toval.debug.SimpleDebugger;
import de.invation.code.toval.file.FileUtils;
import de.invation.code.toval.validate.ParameterException;
import java.io.File;
import java.io.IOException;
import java.util.Set;

/**
 *
 * @author stocker
 */
public class SOABaseContainer extends AbstractSOABaseContainer<SOABase,SOABaseProperties>{
    
    private static final String SOABASE_DESCRIPTOR = "SOABase";

    public SOABaseContainer(String serializationPath) {
        super(serializationPath);
    }

    public SOABaseContainer(String serializationPath, SimpleDebugger debugger) {
        super(serializationPath, debugger);
    }
    
    @Override
    public String getComponentDescriptor() {
        return SOABASE_DESCRIPTOR;
    }

    @Override
    protected SOABaseProperties createNewProperties() {
        return new SOABaseProperties();
    }

    @Override
    protected void loadCustomContent(SOABase soaBase, SOABaseProperties properties) {}

    @Override
    protected SOABase createSOABaseFromProperties(SOABaseProperties properties) throws Exception {
        return new SOABase(properties);
    }
    
}
