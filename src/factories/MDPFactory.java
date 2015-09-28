package factories;

import model.PopulatedMDP;
import model.TileWorld;
import model.mdp.MDP;
import model.mdp.operations.GeneralMDPGenerator;
import model.mdp.operations.MDPGenerator;
import model.mdp.operations.TileWorldGenerator;

public class MDPFactory 
{		
	private MDPType mdpType;
	
	public MDPFactory(MDPType type) {
		this.mdpType = type;
	}
		
	public MDP buildMDP() {
		switch (mdpType) 
		{
		case POPULATED_MDP: return new PopulatedMDP();
		case TILEWORLD: return new TileWorld();
		default: return new MDP();
		}
	}
	
	public MDPGenerator buildMDPGenerator() {
		switch (mdpType) 
		{
		case POPULATED_MDP: return new GeneralMDPGenerator();
		case TILEWORLD: return new TileWorldGenerator();
		default: return new GeneralMDPGenerator();
		}
	}
}
