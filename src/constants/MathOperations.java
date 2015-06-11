package constants;

/**
 * Class containing static helper functions.
 * We ensure it cannot be instantiated by declaring a prive constructor.
 * 
 * @author marc.vanzee
 *
 */
public class MathOperations 
{
	//Suppress default constructor for noninstantiability
	private MathOperations() {
		// This constructor will never be invoked
	}
	
	public static double round(double d, int decimals) {
		return Math.round(d * 100.0)/100.0;
	}
}
