package graphic;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Polygon;


public class GraphicUtils {
	
	//--Drawing circles
	
	/**
	 * Draws a circle with the specified diameter using the given point as center.
	 * @param g Graphics context
	 * @param center Circle center
	 * @param diameter Circle diameter
	 */
	public static void drawCircle(Graphics g, Point center, int diameter){
		drawCircle(g, (int) center.getX(), (int) center.getY(), diameter);
	}
	
	/**
	 * Draws a circle with the specified diameter using the given point coordinates as center.
	 * @param g Graphics context
	 * @param centerX X coordinate of circle center
	 * @param centerY Y coordinate of circle center
	 * @param diameter Circle diameter
	 */
	public static void drawCircle(Graphics g, int centerX, int centerY, int diameter){
		g.drawOval((int) (centerX-diameter/2), (int) (centerY-diameter/2), diameter, diameter);
	}
	
	/**
	 * Draws a circle with the specified diameter using the given point as center
	 * and fills it with the current color of the graphics context.
	 * @param g Graphics context
	 * @param center Circle center
	 * @param diameter Circle diameter
	 */
	public static void fillCircle(Graphics g, Point center, int diameter){
		fillCircle(g, (int) center.getX(), (int) center.getY(), diameter);
	}
	
	/**
	 * Draws a circle with the specified diameter using the given point coordinates as center
	 * and fills it with the current color of the graphics context.
	 * @param g Graphics context
	 * @param centerX X coordinate of circle center
	 * @param centerY Y coordinate of circle center
	 * @param diameter Circle diameter
	 */
	public static void fillCircle(Graphics g, int centerX, int centerY, int diam){
		g.fillOval((int) (centerX-diam/2), (int) (centerY-diam/2), diam, diam);
	}
	
	/**
	 * Draws a circle with the specified diameter using the given point as center
	 * and fills it with the given color.
	 * @param g Graphics context
	 * @param center Circle center
	 * @param diameter Circle diameter
	 */
	public static void fillCircle(Graphics g, Point center, int diameter, Color color){
		fillCircle(g, (int) center.x, (int) center.y, diameter, color);
	}
	
	/**
	 * Draws a circle with the specified diameter using the given point coordinates as center
	 * and fills it with the given color.
	 * @param g Graphics context
	 * @param centerX X coordinate of circle center
	 * @param centerY Y coordinate of circle center
	 * @param diameter Circle diameter
	 */
	public static void fillCircle(Graphics g, int centerX, int centerY, int diam, Color color){
		Color c = g.getColor();
		g.setColor(color);
		fillCircle(g, centerX, centerY, diam);
		g.setColor(c);
	}
	
	
	
	
	
	//--Drawing arrows
	
	/**
	 * Draws an arrow between two given points using the specified weight.<br>
	 * The arrow is leading from point {@link from} to point {@link to}.
	 * @param g Graphics context
	 * @param from Arrow starting point
	 * @param to Arrow ending point
	 * @param weight Arrow weight
	 */
	public static void drawArrow(Graphics g, Point from, Point to, double weight){
		drawArrow(g, from.getX(), from.getY(), to.getX(), to.getY(), weight);
	}
	
	/**
	 * Draws an arrow between two points (specified by their coordinates) using the specified weight.<br>
	 * The arrow is leading from point {@link from} to point {@link to}.
	 * @param g Graphics context
	 * @param x0 X coordinate of arrow starting point
	 * @param y0 Y coordinate of arrow starting point
	 * @param x1 X coordinate of arrow ending point
	 * @param y1 Y coordinate of arrow ending point
	 * @param weight Arrow weight
	 */
	public static void drawArrow(Graphics g, double x0, double y0, double x1, double y1, double weight){
		int ix2,iy2,ix3,iy3;                 
	    double sinPhi,cosPhi,dx,dy,xk1,yk1,xk2,yk2,s;
	    dx=x1-x0;
	    dy=y1-y0;
	    int maxArrowWidth = 10;
	    //arrow length
	    s=Math.sqrt(dy*dy+dx*dx);
	    //arrow head length
	    int headLength = (int) Math.round(s*0.5);
	    
	    double arrowAngle = Math.atan((double) (weight*maxArrowWidth)/headLength);
	    double arrowAngle2 = Math.atan((double) maxArrowWidth/headLength);

	    sinPhi=dy/s;                          // Winkel des Pfeils 
	    cosPhi=dx/s;                          // mit der X-Achse
	    if(s<headLength) {                        // Der Pfeil 
	      x0=x1-headLength*cosPhi;                // .. darf nicht kürzer 
	      y0=y1-headLength*sinPhi;                // .. als die Spitze sein
	    }
	    xk1=-headLength*Math.cos(arrowAngle);              // Koordinaten
	    yk1=headLength*Math.sin(arrowAngle);               // .. der Pfeilspitze

	    xk2=-headLength*Math.cos(arrowAngle2);              // Koordinaten
	    yk2=headLength*Math.sin(arrowAngle2);  
	    ix2=(int)(x1 + xk1*cosPhi - yk1*sinPhi);  // Pfeilspitze 
	    iy2=(int)(y1 + xk1*sinPhi + yk1*cosPhi);  // .. um Winkel Phi 
	    ix3=(int)(x1 + xk1*cosPhi + yk1*sinPhi);  // .. rotieren 
	    iy3=(int)(y1 + xk1*sinPhi - yk1*cosPhi);  // .. und translatieren 
	    Color c = g.getColor();
	    g.setColor(Color.black);
	    g.drawLine((int)x0,(int)y0,(int)x1,(int)y1); // Pfeilachse 
	    Polygon p = new Polygon();
	    p.addPoint((int) x1, (int) y1);
	    p.addPoint((int) ix2, (int) iy2);
	    p.addPoint((int) ix3, (int) iy3);
	    g.setColor(c);
	    g.fillPolygon(p);
	    g.setColor(Color.black);
	    g.drawPolygon(p);
	    g.drawLine((int)(x1 + xk2*cosPhi - yk2*sinPhi), (int)(y1 + xk2*sinPhi + yk2*cosPhi), (int)(x1 + xk2*cosPhi + yk2*sinPhi), iy3=(int)(y1 + xk2*sinPhi - yk2*cosPhi));
	}
	
	/**
	 * Draws an arrow between two given points using the specified weight and offsets.<br>
	 * The arrow lies on the line between point {@link from} to point {@link to} and points to point {@link to}.
	 * The distance between point {@link from} and the arrow start is defined by {@link offsetFrom},
	 * respectively for the arrow end, {@link to} and {@link offsetTo}.
	 * @param g Graphics context
	 * @param from Basic arrow starting point
	 * @param to Basic arrow ending point
	 * @param offsetFrom Distance between {@link from} and arrow starting point
	 * @param offsetTo Distance between {@link to} and arrow ending point
	 * @param weight Arrow weight
	 */
	public static void drawArrowOffset(Graphics g, Point from, Point to, int offsetFrom, int offsetTo, double weight){
		double xk1,xk2,s,dx,dy,sinPhi,cosPhi;
		dx = to.x-from.x;
		dy = to.y-from.y;
		xk1=-offsetFrom/2;
		xk2=-offsetTo/2;
	    s=Math.sqrt(dy*dy+dx*dx);
	    sinPhi=dy/s;
	    cosPhi=dx/s; 		
		drawArrow(g, from.getX()-xk1*cosPhi, from.getY()-xk1*sinPhi, to.getX()+xk2*cosPhi, to.getY()+xk2*sinPhi, weight);
	}
}
