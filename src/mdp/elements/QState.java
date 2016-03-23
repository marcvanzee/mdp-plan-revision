package mdp.elements;

import java.awt.Color;

public class QState extends Vertex<QEdge> 
{
	public QState() {
		this.color = Color.BLUE;
	}

	@Override
	public int getSize() {
		// TODO Auto-generated method stub
		return 20;
	}
}
