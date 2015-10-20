package mdp.elements;

public enum TileworldActionType 
{
	UP ("up"),
	DOWN ("down"),
	LEFT ("left"),
	RIGHT ("right");
	
	private final String name;
	
	TileworldActionType(String name) {
		this.name = name;
	}
	
	public String getName()
	{
		return this.name;
	}
}