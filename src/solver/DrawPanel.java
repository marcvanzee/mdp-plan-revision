package solver;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

class DrawPanel extends JPanel {
	
	Model model;
	State[][] states;
	Agent agent = null;
	
	DrawPanel() {
		// set a preferred size for the custom panel.
		setPreferredSize(new Dimension(400,250));
		setBackground(new java.awt.Color(255, 255, 255));
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D)g;
		g2.setPaint(Color.black);
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		int maxX = Parameters.maxX, maxY = Parameters.maxY;
		
		// we lead out PAD space free around the edges
		int w = getWidth();
        int h = getHeight();
        
        int hStep = w / (maxX+1);
        int vStep = h / (maxY+1);
        
        int r = ((int)((w < h ? hStep : vStep) / 2));
        
        int hSpace = (w - r * maxX) / (maxX + 1);
        int vSpace = (h - r * maxY) / (maxY + 1);
        
        if (states == null || states.length == 0)
        	return;
        
        // iterate over all states and draw them and arrows at the appropriate places
        for (int i=0; i<states.length; i++) {
        	if (states[i] == null)
        		continue;
        	
        	for (int j=0; j<states[i].length; j++) {
        		State s = states[i][j];
        		if (s == null)
        			continue;
        		
        		int x = computeCoordinate(hSpace,i,r);
        		int y = computeCoordinate(vSpace,j,r);
        		g2.drawOval(x, y, r, r);
        		String name = s.name;
        		g2.drawString(name, x + r/2 - name.length()*2, y + r/2 + 2);
        		
        		// now check transitions
        		// go over all actions
        		for (Action a : s.getActions()) 
        		{
        			// go over all transitions
        			for (Transition t : a.getTransitions()) 
        			{
        				// draw a line from the current state to the other state
        				int x2 = computeCoordinate(hSpace,t.getState().x,r);
        				int y2 = computeCoordinate(vSpace,t.getState().y,r);
        				
        				g2.drawLine(x, y, x2, y2);
        			}
        		}
        	}
        }
        
        // color the location of the agent
        if (agent != null) {
        	g2.setColor(Color.RED);
        	int agentX = computeCoordinate(hSpace,agent.x,r);
			int agentY = computeCoordinate(vSpace,agent.y,r);
        	g2.fillOval(agentX, agentY, r, r);
        }
    }
	
	public void setAgent(Agent a) {
		this.agent = a;
		repaint();
	}
	
	public void updateModel(Model m) {
		model = m;
		
		if (model.getSizeOfStateSpace() == 0) 
			return;
		
		// save states in an array so we can visualize it as a grid
		states = model.getStates();
		
		System.out.println("states: " + states.length);
		
		agent = null;
		repaint();
	}
	
	private int computeCoordinate(int spacing, int index, int radius) {
		return spacing * (index+1) + radius*index;
	}
	
}