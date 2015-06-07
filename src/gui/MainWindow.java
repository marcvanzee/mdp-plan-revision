package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import model.Model;

/**
 * This is the CONTROLLER
 * 
 * @author marc.vanzee
 *
 */
public class MainWindow {
	
	private Model model;
	
	private JFrame frame;
	
	private Settings settings;
	
	JTextField textFieldNumStates, textFieldNumActions,
		textFieldMinReward, textFieldMaxReward;
	
	DrawPanel drawPanel;
	private JCheckBox chckbxAllowCycles;
	private JCheckBox chckbxShowQstates;
	private Component horizontalStrut;
	private JLabel lblAvgActionsPer;
	private JTextField textFieldAvgActionsState;
				
	/**
	 * Launch the application.
	 */
	public static void main(String args[]) {
		(new MainWindow()).go();
	}
	
	public void go() {
		try {
			model = new Model();
			drawPanel = new DrawPanel();
			drawPanel.init(model);
			model.addObserver(drawPanel);
			
			buildGUI();
			initializeParametersInGUI();
			
			frame.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void buildGUI() {
		frame = new JFrame();
		frame.setBounds(100, 100, 950, 515);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container cPane = frame.getContentPane();
		cPane.setLayout(new BoxLayout(cPane, BoxLayout.Y_AXIS));
		
		int nav_height = 47;

		Panel pNavContainer = new Panel();
		
		cPane.add(pNavContainer);
		
		pNavContainer.setPreferredSize(new Dimension(frame.getWidth(), nav_height));
		pNavContainer.setMaximumSize(new Dimension(frame.getWidth(), nav_height));
		pNavContainer.setMinimumSize(new Dimension(frame.getWidth(), nav_height));
		pNavContainer.setLayout(new BorderLayout());
		
		JSeparator jSep = new JSeparator();
		
		pNavContainer.add(new JSeparator(), BorderLayout.CENTER);
		
		Panel pNavTop = new Panel();
		
		pNavContainer.add(pNavTop, BorderLayout.NORTH);
		
		
		pNavTop.setLayout(new BoxLayout(pNavTop, BoxLayout.X_AXIS));
		
		pNavTop.add(Box.createHorizontalStrut(5));
		
		pNavTop.add(new JLabel("states"));
		
		textFieldNumStates = new JTextField();
		textFieldNumStates.setMaximumSize( new Dimension(30, 20) );
		pNavTop.add(textFieldNumStates);
		pNavTop.add(Box.createHorizontalStrut(10));
		
		pNavTop.add(new JLabel("actions"));
		textFieldNumActions = new JTextField();
		textFieldNumActions.setMaximumSize( new Dimension(30, 20) );
		pNavTop.add(textFieldNumActions);
		
		pNavTop.add(Box.createHorizontalStrut(10));
		
		lblAvgActionsPer = new JLabel("avg. actions/state");
		pNavTop.add(lblAvgActionsPer);
		
		textFieldAvgActionsState = new JTextField();
		textFieldAvgActionsState.setMaximumSize(new Dimension(30, 20));
		pNavTop.add(textFieldAvgActionsState);
		
		horizontalStrut = Box.createHorizontalStrut(10);
		pNavTop.add(horizontalStrut);
		
		jSep = new JSeparator(JSeparator.VERTICAL);
		jSep.setMaximumSize(new Dimension(2, 25));
		
		pNavTop.add(jSep);
		
		pNavTop.add(new JLabel("min reward: "));
		
		textFieldMinReward = new JTextField();
		textFieldMinReward.setMaximumSize( new Dimension(30, 20) );
		pNavTop.add(textFieldMinReward);
		pNavTop.add(Box.createHorizontalStrut(10));
		
		pNavTop.add(new JLabel("max reward: "));
		textFieldMaxReward = new JTextField();
		textFieldMaxReward.setMaximumSize( new Dimension(30, 20) );
		pNavTop.add(textFieldMaxReward);
		
		pNavTop.add(Box.createHorizontalStrut(10));
		
		jSep = new JSeparator(JSeparator.VERTICAL);
		jSep.setMaximumSize(new Dimension(2, 25));
		
		pNavTop.add(jSep);
		
		chckbxAllowCycles = new JCheckBox("allow cycles");
		pNavTop.add(chckbxAllowCycles);
		
		chckbxShowQstates = new JCheckBox("show q-states");
		pNavTop.add(chckbxShowQstates);
				
		Panel pNavBottom = new Panel();
		
		pNavContainer.add(pNavBottom, BorderLayout.SOUTH);
	
		pNavBottom.setLayout(new BoxLayout(pNavBottom, BoxLayout.X_AXIS));
		
		JButton btnCreateMdp = new JButton("Create MDP");
		btnCreateMdp.setMaximumSize(new Dimension(110, 20));
		
		pNavBottom.add(btnCreateMdp);
		btnCreateMdp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				MainWindow.this.buildNewModel();			
			}
		});
		
		JButton btnCreateAgent = new JButton("Create Agent");
		btnCreateAgent.setMaximumSize(new Dimension(120, 20));
		
		pNavBottom.add(btnCreateAgent);
		btnCreateAgent.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
//				MainWindow.this.notify(GUIConstants.AGENT_REQUESTED);				
			}
		});
		
		JButton btnStep = new JButton("Step");
		btnCreateAgent.setMaximumSize(new Dimension(120, 20));
		
		pNavBottom.add(btnStep);

		
		cPane.add(drawPanel);
		
		frame.setVisible(true);
	}
	
	private void buildNewModel() {
		try
		{
			getParametersFromGUI();
			model.buildNewModel(settings);
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initializeParametersInGUI() {
		
		settings = new Settings();
		
		int numActions = settings.getNumActions(),
				numStates = settings.getNumStates(),
				avgActionsState = settings.getAvgActionsState();
		double minReward = settings.getMinReward();
		double maxReward = settings.getMaxReward();
		
		textFieldNumActions.setText(Integer.toString(numActions));
		textFieldNumStates.setText(Integer.toString(numStates));
		textFieldAvgActionsState.setText(Integer.toString(avgActionsState));
		textFieldMinReward.setText(Double.toString(minReward));
		textFieldMaxReward.setText(Double.toString(maxReward));
	}

	private void getParametersFromGUI() throws Exception {
		
		int numActions = validateInt(textFieldNumActions,1,Settings.MAX_ALLOWED_ACTIONS),
				numStates = validateInt(textFieldNumStates,1,Settings.MAX_ALLOWED_STATES),
				avgActionsState = validateInt(textFieldAvgActionsState, 1, numStates);
		
		double minReward = validateDouble(textFieldMinReward,Settings.MIN_ALLOWED_REWARD,Settings.MAX_ALLOWED_REWARD),
				maxReward = validateDouble(textFieldMaxReward,Settings.MIN_ALLOWED_REWARD,Settings.MAX_ALLOWED_REWARD);
		
		boolean cycles = chckbxAllowCycles.isSelected();
		
		settings = new Settings(numStates, numActions, avgActionsState, minReward, maxReward, cycles);
		
		validateConstraints();
	}
	
	private void validateConstraints() throws Exception {
		if (settings.getMinReward() > settings.getMaxReward()) {
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
}
