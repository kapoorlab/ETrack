package listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComboBox;

import ij.WindowManager;
import pluginTools.EmbryoFileChooser;

public class ChoosesuperProbMap implements ActionListener {

	
	final EmbryoFileChooser parent;
	final JComboBox<String> choice;
	
	public ChoosesuperProbMap(final EmbryoFileChooser parent, final JComboBox<String> choice) {
		
		this.parent = parent;
		this.choice = choice;
		
	}
	
	@Override
	public void actionPerformed(ActionEvent e)  {

		String imagename = (String) choice.getSelectedItem();
		
		
	    	parent.impsuper = WindowManager.getImage(imagename);
			


		

	}	
	
	

		
	
	
	
}
