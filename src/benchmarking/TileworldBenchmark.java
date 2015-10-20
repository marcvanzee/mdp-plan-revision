package benchmarking;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;

import gui.Main;
import settings.BenchmarkSettings;
import settings.TileworldSettings;
import simulations.TileworldSimulation;

public class TileworldBenchmark 
{
	private TileworldSimulation simulation;
	
	public static void main(String args[])
	{
		try {
			try {
				(new TileworldBenchmark()).go();
			} catch (IllegalArgumentException | InvocationTargetException | NoSuchMethodException
					| SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void go() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{		
		try {
			Main.loadSettings();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		PrintStream realSystemOut = System.out;
		
		boolean loga = BenchmarkSettings.LOGARITHMIC;
		
		int dMin = BenchmarkSettings.DYNAMISM_MIN,
				dMax = BenchmarkSettings.DYNAMISM_MAX,
				dPoints = BenchmarkSettings.DYNAMISM_POINTS,
				simLength = BenchmarkSettings.SIMULATION_LENGTH,
				simRep = BenchmarkSettings.REPETITIONS;
	
		double dStep = loga ? (Math.log10(dMax) - Math.log10(dMin)) / dPoints :
					(dMax-dMin)/dPoints;
			
		System.out.println("###### benchmark settings ####");
		System.out.println("# dynamism values: ["+ dMin + ", " + dMax +"], step size: " + dStep);
		System.out.println("# simulation length: " + simLength + ", repetitions per dynamism value: " + simRep);
		System.out.println("# total nr of simulations: " + (dPoints * simRep));
		System.out.println("# logarithmic scale: " + loga);
		System.out.println("##############################\n");
		
		System.out.println("planning time: " + TileworldSettings.PLANNING_TIME);
		
		System.out.println("dynamism; effectiveness");
		
		int dynamism = dMin;
		int iterations = 0;
		
		while (dynamism <= dMax)
		{
			TileworldSettings.DYNAMISM = dynamism;
			
			double totalEff = 0;
			
			//System.out.println("<benchmark>dynamism="+dynamism);
		
			for (int simCount=0; simCount < simRep; simCount++)
			{
				//System.out.println("<benchmark> starting simulation " + (simCount+1));
				simulation = new TileworldSimulation();
				simulation.buildNewModel();
				simulation.startSimulation(simLength);
				
				double score = simulation.getAgentScore(),
						maxScore = simulation.getMaxScore(),
						effectiveness = (double) score / (double) maxScore;
				
				totalEff += effectiveness;
			}
		
			totalEff /= (double) simRep;
			
			System.out.println(dynamism + "; " + totalEff);
			
			
			
			int newDynamism = dynamism;
			
			while (newDynamism == dynamism) {
				iterations++;
				newDynamism = (int) (loga ? dMin + Math.pow(10.0, iterations * dStep) : dynamism + dStep);
			}
			
			dynamism = newDynamism;
		}
	}
	
	private static class NullOutputStream extends OutputStream {
	    @Override
	    public void write(int b){
	         return;
	    }
	    @Override
	    public void write(byte[] b){
	         return;
	    }
	    @Override
	    public void write(byte[] b, int off, int len){
	         return;
	    }
	    public NullOutputStream(){
	    }
	}

}
