package de.invation.code.toval.graphic.diagrams.panels;


import java.awt.Graphics;
import java.awt.Point;

import de.invation.code.toval.graphic.diagrams.models.ChartModel;

@SuppressWarnings("serial")
public class BarChartPanel extends ScatterChartPanel {

	private int barWidthBase = 4;
	private int barWidth = barWidthBase * 2 + 1;

	public BarChartPanel(ChartModel<?,?> diagram) {
		super(diagram, true);
	}
	
	public BarChartPanel(ChartModel<?,?> diagram, boolean zeroBased) {
		super(diagram, zeroBased);
	}
	
	public BarChartPanel(ChartModel<?,?> diagram, boolean zeroBased, boolean onlyIntegerTicksX, boolean onlyIntegerTicksY) {
		super(diagram, zeroBased, onlyIntegerTicksX, onlyIntegerTicksY);
	}

	@Override
	protected void paintValues(Graphics g, boolean paintLines) {
		Point valueLocation;
		for (int i = 0; i < getValueCount(); i++) {
			valueLocation = getPointFor(i);
			g.fillRect(valueLocation.x-barWidthBase, valueLocation.y, barWidth, getPaintingRegion().getBottomLeft().y-valueLocation.y);
		}
	}

}
