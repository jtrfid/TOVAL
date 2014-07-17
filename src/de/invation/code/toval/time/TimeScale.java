package de.invation.code.toval.time;


public enum TimeScale {
	
	MILLISECONDS("milliseconds","ms"), 
	SECONDS("seconds","s"), 
	MINUTES("minutes","m"), 
	HOURS("hours","h"), 
	DAYS("days","d"), 
	WEEKS("weeks","w"), 
	MONTHS("months","mth"), 
	YEARS("years","y");
	
	private String descriptor = null;
	private String shortDescriptor = null;
	private boolean shortDescriptorMode = false;
	
	private TimeScale(String descriptor, String shortDescriptor){
		this.descriptor = descriptor;
		this.shortDescriptor = shortDescriptor;
	}
	
	public void activateShortDescriptorMode(){
		shortDescriptorMode = true;
	}
	
	public void activateLongDescriptorMode(){
		shortDescriptorMode = false;
	}
	
	public boolean isShortDesctiptorMode(){
		return shortDescriptorMode;
	}
	
	@Override
	public String toString(){
		if(shortDescriptorMode)
			return shortDescriptor;
		return descriptor;
	}

}
