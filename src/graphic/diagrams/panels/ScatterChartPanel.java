package graphic.diagrams.panels;

import graphic.diagrams.models.ChartModel;
import graphic.diagrams.models.ChartModel.ValueDimension;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

import de.invation.code.toval.graphic.DisplayFrame;
import de.invation.code.toval.graphic.GraphicUtils;



/**
 * Basic panel for 2-dimensional diagrams.<br>
 * Values for different coordinate axes are kept separately.
 * This gives users the chance to define different kinds of diagrams by changing the way
 * values are depicted inside the diagram itself.
 * The standard behavior defined in this class is that the coordinates of depicted points 
 * directly correspond to the given value sets.
 * Diagram panels can basically operate in two different modes: zero-based or not zero-based.
 * In zero-based mode the coordinate axes always start at zero and end near the highest value for the corresponding dimensions.
 * In non-zero mode the axes start near the smallest values.
 * 
 * @author Thomas Stocker
 */
@SuppressWarnings("serial")
public class ScatterChartPanel extends JPanel {
	
	/**
	 * Spaces between the top, bottom, left and right side of the enclosing panel and the painting area of the diagram itself.
	 */
	protected Insets spacing = new Insets(40,40,40,40);
	/**
	 * Default text distance (i.e. between text and coordinate axes).
	 */
	protected int textSpacing = 5;
	/**
	 * Indicates if the diagram is painted in a zero-based way.<br>
	 * If zero-based, the coordinates always start with zero (possibly discarding values lower than zero) 
	 * and end approximately with the highest value for the corresponding dimension.
	 * Otherwise it will start with the lowest value.
	 */
	protected boolean zeroBased = true;
	/**
	 * Indicates what kind of tick values are valid.<br>
	 * The default behavior is to use floating-point values.
	 */
	private boolean onlyIntegerTicks = false;
	/**
	 * Defines the painting region of the diagram (where axes and values are drawed on).<br>
	 * It is calculated out of the available space of the diagram panel and its spacing.
	 */
	private PaintingRegion paintingRegion = new PaintingRegion();
	/**
	 * Tick information (description information) for coordinate axes.<br>
	 * <code>TickInfo</code>-objects encapsulate helpful information about tick values,
	 * such as the first and last tick, tick range and the format used for printing/drawing tick values.
	 */
	protected HashMap<ValueDimension, TickInfo> tickInfo = new HashMap<ValueDimension, TickInfo>(2);
	/**
	 * Indicates for which dimensions axes are painted.
	 */
	private HashMap<ValueDimension, Boolean> paintDimAxis = new HashMap<ValueDimension, Boolean>();
	/**
	 * Indicates if lines are painted between values.
	 */
	private boolean paintLines = false;
	/**
	 * Underlying diagram.
	 */
	protected ChartModel<?,?> diagram;
	
	/**
	 * Generates a new diagram using the given values for coordinate axes X and Y.
	 * @param xValues Values for the first dimension X.
	 * @param yValues Values for the second dimension Y.
	 */
	public ScatterChartPanel(ChartModel<?,?> diagram) {
		this(diagram, true);
	}
	
	/**
	 * Generates a new diagram using the given values for coordinate axes X and Y.
	 * @param xValues Values for the first dimension X.
	 * @param yValues Values for the second dimension Y.
	 * @param zeroBased Operation mode
	 * @see #zeroBased
	 */
	public ScatterChartPanel(ChartModel<?,?> diagram, boolean zeroBased) {
		this(diagram, zeroBased, false);
	}
	
	/**
	 * Generates a new diagram using the given values for coordinate axes X and Y.
	 * @param xValues Values for the first dimension X.
	 * @param yValues Values for the second dimension Y.
	 * @param zeroBased Operation mode
	 * @param onlyIntegerTicks If true only integers are allowed as tick values
	 * @see #zeroBased
	 */
	public ScatterChartPanel(ChartModel<?,?> diagram, boolean zeroBased, boolean onlyIntegerTicks) {
		this(diagram, zeroBased, onlyIntegerTicks, true, true);
	}
	
	/**
	 * Generates a new diagram using the given values for coordinate axes X and Y.
	 * @param xValues Values for the first dimension X.
	 * @param yValues Values for the second dimension Y.
	 * @param zeroBased Operation mode
	 * @param paintDimAxisX Indicates if an axis is painted for dimension X
	 * @param paintDimAxisY Indicates if an axis is painted for dimension Y
	 * @param onlyIntegerTicks If true only integers are allowed as tick values
	 * @see #zeroBased
	 */
	public ScatterChartPanel(ChartModel<?,?> diagram, boolean zeroBased, boolean onlyIntegerTicks, boolean paintDimAxisX, boolean paintDimAxisY) {
		this.diagram = diagram;
		this.zeroBased = zeroBased;
		this.onlyIntegerTicks = onlyIntegerTicks;
		tickInfo.put(ValueDimension.X, new TickInfo(diagram.getValues(ValueDimension.X).min().doubleValue(), diagram.getValues(ValueDimension.X).max().doubleValue(), zeroBased));
		tickInfo.put(ValueDimension.Y, new TickInfo(diagram.getValues(ValueDimension.Y).min().doubleValue(), diagram.getValues(ValueDimension.Y).max().doubleValue(), zeroBased));
		paintDimAxis.put(ValueDimension.X, paintDimAxisX);
		paintDimAxis.put(ValueDimension.Y, paintDimAxisY);
		if(onlyIntegerTicks){
			setTickSpacing(ValueDimension.X, 1, false);
			setTickSpacing(ValueDimension.Y, 1, false);
		}
		setBackground(getBackground());
	}
	
	@Override
	public Color getBackground() {
		return Color.white;
	}
	
	/**
	 * Returns the range of the maintained values for the given dimension.
	 * @param dim Reference dimension for range extraction
	 * @return Range of the maintained values for the given dimension
	 */
	protected double getRange(ValueDimension dim) {
		return zeroBased ? diagram.getValues(dim).max().doubleValue() : diagram.getValues(dim).range();
	}
	
	/**
	 * Returns the minimum value of the maintained values for the given dimension.
	 * @param dim Reference dimension for minimum extraction
	 * @return Minimum of the maintained values for the given dimension
	 */
	protected double getMinValue(ValueDimension dim) {
		return zeroBased ? 0.0 : diagram.getValues(dim).min().doubleValue();
	}
	
	/**
	 * Returns the maximum value of the maintained values for the given dimension.
	 * @param dim Reference dimension for maximum extraction
	 * @return Maximum of the maintained values for the given dimension
	 */
	protected double getMaxValue(ValueDimension key) {
		return diagram.getValues(key).max().doubleValue();
	}

	/**
	 * Returns the painting region of the diagram (where axes and values are drawed on).<br>
	 * It is calculated out of the available space of the diagram panel and its spacing.
	 * @return Region used for drawing axes and values
	 */
	protected PaintingRegion getPaintingRegion() {
		return paintingRegion;
	}
	
	/**
	 * Returns the text distance (i.e. between text and coordinate axes).
	 * @return Distance between text and other diagram-components
	 */
	protected int getTextSpacing() {
		return textSpacing;
	}
	
	/**
	 * Returns the preferred size of this diagram which is calculated on the basis of the 
	 * required space for ticks and the diagram spacing.
	 */
	@Override
	public Dimension getPreferredSize() {
		return new Dimension(getRequiredPoints(ValueDimension.X) + spacing.left + spacing.right, 
						   	 getRequiredPoints(ValueDimension.Y) + spacing.top + spacing.bottom);
	}
	
	/**
	 * Returns tick information for coordinate axes of the given dimension.
	 * @param dim Reference dimension for tick information
	 * @return tick information for the given dimension
	 */
	protected TickInfo getTickInfo(ValueDimension dim) {
		return tickInfo.get(dim);
	}
	
	/**
	 * Sets the tick spacing for the coordinate axis of the given dimension.<br>
	 * {@link minorTickSpacing} sets the minor tick spacing,
	 * major tick spacing is a multiple of minor tick spacing and determined with the help of a multiplicator.
	 * @param dim Reference dimension for calculation
	 * @param minorTickSpacing Minor tick spacing
	 * @see TickInfo#getTickMultiplicator()
	 */
	public void setTickSpacing(ValueDimension dim, double minorTickSpacing, boolean repaint) {
		setTickSpacing(dim, minorTickSpacing, getTickInfo(dim).getTickMultiplicator(), repaint);
	}
	
	/**
	 * Sets the tick spacing for the coordinate axis of the given dimension.<br>
	 * {@link minorTickSpacing} sets the minor tick spacing,
	 * major tick spacing is a multiple of minor tick spacing and determined with the help of {@link multiplicator} 
	 * @param dim Reference dimension for calculation
	 * @param minorTickSpacing Minor tick spacing
	 * @param multiplicator Multiplicator for detrermining the major tick spacing.
	 */
	public void setTickSpacing(ValueDimension dim, double minorTickSpacing, int multiplicator, boolean repaint) {
		double tickSpacing = minorTickSpacing;
		if(onlyIntegerTicks) {
			tickSpacing = (int) tickSpacing;
			if (tickSpacing == 0) tickSpacing = 1;
		}
		getTickInfo(dim).setTickSpacing(tickSpacing, multiplicator);
		if(repaint){
			revalidate();
			repaint();
		}
	}
	
	/**
	 * Sets the operation mode.
	 * In zero-based mode coordinate axes always start at 0 and end near the maximum value of the corresponding dimension.
	 * Otherwise axes start at the minimum value.
	 * @param zeroBased Operation mode
	 */
	public void setZeroBased(boolean zeroBased) {
		if(zeroBased != this.zeroBased) {
			this.zeroBased = zeroBased;
			for(ValueDimension dim: tickInfo.keySet())
				tickInfo.get(dim).setZeroBased(zeroBased);
		}
	}
	
	/**
	 * Sets the painting lines mode.
	 * If set to true, plines between values are painted.
	 * @param paintLines
	 */
	public void setPaintLines(boolean paintLines){
		this.paintLines = paintLines;
	}
	
	/**
	 * Determines the required space (in points) for painting ticks for the coordinate axis of the given dimension.
	 * The basic assumption here is, that the first dimension (X) is drawed horizontally and the second dimension (Y) is drawed vertically.
	 * @param dim Reference dimension for space calculation
	 * @return Required points for painting tick information
	 */
	protected int getRequiredPoints(ValueDimension dim) {
		int requiredPointsPerTick = getGraphics().getFontMetrics().getHeight();
		if(dim == ValueDimension.X) {
			int first = getGraphics().getFontMetrics().stringWidth(String.format(getTickInfo(dim).getFormat(), getTickInfo(dim).getFirstTick()));
			int last = getGraphics().getFontMetrics().stringWidth(String.format(getTickInfo(dim).getFormat(), getTickInfo(dim).getLastTick()));
			requiredPointsPerTick = Math.max(first, last)+2;
		} 
		return requiredPointsPerTick*getTickInfo(dim).getTickNumber();
	}
	
	/**
	 * Overrides the super-method <code>paintComponent</code> to incorporate axes, ticks and values.
	 */
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		getPaintingRegion().update();
		paintAxes(g);
		paintTicks(g);
		paintValues(g, paintLines);
	}
	 
	/**
	 * Paints axes for all maintained dimensions (in this case two).<br>
	 * Has to be overridden by subclasses, if more than two dimensions have to be considered.
	 * @param g Graphics context.
	 */
	protected void paintAxes(Graphics g) {
		Point origin = getPaintingRegion().getBottomLeft();
		if(isAxisPaintedfor(ValueDimension.X)) {
			Point xEnd = getXAxisEnd();
			g.drawLine(origin.x, origin.y, xEnd.x, xEnd.y);
		}
		if(isAxisPaintedfor(ValueDimension.Y)) {
			Point yEnd = getYAxisEnd();
			g.drawLine(origin.x, origin.y, yEnd.x, yEnd.y);
		}
	}
	
	public boolean isAxisPaintedfor(ValueDimension dim) {
		return paintDimAxis.get(dim);
	}
	
	/**
	 * Paints tick information for all maintained dimensions (in this case two)
	 * @param g Graphics context
	 */
	protected void paintTicks(Graphics g) {
		if(isAxisPaintedfor(ValueDimension.X))
			paintTicks(g, ValueDimension.X);
		if(isAxisPaintedfor(ValueDimension.Y))
			paintTicks(g, ValueDimension.Y);
	}
	
	/**
	 * Paints tick information for the coordinate axis of the given dimension.
	 * @param g Graphics context
	 * @param dim Reference dimension for tick painting
	 */
	protected void paintTicks(Graphics g, ValueDimension dim) {
		for(int i=0; i<getTickInfo(dim).getTickNumber(); i++) {
			if(i % getTickInfo(dim).getTickMultiplicator() != 0)
				paintTick(g, dim, getTickInfo(dim).getFirstTick()+i*getTickInfo(dim).getMinorTickSpacing(), getTickInfo(dim).getMinorTickLength());
			else paintTick(g, dim, getTickInfo(dim).getFirstTick()+i*getTickInfo(dim).getMinorTickSpacing(), getTickInfo(dim).getMajorTickLength());
		}
	}
	
	/**
	 * Paints a tick with the specified length and value with respect to the given dimension.
	 * @param g Graphics context
	 * @param dim Reference dimension for tick painting
	 * @param tickValue Value for the tick
	 * @param tickLength Length of the tick
	 */
	protected void paintTick(Graphics g, ValueDimension dim, Number tickValue, int tickLength) {
		String str = String.format(getTickInfo(dim).getFormat(), tickValue.doubleValue());
		int xPosition;
		int yPosition;
		Point descPos;
		switch (dim) {
		case X:
			xPosition = getXFor(tickValue);
			yPosition = getPaintingRegion().getBottomLeft().y;
			g.drawLine(xPosition, yPosition, xPosition, yPosition+tickLength);
			descPos = getPaintingRegion().getDescriptionPos(dim, str, xPosition);
			g.drawString(str, descPos.x, descPos.y+tickLength);
			break;
		case Y:
			xPosition = getPaintingRegion().getBottomLeft().x;
			yPosition = getYFor(tickValue);
			g.drawLine(xPosition, yPosition, xPosition-tickLength, yPosition);
			descPos = getPaintingRegion().getDescriptionPos(dim, str, yPosition);
			g.drawString(str, descPos.x-tickLength, descPos.y+tickLength);
			break;
		}
	}

	/**
	 * Paints diagram-content on the base of the values for different dimensions.
	 * @param g Graphics context
	 */
	protected void paintValues(Graphics g, boolean paintLines) {
		Point valueLocation;
		Point lastLocation = null;
		for (int i = 0; i < getValueCount(); i++) {
			valueLocation = getPointFor(i);
			GraphicUtils.fillCircle(g, valueLocation, getPointDiameter());
			if(paintLines && lastLocation!=null){
				g.drawLine(lastLocation.x, lastLocation.y, valueLocation.x, valueLocation.y);
			}
			lastLocation = valueLocation;
		}
	}
	
	/**
	 * Returns the point diameter used for painting single points on the diagram surface.
	 * @return Point diameter for painted values in form of points
	 */
	protected int getPointDiameter() {
		return  5;
	}
	
	/**
	 * Returns the number of values maintained for every dimension.<br>
	 * This number is equal for each dimension.
	 * @return
	 */
	protected int getValueCount() {
		return diagram.getValueCount(ValueDimension.X);
	}
	
	/**
	 * Returns the end-point of the X-axis (axis for the first dimension).
	 * Starting-point is always the painting regions' bottom left edge.<br>
	 * Default is the bottom right edge of the painting region. This method can be overridden
	 * to introduce additional spacing.
	 * @return End-point used for painting the X-axis
	 */
	protected Point getXAxisEnd() {
		return getPaintingRegion().getBottomRight();
	}
	
	/**
	 * Returns the end-point of the Y-axis (axis for the second dimension).
	 * Starting-point is always the painting regions' bottom left edge.<br>
	 * Default is the top left edge of the painting region. This method can be overridden
	 * to introduce additional spacing.
	 * @return End-point used for painting the X-axis
	 */
	protected Point getYAxisEnd() {
		return getPaintingRegion().getTopLeft();
	}
	
	/**
	 * Determines the point coordinates for a given value-vector on the base of the maintained dimensions.<br>
	 * Given two dimensions X and Y with values x1,...,xn and y1,...,yn this method just returns a point 
	 * P(xi,yi) which matches the desired behavior of a standard 2-dimensional diagrams such as graph-plots.<br>
	 * For changing the way these points are determined it is recommended to override the methods {@link #getXFor(Number)} and {@link #getXFor(Number)} rather than this method,
	 * since it simply combines the values of these two methods.
	 * @param index Index for values within the maintained value lists for different dimensions
	 * @return Point determined on the basis of maintained values
	 * @see #getXFor(Number)
	 * @see #getYFor(Number)
	 */
	protected Point getPointFor(int index) {
		return new Point(getXFor(diagram.getValue(ValueDimension.X, index)), getYFor(diagram.getValue(ValueDimension.Y, index)));
	}
	
	/**
	 * Returns the X-coordinate for a given value of the first dimension (X),
	 * considering the actual operation-mode {@link #zeroBased}.<br>
	 * This implementation simply returns a value that reflects the position of the given value with respect to the coordinate axis X.
	 * @param value Value of the maintained X-dimension
	 * @return X-coordinate for the given value
	 */
	protected Integer getXFor(Number value) {
		double offset = zeroBased ? 0.0 : getTickInfo(ValueDimension.X).getFirstTick();
		return getPaintingRegion().getBottomLeft().x + (int) Math.round((value.doubleValue()-offset)*getPaintingRegion().getUnit(ValueDimension.X));
	}
	
	/**
	 * Returns the Y-coordinate for a given value of the second dimension (Y),
	 * considering the actual operation-mode {@link #zeroBased}.<br>
	 * This implementation simply returns a value that reflects the position of the given value with respect to the coordinate axis Y.
	 * @param value Value of the maintained Y-dimension
	 * @return Y-coordinate for the given value
	 */
	protected Integer getYFor(Number value) {
		double offset = zeroBased ? 0.0 : getTickInfo(ValueDimension.Y).getFirstTick();
		return getPaintingRegion().getBottomLeft().y - (int) Math.round((value.doubleValue()-offset)*getPaintingRegion().getUnit(ValueDimension.Y));
	}
	
	/**
	 * Displays the diagram as content of a newly generated JFrame-instance.
	 */
	public void asFrame() {
		new DisplayFrame(this, false);
	}
	
	/**
	 * Returns an adjustable version of this chart.
	 * @return An adjustable version of this chart
	 * @see AdjustableDiagramPanel
	 */
	public AdjustableDiagramPanel adjustableVersion() {
		return new AdjustableDiagramPanel(this);
	}
	
	/**
	 * Class for managing properties of the painting region of a diagram panel.
	 * @author Thomas Stocker
	 */
	protected class PaintingRegion {
		/**
		 * Top left corner of the painting region.
		 */
		private Point topLeft;
		/**
		 * Top right corner of the painting region.
		 */
		private Point topRight;
		/**
		 * Bottom left corner of the painting region.
		 */
		private Point bottomLeft;
		/**
		 * Bottom right corner of the painting region.
		 */
		private Point bottomRight;
		/**
		 * Center of the painting region.
		 */
		private Point center;
		/**
		 * Width of the painting region.
		 */
		private int width;
		/**
		 * Height of the painting region.
		 */
		private int height;
		
		/**
		 * Keeps measures depending on the actual size of the painting area.<br>
		 * <table>
		 * <tr><td align="right">Index 0:</td><td>unit</td></tr>
		 * <tr><td></td><td>Points(pixels) per value-unit</td></tr> 
		 * <tr><td align="right">Index 1:</td><td>valueDistance</td></tr>
		 * <tr><td></td><td>Points(pixels) per value-entry</td></tr> 
		 * <tr><td align="right">Index 2:</td><td>text position (for X a Y-value and vice versa)</td></tr>
		 * </table>
		 * Index 1: 
		 * 			Points(pixels) per value-entry
		 * Index 2: text position (for X a Y-value and vice versa)
		 */
		private HashMap<ValueDimension, ArrayList<Double>> measures = new HashMap<ValueDimension, ArrayList<Double>>(2);
		
		/**
		 * Creates a new PaintingRegion.
		 */
		public PaintingRegion() {
			measures.put(ValueDimension.X, new ArrayList<Double>(3));
			measures.put(ValueDimension.Y, new ArrayList<Double>(3));
		}
		
		/**
		 * Updates the stored measures based on the actual size of the diagram.<br>
		 * Because measures depend on diagram dimensions they have to be recalculated
		 * on resize events.
		 */
		public void update() {
			width = getWidth()-(spacing.right+spacing.left);
			height = getHeight()-(spacing.top+spacing.bottom);
			topLeft = new Point(spacing.left, spacing.top);
			topRight = new Point(topLeft.x+width, topLeft.y);
			bottomLeft = new Point(topLeft.x, topLeft.y+height);
			bottomRight = new Point(topRight.x, bottomLeft.y);
			center = new Point(topLeft.x+(int)(width/2.0), topLeft.y+(int)(height/2.0));
			measures.get(ValueDimension.X).clear();
			measures.get(ValueDimension.Y).clear();
			measures.get(ValueDimension.X).add(width/getTickInfo(ValueDimension.X).getTickRange());
			measures.get(ValueDimension.Y).add(height/getTickInfo(ValueDimension.Y).getTickRange());
			measures.get(ValueDimension.X).add(width/(getValueCount() - 1.0));
			measures.get(ValueDimension.Y).add(height/(getValueCount() - 1.0));
			measures.get(ValueDimension.X).add(bottomLeft.y + getTextSpacing() + getGraphics().getFontMetrics().getHeight() + 0.0);
			measures.get(ValueDimension.Y).add(bottomLeft.x - getTextSpacing() + 0.0);
		}
		
		/**
		 * Returns the top left corner of the painting region.
		 * @return The top left corner of the painting region
		 */
		public Point getTopLeft() {
			return topLeft;
		}
		
		/**
		 * Returns the top right corner of the painting region.
		 * @return The top right corner of the painting region
		 */
		public Point getTopRight() {
			return topRight;
		}
		
		/**
		 * Returns the bottom left corner of the painting region.
		 * @return The bottom left corner of the painting region
		 */
		public Point getBottomLeft() {
			return bottomLeft;
		}
		
		/**
		 * Returns the bottom right corner of the painting region.
		 * @return The bottom right corner of the painting region
		 */
		public Point getBottomRight() {
			return bottomRight;
		}
		
		/**
		 * Returns the center of the painting region.
		 * @return The center of the painting region
		 */
		public Point getCenter() {
			return center;
		}
		
		/**
		 * Returns the unit-measure (i.e. the points/pixels per value/unit) for the given dimension.<br>
		 * The unit specifies the per-value space of a dimension in pixels and is based on the value range.<br>
		 * If there is a value range of 10.2 and there are 200 pixels available for this dimension,
		 * the unit would be 200/10.2
		 * @param dim Reference dimension for calculation
		 * @return The per/value unit in pixels for the given dimension
		 */
		public double getUnit(ValueDimension dim) {
			return measures.get(dim).get(0);
		}
		
		/**
		 * Returns the value distance in pixels for the given dimension.<br>
		 * This measure describes how much space in pixels lies between one value and another
		 * if they are arranged with equal distance on a line with a total length of the
		 * available space for a dimension.<br> 
		 * If there are 200 pixels available for the given dimension and there are 20 values to display,
		 * the value distance is 200/(20-1).
		 * @param dim Reference dimension for calculation
		 * @return Value distance for the given dimension
		 */
		public int getValueDistance(ValueDimension dim) {
			return (int) Math.round(measures.get(dim).get(1));
		}
		
		/**
		 * Returns the Position of the given description string according to the given dimension and reference point.<br>
		 * If i.e. there is a value for the X-coordinate at position x0, that has a description (the value itself as string or another textual description),
		 * this method returns the x-Position where the description "desc" can be drawed.
		 * In this implementation it places the descriptions centered relating to the given reference point.
		 * @param dim Reference dimension for calculation
		 * @param str String that has to be drawn
		 * @param ref Reference position (dimension dependent)
		 * @return The position of {@link str} according to the given dimension {@link dim}.
		 */
		public Point getDescriptionPos(ValueDimension dim, String str, int ref) {
			switch(dim) {
				case X: return new Point(ref-getGraphics().getFontMetrics().stringWidth(str)/2, (int) Math.round(measures.get(dim).get(2)));
				case Y: return new Point((int) Math.round(measures.get(dim).get(2)-getGraphics().getFontMetrics().stringWidth(str)), ref-getGraphics().getFontMetrics().getHeight()/2+8);
				default: return null;
			}
		}
		
	}

}
