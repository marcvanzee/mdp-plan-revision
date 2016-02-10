package gui;

import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFrame;

import gui.generalMDP.MDPGUI;
import gui.tileworld.TileworldGUI;
import mdp.algorithms.AlgorithmType;
import settings.FileSettings;
import settings.TileworldSettings;

public class Main 
{
	private JFrame frame;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					
					new Main();
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		
		if (FileSettings.LOAD_SETTINGS)
			try {
				loadSettings();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		initialize();
		frame.setVisible(true);
	}
	
	public static void loadSettings() throws IOException 
	{
		String file = FileSettings.SETTINGS_FILE;
		
		if (!new File(file).exists()) {
			throw new FileNotFoundException("file " + file + " not found!");
		}
				
		Properties settings = new Properties();
		settings.load(new FileInputStream(file));

		// getProperty() returns a String
		if (settings.getProperty("worldSize") != null)
			TileworldSettings.WORLD_SIZE = Integer.parseInt(settings.getProperty("worldSize"));
		
		if (settings.getProperty("holeGestationTimeMin") != null)
			TileworldSettings.HOLE_GESTATION_TIME_MIN = Integer.parseInt(settings.getProperty("holeGestationTimeMin"));
		
		if (settings.getProperty("holeGestationTimeMax") != null)
			TileworldSettings.HOLE_GESTATION_TIME_MAX = Integer.parseInt(settings.getProperty("holeGestationTimeMax"));
		
		if (settings.getProperty("holeLifeExpMin") != null)
			TileworldSettings.HOLE_LIFE_EXP_MIN = Integer.parseInt(settings.getProperty("holeLifeExpMin"));
		
		if (settings.getProperty("holeLifeExpMax") != null)
			TileworldSettings.HOLE_LIFE_EXP_MAX = Integer.parseInt(settings.getProperty("holeLifeExpMax"));
		
		if (settings.getProperty("holeScoreMin") != null)
			TileworldSettings.HOLE_SCORE_MIN = Integer.parseInt(settings.getProperty("holeScoreMin"));
		
		if (settings.getProperty("holeScoreMax") != null)
			TileworldSettings.HOLE_SCORE_MAX = Integer.parseInt(settings.getProperty("holeScoreMax"));
		
		if (settings.getProperty("initialNrHoles") != null)
			TileworldSettings.INITIAL_NR_HOLES = Integer.parseInt(settings.getProperty("initialNrHoles"));
		
		if (settings.getProperty("wallSizeMin") != null)
			TileworldSettings.WALL_SIZE_MIN = Integer.parseInt(settings.getProperty("wallSizeMin"));
		
		if (settings.getProperty("wallSizeMax") != null)
			TileworldSettings.WALL_SIZE_MAX = Integer.parseInt(settings.getProperty("wallSizeMax"));
		
		if (settings.getProperty("initialNrWalls") != null)
			TileworldSettings.INITIAL_NR_WALLS = Integer.parseInt(settings.getProperty("initialNrWalls"));
		
		if (settings.getProperty("dynamism") != null)
			TileworldSettings.DYNAMISM = Integer.parseInt(settings.getProperty("dynamism"));
		
		if (settings.getProperty("planningTime") != null)
			TileworldSettings.PLANNING_TIME = Double.parseDouble(settings.getProperty("planningTime"));
		
		if (settings.getProperty("commitmentDegree") != null)
			TileworldSettings.BOLDNESS = Integer.parseInt(settings.getProperty("commitmentDegree"));
		
		if (settings.getProperty("useReactionStrategy") != null)
			TileworldSettings.USE_REACTION_STRATEGY = Boolean.parseBoolean(settings.getProperty("useReactionStrategy"));
		
		if (settings.getProperty("reactionStrategy") != null)
			TileworldSettings.REACTION_STRATEGY = TileworldSettings.parseReactionStrategy(settings.getProperty("reactionStrategy"));
		
		if (settings.getProperty("learning") != null)
			TileworldSettings.ALGORITHM = AlgorithmType.LEARNING;
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 223, 72);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		JButton btnGeneralMdp = new JButton("General MDP");
		btnGeneralMdp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent arg0) {
				
				frame.dispose();
				
					try {
						(new MDPGUI()).go();
					} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
							| InvocationTargetException | NoSuchMethodException | SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			}
		});
		frame.getContentPane().add(btnGeneralMdp);
		
		JButton btnTileworld = new JButton("Tileworld");
		btnTileworld.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				
				frame.dispose();
				
				try {
					(new TileworldGUI()).go();
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			
			}
		});
		frame.getContentPane().add(btnTileworld);
	}
	
	private static class NullOutputStream extends OutputStream {
	    @Override
	    public void write(int b){
	         return;
	    }
	    @Override
	    public void write(byte[] b){
	         return;
	    }
	    @Override
	    public void write(byte[] b, int off, int len){
	         return;
	    }
	    public NullOutputStream(){
	    }
	}

}
