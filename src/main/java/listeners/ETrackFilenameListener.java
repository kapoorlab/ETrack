package listeners;

import java.awt.TextComponent;
import java.awt.event.TextEvent;
import java.awt.event.TextListener;

import pluginTools.InteractiveEmbryo;



public class ETrackFilenameListener  implements TextListener {

	
	final InteractiveEmbryo parent;
	
	public ETrackFilenameListener(final InteractiveEmbryo parent){
		
		this.parent = parent;
		
	}
	
	@Override
	public void textValueChanged(TextEvent e) {
		final TextComponent tc = (TextComponent)e.getSource();
	    String s = tc.getText();
	   
	    if (s.length() > 0)
		parent.addToName = s;
		
	}


}
