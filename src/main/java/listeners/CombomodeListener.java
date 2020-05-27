package listeners;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import pluginTools.InteractiveEmbryo;

public class CombomodeListener implements ItemListener {

	InteractiveEmbryo parent;

		public CombomodeListener(InteractiveEmbryo parent) {
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

				parent.circlefits = false;
				parent.combomethod = true;
				parent.distancemethod = false;
				parent.resolution = 1; // Integer.parseInt(parent.resolutionField.getText());
			}

		}

	
}
