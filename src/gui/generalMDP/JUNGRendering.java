package gui.generalMDP;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.AbstractEdgeShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.EllipseVertexShapeTransformer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import mdp.elements.Edge;
import mdp.elements.State;
import mdp.elements.Vertex;

/**
 * Task care of all the settings that are necessary to make sure that JUNG works correctly.
 * 
 * @author marc.vanzee
 *
 */
public class JUNGRendering extends VisualizationViewer<Vertex<?>,Edge<?,?>>
{
	private static final long serialVersionUID = 1L;

	//private final Graph<Vertex<?>,Edge<?,?>> g;
	
    //private final AbstractLayout<Vertex<?>,Edge<?,?>> layout;
	
    //private final Layout<Vertex<?>,Edge<?,?>> staticLayout;
	
    public JUNGRendering(final AbstractLayout<Vertex<?>,Edge<?,?>> layout,
    		Layout<Vertex<?>,Edge<?,?>> staticLayout)
    {
    	
    	super(staticLayout, new Dimension(600,600));
    	
    	setGraphMouse(new DefaultModalGraphMouse<Vertex<?>,Edge<?,?>>());
    	getRenderContext().setVertexShapeTransformer(new VertexShapeFunction());
    	getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);
    	getRenderContext().setVertexLabelTransformer(new ToStringLabeller<Vertex<?>>() {
		@Override
		public String transform(Vertex<?> v) 
		{
			return v.getName();
		}
		});
		
		getRenderContext().setVertexFillPaintTransformer(new Transformer<Vertex<?>,Paint>() {
		    public Paint transform(Vertex<?> v) {
		    	return v.getColor();
		    }
		}  );
		    
		getRenderContext().setEdgeDrawPaintTransformer(new Transformer<Edge<?,?>,Paint>() {
			public Paint transform(Edge<?,?> e) 
			{
				return e.getColor(); 
			}
		});
		
		Transformer<Edge<?,?>, Stroke> edgeStrokeTransformer = new Transformer<Edge<?,?>, Stroke>() {
			public Stroke transform(Edge<?,?> e) {
				return new BasicStroke(e.getStrokeWidth());
			}
		};
		
		getRenderContext().setEdgeStrokeTransformer(edgeStrokeTransformer);
		
		getRenderContext().setEdgeLabelTransformer(new Transformer<Edge<?,?>,String>(){
		    public String transform(Edge<?,?> e) 
		    {
		    	return e.toString();
		    }
		});
		
		getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<Vertex<?>,Edge<?,?>>());        
		
		AbstractEdgeShapeTransformer<Vertex<?>,Edge<?,?>> aesf = 
		        (AbstractEdgeShapeTransformer<Vertex<?>,Edge<?,?>>)getRenderContext().getEdgeShapeTransformer();
		    aesf.setControlOffsetIncrement(30);
		
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent arg0) {
				super.componentResized(arg0);
				layout.setSize(arg0.getComponent().getSize());
			}});
		
		setBackground(Color.WHITE);
    }
    
    class VertexShapeFunction extends EllipseVertexShapeTransformer<Vertex<?>> 
    {
        public VertexShapeFunction() 
        {
            setSizeTransformer(new Transformer<Vertex<?>,Integer>() {
        		public Integer transform(Vertex<?> v) 
        		{
                	return v.getSize();
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
