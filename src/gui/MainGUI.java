package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import model.Settings;
import model.TileWorldSimulation;

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
public class MainGUI implements ItemListener {
	
	//private final MDPSimulation model = new MDPSimulation();
	private final TileWorldSimulation model = new TileWorldSimulation();
	private final JFrame frame = new JFrame();
	private final DrawPanel drawPanel = new DrawPanel(this);
	
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
			
	/**
	 * Launch the application.
	 */
	public static void main(String args[]) {
		(new MainGUI()).go();
	}
	
	public void go() {
		try 
		{			
			// build GUI and set parameters in GUI according to model.Settings
			buildGUI();
			addListeners();
			initializeParametersInGUI();
			
			model.addObserver(drawPanel);
						
			frame.setVisible(true);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public TileWorldSimulation getModel() {
		return model;
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
				MainGUI.this.buildNewModel();			
			}
		});
		
		btnPlay.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				MainGUI.this.startSimulation();		
			}
		});
		
		btnStop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				MainGUI.this.stopSimulation();	
			}
		});
		
		btnStep.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				MainGUI.this.step();		
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
			// clear the graph
			drawPanel.clearGraph();
			
			// build the model according to the new settings.
			getParametersFromGUI();
			model.buildNewModel();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void startSimulation() {
		model.startSimulation(drawPanel.getTaskScheduler());
	}
	
	private void stopSimulation() {
		model.stopSimulation();
	}
	
	private void step() {
		model.step();
	}
	
	private void initializeParametersInGUI() 
	{
		int worldSize = Settings.WORLD_SIZE,
				lifeExpectancy = Settings.LIFE_EXPECTANCY,
				gestationPeriod = Settings.GESTATION_PERIOD,
				planningTime = Settings.PLANNING_TIME,
				boldness = Settings.BOLDNESS;
		double dynamicity = Settings.DYNAMICITY,
				obstacleRate = Settings.OBSTACLE_RATE;
		
		textFieldWorldsize.setText(Integer.toString(worldSize));
		textFieldLifeExpectancy.setText(Integer.toString(lifeExpectancy));
		textFieldDynamicity.setText(Double.toString(dynamicity));
		textFieldGestationPeriod.setText(Integer.toString(gestationPeriod));
		textFieldPlanningTime.setText(Integer.toString(planningTime));
		textFieldBoldness.setText(Integer.toString(boldness));
		textFieldObstacleRate.setText(Double.toString(obstacleRate));
	}

	private void getParametersFromGUI() throws Exception 
	{
		int worldSize = validateInt(textFieldWorldsize,1,1000),
				lifeExpectancy = validateInt(textFieldLifeExpectancy,1,1000),
				gestationPeriod = validateInt(textFieldGestationPeriod,1,1000),
				planningTime = validateInt(textFieldPlanningTime,1,1000),
				boldness = validateInt(textFieldBoldness,1,1000);
		double dynamicity = validateDouble(textFieldDynamicity, 0, 1),
				obstacleRate = validateDouble(textFieldObstacleRate, 0, 1);
		
		Settings.WORLD_SIZE = worldSize;
		Settings.LIFE_EXPECTANCY = lifeExpectancy;
		Settings.GESTATION_PERIOD = gestationPeriod;
		Settings.PLANNING_TIME = planningTime;
		Settings.BOLDNESS = boldness;
		Settings.DYNAMICITY = dynamicity;
		Settings.OBSTACLE_RATE = obstacleRate;
		
		//validateConstraints();
	}
	
	private void validateConstraints() throws Exception {
		if (Settings.MIN_REWARD > Settings.MAX_REWARD) {
			throw new Exception("Constraints not satisfied");
		}
	}
	
	private int validateInt(JTextField textField, int min, int max) throws Exception {
		int ret = Integer.parseInt(textField.getText());
		if ((ret >= min) && (ret <= max)) {
			return ret;
		} else {
			throw new Exception("Parsing problem");
		}
	}
	
	private double validateDouble(JTextField textField, double min, double max) throws Exception {
		double ret = Double.parseDouble(textField.getText());
		if ((ret >= min) && (ret <= max)) {
			return ret;
		} else {
			throw new Exception("Parsing problem");
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
	}
}
