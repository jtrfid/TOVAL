package de.invation.code.toval.graphic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import de.invation.code.toval.misc.CollectionUtils;


/**
 * This class manages a number of points and places them in a circular way according to the minimum distance between any two points. <br>
 * For up to 4 points the class places the points in a trivial way:<br>
 * <br>
 * 1: o<br>
 * 2: o o<br>
 * 3:  o<br>
 *    o o<br>
 * 4: o o<br>
 *    o o<br>
 * <br>
 * For 5 or more points, the class places on point at the origin and then introduces one or more circles around this central point
 * and places them on the circular line(s).
 * 
 * @author ts552
 *
 */
public class CircularPointGroup {
	
	private final String trivialPointFormat = "  trivial: %s \n";
	private final String toStringFormat = "CircularPointGroup { \n  points: %s \n%s } \n";
	private final String circleFormat = "  Circle %s [%s/%s]: %s \n";
	
	/**
	 * The point which is used as reference when calculating the coordinates of the managed points.
	 */
	private Position origin = new Position(0,0);
	/**
	 * Minimum distance between any two points.
	 */
	private int minDistance;
	/**
	 * Diameter of points.
	 */
	private int pointDiameter;
	/**
	 * Managed points grouped by color.
	 */
	private Map<PColor, Integer> points = new HashMap<PColor, Integer>();
	/**
	 * Number of managed points.
	 */
	private int numPoints = 0;
	/**
	 * Managed points circles. <br>
	 * Each pointCircle manages a number of points and places them in a circle.
	 */
	private ArrayList<PointCircle> pointCircles = new ArrayList<PointCircle>();
	/**
	 * The order in which colors are assigned to managed points.<br>
	 */
	private ArrayList<PColor> colorOrder = new ArrayList<PColor>();
	/**
	 * Names for trivial points.<br>
	 * If there are less than 5 points, they are not positioned on PointCircles.
	 * for up to 4 points there exist a trivial positioning procedure.
	 * @see CircularPointGroup#getTrivialPoint(TrivialPoint)
	 */
	private enum TrivialPoint {FIRST, SECOND, THIRD, FORTH};
	/**
	 * Indicates the debugging state of this class.<br>
	 * If <code>true</code> status messages are printed on the standard output.
	 */
	private boolean debuggingActivated = false;
	
	/**
	 * Creates a new CircularPointGroup.
	 * @param minDistance The minimum distance between each two points.
	 * @param pointDiameter The diameter of points.
	 */
	public CircularPointGroup(int minDistance, int pointDiameter){
		this.minDistance = minDistance;
		this.pointDiameter = pointDiameter;
	}
	
	/**
	 * Returns the minimum required number of points (pixels) needed to paint all points within all PointCircles.
	 * @return The required points (pixels) for painting all points.
	 */
	public int getRequiredDiameter(){
		return (int) getOutmostPointCircle().getDiameter()+pointDiameter;
	}
	
	/**
	 * Returns the different colors for which points are managed.
	 * @return A set of colors.
	 */
	public Set<PColor> getColors(){
		return points.keySet();
	}
	
	/**
	 * Returns the actual standard point diameter.
	 * @return The diameter of managed points.
	 */
	public int getPointDiameter(){
		return pointDiameter;
	}
	
	/**
	 * Adds a number of points to the CircularPointGroup.
	 * @param color The color of the points.
	 * @param number The number of points.
	 */
	public void addPoints(PColor color, int number){
		debug("Add " + number + " point(s) for color " + color);
		if(points.containsKey(color)){
			System.out.println("color is known");
			points.put(color, points.get(color)+number);
		} else {
			points.put(color, number);
			if(color.equals(PColor.black)){
				colorOrder.add(0, color);
			} else {
				colorOrder.add(color);
			}
		}

		if(numPoints+number>4){
			debug("Introduce point circle(s)");
			int pointsToAdd = number - (numPoints > 4 ? 0 : 1-numPoints);
			int addedPoints = 0;
			while(getOutmostPointCircle()==null || (addedPoints=getOutmostPointCircle().addManagedPoints(pointsToAdd))<pointsToAdd){
				pointCircles.add(new PointCircle(pointCircles.size()+1));
				pointsToAdd -= addedPoints;
			}
		} else {
			debug("No need to introduce point circles");
		}
		numPoints += number;
	}

	/**
	 * Returns the outmost PointCircle. <br>
	 * This is particularly helpful for adding new points since only the outmost circle may have space.
	 * @return The outmost PointCircle
	 * @see PointCircle
	 */
	private PointCircle getOutmostPointCircle(){
		if(pointCircles.isEmpty())
			return null;
		return pointCircles.get(pointCircles.size()-1);
	}
	
	/**
	 * Removes all points of a specific color (if there are points at all).
	 * @param color The color for which all points should be removed.
	 */
	public void removeColor(PColor color){
		if(!points.containsKey(color))
			System.out.println("pille");
		removePoints(color, points.get(color));
	}
	
	/**
	 * Removes the specified number of points for a specific color.
	 * @param color The color for which points should be removed.
	 * @param number The number of points that sould be removed.
	 * @throws IllegalArgumentException if the number of points is negative of higher than the number of managed points.
	 */
	public void removePoints(PColor color, int number) throws IllegalArgumentException {
		debug("Remove "+number+" points for color "+color);
		if(!points.containsKey(color))
			throw new IllegalArgumentException("No coordinates for the given color!");
		if(number<0)
			throw new IllegalArgumentException("Cannot remove a negative number of points!");
		if(number>points.get(color))
			throw new IllegalArgumentException("There are only "+numPoints+" managed points.");
		if(number==0)
			return;
		
		if(numPoints < 5){
			debug("Entering trivial mode.");
			//Trivial case: There are no point circles
			numPoints -= number;
			debug(number+" points removed.");
			return;
		} else {
			debug("Entering non-trivial mode");
			int pointsToRemove = number;
			int removedPoints = 0;
			debug("Points left to remove: "+pointsToRemove);
			while(getOutmostPointCircle()!=null && (removedPoints = getOutmostPointCircle().removeManagedPoints(pointsToRemove)) < pointsToRemove){
				debug("Removed points: "+removedPoints);
				debug("Remove PointCircle "+pointCircles.size());
				pointCircles.remove(getOutmostPointCircle());
				pointsToRemove -= removedPoints;
				debug("Points left to remove: "+pointsToRemove);
			}
			debug("Points removed from circle "+pointCircles.size()+": "+removedPoints);
			if(getOutmostPointCircle().size() == 0){
				debug("Remove PointCircle "+pointCircles.size());
				pointCircles.remove(getOutmostPointCircle());
			}
			numPoints -= number;
		}
		
		//Remove color from color order if not points are left
		if(number==points.get(color)){
			debug("Remove color "+color+" from color order");
			colorOrder.remove(color);
		}
	}
	
	/**
	 * Returns all point coordinates for a specific color.
	 * @param color The color for which the point coordinates are desired.
	 * @return A list of point coordinates.
	 * @throws IllegalArgumentException if there are no coordinates for the given color.
	 */
	public ArrayList<Position> getCoordinatesFor(PColor color) throws IllegalArgumentException {
		debug("");
		debug("Retrieve coordinates for color "+color);
		if(!points.containsKey(color))
			throw new IllegalArgumentException("No coordinates for the given color!");
		
		ArrayList<Position> result = new ArrayList<Position>();
		debug("Determine total position");
		int position = 0;
		int addedPoints = 0;
		for(PColor c: points.keySet()){
			if(!c.equals(color)){
				position += points.get(c);
			} else {
				break;
			}
		}
		debug("Position: "+(position+1)+"/"+numPoints);
		debug("Coords to retrieve: "+points.get(color));
		
		if(numPoints<5){
			//Trivial cases
			debug("Entering mode for trivial cases.");
			ArrayList<TrivialPoint> trivialPoints = new ArrayList<TrivialPoint>(points.get(color));
			for(int i=position; i<position+points.get(color); i++){
				switch(i){
					case 0: 
						trivialPoints.add(TrivialPoint.FIRST); 
						break;
					case 1: 
						trivialPoints.add(TrivialPoint.SECOND); 
						break;
					case 2: 
						trivialPoints.add(TrivialPoint.THIRD); 
						break;
					case 3: 
						trivialPoints.add(TrivialPoint.FORTH); 
						break;
				}
			}
			try{
				for(TrivialPoint p: trivialPoints){
					result.add(getTrivialPoint(p));
				}
			} catch(Exception e){
				//Should not happen since we are sure that there are managed points and we are in trivial mode.
				e.printStackTrace();
			}
		} else {
			//Non-trivial cases
			debug("Entering mode for non-trivial cases.");
			
			//Find the circle that holds the first point dependent on its total position
			debug("Find the circle that holds the first point dependent on its total position");
			int actCircle = 0;
			int startNumber = 1;
			if(position==0){
				debug("First point is the origin");
				//The first point is the origin
				result.add(origin);
				addedPoints++;
			} else {
				debug("First point is inside a circle");
				//The first point is inside a circle
				int counter = 1; //because the first element is not within a circle
				int tmp;
				for(int i=0; i<pointCircles.size(); i++){
					debug("Points in circle " + (actCircle+1) + ": " + pointCircles.get(i).size());
					tmp = counter;
					if(position < (counter += pointCircles.get(i).size())){
						debug("Circle " + (actCircle+1) + " contains the first point");
						startNumber = actCircle==0 ? position : (position + 1) - tmp;
						break;
					} else {
						debug("Circle " + (actCircle+1) + " does not contain the first point");
						actCircle++;
					}
				}
			}
			
			//Retrieve the point coordinates from the circle(s)
			debug("Retrieve point coordinates...");
			try{
				while(addedPoints<points.get(color)){
					debug("Coords left to retrieve: " + (points.get(color)-addedPoints));
					debug("Coords in circle " + (actCircle + 1) + ": "+ pointCircles.get(actCircle).size());
					
					if(points.get(color)-addedPoints >= pointCircles.get(actCircle).size() - startNumber + 1){
						debug("Retrieve all coords from number " + startNumber);
						result.addAll(pointCircles.get(actCircle).getCoordinatesFrom(startNumber));
						addedPoints += pointCircles.get(actCircle).size() - startNumber + 1;
						debug(" -> Retrieved: " + (pointCircles.get(actCircle).size() - startNumber + 1));
					} else {
						debug("Retrieve coords from number " + startNumber + " to number " + (startNumber + points.get(color) - addedPoints - 1));
						result.addAll(pointCircles.get(actCircle).getCoordinates(startNumber, startNumber + points.get(color) - addedPoints - 1));
						debug(" -> Retrieved: " + (points.get(color) - addedPoints));
						addedPoints += points.get(color) - addedPoints;
					}
					startNumber = 1;
					actCircle++;
				}	
			} catch(Exception e){
				//Should not happen since we know the right parameters.
				e.printStackTrace();
			}
			//End non-trivial mode
		} 
		
		return result;
	}
	
	/**
	 * Returns the position of a point in trivial mode (when less than 5 points are managed.<br>
	 * In case only one point is managed, its position equals the origin.<br>
	 * In case two points are managed, they are placed on a line with equal distance to the origin and minDistance to each other.<br>
	 * In case three points are managed, they are each positioned at a corner of a equilateral triangle whose center is the origin.<br>
	 * In case four points are managed, they are each positiones at a corner of a square whose center is the origin.
	 * @param trivial The point for which the coordinates are desired (first, second, third or forth)
	 * @return The coordinated for the desired trivial point.
	 * @throws Exception
	 */
	private Position getTrivialPoint(TrivialPoint trivial) throws Exception{
		if(numPoints==0)
			throw new Exception("No managed points.");
		if(numPoints>4)
			throw new Exception("No trivial positioning for more than 4 points!");
		if(trivial.ordinal()+1>numPoints)
			throw new IllegalArgumentException("There are only "+numPoints+" managed points.");
		
		double dist1,dist2,dist3;
		dist1 = (minDistance+pointDiameter)/2.0;
		switch(numPoints){
			case 1:
				return origin;
			case 2:
				switch(trivial){
					case FIRST: 
						return new Position(origin.getX(), (int) Math.round(origin.getY()-dist1));
					case SECOND: 
						return new Position(origin.getX(), (int) Math.round(origin.getY()+dist1));
					default:
						throw new Exception();
				}
			case 3:
				dist2 = (minDistance+pointDiameter) / Math.pow(3, 1.0/3);
				dist3 = ((minDistance+pointDiameter) * Math.pow(3, 1.0/3))/6;
				switch(trivial){
					case FIRST: 
						return new Position(origin.getX(), (int) Math.round(origin.getY()-dist2));
					case SECOND: 
						return new Position((int) Math.round(origin.getX()-dist1), (int) Math.round(origin.getY()+dist3));
					case THIRD: 
						return new Position((int) Math.round(origin.getX()+dist1), (int) Math.round(origin.getY()+dist3));
					default:
						throw new Exception();
				}
			case 4:
				switch(trivial){
					case FIRST: 
						return new Position((int) Math.round(origin.getX()-dist1), (int) Math.round(origin.getY()-dist1));
					case SECOND: 
						return new Position((int) Math.round(origin.getX()+dist1), (int) Math.round(origin.getY()-dist1));
					case THIRD: 
						return new Position((int) Math.round(origin.getX()-dist1), (int) Math.round(origin.getY()+dist1));
					case FORTH: 
						return new Position((int) Math.round(origin.getX()+dist1), (int) Math.round(origin.getY()+dist1));
					default:
						throw new Exception();
				}
			default:
				throw new Exception();
		}
		
	}
	
	/**
	 * Sets the debugging mode.<br>
	 * If <code>true</code>, status messages are printed to the standard output.
	 * @param active The desired debug mode.
	 */
	public void setDebugMode(boolean active){
		debuggingActivated = active;
	}
	
	/**
	 * Prints debug messages to the standard output unless debugging is deactivated.
	 * @param message The status message to be printed.
	 */
	private void debug(String message){
		if(debuggingActivated){
			System.out.println(message);
		}
	}
	
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder();
		if(numPoints>0){
			try {
				if(numPoints < 5){
					switch(numPoints){
						case 4: builder.append(String.format(trivialPointFormat, getTrivialPoint(TrivialPoint.FORTH)));
						case 3: builder.append(String.format(trivialPointFormat, getTrivialPoint(TrivialPoint.THIRD)));
						case 2: builder.append(String.format(trivialPointFormat, getTrivialPoint(TrivialPoint.SECOND)));
						case 1: builder.append(String.format(trivialPointFormat, getTrivialPoint(TrivialPoint.FIRST)));
						}
				} else {
					builder.append(String.format(trivialPointFormat, origin));
					for(int i=0; i<pointCircles.size(); i++){
						builder.append(String.format(circleFormat, 
													 pointCircles.get(i).order, 
													 pointCircles.get(i).size(), 
												     pointCircles.get(i).capacity(), 
													 CollectionUtils.toString(pointCircles.get(i).getCoordinatesFrom(1))));
					}
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return String.format(toStringFormat, numPoints, builder.toString());
	}
	
	
	
	/**
	 * This class stands for a circle that maintains a number of points that are positioned on the circle line with equal distance.
	 * Each PointCircle has a capacity and a number of points actually managed. The order of a PointCircle determines its relation to the origin.
	 * The higher the order, the more points can be managed. Within the circlar area of a PointCircle one additional PointCircle for each lower order 
	 * together with a point at the origin can be placed without overlapping and respecting the minDistance for points.<br>
	 * <br>
	 * A PointCirclewith order n has the following properties:<br>
	 * m = minimum distance between any two points<br>
	 * d = diameter of points<br>
	 * radius: n*(m+d)<br>
	 * capacity: ceil(2*n*PI)<br>
	 */
	private class PointCircle{
		/**
		 * The order of the PointCircle.
		 */
		private int order;
		/**
		 * The number of points that can be managed (positioned).
		 */
		private int capacity;
		/**
		 * The radius of the circle that is used for placement.
		 */
		private double radius;
		/**
		 * The number of managed points.
		 */
		private int managedPoints;
		/**
		 * The coordinates on the circular line that are used to place points.
		 */
		private Position[] coordinates;

		/**
		 * Creates a new PointCircle with the given order.
		 * @param order The desired order for the PointCircle.
		 */
		public PointCircle(int order){
			this.order = order;
			this.capacity = (int) Math.floor(2*Math.PI*order);
			coordinates = new Position[capacity];
			initialize();
		}
		
		/**
		 * Determines the properties of the underlying circle and the point coordinates.
		 */
		public void initialize(){
			radius = order*(minDistance+pointDiameter);
			for (int i=0; i<capacity; i++){
				coordinates[(i+order-1) % capacity] = new Position((int) (origin.getX() + radius * Math.cos(i * 2 * Math.PI / capacity)), (int) (origin.getY() + radius * Math.sin(i * 2 * Math.PI / capacity)));
			}
		}
		
		/**
		 * Returns the 
		 * @return
		 */
		public double getDiameter(){
			return radius*2;
		}
		
		/**
		 * Returns the number of managed points.
		 * @return The number of managed points.
		 */
		public int size(){
			return managedPoints;
		}
		
		/**
		 * Returns the number of points that can be managed at most.
		 * @return The capacity of the PointCircle.
		 */
		public int capacity(){
			return capacity;
		}
		
		/**
		 * Increases the number of managed points.<br>
		 * In case of a capacity overrun (not the complete number of points can be added to the PointCircle), 
		 * the return value is less than {@link number}.
		 * @param number The number of newly introduced points.
		 * @return The number of points that could be added.
		 */
		public Integer addManagedPoints(int number){
			debug("Add " + number + " points to circle " + order);
			if(number < 0)
				throw new IllegalArgumentException("Cannot add a negative number of points.");
			if(number == 0)
				return 0;
			
			if(capacity >= managedPoints+number){
				managedPoints += number;
				return number;
			} else {
				int space = capacity-managedPoints;
				managedPoints = capacity;
				return space;
			}
		}
		
		/**
		 * Reduces the number of managed points.<br>
		 * In case the PointCircle contains less than {@link number} points,
		 * the return value is less than {@link number}.
		 * @param number The number of points to remove.
		 * @return The number of points that could be removed.
		 */
		public Integer removeManagedPoints(int number){
			debug("Remove " + number + " points from circle " + order);
			if(number < 0)
				throw new IllegalArgumentException("Cannot add a negative number of points.");
			if(number == 0)
				return 0;
			
			if(managedPoints >= number){
				debug("Circle contains at least "+number+" points.");
				managedPoints -= number;
				return number;
			} else {
				debug("Circle contains less than "+number+" points.");
				int tmp = managedPoints;
				managedPoints = 0;
				return tmp;
			}
		}
		
		/**
		 * Returns the coordinates for point numbers {@link from} to {@link from}.<br>
		 * Point numbers are counted from 1.
		 * @param from The starting point number
		 * @param to The ending point number.
		 * @return A list of coordinates for all points in the given range.
		 * @throws Exception in case of invalid parameter values.
		 */
		public ArrayList<Position> getCoordinates(int from, int to) throws Exception {
			if(from<0 || to<0)
				throw new IllegalArgumentException("Negative values are not permitted!");
			if(from>to)
				throw new IllegalArgumentException("From-value must be lower or equal than to-value!");
			if(from>managedPoints || to>managedPoints)
				throw new IllegalArgumentException("This PointCircle holds only "+managedPoints+" points.");
			
			ArrayList<Position> result = new ArrayList<Position>();
			if(from==to){
				result.add(coordinates[from-1]);
			} else {
				for(int i=from-1; i<to; i++)
					result.add(coordinates[i]);
			}
			return result;
		}
		
		/**
		 * Returns coordinates for all managed points beginning at number {@link from}.<br>
		 * Point numbers are counted from 1.
		 * @param from Starting point number.
		 * @return A List of coordinates for all points in the given range.
		 * @throws Exception in case of invalid parameter values.
		 */
		public ArrayList<Position> getCoordinatesFrom(int from) throws Exception {
			return getCoordinates(from, managedPoints);
		}
		
	}
	
	
	

	
	public static void main(String[] args) throws Exception {
		CircularPointGroup g = new CircularPointGroup(5, 10);
		g.addPoints(PColor.black, 1);
		g.addPoints(PColor.blue, 5);
		VisualCircularPointGroup vg = new VisualCircularPointGroup(g);
		vg.asFrame();
	}
	

}
