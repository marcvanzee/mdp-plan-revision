package gui.tileworld;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.Observable;
import java.util.Observer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import constants.MathOperations;
import gui.Main;
import simulations.TileworldSimulation;

public class TileworldGUI implements ItemListener, Observer {
	
	//private final MDPSimulation model = new MDPSimulation();
	private final TileworldSimulation simulation;
	private final JFrame frame = new JFrame("Tileworld MDP");
	private final TileworldPanel drawPanel;
	
	// TODO: make them public for now so we can change them easily in the DrawPanel
	public JTextField textFieldPoints = new JTextField(),
			textFieldSteps	 = new JTextField();
	private final JButton btnStep = new JButton("Step");
	
	final JButton btnPlay = new JButton("|>"),
			btnStop = new JButton("[]"),
			btnNewModel = new JButton("New Simulation");
	private final Component horizontalStrut_3 = Box.createHorizontalStrut(10);
	private final JLabel lblEffectiveness = new JLabel("effectiveness");
	private final Component horizontalStrut_4 = Box.createHorizontalStrut(10);
	private final JTextField textFieldEffectiveness = new JTextField();
	private final Component horizontalStrut_7 = Box.createHorizontalStrut(10);
	private final JLabel lblMaxPoints = new JLabel("max score");
	private final Component horizontalStrut_8 = Box.createHorizontalStrut(10);
	private final JTextField textFieldMaxPoints = new JTextField();
	private final JButton btnSettings = new JButton("Settings");
	
	public TileworldGUI() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException
	{
		simulation = new TileworldSimulation();
		drawPanel = new TileworldPanel(simulation.getMDP());
	}
	
	public static void main(String args[]) {
		try {
			(new TileworldGUI()).go();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @wbp.parser.entryPoint
	 */
	public void go() {
		try 
		{			
			// build GUI and set parameters in GUI according to settings file
			buildGUI();
			Main.loadSettings();
			addListeners();
			
			simulation.addObserver(drawPanel);
			simulation.addObserver(this);
						
			frame.setVisible(true);
						
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public TileworldSimulation getSimulation() {
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
		
		final JSeparator jSep1 = new JSeparator(JSeparator.VERTICAL),
				jSep2 = new JSeparator(JSeparator.VERTICAL);
		
		//frame.setBounds(100, 100, 1057, 822);
		frame.setBounds(100, 100, 800, 563);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		cPane.setLayout(new BoxLayout(cPane, BoxLayout.Y_AXIS));
		cPane.add(pNavContainer);
		
		pNavContainer.setPreferredSize(new Dimension(1057, 25));
		pNavContainer.setMaximumSize(new Dimension(1057, 25));
		pNavContainer.setMinimumSize(new Dimension(1057, 25));
		pNavContainer.setLayout(new BorderLayout());
		pNavContainer.add(pNavTop, BorderLayout.NORTH);
		
		pNavTop.setLayout(new BoxLayout(pNavTop, BoxLayout.X_AXIS));
		pNavTop.add(Box.createHorizontalStrut(5));
		
		jSep1.setMaximumSize(new Dimension(2, 25));
		
		pNavTop.add(jSep1);
		
		jSep2.setMaximumSize(new Dimension(2, 25));
		
		pNavTop.add(jSep2);
		
		pNavContainer.add(pNavBottom, BorderLayout.SOUTH);
	
		pNavBottom.setLayout(new BoxLayout(pNavBottom, BoxLayout.X_AXIS));
		pNavBottom.add(btnNewModel);
		
		btnNewModel.setMaximumSize(new Dimension(140, 20));
		btnSettings.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				(new SettingsDialog()).go();
			}
		});
		
		pNavBottom.add(btnSettings);
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
		
		pNavBottom.add(new JLabel("score"));
		
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
					TileworldGUI.this.buildNewModel();	
			}
		});
		
		btnPlay.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				TileworldGUI.this.startSimulation();
			}
		});
		
		btnStop.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				TileworldGUI.this.stopSimulation();
			}
		});
		
		btnStep.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				TileworldGUI.this.step();
			}
		});
		
		
	}
	
	//
	// CALLBACK METHODS
	//
	
	/**
	 * Build a new model according to the current settings in the GUI
	 */
	public void buildNewModel() {
		try
		{
			simulation.buildNewModel();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void startSimulation() {
		simulation.startSimulation(null);
	}
	
	private void stopSimulation() {
		simulation.stopSimulation();
	}
	
	private void step() {
		simulation.step();
	}
	
	

	@Override
	public void itemStateChanged(ItemEvent e) {
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		double score = simulation.getAgentScore(),
				maxScore = simulation.getMaxScore(),
				effectivenes = (double) score / (double) maxScore;
		
		textFieldSteps.setText(simulation.getSteps()+"");
		textFieldMaxPoints.setText(MathOperations.round(maxScore,2)+"");
		textFieldPoints.setText(MathOperations.round(score,2)+"");
		
		textFieldEffectiveness.setText(MathOperations.round(effectivenes, 2)+"");
		
	}
}