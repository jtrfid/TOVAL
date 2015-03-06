package de.invation.code.toval.misc.soabase;


public interface SOABaseListener {
	
	public String getListenerDescription();
	
	public void nameChanged(String oldName, String newName);
	
	public void subjectAdded(String subject);
	
	public void subjectRemoved(String subject);
	
	public void objectAdded(String object);
	
	public void objectRemoved(String object);
	
	public void activityAdded(String activities);
	
	public void activityRemoved(String activities);
	
	public SOABaseChangeReply allowSubjectRemoval(String subject);
	
	public SOABaseChangeReply allowObjectRemoval(String object);
	
	public SOABaseChangeReply allowActivityRemoval(String subject);

}
