package solver;

// The probability given an action that an agent will reach a specified destinaion state.
public class Transition {
    private State stateReached;
    private int timesPathTaken;
    double trueProbability;
    
    
    Transition(State pState){
        stateReached = pState;
        trueProbability = 0;
        //
        timesPathTaken=0;
    }
    Transition(double pProb, State pState){
        stateReached = pState;
        trueProbability = pProb;
        //
        timesPathTaken=0;
    }
    double getProb(){
        return trueProbability;
    }
    State getState(){
        return stateReached;
    }
    double getProbWeightedVal(){// get freqTaken weighted val
        return trueProbability * stateReached.getPreVal();
    }
    void print(){
        System.out.println("Probability: " + trueProbability + " " + stateReached.name);
    }

    

}
