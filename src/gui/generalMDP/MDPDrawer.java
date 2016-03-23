package gui.generalMDP;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JPanel;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.algorithms.layout.util.VisRunner;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.util.Graphs;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.util.Animator;
import mdp.elements.Edge;
import mdp.elements.Vertex;
import messaging.jung.ChangeMessage;
import messaging.jung.ChangeMessageBuffer;
import messaging.jung.ClearGraphMessage;
import settings.GeneralMDPSettings;
import simulations.MDPSimulation;

/**
 * This is the VIEWPOINT
 * 
 * @author marc.vanzee
 *
 */
public class MDPDrawer extends JPanel implements Observer 
{	

	private static final long serialVersionUID = -5345319851341875800L;
	
	private final Graph<Vertex<?>,Edge<?,?>> ig = 
			Graphs.<Vertex<?>,Edge<?,?>>synchronizedDirectedGraph(new DirectedSparseMultigraph<Vertex<?>,Edge<?,?>>());

    private final Graph<Vertex<?>,Edge<?,?>> g = new ObservableGraph<Vertex<?>,Edge<?,?>>(ig);;
    
    private final AbstractLayout<Vertex<?>,Edge<?,?>> layout = new KKLayout<Vertex<?>,Edge<?,?>>(g);
	
    private final Layout<Vertex<?>,Edge<?,?>> staticLayout = new StaticLayout<Vertex<?>,Edge<?,?>>(g, layout);
    
    private final VisualizationViewer<Vertex<?>,Edge<?,?>> vv = new JUNGRendering(layout, staticLayout);
    	
	private final MDPGUI mdpGUI;
	private final MDPSimulation simulation;
	
	private boolean animate = true;
	
	private DrawTaskScheduler taskScheduler = new DrawTaskScheduler(this);
	
	public MDPDrawer(MDPGUI mdpGUI) 
	{
		this.mdpGUI = mdpGUI;
		this.simulation = mdpGUI.getSimulation();
		// set a preferred size for the custom panel.
		setPreferredSize(new Dimension(400,250));
		
		//create a graphdraw
        layout.setSize(new Dimension(600,600));
			
        setLayout(new BorderLayout());
        setSize(new Dimension(600, 600));

        add(vv);
        
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.QuadCurve<Vertex<?>,Edge<?,?>>());
        
        vv.repaint();
   }

    /**
     * Receive messages, type is defined in MessageType
     */
    @Override
	public void update(Observable m, Object type) 
    {
    	// schedule to execute the message by adding it to the queue
    	taskScheduler.add((ChangeMessageBuffer)type);
    }
        
    public void toggleAnimate() {
    	animate = !animate;
    	
    	if (layout != null)
    		layout.lock(animate);
    }
    
    /**
     * Remove all vertices and edges
     */
    public void clearGraph() 
    {
    	ChangeMessage gc = new ClearGraphMessage();
    	taskScheduler.add(gc);
    }
      
    public int countVertices() {
    	return g.getVertexCount();
    }
    
    /**
     * Callback method for the Task Scheduler
     * 
     * @param change
     */
    public void changeGraph(ChangeMessage change) 
    {
    	change.modifyGraph(g);
		
    	//Agent ag = this.simulation.getAgent();
    	
    	//if (ag != null)
    	//{
    		//mainApp.textFieldRewards.setText(model.getAgent().getReward()+"");
    		//mainApp.textFieldDeliberations.setText(model.getAgent().getDeliberations()+"");
    		//mainApp.textFieldActs.setText(model.getAgent().getActs()+"");
    	//}
    	
    	//mdpGUI.textFieldSteps.setText(simulation.getSteps()+"");
    	
    	animate();
    }
    
    public DrawTaskScheduler getTaskScheduler() {
    	return taskScheduler;
    }
    
    private void animate()
    {
    	// while we are animating, the task scheduler is not allowed to modify the graph
    	// otherwise we will have a ConcurrentModificationException, because this animation iterates
    	// over the vertices and edges, while the task scheduler thread modifies them.
    	
    	layout.initialize();

    	if (GeneralMDPSettings.ANIMATE) {
	    	Relaxer relaxer = new VisRunner((IterativeContext)layout);
			relaxer.stop();
			relaxer.prerelax();
			
			StaticLayout<Vertex<?>,Edge<?,?>> staticLayout =
 			new StaticLayout<Vertex<?>,Edge<?,?>>(g, layout);
			LayoutTransition<Vertex<?>,Edge<?,?>> lt =
				new LayoutTransition<Vertex<?>,Edge<?,?>>(vv, vv.getGraphLayout(),
						staticLayout);
		
			Animator animator = new Animator(lt);
			animator.start();
    	}
    	
		vv.repaint();
    	repaint();
    	
    	// resume scheduling after 200ms, give the visualization some time to draw before editing the graph again
    	resumeTaskScheduler(GeneralMDPSettings.REPAINT_DELAY-100); 
    }
    
    private void resumeTaskScheduler(int ms) {
    	(new Timer()).schedule(new TimerTask() {
    		public void run() {
    			taskScheduler.setWait(false);
    		}
    	}, ms);
    }
}