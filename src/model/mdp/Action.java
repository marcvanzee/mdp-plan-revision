package model.mdp;


public class Action {
	String name;
	
	public Action(String name) {
		try {
			toName(Integer.parseInt(name));
			
			} catch (NumberFormatException e) {
			  this.name = name;
			}
	}
	
	public Action(int i) {
		toName(i);
	}
	
	private void toName(int i) {
		int times = (int) Math.floor(i / 26);
		int num = i % 26;
		char c = (char) ('a' + num);
		
		System.out.println("action char: " + c + "" + times);
		
		this.name = c + (times > 0 ? times + "" : "");
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
