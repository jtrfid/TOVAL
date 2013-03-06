package graphic.diagrams.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DotChartModel<S extends Number & Comparable<? super S>> extends ScatterChartModel<S, Integer> {

	public DotChartModel(Map<S, Integer> statistic) {
		this(new ArrayList<S>(statistic.keySet()), new ArrayList<Integer>(statistic.values()));
	}
	
	public DotChartModel(List<S> values, List<Integer> counts) {
		super(values, counts, true);
	}
	
	

}
