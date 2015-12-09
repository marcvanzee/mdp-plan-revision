package UNUSED.old;

import java.util.ArrayList;
// The class for actions (directly associated with states). Has a set of probabilistic moves called transitions.
public class Action {
    private ArrayList<Transition> transitions;
    String name;
    //
    double qVal;
    int timesDone;
    double freqDone;
    
    
    Action(String pName){
        timesDone = 0;
        transitions = new ArrayList<Transition>();
        name = pName;
        qVal = 0;
        freqDone = 0;
    }

    void addTransition(State_old pState){
        Transition current = new Transition(pState);
        transitions.add(current);
    }
    
    void addTransition(double pProb, State_old pState){
        Transition current = new Transition(pProb, pState);
        transitions.add(current);
    }
    
    public ArrayList<Transition> getTransitions(){
        return transitions;
    }
    
    double calculateExpectedValue(){
        double expVal;
        expVal = 0;
        for (int i = 0; i< transitions.size(); i++){
            expVal+= transitions.get(i).getProbWeightedVal();// get average summed expected reward
        }
        return expVal;
    }
    void print(){
        System.out.println("Action Name: " + name);
        for (int i = 0; i< transitions.size(); i++){
            transitions.get(i).print();
        }
    }
    void printLess(){
        System.out.println("\tAction Name: " + name);
    }
    //
    //
    //
    State_old doAction(){
        incrementActionCount();
        State_old landing = null;
        double randVal;
        randVal = (double) Math.random();
        double accumVal; // sums of the passed over probabilities
        accumVal =0;
        for(int i = 0; i < transitions.size(); i++ ){
            accumVal += transitions.get(i).getProb();
            if(accumVal > randVal || accumVal > 1){
                return transitions.get(i).getState();
            }
        }
        return landing;
    }
    
    void incrementActionCount(){
        timesDone++;
    }
    void setQVal( double pVal){
        qVal = pVal;
    }
    double getQVal(){
        return qVal;
    }
    int getTimesDone(){
        return timesDone;
    }
    
    void calcFrequency(int denominator){
        freqDone = ((double)timesDone)/ ((double)denominator);
    }
    double getFreq(){
        return freqDone;
    }
}
