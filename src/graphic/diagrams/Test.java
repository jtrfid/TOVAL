package graphic.diagrams;
import graphic.diagrams.models.OneDimChartModel;
import graphic.diagrams.panels.BarChartPanel;

import java.util.Arrays;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		ScatterChartPanel p = new ScatterChartPanel(new ScatterChartModel<Integer,Double>(Arrays.asList(1,2,3,4), Arrays.asList(0.5,0.7,2.5,4.8), true), true, true);
//		BarChartPanel p = new BarChartPanel(new ScatterChartModel<Integer,Double>(Arrays.asList(1,2,3,4), Arrays.asList(0.5,0.7,2.5,4.8), true), true, true);
		BarChartPanel p = new BarChartPanel(new OneDimChartModel<Double>(Arrays.asList(0.5,0.7,2.5,4.8)), true, true);
//		AdjustableDiagramPanel ap = new AdjustableDiagramPanel(p);
		p.asFrame();

	}

}
