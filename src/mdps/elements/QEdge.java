package mdps.elements;

import constants.MathOperations;


public class QEdge extends Edge<QState,State> {
	double probability;
	double reward;
	
	public QEdge(QState from, State to, double probability, double reward) {
		super(from, to);
		this.probability = MathOperations.round(probability, 2);
		this.reward = MathOperations.round(reward, 2);
	}
	
	public String toString() {
		return "p="+probability + ", r=" + (reward > 0 ? "+":"") + reward;
	}
	
	public double getProbability() {
		return probability;
	}
	
	public double getReward() {
		return reward;
	}
	
	public void setReward(double reward) {
		this.reward = reward;
	}

}