package mdps.factories;

import mdps.MDP;
import mdps.PopulatedMDP;
import mdps.TileWorld;
import mdps.generators.GeneralMDPGenerator;
import mdps.generators.MDPGenerator;
import mdps.generators.TileWorldGenerator;

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
