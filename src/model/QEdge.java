package model;

import constants.Operations;

public class QEdge extends Edge<QState,State> {
	double probability;
	double reward;
	
	public QEdge(QState from, State to, double probability, double reward) {
		super(from, to);
		this.probability = Operations.round(probability, 2);
		this.reward = Operations.round(reward, 2);
	}
	
	public String toString() {
		return probability + " / " + (reward > 0 ? "+":"") + reward;
	}

}
