package model;

import gui.DrawTaskScheduler;
import model.mdp.MDP;
import model.mdp.State;

public interface Simulation
{
	/*
	 * Methods to control the simulation
	 */
	public void buildNewModel();
	public void startSimulation(DrawTaskScheduler scheduler);
	public void stopSimulation();
	public void step();
	public void notifyGUI();
	
	
	/*
	 * Getters
	 */
	public MDP getMDP();
	public int getSteps();
}
