package constants;

import settings.TileworldSettings;

public class Printing 
{
	private enum Reporter {
	
		ANGEL, HYP, SIM, MINSIM, SPA;
		
		private static boolean report(Reporter r){
			if (TileworldSettings.PRINT_NOTHING)
				return false;
			switch (r) {
			case ANGEL: return true;
			case HYP: return false;
			case SIM: return false;
			case MINSIM: return true;
			case SPA: return true;
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
	
	public static void spa(String str)
	{
		if (Reporter.report(Reporter.SPA))
			System.out.println("<shortest path agent>" + str);
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
