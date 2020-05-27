
package listeners;

import java.awt.Label;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;

import javax.swing.JScrollBar;

import pluginTools.InteractiveEmbryo;
import utility.ShowView;

public class MinSegDistListener implements AdjustmentListener {
	final Label label;
	final String string;
	InteractiveEmbryo parent;
	final float min;
	final int scrollbarSize;

	final JScrollBar deltaScrollbar;

	public MinSegDistListener(final InteractiveEmbryo parent, final Label label, final String string, final float min,
			final int scrollbarSize, final JScrollBar deltaScrollbar) {
		this.label = label;
		this.parent = parent;
		this.string = string;
		this.min = min;
		
		this.scrollbarSize = scrollbarSize;
		deltaScrollbar.addMouseListener( new CurvatureMouseListener( parent ) );
		this.deltaScrollbar = deltaScrollbar;
		deltaScrollbar.setBlockIncrement(1);
	
	}

	@Override
	public void adjustmentValueChanged(AdjustmentEvent e) {
		
		float max = parent.maxSegDist;
		parent.minSegDist =  (int) utility.Slicer.computeValueFromScrollbarPosition(e.getValue(), min, max, scrollbarSize);
	
		
		
		
		deltaScrollbar
				.setValue(utility.Slicer.computeScrollbarPositionFromValue(parent.minSegDist, min, max, scrollbarSize));

		label.setText(string +  " = "  + parent.nf.format(parent.minSegDist));
		
	 
		
		parent.minSegDistField.setText(Integer.toString(Math.round(parent.minSegDist)));
		parent.panelFirst.validate();
		parent.panelFirst.repaint();
		
		
	}
	

	
	
	
	
}