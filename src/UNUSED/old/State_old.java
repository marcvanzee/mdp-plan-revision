package UNUSED.old;

import java.util.ArrayList;

// A class for single states in the state space. Each state has its own possible actions.
public class State_old {
    private double reward;
    private double curVal;//currentValue
    private double preVal;// previous value
    private ArrayList<Action> actions; // possible actions to take here
    private Action policyParticle;//what action should be taken at this state
    public String name;
    public int x, y;
    
    public double qSNone;// the value of its lacking an action
    int timesVisited;
    
    State_old() {
        curVal = 0;
        actions = new ArrayList<Action>();
        policyParticle = null;
        qSNone = 0;
        timesVisited = 0;
    }
    
    State_old(double pReward, String pName, int x, int y){
        reward = pReward;
        curVal = reward;
        actions = new ArrayList<Action>();
        name = pName;
        policyParticle = null;
        qSNone = 0;
        timesVisited = 0;
        this.x = x;
        this.y = y;
    }
    
    State_old(double pReward, double pHeurVal, String pName){// if you want to give i an initial heuristic value
        reward = pReward;
        curVal = pHeurVal;//kkk
        actions = new ArrayList<Action>();
        name = pName;
        policyParticle = null;
        timesVisited = 0;
    }
    
    ArrayList<Action> getActions(){
        return actions;
    }
    
    double getReward(){
        return reward;
    }
    
    double getCurVal(){
        return curVal;
    }
    double getPreVal(){
        return preVal;
    }
    void changeEpoch(){
        preVal = curVal;
    }
    Action getBestAction(){
        if(actions == null || actions.isEmpty()) return null;
        Action BestAction = policyParticle;
        for(int i = 0; i< actions.size();  i++){
            if(actions.get(i).calculateExpectedValue()> BestAction.calculateExpectedValue()){
                BestAction = actions.get(i);
            }
        }
        return BestAction;
    }
    boolean updatePolicy(){
        Action possible = getBestAction();
        if(possible!= policyParticle){
            policyParticle = getBestAction();
            return true;// IF POLICY DOES CHANGE RETURN TRUE
        }
        return false;
    }
    
    boolean updateValueAndPolicy(double discRate){
        boolean retVal;
        retVal = updatePolicy();
        if(policyParticle== null){
            curVal = reward;
        }else{   
            curVal = reward +( discRate * policyParticle.calculateExpectedValue());
        }
        return retVal;
    }
    
    void updateValueOnly(double discRate){
        if(policyParticle== null){
            curVal = reward;
        }else{   
            curVal = reward +( discRate * policyParticle.calculateExpectedValue());
        }
    }
    
    double getDiff(){
        return preVal - curVal;
    }
    void addAction(Action pAct){
        actions.add(pAct);
        if(policyParticle == null){// If its the first action added make it the policy to avoid nullpointers
            policyParticle = pAct;
        }
    }
    void print(){
        System.out.println("State Name: "+ name + " Value: " + curVal);
        for(int i = 0; i< actions.size();  i++){
            actions.get(i).print();
        }
    }
    void printPolicy(){
        System.out.println("Policy at state " + name + " is ");
        if (policyParticle != null) {
            
            policyParticle.printLess();
        }else{
            System.out.println("\tGoal/Sink State");
        }
        System.out.println("\tThe value of the state is " + curVal);
    }
    //
    //
    //
    boolean isTerminal(){
        if(actions == null || actions.isEmpty()){
            return true;
        }
        return false;
    }
    
    Action getMaxQValdAct(){
        Action maxQValdAct = actions.get(0);
        double valToBeat = maxQValdAct.qVal;
        double contender;
        for(int i = 1; i< actions.size(); i++){
            contender = actions.get(i).qVal;
            if( contender > valToBeat){
                valToBeat = contender;
                maxQValdAct = actions.get(i);
            }
        }
        return maxQValdAct;
    }
    
    
    void incrementTimesVisted(){
        timesVisited++;
    }
}
