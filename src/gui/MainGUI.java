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
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JTextField;

import constants.SimulationConstants;
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
	public JTextField textFieldRewards = new JTextField(), 
			textFieldActs = new JTextField(), 
			textFieldDeliberations = new JTextField(),
			textFieldNumStates = new JTextField(), 
			textFieldNumActions = new JTextField(),
			textFieldAvgActionsState = new JTextField(),
			textFieldDynamicity = new JTextField(),
			textFieldSteps	 = new JTextField();
	
	
	private final JCheckBox chckbxAllowCycles = new JCheckBox("allow cycles"),
			chckbxAnimate = new JCheckBox("dynamic nodes");
	private final JButton btnStep = new JButton("Step");
	private final JSlider slider = new JSlider();
	private final Component horizontalStrut = Box.createHorizontalStrut(10);
	private final JLabel lblSpeed = new JLabel("speed:");
	
	final JButton btnPlay = new JButton("|>"),
			btnStop = new JButton("[]"),
			btnNewModel = new JButton("New Model");
			
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
		pNavTop.add(new JLabel("states"));
		
		textFieldNumStates.setMaximumSize( new Dimension(30, 20) );
		pNavTop.add(textFieldNumStates);
		pNavTop.add(Box.createHorizontalStrut(10));
		
		pNavTop.add(new JLabel("actions"));
		textFieldNumActions.setMaximumSize( new Dimension(30, 20) );
		pNavTop.add(textFieldNumActions);
		
		pNavTop.add(Box.createHorizontalStrut(10));
		pNavTop.add(new JLabel("avg. actions/state"));
		
		textFieldAvgActionsState.setMaximumSize(new Dimension(30, 20));
		pNavTop.add(textFieldAvgActionsState);
		
		pNavTop.add(Box.createHorizontalStrut(10));
		
		jSep1.setMaximumSize(new Dimension(2, 25));
		
		pNavTop.add(jSep1);
		
		jSep2.setMaximumSize(new Dimension(2, 25));
		
		pNavTop.add(jSep2);
		
		pNavTop.add(chckbxAllowCycles);
		
		chckbxAnimate.addItemListener(this);
		
		pNavTop.add(chckbxAnimate);
		
		pNavTop.add(Box.createHorizontalStrut(10));
		
		pNavTop.add(new JLabel("dynamicity:"));
		
		textFieldDynamicity.setText("0.5");
		textFieldDynamicity.setMaximumSize(new Dimension(50, 20));
		pNavTop.add(textFieldDynamicity);
		
		pNavTop.add(horizontalStrut);
		
		pNavTop.add(lblSpeed);
		
		slider.setMaximumSize(new Dimension(200, 20));
		
		pNavTop.add(slider);
		
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
		
		pNavBottom.add(new JLabel("deliberations"));
		
		textFieldDeliberations.setEditable(false);
		textFieldDeliberations.setText("0");
		textFieldDeliberations.setMaximumSize(new Dimension(50, 20));
		pNavBottom.add(textFieldDeliberations);
		
		pNavBottom.add(Box.createHorizontalStrut(10));
		
		pNavBottom.add(new JLabel("acts"));
		
		textFieldActs.setEditable(false);
		textFieldActs.setText("0");
		textFieldActs.setMaximumSize(new Dimension(50, 20));
		pNavBottom.add(textFieldActs);
		
		pNavBottom.add(new JLabel("reward"));
		
		pNavBottom.add(Box.createHorizontalStrut(10));
		
		textFieldRewards.setEditable(false);
		textFieldRewards.setText("0");
		textFieldRewards.setMaximumSize(new Dimension(50, 20));
		pNavBottom.add(textFieldRewards);

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
		int numActions = Settings.NUM_ACTIONS,
				numStates = Settings.NUM_STATES,
				avgActionsState = Settings.AVG_ACTIONS_STATE;
		double dynamicity = Settings.DYNAMICITY;
		boolean allowCycles = Settings.CYCLES_ALLOWED;
		
		textFieldNumActions.setText(Integer.toString(numActions));
		textFieldNumStates.setText(Integer.toString(numStates));
		textFieldAvgActionsState.setText(Integer.toString(avgActionsState));
		textFieldDynamicity.setText(Double.toString(dynamicity));
		chckbxAllowCycles.setSelected(allowCycles);
	}

	private void getParametersFromGUI() throws Exception 
	{
		int numActions = validateInt(textFieldNumActions,1,SimulationConstants.MAX_ALLOWED_ACTIONS),
				numStates = validateInt(textFieldNumStates,1,SimulationConstants.MAX_ALLOWED_STATES),
				avgActionsState = validateInt(textFieldAvgActionsState, 1, numStates);
		
		boolean cycles = chckbxAllowCycles.isSelected();
		
		Settings.NUM_STATES = numStates;
		Settings.NUM_ACTIONS = numActions;
		Settings.AVG_ACTIONS_STATE = avgActionsState;
		Settings.CYCLES_ALLOWED = cycles;
		
		validateConstraints();
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
		// TODO Auto-generated method stub
		if (e.getItem() == chckbxAnimate) {
			drawPanel.toggleAnimate();
		}
	}
}
