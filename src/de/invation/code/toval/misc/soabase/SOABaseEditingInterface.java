package de.invation.code.toval.misc.soabase;

import java.awt.Window;

public interface SOABaseEditingInterface {
	
    /**
     * Opens a dialog whic hcan be used to edit the SOA-base.
     * @param <S>
     * @param parent
     * @return <code>true</code> when dialog was closed with accept; <code>false</code> if aborted.
     * @throws Exception 
     */
	public <S extends SOABase> boolean showDialog(Window parent) throws Exception;

}
