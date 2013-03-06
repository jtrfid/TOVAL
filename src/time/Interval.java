package time;

public class Interval implements Comparable<Interval>{

	private final String toStringFormat = "[%s , %s]";
	private long start;
	private long end;

	public Interval(long startTime, long endTime) {
		this.start = startTime;
		this.end = endTime;
		checkValidity();
	}
	
	private void checkValidity(){
		if(start == end || start > end)
			throw new IllegalArgumentException();
	}
	
	public long getStart(){
		return start;
	}
	
	public void setStart(long startTime){
		this.start = startTime;
		checkValidity();
	}
	
	public long getEnd(){
		return end;
	}
	
	public void setEnd(long endTime){
		this.end = endTime;
		checkValidity();
	}
	
	public long length(){
		return end - start;
	}
	
	public boolean contains(Interval interval){
		return interval.getStart() >= getStart() && interval.getEnd() <= getEnd();
	}

	@Override
	public int compareTo(Interval o) {
		return (int) Math.signum(o.length()-length());
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (end ^ (end >>> 32));
		result = prime * result + (int) (start ^ (start >>> 32));
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
		Interval other = (Interval) obj;
		if (end != other.end)
			return false;
		if (start != other.start)
			return false;
		return true;
	}
	
	@Override
	public Interval clone(){
		return new Interval(getStart(), getEnd());
	}
	
	@Override
	public String toString() {
		return String.format(toStringFormat, start, end);
	}
	
	public static void main(String[] args) {
		new Interval(1L,10L);
	}

}