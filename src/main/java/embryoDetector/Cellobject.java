package embryoDetector;

import java.util.ArrayList;

public class Cellobject {
	
	public final ArrayList<Embryoobject> cell;
	
	public final int ID;
	
	public Cellobject(final ArrayList<Embryoobject> cell, final int ID) {
		
		this.cell = cell;
		
		this.ID = ID;
	}

}
