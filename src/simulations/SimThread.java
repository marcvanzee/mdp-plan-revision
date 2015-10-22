package simulations;

import java.util.Stack;

import constants.Printing;
import mdp.agent.MetaAction;

public class SimThread implements Runnable 
{
	final Hypothesis h;
	int score = 0;
	int numFinishedSims = 0;
	MetaAction metaAct;
	
	public SimThread(Hypothesis h)
	{
		this.h = h;
		metaAct = h.metaAction;
	}
	
	public int countSims(){
		return numFinishedSims;
	}
	
	public void run()
	{  
		Printing.minsim("getting optimal score for " + h.metaAction + " (depth=" + h.depth +")");
		
		int maxScore = Integer.MIN_VALUE;
		
		// we implement this as iterative depth-first search using a stack
		Stack<Hypothesis> hStack = new Stack<Hypothesis>();
		
		hStack.add(h);
		
		while (!hStack.isEmpty())
		{
			Hypothesis curH = hStack.pop();
			
			if (curH.metaAction == null) {
				System.out.println("meta-action empty in hypothesis!");
				continue;
			}
			
			Printing.minsim("Popped hypothesis from the stack with depth="+curH.depth + ", metaAction="+curH.metaAction);

			// step until the agent has executed the meta-action
			curH.step(); 
			
			// if we are at the leaf of the search tree, see whether we have found an optimal score
			if (curH.depth <= 0)
			{
				numFinishedSims++;
				
				//if (curH.score > 0)
				//	Printing.angel("For " + metaAct + ", score " + curH.score + MinimalTileworldSimulation.toStringWithHyp(curH));
				
				Printing.minsim("Finished a hypothesis with score " + curH.score + " (maxScore=" + maxScore +")");
				if (curH.score > maxScore) {
					maxScore = curH.score;
					Printing.minsim("highest score so far!");
				};
				continue;
			}
			
			// use the same hypothesis as we had to deliberate
			curH.depth--;
			curH.metaAction = MetaAction.DELIBERATE;
		
			hStack.push(curH);
			
			// if there is a plan in the hypothesis, we can act
			if (curH.plan.size() > 0)
			{
				Hypothesis hAct = new Hypothesis(curH, MetaAction.ACT);
				
				hStack.push(hAct);
			}
		}
		
		score = maxScore;
	}
	
	public int getMaxScore()
	{
		return score;
	}

}
