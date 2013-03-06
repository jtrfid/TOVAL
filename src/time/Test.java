package time;

public class Test implements OverlapListener{
	
	public void startTest(){
//		AbstractIntervalOverlap c = new AbstractIntervalOverlap();
//		c.registerListener(this);
//		c.reportTimeInterval(1L,10L);
//		c.reportTimeInterval(2L,5L);
//		c.reportTimeInterval(2L,5L);
//		c.reportTimeInterval(3L,4L);
//		c.reportTimeInterval(3L,7L);
//		c.reportTimeInterval(4L,7L);
//		c.reportTimeInterval(5L,8L);
//		c.reportTimeInterval(6L,9L);
//		c.reportTimeInterval(10L,12L);
//		c.reportTimeInterval(11L,12L);
//		c.closeTimeIntervalReporting();
	}

	@Override
	public void overlapDetected(OverlapEvent overlapEvent) {
		System.out.println(overlapEvent.getTimeIntervals());
	}
	
	public static void main(String[] args) {
		Test t = new Test();
		t.startTest();
	}

}
