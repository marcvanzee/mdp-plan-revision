package model;

public class Operations 
{
	private static final boolean report = false;
	
	public static double round(double d, int decimals) {
		return Math.round(d * 100.0)/100.0;
	}
}
