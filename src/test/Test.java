package test;

import java.util.ArrayList;
import java.util.List;

import de.invation.code.toval.misc.CollectionUtils;




public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
//		Number[] arr = new Number[] {1,4.7,5,8,9};
//		//Double[] arr2 = new Double[] {1.0,-4.7,59.3,8.0,9.0};
//		Double[] arr2 = new Double[] {0.01,-0.07,0.008,0.008,0.7};
//		Double[] arr3 = new Double[] {3.8,0.7,-5.3,6.4,3.9};
//		Object[] obj = new Object[] {1,4.7,5,8,9, "didi"};
//		List<Number> result = Arrays.asList(arr);
//		List<Object> ol = Arrays.asList(obj);
//		Number[] e = ListUtils.asArray(result);
//		List<List<Number>> r = ListUtils.divideList(result, 3);
//		ArrayList<String> t = new ArrayList<String>();
//		DynamicMatrix<String, Integer> dm = new DynamicMatrix<String, Integer>();
//		dm.putValue("Reins", "Czwei", 11);
//		dm.putValue("Rzwei", "Ceins", 11);
//		System.out.println("->"+FormatUtils.formatTrimmed(2.7, 10)+"<-");
//		List<Double> lll = Arrays.asList(-0.5,3.9,7.8);
		//StatList<Number> n = new StatList<Number>(lll);
//		System.out.println(n.max());
//		System.out.println(n.min());
//		System.out.println(n.range());
		//Observation o = new Observation(Arrays.asList(arr2));
		//new AdjustableDiagramPanel(new BarChartPanel<Double, Double>(Arrays.asList(arr2), Arrays.asList(arr3), false)).asFrame();
		//new AdjustableDiagramPanel(new DotPanel<Double>(o.getValueStatistic(), false)).asFrame();
		//ChartFactory.createBarChartPanel(Arrays.asList(arr2), Arrays.asList(arr3)).adjustableVersion().asFrame();
//		DynamicMatrix<String, Double> dm = new DynamicMatrix<String, Double>();
//		dm.putValue("A", "B", 5.7);
//		System.out.println(dm);
//		dm.putValue("B", "C", 4.6);
//		System.out.println(dm);
//		String[] actors =  {"ben","pat","dam","aht","rob","sim","jul"};
//		Integer[] values = {1,2,3,5,6,7,8};
//		Allocation a = new Allocation(actors, values);
//		a.addExclusion("rob", 1);
//		a.addExclusion("jul", 2);
//		a.addExclusion("dam", 3);
//		a.addExclusion("aht", 5);
//		a.addExclusion("pat", 6);
//		a.addExclusion("ben", 7);
//		a.addExclusion("sim", 8);
//		
//		a.setAllocationCount(2);
//		System.out.println(a.getMapping());
		
//		ValueStatCancellable<Double> s = new ValueStatCancellable<Double>(true);
//		s.update(2.0);
//		System.out.println(s);
//		s.update(1.0);
//		System.out.println(s);
//		s.updateCancel(2.0);
//		System.out.println(s);
//		s.updateCancel(1.0);
//		System.out.println(s);
//		s.update(3.0);
//		s.update(1.0);
//		System.out.println(s);
		
//		final char BACKSPACE = (char) 8;
//		HashSet<String> s1 = new HashSet<String>(Arrays.asList("A","B","C","D"));
//		HashSet<String> s2 = new HashSet<String>(Arrays.asList("A","B","C","D"));
//		HashSet<String> s3 = new HashSet<String>(Arrays.asList("A","B","C","D"));
//		System.out.println(CollectionUtils.toSimpleString(s1));
//		System.out.println(SetUtils.containSameElements(new ArrayList<Set<String>>(Arrays.asList(s1,s2,s3))));
		
//		TransitionSystem<Event, State> ts = new TransitionSystem<Event, State>();
//		State s0 = new State("s0");
//		State s1 = new State("s1");
//		State s2 = new State("s2");
//		ts.addState(s0);
//		ts.addState(s1);
//		ts.addState(s2);
//		Event e1 = new Event("E");
//		ts.addEvent(e1);
//		ts.addRelation(s0, s1, e1);
//		System.out.println(ts.isDFN());
//		ts.addRelation(s0, s2, e1);
//		System.out.println(ts);
//		System.out.println(ts.isDFN());
		
		List<Integer> l = new ArrayList<Integer>();
		l.add(1);
		l.add(1);
		System.out.println(CollectionUtils.isTrivial(l));
 	}


}
