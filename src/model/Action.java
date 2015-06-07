package model;


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
		char c = (char) ('a' + i);
		
		this.name = c + (times > 0 ? c + Integer.toString(times) : "");
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
