package UNUSED.old;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

// The stochastic environment class composed of a state space and the states individual transition model.
public class Model {
    
	private State_old[][] states;
    private double gamma; // discount rate
    private double epsilon; // convergence threshold
    private int currentEpoch;// current backwards induction epoch that must reach k for finite horizon
    private int stateCount;
    
    Model(){
        gamma = 1;
        epsilon = 0;
        currentEpoch = 0;
    }
    
    public State_old[][] getStates() {
    	return states;
    }
    
    public State_old getRandomState() {
    	if (states == null || states[0] == null)
    		return null;
    	
    	Random r = new Random();
    	State_old s = null;
    	
    	while (s == null) {
    		int x = r.nextInt(10);
    		int y = r.nextInt(10);
    		s = states[x][y];
    	}
    	
    	return s;
    }

    //Does discounted infinite value iteration with discount pDisoucnt and convergence threshold pEpsilon
    //Loop halts when the max norm [the largest scalar difference between corresponding elements of the utility vectors U and U'] is less than (e(1-g)/g)
    void infiniteValueIteration(double pDiscount, double pEpsilon){
        gamma = pDiscount;
        epsilon = pEpsilon;
        double maxNorm;
        maxNorm = pEpsilon;
        double possibleMax;
        while(epsilon*(1-gamma)/gamma <= maxNorm){
            possibleMax = 0;
            changeWholeEpoch();
            
            for (State_old[] stateArray : states) {
            	for (State_old state : stateArray) {
            		state.updateValueAndPolicy(gamma);
            		
            		if(state.getDiff()> possibleMax)
            			possibleMax = state.getDiff();
                }
            }
            maxNorm = possibleMax;
        }
    }
        
    void setStates(State_old[][] states){
        this.states = states;
        
        stateCount = 0;
        for (State_old[] stateArray : states) {
        	for (State_old state : stateArray) {
                if (state != null)
                	stateCount++;
            }
    	}
    }
    
    void print(){
    	for (State_old[] stateArray : states) {
        	for (State_old state : stateArray) {
                state.print();
            }
    	}
    }
    
    void printPolicy(){
    	for (State_old[] stateArray : states) {
        	for (State_old state : stateArray) {
                state.printPolicy();
            }
    	}
    }
    
    void changeWholeEpoch(){
        timeProceedsSinEpochPP();// I separated it like this to clarify the difference between the two is minimal
        currentEpoch++; //  epoch change happens here
    }
    
    void timeProceedsSinEpochPP(){
    	for (State_old[] stateArray : states) {
        	for (State_old state : stateArray) {
                state.changeEpoch();
        	}
        }
    }
    	
    void printEpoch(){
        System.out.println("The Current Epoch is " + currentEpoch);
    }
        
    double getMaxReward(){ // what is the maximum possible reward in any state in the model.
        if (states == null || states[0] == null)
        	return Integer.MIN_VALUE;
    	
    	double outVal = states[0][0].getReward();
        double candidateVal;
        for (State_old[] stateArray : states) {
        	for (State_old state : stateArray) {
	            candidateVal = state.getReward();
	            
	            if(candidateVal > outVal) 
	            	outVal = candidateVal;
        	}
        }
        	
        return outVal;
    }
    
    // a single episode of q-learning given a model and start state
    void Q_LearnEpisode(State_old pState, double learnRate, double discount){ 
        State_old currentState = pState;
        State_old futureState;
        
        double r;
        if(currentState.isTerminal()){
            currentState.qSNone = currentState.getReward();
        }else{
            Action a = pState.getMaxQValdAct();
            double calcVal;
            double diff;
            double RPLUS = getMaxReward();
            //double RPLUS = 1000.0;
            
            double toChangeTo;
            while(currentState != null && !currentState.isTerminal()){

            a = funExplorationPolicy(currentState, currentState.getMaxQValdAct().getFreq(), RPLUS);
            //a = funExplorationPolicy(currentState, 1-currentState.getMaxQValdAct().getFreq(), RPLUS);
            //a = funExplorationPolicy(currentState, currentState.getMaxQValdAct().getFreq(), 1000);
            //a = infrequentFunExplorationPolicy(currentState, 5, 2);
            //a = funExplorationPolicy(currentState, .00001, RPLUS);
            //a = completelyRandomAction(currentState);
            
            currentState.incrementTimesVisted();
            a.calcFrequency(currentState.timesVisited);
            futureState = a.doAction();
            r = currentState.getReward();
            //
            
            if(futureState== null ||futureState.isTerminal()){
                diff = discount*futureState.getReward() - a.getQVal();
            }else{
                diff = discount*futureState.getMaxQValdAct().getQVal() - a.getQVal();
            }
            //
            //calcVal = learnRate*  (r +  diff); // this one converges
            //calcVal = learnRate* a.getFreq() * (r +  diff); //this one doesn't necessarily (maybe due to roundoff error?... seems unlikely)
            calcVal = learnRate* ((a.getFreq()+ 1)/2) * (r +  diff); //this definately converes and has good average time complexity but has HORRIBLE upper bounds
            //
            toChangeTo= calcVal + a.getQVal();
            a.setQVal(toChangeTo);
            // s,a,r,<-- s', argmax a' f(Q[s',a'], frequency[s',a']),r'
            currentState = futureState;
            }
        }
    }
  
    Action completelyRandomAction(State_old pState){
        int numberOfActions = pState.getActions().size();
        double randVal = Math.random();
        return pState.getActions().get((int)(((double)numberOfActions)* randVal));
    }
    
   static Action funExplorationPolicy(State_old pState, double explrThresh, double RPlus){
        Action retAct = pState.getActions().get(0);// initialize it to the first action
        Action candidate;// candidate action
        double candidateVal;// the value of the candidate
        double currentMax = retAct.getQVal();// initialize the currentMax to the value of the initial action
        for(int i = 0; i< pState.getActions().size(); i++){
            candidate = pState.getActions().get(i);
            if(candidate.getFreq() < explrThresh){// give it the highest if it hasnt occured as often as that thing
                candidateVal = RPlus;
                //System.out.println("EXPLORATION OCCURED");
            }else{
                candidateVal = candidate.getQVal();
                //System.out.println("FUN OCCURED");
            }
            if (candidateVal > currentMax) {
                retAct = candidate;
                currentMax = candidateVal;
            }
        }
        return retAct;
    }
   
   static Action infrequentFunExplorationPolicy(State_old pState, int explrThresh, double RPlus){
        Action retAct = pState.getActions().get(0);// initialize it to the first action
        Action candidate;// candidate action
        double candidateVal;// the value of the candidate
        double currentMax = retAct.getQVal();// initialize the currentMax to the value of the initial action
        for(int i = 0; i< pState.getActions().size(); i++){
            candidate = pState.getActions().get(i);
            if(candidate.timesDone < explrThresh){// give it the highest if it hasnt occured as often as that thing
                candidateVal = RPlus;
            }else{
                candidateVal = candidate.getQVal();
            }
            if (candidateVal > currentMax) {
                retAct = candidate;
                currentMax = candidateVal;
            }
        }
        return retAct;
    }
    void printQPolicy(){
    	for (State_old[] stateArray : states) {
        	for (State_old state : stateArray) {
	            System.out.print("At state " +'"'+ state.name + '"'+  " the policy is: ");
	            
	            if (!state.isTerminal()) {
	                System.out.println(state.getMaxQValdAct().name);
	            } else {
	                System.out.println("NOTHING BECAUSE ITS TERMINAL");
	            }
        	}
            
        }
    }
    
    void printQPolicyAndVal(){
    	for (State_old[] stateArray : states) {
        	for (State_old state : stateArray) {
	            System.out.print("At state " +'"'+ state.name + '"'+  " the policy is: ");
	            
	            if(!state.isTerminal()) {
	                System.out.println(state.getMaxQValdAct().name + " with value: " + state.getMaxQValdAct().getQVal());
	            } else {
	                System.out.println("NOTHING BECAUSE ITS TERMINAL");
	            }
        	}
            
        }
    }
    
    double compare_QValPol_and_TruePol(){
    	
    	if (states == null || states[0] == null)
    		return Integer.MIN_VALUE;
    	
        double diff;
        double maxDiff = Integer.MIN_VALUE;

        Action aP;
        Action aQ;
                
        for (State_old[] stateArray : states) 
        {
        	for (State_old state : stateArray) 
        	{
        		if (state == null) 
        			continue;
        		
	            if (!state.isTerminal()) {
	            	
	                aP = state.getBestAction();
	                aQ = state.getMaxQValdAct();
	                diff = Math.abs(aP.calculateExpectedValue() - aQ.calculateExpectedValue());
	                
	                if(aP == aQ) 
	                	diff = 0;
	            } else {
	                diff = 0;
	            }
	            
	            if(diff > maxDiff) 
	            {
	                maxDiff = diff;
	            }
        	}
        }
        //
        //System.out.println("The difference is: " + maxDiff + " and occured in state " + maxDiffState.name);
        return maxDiff;
    }
    
    int getSizeOfStateSpace(){
        return stateCount;
    }

    
    HashMap<State_old,ArrayList<Double>> Q_Learn_To_Beyond_Convergence_And_Record_Objective_Valuations(){
        
        long countOfWhile = 0;
        
        HashMap<State_old,ArrayList<Double>> ret = new HashMap<State_old,ArrayList<Double>>();
        
        for (State_old[] stateArray : states) {
        	for (State_old state : stateArray) {
        		ret.put(state, new ArrayList<Double>());
            }
    	}
        
        while(compare_QValPol_and_TruePol() > 0 || countOfWhile < 30){ // considered to have converged when the valuation difference is ==
            countOfWhile++;
            Q_LearnEpisode(states[0][0], .99, .99);
            
            //RECORD RESULTS
            for (State_old[] stateArray : states) {
            	for (State_old state : stateArray) {
            		if(!state.isTerminal()) 
            		{
            			ret.get(state).add(new Double(state.getMaxQValdAct().calculateExpectedValue()));
            		} else 
            		{
                    	ret.get(state).add(new Double(state.getReward()));
            		}
                }
            }

        }
        
        System.out.println("It took " + countOfWhile + " episodes to get it to converge.");
        
        for(int count=0; count < (1*countOfWhile); count++)
        {
            Q_LearnEpisode(states[0][0], .99, .99);
            
            for (State_old[] stateArray : states) {
            	for (State_old state : stateArray) {
            		if(!state.isTerminal()) 
            		{
            			ret.get(state).add(new Double(state.getMaxQValdAct().calculateExpectedValue()));
            		} else
            		{
            			ret.get(state).add(new Double(state.getReward()));
            		}
                }
            }

        }
        // RETURN RESULTS and display in main
        return ret;
    }
    
    HashMap<State_old,ArrayList<Double>> Q_Learn_To_Beyond_Convergence_And_Record_Q_Val_Results()
    {
    	HashMap<State_old,ArrayList<Double>> ret = new HashMap<State_old,ArrayList<Double>>();
        long countOfWhile = 0;
    	
    	for (State_old[] stateArray : states) {
        	for (State_old state : stateArray) {
        		ret.put(state, new ArrayList<Double>());
            }
    	}
    	        
        while(compare_QValPol_and_TruePol() >0/*|| countOfWhile < 50*/)
        {
            countOfWhile++;
            Q_LearnEpisode(states[0][0], .5, .99);
            
            //RECORD RESULTS
            for (State_old[] stateArray : states) {
            	for (State_old state : stateArray) {
	                if(!state.isTerminal()){
	                    ret.get(state).add(new Double(state.getMaxQValdAct().getQVal()));
	                }else{
	                    ret.get(state).add(new Double(state.getReward()));
	                }
            	}
            }

        }
            
        printQPolicy();
        
        System.out.println("It took " + countOfWhile + " episodes to get it to reach optimal policy (though more to technically converge).");
        
        for(int i =0; i < (4*countOfWhile); i++)
        {
            //alt_Q_LearnEpisode(stateSpace.get(7), .99, .99);
            Q_LearnEpisode(states[0][0], .5, .99);
            
            for (State_old[] stateArray : states) {
            	for (State_old state : stateArray) {
	                if(!state.isTerminal()){
	                    ret.get(state).add(new Double(state.getMaxQValdAct().getQVal()));
	                }else{
	                    ret.get(state).add(new Double(state.getReward()));
	                }
            	}
            }
        }
        // RETURN RESULTS and display in main
        return ret;
    }
    
    // The exploration ==> exploitation policy I mention in my report. Explores to the first occurence of optimal policy then switches to exploitation.
    void alt_Q_LearnEpisode(State_old pState, double learnRate, double discount){ // q learn given a model and start state
        State_old currentState = pState;
        State_old futureState;
        
        double r;
        if(currentState.isTerminal()){
            currentState.qSNone = currentState.getReward();
        }else{
            Action a = pState.getMaxQValdAct();
            double calcVal;
            double diff;
            double RPLUS = getMaxReward();
            
            double toChangeTo;
            while(currentState != null && !currentState.isTerminal()){

            
            a = funExplorationPolicy(currentState, 1-currentState.getMaxQValdAct().getFreq(), RPLUS);
            
            currentState.incrementTimesVisted();
            a.calcFrequency(currentState.timesVisited);
            futureState = a.doAction();
            r = currentState.getReward();// pretty sure this is supposed to be current not future as its indicated as r not r'
            //
            
            if(futureState== null ||futureState.isTerminal()){
                diff = discount*futureState.getReward() - a.getQVal();
            }else{
                diff = discount*futureState.getMaxQValdAct().getQVal() - a.getQVal();
            }
            //
            calcVal = learnRate* ((a.getFreq()+ 1)/2) * (r +  diff); //this definately converes and has good average time complexity but has HORRIBLE upper bounds
            //
            toChangeTo= calcVal + a.getQVal();
            a.setQVal(toChangeTo);
            // s,a,r,<-- s', argmax a' f(Q[s',a'], frequency[s',a']),r'
            currentState = futureState;
            }
        }
    }
}
