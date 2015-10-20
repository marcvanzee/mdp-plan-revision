package constants;

public class Printing 
{
	private static boolean PRINT = false;
	
	public static String spaces(int num)
	{
		return new String(new char[num]).replace("\0", " ");
	}

	public static void angel(String str)
	{
		if (PRINT)
			System.out.println("<angel>" + str);
	}
	
	public static void hyp(String str)
	{
		if (PRINT)
			System.out.println("<hypothesis>" + str);
	}
	
	public static void sim(String str)
	{
		if (PRINT)
			System.out.println("<simulation>" + str);
	}
	
	public static void minsim(String str)
	{
		if (PRINT)
			System.out.println("<minsimulation>" + str);
	}
	
}
