package model.mdp;

import model.Operations;


public class QEdge extends Edge<QState,State> {
	double probability;
	double reward;
	
	public QEdge(QState from, State to, double probability, double reward) {
		super(from, to);
		this.probability = Operations.round(probability, 2);
		this.reward = Operations.round(reward, 2);
		
		System.out.println("storing p=" + this.probability);
	}
	
	public String toString() {
		return probability + " / " + (reward > 0 ? "+":"") + reward;
	}
	
	public double getProbability() {
		return probability;
	}
	
	public double getReward() {
		return reward;
	}

}
