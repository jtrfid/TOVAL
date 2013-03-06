package time;

public class IntervalOverlap extends AbstractIntervalOverlap<Interval>{

	@Override
	protected Interval getNewInterval(long start, long end) {
		return new Interval(start, end);
	}

}
