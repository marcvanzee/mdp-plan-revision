package model.mdp.operations;

import model.mdp.Action;
import model.mdp.MDP;
import model.mdp.State;
import constants.MathOperations;

/**
 * This class can make the following changes to an MDP:
 * - Add a vertex
 * - Remove a vertex
 * - Change a probability on a QEdge
 * - Change a reward on a QEdge
 * 
 * The dynamicity d is in range [0,1] where
 * 
 * - If d=0:
 * 		-> No nodes/edges/probabilities/rewards are added/removed/changed
 * 
 * - If d=1: On average,
 * 		-> 40% of the number of nodes in the MDP are added at each step
 * 		-> 40% of the number of nodes in the MDP are removed at each step
 * 		-> 60% of the existing QEdges have their probability changed
 * 		-> 60% of the existing QEdges have their reward changed
 * 
 * The average number of nodes to add/remove and QEdges to change is computed as follows:
 *    
 * [ int numNodesToAdd =    numStates * dynamicity * dGamma ]
 * [ int numNodesToRemove = numStates * dynamicity * dGamma ]
 * [ int numQEdgesToChangeProbability = numStates * dynamicity * dGamma ]
 * [ int numQEdgesToChangeReward =      numStates * dynamicity * dGamma ]
 * 
 *  where numStates = MDP.countstates();
 *        dynamicity is in the domain [0,1]) set in GUI
 *        dGamma = SimulationSettings.dGamma
 *        
 * @author marc.vanzee
 *
 */
public class MDPChanger extends MDPOperation 
{
	private int agentChoice = -1;
	private final int variance = settings.getActionVariance();
	private final MDPGenerator mdpGenerator = new MDPGenerator();
	
	public void setAgentChoice(int newAgentChoice) 
	{
		this.agentChoice = newAgentChoice;
	}
	
	public void run(MDP mdp) 
	{
		this.mdp = mdp;
					
		// first remove nodes, so we make sure we won't remove nodes we just have added
		removeNodes();
		addNodes();
		
		changeProbabilities();
		changeRewards();	
	}
	
	private void addNodes() 
	{
		int avgNodesToAdd = avgNodesToChange(mdp, settings.getDGamma());
		
		// there's no limit on the number of nodes to add
		int numNodesToAdd = MathOperations.getRandomInt(avgNodesToAdd, variance, 0, Integer.MAX_VALUE);
		
		while (numNodesToAdd > 0)
		{
			// select a random state to start from
			State s = mdp.getRandomState();
			
			// select a random action to take
			Action a = mdp.getRandomAction();
			
			int countStatesBefore = mdp.countStates();
			mdpGenerator.generateStates(mdp, s, a);
			int nodesAdded = mdp.countStates() - countStatesBefore;
			
			numNodesToAdd -= nodesAdded;
		}
	}
	private void removeNodes() 
	{
		int avgNodesToRemove = avgNodesToChange(mdp, settings.getDGamma());
				
		// we can not remove all nodes, because the one where the agent is on should never be removed
		int numNodesToRemove = MathOperations.getRandomInt(avgNodesToRemove, variance, 1, mdp.countActions());
		
		// remove nodes
	}
	
	private void changeProbabilities() 
	{
		// obtain the average amount of QEdges from which to change the probability
		int avgNodesToChangeProbability = avgNodesToChange(mdp, settings.getDGamma());
		
		int numNodesToChangeProbability = 
				MathOperations.getRandomInt(avgNodesToChangeProbability, variance, 0, mdp.countQEdges());
		
		// change probabilities here
	}
	
	private void changeRewards()
	{
		// obtain the average amount of QEdges from which to change the rewards
		int avgNodesToChangeReward = avgNodesToChange(mdp, settings.getDGamma());
		
		int numNodesToChangeReward =
				MathOperations.getRandomInt(avgNodesToChangeReward, variance, 0, mdp.countQEdges());	
	}
	
	// Note: nodeToAdd(MDP) and nodeToRemove(MDP) are currently identical, 
	// but they could be altered to implement for instance a growing or shrinking MDP.
	private int avgNodesToChange(MDP mdp, double gamma) 
	{
		double dynamicity = settings.getDynamicity(); 
		int numStates = mdp.countStates();
		
		return (int) (dynamicity * numStates * gamma);
	}

}
