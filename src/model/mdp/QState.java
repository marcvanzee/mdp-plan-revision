package model.mdp;

import java.awt.Color;

public class QState extends Vertex<QEdge> 
{
	public QState() {
	}
	
	public QState(String newName) {
		super(newName);
		this.color = Color.BLUE;
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 20;
	}
}
