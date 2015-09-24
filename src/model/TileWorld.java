package model;

import model.mdp.operations.TileWorldChanger;

/**
 * This is the Kinny and Georgeff TileWorld
 * 
 * @author marc.vanzee
 *
 */
public class TileWorld extends PopulatedMDP 
{
	public TileWorld() {
		super();
		mdpChanger = new TileWorldChanger();
	}
	
}
