package UNUSED.old;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;

import org.jgraph.JGraph;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.DirectedGraph;
import org.jgrapht.ListenableGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultListenableGraph;
import org.jgrapht.graph.DirectedMultigraph;

import settings.GeneralMDPSettings;

class DrawPanel_old extends JPanel {
	
	Model model;
	State_old[][] states;
	Agent agent = null;
	
    private static final long serialVersionUID = 3256444702936019250L;
    private static final Color DEFAULT_BG_COLOR = Color.decode("#FAFBFF");
    private static final Dimension DEFAULT_SIZE = new Dimension(530, 320);

    private JGraphModelAdapter<String, DefaultEdge> jgAdapter;
	
	DrawPanel_old() {
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
		
		int maxX = 0, maxY = 0; // this is wrong
		
		
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
        		State_old s = states[i][j];
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
	
	/**
     * {@inheritDoc}
     */
    public void init()
    {
        // create a JGraphT graph
        ListenableGraph<String, DefaultEdge> g =
            new ListenableDirectedMultigraph<String, DefaultEdge>(
                DefaultEdge.class);

        // create a visualization using JGraph, via an adapter
        jgAdapter = new JGraphModelAdapter<String, DefaultEdge>(g);

        JGraph jgraph = new JGraph(jgAdapter);

        this.add(jgraph);
        resize(DEFAULT_SIZE);

        String v1 = "v1";
        String v2 = "v2";
        String v3 = "v3";
        String v4 = "v4";

        // add some sample data (graph manipulated via JGraphT)
        g.addVertex(v1);
        g.addVertex(v2);
        g.addVertex(v3);
        g.addVertex(v4);

        g.addEdge(v1, v2);
        g.addEdge(v2, v3);
        g.addEdge(v3, v1);
        g.addEdge(v4, v3);

        // position vertices nicely within JGraph component
        positionVertexAt(v1, 130, 40);
        positionVertexAt(v2, 60, 200);
        positionVertexAt(v3, 310, 230);
        positionVertexAt(v4, 380, 70);

        // that's all there is to it!...
    }

    @SuppressWarnings("unchecked") // FIXME hb 28-nov-05: See FIXME below
    private void positionVertexAt(Object vertex, int x, int y)
    {
        DefaultGraphCell cell = jgAdapter.getVertexCell(vertex);
        AttributeMap attr = cell.getAttributes();
        Rectangle2D bounds = GraphConstants.getBounds(attr);

        Rectangle2D newBounds =
            new Rectangle2D.Double(
                x,
                y,
                bounds.getWidth(),
                bounds.getHeight());

        GraphConstants.setBounds(attr, newBounds);

        // TODO: Clean up generics once JGraph goes generic
        AttributeMap cellAttr = new AttributeMap();
        cellAttr.put(cell, attr);
        jgAdapter.edit(cellAttr, null, null, null);
    }

    

    /**
     * a listenable directed multigraph that allows loops and parallel edges.
     */
    private static class ListenableDirectedMultigraph<V, E>
        extends DefaultListenableGraph<V, E>
        implements DirectedGraph<V, E>
    {
        private static final long serialVersionUID = 1L;

        ListenableDirectedMultigraph(Class<E> edgeClass)
        {
            super(new DirectedMultigraph<V, E>(edgeClass));
        }
    }
	
}