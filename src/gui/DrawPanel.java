package gui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JPanel;

import messaging.MessageType;
import model.Model;
import model.mdp.ActionEdge;
import model.mdp.Edge;
import model.mdp.MDP;
import model.mdp.QEdge;
import model.mdp.QState;
import model.mdp.State;
import model.mdp.Vertex;

import org.apache.commons.collections15.Transformer;

import constants.GUIConstants;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.KKLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.SpringLayout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.algorithms.layout.util.Relaxer;
import edu.uci.ics.jung.algorithms.layout.util.VisRunner;
import edu.uci.ics.jung.algorithms.util.IterativeContext;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.ObservableGraph;
import edu.uci.ics.jung.graph.util.Graphs;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.AbstractEdgeShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.layout.LayoutTransition;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.util.Animator;

/**
 * This is the VIEWPOINT
 * 
 * @author marc.vanzee
 *
 */
class DrawPanel extends JPanel implements Observer 
{	
	private static final long serialVersionUID = -5345319851341875800L;
	
	private VisualizationViewer<Vertex<?>,Edge<?,?>> vv = null;
	private AbstractLayout<Vertex<?>,Edge<?,?>> layout = null;
	private Graph<Vertex<?>,Edge<?,?>> g = null;
	SpringLayout<Vertex<?>, Edge<?,?>> springLayout;
	
	private Model model;
	
	boolean animate = true;
	
	DrawPanel() {
		// set a preferred size for the custom panel.
		setPreferredSize(new Dimension(400,250));
		init();
	}
	
    public void init()
    {
    	 //create a graph
    	Graph<Vertex<?>,Edge<?,?>> ig = Graphs.<Vertex<?>,Edge<?,?>>synchronizedDirectedGraph(new DirectedSparseMultigraph<Vertex<?>,Edge<?,?>>());

    	ObservableGraph<Vertex<?>,Edge<?,?>> og = new ObservableGraph<Vertex<?>,Edge<?,?>>(ig);
     
        this.g = og;
        
        //create a graphdraw
        layout = new KKLayout<Vertex<?>,Edge<?,?>>(g);
        layout.setSize(new Dimension(600,600));
		Relaxer relaxer = new VisRunner((IterativeContext)layout);
		relaxer.stop();
		relaxer.prerelax();
		
		Layout<Vertex<?>,Edge<?,?>> staticLayout = new StaticLayout<Vertex<?>,Edge<?,?>>(g, layout);

        vv = new VisualizationViewer<Vertex<?>,Edge<?,?>>(staticLayout, new Dimension(600,600));

        setLayout(new BorderLayout());
        setSize(new Dimension(2000, 2000));

        vv.setGraphMouse(new DefaultModalGraphMouse<Vertex<?>,Edge<?,?>>());

        vv.getRenderContext().setVertexShapeTransformer(new VertexShapeFunction());
        
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Vertex<?>>() {
        	@Override
        	public String transform(Vertex<?> v) 
        	{
        		return v.getName();
        	}
        });
        
        vv.getRenderContext().setVertexFillPaintTransformer(new Transformer<Vertex<?>,Paint>() {
            public Paint transform(Vertex<?> v) {
                return (v instanceof State ? Color.RED : Color.BLUE);
            }
        }  );
            
        vv.getRenderContext().setEdgeDrawPaintTransformer(new Transformer<Edge<?,?>,Paint>() {
        	public Paint transform(Edge<?,?> e) 
        	{
        		return (e.isOptimal() ? Color.GREEN : Color.BLACK); 
        	}
        });
        
        Transformer<Edge<?,?>, Stroke> edgeStrokeTransformer = new Transformer<Edge<?,?>, Stroke>() {
        	public Stroke transform(Edge<?,?> s) {
        		float d = s.isOptimal() ? 10.0f : 1.0f;
        		return new BasicStroke(d);
        	}
        };
        
        vv.getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);

        vv.getRenderContext().setEdgeLabelTransformer(new Transformer<Edge<?,?>,String>(){
            public String transform(Edge<?,?> e) 
            {
            	return e.toString();
            }
        });
        
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<Vertex<?>,Edge<?,?>>());        
        
        AbstractEdgeShapeTransformer<Vertex<?>,Edge<?,?>> aesf = 
                (AbstractEdgeShapeTransformer<Vertex<?>,Edge<?,?>>)vv.getRenderContext().getEdgeShapeTransformer();
            aesf.setControlOffsetIncrement(30);
        
        
        vv.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				super.componentResized(arg0);
				layout.setSize(arg0.getComponent().getSize());
			}});

        vv.setBackground(Color.WHITE);
        add(vv);
   }

    /**
     * Receive messages, type is defined in MessageType
     */
    @Override
	public void update(Observable model, Object type) 
    {
    	if (!(type instanceof Integer)) 
    	{
    		System.out.println("Unidentified messagetype: " + type.toString());
    		return;
    	}
    	
    	switch ((int) type) {
    	case MessageType.REFRESH_MDP: repaint(); break;
    	case MessageType.RELOAD_MDP: 
    		if (!(model instanceof Model))
    		{
    			System.out.println("Not a model for updating");
    			return;
    		}
    		load((Model) model);
    		break;
    	}
    }
    
    /**
     * Load a new model
     * 
     * @param newModel
     */
    public void load(Model newModel) 
    {
    	this.model = newModel;
    	
    	clearGraph();
    	
		try 
		{
    		 MDP mdp = this.model.getMDP();
    		 
    		 for (State s : mdp.getStates()) {
    			 g.addVertex(s);
    		 }
    		 
    		 for (QState qs : mdp.getQStates()) {
    			g.addVertex(qs);
    		 }
    		 
    		 for (ActionEdge ae : mdp.getActionEdges()) {
    			 g.addEdge(ae, ae.getFromVertex(), ae.getToVertex());
    			
    		 }
    		 
    		 for (QEdge qe : mdp.getQEdges()) {
    			 g.addEdge(qe, qe.getFromVertex(), qe.getToVertex());
    		 }
    		 
        	 springLayout = new SpringLayout<Vertex<?>, Edge<?,?>>(g);
             
        	 vv.setSize(new Dimension(2000, 2000));
             springLayout.setInitializer(vv.getGraphLayout());
             springLayout.setSize(vv.getSize());
             
             LayoutTransition<Vertex<?>, Edge<?,?>> lt =
					new LayoutTransition<Vertex<?>, Edge<?,?>>(vv, vv.getGraphLayout(), springLayout);
             
             Animator animator = new Animator(lt);
             animator.start();
             vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
			
             springLayout.lock(animate);
             
             vv.repaint();
				
         } catch (Exception e) {
             e.printStackTrace();
         }	
    }
    
    public void toggleAnimate() {
    	animate = !animate;
    	
    	if (springLayout != null)
    		springLayout.lock(animate);
    }
    
    
    
    
    /**
     * Remove all vertices and edges
     */
    private void clearGraph() 
    {
    	if (g.getVertexCount() > 0)
    	{

    		ArrayList<Vertex<?>> vertices = new ArrayList<Vertex<?>>(g.getVertices());
    		for (Vertex<?> v : vertices) {
    			g.removeVertex(v);
    		}
    	}
    	if (g.getEdgeCount() > 0) 
    	{
    		ArrayList<Edge<?,?>> edges = new ArrayList<Edge<?,?>>(g.getEdges());
    		for (Edge<?,?> e : edges) {
    			g.removeEdge(e);
    		}
    	}
    }
    
    public void setEdgeType(int type) 
    {
    	if (type == GUIConstants.STRAIGHT_EDGES) {
    		vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<Vertex<?>,Edge<?,?>>());
    	} else {
    		vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.QuadCurve<Vertex<?>,Edge<?,?>>());
    	}
    	vv.repaint();
    }
        
    class VertexShapeFunction extends EllipseVertexShapeTransformer<Vertex<?>> 
    {
        public VertexShapeFunction() 
        {
            setSizeTransformer(new Transformer<Vertex<?>,Integer>() {
        		public Integer transform(Vertex<?> v) 
        		{
                	return (v instanceof State ? 20 : 20);
                }
            });
        }
        
        @Override
        public Shape transform(Vertex<?> v) {
        	        	
            if (v instanceof State) {
            	return factory.getEllipse(v);
            }
            
            else {
               return factory.getRegularPolygon(v, 3);
            }
        }
    }
}