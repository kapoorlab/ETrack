package listeners;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import ij.WindowManager;
import pluginTools.EmbryoFileChooser;

public class ChoosesecOrigMap implements ActionListener {

	
	final EmbryoFileChooser parent;
	final JComboBox<String> choice;
	
	public ChoosesecOrigMap(final EmbryoFileChooser parent, final JComboBox<String> choice) {
		
		this.parent = parent;
		this.choice = choice;
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {

		String imagename = (String) choice.getSelectedItem();
		
	 parent.twochannel = true;
		
	    	parent.impSec = WindowManager.getImage(imagename);
	    	
	
			
			
			
		
	}
	
	
	
}
