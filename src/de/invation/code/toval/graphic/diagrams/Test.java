package de.invation.code.toval.graphic.diagrams;

import java.util.Arrays;

import de.invation.code.toval.graphic.diagrams.models.OneDimChartModel;
import de.invation.code.toval.graphic.diagrams.panels.BarChartPanel;


public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		ScatterChartPanel p = new ScatterChartPanel(new ScatterChartModel<Integer,Double>(Arrays.asList(1,2,3,4), Arrays.asList(0.5,0.7,2.5,4.8), true), true, true);
//		BarChartPanel p = new BarChartPanel(new ScatterChartModel<Integer,Double>(Arrays.asList(1,2,3,4), Arrays.asList(0.5,0.7,2.5,4.8), true), true, true);
		BarChartPanel p = new BarChartPanel(new OneDimChartModel<Double>(Arrays.asList(0.5,0.7,2.5,4.8)), true, true, false);
//		AdjustableDiagramPanel ap = new AdjustableDiagramPanel(p);
		p.asFrame();

	}

}
