package mdps.factories;

import mdps.MDP;
import mdps.PopulatedMDP;
import mdps.Tileworld;
import mdps.generators.GeneralMDPGenerator;
import mdps.generators.MDPGenerator;
import mdps.generators.TileworldGenerator;

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
		case TILEWORLD: return new Tileworld();
		default: return new MDP();
		}
	}
	
	public MDPGenerator buildMDPGenerator() {
		switch (mdpType) 
		{
		case POPULATED_MDP: return new GeneralMDPGenerator();
		case TILEWORLD: return new TileworldGenerator();
		default: return new GeneralMDPGenerator();
		}
	}
}
