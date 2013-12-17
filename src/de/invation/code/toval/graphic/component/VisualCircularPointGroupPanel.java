package de.invation.code.toval.graphic.component;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.Iterator;

import javax.swing.JPanel;

import de.invation.code.toval.graphic.misc.CircularPointGroup;
import de.invation.code.toval.graphic.misc.PColor;
import de.invation.code.toval.graphic.misc.Position;
import de.invation.code.toval.graphic.util.GraphicUtils;

public class VisualCircularPointGroupPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	protected static final Dimension defaultDimension = new Dimension(400,400);
	protected Dimension dimension = defaultDimension;
	protected Point center; 
	protected CircularPointGroup pointGroup;
	
	private VisualCircularPointGroupPanel(Dimension dimension){
		this.dimension = dimension;
		setPreferredSize(dimension);
		center = new Point(dimension.width/2,dimension.height/2);
	}
	
	public VisualCircularPointGroupPanel(CircularPointGroup circularPointGroup){
		this(new Dimension(circularPointGroup.getRequiredDiameter(), circularPointGroup.getRequiredDiameter()));
		this.pointGroup = circularPointGroup;	
	}
	
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawPoints();
	}
	
	protected void drawPoints(){
		Graphics g = getGraphics();
		Iterator<PColor> iter = pointGroup.getColors().iterator();
		PColor actColor;
		while(iter.hasNext()){
			actColor = iter.next();
			g.setColor(new Color(actColor.getRGB()));
			for(Position p: pointGroup.getCoordinatesFor(actColor)){
				GraphicUtils.fillCircle(g, (int) (center.getX()+p.getX()), (int) (center.getY()+p.getY()), pointGroup.getPointDiameter());
			}
		}
	}
	
	public void asFrame() {
		new DisplayFrame(this, true);
	}

}
