package de.invation.code.toval.graphic.diagrams.panels;


import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;

import de.invation.code.toval.graphic.diagrams.models.DotChartModel;
import de.invation.code.toval.graphic.diagrams.models.ChartModel.ValueDimension;
import de.invation.code.toval.graphic.util.GraphicUtils;

@SuppressWarnings("serial")
public class DotChartPanel extends ScatterChartPanel {
	
	private static final int maxPointDiameter = 100;
	
	public DotChartPanel(DotChartModel<?> dotChart) {
		this(dotChart, true);
	}
	
	public DotChartPanel(DotChartModel<?> dotChart, boolean zeroBased) {
		this(dotChart, zeroBased, false);
	}
	
	public DotChartPanel(DotChartModel<?> dotChart, boolean zeroBased, boolean onlyIntegerTicks) {
		super(dotChart, zeroBased, onlyIntegerTicks, true, false);
	}
	
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(getRequiredPoints(ValueDimension.X) + spacing.left + spacing.right, 
							 getPointDiameterFor(diagram.getValues(ValueDimension.Y).max().doubleValue()) + spacing.top + spacing.bottom);
	}
	
	protected int getPointDiameterFor(Number value) {
		return  (int) Math.round((maxPointDiameter/diagram.getValues(ValueDimension.Y).max().doubleValue())*value.doubleValue());
	}
	
	@Override
	protected void paintValues(Graphics g, boolean paintLines) {
		Point valueLocation;
		for (int i = 0; i < getValueCount(); i++) {
			valueLocation = getPointFor(i);
			int diameter = getPointDiameterFor(diagram.getValues(ValueDimension.Y).get(i));
			GraphicUtils.drawCircle(g, valueLocation, diameter);
//			g.drawLine(valueLocation.x, getPaintingRegion().getBottomLeft().y, valueLocation.x, (int) (valueLocation.y+Math.ceil(diameter/2.0)));
		}
	}
	
	@Override
	protected Integer getYFor(Number value) {
		return getPaintingRegion().getCenter().y-spacing.bottom/2;
	}

}
