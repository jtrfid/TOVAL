package time;


public enum TimeScale {
	
	MILLISECONDS("milliseconds"), 
	SECONDS("seconds"), 
	MINUTES("minutes"), 
	HOURS("hours"), 
	DAYS("days"), 
	WEEKS("weeks"), 
	MONTHS("months"), 
	YEARS("years");
	
	private String descriptor = null;
	
	private TimeScale(String descriptor){
		this.descriptor = descriptor;
	}
	
	@Override
	public String toString(){
		return descriptor;
	}

}
