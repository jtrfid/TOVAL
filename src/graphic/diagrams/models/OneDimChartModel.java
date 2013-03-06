package graphic.diagrams.models;

import java.util.List;

public class OneDimChartModel<S extends Number & Comparable<? super S>> extends ScatterChartModel<S,S> {

	public OneDimChartModel(List<S> values) {
		super(values, values, false);
	}
}
