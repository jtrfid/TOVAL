package de.invation.code.toval.graphic;

public class Position {
	private final String toStringFormat = "(%s;%s)";
	private int xCoordinate;
	private int yCoordinate;
	
	public Position(int xCoord, int yCoord){
		xCoordinate = xCoord;
		yCoordinate = yCoord;
	}
	
	public int getX(){
		return xCoordinate;
	}
	public int getY(){
		return yCoordinate;
	}
	@Override
	public String toString(){
		return String.format(toStringFormat, xCoordinate, yCoordinate);
	}
}