package de.invation.code.toval.misc.soabase;

import java.awt.Window;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.invation.code.toval.graphic.dialog.DialogObject;
import de.invation.code.toval.properties.PropertyException;
import de.invation.code.toval.validate.CompatibilityException;
import de.invation.code.toval.validate.InconsistencyException;
import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.ParameterException.ErrorCode;
import de.invation.code.toval.validate.Validate;

public class SOABase implements Cloneable, DialogObject<SOABase>, SOABaseEditingInterface{
	
	public static final String DEFAULT_NAME = "Context";
	public static final String DEFAULT_SUBJECT_DESCRIPTOR = "Subject;Subjects";
	public static final String DEFAULT_OBJECT_DESCRIPTOR = "Object;Objects";
	public static final String DEFAULT_ACTIVITY_DESCRIPTOR = "Activity;Activities";
	private static final String descriptorFormat = "%s;%s";

	protected String name;
	
	protected Set<String> subjects;
	protected Set<String> objects;
	protected Set<String> activities;
	
	protected String subjectDescriptorSingular;
	protected String subjectDescriptorPlural;
	protected String objectDescriptorSingular;
	protected String objectDescriptorPlural;
	protected String activityDescriptorSingular;
	protected String activityDescriptorPlural;
	
	protected SOABaseListenerSupport contextListenerSupport;
	
	public SOABase(){
		this(DEFAULT_NAME);
	}
	
	public SOABase(String name) {
		initialize();
		setName(name, false);
	}
	
	protected void initialize(){
		subjects = new HashSet<String>();
		objects = new HashSet<String>();
		activities = new HashSet<String>();
		contextListenerSupport = new SOABaseListenerSupport();
		setActivityDescriptor(DEFAULT_ACTIVITY_DESCRIPTOR);
		setSubjectDescriptor(DEFAULT_SUBJECT_DESCRIPTOR);
		setObjectDescriptor(DEFAULT_OBJECT_DESCRIPTOR);
	}
	
	public SOABase(SOABaseProperties properties) throws PropertyException{
		Validate.notNull(properties);
		initialize();
		setName(properties.getName());
		Set<String> subjects = properties.getSubjects();
		if(subjects != null)
			setSubjects(subjects);
		Set<String> objects = properties.getObjects();
		if(objects != null)
			setObjects(objects);
		Set<String> activities = properties.getActivities();
		if(activities != null)
			setActivities(activities);
	}
	
	public boolean addContextListener(SOABaseListener listener){
		return contextListenerSupport.addListener(listener);
	}
	
	public boolean removeContextListener(SOABaseListener listener){
		return contextListenerSupport.removeListener(listener);
	}
	
	public String getSubjectDescriptor(){
		return String.format(descriptorFormat, getSubjectDescriptorSingular(), getSubjectDescriptorPlural());
	}
	
	public String getSubjectDescriptorSingular() {
		return subjectDescriptorSingular;
	}
	
	public String getSubjectDescriptorPlural() {
		return subjectDescriptorPlural;
	}

	public void setSubjectDescriptor(String subjectDescriptor) {
		validateDescriptor(subjectDescriptor);
		this.subjectDescriptorSingular = subjectDescriptor.substring(0, subjectDescriptor.indexOf(';'));
		this.subjectDescriptorPlural = subjectDescriptor.substring(subjectDescriptor.indexOf(';')+1, subjectDescriptor.length());
	}
	
	public String getObjectDescriptor(){
		return String.format(descriptorFormat, getObjectDescriptorSingular(), getObjectDescriptorPlural());
	}

	public String getObjectDescriptorSingular() {
		return objectDescriptorSingular;
	}
	
	public String getObjectDescriptorPlural() {
		return objectDescriptorPlural;
	}

	public void setObjectDescriptor(String objectDescriptor) {
		validateDescriptor(objectDescriptor);
		this.objectDescriptorSingular = objectDescriptor.substring(0, objectDescriptor.indexOf(';'));
		this.objectDescriptorPlural = objectDescriptor.substring(objectDescriptor.indexOf(';')+1, objectDescriptor.length());
	}
	
	private void validateDescriptor(String descriptor){
		if(!descriptor.contains(";"))
			throw new ParameterException("Descriptor must contain two values separated with ';'");
		if(descriptor.indexOf(';') == descriptor.length() -1)
			throw new ParameterException("Descriptor must contain two values separated with ';'");
	}
	
	public String getActivityDescriptor(){
		return String.format(descriptorFormat, getActivityDescriptorSingular(), getActivityDescriptorPlural());
	}

	public String getActivityDescriptorSingular() {
		return activityDescriptorSingular;
	}

	public String getActivityDescriptorPlural() {
		return activityDescriptorPlural;
	}
	
	public void setActivityDescriptor(String activityDescriptor) {
		validateDescriptor(activityDescriptor);
		this.activityDescriptorSingular = activityDescriptor.substring(0, activityDescriptor.indexOf(';'));
		this.activityDescriptorPlural = activityDescriptor.substring(activityDescriptor.indexOf(';')+1, activityDescriptor.length());
	}

	public String getName(){
		return name;
	}
	
	public void setName(String name) {
		setName(name, true);
	}
	
	public void setName(String name, boolean notifyListeners) {
		Validate.notNull(name);
		Validate.notEmpty(name);
		if(this.name != null && this.name.equals(name))
			return;
		String oldName = this.name;
		this.name = name;
		if(notifyListeners)
			contextListenerSupport.notifyNameChange(oldName, name);
	}
	
	public Set<String> getSubjects(){
		return Collections.unmodifiableSet(subjects);
	}
	
	public boolean containsSubjects(){
		return !subjects.isEmpty();
	}
	
	public boolean containsSubject(String subject){
		return subjects.contains(subject);
	}
	
	public void addSubjects(Collection<String> subjects) {
		addSubjects(subjects, true);
	}
	
	public void addSubjects(Collection<String> subjects, boolean notifyListeners) {
		Validate.notNull(subjects);
		if(subjects.isEmpty())
			return;
		for(String subject: subjects){
			addSubject(subject, notifyListeners);
		}
	}
	
	public boolean addSubject(String subject) {
		return addSubject(subject, true);
	}
	
	protected boolean addSubject(String subject, boolean notifyListeners) {
		Validate.notNull(subject);
		if(subjects.add(subject)){
			if(notifyListeners)
				contextListenerSupport.notifySubjectAdded(subject);
			return true;
		}
		return false;
	}
	
	public void setSubjects(Collection<String> subjects) {
		setSubjects(subjects, true);
	}
	
	public void setSubjects(String... subjects) {
		setSubjects(Arrays.asList(subjects));
	}
	
	public void setSubjects(Collection<String> subjects, boolean notifyListeners) {
		Validate.notNull(subjects);
		if(subjects.isEmpty()){
			removeSubjects();
			return;
		}
		Set<String> newSubjects = new HashSet<String>(subjects);
		newSubjects.removeAll(getSubjects());
		Set<String> obsoleteSubjects = new HashSet<String>(getSubjects());
		obsoleteSubjects.removeAll(subjects);
		addSubjects(newSubjects, notifyListeners);
		removeSubjects(obsoleteSubjects, notifyListeners);
	}
	
	public void removeSubjects() {
		removeSubjects(true);
	}
	
	public void removeSubjects(boolean notifyListeners) {
		List<String> subjects = new ArrayList<String>(getSubjects());
		SOABaseChangeDecision changeDecision = contextListenerSupport.subjectRemovalAllowed(getSubjects());
		if(!changeDecision.decision)
			throw new InconsistencyException("Cannot remove all "+getSubjectDescriptorPlural().toLowerCase()+".\n"+changeDecision.vetoObject.getListenerDescription()+" prevented removal of "+getSubjectDescriptorSingular().toLowerCase() + " " +changeDecision.changeObject+" due to inconsistencies.");
		this.subjects.clear();
		if(notifyListeners){
			for(String subject: subjects)
				contextListenerSupport.notifySubjectRemoved(subject);
		}
	}
	
	public void removeSubjects(Collection<String> subjects) {
		removeSubjects(subjects, true);
	}
	
	public void removeSubjects(Collection<String> subjects, boolean notifyListeners) {
		Validate.notNull(subjects);
		if(subjects.isEmpty())
			return;

		SOABaseChangeDecision changeDecision = contextListenerSupport.subjectRemovalAllowed(subjects);
		if(!changeDecision.decision)
			throw new InconsistencyException("Cannot remove all "+getSubjectDescriptorPlural().toLowerCase()+".\n"+changeDecision.vetoObject.getListenerDescription()+" prevented removal of "+getSubjectDescriptorSingular().toLowerCase() + " " +changeDecision.changeObject+" due to inconsistencies.");
		
		for(String subject: subjects){
			removeSubject(subject, notifyListeners);
		}
	}
	
	public boolean removeSubject(String subject) {
		return removeSubject(subject, true);
	}
	
	protected boolean removeSubject(String subject, boolean notifyListeners) {
		SOABaseChangeDecision changeDecision = contextListenerSupport.subjectRemovalAllowed(subject);
		if(!changeDecision.decision)
			throw new InconsistencyException(getSubjectDescriptorSingular() + "\"" + subject + "\" cannot be removed.\n"+changeDecision.vetoObject.getListenerDescription()+" prevented removal due to inconsistencies.");
		if(subjects.remove(subject)){
			if(notifyListeners)
				contextListenerSupport.notifySubjectRemoved(subject);
			return true;
		}
		return false;
	}
	
	public Set<String> getActivities(){
		return Collections.unmodifiableSet(activities);
	}
	
	public boolean containsActivities(){
		return !activities.isEmpty();
	}
	
	public boolean containsActivity(String activity){
		return activities.contains(activity);
	}
	
	public void setActivities(Collection<String> activities) {
		setActivities(activities, true);
	}
	
	public void setActivities(String... activities) {
		setActivities(Arrays.asList(activities));
	}
	
	public void setActivities(Collection<String> activities, boolean notifyListeners) {
		Validate.notNull(activities);
		if(activities.isEmpty()){
			removeActivities();
			return;
		}
		Set<String> newActivities = new HashSet<String>(activities);
		newActivities.removeAll(getActivities());
		Set<String> obsoleteActivities = new HashSet<String>(getActivities());
		obsoleteActivities.removeAll(activities);
		addActivities(newActivities, notifyListeners);
		removeActivities(obsoleteActivities, notifyListeners);
	}
	
	public void addActivities(Collection<String> activities) {
		addActivities(activities, true);
	}
	
	public void addActivities(Collection<String> activities, boolean notifyListeners) {
		Validate.notNull(activities);
		if(activities.isEmpty())
			return;
		for(String activity: activities){
			addActivity(activity, notifyListeners);
		}
	}
	
	public boolean addActivity(String activity) {
		return addActivity(activity, true);
	}
	
	public boolean addActivity(String activity, boolean notifyListeners) {
		Validate.notNull(activity);
		if(activities.add(activity)){
			if(notifyListeners)
				contextListenerSupport.notifyActivityAdded(activity);
			return true;
		}
		return false;
	}
	
	public void removeActivities() {
		removeActivities(true);
	}
	
	public void removeActivities(boolean notifyListeners) {
		List<String> activities = new ArrayList<String>(getActivities());
		SOABaseChangeDecision changeDecision = contextListenerSupport.activityRemovalAllowed(getActivities());
		if(!changeDecision.decision)
			throw new InconsistencyException("Cannot remove all "+ getActivityDescriptorPlural().toLowerCase() +".\n"+changeDecision.vetoObject.getListenerDescription()+" prevented removal of "+getActivityDescriptorSingular().toLowerCase() + " " +changeDecision.changeObject+" due to inconsistencies.");
		
		this.activities.clear();
		if(notifyListeners){
			for(String activity: activities)
				contextListenerSupport.notifyActivityRemoved(activity);
		}
	}
	
	public void removeActivities(Collection<String> activities) {
		removeActivities(activities, true);
	}
	
	public void removeActivities(Collection<String> activities, boolean notifyListeners) {
		Validate.notNull(activities);
		if(activities.isEmpty())
			return;
		
		SOABaseChangeDecision changeDecision = contextListenerSupport.activityRemovalAllowed(activities);
		if(!changeDecision.decision)
			throw new InconsistencyException("Cannot remove all "+ getActivityDescriptorPlural().toLowerCase() +".\n"+changeDecision.vetoObject.getListenerDescription()+" prevented removal of "+getActivityDescriptorSingular().toLowerCase() + " " +changeDecision.changeObject+" due to inconsistencies.");
		
		for(String activity: activities){
			removeActivity(activity, notifyListeners);
		}
	}
	
	public boolean removeActivity(String activity) {
		return removeActivity(activity, true);
	}
	
	protected boolean removeActivity(String activity, boolean notifyListeners) throws InconsistencyException{
		SOABaseChangeDecision changeDecision = contextListenerSupport.activityRemovalAllowed(activity);
		if(!changeDecision.decision)
			throw new InconsistencyException(getActivityDescriptorSingular() + "\"" + activity + "\" cannot be removed.\n"+changeDecision.vetoObject.getListenerDescription()+" prevented removal due to inconsistencies.");
		if(activities.remove(activity)){
			if(notifyListeners)
				contextListenerSupport.notifyActivityRemoved(activity);
			return true;
		}
		return false;
	}
	
	public Set<String> getObjects(){
		return Collections.unmodifiableSet(objects);
	}
	
	public boolean containsObjects(){
		return !objects.isEmpty();
	}
	
	public boolean containsObject(String object){
		return objects.contains(object);
	}
	
	public void setObjects(Collection<String> objects) {
		setObjects(objects, true);
	}
	
	public void setObjects(String... objects) {
		setObjects(Arrays.asList(objects));
	}
	
	public void setObjects(Collection<String> objects, boolean notifyListeners) {
		Validate.notNull(objects);
		if(objects.isEmpty()){
			removeObjects();
			return;
		}
		Set<String> newObjects = new HashSet<String>(objects);
		newObjects.removeAll(getObjects());
		Set<String> obsoleteObjects = new HashSet<String>(getObjects());
		obsoleteObjects.removeAll(objects);
		addObjects(newObjects, notifyListeners);
		removeObjects(obsoleteObjects, notifyListeners);
	}
	
	public void addObjects(Collection<String> objects) {
		addObjects(objects, true);
	}
	
	public void addObjects(Collection<String> objects, boolean notifyListeners) {
		Validate.notNull(objects);
		if(objects.isEmpty())
			return;
		for(String object: objects){
			addObject(object, notifyListeners);
		}
	}
	
	public boolean addObject(String object) {
		return addObject(object, true);
	}
	
	protected boolean addObject(String object, boolean notifyListeners) {
		Validate.notNull(object);
		if(objects.add(object)){
			if(notifyListeners)
				contextListenerSupport.notifyObjectAdded(object);
			return true;
		}
		return false;
	}
	
	public void removeObjects() {
		removeObjects(true);
	}
	
	public void removeObjects(boolean notifyListeners) {
		List<String> objects = new ArrayList<String>(getObjects());
		SOABaseChangeDecision changeDecision = contextListenerSupport.objectRemovalAllowed(getObjects());
		if(!changeDecision.decision)
			throw new InconsistencyException("Cannot remove all "+getObjectDescriptorPlural().toLowerCase()+".\n"+changeDecision.vetoObject.getListenerDescription()+" prevented removal of "+getObjectDescriptorSingular().toLowerCase() + " " +changeDecision.changeObject+" due to inconsistencies.");
		
		this.objects.clear();
		if(notifyListeners){
			for(String object: objects)
				contextListenerSupport.notifyObjectRemoved(object);
		}
	}
	
	public void removeObjects(Collection<String> objects) {
		removeObjects(objects, true);
	}
	
	public void removeObjects(Collection<String> objects, boolean notifyListeners) {
		Validate.notNull(objects);
		if(objects.isEmpty())
			return;
		
		SOABaseChangeDecision changeDecision = contextListenerSupport.objectRemovalAllowed(objects);
		if(!changeDecision.decision)
			throw new InconsistencyException("Cannot remove all "+getObjectDescriptorPlural().toLowerCase()+".\n"+changeDecision.vetoObject.getListenerDescription()+" prevented removal of "+getObjectDescriptorSingular().toLowerCase() + " " +changeDecision.changeObject+" due to inconsistencies.");
		
		for(String object: objects){
			removeObject(object, notifyListeners);
		}
	}
	
	public boolean removeObject(String object) {
		return removeObject(object, true);
	}
	
	protected boolean removeObject(String object, boolean notifyListeners) {
		SOABaseChangeDecision changeDecision = contextListenerSupport.objectRemovalAllowed(object);
		if(!changeDecision.decision)
			throw new InconsistencyException(getObjectDescriptorSingular() + "\"" + object + "\" cannot be removed.\n"+changeDecision.vetoObject.getListenerDescription()+" prevented removal due to inconsistencies.");
		if(objects.remove(object)){
			if(notifyListeners)
				contextListenerSupport.notifyObjectRemoved(object);
			return true;
		}
		return false;
	}
	
	public boolean isEmpty(){
		return activities.isEmpty() && subjects.isEmpty() && objects.isEmpty();
	}
	
	public void validateSubject(String subject) throws CompatibilityException {
		Validate.notNull(subject);
		if(!subjects.contains(subject))
			throw new CompatibilityException("Unknown " + getSubjectDescriptorSingular().toLowerCase() + ": " + subject);
	}
	
	public void validateSubjects(Collection<String> subjects) throws CompatibilityException {
		Validate.notNull(subjects);
		for(String subject: subjects)
			validateSubject(subject);
	}
	
	public void validateActivity(String activity) throws CompatibilityException {
		Validate.notNull(activity);
		if(!activities.contains(activity))
			throw new CompatibilityException("Unknown " + getActivityDescriptor().toLowerCase() + ": " + activity);
	}
	
	public void validateActivities(Collection<String> activities) throws CompatibilityException {
		Validate.notNull(activities);
		for(String activity: this.activities)
			validateActivity(activity);
	}
	
	public void validateObject(String object) throws CompatibilityException {
		Validate.notNull(object);
		if(!objects.contains(object))
			throw new CompatibilityException("Unknown " + getObjectDescriptor().toLowerCase() + ": " + object);
	}
	
	public void validateObjects(Collection<String> objects) throws CompatibilityException {
		Validate.notNull(objects);
		for(String object: objects)
			validateObject(object);
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		builder.append("Context{");
		builder.append('\n');
		addStringContent(builder);
		builder.append('}');
		builder.append('\n');
		return builder.toString();
	}
	
	protected void addStringContent(StringBuilder builder){
		builder.append("      name: ");
		builder.append(getName());
		builder.append('\n');

		if(containsSubjects()){
			builder.append("  " + getSubjectDescriptorPlural().toLowerCase() + ": ");
			builder.append(getSubjects());
			builder.append('\n');
		}
		if(containsObjects()){
			builder.append("   " + getObjectDescriptorPlural().toLowerCase() + ": ");
			builder.append(getObjects());
			builder.append('\n');
		}
		if(containsActivities()){
			builder.append(getActivityDescriptorPlural().toLowerCase() + ": ");
			builder.append(getActivities());
			builder.append('\n');
		}
	}
	
	protected Class<?> getPropertiesClass(){
		return SOABaseProperties.class;
	}
	
	public SOABaseProperties getProperties() throws PropertyException {
		if(!isValid())
			throw new ParameterException(ErrorCode.INCONSISTENCY, "Cannot extract properties in invalid state!");
		
		SOABaseProperties properties = null;
		try{
			properties = (SOABaseProperties) getPropertiesClass().getConstructor().newInstance();
		} catch(Exception e){
			throw new ParameterException(ErrorCode.TYPE, "Cannot create properties instance.\nReason: " + e.getMessage());
		}

		properties.setBaseClass(this.getClass());
		properties.setPropertiesClass(getPropertiesClass());
		properties.setName(getName());
		properties.setSubjects(getSubjects());
		properties.setObjects(getObjects());
		properties.setActivities(getActivities());
		return properties;
	}
	
	public void takeoverValues(SOABase context) throws Exception {
		takeoverValues(context, false);
	}
	
	public void takeoverValues(SOABase context, boolean notifyListeners) throws Exception {
		Validate.notNull(context);
		if(this == context)
			return;
		
		if(notifyListeners){
			setName(context.getName());
			setActivities(context.getActivities());
			setSubjects(context.getSubjects());
			setObjects(context.getObjects());
			setActivityDescriptor(context.getActivityDescriptor());
			setSubjectDescriptor(context.getSubjectDescriptor());
			setObjectDescriptor(context.getObjectDescriptor());
		} else {
			this.name = context.getName();
			
			Set<String> otherActivities = context.getActivities();
			if(otherActivities != null){
				activities.clear();
				activities.addAll(otherActivities);
			}
			
			Set<String> otherSubjects = context.getSubjects();
			if(otherSubjects != null){
				subjects.clear();
				subjects.addAll(otherSubjects);
			}
			
			Set<String> otherAttributes = context.getObjects();
			if(otherAttributes != null){
				objects.clear();
				objects.addAll(otherAttributes);
			}
		}
	}

	
	@Override
	public SOABase clone() {
		SOABase result = null;
		try{
			result = new SOABase(getName());
			result.takeoverValues(this, false);
		} catch(Exception e){
			e.printStackTrace();
			return null;
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((activities == null) ? 0 : activities.hashCode());
		result = prime * result + ((activityDescriptorPlural == null) ? 0 : activityDescriptorPlural.hashCode());
		result = prime * result + ((activityDescriptorSingular == null) ? 0 : activityDescriptorSingular.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((objectDescriptorPlural == null) ? 0 : objectDescriptorPlural.hashCode());
		result = prime * result + ((objectDescriptorSingular == null) ? 0 : objectDescriptorSingular.hashCode());
		result = prime * result + ((objects == null) ? 0 : objects.hashCode());
		result = prime * result + ((subjectDescriptorPlural == null) ? 0 : subjectDescriptorPlural.hashCode());
		result = prime * result + ((subjectDescriptorSingular == null) ? 0 : subjectDescriptorSingular.hashCode());
		result = prime * result + ((subjects == null) ? 0 : subjects.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SOABase other = (SOABase) obj;
		if (activities == null) {
			if (other.activities != null)
				return false;
		} else if (!activities.equals(other.activities))
			return false;
		if (activityDescriptorPlural == null) {
			if (other.activityDescriptorPlural != null)
				return false;
		} else if (!activityDescriptorPlural.equals(other.activityDescriptorPlural))
			return false;
		if (activityDescriptorSingular == null) {
			if (other.activityDescriptorSingular != null)
				return false;
		} else if (!activityDescriptorSingular.equals(other.activityDescriptorSingular))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (objectDescriptorPlural == null) {
			if (other.objectDescriptorPlural != null)
				return false;
		} else if (!objectDescriptorPlural.equals(other.objectDescriptorPlural))
			return false;
		if (objectDescriptorSingular == null) {
			if (other.objectDescriptorSingular != null)
				return false;
		} else if (!objectDescriptorSingular.equals(other.objectDescriptorSingular))
			return false;
		if (objects == null) {
			if (other.objects != null)
				return false;
		} else if (!objects.equals(other.objects))
			return false;
		if (subjectDescriptorPlural == null) {
			if (other.subjectDescriptorPlural != null)
				return false;
		} else if (!subjectDescriptorPlural.equals(other.subjectDescriptorPlural))
			return false;
		if (subjectDescriptorSingular == null) {
			if (other.subjectDescriptorSingular != null)
				return false;
		} else if (!subjectDescriptorSingular.equals(other.subjectDescriptorSingular))
			return false;
		if (subjects == null) {
			if (other.subjects != null)
				return false;
		} else if (!subjects.equals(other.subjects))
			return false;
		return true;
	}
	
	//------- Validity --------------------------------------------------------------------------------------------------
	
	/**
	 * Checks if the context is in a valid state.<br>
	 * A context is valid, if every activity is executable,
	 * i.e. there exists at least one subject that is permitted to execute it.
	 * @return <code>true</code> if the context is valid;<br>
	 * <code>false</code> otherwise.
	 */
	public boolean isValid(){
		return true;
	}

	@Override
	public void showDialog(Window parent) throws Exception {
		SOABaseDialog.showDialog(parent, this);
	}
	
	public static <P extends SOABaseProperties> SOABase createFromFile(File file) throws Exception{
		return createFromProperties(SOABaseProperties.loadPropertiesFromFile(file));
	}
	
	public static <P extends SOABaseProperties> SOABase createFromProperties(P properties) throws Exception{
		Validate.notNull(properties);
		Class<?> baseClass = properties.getBaseClass();
		// Try to get constructor
		Constructor<?> constructor = null;	
		try {
			constructor = baseClass.getConstructor(properties.getClass());
		} catch (SecurityException e) {
			e.printStackTrace();
			throw new Exception("Cannot extract SOABase constructor.\nReason: " + e.getMessage());
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
			throw new Exception("Cannot extract SOABase constructor.\nReason: " + e.getMessage());
		}


		Object newInstance = null;
		try {
			newInstance = constructor.newInstance(properties);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
			throw new Exception("Cannot create SOABase instance.\nReason: " + e.getMessage());
		} catch (InstantiationException e) {
			e.printStackTrace();
			throw new Exception("Cannot create SOABase instance.\nReason: " + e.getMessage());
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new Exception("Cannot create SOABase instance.\nReason: " + e.getMessage());
		} catch (InvocationTargetException e) {
			e.printStackTrace();
			throw new Exception("Cannot create SOABase instance.\nReason: " + e.getMessage());
		}
		return (SOABase) newInstance;
	}
	
	public static SOABase createSOABase(String name, int activities, int subjects, int objects){
		SOABase result = new SOABase(name);
		String activityPrefix = "act";
		String objectPrefix = "obj";
		String subjectPrefix = "subj";
		for(int i=0; i<activities; i++)
			result.addActivity(String.format("%s_%s", activityPrefix, i));
		for(int j=0; j<subjects; j++)
			result.addSubject(String.format("%s_%s", subjectPrefix, j));
		for(int k=0; k<objects; k++)
			result.addObject(String.format("%s_%s", objectPrefix, k));
		return result;
	}

//	public static void main(String[] args) throws Exception {
//		SOABase c = new SOABase("Base");
//		c.setSubjects(new HashSet<String>(Arrays.asList("subj1", "subj2", "subj3")));
//		c.setObjects(new HashSet<String>(Arrays.asList("obj1", "obj2", "obj3")));
//		c.setActivities(new HashSet<String>(Arrays.asList("t1", "t2", "t3")));
//		c.getProperties().store("/Users/stocker/Desktop/Base");
//		
//		SOABaseProperties properties = new SOABaseProperties();
//		properties.load("/Users/stocker/Desktop/Base");
//		SOABase c1 = SOABaseProperties.createFromProperties(properties);
//		System.out.println(c1);
//		System.out.println(c1.equals(c));
//		System.out.println(properties.getBaseClass());
//	}

}
