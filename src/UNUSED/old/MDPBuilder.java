package UNUSED.old;

import java.util.HashSet;
import java.util.Random;

import settings.GeneralMDPSettings;

public class MDPBuilder {
   
	Random r = new Random();
	Model model = new Model();
	
	public MDPBuilder() {
		System.out.println("constructing");
		
	}
	
	//model.infiniteValueIteration(.99f, .1f);
    /* This infinite value iteration is only done to evaluate convergence of qLearning to optimal policy
     * The values derived from it are in no other ways used to calculate q
     * values or generate exploration policies/ policy factors.
    */
    //runQLearning_and_graphQVals();// Runs q learning and sends the information to a visualizer.
 
	public void buildNewModel() {    	
        model = new Model();

    	// first generate all states
    	State_old [][] states = null; //generateStates();
    	System.out.println("buildNewModel(): " + states.length);
    	
    	// then generate transitions
    	generateTransitions(states);
    	
    	// finally add states to the model
    	model.setStates(states);
    }
	
	public void printModel() {
		model.print();
	}
	
	public Model getModel() {
		return model;
	}
	/*
	private State_old[][] generateStates() {
		int maxX = Parameters.maxX, maxY = Parameters.maxY;
		double pState = Parameters.pState, minReward = Parameters.minReward, 
				maxReward = Parameters.maxReward;
		
    	State_old[][] states = new State_old[maxX][maxY];
    	
    	int num = 0;
    	for (int i=0; i<maxX; i++) 
    	{    		
    		for (int j=0; j<maxY; j++) 
    		{
    			states[i][j] = (test_p(pState) ? 
    					new State_old(rand_double(minReward, maxReward), "" + num, i, j):
    						null);
    			if (states[i][j] != null) num++;
    		}
    	}
    	
    	return states;
	}
	*/
	
	private void generateTransitions(State_old[][] states) {
		//int numActions = Settings.numActions, maxX = Settings.maxX, maxY = Settings.maxY;
		//double pExecutable = Settings.pExecutable, pDeterministic = Settings.pDeterministic;
		
		// this is wrong but changed so it doesn't throw errors
		int numActions = 0, maxX = 0, maxY = 0;
		double pExecutable = 0, pDeterministic = 0;
		
    	// for each state, determine whether it is executable and, if so, whether it is deterministic
    	// if it is deterministic, choose a random neighbor and create a link
    	// if not, collect all neighbors and distribute probability over these neighbors arbitrarily
    	for (int a = 0; a < numActions; a++) 
    	{
	    	for (int x = 0; x < maxX; x++) 
	    	{
				for (int y = 0; y < maxY; y++) 
				{
					if (states[x][y] == null) continue;
					if (test_p(pExecutable)) 
					{
						// action is executable, so first create it
						Action action = new Action(Character.toString(((char)('a'+a))));
						
						// add the action to the state
						states[x][y].addAction(action);
						
						if (test_p(pDeterministic)) 
						{
							// action is deterministic
							// create transition with probability 1 to a random neighbor
							int neighbor[] = random_neighbor(x,y,maxX,maxY);

							action.addTransition(1, states[neighbor[0]][neighbor[1]]);
						} else 
						{
							// action is non-deterministic
							// collect a random number of neighbors
							HashSet<int[]> neighbors = random_neighbors(x,y,maxX,maxY);
							
							// assign an arbitrary probability distribution over the neighbors
							double[] prop_set = prob_distr(neighbors.size());
							
							// now add transitions with probabilities
							int i=0;
							for (int[] neighbor : neighbors) {
								action.addTransition(prop_set[i], states[neighbor[0]][neighbor[1]]);
								i++;
							}
						}
					}
				}
			}
    	}
    }
	    	
    /**
     * Pick a random neighbor in the matrix, taking into account the boundaries of the grid.
     * 
     * @param x x coordinate of a State in a grid
     * @param y y coordinate of a State in a grid 
     * @param max_x maximal x coordinate of the grid
     * @param max_y maximal y coordinate of the grid
     * @return integer array [x,y] containing the coordinate of a random neighbor in the grid
     */
    private int[] random_neighbor(int x, int y, int max_x, int max_y) {
    	
    	int ret_x = (x == 0 ? 1 : 
    		(x == max_x-1 ? max_x - 2 : 
    			(rand_int(0,1) == 0 ? x - 1 : x + 1)));

    	int ret_y = (y == 0 ? 1 : 
    		(y == max_y-1 ? max_y - 2 : 
    			(rand_int(0,1) == 0 ? y - 1 : y + 1)));
    	
    	return new int[] { ret_x, ret_y };
    }
    
    private HashSet<int[]> random_neighbors(int x, int y, int max_x, int max_y) {
    	HashSet<int[]> neighbors = new HashSet<int[]>();
    	
    	// first get the coordinates of all neighbors of state (x,y)
    	for (int i=x-1; i<=x+1; i++) 
    	{
    		if (i < 0 || i >= max_x) continue;
    		for (int j=y-1; j<=y+1; j++) 
    		{
    			if (j < 0 || j >= max_y) continue;
    			
    			neighbors.add(new int[] { i, j});
    		}
    	}
    	
    	return neighbors;
    }
    
    private double[] prob_distr(int vars) {
    	double[] ret = new double[vars];
    	double sum = 0;
    	
    	for (int i=0; i<vars; i++) {
    		ret[i] = r.nextDouble();
    		sum += ret[i];
    	}
    	
    	for (int i=0; i < ret.length; i++) {
    		ret[i] = round(ret[i]/sum,2);
    	}
    	
//    	System.out.print("prop: ");
//    	for (double d : ret) System.out.print(d + " ");
//    	System.out.println("");
    	return ret;
    }
    
    private double round(double d, int decimals) {
    	return Math.round(d * 100.0)/100.0;
    }
    
    private double rand_double(double min, double max) {
    	return r.nextDouble()*(max-min)+min;
    }
    
    private int rand_int(int min, int max) {
    	return r.nextInt((max+1)-min)+min;
    }
    
     private boolean test_p(double d) {
    	return r.nextDouble() < d;
    }
}
