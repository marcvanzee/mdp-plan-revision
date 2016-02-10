package gui.tileworld;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import constants.MathOperations;
import mdp.MDP;
import mdp.Tileworld;
import mdp.agent.Agent;
import mdp.agent.Angel;
import mdp.agent.LearningAgent;
import mdp.agent.ShortestPathAgent;
import mdp.elements.Action;
import mdp.elements.State;
import settings.TileworldSettings;

/**
 * This is the VIEWPOINT
 * 
 * @author marc.vanzee
 *
 */
public class TileworldPanel extends JPanel implements Observer
{	

	enum StateShape { SQUARE, CIRCLE };
	
	private static final long serialVersionUID = -5345319851341875800L;
	    	
	private final Tileworld tileworld;
	private boolean hasStarted = false;
	
	private double maxX, maxY, 
				wStep, hStep,
				d;
	
	private BufferedImage image;
	
	public TileworldPanel(MDP tileworld) 
	{
		this.tileworld = (Tileworld) tileworld;
		
		// set a preferred size for the custom panel.
		setPreferredSize(new Dimension(400,250));
			
        setLayout(new BorderLayout());
        setSize(new Dimension(600, 600));
        
        try {                
        	image = ImageIO.read(new File("images/agent.jpg"));
        } catch (IOException ex) { 
        	System.err.println("Failed loading image"); 
        }
    }

    /**
     * Receive messages, type is defined in MessageType
     */
    @Override
	public void update(Observable m, Object type) 
    {
    	hasStarted = true;
    	    	
    	repaint();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        setDimensions();
        drawBackground(g);
                
        if (this.hasStarted && tileworld != null && tileworld.countStates() > 0)
        	drawTileworld(g);
    }
    
    private void setDimensions() 
    {
        Dimension dim = getSize();
        
        maxX = (double) dim.width-1;
        maxY = (double) dim.height-1;
        
        d = (double) TileworldSettings.WORLD_SIZE;
    }
    
    private void drawTileworld(Graphics g) 
    {
    	drawGrid(g);
    	drawElements(g);
    	
    	switch (TileworldSettings.ALGORITHM) {
    	case VALUE_ITERATION:
    		drawPolicy(g);
    		drawStateValues(g);
    		break;
    	case SHORTEST_PATH: 
    	case LEARNING:
    		drawPlan(g);
    		break;
    	case ANGELIC:
    		drawPlan(g);
    		break;
    	}
    	
    }
    
    private void drawBackground(Graphics g) 
    {
    	 g.setColor(Color.WHITE);
         g.fillRect(0, 0, (int) maxX-1, (int) maxY-1);
    }
    
    private void drawGrid(Graphics g)
    {
    	wStep = maxX / d;
    	hStep = maxY / d;
    	
    	g.setColor(Color.BLACK);
    	
    	for (double i=1; i<=d; i++) 
    	{
    		int xCoord = (int) (i*wStep),
    				yCoord = (int) (i*hStep);
    		
    		g.drawLine(0, yCoord, (int) maxX, yCoord); // horizontal lines
    		g.drawLine(xCoord, 0, xCoord, (int) maxY); // vertical lines
    	}
    }
    
    private void drawElements(Graphics g) {
    	drawObstacles(g);
    	drawHoles(g);
    	
    	if (tileworld.getAgent().getCurrentState() != null)
    		drawAgent(g);
    }
    
    private void drawObstacles(Graphics g) 
    {
    	drawStates(tileworld.getObstacles(), Color.DARK_GRAY, StateShape.SQUARE, g);
    }
    
    private void drawHoles(Graphics g)
    {
    	drawStates(tileworld.getHoles(), Color.GREEN, StateShape.CIRCLE, g);
    	drawStatesValues(tileworld.getHoles(), g);
    }
    
    private void drawPolicy(Graphics g)
    {
    	g.setColor(Color.RED);
    	
    	Map<State,State> statePolicy = tileworld.getStatePolicy();
    	
    	for (State s : tileworld.getStates())
    	{
    		if (statePolicy.containsKey(s)) {
	    		State s2 = statePolicy.get(s);
	    		
	    		if (s.isObstacle() || s2 == null || s2.isObstacle())
	    			continue;
	    		
	    		int x1, x2, y1, y2;
	    		
	    		double arr = 6.0;
	    		
	    		if (s2.getX() < s.getX()) 
	    		{
	    			// arrow to the left
	    			x1 = (int) (s.getX() * wStep + wStep/arr);
	    			y1 = y2 = (int) (s.getY() * hStep + hStep/2);
	    			x2 = (int) (s.getX() * wStep - wStep/arr);
	    		} 
	    		else if (s2.getX() > s.getX()) 
	    		{ 
	    			// arrow to the right
	    			x1 = (int) ((s.getX()+1) * wStep - wStep/arr);
	    			y1 = y2 = (int) (s.getY() * hStep + hStep/2);
	    			x2 = (int) ((s.getX()+1) * wStep + wStep/arr);
	    		} 
	    		else if (s2.getY() < s.getY()) 
	    		{
	    			// arrow above
	    			x1 = x2 = (int) (s.getX() * wStep + wStep/2);
	    			y1 = (int) (s.getY() * hStep + hStep/arr);
	    			y2 = (int) (s.getY() * hStep - hStep/arr);
	    		}
	    		else
	    		{
	    			// arrow below
	    			x1 = x2 = (int) (s.getX() * wStep + wStep/2);
	    			y1 = (int) ((s.getY()+1) * hStep - hStep/arr);
	    			y2 = (int) ((s.getY()+1) * hStep + hStep/arr);
	    		}
	    			    		
	    		drawArrowLine(g, x1, y1, x2, y2, (int)(hStep/15.0), (int)(hStep/15.0));
    		}
    		
    	}
    }
    
    private void drawStateValues(Graphics g)
    {
    	g.setColor(Color.DARK_GRAY);
    	
    	for (State s : tileworld.getStates())
    	{
    		if (s.isObstacle())
    			continue;
    		
    		int x = (int) (s.getX() * wStep + wStep/4),
    				y = (int) (s.getY() * hStep + hStep/2);
    		
    		g.setFont(new Font("Verdana", 0, 10));
    		double value = MathOperations.round(s.getValue(), 2);
    		g.drawString(value+"", x, y);
    		
    	}
    }
    
    private void drawStatesValues(List<State> states, Graphics g)
    {
    	g.setColor(Color.DARK_GRAY);
    	
    	for (State s : states)
    	{
    		if (s.isObstacle())
    			continue;
    		
    		int x = (int) (s.getX() * wStep + wStep/4),
    				y = (int) (s.getY() * hStep + hStep/2);
    		
    		g.setFont(new Font("Verdana", 0, 10));
    		double value = MathOperations.round(s.getReward(), 2);
    		g.drawString(value+"/"+s.getLifetime(), x, y);
    		
    	}
    }
    
    private void drawAgent(Graphics g)
    {
    	State s = tileworld.getAgent().getCurrentState();
 
    	int x = s.getX(), y = s.getY();
    	
    	g.drawImage(image,(int)(x*wStep+wStep/10), (int)(y*hStep+hStep/10), (int) (wStep-2*wStep/10), (int) (hStep-2*hStep/10), null);
    }
    
    
    
    private void drawStates(List<State> states, Color c, StateShape shape, Graphics g)
    {
    	g.setColor(c);
    	
    	for (State s : states) {
    		int x = s.getX(),
    				y = s.getY();
    		
    		switch (shape) {
    		case SQUARE: fillSquare(g, x, y); break;
    		case CIRCLE: fillCircle(g, x, y); break;
    		}
    	}
    } 
   
    private void fillSquare(Graphics g, int x, int y) {
		g.fillRect((int)(x*wStep), (int)(y*hStep), (int) wStep, (int) hStep);
    }
    
    private void fillCircle(Graphics g, int x, int y) {
		g.fillOval((int)(x*wStep), (int)(y*hStep), (int) wStep, (int) hStep);
    }   
    
    /**
     * Draw an arrow line between two point 
     * @param g the graphic component
     * @param x1 x-position of first point
     * @param y1 y-position of first point
     * @param x2 x-position of second point
     * @param y2 y-position of second point
     * @param d  the width of the arrow
     * @param h  the height of the arrow
     */
    private void drawArrowLine(Graphics g, int x1, int y1, int x2, int y2, int d, int h){
    	d = Math.max(d, 3);
    	h = Math.max(h, 3);
       int dx = x2 - x1, dy = y2 - y1;
       double D = Math.sqrt(dx*dx + dy*dy);
       double xm = D - d, xn = xm, ym = h, yn = -h, x;
       double sin = dy/D, cos = dx/D;

       x = xm*cos - ym*sin + x1;
       ym = xm*sin + ym*cos + y1;
       xm = x;

       x = xn*cos - yn*sin + x1;
       yn = xn*sin + yn*cos + y1;
       xn = x;

       int[] xpoints = {x2, (int) xm, (int) xn};
       int[] ypoints = {y2, (int) ym, (int) yn};

       g.drawLine(x1, y1, x2, y2);
       g.fillPolygon(xpoints, ypoints, 3);
    }
    
    private void drawPlan(Graphics g) 
    {
    	g.setColor(Color.RED);
    	Agent agent = tileworld.getAgent();
    	State s = agent.getCurrentState();
    	LinkedList<Action> plan = (agent instanceof ShortestPathAgent ? ((ShortestPathAgent)agent).getPlan() : 
    		(agent instanceof LearningAgent ? ((LearningAgent)agent).getPlan() : ((Angel)agent).getPlan()));
    	
    	if (plan == null || plan.isEmpty())
    		return;
    	
    	int x = s.getX()*(int)wStep + (int)(wStep/2.0), 
    			y = s.getY()*(int)hStep + (int)(hStep/2.0);
    	
    	for (Action a : plan) 
    	{
    		int newX = x, newY = y;
    		
    		switch (a.getName()) {
    		case "up":
    			newY = y - (int)hStep;
    			break;
    		case "down":
    			newY = y + (int)hStep;
    			break;
    		case "left":
    			newX = x - (int)wStep;
    			break;
    		case "right":
    			newX = x + (int)wStep;
    			break;	
    		}
    		g.drawLine(x, y, newX, newY);
    		x = newX;
    		y = newY;
    	}
    }
}