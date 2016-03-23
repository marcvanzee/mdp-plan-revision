package simulations;

import java.util.LinkedList;
import java.util.Stack;

import constants.Printing;
import mdp.agent.MetaAction;

public class SimThread implements Runnable 
{
	final Hypothesis h;
	int score = 0;
	MetaAction metaAct;
	
	public SimThread(Hypothesis h)
	{
		this.h = h;
		metaAct = h.metaAction;
	}
			
	public void run()
	{  
		Printing.minsim("getting optimal score for " + h.metaAction + " (depth=" + h.depth +")");
		
		int maxScore = Integer.MIN_VALUE;
		
		// we implement this as iterative depth-first search using a stack
		Stack<Hypothesis> hStack = new Stack<Hypothesis>();
		
		hStack.add(h);
		
		LinkedList<Hypothesis> history = new LinkedList<Hypothesis>();
		
		h.save();
		
		while (!hStack.isEmpty())
		{
			Hypothesis curH = hStack.pop();
			
			if (curH.metaAction == null) {
				System.out.println("meta-action empty in hypothesis!");
				continue;
			}
						
			// step until the agent has executed the meta-action
			curH.step(); 
			
			curH.save();
			
			// if we are at the leaf of the search tree, see whether we have found an optimal score
			if (curH.depth <= 0)
			{
				//if (curH.score > 0)
				//	Printing.angel("For " + metaAct + ", score " + curH.score + MinimalTileworldSimulation.toStringWithHyp(curH));
						
				if (curH.score > maxScore) {
					maxScore = curH.score;
					history = curH.history;
				};
				continue;
			}
			
			// use the same hypothesis as we had to deliberate
			
			//curH.depth--;
			curH.metaAction = MetaAction.DELIBERATE;
		
			hStack.push(curH);
			
			// if there is a plan in the hypothesis, we can act
			if (curH.plan.size() > 0)
			{
				Hypothesis hAct = new Hypothesis(curH, MetaAction.ACT);
				
				hStack.push(hAct);
			}
		}

		if (history.size() > 0){
		Hypothesis opt = history.getLast();
		
		Printing.minsim("Finished the highest hypothesis for "+metaAct+" with score " + opt.score + " (steps="+opt.steps+")");
		Printing.minsim("Normalized score: " + maxScore);
		
		
		Printing.minsim("=== history:");
		
		for (Hypothesis h : history)
		{
			Printing.minsim(MinimalTileworldSimulation.toStringWithHyp(h));
		}
		}
		
		score = maxScore;
	}
	
	public double getMaxScore()
	{
		return score;
	}

}
