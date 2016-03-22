package gui.tileworld;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingConstants;

import mdp.agent.ReactionStrategy;
import settings.TileworldSettings;

public class SettingsDialog {

	private JFrame frame;
	private JTextField txtWorldSize;
	private JTextField txtHoleGestationTimeMin;
	private JTextField txtHoleGestationTimeMax;
	private JTextField txtHoleLifeExpMin;
	private JTextField txtHoleLifeExpMax;
	private JTextField txtHoleScoreMin;
	private JTextField txtHoleScoreMax;
	private JTextField txtInitialNrHoles;
	private JTextField txtWallSizeMin;
	private JTextField txtWallSizeMax;
	private JTextField txtInitialNrWalls;
	private JLabel lblNewLabel;
	private JLabel lblHoleScore;
	private JLabel lblInitialNoOf;
	private JLabel lblHoleSize;
	private JLabel lblInitialNoOf_1;
	private JLabel lblEnvironmentSettings;
	private JSeparator separator;
	private JLabel lblAgentSettings;
	private JTextField txtDynamism;
	private JTextField txtPlanningTime;
	private JTextField txtCommitmentDegree;
	private JLabel lblDynamism;
	private JLabel lblPlanningTime;
	private JLabel lblDegreeOfCommitment;
	private JCheckBox checkUseReactionStrategy;
	private JRadioButton radioTargetDisappears;
	private JRadioButton radioTargetDisOrNearerHole;
	private JRadioButton radioTargetDisOrAnyHole;
	private final ButtonGroup buttonGroup = new ButtonGroup();
	
	public void go() {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SettingsDialog window = new SettingsDialog();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public SettingsDialog() {
		initialize();
		
		initializeParametersInGUI();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Tileworld Settings");
		frame.setBounds(400, 300, 430, 280);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		SpringLayout springLayout = new SpringLayout();
		frame.getContentPane().setLayout(springLayout);
		
		JLabel lblWorldDimensions = new JLabel("world dimension");
		lblWorldDimensions.setFont(new Font("Tahoma", Font.PLAIN, 10));
		springLayout.putConstraint(SpringLayout.WEST, lblWorldDimensions, 38, SpringLayout.WEST, frame.getContentPane());
		frame.getContentPane().add(lblWorldDimensions);
		
		txtWorldSize = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, lblWorldDimensions, 3, SpringLayout.NORTH, txtWorldSize);
		springLayout.putConstraint(SpringLayout.EAST, lblWorldDimensions, -6, SpringLayout.WEST, txtWorldSize);
		springLayout.putConstraint(SpringLayout.SOUTH, txtWorldSize, -194, SpringLayout.SOUTH, frame.getContentPane());
		frame.getContentPane().add(txtWorldSize);
		txtWorldSize.setColumns(10);
		
		JLabel lblMinHoleGestation = new JLabel("hole gestation time");
		lblMinHoleGestation.setFont(new Font("Tahoma", Font.PLAIN, 10));
		springLayout.putConstraint(SpringLayout.SOUTH, lblWorldDimensions, -14, SpringLayout.NORTH, lblMinHoleGestation);
		springLayout.putConstraint(SpringLayout.WEST, lblMinHoleGestation, 28, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, lblMinHoleGestation, -169, SpringLayout.SOUTH, frame.getContentPane());
		frame.getContentPane().add(lblMinHoleGestation);
		
		txtHoleGestationTimeMin = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txtHoleGestationTimeMin, 8, SpringLayout.SOUTH, txtWorldSize);
		springLayout.putConstraint(SpringLayout.EAST, txtWorldSize, 0, SpringLayout.EAST, txtHoleGestationTimeMin);
		springLayout.putConstraint(SpringLayout.WEST, txtHoleGestationTimeMin, 125, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, txtHoleGestationTimeMin, -267, SpringLayout.EAST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.NORTH, lblMinHoleGestation, 3, SpringLayout.NORTH, txtHoleGestationTimeMin);
		springLayout.putConstraint(SpringLayout.EAST, lblMinHoleGestation, -6, SpringLayout.WEST, txtHoleGestationTimeMin);
		springLayout.putConstraint(SpringLayout.WEST, txtWorldSize, 0, SpringLayout.WEST, txtHoleGestationTimeMin);
		frame.getContentPane().add(txtHoleGestationTimeMin);
		txtHoleGestationTimeMin.setColumns(10);
		
		txtHoleGestationTimeMax = new JTextField();
		springLayout.putConstraint(SpringLayout.WEST, txtHoleGestationTimeMax, 174, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, txtHoleGestationTimeMax, -166, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, txtHoleGestationTimeMax, 49, SpringLayout.EAST, txtHoleGestationTimeMin);
		frame.getContentPane().add(txtHoleGestationTimeMax);
		txtHoleGestationTimeMax.setColumns(10);
		
		txtHoleLifeExpMin = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txtHoleLifeExpMin, 102, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, txtHoleGestationTimeMin, -6, SpringLayout.NORTH, txtHoleLifeExpMin);
		springLayout.putConstraint(SpringLayout.WEST, txtHoleLifeExpMin, 125, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, txtHoleLifeExpMin, -140, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, txtHoleLifeExpMin, 167, SpringLayout.WEST, frame.getContentPane());
		frame.getContentPane().add(txtHoleLifeExpMin);
		txtHoleLifeExpMin.setColumns(10);
		
		txtHoleLifeExpMax = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txtHoleLifeExpMax, 6, SpringLayout.SOUTH, txtHoleGestationTimeMax);
		springLayout.putConstraint(SpringLayout.WEST, txtHoleLifeExpMax, 7, SpringLayout.EAST, txtHoleLifeExpMin);
		springLayout.putConstraint(SpringLayout.EAST, txtHoleLifeExpMax, 0, SpringLayout.EAST, txtHoleGestationTimeMax);
		frame.getContentPane().add(txtHoleLifeExpMax);
		txtHoleLifeExpMax.setColumns(10);
		
		txtHoleScoreMin = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txtHoleScoreMin, 6, SpringLayout.SOUTH, txtHoleLifeExpMin);
		springLayout.putConstraint(SpringLayout.SOUTH, txtHoleScoreMin, -114, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, txtHoleScoreMin, -267, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().add(txtHoleScoreMin);
		txtHoleScoreMin.setColumns(10);
		
		txtHoleScoreMax = new JTextField();
		springLayout.putConstraint(SpringLayout.SOUTH, txtHoleLifeExpMax, -6, SpringLayout.NORTH, txtHoleScoreMax);
		springLayout.putConstraint(SpringLayout.NORTH, txtHoleScoreMax, 128, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, txtHoleScoreMax, 0, SpringLayout.EAST, txtHoleGestationTimeMax);
		springLayout.putConstraint(SpringLayout.WEST, txtHoleScoreMax, 174, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, txtHoleScoreMax, -114, SpringLayout.SOUTH, frame.getContentPane());
		frame.getContentPane().add(txtHoleScoreMax);
		txtHoleScoreMax.setColumns(10);
		
		txtInitialNrHoles = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txtInitialNrHoles, 6, SpringLayout.SOUTH, txtHoleScoreMin);
		springLayout.putConstraint(SpringLayout.SOUTH, txtInitialNrHoles, -88, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, txtInitialNrHoles, 0, SpringLayout.EAST, txtWorldSize);
		frame.getContentPane().add(txtInitialNrHoles);
		txtInitialNrHoles.setColumns(10);
		
		txtWallSizeMin = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txtWallSizeMin, 6, SpringLayout.SOUTH, txtInitialNrHoles);
		springLayout.putConstraint(SpringLayout.WEST, txtWallSizeMin, 125, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, txtWallSizeMin, 167, SpringLayout.WEST, frame.getContentPane());
		frame.getContentPane().add(txtWallSizeMin);
		txtWallSizeMin.setColumns(10);
		
		txtWallSizeMax = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txtWallSizeMax, 32, SpringLayout.SOUTH, txtHoleScoreMax);
		springLayout.putConstraint(SpringLayout.WEST, txtWallSizeMax, 7, SpringLayout.EAST, txtWallSizeMin);
		springLayout.putConstraint(SpringLayout.SOUTH, txtWallSizeMax, -62, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, txtWallSizeMax, 0, SpringLayout.EAST, txtHoleGestationTimeMax);
		frame.getContentPane().add(txtWallSizeMax);
		txtWallSizeMax.setColumns(10);
		
		txtInitialNrWalls = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txtInitialNrWalls, 206, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, txtWallSizeMin, -6, SpringLayout.NORTH, txtInitialNrWalls);
		springLayout.putConstraint(SpringLayout.WEST, txtInitialNrWalls, 125, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, txtInitialNrWalls, -36, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, txtInitialNrWalls, 167, SpringLayout.WEST, frame.getContentPane());
		frame.getContentPane().add(txtInitialNrWalls);
		txtInitialNrWalls.setColumns(10);
		
		lblNewLabel = new JLabel("hole life expectancy");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 10));
		springLayout.putConstraint(SpringLayout.WEST, lblNewLabel, 23, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, lblNewLabel, 0, SpringLayout.SOUTH, txtHoleLifeExpMin);
		springLayout.putConstraint(SpringLayout.EAST, lblNewLabel, -6, SpringLayout.WEST, txtHoleLifeExpMin);
		frame.getContentPane().add(lblNewLabel);
		
		lblHoleScore = new JLabel("hole score");
		lblHoleScore.setFont(new Font("Tahoma", Font.PLAIN, 10));
		springLayout.putConstraint(SpringLayout.WEST, lblHoleScore, 70, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, lblHoleScore, -315, SpringLayout.EAST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, txtHoleScoreMin, 6, SpringLayout.EAST, lblHoleScore);
		springLayout.putConstraint(SpringLayout.SOUTH, lblHoleScore, 0, SpringLayout.SOUTH, txtHoleScoreMin);
		frame.getContentPane().add(lblHoleScore);
		
		lblInitialNoOf = new JLabel("initial no. of holes");
		lblInitialNoOf.setFont(new Font("Tahoma", Font.PLAIN, 10));
		springLayout.putConstraint(SpringLayout.WEST, lblInitialNoOf, 35, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, lblInitialNoOf, -315, SpringLayout.EAST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, txtInitialNrHoles, 6, SpringLayout.EAST, lblInitialNoOf);
		frame.getContentPane().add(lblInitialNoOf);
		
		lblHoleSize = new JLabel("wall size");
		lblHoleSize.setFont(new Font("Tahoma", Font.PLAIN, 10));
		springLayout.putConstraint(SpringLayout.WEST, lblHoleSize, 80, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, lblHoleSize, -6, SpringLayout.WEST, txtWallSizeMin);
		springLayout.putConstraint(SpringLayout.SOUTH, lblInitialNoOf, -12, SpringLayout.NORTH, lblHoleSize);
		springLayout.putConstraint(SpringLayout.SOUTH, lblHoleSize, 0, SpringLayout.SOUTH, txtWallSizeMin);
		frame.getContentPane().add(lblHoleSize);
		
		lblInitialNoOf_1 = new JLabel("initial no. of walls");
		lblInitialNoOf_1.setFont(new Font("Tahoma", Font.PLAIN, 10));
		springLayout.putConstraint(SpringLayout.WEST, lblInitialNoOf_1, 37, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, lblInitialNoOf_1, 0, SpringLayout.SOUTH, txtInitialNrWalls);
		springLayout.putConstraint(SpringLayout.EAST, lblInitialNoOf_1, -6, SpringLayout.WEST, txtInitialNrWalls);
		frame.getContentPane().add(lblInitialNoOf_1);
		
		lblEnvironmentSettings = new JLabel("Environment Settings");
		springLayout.putConstraint(SpringLayout.NORTH, lblEnvironmentSettings, 10, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, lblEnvironmentSettings, 58, SpringLayout.WEST, frame.getContentPane());
		lblEnvironmentSettings.setFont(new Font("Tahoma", Font.BOLD, 12));
		frame.getContentPane().add(lblEnvironmentSettings);
		
		separator = new JSeparator();
		springLayout.putConstraint(SpringLayout.NORTH, separator, 10, SpringLayout.NORTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.WEST, separator, 15, SpringLayout.EAST, txtHoleGestationTimeMax);
		springLayout.putConstraint(SpringLayout.SOUTH, separator, -24, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, separator, 17, SpringLayout.EAST, txtWallSizeMax);
		separator.setOrientation(SwingConstants.VERTICAL);
		frame.getContentPane().add(separator);
		
		lblAgentSettings = new JLabel("Agent Settings");
		springLayout.putConstraint(SpringLayout.NORTH, lblAgentSettings, 0, SpringLayout.NORTH, lblEnvironmentSettings);
		springLayout.putConstraint(SpringLayout.WEST, lblAgentSettings, 50, SpringLayout.EAST, separator);
		lblAgentSettings.setFont(new Font("Tahoma", Font.BOLD, 12));
		frame.getContentPane().add(lblAgentSettings);
		
		txtDynamism = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txtDynamism, 23, SpringLayout.SOUTH, lblAgentSettings);
		springLayout.putConstraint(SpringLayout.WEST, txtDynamism, -67, SpringLayout.EAST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, txtDynamism, -25, SpringLayout.EAST, frame.getContentPane());
		frame.getContentPane().add(txtDynamism);
		txtDynamism.setColumns(10);
		
		txtPlanningTime = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txtPlanningTime, -3, SpringLayout.NORTH, lblMinHoleGestation);
		springLayout.putConstraint(SpringLayout.WEST, txtPlanningTime, 134, SpringLayout.EAST, separator);
		springLayout.putConstraint(SpringLayout.EAST, txtPlanningTime, 0, SpringLayout.EAST, txtDynamism);
		frame.getContentPane().add(txtPlanningTime);
		txtPlanningTime.setColumns(10);
		
		txtCommitmentDegree = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, txtCommitmentDegree, 0, SpringLayout.NORTH, txtHoleLifeExpMin);
		springLayout.putConstraint(SpringLayout.WEST, txtCommitmentDegree, 134, SpringLayout.EAST, separator);
		springLayout.putConstraint(SpringLayout.EAST, txtCommitmentDegree, 0, SpringLayout.EAST, txtDynamism);
		frame.getContentPane().add(txtCommitmentDegree);
		txtCommitmentDegree.setColumns(10);
		
		lblDynamism = new JLabel("dynamism");
		lblDynamism.setFont(new Font("Tahoma", Font.PLAIN, 10));
		springLayout.putConstraint(SpringLayout.NORTH, lblDynamism, 26, SpringLayout.SOUTH, lblAgentSettings);
		springLayout.putConstraint(SpringLayout.EAST, lblDynamism, -6, SpringLayout.WEST, txtDynamism);
		frame.getContentPane().add(lblDynamism);
		
		lblPlanningTime = new JLabel("planning time");
		lblPlanningTime.setFont(new Font("Tahoma", Font.PLAIN, 10));
		springLayout.putConstraint(SpringLayout.NORTH, lblPlanningTime, 0, SpringLayout.NORTH, lblMinHoleGestation);
		springLayout.putConstraint(SpringLayout.EAST, lblPlanningTime, -6, SpringLayout.WEST, txtPlanningTime);
		frame.getContentPane().add(lblPlanningTime);
		
		lblDegreeOfCommitment = new JLabel("commitment degree");
		lblDegreeOfCommitment.setFont(new Font("Tahoma", Font.PLAIN, 10));
		springLayout.putConstraint(SpringLayout.NORTH, lblDegreeOfCommitment, 3, SpringLayout.NORTH, txtHoleLifeExpMin);
		springLayout.putConstraint(SpringLayout.EAST, lblDegreeOfCommitment, -6, SpringLayout.WEST, txtCommitmentDegree);
		frame.getContentPane().add(lblDegreeOfCommitment);
		
		checkUseReactionStrategy = new JCheckBox("replan when...");
		checkUseReactionStrategy.setFont(new Font("Tahoma", Font.PLAIN, 10));
		springLayout.putConstraint(SpringLayout.NORTH, checkUseReactionStrategy, 0, SpringLayout.NORTH, lblHoleScore);
		springLayout.putConstraint(SpringLayout.WEST, checkUseReactionStrategy, 6, SpringLayout.EAST, separator);
		frame.getContentPane().add(checkUseReactionStrategy);
		
		radioTargetDisappears = new JRadioButton("target disappears");
		buttonGroup.add(radioTargetDisappears);
		radioTargetDisappears.setFont(new Font("Tahoma", Font.PLAIN, 10));
		springLayout.putConstraint(SpringLayout.NORTH, radioTargetDisappears, 0, SpringLayout.NORTH, lblInitialNoOf);
		springLayout.putConstraint(SpringLayout.WEST, radioTargetDisappears, 7, SpringLayout.EAST, separator);
		springLayout.putConstraint(SpringLayout.SOUTH, radioTargetDisappears, -79, SpringLayout.SOUTH, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.EAST, radioTargetDisappears, 0, SpringLayout.EAST, lblDynamism);
		frame.getContentPane().add(radioTargetDisappears);
		
		radioTargetDisOrNearerHole = new JRadioButton("target dis. or nearer hole");
		buttonGroup.add(radioTargetDisOrNearerHole);
		radioTargetDisOrNearerHole.setFont(new Font("Tahoma", Font.PLAIN, 10));
		springLayout.putConstraint(SpringLayout.NORTH, radioTargetDisOrNearerHole, 0, SpringLayout.NORTH, lblHoleSize);
		springLayout.putConstraint(SpringLayout.WEST, radioTargetDisOrNearerHole, 6, SpringLayout.EAST, separator);
		frame.getContentPane().add(radioTargetDisOrNearerHole);
		
		radioTargetDisOrAnyHole = new JRadioButton("target dis. or any new hole");
		buttonGroup.add(radioTargetDisOrAnyHole);
		radioTargetDisOrAnyHole.setFont(new Font("Tahoma", Font.PLAIN, 10));
		springLayout.putConstraint(SpringLayout.WEST, radioTargetDisOrAnyHole, 6, SpringLayout.EAST, separator);
		springLayout.putConstraint(SpringLayout.SOUTH, radioTargetDisOrAnyHole, 0, SpringLayout.SOUTH, separator);
		frame.getContentPane().add(radioTargetDisOrAnyHole);
		
		JButton btnOk = new JButton("Save");
		btnOk.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				try {
					getParametersFromGUI();
					frame.dispose();
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				
			}

			
		});
		springLayout.putConstraint(SpringLayout.WEST, btnOk, 225, SpringLayout.WEST, frame.getContentPane());
		springLayout.putConstraint(SpringLayout.SOUTH, btnOk, 0, SpringLayout.SOUTH, frame.getContentPane());
		frame.getContentPane().add(btnOk);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				frame.dispose();
			}
		});
		springLayout.putConstraint(SpringLayout.NORTH, btnCancel, 0, SpringLayout.NORTH, btnOk);
		springLayout.putConstraint(SpringLayout.EAST, btnCancel, -11, SpringLayout.WEST, btnOk);
		frame.getContentPane().add(btnCancel);
	}
	
	
	
	private void initializeParametersInGUI() {
		txtWorldSize.setText(TileworldSettings.WORLD_SIZE+"");
		
		txtHoleGestationTimeMin.setText(
				TileworldSettings.HOLE_GESTATION_TIME_MIN + "");
		txtHoleGestationTimeMax.setText(
				TileworldSettings.HOLE_GESTATION_TIME_MAX + "");
		
		txtHoleLifeExpMin.setText(
				TileworldSettings.HOLE_LIFE_EXP_MIN + "");
		txtHoleLifeExpMax.setText(
				TileworldSettings.HOLE_LIFE_EXP_MAX + "");
		
		txtHoleScoreMin.setText(
				TileworldSettings.HOLE_SCORE_MIN + "");
		txtHoleScoreMax.setText(
				TileworldSettings.HOLE_SCORE_MAX + "");
		
		txtInitialNrHoles.setText(TileworldSettings.INITIAL_NR_HOLES+"");
		
		txtWallSizeMin.setText(
				TileworldSettings.WALL_SIZE_MIN + "");
		txtWallSizeMax.setText(
				TileworldSettings.WALL_SIZE_MAX + "");
		
		txtInitialNrWalls.setText(TileworldSettings.INITIAL_NR_WALLS+"");
		
		txtDynamism.setText(TileworldSettings.DYNAMISM+"");
		txtPlanningTime.setText(TileworldSettings.PLANNING_TIME+"");
		txtCommitmentDegree.setText(TileworldSettings.BOLDNESS+"");
		
		checkUseReactionStrategy.setSelected(TileworldSettings.USE_REACTION_STRATEGY);
		
		if (TileworldSettings.USE_REACTION_STRATEGY) {
			switch (TileworldSettings.REACTION_STRATEGY) {
			case TARGET_DISAPPEARS: radioTargetDisappears.setSelected(true); break;
			case TARGET_DIS_OR_ANY_HOLE: radioTargetDisOrAnyHole.setSelected(true); break;
			case TARGET_DIS_OR_NEARER_HOLE: radioTargetDisOrNearerHole.setSelected(true); break;
			}
		}
	}
	
	private void getParametersFromGUI() throws Exception 
	{
		int worldSize = validateInt(txtWorldSize, 2, 1000),
				holeGestationTimeMin = validateInt(txtHoleGestationTimeMin, 1, 1000),
				holeGestationTimeMax = validateInt(txtHoleGestationTimeMax, 1, 1000),
				holeLifeExpMin = validateInt(txtHoleLifeExpMin, 1, 1000),
				holeLifeExpMax = validateInt(txtHoleLifeExpMax, 1, 1000),
				holeScoreMin = validateInt(txtHoleScoreMin, 1, 1000),
				holeScoreMax = validateInt(txtHoleScoreMax, 1, 1000),
				initialNrHoles = validateInt(txtInitialNrHoles, 0, worldSize*worldSize-1),
				wallSizeMin = validateInt(txtWallSizeMin, 1, worldSize),
				wallSizeMax = validateInt(txtWallSizeMax, 1, worldSize),
				initialNrWalls = validateInt(txtInitialNrWalls, 0, (worldSize*worldSize)/wallSizeMax),
				dynamism = validateInt(txtDynamism, 1, 100),
				commitmentDegree = validateInt(txtCommitmentDegree, -1, 100);
		
		double planningTime = validateDouble(txtPlanningTime, 0, 100);
		
		boolean useReactionStrategy = checkUseReactionStrategy.isSelected();
		
		ReactionStrategy reactionStrategy = (radioTargetDisappears.isSelected() ? ReactionStrategy.TARGET_DISAPPEARS :
			(radioTargetDisOrAnyHole.isSelected() ? ReactionStrategy.TARGET_DIS_OR_ANY_HOLE :
				(radioTargetDisOrNearerHole.isSelected() ? ReactionStrategy.TARGET_DIS_OR_NEARER_HOLE :
					null)));
		
		validateConstraint(holeGestationTimeMin <= holeGestationTimeMax);
		validateConstraint(holeLifeExpMin < holeLifeExpMax);
		validateConstraint(holeScoreMin < holeScoreMax);
		validateConstraint(wallSizeMin < wallSizeMax);
		validateConstraint((initialNrWalls*wallSizeMax + 1 + initialNrHoles) < worldSize*worldSize);
		
		TileworldSettings.WORLD_SIZE = worldSize;
		TileworldSettings.HOLE_GESTATION_TIME_MIN = holeGestationTimeMin;
		TileworldSettings.HOLE_GESTATION_TIME_MAX = holeGestationTimeMax;
		TileworldSettings.HOLE_LIFE_EXP_MIN = holeLifeExpMin;
		TileworldSettings.HOLE_LIFE_EXP_MAX = holeLifeExpMax;
		TileworldSettings.HOLE_SCORE_MIN = holeScoreMin;
		TileworldSettings.HOLE_SCORE_MAX = holeScoreMax;
		TileworldSettings.INITIAL_NR_HOLES = initialNrHoles;
		TileworldSettings.WALL_SIZE_MIN = wallSizeMin;
		TileworldSettings.WALL_SIZE_MAX = wallSizeMax;
		TileworldSettings.INITIAL_NR_WALLS = initialNrWalls;
		TileworldSettings.DYNAMISM = dynamism;
		TileworldSettings.PLANNING_TIME = planningTime;
		TileworldSettings.BOLDNESS = commitmentDegree;
		TileworldSettings.USE_REACTION_STRATEGY = useReactionStrategy;
		TileworldSettings.REACTION_STRATEGY = reactionStrategy;
		
	}
	
	private void validateConstraint(Boolean c) throws Exception {
		if (!c) {
			throw new Exception("Constraint not satisfied");
		}
	}
	
	private int validateInt(JTextField textField, int min, int max) throws Exception {
		int ret = Integer.parseInt(textField.getText());
		if ((ret >= min) && (ret <= max)) {
			return ret;
		} else {
			throw new Exception("Parsing problem with " + textField.getName() + "(bounds: ["+min+","+max+"])");
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
