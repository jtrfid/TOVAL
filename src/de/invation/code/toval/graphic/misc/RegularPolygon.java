package de.invation.code.toval.graphic.misc;

import java.awt.Point;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;

import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;

/**
 * This class stands for a circle that maintains a number of points that are positioned on the circle line with equal distance.
 */
public class RegularPolygon {

	/**
	 * The radius of the circle that is used for placement.
	 */
	protected double radius;
	/**
	 * The number of points.
	 */
	protected int numPoints;
	protected Point origin = new Point(0,0);
	/**
	 * The coordinates on the circular line that are used to place points.
	 */
	protected List<Position> coordinates = null;

	/**
	 * Creates a new PointCircle with the given order.
	 * @throws ParameterException 
	 */
	public RegularPolygon(int radius, int numPoints) throws ParameterException{
		Validate.positive(radius);
		Validate.positive(numPoints);
		Validate.bigger(numPoints, 2);
		
		this.radius = radius;
		this.numPoints = numPoints;
		coordinates = new ArrayList<Position>(numPoints);
		for(int i=0; i<numPoints; i++){
			coordinates.add(null);
		}
	}
	
	/**
	 * Determines the properties of the underlying circle and the point coordinates.
	 */
	protected void determineCoordinates(){
		for (int i=0; i<numPoints; i++){
			coordinates.set(i, getPointCoordinate(i));
		}
	}
	
	protected Position getPointCoordinate(int pointIndex){
		return new Position((int) (origin.getX() + radius * Math.cos(pointIndex * 2 * Math.PI / numPoints)), (int) (origin.getY() + radius * Math.sin(pointIndex * 2 * Math.PI / numPoints)));
	}
	
	public void addPoint(){
		numPoints++;
		coordinates.add(null);
	}
	
	public void removePoint() throws ParameterException{
		if(numPoints == 3)
			throw new ParameterException("REgularPolygon must at least contain 3 points.");
		numPoints--;
		coordinates.remove(numPoints-1);
	}
	
	public void setOrigin(int x, int y) throws ParameterException{
		Validate.notNull(x);
		Validate.notNull(y);
		this.origin = new Point(x,y);
		determineCoordinates();
	}
	
	public void setRadius(int radius) throws ParameterException{
		Validate.positive(radius);
		this.radius = radius;
		determineCoordinates();
	}
	
	/**
	 * Returns the diameter
	 */
	public double getDiameter(){
		return radius*2;
	}
	
	public boolean pushIntoFirstQuadrant(){
		// Check if it is necessary to shift the coordinates
		boolean shiftNecessary = false;
		for (int i=0; i<numPoints; i++){
			if(coordinates.get(i).getX() < 0 || coordinates.get(i).getY() < 0){
				shiftNecessary = true;
				break;
			}
		}
		if(!shiftNecessary)
			return false;
		
		try {
			setOrigin((int) (origin.getX()+radius), (int) (origin.getY()+radius));
		} catch (ParameterException e) {}
		return true;
	}
	
	/**
	 * Returns the number of points that can be managed at most.
	 * @return The capacity of the PointCircle.
	 */
	public int numPoints(){
		return numPoints;
	}
	
	public List<Position> getCoordinates() {
		determineCoordinates();
		return coordinates;
	}
	
	public Polygon getPolygon(){
		Polygon result = new Polygon();
		for(Position coordinate: coordinates){
			result.addPoint(coordinate.getX(), coordinate.getY());
		}
		return result;
	}
	
	public Point getOrigin(){
		return new Point((int) origin.getX(), (int) origin.getY());
	}
	
	public double getArea(){
		getCoordinates();
		int oldOriginX = (int) origin.getX();
		int oldOriginY = (int) origin.getY();
		boolean resetNecessary = pushIntoFirstQuadrant();
		int sum = 0;
		int index = 0;
		int nextIndex = 0;
		for(int i=0; i<numPoints; i++){
			index = i % coordinates.size();
			nextIndex = (i+1) % coordinates.size();
			sum += (coordinates.get(index).getY() + coordinates.get(nextIndex).getY())*
				   (coordinates.get(index).getX() - coordinates.get(nextIndex).getX());
		}
		if(resetNecessary){
			try {
				setOrigin(oldOriginX, oldOriginY);
			} catch (ParameterException e) {}
		}
		return sum/2.0;
	}
	
	public static void main(String[] args) throws ParameterException {
		RegularPolygon c = new RegularPolygon(100, 3);
		System.out.println(c.getCoordinates());
		c.pushIntoFirstQuadrant();
		System.out.println(c.getCoordinates());
		System.out.println(c.getArea());
		System.out.println(Math.PI*100*100);
		System.out.println(c.getCoordinates());
		c.addPoint();
		System.out.println(c.getCoordinates());
		c.removePoint();
		System.out.println(c.getCoordinates());
	}
}
