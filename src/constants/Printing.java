package constants;

public class Printing 
{
	private enum Reporter {
		ANGEL, HYP, SIM, MINSIM;
		
		private static boolean report(Reporter r){
			switch (r) {
			case ANGEL: return false;
			case HYP: return false;
			case SIM: return false;
			case MINSIM: return false;
			}
			
			return false;
		}
	}
	
	public static String spaces(int num)
	{
		return new String(new char[num]).replace("\0", " ");
	}

	public static void angel(String str)
	{
		if (Reporter.report(Reporter.ANGEL))
			System.out.println("<angel>" + str);
	}
	
	public static void hyp(String str)
	{
		if (Reporter.report(Reporter.HYP))
			System.out.println("<hypothesis>" + str);
	}
	
	public static void sim(String str)
	{
		if (Reporter.report(Reporter.SIM))
			System.out.println("<simulation>" + str);
	}
	
	public static void minsim(String str)
	{
		if (Reporter.report(Reporter.MINSIM))
			System.out.println("<minsimulation>" + str);
	}
	
}
