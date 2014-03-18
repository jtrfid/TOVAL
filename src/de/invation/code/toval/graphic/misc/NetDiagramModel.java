package de.invation.code.toval.graphic.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.invation.code.toval.validate.ParameterException;
import de.invation.code.toval.validate.Validate;

/**
 * Enforces that points with reductions are next to each other and sorted ascending wrt. to the reduction percentage.
 */
public class NetDiagramModel extends RegularPolygon{
	
	protected List<Double> percentages;
	protected Map<Object, Integer> objectIndexes = new HashMap<Object,Integer>();
	protected Map<Integer, Object> indexObjects = new HashMap<Integer, Object>();
	protected double maxArea = 0.0;

	public NetDiagramModel(int radius, Object... objects) throws ParameterException{
		super(radius, objects.length);
		percentages = new ArrayList<Double>();
		int index = 0;
		for(Object object: objects){
			percentages.add(1.0);
			indexObjects.put(index, object);
			objectIndexes.put(object, index++);
		}
		maxArea = getArea();
	}
	
	@Override
	protected Position getPointCoordinate(int pointIndex){
		return new Position((int) (origin.getX() + percentages.get(pointIndex) * radius * Math.cos(pointIndex * 2 * Math.PI / numPoints)), (int) (origin.getY() + percentages.get(pointIndex) * radius * Math.sin(pointIndex * 2 * Math.PI / numPoints)));
	}
	
	public void addObject(Object object) throws ParameterException{
		if(objectIndexes.keySet().contains(object))
			throw new ParameterException("Known object: " + object);
		super.addPoint();
		objectIndexes.put(object, numPoints-1);
		indexObjects.put(numPoints-1, object);
		percentages.add(1.0);
	}
	
	public void removeObject(Object object) throws ParameterException{
		if(!objectIndexes.keySet().contains(object))
			throw new ParameterException("Unknown object: " + object);

		int objectIndex = objectIndexes.get(object);
		Object swapObject = indexObjects.get(numPoints - 1);
		Double swapObjectPercentage = percentages.get(numPoints - 1);
		super.removePoint();
		indexObjects.remove(objectIndexes.get(object));
		objectIndexes.remove(object);
		percentages.remove(numPoints - 1);
		if(object != swapObject){
			objectIndexes.put(swapObject, objectIndex);
			indexObjects.put(objectIndex, swapObject);
			percentages.set(objectIndex, swapObjectPercentage);
		}
	}
	
	@Override
	public void addPoint() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void removePoint() throws ParameterException {
		throw new UnsupportedOperationException();
	}

	public void setPointPercentage(Object object, double percentage) throws ParameterException{
		if(!objectIndexes.keySet().contains(object))
			throw new ParameterException("Unknown object: " + object);
		Validate.probability(percentage);
		int objectIndex = objectIndexes.get(object);
		percentages.set(objectIndex, percentage);
		coordinates.set(objectIndex, getPointCoordinate(objectIndex));
		int n = percentages.size();
		do {
			int newn = 1;
			for (int i = 0; i < n - 1; ++i) {
				if (percentages.get(i) > percentages.get(i+1)) {
					swapIndexes(i, i+1);
					newn = i + 1;
				}
			}
			n = newn;
		} while (n > 1);
	}
	
	private void swapIndexes(int i, int j){
		Collections.swap(percentages, i, j);
		Collections.swap(coordinates, i, j);
		Object objectI = indexObjects.get(i);
		Object objectJ = indexObjects.get(j);
		indexObjects.put(i, objectJ);
		indexObjects.put(j, objectI);
		objectIndexes.put(objectI, j);
		objectIndexes.put(objectJ, i);
	}
	
	public double getNormalizedValue(){
		return getArea() / maxArea;
	}
	
	public static void main(String[] args) throws ParameterException {
		NetDiagramModel c = new NetDiagramModel(100, "a","b","c","d","e","f");
		System.out.println(c.getCoordinates());
		System.out.println(c.getArea());
		c.setPointPercentage("d", .5);
		System.out.println(c.getCoordinates());
		c.setPointPercentage("a", .6);
		System.out.println(c.getCoordinates());
		System.out.println(c.getNormalizedValue());
		c.removeObject("d");
	}
}
