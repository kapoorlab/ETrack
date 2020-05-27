package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import pluginTools.InteractiveEmbryo;

public class DistancemodeListener implements ItemListener {

	InteractiveEmbryo parent;

	public DistancemodeListener(InteractiveEmbryo parent) {
		this.parent = parent;
	}

	@Override
	public void itemStateChanged(final ItemEvent arg0) {

		if (arg0.getStateChange() == ItemEvent.DESELECTED) {

			parent.circlefits = true;
			parent.distancemethod = false;
			parent.combomethod = false;

		}

		else if (arg0.getStateChange() == ItemEvent.SELECTED) {

			parent.circlefits = true;
			parent.combomethod = false;
			parent.distancemethod = true;
			parent.resolution = 1; //Integer.parseInt(parent.resolutionField.getText());
		}

	}

}