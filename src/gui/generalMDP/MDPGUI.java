package gui.generalMDP;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import settings.GeneralMDPSettings;
import simulations.MDPSimulation;
import simulations.TileworldSimulation;

/**
 * This is the main application. 
 * Run this as Java application!
 * 
 * I have implemented the Model-View-Controller pattern in this project.
 * - The Model: This class contains a variable "model", where a model is stored.
 *   The model consists of the entire simulation.
 *   
 * - The View: The class DrawPanel.java represents the JPanel in the GUI on which the model is drawn.
 *   This is thus the view of the application.
 *   
 * - The Controller: This class is the controller.
 * 
 * Communication between the model, the view, and the controller goes as follows:
 * - The Controller (this class) has direct access to the model and is able to start, stop, and change simulations.
 * - The model implements the "Observable" interface, which means it can be observed.
 *   When the model calls "setChanged(); notifyObservers();", all observers are notified.
 * - The view implements the "Observer" interface. 
 *   By implementing the method "update(Observable model, Object arg)", it can respond to changes in the model and display thme.
 * 
 * @author marc.vanzee
 * 
 *
 */
public class MDPGUI implements ItemListener {
	
	//private final MDPSimulation model = new MDPSimulation();
	private final MDPSimulation simulation;
	private final JFrame frame = new JFrame();
	private final MDPDrawer drawPanel = new MDPDrawer(this);
	
	// TODO: make them public for now so we can change them easily in the DrawPanel
	public JTextField textFieldPoints = new JTextField(),
			textFieldWorldsize = new JTextField(),
			textFieldDynamicity = new JTextField(),
			textFieldSteps	 = new JTextField();
	private final JButton btnStep = new JButton("Step");
	private final Component horizontalStrut = Box.createHorizontalStrut(10);
	
	final JButton btnPlay = new JButton("|>"),
			btnStop = new JButton("[]"),
			btnNewModel = new JButton("New Tileworld");
	private final JLabel lblHoleLifeExpectancy = new JLabel("hole life expectancy");
	private final JTextField textFieldLifeExpectancy = new JTextField();
	private final Component horizontalStrut_1 = Box.createHorizontalStrut(10);
	private final JLabel lblGestationPeriod = new JLabel("gestation period:");
	private final JTextField textFieldGestationPeriod = new JTextField();
	private final Component horizontalStrut_2 = Box.createHorizontalStrut(10);
	private final Component horizontalStrut_3 = Box.createHorizontalStrut(10);
	private final JLabel lblEffectiveness = new JLabel("effectiveness");
	private final Component horizontalStrut_4 = Box.createHorizontalStrut(10);
	private final JTextField textFieldEffectiveness = new JTextField();
	private final JLabel lblPlanningTime = new JLabel("planning time:");
	private final JTextField textFieldPlanningTime = new JTextField();
	private final Component horizontalStrut_5 = Box.createHorizontalStrut(10);
	private final JLabel lblBoldness = new JLabel("boldness:");
	private final JTextField textFieldBoldness = new JTextField();
	private final Component horizontalStrut_6 = Box.createHorizontalStrut(10);
	private final Component horizontalStrut_7 = Box.createHorizontalStrut(10);
	private final JLabel lblMaxPoints = new JLabel("max points");
	private final Component horizontalStrut_8 = Box.createHorizontalStrut(10);
	private final JTextField textFieldMaxPoints = new JTextField();
	private final Component horizontalStrut_9 = Box.createHorizontalStrut(10);
	private final JLabel lblObstacleRate = new JLabel("obstacle rate");
	private final JTextField textFieldObstacleRate = new JTextField();
	
	
	public static void main(String args[]) { 
		try {
		(new MDPGUI()).go();
	} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}}
	
	public MDPGUI() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		simulation = new MDPSimulation();
	}
	/**
	 * Launch the application.
	 */
	public void go() {
		try 
		{			
			// build GUI and set parameters in GUI according to model.Settings
			buildGUI();
			addListeners();
			initializeParametersInGUI();
			
			simulation.addObserver(drawPanel);
						
			frame.setVisible(true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public MDPSimulation getSimulation() {
		return simulation;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void buildGUI()
	{
		final Container cPane = frame.getContentPane();
				
		final Panel pNavContainer = new Panel(),
				pNavTop = new Panel(),
				pNavBottom = new Panel();
		
		final int nav_height = 47;
		
		final JSeparator jSep1 = new JSeparator(JSeparator.VERTICAL),
				jSep2 = new JSeparator(JSeparator.VERTICAL);
		
		frame.setBounds(100, 100, 950, 515);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		cPane.setLayout(new BoxLayout(cPane, BoxLayout.Y_AXIS));
		cPane.add(pNavContainer);
		
		pNavContainer.setPreferredSize(new Dimension(frame.getWidth(), nav_height));
		pNavContainer.setMaximumSize(new Dimension(frame.getWidth(), nav_height));
		pNavContainer.setMinimumSize(new Dimension(frame.getWidth(), nav_height));
		pNavContainer.setLayout(new BorderLayout());
		pNavContainer.add(new JSeparator(), BorderLayout.CENTER);
		pNavContainer.add(pNavTop, BorderLayout.NORTH);
		
		pNavTop.setLayout(new BoxLayout(pNavTop, BoxLayout.X_AXIS));
		pNavTop.add(Box.createHorizontalStrut(5));
		pNavTop.add(new JLabel("world size"));
		textFieldWorldsize.setText("20");
		
		textFieldWorldsize.setMaximumSize( new Dimension(30, 20) );
		pNavTop.add(textFieldWorldsize);
		pNavTop.add(Box.createHorizontalStrut(10));
		
		jSep1.setMaximumSize(new Dimension(2, 25));
		
		pNavTop.add(jSep1);
		
		jSep2.setMaximumSize(new Dimension(2, 25));
		
		pNavTop.add(jSep2);
		
		pNavTop.add(new JLabel("dynamicity:"));
		
		textFieldDynamicity.setText("0.5");
		textFieldDynamicity.setMaximumSize(new Dimension(50, 20));
		pNavTop.add(textFieldDynamicity);
		
		pNavTop.add(horizontalStrut);
		
		pNavTop.add(lblHoleLifeExpectancy);
		textFieldLifeExpectancy.setText("10");
		textFieldLifeExpectancy.setMaximumSize(new Dimension(50, 20));
		
		pNavTop.add(textFieldLifeExpectancy);
		
		pNavTop.add(horizontalStrut_9);
		
		pNavTop.add(lblObstacleRate);
		textFieldObstacleRate.setText("10");
		textFieldObstacleRate.setMaximumSize(new Dimension(50, 20));
		
		pNavTop.add(textFieldObstacleRate);
		
		pNavTop.add(horizontalStrut_1);
		
		pNavTop.add(lblGestationPeriod);
		textFieldGestationPeriod.setText("30");
		textFieldGestationPeriod.setMaximumSize(new Dimension(50, 20));
		
		pNavTop.add(textFieldGestationPeriod);
		
		pNavTop.add(horizontalStrut_2);
		
		pNavTop.add(lblPlanningTime);
		textFieldPlanningTime.setText("3");
		textFieldPlanningTime.setMaximumSize(new Dimension(50, 20));
		
		pNavTop.add(textFieldPlanningTime);
		
		pNavTop.add(horizontalStrut_5);
		
		pNavTop.add(lblBoldness);
		textFieldBoldness.setText("1");
		textFieldBoldness.setMaximumSize(new Dimension(50, 20));
		
		pNavTop.add(textFieldBoldness);
		
		pNavTop.add(horizontalStrut_6);
		
		pNavContainer.add(pNavBottom, BorderLayout.SOUTH);
	
		pNavBottom.setLayout(new BoxLayout(pNavBottom, BoxLayout.X_AXIS));
		
		btnNewModel.setMaximumSize(new Dimension(100, 20));
		
		pNavBottom.add(btnNewModel);
		btnStep.setMaximumSize(new Dimension(80, 20));
		
		pNavBottom.add(btnStep);
		
		btnStop.setMaximumSize(new Dimension(60, 20));
		btnPlay.setMaximumSize(new Dimension(60, 20));
		
		pNavBottom.add(btnPlay);
		pNavBottom.add(btnStop);
		pNavBottom.add(Box.createHorizontalStrut(10));
		
		pNavBottom.add(new JLabel("total steps"));
		
		textFieldSteps.setEditable(false);
		textFieldSteps.setText("0");
		textFieldSteps.setMaximumSize(new Dimension(50, 20));
		pNavBottom.add(textFieldSteps);
		
		pNavBottom.add(Box.createHorizontalStrut(10));
		
		pNavBottom.add(new JLabel("points"));
		
		pNavBottom.add(Box.createHorizontalStrut(10));
		
		textFieldPoints.setEditable(false);
		textFieldPoints.setText("0");
		textFieldPoints.setMaximumSize(new Dimension(50, 20));
		pNavBottom.add(textFieldPoints);
		
		pNavBottom.add(horizontalStrut_7);
		
		pNavBottom.add(lblMaxPoints);
		
		pNavBottom.add(horizontalStrut_8);
		textFieldMaxPoints.setText("0");
		textFieldMaxPoints.setMaximumSize(new Dimension(50, 20));
		textFieldMaxPoints.setEditable(false);
		
		pNavBottom.add(textFieldMaxPoints);
		
		pNavBottom.add(horizontalStrut_3);
		
		pNavBottom.add(lblEffectiveness);
		
		pNavBottom.add(horizontalStrut_4);
		textFieldEffectiveness.setText("0");
		textFieldEffectiveness.setMaximumSize(new Dimension(50, 20));
		textFieldEffectiveness.setEditable(false);
		
		pNavBottom.add(textFieldEffectiveness);

		cPane.add(drawPanel);
		
		frame.setVisible(true);
	}
	
	
	private void addListeners() 
	{
		btnNewModel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				MDPGUI.this.buildNewModel();			
			}
		});
		
		btnPlay.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				MDPGUI.this.startSimulation();		
			}
		});
		
		btnStop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				MDPGUI.this.stopSimulation();	
			}
		});
		
		btnStep.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				MDPGUI.this.step();		
			}
		});
		
		
	}
	
	//
	// CALLBACK METHODS
	//
	
	/**
	 * Build a new model according to the current settings in the GUI
	 */
	private void buildNewModel() {
		try
		{
			GeneralMDPSettings.NUM_STATES = Integer.parseInt(textFieldWorldsize.getText()); 
			// clear the graph
			drawPanel.clearGraph();
			
			// build the model according to the new settings.
			getParametersFromGUI();
			simulation.buildNewModel();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void startSimulation() {
		simulation.startSimulation(drawPanel.getTaskScheduler());
	}
	
	private void stopSimulation() {
		simulation.stopSimulation();
	}
	
	private void step() {
		simulation.step();
	}
	
	private void initializeParametersInGUI() 
	{
		
	}

	private void getParametersFromGUI() throws Exception 
	{
		
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
	}
}
