package solver;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Panel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

public class MainGUI {

	private JFrame frame;
	
	JTextField textFieldMaxX, textFieldMaxY, textFieldNumActions,
		textFieldPState, textFieldPDeterministic, textFieldPExecutable,
		textFieldMinReward, textFieldMaxReward;
	
	MDPBuilder mdpBuilder = new MDPBuilder();
	
	DrawPanel drawPanel;
	
	Agent agent;
			
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGUI window = new MainGUI();
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
	public MainGUI() {
		buildGUI();
		setParametersInGUI();
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
		drawPanel = new DrawPanel();
		
		JSeparator jSep = new JSeparator();
		
		pNavContainer.add(new JSeparator(), BorderLayout.CENTER);
		
		Panel pNavTop = new Panel();
		
		pNavContainer.add(pNavTop, BorderLayout.NORTH);
		
		
		pNavTop.setLayout(new BoxLayout(pNavTop, BoxLayout.X_AXIS));
		
		pNavTop.add(Box.createHorizontalStrut(5));
		
		pNavTop.add(new JLabel("max x: "));
		
		textFieldMaxX = new JTextField();
		textFieldMaxX.setMaximumSize( new Dimension(30, 20) );
		pNavTop.add(textFieldMaxX);
		pNavTop.add(Box.createHorizontalStrut(10));
		
		pNavTop.add(new JLabel("max y: "));
		textFieldMaxY = new JTextField();
		textFieldMaxY.setMaximumSize( new Dimension(30, 20) );
		pNavTop.add(textFieldMaxY);
		
		pNavTop.add(Box.createHorizontalStrut(10));
		pNavTop.add(new JLabel("p state: "));
		
		textFieldPState = new JTextField();
		textFieldPState.setMaximumSize( new Dimension(30, 20) );
		pNavTop.add(textFieldPState);
		pNavTop.add(Box.createHorizontalStrut(5));
		
		jSep = new JSeparator(JSeparator.VERTICAL);
		jSep.setMaximumSize(new Dimension(2, 25));
		
		pNavTop.add(jSep);
		
		pNavTop.add(Box.createHorizontalStrut(5));
		
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
		
		pNavTop.add(Box.createHorizontalStrut(5));
		
		pNavTop.add(new JLabel("num actions: "));
		textFieldNumActions = new JTextField();
		textFieldNumActions.setMaximumSize( new Dimension(30, 20) );
		pNavTop.add(textFieldNumActions);
		
		pNavTop.add(Box.createHorizontalStrut(10));
		
		pNavTop.add(new JLabel("p executable: "));
		textFieldPExecutable = new JTextField();
		textFieldPExecutable.setMaximumSize( new Dimension(30, 20) );
		pNavTop.add(textFieldPExecutable);
		
		pNavTop.add(Box.createHorizontalStrut(10));
		
		pNavTop.add(new JLabel("p_deterministic: "));
		textFieldPDeterministic = new JTextField();
		textFieldPDeterministic.setMaximumSize( new Dimension(30, 20) );
		pNavTop.add(textFieldPDeterministic);
		
		pNavTop.add(Box.createHorizontalStrut(10));
		
		Panel pNavBottom = new Panel();
		
		pNavContainer.add(pNavBottom, BorderLayout.SOUTH);
	
		pNavBottom.setLayout(new BoxLayout(pNavBottom, BoxLayout.X_AXIS));
		
		JButton btnCreateMdp = new JButton("Create MDP");
		btnCreateMdp.setMaximumSize(new Dimension(110, 20));
		
		pNavBottom.add(btnCreateMdp);
		btnCreateMdp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				MainGUI.this.buildAndShowMDP();					
			}
		});
		
		JButton btnCreateAgent = new JButton("Create Agent");
		btnCreateAgent.setMaximumSize(new Dimension(120, 20));
		
		pNavBottom.add(btnCreateAgent);
		btnCreateAgent.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				MainGUI.this.addAgent();					
			}
		});
		
		JButton btnStep = new JButton("Step");
		btnCreateAgent.setMaximumSize(new Dimension(120, 20));
		
		pNavBottom.add(btnStep);
		btnStep.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				MainGUI.this.moveAgent();					
			}
		});
		
		cPane.add(drawPanel);
		
		frame.setVisible(true);
		
	}
	
	private void buildAndShowMDP() {
		try {
			getParametersFromGUI();
			mdpBuilder.buildNewModel();
			drawPanel.updateModel(mdpBuilder.getModel());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void addAgent() {
		if (mdpBuilder == null || mdpBuilder.getModel() == null) 
			return;
		
		Model model = mdpBuilder.getModel();
		
		State s = model.getRandomState();
		
		agent = new Agent(model, s.x, s.y);
		
		drawPanel.setAgent(agent);
	}
	
	private void moveAgent() {
		agent.move();
		drawPanel.repaint();
	}
	
	private void setParametersInGUI() {
		textFieldMaxX.setText(Integer.toString(Parameters.maxX));
		textFieldMaxY.setText(Integer.toString(Parameters.maxY));
		textFieldNumActions.setText(Integer.toString(Parameters.numActions));
		textFieldMinReward.setText(Double.toString(Parameters.minReward));
		textFieldMaxReward.setText(Double.toString(Parameters.maxReward));
		textFieldPDeterministic.setText(Double.toString(Parameters.pDeterministic));
		textFieldPExecutable.setText(Double.toString(Parameters.pExecutable));
		textFieldPState.setText(Double.toString(Parameters.pState));
	}

	private void getParametersFromGUI() throws Exception {
		
		Parameters.maxX = validateInt(textFieldMaxX,1,100);
		Parameters.maxY = validateInt(textFieldMaxY,1,100);
		Parameters.minReward = validateDouble(textFieldMinReward,-100.0,100.0);
		Parameters.maxReward = validateDouble(textFieldMaxReward,-100.0, 100.0);
		Parameters.pDeterministic = validateProbability(textFieldPDeterministic);
		Parameters.pExecutable = validateProbability(textFieldPExecutable);
		Parameters.pState = validateProbability(textFieldPState);
		
		validateConstraints();
	}
	
	private void validateConstraints() throws Exception {
		if (Parameters.minReward > Parameters.maxReward) {
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
	
	private double validateProbability(JTextField textField) throws Exception {
		return validateDouble(textField, 0.0, 1.0);
	}
}
