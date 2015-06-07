package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JPanel;

import model.ActionEdge;
import model.Edge;
import model.MDP;
import model.Model;
import model.QEdge;
import model.QState;
import model.State;
import model.Vertex;

import org.apache.commons.collections15.Transformer;

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
import edu.uci.ics.jung.visualization.decorators.PickableEdgePaintTransformer;
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
class DrawPanel extends JPanel implements Observer {
	
	private static final long serialVersionUID = -5345319851341875800L;
	
	private VisualizationViewer<Vertex<?>,Edge<?,?>> vv = null;
	private AbstractLayout<Vertex<?>,Edge<?,?>> layout = null;
	private Graph<Vertex<?>,Edge<?,?>> g = null;
	
	Timer timer;
	boolean done;
	protected JButton switchLayout;
	
	Model model;
	
	DrawPanel() {
		// set a preferred size for the custom panel.
		setPreferredSize(new Dimension(400,250));
		
		System.out.println(this.getMaximumSize());
		
	}
	
    public void init(Model model)
    {
    	 //create a graph
    	Graph<Vertex<?>,Edge<?,?>> ig = Graphs.<Vertex<?>,Edge<?,?>>synchronizedDirectedGraph(new DirectedSparseMultigraph<Vertex<?>,Edge<?,?>>());

    	ObservableGraph<Vertex<?>,Edge<?,?>> og = new ObservableGraph<Vertex<?>,Edge<?,?>>(ig);
     
        this.g = og;
        //create a graphdraw
        layout = new SpringLayout<Vertex<?>,Edge<?,?>>(g);
        layout.setSize(new Dimension(600,600));
		Relaxer relaxer = new VisRunner((IterativeContext)layout);
		relaxer.stop();
		relaxer.prerelax();

		Layout<Vertex<?>,Edge<?,?>> staticLayout = new StaticLayout<Vertex<?>,Edge<?,?>>(g, layout);

        vv = new VisualizationViewer<Vertex<?>,Edge<?,?>>(staticLayout, new Dimension(600,600));

        setLayout(new BorderLayout());
        setBackground(java.awt.Color.WHITE);
        setFont(new Font("Serif", Font.BOLD, 15));

        vv.setGraphMouse(new DefaultModalGraphMouse<Vertex<?>,Edge<?,?>>());

        vv.getRenderContext().setVertexShapeTransformer(new VertexShapeFunction());
        
        vv.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
        vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Vertex<?>>() {
        	@Override
        	public String transform(Vertex<?> v) {
        		return v.getName();
        	}
        });
        
        vv.getRenderContext().setVertexFillPaintTransformer(new Transformer<Vertex<?>,Paint>() {
            public Paint transform(Vertex<?> v) {
                return (v instanceof State ? Color.RED : Color.BLUE);
            }
        }  );
        
        vv.getRenderContext().setEdgeLabelTransformer(new Transformer<Edge<?,?>,String>(){
            public String transform(Edge<?,?> e) 
            {
            	if (e instanceof ActionEdge) {
            		return ((ActionEdge) e).toString();
            	} else if (e instanceof QEdge) {
            		return ((QEdge) e).toString();
            	}
                return null;
            }
        });
        
        vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.QuadCurve<Vertex<?>,Edge<?,?>>());        
        vv.getRenderContext().setEdgeDrawPaintTransformer(new PickableEdgePaintTransformer<Edge<?,?>>(vv.getPickedEdgeState(), Color.black, Color.cyan));
        
        AbstractEdgeShapeTransformer<Vertex<?>,Edge<?,?>> aesf = 
                (AbstractEdgeShapeTransformer<Vertex<?>,Edge<?,?>>)vv.getRenderContext().getEdgeShapeTransformer();
            aesf.setControlOffsetIncrement(30);
        
        
        vv.addComponentListener(new ComponentAdapter() {

			/**
			 * @see java.awt.event.ComponentAdapter#componentResized(java.awt.event.ComponentEvent)
			 */
			@Override
			public void componentResized(ComponentEvent arg0) {
				super.componentResized(arg0);
				System.err.println("resized");
				layout.setSize(arg0.getComponent().getSize());
			}});

        add(vv);
   }

    @Override
	public void update(Observable model, Object arg) {
    	
    	this.model = ((Model)model);
    	
    	clearGraph();
    	
		vv.getRenderContext().getPickedVertexState().clear();
    	vv.getRenderContext().getPickedEdgeState().clear();
		
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
    		 
    		 vv.repaint();
        	 repaint();	
    		 
        	 SpringLayout<Vertex<?>, Edge<?,?>> l = new SpringLayout<Vertex<?>, Edge<?,?>>(g);
             
             l.setInitializer(vv.getGraphLayout());
             l.setSize(vv.getSize());
             
				LayoutTransition<Vertex<?>, Edge<?,?>> lt =
					new LayoutTransition<Vertex<?>, Edge<?,?>>(vv, vv.getGraphLayout(), l);
				Animator animator = new Animator(lt);
				animator.start();
				vv.getRenderContext().getMultiLayerTransformer().setToIdentity();
				vv.repaint();
        	 
         } catch (Exception e) {
             e.printStackTrace();

         }
		
		
    }
    
    private void clearGraph() {
    	
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
        
    class VertexShapeFunction extends EllipseVertexShapeTransformer<Vertex<?>> 
    {
        public VertexShapeFunction() 
        {
            setSizeTransformer(new Transformer<Vertex<?>,Integer>() {
        		public Integer transform(Vertex<?> v) 
        		{
                	return (v instanceof State ? 30 : 20);
                }
            });
        }
        
        @Override
        public Shape transform(Vertex<?> v) {
        	        	
            if (v instanceof State) {
                return factory.getRegularPolygon(v, 3);
            }
            
            else {
                return factory.getEllipse(v);
            }
        }
    }
}