package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import pluginTools.InteractiveEmbryo;

public class CirclemodeListener implements ItemListener {

	InteractiveEmbryo parent;

	public CirclemodeListener(InteractiveEmbryo parent) {
		this.parent = parent;
	}

	@Override
	public void itemStateChanged(final ItemEvent arg0) {

		if (arg0.getStateChange() == ItemEvent.DESELECTED) {

			parent.circlefits = false;
			parent.distancemethod = true;
			parent.combomethod = false;

		}

		else if (arg0.getStateChange() == ItemEvent.SELECTED) {

			parent.circlefits = true;
			parent.combomethod = false;
			parent.distancemethod = false;
			parent.resolution = 1; //Integer.parseInt(parent.resolutionField.getText());
		}

	}

}