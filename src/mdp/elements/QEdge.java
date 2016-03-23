package mdp.elements;

import constants.MathOperations;


public class QEdge extends Edge<QState,State> {
	double probability;
	
	public QEdge(QState from, State to, double probability) {
		super(from, to);
		this.probability = MathOperations.round(probability, 2);
	}
	
	public String toString() {
		return "QEDge: p="+probability;
	}
	
	public double getProbability() {
		return probability;
	}
}
