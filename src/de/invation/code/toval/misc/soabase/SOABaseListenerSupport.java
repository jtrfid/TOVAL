package de.invation.code.toval.misc.soabase;

import java.util.Arrays;
import java.util.Collection;

import de.invation.code.toval.event.AbstractListenerSupport;

public class SOABaseListenerSupport extends AbstractListenerSupport<SOABaseListener>{

	private static final long serialVersionUID = 5444506073390015628L;

	public void notifyNameChange(String oldName, String newName){
		for(SOABaseListener listener: listeners){
			listener.nameChanged(oldName, newName);
		}
	}
	
	public void notifySubjectAdded(String subject){
		for(SOABaseListener listener: listeners){
			listener.subjectAdded(subject);
		}
	}
	
	public void notifySubjectRemoved(String subject){
		for(SOABaseListener listener: listeners){
			listener.subjectRemoved(subject);
		}
	}
	
	public void notifyObjectAdded(String object){
		for(SOABaseListener listener: listeners){
			listener.objectAdded(object);
		}
	}
	
	public void notifyObjectRemoved(String object){
		for(SOABaseListener listener: listeners){
			listener.objectRemoved(object);
		}
	}
	
	public void notifyActivityAdded(String activity){
		for(SOABaseListener listener: listeners){
			listener.activityAdded(activity);
		}
	}
	
	public void notifyActivityRemoved(String activity){
		for(SOABaseListener listener: listeners){
			listener.activityRemoved(activity);
		}
	}
	
	public SOABaseChangeDecision subjectRemovalAllowed(String... subjects){
		return subjectRemovalAllowed(Arrays.asList(subjects));
	}
	
	public SOABaseChangeDecision subjectRemovalAllowed(Collection<String> subjects){
		for(String subject: subjects){
			for(SOABaseListener listener: listeners){
				if(!listener.allowSubjectRemoval(subject).decision)
					return new SOABaseChangeDecision(listener, subject);
			}
		}
		return new SOABaseChangeDecision();
	}
	
	public SOABaseChangeDecision objectRemovalAllowed(String... objects){
		return objectRemovalAllowed(Arrays.asList(objects));
	}
	
	public SOABaseChangeDecision objectRemovalAllowed(Collection<String> objects){
		for(String object: objects){
			for(SOABaseListener listener: listeners){
				if(!listener.allowObjectRemoval(object).decision)
					return new SOABaseChangeDecision(listener, object);
			}
		}
		return new SOABaseChangeDecision();
	}
	
	public SOABaseChangeDecision activityRemovalAllowed(String... activities){
		return activityRemovalAllowed(Arrays.asList(activities));
	}
	
	public SOABaseChangeDecision activityRemovalAllowed(Collection<String> activities){
		for(String activity: activities){
			for(SOABaseListener listener: listeners){
				if(!listener.allowActivityRemoval(activity).decision)
					return new SOABaseChangeDecision(listener, activity);
			}
		}
		return new SOABaseChangeDecision();
	}

}
