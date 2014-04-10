package de.invation.code.toval.graphic.diagrams.panels;



import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.SpringLayout;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import de.invation.code.toval.graphic.component.DisplayFrame;
import de.invation.code.toval.graphic.diagrams.models.ChartModel.ValueDimension;
import de.invation.code.toval.graphic.util.SpringUtilities;


@SuppressWarnings("serial")
public class AdjustableDiagramPanel extends JPanel {
	
	private ScatterChartPanel chart;

	private int spacingUnitsFine = 100;
	private int spacingUnitsRaw = 100;
	private double spacingScaleRaw = 1000;
	private double spacingScaleFine = spacingUnitsFine;
	private final int sliderSpace = 30;
	private HorizontalSliderPanel sliderPanelH;
	private VerticalSliderPanel sliderPanelV;
	private HashMap<ValueDimension, Double> basicTickSpacing = new HashMap<ValueDimension, Double>();
	private JPanel diagramPanel = new JPanel(new BorderLayout(0,0));
	private ValueDimension lastDimension;
	private int lastTickValueRaw;
	private int lastTickValueFine;
	private JLabel reset;
	
	public AdjustableDiagramPanel(ScatterChartPanel _chart) {
		setContent();
		setChart(_chart, false, false);
	}
	
	public void setChart(ScatterChartPanel _chart, boolean repaint, boolean takeOverTickSpacing){
		if(takeOverTickSpacing && chart != null){
			_chart.setTickSpacing(ValueDimension.X, chart.getTickInfo(ValueDimension.X).getMinorTickLength(), false);
			_chart.setTickSpacing(ValueDimension.Y, chart.getTickInfo(ValueDimension.Y).getMinorTickLength(), false);
		}
		
		chart = _chart;
		diagramPanel.removeAll();
		diagramPanel.add(new JScrollPane(chart, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
		basicTickSpacing.put(ValueDimension.X, chart.getTickInfo(ValueDimension.X).getMinorTickSpacing());
		basicTickSpacing.put(ValueDimension.Y, chart.getTickInfo(ValueDimension.Y).getMinorTickSpacing());
		setSliderPanelVisibility();
		if(repaint){
			revalidate();
			repaint();
		}
		if(lastDimension!=null)
			updateTickSpacing(lastDimension, lastTickValueRaw, lastTickValueFine);
	}
	
	private void setSliderPanelVisibility(){
		sliderPanelH.setVisible(chart.isAxisPaintedfor(ValueDimension.X));
		sliderPanelV.setVisible(chart.isAxisPaintedfor(ValueDimension.Y));
	}
	
	private void setContent() {
		SpringLayout sl = new SpringLayout();
		setLayout(sl);
		add(diagramPanel);
	
		sliderPanelH = new HorizontalSliderPanel();
		add(sliderPanelH);
		sliderPanelV = new VerticalSliderPanel();
		add(sliderPanelV);
		reset = new JLabel();
		reset.setHorizontalAlignment(JLabel.CENTER);
		reset.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				sliderPanelH.reset();
				sliderPanelV.reset();
			}
		});
		reset.setText("R");
		add(reset);
		
		sl.putConstraint(SpringLayout.WEST, diagramPanel, sliderSpace, SpringLayout.WEST, this);
		sl.putConstraint(SpringLayout.EAST, diagramPanel, 0, SpringLayout.EAST, this);
		sl.putConstraint(SpringLayout.NORTH, diagramPanel, 0, SpringLayout.NORTH, this);
		sl.putConstraint(SpringLayout.SOUTH, diagramPanel, -sliderSpace, SpringLayout.SOUTH, this);
		sl.putConstraint(SpringLayout.WEST, sliderPanelH, sliderSpace, SpringLayout.WEST, this);
		sl.putConstraint(SpringLayout.EAST, sliderPanelH, 0, SpringLayout.EAST, this);
		sl.putConstraint(SpringLayout.NORTH, sliderPanelH, 0, SpringLayout.SOUTH, diagramPanel);
		sl.putConstraint(SpringLayout.SOUTH, sliderPanelH, 0, SpringLayout.SOUTH, this);
		sl.putConstraint(SpringLayout.WEST, sliderPanelV, 0, SpringLayout.WEST, this);
		sl.putConstraint(SpringLayout.EAST, sliderPanelV, 0, SpringLayout.WEST, diagramPanel);
		sl.putConstraint(SpringLayout.NORTH, sliderPanelV, 0, SpringLayout.NORTH, this);
		sl.putConstraint(SpringLayout.SOUTH, sliderPanelV, -sliderSpace, SpringLayout.SOUTH, this);
		sl.putConstraint(SpringLayout.WEST, reset, 0, SpringLayout.WEST, this);
		sl.putConstraint(SpringLayout.EAST, reset, 0, SpringLayout.WEST, diagramPanel);
		sl.putConstraint(SpringLayout.NORTH, reset, 0, SpringLayout.SOUTH, diagramPanel);
		sl.putConstraint(SpringLayout.SOUTH, reset, 0, SpringLayout.SOUTH, this);
	}
	
	private void updateTickSpacing(ValueDimension dim, int tickValueRaw, int tickValueFine) {
		lastDimension = dim;
		lastTickValueRaw = tickValueRaw;
		lastTickValueFine = tickValueFine;
		double unit,spacing;
		if(tickValueRaw>0) {
			unit = basicTickSpacing.get(dim)-basicTickSpacing.get(dim)/spacingScaleRaw;
			spacing = basicTickSpacing.get(dim) - tickValueRaw* (double) unit/spacingUnitsRaw;
		} else if(tickValueRaw<0) {
			unit = basicTickSpacing.get(dim)*spacingScaleRaw-basicTickSpacing.get(dim);
			spacing = basicTickSpacing.get(dim) - tickValueRaw* (double) unit/spacingUnitsRaw;
		} else {
			spacing = basicTickSpacing.get(dim);
		}
		unit = spacing-spacing/spacingScaleFine;
		spacing = spacing -tickValueFine* (double) unit/spacingUnitsFine;
		chart.setTickSpacing(dim, spacing, true);
	}
	
	public void asFrame() {
		new DisplayFrame(this, true);
	}
	
	private abstract class SliderPanel extends JPanel{
		
		protected JSlider tickSliderRaw;
		protected JSlider tickSliderFine;
		protected JLabel rawLabel;
		protected JLabel fineLabel;
		protected ValueDimension valueDimension;
		protected int orientation;
		
		public SliderPanel(ValueDimension dim) {
			super();
			SpringLayout sl = new SpringLayout();
			setLayout(sl);
			valueDimension = dim;
			orientation = dim==ValueDimension.X ? JSlider.HORIZONTAL : JSlider.VERTICAL;
			tickSliderRaw = new JSlider(orientation, -spacingUnitsRaw, spacingUnitsRaw, 0);
			tickSliderRaw.setPaintLabels(true);
			tickSliderRaw.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					updateTickSpacing(valueDimension, tickSliderRaw.getValue(), tickSliderFine.getValue());
				}
			});
			rawLabel = new JLabel("raw", JLabel.CENTER);
			tickSliderFine = new JSlider(orientation, 0, spacingUnitsFine, 0);
			tickSliderFine.setPaintLabels(true);
			tickSliderFine.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent e) {
					updateTickSpacing(valueDimension, tickSliderRaw.getValue(), tickSliderFine.getValue());
				}
			});
			fineLabel = new JLabel("fine", JLabel.CENTER);
		}
		
		public void reset(){
			tickSliderRaw.setValue(0);
			tickSliderFine.setValue(0);
		}
	}
	
	private class HorizontalSliderPanel extends SliderPanel {
		public HorizontalSliderPanel() {
			super(ValueDimension.X);
			add(rawLabel);
			add(tickSliderRaw);
			add(fineLabel);
			add(tickSliderFine);
			SpringUtilities.makeCompactGrid(this, 1, 4, 0, 5, 10, 0);
		}
	}
	
	private class VerticalSliderPanel extends SliderPanel {
		public VerticalSliderPanel() {
			super(ValueDimension.Y);
			add(tickSliderFine);
			add(fineLabel);
			add(tickSliderRaw);
			add(rawLabel);
			SpringUtilities.makeCompactGrid(this, 4, 1, 5, 0, 10, 0);
		}
	}

}
