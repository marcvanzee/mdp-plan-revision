package solver;

import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;
import java.util.ArrayList;
import java.util.Random;
public class GraphingProcess extends JPanel {
    String[] names;
    double[][] doubles; // an array of array lists
    double doubMax;
    double doubMin;
    int PAD = 20;
    // Takes in the input data (either q values or objective values) and sets min and max values
    public void takeInArray(ArrayList[] pArrList){// array of ArrayList<DOUBLE OBJECT>
        doubMax = Double.MIN_VALUE;
        doubMin = Double.MAX_VALUE;
        int numOfStates = pArrList.length;
        int numOfFrames = pArrList[0].size();
        doubles = new double[numOfStates][numOfFrames];
        double in;
        for(int i = 0; i< numOfStates; i++){
             for(int j = 0; j< numOfFrames; j++){
                 in = ((Double)pArrList[i].get(j)).doubleValue();
                 doubles[i][j]= in;
                 if(in > doubMax) doubMax = in;
                 if(in < doubMin) doubMin = in;
            }
        }
    }
    
    public void takeInNames(String[] pNames){
        names = pNames;
    }
    
    
    // Renders the input data.
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        int w = getWidth();
        int h = getHeight();
        PAD = (int)(((double)h)/6.0f);
        // Draw ordinate.
        g2.draw(new Line2D.Double(PAD, PAD, PAD, h-PAD));
        //g2.drawString(""+doubMax, PAD, PAD);
        g2.drawString(""+1.0f, PAD, PAD);
        // Draw abcissa.
        g2.draw(new Line2D.Double(PAD, h-PAD, w-PAD, h-PAD));
        g2.drawString("Q-Values", 0, h/2);
        g2.drawString(""+doubles[0].length, w-PAD, h-PAD);
        g2.drawString("Trials", (w/2)-PAD, h-(PAD/2));
        double xInc = (double)(w - 2*PAD)/(doubles[0].length-1);
        double scale = (double)(h - 2*PAD)/doubMax;
        
        
        // Java 'Color' class takes 3 floats, from 0 to 1.
        Random rand = new Random();
        float r;
        float gr;
        float b;

        Color randomColor;
        
        double prevX = 0;
        double prevY = 0;
        for(int j = 0; j< doubles.length; j++){
            r = rand.nextFloat();
            gr = rand.nextFloat();
            b = rand.nextFloat();
            randomColor = new Color(r, gr, b);
            g2.setPaint(randomColor);
            for(int i = 0; i < doubles[j].length; i++) {
                double x = PAD + i*xInc;
                double y = h - PAD - scale*doubles[j][i];
                g2.fill(new Ellipse2D.Double(x-2, y-2, 1, 1));
                if(i>0){
                    Point2D p1=new Point2D.Double(x,y);  
                    Point2D p2=new Point2D.Double(prevX,prevY);  
                    Line2D l1=new Line2D.Double(p1,p2);  
                    g2.draw(l1);
                    if(i == doubles[j].length-1 && names != null && j < names.length){
                        g2.drawString(names[j], w-PAD, (int)y);
                    }
                }
                prevX = x;
                prevY = y;
            }
        }
        g2.setStroke(new BasicStroke(8));
        randomColor = new Color(0, 0, 0,128);
        g2.setPaint(randomColor);
        g2.draw(new Line2D.Double(PAD+ ((w-(2*PAD))/5), PAD/2+ 10, PAD+ ((w-(2*PAD))/5), h-PAD));
        g2.drawString("First Instance of optimal policy.", PAD+ ((w-(2*PAD))/5), PAD/2);
    }
    
}